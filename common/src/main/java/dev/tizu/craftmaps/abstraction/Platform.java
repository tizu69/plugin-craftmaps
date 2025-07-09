package dev.tizu.craftmaps.abstraction;

public class Platform {
	private Platform() {
	}

	public static interface Listener {
		Chunk getChunkAt(int x, int z);
	}

	public static interface Handler {
		void onChunkLoaded(int x, int z, Chunk data);
	}
}
