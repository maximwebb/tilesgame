package dev;

import dev.models.DFS;

public class Launcher {
	public static void main(String[] args) {
		DFS dfs = new DFS(6, 30);
		Game game = new Game("2048", 800, 800, dfs);
		game.start();
	}
}
