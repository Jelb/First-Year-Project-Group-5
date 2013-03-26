package resize_virker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/*
 * Window class is the GUI of our program, which puts the map and other components together
 */ 

public class Window extends JFrame {
		
	private static final long serialVersionUID = 1L;
	private static Window instance;
	private static Container contentPane;
	private static boolean initializing = true;
	
	public static int offsetX = 0;			// NOT DONE! The current offset of the windows top left position 
	public static int offsetY = 0;		// NOT DONE! The current offset of the windows top left position
	
	public static int windowSize = 760; 		// NOT DONE! Will take the dynamic ACTUAL size of the window
	public static double zoomFactor = 0.75;   	// NOT DONE! Will change according to the ACTUAL current zoom factor

	private Timer timer;
	private static final int DELAY = 2000;
	private int counter;
	
	private Window(){
		super("Better than apple maps");
	}

	//Singleton
	public static Window use() {
		if(instance == null) {
			instance = new Window();
			instance.makeFrame();
		}
		return instance;
}
	
	/**
	 * Creates the GUI
	 */
	private void makeFrame(){		
		//setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);	//Frame starts maximized
		setPreferredSize(new Dimension(1024,640)); //When minimized goes to this
		contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout()); 
        BufferedImage image = null;
		try {
			image = ImageIO.read(new File("splash.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        ImageIcon ic = new ImageIcon(image);
        JLabel background = new JLabel(ic);
        contentPane.add(background);
        setResizable(false);
        pack();
		setLocation((int)((Toolkit.getDefaultToolkit().getScreenSize().getWidth() - getWidth())/2),
					(int)((Toolkit.getDefaultToolkit().getScreenSize().getHeight() - getHeight())/2));
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addListeners();
	}
	
	private void addListeners() {
		addKeyListener(new MKeyListener());
		zoomWithBoxListener();
		resizeAdapter();
	}
	
	
	// inner class key listener
	class MKeyListener extends KeyAdapter {
		
		public void keyPressed(KeyEvent event) {
			
			if(event.getKeyCode() == KeyEvent.VK_UP) {
				System.out.println("Up pressed");
				Window.offsetY += -10000;
			}
			if(event.getKeyCode() == KeyEvent.VK_DOWN) {
				System.out.println("Down pressed");
				Window.offsetY += 10000;
			}
			if(event.getKeyCode() == KeyEvent.VK_LEFT) {
				System.out.println("Left pressed");
				Window.offsetX += 10000;
			}
			if(event.getKeyCode() == KeyEvent.VK_RIGHT) {
				System.out.println("Right pressed");
				Window.offsetX += -10000;
			}
			
			WindowHandler.calculatePixels();
			repaint();
		}
	}
	
	//add a resize adapter
	public void resizeAdapter() {
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				if(Map.use().getRoadSegments() != null)
				Map.use().updatePix();
				System.out.println("resized");
				repaint();
        }
     });}
	
	// make a timer thats used every 2 seconds when a resize happens 
	public void makeTimer(){
		timer = new Timer(DELAY, new ActionListener() {
			   public void actionPerformed(ActionEvent e) {
			    System.out.println("timer action " + counter++ + "!");
			    WindowHandler.calculatePixels();
			   }
			  });
			  timer.setRepeats(false);
	}	
	
	//Press mouse and hold, then drag to new spot. Pressed and released pixels printed out
	public void zoomWithBoxListener(){
		addMouseListener(new MouseAdapter(){
			private int pressedX;
			private int pressedY;
			private int releasedX;
			private int releasedY;
			
			public void mousePressed(MouseEvent e){	
				pressedX = e.getX();
				pressedY = e.getY();
				
				System.out.println("Pressed X : "+ pressedX);
				System.out.println("Pressed Y : "+ pressedY);
			}
			
			public void mouseReleased(MouseEvent e){
				releasedX = e.getX();
				releasedY =  e.getY();
				System.out.println("Released X : "+ releasedX);
				System.out.println("Released Y : "+ releasedY);
				
				WindowHandler.search(pressedX, releasedY, pressedX, releasedY);
				
				WindowHandler.calculatePixels();
				repaint();
			}
		});		
	}
	
	public void updateMap() {
		contentPane.removeAll();
		contentPane.add(Map.use(), BorderLayout.CENTER);
		System.out.println("updateMap is called");
		repaint();
		validate();
		if(initializing){
			setResizable(true);
			initializing = false;
		}
	}
	
	public void setZoomFactor(double z){
		zoomFactor = z;
	}
	
	public double getZoomFactor(){
		return zoomFactor;
	}
}
