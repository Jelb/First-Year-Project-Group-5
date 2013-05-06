package Part1;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;

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

import Part1.DijkstraSP.CompareType;
import Part1.DijkstraSP.TransportWay;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
	private JButton resetZoom, zoomOut, zoomIn, korteste, hurtigste;
	private JButton west, east, north, south, findPath, bike, blueBike, car, blueCar;
	private JTextField from, to;
	private JComboBox searchResultBox;
	private boolean navigateVisible = false;
	private String[] result;
	private String[] zipArray;
	private static boolean fromBool;
	
	private boolean byCar = true;
	private boolean fastest = true;
	
	//GUI background
	private JPanel background;

	private int mousePanX = 0;
	private int mousePanY = 0;
	
	

	

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
		
		
		if(!isVisible()){
			SplashScreen.use().setAlwaysOnTop(true);
			Map.use().setBounds(0, 0, contentPane.getPreferredSize().width, contentPane.getPreferredSize().height);		
			addListeners();
			setVisible(true);
			SplashScreen.use().setAlwaysOnTop(false);
		} else {
			requestFocus();			
		}
		Map.use().flipImageBuffer();
		repaint();
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
		
		bike = createButton("cycle.png", "By bike or walking", 45, 350);
		bike.setVisible(false);
		blueBike = createButton("cycle_blue.png", "By bike or walking", 45, 350);
		blueBike.setVisible(false);
		car = createButton("motor.png", "By car", 105, 350);
		car.setVisible(false);
		blueCar = createButton("motor_blue.png", "By car", 105, 350);
		blueCar.setVisible(false);
		
		korteste = new JButton("Shortest");
		korteste.setMargin(new Insets(5,5,5,5));
		korteste.setBounds(20, 395, 70, 20);
		korteste.setVisible(false);
		hurtigste = new JButton("Fastest");
		hurtigste.setMargin(new Insets(5,5,5,5));
		hurtigste.setBounds(95, 395, 70, 20);
		hurtigste.setVisible(false);
		
		searchResultBox = new JComboBox();

		from = new JTextField("From");
		from.setBounds(20, 280, 145, 25);
		from.setBackground(Color.WHITE);
		from.setVisible(false);
		
		to = new JTextField("To");
		to.setBounds(20, 315, 145, 25);
		to.setBackground(Color.WHITE);
		to.setVisible(false);
		
		toms = new JButton("Search");
		toms.setBounds(20, 425,70, 20);
		toms.setMargin(new Insets(5,5,5,5));
		toms.setFont(null);
		toms.setVisible(false);
		
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
		background.setBounds(10,20,165,275);
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
				addressParse(fromText, 185, 280, true);
			}
		});

		to.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				String toText = to.getText();
				addressParse(toText, 185, 315,false);
			}
		});
		
		bike.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				System.out.println("bike");
				byCar = false;
				blueCar.setVisible(false);
				car.setVisible(true);
				blueBike.setVisible(true);
				bike.setVisible(false);
				korteste.setVisible(false);
				hurtigste.setVisible(false);
				toms.setBounds(20, 395,70, 20);
				fastest = false; //We want the shortest route if by bike
			}
		});
		
		car.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				System.out.println("car");
				byCar = true;
				blueCar.setVisible(true);
				car.setVisible(false);
				blueBike.setVisible(false);
				bike.setVisible(true);
				korteste.setVisible(true);
				korteste.setFont(null);
				hurtigste.setVisible(true);
				hurtigste.setFont(new Font("Shortest", Font.BOLD, 12));
				toms.setBounds(20, 425,70, 20);
				fastest = true; //We want the fastest route by default if by car
			}
		});
		
		hurtigste.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				System.out.println("Hurtigste rute valgt");
				hurtigste.setFont(new Font("Fastest", Font.BOLD, 12));
				korteste.setFont(null);
				fastest = true;
			}
		});
		
		korteste.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				System.out.println("Korteste rute valgt");
				korteste.setFont(new Font("Shortest", Font.BOLD, 12));
				hurtigste.setFont(null);
				fastest = false;
			}
		});

		toms.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {	
				//Checks if the user is going by car or bike, and if they want the shortest or fastest route
				if(byCar) { 
					if(fastest) {
						WindowHandler.pathFindingTest(TransportWay.CAR, CompareType.FASTEST);
						System.out.println("By car, fastest route");
					} else {
						WindowHandler.pathFindingTest(TransportWay.CAR, CompareType.SHORTEST);
						System.out.println("By car, shortest route");
					}
				} else {
					WindowHandler.pathFindingTest(TransportWay.BIKE, CompareType.SHORTEST);
					System.out.println("By bike, shortest route");
				}
			}
		});
		
		findPath.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				if(!navigateVisible){
					to.setVisible(true);
					from.setVisible(true);
					bike.setVisible(true);
					blueCar.setVisible(true);
					navigateVisible = true;
					korteste.setVisible(true);
					hurtigste.setVisible(true);
					hurtigste.setFont(new Font("Shortest", Font.BOLD, 12));
					korteste.setFont(null);
					toms.setVisible(true);
					background.setBounds(10,20,165,440);
				}
				else {
					to.setVisible(false);
					from.setVisible(false);
					bike.setVisible(false);
					blueBike.setVisible(false);
					car.setVisible(false);
					blueCar.setVisible(false);
					searchResultBox.setVisible(false);
					navigateVisible = false;
					korteste.setVisible(false);
					hurtigste.setVisible(false);
					toms.setVisible(false);
					background.setBounds(10,20,165,275);
				}
			}
		});
	}
	
	/**
	 * Parses the address from the search text field to the search box
	 * 
	 * @param text The text to search for cities with
	 * @param x The x-position of the search box to be created
	 * @param y The y-position of the search box to be created
	 * @param fromBool Set to true if the search comes from the "from-field". Set to false if the search comes from the "to-field"
	 */
	private void addressParse(String text, int x,int y, boolean fromBool){		
		result = AddressParser.use().parseAddress(text);
		String[] setArray;
		if(result[0].equals("")){
			createSearchBox(new String[]{"No Results"},x,y,fromBool);
		}
		else {
			// If there has been typed in a city name
			if (!result[5].equals("")) {
				HashMap<String, String> zipToCityMap = WindowHandler.getZipToCityMap();
				Set<String> zips = zipToCityMap.keySet();
				ArrayList<String> zipList = new ArrayList<String>();
				for (String zip : zips) {
					if (result[5].toLowerCase().equals(zipToCityMap.get(zip).toLowerCase())) {
						if (WindowHandler.getRoadToZipMap().get(result[0]).contains(zip)) {
							zipList.add(zip);
						}
					}
				}
				setArray = new String[zipList.size()];
				for (int i = 0; i < setArray.length; i++) {
					setArray[i] = result[0]+" " + result[1]+" " + result[2]+" " + result[3] + " " + zipList.get(i) + " " + result[5];
					setArray[i] = setArray[i].replaceAll("\\s+", " ");
				}
				zipArray = Arrays.copyOf(zipList.toArray(), zipList.size(), String[].class);
			}
			// If there has been typed in a zip code
			else if(!(result[4].equals(""))){
				setArray = new String[1]; 
				setArray[0] = result[0]+" " + result[1]+" " + result[2]+" " + result[3] + " " + result[4]+ " " + WindowHandler.getZipToCityMap().get(result[4]);
				setArray[0] = setArray[0].replaceAll("\\s+", " ");
				zipArray = new String[]{result[4]};
				}
			// If there has been typed in no city name or zip code
			else{
				HashSet<String> set = WindowHandler.getRoadToZipMap().get(result[0]);
				setArray = set.toArray(new String[0]);
				zipArray = new String[setArray.length];
				for (int i = 0; i < setArray.length; i++) {
					zipArray[i] = setArray[i];
				}
				for(int i = 0; i < setArray.length; i++){
					String city = WindowHandler.getZipToCityMap().get(setArray[i]);
					setArray[i] = result[0]+" " + result[1]+" " + result[2]+" " + result[3] + " " + setArray[i] + " " + city;
					setArray[i] = setArray[i].replaceAll("\\s+", " ");				
				}						
			}
			//if(setArray.length > 1) {
				createSearchBox(setArray,x,y,fromBool);
			//}
		}
	}
	
	/**
	 * Creates a search box which shows the search results from the address search fields
	 * 
	 * @param array Holds the cities that matches the search
	 * @param x The x-position of the search box
	 * @param y The y-position of the search box
	 * @param fromBool Set to true if the search text field is the "from field", set to false if the search text field if the "to field"
	 */
	private void createSearchBox(String[] array, int x, int y,boolean fromBool){
		Window.fromBool = fromBool;
		screen.remove(searchResultBox);
		searchResultBox = new JComboBox(array);
		searchResultBox.setBounds(x,y ,200,25);	
		searchResultBox.addActionListener(new ActionListener(){ 
			
			public void actionPerformed(ActionEvent e) {
				int i = searchResultBox.getSelectedIndex(); 
				Edge randomCorrectEdge = null;
				System.out.println(result[0]);
				System.out.println(zipArray[i]);
				Node flagNode = null;
				String text = (String) searchResultBox.getSelectedItem();
				for(Edge edge : WindowHandler.getEdges()){
						if(edge.getVEJNAVN().equals(result[0]) && (edge.getV_POSTNR().equals(zipArray[i]) || edge.getH_POSTNR().equals(zipArray[i]) )){
							randomCorrectEdge = edge;
							String houseNumberString = result[1];
							if (!houseNumberString.equals("")) {
								int houseNumber = Integer.parseInt(houseNumberString);
								if (houseNumber % 2 == 0) {
									if (houseNumber >= edge.getHouseNumberMinEven() && houseNumber <= edge.getHouseNumberMaxEven()) {
										WindowHandler.setNode(edge.getFromNode().getKdvID(), Window.fromBool);
										flagNode = edge.getFromNode();
										randomCorrectEdge = null;
										break;
									}
								}
								else if (houseNumber >= edge.getHouseNumberMinOdd() && houseNumber <= edge.getHouseNumberMaxOdd()) {
										WindowHandler.setNode(edge.getFromNode().getKdvID(), Window.fromBool);
										flagNode = edge.getFromNode();
										randomCorrectEdge = null;
										break;
								}
							}
							else {
								WindowHandler.setNode(edge.getFromNode().getKdvID(), Window.fromBool);
								flagNode = edge.getFromNode();
								randomCorrectEdge = null;
								break;
							}
						}
				}
				if (randomCorrectEdge != null){
					WindowHandler.setNode(randomCorrectEdge.getFromNode().getKdvID(), Window.fromBool);
					flagNode = randomCorrectEdge.getFromNode();
				}
				searchResultBox.setVisible(false);
				if (flagNode != null) {
					if(Window.fromBool){
						from.setText(text);
						double x = flagNode.getXCord();
						double y = flagNode.getYCord();
						new Flag(x,y,Window.fromBool);
					}
					else{
						to.setText(text);
						double x = flagNode.getXCord();
						double y = flagNode.getYCord();
						new Flag(x,y,Window.fromBool);
					}
				}
				updateMap();
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
		screen.add(bike, JLayeredPane.PALETTE_LAYER);
		screen.add(car, JLayeredPane.PALETTE_LAYER);
		screen.add(blueBike, JLayeredPane.PALETTE_LAYER);
		screen.add(blueCar, JLayeredPane.PALETTE_LAYER);
		screen.add(hurtigste, JLayeredPane.PALETTE_LAYER);
		screen.add(korteste, JLayeredPane.PALETTE_LAYER);
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
				Map.use().flipImageBuffer();
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
				
//				WindowHandler.closestEdge(pressedX, pressedY);
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
					setMousePanX(e.getX() - prevX);
					setMousePanY(e.getY() - prevY);
					repaint();
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
				if (dragging) {
					PanHandler.pixelPan((prevX-e.getX()), (e.getY()-prevY));
					setMousePanX(0);
					setMousePanY(0);
					dragging = false;
				}
			}
			Map.use().flipImageBuffer();
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
			Map.use().flipImageBuffer();
		}
	}
	
	public int getMousePanX() {
		return mousePanX;
	}

	public void setMousePanX(int mousePanX) {
		this.mousePanX = mousePanX;
	}

	public int getMousePanY() {
		return mousePanY;
	}

	public void setMousePanY(int mousePanY) {
		this.mousePanY = mousePanY;
	}
	
}
