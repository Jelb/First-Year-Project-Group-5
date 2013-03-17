package Part1;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * Window class is the GUI of our program, which puts the map and other components together
 */ 

public class Window {
		
	private JFrame frame;
	private Container contentPane;
	private Map mapObject;
	private JPanel drawnMap;

	public Window(){
		
	}
	/**
	 * Creates the GUI
	 */
	private void makeFrame(){
		frame = new JFrame("Better than apple maps");
		
        contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());	       
        
        mapObject = new Map();
        mapObject.TESTdrawSegments();
        drawnMap = mapObject.getMap();              
        
        contentPane.add(drawnMap, BorderLayout.CENTER);
        
        contentPane.setBackground(Color.WHITE);
        frame.pack();
        //frame.setMinimumSize(new Dimension(1250, 700));
        frame.setSize(1300, 700);
        frame.setVisible(true);
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
	
	public static void main(String[] args){
		Window testWindow = new Window();
		testWindow.makeFrame();
	}
}
