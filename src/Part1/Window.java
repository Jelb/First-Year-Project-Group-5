package Part1;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
		//setUndecorated(true);
		contentPane.setPreferredSize(new Dimension(1024,640)); //Sets the dimension on the content pane.		
        contentPane.setLayout(new BorderLayout()); //Sets the layout manager for the content pane.

        // Showed if the background image is unable to be loaded.
        JLabel background = new JLabel("<html><center><b>Loading image could not load.<br>The map is loading...</b></html>", JLabel.CENTER);
		try {
			background = new JLabel(new ImageIcon(ImageIO.read(new File("splash.jpg"))));
		} catch (IOException e) {
			
		}
		addMenu();
        contentPane.add(background);
        setResizable(false);
        pack();
        centerWindow();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Used to center the application on the screen at launch.
	 */
	private void centerWindow() {
		setLocation((int)((Toolkit.getDefaultToolkit().getScreenSize().getWidth() - getWidth())/2),
				(int)((Toolkit.getDefaultToolkit().getScreenSize().getHeight() - getHeight())/2));
	}
	
	/**
	 * Adds the needed listeners to the window instance.
	 */
	private void addListeners() {
		addKeyListener(new MKeyListener());
		addComponentListener(new resizeListener());
		Map.use().addMouseListener(new mouseZoom());
	}
	
	/**
	 * Redraws the map when its content has changed or 
	 * the window has been resized. 
	 */
	public void updateMap() {
		contentPane.removeAll();
		contentPane.add(Map.use(), BorderLayout.CENTER);
		System.out.println("updateMap is called");
		if(initializing){
			contentPane.setPreferredSize(new Dimension((int)(640*WindowHandler.getRatio()),640));
			pack();
			centerWindow();
			setResizable(true);
			initializing = false;
			addListeners();
		}
		repaint();
		validate();
	}
	
	/**
	 * Creates and adds the menu bar.
	 */
	private void addMenu() {
		JMenuBar menu = new JMenuBar();
		JMenu zoomMenu = new JMenu("Don't panic");
		JMenuItem resetZoomButton = new JMenuItem("Reset zoom");
		menu.add(zoomMenu);
		zoomMenu.add(resetZoomButton);
		add(menu);
		setJMenuBar(menu);
		resetZoomButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				WindowHandler.resetMap();
			}
		});
	}
	
	public int getMapWidth() {
		return contentPane.getComponent(0).getWidth();
	}
	
	public int getMapHeight() {
		return contentPane.getComponent(0).getHeight();
	}
	
	/**
	 * Adds a key listener used to move around the map.
	 * 
	 * @author Tom (TMCH@ITU.DK)
	 */
	class MKeyListener extends KeyAdapter {
		/**
		 * Adds a key listener that sends the pressed button to the pan method of WindowHandler.
		 */
		public void keyPressed(KeyEvent event) {
			WindowHandler.pan(event);
			WindowHandler.calculatePixels();

			updateMap();
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
		int height;
		int width;
		int count;
		boolean run;
	
		public void componentResized(ComponentEvent evt) {
			if(run){
			if(Math.abs(width - Window.use().getWidth())>0){
				Window.use().setPreferredSize(new Dimension(Window.use().getWidth(), (int)(Window.use().getWidth()/1.2)));
			} else if(Math.abs(width - Window.use().getWidth())>0){
				Window.use().setPreferredSize(new Dimension((int)(Window.use().getHeight()*1.2), Window.use().getHeight()));
			}
			pack();
			height = Window.use().getHeight();
			width = Window.use().getWidth();
			if(Map.use().getRoadSegments() != null)
			Map.use().updatePix();
			repaint();

			System.out.println(++count);
			}
			run =!run;
		}
	}
	
	
	/**
	 * Adds a mouse listener used for "box zooming" on the map.
	 * 
	 */
	private class mouseZoom extends MouseAdapter {
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
			

			WindowHandler.pixelSearch(pressedX, releasedX, pressedY, releasedY);
			
			
			WindowHandler.calculatePixels();

			updateMap();
		}
	}
}
