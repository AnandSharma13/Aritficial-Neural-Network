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

public class EdgeFactory {

	//Flyweight factory for curved and straight arrows TODO  Will hope to add flyweight functionality later
	//but first we'll just draw the edges and get them looking right.
	private ArrayList<Arc2D> curves = new ArrayList<Arc2D>();
	private ArrayList<Line2D> lines = new ArrayList<Line2D>();
	
	
	//Instead of passing everything around, this seems cleaner, as long as it's handled carefully.
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	
	//the unit vector pointing from the source to the target.
	private double unitX;
	private double unitY;
	
	private ImageIcon arrowheadIcon;
	private Image arrowheadImage;
	
	private Synapse synapse;
	
	//Needs reference to Neuron and Synapse maps and a place to draw
	private NeuralNetwork network;
	private JPanel myPanel;
	
	public EdgeFactory(NeuralNetwork network, JPanel panel) {
		this.network = network;
		this.myPanel = panel;
	}
	
	
	//uses other functions to draw the approriate representation of the Synapse
	public void drawEdge(Synapse synapse, Color color, Graphics2D g2d) {
		this.synapse = synapse;
		Neuron sourceNeuron = network.getNeuronMap().get(synapse.getSourceNeuron());
		Neuron targetNeuron = network.getNeuronMap().get(synapse.getTargetNeuron());
		
		setEdgeEndpoints(sourceNeuron, targetNeuron);

		//draw input and output edges
		if(sourceNeuron.getLayerType().equals("I"))
		{
			int x = sourceNeuron.getX();
			int y = sourceNeuron.getY();
			
			g2d.setStroke(new java.awt.BasicStroke(1));
			g2d.setColor(Color.BLACK);
			g2d.drawLine(x-50, y, x, y);

		}
		if(targetNeuron.getLayerType().equals("O"))
		{
			int x = targetNeuron.getX();
			int y = targetNeuron.getY();
			g2d.setStroke(new java.awt.BasicStroke(1));
			g2d.setColor(Color.BLACK);
			g2d.drawLine(x, y, x+50, y);

		}
		
		
		
		//draw Synapse
		if (needsCurvature(synapse)) {
			drawCurve(color, g2d);
		}
		else {
			drawLine(color, g2d);
		}
		if (network.isLabeled()) {
			drawSynapseLabel(synapse, g2d);
		}
		
		//add arrow head image at target neuron
		drawArrowHead(g2d);
	}
	
	
	
	
	
	
	// will return true if the drawn synapse needs to be curved
	public boolean needsCurvature(Synapse synapse) {
		//TODO
		//check for whether another synapse is connected in the opposite direction
		return false;
	}
	
	//either builds and draws a new curve, or takes an old curve and redraws it in a new location
	public void drawCurve(Color color, Graphics2D g2d) {
		//check if arc already exists in list
		//then adjust coordinates and draw
		
		
		//if it doesn't exist
		//find points for arc
		//draw arc
		//store in list
		//TODO
	}
	
	//either builds and draws a new line, or takes an old line and redraws it in a new location
	public void drawLine(Color color, Graphics2D g2d) {
		
		Color originalColor = g2d.getColor();
		BasicStroke originalStroke = (BasicStroke) g2d.getStroke();
		g2d.setColor(color);
		g2d.setStroke(new BasicStroke((float) (EDGE_WIDTH_FACTOR * Math.abs(synapse.getSynapticWeight())) ));
		g2d.drawLine(x1,y1,x2,y2);
		g2d.setStroke(originalStroke);
		g2d.setColor(originalColor);
	}
	
	
	//turns a line or curve into an arrow
	public void drawArrowHead(Graphics2D g2d) {
		
		
		//get the arrowhead image
		ClassLoader cl = getClass().getClassLoader();
		arrowheadIcon = new ImageIcon(cl.getResource(Constants.ImageFilePaths.RED_TRIANGLE.getPath()));
		//arrowheadImage = arrowheadIcon.getImage();
		
		 
		 arrowheadImage = arrowheadIcon.getImage();
		
		//translate to the intersection of the synapse with the target
		g2d.translate(x2, y2);
		//rotate the image to the slope of the chord that connects the two neurons' centers
		// positive because we are rotating the coordinate system
		g2d.rotate(Math.atan2(y2-y1,x2-x1) - Math.PI / 2 );
        //move to corner of where image will be
        g2d.translate(-1 * arrowheadIcon.getIconWidth() / 2, -1 * arrowheadIcon.getIconHeight());
        //draw image at origin
	    g2d.drawImage(arrowheadImage, 0,  0, myPanel);
	    //translate back to (x2,y2)
	    g2d.translate(arrowheadIcon.getIconWidth() / 2, arrowheadIcon.getIconHeight());
	    //rotate back
	    g2d.rotate(-1 * Math.atan2(y2-y1,x2-x1) + Math.PI / 2 );
	    //translate back
	    g2d.translate(-x2, -y2);
	}
	
