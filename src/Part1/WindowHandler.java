package Part1;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import QuadTree.QuadTree;

public class WindowHandler {
	static List<Node> nodes;
	static List<Edge> edges;
	static List<Edge> longestRoads;
	static int longestRoadsFloor;
	static QuadTree QT;
	static Graph graph;
	static Window window;
	static double geoWidth;
	static double geoHeight;
			
	// Converts X-pixel to coordinate
	private static double pixelToGeoX(int x) {
		return  (((double)x/ Window.use().getWidth()) * geoWidth);
	}
	
	
	// Converts Y-pixel to coordinate
	private static double pixelToGeoY(int y) {
		return  (((double)y/(Window.use().getHeight()) * geoHeight));
	}
	
	// Takes a search area in pixels, and saves a list of all the edges to be drawn on the map
	public static void search(int x1, int x2, int y1, int y2) {
		//TODO: Add the longestRoadsFloor to each border of the search area and use this to optimize panning.
		double geoXMax;
		double geoXMin;
		double geoYMax;
		double geoYMin;
		if (x1 > x2) {
			geoXMax = pixelToGeoX(x1);
			geoXMin = pixelToGeoX(x2);
		}
		else {
			geoXMax = pixelToGeoX(x2);
			geoXMin = pixelToGeoX(x1);
		}
		if (y1 > y2) {
			geoYMax = geoHeight - pixelToGeoY(y2);
			geoYMin = geoHeight - pixelToGeoY(y1);
		}
		else {
			geoYMax = geoHeight - pixelToGeoY(y1);
			geoYMin = geoHeight - pixelToGeoY(y2);
		}
		System.out.println("Start point: (" + geoXMin + ", " + geoYMin + ")");
		System.out.println("End point: (" + geoXMax + ", " + geoYMax + ")");
		geoWidth = geoXMax - geoXMin;
		geoHeight = geoYMax - geoYMin;
		nodes = QT.query(geoXMin, geoYMin, geoXMax, geoYMax);
		getEdgesFromNodes();
		RoadSegment.setMapSize(geoXMax, geoYMax, geoXMin, geoYMin);

		
		// TODO:check whether any of the longest roads intersect with the searched area
//		List<Edge> searchedEdges = new LinkedList<Edge>();
//		for (Edge e : longestRoads) {
//			if(lineIntersects(geoX1, geoX2, geoY1, geoY2, e.getFromNode().getXCord(), e.getFromNode().getYCord(),
//				e.getToNode().getXCord(), e.getFromNode().getYCord())) {
//			searchedEdges.add(e);
//			}
//		}
//		for (Node n: nodes) {
//			Iterable<Edge> edgesFromNode = graph.adjOut(n.getKdvID());
//			for (Edge e : edgesFromNode) {
//				searchedEdges.add(e);
//			}
//		}
//		edges = searchedEdges;
		
	}
	
	//TODO: Write code to detect intersection between area of interest and road.
	@SuppressWarnings("unused")
	private static boolean lineIntersects(double boxX1, double boxX2, double boxY1, double boxY2,
									double lineX1, double lineX2, double lineY1, double lineY2) {
		return false;
	}
	
	// Gets all the edges that go out from each Node in the list of nodes
	public static void getEdgesFromNodes() {
		edges = new LinkedList<Edge>();
		for (Node n: nodes) {
			Iterable<Edge> edgesFromNode = graph.adjOut(n.getKdvID());
			for (Edge e : edgesFromNode) {
				//if (e.getType() != 5) continue;
				edges.add(e);
			}
		}
	}
	
	//Adds road segments to arrayList within Map class
	public static void calculatePixels() {
		Map.use().newArrayList();
		Long startTime = System.currentTimeMillis();
		for (Edge e : edges) {
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

	
	public static void setGeoWidth(double geoWidth) {
		WindowHandler.geoWidth = geoWidth;
	}


	public static void setGeoHeight(double geoHeight) {
		WindowHandler.geoHeight = geoHeight;
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
		krakLoader = null;		//Avoid loitering
		
		Long endTime = System.currentTimeMillis();
		Long duration = endTime - startTime;
		System.out.println("Time to create Nodelist, Graph and QuadTree: " + duration/1000.0 + " s");
		
		setGeoHeight(DataReader.getMaxY()-DataReader.getMinY());
		setGeoWidth(DataReader.getMaxX()-DataReader.getMinX());
		
		startTime = System.currentTimeMillis();
		
		nodes = QT.query(0, 0, geoWidth, geoHeight);
		RoadSegment.setMapSize(geoWidth, geoHeight, 0.0, 0.0);
		getEdgesFromNodes();
		
		endTime = System.currentTimeMillis();
		duration = endTime - startTime;
		System.out.println("Time to query all nodes and find their neighbours: " 
														+ duration/1000.0 + " s");
		
		System.out.println("Length of the result from full query: " + nodes.size());

		calculatePixels();
		Window.use().updateMap();
		
		System.out.printf("Graph has %d edges%n", graph.getE());
		//MemoryMXBean mxbean = ManagementFactory.getMemoryMXBean();
		//System.out.printf("Heap memory usage: %d MB%n", mxbean
		//		.getHeapMemoryUsage().getUsed() / (1000000));
	}
	
}
