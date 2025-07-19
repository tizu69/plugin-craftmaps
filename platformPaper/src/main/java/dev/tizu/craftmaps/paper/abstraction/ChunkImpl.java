package dev.tizu.craftmaps.paper.abstraction;

import java.awt.Color;

import org.bukkit.Chunk;
import org.bukkit.HeightMap;
import org.bukkit.map.MapPalette;

import dev.tizu.craftmaps.abstraction.CMBlock;
import dev.tizu.craftmaps.abstraction.CMChunk;
import dev.tizu.craftmaps.platform.BlockColor;
import dev.tizu.craftmaps.positions.BlockPosition;
import dev.tizu.craftmaps.positions.ChunkPosition;

public class ChunkImpl implements CMChunk {
	private ChunkPosition pos;
	private CMBlock[][] blocks = CMBlock.generateDefault();

	public ChunkImpl(Chunk chunk) {
		pos = new ChunkPosition(chunk.getX(), chunk.getZ(), chunk.getWorld().getKey().asString());
		var world = chunk.getWorld();
		for (int x = 0; x < 16; x++)
			for (int z = 0; z < 16; z++) {
				var block = world.getHighestBlockAt(pos.x() * 16 + x, pos.z() * 16 + z,
						HeightMap.WORLD_SURFACE);
				var color = getMapColor(block.getBlockData().getMapColor());
				var id = block.getType().getKey().asString();
				blocks[x][z] = new CMBlock(id, color, block.getY());
			}
	}

	@SuppressWarnings("removal")
	private BlockColor getMapColor(org.bukkit.Color c) {
		var awtColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
		var index = MapPalette.matchColor(awtColor);
		return BlockColor.of(index == -1 ? 0 : Math.floorDiv(index, 4));
	}

	@Override
	public ChunkPosition getPosition() {
		return pos;
	}

	@Override
	public CMBlock getAt(BlockPosition pos) {
		return blocks[pos.x()][pos.z()];
	}

	@Override
	public void setAt(BlockPosition pos, CMBlock block) {
		blocks[pos.x()][pos.z()] = block;
	}

	@Override
	public CMBlock[][] getBlocks() {
		return blocks;
	}
}
