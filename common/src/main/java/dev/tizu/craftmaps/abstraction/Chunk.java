package dev.tizu.craftmaps.abstraction;

public interface Chunk {
	ChunkPosition getPosition();

	Block getAt(BlockPosition pos);

	void setAt(BlockPosition pos, Block block);

	Block[][] getBlocks();

	public static int REGION_SIZE = 16;

	/** Coordinates of the chunk */
	public static record ChunkPosition(int x, int z, String world) {
		public boolean isWithin(BlockPosition pos) {
			var chunkPos = pos.getChunkPosition(world);
			return chunkPos.x == x && chunkPos.z == z;
		}
	}

	/** Coordinates of the block, not chunk-relative */
	public static record BlockPosition(int x, int z) {
		/** Get the coordinates of the chunk */
		public ChunkPosition getChunkPosition(String world) {
			return new ChunkPosition(x / 16, z / 16, world);
		}
	}

	/** Coordinates of a region. NOT A MINECRAFT-SIZED REGION! */
	public static record RegionPosition(int x, int z, String world) {
		/** Get the coordinates of the top-left chunk */
		public ChunkPosition getChunkPositionTL() {
			return new ChunkPosition(x * REGION_SIZE, z * REGION_SIZE, world);
		}

		/** Get the coordinates of the bottom-right chunk */
		public ChunkPosition getChunkPositionBR() {
			return new ChunkPosition(x * REGION_SIZE + 31, z * REGION_SIZE + 31, world);
		}

		public static RegionPosition fromChunk(ChunkPosition c) {
			return new RegionPosition(Math.floorDiv(c.x, REGION_SIZE),
					Math.floorDiv(c.z, REGION_SIZE), c.world);
		}
	}
}
