package dev.tizu.craftmaps;

import java.util.HashMap;
import java.util.Map;

import dev.tizu.craftmaps.abstraction.Block;
import dev.tizu.craftmaps.abstraction.Chunk;
import dev.tizu.craftmaps.abstraction.Chunk.BlockPosition;
import dev.tizu.craftmaps.abstraction.Chunk.ChunkPosition;

public class CraftMapsMap {
	private CraftMaps parent;
	private Map<ChunkPosition, Chunk> chunks = new HashMap<>();

	protected CraftMapsMap(CraftMaps parent) {
		this.parent = parent;
	}

	public Chunk getChunk(ChunkPosition pos) {
		var chunk = chunks.get(pos);
		if (Math.abs(pos.x()) > 1875000 || Math.abs(pos.z()) > 1875000)
			return null;
		if (chunk == null) {
			chunk = parent.plat().getChunkAt(pos);
			setChunk(pos, chunk);
		}
		return chunk;
	}

	public void setChunk(ChunkPosition pos, Chunk data) {
		chunks.put(pos, data);
	}

	/** Will discard the block if the chunk is not loaded. */
	public void setBlock(BlockPosition pos, Block block, String world) {
		var data = getChunk(pos.getChunkPosition(world));
		if (data == null)
			return;
		data.setAt(pos, block);
	}
}
