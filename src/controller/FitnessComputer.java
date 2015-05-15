package controller;

import java.util.ArrayList;
import java.util.List;

import view.Constants.FeedForwardNetworkMode;
import model.Gene;
import model.NeuralNetwork;

import org.apache.logging.log4j.LogManager;

/**  
* FitnessComputer class computes the fitness of a genome.
*/

public class FitnessComputer
{
	/** first genome that is used to start the evolution process  */
	private NeuralNetwork firstNeuralNetwork = null;
	
	/** instance of the feed forward simulator used for simulating the genome in order to test its fitness  */
	private FeedforwardSimulator feedforwardSimulator = null;
	
	/** list of expected outputs  */
	private List<Double> expectedOutputs = new ArrayList<Double>();
	
	/** log4j file name **/
	private static final String LOG4J_FILE = "log4j2.xml";
	private static final org.apache.logging.log4j.Logger LOG = LogManager
			.getLogger(FeedforwardSimulator.class);


	/**
	 * Constructs a FitnessComputer class 
	 * It initializes the logger file and the list of expected outputs
	 */
	public FitnessComputer() {
		org.apache.logging.log4j.core.config.Configurator.initialize("test",
				LOG4J_FILE);

		expectedOutputs.add(0.0);
		expectedOutputs.add(1.0);
		expectedOutputs.add(1.0);
		expectedOutputs.add(0.0);
	}

	/**
	 * Computes the fitness of a genome
	 * 
	 * @param genome genome whose fitness needs to be computed
	 * @return fitness fitness value of the genome
	 */
	public double findFitness(List<Gene> genome) throws InterruptedException
	{
		// build network from List<Gene> and set it as oldNeuralNetwork
		NeuralNetwork neuralNetwork = buildNetworkFromGenome(genome);		

		// calculate fitness
		double fitness = computeFitness(neuralNetwork);

		return fitness;
	}


	/**
	 * Computes the fitness of the network generated from the genome
	 * @param neuralNetwork neuralNetwork whose fitness needs to be computed
	 * @return fitness fitness value of the neuralNetwork
	 */
	private double computeFitness(NeuralNetwork neuralNetwork) throws InterruptedException 
	{

		feedforwardSimulator = new FeedforwardSimulator(neuralNetwork, neuralNetwork.getNetworkBuilder().getInputTimeSignalMap(), FeedForwardNetworkMode.NOT_TIMED);
		feedforwardSimulator.simulateWithoutTimer();

		List<Integer> actualOutputs = feedforwardSimulator.getOutputList();

		return computeFitness(actualOutputs);
	}

	/**
	 * Computes the fitness based on the fitness formula ( inverted mean square error )
	 * 
	 * @param actualOutputs list of outputs to be used in the fitness formula
	 * @return fitness fitness value computed using the formula
	 */
	private double computeFitness(List<Integer> actualOutputs) {

		double result=0.0;

		for(int i =0; i< expectedOutputs.size();i++)
			result = result + Math.pow((expectedOutputs.get(i) - actualOutputs.get(i)),2);

		return 1 - (result/expectedOutputs.size());
	}

	/**
	 * Builds a network from the incoming genome, so that it can be simulated while computing the genome's fitness
	 * 
	 * @param genome genome whose network needs to be built
	 * @return neuralNetwork neuralNetwork built from the incoming genome
	 */
	public NeuralNetwork buildNetworkFromGenome(List<Gene> genome) 
	{
		NeuralNetwork neuralNetwork = null;
		
		try {
			neuralNetwork = firstNeuralNetwork.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Gene gene : genome)
			if(neuralNetwork!=null)
				gene.writeMyself(neuralNetwork);	
			else
				LOG.info("Could not build a network for the genome");
		
		return neuralNetwork;
		
	}

	/**
	 * Setter for firstNeuralNetwork
	 * @param firstNeuralNetwork genome that is used to start the evolution process
	 */
	public void setFirstNeuralNetwork(NeuralNetwork firstNeuralNetwork) {
		this.firstNeuralNetwork = firstNeuralNetwork;
	}

}
