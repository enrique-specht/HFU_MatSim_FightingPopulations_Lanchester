package app;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.*;
import javax.swing.event.*;

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
		frame.setVisible(false);

		frames.add(frame);

		createGraphFrame(applicationTimeThread, frame);

		return frames;
	}

	private static void createGraphFrame(ApplicationTime thread, JFrame frame) {
		JFrame graphFrame = new JFrame("Mathematik und Simulation: Graphen der Funktionen G(t) und H(t)");
		graphFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel graphPanel = new JPanel();

		graphFrame.add(graphPanel);
		graphFrame.setVisible(false);
		graphFrame.pack();

		createStartFrame(thread, frame, graphFrame);
	}

	private static void createStartFrame(ApplicationTime thread, JFrame frame, JFrame graphFrame) {
		JFrame startFrame = new JFrame("Mathematik und Simulation: Lanchester Starteigenschaften");
		startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		startFrame.setLayout(new GridLayout(1, 4, 10, 0));

		JPanel startPanel = new JPanel();
		startPanel.setLayout(new GridLayout(7, 2, 10, 0));

		int intMinimum = 0;
		int intMaximum = 1000;
		int textFieldSize = 20;

		//slider and inputs for G0 and H0
		JLabel g0Label = new JLabel("Populationsstärke G0:");
		startPanel.add(g0Label);
		JLabel h0Label = new JLabel("Populationsstärke H0:");
		startPanel.add(h0Label);
		JSlider g0Slider = new JSlider(intMinimum, intMaximum);
		startPanel.add(g0Slider);
		JSlider h0Slider = new JSlider(intMinimum, intMaximum);
		startPanel.add(h0Slider);
		JTextField g0Input = new JTextField(textFieldSize);
		startPanel.add(g0Input);
		JTextField h0Input = new JTextField(textFieldSize);
		startPanel.add(h0Input);

		//sliders and inputs for r and s
		JLabel rLabel = new JLabel("r:");
		startPanel.add(rLabel);
		JLabel sLabel = new JLabel("s:");
		startPanel.add(sLabel);
		JSlider rSlider = new JSlider(intMinimum, intMaximum);
		startPanel.add(rSlider);
		JSlider sSlider = new JSlider(intMinimum, intMaximum);
		startPanel.add(sSlider);
		JTextField rInput = new JTextField(textFieldSize);
		startPanel.add(rInput);
		JTextField sInput = new JTextField(textFieldSize);
		startPanel.add(sInput);
		JButton startButton = new JButton("Starten");
		startPanel.add(startButton);

		g0Input.setText(""+g0Slider.getValue());
		h0Input.setText(""+h0Slider.getValue());
		rInput.setText(""+ (double) rSlider.getValue()/100);
		sInput.setText(""+ (double) sSlider.getValue()/100);

		//set sliders and inputs
		JSlider[] allSliders = new JSlider[4];
		allSliders[0] = g0Slider;
		allSliders[1] = h0Slider;
		allSliders[2] = rSlider;
		allSliders[3] = sSlider;

		JTextField[] allInputs = new JTextField[4];
		allInputs[0] = g0Input;
		allInputs[1] = h0Input;
		allInputs[2] = rInput;
		allInputs[3] = sInput;

		//Sync Sliders with Textfield
		for (JSlider slider : allSliders) {
			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					int i = Arrays.asList(allSliders).indexOf(slider);
					if(allInputs[i] == allInputs[2] || allInputs[i] == allInputs[3]) {
						allInputs[i].setText("" + (double) allSliders[i].getValue()/100);
					} else {
						allInputs[i].setText("" + allSliders[i].getValue());
					}
				}
			});
		}

		//Sync Textfield with Sliders
		for (JTextField input : allInputs) {
			input.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent e) {
					int i = Arrays.asList(allInputs).indexOf(input);
					if (allInputs[i].getText() == null) {
						allSliders[i].setValue(1);
					}
					try {
						int x = Integer.parseInt(allInputs[i].getText());
						if (x > intMaximum || x < intMinimum) {
							allInputs[i].setText("");
						}
						allSliders[i].setValue(Integer.parseInt(allInputs[i].getText()));
					} catch (NumberFormatException nfe) {
						allInputs[i].setText("");
						allSliders[i].setValue(1);
					}

				}
			});
		}

		//start button
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				G0 = allSliders[0].getValue();
				H0 = allSliders[1].getValue();
				r = (double) allSliders[2].getValue()/100;
				s= (double) allSliders[3].getValue()/100;
				startFrame.dispose();
				frame.setVisible(true);
				graphFrame.setVisible(true);
				LanchesterPanel.initialization();
				System.out.println("G0=" + G0 + " H0=" + H0 + " r=" + r + " s=" + s);
			}
		} );

		startFrame.add(startPanel);
		startFrame.setVisible(true);
		startFrame.pack();
	}

	public static int G0;
	public static int H0;
	public static double r;
	public static double s;
}

class LanchesterPanel extends JPanel {

	private final ApplicationTime t;
	private double time;

	private static double[] g0vX;
	private static double[] g0vY;
	private static double[] g0currentX;
	private static double[] g0currentY;
	private static double[] h0vX;
	private static double[] h0vY;
	private static double[] h0currentX;
	private static double[] h0currentY;

	public LanchesterPanel(ApplicationTime thread) {
		this.t = thread;
	}

	// set this panel's preferred size for auto-sizing the container JFrame
	public Dimension getPreferredSize() {
		return new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
	}

