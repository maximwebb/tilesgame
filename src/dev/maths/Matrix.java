package dev.maths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Matrix {

	private Map<Integer, Map<Integer, Double>> matrix;
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

	// from = col, to = row
	public double get(int from, int to) {
		return matrix.get(to).get(from);
	}

	public void set(int from, int to, double value) {
		matrix.get(to).put(from, value);
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
}
