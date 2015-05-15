package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import view.Constants.ModFunctionTarget;

public class Polynomial {
	private HashMap<String, Double> coefficients = new HashMap<String, Double>();
	private HashMap<String, Double> powers = new HashMap<String, Double>();
	private String polyID;
	
	private ModFunctionTarget functionTarget;
	
	
	public double evaluate(HashMap<String, Double> inputs) {
		double x = 0;
		
		Set inputsEntrySet = inputs.entrySet();
		Iterator inputIterator = inputsEntrySet.iterator();
		
		while (inputIterator.hasNext()) {
			Map.Entry currentEntry = (Map.Entry) inputIterator.next();
			String currentID = (String) currentEntry.getKey();
			
			
			if( powers.keySet().contains(currentID) && coefficients.keySet().contains(currentID)) {
				x += coefficients.get(currentID) * Math.pow(inputs.get(currentID), powers.get(currentID));
			}
		}
		
		return x;
	}



	public HashMap<String, Double> getCoefficients() {
		return coefficients;
	}



	public void setCoefficients(
			HashMap<String, Double> coefficients) {
		this.coefficients = coefficients;
	}

	
	public HashMap<String, Double> getPowers() {
		return powers;
	}



	public void setPowers(HashMap<String, Double> powers) {
		this.powers = powers;
	}



	public String getPolyID() {
		return polyID;
	}



	public void setPolyID(String polyID) {
		this.polyID = polyID;
	}



	public ModFunctionTarget getFunctionTarget() {
		return functionTarget;
	}



	public void setFunctionTarget(ModFunctionTarget functionTarget) {
		this.functionTarget = functionTarget;
	}

}
