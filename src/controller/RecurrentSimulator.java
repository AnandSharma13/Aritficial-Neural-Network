package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Gas;
import model.GasDispersionUnit;
import model.NetworkState;
import model.NeuralNetwork;
import model.Neuron;
import model.Synapse;

import org.apache.logging.log4j.LogManager;

import view.RecursiveNeuralNetworkFrame;
import view.Constants;

/**
 * This class performs a timer based simulation of a recurrent neural network.
 */
public class RecurrentSimulator {

	/**  Timer class instance */
	private Timer timer;

	/**  list of ids of activated neurons at each time instant that gets supplied to the view */
	private List<String> currentActivatedNeurons;

	/**  neural network instance being simulated */
	private NeuralNetwork neuralNetwork;

	/**  clone of neural network instance, used to store in networkStateList */
	private NeuralNetwork neuralNetworkClone;

	/**  list of outputs generated during network simulation */
	private ArrayList<Integer> outputList;

	/**  list of neurons in the input layer */
	private List<String> inputNeurons;

	/**  map representing input signals at each time instant */
	private Map<Integer, List<Integer>> inputTimeSignalMap;

	/**  represents a time instant */
	private int tickCount = 0;

	/** list of input signals at a time instant */
	private ArrayList<Integer> inputList;
	
	/** boolean to check if we are done simulating all input signals */
	private boolean doneSimulatingInputs = false;
	
	/** keeps track of the number of input signals simulated */
	private int inputCount = 0;
	
	/** just a number that determines when to stop the simulator. 
	 * So the simulator runs 20 times after reading the last input signal and then stops the 
	 * simulation process */
	private int stabilizingCounter = 20;
	
	/** view that shows current simulation */
	private RecursiveNeuralNetworkFrame neuralNetworkFrame;
	
	/** list of network state at each time instant */
	private ArrayList<NetworkState> networkStateList;
	
	/** indicates if we are in the replay mode or play mode */
	private boolean replaying = false;
	
	/** indicates if we are playing normally or playing after back stepping */
	private boolean playAfterBackStepping = false;
	
	/** represents the index of the network state list during replay */
	private int replayIndex = 0;

	/** represents the index of the network state list while back stepping */
	private int backstepIndex = -1;
	
	/** logger file name */
	private static final String LOG4J_FILE = "log4j2.xml";
	
	/** logger instance */
	private static final org.apache.logging.log4j.Logger LOG = LogManager
			.getLogger(RecurrentSimulator.class);

	/**
	 * Creates an instance of the recurrent simulator  
	 * 
	 * @param neuralNetwork neural network to simulate
	 * @param inputTimeSignalMap input signals supplied for simulation
	 */
	public RecurrentSimulator(NeuralNetwork neuralNetwork,
			Map<Integer, List<Integer>> inputTimeSignalMap) {
		inputNeurons = new ArrayList<String>();
		currentActivatedNeurons = new ArrayList<String>();
		this.inputTimeSignalMap = inputTimeSignalMap;
		this.neuralNetwork = neuralNetwork;
		outputList = new ArrayList<Integer>();
		inputList = new ArrayList<Integer>();
		networkStateList = new ArrayList<NetworkState>();

		org.apache.logging.log4j.core.config.Configurator.initialize("test",
				LOG4J_FILE);
	}

	/**
	 * simulates the neural network
	 */
	public void simulate() {

		getInputNeurons();
		createGasDispersionUnit();
		timer = new Timer(Constants.AVG_TIMER_DELAY, timerListener);
		initPanel();
		timer.setDelay(Constants.AVG_TIMER_DELAY);
		timer.start();
	}

