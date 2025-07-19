package dev.tizu.craftmaps.paper.abstraction;

import java.awt.Color;
import java.util.List;

import org.bukkit.Chunk;

import dev.tizu.craftmaps.abstraction.CMBlock;
import dev.tizu.craftmaps.abstraction.CMChunk;
import dev.tizu.craftmaps.platform.BlockColor;
import dev.tizu.craftmaps.positions.BlockPosition;
import dev.tizu.craftmaps.positions.ChunkPosition;

public class ChunkImpl implements CMChunk {
	private ChunkPosition pos;
	private int[][] blocks = CMChunk.generateDefaultBlocks();
	private List<CMBlock> palette = CMChunk.generateDefaultPalette();

	public ChunkImpl(Chunk chunk) {
		pos = new ChunkPosition(chunk.getX(), chunk.getZ(), chunk.getWorld().getKey().asString());
		var world = chunk.getWorld();
		for (int x = 0; x < 16; x++)
			for (int z = 0; z < 16; z++) {
				var block = world.getHighestBlockAt(pos.x() * 16 + x, pos.z() * 16 + z);
				var color = getMapColor(block.getBlockData().getMapColor());
				var id = block.getType().getKey().asString();
				this.setAt(new BlockPosition(x, z), new CMBlock(id, color, block.getY()));
			}
	}

	private BlockColor getMapColor(org.bukkit.Color c) {
		var awtColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
		return BlockColor.of(awtColor);
	}

	@Override
	public ChunkPosition getPosition() {
		return pos;
	}

	@Override
	public CMBlock getAt(BlockPosition pos) {
		return palette.get(blocks[pos.x()][pos.z()]);
	}

	@Override
	public void setAt(BlockPosition pos, CMBlock block) {
		var index = palette.indexOf(block);
		if (index == -1) {
			palette.add(block);
			index = palette.size() - 1;
		}
		blocks[pos.x()][pos.z()] = index;
	}

	@Override
	public CMBlock[] getPalette() {
		return palette.toArray(new CMBlock[0]);
	}

	@Override
	public int[][] getBlocks() {
		return blocks;
	}
}