	static int width = Constants.WINDOW_WIDTH;
	static int height = Constants.WINDOW_HEIGHT;
	static int halfWidth = width/2;
	static int diameter = 10;
	static double velocity = 4;

	public static void initialization() {
		//G0 params
		double[] g0startX = new double[Lanchester.G0];
		double[] g0startY = new double[Lanchester.G0];
		g0vX = new double[Lanchester.G0];
		g0vY = new double[Lanchester.G0];
		g0currentX = new double[Lanchester.G0];
		g0currentY = new double[Lanchester.G0];

		for (int i = 0; i < Lanchester.G0; i++) {

			g0startX[i] = Math.floor(Math.random()*(halfWidth-diameter-diameter+1)+diameter);
			g0startY[i] = Math.floor(Math.random()*(height-diameter-diameter+1)+diameter);

			Random r = new Random();
			switch (r.nextInt(2)) {
				case 0 -> g0vX[i] = velocity;
				case 1 -> g0vX[i] = -velocity;
			}
			switch (r.nextInt(2)) {
				case 0 -> g0vY[i] = velocity;
				case 1 -> g0vY[i] = -velocity;
			}

			g0currentX[i] = g0startX[i];
			g0currentY[i] = g0startY[i];
		}

		//H0 params
		double[] h0startX = new double[Lanchester.H0];
		double[] h0startY = new double[Lanchester.H0];
		h0vX = new double[Lanchester.H0];
		h0vY = new double[Lanchester.H0];
		h0currentX = new double[Lanchester.H0];
		h0currentY = new double[Lanchester.H0];

		for (int i = 0; i < Lanchester.H0; i++) {

			h0startX[i] = Math.floor(Math.random()*(width-diameter-halfWidth+diameter+1)+halfWidth+diameter);
			h0startY[i] = Math.floor(Math.random()*(height-diameter-diameter+1)+diameter);

			Random r = new Random();
			switch (r.nextInt(2)) {
				case 0 -> h0vX[i] = velocity;
				case 1 -> h0vX[i] = -velocity;
			}
			switch (r.nextInt(2)) {
				case 0 -> h0vY[i] = velocity;
				case 1 -> h0vY[i] = -velocity;
			}

			h0currentX[i] = h0startX[i];
			h0currentY[i] = h0startY[i];
		}
	}
	public static void lanchesterMath (double G0,double H0,double r,double s) {
		int t = 0;
		while (G0 >= 0.5 || H0 >= 0.5) {

			double Gvont = 0;
			double Hvont = 0;
			if (G0 <= 0.5) {

			}
			double k = Math.sqrt(s * r);
			Gvont = G0 * Math.cosh(k * t) - Math.sqrt(r / s) * H0 * Math.sinh(k * t);
			Hvont = H0 * Math.cosh(k * t) - Math.sqrt(r / s) * G0 * Math.sinh(k * t);
			t++;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		//stop drawing if start parameter haven't been chosen yet
		if(Lanchester.G0 == 0 || Lanchester.H0 == 0) {
			return;
		}

		super.paintComponent(g);
		time = t.getTimeInSeconds();

		//draw background
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, width, height);

		//draw field
		g.setColor(Color.BLUE);
		g.drawRect(1, 0, width/2-2, height-1);
		g.setColor(Color.RED);
		g.drawRect(width/2+1, 0, width/2-2, height-1);
		g.setColor(Color.BLACK);
		g.drawLine(width/2, 0, width/2, height);

		//draw start params
		g.setColor(Color.BLACK);
		int textdistance = 25;
		g.drawString("Start Parameter:", textdistance, textdistance);
		g.drawString("G0 = " + String.valueOf(Lanchester.G0),textdistance,textdistance*2);
		g.drawString("H0 = " + String.valueOf(Lanchester.H0),textdistance,textdistance*3);
		g.drawString("r = " + String.valueOf(Lanchester.r),textdistance,textdistance*4);
		g.drawString("s = " + String.valueOf(Lanchester.s),textdistance,textdistance*5);

		//draw G0 population
		for (int i = 0; i < Lanchester.G0; i++) {

			g0currentX[i] += g0vX[i];
			g0currentY[i] += g0vY[i];

			if (g0currentX[i] < halfWidth) {
				if (g0currentX[i] > halfWidth - diameter || g0currentX[i] < 0) {
					g0vX[i] *= -1;
				}
			}
			if (g0currentX[i] > halfWidth) {
				if (g0currentX[i] > width - diameter || g0currentX[i] < halfWidth) {
					g0vX[i] *= -1;
				}
			}
			if (g0currentY[i] < 0 || g0currentY[i] > height - diameter) {
				g0vY[i] *= -1;
			}

			g.setColor(Color.BLUE);
			g.fillOval((int) g0currentX[i], (int) g0currentY[i], diameter, diameter);

		}
		//draw H0 population
		for (int i = 0; i < Lanchester.H0; i++) {

			h0currentX[i] += h0vX[i];
			h0currentY[i] += h0vY[i];

			if (h0currentX[i] < halfWidth) {
				if (h0currentX[i] > halfWidth - diameter || h0currentX[i] < 0) {
					h0vX[i] *= -1;
				}
			}
			if (h0currentX[i] > halfWidth) {
				if (h0currentX[i] > width - diameter || h0currentX[i] < halfWidth) {
					h0vX[i] *= -1;
				}
			}
			if (h0currentY[i] < 0 || h0currentY[i] > height - diameter) {
				h0vY[i] *= -1;
			}

			g.setColor(Color.RED);
			g.fillOval((int) h0currentX[i], (int) h0currentY[i], diameter, diameter);

		}

	}
}
