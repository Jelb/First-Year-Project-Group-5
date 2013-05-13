package Part1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import javax.swing.JOptionPane;

/**
 * Dijkstra's Shortest Path algorithm, as per Sedgewick p. 655
 */
public class DijkstraSP {
	private Edge[] edgeTo;
	private double[] distTo;
	private IndexMinPQ<Double> pq;
	private static HashMap<TransportWay, HashSet<Integer>> disallowedTypes;
	
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
				if (disallowedTypes.get(t).contains(e.getType())) continue;
				if (e.getType() == 80 && !useFerry) continue;
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
	 * compares edges according to their drive time
	 */
	private void relaxDriveTime(Edge e) {
		int v = e.getFromNodeID(), w = e.getToNodeID();

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
	
	public double distTo(int v) {
		return distTo[v];
	}
	
	public boolean hasPathTo(int v) {
		return distTo[v] < Double.POSITIVE_INFINITY;
	}
	
	public ArrayList<Edge> pathTo(int v) {
		ArrayList<Edge> path = new ArrayList<Edge>();
		if(!hasPathTo(v)) {
			JOptionPane.showMessageDialog(Window.use(),"No route found. Try enabling ferry or bike routes.");
			return path;
		}
		for(Edge e = edgeTo[v]; e != null; e = edgeTo[e.getFromNodeID()]) {
			path.add(e);
		}
		return path;
	}
	
	private void createDisallowedTypes() {
		disallowedTypes = new HashMap<TransportWay, HashSet<Integer>>();
		disallowedTypes.put(TransportWay.CAR, new HashSet<Integer>(Arrays.asList(8, 48, 99))); //Roads cars can't drive at
		disallowedTypes.put(TransportWay.BIKE, new HashSet<Integer>(Arrays.asList(1, 2, 31, 32, 41, 42))); //Roads bikes can't drive at
	}
	
	public enum TransportWay {
		CAR, BIKE;
	}
	
	public enum CompareType {
		SHORTEST, FASTEST;
	}
}
