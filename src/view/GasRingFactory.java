package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import view.Constants.VisualizationModes;
import model.Gas;
import model.GasDispersionUnit;
import model.GasDispersionSlot;
import model.NeuralNetwork;
import model.Neuron;
import static view.Constants.GAS_BOUNDARY_THICKNESS;
import static view.Constants.GAS_RING_WIDTH_PARAMETER;
import static view.Constants.GAS_LEGEND_BOUNDARY_THICKNESS;
import static view.Constants.SIMULATION_FRAME_HEIGHT;
import static view.Constants.SIMULATION_FRAME_WIDTH;
import static view.Constants.BUTTON_PANEL_HEIGHT;
import static view.Constants.GAS_LEGEND_SWATCH_HEIGHT;
import static view.Constants.GAS_LEGEND_SWATCH_WIDTH;

public class GasRingFactory {

	// Neuron Coordinates
	private Neuron neuron;
	private int x;
	private int y;
	private double radius;
	private GasDispersionUnit gasChannel;
	private ArrayList<GasDispersionSlot> channel;
	private double slotSize;
	private Color color;



	// Slot Coordinates
	private GasDispersionSlot slot;

	private ImageIcon neuronIcon;
	private Image neuronImage;

	// Needs reference to Neuron and Synapse maps and a place to draw
	private NeuralNetwork network;
	private JPanel myPanel;

	public GasRingFactory(NeuralNetwork network, JPanel panel) {
		this.network = network;
		this.myPanel = panel;
	}


	// uses other functions to draw the appropriate representation of the gas
	// rings
	public void drawGasRings(Neuron neuron, Graphics2D g2d) {

		setCoordinates(neuron);
		slotSize = neuron.getGasDispersionUnit().getSlotSize();

		for (int i = 0; i < channel.size(); i++) {
			slot = channel.get(i);
			
			if (network.getMode() == VisualizationModes.GAS_RINGS) {	
				drawGasRing(g2d);}
			else if (network.getMode() == VisualizationModes.TRANSLUCENT_GAS) {
				drawTranslucentGas(g2d);
			}
			
			//TODO
			//draw small (black? light gray?) ring around neuron to indicate that it receives the gas.
			
			if (network.isLabeled()) {
				drawGasRingLabel(neuron, g2d);
			}

			// add icon to a gas ring
			drawGasRingIcon(g2d);
		}
	}



	public void drawGasRing(Graphics2D g2d) {

		if (slot.getGasConcentration() != 0) {
			g2d.setStroke(new BasicStroke(GAS_BOUNDARY_THICKNESS));
			double radius = slot.getSlotRadius();
			double slotUpperLimit = radius + slotSize;
			g2d.setColor(Color.BLACK);
			g2d.drawOval((int) (neuron.getX() - slotUpperLimit), (int) (neuron.getY() - slotUpperLimit), (int) (2 * slotUpperLimit), (int) (2 * slotUpperLimit));
			g2d.drawOval((int) (neuron.getX() - radius), (int) (neuron.getY() - radius), (int) (2 * radius), (int) (2 * radius));
			drawInnerRing(slotUpperLimit, g2d);
			
		}
	}
	
	public void drawInnerRing(double slotUpperLimit, Graphics2D g2d)
	{	
		g2d.setColor(color);
		g2d.setStroke(new BasicStroke((float) (slot.getGasConcentration()) *  GAS_RING_WIDTH_PARAMETER));
		slotUpperLimit -= slotSize / 2;
		g2d.drawOval((int) (neuron.getX() - slotUpperLimit), (int) (neuron.getY() - slotUpperLimit), (int) (2 * slotUpperLimit), (int) (2 * slotUpperLimit));
	}
	
	public void drawTranslucentGas(Graphics2D g2d) {
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		int alpha = (int) Math.floor(slot.getGasConcentration() * 255);
		Color translucentColor = new Color(r,g,b,alpha);
		g2d.setColor(translucentColor);
		g2d.setStroke(new BasicStroke((float) slotSize));
		int ringRadius = (int) Math.floor(slot.getSlotRadius() + slotSize);
		g2d.drawOval(neuron.getX() - ringRadius, neuron.getY() - ringRadius, 2 * ringRadius, 2 * ringRadius);
	}
	
	public void setCoordinates(Neuron neuron) {
		this.neuron = neuron;
		x = neuron.getX();
		y = neuron.getY();
		radius = neuron.getRadius();
		gasChannel = neuron.getGasDispersionUnit();
		channel = neuron.getGasDispersionUnit().getGasDispersionSlotList();
		color = neuron.getGasColor();
	}

	// turns a line or curve into an arrow
	public void drawGasRingIcon(Graphics2D g2d) {

	}

	// draws the label in appropriate location based on curvature and direction
	// of synapse
	public void drawGasRingLabel(Neuron neuron, Graphics2D g2d) {

	}

}
