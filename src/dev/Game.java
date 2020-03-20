package dev;
import dev.display.Display;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class Game implements Runnable {
	private Display display;
	public int width, height;
	public String title;
	public GameBoard gameBoard;

	private boolean running = false;
	private Thread thread;

	private BufferStrategy bs;
	private Graphics g;

	public Game(String title, int width, int height){
		this.width = width;
		this.height = height;
		this.title = title;
		gameBoard = new GameBoard(4);
		gameBoard.addTile(0, 0, 2);
		gameBoard.addTile(1, 0, 2);
		gameBoard.addTile(2, 0, 2);
		gameBoard.addTile(3, 0, 2);
	}

	private void init(){
		display = new Display(title, width, height);
	}

	private void tick(){
		gameBoard.move(1, 0);
		gameBoard.move(0, -1);
		gameBoard.move(0, 1);
	}

	private void render(){
		bs = display.getCanvas().getBufferStrategy();
		if(bs == null){
			display.getCanvas().createBufferStrategy(3);
			return;
		}
		g = bs.getDrawGraphics();
		//Draw Here!

		g.fillRect(0, 0, 100, 100);

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
