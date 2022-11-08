package app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import utils.ApplicationTime;

public class Lanchester extends Animation {

	@Override
	protected ArrayList<JFrame> createFrames(ApplicationTime applicationTimeThread) {
		// a list of all frames (windows) that will be shown
		ArrayList<JFrame> frames = new ArrayList<>();

		// Create main frame (window)
		JFrame frame = new JFrame("Mathematik und Simulation: Lanchester Modell");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new LanchesterPanel(applicationTimeThread);
		frame.add(panel);
		frame.pack(); // adjusts size of the JFrame to fit the size of it's components
		frame.setVisible(true);

		frames.add(frame);
		createGraphFrame(applicationTimeThread);
		return frames;
	}

	private static void createGraphFrame(ApplicationTime thread) {
		JFrame graphFrame = new JFrame("Mathematik und Simulation: Graphen der Funktionen G(t) und H(t)");
		graphFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();

		graphFrame.add(panel);
		graphFrame.setVisible(true);
		graphFrame.pack();
	}

}

class LanchesterPanel extends JPanel {

	// panel has a single time tracking thread associated with it
	private final ApplicationTime t;

	private double time;

	public LanchesterPanel(ApplicationTime thread) {
		this.t = thread;
	}

	// set this panel's preferred size for auto-sizing the container JFrame
	public Dimension getPreferredSize() {
		return new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
	}

	int width = Constants.WINDOW_WIDTH;
	int height = Constants.WINDOW_HEIGHT;
	double startX = 500;
	double startY = 300;
	double vX = 5;
	double vY = 5;
	double currentX = startX;
	double currentY = startY;
	int diameter = 50;

	// drawing operations should be done in this method
	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		time = t.getTimeInSeconds();

		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

		double deltaTime = 0.0;
		double lastFrameTime = 0.0;
		deltaTime = time - lastFrameTime;
		lastFrameTime = time;
		currentX += (vX * deltaTime);
		currentY += (vY * deltaTime);

		if (currentX > Constants.WINDOW_WIDTH - diameter || currentX < 0) {
			System.out.println("Object has hit the a side wall.");
			vX *= -1;
		}
		if (currentY < 0 || currentY > Constants.WINDOW_HEIGHT - diameter) {
			System.out.println("Object has hit the ceiling or floor.");
			vY *= -1;
		}

		g.setColor(Color.RED);
		g.fillOval((int) currentX, (int) currentY, diameter, diameter);

	}
}
