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
		
		for(int v = 1; v < G.getV(); v++) {
			distTo[v] = Double.POSITIVE_INFINITY;
		}
		distTo[s] = 0.0;
		
		pq.insert(s, distTo[s]);
		while(!pq.isEmpty()) {			
			int v = pq.delMin();
			Iterable<Edge> adj = G.adj(v);
			for (Edge e : G.adj(v))
                relax(e);
		}
	}
	
	private void relax(Edge e) {
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
		for(Edge e = edgeTo[v]; e != null; e = edgeTo[e.getFromNodeID()]) {
			path.push(e);
			System.out.println(e.getFromNodeID() + " -> " + e.getToNodeID());			
		}
		return path;
	}
}
