package dev.models;

import dev.Game;
import dev.GameBoard;
import dev.Vector;

import java.util.ArrayList;
import java.util.List;

public class DFS implements Model {
	private final List<Vector> fixedMoves = new ArrayList<>(List.of(Vector.up(), Vector.down(), Vector.left(), Vector.right()));
	List<List<Vector>> moveCombinations;
	public final int depth;
	public final int randomDepth;

	public DFS(int depth, int randomDepth) {
		this.depth = depth;
		this.randomDepth = randomDepth;
		moveCombinations = new ArrayList<>(List.of(new ArrayList<>()));
		moveCombinations = generateMoveCombinations(moveCombinations, depth);
	}

	// Generates cartesian product {up, down, left, right}^d.
	private List<List<Vector>> generateMoveCombinations(List<List<Vector>> moves, int d) {
		if (d == 0) {
			return moves;
		}
		else {
			List<List<Vector>> newMoves = new ArrayList<>();

			// For all given directions, copy the current list of move combos, and append this direction to all of them.
			for (Vector move : fixedMoves) {
				List<List<Vector>> directionMoves = duplicateList(moves);
				for (List<Vector> moveCombo : directionMoves) {
					moveCombo.add(move);
				}
				newMoves.addAll(directionMoves);
			}
			return generateMoveCombinations(newMoves, d-1);
		}
	}

	private List<List<Vector>> duplicateList(List<List<Vector>> list) {
		List<List<Vector>> result = new ArrayList<>();
		for (List<Vector> row : list) {
			result.add(new ArrayList<>(row));
		}
		return result;
	}

	public List<Vector> computeMove(GameBoard gameBoard) {
		// Up, down, left, right
		List<List<Vector>> moves;
		GameBoard virtualGameBoard;
		int maxScore = -1;
		List<Vector> result = new ArrayList<>();

		for (int i = 0; i < moveCombinations.size(); i++) {
			int score = 0;
			boolean valid = true;
			List<Vector> moveCombo = moveCombinations.get(i);
			virtualGameBoard = new GameBoard(gameBoard);
			// Perform all possible movements for given depth.
			for (Vector move : moveCombo) {
				if (virtualGameBoard.checkMoveValid(move.getY(), move.getX())) {
					virtualGameBoard.move(move.getY(), move.getX());
				}
				else {
					score = virtualGameBoard.getScore();
					valid = false;
				}
			}
			// Perform random movements for large number of steps and determine maximum score.
			if (valid) {
				int iter = 0;

				while (virtualGameBoard.checkValidMoveExists() && iter < randomDepth) {
					iter++;
					int rand = (int)Math.floor(Math.random() * 4);
					Vector move = fixedMoves.get(rand);
					virtualGameBoard.move(move.getY(), move.getX());
				}
				score = virtualGameBoard.getScore();
			}
			// Determine maximum scoring move combination
			if (score > maxScore) {
				maxScore = score;
				result = moveCombo;
			}
		}
		return new ArrayList<>(result);
	}
}
