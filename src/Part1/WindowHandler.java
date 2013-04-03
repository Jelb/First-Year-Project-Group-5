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
	static double offsetX;
	static double offsetY;
	static double ratio;
			
	// Converts X-pixel to coordinate
	private static double pixelToGeoX(int x) {
		return  (((double)x/ Window.use().getMapWidth()) * geoWidth);
	}
	
	
	// Converts Y-pixel to coordinate
	private static double pixelToGeoY(int y) {
		return  (((double)y/(Window.use().getMapHeight()) * geoHeight));
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
		// recalculate the search area to fit the aspect ratio
		if (geoWidth > geoHeight * ratio) {
			double newGeoHeight = geoWidth / ratio;
			double diff = (newGeoHeight - geoHeight) / 2;
			geoYMax += diff;
			geoYMin -= diff;
			geoHeight = newGeoHeight;
		}
		else {
			double newGeoWidth = geoHeight * ratio;
			double diff = (newGeoWidth - geoWidth) / 2;
			geoXMax += diff;
			geoXMin -= diff;
			geoWidth = newGeoWidth;
		}
		
		nodes = QT.query(geoXMin+offsetX, geoYMin+offsetY, geoXMax+offsetX, geoYMax+offsetY);
		getEdgesFromNodes();
		RoadSegment.setMapSize(geoXMax+offsetX, geoYMax+offsetY, geoXMin+offsetX, geoYMin+offsetY);
		offsetX += geoXMin;
		offsetY += geoYMin;

		
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
		// For all the edges currently in the field of view,
		// create a roadSegment and add it to the list
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
	
	public static void resetMap() {
		setGeoHeight(DataReader.getMaxY()-DataReader.getMinY());
		setGeoWidth(DataReader.getMaxX()-DataReader.getMinX());
		offsetX = 0;
		offsetY = 0;
		nodes = QT.query(0, 0, geoWidth, geoHeight);
		RoadSegment.setMapSize(geoWidth, geoHeight, 0.0, 0.0);
		getEdgesFromNodes();
		calculatePixels();
		Window.use().updateMap();
	}

	
	public static void setGeoWidth(double geoWidth) {
		WindowHandler.geoWidth = geoWidth;
	}


	public static void setGeoHeight(double geoHeight) {
		WindowHandler.geoHeight = geoHeight;
	}
	
	public static double getRatio() {
		return ratio;
	}


	public static void main(String[] args) throws IOException {
		Window.use();
		
		// Timer for testing purposes
		Long startTime = System.currentTimeMillis();
		
		//Initializing of data from KrakLoader
		DataReader krakLoader = DataReader.use("kdv_node_unload.txt","kdv_unload.txt");
		
		//ArraylList with Nodes
		krakLoader.createNodeList();
		
		longestRoadsFloor = 10000;
		
		//All roads with length larger than the longest road floor are added to the longest roads list
		//Makes graph object and list of roads longer than the longest roads floor
		graph = krakLoader.createGraphAndLongestRoadsList(longestRoadsFloor);
		
		//Makes and returns a quadTree
		QT = krakLoader.createQuadTree();
		longestRoads = krakLoader.getLongestRoads();
		
		//Avoid loitering
		krakLoader = null;
		
		// Counts and prints the time spent initializing
		Long endTime = System.currentTimeMillis();
		Long duration = endTime - startTime;
		System.out.println("Time to create Nodelist, Graph and QuadTree: " + duration/1000.0 + " s");
		
		// Sets the delta width and height of the entire map
		setGeoHeight(DataReader.getMaxY()-DataReader.getMinY());
		setGeoWidth(DataReader.getMaxX()-DataReader.getMinX());
		
		ratio = geoWidth/geoHeight;
		
		// Starts a new test timer
		startTime = System.currentTimeMillis();
		
		// Finds all the nodes in the view area
		nodes = QT.query(0, 0, geoWidth, geoHeight);
		RoadSegment.setMapSize(geoWidth, geoHeight, 0.0, 0.0);
		
		// Finds all the edges for these nodes
		getEdgesFromNodes();
		
		// Ends timer, prints result
		endTime = System.currentTimeMillis();
		duration = endTime - startTime;
		System.out.println("Time to query all nodes and find their neighbours: " 
														+ duration/1000.0 + " s");
		
		System.out.println("Length of the result from full query: " + nodes.size());

		// Creates and adds roadSegments to an the arraylist 'edges'
		calculatePixels();
		
		// Throws out the old contentPane, then adds a new and calls repaint/validate,
		// thus calling the internal method paintComponents found in the roadSegments objects
		Window.use().updateMap();
		Window.use().repaint();
		System.out.printf("Graph has %d edges%n", graph.getE());
		//MemoryMXBean mxbean = ManagementFactory.getMemoryMXBean();
		//System.out.printf("Heap memory usage: %d MB%n", mxbean
		//		.getHeapMemoryUsage().getUsed() / (1000000));
	}
	
}
