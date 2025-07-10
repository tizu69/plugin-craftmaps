package dev.tizu.craftmaps.paper;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import dev.tizu.craftmaps.abstraction.Chunk;
import dev.tizu.craftmaps.paper.abstraction.ChunkImpl;

public class MapListener implements Listener {
	protected MapListener() {
	}

	@EventHandler
	public static void onChunkLoaded(ChunkLoadEvent event) {
		var chunk = event.getChunk();
		ThisPlugin.cm().getMap().setChunk(new Chunk.ChunkPosition(chunk.getX(), chunk.getZ(),
				chunk.getWorld().getName()), new ChunkImpl(chunk));
	}
}
