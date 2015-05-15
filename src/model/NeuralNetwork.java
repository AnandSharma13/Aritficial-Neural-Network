package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import view.Constants.ActivationFunction;
import view.Constants.NetworkType;
import view.Constants.VisualizationModes;

/**
 * Neural network class contains fields to store wholistic information about a
 * neural network. It provides methods to build a neural network from MS Excel
 * file and cloning a neural network.
 */
public class NeuralNetwork implements Cloneable {

	/** Neural Network Neurons HashMap*/
	private HashMap<String, Neuron> neuronMap;
	/** Neural Network Synapse HashMap*/
	private HashMap<String, Synapse> synapseMap;
	/** Neural Network GAS HashMap*/
	private HashMap<String, Gas> gasMap;
	/** Gas Receiving Neurons HashMap*/
	private HashMap<String, List<Neuron>> gasReceiverNeuronsMap;
	/** Receptor Neurons HashMap*/
	private HashMap<String, Receptor> receptorMap;
	/** Neural Network Polynomial Map*/
	private HashMap<String, Polynomial> functionMap;
	/** Network Builder class object*/
	private NetworkBuilder networkBuilder;
	/** Neural Network Activation function*/
	private ActivationFunction activationFunction;
	/** Boolean for setting Labels*/
	private boolean labeled;
	/** Neural Network Visualization mode*/
	private VisualizationModes mode;
	/** NetworType object*/
	private NetworkType networkType;
	/** Keeps track of Neural Network generation*/
	private Integer generation;

	/**
	 * Constructs a Neural Network
	 */
	public NeuralNetwork() {
		neuronMap = new HashMap<String, Neuron>();
		synapseMap = new HashMap<String, Synapse>();
		gasMap = new HashMap<String, Gas>();
		setReceptorMap(new HashMap<String, Receptor>());
		setFunctionMap(new HashMap<String, Polynomial>());
		gasReceiverNeuronsMap = new HashMap<String, List<Neuron>>();
		networkBuilder = new NetworkBuilder();
	}

	/**
	 * Builds a Neural Network from an Excel file.
	 * 
	 * @param fileName
	 *            Excel File Name
	 * @param inputTimeSignalMap
	 *            Input Time Signal Map
	 */
	public void buildNetwork(String fileName, Map<Integer, List<Integer>> inputTimeSignalMap) {
		networkBuilder.setInputTimeSignalMap(inputTimeSignalMap);
		networkBuilder.buildNetwork(fileName, this);
	}

	/**
	 * Builds Neural Network from an Excel File. This builds a network with
	 * SynapaticWeights as labels of the synapses.
	 * 
	 * @param fileName
	 *            Excel File Name
	 * @param inputTimeSignalMap
	 *            Input Time Signal Map
	 * @param labeled
	 *            True if Labels are enabled
	 */
	public void buildNetwork(String fileName, Map<Integer, List<Integer>> inputTimeSignalMap, boolean labeled) {
		networkBuilder.setInputTimeSignalMap(inputTimeSignalMap);
		this.labeled = labeled;
		networkBuilder.buildNetwork(fileName, this);
	}

	/**
	 * Creates a clone of Neural Network
	 * 
	 * @return NeuralNetwork Clone of calling return Neuralk Network object
	 * @throws CloneNotSupportedException
	 *             Clone not supported exception
	 */
	public NeuralNetwork clone() throws CloneNotSupportedException {
		NeuralNetwork neuralNetworkCopy = (NeuralNetwork) super.clone();
		neuralNetworkCopy.setNeuronMap((HashMap<String, Neuron>) deepClone(neuronMap));
		neuralNetworkCopy.setSynapseMap((HashMap<String, Synapse>) deepClone(synapseMap));
		neuralNetworkCopy.setGasMap((HashMap<String, Gas>) deepClone(gasMap));
		neuralNetworkCopy.setGasReceiverNeuronsMap((HashMap<String, List<Neuron>>) deepClone(gasReceiverNeuronsMap));
		return neuralNetworkCopy;
	}

	/**
	 * This method returns a deep copy of a Map
	 * 
	 * @param map
	 *            HashMap to be cloned
	 * @throws CloneNotSupportedException
	 *             Clone not supported exception
	 * @return mapCopy Clone of HashMap
	 */
	private Object deepClone(HashMap map) throws CloneNotSupportedException {
		HashMap mapCopy = new HashMap();
		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			Entry entry = (Entry) it.next();
			if (entry.getValue().getClass() == Neuron.class)
				mapCopy.put(entry.getKey(), ((Neuron) entry.getValue()).clone());
			else if (entry.getValue().getClass() == Synapse.class)
				mapCopy.put(entry.getKey(), ((Synapse) entry.getValue()).clone());
			else if (entry.getValue().getClass() == Gas.class)
				mapCopy.put(entry.getKey(), ((Gas) entry.getValue()).clone());
		}