	//finds the intersections of the boundaries of two neurons with the line that connects their centers
	//sets the local variables to those two points (x1,y1) for the source neuron, (x2,y2) for the target neuron
	public void setEdgeEndpoints(Neuron source, Neuron target) {
		int sourceX = source.getX();
		int sourceY = source.getY();
		int targetX = target.getX();
		int targetY = target.getY();
		
		//distance from source to target
		double distance = Math.sqrt( Math.pow((targetX - sourceX), 2) + Math.pow((targetY - sourceY), 2) );
		
		//find the unit vector pointing from source to target
		unitX = (targetX - sourceX) / distance;		
		unitY = (targetY - sourceY) / distance;
		
		//find and set where the line intersects the circle near the source
		x1 = sourceX + (int) (source.getRadius() * unitX);
		y1 = sourceY + (int) (source.getRadius() * unitY);
		//and near the target (pointing toward the source)
		x2 = targetX - (int) (target.getRadius() * unitX);
		y2 = targetY - (int) (target.getRadius() * unitY);				
	}
	
	//draws the label in appropriate location based on curvature and direction of synapse
	public void drawSynapseLabel(Synapse synapse, Graphics2D g2d) {
		Neuron sourceNeuron = network.getNeuronMap().get(
				synapse.getSourceNeuron());
		Neuron targetNeuron = network.getNeuronMap().get(
				synapse.getTargetNeuron());
		int x1 = sourceNeuron.getX();
		int y1 = sourceNeuron.getY();
		int x2 = targetNeuron.getX();
		int y2 = targetNeuron.getY();
		int x = 0;
		int y = 0;
		if (Math.abs(x1 - x2) > Math.abs(y1 - y2)) {
			x = (int) Math.ceil(((x1 + x2) / 2) - Math.ceil(0.5 * 16)); 
				// magic numbers: 1 is the width of the string
			y = (int) Math.ceil(((y1 + y2) / 2) - 7); 
				// magic numbers: 7 is the height of the string
			y -= (int) Math.floor(5 * Math.abs((y1 - y2) / (x1 - x2))); 
				// adjust for the slope of the line
			y -= ((BasicStroke) g2d.getStroke()).getLineWidth(); 
				// adjust for the thickness of the line
		} else {
			if ((x1 - x2) >= (y1 - y2)) {
				x = (int) Math.ceil(((x1 + x2) / 2) + 7); // 7 is just extra
				y = (int) Math.ceil(((y1 + y2) / 2) - Math.ceil(0.5 * 7));
					// magic numbers: 7 is the height of the string
				y -= ((BasicStroke) g2d.getStroke()).getLineWidth();
					// adjust for the thickness of the line
				x += ((BasicStroke) g2d.getStroke()).getLineWidth();
			} else {
				x = (int) Math.ceil(((x1 + x2) / 2) - 7 - 16); 
					// 7 is extra and 16 is the width of the string
				y = (int) Math.ceil(((y1 + y2) / 2) - Math.ceil(0.5 * 7));
					// magic numbers: 7 is the height of the string
				y -= ((BasicStroke) g2d.getStroke()).getLineWidth(); 
					// adjust for the thickness of the line
				x -= ((BasicStroke) g2d.getStroke()).getLineWidth();
			}
		}
		g2d.drawString(String.format("%.3g%n", synapse.getSynapticWeight()), x , y);
	}
	
}
