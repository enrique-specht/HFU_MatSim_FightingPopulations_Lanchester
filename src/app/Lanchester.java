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
		graphFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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
	int halfWidth = width/2;
	double startX = 600;
	double startY = 300;
	double vX = 5;
	double vY = 5;
	double currentX = startX;
	double currentY = startY;
	int diameter = 10;

	// drawing operations should be done in this method
	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		time = t.getTimeInSeconds();

		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, width, height);

		currentX += vX;
		currentY += vY;

		g.setColor(Color.BLUE);
		g.drawRect(1, 0, width/2-2, height-1);
		g.setColor(Color.RED);
		g.drawRect(width/2+1, 0, width/2-2, height-1);
		g.setColor(Color.BLACK);
		g.drawLine(width/2, 0, width/2, height);

		if(currentX < halfWidth) {
			if (currentX > halfWidth - diameter || currentX < 0) {
				vX *= -1;
			}
		}
		if(currentX > halfWidth) {
			if (currentX > width - diameter || currentX < halfWidth) {
				vX *= -1;
			}
		}
		if (currentY < 0 || currentY > height - diameter) {
			vY *= -1;
		}

		g.setColor(Color.RED);
		g.fillOval((int) currentX, (int) currentY, diameter, diameter);

	}
}
