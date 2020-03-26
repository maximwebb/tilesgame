package dev.models;

import dev.GameBoard;
import dev.Tile;
import dev.Vector;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class IndividualNN implements Model {

	private final int layerNum;
	public List<Integer> layerSizes;
	public List<List<Double>> layers;
	public List<Map<Integer, Map<Integer, Double>>> weights;
	private double fitness;

	public IndividualNN(int layerNum, List<Integer> layerSizes, boolean randomInit) {
		this.layerNum = layerNum;
		this.layerSizes = layerSizes;

		/* Initialises all neuron activations to 0 */
		layers = new ArrayList<>();
		for (int i = 0; i < layerNum; i++) {
			List<Double> layer = new ArrayList<>();
			for (int j = 0; j < layerSizes.get(i); j++) {
				layer.add(0d);
			}
			layers.add(layer);
		}

		/* Initialises all weights to 0 */
		weights = new ArrayList<>();
		for (int i = 0; i < layerNum - 1; i++) {
			Map<Integer, Map<Integer, Double>> weightLayer = new HashMap<>();
			for (int j = 0; j < layers.get(i).size(); j++) {
				Map<Integer, Double> singleSourceWeights = new HashMap<>();
				for (int k = 0; k < layers.get(i+1).size(); k++) {
					if (randomInit) {
						singleSourceWeights.put(k, Math.random());
					}
					else {
						singleSourceWeights.put(k, 0d);
					}
				}
				weightLayer.put(j, singleSourceWeights);
			}
			weights.add(weightLayer);
		}

		this.fitness = 1d;
	}

	public double getWeight(int layer, int sourceNeuron, int targetNeuron) {
		return weights.get(layer).get(sourceNeuron).get(targetNeuron);
	}

	public void setWeight(int layer, int sourceNeuron, int targetNeuron, double value) {
		weights.get(layer).get(sourceNeuron).put(targetNeuron, value);
	}

	public void setInputs(List<Double> values) {
		if (layers.get(0).size() == values.size()) {
			layers.set(0, new ArrayList<>(values));
		}
		else {
			System.out.println("Someone fucked up.");
		}
	}

	public List<Double> getOutputs() {
		return new ArrayList<>(layers.get(layers.size() - 1));
	}

	public double getNeuron(int layer, int sourceNeuron) {
		return layers.get(layer).get(sourceNeuron);
	}

	public void setNeuron(int layer, int sourceNeuron, double value) {
		layers.get(layer).set(sourceNeuron, value);
	}

	public void clearAllNeurons() {
		for (List<Double> layer : layers) {
			for (int i = 0; i < layer.size(); i++) {
				layer.set(i, 0d);
			}
		}
	}

	public void forwardPropagate(List<Double> values) {
		setInputs(values);
		for (int i = 0; i < layers.size() - 1; i++) {
			List<Double> layer = layers.get(i);
			for (int j = 0; j < layer.size(); j++) {
				double sourceActivation = layer.get(j);
				for (Map.Entry<Integer, Double> weight : weights.get(i).get(j).entrySet()) {
					int target = weight.getKey();
					double targetActivation = layers.get(i+1).get(target);
					double weightValue = weight.getValue();
					layers.get(i+1).set(target, targetActivation + sourceActivation * weightValue);
				}
			}
		}
	}

	public void backPropagate(List<Double> values) {

	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public double getFitness() {
		return this.fitness;
	}

	@Override
	public List<Vector> computeMove(GameBoard gameBoard) {
		List<Tile> tiles = gameBoard.getAllTiles();
		List<Double> inputs = tiles.stream().map(t -> (Math.log(t.getValue())/Math.log(2))/16).collect(toList());
		forwardPropagate(inputs);

		List<Vector> possibleMoves = new ArrayList<>(List.of(Vector.up(), Vector.down(), Vector.left(), Vector.right()));
		List<Double> outputs = getOutputs();
		Map<Double, Vector> outputMap = new TreeMap<>(Collections.reverseOrder());

		for (int i = 0; i < outputs.size(); i++) {
			outputMap.put(outputs.get(i), possibleMoves.get(i));
		}

		return new ArrayList<>(outputMap.values());
	}
}
