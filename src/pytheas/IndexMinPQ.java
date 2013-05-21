package pytheas;

import java.util.NoSuchElementException;

/**
 *  Sedgewick p. 655, p. 333 et al
 * 
 *  The <tt>IndexMinPQ</tt> class represents an indexed priority queue of generic keys.
 *  It supports the usual <em>insert</em> and <em>delete-the-minimum</em>
 *  operations, along with <em>delete</em> and <em>change-the-key</em> 
 *  methods. In order to let the client refer to items on the priority queue,
 *  an integer between 0 and NMAX-1 is associated with each key&mdash;the client
 *  uses this integer to specify which key to delete or change.
 *  It also supports methods for peeking at the minimum key,
 *  testing if the priority queue is empty, and iterating through
 *  the keys.
 *  <p>
 *  The <em>insert</em>, <em>delete-the-minimum</em>, <em>delete</em>,
 *  <em>change-key</em>, <em>decrease-key</em>, and <em>increase-key</em>
 *  operations take logarithmic time.
 *  The <em>is-empty</em>, <em>size</em>, <em>min-index</em>, <em>min-key</em>, and <em>key-of</em>
 *  operations take constant time.
 *  Construction takes time proportional to the specified capacity.
 *  <p>
 *  This implementation uses a binary heap along with an array to associate
 *  keys with integers in the given range.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/24pq">Section 2.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 */
public class IndexMinPQ<Key extends Comparable<Key>> {
	
	private int NMAX;        // maximum number of elements on PQ
    private int N;           // number of elements on PQ
    private int[] pq;        // binary heap using 1-based indexing
    private int[] qp;        // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
    private Key[] keys;      // keys[i] = priority of i
	
    /**
     * Create an empty indexed priority queue with indices between 0 and NMAX-1.
     * @throws java.lang.IllegalArgumentException if NMAX < 0
     */
    public IndexMinPQ(int NMAX) {
        if (NMAX < 0) throw new IllegalArgumentException();
        this.NMAX = NMAX;
        keys = (Key[]) new Comparable[NMAX + 1];    // make this of length NMAX??
        pq   = new int[NMAX + 1];
        qp   = new int[NMAX + 1];                   // make this of length NMAX??
        for (int i = 0; i <= NMAX; i++) qp[i] = -1;
    }
	
    /**
     * Is the priority queue empty?
     */
    public boolean isEmpty() { return N == 0; }
	
    /**
     * Is i an index on the priority queue?
     * @throws java.lang.IndexOutOfBoundsException unless (0 &le; i < NMAX)
     */
    public boolean contains(int i) {
        if (i < 0 || i >= NMAX) throw new IndexOutOfBoundsException();
        return qp[i] != -1;
    }
	
    /**
     * Associate key with index i.
     * @throws java.lang.IndexOutOfBoundsException unless 0 &le; i < NMAX
     * @throws java.util.IllegalArgumentException if there already is an item associated with index i.
     */
    public void insert(int i, Key key) {
        if (i < 0 || i >= NMAX) throw new IndexOutOfBoundsException();
        if (contains(i)) throw new IllegalArgumentException("index is already in the priority queue");
        N++;
        qp[i] = N;
        pq[N] = i;
        keys[i] = key;
        swim(N);
    }
	
    /**
     * Change the key associated with index i to the specified value.
     * @throws java.lang.IndexOutOfBoundsException unless 0 &le; i < NMAX
     * @throws java.util.NoSuchElementException no key is associated with index i
     */
    public void changeKey(int i, Key key) {
        if (i < 0 || i >= NMAX) throw new IndexOutOfBoundsException();
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        keys[i] = key;
        swim(qp[i]);
        sink(qp[i]);
    }
	
    /**
     * Delete the key associated with index i.
     * @throws java.lang.IndexOutOfBoundsException unless 0 &le; i < NMAX
     * @throws java.util.NoSuchElementException no key is associated with index i
     */
    public void delete(int i) {
        if (i < 0 || i >= NMAX) throw new IndexOutOfBoundsException();
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        int index = qp[i];
        exch(index, N--);
        swim(index);
        sink(index);
        keys[i] = null;
        qp[i] = -1;
    }
	
