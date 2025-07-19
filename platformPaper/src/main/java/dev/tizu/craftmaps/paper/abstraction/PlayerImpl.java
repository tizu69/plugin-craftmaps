package dev.tizu.craftmaps.paper.abstraction;

import java.net.URL;
import java.util.UUID;

import org.bukkit.entity.Player;

import dev.tizu.craftmaps.abstraction.CMPlayer;
import dev.tizu.craftmaps.positions.Position;

public class PlayerImpl implements CMPlayer {
	private Player player;

	public PlayerImpl(Player player) {
		this.player = player;
	}

	@Override
	public String getName() {
		return player.getName();
	}

	@Override
	public UUID getUUID() {
		return player.getUniqueId();
	}

	@Override
	public URL getSkinData() {
		return player.getPlayerProfile().getTextures().getSkin();
	}

	@Override
	public Position getPosition() {
		return new Position((int) player.getX(), (int) player.getY(), (int) player.getZ(),
				player.getWorld().getName());
	}
}
