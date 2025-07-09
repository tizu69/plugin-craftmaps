package dev.tizu.craftmaps;

import java.util.logging.Logger;

import io.javalin.Javalin;

public class CraftMaps {
	protected Logger logger;
	private Javalin server;

	public CraftMaps(Logger logger) {
		this.logger = logger;
		server = Javalin.create();
		logger.info("Initializing new CraftMaps instance");
	}

	protected Javalin getServer() {
		return server;
	}

	public void startWeb(int port) {
		server.start(port);
	}

	// #region API
	private CraftMapsMap map;

	public CraftMapsMap getMap() {
		if (map == null)
			map = new CraftMapsMap(this);
		return map;
	}
	// #endregion
}
