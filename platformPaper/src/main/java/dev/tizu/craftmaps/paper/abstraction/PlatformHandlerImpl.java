package dev.tizu.craftmaps.paper.abstraction;

import dev.tizu.craftmaps.abstraction.CMChunk;
import dev.tizu.craftmaps.paper.ThisPlugin;
import dev.tizu.craftmaps.platform.PlatformHandler;
import dev.tizu.craftmaps.positions.ChunkPosition;

public class PlatformHandlerImpl implements PlatformHandler {
	@Override
	public CMChunk getChunkAt(ChunkPosition pos) {
		return getChunkAt(pos, false);
	}

	@Override
	public CMChunk getChunkAt(ChunkPosition pos, boolean generateIfAbsent) {
		var world = ThisPlugin.i().getServer().getWorld(pos.world());
		if (world == null)
			return null;
		if (!world.isChunkGenerated(pos.x(), pos.z()) && !generateIfAbsent)
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
