package Part1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JFrame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

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
		/**
		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent event) {
				
				int w = frame.getWidth();
				int h = frame.getHeight();
				
				if(w < h) windowSize = w;
			    else windowSize = h;
				
				System.out.println(windowSize);	
				contentPane.removeAll();
				mapObject = new Map();
				
				contentPane.add(mapObject, BorderLayout.CENTER);
			    
			    frame.repaint();
			  }
		});*/
		
		frame.addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent evt) {
            	//Component c = (Component)evt.getSource();
            	int w = frame.getWidth();
				int h = frame.getHeight();
				
				if(w < h) windowSize = w;
			    else windowSize = h;
            	//System.out.println(windowSize);
            	mapObject.getMapTestMethod();
            	//frame.repaint();
            }
         });
		
        contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());	       
        
        mapObject = new Map();
        contentPane.add(mapObject, BorderLayout.CENTER);
        frame.repaint();
        
        contentPane.setBackground(Color.WHITE);
        frame.pack();
        //frame.setMinimumSize(new Dimension(1250, 700));
        frame.setSize(500, 500);
        frame.setVisible(true);
	}
	
	public static void main(String[] args){
		Window testWindow = new Window();
		testWindow.makeFrame();
	}
}
