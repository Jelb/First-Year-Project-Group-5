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
}

//Can draw lines from RoadSegment[] 
//where RoadSegment includes from(x,y) to(x,y) and colour
//Pixel vs coordninate problem
//Centre x,y coordinate
// Scaling/rezising
