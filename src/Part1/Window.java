package Part1;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/*
 * Window class is the GUI of our program, which puts the map and other GUI components together.
 */ 

public class Window extends JFrame {

	private static final long serialVersionUID = 1L;
	private static Window instance;
	private static JLayeredPane screen; //The layered pane in which all the buttons are placed
	private static Container contentPane;

	//Fields used for drag zoom
	private static int pressedX;
	private static int pressedY;
	private static int releasedX;
	private static int releasedY;
	private static JComponent rect;
	private boolean dragging;
	private boolean noMoreBoxes; //Boolean to control the drawing of the zoom box
	
	//Resizing fields
	private Timer timer;
	private static int maxHeight;

	//Buttons and text fields
	private JButton resetZoom, zoomOut, zoomIn, shipUnselected, shipSelected, search, findButton;
	private JButton west, east, north, south, findPath, bikeUnselected, bikeSelected, carUnselected, carSelected, reset, findPathBlue;
	private JButton findPlace, findPlaceBlue;
	private JTextField from, to, find;
	private ButtonGroup group;
	private JRadioButton fastestRadio, shortestRadio;
	private JComboBox searchFromResultBox, searchToResultBox, searchFindResultBox;
	
	private boolean navigateVisible = false;
	private boolean fromMarked, toMarked, findMarked;
	private boolean byCar = true;
	private boolean fastest = true;
	private boolean byShip = true;

	//The tree flags
	private Flag fromFlag = new Flag(TextType.FROM);
	private Flag toFlag = new Flag(TextType.TO);
	private Flag findFlag = new Flag(TextType.FIND);

	//Currently saved from and to text
	private String fromText;
	private String toText;
	private String findText;

	//Currently saved node to search for
	private Node findNode;

	//The label holding the current city and zip
	private JLabel cityAndZipLabel;

	//The default text in the textfields
	private final String fromDefault = "Enter start point";
	private final String toDefault = "Enter destination";
	private final String findDefault = "Enter address";

	//GUI background
	private JPanel background, routeInfo;
	private int infoSize; //Size of route info panel

	// The temporary displacement of the buffered image
	private int mousePanX;	
	private int mousePanY;

	/**
	 * Flag types
	 */
	public enum TextType {
		FIND, TO, FROM;
	}

	/**
	 * Constructor for the Window class.
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
		contentPane = getContentPane();
		setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		contentPane.setPreferredSize(new Dimension((int)(600*WindowHandler.getRatio()),600)); //Sets the dimension on the content pane.
		screen = new JLayeredPane();	
		screen.add(Map.use(), JLayeredPane.DEFAULT_LAYER);
		contentPane.add(screen);
		createButtons();
		addButtons();	
		setBackground(new Color(165,191,221));		// Tom's ocean color, courtesy of Google Maps
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
		Map.use().addMouseWheelListener(new MouseWheelZoom());
		//Listeners for when mouse is pressed, dragged or released
		MouseListener mouseListener = new MouseListener();
		Map.use().addMouseListener(mouseListener);
		Map.use().addMouseMotionListener(mouseListener);
		//Listener for the buttons for zoom, pan
		addButtonListeners();
	}

	/**
	 * Redraws the map when its content has changed or 
	 * the window has been resized. 
	 */
	public void updateMap() {
		Map.use().updatePath();
		
		if(!isVisible()){
			Map.use().setBounds(0, 0, contentPane.getPreferredSize().width, contentPane.getPreferredSize().height);		
			addListeners();
		} else {
			requestFocus();			
		}
		Map.use().createBufferImage();
		repaint();
	}

