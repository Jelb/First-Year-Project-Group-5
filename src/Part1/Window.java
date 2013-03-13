package Part1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * Window class is the GUI of our program, including drawing of the map.
 */ 

public class Window {
	
	private RoadSegment[] segmentArray;
	private JFrame frame;
	private Container contentPane;
	private JPanel map;

	public Window(){
		frame = new JFrame("Better than apple maps");
		
        contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());
		
        drawMap();
        contentPane.add(map, BorderLayout.CENTER);
        
        contentPane.setBackground(Color.WHITE);
        frame.pack();
        frame.setVisible(true);
	}
	
	private JPanel drawMap(){
	
		return map;
	}
	
	private void redrawMap(){
	
	}
	
	/**
	 * Calculates the pixel coordinate of a given geo coordinate NOT DONE!
	 * 
	 * @param coord The geo coordinate
	 * @return 		The pixel placement of said coordinate
	 */
	private int geoToPixel(double coord) {
		return (int) Math.round(coord);
	}

	/**
	 * Calculates the geo coordinate of a given pixel NOT DONE!
	 * 
	 * @param coord The pixel
	 * @return 		The geo coordinate of said pixel
	 */	
	private double pixelToGeo(int coord) {
		return (double) coord;
	}
	
}

//Can draw lines from RoadSegment[] 
//where RoadSegment includes from(x,y) to(x,y) and colour
//Pixel vs coordninate problem
//Centre x,y coordinate
// Scaling/rezising
