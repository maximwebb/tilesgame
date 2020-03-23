package dev;
import dev.display.Display;
import dev.input.KeyManager;
import dev.models.DFS;
import dev.models.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.List;
import java.util.ArrayList;

public class Game implements Runnable {
	private final Model model;
	private Display display;
	public int width, height;
	public String title;
	public GameBoard gameBoard;
	public List<GameBoard> gameBoardHistory;
	public int ticks = 0;

	private boolean running = false;
	private Thread thread;

	private BufferStrategy bs;
	private Graphics g;

	private KeyManager keyManager;

	public Game(String title, int width, int height, Model model){
		this.width = width;
		this.height = height;
		this.title = title;
		this.model = model;

		keyManager = new KeyManager();
		gameBoard = new GameBoard(this, 4);
		gameBoardHistory = new ArrayList<>();
		gameBoard.addRandomTile();
		gameBoard.addRandomTile();
		//gameBoard.addTile(0, 0, 4);
		//gameBoard.addTile(1, 0, 2);
		//gameBoard.addTile(2, 0, 2);
		gameBoard.printBoard();
	}

	private void init(){
		display = new Display(title, width, height);
		display.getFrame().addKeyListener(keyManager);
	}

	private void tick(){
		ticks++;
		if (this.model == null) {
			if (gameBoard.checkValidMoveExists()) {
				gameBoard.tick();
			}
			else {
				System.out.println("Game Over...");
				System.out.println("Score:" + gameBoard.getScore());
				stop();
			}
		}
		else {
			//if (ticks % 100 == 0) {
				List<Vector> moves = this.model.computeMove(new GameBoard(gameBoard));
				if (moves.isEmpty()) {
					System.out.println("Game Over...");
					System.out.println("Score:" + gameBoard.getScore());
					stop();
				}
				else {
					Vector move = moves.get(0);
					gameBoard.move(move.getY(), move.getX());
					gameBoardHistory.add(new GameBoard(gameBoard));
				}
			//}
		}


	}

	private void render(){
		bs = display.getCanvas().getBufferStrategy();
		if (bs == null){
			display.getCanvas().createBufferStrategy(3);
			bs = display.getCanvas().getBufferStrategy();
		}
		g = bs.getDrawGraphics();

		g.fillRect(0, 0, width, height);
		int offset = (int)(0.1 * width);
		int tileWidth = (int)(0.8 * width/gameBoard.length);
		int borderWidth = 4;
		for (Tile tile : gameBoard.getAllTiles()) {
			int x = tile.x * tileWidth + offset;
			int y = tile.y * tileWidth + offset;
			int digits = (int)Math.log10(tile.getValue());
			g.setColor(new Color(68, 68, 68));
			g.fillRect(x, y, tileWidth, tileWidth);
			g.setColor(gameBoard.colorPalette.get(tile.color));
			g.fillRect(x + borderWidth, y + borderWidth, tileWidth - 2 * borderWidth, tileWidth - 2 * borderWidth);
			g.setColor(Color.white);
			g.setFont(new Font("arial", Font.BOLD, 48));
			g.drawString(String.valueOf(tile.getValue()), (int)(x + 0.5 * tileWidth - 2 * borderWidth - 16 * digits), (int)(y + 0.5 * tileWidth + borderWidth + 10));
		}

		//End Drawing!
		bs.show();
		g.dispose();
	}

	public void run(){

		init();

		while(running){
			tick();
			render();
		}

		stop();

	}

	public synchronized void start(){
		if(running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public synchronized void stop(){
		if(!running)
			return;
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public KeyManager getKeyManager() {
		return keyManager;
	}
}
