package dev.tizu.craftmaps.paper;

import org.bukkit.plugin.java.JavaPlugin;

import dev.tizu.craftmaps.CraftMaps;

public class ThisPlugin extends JavaPlugin {
	private static ThisPlugin instance;
	private CraftMaps cm;

	public static ThisPlugin i() {
		return instance;
	}

	public CraftMaps getCM() {
		return cm;
	}

	@Override
	public void onEnable() {
		instance = this;
		cm = new CraftMaps(this.getLogger());
	}
}
