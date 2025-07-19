package dev.tizu.craftmaps.api;

import dev.tizu.craftmaps.CraftMaps;
import io.javalin.http.Context;

public class ApiPlayer {
	private CraftMaps parent;

	public ApiPlayer(CraftMaps parent) {
		this.parent = parent;
	}

	public void getPlayers(Context ctx) {
		var world = ctx.pathParam("world");
		ctx.json(parent.plat().getPlayers(world));
	}
}
