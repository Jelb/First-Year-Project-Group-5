package Part1;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.List;

import krakLoader.KrakLoader;
import QuadTree.QuadTree;

public class WindowHandler {

	/**
	 * Calculates the pixel coordinate of a given geo coordinate, and returns a new coordinate set with int values
	 * 
	 * @param geoCoord  The geo coordinate
	 * @return 			A new Coordinate set of int values (a pixel coordinate set)
	 */
	private Coordinate geoToPixel(Coordinate geoCoord) {
		double x = geoCoord.getXcoordDouble();
		double y = geoCoord.getYcoordDouble();
		
		int intX = (int) Math.round(( (x - 450000 + Window.origoX) * (Window.windowSize / 200) * Window.zoomFactor) / 1000);
		int intY = (int) Math.round(((6200000 + y + Window.origoY) * (Window.windowSize / 200) * Window.zoomFactor) / 1000);
		
		return new Coordinate(intX, intY);
	}
	
	/**
	 * Calculates the UMT coordinate of a given on-screen pixel coordinate, and returns a new coordinate set with double values
	 * 
	 * @param pixelCoord 	The coordinate of the pixel
	 * @return 				The UMT coordinate of said pixel
	 */	
	private Coordinate pixelToGeo(Coordinate pixelCoord) {
		int x = pixelCoord.getXcoordInt();
		int y = pixelCoord.getYcoordInt();
		
		double doubleX = (double) (( (x + 450000 - Window.origoX) / (Window.windowSize / 200) / Window.zoomFactor) * 1000);
		double doubleY = (double) (((6200000 + y - Window.origoY) / (Window.windowSize / 200) / Window.zoomFactor) * 1000);
		
		return new Coordinate(doubleX, doubleY);
	}
	

	public static void main(String[] args) throws IOException {
		Long startTime = System.currentTimeMillis();
		KrakLoader krakLoader = KrakLoader.use("kdv_node_unload.txt",
				"kdv_unload.txt");
		krakLoader.createNodeList();
		Graph graph = krakLoader.createGraph();
		QuadTree QT = krakLoader.createQuadTree();
		krakLoader = null;
		Long endTime = System.currentTimeMillis();
		Long duration = endTime - startTime;
		System.out.println("Time to create Nodelist, Graph and QuadTree: " + duration/1000.0 + " s");
		startTime = System.currentTimeMillis();
		List<Node> list = QT.query(0, 0, KrakLoader.getMaxX()-KrakLoader.getMinX(), KrakLoader.getMaxY()-KrakLoader.getMinY());
		System.out.println("Length of the result from full query: " + list.size());
		for (Node n : list) {
			Iterable<Edge> edges = graph.adjOut(n.getKdvID());
			for (Edge e : edges) {
				double x1 = n.getXCord();
				double y1 = n.getYCord();
				double x2 = e.getToNode().getXCord();
				double y2 = e.getToNode().getYCord();
			}
		}
		endTime = System.currentTimeMillis();
		duration = endTime - startTime;
		System.out.println("Time to query all nodes and find their neighbours: " + duration/1000.0 + " s");
		System.out.printf("Graph has %d edges%n", graph.getE());
		//MemoryMXBean mxbean = ManagementFactory.getMemoryMXBean();
		//System.out.printf("Heap memory usage: %d MB%n", mxbean
		//		.getHeapMemoryUsage().getUsed() / (1000000));
	}
	
}
