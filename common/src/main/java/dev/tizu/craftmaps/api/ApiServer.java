package dev.tizu.craftmaps.api;

import dev.tizu.craftmaps.CraftMaps;
import io.javalin.http.Context;

public class ApiServer {
	private CraftMaps parent;

	public ApiServer(CraftMaps parent) {
		this.parent = parent;
	}

	public void getWorlds(Context ctx) {
		ctx.json(parent.plat().getWorlds());
	}
}
