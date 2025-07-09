package dev.tizu.craftmaps.paper.abstraction;

import dev.tizu.craftmaps.abstraction.Block;
import dev.tizu.craftmaps.abstraction.ChunkData;

public class ChunkDataImpl implements ChunkData {
	private int x;
	private int z;
	private Block[][] blocks = Block.generateDefault();

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getZ() {
		return z;
	}

	@Override
	public Block getAt(int x, int z) {
		return blocks[x][z];
	}
}
