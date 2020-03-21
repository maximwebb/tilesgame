package dev;
import dev.display.Display;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class Game implements Runnable {
	private Display display;
	public int width, height;
	public String title;
	public GameBoard gameBoard;
	public int count = 0;

	private boolean running = false;
	private Thread thread;

	private BufferStrategy bs;
	private Graphics g;

	public Game(String title, int width, int height){
		this.width = width;
		this.height = height;
		this.title = title;
		gameBoard = new GameBoard(4);
		gameBoard.addRandomTile();
		gameBoard.addRandomTile();
		gameBoard.printBoard();
	}

	private void init(){
		display = new Display(title, width, height);
	}

	private void tick(){

		if (gameBoard.checkValidMoveExists()) {
			if (count % 4 == 0)
				gameBoard.move(0, 1);
			if (count % 4 == 1)
				gameBoard.move(1, 0);
			if (count % 4 == 2)
				gameBoard.move(0, -1);
			if (count % 4 == 3)
				gameBoard.move(-1, 0);
			System.out.println(gameBoard.getScore());
			count++;
		}
		else {
			System.out.println("Game Over...");
			System.out.println("Score:" + gameBoard.getScore());
			stop();
		}

	}

	private void render(){
		bs = display.getCanvas().getBufferStrategy();
		if (bs == null){
			display.getCanvas().createBufferStrategy(3);
			return;
		}
		g = bs.getDrawGraphics();
		//Draw Here!

		g.fillRect(0, 0, width, height);
		int offset = (int)(0.1 * width);
		int tileWidth = (int)(0.8 * width/gameBoard.length);
		int borderWidth = 4;
		for (Tile tile : gameBoard.getAllTiles()) {
			g.setColor(new Color(68, 68, 68));
			g.fillRect(tile.x * tileWidth + offset, tile.y * tileWidth + offset, tileWidth, tileWidth);
			g.setColor(gameBoard.colorPalette.get(tile.color));
			g.fillRect(tile.x * tileWidth + offset + borderWidth, tile.y * tileWidth + offset + borderWidth, tileWidth - 2 * borderWidth, tileWidth - 2 * borderWidth);
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
}
