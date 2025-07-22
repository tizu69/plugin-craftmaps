package dev.tizu.craftmaps;

import java.util.HashMap;
import java.util.Map;

import dev.tizu.craftmaps.abstraction.CMBlock;
import dev.tizu.craftmaps.abstraction.CMChunk;
import dev.tizu.craftmaps.positions.BlockPosition;
import dev.tizu.craftmaps.positions.ChunkPosition;
import dev.tizu.craftmaps.positions.RegionPosition;

public class CraftMapsMap {
	private CraftMaps parent;
	private Map<ChunkPosition, CMChunk> chunks = new HashMap<>();

	protected CraftMapsMap(CraftMaps parent) {
		this.parent = parent;
	}

	public CMChunk getChunk(ChunkPosition pos) {
		var chunk = chunks.get(pos);
		if (Math.abs(pos.x()) > 1875000 || Math.abs(pos.z()) > 1875000)
			return null;
		if (chunk == null) {
			chunk = parent.plat().getChunkAt(pos, true);
			setChunk(pos, chunk);
		}
		return chunk;
	}

	public CMChunk[] getChunks(ChunkPosition pos, int sizeX, int sizeZ) {
		CMChunk[] chosen = new CMChunk[sizeX * sizeZ];
		for (int x = 0; x < sizeX; x++)
			for (int z = 0; z < sizeZ; z++)
				chosen[x * sizeZ + z] = getChunk(new ChunkPosition(pos.x() + x,
						pos.z() + z, pos.world()));
		return chosen;
	}

	public CMChunk[] getRegion(RegionPosition pos) {
		return getChunks(pos.getChunkPositionTL(), RegionPosition.REGION_SIZE,
				RegionPosition.REGION_SIZE);
	}

	public void setChunk(ChunkPosition pos, CMChunk data) {
		chunks.put(pos, data);
	}

	/** Will discard the block if the chunk is not loaded. */
	public void setBlock(BlockPosition pos, CMBlock block, String world) {
		var data = getChunk(pos.getChunkPosition(world));
		if (data == null)
			return;
		data.setAt(pos, block);
	}
}
