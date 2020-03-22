package dev.display;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Display {

	private JFrame frame;
	private Canvas canvas;

	private String title;
	private int width, height;

	public HashMap<Integer, Color> colorPalette = new HashMap<>();

	public Display(String title, int width, int height){
		this.title = title;
		this.width = width;
		this.height = height;

		colorPalette.put(0, new Color(227, 227, 227));
		colorPalette.put(1, new Color(200, 200, 200));
		colorPalette.put(2, new Color(245, 241, 201));
		colorPalette.put(3, new Color(237, 229, 147));
		colorPalette.put(4, new Color(245, 233, 108));
		colorPalette.put(5, new Color(247, 192, 64));
		colorPalette.put(6, new Color(222, 149, 2));
		colorPalette.put(7, new Color(196, 85, 0));
		colorPalette.put(8, new Color(184, 48, 7));
		colorPalette.put(9, new Color(250, 57, 57));
		colorPalette.put(10, new Color(250, 57, 125));
		colorPalette.put(11, new Color(179, 36, 143));
		colorPalette.put(12, new Color(127, 31, 156));
		colorPalette.put(13, new Color(85, 31, 156));

		createDisplay();
	}

	private void createDisplay(){
		frame = new JFrame(title);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(width, height));
		canvas.setMaximumSize(new Dimension(width, height));
		canvas.setMinimumSize(new Dimension(width, height));
		canvas.setFocusable(false);

		frame.add(canvas);
		frame.pack();
	}

	public Canvas getCanvas(){
		return canvas;
	}

	public JFrame getFrame() {
		return frame;
	}


}
