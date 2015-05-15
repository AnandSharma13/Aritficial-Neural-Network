package model;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import model.NeuralNetwork;
import model.Neuron;
import model.Synapse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import view.Constants.ActivationFunction;
import view.Constants.ModFunctionTarget;
import view.Constants.NetworkType;
import view.Constants.VisualizationModes;

/** This class is used to read input files and build neural network
 * based on input files. 
 */
public class NetworkBuilder
{
	/** neural network instance which needs to be built*/
	private NeuralNetwork neuralNetwork;

	/** map of input signals based on time instance (Map structure : Time Instant, List of Input Signals) */
	private Map<Integer, List<Integer>> inputTimeSignalMap;

	/** map of expected output signals based on time instance (Map structure : Time Instant, List of Output Signals) */
	private Map<Integer, List<Integer>> outputTimeSignalMap;

	/** red color value used to paint appropriate view contents */
	private float r = 0;

	/** green color value used to paint appropriate view contents */
	private float g = 0;

	/** blue color value used to paint appropriate view contents */
	private float b = 0;

	/**
	 * builds a neural network by reading files
	 * 
	 * @param fileName name of the excel workbook that has all information to build a network
	 * @param neuralNetwork neuralNetwork instance that gets built
	 */
	@SuppressWarnings("resource")
	public void buildNetwork(String fileName, NeuralNetwork neuralNetwork)
	{
		String path = this.getClass().getClassLoader().getResource(fileName).getFile();
		this.neuralNetwork = neuralNetwork;
		this.outputTimeSignalMap = new HashMap<Integer, List<Integer>>();

		InputStream fis = null;
		try
		{
			fis = new FileInputStream(path);

			// Using XSSF for xlsx format, for xls use HSSF
			Workbook workbook = new XSSFWorkbook(fis);

			int numberOfSheets = workbook.getNumberOfSheets();

			//looping over each workbook sheet
			for (int i = 0; i < numberOfSheets; i++)
			{
				switch(i)
				{
				// sheet 0 - Network data
				case 0 :
					setNetworkParameters(workbook.getSheetAt(i));
					break;

					// sheet 1 - gases data
				case 1:
					buildGasMap(workbook.getSheetAt(i));
					break;

					// sheet 2 - functions data
				case 2:
					buildFunctions(workbook.getSheetAt(i));
					break;

					// sheet 3 - receptors data
				case 3:			
					buildReceptors(workbook.getSheetAt(i));
					break;


					// sheet 4 - neuron data
				case 4 :
					buildNeuronMap(workbook.getSheetAt(i));
					break;

					// sheet 5 - synapses data
				case 5:
					buildSynapsesMap(workbook.getSheetAt(i));
					break;

					// sheet 6 - input-signals data
				case 6:
					// should only work in simulation mode, not evolution mode
					if(inputTimeSignalMap!=null)
						buildInputTimeSignalMap(workbook.getSheetAt(i));
					break;

					// sheet 7 - output-signals data
				case 7:
					// should only work in simulation mode, not evolution mode
					if(inputTimeSignalMap!=null)
						buildOutputTimeSignalMap(workbook.getSheetAt(i));
					break;

				default:
					break;

				}

			}

			fis.close();
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	/**
	 * Getter for inputTimeSignalMap
	 * @return inputTimeSignalMap map of input signals based on time instance (Map structure : Time Instant, List of Input Signals)
	 */
	public Map<Integer, List<Integer>> getInputTimeSignalMap() {
		return inputTimeSignalMap;
	}

	/**
	 * Setter for inputTimeSignalMap
	 * 
	 * @param inputTimeSignalMap sets map of input signals based on time instance (Map structure : Time Instant, List of Input Signals) 
	 */
	public void setInputTimeSignalMap(Map<Integer, List<Integer>> inputTimeSignalMap) {
		this.inputTimeSignalMap = inputTimeSignalMap;
	}


	/**
	 * Sets network wide parameters  using the information on excel sheet
	 * @param sheet Excel sheet containing information of network wide parameters
	 */
	private void setNetworkParameters(Sheet sheet) {
		ArrayList<Cell> column = new ArrayList<Cell>();

		// build the column by iterating over each row
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) 
		{	
			Row row = (Row) rowIterator.next();
			Cell cell = row.getCell(1);
			column.add(cell);
		}

		//populate neuralNetwork with parameters from the column
		Iterator<Cell> columnIterator = column.iterator();
		while (columnIterator.hasNext()) {
			Cell cell = columnIterator.next();
			if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
				if (cell.getRowIndex() == 1) {
					if(cell.getStringCellValue().equalsIgnoreCase("FeedForward")) {
						neuralNetwork.setNetworkType(NetworkType.FEEDFORWARD);
					}
					else if (cell.getStringCellValue().equalsIgnoreCase("Recurrent")) {
						neuralNetwork.setNetworkType(NetworkType.RECURRENT);
					}
				}
				else if (cell.getRowIndex() == 2) {
					if(cell.getStringCellValue().equalsIgnoreCase("Step")) {
						neuralNetwork.setActivationFunction(ActivationFunction.STEP_FUNCTION);
					}
					else if (cell.getStringCellValue().equalsIgnoreCase("LogSig")) {
						neuralNetwork.setActivationFunction(ActivationFunction.LOGARITHMIC_SIGMOID);
					}
				}
				else if (cell.getRowIndex() == 3) {
					if(cell.getStringCellValue().equalsIgnoreCase("Hidden")) {
						neuralNetwork.setMode(VisualizationModes.GAS_HIDDEN);
					}
					else if (cell.getStringCellValue().equalsIgnoreCase("Translucent")) {
						neuralNetwork.setMode(VisualizationModes.TRANSLUCENT_GAS);
					}
					else if (cell.getStringCellValue().equalsIgnoreCase("Rings")) {
						neuralNetwork.setMode(VisualizationModes.GAS_RINGS);
					}
				}
			}

		}


	}
	/**
	 * builds a map of gas id against gas object by reading excel sheet
	 * 
	 * @param sheet Excel sheet that contains information of gases
	 */
	private void buildGasMap(Sheet sheet) 
	{
		Iterator<Row> rowIterator = sheet.iterator();

		//iterating over each row
		while (rowIterator.hasNext()) 
		{
			Row row = (Row) rowIterator.next();

			// Always skip reading first row because it contains headers
			if(row.getRowNum() == 0)
				continue;

			// create one gas per row
			Gas gas = new Gas();

			Iterator<Cell> cellIterator = row.cellIterator();

			//Iterating over each cell (column wise)  in a particular row.
			while (cellIterator.hasNext()) 
			{
				Cell cell = (Cell) cellIterator.next();

				if (Cell.CELL_TYPE_STRING == cell.getCellType()) 
				{
					if (cell.getColumnIndex() == 0) {
						gas.setGasID(cell.getStringCellValue());
					}
					else if(cell.getColumnIndex() == 1)
						gas.setName(cell.getStringCellValue());
					else if (cell.getColumnIndex() == 4)
						gas.setGasDispersionType(cell.getStringCellValue());
				}
				else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType())
					if(cell.getColumnIndex() == 2)
						gas.setPropagationSpeed((int)cell.getNumericCellValue());
					else if(cell.getColumnIndex() == 3)
						gas.setDecayFactor(cell.getNumericCellValue());
					else if(cell.getColumnIndex() == 5)
						r = (float) cell.getNumericCellValue();
					else if(cell.getColumnIndex() == 6)
						g =  (float) cell.getNumericCellValue();
					else if(cell.getColumnIndex() == 7){
						b = (float) cell.getNumericCellValue();
						gas.setColor(r,g,b);
					}

			}

			neuralNetwork.getGasMap().put(gas.getGasID(), gas);
		}

	}


	/**
	 * builds a map of polynomial id against polynomial function by reading excel sheet
	 * 
	 * @param sheet excel sheet containing information of polynomial functions to be built
	 */
	private void buildFunctions(Sheet sheet) {
		//iterating over each row
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) 
		{
			Row row = (Row) rowIterator.next();

			// Always skip reading first row because it contains headers
			if(row.getRowNum() == 0) {
				continue;
			}

			// create one polynomial per row
			Polynomial polynomial = new Polynomial();


			Iterator<Cell> cellIterator = row.cellIterator();

			//Iterating over each cell (column wise)  in a particular row.
			while (cellIterator.hasNext()) 
			{
				Cell cell = (Cell) cellIterator.next();

				if (Cell.CELL_TYPE_STRING == cell.getCellType()) 
				{
					if (cell.getColumnIndex() == 0)
						polynomial.setPolyID(cell.getStringCellValue());
					else if(cell.getColumnIndex() == 1)
						if (cell.getStringCellValue().equalsIgnoreCase("A")) {
							polynomial.setFunctionTarget(ModFunctionTarget.ACTIVATION);
						}
						else if (cell.getStringCellValue().equalsIgnoreCase("P")) {
							polynomial.setFunctionTarget(ModFunctionTarget.PLASTICITY);
						}
						else if(cell.getColumnIndex() % 2 == 0) {
							polynomial.getCoefficients().put(cell.getStringCellValue(), 1.0);
							polynomial.getPowers().put(cell.getStringCellValue(),1.0);
						}


				}
				else if (cell.getColumnIndex() >= 3 && Cell.CELL_TYPE_NUMERIC == cell.getCellType())
					if (cell.getColumnIndex() % 3 == 0)
						polynomial.getCoefficients().put(row.getCell(cell.getColumnIndex() - 1)
								.getStringCellValue(), cell.getNumericCellValue());
					else if(cell.getColumnIndex() % 3 == 1)
						polynomial.getPowers().put(row.getCell(cell.getColumnIndex() - 2)
								.getStringCellValue(), cell.getNumericCellValue());

			}

			neuralNetwork.getFunctionMap().put(polynomial.getPolyID(), polynomial);
		}

	}		

	/**
	 * builds a map of receptor id against receptor instance by reading excel sheet
	 * @param sheet excel sheet containing receptor information
	 */
	private void buildReceptors(Sheet sheet) {


		Iterator<Row> rowIterator = sheet.iterator();

		//iterating over each row
		while (rowIterator.hasNext()) 
		{
			Row row = (Row) rowIterator.next();

			// Always skip reading first row because it contains headers
			if(row.getRowNum() == 0)
				continue;

			// create one receptor per row
			Receptor receptor = new Receptor();

			Iterator<Cell> cellIterator = row.cellIterator();

			//Iterating over each cell (column wise)  in a particular row.
			while (cellIterator.hasNext()) 
			{
				Cell cell = (Cell) cellIterator.next();

				if (Cell.CELL_TYPE_STRING == cell.getCellType()) 
				{
					if (cell.getColumnIndex() == 0)
						receptor.setReceptorID(cell.getStringCellValue());
					else if(cell.getColumnIndex() == 1) {
						receptor.setActivationType(cell.getStringCellValue());
						receptor.getBuiltUpConcentrations().put(receptor.getActivationType(), 0.0);
					}

					else if(cell.getColumnIndex() == 2){
						String gasString = cell.getStringCellValue();
						//get gases from the (String) list and save an ArrayList in the Receptor.
						ArrayList<String> gasList = new ArrayList<String>(Arrays.asList(gasString.split("\\s*,\\s*")));
						receptor.setGasList(gasList);
						for (String gas: gasList) {
							receptor.getBuiltUpConcentrations().put(gas, 0.0);
						}
					}

					else if(cell.getColumnIndex() == 3)
						receptor.setActivationModFunction(neuralNetwork.getFunctionMap().get(cell.getStringCellValue()));
					else if(cell.getColumnIndex() == 4)
						receptor.setPlasticityModFunction(neuralNetwork.getFunctionMap().get(cell.getStringCellValue()));

				}

			}
			if (receptor.getActivationModFunction() != null) {
				neuralNetwork.getReceptorMap().put(receptor.getReceptorID(), receptor);
			}
		}



	}


	/**
	 * builds a map of neuron id against neuron object by reading excel sheet
	 * @param sheet excel sheet that contains neuron information
	 */
	private void buildNeuronMap(Sheet sheet) 
	{
		Iterator<Row> rowIterator = sheet.iterator();

		//iterating over each row
		while (rowIterator.hasNext()) 
		{
			Row row = (Row) rowIterator.next();

			// Always skip reading first row because it contains headers
			if(row.getRowNum() == 0)
				continue;

			// create one neuron per row
			Neuron neuron = new Neuron();
			neuron.setActivationFunction(neuralNetwork.getActivationFunction());

			Iterator<Cell> cellIterator = row.cellIterator();

			//Iterating over each cell (column wise)  in a particular row.
			while (cellIterator.hasNext()) 
			{
				Cell cell = (Cell) cellIterator.next();

				if (Cell.CELL_TYPE_STRING == cell.getCellType()) 
				{
					if (cell.getColumnIndex() == 0)
						neuron.setNeuronID(cell.getStringCellValue());
					else if(cell.getColumnIndex() == 3)
						neuron.setLayerType(cell.getStringCellValue());
					else if(cell.getColumnIndex() == 5)
						neuron.setGasEmitter(cell.getStringCellValue().equalsIgnoreCase("T"));
					else if(cell.getColumnIndex() == 6 && neuron.isGasEmitter()) {
						neuron.setGasType(cell.getStringCellValue());
						Color color = neuralNetwork.getGasMap().get(cell.getStringCellValue()).getColor();
						neuron.setGasColor(color);
					}
					else if(cell.getColumnIndex() == 9)
						neuron.setGasReceiver(cell.getStringCellValue().equalsIgnoreCase("T")?true:false);
					else if(cell.getColumnIndex() == 10) {
						Receptor receptor = neuralNetwork.getReceptorMap().get(cell.getStringCellValue());
						neuron.setReceptor(receptor);

						//and build the gas/receiver neurons map
						if (neuron.isGasReceiver()) {								
							ArrayList<String> gasList = receptor.getGasList();
							for (String gasID : gasList) {

								if (neuralNetwork.getGasReceiverNeuronsMap().keySet().contains(gasID))
									neuralNetwork.getGasReceiverNeuronsMap().get(gasID).add(neuron);
								else
								{
									ArrayList<Neuron> neurons = new ArrayList<Neuron>();
									neurons.add(neuron);
									neuralNetwork.getGasReceiverNeuronsMap().put(gasID, neurons);
								}
							}
						}
					}


				}
				else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
					if (cell.getColumnIndex() == 1)
						neuron.setX((int)cell.getNumericCellValue());
					else if(cell.getColumnIndex() == 2)
						neuron.setY((int)cell.getNumericCellValue());
					else if(cell.getColumnIndex() == 4)
						neuron.setThreshold(cell.getNumericCellValue());
					else if(cell.getColumnIndex() == 8 && neuron.isGasEmitter())
						neuron.setEmmissionRadius(cell.getNumericCellValue());
					else if(cell.getColumnIndex() == 7 && neuron.isGasEmitter())
						neuron.setBaseProduction(cell.getNumericCellValue());
				}

			}

			neuralNetwork.getNeuronMap().put(neuron.getNeuronID(), neuron);

		}

	}


	/**
	 * builds a map of synapse id against synapse object by reading excel sheet
	 * @param sheet excel sheet containing synapse information
	 */
	private void buildSynapsesMap(Sheet sheet)
	{
		Iterator<Row> rowIterator = sheet.iterator();

		List<String> targetNeurons = new ArrayList<String>();

		//iterating over each row
		while (rowIterator.hasNext()) 
		{
			Row row = (Row) rowIterator.next();

			String sourceNeuron = null;

			Iterator<Cell> cellIterator = row.cellIterator();

			//Iterating over each cell (column wise)  in a particular row.
			while (cellIterator.hasNext()) 
			{
				Cell cell = (Cell) cellIterator.next();

				if(Cell.CELL_TYPE_STRING == cell.getCellType())
				{
					//fill the targetNodes list with elements of row 0, starting with cell 1
					if (row.getRowNum() == 0) 
					{
						if(cell.getColumnIndex()>=1)
							targetNeurons.add(cell.getStringCellValue());
					}
					else if(cell.getColumnIndex() == 0)
						sourceNeuron = cell.getStringCellValue();
				}

				// fill synapseMap
				else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType())
				{	
					// valid column indexes are from 1 to targetNodes.size()
					if(cell.getColumnIndex() >=1 &&  cell.getColumnIndex() <= targetNeurons.size() && cell.getNumericCellValue() != 0)
					{
						// create one synapse per weight cell
						Synapse synapse = new Synapse();
						synapse.setSourceNeuron(sourceNeuron);
						synapse.setTargetNeuron(targetNeurons.get(cell.getColumnIndex()-1));
						synapse.setSynapticWeight(cell.getNumericCellValue());

						// setting synapseID as S<SourceNeuron><TargetNeuron> ex. SN1N2, SN2N3
						synapse.setSynapseID("S"+synapse.getSourceNeuron()+synapse.getTargetNeuron());

						// filling the synapse list in source neuron
						Neuron source = neuralNetwork.getNeuronMap().get(sourceNeuron);
						source.getSynapsesList().add(synapse.getSynapseID());

						neuralNetwork.getSynapseMap().put(synapse.getSynapseID(), synapse);
					}
				}
			}
		}

	}


	/**
	 * builds a map of time instant against list of input signals by reading excel sheet
	 * 
	 * @param sheet excel sheet containing input signal information
	 */
	private void buildInputTimeSignalMap(Sheet sheet)
	{
		Iterator<Row> rowIterator = sheet.iterator();

		//iterating over each row
		while (rowIterator.hasNext()) 
		{
			Row row = (Row) rowIterator.next();

			// Always skip reading first row because it contains headers
			if(row.getRowNum() == 0)
				continue;

			// random initialization
			int timeInstant = -1;

			Iterator<Cell> cellIterator = row.cellIterator();

			//Iterating over each cell (column wise)  in a particular row.
			while (cellIterator.hasNext()) 
			{
				Cell cell = (Cell) cellIterator.next();

				if (Cell.CELL_TYPE_NUMERIC == cell.getCellType())

					if (cell.getColumnIndex() == 0)
						timeInstant = (int)cell.getNumericCellValue();

					else
					{
						List<Integer> signalList;

						if(inputTimeSignalMap.get(timeInstant)==null)
							signalList = new ArrayList<Integer>();
						else
							signalList = inputTimeSignalMap.get(timeInstant);

						signalList.add((int)cell.getNumericCellValue());
						inputTimeSignalMap.put(timeInstant, signalList);
					}
			}
		}
	}

	/**
	 * builds a map of time instant against list of output signals by reading excel sheet
	 * @param sheet excel sheet that contains information of expected output signals
	 */
	private void buildOutputTimeSignalMap(Sheet sheet) {
		Iterator<Row> rowIterator = sheet.iterator();

		//iterating over each row
		while (rowIterator.hasNext()) 
		{
			Row row = (Row) rowIterator.next();

			// Always skip reading first row because it contains headers
			if(row.getRowNum() == 0)
				continue;

			// random initialization
			int timeInstant = -1;

			Iterator<Cell> cellIterator = row.cellIterator();

			//Iterating over each cell (column wise)  in a particular row.
			while (cellIterator.hasNext()) 
			{
				Cell cell = (Cell) cellIterator.next();

				if (Cell.CELL_TYPE_NUMERIC == cell.getCellType())

					if (cell.getColumnIndex() == 0)
						timeInstant = (int)cell.getNumericCellValue();

					else
					{
						List<Integer> signalList;

						if(outputTimeSignalMap.get(timeInstant)==null)
							signalList = new ArrayList<Integer>();
						else
							signalList = outputTimeSignalMap.get(timeInstant);

						signalList.add((int)cell.getNumericCellValue());
						outputTimeSignalMap.put(timeInstant, signalList);
					}
			}
		}

	}




}
