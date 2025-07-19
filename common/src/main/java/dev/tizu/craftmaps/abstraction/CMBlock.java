package dev.tizu.craftmaps.abstraction;

import dev.tizu.craftmaps.platform.BlockColor;
import dev.tizu.craftmaps.utils.StringConversion;

public class CMBlock {
	private String id;
	private BlockColor color;
	private int y;

	public CMBlock(String id, BlockColor color, int y) {
		this.id = id;
		this.color = color;
		this.y = y;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		var name = this.id;
		if (name.contains(":"))
			name = name.split(":")[1];
		return StringConversion.camelToTitleCase(name);
	}

	public BlockColor getColor() {
		return color;
	}

	public int getY() {
		return y;
	}

	public static CMBlock[][] generateDefault() {
		CMBlock[][] blocks = new CMBlock[16][16];
		for (int x = 0; x < 16; x++)
			for (int z = 0; z < 16; z++)
				blocks[x][z] = null;
		return blocks;
	}
}
