package view;

import static view.Constants.BUTTON_PANEL_HEIGHT;
import static view.Constants.GAS_LEGEND_BOUNDARY_THICKNESS;
import static view.Constants.GAS_LEGEND_SWATCH_HEIGHT;
import static view.Constants.GAS_LEGEND_SWATCH_WIDTH;
import static view.Constants.SIMULATION_FRAME_HEIGHT;
import static view.Constants.SIMULATION_FRAME_WIDTH;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeListener;

import model.Gas;
import model.NeuralNetwork;
import model.Neuron;
import model.Synapse;

import org.apache.logging.log4j.LogManager;

/**
 * RecursiveNeuralNetworkFrame class is responsible for setting up the frame for
 * simulation of a recursive neural network.
 *
 */
@SuppressWarnings("serial")
public class RecursiveNeuralNetworkFrame extends JFrame {

	/** Parent panel to add children panels. */
	private JPanel outerPanel;

	/** Panel to display the simulation. */
	private JPanel neuralNetworkPanel;

	/** Label to display the status of the simulation. */
	private JLabel statusLabel = new JLabel();

	/** Label for the period slider. */
	private JLabel sliderLabel = new JLabel("Timer Slider(in ms): ");

	/** Button to play or pause the simulation. */
	private JButton playPauseButton = new JButton("Pause");

	/** Button to step back in the simulation. */
	private JButton backstepButton = new JButton("Back Step");

	/** Button to step forward in the simulation. */
	private JButton forwardstepButton = new JButton("Forward Step");

	/** Button to replay the simulation. */
	private JButton replayButton = new JButton("Replay");

	/** Slider to change the timer tick period in the simulation. */
	private JSlider periodSlider = new JSlider(Constants.MIN_TIMER_DELAY,
			Constants.MAX_TIMER_DELAY);

	/** Reference variable for NeuralNetwork object. */
	private NeuralNetwork neuralNetwork;

	/** Reference variable for EdgeFactory object. */
	private EdgeFactory edgeFactory;

	/** Reference variable for GasRingFactory object. */
	private GasRingFactory gasRingFactory;

	/** Reference variable for NodeFactory object. */
	private NodeFactory nodeFactory;

	/** Variable for setting the mode of the simulation. */
	private String currentMode;

	/**
	 * Variable for setting the activated neurons in a time instant in the
	 * simulation
	 */
	private List<String> currentActivatedNeurons;

	/** Logging Configuration file */
	private static final String LOG4J_FILE = "log4j2.xml";

	/** Setting Logger object of Log4j */
	private static final org.apache.logging.log4j.Logger LOG = LogManager
			.getLogger(RecursiveNeuralNetworkFrame.class);

	/**
	 * Constructor: Gets called whenever an object of the class gets
	 * instantiated. Sets the properties of frames and all the private fields of
	 * the class
	 * 
	 * @param neuralNetwork
	 *            the neuralNetwork object which is to be simulated
	 */
	public RecursiveNeuralNetworkFrame(NeuralNetwork neuralNetwork) {

		setSize(new Dimension(SIMULATION_FRAME_WIDTH, SIMULATION_FRAME_HEIGHT));
		setLocationRelativeTo(null);
		outerPanel = new JPanel();
		outerPanel.setLayout(new BorderLayout());
		this.neuralNetwork = neuralNetwork;

		// setting currentMode to play by default
		this.currentMode = "play";
		createNeuralNetworkPanel();
		outerPanel.add(neuralNetworkPanel);
		createButtonsAndStatusPanels();
		org.apache.logging.log4j.core.config.Configurator.initialize("test",
				LOG4J_FILE);

		add(outerPanel);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		edgeFactory = new EdgeFactory(neuralNetwork, neuralNetworkPanel);
		gasRingFactory = new GasRingFactory(neuralNetwork, neuralNetworkPanel);
		nodeFactory = new NodeFactory(neuralNetwork, neuralNetworkPanel);
	}

	/**
	 * Method to update network simulation based on inputs from engine class at
	 * each tick of the timer. Updates simulation based on each timer tick.
	 * 
	 * @param currentMode
	 *            a string which can be one of play, replay, backstep or
	 *            forwardstep based on the user interaction with the buttons.
	 * @param neuralNetwork
	 *            the object of neuralNetwork class which contains all the
	 *            activated neurons, synapses and gas dispersion information.
	 */
	public void updateNeuralNetworkPanel(String currentMode,
			NeuralNetwork neuralNetwork) {
		this.currentMode = currentMode;
		this.neuralNetwork = neuralNetwork;
		setCurrentStatus();
		repaint();
	}

