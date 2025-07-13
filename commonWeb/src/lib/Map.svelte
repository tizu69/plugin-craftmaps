<script lang="ts">
	import { colors } from '$lib';
	import { onMount } from 'svelte';
	import { Tween } from 'svelte/motion';
	import type { BlockColor } from './colors';

	interface Block {
		name: string;
		id: string;
		color: BlockColor;
		y: number;
	}

	let {
		scale = $bindable(),
		pos: cameraPos = $bindable()
	}: {
		scale: number;
		pos: { x: number; z: number; world: string };
	} = $props();
	const scaleLimit = [1, 10];

	const display = new Tween({ scale, x: cameraPos.x, z: cameraPos.z });
	$effect(() => {
		display.set({ scale, x: cameraPos.x, z: cameraPos.z });
	});

	let chunks: Record<string, '...' | Block[][]> = $state({});
	let canvas = $state<HTMLCanvasElement>();
	let cachedChunks = $state<Record<string, HTMLCanvasElement>>({});

	let taskQueue = $state<(() => void)[]>([]);
	onMount(() => {
		setInterval(() => {
			if (taskQueue.length) taskQueue.shift()!();
		}, 1);
	});

	const createChunkImage = (chunkData: Block[][]): HTMLCanvasElement => {
		const chunkCanvas = document.createElement('canvas');
		chunkCanvas.width = 16;
		chunkCanvas.height = 16;

		const ctx = chunkCanvas.getContext('2d', { willReadFrequently: true })!;
		ctx.clearRect(0, 0, 16, 16);

		for (let x = 0; x < 16; x++) {
			for (let z = 0; z < 16; z++) {
				const block = chunkData[x][z];
				if (!block || block.color == 'NONE') continue;

				ctx.fillStyle = `rgb(${colors.get(block.color).join(',')})`;
				ctx.fillRect(x, z, 1, 1);
			}
		}

		return chunkCanvas;
	};
	$effect(() => {
		if (!canvas) return;
		const ctx = canvas.getContext('2d', { willReadFrequently: true })!;

		canvas.width = canvas.clientWidth;
		canvas.height = canvas.clientHeight;

		ctx.fillStyle = '#000000';
		ctx.fillRect(0, 0, canvas.width, canvas.height);

		const centerChunk = { x: Math.floor(cameraPos.x / 16), z: Math.floor(cameraPos.z / 16) };
		const chunkBlock = { x: ((cameraPos.x % 16) + 16) % 16, z: ((cameraPos.z % 16) + 16) % 16 };
		const chunkCount = {
			x: Math.ceil(canvas.width / scale / 16) + 2,
			z: Math.ceil(canvas.height / scale / 16) + 2
		};
		const topLeftChunk = {
			x: centerChunk.x - Math.floor(chunkCount.x / 2),
			z: centerChunk.z - Math.floor(chunkCount.z / 2)
		};

		const visibleChunks: { x: number; z: number; ix: number; iz: number }[] = [];
		for (let x = 0; x < chunkCount.x; x++)
			for (let z = 0; z < chunkCount.z; z++)
				visibleChunks.push({ x: topLeftChunk.x + x, z: topLeftChunk.z + z, ix: x, iz: z });

		visibleChunks.forEach((chunk) => {
			const chunkKey = `${chunk.x}:${chunk.z}`;

			if (!chunks[chunkKey]) {
				chunks[chunkKey] = '...';
				taskQueue.push(() => {
					fetch(`/api/${cameraPos.world}/chunk/${chunk.x}/${chunk.z}`)
						.then((res) => (res.ok ? res.json() : null))
						.then((data) => {
							if (!data) return;
							chunks[chunkKey] = data.blocks;
							cachedChunks[chunkKey] = createChunkImage(data.blocks);
						});
				});
			}

			const data = chunks[chunkKey];
			if (data === '...') return;
			const cached = cachedChunks[chunkKey];
			if (!cached) return;

			const screenX = display.current.scale * (chunk.ix * 16 - 16 - chunkBlock.x);
			const screenZ = display.current.scale * (chunk.iz * 16 - 16 - chunkBlock.z);

			ctx.imageSmoothingEnabled = false;
			ctx.drawImage(
				cached,
				screenX,
				screenZ,
				display.current.scale * 16,
				display.current.scale * 16
			);

			if (chunk.x == 0 && chunk.z == 0) {
				ctx.fillStyle = 'rgba(255, 0, 0, 0.3)';
				ctx.fillRect(
					screenX,
					screenZ,
					display.current.scale * 16,
					display.current.scale * 16
				);
			}
		});
	});

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
		scale = Math.round(
			Math.min(Math.max(scale + -e.deltaY / 100, scaleLimit[0]), scaleLimit[1])
		);
	}}
	onmousedown={() => {
		isClicking = true;
	}}
	onmouseup={() => {
		isClicking = false;
	}}
></canvas>
