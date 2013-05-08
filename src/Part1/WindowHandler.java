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
import Part1.Window.TextType;
import QuadTree.QuadTree;

public class WindowHandler {
	private static List<Node> nodes;
	private static List<Edge> edges;
	private static List<Edge> longestRoads;
	private static int startNode;
	private static int endNode;
	private static HashMap<String, HashSet<String>> roadToZipMap;
	private static int longestRoadsFloor;
	private static QuadTree QT;
	private static Graph graph;
	private static AddressParser ap;
	private static double geoWidth;		// The  width of the view area in meters
	private static double geoHeight;	// The height of the view area in meters
	private static double offsetX;		// Offset of the current view area relative to the 'outer' map constraints
	private static double offsetY;		// Offset of the current view area relative to the 'outer' map constraints
	private static double ratio;
	private static double minLength;
	private static double maxMapHeight;	// = DataReader.getMaxY()-DataReader.getMinY();
	private static double maxMapWidth;	// = DataReader.getMaxX()-DataReader.getMinX();
	private static HashMap<String, String> zipToCityMap;
	private static ArrayList<CoastPoint[]> coast, lakes, islands, border;

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
			Iterable<Edge> edgesFromNode = graph.adj(n.getKdvID());
			for (Edge e : edgesFromNode) {
				if (e.isDrawable() && includeEdge(e)) {
//					double x1 = e.getFromNode().getAbsoluteXCoordinate();
//					double y1 = e.getFromNode().getAbsoluteYCoordinate();
//					double x2 = e.getToNode().getAbsoluteXCoordinate();
//					double y2 = e.getToNode().getAbsoluteYCoordinate();
					if (Equation.pointWithinChannel(n,e)) {
						double tempDist = Equation.distanceBetweenPointAndLine(e, n);
						if (tempDist < shortestDist) {
							shortestDist = tempDist;
							closestEdge = e;
						}
					} else {

						double tempDist1 = Equation.vectorLength(Equation.nodesToVector(e.getFromNode(), e.getToNode()));
						double tempDist2 = Equation.vectorLength(Equation.nodesToVector(e.getToNode(), e.getFromNode()));

						if (tempDist1 < shortestDist) {
							shortestDist = tempDist1;
							closestEdge = e;
						}

						if (tempDist2 < shortestDist) {
							shortestDist = tempDist2;
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
			System.out.println("No name found");
		}
	}
	

	

	
	public static void randomSPtest() {
		Random rnd = new Random();
		startNode = rnd.nextInt(graph.getV()-1)+1;
		endNode = rnd.nextInt(graph.getV()-1)+1;
		//pathFindingTest();
	}
	
	public static void setNode(int node, TextType t){
		if(t == TextType.FROM){
			WindowHandler.startNode = node;
		}
		else if (t == TextType.TO){
			WindowHandler.endNode = node;
		}
			
	}
	
	/**
	 * 
	 */
	public static void pathFindingTest(TransportWay transport, CompareType compareType, boolean useFerry) {
		System.out.println("Start node: " + startNode);
		
		System.out.println("Creating SP object... ");
		DijkstraSP dsp = new DijkstraSP(graph, startNode, transport, compareType, useFerry);
		
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
	 * Makes a search in the QuadTree containing the entire path
	 */
	public static void addRouteToMap(Stack<Edge> route) {
		ArrayList<DrawableItem> path = new ArrayList<DrawableItem>();
		double minX = Double.MAX_VALUE, maxX = 0,  minY = Double.MAX_VALUE, maxY = 0;
		while(!route.empty()) {
			Edge edge = route.pop();
			double x1 = edge.getFromNode().getXCord();
			if (x1 < minX) minX = x1;
			if (x1 > maxX) maxX = x1;
			double y1 = edge.getFromNode().getYCord();
			if (y1 < minY) minY = y1;
			if (y1 > maxY) maxY = y1;
			double x2 = edge.getToNode().getXCord();
			if (x2 < minX) minX = x2;
			if (x2 > maxX) maxX = x2;
			double y2 = edge.getToNode().getYCord();
			if (y2 < minY) minY = y2;
			if (y2 > maxY) maxY = y2;
			boolean border = false;											// for now, no borders will be drawn
			path.add(new RoadSegment(x1, y1, x2, y2, 4242, border));
		}
		Map.use().setPath(path);
		if (!path.isEmpty()) {
			double deltaX = (maxX-minX)*0.1;
			double deltaY = (maxY-minY)*0.1; 
			search(minX-deltaX-offsetX, maxX+deltaX-offsetX, minY-deltaY-offsetY, maxY+deltaY-offsetY);
		}
	}
	
	/**
	 * Centers the view on the given node
	 */
	public static void centerOnNode(Node node) {
		double distance = 2000;
		search(node.getXCord()-distance-offsetX, node.getXCord()+distance-offsetX, 
				node.getYCord()-distance-offsetY, node.getYCord()+distance-offsetY);
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
		RoadSegment.setZoomLevel();
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
		
		Map.use().createBufferImage();
		Window.use().repaint();
		
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
//		else if (geoWidth < 100000 && (t == 5)) return true;
		else if (geoWidth < 13000) return true;
		else return false;
	}
	
	// Gets all the edges that go out from each Node in the list of nodes
	public static void getEdgesFromNodes() {
		Map.use().newArrayList();
		for (Node n : nodes) {
			Iterable<Edge> edgesFromNode = graph.adj(n.getKdvID());
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

	public static Graph getGraph() {
		return graph;
	}
	


	public static HashMap<String, String> getZipToCityMap() {
		return zipToCityMap;	
	}
	
	public static ArrayList<CoastPoint[]> getCoast() {
		return coast;
	}
	
	public static ArrayList<CoastPoint[]> getLakes() {
		return lakes;
	}

	public static void main(String[] args) throws IOException {
		String nodeFile = "kdv_node_unload.txt";
		String edgeFile = "kdv_unload.txt";
		String coastFile = "coastline.txt";
		String lakeFile = "lake.txt";
		String islandFile = "island.txt";
		String borderFile = "border.txt";
				
		SplashScreen.initialize(nodeFile, edgeFile);
		SplashScreen.use();
		
		// Timer for testing purposes
		Long startTime = System.currentTimeMillis();
		
		//Initializing of data from KrakLoader
		DataReader dataReader = DataReader.use("kdv_node_unload.txt","kdv_unload.txt");
		
		//ArraylList with Nodes
		dataReader.createNodeList();
		coast = dataReader.readCoast(coastFile);
		lakes = dataReader.readCoast(lakeFile);
		islands = dataReader.readCoast(islandFile);
		border = dataReader.readCoast(borderFile);
		
		Map.setCoast(coast, lakes, islands);
		Map.setBorder(border);
		
		longestRoadsFloor = 10000;
		
		//All roads with length larger than the longest road floor are added to the longest roads list
		//Makes graph object and list of roads longer than the longest roads floor
		graph = dataReader.createGraphAndLongestRoadsList(longestRoadsFloor);
		
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
		
		// Throws out the old contentPane, then adds a new and calls repaint/validate,
		// thus calling the internal method paintComponents found in the roadSegments objects
		
		startTime = System.currentTimeMillis();
		Window.use().updateMap();
		endTime = System.currentTimeMillis();
		duration = endTime - startTime;
		System.out.println("Time to update map the first time: " + duration/1000.0 + "s");
		
		System.out.printf("Graph has %d edges%n", graph.getE());
		SplashScreen.use().close();
	}

}
