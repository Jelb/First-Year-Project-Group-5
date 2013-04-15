package Part1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

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
	private static JLayeredPane screen;
	private static Container contentPane;
	private JLabel background;

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
		contentPane.setLayout(new BorderLayout());

		screen = new JLayeredPane();		
		screen.setPreferredSize(new Dimension(1024,640)); //Sets the dimension on the content pane.

        // Showed if the background image is unable to be loaded.
        background = new JLabel("<html><center><b>Loading image could not load.<br>The map is loading...</b></html>", JLabel.CENTER);
		try {
			background = new JLabel(new ImageIcon(ImageIO.read(new File("splash.jpg"))));
		} catch (IOException e) {
			
		}
		background.setBounds(0, 0, 1024, 640);

		
		contentPane.add(screen, BorderLayout.CENTER);
        screen.add(background, JLayeredPane.DRAG_LAYER);
        screen.setVisible(true);
        setResizable(false);
        pack();
        centerWindow(getWidth(), getHeight());
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Used to center the application on the screen at launch.
	 */
	private void centerWindow(int width, int height) {
		setLocation((int)((Toolkit.getDefaultToolkit().getScreenSize().getWidth() - width)/2),
				(int)((Toolkit.getDefaultToolkit().getScreenSize().getHeight() - height)/2));
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
		removeDefaultLayer();
		long startTime = System.currentTimeMillis();
		screen.add(Map.use(), JLayeredPane.DEFAULT_LAYER);
		Map.use().setBounds(0, 0, (int)(640*WindowHandler.getRatio()), 640);
		repaint();
		validate();
		if(background!= null){
			screen.setPreferredSize(new Dimension((int)(640*WindowHandler.getRatio()),640));
			screen.remove(background);
			addButtons();
			setResizable(true);
			addListeners();
			centerWindow(getPreferredSize().width, getPreferredSize().height);
			pack();
			background = null;
		}
		System.out.println("Time to update map: " + (System.currentTimeMillis()-startTime)/1000.0);
	}
	
	private void addButtons() {
		JButton zoomOut = createButton("ZoomOut.png", "Zoom out", 75, 75);
		JButton west = createButton("West.png", "West", 25, 75);
		JButton east = createButton("East.png", "East", 125, 75);
		JButton north = createButton("North.png", "North",75, 25);
		JButton south = createButton("South.png", "South", 75, 125);
		
		zoomOut.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowHandler.resetMap();
			}
		});
		
		west.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowHandler.pan(Direction.WEST);
			}
		});
		
		east.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowHandler.pan(Direction.EAST);
			}
		});
		
		north.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowHandler.pan(Direction.NORTH);
			}
		});
		
		south.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				WindowHandler.pan(Direction.SOUTH);
			}
		});		
		
		screen.add(zoomOut, JLayeredPane.PALETTE_LAYER);
		screen.add(west, JLayeredPane.PALETTE_LAYER);
		screen.add(east, JLayeredPane.PALETTE_LAYER);
		screen.add(north, JLayeredPane.PALETTE_LAYER);
		screen.add(south, JLayeredPane.PALETTE_LAYER);
	}
	
	private JButton createButton(String file, String hoverText, int x, int y){
		ImageIcon icon = new ImageIcon(file, hoverText);
		JButton button = new JButton(icon);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setBounds(x, y, icon.getIconWidth(),icon.getIconHeight());
		button.setToolTipText(hoverText);
		return button;
	}
	
	
	private void removeDefaultLayer() {
		Component[] layer = screen.getComponentsInLayer(JLayeredPane.DEFAULT_LAYER);
		for(Component c: layer)
			screen.remove(c);
	}
	
	public int getMapWidth() {
		return contentPane.getWidth();
	}
	
	public int getMapHeight() {
		return contentPane.getHeight();
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
				switch(event.getKeyCode()){
					case KeyEvent.VK_1:
						WindowHandler.zoomOut();
						break;
					case KeyEvent.VK_2:
						WindowHandler.zoomIn();
						break;
					case KeyEvent.VK_UP:
						WindowHandler.pan(Direction.NORTH);
						break;
					case KeyEvent.VK_DOWN:
						WindowHandler.pan(Direction.SOUTH);
						break;
					case KeyEvent.VK_LEFT:
						WindowHandler.pan(Direction.WEST);
						break;
					case KeyEvent.VK_RIGHT:
						WindowHandler.pan(Direction.EAST);
						break;
				}
				

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
			//if(run){
			if(Math.abs(width - Window.use().getWidth())>0){
				Window.use().setPreferredSize(new Dimension(Window.use().getWidth(), (int)(Window.use().getWidth()/WindowHandler.getRatio())));
			} else if(Math.abs(height - Window.use().getHeight())>0){
				Window.use().setPreferredSize(new Dimension((int)(Window.use().getHeight()*WindowHandler.getRatio()), Window.use().getHeight()));
			}
			pack();
			height = Window.use().getHeight();
			width = Window.use().getWidth();
			if(Map.use().getRoadSegments() != null)
			Map.use().updatePix();
			repaint();

			System.out.println(++count);
//			}
//			run =!run;
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
			
			
			//WindowHandler.calculatePixels();

			updateMap();
		}
	}
}
