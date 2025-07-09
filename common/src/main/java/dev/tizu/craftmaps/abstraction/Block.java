package dev.tizu.craftmaps.abstraction;

public class Block {
	private String name;
	private BlockColor color;
	private int y;

	public Block(String name, BlockColor color, int y) {
		this.name = name;
		this.color = color;
		this.y = y;
	}

	public String getName() {
		return name;
	}

	public BlockColor getColor() {
		return color;
	}

	public int getY() {
		return y;
	}

	public static Block[][] generateDefault() {
		Block[][] blocks = new Block[16][16];
		for (int x = 0; x < 16; x++)
			for (int z = 0; z < 16; z++)
				blocks[x][z] = null;
		return blocks;
	}
}
