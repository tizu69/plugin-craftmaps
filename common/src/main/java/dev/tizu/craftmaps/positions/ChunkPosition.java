package dev.tizu.craftmaps.positions;

/** Coordinates of the chunk */
public record ChunkPosition(int x, int z, String world) {
	public boolean isWithin(BlockPosition pos) {
		var chunkPos = pos.getChunkPosition(world);
		return chunkPos.x == x && chunkPos.z == z;
	}
}
