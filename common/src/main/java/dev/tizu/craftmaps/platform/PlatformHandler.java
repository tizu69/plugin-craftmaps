package dev.tizu.craftmaps.platform;

import dev.tizu.craftmaps.abstraction.CMChunk;
import dev.tizu.craftmaps.positions.ChunkPosition;

public interface PlatformHandler {
	CMChunk getChunkAt(ChunkPosition pos);

	CMChunk getChunkAt(ChunkPosition pos, boolean generateIfAbsent);

	String[] getWorlds();
}
