package Part1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import Part1.DijkstraSP.CompareType;
import Part1.DijkstraSP.TransportWay;
import Part1.SplashScreen.Task;
import QuadTree.QuadTree;

public class WindowHandler {
	static List<Node> nodes;
	static List<Edge> edges;
	static List<Edge> longestRoads;
	static int startNode;
	static int endNode;
	static HashMap<String, HashSet<String>> roadToZipMap;
	static int longestRoadsFloor;
	static QuadTree QT;
	static Graph graph;
//	static MultiGraph multiGraph;
	static Window window;
	static AddressParser ap;
	static double geoWidth;		// The  width of the view area in meters
	static double geoHeight;	// The height of the view area in meters
	static double offsetX;		// Offset of the current view area relative to the 'outer' map constraints
	static double offsetY;		// Offset of the current view area relative to the 'outer' map constraints
	static double ratio;
	static double minLength;
	static double maxMapHeight;	// = DataReader.getMaxY()-DataReader.getMinY();
	static double maxMapWidth;	// = DataReader.getMaxX()-DataReader.getMinX();
	private static HashMap<String, String> zipToCityMap;
	private static ArrayList<CoastPoint[]> coast; 

	/**
	 * Calculates the absolute geo X coordinate of a given pixel value X.
	 * @param x		The positive pixel value X
	 * @return		The UTM geo coordinate X
	 */
	public static double pixelToAbsoluteGeoX(int x) {
		return Node.getAbsoluteXoffset() + pixelToGeoX(x) + offsetX;
	}
	
	/**
	 * Calculates the absolute geo Y coordinate.
	 * of a given pixel value Y.
	 * @param y		The positive pixel value Y
	 * @return		The UTM geo coordinate Y
	 */
	public static double pixelToAbsoluteGeoY(int y) {
		return Node.getAbsoluteYoffset() + (geoHeight - pixelToGeoY(y)) + offsetY;
	}
			
	/**
	 * Calculates the window-relative geo X coordinate.
	 * The method returns the geo X coordinate measured from the current windows origo.
	 * @param x		The positive pixel value X
	 * @return		Meters from the left side of the window to the pixel
	 */
	public static double pixelToGeoX(int x) {
		return  (((double)x/ Window.use().getMapWidth()) * geoWidth);
	}
	
	
	/**
	 * Calculates the window-relative geo Y coordinate.
	 * The method returns the geo Y coordinate measured from the current windows origo.
	 * @param y		The positive pixel value Y
	 * @return		Meters down from the top of the window to the pixel
	 */
	public static double pixelToGeoY(int y) {
		return  (((double)y/(Window.use().getMapHeight()) * geoHeight));
	}
	
	/**
	 * Given a pixel coordinate, return the nearest visible edge.
	 * @param x		X value of pixel coordinate
	 * @param y		Y value of pixel coordinate
	 * @return		The edge nearest the coordinate
	 */
	public static void closestEdge(int pixelX, int pixelY) {
		double x = pixelToAbsoluteGeoX(pixelX);
		double y = pixelToAbsoluteGeoY(pixelY);

		Map.use().newArrayList();
		Edge closestEdge = null;
		double shortestDist = Double.MAX_VALUE;

		for (Node n : nodes) {
//			Iterable<Edge> edgesFromNode = multiGraph.adj(n.getKdvID(),1);
			Iterable<Edge> edgesFromNode = graph.adj(n.getKdvID());
			for (Edge e : edgesFromNode) {
				if (e.isDrawable() && includeEdge(e)) {
					double x1 = e.getFromNode().getAbsoluteXCoordinate();
					double y1 = e.getFromNode().getAbsoluteYCoordinate();
					double x2 = e.getToNode().getAbsoluteXCoordinate();
					double y2 = e.getToNode().getAbsoluteYCoordinate();
					
					double tempDist = distanceBetweenPoints(x1, y1, x, y);
					if(tempDist < shortestDist) {
						shortestDist = tempDist;
						closestEdge = e;
						tempDist = distanceBetweenPoints(x2, y2, x, y);
						if(tempDist < shortestDist) {
							shortestDist = tempDist;
							closestEdge = e;
						}
					}
				}
			}
		}
		testDrawClosestEdge(closestEdge);
		System.out.print("Closest edge: ");
		if(closestEdge.getVEJNAVN().length() > 0)
			System.out.println(closestEdge.getVEJNAVN());
		else {
//			String streetname;
//			streetname = closestEdge.lookForStreetname(closestEdge);
//			System.out.println(streetname);
			System.out.println("No name found");
		}
	}
	
