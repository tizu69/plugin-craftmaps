<script lang="ts">
	import { c, colors, MapCore } from '$lib';
	import { onMount } from 'svelte';
	import { expoOut } from 'svelte/easing';
	import { Tween } from 'svelte/motion';
	import type { BlockColor, ColorShades } from './colors';
	import { goto } from '$app/navigation';

	let {
		pos: cameraPos = $bindable()
	}: {
		pos: { x: number; z: number; world: string };
	} = $props();

	const maxScale = 16;
	let core = $state(new MapCore('minecraft:overworld'));

	let canvas = $state<HTMLCanvasElement>();

	let taskQueue = $state<(() => void)[]>([]);
	onMount(() => {
		setInterval(() => {
			taskQueue.shift()?.();
		}, 40);
	});

	const forceRerender = () => {
		if (!canvas) return;
		const ctx = canvas.getContext('2d', { willReadFrequently: true })!;

		canvas.width = canvas.clientWidth;
		canvas.height = canvas.clientHeight;

		ctx.fillStyle = '#000000';
		ctx.fillRect(0, 0, canvas.width, canvas.height);

		const centerX = Math.floor(core.cam.current.x / c.REGION_BLOCKS);
		const centerZ = Math.floor(core.cam.current.z / c.REGION_BLOCKS);
		const countX = Math.ceil(canvas.width / core.cam.current.scale / c.REGION_BLOCKS) + 2;
		const countZ = Math.ceil(canvas.height / core.cam.current.scale / c.REGION_BLOCKS) + 2;
		const topLeftX = centerX - Math.floor(countX / 2);
		const topLeftZ = centerZ - Math.floor(countZ / 2);
		const screenCenterX = Math.floor(canvas.width / 2);
		const screenCenterZ = Math.floor(canvas.height / 2);

		const visible: { x: number; z: number; ix: number; iz: number }[] = [];
		for (let x = 0; x < countX; x++)
			for (let z = 0; z < countZ; z++)
				visible.push({ x: topLeftX + x, z: topLeftZ + z, ix: x, iz: z });

		visible.forEach((region) => {
			const r = core.getRegion(region.x, region.z);
			if (!r) return;

			const worldX = region.x * c.REGION_BLOCKS;
			const worldZ = region.z * c.REGION_BLOCKS;
			const screenX = screenCenterX + core.cam.current.scale * (worldX - core.cam.current.x);
			const screenZ = screenCenterZ + core.cam.current.scale * (worldZ - core.cam.current.z);

			ctx.imageSmoothingEnabled = false;
			ctx.drawImage(
				core.renderRegion(region.x, region.z)!,
				screenX,
				screenZ,
				core.cam.current.scale * c.REGION_BLOCKS,
				core.cam.current.scale * c.REGION_BLOCKS
			);

			if (region.x == 0 && region.z == 0) {
				ctx.fillStyle = 'rgba(255, 0, 0, 0.3)';
				ctx.fillRect(
					screenX,
					screenZ,
					core.cam.current.scale * c.REGION_BLOCKS,
					core.cam.current.scale * c.REGION_BLOCKS
				);
			}
		});
	};
	$effect(() => forceRerender());

	let isClicking = false;
	let lastTouch: { x: number; y: number } | null = null;
	let lastZoomDistance: number | null = null;
	const onPan = (x: number, y: number, smooth: boolean) => {
		if (!isClicking) return;
		core.cam.set(
			{
				x: core.cam.target.x - x / core.cam.target.scale,
				z: core.cam.target.z - y / core.cam.target.scale,
				scale: core.cam.target.scale
			},
			{ duration: !smooth ? 0 : undefined }
		);
	};
	const onZoom = (delta: number, centerX: number, centerY: number) => {
		const screenCenterX = canvas!.width / 2;
		const screenCenterZ = canvas!.height / 2;

		const worldX = core.cam.current.x + (centerX - screenCenterX) / core.cam.current.scale;
		const worldZ = core.cam.current.z + (centerY - screenCenterZ) / core.cam.current.scale;
		const newScale = Math.min(Math.max(core.cam.target.scale + delta, 1 / 16), maxScale);

		core.cam.set({
			x: worldX - (centerX - screenCenterX) / newScale,
			z: worldZ - (centerY - screenCenterZ) / newScale,
			scale: newScale
		});
	};
</script>

<canvas
	class="size-full"
	bind:this={canvas}
	onmousemove={(e) => onPan(e.movementX, e.movementY, false)}
	ontouchmove={(e) => {
		switch (e.touches.length) {
			case 1:
				const touch = e.touches[0];
				onPan(
					(touch.clientX - lastTouch?.x!) * 2,
					(touch.clientY - lastTouch?.y!) * 2,
					true
				);
				lastTouch = { x: touch.clientX, y: touch.clientY };
				break;
			case 2:
				const touch1 = e.touches[0];
				const touch2 = e.touches[1];
				const dx = touch1.clientX - touch2.clientX;
				const dy = touch1.clientY - touch2.clientY;
				const distance = Math.sqrt(dx * dx + dy * dy);
				const centerX = (touch1.clientX + touch2.clientX) / 2;
				const centerY = (touch1.clientY + touch2.clientY) / 2;
				if (lastZoomDistance != null)
					onZoom((distance - lastZoomDistance) / 10, centerX, centerY);
				lastZoomDistance = distance;
				break;
		}
	}}
	onwheel={(e) => onZoom(Math.sign(-e.deltaY), e.clientX, e.clientY)}
	onmousedown={(e) => {
		e.preventDefault();
		if (e.button === 0 || e.button === 1) return (isClicking = true);

		const pos = core.viewportToWorld(e.offsetX, e.offsetY);
		const block = core.getBlock(pos[0], pos[1]);
		if (!block) return

		goto(`?x=${pos[0]}&z=${pos[1]}&block=${block.id}&blockY=${block.y}`);
		navigator.clipboard.writeText(`/tp ${pos[0]} ${block.y} ${pos[1]}`);
	}}
	onmouseup={() => (isClicking = false)}
	onmouseleave={() => (isClicking = false)}
	ontouchstart={(e) => {
		isClicking = true;
		lastTouch = { x: e.touches[0].clientX, y: e.touches[0].clientY };
	}}
	ontouchend={() => ((isClicking = false), (lastTouch = null), (lastZoomDistance = null))}
></canvas>
<svelte:window onresize={() => forceRerender()} />
