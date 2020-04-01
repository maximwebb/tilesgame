package dev;

import dev.maths.Matrix;
import dev.models.DFS;
import dev.models.IndividualNN;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public class Launcher {
	public static void main(String[] args) {
		DFS dfs = new DFS(7, 1);
		IndividualNN inn = new IndividualNN(3, new ArrayList<>(List.of(1, 4, 1)), true);

		List<List<Double>> trainingInputsList = new ArrayList<>();
		List<Matrix> trainingTargetsList = new ArrayList<>();
		List<List<Double>> testInputsList = new ArrayList<>();
		List<Matrix> testTargetsList = new ArrayList<>();

		int a = 1;
		double b = 0.2;
		int c = 0;

		//DoubleUnaryOperator f = x -> a * x * x + b * x + c;
		DoubleUnaryOperator f = x -> x;

		for (int i = 0; i < 100000; i++) {
			double num = (Math.random() * 2) - 1;
			trainingInputsList.add(new ArrayList<>(List.of(num)));
			trainingTargetsList.add(new Matrix(new ArrayList<>(List.of(f.applyAsDouble(num))), 1, 1));
		}

		for (int i = 0; i < 100; i++) {
			double num = (Math.random() * 2) - 1;
			testInputsList.add(new ArrayList<>(List.of(num)));
			testTargetsList.add(new Matrix(new ArrayList<>(List.of(f.applyAsDouble(num))), 2, 1));
		}

		inn.backPropagate(trainingInputsList, trainingTargetsList);

		System.out.println(inn.getAccuracy(testInputsList, testTargetsList));

		//Game game = Game.getInstance();
		//game.setModel(dfs);
		//game.start();
	}
}
