package model;

/**
 * Implementation of Gene Abstract class. This class stores information of
 * NeuronGene and provides methods for writing a NeuronGene to a Neural Network
 */

public class NeuronGene extends Gene {
	
	/** Unique id of NeuronGene*/
	private String neuronID;
	/** x coordinate of NeuronGene in 2D plane*/
	private int x;
	/** y coordinate of NeuronGene in 2D plane*/
	private int y;
	/** Threshold of NeuronGene*/
	private double threshold;

	/**
	 * Constructs a NeuronGene from a neuron object
	 * 
	 * @param neuron Neuron Object
	 */
	public NeuronGene(Neuron neuron) {
		neuronID = neuron.getNeuronID();
		x = neuron.getX();
		y = neuron.getY();
		threshold = neuron.getThreshold();
	}

	/**
	 * Copy constructor that constructs a deep copy of neuronGene object
	 * 
	 * @param neuronGene Neuron Gene Object
	 */
	public NeuronGene(NeuronGene neuronGene) {
		neuronID = neuronGene.neuronID;
		x = neuronGene.x;
		y = neuronGene.y;
		historical_id_counter = neuronGene.historical_id_counter;
		historicalID = neuronGene.historicalID;
		threshold = neuronGene.threshold;
	}

	/**
	 * Writes the calling NeuronGene object to NeuralNetwork. It is
	 * an implementation of writeMyself abstract method of Gene abstract class.
	 * 
	 * @param network Neural Network
	 */
	@Override
	public void writeMyself(NeuralNetwork network) {
		Neuron neuron = network.getNeuronMap().get(neuronID);
		neuron.setX(x);
		neuron.setY(y);
		neuron.setThreshold(threshold);
	}

	/**
	 * Mutates a NeuronGene object (Implementation pending)  
	 */
	@Override
	public void mutateMyself() {

	}
}
