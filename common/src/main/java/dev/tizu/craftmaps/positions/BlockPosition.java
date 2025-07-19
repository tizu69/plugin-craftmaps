package dev.tizu.craftmaps.positions;

/** Coordinates of the block, not chunk-relative */
public record BlockPosition(int x, int z) {
	/** Get the coordinates of the chunk */
	public ChunkPosition getChunkPosition(String world) {
		return new ChunkPosition(x / 16, z / 16, world);
	}
}
