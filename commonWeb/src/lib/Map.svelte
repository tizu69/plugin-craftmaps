<script lang="ts">
	import { colors } from '$lib';
	import { onMount } from 'svelte';
	import { expoOut } from 'svelte/easing';
	import { Tween } from 'svelte/motion';
	import type { BlockColor, ColorShades } from './colors';

	interface Block {
		name: string;
		id: string;
		color: BlockColor;
		y: number;
	}
	interface Chunk {
		pos: {
			x: number;
			z: number;
			world: string;
		};
		blocks: number[][];
		palette: Block[];
	}
	type Region = Chunk[][];

	let {
		pos: cameraPos = $bindable()
	}: {
		pos: { x: number; z: number; world: string };
	} = $props();

	const maxScale = 16;
	const scale = new Tween(5, { easing: expoOut, duration: 700 });
	const cam = new Tween({ x: cameraPos.x, z: cameraPos.z }, { easing: expoOut, duration: 700 });

	const CHUNK_SIZE = 16;
	const REGION_SIZE = 16;
	const SIZE_PER_REGION = REGION_SIZE * CHUNK_SIZE;
	let regions: Record<string, null | Region> = $state({});
	let canvas = $state<HTMLCanvasElement>();
	let cachedRegions = $state<Record<string, HTMLCanvasElement>>({});

	let taskQueue = $state<(() => void)[]>([]);
	onMount(() => {
		setInterval(() => {
			taskQueue.shift()?.();
		}, 40);
	});

	const createRegionImage = (region: Region): HTMLCanvasElement => {
		const regionCanvas = document.createElement('canvas');
		regionCanvas.width = SIZE_PER_REGION;
		regionCanvas.height = SIZE_PER_REGION;

		const ctx = regionCanvas.getContext('2d', { willReadFrequently: true })!;
		ctx.clearRect(0, 0, SIZE_PER_REGION, SIZE_PER_REGION);

		for (let cx = 0; cx < REGION_SIZE; cx++)
			for (let cz = 0; cz < REGION_SIZE; cz++) {
				/* ctx.fillStyle = cx % 2 == cz % 2 ? `#1e1e2e` : '#000000';
				ctx.fillRect(cx * CHUNK_SIZE, cz * CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE); */

				for (let x = 0; x < CHUNK_SIZE; x++)
					for (let z = 0; z < CHUNK_SIZE; z++) {
						const chunk = region[cx]?.[cz];
						const block = chunk?.blocks?.[x]?.[z];
						if (!block) continue;
						const current = chunk.palette[block];

						let below = chunk.palette[chunk.blocks[x][z - 1]];
						let shade: ColorShades =
							!below || below?.y === current.y ? 0 : below?.y < current.y ? 1 : -1;

						ctx.fillStyle = colors.get(current.color, shade);
						ctx.fillRect(cx * CHUNK_SIZE + x, cz * CHUNK_SIZE + z, 1, 1);
					}
			}

		return regionCanvas;
	};
	const forceRerender = () => {
		if (!canvas) return;
		const ctx = canvas.getContext('2d', { willReadFrequently: true })!;

		canvas.width = canvas.clientWidth;
		canvas.height = canvas.clientHeight;

		ctx.fillStyle = '#000000';
		ctx.fillRect(0, 0, canvas.width, canvas.height);

		const centerX = Math.floor(cam.current.x / SIZE_PER_REGION);
		const centerZ = Math.floor(cam.current.z / SIZE_PER_REGION);
		const countX = Math.ceil(canvas.width / scale.current / SIZE_PER_REGION) + 2;
		const countZ = Math.ceil(canvas.height / scale.current / SIZE_PER_REGION) + 2;
		const topLeftX = centerX - Math.floor(countX / 2);
		const topLeftZ = centerZ - Math.floor(countZ / 2);
		const screenCenterX = Math.floor(canvas.width / 2);
		const screenCenterZ = Math.floor(canvas.height / 2);

		const visible: { x: number; z: number; ix: number; iz: number }[] = [];
		for (let x = 0; x < countX; x++)
			for (let z = 0; z < countZ; z++)
				visible.push({ x: topLeftX + x, z: topLeftZ + z, ix: x, iz: z });

		visible.forEach((region) => {
			const key = `${region.x}:${region.z}`;

			if (regions[key] === undefined) {
				regions[key] = null;
				taskQueue.push(() => {
					fetch(`/api/w/${cameraPos.world}/region/${region.x}:${region.z}`).then(
						(res) => {
							if (res.status == 404) return (regions[key] = null);
							if (!res.ok) return setTimeout(() => delete regions[key], 10 * 1000);
							res.json().then((data: Region) => {
								regions[key] = data;
								cachedRegions[key] = createRegionImage(data);
							});
						}
					);
				});
			}

			if (regions[key] === null) return;
			const cached = cachedRegions[key];
			if (!cached) return;

			const worldX = region.x * SIZE_PER_REGION;
			const worldZ = region.z * SIZE_PER_REGION;
			const screenX = screenCenterX + scale.current * (worldX - cam.current.x);
			const screenZ = screenCenterZ + scale.current * (worldZ - cam.current.z);

			ctx.imageSmoothingEnabled = false;
			ctx.drawImage(
				cached,
				screenX,
				screenZ,
				scale.current * SIZE_PER_REGION,
				scale.current * SIZE_PER_REGION
			);

			if (region.x == 0 && region.z == 0) {
				ctx.fillStyle = 'rgba(255, 0, 0, 0.3)';
				ctx.fillRect(
					screenX,
					screenZ,
					scale.current * SIZE_PER_REGION,
					scale.current * SIZE_PER_REGION
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
		cam.set(
			{
				x: cam.target.x - x / scale.current,
				z: cam.target.z - y / scale.current
			},
			{ duration: !smooth ? 0 : undefined }
		);
	};
	const onZoom = (delta: number, centerX: number, centerY: number) => {
		const screenCenterX = canvas!.width / 2;
		const screenCenterZ = canvas!.height / 2;

		const worldX = cam.current.x + (centerX - screenCenterX) / scale.current;
		const worldZ = cam.current.z + (centerY - screenCenterZ) / scale.current;
		const newScale = Math.min(Math.max(scale.current + delta, 1 / 16), maxScale);

		cam.set({
			x: worldX - (centerX - screenCenterX) / newScale,
			z: worldZ - (centerY - screenCenterZ) / newScale
		});
		scale.set(newScale);
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
	onmousedown={() => (isClicking = true)}
	onmouseup={() => (isClicking = false)}
	onmouseleave={() => (isClicking = false)}
	ontouchstart={(e) => {
		isClicking = true;
		lastTouch = { x: e.touches[0].clientX, y: e.touches[0].clientY };
	}}
	ontouchend={() => ((isClicking = false), (lastTouch = null), (lastZoomDistance = null))}
	onresize={() => forceRerender()}
></canvas>