	/**
	 * creates Gas Dispersion Unit for every gas emiting neuron
	 */
	private void createGasDispersionUnit() {

		Gas emittedGas;

		for (Map.Entry<String, Neuron> entry : neuralNetwork.getNeuronMap()
				.entrySet()) {
			Neuron sourceNeuron = entry.getValue();

			// if neuron is gas emitter
			if (sourceNeuron.isGasEmitter()) {
				emittedGas = neuralNetwork.getGasMap().get(
						sourceNeuron.getGasType());
				GasDispersionUnit newGasChannel = new GasDispersionUnit(
						sourceNeuron.getEmmissionRadius(),
						sourceNeuron.getBaseProduction(), emittedGas);

				newGasChannel.createGasChannel();

				List<Neuron> targetNeuronsList = neuralNetwork
						.getGasReceiverNeuronsMap().get(
								sourceNeuron.getGasType());

				newGasChannel.addNeuron(targetNeuronsList, sourceNeuron);

				sourceNeuron.setGasDispersionUnit(newGasChannel);
			}
		}
	}

	/**
	 * fills the inputNeurons list with the list of neurons in the input layer
	 */
	public void getInputNeurons() {
		// Get a set of the entries
		Set neuronMapSet = neuralNetwork.getNeuronMap().entrySet();

		// Get an iterator
		Iterator neuronIterator = neuronMapSet.iterator();

		// Display elements
		while (neuronIterator.hasNext()) {
			Map.Entry currentEntry = (Map.Entry) neuronIterator.next();
			Neuron currentNeuron = (Neuron) currentEntry.getValue();

			if (currentNeuron.getLayerType().contains("I")) {
				inputNeurons.add(currentEntry.getKey().toString());
			}

		}
	}

