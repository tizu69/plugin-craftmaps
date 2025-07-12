package dev.tizu.craftmaps.paper.abstraction;

import java.awt.Color;

import org.bukkit.HeightMap;
import org.bukkit.map.MapPalette;

import dev.tizu.craftmaps.abstraction.Block;
import dev.tizu.craftmaps.abstraction.BlockColor;
import dev.tizu.craftmaps.abstraction.Chunk;
import dev.tizu.craftmaps.utils.StringConversion;

public class ChunkImpl implements Chunk {
	private ChunkPosition pos;
	private Block[][] blocks = Block.generateDefault();

	public ChunkImpl(ChunkPosition pos) {
		this.pos = pos;
	}

	public ChunkImpl(org.bukkit.Chunk chunk) {
		pos = new ChunkPosition(chunk.getX(), chunk.getZ(), chunk.getWorld().getName());
		var world = chunk.getWorld();
		for (int x = 0; x < 16; x++)
			for (int z = 0; z < 16; z++) {
				var block = world.getHighestBlockAt(pos.x() * 16 + x, pos.z() * 16 + z,
						HeightMap.WORLD_SURFACE);
				var name = StringConversion.camelToTitleCase(block.getType().name());
				var color = getMapColor(block.getBlockData().getMapColor());
				blocks[x][z] = new Block(name, color, block.getY());
			}
	}

	// TODO: this may(?) match the tinted colors too, which is wrong?
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
	public Block getAt(BlockPosition pos) {
		return blocks[pos.x()][pos.z()];
	}

	@Override
	public void setAt(BlockPosition pos, Block block) {
		blocks[pos.x()][pos.z()] = block;
	}

	@Override
	public Block[][] getBlocks() {
		return blocks;
	}
}
