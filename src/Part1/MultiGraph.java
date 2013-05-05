package Part1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class MultiGraph {
	
	private HashMap<Integer, HashMap<Integer, LinkedList<Edge>>> adj;	// hashmap of hashmaps, each containing the adjacency list for an edge type
	private ArrayList<Integer> V;										// number of nodes in each hashmap												// number of edges in each hashmap
	
	public MultiGraph() {
		adj = new HashMap<Integer, HashMap<Integer, LinkedList<Edge>>>();
		System.out.println("Hest!");
	}

	public int getV(int t) {
		return V.get(t);
	}
	
	public int getE(int t) {
		return adj.get(t).size();
	}
	
	/**
	 * 
	 * @param e
	 *            Edge to be added to adjancy list
	 */
	public void addEdge(Edge e) {
		if (!adj.containsKey(e.getType())) {
			adj.put(e.getType(), new HashMap<Integer, LinkedList<Edge>>());
		}
		if (!adj.get(e.getType()).containsKey(e.from())) {
			adj.get(e.getType()).put(e.from(), new LinkedList<Edge>());
		}
		adj.get(e.getType()).get(e.from()).add(e);
	}
	
	public Iterable<Edge> adj(int v, int t) {
		LinkedList<Edge> list = new LinkedList<Edge>();		// linkedlist for holding the result
		if(adj.containsKey(t)) {							// check if adj contains roads of that type
			if(adj.get(t).containsKey(v)) {					// if it does, check if that hashmap contains the given node
				int size = adj.get(t).get(v).size();		// get the size of the actual adjacency list
				for (int i = 0; i < size; i++) {			// iterate through the adjacency list
					for (Edge e : adj.get(t).get(v)) {		
						list.add(e);						// add each list to the result
					}
				}
			}
		}
		return list;
	}
	
	/**
	 * Returns an ArrayList of edges of a given type, adjacent to a given node.
	 * @param v
	 * @param t
	 * @return
	 */
	public Iterable<Edge> adjArr(int v, int t) {
		ArrayList<Edge> arr = new ArrayList<Edge>();
		arr.addAll(adj.get(t).get(v));
		return arr;
	}
	
	/**
	 * Iterates through the hash map of a given road type, returning all those roads as edges.
	 * @param t		The road type value
	 * @return		LinkedList<Edge> of roads 
	 */
	public Iterable<Edge> edgesOfType(int t) {
		LinkedList<Edge> list = new LinkedList<Edge>();		
		Iterator it = adj.get(t).entrySet().iterator();
	    while(it.hasNext()) {
	    	LinkedList<Edge> edgeList = (LinkedList<Edge>) it.next();
	    	for(Edge e : edgeList)
				list.add(e);
	    }
		return list;
	}
	
}
