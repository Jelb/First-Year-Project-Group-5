package Part1;

import java.util.ArrayList;
import java.util.LinkedList;

// Based on code on p. 611 in Algorithms 4. ed. Sedgewick, Wayne
public class Graph {
	private final int V;	// number of nodes
	private int E;			// number of edges
	private LinkedList<Edge>[] adj; // array of adjacency lists
	
	public Graph(int V) {
		this.V = V;
		this.E = 0;
		adj = (LinkedList<Edge>[]) new LinkedList[V];
		// creates V linked lists, one for each node
		for (int v = 0; v < V; v++) {
			adj[v] = new LinkedList<Edge>();
		}
		
	}
	
	public int getV() { return V; }
	public int getE() { return E; }
	
	// adds an edge to the nodes that the edge connects
	public void addEdge(Edge e) {
		adj[e.from()].add(e);
		E++;
	}
	
	// returns adjacency list for the given node, as a LinkedList of edges
	public Iterable<Edge> adj(int v) { 
		return adj[v]; 
	}

	// returns adjacency list for the given node, as an ArrayList of edges
	public Iterable<Edge> adjArr(int v) { 
		ArrayList<Edge> arr = new ArrayList<Edge>();
		arr.addAll(adj[v]);
		return arr;
	}

	// returns linked list all edges in the graph
	public Iterable<Edge> edges() {
		LinkedList<Edge> list = new LinkedList<Edge>();
		for (int v = 0; v < V; v++)
			for (Edge e : adj[v])
				list.add(e);
		return list;
	}
}
