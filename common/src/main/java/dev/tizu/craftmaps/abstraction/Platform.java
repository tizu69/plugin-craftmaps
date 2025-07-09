package dev.tizu.craftmaps.abstraction;

public class Platform {
	private Platform() {
	}

	public static interface Listener {
		ChunkData getChunkAt(int x, int z);
	}

	public static interface Handler {
		void onChunkLoaded(int x, int z, ChunkData data);
	}
}
