package resize_virker;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import DataReader.DataReader;
import Part1.*;
import QuadTree.QuadTree;

public class WindowHandler {
	static List<Node> nodes;
	static List<Edge> edges;
	static List<Edge> longestRoads;
	static int longestRoadsFloor;
	static QuadTree QT;
	static Graph graph;
	static Window window;
	static double geoWidth = 450403.8604700001;
	static double geoHeight = 352136.5527900001;
			
	// Converts X-pixel to coordinate
	private static double pixelToGeoX(int x) {
		return  (((double)x/ Window.use().getWidth()) * geoWidth);
	}
	
	
	// Converts Y-pixel to coordinate
	private static double pixelToGeoY(int y) {
		return  (((double)y/(Window.use().getHeight()) * geoHeight));
	}
	
	// Takes a search area in pixels, and returns a list of all the edges to be drawn on the map
	public static void search(int x1, int x2, int y1, int y2) {
		
		double geoX1 = pixelToGeoX(x1) - longestRoadsFloor;
		double geoX2 = pixelToGeoX(x2) + longestRoadsFloor;
		double geoY1 = geoHeight - pixelToGeoY(y1) + longestRoadsFloor;
		double geoY2 = geoHeight - pixelToGeoY(y2) - longestRoadsFloor;
		nodes = QT.query(geoX1, geoY1, geoX2, geoY2);
		// checks whether any of the longest roads intersect with the searched area
		List<Edge> searchedEdges = new LinkedList<Edge>();
		for (Edge e : longestRoads) {
			if(lineIntersects(geoX1, geoX2, geoY1, geoY2, e.getFromNode().getXCord(), e.getFromNode().getYCord(),
				e.getToNode().getXCord(), e.getFromNode().getYCord())) {
			searchedEdges.add(e);
			}
		}
		for (Node n: nodes) {
			Iterable<Edge> edgesFromNode = graph.adjOut(n.getKdvID());
			for (Edge e : edgesFromNode) {
				searchedEdges.add(e);
			}
		}
		//geoWidth = pixelToGeoX(x2) - pixelToGeoX(x1);
		//geoHeight = pixelToGeoY(y2) - pixelToGeoY(y1);
		edges = searchedEdges;
		
	}
	
	private static boolean lineIntersects(double boxX1, double boxX2, double boxY1, double boxY2,
									double lineX1, double lineX2, double lineY1, double lineY2) {
		return false;
	}
	
	// Gets all the edges that go out from each Node in the list of nodes
	public static List<Edge> getEdgesFromNodes(List<Node> nodes) {
		List<Edge> edges = new LinkedList<Edge>();
		for (Node n: nodes) {
			Iterable<Edge> edgesFromNode = graph.adjOut(n.getKdvID());
			for (Edge e : edgesFromNode) {
				edges.add(e);
			}
		}
		return edges;
	}
	
	//Adds road segments to arrayList within Map class
	public static void calculatePixels() {
		RoadSegment.setMapSize(DataReader.getMaxX(), DataReader.getMaxY(), DataReader.getMinX(), DataReader.getMinY());
		Map.use().newArrayList();
		Long startTime = System.currentTimeMillis();
		for (Edge e : edges) {
			//if (e.getType() != 5) break;
			double x1 = e.getFromNode().getXCord();
			double y1 = e.getFromNode().getYCord();
			double x2 = e.getToNode().getXCord();
			double y2 = e.getToNode().getYCord();
			Map.use().addRoadSegment(new RoadSegment(x1, y1, x2, y2, e.getType()));
		}
		Long endTime = System.currentTimeMillis();
		Long duration = endTime - startTime;
		System.out.println("Time to calculate pixels: " + (duration/1000.0) + "s");
	}
	

	public static void main(String[] args) throws IOException {
		Window.use();
		//Initializing of data from KrakLoader
		Long startTime = System.currentTimeMillis();
		DataReader krakLoader = DataReader.use("kdv_node_unload.txt",
				"kdv_unload.txt");
		krakLoader.createNodeList(); 				//ArraylList with Nodes
		longestRoadsFloor = 10000;				//All roads with length larger than the longest road floor are added to the longest roads list
		graph = krakLoader.createGraphAndLongestRoadsList(longestRoadsFloor);			//Makes graph object and list of roads longer than the longest roads floor
		QT = krakLoader.createQuadTree();			//Makes and returns a quadTree
		longestRoads = krakLoader.getLongestRoads();
		krakLoader = null;							//Avoid loithering
		Long endTime = System.currentTimeMillis();
		Long duration = endTime - startTime;
		System.out.println("Time to create Nodelist, Graph and QuadTree: " + duration/1000.0 + " s");
		startTime = System.currentTimeMillis();
		nodes = QT.query(0, 0, DataReader.getMaxX()-DataReader.getMinX(), 
							DataReader.getMaxY()-DataReader.getMinY());
		RoadSegment.setMapSize(DataReader.getMaxX(), DataReader.getMinX(), 
							DataReader.getMaxY(), DataReader.getMinY());
		endTime = System.currentTimeMillis();
		duration = endTime - startTime;
		System.out.println("Time to query all nodes and find their neighbours: " 
														+ duration/1000.0 + " s");
		
		edges = getEdgesFromNodes(nodes);
		
		System.out.println("Length of the result from full query: " + nodes.size());

		calculatePixels();
		Window.use().updateMap();
		
		System.out.printf("Graph has %d edges%n", graph.getE());
		//MemoryMXBean mxbean = ManagementFactory.getMemoryMXBean();
		//System.out.printf("Heap memory usage: %d MB%n", mxbean
		//		.getHeapMemoryUsage().getUsed() / (1000000));
	}
	
}
