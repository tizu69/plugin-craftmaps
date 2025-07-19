package dev.tizu.craftmaps.paper;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import dev.tizu.craftmaps.paper.abstraction.ChunkImpl;
import dev.tizu.craftmaps.positions.ChunkPosition;

public class MapListener implements Listener {
	protected MapListener() {
	}

	@EventHandler
	public static void onChunkLoaded(ChunkLoadEvent event) {
		var chunk = event.getChunk();
		ThisPlugin.cm().getMap().setChunk(new ChunkPosition(chunk.getX(), chunk.getZ(),
				chunk.getWorld().getKey().asString()), new ChunkImpl(chunk));
	}
}
