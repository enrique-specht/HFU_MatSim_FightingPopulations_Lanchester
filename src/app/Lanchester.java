package app;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.*;

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

		// Create GraphFrame
		JFrame graphFrame = new JFrame("Mathematik und Simulation: Graphen der Funktionen G(t) und H(t)");
		graphFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel graphPanel = new GraphPanel();

		graphFrame.add(graphPanel);
		graphFrame.setVisible(false);
		graphFrame.pack();

		frames.add(graphFrame);

		createStartFrame(frame, graphFrame);

		return frames;
	}

	private static void createStartFrame(JFrame frame, JFrame graphFrame) {
		JFrame startFrame = new JFrame("Mathematik und Simulation: Lanchester Starteigenschaften");
		startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		startFrame.setLayout(new GridLayout(1, 4, 10, 10));

		JPanel startPanel = new JPanel();
		startPanel.setLayout(new GridLayout(9, 2, 10, 10));

		int populationMinimum = 0;
		int populationMaximum = 1000;
		int textFieldSize = 20;
		int effectivityMinimum = 0;
		int effectivityMaximum = 100;

		//slider and inputs for G0 and H0
		JLabel g0Label = new JLabel("  Populationsstärke G0:");
		startPanel.add(g0Label);
		JLabel h0Label = new JLabel("  Populationsstärke H0:");
		startPanel.add(h0Label);
		JSlider g0Slider = new JSlider(populationMinimum, populationMaximum);
		startPanel.add(g0Slider);
		JSlider h0Slider = new JSlider(populationMinimum, populationMaximum);
		startPanel.add(h0Slider);
		JTextField g0Input = new JTextField(textFieldSize);
		startPanel.add(g0Input);
		JTextField h0Input = new JTextField(textFieldSize);
		startPanel.add(h0Input);

		//sliders and inputs for r and s
		JLabel sLabel = new JLabel("  s:");
		startPanel.add(sLabel);
		JLabel rLabel = new JLabel("  r:");
		startPanel.add(rLabel);
		JSlider sSlider = new JSlider(effectivityMinimum, effectivityMaximum);
		startPanel.add(sSlider);
		JSlider rSlider = new JSlider(effectivityMinimum, effectivityMaximum);
		startPanel.add(rSlider);
		JTextField sInput = new JTextField(textFieldSize);
		startPanel.add(sInput);
		JTextField rInput = new JTextField(textFieldSize);
		startPanel.add(rInput);
		JButton startButton = new JButton("Starten");
		startPanel.add(startButton);

		g0Input.setText(""+g0Slider.getValue());
		h0Input.setText(""+h0Slider.getValue());
		sInput.setText(""+ (double) sSlider.getValue()/100);
		rInput.setText(""+ (double) rSlider.getValue()/100);

		//set sliders and inputs
		JSlider[] allSliders = new JSlider[4];
		allSliders[0] = g0Slider;
		allSliders[1] = h0Slider;
		allSliders[2] = sSlider;
		allSliders[3] = rSlider;

		JTextField[] allInputs = new JTextField[4];
		allInputs[0] = g0Input;
		allInputs[1] = h0Input;
		allInputs[2] = sInput;
		allInputs[3] = rInput;

		//Sync Sliders with Textfield
		for (JSlider slider : allSliders) {
			slider.addChangeListener(e -> {
				int i = Arrays.asList(allSliders).indexOf(slider);
				if(allInputs[i] == allInputs[2] || allInputs[i] == allInputs[3]) {
					allInputs[i].setText("" + (double) allSliders[i].getValue()/100);
				} else {
					allInputs[i].setText("" + allSliders[i].getValue());
				}
			});
		}

		//Sync Textfield with Sliders
		for (JTextField input : allInputs) {
			input.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent e) {
					int i = Arrays.asList(allInputs).indexOf(input);
					if (allInputs[i].getText() == null) {
						allSliders[i].setValue(0);
					}
					try {
						double x = Double.parseDouble(allInputs[i].getText());
						if(allInputs[i] == allInputs[2] || allInputs[i] == allInputs[3]) {
							if (x > (double)effectivityMaximum/100 || x < effectivityMinimum) {
								allInputs[i].setText("");
							}
							if (allInputs[i].getText().length() >= 5 ) {
								String text = allInputs[i].getText();
								StringBuilder sb = new StringBuilder(text);
								sb.deleteCharAt(text.length()-1);
								x = Double.parseDouble(sb.toString());
								allInputs[i].setText("" + x);
							}
							x*=100;
							allSliders[i].setValue((int)x);
						} else {
							if (x > populationMaximum || x < populationMinimum) {
								allInputs[i].setText("");
							}
							allSliders[i].setValue((int)x);
						}

					} catch (NumberFormatException nfe) {
						allInputs[i].setText("");
						allSliders[i].setValue(0);
					}

				}
			});
		}

		//start button
		startButton.addActionListener(e -> {
			G0 = allSliders[0].getValue();
			H0 = allSliders[1].getValue();
			s = (double) allSliders[2].getValue()/100;
			r = (double) allSliders[3].getValue()/100;
			startFrame.dispose();
			frame.setVisible(true);
			graphFrame.setVisible(true);
			System.out.println("G0=" + G0 + " H0=" + H0 + " s=" + s + " r=" + r);
			LanchesterPanel.initialization();
		});

		//Example Buttons
		JLabel emptyLabel = new JLabel("");
		startPanel.add(emptyLabel);

		JButton exampleButton1 = new JButton("Bsp: H gewinnt");
		startPanel.add(exampleButton1);
		exampleButton1.addActionListener(e -> {
			allSliders[0].setValue(400);
			allSliders[1].setValue(300);
			allSliders[2].setValue(13); // s=0.13
			allSliders[3].setValue(25); // r=0.25
		});

		JButton exampleButton2 = new JButton("Bsp: G gewinnt");
		startPanel.add(exampleButton2);
		exampleButton2.addActionListener(e -> {
			allSliders[0].setValue(75);
			allSliders[1].setValue(50);
			allSliders[2].setValue(20); // s=0.2
			allSliders[3].setValue(40); // r=0.4
		});

		JButton exampleButton3 = new JButton("Bsp: Pyrrhussieg G");
		startPanel.add(exampleButton3);
		exampleButton3.addActionListener(e -> {
			allSliders[0].setValue(100);
			allSliders[1].setValue(50);
			allSliders[2].setValue(20); // s=0.2
			allSliders[3].setValue(80); // r=0.8
		});

		JButton exampleButton4 = new JButton("Bsp: Unentschieden");
		startPanel.add(exampleButton4);
		exampleButton4.addActionListener(e -> {
			allSliders[0].setValue(400);
			allSliders[1].setValue(400);
			allSliders[2].setValue(70); // s=0.7
			allSliders[3].setValue(70); // r=0.7
		});

		startFrame.add(startPanel);
		startFrame.setVisible(true);
		startFrame.pack();
	}

	public static double G0;
	public static double H0;
	public static double s;
	public static double r;
}

