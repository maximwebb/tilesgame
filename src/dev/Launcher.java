package dev;

import dev.models.DFS;
import dev.models.IndividualNN;

import java.util.ArrayList;
import java.util.List;

public class Launcher {
	public static void main(String[] args) {
		DFS dfs = new DFS(7, 1);
		IndividualNN individual = new IndividualNN(4, new ArrayList<>(List.of(16, 16, 16, 4)), true);
		Game game = Game.getInstance();
		//game.setModel(dfs);
		game.start();
	}
}
