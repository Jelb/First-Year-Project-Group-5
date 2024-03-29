package pytheas;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.ToolTipManager;

import pytheas.DijkstraSP.CompareType;
import pytheas.DijkstraSP.TransportWay;
import pytheas.SplashScreen.Task;
import pytheas.Window.TextType;

import QuadTree.QuadTree;

/**
 * WindowHandler handles input from the GUI class Window and takes care of calculations
 * needed to make all the classes cooperating. Collects input when calculated in other
 * classes and passes it to Window.
 */
public class WindowHandler {
	private static List<Node> nodes;
	private static List<Edge> edges;
	private static List<Edge> longestRoads;	// List of roads longer than 10 km
	private static int startNode, endNode;
	private static HashMap<String, HashSet<String>> roadToZipMap;
	private static int longestRoadsFloor;
	private static QuadTree QT;
	private static Graph graph;
	private static double geoWidth;		// The width of the view area in meters
	private static double geoHeight;	// The height of the view area in meters
	private static double offsetX;		// Offset of the current view area relative to the 'outer' map constraints
	private static double offsetY;		// Offset of the current view area relative to the 'outer' map constraints
	private static double ratio;		// The ratio between the window width and height. Is set by finding the best size from the screen
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
	public static Edge closestEdge(int pixelX, int pixelY) {
		double x = pixelToAbsoluteGeoX(pixelX);
		double y = pixelToAbsoluteGeoY(pixelY);

		Edge closestEdge = null;
		double shortestDist = Double.MAX_VALUE;

		//For every node in view area, find the closest edge
		for (Node n : nodes) {
			Iterable<Edge> edgesFromNode = graph.adj(n.getKdvID());
			for (Edge e : edgesFromNode) {
				if (e.isDrawable() && includeEdge(e)) {
					if (Equation.pointWithinChannel(x,y,e)) { //If the point has a dregree less than 90
						double tempDist = Equation.distanceBetweenPointAndLine(e,x,y);
						if (tempDist < shortestDist) {
							shortestDist = tempDist;
							closestEdge = e;
						}
						//If the dregree between the points is more than 90, 
						//calculate the vector lengths between the point and from- and to-node 
					} else { 
						double tempDist1 = Equation.vectorLength(Equation.pointsToVector(
								e.from().getAbsoluteXCoordinate(),
								e.from().getAbsoluteYCoordinate(),
								x, y));

						double tempDist2 = Equation.vectorLength(Equation.pointsToVector(
								e.to().getAbsoluteXCoordinate(),
								e.to().getAbsoluteYCoordinate(),
								x, y));

						// check distance to the FromNode
						if (tempDist1 < shortestDist) {
							shortestDist = tempDist1;
							closestEdge = e;
						}

						// check distance to the ToNode
						if (tempDist2 < shortestDist) {
							shortestDist = tempDist2;
							closestEdge = e;
						}
					}
				}
			}
		}
		if(Equation.onscreenPixelDistance(shortestDist) < 10.0) {
			return closestEdge;
		}
		return null;
	}

	/**
	 * I the text fields is a from-field, set the node to start node. Else, set to end node. 
	 */
	public static void setNode(int node, TextType t){
		if(t == TextType.FROM){
			WindowHandler.startNode = node;
		}
		else if (t == TextType.TO){
			WindowHandler.endNode = node;
		}
	}

	/**
	 * Makes a Dijkstra search with the parameters decided by the user.
	 * Saves the route in an array list which is added to the map together with info about the route.
	 */
	public static void pathFinding(TransportWay transport, CompareType compareType, boolean useFerry) {
		DijkstraSP dsp = new DijkstraSP(graph, startNode, transport, compareType, useFerry);

		ArrayList<Edge> route = dsp.pathTo(endNode);	// find route from start to destination node
		addRouteToMap(route);									// adding the route to the Map()
		if(!route.isEmpty())Window.use().addPathInfo(transport);
		Window.use().updateMap();
	}

