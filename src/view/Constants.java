package view;

public class Constants 
{
	////////////////View////////////////////
	public static final int FRAME_WIDTH = 500;
	public static final int FRAME_HEIGHT = 400;
	public static final int BUTTON_PANEL_HEIGHT = 90;
	
	public static final int SIMULATION_FRAME_WIDTH = 1000;
	public static final int SIMULATION_FRAME_HEIGHT = 850;
	

	public static final double EDGE_WIDTH_FACTOR = 4;
	public static final double EDGE_ARC_CURVATURE = 4;
	public static final double EDGE_ARROW_HEAD_WIDTH = 3;
	public static final double EDGE_ARROW_HEAD_HEIGHT = 7;
	
	public static final float GAS_BOUNDARY_THICKNESS = 1;
	public static final float GAS_RING_WIDTH_PARAMETER = 8;
	
	public static final float GAS_LEGEND_BOUNDARY_THICKNESS = 2;
	public static final int GAS_LEGEND_SWATCH_WIDTH = 30;
	public static final int GAS_LEGEND_SWATCH_HEIGHT = 20;


	public static final double TOP_SPECIES_RETAIN = .25;
	
	public enum ImageFilePaths {
		RED_TRIANGLE("red_triangle.png");

		private String path;

		private ImageFilePaths(String path)
		{
			this.path = "img/" + path;
		}
		
		
		public String getPath()
		{
			return this.path;
		}
	}

	public enum VisualizationModes {
		TRANSLUCENT_GAS, GAS_RINGS, GAS_HIDDEN;
	}
	
	//////////////////Model////////////////
	public static final double LOGARITHMIC_SIGMOID_PARAMETER = 5;
	public static final double HEBBIAN_LEARNING_PARAMETER = 1;
	public static final double HEBBIAN_DECAY_PARAMETER = 1;
	public static final double INITIAL_PLASTICITY = 0.1;
	public static final double MAXIMUM_PLASTICITY = 0.7;
	public static final double MINIMUM_PLASTICITY = 0.0;
	
	public static final double MAXIMUM_SYNAPTIC_WEIGHT = 5;
	public static final double MINIMUM_SYNAPTIC_WEIGHT = -5;
	
	public static final double SYNAPTIC_WEIGHT_MODIFICATION_THRESHOLD = 0.5;

	public static final int SYNAPTIC_WEIGHT_UPPER_BOUND = 1;
	
	public static final int SYNAPTIC_WEIGHT_LOWER_BOUND = -1;
	
	public static final int BREEDING_THRESHOLD = 100;
	
	public static final double GOAL_FITNESS = 0.99;
	
	public static final int NEURON_CIRCLE_RADIUS = 15;
	
	public enum ActivationFunction {
		STEP_FUNCTION, LOGARITHMIC_SIGMOID;
	}
	
	public enum ModFunctionTarget{
		ACTIVATION, PLASTICITY;
	}

	
	///////////Controller///////////////////////
	public enum NetworkType
	{
		FEEDFORWARD, RECURRENT;
	}
	

	public enum FeedForwardNetworkMode
	{
		TIMED, NOT_TIMED;
	}

	// top K species that would pass onto the next generation
	public static int K; 		

	// Timer delay for period between each time tick
	public static final int MIN_TIMER_DELAY = 0;
	public static final int AVG_TIMER_DELAY = 2500;
	public static final int MAX_TIMER_DELAY = 5000;

	
	////////////// Genetic Algorithm//////////////////////
	
	// top percentage of generation passed directly on to next generation
	public static final double PROPORTION_SAVED = 0.1;
	// bottom percentage of generation eliminated before crossover and mutation
	public static final double PROPORTION_KILLED = 0.6;
	// top percentage of survivors available for crossover
	public static final double PROPORTION_CROSSOVER = 1;
	
	public static final int PERCENTAGE_GOOD_SPECIES = 40;
	
	public static final int POPULATION_SIZE = 100; 
	
	public static final int MINIMUM_CROSSOVERS = 1;
	public static final int MAXIMUM_CROSSOVERS = 3;
	
}