	/**
	 * Setter method for text for status label panel.
	 * 
	 * <p>
	 * Sets the current status of the simulation to mode:
	 * <ul>
	 * <li>play - Normal simulation based on timer tick.
	 * <li>replay - Replay the simulation after the simulation completes. Also
	 * based on timer tick.
	 * <li>backstep - Stepping back in the simulation on button click. Pressing
	 * play thereafter resumes the simulation from that point.
	 * <li>forwardstep - Stepping forward in the simulation in button click.
	 * Pressing play thereafter resumes the simulation from that point.
	 * </ul>
	 */
	private void setCurrentStatus() {
		if (currentMode.equalsIgnoreCase("play"))
			statusLabel.setText("You are in Play Mode ...");
		else if (currentMode.equalsIgnoreCase("replay"))
			statusLabel.setText("You are in Replay Mode ..");
		else if (currentMode.equalsIgnoreCase("backstep"))
			statusLabel.setText("You are stepping back in the simulation ..");
		else if (currentMode.equalsIgnoreCase("forwardstep"))
			statusLabel
					.setText("You are stepping forward in the simulation ..");
	}

	/**
	 * Method to assign properties to the timer slider.
	 */
	private void createTimerSlider() {

		periodSlider.setMajorTickSpacing(1000);
		periodSlider.setMinorTickSpacing(500);
		periodSlider.setPaintTicks(true);
		periodSlider.setPaintLabels(true);
		periodSlider.setSnapToTicks(true);

	}

	/**
	 * Method to create control panels and add components inside the panels.
	 * 
	 * <p>
	 * It creates the following panels:
	 * <ul>
	 * <li>controlPanel - Panel which encloses all the following panels.
	 * <li>timerSliderPanel - Panel which contains the timer slider.
	 * <li>statusPanel - Panel which contains the current simulation status
	 * label.
	 * <li>controlButtonsPanel - Panel which contains all the simulation control
	 * buttons.
	 * </ul>
	 * 
	 */
	private void createButtonsAndStatusPanels() {
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

		JPanel timerSliderPanel = new JPanel();
		timerSliderPanel.setBorder(new LineBorder(Color.BLACK));
		createTimerSlider();
		timerSliderPanel.add(sliderLabel);
		timerSliderPanel.add(periodSlider);
		controlPanel.add(timerSliderPanel);

		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new LineBorder(Color.BLACK));
		setCurrentStatus();
		statusPanel.add(statusLabel);
		controlPanel.add(statusPanel);

		JPanel controlButtonsPanel = new JPanel();
		controlButtonsPanel.setBorder(new LineBorder(Color.BLACK));
		controlButtonsPanel.add(backstepButton);
		controlButtonsPanel.add(replayButton);
		controlButtonsPanel.add(playPauseButton);
		controlButtonsPanel.add(forwardstepButton);
		controlPanel.add(controlButtonsPanel);

