package dev;

import java.awt.*;

public class Tile {
	int value;
	int color;
	int x;
	int y;

	private Tile (int value, int color, int x, int y) {
		this.value = value;
		this.color = color == -1 ? (int) ((int) Math.log(value) / Math.log(2)) : color;
		this.x = x;
		this.y = y;
	}

	public Tile (int value, int x, int y) {
		this.value = value;
		this.color = (int) (Math.log(value) / Math.log(2));
		this.x = x;
		this.y = y;
	}

	public Tile (Tile tile, int xNew, int yNew) {
		this.value = tile.value;
		this.color = tile.color;
		this.x = xNew;
		this.y = yNew;
	}

	public void setPos(int xNew, int yNew) {
		x = xNew;
		y = yNew;
	}

	public int getValue() {
		return value;
	}

	public void doubleValue() {
		this.value *= 2;
	}
}
