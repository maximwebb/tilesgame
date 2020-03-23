package dev.models;

import dev.GameBoard;
import dev.Vector;
import java.util.List;

public interface Model {
	List<Vector> computeMove(GameBoard gameBoard);
}
