package dev;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GameBoard {

	public final int length;
	public List<List<Tile>> board;
	private int score;

	public HashMap<Integer, Color> colorPalette = new HashMap<>();
	private Color[] colors = {new Color(255, 255, 255),
			new Color(180, 180, 180),
			new Color(245, 222, 96),
			new Color(237, 172, 70),
			new Color(245, 123, 69),
			new Color(247, 77, 32),
			new Color(187, 22, 17),
			new Color(125, 2, 9),
			new Color(72, 24, 112),
			new Color(195, 30, 250),
			new Color(250, 100, 196),
			new Color(72, 82, 203),
			new Color(59, 183, 192),
			new Color(38, 156, 65)
	};

	public GameBoard (int length) {
		this.length = length;
		this.board = new ArrayList<>();

		for (int i = 0; i < length; i++) {
			List<Tile> row = new ArrayList<>();
			for (int j = 0; j < length; j++) {
				row.add(new Tile(1, j, i));
			}
			board.add(row);
		}

		for (int i = 0; i < colors.length; i++) {
			colorPalette.put(i, colors[i]);
		}
	}

	// Copy constructor, used for simulating game play.
	public GameBoard (GameBoard gameBoard) {
		this.length = gameBoard.length;
		this.board = new ArrayList<>();
		this.colors = gameBoard.colors;

		for (int i = 0; i < gameBoard.board.size(); i++) {
			this.board.add(new ArrayList<>());
			for (int j = 0; j < gameBoard.board.size(); j++) {
				this.board.get(i).add(new Tile(gameBoard.getTile(j, i)));
			}
		}

		for (int i = 0; i < colors.length; i++) {
			colorPalette.put(i, colors[i]);
		}

		this.score = gameBoard.getScore();
	}

	public void tick() {
		Game game = Game.getInstance();
		game.getKeyManager().tick();
		if (game.getKeyManager().pressComplete) {
			if (game.getKeyManager().up) {
				move(-1, 0);
				game.getKeyManager().completePress();
				System.out.println("Up");
			} else if (game.getKeyManager().down) {
				move(1, 0);
				game.getKeyManager().completePress();
				System.out.println("Down");
			} else if (game.getKeyManager().left) {
				move(0, -1);
				game.getKeyManager().completePress();
				System.out.println("Left");
			} else if (game.getKeyManager().right) {
				move(0, 1);
				game.getKeyManager().completePress();
				System.out.println("Right");
			}
		}
	}

	public void addTile(int x, int y, int value) {
		if (x >= 0 && x < length && y >= 0 && y < length) {
			board.get(y).set(x, new Tile(value, x, y));
		}
	}

	public void addRandomTile() {
		List<Vector> emptyTilesList = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				if (getTile(i, j).empty) {
					emptyTilesList.add(new Vector(i, j));
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

	public void setAllTilesUnmerged() {
		for (List<Tile> row : board) {
			for (Tile tile : row) {
				tile.setMerged(false);
			}
		}
	}

	public void clearTile(int x, int y) {
		getTile(x, y).setEmpty();
	}

	public boolean checkMoveValid(int down, int right) {
		boolean valid = false;
		if (!(Math.abs(down) == 1 && right == 0) && !(Math.abs(right) == 1 && down == 0)) {
			return false;
		}
		for (int i = Math.max(0, -down); i < length - Math.max(0, down); i++) {
			for (int j = Math.max(0, -right); j < length - Math.max(0, right); j++) {
				Tile tile = getTile(j, i);
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
		return !getValidMoves().isEmpty();
	}

	public List<Vector> getValidMoves() {
		List<Vector> results = new ArrayList<>();
		if (checkMoveValid(-1, 0))
			results.add(Vector.up());
		if (checkMoveValid(1, 0))
			results.add(Vector.down());
		if (checkMoveValid(0, -1))
			results.add(Vector.left());
		if (checkMoveValid(0, 1))
			results.add(Vector.right());

		return results;
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
			setAllTilesUnmerged();
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
						while (tile != null && i + offset < length && i + offset >= 0 && (tile.empty || (tile.getValue() == startTile.getValue() && !tile.merged)) && !merge) {
							if (!tile.empty && tile.getValue() == startTile.getValue()) {
								tile.doubleValue();
								score += tile.getValue();
								if (isVert)
									clearTile(j, i);
								else
									clearTile(i, j);
								merge = true;
								tile.setMerged(true);
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
							//printBoard();
						}
					}
				}
			}
			addRandomTile();
		}
		//printBoard();
	}

}
