package dev.tizu.craftmaps.abstraction;

import java.net.URL;
import java.util.UUID;

import dev.tizu.craftmaps.positions.Position;

public interface CMPlayer {
	String getName();

	UUID getUUID();

	URL getSkinData();

	Position getPosition();
}
