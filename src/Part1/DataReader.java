package Part1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.JOptionPane;

import Part1.SplashScreen.Task;
import QuadTree.QuadTree;


/**
 * The DataReader is used to extract information about <b>Nodes</b> and <b>Edges</b> 
 * contained within two <i>.txt-files</i>. 
 * Furthermore the DataReader class is capable of creating a graph and a quad-tree based on the information
 * extracted from the files. 
 * After extraction of the information from both files the DataReader instance can create an <i>
 * ArrayList</i> containing all nodes, which in turn is used for creating the graph and the quad-tree.
 * 
 * The data-files used to store the information about nodes and edges has to be formated as
 * it is the case within the data-files handed out by <b>www.Krak.dk</b>.
 * 
 * The <b>DataReader</b> class implements the singleton design pattern. 
 * 
 * 
 *
 */
public class DataReader {
	private static DataReader instance;
	private ArrayList<Node> nodes;
	private ArrayList<Edge> edges;
	private ArrayList<Edge> longestRoads;
	private final String nodeFile, edgeFile;
	private static double maxX = 0, maxY = 0, minX = -1, minY = -1;
	private HashMap<String, HashSet<String>> roadToZipMap;
	private final int increase = 75000;

	/**
	 * Constructor for the DataReader class. 
	 * The constructor stores the files which are required for using the
	 * class methods. 
	 * The Constructor is private to support the singleton implementation.
	 * 
	 * @param nodeFile The path for where the file containing information about the nodes is stored.
	 * @param edgeFile The path for where the file containing information about the edges is stored.
	 */
	private DataReader(String nodeFile, String edgeFile) {
		this.nodeFile = nodeFile;
		this.edgeFile = edgeFile;
	}
	
	public static void resetInstance() {
		instance = null;
	}
	
	/**
	 * This method makes this the <b>DataReader</b> class to a singleton. 
	 * The method checks if an instance of the class already has been created
	 * and returns the current if so. Creates and returns a new instance of the
	 * <b>DataReader</b> if none exists. 
	 * 
	 * @param nodeFile The path for where the file containing information about the nodes is stored.
	 * @param edgeFile The path for where the file containing information about the edges is stored.
	 * @return Returns the instance of <b>DataReader</b> which currently is in use. 
	 */
	public static DataReader use(String nodeFile, String edgeFile) {
		if(instance == null) 
			instance = new DataReader(nodeFile, edgeFile);
		return instance;
	}

