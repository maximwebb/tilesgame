package dev.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

public class KeyManager implements KeyListener {

	private boolean[] keys;
	public boolean up, down, left, right, pressComplete;


	public KeyManager() {
		keys = new boolean[256];
	}

	public void tick() {
		up = keys[KeyEvent.VK_UP];
		down = keys[KeyEvent.VK_DOWN];
		left = keys[KeyEvent.VK_LEFT];
		right = keys[KeyEvent.VK_RIGHT];
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		Arrays.fill(keys, false);
		keys[e.getKeyCode()] = true;
		pressComplete = false;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		pressComplete = true;
	}

	public void completePress() {
		pressComplete = false;
	}
}
