import { c, colors, Queue } from '$lib';
import { expoOut } from 'svelte/easing';
import { Tween } from 'svelte/motion';
import { SvelteMap } from 'svelte/reactivity';

export default class MapCore {
	readonly cam = new Tween({ x: 0, z: 0, scale: 1 }, { easing: expoOut, duration: 700 });
	readonly #regions: c.Regions = new SvelteMap();
	readonly world: string;
	readonly #renderCache: Map<string, HTMLCanvasElement> = new SvelteMap();
	readonly #queue = new Queue(10);

	constructor(world: string) {
		this.world = world;
		window.mapcore = this;
	}

	getRegion(x: number, z: number): c.Region | null {
		const r = this.#regions.get(`${x}:${z}`);
		if (r === undefined) this.downloadRegion(x, z);
		return r || null;
	}

	getChunk(x: number, z: number, regionX?: number, regionZ?: number): c.Chunk | null {
		let chunkX, chunkZ;

		if (regionX !== undefined && regionZ !== undefined) {
			// x,z are relative to the specified region
			chunkX = x;
			chunkZ = z;
		} else {
			// x,z are global coordinates, convert to chunk coordinates
			chunkX = Math.floor(x / c.CHUNK_BLOCKS);
			chunkZ = Math.floor(z / c.CHUNK_BLOCKS);
			// Calculate which region contains this chunk
			regionX = Math.floor(chunkX / c.REGION_CHUNKS);
			regionZ = Math.floor(chunkZ / c.REGION_CHUNKS);
			// Make chunk coordinates relative to the region
			chunkX = chunkX - regionX * c.REGION_CHUNKS;
			chunkZ = chunkZ - regionZ * c.REGION_CHUNKS;
		}

		const region = this.getRegion(regionX, regionZ);
		if (!region) return null;
		return region[chunkX * c.REGION_CHUNKS + chunkZ] || null;
	}

	getBlock(
		x: number,
		z: number,
		chunkX?: number,
		chunkZ?: number,
		regionX?: number,
		regionZ?: number
	): c.Block | null {
		let blockX, blockZ;

		if (chunkX !== undefined && chunkZ !== undefined) {
			// x,z are relative to the specified chunk
			blockX = x;
			blockZ = z;

			if (regionX !== undefined && regionZ !== undefined) {
				// chunkX,chunkZ are relative to the specified region
				// No additional calculation needed for chunk coordinates
			} else {
				// chunkX,chunkZ are global chunk coordinates
				// Calculate which region contains this chunk
				regionX = Math.floor(chunkX / c.REGION_CHUNKS);
				regionZ = Math.floor(chunkZ / c.REGION_CHUNKS);
				// Make chunk coordinates relative to the region
				chunkX = chunkX - regionX * c.REGION_CHUNKS;
				chunkZ = chunkZ - regionZ * c.REGION_CHUNKS;
			}
		} else {
			// x,z are global coordinates
			chunkX = Math.floor(x / c.CHUNK_BLOCKS);
			chunkZ = Math.floor(z / c.CHUNK_BLOCKS);
			regionX = Math.floor(chunkX / c.REGION_CHUNKS);
			regionZ = Math.floor(chunkZ / c.REGION_CHUNKS);

			// Make coordinates relative to their containers
			blockX = x - chunkX * c.CHUNK_BLOCKS;
			blockZ = z - chunkZ * c.CHUNK_BLOCKS;
			chunkX = chunkX - regionX * c.REGION_CHUNKS;
			chunkZ = chunkZ - regionZ * c.REGION_CHUNKS;
		}

		const chunk = this.getChunk(chunkX, chunkZ, regionX, regionZ);
		if (!chunk) return null;

		return chunk.palette[chunk.blocks[blockX][blockZ]] || null;
	}

	viewportToWorld(x: number, y: number): number[] {
		return [
			this.cam.current.x + x / this.cam.current.scale,
			this.cam.current.z + y / this.cam.current.scale
		];
	}

	downloadRegion(x: number, z: number) {
		const key = `${x}:${z}`;
		if (this.#regions.has(key)) return;
		this.#regions.set(key, null);
		this.#queue.push(() => {
			const dataHandler = (data: c.Region) => {
				this.#regions.set(key, data);
				this.#queue.push(() => this.renderRegion(x, z));
			};
			fetch(`/api/w/${this.world}/region/${key}`).then((res) => {
				if (res.status == 404) return this.#regions.set(key, null);
				if (!res.ok) return setTimeout(() => this.#regions.delete(key), 10 * 1000);
				res.json().then(dataHandler);
			});
		});
	}
	renderRegion(rX: number, rZ: number): HTMLCanvasElement | null {
		if (this.#renderCache.has(`${rX}:${rZ}`)) return this.#renderCache.get(`${rX}:${rZ}`)!;
		if (!this.#regions.has(`${rX}:${rZ}`)) return null;

		const regionCanvas = document.createElement('canvas');
		regionCanvas.width = c.REGION_BLOCKS;
		regionCanvas.height = c.REGION_BLOCKS;

		const ctx = regionCanvas.getContext('2d', { willReadFrequently: true })!;
		ctx.clearRect(0, 0, c.REGION_BLOCKS, c.REGION_BLOCKS);

		for (let cx = 0; cx < c.REGION_CHUNKS; cx++)
			for (let cz = 0; cz < c.REGION_CHUNKS; cz++) {
				ctx.fillStyle = cx % 2 == cz % 2 ? `#1e1e2e` : '#000000';
				ctx.fillRect(
					cx * c.CHUNK_BLOCKS,
					cz * c.CHUNK_BLOCKS,
					c.CHUNK_BLOCKS,
					c.CHUNK_BLOCKS
				);

				for (let x = 0; x < c.CHUNK_BLOCKS; x++)
					for (let z = 0; z < c.CHUNK_BLOCKS; z++) {
						const block = this.getBlock(x, z, cx, cz, rX, rZ);
						if (!block) {
							ctx.fillStyle = '#ffaaff';
							ctx.fillRect(cx * c.CHUNK_BLOCKS + x, cz * c.CHUNK_BLOCKS + z, 1, 1);
							continue;
						}

						let below = this.getBlock(x, z - 1, cx, cz, rX, rZ);
						let shade: colors.ColorShades =
							!below || below?.y === block.y ? 0 : below?.y < block.y ? 1 : -1;

						// ctx.fillStyle = colors.get(block.color, shade);
						ctx.fillStyle = colors.getContour(block.color, block.y, 3, 3);
						ctx.fillRect(cx * c.CHUNK_BLOCKS + x, cz * c.CHUNK_BLOCKS + z, 1, 1);
					}
			}

		this.#renderCache.set(`${rX}:${rZ}`, regionCanvas);
		return regionCanvas;
	}
}
