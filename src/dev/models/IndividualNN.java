package dev.models;

import dev.GameBoard;
import dev.Tile;
import dev.Vector;
import dev.maths.Matrix;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class IndividualNN implements Model {

	private int layerNum;
	public List<Integer> layerSizes;
	public List<Matrix> layers;
	public List<Matrix> biases;
	public List<Matrix> weights;
	private double fitness;

	public IndividualNN(int layerNum, List<Integer> layerSizes, boolean randomInit) {
		this.layerNum = layerNum;
		this.layerSizes = layerSizes;

		/* Initialises all neuron activations to 0 */
		layers = new ArrayList<>();
		biases = new ArrayList<>();
		for (int i = 0; i < layerNum; i++) {
			layers.add(Matrix.getMatrix(false, layerSizes.get(i), 1));
			biases.add(Matrix.map(Matrix.getMatrix(randomInit, layerSizes.get(i), 1), a -> a * 10));
		}

		/* Initialises all weights to 0 */
		weights = new ArrayList<>();
		for (int i = 0; i < layerNum - 1; i++) {
			weights.add(Matrix.getMatrix(randomInit, layers.get(i+1).height, layers.get(i).height));
		}

		this.fitness = 1d;
	}

	public double getWeight(int layer, int sourceNeuron, int targetNeuron) {
		return weights.get(layer).get(sourceNeuron, targetNeuron);
	}

	public void setWeight(int layer, int sourceNeuron, int targetNeuron, double value) {
		weights.get(layer).set(sourceNeuron, targetNeuron, value);
	}

	public void setInputs(List<Double> values) {
		if (layers.get(0).height == values.size()) {
			layers.set(0, new Matrix(new ArrayList<>(values), layers.get(0).height, 1));
		}
		else {
			System.out.println("Someone fucked up.");
		}
	}

	//
	public Matrix getOutputs() {
		return layers.get(layers.size() - 1);
	}

	public double getNeuron(int layer, int sourceNeuron) {
		return layers.get(layer).get(0, sourceNeuron);
	}

	public void setNeuron(int layer, int sourceNeuron, double value) {
		layers.get(layer).set(0, sourceNeuron, value);
	}

	public void clearAllNeurons() {
		for (int i = 0; i < layers.size(); i++) {
			layers.set(i, Matrix.getMatrix(false, layers.get(i).height, 1));
		}
	}

	public void forwardPropagate(List<Double> values) {
		setInputs(values);
		//DoubleUnaryOperator ReLU = a -> Math.max(0, a);
		for (int i = 0; i < layers.size() - 1; i++) {
			Matrix layer = layers.get(i);
			layers.set(i+1, Matrix.add(Matrix.multiply(weights.get(i), layer), biases.get(i)));
		}
	}

	public void backPropagate(List<Double> inputs, Matrix targets) {
		double cost1 = getCost(inputs, targets);

		List<Matrix> weightCostGradient = new ArrayList<>();
		for (Matrix mat : weights) {
			weightCostGradient.add(Matrix.getMatrix(false, mat.height, mat.width));
		}
		List<Matrix> biasCostGradient = new ArrayList<>();
		List<Matrix> errors = new ArrayList<>();
		for (Matrix mat : layers) {
			errors.add(Matrix.getMatrix(false, mat.height, 1));
			biasCostGradient.add(Matrix.getMatrix(false, mat.height, 1));
		}

		/* Calculate final layer derivatives */
		int finalLayer = layers.size()-1;
		// i is target node
		for (int i = 0; i < layers.get(finalLayer).height; i++) {
			double deltaCost = 2 * (layers.get(finalLayer).get(i) - targets.get(i)); // dC/d(a_i)^L
			errors.get(finalLayer).set(i, deltaCost);
			double deltaActivation = 1d; //d(a_j)^L/d(z_i)^L
			double deltaRawActivation; // d(z_i)^L/d(w_ij)^L
			// j is source node
			for (int j = 0; j < weights.get(weights.size()-1).height; j++) {
				deltaRawActivation = layers.get(finalLayer - 1).get(j);
				double delta = deltaCost * deltaActivation * deltaRawActivation; // dC/d(w_ij)^L
				weightCostGradient.get(finalLayer - 2).set(j, i, delta);
			}
			biasCostGradient.get(finalLayer - 1).set(i, deltaCost * 1d * 1d);
		}

		/* Calculate hidden layer derivatives */
		// l is the weight layer (l corresponds to the source layer number)
		for (int l = weights.size() - 2; l >= 0; l--) {
			// i is target node
			for (int i = 0; i < layers.get(l+1).height; i++) {
				// Sum over routes that a_i can influence cost function
				double deltaCost = 0d; // dC/d(a_i)^L
				for (int k = 0; i < layers.get(l+1).height; k++) {
					deltaCost += (weights.get(l+1).get(k, i) * 1d * errors.get(l+2).get(k));
				}
				errors.get(l+1).set(i, deltaCost);

				double deltaActivation = 1d; //d(a_j)^L/d(z_i)^L
				double deltaRawActivation; // d(z_i)^L/d(w_ij)^L
				// j is source node
				for (int j = 0; j < layers.get(l).height; j++) {
					deltaRawActivation = layers.get(l+1).get(j);
					double delta = deltaCost * deltaActivation * deltaRawActivation; // dC/d(w_ij)^L
					weightCostGradient.get(l).set(j, i, delta);
				}
				biasCostGradient.get(l + 1).set(i, deltaCost * 1d * 1d);
			}
		}

		int count = 0;
		double epsilon = 0.1;
		double learningRate = 0.2;
		double cost2 = Integer.MAX_VALUE;
		/* Gradient descent */
		while (count < 100 && cost2 - cost1 > epsilon) {
			cost2 = getCost(inputs, targets);
			count++;
			for (int l = 0; l < weights.size(); l++) {
				Matrix weightLayer = weights.get(l);
				for (int i = 0; i < weightLayer.height; i++) {
					for (int j = 0; j < weightLayer.width; j++) {
						weightLayer.set(j, i, weightLayer.get(j, i) - learningRate * weightCostGradient.get(l).get(j, i));
					}
				}
			}
			for (int l = 0; l < layers.size(); l++) {
				Matrix biasLayer = biases.get(l);
				for (int i = 0; i < biasLayer.height; i++) {
					biasLayer.set(i, biasLayer.get(i) - learningRate * biasCostGradient.get(l).get(i));
				}
			}

			cost1 = cost2;
		}
	}

	public double getCost(List<Double> inputs, Matrix targets) {
		forwardPropagate(inputs);
		Matrix outputs = getOutputs();
		double cost = 0d;
		for (int i = 0; i < inputs.size(); i++) {
			cost += (outputs.get(i) - inputs.get(i)) * (outputs.get(i) - inputs.get(i));
		}

		return cost;
	}

	public void setFitness(double fitness) {
		this.fitness += fitness;
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
		Matrix outputs = getOutputs();
		Map<Double, Vector> outputMap = new TreeMap<>(Collections.reverseOrder());

		for (int i = 0; i < outputs.height; i++) {
			outputMap.put(outputs.get(0, i), possibleMoves.get(i));
		}

		return new ArrayList<>(outputMap.values());
	}
}
