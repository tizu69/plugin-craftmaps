package dev.tizu.craftmaps.paper;

import org.bukkit.plugin.java.JavaPlugin;
import dev.tizu.craftmaps.CraftMaps;

public class ThisPlugin extends JavaPlugin {
	private static ThisPlugin instance;

	public static ThisPlugin i() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;
		CraftMaps.test();
	}
}
