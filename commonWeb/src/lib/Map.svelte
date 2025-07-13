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
	const scaleLimit = [0.01, 10];

	const display = new Tween({ scale, x: cameraPos.x, z: cameraPos.z });
	$effect(() => {
		display.set({ scale, x: cameraPos.x, z: cameraPos.z });
	});

	let chunks: Record<string, '...' | Block[][]> = $state({});
	let chunkCache: Record<string, Block[]> = $state({});
	let canvas = $state<HTMLCanvasElement>();

	let taskQueue = $state<(() => void)[]>([]);
	onMount(() => {
		setInterval(() => {
			if (taskQueue.length) taskQueue.shift()!();
		}, 1);
	});

	$effect(() => {
		if (!canvas) return;
		const ctx = canvas.getContext('2d')!;

		canvas.width = canvas.clientWidth;
		canvas.height = canvas.clientHeight;

		ctx.fillStyle = '#000000';
		ctx.fillRect(0, 0, canvas.width, canvas.height);

		const centerChunk = { x: Math.floor(cameraPos.x / 16), z: Math.floor(cameraPos.z / 16) };
		const chunkBlock = { x: cameraPos.x % 16, z: cameraPos.z % 16 };
		const chunkCount = {
			x: Math.ceil(canvas.width / scale / 16) + 1,
			z: Math.ceil(canvas.height / scale / 16) + 1
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
					fetch(`/api/${cameraPos.world}/${chunk.x}/${chunk.z}`)
						.then((res) => (res.ok ? res.json() : null))
						.then((data) => {
							if (data) chunks[chunkKey] = data.blocks;
						});
				});
			}

			const data = chunks[chunkKey];
			if (data === '...') return;

			for (let x = 0; x < 16; x++)
				for (let z = 0; z < 16; z++) {
					const block = data[x][z];
					if (!block) continue;

					const screenX = display.current.scale * (x - chunkBlock.x + chunk.ix * 16 - 16);
					const screenZ = display.current.scale * (z - chunkBlock.z + chunk.iz * 16 - 16);

					ctx.fillStyle = `rgb(${colors.get(block.color).join(',')})`;
					if (chunk.x == 0 && chunk.z == 0) ctx.fillStyle = '#ff0000';
					ctx.fillRect(screenX, screenZ, display.current.scale, display.current.scale);
				}
		});
	});

	let isClicking = false;
	const handleMove = (e: MouseEvent) => {
		if (!isClicking) return;
		cameraPos = {
			x: cameraPos.x - e.movementX / scale,
			z: cameraPos.z - e.movementY / scale,
			world: cameraPos.world
		};
	};
</script>

<canvas
	class="size-full"
	bind:this={canvas}
	onmousemove={handleMove}
	onwheel={(e) => {
		scale = Math.ceil(
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
