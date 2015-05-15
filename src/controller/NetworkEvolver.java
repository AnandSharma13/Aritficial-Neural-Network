package controller;

import static view.Constants.PERCENTAGE_GOOD_SPECIES;
import static view.Constants.POPULATION_SIZE;
import static view.Constants.SYNAPTIC_WEIGHT_MODIFICATION_THRESHOLD;
import static view.Constants.TOP_SPECIES_RETAIN;
import static view.Constants.BREEDING_THRESHOLD;
import static view.Constants.GOAL_FITNESS;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import model.Gene;
import model.NeuralNetwork;
import model.Neuron;
import model.NeuronGene;
import model.Synapse;
import model.SynapseGene;
import org.apache.logging.log4j.LogManager;
import view.Constants.FeedForwardNetworkMode;
import view.Constants.NetworkType;

/**  
* NetworkEvolver class creates and evolves multiple generations
* until network with expected fitness value is reached or maximum 
* number of generations are evolved.
*/

public class NetworkEvolver {
	/** Current generation dna list */
	private List<List<Gene>> dnaList;
	
	/** Network fitness map for current generation */
	private Map<List<Gene>, Double> networkFitnessMap;
	
	/** Object to compute fitness value for each network */
	FitnessComputer fitnessComputer;
	
	/** List to store nest generation dna networks */
	private List<List<Gene>> nextGenerationDnaList;
	
	/** Counter to hold current generation */
	private int currentGeneration = 0;
	
	/** log4j file name **/
	private static final String LOG4J_FILE = "log4j2.xml";
	private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(FeedforwardSimulator.class);
	
	/** top Genome having maximum desired fitness value */
	private List<Gene> topGenome;

	/**
	 * Getter method for next generation dna list
	 * 
	 * @return nextGenerationDnaList	DNA list for next generation
	 */	
	public List<List<Gene>> getNextGenerationDnaList() {
		return nextGenerationDnaList;
	}

	/**
	 * Setter method to set next generation dna list
	 * 
	 * @param nextGenerationDnaList		DNA list for next generation
	 */	
	public void setNextGenerationDnaList(List<List<Gene>> nextGenerationDnaList) {
		this.nextGenerationDnaList = nextGenerationDnaList;
	}

	/**
	 * Constructs a Network Evolver class from input network file name
	 * 
	 * @param fileName	Input file name to build network
	 * @throws	CloneNotSupportedException	exception if list cloning not supported
	 */
	public NetworkEvolver(String fileName) throws CloneNotSupportedException {
		org.apache.logging.log4j.core.config.Configurator.initialize("test", LOG4J_FILE);
		nextGenerationDnaList = new ArrayList<List<Gene>>();
		fitnessComputer = new FitnessComputer();
		dnaList = new ArrayList<List<Gene>>();
		networkFitnessMap = new LinkedHashMap<List<Gene>, Double>();
		//generate identical networks from given input file name 
		initializeFirstGenerationDNAList(fileName);
		currentGeneration++;
		evolve();
	}
	