		outerPanel.add(controlPanel, BorderLayout.SOUTH);
	}

	/**
	 * Method to add replay button action listener to frame.
	 * 
	 * @param replayActionListener
	 *            object of the button listener
	 */
	public void addReplayListener(ActionListener replayActionListener) {
		replayButton.addActionListener(replayActionListener);
	}

	/**
	 * Method to add play/pause button action listener to frame.
	 * 
	 * @param playPauseActionListener
	 *            object of the button listener
	 */
	public void addPlayPauseListener(ActionListener playPauseActionListener) {
		playPauseButton.addActionListener(playPauseActionListener);
	}

	/**
	 * Method to add back step button action listener to frame.
	 * 
	 * @param backstepActionListener
	 *            object of the button listener
	 */
	public void addBackstepListener(ActionListener backstepActionListener) {
		backstepButton.addActionListener(backstepActionListener);
	}

	/**
	 * Method to add forward step button action listener to frame.
	 * 
	 * @param forwardstepActionListener
	 *            object of the button listener
	 */
	public void addForwardstepListener(ActionListener forwardstepActionListener) {
		forwardstepButton.addActionListener(forwardstepActionListener);
	}

	/**
	 * Method to add timer slider change listener to frame.
	 * 
	 * @param periodSliderChangeListener
	 *            object of the slider change listener
	 */
	public void addPeriodSliderChanger(ChangeListener periodSliderChangeListener) {
		periodSlider.addChangeListener(periodSliderChangeListener);
	}

	/**
	 * Method to create panel for network simulation. This panel has a paint
	 * component which gets called on each timer tick and paints the simulation
	 * on the frame. It also draws the gas legend in the bottom right corner of
	 * the frame.
	 */
	public void createNeuralNetworkPanel() {
		this.neuralNetworkPanel = new JPanel() {

			@SuppressWarnings("rawtypes")
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;

				if (neuralNetwork.getGasMap().size() > 0) {
					drawGasLegend(g2d);
				}

				g2d.setColor(Color.BLACK);

				Set synapseMapSet = neuralNetwork.getSynapseMap().entrySet();
				Iterator synapseIterator = synapseMapSet.iterator();

				while (synapseIterator.hasNext()) {
					Map.Entry currentEntry = (Map.Entry) synapseIterator.next();
					Synapse currentSynapse = (Synapse) currentEntry.getValue();

					edgeFactory.drawEdge(currentSynapse, Color.BLACK, g2d);
				}

				Set neuronMapSet = neuralNetwork.getNeuronMap().entrySet();
				Iterator neuronIterator = neuronMapSet.iterator();

				LOG.debug("Before while\t" + currentActivatedNeurons);
				while (neuronIterator.hasNext()) {
					Map.Entry currentEntry = (Map.Entry) neuronIterator.next();
					Neuron currentNeuron = (Neuron) currentEntry.getValue();
					if (currentNeuron.isGasEmitter()) {
						gasRingFactory.drawGasRings(currentNeuron, g2d);

					}
				}

				neuronIterator = neuronMapSet.iterator();
				while (neuronIterator.hasNext()) {
					Map.Entry currentEntry = (Map.Entry) neuronIterator.next();
					Neuron currentNeuron = (Neuron) currentEntry.getValue();
					nodeFactory.drawNode(currentNeuron, g2d);
				}
			}
		};
	}

	/**
	 * Method to draw a gas legend on the bottom right corner above the control
	 * panel. Different gases are represented by different colors which are
	 * taken in as input with the gas parameters.
	 * 
	 * @param g2d
	 *            Graphics2D parameter to draw using basic graphics functions
	 *            like setStroke and setColor
	 */
	public void drawGasLegend(Graphics2D g2d) {
		int gasListSize = neuralNetwork.getGasMap().size();
		g2d.setStroke(new BasicStroke(GAS_LEGEND_BOUNDARY_THICKNESS));
		g2d.setColor(Color.BLACK);
		int legendWidth = 40 + 150 + GAS_LEGEND_SWATCH_WIDTH;
		int legendHeight = 40 + (GAS_LEGEND_SWATCH_HEIGHT + 10) * gasListSize;
		int legendX = SIMULATION_FRAME_WIDTH - (legendWidth + 20);
		int legendY = SIMULATION_FRAME_HEIGHT
				- (legendHeight + 30 + BUTTON_PANEL_HEIGHT);

		g2d.drawRoundRect(legendX, legendY, legendWidth, legendHeight, 10, 10);
		int index = 0;
		int x;
		int y;

		for (Gas gas : neuralNetwork.getGasMap().values()) {
			// draw color patch
			x = legendX + 10;
			y = legendY + 10 + 30 * index;
			g2d.setColor(gas.getColor());
			g2d.fillRoundRect(x, y, GAS_LEGEND_SWATCH_WIDTH,
					GAS_LEGEND_SWATCH_HEIGHT, 3, 3);
			// draw gas name label
			x += 10 + GAS_LEGEND_SWATCH_WIDTH;
			g2d.setColor(Color.BLACK);
			g2d.drawString(gas.getName(), x + 16, y + 16); // 16 approximates
															// the text height
			index++;
		}

	}

	/**
	 * Getter method for playPauseButton object
	 * 
	 * @return returns the playPauseButton object.
	 */
	public JButton getPlayPauseButton() {
		return playPauseButton;
	}

	/**
	 * Getter method for replayButton object
	 * 
	 * @return returns the replayButton object
	 */
	public JButton getReplayButton() {
		return replayButton;
	}

	/**
	 * Getter method for forwardstepButton object
	 * 
	 * @return returns the forwardstepButton object
	 */
	public JButton getForwardstepButton() {
		return forwardstepButton;
	}

	/**
	 * Getter method for backstepButton object
	 * 
	 * @return returns the backstepButton object
	 */
	public JButton getBackstepButton() {
		return backstepButton;
	}

	/**
	 * Getter method for periodSlider object
	 * 
	 * @return returns the periodSlider object
	 */
	public JSlider getPeriodSlider() {
		return periodSlider;
	}

	/**
	 * Setter method for periodSlider object
	 * 
	 * @param periodSlider
	 *            the periodSlider object to be set in the frame.
	 */
	public void setPeriodSlider(JSlider periodSlider) {
		this.periodSlider = periodSlider;
	}

}