class LanchesterPanel extends JPanel {

	private final ApplicationTime t;
	protected static double time;
	private double startDelay;

	private static double[] g0vX;
	private static double[] g0vY;
	private static double[] g0currentX;
	private static double[] g0currentY;
	private static double[] h0vX;
	private static double[] h0vY;
	private static double[] h0currentX;
	private static double[] h0currentY;
	private static double currentG;
	private static double currentH;
	private static double l;
	protected static int currentGRounded;
	protected static int currentHRounded;
	protected static double tPlus;
	private static String tPlusRounded;
	private static String calculatedResult;
	protected static int winnerRestPopulation;

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
	static double velocity = 2;

	public static void initialization() {
		//G0 params
		double[] g0startX = new double[(int)Lanchester.G0];
		double[] g0startY = new double[(int)Lanchester.G0];
		g0vX = new double[(int)Lanchester.G0];
		g0vY = new double[(int)Lanchester.G0];
		g0currentX = new double[(int)Lanchester.G0];
		g0currentY = new double[(int)Lanchester.G0];

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
		double[] h0startX = new double[(int)Lanchester.H0];
		double[] h0startY = new double[(int)Lanchester.H0];
		h0vX = new double[(int)Lanchester.H0];
		h0vY = new double[(int)Lanchester.H0];
		h0currentX = new double[(int)Lanchester.H0];
		h0currentY = new double[(int)Lanchester.H0];

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
		testWhoWillWin();

		currentG = Lanchester.G0;
		currentH = Lanchester.H0;
	}

