package dev;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GameBoard {

	public final int length;
	public List<List<Tile>> board;
	Map<Integer, Set<Integer>> emptyTiles;
	private int score;

	public HashMap<Integer, Color> colorPalette = new HashMap<>();
	private Color[] colors = {new Color(227, 227, 227),
			new Color(200, 200, 200),
			new Color(245, 241, 201),
			new Color(237, 229, 147),
			new Color(245, 233, 108),
			new Color(247, 192, 64),
			new Color(222, 149, 2),
			new Color(196, 85, 0),
			new Color(184, 48, 7),
			new Color(250, 57, 57),
			new Color(250, 57, 125),
			new Color(179, 36, 143),
			new Color(127, 31, 156),
			new Color(85, 31, 156)
	};

	public GameBoard (int length) {
		this.length = length;
		this.board = new ArrayList<>();
		this.emptyTiles = new HashMap<>();

		for (int i = 0; i < length; i++) {
			List<Tile> row = new ArrayList<>();
			Set<Integer> emptyTilesRow = new HashSet<>();
			for (int j = 0; j < length; j++) {
				row.add(new Tile(1, j, i));
				emptyTilesRow.add(j);
			}
			board.add(row);
			emptyTiles.put(i, emptyTilesRow);
		}

		for (int i = 0; i < colors.length; i++) {
			colorPalette.put(i, colors[i]);
		}

//		addRandomTile();
//		addRandomTile();
	}

	public void addTile(int x, int y, int value) {
		if (x >= 0 && x < length && y >= 0 && y < length) {
			board.get(y).set(x, new Tile(value, x, y));
			emptyTiles.get(y).remove(x);
		}
	}

	public void addRandomTile() {
		List<Coordinate> emptyTilesList = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				if (getTile(i, j).empty) {
					emptyTilesList.add(new Coordinate(i, j));
				}
			}
		}

		Collections.shuffle(emptyTilesList);
		int x = emptyTilesList.get(0).getX();
		int y = emptyTilesList.get(0).getY();
		addTile(x, y, 2);
	}

	public void moveTile (int x, int y, int xNew, int yNew) {
		if (x < length && y < length && xNew < length && yNew < length && (x != xNew || y != yNew)) {
			int value = getTile(x, y).getValue();
			getTile(x, y).setEmpty();
			getTile(xNew, yNew).setValue(value);
			emptyTiles.get(yNew).remove(xNew);
			emptyTiles.get(y).add(x);
		}
	}

	public Tile getTile (int x, int y) {
		if (x >= 0 && x < length && y >= 0 && y < length) {
			return board.get(y).get(x);
		}
		else {
			return null;
		}
	}

	public List<Tile> getAllTiles() {
		List<Tile> tiles = new ArrayList<>();
		for (List<Tile> row : board) {
			tiles.addAll(row);
		}

		return tiles;
	}

	public void clearTile(int x, int y) {
		getTile(x, y).setEmpty();
		emptyTiles.get(y).add(x);
	}

	public boolean checkMoveValid(int down, int right) {
		boolean valid = false;
		if (!(Math.abs(down) == 1 && right == 0) && !(Math.abs(right) == 1 && down == 0)) {
			return false;
		}
		for (int i = Math.max(0, -down); i < length - Math.max(0, down); i++) {
			for (int j = Math.max(0, -right); j < length - Math.max(0, right); j++) {
				Tile tile = board.get(i).get(j);
				Tile adjacentTile = getTile(j + right, i + down);
				if (!tile.empty && (adjacentTile.empty || adjacentTile.getValue() == tile.getValue())) {
					valid = true;
					break;
				}
			}
		}
		return valid;
	}

	public boolean checkValidMoveExists() {
		return (checkMoveValid(1, 0) || checkMoveValid(-1, 0) || checkMoveValid(0, 1) || checkMoveValid(0, -1));
	}

	public void printBoard() {
		String line;
		for (List<Tile> row : board) {
			line = "";
			for (Tile tile : row) {
				line += (tile.empty ? 0 : tile.getValue());
				line += " ";
			}
			System.out.println(line);
		}
		System.out.println();
	}

	public void printBoardCode() {
		String line;
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				if (!getTile(i, j).empty)
					line = "";
					line = "gameBoard.addTile(";
					line += j + ", " + i + ", ";
					line += getTile(i, j).getValue();
					line += ");";
					System.out.println(line);
			}
		}
	}

	public int getScore() {
		return score;
	}

	public void move(int down, int right) {
		if (checkMoveValid(down, right)) {
			int dir = down + right;
			boolean isVert = (right == 0);
			boolean isForward = (dir == 1);
			List<Integer> tileIndices = new ArrayList<>();
			if (isForward) {
				for (int i = length - 2; i >= 0; i--) {
					tileIndices.add(i);
				}
			}
			else {
				for (int i = 1; i < length; i++) {
					tileIndices.add(i);
				}
			}

			for (int i : tileIndices) {
				for (int j = 0; j < length; j++) {
					int offset = dir;
					boolean merge = false;

					Tile startTile = (isVert ? getTile(j, i) : getTile(i, j));
					Tile tile = (isVert ? board.get(i + offset).get(j) : board.get(j).get(i + offset));
					if (!startTile.empty) {
						while (tile != null && i + offset < length && i + offset >= 0 && (tile.empty || tile.getValue() == startTile.getValue()) && !merge) {
							if (!tile.empty && tile.getValue() == startTile.getValue()) {
								tile.doubleValue();
								score += tile.getValue();
								if (isVert)
									clearTile(j, i);
								else
									clearTile(i, j);
								merge = true;
							}
							else {
								offset += dir;
								tile = (isVert ? getTile(j, i + offset) : getTile(i + offset, j));
							}
						}
						if (!merge) {
							if (isVert)
								moveTile(j, i, j, i + offset - dir);
							else
								moveTile(i, j, i + offset - dir, j);
						}
					}
				}
			}
			addRandomTile();
		}
		printBoard();
	}

}
