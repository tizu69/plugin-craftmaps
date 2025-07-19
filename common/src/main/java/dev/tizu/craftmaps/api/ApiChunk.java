package dev.tizu.craftmaps.api;

import dev.tizu.craftmaps.CraftMaps;
import dev.tizu.craftmaps.positions.ChunkPosition;
import dev.tizu.craftmaps.positions.RegionPosition;
import io.javalin.http.Context;

public class ApiChunk {
	private CraftMaps parent;

	public ApiChunk(CraftMaps parent) {
		this.parent = parent;
	}

	public void getChunk(Context ctx) {
		var world = ctx.pathParam("world");
		var x = Integer.parseInt(ctx.pathParam("x"));
		var z = Integer.parseInt(ctx.pathParam("z"));

		var chunk = parent.getMap().getChunk(new ChunkPosition(x, z, world));
		if (chunk == null) {
			ctx.status(404);
			return;
		}

		ctx.json(chunk);
	}

	public void getRegion(Context ctx) {
		var world = ctx.pathParam("world");
		var x = Integer.parseInt(ctx.pathParam("x"));
		var z = Integer.parseInt(ctx.pathParam("z"));

		var region = parent.getMap().getRegion(new RegionPosition(x, z, world));
		ctx.json(region);
	}
}
