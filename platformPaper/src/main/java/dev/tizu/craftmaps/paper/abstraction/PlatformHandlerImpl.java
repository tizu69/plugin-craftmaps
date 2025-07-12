package dev.tizu.craftmaps.paper.abstraction;

import dev.tizu.craftmaps.abstraction.Chunk;
import dev.tizu.craftmaps.abstraction.Chunk.ChunkPosition;
import dev.tizu.craftmaps.paper.ThisPlugin;
import dev.tizu.craftmaps.abstraction.PlatformHandler;

public class PlatformHandlerImpl implements PlatformHandler {
	@Override
	public Chunk getChunkAt(ChunkPosition pos) {
		var world = ThisPlugin.i().getServer().getWorld(pos.world());
		if (world == null)
			return null;
		var chunk = world.getChunkAt(pos.x(), pos.z());
		return new ChunkImpl(chunk);
	}

	@Override
	public String[] getWorlds() {
		return ThisPlugin.i().getServer().getWorlds().stream()
				.map(w -> w.getName()).toArray(String[]::new);
	}
}
