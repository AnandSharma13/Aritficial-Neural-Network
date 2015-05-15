package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

import view.Constants.FeedForwardNetworkMode;
import view.FileInputFrame;
import view.Constants.NetworkType;
import model.NeuralNetwork;

public class NetworkSimulationController {
	/** Reference variable of FileInputFrame object */
	private FileInputFrame fileInputScreen;
	/** Reference variable for NeuralNetwork object */
	private NeuralNetwork neuralNetwork;
	/** Reference variable for FeedForwardSimulator object */
	private FeedforwardSimulator feedforwardSimulator;
	/** Reference variable for RecurrentSimulator */
	private RecurrentSimulator recurrentSimulator;
	/** LOG4J File name */
	private static final String LOG4J_FILE = "log4j2.xml";
	/** Setting Logger object for LOG4J */
	private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(NetworkSimulationController.class);

	/***
	 * Contructs a NetworkSimulationController
	 * 
	 * @param fileInputScreen
	 *            FileInputFrame reference variable
	 * @param neuralNetwork
	 *            NeuralNetwork reference variable
	 */
	public NetworkSimulationController(FileInputFrame fileInputScreen, NeuralNetwork neuralNetwork) {
		this.fileInputScreen = fileInputScreen;
		this.neuralNetwork = neuralNetwork;
		this.fileInputScreen.addSimulateActionListener(new SimulateListener());
		this.fileInputScreen.addEvolveActionListener(new EvolveListener());
		org.apache.logging.log4j.core.config.Configurator.initialize("test", LOG4J_FILE);

	}

	/**
	 * Inner class that handles the action performed event of Evolve button
	 * 
	 */
	class EvolveListener implements ActionListener {

		/**
		 * Method that handles the click event of Evolve button
		 * 
		 * @param e ActionEvent object
		 * @throws CloneNotSupportedException Clone Not Supported Exception
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			// start Breeder
			try {
				new NetworkEvolver(fileInputScreen.getFileSelected());
			} catch (CloneNotSupportedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			fileInputScreen.dispose();
		}
	}

	/**
	 * Method to start the simulation of Neural Network
	 * 
	 * @param neuralNetwork Neural Network object
	 * @param inputTimeSignalMap Input time signal map
	 */
	private void simulateNetwork(NeuralNetwork neuralNetwork, Map<Integer, List<Integer>> inputTimeSignalMap) {
		// get type of simulation from view and trigger the appropriate
		// simulator
		NetworkType networkType = neuralNetwork.getNetworkType();
		if (networkType.equals(NetworkType.FEEDFORWARD)) {
			feedforwardSimulator = new FeedforwardSimulator(neuralNetwork, inputTimeSignalMap, FeedForwardNetworkMode.TIMED);
			feedforwardSimulator.simulate();
		} else {
			recurrentSimulator = new RecurrentSimulator(neuralNetwork, inputTimeSignalMap);
			recurrentSimulator.simulate();
		}
	}

	/**
	 * 
	 * Inner class that handles the action performed event of Simulate button
	 *
	 */
	class SimulateListener implements ActionListener {

		/**
		 * Method that handles the click event of Evolve button
		 * 
		 * @param e ActionEvent object
		 * @throws ex Event Exception
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			Map<Integer, List<Integer>> inputTimeSignalMap = new HashMap<Integer, List<Integer>>();

			try {
				String fileName = fileInputScreen.getFileSelected();
				neuralNetwork.buildNetwork(fileName, inputTimeSignalMap, fileInputScreen.isLabelSelected());
				simulateNetwork(neuralNetwork, inputTimeSignalMap);
				fileInputScreen.dispose();
			} catch (Exception ex) {
				ex.printStackTrace();
				LOG.error(ex);
				fileInputScreen.displayErrorMessage("Following exception occured while taking input : \n" + ex);
			}
		}
	}
}