	/**
	 * Adds the shortest path (static field 'route') to the roadSegments on the map.
	 * Makes a search in the QuadTree containing the entire path
	 */
	public static void addRouteToMap(ArrayList<Edge> route) {
		ArrayList<DrawableItem> path = new ArrayList<DrawableItem>();
		double minX = Double.MAX_VALUE, maxX = 0,  minY = Double.MAX_VALUE, maxY = 0, length = 0, driveTime = 0;
		//Finds the max and min x and y values, used for query search
		for(Edge edge : route) {
			double x1 = edge.from().getXCord();
			if (x1 < minX) minX = x1;
			if (x1 > maxX) maxX = x1;
			double y1 = edge.from().getYCord();
			if (y1 < minY) minY = y1;
			if (y1 > maxY) maxY = y1;
			double x2 = edge.to().getXCord();
			if (x2 < minX) minX = x2;
			if (x2 > maxX) maxX = x2;
			double y2 = edge.to().getYCord();
			if (y2 < minY) minY = y2;
			if (y2 > maxY) maxY = y2;
			path.add(new RoadSegment(x1, y1, x2, y2, 4242));
			length += edge.length();
			driveTime += edge.getDriveTime();
		}
		Map.use().setPath(path, length, driveTime);
		if (!path.isEmpty()) {
			double deltaX = (maxX-minX)*0.1;
			double deltaY = (maxY-minY)*0.1; 
			search(minX-deltaX-offsetX, maxX+deltaX-offsetX, minY-deltaY-offsetY, maxY+deltaY-offsetY);
		}
	}

	/**
	 * Centers the view on the given node. Used when searching for single address/node
	 */
	public static void centerOnNode(Node node) {
		double distance = 2000;
		search(node.getXCord()-distance-offsetX, node.getXCord()+distance-offsetX, 
				node.getYCord()-distance-offsetY, node.getYCord()+distance-offsetY);
	}
	
	/**
	 * Zooms out. Checks how far it is to the borders of the map. If the borders are closer than
	 * what we want to zoom, zoom only the remaining distance.
	 * Makes a query serach when it knows how far to zoom.
	 */
	public static void zoomOut(int zoomCount) {
		double minX = geoWidth*(0.111*zoomCount), maxX = geoWidth+geoWidth*(0.111*zoomCount),
				minY = geoHeight*(0.111*zoomCount), maxY = geoHeight+geoHeight*(0.111*zoomCount);
		//If the distance from the view area to the maxMapHeight or width is less than what we want to zoom
		//Zoom only the remaining distance plus the view area height or width
		if((maxMapHeight - (offsetY + geoHeight)) < geoHeight*(0.111*zoomCount)) {
			maxY = maxMapHeight - (offsetY + geoHeight) + geoHeight;
		}
		//If the offset is less than what we want to zoom, zoom only the offset
		if(offsetY < geoHeight*(0.111*zoomCount)) {
			minY = offsetY;
		}
		if(offsetX < geoWidth*(0.111*zoomCount)) {
			minX = offsetX;
		}
		if((maxMapWidth - (offsetX + geoWidth)) < geoWidth*(0.111*zoomCount)) {
			maxX = maxMapWidth - (offsetX + geoWidth) + geoWidth;
		}
		search(-minX, maxX, -minY, maxY);
	}
	
	public static void zoomIn(int zoomCount) {
		double minX = geoWidth*0.1;
		double minY = geoHeight*0.1;
		double maxX = geoWidth-geoWidth*0.1;
		double maxY = geoHeight-geoHeight*0.1;
		for (int i = 0; i < zoomCount-2; i++) {
			double prevMinX = minX;
			double prevMaxX = maxX;
			double prevMinY = minY;
			double prevMaxY = maxY;
			minX += (prevMaxX-prevMinX)*0.1;
			minY += (prevMaxY-prevMinY)*0.1;
			maxX -= (prevMaxX-prevMinX)*0.1;
			maxY -= (prevMaxY-prevMinY)*0.1;
		}
		search(minX, maxX, minY, maxY);
	}

	/**
	 * Searches an area using pixel-values. Used for zooming in by right dragging mouse.
	 * Searches only if the area is more than 10 meters.
	 */
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

