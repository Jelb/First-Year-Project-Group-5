package krakLoader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import Part1.Edge;
import Part1.Node;
import QuadTree.Point;
import QuadTree.QuadTree;

import Part1.Graph;

/**
 * This class can load the data files from krak into a graph representation
 * @author Peter Tiedemann petert@itu.dk
 */
public class KrakLoader {
	ArrayList<Node> nodes;
	String nodeFile;
	String edgeFile;
	double maxX;
	double maxY;
	double minX;
	double minY;

	public KrakLoader(String nodeFile, String edgeFile)
	{ 
		this.nodeFile = nodeFile;
		this.edgeFile = edgeFile;
		try {
			createNodeList();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createNodeList() throws IOException {
		// open the file containing the list of nodes
		BufferedReader br = new BufferedReader(new FileReader(nodeFile));

		br.readLine(); // discard names of columns which is the first line

		String line = br.readLine();

		// An array list containing the nodes we find in the file
		nodes = new ArrayList<Node>();

		maxX = 0;
		maxY = 0;
		minX = 10000000;
		minY = 10000000;

		while(line != null){
			// Splits "line" by ',' and parses the id, x and y values to KrakNode
			String[] lineArray = line.split(",");
			double x = Double.parseDouble(lineArray[3]);
			if (maxX < x) maxX = x;
			double y = Double.parseDouble(lineArray[4]);
			if (maxY < y) maxY = y;
			int id = Integer.parseInt(lineArray[1]);
			if (minX > x) minX = x;
			if (minY > y) minY = y;

			nodes.add(new Node(x, y, id)); 
			line = br.readLine();
		}
		br.close();
	}

	public Graph createGraph() throws IOException {


		System.out.println("Adding " + nodes.size() + " nodes to graph");

		// Create a graph on the nodes
		Graph graph = new Graph(nodes.size()+1);

		//Open the file containing the edge entries
		BufferedReader br = new BufferedReader(new FileReader(edgeFile));

		br.readLine(); // again discarding column names
		String line = br.readLine();

		while(line != null){
			String[] lineArray = line.split(",(?! |[a-zA-ZæÆøØåÅ])");
			Node fromNode = nodes.get(Integer.parseInt(lineArray[0])-1);
			Node toNode = nodes.get(Integer.parseInt(lineArray[1])-1);
			double length = Double.parseDouble(lineArray[2]);
			int type = Integer.parseInt(lineArray[5]);
			Edge edge = new Edge(fromNode, toNode, length, type); // create edge
			graph.addEdge(edge); // add edge to graph
			line = br.readLine();
		}
		br.close();

		return graph;
	}

	public QuadTree createQuadTree() {
		// Create QuadTree
		QuadTree QT = new QuadTree(3, maxX-minX, maxY-minY);
		for (Node node : nodes) {
			QT.insert(node.getXCord()-minX, node.getYCord()-minY, node.getKdvID());
		}

		return QT;
	}

	public static void main(String[] args) throws IOException{
		KrakLoader krakLoader = new KrakLoader("kdv_node_unload.txt", "kdv_unload.txt");
		Graph graph = krakLoader.createGraph();
		QuadTree QT = krakLoader.createQuadTree();
		List<Point> list = QT.query(0, 0, 100000, 100000);
		for (Point p : list) System.out.println(p.getID());
		System.out.printf("Graph has %d edges%n", graph.getE());
		MemoryMXBean mxbean = ManagementFactory.getMemoryMXBean();
		System.out.printf("Heap memory usage: %d MB%n", mxbean.getHeapMemoryUsage().getUsed()/(1000000));
	}
}
