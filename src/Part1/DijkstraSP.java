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
			// lists the distTo[] array as it looks so far
			//System.out.println("********************");
			//System.out.println("distTo[]:");
			//for(int i = 1; i < distTo.length; i++) 
			//	System.out.println(i + " = " + distTo[i]);	
			
			// lists the edgeTo[] array as it looks so far
			//System.out.println("");
			//System.out.println("edgeTo[]:");
			//for(int i = 1; i < edgeTo.length; i++) {
			//	if(edgeTo[i] != null)
			//		System.out.println(i + ": -> " + edgeTo[i].getFromNodeID());
			//	else System.out.println(i + ": - > ???");
			//}

			//System.out.println("********************");
			
			int v = pq.delMin();
			//System.out.println("Relax edges emminating from node: " + v);
			for (Edge e : G.adj(v))
                relax(e);
		}
		System.out.println("PQ is EMPTY! :D");
		System.out.println("********************");
		System.out.println("distTo[]:");
		for(int i = 1; i < distTo.length; i++) 
			System.out.println(i + " = " + distTo[i]);	
		
		System.out.println("");
		System.out.println("edgeTo[]:");
		for(int i = 1; i < edgeTo.length; i++) {
			if(edgeTo[i] != null)
				System.out.println("edgeTo[" + i + "] = " + edgeTo[i].getFromNodeID());
			else System.out.println("edgeTo[" + i + "] = ???");
		}
		System.out.println("");
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
        } else {
																					//System.out.println("NO new shortest path to " + w);
        }
																					//System.out.println();
    }
	
	public double distTo(int v) {
		return distTo[v];
	}
	
	public boolean hasPathTo(int v) {
		return distTo[v] < Double.POSITIVE_INFINITY;
	}

	public void printPath(Stack<Edge> path) {
		while(!path.empty()) {
			Edge edge = path.pop();
			System.out.println(edge.printEdge());
		}
	}
	
	public Iterable<Edge> pathTo(int v) {
		if(!hasPathTo(v)) {
			System.out.println("No path to node " + v);
			return null;
		}
		Stack<Edge> path = new Stack<Edge>();
		for(Edge e = edgeTo[v]; e != null; e = edgeTo[e.getFromNodeID()]) path.push(e);
		return path;
	}
}