	public static void testWhoWillWin() {
		double k = Math.sqrt(Lanchester.s * Lanchester.r);
		l = (Lanchester.s * Math.pow(Lanchester.G0, 2)) - (Lanchester.r * Math.pow(Lanchester.H0, 2));
		System.out.println("L=" + l);
		if(l > 0) {
			tPlus = 1/k * aTanh((k*Lanchester.H0)/(Lanchester.s*Lanchester.G0));
			tPlusRounded = String.format("%.2f",tPlus);
			double gAtTPlus = Math.sqrt(l / Lanchester.s);
			winnerRestPopulation = (int) Math.round(gAtTPlus);
			System.out.println("G wird nach " + tPlusRounded + "s mit " + winnerRestPopulation + " übrigen Personen gewinnen!");
			calculatedResult = "G gewinnt!";
		}
		if(l < 0) {
			tPlus = 1/k * aTanh((k*Lanchester.G0)/(Lanchester.r*Lanchester.H0));
			tPlusRounded = String.format("%.2f",tPlus);
			double hAtTPlus = Math.sqrt(-l / Lanchester.r);
			winnerRestPopulation = (int) Math.round(hAtTPlus);
			System.out.println("H wird nach " + tPlusRounded + "s mit " + winnerRestPopulation + " übrigen Personen gewinnen!");
			calculatedResult = "H gewinnt!";
		}
		if(l == 0) {
			if(Lanchester.G0 == Lanchester.H0 && Lanchester.r == Lanchester.s) {
				tPlus = -(1/k) * Math.log(1/(2*Lanchester.H0));
				tPlusRounded = String.format("%.2f",tPlus);
				System.out.println("Tragisches Unentschieden nach " + tPlusRounded + "s !");
				calculatedResult = "Tragisches Unentschieden!";
			}
			if(Lanchester.G0 > Lanchester.H0) {
				tPlus = -(1/k) * Math.log(1/(2*Lanchester.H0));
				tPlusRounded = String.format("%.2f",tPlus);
				winnerRestPopulation = 1;
				System.out.println("Pyrrhussieg für G nach " + tPlusRounded + "s!");
				calculatedResult = "Pyrrhussieg für G!";
			}
			if(Lanchester.G0 < Lanchester.H0) {
				tPlus = -(1/k) * Math.log(1/(2*Lanchester.G0));
				tPlusRounded = String.format("%.3f",tPlus);
				winnerRestPopulation = 1;
				System.out.println("Pyrrhussieg für H nach " + tPlusRounded + "s!");
				calculatedResult = "Pyrrhussieg für H!";
			}
		}
		int totalTicks = (int)Math.ceil(tPlus*Constants.FPS*1.25);
		GraphPanel.graphGX = new int[totalTicks];
		GraphPanel.graphGY = new int[totalTicks];
		GraphPanel.graphHX = new int[totalTicks];
		GraphPanel.graphHY = new int[totalTicks];
	}

	private static double aTanh(double x) {
		return (Math.log(1 + x) - Math.log(1 - x)) / 2;
	}

	public void getCurrentGandH () {
		double k = Math.sqrt(Lanchester.s * Lanchester.r);
		//G(t) und H(t)
		currentG = Lanchester.G0 * Math.cosh(k * time) - Math.sqrt(Lanchester.r / Lanchester.s) * Lanchester.H0 * Math.sinh(k * time);
		currentH = Lanchester.H0 * Math.cosh(k * time) - Math.sqrt(Lanchester.s / Lanchester.r) * Lanchester.G0 * Math.sinh(k * time);
		//Round to Int because Persons can't be divided
		currentGRounded = (int) Math.round(currentG);
		currentHRounded = (int) Math.round(currentH);

		//Output for Testing
		if(!t.isInterrupted())
			System.out.println("Bei "+ String.format("%.3f",time) + "s - " + "G:" + currentGRounded + " vs H:" + currentHRounded);
	}