	/**
	 * Evolve method will evolve each generation from the list of input networks,
	 * This method contains logic for computing fitness, killing bad species, 
	 * cross over and mutate
	 */
	private void evolve() {
		// find fitness of each network(geneList) in the dnaList
		double fitness = 0;
		double topFitness = 0;

		networkFitnessMap.clear();
		
		for (List<Gene> geneList : dnaList){
			try {
				//calculate fitness for each  network in genome list
				fitness = fitnessComputer.findFitness(geneList);

				//search for the network with maximum fitness value
				if(fitness > GOAL_FITNESS && fitness > topFitness )	{
					topGenome = geneList;
					topFitness = fitness;
				}
				networkFitnessMap.put(geneList, fitness);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//this method will sort input networks based on fitness values and then 
		//keep top 40 percent of top networks 
		killBadSpecies();
		
		LOG.info("Current Generation is :\t" + currentGeneration);
		LOG.info("Fitness Values :\t"+networkFitnessMap.values());

		try {
			//cross over method will keep first 10 percent networks for next generation
			//and creates 40% new networks with cross over of two randomly selected existing networks
			crossOver();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		//mutate will take 50 percent newly generated genomes and create same number of new networks
		mutate();

		dnaList = deepCopyGenome(nextGenerationDnaList);
		
		nextGenerationDnaList.clear();

		//increment to next generation
		currentGeneration++;

		if (topFitness <= GOAL_FITNESS) {
			if(currentGeneration <= BREEDING_THRESHOLD)
				//evolve new generation if goal fitness or breeding threshold is not reached
				evolve();
			else
				LOG.info("REACHED THRESHOLD FOR BREEDING!!");
		} else {

			LOG.info("REACHED GOAL FITNESS!!"+topFitness);
			//display the network with maximum fitness value
			displayTopNetwork();
		}
	}

	/**
	 * This method reads excel files and create POPULATION_SIZE(= 100) new networks
	 * 
	 * @param fileName	Input file name to build network
	 * 
	 * @throws	CloneNotSupportedException	exception if list cloning not supported
	 */
	public void initializeFirstGenerationDNAList(String fileName) throws CloneNotSupportedException {
		// read excel files and create POPULATION_SIZE(= 100) new networks

		// creating 1st neural network
		NeuralNetwork neuralNetwork = new NeuralNetwork();
		neuralNetwork.buildNetwork(fileName, new HashMap<Integer, List<Integer>>());

		// using 1st neural network to build 99 similar networks
		for (int i = 0; i < POPULATION_SIZE; i++) {
			// create a new network with random synaptic weights
			randomizeWeights(neuralNetwork);

			if(i==0)
				fitnessComputer.setFirstNeuralNetwork(neuralNetwork);

			dnaList.add(createGeneList(neuralNetwork));
		}

	}

	/**
	 * This method implements functionality to display the top network
	 * which gives desired fitness value
	 */	
	private void displayTopNetwork() {
		// build a neural network out of the most fit genome.
		NeuralNetwork neuralNetwork = fitnessComputer.buildNetworkFromGenome(topGenome);

		Map<Integer, List<Integer>> inputTimeSignalMap = neuralNetwork.getNetworkBuilder().getInputTimeSignalMap();

		neuralNetwork.setGeneration(currentGeneration);

		// get type of simulation from view and trigger the appropriate
		// simulator
		NetworkType networkType = neuralNetwork.getNetworkType();

		if (networkType.equals(NetworkType.FEEDFORWARD)) {
			neuralNetwork.setLabeled(true);
			FeedforwardSimulator feedforwardSimulator = new FeedforwardSimulator(neuralNetwork, inputTimeSignalMap,
					FeedForwardNetworkMode.TIMED);
			feedforwardSimulator.simulate();
		} else {
			RecurrentSimulator recurrentSimulator = new RecurrentSimulator(neuralNetwork, inputTimeSignalMap);
			recurrentSimulator.simulate();
		}
	}

	/**
	 * Method to convert the neural network into a list of genes
	 * 
	 * @param neuralNetwork		neural network to create gene list
	 * 
	 * @return geneList			output gene list created from neural network
	 */
	private List<Gene> createGeneList(NeuralNetwork neuralNetwork) {
		List<Gene> geneList = new ArrayList<Gene>();

		// adding all neuron genes - iterate on neuronMap and create NeuronGenes
		for (Neuron neuron : neuralNetwork.getNeuronMap().values()) {
			NeuronGene neuronGene = new NeuronGene(neuron);
			geneList.add(neuronGene);
		}

		// adding all synapse genes - iterate on synapseMap and create
		// SynapseGenes
		for (Synapse synapse : neuralNetwork.getSynapseMap().values()) {
			SynapseGene synapseGene = new SynapseGene(synapse);
			geneList.add(synapseGene);
		}
		return geneList;
	}

	/**
	 * Method to sort existing network fitness map based on fitness values
	 * 
	 *  @param list			input list to sort
	 */
	public void sortFitnessNetworkMap(List list)
	{
		//use sort method to sort based on fitness value
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return -((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});
	}

	/**
	 * Method to sort input networks based on fitness values and 
	 * then keep top 40 percent of top networks
	 */	
	public void killBadSpecies() {

		// sort the network fitness map based on fitness function value
		List list = new LinkedList(networkFitnessMap.entrySet());

		sortFitnessNetworkMap(list);

		// kill the bad species by just retaining the top K entries of
		// calculate number of species that survive after first generation
		int numberOfSpeciesSurvive = (networkFitnessMap.size() * PERCENTAGE_GOOD_SPECIES) / 100;
		int counter = 0;

		// copy survived species to network fitness map
		// Linked hash map is used to store order of keys based on insertion
		Map<List<Gene>, Double> sortedNetworkFitnessMap = new LinkedHashMap();

		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			if (counter < numberOfSpeciesSurvive) {
				sortedNetworkFitnessMap.put((List<Gene>) entry.getKey(), (Double) entry.getValue());
			} else {
				break;
			}
			counter++;
		}

		networkFitnessMap = sortedNetworkFitnessMap;
	}

	/**
	 * Cross over method to keep first 10 percent networks for next generation and 
	 * create 40% new networks with cross over of two randomly selected existing networks
	 * 
	 * @throws	CloneNotSupportedException	exception if list cloning not supported
	 */	
	public void crossOver() throws CloneNotSupportedException {
		// use networkFitnessMap to get the list of genes to cross over
		Map<Integer, List<Gene>> sorteddnaList = new HashMap<Integer, List<Gene>>();
		int counter = 0, networkCounter = 0;

		//convert existing fitness map in new map with index value to select random network
		Set<Entry<List<Gene>, Double>> networkFitnessList = networkFitnessMap.entrySet();

		for (Iterator it = networkFitnessList.iterator(); it.hasNext();) {
			Map.Entry<List<Gene>, Double> entry = (Map.Entry) it.next();
			sorteddnaList.put(counter, entry.getKey());
			counter++;
		}

		counter = 0;

		//keep selected top species as it is and copy them to next generation list
		int topSpecies = (int)(TOP_SPECIES_RETAIN * sorteddnaList.size());
		while (counter < topSpecies) {
			List<Gene> network = sorteddnaList.get(counter);
			List<Gene> tempNetwork = new ArrayList<Gene>();
			//copy all genes in existing genome to new genome
			for(Gene copyGene: network){
				if (copyGene instanceof SynapseGene) {
					Gene gene = new SynapseGene((SynapseGene) copyGene);
					tempNetwork.add(gene);
				} else {
					Gene gene = new NeuronGene((NeuronGene) copyGene);
					tempNetwork.add(gene);
				}
			}

			nextGenerationDnaList.add(tempNetwork);
			counter++;
		}

		//produce equal number of crossover networks as the input network list  
		while (networkCounter < sorteddnaList.size()) {

			counter = 0;

			//select two random networks for cross over
			int network1Index = (int) generateRandomNumber(0, sorteddnaList.size() - 1);
			int network2Index = (int) generateRandomNumber(0, sorteddnaList.size() - 1);

			//discard if same network is selected twice
			while(network1Index == network2Index)
			{
				network2Index = (int) generateRandomNumber(0, sorteddnaList.size() - 1);
			}

			List<Gene> network1 = sorteddnaList.get(network1Index);
			List<Gene> network2 = sorteddnaList.get(network2Index);
			List<Gene> crossOverNetwork = new ArrayList<Gene>();

			//generate a random cross over index
			int crossOverIndex = (int) generateRandomNumber(0, network1.size() - 1);

			//copy genes from first network until cross over index is reached
			while (counter <= crossOverIndex) {
				Gene copyGene = network1.get(counter);

				if (copyGene instanceof SynapseGene) {
					Gene gene = new SynapseGene((SynapseGene) copyGene);
					crossOverNetwork.add(gene);
				} else {
					Gene gene = new NeuronGene((NeuronGene) copyGene);
					crossOverNetwork.add(gene);
				}
				counter++;
			}

			//copy genes from second network after cross over index until all genes are copied
			while (counter < network2.size()) {				
				Gene copyGene = network2.get(counter);
				if (copyGene instanceof SynapseGene) {
					Gene gene = new SynapseGene((SynapseGene) copyGene);
					crossOverNetwork.add(gene);
				} else {
					Gene gene = new NeuronGene((NeuronGene) copyGene);
					crossOverNetwork.add(gene);
				}

				counter++;
			}

			//add new crossover network to next generation dna list
			nextGenerationDnaList.add(crossOverNetwork);
			networkCounter++;
		}

	}

	/**
	 * This method implements functionality to loop through 
	 * synapseMap of network and randomize weights
	 * 
	 * @param	neuralNetwork	Input neural network to create randomize weights
	 */	
	public void randomizeWeights(NeuralNetwork neuralNetwork) {
		for (Synapse synapse : neuralNetwork.getSynapseMap().values())
			synapse.setSynapticWeight(generateRandomNumber(-1, 1));
	}

	/**
	 * This method implements mutate functionality which takes 
	 * 50 percent newly generated genomes and create same number of new genomes
	 */	
	public void mutate() {

		List<List<Gene>> nextTempGenerationDnaList = deepCopyGenome(getNextGenerationDnaList());
		for (List<Gene> genome : getNextGenerationDnaList()) {
			for (Gene synapseGene : genome) {
				if (synapseGene instanceof SynapseGene) {
					double toss = Math.random();
					//flip a coin and mutate synaptic weight if value is > threshold
					if (toss >= SYNAPTIC_WEIGHT_MODIFICATION_THRESHOLD) {
						synapseGene.mutateMyself();
					}
				}
			}
		}
		getNextGenerationDnaList().addAll(nextTempGenerationDnaList);
	}

	/**
	 * This method implements deep copy functionality which takes genome list as input
	 * and copies it into new genome list
	 * 
	 * @param nextGenerationDnaList		input genome list to copy 
	 * 
	 * @return nextTempGenerationDnaList output copied gene list
	 */	
	public List<List<Gene>> deepCopyGenome(List<List<Gene>> nextGenerationDnaList) {

		List<List<Gene>> nextTempGenerationDnaList = new ArrayList<List<Gene>>();

		for (List<Gene> genome : nextGenerationDnaList) {
			List<Gene> nextGenomeList = new ArrayList<Gene>();
			for (Gene copyGene : genome) {
				if (copyGene instanceof SynapseGene) {
					Gene gene = new SynapseGene((SynapseGene) copyGene);
					nextGenomeList.add(gene);
				} else {
					Gene gene = new NeuronGene((NeuronGene) copyGene);
					nextGenomeList.add(gene);
				}
			}
			nextTempGenerationDnaList.add(nextGenomeList);
		}

		return nextTempGenerationDnaList;
	}

	/**
	 * Utility method to generate random number between two numbers
	 * 
	 * @param a			lower limit
	 * @param b			upper limit
	 * 
	 * @return	double 		random number between a and b
	 */	
	public static double generateRandomNumber(int a, int b) {
		return Math.random() * Math.abs(a - b) + Math.min(a, b);
	}

	/**
	 * Getter method to get network fitness map
	 * 
	 * @return networkFitnessMap	network fitness map
	 */
	public Map<List<Gene>, Double> getNetworkFitnessMap() {
		return networkFitnessMap;
	}

	/**
	 * Setter method to set network fitness map
	 * 
	 * @param networkFitnessMap	network fitness map
	 */	
	public void setNetworkFitnessMap(Map<List<Gene>, Double> networkFitnessMap) {
		this.networkFitnessMap = networkFitnessMap;
	}

}
