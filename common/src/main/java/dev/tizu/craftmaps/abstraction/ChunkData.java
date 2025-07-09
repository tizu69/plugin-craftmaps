package dev.tizu.craftmaps.abstraction;

public interface ChunkData {
	/** @return X coordinate of the chunk */
	int getX();

	/** @return Z coordinate of the chunk */
	int getZ();

	/**
	 * @param x Chunk-relative X coordinate
	 * @param z Chunk-relative Z coordinate
	 * @return The top-most map-visible Block
	 */
	Block getAt(int x, int z);
}