	/**
	 * Takes a search area in UTM-coordinates, and saves a list of all the edges 
	 * to be drawn on the map.
	 */
	public static void search(double geoXMin, double geoXMax, double geoYMin, double geoYMax) {

		//ensures that the search area is wider than 500 meters
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

		//recalculate the search area to fit the aspect ratio
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
		
		//System.out.println("Size of map [width x height]: 
		//	[" + (int)geoWidth + " m x " + (int)geoHeight + " m]");

		long startTime = System.currentTimeMillis(); // Timing experiments
		nodes = QT.query(geoXMin+offsetX-longestRoadsFloor, geoYMin+offsetY-longestRoadsFloor,
				geoXMax+offsetX+longestRoadsFloor, geoYMax+offsetY+longestRoadsFloor);
		//System.out.println("Time to query quadtree: " +
		//	(System.currentTimeMillis()-startTime)/1000.0 + " s");
		
		DrawableItem.setMapSize(geoXMax+offsetX, geoYMax+offsetY, geoXMin+offsetX, geoYMin+offsetY);
		RoadSegment.setZoomLevel();
		
//		startTime = System.currentTimeMillis(); // Timing experiments
		getEdgesFromNodes();
		//Timing experiments
//		System.out.println("Time to find all drawable edges and create road segments: " + 
//									(System.currentTimeMillis()-startTime)/1000.0 + " s");
		
		offsetX += geoXMin;
		offsetY += geoYMin;

		//check whether any of the longest roads intersect with the searched area
		for (Edge e : longestRoads) {
			double x1 = e.from().getXCord();
			double y1 = e.from().getYCord();
			double x2 = e.to().getXCord();
			double y2 = e.to().getYCord();					// for now, no borders will be drawn
				if(e.type() == 1) Map.use().addRoadSegment1(
						new RoadSegment(x1, y1, x2, y2, e.type()));
				else if(e.type() == 2) Map.use().addRoadSegment2(
						new RoadSegment(x1, y1, x2, y2, e.type()));
				else if(e.type() == 3) Map.use().addRoadSegment3(
						new RoadSegment(x1, y1, x2, y2, e.type()));
				else if(e.type() == 4) Map.use().addRoadSegment4(
						new RoadSegment(x1, y1, x2, y2, e.type()));
				else if(e.type() == 5) Map.use().addRoadSegment5(
						new RoadSegment(x1, y1, x2, y2, e.type()));
				else if(e.type() == 6) Map.use().addRoadSegment6(
						new RoadSegment(x1, y1, x2, y2, e.type()));
				else Map.use().addRoadSegment8(
						new RoadSegment(x1, y1, x2, y2, e.type()));
			}
		
//		startTime = System.currentTimeMillis(); // Timing experiments
		Window.use().updateMap();	
//		System.out.println("Time to draw map: " + (System.currentTimeMillis()-startTime)/1000.0 + " s"); // Timing experiments
//		System.out.println(); // Timing experiments
		
	}

	/**
	 * Returns true if the given edge will be shown on the map with the current zoom level
	 */
	private static boolean includeEdge(Edge e) {
		int t = e.type();
		if (t == 1 || t == 2 || t == 3 || t == 80) return true;
		else if (geoWidth < 250000 && (t == 4)) return true;
		else if (geoWidth < 60000 && (t == 5)) return true;
		else if (geoWidth < 8000) return true;
		else return false;
	}

	/**
	 * Gets all the edges that go out from each Node in the list of nodes.
	 * Adds the found edges to the map.
	 */
	public static void getEdgesFromNodes() {
		Map.use().newArrayList();
		for (Node n : nodes) {
			Iterable<Edge> edgesFromNode = graph.adj(n.getKdvID());
			for (Edge e : edgesFromNode) {
				if (e.isDrawable() && includeEdge(e)) {
					double x1 = e.from().getXCord();
					double y1 = e.from().getYCord();
					double x2 = e.to().getXCord();
					double y2 = e.to().getYCord();
					if(e.type() == 1) Map.use().addRoadSegment1(
							new RoadSegment(x1, y1, x2, y2, e.type()));
					else if(e.type() == 2) Map.use().addRoadSegment2(
							new RoadSegment(x1, y1, x2, y2, e.type()));
					else if(e.type() == 3) Map.use().addRoadSegment3(
							new RoadSegment(x1, y1, x2, y2, e.type()));
					else if(e.type() == 4) Map.use().addRoadSegment4(
							new RoadSegment(x1, y1, x2, y2, e.type()));
					else if(e.type() == 5) Map.use().addRoadSegment5(
							new RoadSegment(x1, y1, x2, y2, e.type()));
					else if(e.type() == 6) Map.use().addRoadSegment6(
							new RoadSegment(x1, y1, x2, y2, e.type()));
					else Map.use().addRoadSegment8(
							new RoadSegment(x1, y1, x2, y2, e.type()));
				}
			}
		}
	}

