package view;

import static view.Constants.SIMULATION_FRAME_HEIGHT;
import static view.Constants.SIMULATION_FRAME_WIDTH;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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

import model.NeuralNetwork;
import model.Neuron;
import model.Synapse;

import org.apache.logging.log4j.LogManager;

/**
 * FeedForwardNetworkFrame class is responsible for setting up the frame for
 * simulation of a feed forward neural network.
 * 
 */
@SuppressWarnings("serial")
public class FeedForwardNetworkFrame extends JFrame {

	/** Parent panel to add children panels. */
	private JPanel outerPanel;

	/** Panel to display the simulation. */
	private JPanel feedForwardNetworkPanel;

	/** Button to play or pause the simulation. */
	private JButton playPauseButton = new JButton("Pause");

	/** Button to replay the simulation. */
	private JButton replayButton = new JButton("Replay");

	/** Button to step back in the simulation. */
	private JButton backstepButton = new JButton("Back Step");

	/** Button to step forward in the simulation. */
	private JButton forwardstepButton = new JButton("Forward Step");

	/** Slider to change the timer tick period in the simulation. */
	private JSlider periodSlider = new JSlider(Constants.MIN_TIMER_DELAY,
			Constants.MAX_TIMER_DELAY);

	/** Label to display the status of the simulation. */
	private JLabel statusLabel = new JLabel();

	/** Label for the period slider. */
	private JLabel sliderLabel = new JLabel("Timer Slider(in ms): ");

	/** Reference variable for NeuralNetwork object. */
	private NeuralNetwork neuralNetwork;

	/** Reference variable for EdgeFactory object. */
	private EdgeFactory edgeFactory;

	/** Variable for setting the mode of the simulation. */
	private String currentMode;

	/** Variable for setting the activated path in the simulation. */
	private List<String> activatedPath;

	/** Logging Configuration file */
	private static final String LOG4J_FILE = "log4j2.xml";
	
	/** Setting Logger object of Log4j */
	private static final org.apache.logging.log4j.Logger LOG = LogManager
			.getLogger(FeedForwardNetworkFrame.class);

	/**
	 * Constructor: Gets called whenever an object of the class gets
	 * instantiated. Sets the properties of frames and all the private fields of
	 * the class
	 * 
	 * @param neuralNetwork
	 *            the neuralNetwork object which is to be simulated
	 */
	public FeedForwardNetworkFrame(NeuralNetwork neuralNetwork) {
		this.setVisible(true);
		this.setSize(new Dimension(SIMULATION_FRAME_WIDTH,
				SIMULATION_FRAME_HEIGHT));
		this.setLocationRelativeTo(null);

		this.outerPanel = new JPanel();
		outerPanel.setLayout(new BorderLayout());

		this.currentMode = "play";
		this.neuralNetwork = neuralNetwork;
		this.activatedPath = new ArrayList<String>();

		createNeuralNetworkPanel();
		outerPanel.add(feedForwardNetworkPanel);
		createButtonsAndStatusPanels();

		this.add(outerPanel);
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		org.apache.logging.log4j.core.config.Configurator.initialize("test",
				LOG4J_FILE);
		this.edgeFactory = new EdgeFactory(neuralNetwork,
				feedForwardNetworkPanel);
	}

	/**
	 * Method to update network simulation based on inputs from engine class at
	 * each tick of the timer. Updates simulation based on each timer tick.
	 * 
	 * @param currentMode
	 *            a string which can be one of play, replay, backstep or
	 *            forwardstep based on the user interaction with the buttons.
	 * @param activatedPath
	 *            a list of current neurons which is to be painted on the
	 *            simulation panel.
	 */
	public void updateNeuralNetworkPanel(String currentMode,
			ArrayList<String> activatedPath) {
		this.currentMode = currentMode;
		this.activatedPath = activatedPath;
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
			statusLabel.setText("You are in Play Mode");
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
	 * Method to add play/pause button action listener to frame.
	 * 
	 * @param playPauseActionListener
	 *            object of the button listener
	 */
	public void addPlayPauseListener(ActionListener playPauseActionListener) {
		playPauseButton.addActionListener(playPauseActionListener);
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
	 * on the frame.
	 */
	public void createNeuralNetworkPanel() {
		this.feedForwardNetworkPanel = new JPanel() {

			@SuppressWarnings("rawtypes")
			@Override
			public void paint(Graphics g) {

				Graphics2D g2d = (Graphics2D) g;
				g2d.setColor(Color.BLACK);

				int x1;// = sourceNeuron.getX();
				int y1;// = sourceNeuron.getY();

				int x2;// = targetNeuron.getX();
				int y2;// = targetNeuron.getY();

				Set synapseMapSet = neuralNetwork.getSynapseMap().entrySet();

				Iterator synapseIterator = synapseMapSet.iterator();

				while (synapseIterator.hasNext()) {
					Map.Entry currentEntry = (Map.Entry) synapseIterator.next();
					Synapse currentSynapse = (Synapse) currentEntry.getValue();

					Neuron sourceNeuron = neuralNetwork.getNeuronMap().get(
							currentSynapse.getSourceNeuron());
					Neuron targetNeuron = neuralNetwork.getNeuronMap().get(
							currentSynapse.getTargetNeuron());

					x1 = sourceNeuron.getX();
					y1 = sourceNeuron.getY();
					x2 = targetNeuron.getX();
					y2 = targetNeuron.getY();

					if (sourceNeuron.getLayerType().equals("I")) {
						g2d.setStroke(new java.awt.BasicStroke(1));
						g2d.drawLine(x1 - 50, y1, x1, y1);

					}
					if (targetNeuron.getLayerType().equals("O")) {
						g2d.setStroke(new java.awt.BasicStroke(1));
						g2d.drawLine(x2, y2, x2 + 50, y2);

					}

					edgeFactory.drawEdge(currentSynapse, Color.BLACK, g2d);
				}

				g2d.setColor(Color.RED);
				Set neuronMapSet = neuralNetwork.getNeuronMap().entrySet();

				Iterator neuronIterator = neuronMapSet.iterator();

				LOG.debug("Before while\t" + activatedPath);
				while (neuronIterator.hasNext()) {
					Map.Entry currentEntry = (Map.Entry) neuronIterator.next();
					Neuron currentNeuron = (Neuron) currentEntry.getValue();

					x1 = currentNeuron.getX();
					y1 = currentNeuron.getY();
					int temprad = 15;

					if (activatedPath.contains(currentNeuron.getNeuronID())) {

						LOG.debug("inside if\t" + currentNeuron.getNeuronID());

						if (currentNeuron.getLayerType().contains("O")) {
							g2d.setColor(Color.GREEN);
							g2d.fillOval(x1 - temprad, y1 - temprad,
									2 * temprad, 2 * temprad);
							g2d.setColor(Color.RED);
						} else {
							g2d.setColor(Color.YELLOW);
							g2d.fillOval(x1 - temprad, y1 - temprad,
									2 * temprad, 2 * temprad);
							if (neuralNetwork.isLabeled()) {
								g2d.setColor(Color.BLACK);
								g2d.drawString(currentNeuron.getNeuronID(),
										x1 - 8, y1 + 4);
							}
							g2d.setColor(Color.RED);
						}
					} else

						g2d.fillOval(x1 - temprad, y1 - temprad, 2 * temprad,
								2 * temprad);
					if (neuralNetwork.isLabeled()) {
						g2d.setColor(Color.BLACK);
						g2d.drawString(currentNeuron.getNeuronID(), x1 - 8,
								y1 + 4);
					}
					g2d.setColor(Color.RED);
				}
			}
		};
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
