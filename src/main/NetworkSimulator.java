package main;

import java.awt.EventQueue;

import view.FileInputFrame;
import controller.NetworkSimulationController;
import model.NetworkBuilder;
import model.NeuralNetwork;

public class NetworkSimulator
{
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				// create view
				FileInputFrame fileInputScreen = new FileInputFrame();

				// create model
				NeuralNetwork neuralNetwork = new NeuralNetwork();

				// create controller
				NetworkSimulationController networkSimulationController = new NetworkSimulationController(fileInputScreen, neuralNetwork);

				fileInputScreen.setVisible(true);
			}
		});
	}
}
