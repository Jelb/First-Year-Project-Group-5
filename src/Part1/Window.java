package Part1;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashSet;

/*
 * Window class is the GUI of our program, which puts the map and other components together
 */ 

public class Window extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private static Window instance;
	private static JLayeredPane screen;
	private static Container contentPane;
	private boolean dragging;
	private boolean noMoreBoxes;

	//Fields used for drag zoom
	private static int pressedX;
	private static int pressedY;
	private static int releasedX;
	private static int releasedY;
	private static JComponent rect;
	private Timer timer;
	private static int maxHeight;

	//Buttons to pan and zoom
	private JButton resetZoom, zoomOut, zoomIn;
	private JButton west, east, north, south, findPath;
	private JTextField from, to;
	private JComboBox searchResultBox;
	private boolean navigateVisible=false;
	private String[] result;
	
	//GUI background
	private JPanel background;


	//Midlertidige felter
	private JButton toms;


	/**
	 * Constructor for the window class.
	 */
	private Window(){
		super("Pytheas");
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
			instance.makeContent();
		}
		return instance;
	}

	/**
	 * The method is used to set up the initial instance of the window.
	 * After setting the dimension and the layout a splash-screen
	 * is displayed while the program is loading.
	 */
	private void makeContent(){
		getEffectiveScreenSize();
		contentPane = getContentPane();
		setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		contentPane.setPreferredSize(new Dimension((int)(640*WindowHandler.getRatio()),640)); //Sets the dimension on the content pane.
		screen = new JLayeredPane();	
		screen.add(Map.use(), JLayeredPane.DEFAULT_LAYER);
		contentPane.add(screen);
		createButtons();
		addButtons();			
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Adds the needed listeners to the window instance.
	 */
	public void addListeners() {
		addKeyListener(new MKeyListener());
		addComponentListener(new resizeListener());
		Map.use().addMouseWheelListener(new mouseWheelZoom());
		//Listeners for when mouse is pressed, dragged or released
		MouseListener mouseListener = new MouseListener();
		Map.use().addMouseListener(mouseListener);
		Map.use().addMouseMotionListener(mouseListener);
		//Listener for the buttons for zoom, pan
		addButtonListeners();
	}

	/**
	 * Used to get the effective size of the screen. 
	 * The effective size of the screen is equals to the size of the screen 
	 * excluding the size reserved for other objects such as docks and tool bars.
	 */
	private static void getEffectiveScreenSize() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		maxHeight  = ge.getMaximumWindowBounds().height;
	}

	/**
	 * Redraws the map when its content has changed or 
	 * the window has been resized. 
	 */
	public void updateMap() {
		long startTime = System.currentTimeMillis();
		Map.use().updatePath();
		validate();
		repaint();
		if(!isVisible()){
			SplashScreen.use().setAlwaysOnTop(true);
			Map.use().setBounds(0, 0, contentPane.getPreferredSize().width, contentPane.getPreferredSize().height);		
			addListeners();
			setVisible(true);
			SplashScreen.use().setAlwaysOnTop(false);
		} else {
			requestFocus();			
		}
		System.out.println("Time to update map: " + (System.currentTimeMillis()-startTime)/1000.0);
	}

	/**
	 * Creates the buttons which makes up the GUI
	 */
	private void createButtons() {
		//Icons from http://www.iconfinder.com/search/?q=iconset%3Abrightmix
		resetZoom = createButton("ResetZoom.png", "Reset zoom", 75, 75);
		zoomOut = createButton("ZoomOut.png", "Zoom out", 100, 175);
		zoomIn = createButton("ZoomIn.png", "Zoom in", 50, 175);
		west = createButton("West.png", "West", 25, 75);
		east = createButton("East.png", "East", 125, 75);
		north = createButton("North.png", "North",75, 25);
		south = createButton("South.png", "South", 75, 125);
		findPath = createButton("FindPath.png", "Find Path", 75, 240);		
		
		searchResultBox = new JComboBox();

		from = new JTextField("From");
		from.setBounds(25 , 275,140, 25);
		from.setBackground(Color.WHITE);
		from.setVisible(false);
		
		to = new JTextField("To");
		to.setBounds(25, 310, 140,25);
		to.setBackground(Color.WHITE);
		to.setVisible(false);
		
		//Internet magic from http://tips4java.wordpress.com/2009/05/31/backgrounds-with-transparency/
		background = new JPanel()
		{
		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g)
		    {
		        g.setColor( getBackground() );
		        g.fillRect(0, 0, getWidth(), getHeight());
		        super.paintComponent(g);
		    }
		};
		background.setOpaque(false);		
		//background.setBackground(new Color(255,255,255,200)); White
		background.setBackground(new Color(0,0,0,20));
		background.setBounds(10,20,165,340);
		
		toms = new JButton("Tom");
		toms.setBounds(35, 400, 20,20);
	}
	
	/**
	 * Each button gets a listener with a corresponding action
	 */

	private void addButtonListeners(){

		resetZoom.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				WindowHandler.resetMap();
			}
		});

		zoomOut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				WindowHandler.zoomOut();
			}
		});

		zoomIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				WindowHandler.zoomIn();
			}
		});

		west.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				PanHandler.directionPan(Direction.WEST);
			}
		});

		east.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				PanHandler.directionPan(Direction.EAST);
			}
		});

		north.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				PanHandler.directionPan(Direction.NORTH);
			}
		});

		south.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				PanHandler.directionPan(Direction.SOUTH);
			}
		});

		from.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				String fromText = from.getText();
				addressParse(fromText, 185, 275);
			}
		});

		to.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				String toText = to.getText();
				addressParse(toText, 185, 310);
			}
		});

		toms.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				WindowHandler.pathFindingTest();
			}
		});
		
		findPath.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				if(!navigateVisible){
					to.setVisible(true);
					from.setVisible(true);
					navigateVisible= true;
				}
				else{
					to.setVisible(false);
					from.setVisible(false);
					searchResultBox.setVisible(false);
					navigateVisible= false;
				}
				
			}
		});
	}
	
	private void addressParse(String text, int x,int y){		
		result = AddressParser.use().parseAddress(text);
		String[] setArray;
		if(result[0]==""){
			createSearchBox(new String[]{"No Results"},x,y);
		}		
		if(!(result[4]=="")){
			setArray = new String[1]; 
			setArray[0] = result[0]+" " + result[1]+" " + result[2]+" " + result[3] + " i " + StreetNameReader.getZipToCityMap().get(result[4]);
			setArray[0] = setArray[0].replaceAll("\\s+", " ");
			}		
		else{
			HashSet<String> set = WindowHandler.getRoadToCityMap().get(result[0]);
			setArray = set.toArray(new String[0]);
			for(int i = 0; i < setArray.length; i++){
				setArray[i] = result[0]+" " + result[1]+" " + result[2]+" " + result[3] + " i " + setArray[i];
				setArray[i] = setArray[i].replaceAll("\\s+", " ");				
				}						
			}
		createSearchBox(setArray,x,y);
	}
	
	private void createSearchBox(String[] array, int x, int y){
		searchResultBox.setVisible(false);
		searchResultBox = null;
		searchResultBox = new JComboBox(array);
		searchResultBox.setBounds(x,y ,175,25);	
		searchResultBox.addActionListener(new ActionListener(){ 
			
			public void actionPerformed(ActionEvent e) {
				String selectedItem = (String)searchResultBox.getSelectedItem();
				for(Edge edge : WindowHandler.getEdges()){
						if(edge.getVEJNAVN().equals(result[0])&& (edge.getV_POSTNR()== result[4] || edge.getH_POSTNR()== result[4] )){
							edge.getFromNode();
						}
				}
			}
	});
		
		screen.add(searchResultBox, JLayeredPane.PALETTE_LAYER);			
	}
	
	
	/**
	 * Buttons added to the Pallette_Layer, above the map layer
	 */

	private void addButtons(){
		screen.add(resetZoom, JLayeredPane.PALETTE_LAYER);
		screen.add(zoomOut, JLayeredPane.PALETTE_LAYER);
		screen.add(zoomIn, JLayeredPane.PALETTE_LAYER);
		screen.add(west, JLayeredPane.PALETTE_LAYER);
		screen.add(east, JLayeredPane.PALETTE_LAYER);
		screen.add(north, JLayeredPane.PALETTE_LAYER);
		screen.add(south, JLayeredPane.PALETTE_LAYER);
		screen.add(from, JLayeredPane.PALETTE_LAYER);
		screen.add(to, JLayeredPane.PALETTE_LAYER);
		screen.add(toms, JLayeredPane.PALETTE_LAYER);
		screen.add(findPath, JLayeredPane.PALETTE_LAYER);
		screen.add(background, JLayeredPane.PALETTE_LAYER);
	}

	/**
	 * Method to create buttons, only to avoid code duplication
	 */
	private JButton createButton(String file, String hoverText, int x, int y){
		ImageIcon icon = new ImageIcon(file, hoverText);
		JButton button = new JButton(icon);		
		button.setBorderPainted(false); 
		button.setContentAreaFilled(false); 
		button.setFocusPainted(false); 
		button.setBounds(x, y, icon.getIconWidth(),icon.getIconHeight());
		button.setToolTipText(hoverText);
		return button;
	}

	public int getMapWidth() {
		return contentPane.getWidth();
	}

	public int getMapHeight() {
		return contentPane.getHeight();
	}

	/**
	 * Method for drawing the rectangle to show where the user is dragging for zoom
	 * The method compares where the user is dragging from and to, and hereby calculates
	 * the rectangle.
	 * 
	 * @author Nico
	 */
	@SuppressWarnings("serial")
	static class DrawRect extends JComponent {
		public void paint(Graphics g) {
			super.paint(g);
			g.setColor(Color.orange);
			if(getMousePosition() != null && !Window.use().noMoreBoxes) {
				if(pressedX < getMousePosition().x && pressedY < getMousePosition().y) {
					g.drawRect(pressedX, pressedY, getMousePosition().x - pressedX, getMousePosition().y - pressedY);
					System.out.println("Box width: " + (getMousePosition().x - pressedX) + " and height: " + (getMousePosition().y - pressedY));
				}
				else if(pressedX > getMousePosition().x && pressedY > getMousePosition().y) {
					g.drawRect(getMousePosition().x, getMousePosition().y, pressedX - getMousePosition().x, pressedY - getMousePosition().y);
					System.out.println("Box width: " + (pressedX - getMousePosition().x) + " and height: " + (pressedY - getMousePosition().y));
				}
				else if(pressedX < getMousePosition().x && pressedY > getMousePosition().y) {
					g.drawRect(pressedX, getMousePosition().y, getMousePosition().x - pressedX, pressedY - getMousePosition().y);
					System.out.println("Box width: " + (getMousePosition().x - pressedX) + " and height: " + (pressedY - getMousePosition().y));
				}
				else if(pressedX > getMousePosition().x && pressedY < getMousePosition().y) {
					g.drawRect(getMousePosition().x, pressedY, pressedX - getMousePosition().x, getMousePosition().y - pressedY);
					System.out.println("Box width: " + (pressedX - getMousePosition().x) + " and height: " + (getMousePosition().y - pressedY));
				}
			}
			//StdOut.println("drawing");
			//System.out.println(getMousePosition());
		}
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
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()){
			case KeyEvent.VK_1:
				WindowHandler.zoomOut();
				break;
			case KeyEvent.VK_2:
				WindowHandler.zoomIn();
				break;
			case KeyEvent.VK_UP:
				PanHandler.directionPan(Direction.NORTH);
				break;
			case KeyEvent.VK_DOWN:
				PanHandler.directionPan(Direction.SOUTH);
				break;
			case KeyEvent.VK_LEFT:
				PanHandler.directionPan(Direction.WEST);
				break;
			case KeyEvent.VK_RIGHT:
				PanHandler.directionPan(Direction.EAST);
				break;
			}
		}
	}

	/**
	 * Adds a listener to the window instance.
	 * The listener recalculates the position of each edge so 
	 * that it fits the screen.
	 */
	private class resizeListener extends ComponentAdapter {
		int height;
		int width;

		public void componentResized(ComponentEvent evt) {
			if(timer == null){
				timer = new Timer(50, new ResizeTask());
				timer.start();
			}
			timer.restart();
		}

		private class ResizeTask implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				timer.stop();
				if(Window.use().getHeight() < maxHeight && Window.use().getWidth()/WindowHandler.getRatio() < maxHeight) {
					if(Math.abs(height - Window.use().getHeight())>0 && Window.use().getHeight() < maxHeight){
						Window.use().setPreferredSize(new Dimension((int)(Window.use().getHeight()*WindowHandler.getRatio()), Window.use().getHeight()));
					} else if(Math.abs(width - Window.use().getWidth())>0 && Window.use().getHeight() < maxHeight){
						Window.use().setPreferredSize(new Dimension(Window.use().getWidth(), (int)(Window.use().getWidth()/WindowHandler.getRatio())));
					}
				} else {
					Window.use().setPreferredSize(new Dimension((int)(maxHeight*WindowHandler.getRatio()), maxHeight));
				}
				pack();
				Map.use().setSize(Window.use().getSize());
				if(Map.use().getRoadSegments() != null)
					Map.use().updatePix();
				height = Window.use().getHeight();
				width = Window.use().getWidth();
				timer = null;
			}
		}
	}

	private class MouseListener extends MouseInputAdapter {
		int prevX;
		int prevY;

		public void mousePressed(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				System.out.println("Mouse pressed");
				pressedX = e.getX();
				pressedY = e.getY();
				noMoreBoxes = false;

				System.out.println("Pressed X : "+ pressedX);
				System.out.println("Pressed Y : "+ pressedY);
			} else if (SwingUtilities.isLeftMouseButton(e)) {
				pressedX = e.getX();
				pressedY = e.getY();
				
				WindowHandler.closestEdge(pressedX, pressedY);
			}
		}

		public void mouseDragged(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e) && !noMoreBoxes) {
				System.out.println("Mouse dragged");
				rect = new DrawRect();
				rect.setBounds(0, 0, contentPane.getWidth(), contentPane.getHeight());
				screen.add(rect, JLayeredPane.POPUP_LAYER);
			}
			else if (SwingUtilities.isLeftMouseButton(e)) {
				if (dragging) {
					int x = e.getX();
					int y = e.getY();
					int dist;
					if (x > y) dist = Math.abs(x-prevX);
					else dist = Math.abs(y-prevY);
					System.out.println("distance dragged: " + dist);
					if (dist > 1) {
						System.out.println("Im Panning");
						PanHandler.pixelPan((prevX-e.getX()), (e.getY()-prevY));
						prevX = e.getX();
						prevY = e.getY();
						//updateMap();
					}
				}
				else {
					prevX = e.getX();
					prevY = e.getY();
					dragging = true;
					System.out.println("Set dragging to true");
				}
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				System.out.println("Mouse released");
				releasedX = e.getX();
				releasedY =  e.getY();
				System.out.println("Released X : "+ releasedX);
				System.out.println("Released Y : "+ releasedY);
				WindowHandler.pixelSearch(pressedX, releasedX, pressedY, releasedY);
				//if(rect != null)
				rect.setVisible(false); //Removes the rectangle when zoom box is chosen
				noMoreBoxes = true;
				updateMap();
			}
			else if (SwingUtilities.isLeftMouseButton(e)) {
				dragging = false;
			}
		}
	}

	private class mouseWheelZoom implements MouseWheelListener{
		public void mouseWheelMoved(MouseWheelEvent e) {
			int notches = e.getWheelRotation();
			if (notches < 0) {
				WindowHandler.zoomIn();
			} else {	            
				WindowHandler.zoomOut();
			}
		}
	}
}
