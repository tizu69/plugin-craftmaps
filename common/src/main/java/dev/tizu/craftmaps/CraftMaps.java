package dev.tizu.craftmaps;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import dev.tizu.craftmaps.abstraction.Block;
import dev.tizu.craftmaps.abstraction.Chunk;
import dev.tizu.craftmaps.abstraction.Chunk.BlockPosition;
import dev.tizu.craftmaps.abstraction.Chunk.ChunkPosition;
import io.javalin.Javalin;

public class CraftMaps {
	private Map<ChunkPosition, Chunk> chunks = new HashMap<>();
	protected Logger logger;
	private Javalin server;

	public CraftMaps(Logger logger) {
		this.logger = logger;
		server = Javalin.create();
		logger.info("Initializing new CraftMaps instance");
	}

	/** DO NOT USE UNLESS YOU KNOW WHAT YOU ARE DOING! */
	public Javalin getServer() {
		return server;
	}

	public Chunk getChunk(ChunkPosition pos) {
		return chunks.get(pos);
	}

	public void setChunk(ChunkPosition pos, Chunk data) {
		chunks.put(pos, data);
	}

	/** Will discard the block if the chunk is not loaded. */
	public void setBlock(BlockPosition pos, Block block) {
		var data = getChunk(pos.getChunkPosition());
		if (data == null)
			return;
		data.setAt(pos, block);
	}
}
