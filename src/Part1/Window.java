package Part1;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Arrays;
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
	private JButton resetZoom, zoomOut, zoomIn, shortest, fastestsButton, ship, blueShip, search, findButton;
	private JButton west, east, north, south, findPath, bike, blueBike, car, blueCar, reset;
	private JTextField from, to, find;
	private JComboBox searchFromResultBox, searchToResultBox, searchFindResultBox;
	private boolean navigateVisible = false;
	private boolean fromMarked, toMarked, findMarked;
	
	private boolean byCar = true;
	private boolean fastest = true;
	private boolean byShip = true;
	
	// The two flags
	private Flag fromFlag = new Flag(true);
	private Flag toFlag = new Flag(false);
	
	// Currently saved from and to text
	private String fromText;
	private String toText;
	private String findText;
	
	//Currently saved node to search for
	private Node findNode;
	
	//GUI background
	private JPanel background;

	private int mousePanX;	// The temporary displacement of the buffered image
	private int mousePanY;
	
	public enum TextType {
		FIND, TO, FROM;
	}

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
		setBackground(new Color(71, 180, 201));
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
		Map.use().createBufferImage();
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
		
		bike = createButton("cycle.png", "By bike or walking", 25, 350);
		bike.setVisible(false);
		blueBike = createButton("cycle_blue.png", "By bike or walking", 25, 350);
		blueBike.setVisible(false);
		car = createButton("motor.png", "By car", 78, 350);
		car.setVisible(false);
		blueCar = createButton("motor_blue.png", "By car", 78, 350);
		blueCar.setVisible(false);
		ship = createButton("ship.png", "If fastest or shortest, I would like to travel with ferry", 125, 347);
		ship.setVisible(false);
		blueShip = createButton("ship_blue.png", "If fastest or shortest, I would like to travel with ferry", 125, 347);
		blueShip.setVisible(false);
		
		shortest = new JButton("Shortest");
		shortest.setMargin(new Insets(5,5,5,5));
		shortest.setBounds(20, 395, 70, 20);
		shortest.setVisible(false);
		fastestsButton = new JButton("Fastest");
		fastestsButton.setMargin(new Insets(5,5,5,5));
		fastestsButton.setBounds(95, 395, 70, 20);
		fastestsButton.setVisible(false);
		
		searchFromResultBox = new JComboBox();
		searchToResultBox = new JComboBox();
		searchFindResultBox = new JComboBox();

		from = new JTextField("From");
		fromText = "From";
		from.setBounds(20, 280, 145, 25);
		from.setBackground(Color.WHITE);
		from.setVisible(false);
		
		to = new JTextField("To");
		toText = "To";
		to.setBounds(20, 315, 145, 25);
		to.setBackground(Color.WHITE);
		to.setVisible(false);
		
		find = new JTextField("Enter address");
		findText = "Enter address";
		find.setBounds(20, 280, 145, 25);
		find.setBackground(Color.WHITE);
		find.setVisible(true);
		
		search = new JButton("Search");
		search.setBounds(20, 425,70, 20);
		search.setMargin(new Insets(5,5,5,5));
		search.setFont(null);
		search.setVisible(false);
		
		findButton = new JButton("Find");
		findButton.setBounds(25, 315,70, 20);
		findButton.setMargin(new Insets(5,5,5,5));
		findButton.setFont(null);
		findButton.setVisible(true);
		
		reset = new JButton("Reset");
		reset.setBounds(95, 425, 70, 20);
		reset.setMargin(new Insets(5,5,5,5));
		reset.setFont(null);
		reset.setVisible(false);
		
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
		background.setBackground(new Color(0,0,0,50));
		background.setBounds(10,20,165,335);
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
		
		from.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				fromText = from.getText();
				addressParse(fromText, 185, 280, TextType.FROM);
			}
		});
		
		from.addMouseListener(new mouseOnText(TextType.FROM));

		to.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				toText = to.getText();
				addressParse(toText, 185, 315, TextType.TO);
			}
		});
		
		to.addMouseListener(new mouseOnText(TextType.TO));
		
		find.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				findText = find.getText();
				addressParse(findText, 185, 280, TextType.FIND);
			}
		});
		
		find.addMouseListener(new mouseOnText(TextType.FIND));
		
		bike.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				System.out.println("bike");
				byCar = false;
				blueCar.setVisible(false);
				car.setVisible(true);
				blueBike.setVisible(true);
				bike.setVisible(false);
				shortest.setVisible(false);
				fastestsButton.setVisible(false);
				search.setBounds(20, 395,70, 20);
				reset.setBounds(95, 395, 70, 20);
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
				shortest.setVisible(true);
				shortest.setFont(null);
				//reset.setVisible(true);
				reset.setBounds(95, 425, 70, 20);
				fastestsButton.setVisible(true);
				fastestsButton.setFont(new Font("Shortest", Font.BOLD, 12));
				search.setBounds(20, 425,70, 20);
				fastest = true; //We want the fastest route by default if by car
			}
		});
		
		ship.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				System.out.println("ship");
				ship.setVisible(false);
				blueShip.setVisible(true);
				byShip = true;
			}
		});
		
		blueShip.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				System.out.println("Blue ship");
				blueShip.setVisible(false);
				ship.setVisible(true);
				byShip = false;
			}
		});
		
		fastestsButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				System.out.println("Hurtigste rute valgt");
				fastestsButton.setFont(new Font("Fastest", Font.BOLD, 12));
				shortest.setFont(null);
				fastest = true;
			}
		});
		
		shortest.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				System.out.println("Korteste rute valgt");
				shortest.setFont(new Font("Shortest", Font.BOLD, 12));
				fastestsButton.setFont(null);
				fastest = false;
			}
		});
		
		reset.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				System.out.println("reset");
				from.setText(null);
				to.setText(null);
				screen.remove(searchFromResultBox);
				screen.remove(searchToResultBox);
				fromMarked = false;
				toMarked = false;
				Map.use().resetPath();
				updateMap();
			}
		});

		search.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				// If the text has been changed the user must choose a new address
				if (!fromText.equals(from.getText())) fromMarked = false;
				if (!toText.equals(to.getText())) toMarked = false;
				
				if(!fromMarked) {
					String fromText = from.getText();
					addressParse(fromText, 185, 280, TextType.FROM);
				}
				if (!toMarked) {	
					String toText = to.getText();
					addressParse(toText, 185, 315,TextType.TO);
				}
				
				if(fromMarked && toMarked) {
					System.out.println("searching for route");
					//Checks if the user is going by car or bike, and if they want the shortest or fastest route
					if(byCar) { 
						if(fastest) {
							WindowHandler.pathFindingTest(TransportWay.CAR, CompareType.FASTEST, byShip);
						} else {
							WindowHandler.pathFindingTest(TransportWay.CAR, CompareType.SHORTEST, byShip);
						}
					} else {
						WindowHandler.pathFindingTest(TransportWay.BIKE, CompareType.SHORTEST, byShip);
					}
				}
			}
		});
		
		findButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				// If the text has been changed the user must choose a new address
				if (!findText.equals(find.getText())) findMarked = false;
				
				if(!findMarked) {
					String findText = find.getText();
					addressParse(findText, 185, 280, TextType.FIND);
				}
				
				if(findMarked) {
					WindowHandler.centerOnNode(findNode);
					updateMap();
				}
			}
		});
		
		findPath.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				if(!navigateVisible){
					to.setVisible(true);
					from.setVisible(true);
					find.setVisible(false);
					if (byCar) {
						bike.setVisible(true);
						blueCar.setVisible(true);
						if (fastest) {
							shortest.setVisible(true);
							fastestsButton.setVisible(true);
							fastestsButton.setFont(new Font("Fastest", Font.BOLD, 12));
							shortest.setFont(null);
						}
						else {
							shortest.setVisible(true);
							fastestsButton.setVisible(true);
							shortest.setFont(new Font("Shortest", Font.BOLD, 12));
							fastestsButton.setFont(null);
						}
					}
					else {
						blueBike.setVisible(true);
						car.setVisible(true);
					}
					navigateVisible = true;
					if (byShip) blueShip.setVisible(true);
					else ship.setVisible(true);
					search.setVisible(true);
					findButton.setVisible(false);
					reset.setVisible(true);
					searchFindResultBox.setVisible(false);
					background.setBounds(10,20,165,440);
				}
				else {
					find.setVisible(true);
					to.setVisible(false);
					from.setVisible(false);
					bike.setVisible(false);
					blueBike.setVisible(false);
					car.setVisible(false);
					blueCar.setVisible(false);
					searchFromResultBox.setVisible(false);
					searchToResultBox.setVisible(false);
					navigateVisible = false;
					shortest.setVisible(false);
					fastestsButton.setVisible(false);
					ship.setVisible(false);
					blueShip.setVisible(false);
					search.setVisible(false);
					findButton.setVisible(true);
					reset.setVisible(false);
					background.setBounds(10,20,165,335);
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
	private void addressParse(String text, int x,int y, TextType t){		
		String[] result = AddressParser.use().parseAddress(text);
		String[] setArray = new String[0];
		String[] zipArray;
		// Sets input text to red if there is no road name included
		if(result[0].equals("")){
			zipArray = new String[0];
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
					setArray[i] = result[0]+" " + result[1] + result[2]+" " + result[3] + " " + zipList.get(i) + " " + result[5];
					setArray[i] = setArray[i].replaceAll("\\s+", " ");
				}
				zipArray = Arrays.copyOf(zipList.toArray(), zipList.size(), String[].class);
			}
			// If there has been typed in a zip code
			else if(!(result[4].equals(""))){
				setArray = new String[1]; 
				setArray[0] = result[0]+" " + result[1] + result[2]+" " + result[3] + " " + result[4]+ " " + WindowHandler.getZipToCityMap().get(result[4]);
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
					setArray[i] = result[0]+" " + result[1] + result[2]+" " + result[3] + " " + setArray[i] + " " + city;
					setArray[i] = setArray[i].replaceAll("\\s+", " ");				
				}						
			}
		}
		if (zipArray.length == 0) setArray = new String[]{"No results"};
		createSearchBox(setArray,zipArray,result,x,y,t);
	}
	
	/**
	 * Creates a search box which shows the search results from the address search fields
	 * 
	 * @param addressArray Holds the addresses that matches the search
	 * @param zipArray Holds the zips matching the addresses in addressArray
	 * @param textArray Holds the array resulting from parsing the text from the textfield
	 * @param x The x-position of the search box
	 * @param y The y-position of the search box
	 * @param fromBool Set to true if the search text field is the "from field", set to false if the search text field if the "to field"
	 */
	private void createSearchBox(String[] addressArray, String[] zipArray, String[] textArray, int x, int y, TextType t){
		if(t == TextType.FROM) {
			screen.remove(searchFromResultBox);
			searchFromResultBox = new JComboBox(addressArray);
			searchFromResultBox.setBounds(x,y ,200,25);	
			searchFromResultBox.addActionListener(new comboBoxListener(searchFromResultBox, zipArray, textArray, t));
			screen.add(searchFromResultBox, JLayeredPane.PALETTE_LAYER);
		} else if (t == TextType.TO) {
			screen.remove(searchToResultBox);
			searchToResultBox = new JComboBox(addressArray);
			searchToResultBox.setBounds(x,y ,200,25);
			searchToResultBox.addActionListener(new comboBoxListener(searchToResultBox, zipArray, textArray, t));
			screen.add(searchToResultBox, JLayeredPane.PALETTE_LAYER);
		}	
		else if (t == TextType.FIND) {
			screen.remove(searchFindResultBox);
			searchFindResultBox = new JComboBox(addressArray);
			searchFindResultBox.setBounds(x, y, 200, 25);
			searchFindResultBox.addActionListener(new comboBoxListener(searchFindResultBox, zipArray, textArray, t));
			screen.add(searchFindResultBox, JLayeredPane.PALETTE_LAYER);
		}
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
		screen.add(find, JLayeredPane.PALETTE_LAYER);
		screen.add(search, JLayeredPane.PALETTE_LAYER);
		screen.add(findButton, JLayeredPane.PALETTE_LAYER);
		screen.add(findPath, JLayeredPane.PALETTE_LAYER);
		screen.add(background, JLayeredPane.PALETTE_LAYER);
		screen.add(bike, JLayeredPane.PALETTE_LAYER);
		screen.add(car, JLayeredPane.PALETTE_LAYER);
		screen.add(blueBike, JLayeredPane.PALETTE_LAYER);
		screen.add(blueCar, JLayeredPane.PALETTE_LAYER);
		screen.add(fastestsButton, JLayeredPane.PALETTE_LAYER);
		screen.add(shortest, JLayeredPane.PALETTE_LAYER);
		screen.add(ship, JLayeredPane.PALETTE_LAYER);
		screen.add(blueShip, JLayeredPane.PALETTE_LAYER);
		screen.add(reset, JLayeredPane.PALETTE_LAYER);
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
	
	private class comboBoxListener implements ActionListener {
		JComboBox searchResultBox;
		TextType t;
		String[] zipArray, textArray;
		
		public comboBoxListener(JComboBox searchResultBox, String[] zipArray, String[] textArray, TextType t) {
			this.searchResultBox = searchResultBox;
			this.t = t;
			this.zipArray = zipArray;
			this.textArray = textArray;
		}

		@Override
		public void actionPerformed(ActionEvent e) {	
			// if the zip array is empty, the search yielded no results
			if (zipArray.length == 0) return;
			
			if(t == TextType.FROM) fromMarked = true; //register if the from or to combo box is marked
			else if (t == TextType.TO) toMarked = true;
			else if (t == TextType.FIND) findMarked = true;
			int i = searchResultBox.getSelectedIndex();
			Edge randomCorrectEdge = null;
			System.out.println(textArray[0]);
			System.out.println(zipArray[i]);
			Node flagNode = null;
			String text = (String) searchResultBox.getSelectedItem();
			for(Edge edge : WindowHandler.getEdges()){
					if(edge.getVEJNAVN().equals(textArray[0]) && (edge.getV_POSTNR().equals(zipArray[i]) || edge.getH_POSTNR().equals(zipArray[i]) )){
						randomCorrectEdge = edge;
						String houseNumberString = textArray[1];
						if (!houseNumberString.equals("")) {
							int houseNumber = Integer.parseInt(houseNumberString);
							if (houseNumber % 2 == 0) {
								if (houseNumber >= edge.getHouseNumberMinEven() && houseNumber <= edge.getHouseNumberMaxEven()) {
									WindowHandler.setNode(edge.getFromNode().getKdvID(), t);
									flagNode = edge.getFromNode();
									randomCorrectEdge = null;
									break;
								}
							}
							else if (houseNumber >= edge.getHouseNumberMinOdd() && houseNumber <= edge.getHouseNumberMaxOdd()) {
									WindowHandler.setNode(edge.getFromNode().getKdvID(), t);
									flagNode = edge.getFromNode();
									randomCorrectEdge = null;
									break;
							}
						}
						else {
							WindowHandler.setNode(edge.getFromNode().getKdvID(), t);
							flagNode = edge.getFromNode();
							randomCorrectEdge = null;
							break;
						}
					}
			}
			if (randomCorrectEdge != null){
				WindowHandler.setNode(randomCorrectEdge.getFromNode().getKdvID(), t);
				flagNode = randomCorrectEdge.getFromNode();
			}
			searchResultBox.setVisible(false);
			if (flagNode != null) {
				if(t == TextType.FROM){
					from.setText(text);
					fromText = text;
					double x = flagNode.getXCord();
					double y = flagNode.getYCord();
					fromFlag.setPosition(x, y);
					Map.use().addFlag(fromFlag);
				}
				else if (t == TextType.TO) {
					to.setText(text);
					toText = text;
					double x = flagNode.getXCord();
					double y = flagNode.getYCord();
					toFlag.setPosition(x, y);
					Map.use().addFlag(toFlag);
				}
				else if (t == TextType.FIND) {
					find.setText(text);
					findText = text;
					findNode = flagNode;
				}
			}
			updateMap();
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
				Map.use().createBufferImage();
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
				if (!dragging) {
					prevX = e.getX();								// Before dragging starts, prevX and prevY is set to 
					prevY = e.getY();								// the current cursor location.
					dragging = true;								// Dragging is then set to begin.
					System.out.println("Set dragging to true");			
				}										
				else {
					setMousePanX(e.getX() - prevX);		// While we are dragging, mousePanX and mousePanY is continually set to 
					setMousePanY(e.getY() - prevY);		// difference between cursors start location and cursors current location.
					repaint();							// The Window is then continually repainted using the override 
				}										// method paint() in Map.
			}
		}
		
		public void mouseMoved(MouseEvent e){
			Map.use().setToolTipText(WindowHandler.closestEdge(e.getX(), e.getY()));
		}
		
		public void mouseReleased(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				System.out.println("Mouse released");
				releasedX = e.getX();
				releasedY =  e.getY();
				System.out.println("Released X : "+ releasedX);
				System.out.println("Released Y : "+ releasedY);
				WindowHandler.pixelSearch(pressedX, releasedX, pressedY, releasedY);
				rect.setVisible(false); //Removes the rectangle when zoom box is chosen
				noMoreBoxes = true;
				updateMap();
			}
			else if (SwingUtilities.isLeftMouseButton(e)) {
				if (dragging) {
					PanHandler.pixelPan((prevX-e.getX()), (e.getY()-prevY));	// When mouse is released, the new map data is calculated.
					setMousePanX(0);											// mousePanX and mousePanY is reset to zero.
					setMousePanY(0);
					dragging = false;											// Dragging ends.
				}
			}
			Map.use().createBufferImage();										// The new image is drawn to the buffer and flipped in when
		}																		// it is completed (see Map.flipImageBuffer() for details).
	}																			

	private class mouseWheelZoom implements MouseWheelListener{
		public void mouseWheelMoved(MouseWheelEvent e) {
			int notches = e.getWheelRotation();
			if (notches < 0) {
				WindowHandler.zoomIn();
			} else {	            
				WindowHandler.zoomOut();
			}
			Map.use().createBufferImage();
		}
	}
	
	private class mouseOnText extends MouseAdapter {
		private TextType t;
		
		mouseOnText(TextType t) {
			this.t = t;
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			if (TextType.FROM == t && from.getText().equals("From")) from.setText("");
			else if (t == TextType.TO && to.getText().equals("To")) to.setText("");
			else if (t == TextType.FIND && find.getText().equals("Enter address")) find.setText("");
			
		}
	}
	
	public int getMousePanX() {
		return mousePanX;
	}
	
	public int getMousePanY() {
		return mousePanY;
	}

	public void setMousePanX(int inputMousePanX) {
		mousePanX = inputMousePanX;
	}

	public void setMousePanY(int inputMousePanY) {
		mousePanY = inputMousePanY;
	}
	

}
