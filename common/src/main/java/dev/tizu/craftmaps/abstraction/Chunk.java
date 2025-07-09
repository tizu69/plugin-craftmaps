package dev.tizu.craftmaps.abstraction;

public interface Chunk {
	ChunkPosition getPosition();

	Block getAt(BlockPosition pos);

	void setAt(BlockPosition pos, Block block);

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
}
