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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CMBlock cmBlock = (CMBlock) o;
		return id.equals(cmBlock.id) && color == cmBlock.color && y == cmBlock.y;
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + color.hashCode();
		result = 31 * result + y;
		return result;
	}
}
