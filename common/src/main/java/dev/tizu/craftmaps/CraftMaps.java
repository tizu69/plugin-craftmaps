package dev.tizu.craftmaps;

import java.util.logging.Logger;

import dev.tizu.craftmaps.api.ApiChunk;
import io.javalin.Javalin;

public class CraftMaps {
	protected Logger logger;
	private Javalin server;

	public CraftMaps(Logger logger) {
		this.logger = logger;

		var apiChunk = new ApiChunk(this);
		server = Javalin.create(cfg -> {
			cfg.showJavalinBanner = false;
		})
				.get("/{world}/{x}/{z}", apiChunk::getChunk);

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