	/**
	 * Creates a 2D vector based on a set of nodes.
	 * @param fnode		The 'from' node
	 * @param tnode		The 'to' node
	 * @return			The vector between the two
	 */
	public static double[] nodeToVector(Node fnode, Node tnode) {
		double[] vector = new double[2];
		vector[0] = tnode.getAbsoluteXCoordinate() - fnode.getAbsoluteXCoordinate();
		vector[1] = tnode.getAbsoluteYCoordinate() - fnode.getAbsoluteYCoordinate();
		return vector;
	}
	
	/**
	 * Calculates the distance between two nodes.
	 * @param fnode		The 'from' node
	 * @param tnode		The 'to' node
	 * @return			The distance between the two
	 */
	public static double distanceBetweenNodes(Node fnode, Node tnode) {
		return vectorLength(nodeToVector(fnode, tnode));
	}
	
	/**
	 * Calculates the distance between two points.
	 * @param x1		X coord, first point
	 * @param y1		Y coord, first point
	 * @param x2		X coord, second point
	 * @param y2		Y coord, second point
	 * @return			The distance between the two points
	 */
	public static double distanceBetweenPoints(double x1, double y1, double x2, double y2) {
		return vectorLength(pointsToVector(x1, y1, x2, y2));
	}
	
	/**
	 * Calculates the length of a 2D vector.
	 * @param vector	A double array of 2 values, describing the vector
	 * @return			The length of the vector
	 */
	public static double vectorLength(double[] vector) {
		return Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2));
	}
	
	/** 
	 * Calculates the scalar product of the 2D vectors.
	 * @param vectorA	A double array of 2 values, describing the first vector
	 * @param vectorB	A double array of 2 values, describing the second vector
	 * @return			The scalar product
	 */
	public static double scalarProduct(double[] vectorA, double[] vectorB) {
		return (vectorA[0] * vectorB[0]) + (vectorA[1] * vectorB[1]);
	}
	
	/**
	 * Calculates the cosine value of the angle between 2 vectors.
	 * @param vectorA	A double array of 2 values, describing the first vector
	 * @param vectorB	A double array of 2 values, describing the second vector
	 * @return			The cosine angle
	 */
	public static double cosVectorAngle(double[] vectorA, double[] vectorB) {
		return (scalarProduct(vectorA, vectorB) / (vectorLength(vectorA) * vectorLength(vectorB)));
	}
	
	/**
	 * Creates a 2D vector from two points
	 * @param x1		X coord, first point
	 * @param y1		Y coord, first point
	 * @param x2		X coord, second point
	 * @param y2		Y coord, second point
	 * @return			The resulting vector between the two
	 */
	public static double[] pointsToVector(double x1, double y1, double x2, double y2) {
		double[] vector = new double[2];
		vector[0] = x2 - x1;
		vector[1] = y2 - y1;
		return vector;
	}
	
	public static void randomSPtest() {
		Random rnd = new Random();
//		startNode = rnd.nextInt(multiGraph.getV(1)-1)+1;
//		endNode = rnd.nextInt(multiGraph.getV(1)-1)+1;
		startNode = rnd.nextInt(graph.getV()-1)+1;
		endNode = rnd.nextInt(graph.getV()-1)+1;
		//pathFindingTest();
	}
	
	public static void setNode(int node, boolean from){
		if(from){
			WindowHandler.startNode = node;
		}
		else{
			WindowHandler.endNode = node;
		}
			
	}
	
	/**
	 * Picks to nodes at random and calculates the shortest path between them.
	 * For testing purposes only.
	 */
	public static void pathFindingTest(TransportWay transport, CompareType compareType) {
		System.out.println("Start node: " + startNode);
		
		System.out.println("Creating SP object... ");
		DijkstraSP dsp = new DijkstraSP(graph, startNode, transport, compareType);	// use random node at our start node for shortest path calculation
		
		Stack<Edge> route = new Stack<Edge>();					// clears any previous route
		
		System.out.println("Start node: " + startNode);
		System.out.println("  End node: " + endNode);
		
		route = (Stack<Edge>) dsp.pathTo(endNode);	// find route from start to destination node
		
		addRouteToMap(route);									// adding the route to the Map()
		Window.use().updateMap();
	}
	
	/**
	 * Test method, draws the closest edge as a shortest path.
	 * @param edge	The 'closest' edge
	 */
	public static void testDrawClosestEdge(Edge edge) {
		ArrayList<DrawableItem> path = new ArrayList<DrawableItem>();
		double x1 = edge.getFromNode().getXCord();
		double y1 = edge.getFromNode().getYCord();
		double x2 = edge.getToNode().getXCord();
		double y2 = edge.getToNode().getYCord();
		boolean border = false;											// for now, no borders will be drawn
		path.add(new RoadSegment(x1, y1, x2, y2, 4242, border));
		Map.use().setPath(path);
	}
	
	/**
	 * Adds the shortest path (static field 'route') to the roadSegments on the map.
	 */
	public static void addRouteToMap(Stack<Edge> route) {
		ArrayList<DrawableItem> path = new ArrayList<DrawableItem>();
		while(!route.empty()) {
			Edge edge = route.pop();
			double x1 = edge.getFromNode().getXCord();
			double y1 = edge.getFromNode().getYCord();
			double x2 = edge.getToNode().getXCord();
			double y2 = edge.getToNode().getYCord();
			boolean border = false;											// for now, no borders will be drawn
			path.add(new RoadSegment(x1, y1, x2, y2, 4242, border));
		}
		Map.use().setPath(path);
	}
	
	public static void zoomOut() {
		double minX = geoWidth*0.1, maxX = geoWidth*1.1, minY = geoHeight*0.1, maxY = geoHeight*1.1;
		if((maxMapHeight - (offsetY + geoHeight)) < geoHeight*0.1) {
			maxY = maxMapHeight - (offsetY + geoHeight) + geoHeight;
		}
		if(offsetY < geoHeight*0.1) {
			minY = offsetY;
		}
		if(offsetX < geoWidth*0.1) {
			minX = offsetX;
		}
		if((maxMapWidth - (offsetX + geoWidth)) < geoWidth*0.1) {
			maxX = maxMapWidth - (offsetX + geoWidth) + geoWidth;
		}
		search(-minX, maxX, -minY, maxY);
		Window.use().updateMap();
		System.out.println("geoWidth = " + geoWidth);
	}
	
	public static void zoomIn() {
		search(geoWidth*0.1, geoWidth*0.9, geoHeight*0.1, geoHeight*0.9);
		Window.use().updateMap();
		System.out.println("geoWidth = " + geoWidth);
	}
	
	// Searches an area using pixel-values
	public static void pixelSearch(int x1, int x2, int y1, int y2) {
		double geoXMax;
		double geoXMin;
		double geoYMax;
		double geoYMin;
		
		// If not ∆x or ∆y is above 10 the method is stopped.
		if (!(Math.abs(x1-x2) > 10 || Math.abs(y1-y2) > 10)) return;
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
		search(geoXMin, geoXMax, geoYMin, geoYMax);
	}
	
	// Takes a search area in UTM-coordinates, and saves a list of all the edges to be drawn on the map
	public static void search(double geoXMin, double geoXMax, double geoYMin, double geoYMax) {
		
		//ensures that the search area is wider than 200m
		if (geoXMax - geoXMin < 500) {
			double centerX = (geoXMax - geoXMin)/2 + geoXMin;
			geoXMin = centerX - 250;
			geoXMax = centerX + 250;
			double centerY = (geoYMax - geoYMin)/2 + geoYMin;
			geoYMin = centerY - 250/ratio;
			geoYMax = centerY + 250/ratio;
		}
		//System.out.println("Start point: (" + geoXMin + ", " + geoYMin + ")");
		//System.out.println("End point: (" + geoXMax + ", " + geoYMax + ")");
		
		
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
			System.out.println("Gammel width: " + geoWidth);
			System.out.println("Ny width:" + newGeoWidth);
			geoWidth = newGeoWidth;
		}
		
		
		long startTime = System.currentTimeMillis();
		nodes = QT.query(geoXMin+offsetX-longestRoadsFloor, geoYMin+offsetY-longestRoadsFloor,
				geoXMax+offsetX+longestRoadsFloor, geoYMax+offsetY+longestRoadsFloor);
		System.out.println("Time for query in quadtree: " + (System.currentTimeMillis()-startTime)/1000.0);
		DrawableItem.setMapSize(geoXMax+offsetX, geoYMax+offsetY, geoXMin+offsetX, geoYMin+offsetY);
		startTime = System.currentTimeMillis();
		getEdgesFromNodes();
		System.out.println("Time to create list of road segments: " + (System.currentTimeMillis()-startTime)/1000.0);
		offsetX += geoXMin;
		offsetY += geoYMin;

		
		// check whether any of the longest roads intersect with the searched area
		for (Edge e : longestRoads) {
			double x1 = e.getFromNode().getXCord();
			double y1 = e.getFromNode().getYCord();
			double x2 = e.getToNode().getXCord();
			double y2 = e.getToNode().getYCord();
			boolean border = false;											// for now, no borders will be drawn
			if(lineIntersects(geoXMin, geoXMax, geoYMin, geoYMax, x1, x2,
				y1, y2)) Map.use().addRoadSegment(new RoadSegment(x1, y1, x2, y2, e.getType(),border));
		}
		
	}
	
	//TODO: Write code to detect intersection between area of interest and road.
	private static boolean lineIntersects(double boxX1, double boxX2, double boxY1, double boxY2,
									double lineX1, double lineX2, double lineY1, double lineY2) {
//		if (lineX1 > boxX2 && lineX2 > boxX2) return false;
//		else if (lineX1 < boxX1 && lineX2 < boxX1) return false;
//		else if (lineY1 > boxY2 && lineY2 > boxY2) return false;
//		else if (lineY1 < boxY1 && lineY1 < boxY1) return false;
//		else return true;
		return true;
	}
	
	
	// returns true if the given edge will be shown on the map with the current zoom level
	private static boolean includeEdge(Edge e) {
		int t = e.getType();
		//if (t == 1 || t == 2 || t == 3 || t == 4 || t == 80) return true;
		if (t == 1 || t == 2 || t == 3 || t == 80) return true;
		else if (geoWidth < 250000 && (t == 4)) return true;
		else if (geoWidth < 60000 && (t == 5)) return true;
		//else if (geoWidth < 100000 && (t == 5)) return true;
		else if (geoWidth < 13000) return true;
		else return false;
	}
	
	// Gets all the edges that go out from each Node in the list of nodes
	public static void getEdgesFromNodes() {
		Map.use().newArrayList();
		for (Node n : nodes) {
			Iterable<Edge> edgesFromNode = graph.adj(n.getKdvID());
//			for (int type = 1; type < 4; type++) {
//			Iterable<Edge> edgesFromNode = multiGraph.adj(n.getKdvID(), type);
			for (Edge e : edgesFromNode) {
				if (e.isDrawable() && includeEdge(e)) {
					double x1 = e.getFromNode().getXCord();
					double y1 = e.getFromNode().getYCord();
					double x2 = e.getToNode().getXCord();
					double y2 = e.getToNode().getYCord();
					boolean border = false;											// for now, no borders will be drawn
					Map.use().addRoadSegment(
							new RoadSegment(x1, y1, x2, y2, e.getType(),border));
				}
			}
//			}
		}
	}
	
	public static void resetMap() {
		search(-offsetX, maxMapWidth-offsetX, -offsetY, maxMapHeight-offsetY);
		Window.use().updateMap();
	}

	
	public static void setGeoWidth(double geoWidth) {
		WindowHandler.geoWidth = geoWidth;
	}


	public static void setGeoHeight(double geoHeight) {
		WindowHandler.geoHeight = geoHeight;
	}
	
	public static double getGeoWidth() {
		return geoWidth;
	}


	public static double getGeoHeight() {
		return geoHeight;
	}


	public static double getOffsetX() {
		return offsetX;
	}


	public static double getOffsetY() {
		return offsetY;
	}


	public static double getMaxMapHeight() {
		return maxMapHeight;
	}


	public static double getMaxMapWidth() {
		return maxMapWidth;
	}


	public static double getRatio() {
		return ratio;
	}
	
	public static HashMap<String, HashSet<String>> getRoadToZipMap() {
		return roadToZipMap;
	}
	
	public static List<Edge> getEdges(){
		return edges;
	}


	public static void main(String[] args) throws IOException {
		String nodeFile = "kdv_node_unload.txt";
		String edgeFile = "kdv_unload.txt";
		SplashScreen.initialize(nodeFile, edgeFile);
		SplashScreen.use();
		
		// Timer for testing purposes
		Long startTime = System.currentTimeMillis();
		
		//Initializing of data from KrakLoader
		DataReader dataReader = DataReader.use("kdv_node_unload.txt","kdv_unload.txt");
//		DataReader dataReader = DataReader.use("testNodes.txt","testEdges.txt");
		
		//ArraylList with Nodes
		dataReader.createNodeList();
		coast = dataReader.readCoast();
		
		longestRoadsFloor = 10000;
		
		//All roads with length larger than the longest road floor are added to the longest roads list
		//Makes graph object and list of roads longer than the longest roads floor
		graph = dataReader.createGraphAndLongestRoadsList(longestRoadsFloor);
//		multiGraph = dataReader.createGraphAndLongestRoadsList(longestRoadsFloor);
		
		//Makes and returns a quadTree
		QT = dataReader.createQuadTree();
		longestRoads = dataReader.getLongestRoads();
		
		roadToZipMap = dataReader.getRoadToZipMap();
		
		// Create zip to city map
		StreetNameReader snr = new StreetNameReader();
		zipToCityMap = snr.zipToCityMap();
		snr = null;
		
		//set arraylist of all egdes
		edges = dataReader.getEdges();
		
		//Avoid loitering
		dataReader = null;
		
		// Counts and prints the time spent initializing
		Long endTime = System.currentTimeMillis();
		Long duration = endTime - startTime;
		System.out.println("Time to create Nodelist, Graph and QuadTree: " + duration/1000.0 + " s");
		
		// Sets the delta width and height of the entire map
		setGeoHeight(DataReader.getMaxY()-DataReader.getMinY());
		setGeoWidth(DataReader.getMaxX()-DataReader.getMinX());
		
		ratio = geoWidth/geoHeight;
		SplashScreen.use().setTaskName(Task.MAP);
		
		// Starts a new test timer
		startTime = System.currentTimeMillis();
		
		// Finds all the nodes in the view area
		nodes = QT.query(0, 0, geoWidth, geoHeight);
		DrawableItem.setMapSize(geoWidth, geoHeight, 0.0, 0.0);
		
		// Constructs the GoogleMaps style colors
		RoadSegment.setColors();
		
		// Finds all the edges for these nodes
		getEdgesFromNodes();
		
		// Ends timer, prints result
		endTime = System.currentTimeMillis();
		duration = endTime - startTime;
		System.out.println("Time to query all nodes and find their neighbours: " 
														+ duration/1000.0 + " s");
		
		System.out.println("Length of the result from full query: " + nodes.size());

		maxMapHeight = DataReader.getMaxY()-DataReader.getMinY();
		maxMapWidth = DataReader.getMaxX()-DataReader.getMinX();
		// Creates and adds roadSegments to an the arraylist 'edges'
		//calculatePixels();
		
		// Throws out the old contentPane, then adds a new and calls repaint/validate,
		// thus calling the internal method paintComponents found in the roadSegments objects
		
		startTime = System.currentTimeMillis();
		Window.use().updateMap();
		endTime = System.currentTimeMillis();
		duration = endTime - startTime;
		System.out.println("Time to update map the first time: " + duration/1000.0 + "s");
		
		System.out.printf("Graph has %d edges%n", graph.getE());
//		System.out.printf("Graph has %d edges%n", multiGraph.getE(1));
		//MemoryMXBean mxbean = ManagementFactory.getMemoryMXBean();
		//System.out.printf("Heap memory usage: %d MB%n", mxbean
		//		.getHeapMemoryUsage().getUsed() / (1000000));
		SplashScreen.use().close();
	}


	public static HashMap<String, String> getZipToCityMap() {
		return zipToCityMap;	
	}
	
	public static ArrayList<CoastPoint[]> getCoast() {
		return coast;
	}
}
