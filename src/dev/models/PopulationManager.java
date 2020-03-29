package dev.models;

import dev.GameBoard;
import dev.Vector;
import dev.maths.Matrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleBinaryOperator;

public class PopulationManager {

	private final int populationSize;
	private final double mutationChance;
	private final ArrayList<Integer> layerSizes;
	private int maxScore;
	private int currentMaxScore;
	private IndividualNN fittestIndividual;
	private double threshold;
	private final int layerNum;
	private final int layerSize;
	private ReproductionType reproductionType;
	public List<IndividualNN> population = new ArrayList<>();

	public PopulationManager(int populationSize, double mutationChance, double threshold, int layerNum, int inputLayerSize, int hiddenLayerSize, int outputLayerSize, ReproductionType reproductionType) {

		this.populationSize = populationSize;
		this.mutationChance = mutationChance;
		this.threshold = threshold;
		this.layerNum = layerNum;
		this.layerSize = hiddenLayerSize;
		this.reproductionType = reproductionType;
		this.maxScore = 1;
		this.currentMaxScore = 1;
		this.fittestIndividual = null;

		this.layerSizes = new ArrayList<>(Collections.nCopies(layerNum-2, layerSize));
		layerSizes.add(0, inputLayerSize);
		layerSizes.add(outputLayerSize);

		for (int i = 0; i < populationSize; i++) {
			population.add(new IndividualNN(layerNum, layerSizes, true));
		}
	}

	/* Merges the weights of two individuals, with behaviour determined by the reproduction type. */
	public IndividualNN merge(IndividualNN parent1,  IndividualNN parent2) {
		IndividualNN child = new IndividualNN(layerNum, layerSizes, false);
		double parent1Fitness = parent1.getFitness();
		double parent2Fitness = parent2.getFitness();
		DoubleBinaryOperator f = null;
		switch (reproductionType) {
			case AVERAGE:
				f = (a, b) -> (a + b) / 2;
				break;
			case WEIGHTED_AVERAGE:
				f = (a, b) -> (a * parent1Fitness + b * parent2Fitness) / (parent1Fitness + parent2Fitness);
				break;
			case WEIGHTED:
				f = (a, b) -> (Math.random() < parent1Fitness / (parent1Fitness + parent2Fitness) ? a : b);
		}
		DoubleBinaryOperator finalF = f;
		DoubleBinaryOperator g = (a, b) -> (Math.random() < mutationChance) ? (Math.random() * 2) - 1 : finalF.applyAsDouble(a, b);

		for (int i = 0; i < parent1.weights.size(); i++) {
			child.weights.set(i, Matrix.infixMap(parent1.weights.get(i), parent2.weights.get(i), g));
		}

		for (int i = 0; i < parent1.layers.size(); i++) {
			child.biases.set(i, Matrix.infixMap(parent1.biases.get(i), parent2.biases.get(i), g));
		}
		return child;
	}

	public void assignPopulationFitness() {
		int maxMoves = 4000;
		int prog = 0;
		currentMaxScore = 1;
		for (int i = 0; i < 3; i++) {
			for (IndividualNN individual : population) {
				//System.out.println("Fitness assignment " + Math.round((double)(1000*prog)/populationSize)/10 + "% complete");
				prog++;
				GameBoard gameBoard = new GameBoard(4);
				gameBoard.addRandomTile();
				gameBoard.addRandomTile();
				int n = 0;
				while (gameBoard.checkValidMoveExists() && n < maxMoves) {
					n++;
					List<Vector> moves = individual.computeMove(gameBoard);
					boolean moveValid = false;
					int k = 0;
					while (!moveValid && k < moves.size()) {
						moveValid = gameBoard.checkMoveValid(moves.get(k).getX(), moves.get(k).getY());
						if (moveValid)
							gameBoard.move(moves.get(k).getX(), moves.get(k).getY());
						k++;
					}
				}
				individual.setFitness(gameBoard.getScore());
				if (gameBoard.getScore() > maxScore) {
					maxScore = gameBoard.getScore();
					fittestIndividual = individual;
					gameBoard.printBoard();
				}
				if (gameBoard.getScore() > currentMaxScore) {
					currentMaxScore = gameBoard.getScore();
				}
			}
		}
	}

	public void generateNewPopulation() {
		assignPopulationFitness();
		// Assumes population > threshold * 100
		int absoluteThreshold = (int)Math.ceil(threshold * populationSize);
		population.sort((i1, i2) -> (int) (i2.getFitness() - i1.getFitness()));
		List<IndividualNN> fittestIndividuals = population.subList(0, absoluteThreshold);

		List<IndividualNN> newPopulation = new ArrayList<>();
		int prog = 0;
		while (newPopulation.size() <= populationSize) {
			//System.out.println("Reproduction " + Math.round((double)prog/populationSize * 1000)/10 + "% complete");
			prog++;
			int randomIndex1 = (int) Math.floor(Math.random() * fittestIndividuals.size());
			IndividualNN parent1 = fittestIndividuals.remove(randomIndex1);
			int randomIndex2 = (int) Math.floor(Math.random() * fittestIndividuals.size());
			IndividualNN parent2 = fittestIndividuals.get(randomIndex2);

			newPopulation.add(merge(parent1, parent2));
			fittestIndividuals.add(parent1);
		}
		population = newPopulation;
		System.out.println("Max score: " + this.maxScore + ", current generation max: " + this.currentMaxScore);
	}

	public IndividualNN getFittestIndividual() {
		return this.fittestIndividual;
	}
}
