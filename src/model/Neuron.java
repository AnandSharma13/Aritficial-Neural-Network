package model;

import static view.Constants.LOGARITHMIC_SIGMOID_PARAMETER;
import static view.Constants.NEURON_CIRCLE_RADIUS;

import java.awt.Color;
import java.util.ArrayList;

import view.Constants.ActivationFunction;

/**
 * This class holds information about each neuron such as neuron ID, neuron
 * layer(Input,Output,Hidden), gas type.
 */
public class Neuron implements Cloneable {

	/** Unique Id of Neuron*/
	private String neuronID;
	/** Neuron Layer type*/
	private String layerType;
	/** Gas type emitted by Neuron*/
	private String gasType;
	/** Neuron X coordinate on a 2D plane*/
	private int x;
	/** Neuron Y coordinate on a 2D plane*/
	private int y;
	/** Neuron circle radius*/
	private double radius = NEURON_CIRCLE_RADIUS;
	/** Bas production of Gas*/
	private double baseProduction;
	/** Gas emission radius*/
	private double emmissionRadius;
	/** Outgoing Synapses List*/
	private ArrayList<String> synapsesList;
	/** Threshold of Neuron*/
	private double threshold;
	/** Activation Concentration of Neuron*/
	private double activationConcentration;
	/** Boolean for storing Gas emitting flag*/
	private boolean gasEmitter;
	/** Boolean for storing Gas receiving flag*/
	private boolean gasReceiver;
	/** Neuron gas color*/
	private Color gasColor;
	/** Neuron gas dispersion unit*/
	private GasDispersionUnit gasDispersionUnit;
	/** Enum to select behavior for calculating output concentration upon firing*/
	private ActivationFunction activationFunction;
	/** Receptor class reference variable*/
	private Receptor receptor;

	/**
	 * Constructs a Neuron. Initializes outgoing synapse list of a neuron
	 */
	public Neuron() {
		synapsesList = new ArrayList<String>();
	}

	/**
	 * Updates the concentration of the receptor Neuron by setting the
	 * builtUpConcentration to the activationConcentration
	 * 
	 */
	public void updateConcentration() {
		receptor.incrementActivationConcentration();
		receptor.modulateActivationConcentration();
		activationConcentration = receptor.getActivationConcentration();
		receptor.clearConcentrations();
	}

	/**
	 * Calculates the Activation value of a Neuron for changing the weight of
	 * Synapse
	 * 
	 * @param concentration
	 *            Concentration of Neuron
	 * @return signalAmplitude Signal Amplitude
	 * 
	 * @return double
	 */
	public double calculateActivation(double concentration) {
		double signalAmplitude = 0;
		// set the output signal using selected activation function
		switch (activationFunction) {
		case LOGARITHMIC_SIGMOID:
			signalAmplitude = 1 / (1 + Math.exp(-1 * LOGARITHMIC_SIGMOID_PARAMETER * (concentration - threshold)));
		case STEP_FUNCTION:
			if (concentration - threshold >= 0) {
				signalAmplitude = 1;
			} else {
				signalAmplitude = 0;
			}
		default:
			break;
		}
		return signalAmplitude;
	}

	/**
	 * Calculates the activation of a Neuron
	 * 
	 * @return signalAmplitude Signal Amplitude
	 */
	public double calculateActivation() {
		double signalAmplitude = 0;
		// set the output signal using selected activation function
		switch (activationFunction) {
		case LOGARITHMIC_SIGMOID:
			signalAmplitude = 1 / (1 + Math.exp(-1 * LOGARITHMIC_SIGMOID_PARAMETER * (activationConcentration - threshold)));
		case STEP_FUNCTION:
			if (activationConcentration - threshold >= 0) {
				signalAmplitude = 1;
			} else {
				signalAmplitude = 0;
			}
		default:
			break;
		}
		return signalAmplitude;
	}

	/**
	 * Creates a clone of the Neuron
	 * 
	 * @return neuron Clone of Neuron Object
	 */
	public Neuron clone() throws CloneNotSupportedException {
		Neuron neuron = (Neuron) super.clone();
		neuron.setSynapsesList((ArrayList<String>) synapsesList.clone());
		if (neuron.gasEmitter)
			neuron.setGasDispersionUnit((GasDispersionUnit) gasDispersionUnit.clone());
		return neuron;
	}

	/**
	 * Returns ArrayList of outgoing Synapses
	 * 
	 * @return synapseList Outgoing Synapases List
	 */
	public ArrayList<String> getSynapsesList() {
		return synapsesList;
	}

	/**
	 * Returns NeuronID
	 * 
	 * @return neuronID Neuron ID
	 */
	public String getNeuronID() {
		return neuronID;
	}

	/**
	 * Sets NeuronID
	 * 
	 * @param neuronID
	 *            Neuron ID
	 */
	public void setNeuronID(String neuronID) {
		this.neuronID = neuronID;
	}

	/**
	 * Returns layer of the Neuron
	 * 
	 * @return layerType Layer of Neuron
	 */
	public String getLayerType() {
		return layerType;
	}

	/**
	 * Sets layer of Neuron
	 * 
	 * @param layerType
	 *            Layer of Neuron
	 */
	public void setLayerType(String layerType) {
		this.layerType = layerType;
	}

	/**
	 * Sets X coordinate of Neuron in the a 2D plane
	 * 
	 * @param x
	 *            Location of Neuron Object on X axis
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Sets Y coordinate of Neuron in the a 2D plane
	 * 
	 * @param y
	 *            Location of Neuron Object on X axis
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Returns threshold of a Neuron
	 * 
	 * @return threshold Threshold of Neuron
	 */
	public double getThreshold() {
		return threshold;
	}

