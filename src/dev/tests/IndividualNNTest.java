package dev.tests;
import dev.Game;
import dev.GameBoard;
import dev.Vector;
import dev.maths.Matrix;
import dev.models.IndividualNN;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IndividualNNTest {
	@Test
	void layersInitialiseCorrectly() {
		// ARRANGE
		IndividualNN inn = new IndividualNN(4, new ArrayList<>(List.of(10, 16, 16, 4)), false);

		Matrix mat = new Matrix(new ArrayList<>(List.of(1d, 2d, 3d, 4d, 5d, 6d)), 3, 2);
		System.out.println(mat.toString());

		// ACT
		int layerNum = inn.layers.size();
		int layer1Size = inn.layers.get(0).height;
		int layer2Size = inn.layers.get(1).height;
		int layer3Size = inn.layers.get(2).height;
		int layer4Size = inn.layers.get(3).height;

		// ASSERT
		assertEquals(4,layerNum);
		assertEquals(Matrix.getMatrix(false, layer1Size, 1), inn.layers.get(0));
		assertEquals(Matrix.getMatrix(false, layer2Size, 1), inn.layers.get(1));
		assertEquals(Matrix.getMatrix(false, layer3Size, 1), inn.layers.get(2));
		assertEquals(Matrix.getMatrix(false, layer4Size, 1), inn.layers.get(3));
	}

	@Test
	void weightsInitialiseCorrectly() {
		// ARRANGE
		IndividualNN inn = new IndividualNN(3, new ArrayList<>(List.of(3, 4, 2)), false);

		// ACT
		int weightLayersNum = inn.weights.size();
		int weightLayer2Size = inn.weights.get(1).width;

		// ASSERT
		assertEquals(weightLayersNum, 2);
		assertEquals(weightLayer2Size, 4);
		assertEquals(0d, inn.weights.get(0).get(2, 1));
	}

	@Test
	void setsWeightCorrectly() {
		// ARRANGE
		IndividualNN inn = new IndividualNN(3, new ArrayList<>(List.of(3, 4, 2)), false);

		// ACT
		inn.setWeight(0, 1, 0, -3);
		inn.setWeight(1, 3, 1, 0.9);
		double weight1 = inn.weights.get(0).get(1, 0);
		double weight2 = inn.weights.get(1).get(3, 1);

		// ASSERT
		assertEquals(-3, weight1);
		assertEquals(0.9, weight2);
	}

	@Test
	void getsWeightCorrectly() {
		// ARRANGE
		IndividualNN inn = new IndividualNN(4, new ArrayList<>(List.of(3, 4, 6, 3)), false);
		inn.setWeight(1, 2, 5, 0.66);
		inn.setWeight(2, 0, 0, 100);

		// ACT
		double weight1 = inn.getWeight(1, 2, 5);
		double weight2 = inn.getWeight(2, 0, 0);

		// ASSERT
		assertEquals(0.66, weight1);
		assertEquals(100, weight2);
	}

	@Test
	void setsNeuronCorrectly() {
		// ARRANGE
		IndividualNN inn = new IndividualNN(4, new ArrayList<>(List.of(3, 4, 6, 3)), false);

		// ACT
		inn.setNeuron(1, 3, -9d);
		inn.setNeuron(3, 0, 1.2);

		// ASSERT
		assertEquals(-9d, inn.layers.get(1).get(3));
		assertEquals(1.2, inn.layers.get(3).get(0));
	}

	@Test
	void getsNeuronCorrectly() {
		// ARRANGE
		IndividualNN inn = new IndividualNN(4, new ArrayList<>(List.of(3, 4, 6, 3)), false);
		inn.setNeuron(0, 1, 3.141);
		inn.setNeuron(0, 1, -0.4);
		inn.setNeuron(3, 1, 1.5);

		// ACT
		double neuron1 = inn.getNeuron(0, 1);
		double neuron2 = inn.getNeuron(3, 1);

		// ASSERT
		assertEquals(-0.4, neuron1);
		assertEquals(1.5, neuron2);
	}

	@Test
	void setsInputsCorrectly() {
		// ARRANGE
		IndividualNN inn = new IndividualNN(3, new ArrayList<>(List.of(4, 10, 4)), false);
		List<Double> inputs = new ArrayList<>(List.of(3.141, 592d, -6.5, 0d));

		// ACT
		inn.setInputs(inputs);

		// ASSERT
		assertEquals(3.141, inn.getNeuron(0, 0));
		assertEquals(592d, inn.getNeuron(0, 1));
		assertEquals(-6.5, inn.getNeuron(0, 2));
		assertEquals(0d, inn.getNeuron(0, 3));
	}

	@Test
	void getsOutputsCorrectly() {
		// ARRANGE
		IndividualNN inn = new IndividualNN(3, new ArrayList<>(List.of(4, 10, 3)), false);
		inn.setNeuron(2, 0, 2.718);
		inn.setNeuron(2, 1, -1.618);
		inn.setNeuron(2, 2, 0.571);

		// ACT
		Matrix outputs = inn.getOutputs();

		// ASSERT
		assertEquals(new Matrix(new ArrayList<>(List.of(2.718, -1.618, 0.571)), 3, 1), outputs);
	}

	@Test
	void clearsNeuronsCorrectly() {
		// ARRANGE
		IndividualNN inn = new IndividualNN(4, new ArrayList<>(List.of(3, 4, 4, 2)), false);
		inn.setNeuron(0, 1, 1.3);
		inn.setNeuron(3, 0, -1.4);
		inn.setNeuron(3, 1, 5d);

		// ACT
		inn.clearAllNeurons();

		// ASSERT
		assertEquals(0d, inn.getNeuron(0, 1));
		assertEquals(0d, inn.getNeuron(2, 2));
		assertEquals(0d, inn.getNeuron(3, 0));
		assertEquals(0d, inn.getNeuron(3, 1));
	}

	@Test
	void forwardPropagationIsCorrect() {
		// ARRANGE
		IndividualNN inn = new IndividualNN(4, new ArrayList<>(List.of(2, 2, 3, 2)), false);
		List<Double> inputs = new ArrayList<>(List.of(0.8, 0.3));
		inn.setWeight(0, 0, 0, 0.5);
		inn.setWeight(0, 0, 1, 0.5);
		inn.setWeight(0, 1, 0, 1.0);
		inn.setWeight(0, 1, 1, 0.3);

		inn.setWeight(1, 0, 0, 1.2);
		inn.setWeight(1, 0, 1, 0.6);
		inn.setWeight(1, 0, 2, 0.1);
		inn.setWeight(1, 1, 0, 0.1);
		inn.setWeight(1, 1, 1, 0.9);
		inn.setWeight(1, 1, 2, 0.1);

		inn.setWeight(2, 0, 0, 1.2);
		inn.setWeight(2, 0, 1, 0.3);
		inn.setWeight(2, 1, 0, 1.4);
		inn.setWeight(2, 1, 1, 0.4);
		inn.setWeight(2, 2, 0, 0.2);
		inn.setWeight(2, 2, 1, 1.7);

		// ACT
		inn.forwardPropagate(inputs);
		double output1 = (double)Math.round(inn.getOutputs().get(0) * 10000)/10000d;
		double output2 = (double)Math.round(inn.getOutputs().get(1) * 10000)/10000d;

		// ASSERT
		assertEquals(2.296, output1);
		assertEquals(0.8134, output2);
	}

	@Test
	void singleLayerBackPropagationIsCorrect() {
		// ARRANGE
		IndividualNN inn = new IndividualNN(4, new ArrayList<>(List.of(1, 2, 2,  1)), false);
		inn.setWeight(0, 0, 0, 0.1);
		inn.setWeight(0, 0, 1, 0.3);

		inn.setWeight(1, 0, 0, 0.7);
		inn.setWeight(1, 1, 0, 0.1);
		inn.setWeight(1, 0, 1, -0.5);
		inn.setWeight(1, 1, 1, 0.9);
		inn.setBias(1, 0, 3);
		inn.setBias(1, 1, -3);

		inn.setWeight(2, 0, 0, 0.6);
		inn.setWeight(2, 1, 0, -0.4);
		inn.setBias(2, 0, 2);
		inn.setBias(2, 1, -2);

		inn.setBias(3, 0, 0.5);

		inn.forwardPropagate(new ArrayList<>(List.of(0.6)));
		inn.backPropagate(new ArrayList<>(List.of(new ArrayList<>(List.of(0.6)))), new ArrayList<>(List.of(new Matrix(new ArrayList<>(List.of(0.36)), 1, 1))));
		List<List<Double>> trainingInputsList = new ArrayList<>();
		List<Matrix> trainingTargetsList = new ArrayList<>();
		List<List<Double>> testInputsList = new ArrayList<>();
		List<Matrix> testTargetsList = new ArrayList<>();

		for (int i = 0; i < 1000; i++) {
			double num = Math.random() * 2 - 1;
			trainingInputsList.add(new ArrayList<>(List.of(num)));
			trainingTargetsList.add(new Matrix(new ArrayList<>(List.of(num * 0.4 + 0.2)), 1, 1));
		}

		for (int i = 0; i < 100; i++) {
			double num = Math.random() * 100;
			testInputsList.add(new ArrayList<>(List.of(num)));
			testTargetsList.add(new Matrix(new ArrayList<>(List.of(num * 0.4 + 0.2)), 1, 1));
		}


		// ACT
		inn.backPropagate(trainingInputsList, trainingTargetsList);

		// ASSERT
		assertTrue(inn.getAccuracy(testInputsList, testTargetsList) < 0.01);
		System.out.println(inn.getAccuracy(testInputsList, testTargetsList));

	}

	@Test
	void computesMoveSuccessfully() {
		// ARRANGE
		IndividualNN inn = new IndividualNN(4, new ArrayList<>(List.of(16, 16, 16, 4)), false);
		Game game = new Game("2048", 1, 1, null);
		GameBoard gameBoard = new GameBoard(4);
		gameBoard.addTile(0, 0, 2);
		gameBoard.addTile(0, 3, 2);
		gameBoard.addTile(1, 2, 32);
		gameBoard.addTile(2, 1, 16);
		gameBoard.addTile(2, 2, 8);
		gameBoard.addTile(3, 3, 4);

		// ACT
		List<Vector> moves = inn.computeMove(gameBoard);
	}
}
