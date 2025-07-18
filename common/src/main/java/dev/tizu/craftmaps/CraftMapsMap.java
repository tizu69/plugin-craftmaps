package dev.tizu.craftmaps;

import java.util.HashMap;
import java.util.Map;

import dev.tizu.craftmaps.abstraction.Block;
import dev.tizu.craftmaps.abstraction.Chunk;
import dev.tizu.craftmaps.abstraction.Chunk.BlockPosition;
import dev.tizu.craftmaps.abstraction.Chunk.ChunkPosition;
import dev.tizu.craftmaps.abstraction.Chunk.RegionPosition;

public class CraftMapsMap {
	private CraftMaps parent;
	private Map<ChunkPosition, Chunk> chunks = new HashMap<>();

	protected CraftMapsMap(CraftMaps parent) {
		this.parent = parent;
	}

	public Chunk getChunk(ChunkPosition pos) {
		var chunk = chunks.get(pos);
		if (Math.abs(pos.x()) > 1875000 || Math.abs(pos.z()) > 1875000)
			return null;
		if (chunk == null) {
			chunk = parent.plat().getChunkAt(pos);
			setChunk(pos, chunk);
		}
		return chunk;
	}

	public Chunk[][] getChunks(ChunkPosition pos, int sizeX, int sizeZ) {
		Chunk[][] chosen = new Chunk[sizeX][sizeZ];
		for (int x = 0; x < sizeX; x++)
			for (int z = 0; z < sizeZ; z++)
				chosen[x][z] = getChunk(new ChunkPosition(pos.x() + x, pos.z() + z, pos.world()));
		return chosen;
	}

	public Chunk[][] getRegion(RegionPosition pos) {
		return getChunks(pos.getChunkPositionTL(), Chunk.REGION_SIZE, Chunk.REGION_SIZE);
	}

	public void setChunk(ChunkPosition pos, Chunk data) {
		chunks.put(pos, data);
	}

	/** Will discard the block if the chunk is not loaded. */
	public void setBlock(BlockPosition pos, Block block, String world) {
		var data = getChunk(pos.getChunkPosition(world));
		if (data == null)
			return;
		data.setAt(pos, block);
	}
}
