package Part1;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Based on code on p. 611 in Algorithms 4. ed. Sedgewick, Wayne
 */
public class Graph {
	private final int V;	// length of graph. Number of nodes is V-1
	private int E;			// number of edges
	private LinkedList<Edge>[] adj; // array of adjacency lists
	
	/**
	 * Graph constructor. Makes graph array and inserts empty linked lists in every index.
	 * @param V		Length of graph
	 */
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
	
	/**
	 * Adds an edge to the nodes that the edge connects
	 */
	public void addEdge(Edge e) {
		adj[e.from()].add(e);
		E++;
	}
	
	/**
	 * Returns adjacency list for the given node, as a LinkedList of edges
	 */
	public Iterable<Edge> adj(int v) { 
		return adj[v]; 
	}

	/**
	 * Returns adjacency list for the given node, as an ArrayList of edges
	 */
	public ArrayList<Edge> adjArr(int v) { 
		ArrayList<Edge> arr = new ArrayList<Edge>();
		arr.addAll(adj[v]);
		return arr;
	}
}
