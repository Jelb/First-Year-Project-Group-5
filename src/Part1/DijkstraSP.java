package Part1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JOptionPane;

/**
 * Dijkstra's Shortest Path algorithm, as per Sedgewick p. 655
 */
public class DijkstraSP {
	private Edge[] edgeTo;
	private double[] distTo;
	private IndexMinPQ<Double> pq;
	private static HashMap<TransportWay, HashSet<Integer>> disallowedTypes;
	
	/**
	 * Constructs a shortest-path object based on a given point and travel constraints.
	 * @param G			The graph of node/edges
	 * @param s			The origin node ID number
	 * @param t			The type of transportation used (CAR or BIKE)
	 * @param c			The kind of weight used for pathfinding (FASTEST or SHORTEST)
	 * @param useFerry	True if pathfinder is allowed to use ferry connections
	 */
	public DijkstraSP(Graph G, int s, TransportWay t, CompareType c, boolean useFerry) {
		if (disallowedTypes == null) {
			createDisallowedTypes();
		}
		edgeTo = new Edge[G.getV()];
		distTo = new double[G.getV()];
		pq = new IndexMinPQ<Double>(G.getV());
		
		for(int v = 1; v < G.getV(); v++) {
			distTo[v] = Double.POSITIVE_INFINITY;
		}
		distTo[s] = 0.0;
		
		pq.insert(s, distTo[s]);
		while(!pq.isEmpty()) {			
			int v = pq.delMin();
			for (Edge e : G.adj(v)) {
				// if an edge is not allowed for the chosen transportation then it is skipped
				if (disallowedTypes.get(t).contains(e.type())) continue;
				if (e.type() == 80 && !useFerry) continue;
				if (c.equals(CompareType.SHORTEST)) relaxLength(e);
				else if (c.equals(CompareType.FASTEST)) relaxDriveTime(e);
			}
		}
	}
	
	/**
	 * Compares and relaxes edges according to their length.
	 */
	private void relaxLength(Edge e) {
        int v = e.fromID(), w = e.toID();

        if (distTo[w] > distTo[v] + e.length()) {
        	distTo[w] = distTo[v] + e.length();
            edgeTo[w] = e;
            if (pq.contains(w)) { 
            	pq.decreaseKey(w, distTo[w]);
            }
            else {
            	pq.insert(w, distTo[w]);
            }
        }
    }
	
	/**
	 * Compares and relaxes edges according to their drive time.
	 */
	private void relaxDriveTime(Edge e) {
		int v = e.fromID(), w = e.toID();

		if (distTo[w] > distTo[v] + e.getDriveTime()) {
			distTo[w] = distTo[v] + e.getDriveTime();
			edgeTo[w] = e;
			if (pq.contains(w)) { 
				pq.decreaseKey(w, distTo[w]);
			}
			else {
				pq.insert(w, distTo[w]);
			}
		}
	}
	
	/**
	 * Returns the distance to a given node.
	 * @param v		ID number of node
	 * @return		Distance to node v
	 */
	public double distTo(int v) {
		return distTo[v];
	}
	
	/**
	 * Checks if a path exists to a given node.
	 * @param v		ID number of node
	 * @return		Returns true if a path exists
	 */
	public boolean hasPathTo(int v) {
		return distTo[v] < Double.POSITIVE_INFINITY;
	}
	
	/**
	 * Calculates the path from the origin node to a given destination node.
	 * @param v		ID number of given destination node
	 * @return		ArrayList<Edge> of edges included in shortest path to destination
	 */
	public ArrayList<Edge> pathTo(int v) {
		ArrayList<Edge> path = new ArrayList<Edge>();
		if(!hasPathTo(v)) {
			JOptionPane.showMessageDialog(Window.use(),"No route found. Try enabling ferry or bike routes.");
			return path;
		}
		for(Edge e = edgeTo[v]; e != null; e = edgeTo[e.fromID()]) {
			path.add(e);
		}
		return path;
	}
	
	/**
	 * Creates a HashMap of the sets of road types disallowed for traveling.
	 */
	private static void createDisallowedTypes() {
		disallowedTypes = new HashMap<TransportWay, HashSet<Integer>>();
		disallowedTypes.put(TransportWay.CAR, new HashSet<Integer>(Arrays.asList(8, 48, 99))); //Roads cars can't drive at
		disallowedTypes.put(TransportWay.BIKE, new HashSet<Integer>(Arrays.asList(1, 2, 31, 32, 41, 42))); //Roads bikes can't drive at
	}
	
	/**
	 * Get the specific set of disallowed road types for the particular search.
	 * @return	hashSet containing disallowed road types
	 */
	public static HashSet<Integer> getDisallowedTypes() {
		if (disallowedTypes == null) {
			createDisallowedTypes();
		}
		HashSet<Integer> disallowed = new HashSet<Integer>();
		for (TransportWay t : TransportWay.values()) {
			disallowed.addAll(disallowedTypes.get(t));
		}
		return disallowed;
	}
	
	public enum TransportWay {
		CAR, BIKE;
	}
	
	public enum CompareType {
		SHORTEST, FASTEST;
	}
}
