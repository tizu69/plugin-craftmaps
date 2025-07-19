package dev.tizu.craftmaps.abstraction;

import dev.tizu.craftmaps.positions.BlockPosition;
import dev.tizu.craftmaps.positions.ChunkPosition;

public interface CMChunk {
	ChunkPosition getPosition();

	CMBlock getAt(BlockPosition pos);

	void setAt(BlockPosition pos, CMBlock block);

	CMBlock[][] getBlocks();

}
