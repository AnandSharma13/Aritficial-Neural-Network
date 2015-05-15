package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import model.NeuralNetwork;
import model.Neuron;
import model.Synapse;
import static view.Constants.EDGE_WIDTH_FACTOR;

public class NodeFactory {

	
	//Instead of passing everything around, this seems cleaner, as long as it's handled carefully.
	private int x;
	private int y;
	private double radius;
	
	private Neuron neuron;
	
	private ImageIcon neuronIcon;
	private Image neuronImage;
	
	//Needs reference to Neuron and Synapse maps and a place to draw
	private NeuralNetwork network;
	private JPanel myPanel;
	
	public NodeFactory(NeuralNetwork network, JPanel panel) {
		this.network = network;
		this.myPanel = panel;
	}
	
	
	//uses other functions to draw the approriate representation of the Neuron
	public void drawNode(Neuron neuron, Color color, Graphics2D g2d) {		
		setCoordinates(neuron);
		
		drawNeuron(color, g2d);
		if (network.isLabeled()) {
			drawNeuronLabel(g2d);
		}
		
		//add arrow head image at target neuron
		drawNeuronIcon(g2d);
	}
	
	
	
	//uses other functions to draw the approriate representation of the Neuron
	public void drawNode(Neuron neuron, Graphics2D g2d) {
		this.neuron = neuron;
		
		setCoordinates(neuron);
		
		drawNeuron(g2d);
		
		if (network.isLabeled()) {
			drawNeuronLabel(g2d);
		}
		
		//add arrow head image at target neuron
		drawNeuronIcon(g2d);
	}
	
	public void setCoordinates(Neuron neuron) {
		this.neuron = neuron;
		x = neuron.getX();
		y = neuron.getY();
		radius = neuron.getRadius();
	}
	
	
	
		
	//draws a neuron of the specified color using the current coordinate values in NodeFactory
	public void drawNeuron(Color color, Graphics2D g2d) {
		g2d.setColor(color);
		g2d.fillOval((int) (x-radius), (int) (y-radius), (int) (2*radius), (int) (2*radius));
	}

	

	//draws a neuron of the specified color using the current coordinate values in NodeFactory
	//and the state of activation of the given neuron
	public void drawNeuron(Graphics2D g2d) {
		// if activated
		if (neuron.getConcentration() >= neuron.getThreshold()) {
			// draw neuron with different color
			if(neuron.getLayerType().contains("O") )
			{
				drawNeuron(Color.GREEN, g2d);
			}
			else 
			{
				if (neuron.isGasEmitter()){
					drawNeuron(neuron.getGasColor(), g2d);
				}
				else {
					drawNeuron(Color.YELLOW, g2d);
				}	
			}		
		} 
		else {
			drawNeuron(Color.RED, g2d);
		}
	}
	
	//turns a line or curve into an arrow
	public void drawNeuronIcon(Graphics2D g2d) {
			
	}
	
	//draws the label in appropriate location based on curvature and direction of synapse
	public void drawNeuronLabel(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		g2d.drawString(neuron.getNeuronID(), x-8, y+4);

		//for debugging using visualization
//		String str = String.valueOf(neuron.getConcentration());
//		g2d.drawString(str, x-8, y-10);
//		str = String.valueOf(neuron.getReceptor().getActivationConcentration());
//		g2d.drawString(str, x-8, y-20);
//		str = String.valueOf(neuron.getReceptor().getBuiltUpConcentrations().get("S"));
//		g2d.drawString(str, x-8, y-30);
	}
	
}
