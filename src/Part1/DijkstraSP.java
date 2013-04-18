package Part1;

import java.util.Stack;

/**
 * Dijkstra's Shortest Path algorithm, as per Sedgewick p. 655
 */
public class DijkstraSP {
	private Edge[] edgeTo;
	private double[] distTo;
	private IndexMinPQ<Double> pq;
	
	public DijkstraSP(Graph G, int s) {
		edgeTo = new Edge[G.getV()];
		distTo = new double[G.getV()];
		pq = new IndexMinPQ<Double>(G.getV());
		
		for(int v = 0; v < G.getV(); v++) {
			distTo[v] = Double.POSITIVE_INFINITY;
		}
		distTo[s] = 0.0;
		
		pq.insert(s, 0.0);
		while(!pq.isEmpty()) {
			relax(G, pq.delMin());
		}
	}
	
	private void relax(Graph G, int v) {
		for(Edge e : G.adj(v)) {
			int w = e.getToNodeID();
			if(distTo[w] > distTo[v] + e.length()) {
				distTo[w] = distTo[v] + e.length();
				edgeTo[w] = e;
				if(pq.contains(w)) pq.changeKey(w, distTo[w]);
				else			   pq.insert(w, distTo[w]);
			}
		}
	}
	
	public double distTo(int v) {
		return distTo[v];
	}
	
	public boolean hasPathTo(int v) {
		return distTo[v] < Double.POSITIVE_INFINITY;
	}

	public  Iterable<Edge> pathTo(int v) {
		if(!hasPathTo(v)) return null;
		Stack<Edge> path = new Stack<Edge>();
		for(Edge e = edgeTo[v]; e != null; e = edgeTo[e.getFromNodeID()]) path.push(e);
		return path;
	}
}
