package resize_virker;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

/*
 * Window class is the GUI of our program, which puts the map and other components together
 */ 

public class Window extends JFrame {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Window instance;
	private static Container contentPane;
	private static boolean initializing = true;
	
	public static int offsetX = 0;			// NOT DONE! The current offset of the windows top left position 
	public static int offsetY = 0;		// NOT DONE! The current offset of the windows top left position
	
	public static int windowSize = 760; 		// NOT DONE! Will take the dynamic ACTUAL size of the window
	public static double zoomFactor = 0.75;   	// NOT DONE! Will change according to the ACTUAL current zoom factor

	/**
	 * Constructor for the window class.
	 */
	private Window(){
		super("Better than apple maps");
	}

	/**
	 * Checks if an instance of the class is created.
	 * If not a new is created.
	 * 
	 * @return The current instance of window. If no such a new i created and returned.
	 */
	public static Window use() {
		if(instance == null) {
			instance = new Window();
			instance.makeFrame();
		}
		return instance;
}
	
	/**
	 * The method is used to set up the initial instance of the window.
	 * After setting the dimension and the layout a splash-screen
	 * is displayed while the program is loading.
	 * 
	 * @throws IOException 
	 */
	private void makeFrame() {		
		
		contentPane = getContentPane();
		contentPane.setPreferredSize(new Dimension(1024,640)); //Sets the dimension on the content pane.		
        contentPane.setLayout(new BorderLayout()); //Sets the layout manager for the content pane.

        // Showed if the background image is unable to be loaded.
        JLabel background = new JLabel("<html><center><b>Loading image could not load.<br>The map is loading...</b></html>", JLabel.CENTER);
		try {
			background = new JLabel(new ImageIcon(ImageIO.read(new File("splash.jpg"))));
			throw new IOException();
		} catch (IOException e) {

		}
        contentPane.add(background);
        setResizable(false);
        pack();
        //Used to center the application on the screen at launch.
		setLocation((int)((Toolkit.getDefaultToolkit().getScreenSize().getWidth() - getWidth())/2),
					(int)((Toolkit.getDefaultToolkit().getScreenSize().getHeight() - getHeight())/2));
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Adds the needed listeners to the window instance.
	 */
	private void addListeners() {
		addKeyListener(new MKeyListener());
		addComponentListener(new resizeListener());
		addMouseListener(new mouseZoom());
	}
	
	/**
	 * Redraws the map when its content has changed or 
	 * the window has been resized. 
	 */
	public void updateMap() {
		contentPane.removeAll();
		contentPane.add(Map.use(), BorderLayout.CENTER);
		System.out.println("updateMap is called");
		repaint();
		validate();
		if(initializing){
			setResizable(true);
			initializing = false;
			addListeners();
		}
	}
	
	/**
	 * Setter method for the zoom factor.
	 * 
	 * @param z The new zoom factor to be used on the map.
	 */
	public void setZoomFactor(double z){
		zoomFactor = z;
	}
	
	/**
	 * Getter method for the zoom factor.
	 * 
	 * @return The current zoom factor for the map.
	 */
	public double getZoomFactor(){
		return zoomFactor;
	}
	
	/**
	 * Adds a key listener used to move around the map.
	 * 
	 * @author Tom (TMCH@ITU.DK)
	 */
	class MKeyListener extends KeyAdapter {
		/**
		 * Adds a key listener to each of the arrow keys.
		 */
		public void keyPressed(KeyEvent event) {

			if(event.getKeyCode() == KeyEvent.VK_UP) {
				System.out.println("Up pressed");
				offsetY += -10000;
			}
			if(event.getKeyCode() == KeyEvent.VK_DOWN) {
				System.out.println("Down pressed");
				offsetY += 10000;
			}
			if(event.getKeyCode() == KeyEvent.VK_LEFT) {
				System.out.println("Left pressed");
				offsetX += 10000;
			}
			if(event.getKeyCode() == KeyEvent.VK_RIGHT) {
				System.out.println("Right pressed");
				offsetX += -10000;
			}

			WindowHandler.calculatePixels();
			repaint();
		}
	}
	
	/**
	 * Adds a listener to the window instance.
	 * The listener recalculates the position of each edge so 
	 * that it fits the screen.
	 * 
	 * @author Jonas (JELB@ITU.DK)
	 */
	public class resizeListener extends ComponentAdapter {
		public void componentResized(ComponentEvent evt) {
			if(Map.use().getRoadSegments() != null)
			Map.use().updatePix();
			repaint();
		}
	}
	
	
	/**
	 * Adds a mouse listener used for "box zooming" on the map.
	 * 
	 */
	public class mouseZoom extends MouseAdapter {
		private int pressedX;
		private int pressedY;
		private int releasedX;
		private int releasedY;
		
		/**
		 * Records which pixel the mouse is pressed on.
		 */
		public void mousePressed(MouseEvent e){	
			pressedX = e.getX();
			pressedY = e.getY();
			
			System.out.println("Pressed X : "+ pressedX);
			System.out.println("Pressed Y : "+ pressedY);
		}
		
		/**
		 * Records which pixel the mouse is released on.
		 */
		public void mouseReleased(MouseEvent e){
			releasedX = e.getX();
			releasedY =  e.getY();
			System.out.println("Released X : "+ releasedX);
			System.out.println("Released Y : "+ releasedY);
			
			//WindowHandler.search((int)pressedX, (int)releasedY, (int)pressedX, (int)releasedY);
			//search(int x1, int x2, int y1, int y2)
			WindowHandler.search(pressedX, releasedX, pressedY, releasedY);
			
			
			WindowHandler.calculatePixels();
			//RoadSegment.setMapSize(releasedX, pressedY, pressedX, releasedY);
			//Map.use().updatePix(); //Problem: updatePix er afhængig af vinduets nuværende størrelse*/
			updateMap();
		}
	}
}
