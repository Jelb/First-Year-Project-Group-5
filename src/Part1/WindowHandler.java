package Part1;

import java.io.IOException;
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
	static double geoWidth; //The width of the view area
	static double geoHeight; //The height of the view area
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
	
	/**
	 * Pans the map 10% in the direction specified.
	 * The if-statements makes sure that the user don't pan outside the map
	 * @param keyEvent A key event holding the value of the key pressed.
	 */
	public static void pan(Direction d) {
		double maxMapHeight = DataReader.getMaxY()-DataReader.getMinY();
		double maxMapWidth = DataReader.getMaxX()-DataReader.getMinX();
		switch(d){
			case NORTH:
				//If the diff between the border of the map and the view area is less than 0.1, go to the border
				if((maxMapHeight - (offsetY + geoHeight)) < geoHeight*0.1) {
					System.out.println("Pan North");
					System.out.println("Difference less than 0.1");
					pan(0, maxMapHeight - (offsetY + geoHeight));
					Window.use().updateMap();
				}
				else if((offsetY + geoHeight) < maxMapHeight) {
					System.out.println("OffsetY - geoHeight: " + (offsetY + geoHeight));
					System.out.println("MaxY: " + maxMapHeight);
					System.out.println("Pan North");
					pan(0, geoHeight*0.1);
					Window.use().updateMap();
				}
					break; 			
			case SOUTH:
				if(offsetY < geoHeight*0.1) {
					System.out.println("Pan South");
					pan(0, -offsetY);
					Window.use().updateMap();
				}
				else if(offsetY > 0) {
					System.out.println("Pan South");
					pan(0, -geoHeight*0.1);
					Window.use().updateMap();
				}
					break;
			case WEST:
				if(offsetX < geoWidth*0.1) {
					System.out.println("Pan West");
					pan(-offsetX, 0);
					Window.use().updateMap();
				}
				else if(offsetX > 0) {
					System.out.println("Pan West");
					pan(-geoWidth*0.1, 0);
					Window.use().updateMap();
				}
					break;
			case EAST:
				if((maxMapWidth - (offsetX + geoWidth)) < geoWidth*0.1) {
					System.out.println("Pan East");
					pan(maxMapWidth - (offsetX + geoWidth), 0);
					Window.use().updateMap();
				}
				else if(offsetX + geoWidth < maxMapWidth) {
					System.out.println("Pan East");
					pan(geoWidth*0.1, 0);
					Window.use().updateMap();
				}
					break; 
		}
	}
	
	/**
	 * A general pan method that takes as parameter how much the viewport has moved on the x-axis and y-axis
	 */
	//TODO: Optimize pan to take advantage of the point we already have loaded
	public static void pan(double deltaX, double deltaY) {
		search(deltaX, geoWidth+deltaX, deltaY, geoHeight+deltaY);
	}
	
	public static void zoomOut() {
		search(-geoWidth*0.1, geoWidth*1.1, -geoHeight*0.1, geoHeight*1.1);
		Window.use().updateMap();
	}
	
	public static void zoomIn() {
		search(geoWidth*0.1, geoWidth*0.9, geoHeight*0.1, geoHeight*0.9);
		Window.use().updateMap();
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
		//TODO: Add the longestRoadsFloor to each border of the search area and use this to optimize panning.
		
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
			geoWidth = newGeoWidth;
		}
		long startTime = System.currentTimeMillis();
		nodes = QT.query(geoXMin+offsetX-longestRoadsFloor, geoYMin+offsetY-longestRoadsFloor,
				geoXMax+offsetX+longestRoadsFloor, geoYMax+offsetY+longestRoadsFloor);
		System.out.println("Time for query in quadtree: " + (System.currentTimeMillis()-startTime)/1000.0);
		RoadSegment.setMapSize(geoXMax+offsetX, geoYMax+offsetY, geoXMin+offsetX, geoYMin+offsetY);
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
			if(lineIntersects(geoXMin, geoXMax, geoYMin, geoYMax, x1, x2,
				y1, y2)) Map.use().addRoadSegment(new RoadSegment(x1, y1, x2, y2, e.getType()));
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
		if (t == 1 || t == 2 || t == 3 || t == 4 || t==80) return true;
		else if (geoWidth < 100000 && (t == 5)) return true;
		else if (geoWidth < 30000) return true;
		else return false;
	}
	
	// Gets all the edges that go out from each Node in the list of nodes
	public static void getEdgesFromNodes() {
		//edges = new LinkedList<Edge>();
		Map.use().newArrayList();
		for (Node n : nodes) {
			Iterable<Edge> edgesFromNode = graph.adjOut(n.getKdvID());
			for (Edge e : edgesFromNode) {
				if (includeEdge(e)) {
					//edges.add(e);
					double x1 = e.getFromNode().getXCord();
					double y1 = e.getFromNode().getYCord();
					double x2 = e.getToNode().getXCord();
					double y2 = e.getToNode().getYCord();
					Map.use().addRoadSegment(new RoadSegment(x1, y1, x2, y2, e.getType()));
				}
			}
		}
	}
	
	//Adds road segments to arrayList within Map class
	public static void calculatePixels() {
		long time1 = 0;
		long time2 = 0;
		Map.use().newArrayList();
		// For all the edges currently in the field of view,
		// create a roadSegment and add it to the list
		for (Edge e : edges) {
			Long startTime = System.currentTimeMillis();
			double x1 = e.getFromNode().getXCord();
			double y1 = e.getFromNode().getYCord();
			double x2 = e.getToNode().getXCord();
			double y2 = e.getToNode().getYCord();
			Long endTime = System.currentTimeMillis();
			time1 += endTime-startTime;
			startTime = System.currentTimeMillis();
			Map.use().addRoadSegment(new RoadSegment(x1, y1, x2, y2, e.getType()));
			endTime = System.currentTimeMillis();
			time2 += endTime-startTime;
		}
		System.out.println("Time to read edges: " + (time1/1000.0) + "s");
		System.out.println("Time to insert road segments: " + (time2/1000.0) + "s");
	}
	
	public static void resetMap() {
		setGeoHeight(DataReader.getMaxY()-DataReader.getMinY());
		setGeoWidth(DataReader.getMaxX()-DataReader.getMinX());
		offsetX = 0;
		offsetY = 0;
		nodes = QT.query(0, 0, geoWidth, geoHeight);
		RoadSegment.setMapSize(geoWidth, geoHeight, 0.0, 0.0);
		getEdgesFromNodes();
		//calculatePixels();
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
		//calculatePixels();
		
		// Throws out the old contentPane, then adds a new and calls repaint/validate,
		// thus calling the internal method paintComponents found in the roadSegments objects
		
		startTime = System.currentTimeMillis();
		Window.use().updateMap();
		endTime = System.currentTimeMillis();
		duration = endTime - startTime;
		System.out.println("Time to update map the first time: " + duration/1000.0 + "s");
		
		System.out.printf("Graph has %d edges%n", graph.getE());
		//MemoryMXBean mxbean = ManagementFactory.getMemoryMXBean();
		//System.out.printf("Heap memory usage: %d MB%n", mxbean
		//		.getHeapMemoryUsage().getUsed() / (1000000));
	}
	
}
