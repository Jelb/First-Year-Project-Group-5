package Part1;

/**
 * Sedgewick p. 655, p. 333 et al
 */
public class IndexMinPQ<Key extends Comparable<Key>> {
	
	private int N;			// number of elements on PQ
	private int[] pq;		// binary heap using 1-based indexing
	private int[] qp;		// inverse: qp[pq[i]] = pq[qp[i]] = i
	private Key[] keys;		// items with priorities
	
	public IndexMinPQ(int maxN) {
		keys = (Key[]) new Comparable[maxN + 1];
		pq   = new int[maxN + 1];
		qp   = new int[maxN + 1];
		for(int i = 0; i <= maxN; i++) qp[i] = -1;
	}
	
	public boolean isEmpty() {
		return N == 0;
	}
	
	public boolean contains(int i) {
		return qp[i] != -1;
	}
	
	public void insert(int i, Key key) {
		N++;
		qp[i] = N;
		pq[N] = i;
		keys[i] = key;
		swim(N);
	}
	
	public void changeKey(int i, Key key) {
		keys[i] = key;
		swim(qp[i]);
		sink(qp[i]);
	}
	
	public void delete(int i) {
		exch(i, N--);
		swim(qp[i]);
		sink(qp[i]);
		keys[pq[N+1]] = null;
		qp[pq[N+1]] = -1;
	}
	
	public Key minKey() {
		return keys[pq[1]];
	}
	
	public int minIndex() {
		return pq[1];
	}
	
	public int delMin() {
		int indexOfMin = pq[1];
		exch(1, N--);
		sink(1);
		keys[pq[N+1]] = null;
		qp[pq[N+1]] = -1;
		return indexOfMin;
	}
	
	public int size() {
		return N;
	}
	
	public Key keyOf(int i) {
		return keys[i];
	}
	
	public void swim(int k) {
		while(k > 1 && less(k/2, k)) {
			exch(k/2, k);
			k = k/2;
		}
	}
	
	public void sink(int k) {
		while(2*k <= N) {
			int j = 2*k;
			if(j < N && less(j, j+1)) j++;
			if(!less(k, j)) break;
			exch(k, j);
			k = j;
		}
		
	}
	
	public void exch(int i, int j) {
		int t = pq[i];						// skulle t ha været en Key og ikke en int?
		pq[i] = pq[j];
		pq[j] = t;
	}
	
	public boolean less(int i, int j) {
		return pq[i] < 0; 					// skulle det ha været en key? og været pq[i].compareTo() ?
	}

}
