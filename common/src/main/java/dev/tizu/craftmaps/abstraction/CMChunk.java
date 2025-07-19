package dev.tizu.craftmaps.abstraction;

import java.util.ArrayList;
import java.util.List;

import dev.tizu.craftmaps.platform.BlockColor;
import dev.tizu.craftmaps.positions.BlockPosition;
import dev.tizu.craftmaps.positions.ChunkPosition;

public interface CMChunk {
	ChunkPosition getPosition();

	CMBlock getAt(BlockPosition pos);

	void setAt(BlockPosition pos, CMBlock block);

	CMBlock[] getPalette();

	int[][] getBlocks();

	public static int[][] generateDefaultBlocks() {
		int[][] blocks = new int[16][16];
		for (int x = 0; x < 16; x++)
			for (int z = 0; z < 16; z++)
				blocks[x][z] = 0;
		return blocks;
	}

	public static List<CMBlock> generateDefaultPalette() {
		var a = new ArrayList<CMBlock>();
		a.add(new CMBlock("air", BlockColor.NONE, 0));
		return a;
	}
}