	/**
	 * Sets threshold of a Neuron
	 * 
	 * @param threshold
	 *            Threshold of Neuron
	 */
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	/**
	 * Returns activationConcentration
	 * 
	 * @return activationConcentration Activation concentration
	 */
	public double getConcentration() {
		return activationConcentration;
	}

	/**
	 * Sets activationConcentration
	 * 
	 * @param concentration
	 *            Activation concentration
	 */
	public void setConcentration(double concentration) {
		this.activationConcentration = concentration;
	}

	/**
	 * Returns radius of neurons
	 * 
	 * @return radius
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * Sets radius of a Neuron
	 * 
	 * @param radius
	 *            Radius of Neuron in a 2D plane
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}

	/**
	 * Returns BaseProduction of a Neuron
	 * 
	 * @return baseProduction Gas base production value
	 */
	public double getBaseProduction() {
		return baseProduction;
	}

	/**
	 * Sets BaseProduction of a Neuron
	 * 
	 * @param baseProduction
	 *            Gas base production value
	 */
	public void setBaseProduction(double baseProduction) {
		this.baseProduction = baseProduction;
	}

	/**
	 * Returns gas emissionRadius
	 * 
	 * @return emissionRadius Gas emission radius of Neuron in a 2D plane
	 */
	public double getEmmissionRadius() {
		return emmissionRadius;
	}

	/**
	 * Sets gas emissionRadius
	 * 
	 * @param emmissionRadius
	 *            Gas emission radius of Neuron in a 2D plane
	 */
	public void setEmmissionRadius(double emmissionRadius) {
		this.emmissionRadius = emmissionRadius;
	}

	/**
	 * Returns gasChannel
	 * 
	 * @return gasChannel Gas channel of Neuron
	 */

	public GasDispersionUnit getGasDispersionUnit() {
		return gasDispersionUnit;
	}

	/**
	 * Sets gasChannel of a Neuron
	 * 
	 * @param gasChannel
	 *            Gas channel of Neuron
	 */

	public void setGasDispersionUnit(GasDispersionUnit gasChannel) {
		this.gasDispersionUnit = gasChannel;
	}

	/**
	 * Returns true if a Neuron receives gas
	 * 
	 * @return gasReceiver True of Neuron is a gas Receiver
	 */
	public boolean isGasReceiver() {
		return gasReceiver;
	}

	/**
	 * Sets gas receiving flag of a neuron
	 * 
	 * @param gasReceiver
	 *            True of Neuron is a gas Receiver
	 */
	public void setGasReceiver(boolean gasReceiver) {
		this.gasReceiver = gasReceiver;
	}

	/**
	 * Returns Neuron gasType of Neuron
	 * 
	 * @return gasType Gas type a Neuron
	 */
	public String getGasType() {
		return gasType;
	}

	/**
	 * Sets gasType of Neuron
	 * 
	 * @param gasType
	 *            Gas type of a Neuron
	 */
	public void setGasType(String gasType) {
		this.gasType = gasType;
	}

	/**
	 * Returns true if a Neuron is a gas emitter
	 * 
	 * @return gasEmitter True if Neuron is gas emitter
	 */
	public boolean isGasEmitter() {
		return gasEmitter;
	}

	/**
	 * Sets gas emitting flag of a neuron
	 * 
	 * @param gasEmitter
	 *            True if Neuron is gas emitter
	 */
	public void setGasEmitter(boolean gasEmitter) {
		this.gasEmitter = gasEmitter;
	}

	/**
	 * Returns Activation function of a Neuron
	 * 
	 * @return activationFunction Activation Function of Neuron
	 */
	public ActivationFunction getActivationFunction() {
		return activationFunction;
	}

	/**
	 * Sets the activationFunction of a Neuron
	 * 
	 * @param activationFunction
	 *            Activation Function of Neuron
	 */
	public void setActivationFunction(ActivationFunction activationFunction) {
		this.activationFunction = activationFunction;
	}

	/**
	 * Returns the location x coordinate of Neuron in 2D plane
	 * 
	 * @return x X Coordinate of Neuron
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets outgoing SynapasesList of a Neuron
	 * 
	 * @param synapsesList
	 *            Outgoing Synapse List
	 */
	public void setSynapsesList(ArrayList<String> synapsesList) {
		this.synapsesList = synapsesList;
	}

	/**
	 * Returns Y coordinate of Neuron on 2D plane
	 * 
	 * @return y Y Coordinate of Neuron
	 */
	public int getY() {
		return y;
	}

	/**
	 * Returns gasColor
	 * 
	 * @return gasColor Color of gas emitted by Neuron
	 */
	public Color getGasColor() {
		return gasColor;
	}

	/**
	 * Sets gasColor
	 * 
	 * @param gasColor
	 *            Color of gas emitted by Neuron
	 */
	public void setGasColor(Color gasColor) {
		this.gasColor = gasColor;
	}

	/**
	 * Returns Receptor
	 * 
	 * @return receptor Returns the receptor object of Neuron
	 */
	public Receptor getReceptor() {
		return receptor;
	}

	/**
	 * Sets Receptor
	 * 
	 * @param receptor
	 *            receptor Returns the receptor object of Neuron
	 */
	public void setReceptor(Receptor receptor) {
		this.receptor = receptor;
	}
}
