package Part1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import javax.swing.JFrame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/*
 * Window class is the GUI of our program, which puts the map and other components together
 */ 

public class Window extends JFrame {
		
	private static final long serialVersionUID = 1L;
	private Container contentPane;
	private Map mapObject;
	
	public static int origoX = 0;			// NOT DONE! The current offset of the windows top left position
	public static int origoY = 0;			// NOT DONE! The current offset of the windows top left position
	
	public static int windowSize = 500; 	// NOT DONE! Will take the dynamic ACTUAL size of the window
	public static int zoomFactor = 1;   	// NOT DONE! Will change according to the ACTUAL current zoom factor

	public Window(){
		super("Better than apple maps");
	}
	/**
	 * Creates the GUI
	 */
	private void makeFrame(){;		
		addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent evt) {
            	//Component c = (Component)evt.getSource();
            	int w = getWidth();
				int h = getHeight();
				
				if(w < h) windowSize = w;
			    else windowSize = h;
            	//System.out.println(windowSize);
            	mapObject.getMapTestMethod();
            	//frame.repaint();
            }
         });
		
        contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());	       
        
        mapObject = new Map();
        contentPane.add(mapObject, BorderLayout.CENTER);
        repaint();
        
        contentPane.setBackground(Color.WHITE);
        pack();
        setSize(500, 500);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args){
		Window testWindow = new Window();
		testWindow.makeFrame();
	}
}