	/**
     * Return a minimal key.
     * @throws java.util.NoSuchElementException if priority queue is empty.
     */
    public Key minKey() { 
        if (N == 0) throw new NoSuchElementException("Priority queue underflow");
        return keys[pq[1]];        
    }
	
	/**
     * Return the index associated with a minimal key.
     * @throws java.util.NoSuchElementException if priority queue is empty.
     */
    public int minIndex() { 
        if (N == 0) throw new NoSuchElementException("Priority queue underflow");
        return pq[1];        
    }
	
    /**
     * Decrease the key associated with index i to the specified value.
     * @throws java.lang.IndexOutOfBoundsException unless 0 &le; i < NMAX
     * @throws java.lang.IllegalArgumentException if key &ge; key associated with index i
     * @throws java.util.NoSuchElementException no key is associated with index i
     */
    public void decreaseKey(int i, Key key) {
        if (i < 0 || i >= NMAX) throw new IndexOutOfBoundsException();
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        if (keys[i].compareTo(key) <= 0) throw new IllegalArgumentException("Calling decreaseKey() with given argument would not strictly decrease the key");
        keys[i] = key;
        swim(qp[i]);
    }
    
    /**
     * Increase the key associated with index i to the specified value.
     * @throws java.lang.IndexOutOfBoundsException unless 0 &le; i < NMAX
     * @throws java.lang.IllegalArgumentException if key &le; key associated with index i
     * @throws java.util.NoSuchElementException no key is associated with index i
     */
    public void increaseKey(int i, Key key) {
        if (i < 0 || i >= NMAX) throw new IndexOutOfBoundsException();
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        if (keys[i].compareTo(key) >= 0) throw new IllegalArgumentException("Calling increaseKey() with given argument would not strictly increase the key");
        keys[i] = key;
        sink(qp[i]);
    }
    
    /**
     * Delete a minimal key and return its associated index.
     * @throws java.util.NoSuchElementException if priority queue is empty.
     */
    public int delMin() { 
        if (N == 0) throw new NoSuchElementException("Priority queue underflow");
        int min = pq[1];        
        exch(1, N--); 
        sink(1);
        qp[min] = -1;            // delete
        keys[pq[N+1]] = null;    // to help with garbage collection
        pq[N+1] = -1;            // not needed
        return min; 
    }
	
	/**
     * Return the number of keys on the priority queue.
     */
    public int size() {
        return N;
    }
	
    /**
     * Return the key associated with index i.
     * @throws java.lang.IndexOutOfBoundsException unless 0 &le; i < NMAX
     * @throws java.util.NoSuchElementException no key is associated with index i
     */
    public Key keyOf(int i) {
        if (i < 0 || i >= NMAX) throw new IndexOutOfBoundsException();
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        else return keys[i];
    }
	
    /**
     * 'Swim' the index up through the PQ.
     * @param k		Index number
     */
	private void swim(int k)  {
        while (k > 1 && greater(k/2, k)) {
            exch(k, k/2);
            k = k/2;
        }
    }

	/**
	 * 'Sink' the index down through the PQ.
	 * @param k		Index number
	 */
    private void sink(int k) {
        while (2*k <= N) {
            int j = 2*k;
            if (j < N && greater(j, j+1)) j++;
            if (!greater(k, j)) break;
            exch(k, j);
            k = j;
        }
    }
	
    /**
     * Compares two indexes to find their priority.
     * @param i		Index of 'first' Key
     * @param j		Index of 'second' Key
     * @return		Return true if first key is greater than second
     */
    private boolean greater(int i, int j) {
        return keys[pq[i]].compareTo(keys[pq[j]]) > 0;
    }
    
    /**
     * Swap two Keys in the PQ around.
     * @param i		First Key
     * @param j		Sesonc Key
     */
	public void exch(int i, int j) {
		int swap = pq[i];						
		pq[i] = pq[j];
		pq[j] = swap;
		
		qp[pq[i]] = i;
		qp[pq[j]] = j;
	}
	
}
