package Part1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
	public ArrayList<Node> nodes;
	private ArrayList<Edge> edges;
	private ArrayList<Edge> longestRoads;
	private final String nodeFile, edgeFile;
	public static double maxX = 0, maxY = 0, minX = -1, minY = -1;
	private HashMap<String, HashSet<String>> roadToZipMap;

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
	
	public void setInstance() {
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
		BufferedReader br = new BufferedReader(new FileReader(nodeFile));

		br.readLine(); // discard names of columns which is the first line

		String line = br.readLine();

		// An array list containing the nodes we find in the file
		nodes = new ArrayList<Node>();
		nodes.add(null);
		while (line != null) {
			// Splits "line" by ',' and parses the id, x and y values to
			String[] lineArray = line.split(",");
			double x = Double.parseDouble(lineArray[3]);
			if (maxX < x)
				maxX = x;
			double y = Double.parseDouble(lineArray[4]);
			if (maxY < y)
				maxY = y;
			int id = Integer.parseInt(lineArray[1]);
			if (minX > x || minX < 0) // Is allowed because all our coordinates are positive.
				minX = x;
			if (minY > y || minY < 0) // Is allowed because all our coordinates are positive.
				minY = y;

			nodes.add(new Node(x, y, id));
			SplashScreen.use().updateProgress();
			line = br.readLine();
		}
		
		// The coordinates of every node is corrected for the offset
		System.out.println("Offset x: " + minX);
		System.out.println("Offset y: " + minY);
		Node.setXOffset(minX);
		Node.setYOffset(minY);
		System.out.println("Width of map: " + (maxX-minX));
		System.out.println("Height of map: " + (maxY-minY));
		br.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "The \"nodeFile\" was not found. \nThe program will terminate.", "ERROR", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
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
//	public MultiGraph createGraphAndLongestRoadsList(int longestRoadsFloor) {
		SplashScreen.use().setTaskName(Task.EDGES);
		System.out.println("Adding " + (nodes.size()-1) + " nodes to graph");
		longestRoads = new ArrayList<Edge>();
		try {
			// Create hash map where road name gets mapped to a list of zip codes			
			roadToZipMap = new HashMap<String, HashSet<String>>();
			
			// Create a graph on the nodes
			Graph graph = new Graph(nodes.size());
//			MultiGraph multiGraph = new MultiGraph();
			
			// Reads the "kdv_unload.txt" file into the buffer.
			BufferedReader br = new BufferedReader(new FileReader(edgeFile));
	
			br.readLine(); // again discarding column names
			String line = br.readLine();
			
			edges = new ArrayList<Edge>();
			
			while (line != null) {
				String[] lineArray = line.split(",(?! |[a-zA-ZæÆøØåÅ])"); // Regex matches ',' not followed by spaces of letters.
				Node fromNode = nodes.get(Integer.parseInt(lineArray[0]));
				Node toNode = nodes.get(Integer.parseInt(lineArray[1]));
				double length = Double.parseDouble(lineArray[2]);
				int type = Integer.parseInt(lineArray[5]);
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
//					WindowHandler.graph.addEdge(edge);
					graph.addEdge(edge);
				}
				else if (oneway.equals("tf")) {
					Edge edge = new Edge(toNode, fromNode, length, vejnavn, type, v_postnr, h_postnr, driveTime,
							houseNumberFromLeft, houseNumberToLeft, houseNumberFromRight, houseNumberToRight, true);
					if (length > longestRoadsFloor) longestRoads.add(edge);
					edges.add(edge);
					graph.addEdge(edge);
//					multiGraph.addEdge(edge);
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
//					multiGraph.addEdge(fEdge);
//					multiGraph.addEdge(tEdge);
				}
				
				line = br.readLine();
				SplashScreen.use().updateProgress();
			}
			br.close();
			return graph;
		} 
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "The \"edgeFile\" was not foud. \nThe will terminate.", "ERROR", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		return null;
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
	
	public ArrayList<CoastPoint[]> readCoast() {
		ArrayList<CoastPoint[]> area = new ArrayList<>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader("coastLine.txt"));
			String line = reader.readLine().trim();
			ArrayList<CoastPoint> current = null;
			while(!(line == null)) {
				if(line.contains(">")) {
					line = reader.readLine().trim();
					if(current == null) {
						current = new ArrayList<CoastPoint>();
					} else {
						area.add(current.toArray(new CoastPoint[1]));
						current = new ArrayList<CoastPoint>();
					}
					continue;
				}
				double lat = Double.parseDouble(line.substring(0, line.indexOf("\t")));
				double lon = Double.parseDouble(line.substring(line.indexOf("\t")+1));
				current.add(lonLatToUTM.convertToUTM(lat, lon));

				line = reader.readLine();
				}
			area.add(current.toArray(new CoastPoint[1]));
			reader.close();
			System.out.println("Number of polygons: " +area.size());
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "The \"coastline-file\" was not foud. \nThe will terminate.", "ERROR", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		return area;
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
	
	public HashMap<String, HashSet<String>> getRoadToZipMap() {
		return roadToZipMap;
	}

	public List<Edge> getEdges() {
		return edges;
	}
}