		return mapCopy;
	}

	/**
	 * Returns HashMap of all Receptors in the Neural Network
	 * 
	 * @return receptorMap HashMap of Receptors
	 */
	public HashMap<String, Receptor> getReceptorMap() {
		return receptorMap;
	}

	/**
	 * Sets HashMap of Receptors in the Neural Network
	 * 
	 * @param receptorMap
	 *            HashMap of Receptors
	 */
	public void setReceptorMap(HashMap<String, Receptor> receptorMap) {
		this.receptorMap = receptorMap;
	}

	/**
	 * Returns HashMap of Polynomials
	 * 
	 * @return functionMap HashMap of Polynomials
	 */
	public HashMap<String, Polynomial> getFunctionMap() {
		return functionMap;
	}

	/**
	 * Sets HashMap of Polynomials
	 * 
	 * @param functionMap
	 *            HashMap of Polynomials
	 */
	public void setFunctionMap(HashMap<String, Polynomial> functionMap) {
		this.functionMap = functionMap;
	}

	/**
	 * Returns ActivationFunction of Neural Network
	 * 
	 * @return activationFunction Activation function of Neural Network
	 */
	public ActivationFunction getActivationFunction() {
		return activationFunction;
	}

	/**
	 * Sets ActivationFunction of Neural Network
	 * 
	 * @param activationFunction
	 *            Activation function of Neural Network
	 */
	public void setActivationFunction(ActivationFunction activationFunction) {
		this.activationFunction = activationFunction;
	}

	/**
	 * Returns Visualization mode of Neural Network
	 * 
	 * @return mode Visualization mode
	 */
	public VisualizationModes getMode() {
		return mode;
	}

	/**
	 * Sets Visualization mode of Neural Network
	 * 
	 * @param mode
	 *            Visualization mode
	 */
	public void setMode(VisualizationModes mode) {
		this.mode = mode;
	}

	/**
	 * Returns Neural Network Type
	 * 
	 * @return networkType Neural Network Type
	 */
	public NetworkType getNetworkType() {
		return networkType;
	}

	/**
	 * Sets Neural Network Type
	 * 
	 * @param networkType
	 *            Neural Network Type
	 */
	public void setNetworkType(NetworkType networkType) {
		this.networkType = networkType;
	}

	/**
	 * Returns Network Builder class object
	 * 
	 * @return networkBuilder Network builder class object
	 */
	public NetworkBuilder getNetworkBuilder() {
		return networkBuilder;
	}

	/**
	 * Sets Network Builder class object
	 * 
	 * @param networkBuilder
	 *            Network builder class object
	 */
	public void setNetworkBuilder(NetworkBuilder networkBuilder) {
		this.networkBuilder = networkBuilder;
	}

	/**
	 * Returns NeuronMap Hashmap
	 * 
	 * @return neuronMap HashMap of Neurons
	 */
	public HashMap<String, Neuron> getNeuronMap() {
		return neuronMap;
	}

	/**
	 * Returns Synapse Hashmap
	 * 
	 * @return synapseMap HashMap of Synapses
	 */
	public HashMap<String, Synapse> getSynapseMap() {
		return synapseMap;
	}

	/**
	 * Returns True if Neural Network is labeled
	 * 
	 * @return labeled True if Neural Network is labeled
	 */
	public boolean isLabeled() {
		return labeled;
	}

	/**
	 * Sets boolean for labels of Neural Network
	 * 
	 * @param labeled
	 *            True if Neural Network is labeled
	 */
	public void setLabeled(boolean labeled) {
		this.labeled = labeled;
	}

	/**
	 * Sets NeuronMap HashMap
	 * 
	 * @param neuronMap
	 *            HashMap of neurons
	 */
	public void setNeuronMap(HashMap<String, Neuron> neuronMap) {
		this.neuronMap = neuronMap;
	}

	/**
	 * Sets SynapseMAp HashMap
	 * 
	 * @param synapseMap
	 *            HashMap of synapses
	 */
	public void setSynapseMap(HashMap<String, Synapse> synapseMap) {
		this.synapseMap = synapseMap;
	}

	/**
	 * Sets Gas Receiver Neurons Map
	 * 
	 * @param gasReceiverNeuronsMap
	 *            HashMap of gas receiver Neurons
	 */
	public void setGasReceiverNeuronsMap(HashMap<String, List<Neuron>> gasReceiverNeuronsMap) {
		this.gasReceiverNeuronsMap = gasReceiverNeuronsMap;
	}

	/**
	 * Returns HashMap of gases
	 * 
	 * @return gasMap HashMap of gases
	 */
	public HashMap<String, Gas> getGasMap() {
		return gasMap;
	}

	/**
	 * Sets HashMap of gases
	 * 
	 * @param gasMap
	 *            HashMap of gases
	 */
	public void setGasMap(HashMap<String, Gas> gasMap) {
		this.gasMap = gasMap;
	}

	/**
	 * Returns HashMap of gas receiver map
	 * 
	 * @return gasReceiverNeuronsMap HashMap of gas receiver neurons
	 */
	public HashMap<String, List<Neuron>> getGasReceiverNeuronsMap() {
		return gasReceiverNeuronsMap;
	}

	/**
	 * Sets generation of Neural Network
	 * 
	 * @param generation
	 *            Generation of Neural Network
	 */
	public void setGeneration(Integer generation) {
		this.generation = generation;
	}

}