	/**
	 *   Each tick of the timer executes the code within actionPerformed() of the timer, which
	 *   includes playing one step of network simulation per timer tick.
	 */  
	ActionListener timerListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent event) {
			if (replaying) {
				replayStep();
			} else {
				if (doneSimulatingInputs && !playAfterBackStepping)
					stabilizingCounter--;
				if (stabilizingCounter == 0) {
					timer.stop();
					neuralNetworkFrame.getReplayButton().setEnabled(true);
					neuralNetworkFrame.getPlayPauseButton().setEnabled(false);
					neuralNetworkFrame.getBackstepButton().setEnabled(true);
					neuralNetworkFrame.getForwardstepButton().setEnabled(false);
				}

				try {
					playStep();
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	/**
	 * plays the networkStateList all over again, to display replay of simulation
	 */
	public void replayStep() {

		if (replayIndex <= networkStateList.size()) {

			if (replayIndex == networkStateList.size()) {
				replayIndex = 0;
				timer.stop();
				neuralNetworkFrame.getReplayButton().setEnabled(true);
				neuralNetworkFrame.getPlayPauseButton().setEnabled(false);
			} else {
				neuralNetworkFrame.updateNeuralNetworkPanel("replay",
						networkStateList.get(replayIndex).getNeuralNetwork());
				replayIndex++;
			}
		} else {
			replaying = false;
			timer.stop();
			replayIndex = 0;
			neuralNetworkFrame.getReplayButton().setEnabled(true);
			neuralNetworkFrame.getPlayPauseButton().setEnabled(false);
		}
	}

	/**
	 * plays each step of network simulation 
	 * which includes simulating the network, modulating synaptic weights based on Hebbian Plasticity
	 * and then resetting concentrations of activated neurons
	 * 	 
	 * @throws CloneNotSupportedException
	 */
	public void playStep() throws CloneNotSupportedException {

		// if play button is hit after backstepping
		if (playAfterBackStepping) {
			backstepIndex++;
			neuralNetwork = networkStateList.get(backstepIndex)
					.getNeuralNetwork();
			neuralNetworkFrame.updateNeuralNetworkPanel("play", neuralNetwork);
		} 
		// regular play
		else {
			
			tickCount++;
			if (inputTimeSignalMap.containsKey(tickCount)) {
				inputList = (ArrayList<Integer>) inputTimeSignalMap
						.get(tickCount);
				LOG.debug(inputList);
				activateNeurons(inputList);

				inputCount++;
			}

			if (inputCount == inputTimeSignalMap.size()) {
				doneSimulatingInputs = true;
			}

			simulateNetwork();
			modulateSynapses();
			updateActivatedNeurons();

			neuralNetworkFrame.updateNeuralNetworkPanel("play", neuralNetwork);

			NetworkState networkState = new NetworkState();

			try {
				neuralNetworkClone = neuralNetwork.clone();

			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}

			// adding network state to networkStateList for replay, forward and backward stepping
			networkState.setNeuralNetwork(neuralNetworkClone);
			networkStateList.add(networkState);
			backstepIndex = networkStateList.size() - 1;
		}

		if (backstepIndex == networkStateList.size() - 1) {
			backstepIndex--;
			playAfterBackStepping = false;
		}

	}

	/**
	 * Activates neurons based on incoming input signals
	 * 
	 * @param inputSignals
	 */
	public void activateNeurons(List<Integer> inputSignals) {

		List<Integer> currentSignal = inputSignals;

		LOG.debug("\nCurrent Input Signal : " + inputSignals.toString());

		for (int index = 0; index < currentSignal.size(); index++) {
			String currentNeuron = inputNeurons.get(index);
			Neuron neuron = neuralNetwork.getNeuronMap().get(currentNeuron);
			Double buildUpConcentration = neuron.getReceptor()
					.getBuiltUpConcentrations().get("S")
					+ currentSignal.get(index);
			neuron.getReceptor().getBuiltUpConcentrations()
			.put("S", buildUpConcentration);
		}

	}

	/**
	 * Getter for list of output signals
	 * @return
	 */
	public ArrayList<Integer> getOutputList() {
		return outputList;
	}

	/**
	 * simulates the neural network
	 */
	public void simulateNetwork() {

		// loop through all neurons in network
		for (String neuronID : neuralNetwork.getNeuronMap().keySet()) {

			Neuron tempNeuron = neuralNetwork.getNeuronMap().get(neuronID);

			// TODO might need to switch the order of emitting/advancing and
			// updating Target Neurons
			if (tempNeuron.isGasEmitter()) {
				tempNeuron.getGasDispersionUnit().updateTargetNeurons(
						neuralNetwork.getNeuronMap());
				tempNeuron.getGasDispersionUnit().advance();
				if (tempNeuron.getConcentration() >= tempNeuron.getThreshold()) {
					tempNeuron.getGasDispersionUnit().increaseStrength();
					tempNeuron.getGasDispersionUnit().emitGas();
				} else {
					tempNeuron.getGasDispersionUnit().decreaseStrength();
				}
			}

			else {
				ArrayList<String> synapseList = tempNeuron.getSynapsesList();
				for (String synapseID : synapseList) {
					Synapse tempSynapse = neuralNetwork.getSynapseMap().get(
							synapseID);
					String targetNeuronID = tempSynapse.getTargetNeuron();
					Neuron targetNeuron = neuralNetwork.getNeuronMap().get(
							targetNeuronID);

					double buildUpConcentration = tempSynapse
							.getSynapticWeight()
							* tempNeuron.calculateActivation();
					buildUpConcentration += targetNeuron.getReceptor()
							.getBuiltUpConcentrations().get("S");
					targetNeuron.getReceptor().getBuiltUpConcentrations()
					.put("S", buildUpConcentration);
				}
			}
		}
		currentActivatedNeurons.clear();
	}

	/**
	 * Performs the Hebbian learning algorithm
	 */
	private void modulateSynapses() {
		Set synapseMapSet = neuralNetwork.getSynapseMap().entrySet();
		Iterator synapseIterator = synapseMapSet.iterator();

		while (synapseIterator.hasNext()) {
			Map.Entry currentEntry = (Map.Entry) synapseIterator.next();
			Synapse currentSynapse = (Synapse) currentEntry.getValue();

			currentSynapse.updatePlasticity(neuralNetwork);

			Neuron sourceNeuron = neuralNetwork.getNeuronMap().get(
					currentSynapse.getSourceNeuron());
			Neuron targetNeuron = neuralNetwork.getNeuronMap().get(
					currentSynapse.getTargetNeuron());
			double a = currentSynapse.getPriorActivation();
			double b = targetNeuron.getConcentration();
			if (a >= sourceNeuron.getThreshold()
					&& b >= targetNeuron.getThreshold()) {
				currentSynapse.learn(sourceNeuron.calculateActivation(a),
						targetNeuron.calculateActivation(b));
			} else {
				currentSynapse.unlearn(sourceNeuron.calculateActivation(a),
						targetNeuron.calculateActivation(b));
			}
			currentSynapse.setPriorActivation(sourceNeuron.getConcentration());

		}
	}

	/**
	 * resets concentration of activated neurons 
	 */
	private void updateActivatedNeurons() {

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
				
				// used for visualization
				currentActivatedNeurons.add(currentNeuron.getNeuronID());
			}

			// resets the build-up concentration and moves it to the current
			// concentration to be checked for activation
			currentNeuron.updateConcentration();
		}
	}

	/**
	 * initializing view components
	 */
	public void initPanel() {
		neuralNetworkFrame = new RecursiveNeuralNetworkFrame(neuralNetwork);
		neuralNetworkFrame.addReplayListener(new RecurrentReplayListener());
		neuralNetworkFrame
		.addPlayPauseListener(new RecursivePlayPauseListener());
		neuralNetworkFrame.addBackstepListener(new RecurrentBackstepListener());
		neuralNetworkFrame
		.addForwardstepListener(new RecurrentForwardstepListener());
		neuralNetworkFrame
		.addPeriodSliderChanger(new RecurrentPeriodSliderChanger());
		neuralNetworkFrame.getReplayButton().setEnabled(false);
	}

	/**
	 * action listener for play/pause button
	 *
	 */
	class RecursivePlayPauseListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			neuralNetworkFrame.getBackstepButton().setEnabled(true);
			if (timer.isRunning()) {
				timer.stop();
				neuralNetworkFrame.getPlayPauseButton().setText("Play");
				neuralNetworkFrame.setEnabled(true);
			} else {
				neuralNetworkFrame.getPlayPauseButton().setText("Pause");
				if (backstepIndex < (networkStateList.size() - 1)) {
					playAfterBackStepping = true;
				}
				timer.start();
			}
		}
	}

	/**
	 * action listener for replay button
	 *
	 */
	class RecurrentReplayListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			neuralNetworkFrame.getReplayButton().setEnabled(false);
			neuralNetworkFrame.getPlayPauseButton().setEnabled(true);
			replaying = true;
			replayIndex = 0;
			if (!timer.isRunning())
				timer.start();
		}
	}

	/**
	 * action listener for backstep button
	 *
	 */
	class RecurrentBackstepListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (timer.isRunning())
				timer.stop();

			neuralNetworkFrame.getPlayPauseButton().setText("Play");
			neuralNetworkFrame.getPlayPauseButton().setEnabled(true);
			neuralNetworkFrame.getReplayButton().setEnabled(false);

			if (!neuralNetworkFrame.getForwardstepButton().isEnabled())
				neuralNetworkFrame.getForwardstepButton().setEnabled(true);

			NetworkState currentState = networkStateList.get(--backstepIndex);
			neuralNetworkFrame.updateNeuralNetworkPanel("backstep",
					currentState.getNeuralNetwork());

			if (backstepIndex == 0)
				neuralNetworkFrame.getBackstepButton().setEnabled(false);

		}
	}

	/**
	 * action listener for forward step button
	 *
	 */
	class RecurrentForwardstepListener implements ActionListener {

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

			if (backstepIndex < networkStateList.size() - 2) {
				backstepIndex++;
				NetworkState currentState = networkStateList.get(backstepIndex);
				neuralNetworkFrame.updateNeuralNetworkPanel("forwardstep",
						currentState.getNeuralNetwork());
			} else {
				try {
					playStep();
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				if (doneSimulatingInputs)
					stabilizingCounter--;
				if (stabilizingCounter == 0) {
					neuralNetworkFrame.getForwardstepButton().setEnabled(false);
					neuralNetworkFrame.getPlayPauseButton().setEnabled(false);
				}
			}

			if (backstepIndex == tickCount - 1)
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

	/**
	 * action listener for slider
	 */
	class RecurrentPeriodSliderChanger implements ChangeListener {

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
