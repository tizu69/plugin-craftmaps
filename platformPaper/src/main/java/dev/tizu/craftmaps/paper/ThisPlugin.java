package dev.tizu.craftmaps.paper;

import org.bukkit.plugin.java.JavaPlugin;

import dev.tizu.craftmaps.CraftMaps;
import dev.tizu.craftmaps.paper.abstraction.PlatformHandlerImpl;

public class ThisPlugin extends JavaPlugin {
	private static ThisPlugin instance;
	private CraftMaps cm;

	public static ThisPlugin i() {
		return instance;
	}

	public static CraftMaps cm() {
		return instance.cm;
	}

	@Override
	public void onEnable() {
		instance = this;
		cm = new CraftMaps(this.getLogger(), new PlatformHandlerImpl());
		this.getServer().getPluginManager().registerEvents(new MapListener(), this);
		cm.startWeb(8080);
	}
}
