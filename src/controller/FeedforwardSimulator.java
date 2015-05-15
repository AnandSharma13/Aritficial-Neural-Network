package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.NetworkState;
import model.NeuralNetwork;
import model.Neuron;
import model.Synapse;

import org.apache.logging.log4j.LogManager;

import controller.RecurrentSimulator.RecurrentPeriodSliderChanger;
import view.Constants;
import view.Constants.FeedForwardNetworkMode;
import view.FeedForwardNetworkFrame;

public class FeedforwardSimulator {

	private NeuralNetwork neuralNetwork;
	private List<String> activatedNeurons;
	private List<String> currentActivatedNeurons;
	private String outputNeuron;
	private ArrayList<Integer> outputList;
	private ArrayList<NetworkState> listForNetworkStatus;
	private FeedForwardNetworkFrame neuralNetworkFrame;
	private FeedForwardNetworkMode mode;
	private int tickCount = 1;
	private int inputCount = 0;
	private int backstepIndex = -1;
	private boolean inputsGotOver = false;
	private boolean isReplay = false;
	private int replayIndex = 0;
	private boolean playAfterBacking = false;

	// represents the input signals (sheet 3) in a map<Time, List<Signals>>
	private Map<Integer, List<Integer>> inputTimeSignalMap;

	private Timer timer;
	private Set<Entry<Integer, List<Integer>>> inputSignalMapSet;
	private Iterator<Entry<Integer, List<Integer>>> signalIterator;

	private static final String LOG4J_FILE = "log4j2.xml";
	private static final org.apache.logging.log4j.Logger LOG = LogManager
			.getLogger(FeedforwardSimulator.class);

	public ArrayList<Integer> getOutputList() {
		return outputList;
	}

	public void setOutputList(ArrayList<Integer> outputList) {
		this.outputList = outputList;
	}

	public FeedforwardSimulator(NeuralNetwork neuralNetwork,
			Map<Integer, List<Integer>> inputTimeSignalMap,
			FeedForwardNetworkMode mode) {

		activatedNeurons = new ArrayList<String>();
		currentActivatedNeurons = new ArrayList<String>();
		outputList = new ArrayList<Integer>();
		listForNetworkStatus = new ArrayList<NetworkState>();
		this.inputTimeSignalMap = inputTimeSignalMap;
		this.neuralNetwork = neuralNetwork;
		this.mode = mode;
		org.apache.logging.log4j.core.config.Configurator.initialize("test",
				LOG4J_FILE);
	}

	public void simulateWithoutTimer() throws InterruptedException {
		getInputOutputNeurons();

		for (Entry<Integer, List<Integer>> entry : inputTimeSignalMap
				.entrySet())
			selectActivatedNeuronsOnInputSignal(entry);

	}

	public void simulate() {

		if (mode == FeedForwardNetworkMode.NOT_TIMED) {
			try {
				simulateWithoutTimer();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		getInputOutputNeurons();

		// Get a set of the entries
		inputSignalMapSet = inputTimeSignalMap.entrySet();

		// Get an iterator
		signalIterator = inputSignalMapSet.iterator();
		timer = new Timer(Constants.AVG_TIMER_DELAY, action);
		initPanel();
		timer.setDelay(Constants.AVG_TIMER_DELAY);
		timer.start();

	}

	// find out input neurons and process input signal to decide which neuron
	// fire at start
	public void getInputOutputNeurons() {
		// Get a set of the entries
		Set neuronMapSet = neuralNetwork.getNeuronMap().entrySet();

		// Get an iterator
		Iterator neuronIterator = neuronMapSet.iterator();

		// Display elements
		while (neuronIterator.hasNext()) {
			Map.Entry currentEntry = (Map.Entry) neuronIterator.next();
			Neuron currentNeuron = (Neuron) currentEntry.getValue();

			if (currentNeuron.getLayerType().contains("I")) {
				activatedNeurons.add(currentEntry.getKey().toString());
			}

			else if (currentNeuron.getLayerType().contains("O")) {
				outputNeuron = currentEntry.getKey().toString();
			}
		}
	}

	ActionListener action = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent event) {

			if (isReplay == true) {
				replayStep();
			} else {
				if (inputsGotOver && !playAfterBacking) {
					timer.stop();
					neuralNetworkFrame.getReplayButton().setEnabled(true);
					neuralNetworkFrame.getPlayPauseButton().setEnabled(false);
					neuralNetworkFrame.getBackstepButton().setEnabled(true);
					neuralNetworkFrame.getForwardstepButton().setEnabled(false);
				} else {
					playStep();
				}
			}
		}
	};