	/**
	 * Creates the buttons which makes up the GUI
	 */
	private void createButtons() {
		//Icons from http://www.iconfinder.com/search/?q=iconset%3Abrightmix
		resetZoom = createButton("ResetZoom.png", "Reset zoom", 73, 73);
		zoomOut = createButton("minus_black.png", "Zoom out", 105, 175);
		zoomIn = createButton("plus.png", "Zoom in", 55, 175);
		west = createButton("West.png", "West", 25, 75);
		east = createButton("East.png", "East", 125, 75);
		north = createButton("North.png", "North",75, 25);
		south = createButton("South.png", "South", 75, 125);

		//Icons for single address search or find path
		findPath = createButton("path_off.png", "Find Path", 100, 215);
		findPathBlue = createButton("path.png", "Find Path", 100, 215);
		findPathBlue.setVisible(false);
		findPlace = createButton("find_off.png", "Find Place", 50, 215);
		findPlaceBlue = createButton("find_red.png", "Find Place", 50, 215);
		findPlace.setVisible(false);

		//Route types (car, bike, ferry)
		bikeUnselected = createButton("cycle_unmarked.png", "By bike or walking", 18, 330);
		bikeUnselected.setVisible(false);
		bikeSelected = createButton("cycle_marked.png", "By bike or walking", 18, 330);
		bikeSelected.setVisible(false);
		carUnselected = createButton("motor_unmarked.png", "By car", 68, 330);
		carUnselected.setVisible(false);
		carSelected = createButton("motor_marked.png", "By car", 68, 330);
		carSelected.setVisible(false);
		shipUnselected = createButton("ferry_unmarked.png", "I would like to travel with ferry", 130, 330);
		shipUnselected.setVisible(false);
		shipSelected = createButton("ferry_marked.png", "I would like to travel with ferry", 130, 330);
		shipSelected.setVisible(false);

		searchFromResultBox = new JComboBox();
		searchToResultBox = new JComboBox();
		searchFindResultBox = new JComboBox();

		from = new JTextField(fromDefault);
		fromText = fromDefault;
		from.setBounds(20, 260, 145, 25);
		from.setVisible(false);

		to = new JTextField(toDefault);
		toText = toDefault;
		to.setBounds(20, 295, 145, 25);
		to.setVisible(false);

		find = new JTextField(findDefault);
		findText = findDefault;
		find.setBounds(20, 260, 145, 25);
		find.setVisible(true);

		search = new JButton("Search");
		search.setBounds(20, 405,70, 20);
		search.setMargin(new Insets(5,5,5,5));
		search.setFont(null);
		search.setVisible(false);

		findButton = new JButton("Find");
		findButton.setBounds(20, 295,70, 20);
		findButton.setMargin(new Insets(5,5,5,5));
		findButton.setFont(null);
		findButton.setVisible(true);

		reset = new JButton("Reset");
		reset.setBounds(95, 295, 70, 20);
		reset.setMargin(new Insets(5,5,5,5));
		reset.setFont(null);
		reset.setVisible(true);

		//Shown in the leftmost lower corner of the GUI
		cityAndZipLabel = new JLabel("");
		cityAndZipLabel.setBounds(20, 600-40, 200, 20);
		cityAndZipLabel.setVisible(true);

		//Background of the menu
		background = new TransparentPane();
		background.setBounds(10,15,165,315);
		routeInfo = new TransparentPane();
		
		fastestRadio = new JRadioButton("Fastest");
		fastestRadio.setSelected(true);
		fastestRadio.setOpaque(false);
		fastestRadio.setBounds(95, 375, 80, 20);
		fastestRadio.setVisible(false);
		
		shortestRadio = new JRadioButton("Shortest");
		shortestRadio.setOpaque(false);
		shortestRadio.setBounds(15, 375, 85, 20);
		shortestRadio.setVisible(false);
	    
		group = new ButtonGroup();
	    group.add(fastestRadio);
	    group.add(shortestRadio);
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
				WindowHandler.zoomOut(1);
			}
		});

		zoomIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				WindowHandler.zoomIn(1);
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
				addressParse(fromText, 185, 260, TextType.FROM);
			}
		});


		from.addMouseListener(new mouseOnText(TextType.FROM));

		to.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				toText = to.getText();
				addressParse(toText, 185, 295, TextType.TO);
			}
		});

		to.addMouseListener(new mouseOnText(TextType.TO));

		find.addActionListener(new ActionListener() { //Find single address text field listener
			public void actionPerformed(ActionEvent evt) {
				findText = find.getText();
				addressParse(findText, 185, 260, TextType.FIND);
			}
		});

		find.addMouseListener(new mouseOnText(TextType.FIND));

		bikeUnselected.addActionListener(new ActionListener(){ //If choosing bike

			public void actionPerformed(ActionEvent evt) {
				byCar = false;
				carSelected.setVisible(false);
				carUnselected.setVisible(true);
				bikeSelected.setVisible(true);
				bikeUnselected.setVisible(false);
				shortestRadio.setVisible(false);
				fastestRadio.setVisible(false);
				search.setBounds(20, 375,70, 20);
				reset.setBounds(95, 375, 70, 20);
				fastest = false; //We want the shortest route if by bike
				background.setBounds(10,15,165,390);
				repositionInfo();
			}
		});

		carUnselected.addActionListener(new ActionListener(){ //If choosing car

			public void actionPerformed(ActionEvent evt) {
				byCar = true;
				carSelected.setVisible(true);
				carUnselected.setVisible(false);
				bikeSelected.setVisible(false);
				bikeUnselected.setVisible(true);
				reset.setBounds(95, 405, 70, 20);
				search.setBounds(20, 405,70, 20);
				shortestRadio.setVisible(true);
				fastestRadio.setVisible(true);
				fastest = true; //We want the fastest route by default if by car
				fastestRadio.setSelected(true);
				background.setBounds(10,15,165,420);
				repositionInfo();
			}
		});

		shipUnselected.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				shipUnselected.setVisible(false);
				shipSelected.setVisible(true);
				byShip = true;
			}
		});

		shipSelected.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				shipSelected.setVisible(false);
				shipUnselected.setVisible(true);
				byShip = false;
			}
		});

		reset.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				from.setText(fromDefault);
				fromText = fromDefault;
				to.setText(toDefault);
				toText = toDefault;
				find.setText(findDefault);
				findText = findDefault;
				screen.remove(searchFromResultBox);
				screen.remove(searchToResultBox);
				screen.remove(routeInfo);
				screen.remove(searchFindResultBox);
				fromMarked = false;
				toMarked = false;
				Map.use().resetPath();
				updateMap();
			}
		});

		search.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				findPath();
			}
		});

		findButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				findPlace();
			}
		});

		findPlace.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				findPlace.setVisible(false);
				findPlaceBlue.setVisible(true);
				findPathBlue.setVisible(false);
				findPath.setVisible(true);

				find.setVisible(true);
				reset.setBounds(95, 295, 70, 20);
				findPath.setVisible(true);
				findPathBlue.setVisible(false);
				to.setVisible(false);
				from.setVisible(false);
				bikeUnselected.setVisible(false);
				bikeSelected.setVisible(false);
				carUnselected.setVisible(false);
				carSelected.setVisible(false);
				searchFromResultBox.setVisible(false);
				searchToResultBox.setVisible(false);
				navigateVisible = false;
				shortestRadio.setVisible(false);
				fastestRadio.setVisible(false);
				shipUnselected.setVisible(false);				
				shipSelected.setVisible(false);
				search.setVisible(false);
				findButton.setVisible(true);
				background.setBounds(10,15,165,315);
				repositionInfo();
			}
		});

		findPath.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				if(!navigateVisible){
					to.setVisible(true);
					from.setVisible(true);
					find.setVisible(false);
					if (byCar) {
						bikeUnselected.setVisible(true);
						carSelected.setVisible(true);
						reset.setBounds(95, 405, 70, 20);
						background.setBounds(10,15,165,420);
						if (fastest) {
							fastestRadio.setVisible(true);
							shortestRadio.setVisible(true);
							fastestRadio.setSelected(true);
						}
						else {
							fastestRadio.setVisible(true);
							shortestRadio.setVisible(true);
							shortestRadio.setSelected(true);
						}
					}
					else {
						bikeSelected.setVisible(true);
						carUnselected.setVisible(true);
						reset.setBounds(95, 375, 70, 20);
						background.setBounds(10,15,165,390);
					}
					navigateVisible = true;
					if (byShip) shipSelected.setVisible(true);
					else shipUnselected.setVisible(true);
					search.setVisible(true);
					findButton.setVisible(false);
					findPath.setVisible(false);
					findPathBlue.setVisible(true);
					findPlace.setVisible(true);
					findPlaceBlue.setVisible(false);
					searchFindResultBox.setVisible(false);
					repositionInfo();
				}
			}
		});
		
		fastestRadio.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				fastest = true;
			}
		});
		
		shortestRadio.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent evt) {
				fastest = false;
			}
		});
	}
	
	/**
	 * Checks the text fields when searching for path.
	 * If filled, parse text andcall pathFinding() from WindowHandler with
	 * the specified parameters.
	 */
	public void findPath() {
		isTextChanged();
		if(!fromMarked) {
			String fromText = from.getText();
			addressParse(fromText, 185, 260, TextType.FROM);
		}
		if (!toMarked) {	
			String toText = to.getText();
			addressParse(toText, 185, 295,TextType.TO);
		}
	
		if(fromMarked && toMarked) {
			Map.use().addFlag(fromFlag, TextType.FROM);
			Map.use().addFlag(toFlag, TextType.TO);
			//Checks if the user is going by car or bike, and if they want the shortest or fastest route
			if(byCar) { 
				if(fastest) {
					WindowHandler.pathFinding(TransportWay.CAR, CompareType.FASTEST, byShip);
				} else {
					WindowHandler.pathFinding(TransportWay.CAR, CompareType.SHORTEST, byShip);
				}
			} else {
				WindowHandler.pathFinding(TransportWay.BIKE, CompareType.SHORTEST, byShip);
			}
		}
	}
	
	/**
	 * When seraching for single address. Parse addres and center on the found node.
	 */
	public void findPlace() {
		// If the text has been changed the user must choose a new address
		if (!findText.equals(find.getText())) findMarked = false;

		if(!findMarked) {
			String findText = find.getText();
			addressParse(findText, 185, 260, TextType.FIND);
		}
		
		if(findMarked) {
			Map.use().addFlag(findFlag, TextType.FIND);
			WindowHandler.centerOnNode(findNode);
		}
	}
	
	/**
	 * If the text has been changed the user must choose a new address
	 */
	public void isTextChanged() {
		if (!fromText.equals(from.getText())) fromMarked = false;
		if (!toText.equals(to.getText())) toMarked = false;
	}

	/**
	 * Parses the address from the search text field to the search box
	 * 
	 * @param text The text to parse
	 * @param x The x-position of the search box to be created
	 * @param y The y-position of the search box to be created
	 * @param t
	 */
	private void addressParse(String text, int x,int y, TextType t){
//		System.out.println("Input: " + text); // Used for white box test
		String[] result = AddressParser.use().parseAddress(text);
		String[] setArray = new String[0];
		String[] zipArray;
		String[] cityNameArray; // used to sort the results

		// The numbers beside the code refers to the white box test
		
		// If there has been typed in a zip code
		if(!(result[4].equals(""))){ // 1
			setArray = new String[1];
			cityNameArray = new String[]{WindowHandler.getZipToCityMap().get(result[4])};
			setArray[0] = result[0]+" " + result[1] + result[2]+" " + result[3] + " " + result[4]+ " " + WindowHandler.getZipToCityMap().get(result[4]);
			setArray[0] = setArray[0].replaceAll("\\s+", " ").trim();
			zipArray = new String[]{result[4]};
		}
		
		// If there has been typed in a city name and no zip code
		else if (!result[5].equals("")) { // 2
			HashMap<String, String> zipToCityMap = WindowHandler.getZipToCityMap();
			Set<String> zips = zipToCityMap.keySet();
			ArrayList<String> zipList = new ArrayList<String>();
			for (String zip : zips) { // 3
				if (result[5].toLowerCase().equals(zipToCityMap.get(zip).toLowerCase())) { // 4
					if (result[0].equals("")) { // 5
						zipList.add(zip);
					}
					else if (WindowHandler.getRoadToZipMap().get(result[0]).contains(zip)) { // 6
						zipList.add(zip);
					}
				}
			}
			cityNameArray = new String[zipList.size()];
			setArray = new String[zipList.size()];
			for (int i = 0; i < setArray.length; i++) { // 7
				setArray[i] = result[0]+" " + result[1] + result[2]+" " + result[3] + " " + zipList.get(i) + " " + result[5];
				setArray[i] = setArray[i].replaceAll("\\s+", " ").trim();
				cityNameArray[i] = result[5];
			}
			zipArray = Arrays.copyOf(zipList.toArray(), zipList.size(), String[].class);
		}
		// If there has been typed in no city name or zip code
		else{
			HashSet<String> set = WindowHandler.getRoadToZipMap().get(result[0]);
			if (set == null) setArray = new String[0]; // 8
			else setArray = set.toArray(new String[0]);
			cityNameArray = new String[setArray.length];
			zipArray = new String[setArray.length];
			for (int i = 0; i < setArray.length; i++) { // 9
				zipArray[i] = setArray[i];
			}
			for(int i = 0; i < setArray.length; i++){ // 10
				String city = WindowHandler.getZipToCityMap().get(setArray[i]);
				cityNameArray[i] = city;
				setArray[i] = result[0]+" " + result[1] + result[2]+" " + result[3] + " " + setArray[i] + " " + city;
				setArray[i] = setArray[i].replaceAll("\\s+", " ").trim();				
			}						
		}
		if (zipArray.length == 0) setArray = new String[]{"No results"}; // 11
		// sort the result according to the city name
		Arrays.sort(cityNameArray);
		for (int i = 0; i < cityNameArray.length; i++) { // 12
			for (int j = 0; j < setArray.length; j++) { // 13
				if (setArray[j].contains(cityNameArray[i])) { // 14
					String temp = setArray[i]; setArray[i] = setArray[j]; setArray[j] = temp;
					temp = zipArray[i]; zipArray[i] = zipArray[j]; zipArray[j] = temp;
					break;
				}
			}
		}
//		System.out.print("Array of matching addresses: "); // Used for white box test
//		for (String s : setArray) System.out.println(s + ", "); // Used for white box test
		if (zipArray.length == 1) { //If only one possible address is found
			chooseAddress(zipArray, result, t, 0, setArray[0]); // 15
//			System.out.println("Locating address"); // Used for white box test
		}
		else {
			createSearchBox(setArray,zipArray,result,x,y,t);
//			System.out.println("Creating combo box"); // Used for white box test
		}
	}
	
	/**
	 * If only one possible address is found from text field or when choosing an address
	 * from a search result combo box. Reads the text input.
	 */
	private void chooseAddress(String[] zipArray, String[] textArray, TextType t, int i, String text) {
		// if the zip array is empty, the search yielded no results
		if (zipArray.length == 0) return;

		if(t == TextType.FROM) fromMarked = true; //register if the from or to combo box is marked
		else if (t == TextType.TO) toMarked = true;
		else if (t == TextType.FIND) findMarked = true;
		Edge randomCorrectEdge = null;
		Node flagNode = null;
		// if there is no road name we look for a random road in the zip code area
		if (textArray[0].equals("")) {
			ArrayList<Edge> allEdgesForZip = new ArrayList<Edge>();
			HashSet<Integer> disallowedTypes = DijkstraSP.getDisallowedTypes();
			for(Edge edge : WindowHandler.getEdges()){
				if (edge.getV_POSTNR().equals(zipArray[i]) && edge.getH_POSTNR().equals(zipArray[i]) && !disallowedTypes.contains(edge.getType())) {
					allEdgesForZip.add(edge);
				}
			}
			randomCorrectEdge = allEdgesForZip.get(allEdgesForZip.size()/2);
		}
		else {
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
		}
		if (randomCorrectEdge != null){
			WindowHandler.setNode(randomCorrectEdge.getFromNode().getKdvID(), t);
			flagNode = randomCorrectEdge.getFromNode();
		}
		if (t == TextType.FIND) searchFindResultBox.setVisible(false);
		else if (t == TextType.TO) searchToResultBox.setVisible(false);
		else if (t == TextType.FROM) searchFromResultBox.setVisible(false);
		
		if (flagNode != null) {
			if(t == TextType.FROM){
				from.setText(text);
				fromText = text;
				double x = flagNode.getXCord();
				double y = flagNode.getYCord();
				fromFlag.setPosition(x, y);
				Map.use().addFlag(fromFlag, t);
			}
			else if (t == TextType.TO) {
				to.setText(text);
				toText = text;
				double x = flagNode.getXCord();
				double y = flagNode.getYCord();
				toFlag.setPosition(x, y);
				Map.use().addFlag(toFlag, t);
			}
			else if (t == TextType.FIND) {
				find.setText(text);
				findText = text;
				double x = flagNode.getXCord();
				double y = flagNode.getYCord();
				findFlag.setPosition(x, y);
				Map.use().addFlag(findFlag, t);
				findNode = flagNode;
			}
		}
		isTextChanged();
		if (fromMarked && toMarked && !(t == TextType.FIND)) findPath();
		else if (t == TextType.FIND) findPlace();
		else if (fromMarked || toMarked) WindowHandler.centerOnNode(flagNode);
		else updateMap();
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
			searchFromResultBox.addActionListener(new comboBoxListener(zipArray, textArray, t));
			screen.add(searchFromResultBox, JLayeredPane.PALETTE_LAYER);
		} else if (t == TextType.TO) {
			screen.remove(searchToResultBox);
			searchToResultBox = new JComboBox(addressArray);
			searchToResultBox.setBounds(x,y ,200,25);
			searchToResultBox.addActionListener(new comboBoxListener(zipArray, textArray, t));
			screen.add(searchToResultBox, JLayeredPane.PALETTE_LAYER);
		}	
		else if (t == TextType.FIND) {
			screen.remove(searchFindResultBox);
			searchFindResultBox = new JComboBox(addressArray);
			searchFindResultBox.setBounds(x, y, 200, 25);
			searchFindResultBox.addActionListener(new comboBoxListener(zipArray, textArray, t));
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
		screen.add(findPathBlue, JLayeredPane.PALETTE_LAYER);
		screen.add(bikeUnselected, JLayeredPane.PALETTE_LAYER);
		screen.add(carUnselected, JLayeredPane.PALETTE_LAYER);
		screen.add(bikeSelected, JLayeredPane.PALETTE_LAYER);
		screen.add(carSelected, JLayeredPane.PALETTE_LAYER);
		screen.add(fastestRadio, JLayeredPane.PALETTE_LAYER);
		screen.add(shortestRadio, JLayeredPane.PALETTE_LAYER);
		screen.add(shipUnselected, JLayeredPane.PALETTE_LAYER);
		screen.add(shipSelected, JLayeredPane.PALETTE_LAYER);
		screen.add(reset, JLayeredPane.PALETTE_LAYER);
		screen.add(findPlace, JLayeredPane.PALETTE_LAYER);
		screen.add(findPlaceBlue, JLayeredPane.PALETTE_LAYER);
		screen.add(background, JLayeredPane.PALETTE_LAYER);
		screen.add(cityAndZipLabel, JLayeredPane.PALETTE_LAYER);
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

	/**
	 * Adds the path info below the menu when a route is found.
	 */
	public void addPathInfo(TransportWay transport) {
		double dist = (Map.use().getPathLengt()/1000);
		int hour = (int)Map.use().getDriveTime()/60, min = (int)Map.use().getDriveTime()%60;
		DecimalFormat df = new DecimalFormat();
		if(routeInfo != null) screen.remove(routeInfo);
		routeInfo = new TransparentPane();
		String distStr;
		if(dist < 1) {
			df.applyPattern(".###");
			df.setRoundingMode(RoundingMode.HALF_UP);
			distStr = ("Distance: " + df.format(dist).replaceAll("\\.|,", "") + " m");
		} else if (dist < 100) {
			df.applyPattern("###.#");
			df.setRoundingMode(RoundingMode.HALF_UP);
			distStr = "Distance: " + df.format(dist) + " km";
		} else {
			df.applyPattern("####");
			df.setRoundingMode(RoundingMode.HALF_UP);
			distStr = "Distance: " + df.format(dist) + " km";
		}
		String timeStr;
		if(hour < 1) {

			timeStr = ("Time: " + min + " min");
		} else {
			if(min < 10) {
				timeStr = ("Time: " + hour + ":0" + min + "h");
			} else {
				timeStr = ("Time: " + hour + ":" + min + "h");
			}
		}
		if (transport == TransportWay.CAR) { 
			infoSize = 50;
			routeInfo.setLayout(new GridLayout(2, 1));
			routeInfo.add(new JLabel(distStr, SwingConstants.HORIZONTAL));
			routeInfo.add(new JLabel(timeStr, SwingConstants.HORIZONTAL));
		}
		else {
			infoSize = 25;
			routeInfo.add(new JLabel(distStr, SwingConstants.HORIZONTAL));
		}
		repositionInfo();
		if(dist != -1)  {
			routeInfo.setVisible(true);
		} else {
			routeInfo.setVisible(false);
		}
		screen.add(routeInfo, JLayeredPane.PALETTE_LAYER);
	}

	/**
	 * Repositions the menu and path info backgrounds
	 */
	private void repositionInfo() {
		Rectangle r = background.getBounds();
		routeInfo.setBounds(10, (r.height + r.y +5), r.width, infoSize);
		if(Map.use().getPathLengt() != -1)  {
			routeInfo.setVisible(true);
		} else {
			routeInfo.setVisible(false);
		}
	}

	/**
	 * Method for drawing the rectangle to show where the user is dragging for zoom
	 * The method compares where the user is dragging from and to, and hereby calculates
	 * the rectangle.
	 */
	static class DrawRect extends JComponent {
		public void paint(Graphics g) {
			super.paint(g);
			g.setColor(Color.orange);
			if(getMousePosition() != null && !Window.use().noMoreBoxes) {
				if(pressedX < getMousePosition().x && pressedY < getMousePosition().y) {
					g.drawRect(pressedX, pressedY, getMousePosition().x - pressedX, getMousePosition().y - pressedY);
				}
				else if(pressedX > getMousePosition().x && pressedY > getMousePosition().y) {
					g.drawRect(getMousePosition().x, getMousePosition().y, pressedX - getMousePosition().x, pressedY - getMousePosition().y);
				}
				else if(pressedX < getMousePosition().x && pressedY > getMousePosition().y) {
					g.drawRect(pressedX, getMousePosition().y, getMousePosition().x - pressedX, pressedY - getMousePosition().y);
				}
				else if(pressedX > getMousePosition().x && pressedY < getMousePosition().y) {
					g.drawRect(getMousePosition().x, pressedY, pressedX - getMousePosition().x, getMousePosition().y - pressedY);
				}
			}
		}
	}

	/**
	 * Adds a key listener used to move around the map.
	 */
	class MKeyListener extends KeyAdapter {
		/**
		 * Adds a key listener that sends the pressed button to the pan method of WindowHandler.
		 */
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()){
			case KeyEvent.VK_SUBTRACT:
				WindowHandler.zoomOut(1);
				break;
			case KeyEvent.VK_MINUS:
				WindowHandler.zoomOut(1);
				break;
			case KeyEvent.VK_PLUS:
				WindowHandler.zoomIn(1);
				break;
			case KeyEvent.VK_ADD:
				WindowHandler.zoomIn(1);
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
	 * Combo box listener. Listens if the user chooses one of the possible results.
	 */
	private class comboBoxListener implements ActionListener {
		TextType t; //From, to or find comboBox
		String[] zipArray, textArray;

		public comboBoxListener(String[] zipArray, String[] textArray, TextType t) {
			this.t = t;
			this.zipArray = zipArray;
			this.textArray = textArray;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int i = 0;
			String selected = "";
			if (t == TextType.FIND) {
				i = searchFindResultBox.getSelectedIndex();
				selected = (String) searchFindResultBox.getSelectedItem();
			}
			else if (t == TextType.FROM) {
				i = searchFromResultBox.getSelectedIndex();
				selected = (String) searchFromResultBox.getSelectedItem();
			}
			else if (t == TextType.TO) {
				i = searchToResultBox.getSelectedIndex();
				selected = (String) searchToResultBox.getSelectedItem();
			}
			chooseAddress(zipArray, textArray, t, i, selected);
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
		final int MIN_HEIGHT = 550;

		public void componentResized(ComponentEvent evt) {
			if(timer == null){
				timer = new Timer(600, new ResizeTask());
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
						if (Window.use().getHeight() < MIN_HEIGHT)
							Window.use().setPreferredSize(new Dimension((int)(MIN_HEIGHT*WindowHandler.getRatio()), MIN_HEIGHT));
						else
							Window.use().setPreferredSize(new Dimension((int)(Window.use().getHeight()*WindowHandler.getRatio()), Window.use().getHeight()));
					} else if(Math.abs(width - Window.use().getWidth())>0 && Window.use().getHeight() < maxHeight){
						if (Window.use().getWidth()/WindowHandler.getRatio() < MIN_HEIGHT)
							Window.use().setPreferredSize(new Dimension((int)(MIN_HEIGHT*WindowHandler.getRatio()), MIN_HEIGHT));
						else
							Window.use().setPreferredSize(new Dimension(Window.use().getWidth(), (int)(Window.use().getWidth()/WindowHandler.getRatio())));
					}
				} else {
					Window.use().setPreferredSize(new Dimension((int)(maxHeight*WindowHandler.getRatio()), maxHeight));
				}
				cityAndZipLabel.setBounds(20, contentPane.getHeight()-35, 200, 20);
				pack();
				Map.use().setSize(Window.use().getSize());
				Map.use().updatePix();
				height = Window.use().getHeight();
				width = Window.use().getWidth();
				timer = null;
				updateMap();
			}
		}
	}

	/**
	 * Listens if the mouse is pressed, dragged, moved or released.
	 */
	private class MouseListener extends MouseInputAdapter {
		int prevX;
		int prevY;

		public void mousePressed(MouseEvent e) {
			if(rect == null){
				rect = new DrawRect();
			}
			if (SwingUtilities.isRightMouseButton(e)) {
				pressedX = e.getX();
				pressedY = e.getY();
				noMoreBoxes = false;
			} else if (SwingUtilities.isLeftMouseButton(e)) {
				pressedX = e.getX();
				pressedY = e.getY();
			}
		}

		/**
		 * Right dragging draws zoom box, left dragging calls panning.
		 */
		public void mouseDragged(MouseEvent e) {			
			if (SwingUtilities.isRightMouseButton(e) && !noMoreBoxes) {
				rect = new DrawRect();
				rect.setBounds(0, 0, contentPane.getWidth(), contentPane.getHeight());
				screen.add(rect, JLayeredPane.POPUP_LAYER);
			}
			else if (SwingUtilities.isLeftMouseButton(e)) {
				if (!dragging) {
					prevX = e.getX();					// Before dragging starts, prevX and prevY is set to 
					prevY = e.getY();					// the current cursor location.
					dragging = true;					// Dragging is then set to begin.		
				}										
				else {
					setMousePanX(e.getX() - prevX);		// While we are dragging, mousePanX and mousePanY is continually set to 
					setMousePanY(e.getY() - prevY);		// difference between cursors start location and cursors current location.
					repaint();							// The Window is then continually repainted using the override 
				}										// method paint() in Map.
			}
		}

		/**
		 *  Sets the tooltip with the road name and the label at the bottom 7
		 *  of the screen with the city name and zip
		 */
		public void mouseMoved(MouseEvent e){
			Edge closestEdge = WindowHandler.closestEdge(e.getX(), e.getY());
			if (!(closestEdge == null)) {
				String roadName;
				if(closestEdge.getVEJNAVN().length() > 0) {
					roadName = closestEdge.getVEJNAVN();
				} else {
					roadName =  "No name found";
				}
				Map.use().setToolTipText(roadName);
				String zip = closestEdge.getH_POSTNR();
				String cityName = WindowHandler.getZipToCityMap().get(zip);
				if (cityName != null) {
					cityAndZipLabel.setText(zip + " " + cityName);
				}
			}
		}

		/**
		 * When the mouse is released from dragging, either by left or right dragging,
		 * a search is called to draw the new map.
		 */
		public void mouseReleased(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				releasedX = e.getX();
				releasedY =  e.getY();
				WindowHandler.pixelSearch(pressedX, releasedX, pressedY, releasedY);
				rect.setVisible(false); //Removes the rectangle when zoom box is chosen
				noMoreBoxes = true;
			}
			else if (SwingUtilities.isLeftMouseButton(e)) {
				if (dragging) {
					PanHandler.pixelPan((prevX-e.getX()), (e.getY()-prevY));	// When mouse is released, the new map data is calculated.
					setMousePanX(0);											// mousePanX and mousePanY is reset to zero.
					setMousePanY(0);
					dragging = false;											// Dragging ends.
				}
			}
		}
	}
	
	/**
	 * Listener to mouse wheel for zooming
	 */
	private class MouseWheelZoom implements MouseWheelListener{
		int zoomCount;
		MouseWheelMovedZoom zoomListener;
		
		MouseWheelZoom() {
			zoomListener = new MouseWheelMovedZoom();
		}
		
		/**
		 * When scrolling with mouse when, delay in case of scolling many layers up/down.
		 * Then, call MouseWheelMovedZoom.
		 */
		public void mouseWheelMoved(MouseWheelEvent e) {
			int notches = e.getWheelRotation();
			if (notches < 0)
				zoomCount -= 1;
			else
				zoomCount += 1;
			if(timer == null){
				timer = new Timer(50, zoomListener);
				timer.start();
			}
			timer.restart();
		}
		
		/**
		 * Zoom in/out according to number of notches
		 */
		private class MouseWheelMovedZoom implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				timer.stop();
				if (zoomCount > 10) zoomCount = 5;
				else if (zoomCount < -10) zoomCount = -5;
				if (zoomCount < 0) {
					WindowHandler.zoomIn(Math.abs(zoomCount));
				} else {	            
					WindowHandler.zoomOut(Math.abs(zoomCount));
				}
				zoomCount = 0;
			}
		}
	}

	/**
	 * Mouse listener for when clicking text fields
	 */
	private class mouseOnText extends MouseAdapter {
		private TextType t;

		mouseOnText(TextType t) {
			this.t = t;
		}

		/**
		 * When pressing the text fields the default text should disappear,
		 *allowing the user to input
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			if (TextType.FROM == t && from.getText().equals(fromDefault)) from.setText("");
			else if (t == TextType.TO && to.getText().equals(toDefault)) to.setText("");
			else if (t == TextType.FIND && find.getText().equals(findDefault)) find.setText("");			
		}
	}
	
	/**
	 * Class for drawing background for menu and route info.
	 */
	class TransparentPane extends JPanel{

		public TransparentPane() {
			super();
			setBackground(new Color(0,0,0,50)); // black
			setOpaque(false);
		}

		protected void paintComponent(Graphics g) {
			g.setColor( getBackground() );
			g.fillRect(0, 0, getWidth(), getHeight());
			super.paintComponent(g);
		}
	}

	/**
	 * Getter method for the width of the map.
	 * @return The width of map in pixel.
	 */
	public int getMapWidth() {
		return contentPane.getWidth();
	}

	/**
	 * Getter method for the height of the map.
	 * @return The height of map in pixel.
	 */
	public int getMapHeight() {
		return contentPane.getHeight();
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

	/**
	 * Setter for the maxHeight field. 
	 * maxHeight defines the maximal height of the application window.
	 * @param maxH The maximum height in pixel.
	 */
	public static void setMaxHeight(int maxH) {
		maxHeight = maxH;
	}
	
	public Flag getFindFlag() {
		return findFlag;
	}
}
