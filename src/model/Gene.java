package model;

/**
 * This class is a superclass of SynapseGene and NeuronGene classes. It provides
 * of signatures of abstract methods common to SynapseGene and NeuronGene.
 */

public abstract class Gene {
	/** Historical Id counter of Gene */
	public static int historical_id_counter;
	/** Historical Id of Gene */
	public String historicalID;
	/** Id of Gene */
	protected String id;

	/**
	 * Abstract method to write calling subclass object to NeuralNetwork
	 * 
	 * @param network
	 *            Neural Network
	 */
	public abstract void writeMyself(NeuralNetwork network);

	/**
	 * Abstract method to mutate calling sublcass object
	 */
	public abstract void mutateMyself();

	/**
	 * Returns Id of Gene
	 * 
	 * @return id Gene id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets Id of a Gene 
	 * @param id Gene id
	 */
	public void setId(String id) {
		this.id = id;
	}

}
