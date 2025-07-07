package dev.tizu.craftmaps.paper;

import org.bukkit.plugin.java.JavaPlugin;
import dev.tizu.craftmaps.CraftMaps;

public class ThisPlugin extends JavaPlugin {
	public static ThisPlugin instance;

	@Override
	public void onEnable() {
		instance = this;
		CraftMaps.test();
	}
}
