package dev.tizu.craftmaps;

import java.util.logging.Logger;

import dev.tizu.craftmaps.api.ApiChunk;
import dev.tizu.craftmaps.api.ApiPlayer;
import dev.tizu.craftmaps.api.ApiServer;
import dev.tizu.craftmaps.platform.PlatformHandler;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class CraftMaps {
	protected Logger logger;
	private Javalin server;
	private PlatformHandler platformHandler;

	public CraftMaps(Logger logger, PlatformHandler platformHandler) {
		this.logger = logger;
		this.platformHandler = platformHandler;

		var apiChunk = new ApiChunk(this);
		var apiPlayer = new ApiPlayer(this);
		var apiServer = new ApiServer(this);
		server = Javalin.create(cfg -> {
			cfg.showJavalinBanner = false;
			cfg.staticFiles.add(staticFiles -> {
				staticFiles.hostedPath = "/";
				staticFiles.directory = "/cmweb";
				staticFiles.location = Location.CLASSPATH;
			});
		})
				.get("/api/worlds", apiServer::getWorlds)
				.get("/api/w/{world}/chunk/{x}:{z}", apiChunk::getChunk)
				.get("/api/w/{world}/region/{x}:{z}", apiChunk::getRegion)
				.get("/api/w/{world}/players", apiPlayer::getPlayers);

		logger.info("Initializing new CraftMaps instance");
	}

	protected Javalin getServer() {
		return server;
	}

	public void startWeb(int port) {
		server.start(port);
	}

	public PlatformHandler plat() {
		return platformHandler;
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
