package dev.tizu.craftmaps.positions;

/** Coordinates of a region. NOT A MINECRAFT-SIZED REGION! */
public record RegionPosition(int x, int z, String world) {
	public static final int REGION_SIZE = 16;

	/** Get the coordinates of the top-left chunk */
	public ChunkPosition getChunkPositionTL() {
		return new ChunkPosition(x * REGION_SIZE, z * REGION_SIZE, world);
	}

	/** Get the coordinates of the bottom-right chunk */
	public ChunkPosition getChunkPositionBR() {
		return new ChunkPosition(x * REGION_SIZE + 31, z * REGION_SIZE + 31, world);
	}

	public static RegionPosition fromChunk(ChunkPosition c) {
		return new RegionPosition(Math.floorDiv(c.x(), REGION_SIZE),
				Math.floorDiv(c.z(), REGION_SIZE), c.world());
	}
}