	/**
	 * Resets the map view so that the whole map is shown.
	 */
	public static void resetMap() {
		search(-offsetX, maxMapWidth-offsetX, -offsetY, maxMapHeight-offsetY);
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

	/**
	 * Used to get the effective size of the screen. 
	 * The effective size of the screen is equals to the size of the screen 
	 * excluding the size reserved for other objects such as docks and tool bars.
	 * <br>
	 * The effective size of the screen is used to set the ratio of the application 
	 * and its max height;
	 */
	private static void getEffectiveScreenSize() {
		Rectangle mwb = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		double h = mwb.height;
		double w = mwb.width;
		ratio = (w/h);
		Window.setMaxHeight(mwb.height);	
	}

	/**
	 * Sets the ratio of the application.
	 * This method should only be called if the calculated ratio 
	 * forces map-data to be omitted.  
	 * 
	 * @param argRatio The new ratio of the application
	 */
	public static void setRatio(double argRatio) {
		ratio = argRatio;
	}

	public static void main(String[] args) throws IOException {
		String nodeFile = "kdv_node_unload.txt";
		String edgeFile = "kdv_unload.txt";
		String coastFile = "coasts.dat";
		String lakeFile = "lake.dat";
		String borderFile = "border.dat";
		getEffectiveScreenSize();
		SplashScreen.initialize(nodeFile, edgeFile, coastFile, lakeFile);
		SplashScreen.use();

		//Initializing of data from DataReader
		DataReader dataReader = DataReader.use(nodeFile,edgeFile);

		SplashScreen.use().setTaskName(Task.COAST);
		coast = dataReader.readCoast(coastFile);
		lakes = dataReader.readCoast(lakeFile);
		border = dataReader.readCoast(borderFile);

		Map.setCoast(coast, lakes);
		Map.setBorder(border);

		//ArrayList with Nodes
		dataReader.createNodeList();

		longestRoadsFloor = 10000;

		//All roads with length larger than the longest road floor are added to the longest roads list
		//Makes graph object and list of roads longer than the longest roads floor
		graph = dataReader.createGraphAndLongestRoadsList(longestRoadsFloor);
//		System.out.println("Density of graph: " + graph.getE()/Math.pow(graph.getV(),2)); // Used in the report

		//Makes and returns a quadTree
		QT = dataReader.createQuadTree();
//		System.out.println("Height of quadtree: " + QT.getHeight()); // Used in the report
		longestRoads = dataReader.getLongestRoads();
		roadToZipMap = dataReader.getRoadToZipMap();
		zipToCityMap = dataReader.getZipToCityMap("post.dat");

		//set arraylist of all egdes
		edges = dataReader.getEdges();

		//Avoid loitering
		dataReader = null;

		SplashScreen.use().setTaskName(Task.MAP);
		
		// Getting the desired tool tip effect when mouse over on a road
		ToolTipManager.sharedInstance().setInitialDelay(500);
		ToolTipManager.sharedInstance().setDismissDelay(1200);
		ToolTipManager.sharedInstance().setReshowDelay(0);

		maxMapHeight = DataReader.getMaxY()-DataReader.getMinY();
		maxMapWidth = DataReader.getMaxX()-DataReader.getMinX();
		
		// Searches the entire map, producing a view of the entire map
		search(0, maxMapWidth, 0, maxMapHeight);

		SplashScreen.use().close();
	}
}
