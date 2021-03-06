package dev;

import java.awt.*;
import java.util.Comparator;

public class Tile {
	int value;
	int color;
	int x;
	int y;
	public boolean empty;
	public boolean merged = false;

	/* Value of 1 corresponds to empty */
	private Tile (int value, int color, int x, int y) {
		this.value = value;
		this.color = color == -1 ? (int) ((int) Math.log(value) / Math.log(2)) : color;
		this.x = x;
		this.y = y;
		this.empty = (value == 1);
	}

	public Tile (int value, int x, int y) {
		this.value = value;
		this.color = (int) (Math.log(value) / Math.log(2));
		this.x = x;
		this.y = y;
		this.empty = (value == 1);
	}

	public Tile (Tile tile) {
		this.value = tile.value;
		this.color = tile.color;
		this.x = tile.x;
		this.y = tile.y;
		this.empty = (value == 1);
		this.merged = tile.merged;
	}

	public void setPos(int xNew, int yNew) {
		x = xNew;
		y = yNew;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int val) {
		this.value = val;
		this.color = (int) (Math.log(this.value) / Math.log(2));
		this.empty = (val == 1);
	}

	public void doubleValue() {
		this.value *= 2;
		this.color++;
		this.empty = false;
	}

	public void setEmpty() {
		this.value = 1;
		this.color = 0;
		this.empty = true;
	}

	public void setMerged(boolean merge) {
		this.merged = merge;
	}

	@Override
	public boolean equals(Object o) {
		Tile t = (Tile) o;
		return this.value == t.value && this.color == t.color && this.x == t.x && this.y == t.y && this.empty == t.empty;
	}
}
