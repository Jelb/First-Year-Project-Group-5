package Part1;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

/**
 * Dijkstra's Shortest Path algorithm, as per Sedgewick p. 655
 */
public class DijkstraSP {
	private Edge[] edgeTo;
	private double[] distTo;
	private IndexMinPQ<Double> pq;
	private static HashMap<TransportWay, HashSet<Integer>> disallowedTypes;
	
	public DijkstraSP(Graph G, int s, TransportWay t, CompareType c) {
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
				if (disallowedTypes.get(t).contains(e.getType())) continue;
				if (c.equals(CompareType.SHORTEST)) relaxLength(e);
				else if (c.equals(CompareType.FASTEST)) relaxDriveTime(e);
			}
		}
	}
	
	/**
	 * compares edges according to their length
	 */
	private void relaxLength(Edge e) {
        int v = e.getFromNodeID(), w = e.getToNodeID();
        																			//System.out.println("Relaxing edge " + v + " -> " + w);

        if (distTo[w] > distTo[v] + e.length()) {
        	distTo[w] = distTo[v] + e.length();
            edgeTo[w] = e;
            																		//System.out.println("New shortest path to " + w + ": " + distTo[w]);
            if (pq.contains(w)) {
                																	//System.out.println("Removing node " + w + " from PQ"); 
            	pq.decreaseKey(w, distTo[w]);
            }
            else {
            																		//System.out.println("Adding node " + w + " to priority queue of nodes whose edges we need to relax");
            	pq.insert(w, distTo[w]);
            }
        }
    }
	
	/**
	 * compares edges according to their drive time
	 */
	private void relaxDriveTime(Edge e) {
		int v = e.getFromNodeID(), w = e.getToNodeID();

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
	
	public double distTo(int v) {
		return distTo[v];
	}
	
	public boolean hasPathTo(int v) {
		return distTo[v] < Double.POSITIVE_INFINITY;
	}
	
	public Iterable<Edge> pathTo(int v) {
		Stack<Edge> path = new Stack<Edge>();
		if(!hasPathTo(v)) {
			System.out.println("No path to node " + v);
			return path;
		}
		for(Edge e = edgeTo[v]; e != null; e = edgeTo[e.getFromNodeID()]) path.push(e);
		return path;
	}
	
	private void createDisallowedTypes() {
		disallowedTypes = new HashMap<TransportWay, HashSet<Integer>>();
		disallowedTypes.put(TransportWay.CAR, new HashSet<Integer>(Arrays.asList(8, 48, 80, 99)));
		disallowedTypes.put(TransportWay.BIKE, new HashSet<Integer>(Arrays.asList(1, 2, 31, 32, 41, 42, 80)));
	}
	
	public enum TransportWay {
		CAR, BIKE;
	}
	
	public enum CompareType {
		SHORTEST, FASTEST;
	}
}
