package dev.tests;

import dev.maths.Matrix;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleBinaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MatrixTest {
	@Test
	void creationIsCorrect() {
		// ARRANGE
		List<Double> values = new ArrayList<>(List.of(1d, 2d, 3d, 4d, 5d, 6d));

		// ACT
		Matrix matrix = new Matrix(values, 2, 3);

		// ASSERT
		assertEquals(6d, matrix.matrix.get(1).get(2));
		assertEquals(4d, matrix.matrix.get(1).get(0));
	}

	@Test
	void getIsCorrect() {
		// ARRANGE
		Matrix matrix1 = new Matrix(new ArrayList<>(List.of(3d, 1d, 4d, 2d, 5d, 9d)), 3, 2);
		Matrix matrix2 = new Matrix(new ArrayList<>(List.of(6d, 5d, 4d, 3d, 1d)), 5, 1);

		// ACT
		double val1 = matrix1.get(1, 1);
		double val2 = matrix2.get(3);

		// ASSERT
		assertEquals(2d, val1);
		assertEquals(3d, val2);
	}

	@Test
	void getRowIsCorrect() {
		// ARRANGE
		Matrix matrix1 = new Matrix(new ArrayList<>(List.of(3d, 1d, 4d, 2d, 5d, 9d, 6d, 7d)), 2, 4);

		// ACT
		Matrix row1 = matrix1.getRow(0);
		Matrix row2 = matrix1.getRow(1);

		// ASSERT
		assertEquals(new Matrix(new ArrayList<>(List.of(3d, 1d, 4d, 2d)), 4, 1), row1);
		assertEquals(new Matrix(new ArrayList<>(List.of(5d, 9d, 6d, 7d)), 4, 1), row2);
	}

	@Test
	void getColIsCorrect() {
		// ARRANGE
		Matrix matrix1 = new Matrix(new ArrayList<>(List.of(3d, 1d, 4d, 2d, 5d, 9d, 6d, 7d)), 4, 2);

		// ACT
		Matrix col1 = matrix1.getCol(0);
		Matrix col2 = matrix1.getCol(1);

		// ASSERT
		assertEquals(new Matrix(new ArrayList<>(List.of(3d, 4d, 5d, 6d)), 4, 1), col1);
		assertEquals(new Matrix(new ArrayList<>(List.of(1d, 2d, 9d, 7d)), 4, 1), col2);
	}

	@Test
	void setIsCorrect() {
		// ARRANGE
		Matrix matrix1 = new Matrix(new ArrayList<>(List.of(3d, 1d, 4d, 2d, 5d, 9d, 6d, 7d)), 2, 4);

		// ACT
		matrix1.set(2, 1, 10d);

		// ASSERT
		assertEquals(10d, matrix1.get(2, 1));
	}

	@Test
	void infixMapIsCorrect() {
		// ARRANGE
		Matrix matrix1 = new Matrix(new ArrayList<>(List.of(2d, 1d, 5d, 3d)), 2,2);
		Matrix matrix2 = new Matrix(new ArrayList<>(List.of(3d, 4d, 7d, 1d)), 2,2);
		DoubleBinaryOperator f = (a, b) -> a * b;

		// ACT
		Matrix matrix3 = Matrix.infixMap(matrix1, matrix2, f);

		// ARRANGE
		assertEquals(new Matrix(new ArrayList<>(List.of(6d, 4d, 35d, 3d)), 2, 2), matrix3);
	}
}
