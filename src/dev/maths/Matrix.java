package dev.maths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

public class Matrix {

	public Map<Integer, Map<Integer, Double>> matrix;
	public int width;
	public int height;

	public Matrix(List<Double> values, int height, int width) {
		matrix = new HashMap<>();

		// i is "to"
		for (int i = 0; i < height; i++) {
			Map<Integer, Double> row = new HashMap<>();
			// j is "from"
			for (int j = 0; j < width; j++) {
				row.put(j, values.get(j + i * width));
			}
			matrix.put(i, row);
		}
		this.width = width;
		this.height = height;
	}

	public static Matrix getMatrix(boolean random, int height, int width) {
		List<Double> values = new ArrayList<>();
		for (int i = 0; i < height * width; i++) {
			if (random) {
				values.add((Math.random() * 2) - 1);
			}
			else {
				values.add(0d);
			}
		}
		return new Matrix(values, height, width);
	}

	// from = col, to = row
	public double get(int from, int to) {
		return matrix.get(to).get(from);
	}

	// For column vectors
	public double get(int pos) {
		return matrix.get(pos).get(0);
	}

	public Matrix getRow(int row) {
		return new Matrix(new ArrayList<>(matrix.get(row).values()), this.width, 1);
	}

	public Matrix getCol(int col) {
		List<Double> values = new ArrayList<>();
		for (int i = 0; i < this.height; i++) {
			values.add(matrix.get(i).get(col));
		}
		return new Matrix(values, this.height, 1);
	}

	public void set(int from, int to, double value) {
		matrix.get(to).put(from, value);
	}

	public void set(int pos, double value) {
		matrix.get(pos).put(0, value);
	}

	public static Matrix add(Matrix matrix1, Matrix matrix2) {
		List<Double> values = new ArrayList<>();
		for (int i = 0; i < matrix1.height; i++) {
			for (int j = 0; j < matrix1.width; j++) {
				values.add(matrix1.get(j, i) + matrix2.get(j, i));
			}
		}
		return new Matrix(values, matrix1.height, matrix1.width);
	}

	public static Matrix multiply(Matrix matrix1, Matrix matrix2) {
		List<Double> values = new ArrayList<>();
		int rows = matrix1.height;
		int cols = matrix2.width;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				double count = 0d;
				for (int k = 0; k < matrix1.width; k++) {
					count += (matrix1.get(k, i) * matrix2.get(j, k));
				}
				values.add(count);
			}
		}
		return new Matrix(values, rows, cols);
	}

	public static Matrix map(Matrix matrix1, DoubleUnaryOperator f) {
		Matrix matrix2 = Matrix.getMatrix(false, matrix1.height, matrix1.width);

		for (int i = 0; i < matrix1.width; i++) {
			for (int j = 0; j < matrix1.height; j++) {
				matrix2.set(i, j, f.applyAsDouble(matrix1.get(i, j)));
			}
		}

		return matrix2;
	}


	public static Matrix infixMap(Matrix matrix1, Matrix matrix2, DoubleBinaryOperator f) {
		Matrix matrix3 = Matrix.getMatrix(false, matrix1.height, matrix1.width);

		for (int i = 0; i < matrix1.width; i++) {
			for (int j = 0; j < matrix1.height; j++) {
				matrix3.set(i, j, f.applyAsDouble(matrix1.get(i, j), matrix2.get(i, j)));
			}
		}

		return matrix3;
	}


	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Matrix)) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		Matrix rhs = (Matrix)obj;
		return rhs.matrix.equals(this.matrix);
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				str.append(matrix.get(i).get(j));
				str.append(" ");
			}
			str.append("\n");
		}
		return String.valueOf(str);
	}
}
