package dev.models;

import dev.Game;
import dev.GameBoard;
import dev.Vector;

import java.util.*;

public class PopulationManager {

	private final int populationSize;
	private final double mutationChance;
	private final ArrayList<Integer> layerSizes;
	private int maxScore;
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
		for (int i = 0; i < parent1.weights.size(); i++) {
			for (int j = 0; j < parent1.weights.get(i).size(); j++) {
				Map<Integer, Double> parent1Weights = parent1.weights.get(i).get(j);
				Map<Integer, Double> parent2Weights = parent2.weights.get(i).get(j);
				for (int k = 0; k < parent1Weights.size(); k++) {
					double rand = Math.random();
					double childWeight = 0d;
					if (rand < mutationChance) {
						childWeight = Math.random();
					}
					else {
						double parent1Weight = parent1Weights.get(k);
						double parent2Weight = parent2Weights.get(k);
						double parent1Fitness = parent1.getFitness();
						double parent2Fitness = parent2.getFitness();

						switch (reproductionType) {
							case AVERAGE:
								childWeight = (parent1Weight + parent2Weight) / 2;
								break;
							case WEIGHTED_AVERAGE:
								childWeight = (parent1Weight * parent1Fitness + parent2Weight * parent2Fitness) / (parent1Fitness + parent2Fitness);
								break;
							case WEIGHTED:
								if (rand < parent1Fitness / (parent1Fitness + parent2Fitness + mutationChance)) {
									childWeight = parent1Weight;
								} else {
									childWeight = parent2Weight;
								}
						}
					}
					child.setWeight(i, j, k, childWeight);
				}
			}
		}
		return child;
	}

	public void assignPopulationFitness() {
		int maxMoves = 4000;
		int prog = 0;
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
		System.out.println("Max score: " + this.maxScore);
	}

	public IndividualNN getFittestIndividual() {
		return this.fittestIndividual;
	}
}
