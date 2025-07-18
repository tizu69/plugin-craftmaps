<script lang="ts">
	import { colors } from '$lib';
	import { onMount } from 'svelte';
	import { expoOut } from 'svelte/easing';
	import { Tween } from 'svelte/motion';
	import type { BlockColor } from './colors';

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
		blocks: Block[][];
	}
	type Region = Chunk[][];

	let {
		scale = $bindable(),
		pos: cameraPos = $bindable()
	}: {
		scale: number;
		pos: { x: number; z: number; world: string };
	} = $props();
	const maxScale = 16;

	const display = new Tween(
		{ scale, x: cameraPos.x, z: cameraPos.x + 100 },
		{ easing: expoOut, duration: 700 }
	);
	$effect(() => {
		display.set({ scale, x: cameraPos.x, z: cameraPos.z });
	});

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
				ctx.fillStyle = cx % 2 == cz % 2 ? `#1e1e2e` : '#000000';
				ctx.fillRect(cx * CHUNK_SIZE, cz * CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE);

				for (let x = 0; x < CHUNK_SIZE; x++)
					for (let z = 0; z < CHUNK_SIZE; z++) {
						const block = region[cx]?.[cz]?.blocks?.[x]?.[z];
						if (!block || block.color == 'NONE') continue;

						ctx.fillStyle = `rgb(${colors.get(block.color).join(',')})`;
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

		const scale = display.current.scale;

		const centerX = Math.floor(display.current.x / SIZE_PER_REGION);
		const centerZ = Math.floor(display.current.z / SIZE_PER_REGION);
		const countX = Math.ceil(canvas.width / scale / SIZE_PER_REGION) + 2;
		const countZ = Math.ceil(canvas.height / scale / SIZE_PER_REGION) + 2;
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
					fetch(`/api/${cameraPos.world}/region/${region.x}/${region.z}`).then((res) => {
						if (res.status == 404) return (regions[key] = null);
						if (!res.ok) return setTimeout(() => delete regions[key], 10 * 1000);
						res.json().then((data: Region) => {
							regions[key] = data;
							cachedRegions[key] = createRegionImage(data);
						});
					});
				});
			}

			if (regions[key] === null) return;
			const cached = cachedRegions[key];
			if (!cached) return;

			const worldX = region.x * SIZE_PER_REGION;
			const worldZ = region.z * SIZE_PER_REGION;
			const screenX = screenCenterX + scale * (worldX - display.current.x);
			const screenZ = screenCenterZ + scale * (worldZ - display.current.z);

			ctx.imageSmoothingEnabled = false;
			ctx.drawImage(
				cached,
				screenX,
				screenZ,
				scale * SIZE_PER_REGION,
				scale * SIZE_PER_REGION
			);

			if (region.x == 0 && region.z == 0) {
				ctx.fillStyle = 'rgba(255, 0, 0, 0.3)';
				ctx.fillRect(screenX, screenZ, scale * SIZE_PER_REGION, scale * SIZE_PER_REGION);
			}
		});
	};
	$effect(() => forceRerender());

	let isClicking = false;
</script>

<canvas
	class="size-full"
	bind:this={canvas}
	onmousemove={(e) => {
		if (!isClicking) return;
		cameraPos = {
			x: cameraPos.x - e.movementX / scale,
			z: cameraPos.z - e.movementY / scale,
			world: cameraPos.world
		};
	}}
	onwheel={(e) => {
		const screenCenter = {
			x: canvas!.width / 2,
			z: canvas!.height / 2
		};

		const worldX = cameraPos.x + (e.clientX - screenCenter.x) / scale;
		const worldZ = cameraPos.z + (e.clientY - screenCenter.z) / scale;
		const newScale = Math.round(Math.min(Math.max(scale + Math.sign(-e.deltaY), 1), maxScale));
		console.log('scale', scale, 'delta', e.deltaY, 'new', newScale);

		cameraPos.x = worldX - (e.clientX - screenCenter.x) / newScale;
		cameraPos.z = worldZ - (e.clientY - screenCenter.z) / newScale;
		scale = newScale;
	}}
	onmousedown={() => (isClicking = true)}
	onmouseup={() => (isClicking = false)}
	onresize={() => forceRerender()}
></canvas>
