package model;

import java.util.ArrayList;
import java.util.List;
import controller.NetworkEvolver;
import static view.Constants.SYNAPTIC_WEIGHT_LOWER_BOUND;
import static view.Constants.SYNAPTIC_WEIGHT_UPPER_BOUND;

/**
 * This class extends the Gene abstract class. It's fields store the information
 * of a SynapseGene and it provides methods for writing a SynapseGene to a
 * Genome and for mutating a SynapseGene.
 */
public class SynapseGene extends Gene {
	/** Synaptic weight of a SynapseGene*/
	private double synapticWeight;
	/** Unique Id of SynapseGene*/
	private String synapseID;

	/**
	 * Constructs a SynapseGene from a synapse object
	 * 
	 * @param synapse Synapse Object
	 */
	public SynapseGene(Synapse synapse) {
		synapseID = synapse.getSynapseID();
		synapticWeight = synapse.getSynapticWeight();
	}

	/**
	 * This a copy constructor of SynapseGene object. It constructs a deep copy
	 * of SynapseGene object
	 * 
	 * @param synapseGene SynapseGene Object
	 */
	public SynapseGene(SynapseGene synapseGene) {
		synapticWeight = synapseGene.synapticWeight;
		synapseID = synapseGene.synapseID;
		id = synapseGene.id;
		historicalID = synapseGene.historicalID;
		historical_id_counter = synapseGene.historical_id_counter;
	}

	/**
	 * Writes the calling NeuronGene object to a NeuralNetwork. It is an
	 * implementation of writeMyself abstract method.
	 * 
	 * @param network Neural Network Object
	 */
	@Override
	public void writeMyself(NeuralNetwork network) {
		Synapse synapse = network.getSynapseMap().get(synapseID);
		synapse.setSynapseID(synapseID);
		synapse.setSynapticWeight(synapticWeight);
	}

	/**
	 * Mutates a SynapseGene by assigning a new SynapticWeight.
	 */
	@Override
	public void mutateMyself() 
	{
		synapticWeight = NetworkEvolver.generateRandomNumber(-1, 1);
	}
}
