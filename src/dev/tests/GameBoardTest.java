package dev.tests;

import dev.Game;
import dev.GameBoard;
import dev.Tile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {
	Game game = new Game("asdf", 1, 1, null);

	@Test
	void boardSizeCorrect() {
		// ARRANGE
		GameBoard gameBoard = new GameBoard(game, 8);

		// ACT
		int boardHeight = gameBoard.board.size();
		int boardWidth = gameBoard.board.get(0).size();

		// ASSERT
		assertEquals(boardHeight, 8);
		assertEquals(boardWidth, 8);
	}

	@Test
	void addTileIsCorrect() {
		// ARRANGE
		GameBoard gameBoard = new GameBoard(game, 4);
		gameBoard.addTile(0, 0, 1);
		gameBoard.addTile(3, 3, 4);
		gameBoard.addTile(2, 1, 16);

		// ACT
		Tile tile1 = gameBoard.board.get(0).get(0);
		Tile tile2 = gameBoard.board.get(3).get(3);
		Tile tile3 = gameBoard.board.get(1).get(2);

		// ASSERT
		assertEquals(tile1, new Tile(1, 0, 0));
		assertEquals(tile2, new Tile(4, 3, 3));
		assertEquals(tile3, new Tile(16, 2, 1));
	}

	@Test
	void getTileIsCorrect() {
		// ARRANGE
		GameBoard gameBoard = new GameBoard(game, 4);
		gameBoard.board.get(0).set(0, new Tile(1, 0, 0));
		gameBoard.board.get(3).set(1, new Tile(16, 1, 3));
		gameBoard.board.get(2).set(2, new Tile(32, 2, 2));

		// ACT
		Tile tile1 = gameBoard.getTile(0, 0);
		Tile tile2 = gameBoard.getTile(1, 3);
		Tile tile3 = gameBoard.getTile(2, 2);

		// ASSERT
		assertEquals(tile1, new Tile(1, 0, 0));
		assertEquals(tile2, new Tile(16, 1, 3));
		assertEquals(tile3, new Tile(32, 2, 2));
	}

	@Test
	void clearTileIsCorrect() {
		// ARRANGE
		GameBoard gameBoard = new GameBoard(game, 4);
		gameBoard.board.get(0).set(0, new Tile(1, 0, 0));
		gameBoard.board.get(3).set(1, new Tile(16, 1, 3));
		gameBoard.board.get(2).set(2, new Tile(32, 2, 2));

		// ACT
		gameBoard.clearTile(0, 0);
		gameBoard.clearTile(1, 3);

		// ASSERT
		assertTrue(gameBoard.getTile(0, 0).empty);
		assertTrue(gameBoard.getTile(1, 3).empty);
		assertFalse(gameBoard.getTile(2, 2).empty);
	}

	@Test
	void moveTilesWithoutMerging() {
		// ARRANGE
		GameBoard gameBoard = new GameBoard(game, 4);
		gameBoard.board.get(2).set(0, new Tile(2, 0, 2));
		gameBoard.board.get(2).set(2, new Tile(4, 2, 2));

		// ACT
		gameBoard.move(0, 1);

		// ASSERT
		assertEquals(gameBoard.board.get(2).get(2), new Tile(2, 2, 2));
		assertEquals(gameBoard.board.get(2).get(3), new Tile(4, 3, 2));
	}

	@Test
	void moveTilesWithSingleMerge() {
		// ARRANGE
		GameBoard gameBoard = new GameBoard(game, 4);
		gameBoard.board.get(2).set(0, new Tile(2, 0, 2));
		gameBoard.board.get(2).set(2, new Tile(2, 2, 2));

		// ACT
		gameBoard.move(0, -1);

		// ASSERT
		assertEquals(gameBoard.board.get(2).get(0), new Tile(4, 0, 2));
	}

	@Test
	void multipleMergesCorrect1() {
		// ARRANGE
		GameBoard gameBoard = new GameBoard(game, 4);
		gameBoard.board.get(2).set(0, new Tile(2, 0, 2));
		gameBoard.board.get(2).set(1, new Tile(2, 1, 2));
		gameBoard.board.get(2).set(2, new Tile(2, 2, 2));
		gameBoard.board.get(2).set(3, new Tile(2, 3, 2));

		// ACT
		gameBoard.move(0, 1);

		// ASSERT
		assertEquals(gameBoard.board.get(2).get(2), new Tile(4, 2, 2));
		assertEquals(gameBoard.board.get(2).get(3), new Tile(4, 3, 2));
	}

	@Test
	void multipleMergesCorrect2() {
		// ARRANGE
		GameBoard gameBoard = new GameBoard(game, 4);
		gameBoard.board.get(1).set(0, new Tile(4, 0, 1));
		gameBoard.board.get(1).set(2, new Tile(2, 2, 1));
		gameBoard.board.get(1).set(3, new Tile(2, 3, 1));

		// ACT
		gameBoard.move(0, 1);

		// ASSERT
		assertEquals(gameBoard.board.get(1).get(2), new Tile(4, 2, 1));
		assertEquals(gameBoard.board.get(1).get(3), new Tile(4, 3, 1));
	}
}