	public void playStep() {

		if (playAfterBacking) {
			ArrayList<String> currentPath = new ArrayList<String>();
			NetworkState currentState = new NetworkState();

			currentState = listForNetworkStatus.get(backstepIndex);
			currentPath = currentState.getActivatedPath();
			backstepIndex++;

			neuralNetworkFrame.updateNeuralNetworkPanel("play", currentPath);
		}

		else {
			Map.Entry currentEntry = (Map.Entry) signalIterator.next();
			// Code for each timer tick goes here!!

			LOG.debug(tickCount + " inside run!!");
			try {
				selectActivatedNeuronsOnInputSignal(currentEntry);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			inputCount++;

			if (inputCount == inputTimeSignalMap.size()) {
				inputsGotOver = true;
			}
		}
		LOG.debug("printing from nnf" + currentActivatedNeurons);

		if (backstepIndex == listForNetworkStatus.size()) {
			backstepIndex--;
			playAfterBacking = false;
		}
	}

	public void replayStep() {
		if (replayIndex < listForNetworkStatus.size()) {
			ArrayList<String> currentPath = new ArrayList<String>();
			NetworkState currentState = new NetworkState();

			currentState = listForNetworkStatus.get(replayIndex);
			currentPath = currentState.getActivatedPath();
			replayIndex++;

			neuralNetworkFrame.updateNeuralNetworkPanel("replay", currentPath);
		} else {
			timer.stop();
			replayIndex = 0;
			isReplay = false;
			neuralNetworkFrame.getReplayButton().setEnabled(true);
			neuralNetworkFrame.getPlayPauseButton().setEnabled(false);
		}
	}

	public void selectActivatedNeuronsOnInputSignal(Entry currentEntry2)
			throws InterruptedException {

		List<Integer> currentSignal = (List<Integer>) currentEntry2.getValue();

		currentActivatedNeurons.clear();

		for (int index = 0; index < currentSignal.size(); index++) {
			if (currentSignal.get(index) == 1) {
				String currentNeuron = activatedNeurons.get(index);
				currentActivatedNeurons.add(currentNeuron);
			}
		}

		if (currentActivatedNeurons.size() == 0) {
			NetworkState currentState = new NetworkState();
			ArrayList<String> activatedPath = new ArrayList<String>();
			currentState.setActivatedPath(activatedPath);
			listForNetworkStatus.add(currentState);
			getOutputList().add(0);
		}

		else {
			processInputSignal();
		}
	}

	public void processInputSignal() throws InterruptedException {

		boolean notReachedOutputNeuron = true;
		NetworkState currentState = new NetworkState();
		ArrayList<String> currentPath = new ArrayList<String>();

		while (currentActivatedNeurons.size() > 0) {

			for (String neuronID : currentActivatedNeurons) {
				Neuron tempNeuron = neuralNetwork.getNeuronMap().get(neuronID);

				ArrayList<String> synapseList = tempNeuron.getSynapsesList();

				for (String synapseID : synapseList) {
					Synapse tempSynapse = neuralNetwork.getSynapseMap().get(
							synapseID);

					String targetNeuronID = tempSynapse.getTargetNeuron();

					Neuron targetNeuron = neuralNetwork.getNeuronMap().get(
							targetNeuronID);

					targetNeuron.setConcentration(targetNeuron
							.getConcentration()
							+ tempSynapse.getSynapticWeight());

					if (targetNeuron.getLayerType().contains("O")) {
						notReachedOutputNeuron = false;
					}
				}
			}

			for (String neuronName : currentActivatedNeurons) {
				currentPath.add(neuronName);
			}

			currentActivatedNeurons.clear();

			if (notReachedOutputNeuron == false)
				break;

			// Get a set of the entries
			Set neuronMapSet = neuralNetwork.getNeuronMap().entrySet();

			// Get an iterator
			Iterator neuronIterator = neuronMapSet.iterator();

			// Display elements
			while (neuronIterator.hasNext()) {
				Map.Entry currentEntry = (Map.Entry) neuronIterator.next();
				Neuron currentNeuron = (Neuron) currentEntry.getValue();

				if (currentNeuron.getConcentration() >= currentNeuron
						.getThreshold()) {
					currentActivatedNeurons.add(currentNeuron.getNeuronID());
				}

				currentNeuron.setConcentration(0);
			}
		}

		if (neuralNetwork.getNeuronMap().get(outputNeuron).getConcentration() >= neuralNetwork
				.getNeuronMap().get(outputNeuron).getThreshold()) {
			getOutputList().add(1);
			currentPath.add(outputNeuron);
		} else {
			getOutputList().add(0);
		}

		neuralNetwork.getNeuronMap().get(outputNeuron).setConcentration(0);
		currentState.setActivatedPath(currentPath);

		listForNetworkStatus.add(currentState);

		if (mode == FeedForwardNetworkMode.TIMED) {
			neuralNetworkFrame.updateNeuralNetworkPanel("play", currentPath);
			backstepIndex = listForNetworkStatus.size() - 1;
		}

	}

	public void initPanel() {
		neuralNetworkFrame = new FeedForwardNetworkFrame(neuralNetwork);
		neuralNetworkFrame.addReplayListener(new FeedFwdReplayListener());
		neuralNetworkFrame.addPlayPauseListener(new FeedFwdPlayPauseListener());
		neuralNetworkFrame.addBackstepListener(new FeedFwdBackstepListener());
		neuralNetworkFrame
				.addForwardstepListener(new FeedFwdForwardstepListener());
		neuralNetworkFrame
				.addPeriodSliderChanger(new FeedFwdPeriodSliderChanger());
		neuralNetworkFrame.getReplayButton().setEnabled(false);
	}

	public Iterator<Entry<Integer, List<Integer>>> getSignalIterator() {
		return signalIterator;
	}

	public String getOutputNeuron() {
		return outputNeuron;
	}

	class FeedFwdPlayPauseListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (timer.isRunning()) {
				timer.stop();
				neuralNetworkFrame.getPlayPauseButton().setText("Play");
				neuralNetworkFrame.getReplayButton().setEnabled(true);
			}

			else {
				neuralNetworkFrame.getPlayPauseButton().setText("Pause");
				if (backstepIndex < listForNetworkStatus.size() - 1) {
					playAfterBacking = true;
				}
				timer.start();
			}
		}
	}

	class FeedFwdReplayListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// stop timer if replay is pressed in between simulation
			neuralNetworkFrame.getReplayButton().setEnabled(false);
			neuralNetworkFrame.getPlayPauseButton().setEnabled(true);
			if (timer.isRunning())
				timer.stop();
			else
				timer.start();

			isReplay = true;
			replayIndex = 0;
		}
	}

	class FeedFwdBackstepListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (timer.isRunning()) {
				timer.stop();
			}
			neuralNetworkFrame.getPlayPauseButton().setText("Play");
			neuralNetworkFrame.getPlayPauseButton().setEnabled(true);
			neuralNetworkFrame.getReplayButton().setEnabled(false);

			if (!neuralNetworkFrame.getForwardstepButton().isEnabled())
				neuralNetworkFrame.getForwardstepButton().setEnabled(true);

			NetworkState currentState = listForNetworkStatus
					.get(--backstepIndex);
			ArrayList<String> currentPath = currentState.getActivatedPath();
			neuralNetworkFrame
					.updateNeuralNetworkPanel("backstep", currentPath);

			if (backstepIndex == 0)
				neuralNetworkFrame.getBackstepButton().setEnabled(false);
		}
	}

	class FeedFwdForwardstepListener implements ActionListener {

		private boolean finishedSteps = false;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (timer.isRunning()) {
				timer.stop();
			}
			neuralNetworkFrame.getPlayPauseButton().setText("Play");
			neuralNetworkFrame.getPlayPauseButton().setEnabled(true);
			neuralNetworkFrame.getReplayButton().setEnabled(false);

			if (!neuralNetworkFrame.getBackstepButton().isEnabled())
				neuralNetworkFrame.getBackstepButton().setEnabled(true);

			if (backstepIndex < listForNetworkStatus.size() - 1) {
				backstepIndex++;
				NetworkState currentState = listForNetworkStatus
						.get(backstepIndex);
				ArrayList<String> currentPath = currentState.getActivatedPath();
				neuralNetworkFrame.updateNeuralNetworkPanel("forwardstep",
						currentPath);
			} else {
				playStep();
			}

			if (backstepIndex == inputTimeSignalMap.size() - 1)
				finishedSteps = true;
			else
				finishedSteps = false;

			if (finishedSteps) {
				neuralNetworkFrame.getReplayButton().setEnabled(true);
				neuralNetworkFrame.getPlayPauseButton().setEnabled(false);
				neuralNetworkFrame.getForwardstepButton().setEnabled(false);
			}

		}
	}

	class FeedFwdPeriodSliderChanger implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {

			neuralNetworkFrame.setPeriodSlider((JSlider) e.getSource());

			if (!neuralNetworkFrame.getPeriodSlider().getValueIsAdjusting()) {
				int delay = (int) neuralNetworkFrame.getPeriodSlider()
						.getValue();
				if (delay == 0)
					timer.stop();
				else {
					if (!timer.isRunning())
						timer.start();
					timer.setDelay(delay);
				}
			}
		}
	}
}