	public void testIfDefeated() {
		if (currentG < 0.5 || currentH < 0.5) {
			//Pause if defeated
			t.endThread();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		//stop drawing if start parameter haven't been chosen yet
		if(Lanchester.G0 == 0 || Lanchester.H0 == 0) {
			return;
		}

		super.paintComponent(g);

		if(startDelay == 0) {
			startDelay = t.getTimeInSeconds();
		}
		time = t.getTimeInSeconds() - startDelay;

		//Test if someone was defeated
		testIfDefeated();
		//Get G(t) and H(t)
		getCurrentGandH();

		//draw background
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, width, height);

		//draw field
		g.setColor(Color.RED);
		g.drawRect(1, 0, width/2-2, height-1);
		g.setColor(Color.BLUE);
		g.drawRect(width/2+1, 0, width/2-2, height-1);
		g.setColor(Color.BLACK);
		g.drawLine(width/2, 0, width/2, height);

		//draw start params
		g.setColor(Color.BLACK);
		int textdistance = 25;
		g.drawString("Start Parameter:", textdistance, textdistance);
		g.drawString("G0 = " + Lanchester.G0,textdistance,textdistance*2);
		g.drawString("H0 = " + Lanchester.H0,textdistance,textdistance*3);
		g.drawString("s = " + Lanchester.s,textdistance,textdistance*4);
		g.drawString("r = " + Lanchester.r,textdistance,textdistance*5);
		//calculated results
		g.drawString("Berechnete Ergebnisse:", textdistance, textdistance*7);
		g.drawString("L = " + l, textdistance, textdistance*8);
		g.drawString(calculatedResult,textdistance,textdistance*9);
		g.drawString("Nach: " + tPlusRounded + "s",textdistance,textdistance*10);
		g.drawString("Mit: " + winnerRestPopulation + " übrigen Person(en)",textdistance,textdistance*11);


		//draw G population
		for (int i = 0; i < currentGRounded; i++) {

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

			g.setColor(Color.RED);
			g.fillOval((int) g0currentX[i], (int) g0currentY[i], diameter, diameter);

		}
		//draw H population
		for (int i = 0; i < currentHRounded; i++) {

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

			g.setColor(Color.BLUE);
			g.fillOval((int) h0currentX[i], (int) h0currentY[i], diameter, diameter);

		}

	}
}

class GraphPanel extends JPanel {

	public GraphPanel() {
	}
	public Dimension getPreferredSize() {
		return new Dimension(Constants.GRAPH_WINDOW_WIDTH, Constants.GRAPH_WINDOW_HEIGHT);
	}
	static int width = Constants.GRAPH_WINDOW_WIDTH;
	static int height = Constants.GRAPH_WINDOW_HEIGHT;
	static int padding = 20;
	static int pointdiameter = 5;
	public static int[] graphGX;
	public static int[] graphGY;
	public static int[] graphHX;
	public static int[] graphHY;
	private static int graphPosition;

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if(LanchesterPanel.tPlus > 11) {
			g.drawString("Die Zeit bis zu einem Sieg ist höher als 11s!", width/2, padding);
			g.drawString("Graph kann eventuell nicht bis zum Ende gezeichnet werden!", width/2, padding*2);
		}

		//Label coordinate system
		g.drawString("Populationsstärke", padding*2, padding/2);
		g.drawString("Zeit t", width-2*padding, height-padding/2);
		g.setColor(Color.red);
		g.drawString("G(t)",padding*7,padding/2);
		g.setColor(Color.blue);
		g.drawString("H(t)",padding*9,padding/2);
		g.setColor(Color.BLACK);

		//Draw coordinate system
		int position = -pointdiameter/2;
		int xLabel = 0;
		while (position<=width) {
			g.fillOval(position,height-pointdiameter,pointdiameter,pointdiameter);
			g.drawString(String.valueOf(xLabel),position,height-padding/4);
			position += 100;
			xLabel +=1;
		}
		position = height-pointdiameter/2;
		int yLabel = 0;
		while (position>=0) {
			g.fillOval(-pointdiameter/2,position,pointdiameter,pointdiameter);
			g.drawString(String.valueOf(yLabel),0,position);
			position -= 50;
			yLabel += 100;
		}

		//Draw G(t) & H(t)
		double time = LanchesterPanel.time*100;

		int x = (int)time-pointdiameter/2;
		int yG = Constants.GRAPH_WINDOW_HEIGHT-LanchesterPanel.currentGRounded/2-pointdiameter;
		int yH = Constants.GRAPH_WINDOW_HEIGHT-LanchesterPanel.currentHRounded/2-pointdiameter;

		if(graphPosition < graphGX.length) {
			graphGX[graphPosition] = x;
			graphGY[graphPosition] = yG;
			graphHX[graphPosition] = x;
			graphHY[graphPosition] = yH;
			graphPosition++;
		}
		for (int i = 0; i < graphPosition; i++) {
			g.setColor(Color.red);
			g.fillOval(graphGX[i],graphGY[i],pointdiameter,pointdiameter);
			g.setColor(Color.blue);
			g.fillOval(graphHX[i],graphHY[i],pointdiameter,pointdiameter);
		}
	}
}
