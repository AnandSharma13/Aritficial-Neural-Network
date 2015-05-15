package model;

import java.util.ArrayList;
import java.util.HashMap;
import static view.Constants.INITIAL_PLASTICITY;
import static view.Constants.MAXIMUM_PLASTICITY;
import static view.Constants.MINIMUM_PLASTICITY;

public class Receptor {

	private String receptorID;
	private HashMap<String, Double> builtUpConcentrations = new HashMap<String, Double>();
	
	double activationConcentration = 0;
	double plasticity = INITIAL_PLASTICITY;

	private String activationType;
	private Polynomial activationModFunction;
	private Polynomial plasticityModFunction;
	private ArrayList<String> gasList;


	
	
	public void incrementActivationConcentration() {
		activationConcentration = builtUpConcentrations.get(activationType);
	}
	
	public void clearConcentrations() {
		for (String concentration: builtUpConcentrations.keySet()) {
			builtUpConcentrations.put(concentration, 0.0);
		}
	}
	

	
	public void modulateActivationConcentration() {
		activationConcentration = activationConcentration * (1 + activationModFunction.evaluate(builtUpConcentrations)); //add a parameter?
		// activationConcentration = aC * \beta * p(Gas)
	}
	
	public void modulatePlasticity() {
		plasticity = plasticity * (1 + plasticityModFunction.evaluate(builtUpConcentrations)); //add a parameter?
		// plasticity = plasticity * \beta * p(Gas)
		if (plasticity >= MAXIMUM_PLASTICITY) {
			plasticity = MAXIMUM_PLASTICITY;
		}
		if (plasticity <= MINIMUM_PLASTICITY) {
			plasticity = MINIMUM_PLASTICITY;
		}
	}

	public String getReceptorID() {
		return receptorID;
	}

	public void setReceptorID(String receptorID) {
		this.receptorID = receptorID;
	}

	public ArrayList<String> getGasList() {
		return gasList;
	}

	public void setGasList(ArrayList<String> gasList) {
		this.gasList = gasList;
	}

	public Polynomial getActivationModFunction() {
		return activationModFunction;
	}

	public void setActivationModFunction(Polynomial activationModFunction) {
		this.activationModFunction = activationModFunction;
	}

	public Polynomial getPlasticityModFunction() {
		return plasticityModFunction;
	}

	public void setPlasticityModFunction(Polynomial plasticityModFunction) {
		this.plasticityModFunction = plasticityModFunction;
	}

	public double getActivationConcentration() {
		return activationConcentration;
	}

	public void setActivationConcentration(double activationConcentration) {
		this.activationConcentration = activationConcentration;
	}

	public String getActivationType() {
		return activationType;
	}

	public void setActivationType(String activationType) {
		this.activationType = activationType;
	}


	public HashMap<String, Double> getBuiltUpConcentrations() {
		return builtUpConcentrations;
	}

	public void setBuiltUpConcentrations(HashMap<String, Double> builtUpConcentrations) {
		this.builtUpConcentrations = builtUpConcentrations;
	}



	public double getPlasticity() {
		return plasticity;
	}



	public void setPlasticity(double plasticity) {
		this.plasticity = plasticity;
	}

	
}