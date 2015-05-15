package main;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.NetworkBuilder;
import model.NetworkState;
import model.NeuralNetwork;
import model.Neuron;
import model.Synapse;

import org.junit.Before;
import org.junit.Test;

import view.Constants.FeedForwardNetworkMode;
import view.Constants.VisualizationModes;
import controller.FeedforwardSimulator;

public class FeedForwardLogicGateTest {

	FeedforwardSimulator simulator;
	NeuralNetwork neuralNetwork;
	ArrayList<Integer> expectedOutputList;
	ArrayList<Integer> actualOutputList;
	Map<Integer, List<Integer>> inputTimeSignalMap;
	

	@Before
	public void beforeProcessInputSignalTest(){
		neuralNetwork = new NeuralNetwork();
		inputTimeSignalMap = new HashMap<Integer, List<Integer>>();
		
		neuralNetwork.buildNetwork("data/XORNetwork.xlsx", inputTimeSignalMap, false);
		simulator = new FeedforwardSimulator(neuralNetwork, inputTimeSignalMap, FeedForwardNetworkMode.TIMED);

		expectedOutputList = new ArrayList<Integer>();
		
		expectedOutputList.add(0);
		expectedOutputList.add(1);
		expectedOutputList.add(1);
		expectedOutputList.add(0);
	}
	
	@Test
	public void test() throws IOException {
		
		ArrayList<String> currentActivatedNeurons = new ArrayList<String>();
		List<String> activatedNeurons = new ArrayList<String>();
		ArrayList<Integer> outputList = new ArrayList<Integer>();
		String outputNeuron = simulator.getOutputNeuron();
		
		
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
		
		
		
		for (int i = 1; i < 5; i++) {
			// Code for each timer tick goes here!!
	
			List<Integer> currentSignal = inputTimeSignalMap.get(i);

			currentActivatedNeurons.clear();

			for (int index = 0; index < currentSignal.size(); index++) {
				if (currentSignal.get(index) == 1) {
					String currentNeuron = activatedNeurons.get(index);
					currentActivatedNeurons.add(currentNeuron);
				}
			}

			if (currentActivatedNeurons.size() == 0) {
				outputList.add(0);
			}

			else {
				boolean notReachedOutputNeuron = true;
				NetworkState currentState = new NetworkState();
				ArrayList<String> currentPath = new ArrayList<String>();
				
				
				while (true) {

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
					Set neuronMapSet1 = neuralNetwork.getNeuronMap().entrySet();

					// Get an iterator
					Iterator neuronIterator1 = neuronMapSet1.iterator();

					// Display elements
					while (neuronIterator1.hasNext()) {
						Map.Entry currentEntry = (Map.Entry) neuronIterator1.next();
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
					outputList.add(1);
					currentPath.add(outputNeuron);
				} else {
					outputList.add(0);
				}

				neuralNetwork.getNeuronMap().get(outputNeuron).setConcentration(0);
				currentState.setActivatedPath(currentPath);

			}
		}
		
		actualOutputList = outputList;
		System.out.println(expectedOutputList);
		System.out.println(actualOutputList);
		assertEquals(expectedOutputList, actualOutputList);
		
	}

}
