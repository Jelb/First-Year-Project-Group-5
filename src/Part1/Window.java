package Part1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import javax.swing.JFrame;

/*
 * Window class is the GUI of our program, which puts the map and other components together
 */ 

public class Window {
		
	private JFrame frame;
	private Container contentPane;
	private Map mapObject;
	
	public static int origoX = 0;			// NOT DONE! The current offset of the windows top left position
	public static int origoY = 0;			// NOT DONE! The current offset of the windows top left position
	
	public static int windowSize = 500; 	// NOT DONE! Will take the dynamic ACTUAL size of the window
	public static int zoomFactor = 1;   	// NOT DONE! Will change according to the ACTUAL current zoom factor

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
    
        contentPane.add(mapObject, BorderLayout.CENTER);
        frame.repaint();
        
        contentPane.setBackground(Color.WHITE);
        frame.pack();
        //frame.setMinimumSize(new Dimension(1250, 700));
        frame.setSize(1300, 700);
        frame.setVisible(true);
	}
	
	public static void main(String[] args){
		Window testWindow = new Window();
		testWindow.makeFrame();
	}
}
