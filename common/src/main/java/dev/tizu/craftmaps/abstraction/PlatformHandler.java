package dev.tizu.craftmaps.abstraction;

import dev.tizu.craftmaps.abstraction.Chunk.ChunkPosition;

public interface PlatformHandler {
	Chunk getChunkAt(ChunkPosition pos);

	Chunk getChunkAt(ChunkPosition pos, boolean generateIfAbsent);

	String[] getWorlds();
}
