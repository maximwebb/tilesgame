package dev;

public class Vector {
	private final int y;
	private final int x;

	public Vector(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public static Vector up() {
		return new Vector(0, -1);
	}

	public static Vector down() {
		return new Vector(0, 1);
	}

	public static Vector left() {
		return new Vector(-1, 0);
	}

	public static Vector right() {
		return new Vector(1, 0);
	}
}