	/**
	 * The method extracts information about the nodes from the <i>nodeFile</i>
	 * and creates <b>Node</b> objects corresponding to the extracted information.
	 * <p>
	 * This class will <b>terminate</b> the program if the file path is invalid or the
	 * file is unreadable. 
	 */
	public void createNodeList(){
		SplashScreen.use().setTaskName(Task.NODES);
		// open the file containing the list of nodes
		try {
			Reader reader = new InputStreamReader(new FileInputStream(nodeFile), "UTF-8");
		BufferedReader br = new BufferedReader(reader);
		br.readLine(); // discard names of columns which is the first line

		String line = br.readLine();
		maxX = 0;
		maxY = 0;
		minX = Integer.MAX_VALUE;
		minY = Integer.MAX_VALUE;
		// An array list containing the nodes we find in the file
		nodes = new ArrayList<Node>();
		nodes.add(null);
		while (line != null) {
			// Splits "line" by ',' and parses the id, x and y values to
			String[] lineArray = line.split(",");
			double x = Double.parseDouble(lineArray[3]);
			if (maxX < x) maxX = x; 
			double y = Double.parseDouble(lineArray[4]);
			if (maxY < y) maxY = y;
			int id = Integer.parseInt(lineArray[1]);
			if (minX > x) minX = x; 
			if (minY > y) minY = y;  

			nodes.add(new Node(x, y, id));
			SplashScreen.use().updateProgress();
			line = br.readLine();
		}
		// Increases the area of the map
		minY -= increase;
		maxY += increase;
		double add = ((maxY-minY)*WindowHandler.getRatio() - (maxX-minX))/2;
		if(add < increase) {
			minX -= increase;
			maxX += increase;
			WindowHandler.setRatio((maxX-minX)/(maxY-minY));
		} else {
			minX -= add;
			maxX += add;
		}
		
		// The coordinates of every node is corrected for the offset
		Node.setXOffset(minX);
		Node.setYOffset(minY);
		br.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "The \"nodeFile\" was not found. \nThe program will terminate.", "ERROR", JOptionPane.ERROR_MESSAGE);
			throw new RuntimeException();
		}
	}

	/**
	 * The method creates a <b>Graph</b> based on the information within the "<i>edgeFile</i>.
	 * Before storing the information in the graph the method constructs <b>Edge</b> object based on the information.
	 * These objects are  stored in the graph object.
	 * <p>
	 * This class will <b>terminate</b> the program if the file path is invalid or the
	 * file is unreadable.  
	 * 
	 * @param longestRoadsFloor An int describing the floor value of the elements stored in the ArrayList <b>longestRoads</b>. 
	 * @return Returns a <b>Graph</b> object based on the content <i>edgeFile</i>.
	 */
	public Graph createGraphAndLongestRoadsList(int longestRoadsFloor) {

		SplashScreen.use().setTaskName(Task.EDGES);
		longestRoads = new ArrayList<Edge>();
		try {
			// Create hash map where road name gets mapped to a list of zip codes			
			roadToZipMap = new HashMap<String, HashSet<String>>();
			
			// Create a graph on the nodes
			Graph graph = new Graph(nodes.size());
			
			// Reads the "kdv_unload.txt" file into the buffer.
			Reader reader = new InputStreamReader(new FileInputStream(edgeFile), "UTF-8");
			BufferedReader br = new BufferedReader(reader);
	
			br.readLine(); // again discarding column names
			String line = br.readLine();
			
			edges = new ArrayList<Edge>();
			
			HashSet<Integer> projectedRoads = new HashSet<Integer>(Arrays.asList(21,22,23,24,25,26,28));
			HashSet<Integer> tunnels = new HashSet<Integer>(Arrays.asList(41,42,43,44,45,46,48));
			HashSet<Integer> exits = new HashSet<Integer>(Arrays.asList(32,33,34,35,36,38));
			
			while (line != null) {
				String[] lineArray = line.split(",(?! |[a-zA-ZæÆøØåÅ])"); // Regex matches ',' not followed by spaces of letters.
				Node fromNode = nodes.get(Integer.parseInt(lineArray[0]));
				Node toNode = nodes.get(Integer.parseInt(lineArray[1]));
				double length = Double.parseDouble(lineArray[2]);
				int type = Integer.parseInt(lineArray[5]);
				if (projectedRoads.contains(type)) {
					line = br.readLine();
					SplashScreen.use().updateProgress();
					continue;
				}
				// tunnels are set to same type as the the roads. e.g. motorwaytunnel (41) is set to motorway (1)
				if (tunnels.contains(type)) type -= 40;
				// exits are set to the same type as the roads
				if (exits.contains(type)) type -= 30;
				// Motorway exits are set to the same type as motortrafficway
				if (type == 31) type = 2;
				int houseNumberFromLeft = 0;
				int houseNumberToLeft = 0;
				int houseNumberFromRight = 0;
				int houseNumberToRight = 0;
				if (!lineArray[7].equals("''")) houseNumberFromLeft = Integer.parseInt(lineArray[7]);
				if (!lineArray[8].equals("''")) houseNumberToLeft = Integer.parseInt(lineArray[8]);
				if (!lineArray[9].equals("''")) houseNumberFromRight = Integer.parseInt(lineArray[9]);
				if (!lineArray[10].equals("''")) houseNumberToRight = Integer.parseInt(lineArray[10]);
				String vejnavn = lineArray[6].substring(1,lineArray[6].length()-1);
				String v_postnr = lineArray[17];
				String h_postnr = lineArray[18];
				double driveTime = Double.parseDouble(lineArray[26]);
				String oneway = lineArray[27].replace("'", "");
				if (!vejnavn.equals("")) {
					if (roadToZipMap.containsKey(vejnavn)) {
						roadToZipMap.get(vejnavn).add(v_postnr);
						roadToZipMap.get(vejnavn).add(h_postnr);
					}
					else {
						HashSet<String> set = new HashSet<String>();
						set.add(v_postnr);
						set.add(h_postnr);
						roadToZipMap.put(vejnavn, set);
					}
				}
				
				// Check if the road is one way or two way
				if (oneway.equals("ft")) {
					Edge edge = new Edge(fromNode, toNode, length, vejnavn, type, v_postnr, h_postnr, driveTime,
							houseNumberFromLeft, houseNumberToLeft, houseNumberFromRight, houseNumberToRight, true);
					if (length > longestRoadsFloor) longestRoads.add(edge);
					edges.add(edge);
					graph.addEdge(edge);
				}
				else if (oneway.equals("tf")) {
					Edge edge = new Edge(toNode, fromNode, length, vejnavn, type, v_postnr, h_postnr, driveTime,
							houseNumberFromLeft, houseNumberToLeft, houseNumberFromRight, houseNumberToRight, true);
					if (length > longestRoadsFloor) longestRoads.add(edge);
					edges.add(edge);
					graph.addEdge(edge);
				}
				// if the road is two way, only one of the ways is drawn
				else {
					Edge fEdge = new Edge(fromNode, toNode, length, vejnavn, type, v_postnr, h_postnr, driveTime,
							houseNumberFromLeft, houseNumberToLeft, houseNumberFromRight, houseNumberToRight, true);
					Edge tEdge = new Edge(toNode, fromNode, length, vejnavn, type, v_postnr, h_postnr, driveTime,
							houseNumberFromLeft, houseNumberToLeft, houseNumberFromRight, houseNumberToRight, false);
					if (length > longestRoadsFloor) longestRoads.add(fEdge);
					edges.add(tEdge);
					edges.add(tEdge);
					graph.addEdge(fEdge);
					graph.addEdge(tEdge);
				}
				
				line = br.readLine();
				SplashScreen.use().updateProgress();
			}
			br.close();
			return graph;
		} 
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "The \"edgeFile\" was not foud. \nThe program will terminate.", "ERROR", JOptionPane.ERROR_MESSAGE);
			throw new RuntimeException();
		}
	}

	/**
	 * The method creates an instance of the <b>QuadTree</b> class and inserts all the existing nodes within it.
	 * 
	 * @return Returns a <b>QuadTree</b> object containing all nodes created in the <i>createNodeList()</i> method.
	 */
	public QuadTree createQuadTree() {
		SplashScreen.use().setTaskName(Task.GRAPH);
		if(nodes == null) createNodeList();
		// Create QuadTree
		QuadTree QT = new QuadTree(5, maxX - minX, maxY - minY);
		for (int i = 1; i < nodes.size(); i++) { //For loop start at index 1 because index 0 is null.
			QT.insert(nodes.get(i));
		}

		return QT;
	}
	
	/**
	 * The method creates a <b>ArrayList</b> of CoastPoint arrays. Each CoastPoint array
	 * represent all the points used to create a given piece of land/water depending on the 
	 * data within the input file.
	 * <p>
	 * This class will <b>terminate</b> the program if the file path is invalid or the
	 * file is unreadable.  
	 * 
	 * @param filepath The directory of the file containing the data. 
	 * @return Returns An ArrayList of CoastPoint arrays.d
	 */
	public ArrayList<CoastPoint[]> readCoast(String filepath) {
		ArrayList<CoastPoint[]> coast = new ArrayList<CoastPoint[]>();

		try {
			Reader reader = new InputStreamReader(new FileInputStream(filepath), "UTF-8");
			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine().trim();
			ArrayList<CoastPoint> current = null;
			while(!(line == null)) {
				SplashScreen.use().updateProgress();
				//Creates a new array if the input matches ">".
				if(line.contains(">")) {
					line = br.readLine().trim();
					if(current == null) {
						current = new ArrayList<CoastPoint>();
					} else {
						//Adds the old array to the ArrayList.
						coast.add(current.toArray(new CoastPoint[1]));
						current = new ArrayList<CoastPoint>();
					}
					continue;
				}
				double lon = Double.parseDouble(line.substring(0, line.indexOf("\t")).trim());
				double lat = Double.parseDouble(line.substring(line.indexOf("\t")+1).trim());
				//Adds the CoastPoint returned by the Equation class to the array.
				current.add(Equation.convertLonLatToUTM(lat, lon));

				line = br.readLine();
				}
			coast.add(current.toArray(new CoastPoint[1]));
			reader.close();			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "The file <" + filepath+ "> was not foud. \nThe program will terminate.", "ERROR", JOptionPane.ERROR_MESSAGE);
			throw new RuntimeException();
		}
		return coast;
	}
	
	/**
	 * Creates a HashMap containing all zip-codes and their related city.
	 * 
	 * @param zipFile The directory of the input file.
	 * @return An HashMap containing all zip-codes as keys and the related city as value.  
	 */
	public HashMap<String, String> getZipToCityMap(String zipFile) {
		HashMap<String, String> zipToCityMap = new HashMap<String, String>();
		try {
			Reader reader = new InputStreamReader(new FileInputStream(zipFile), "UTF-8");
			BufferedReader br = new BufferedReader(reader);
		String strLine = br.readLine();
		while(strLine != null) {
			String zipCode = strLine.split(" ", 2)[0];
			String cityName = strLine.split(" ", 2)[1];

			zipToCityMap.put(zipCode, cityName);
			strLine = br.readLine();
		}
		br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return zipToCityMap;
	}
	
	/**
	 * Getter method for the <i>maxX</i> field.
	 * 
	 * @return Returns the current value of the <i>maxX</i> field.
	 */
	public static double getMaxX() {
		return maxX;
	}

	/**
	 * Getter method for the <i>maxY</i> field.
	 * 
	 * @return Returns the current value of the <i>maxY</i> field.
	 */
	public static double getMaxY() {
		return maxY;
	}

	/**
	 * Getter method for the <i>minX</i> field.
	 * 
	 * @return Returns the current value of the <i>minX</i> field.
	 */
	public static double getMinX() {
		return minX;
	}

	/**
	 * Getter method for the <i>minY</i> field.
	 * 
	 * @return Returns the current value of the <i>minY</i> field.
	 */
	public static double getMinY() {
		return minY;
	}

	/**
	 * Getter method for the <i>longestRoads</i> field.
	 * 
	 * @return Returns the current ArrayList for the <i>longestRoads</i> field.
	 */
	public ArrayList<Edge> getLongestRoads() {
		return longestRoads;
	}
	
	/**
	 * Getter for the roadToZipMap field.
	 * roadToZipMap contains zip-codes as keys and all related street names as values.
	 * 
	 * @return An HashMap with strings as keys and a HashSet of strings as values.
	 */
	public HashMap<String, HashSet<String>> getRoadToZipMap() {
		return roadToZipMap;
	}

	/**
	 * Getter for the edge array.
	 * @return An array containing all edges in the system.
	 */
	public List<Edge> getEdges() {
		return edges;
	}
	
	/**
	 * Getter for the nodes array.
	 * 
	 * @return An ArrayList of all nods.
	 */
	public ArrayList<Node> getNodes() {
		return nodes;
	}
	
	/**
	 * Getter for the increase field.
	 * The increase filed contains the value which has been added to the 
	 * map i order to see some of the area around Denmark.
	 * 
	 * @return The values of increase.
	 */
	public int getIncrease() {
		return increase;
	}
}
