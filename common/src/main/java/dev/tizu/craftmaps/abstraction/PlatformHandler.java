package dev.tizu.craftmaps.abstraction;

import dev.tizu.craftmaps.abstraction.Chunk.ChunkPosition;

public interface PlatformHandler {
	Chunk getChunkAt(ChunkPosition pos);

	String[] getWorlds();
}
