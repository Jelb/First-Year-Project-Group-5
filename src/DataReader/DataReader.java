package DataReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

import Part1.Edge;
import Part1.Node;
import QuadTree.QuadTree;

import Part1.Graph;

/**
 * The DataReader is used to extract information about <br>Nodes</br> and <br>Edges</br> 
 * contained within two <i>.txt-files</i>. 
 * Furthermore the DataReader class are capable of creating graphs based on the information
 * extracted form the files. 
 * After extraction of the information from both files the DataReader instance contains an <i>
 * ArrayList</i> containing all nodes and another one containing all edges.
 * 
 * The data-files used to store the information about nodes and edges has to be formated as
 * it is the case within the data-files handed out by <br>www.Krak.dk</br>.
 * 
 * The <br>DataReader</br> class implements the singleton design pattern. 
 * 
 * @author Jonas (JELB@ITU.DK)
 * @author Mads (MENJ@ITU.DK)
 *
 */
public class DataReader {
	private static DataReader instance;
	private ArrayList<Node> nodes;
	private ArrayList<Edge> longestRoads;
	private final String nodeFile, edgeFile;
	private static double maxX = 0, maxY = 0, minX = -1, minY = -1, maxLength = 0;

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
	
	/**
	 * This method makes this the <br>DataReader</br> class to a singleton. 
	 * The method checks if an instance of the class already has been created
	 * and returns the current if so. Create and returns a new instance of the
	 * <br>DataReader</br> if none exists. 
	 * 
	 * @param nodeFile The path for where the file containing information about the nodes is stored.
	 * @param edgeFile The path for where the file containing information about the edges is stored.
	 * @return Returns the instance of <br>DataReader</br> which currently are in use. 
	 */
	public static DataReader use(String nodeFile, String edgeFile) {
		if(instance == null) 
			instance = new DataReader(nodeFile, edgeFile);
		return instance
				;
	}

	/**
	 * The method extracts information about the nodes from the <i>nodeFile</i>
	 * and creates <br>Node</br> objects corresponding to the extracted information.
	 * <p>
	 * This class will <br>terminate</br> the program if the file path is invalid or the
	 * file is unreadable. 
	 */
	public void createNodeList(){
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
			if (minX > x || minX < 0) // Is allowed because all our coordinates are in quadrant meaning all coordinates are positive.
				minX = x;
			if (minY > y || minY < 0) // Is allowed because all our coordinates are in quadrant meaning all coordinates are positive.
				minY = y;

			nodes.add(new Node(x, y, id));
			line = br.readLine();
		}
		
		// The coordinates of every node is corrected for the offset
		Node.setXOffset(minX);
		Node.setYOffset(minY);
		System.out.println("Width of map: " + (maxX-minX));
		System.out.println("Height of map: " + (maxY-minY));
		br.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "The \"nodeFile\" was not foud. \nThe will terminate.", "ERROR", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

	/**
	 * The method creates a <br>Graph</br> based on the information within the "<i>edgeFile</i>.
	 * Before storing the information in the graph the method constructs <br>Edge</br> object based on the information.
	 * These objects are finally stored in the graph object.
	 * <p>
	 * This class will <br>terminate</br> the program if the file path is invalid or the
	 * file is unreadable.  
	 * 
	 * @param longestRoadsFloor An int describing the floor value of the elements stored in the ArrayList <br>longestRoads</br>. 
	 * @return Returns a <br>Graph</br> object based on the content <i>edgeFile</i>.
	 */
	public Graph createGraphAndLongestRoadsList(int longestRoadsFloor) {

		System.out.println("Adding " + (nodes.size()-1) + " nodes to graph");
		longestRoads = new ArrayList<Edge>();
		try {
		// Create a graph on the nodes
		Graph graph = new Graph(nodes.size());

		// Reads the "kdv_unload.txt" file into the buffer.
		BufferedReader br = new BufferedReader(new FileReader(edgeFile));

		br.readLine(); // again discarding column names
		String line = br.readLine();
		
		while (line != null) {
			String[] lineArray = line.split(",(?! |[a-zA-ZæÆøØåÅ])"); // Regex matches ',' not followed by spaces of letters.
			Node fromNode = nodes.get(Integer.parseInt(lineArray[0]));
			Node toNode = nodes.get(Integer.parseInt(lineArray[1]));
			double length = Double.parseDouble(lineArray[2]);
			int type = Integer.parseInt(lineArray[5]);
			Edge edge = new Edge(fromNode, toNode, length, type); // Creates an edge.
			if (length > longestRoadsFloor) longestRoads.add(edge);
			graph.addEdge(edge); // Adds the newly created edge object to the graph.
			line = br.readLine();
		}
		br.close();
		System.out.println("Max length: " + maxLength);
		return graph;
		} 
		catch (IOException e) {
			//
			JOptionPane.showMessageDialog(null, "The \"edgeFile\" was not foud. \nThe will terminate.", "ERROR", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		return null;
	}

	/**
	 * The method creates an instance of the <br>QuadTree</br> class and inserts all the existing nodes within it.
	 * 
	 * @return Returns a <br>QuadTree</br> object containing all nodes created in the <i>createNodeList()</i> method.
	 */
	public QuadTree createQuadTree() {
		if(nodes == null) createNodeList();
		// Create QuadTree
		QuadTree QT = new QuadTree(3, maxX - minX, maxY - minY);
		for (int i = 1; i < nodes.size(); i++) { //For loop start at index 1 because index 0 is null.
			QT.insert(nodes.get(i));
		}

		return QT;
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
}