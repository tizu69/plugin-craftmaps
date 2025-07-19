package dev.tizu.craftmaps.paper.abstraction;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.NamespacedKey;

import dev.tizu.craftmaps.abstraction.CMChunk;
import dev.tizu.craftmaps.abstraction.CMPlayer;
import dev.tizu.craftmaps.paper.ThisPlugin;
import dev.tizu.craftmaps.platform.PlatformHandler;
import dev.tizu.craftmaps.positions.ChunkPosition;

public class PlatformHandlerImpl implements PlatformHandler {
	@Override
	public CMChunk getChunkAt(ChunkPosition pos, boolean attemptLoad) {
		var world = ThisPlugin.i().getServer().getWorld(NamespacedKey.fromString(pos.world()));
		if (world == null)
			return null;
		if (!world.isChunkLoaded(pos.x(), pos.z()) && !attemptLoad)
			return null;
		if (!world.isChunkGenerated(pos.x(), pos.z()))
			return null;
		var chunk = world.getChunkAt(pos.x(), pos.z());
		return new ChunkImpl(chunk);
	}

	@Override
	public String[] getWorlds() {
		return ThisPlugin.i().getServer().getWorlds().stream()
				.map(w -> w.getKey().asString()).toArray(String[]::new);
	}

	@Override
	public List<CMPlayer> getPlayers(String world) {
		var worldData = ThisPlugin.i().getServer().getWorld(NamespacedKey.fromString(world));
		if (worldData == null)
			return List.of();
		return new ArrayList<>(worldData.getPlayers().stream().map(PlayerImpl::new).toList());
	}
}
