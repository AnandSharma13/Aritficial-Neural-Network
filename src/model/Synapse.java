package model;

import static view.Constants.HEBBIAN_DECAY_PARAMETER;
import static view.Constants.HEBBIAN_LEARNING_PARAMETER;
import static view.Constants.MAXIMUM_SYNAPTIC_WEIGHT;
import static view.Constants.MINIMUM_SYNAPTIC_WEIGHT;

/**
 * This class contains information about synapse links in neural network it has
 * synapse ID, source neuron and target neuron to hold information about each
 * synapse link.
 * 
 * In addition to information of Synapse, it has methods to increase and
 * decrease plasticity of Synapse
 */
public class Synapse implements Cloneable {

	/** Unique Id of Synapse*/
	private String synapseID;
	/** Source neuron of Synapse*/
	private String sourceNeuron;
	/** Target neuron of Synapse*/
	private String targetNeuron;
	/** Synaptic weight of Synapse*/
	private double synapticWeight;
	/** Prior activation of Synapse*/
	private double priorActivation = 0;
	/** Plasticity of a Synapse*/
	private double plasticity;

	/**
	 * Updates the Plasticity of the calling Synapse based on the plasticity
	 * modulation function stored in its source neuron receptor
	 * 
	 * @param network
	 *            Neural Network object
	 */
	public void updatePlasticity(NeuralNetwork network) {
		Receptor receptor = network.getNeuronMap().get(sourceNeuron).getReceptor();
		receptor.modulatePlasticity();
		plasticity = receptor.getPlasticity();
	}

	/**
	 * Changes the synapticsWeight of the calling synapse based on Standard
	 * Hebbian
	 * 
	 * @param currentSynapsePriorActivation
	 *            Current Synapse Prior Activation
	 * @param targetNeuronConcentration
	 *            Target Neuron Concentration
	 */
	public void learn(double currentSynapsePriorActivation, double targetNeuronConcentration) {
		if (currentSynapsePriorActivation > 0.2) {
			synapticWeight += plasticity * HEBBIAN_LEARNING_PARAMETER * (currentSynapsePriorActivation - 0.5)
					* (targetNeuronConcentration - 0.5);
			if (synapticWeight >= MAXIMUM_SYNAPTIC_WEIGHT)
				synapticWeight = MAXIMUM_SYNAPTIC_WEIGHT;
			if (synapticWeight <= MINIMUM_SYNAPTIC_WEIGHT)
				synapticWeight = MINIMUM_SYNAPTIC_WEIGHT;
		}
	}

	/**
	 * Changes the synapticsWeight of the calling synapse based on Standard
	 * Hebbian learning formulae
	 * 
	 * @param currentSynapsePriorActivation
	 *            Current Synapse Prior Activation
	 * @param targetNeuronConcentration
	 *            Target Neuron Concentration
	 */
	public void unlearn(double currentSynapsePriorActivation, double targetNeuronConcentration) {
		if (currentSynapsePriorActivation > 0.2) {
			synapticWeight += plasticity * HEBBIAN_DECAY_PARAMETER * (currentSynapsePriorActivation - 0.5)
					* (targetNeuronConcentration - 0.5);
			if (synapticWeight >= MAXIMUM_SYNAPTIC_WEIGHT)
				synapticWeight = MAXIMUM_SYNAPTIC_WEIGHT;
			if (synapticWeight <= MINIMUM_SYNAPTIC_WEIGHT)
				synapticWeight = MINIMUM_SYNAPTIC_WEIGHT;
		}
	}

	/**
	 * Return synapseID of a Synapse
	 * 
	 * @return synapseID Synapse ID
	 */
	public String getSynapseID() {
		return synapseID;
	}

	/**
	 * Sets SynapseID
	 * 
	 * @param synapseID Synapse ID
	 */
	public void setSynapseID(String synapseID) {
		this.synapseID = synapseID;
	}

	/**
	 * Returns source neuron of a Synapse
	 * 
	 * @return sourceNeuron Source Neuron of Synapse
	 */
	public String getSourceNeuron() {
		return sourceNeuron;
	}

	/**
	 * Sets source Neuron of a Synapse
	 * 
	 * @param sourceNeuron Source Neuron of a Synapse
	 */
	public void setSourceNeuron(String sourceNeuron) {
		this.sourceNeuron = sourceNeuron;
	}

	/**
	 * Return target Neuron of a Synapse
	 * 
	 * @return targetNeuron Target Neuron of Synapse
	 */
	public String getTargetNeuron() {
		return targetNeuron;
	}

	/**
	 * Sets target Neuron of a Synpase
	 * 
	 * @param targetNeuron Target Neuron of Synapse
	 */
	public void setTargetNeuron(String targetNeuron) {
		this.targetNeuron = targetNeuron;
	}

	/**
	 * Return Synpatic weight of a Synapse
	 * 
	 * @return synapticWeight Weight a Synapse
	 */
	public double getSynapticWeight() {
		return synapticWeight;
	}

	/**
	 * Sets Synaptic weight of Synapse
	 * 
	 * @param synapticWeight Weight of Synapse
	 */
	public void setSynapticWeight(double synapticWeight) {
		this.synapticWeight = synapticWeight;
	}

	/**
	 * Returns priorActivation
	 * 
	 * @return priorActivation Prior Activation of Synapse
	 */
	public double getPriorActivation() {
		return priorActivation;
	}

	/**
	 * Sets priorActivation
	 * 
	 * @param priorActivation Prior Activation of Synapse
	 */
	public void setPriorActivation(double priorActivation) {
		this.priorActivation = priorActivation;
	}

	/**
	 * Returns clone of Synpase object
	 * 
	 * @return Synapse Clone a Synapse object
	 */
	public Synapse clone() throws CloneNotSupportedException {
		return (Synapse) super.clone();
	}

	/**
	 * Return plasticity
	 * 
	 * @return plasticity Plasticity value of Synapse
	 */
	public double getPlasticity() {
		return plasticity;
	}

	/**
	 * Sets plasticity
	 * 
	 * @param plasticity Plasticity value of Synapse
	 */
	public void setPlasticity(double plasticity) {
		this.plasticity = plasticity;
	}
}
