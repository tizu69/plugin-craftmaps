import type { BlockColor } from './colors';

export const CHUNK_BLOCKS = 16;
export const REGION_CHUNKS = 32;
export const REGION_BLOCKS = REGION_CHUNKS * CHUNK_BLOCKS;

export type Block = {
	name: string;
	id: string;
	color: BlockColor;
	y: number;
};
export type Chunk = {
	pos: {
		x: number;
		z: number;
		world: string;
	};
	blocks: number[][];
	palette: Block[];
};
export type Region = Chunk[];
export type Regions = Map<string, Region | null>;
