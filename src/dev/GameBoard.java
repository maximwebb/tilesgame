package dev;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameBoard {

	private final int length;
	List<List<Tile>> board;

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
		for (int i = 0; i < length; i++) {
			List<Tile> row = new ArrayList<>();
			for (int j = 0; j < length; j++) {
				row.add(null);
			}
			board.add(row);
		}

		for (int i = 0; i < colors.length; i++) {
			colorPalette.put(i, colors[i]);
		}
	}

	public void addTile(int x, int y, int value) {
		if (x < length && y < length) {
			board.get(y).set(x, new Tile(value, x, y));
		}
	}

	public void moveTile (int x, int y, int xNew, int yNew) {
		if (x < length && y < length && xNew < length && yNew < length) {
			Tile tile = board.get(y).get(x);
			board.get(yNew).set(xNew, tile);
			board.get(y).set(x, null);
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

	public void clearTile(int x, int y) {
		board.get(y).set(x, null);
	}

	public boolean checkMoveValid(int down, int right) {
		boolean valid = false;
		for (int i = Math.max(0, -down); i < length - Math.max(0, down); i++) {
			for (int j = Math.max(0, -right); j < length - Math.max(0, right); j++) {
				Tile tile = board.get(i).get(j);
				Tile adjacentTile = board.get(i + down).get(j + right);
				if (tile != null && (adjacentTile == null || adjacentTile.getValue() == tile.getValue())) {
					valid = true;
					break;
				}
			}
		}
		return valid;
	}

	public void printBoard() {
		String line;
		for (List<Tile> row : board) {
			line = "";
			for (Tile tile : row) {
				line += (tile == null ? 0 : tile.getValue());
				line += " ";
			}
			System.out.println(line);
		}
		System.out.println();
	}

	public void move(int down, int right) {
		if (checkMoveValid(down, right)) {
			boolean isVert = (right == 0);
			boolean isForward = (down + right == 1);

			if (isForward) {
				for (int i = length - 2; i >= 0; i--) {
					for (int j = 0; j < length; j++) {
						int offset = 1;
						boolean merge = false;

						Tile startTile = (isVert ? board.get(i).get(j) : board.get(j).get(i));
						Tile tile = (isVert ? board.get(i + offset).get(j) : board.get(j).get(i + offset));
						if (startTile != null) {
							while (i + offset < length && (tile == null || tile.getValue() == startTile.getValue()) && !merge) {
								if (tile != null && tile.getValue() == startTile.getValue()) {
									tile.doubleValue();
									if (isVert)
										clearTile(j, i);
									else
										clearTile(i, j);
									merge = true;
								}
								else {
									offset++;
									tile = (isVert ? getTile(j, i + offset) : getTile(i + offset, j));
								}
							}
							if (!merge) {
								if (isVert)
									moveTile(j, i, j, i + offset - 1);
								else
									moveTile(i, j, i + offset - 1, j);
							}
						}
					}
				}
			}
			else {
				for (int i = 1; i < length; i++) {
					for (int j = 0; j < length; j++) {
						int offset = -1;
						boolean merge = false;

						Tile startTile = (isVert ? board.get(i).get(j) : board.get(j).get(i));
						Tile tile = (isVert ? board.get(i + offset).get(j) : board.get(j).get(i + offset));
						if (startTile != null) {
							while (i + offset >= 0 && (tile == null || tile.getValue() == startTile.getValue()) && !merge) {
								if (tile != null && tile.getValue() == startTile.getValue()) {
									tile.doubleValue();
									if (isVert)
										clearTile(j, i);
									else
										clearTile(i, j);
									merge = true;
								}
								else {
									offset--;
									tile = (isVert ? getTile(j, i + offset) : getTile(i + offset, j));
								}
							}
							if (!merge) {
								if (isVert)
									moveTile(j, i, j, i + offset + 1);
								else
									moveTile(i, j, i + offset + 1, j);
							}
						}
					}
				}
			}
		}
		printBoard();
	}

}
