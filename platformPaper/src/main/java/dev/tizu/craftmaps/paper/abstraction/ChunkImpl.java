package dev.tizu.craftmaps.paper.abstraction;

import dev.tizu.craftmaps.abstraction.Block;
import dev.tizu.craftmaps.abstraction.Chunk;

public class ChunkImpl implements Chunk {
	private ChunkPosition pos;
	private Block[][] blocks = Block.generateDefault();

	public ChunkImpl(ChunkPosition pos) {
		this.pos = pos;
	}

	public ChunkImpl(org.bukkit.Chunk chunk) {
		pos = new ChunkPosition(chunk.getX(), chunk.getZ(), chunk.getWorld().getName());
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
}
