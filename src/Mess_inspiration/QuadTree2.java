package Mess_inspiration;


public class QuadTree2<Key extends Comparable, Value> {
	private Node root;

    // helper node data type - denne klasse skal ud
    private class Node {
        Key x, y;              // x- and y- coordinates
        Node NW, NE, SE, SW;   // four subtrees
        Value value;           // associated data

        Node(Key x, Key y, Value value) { //Løsninger: Node kan extend'e Key, og lave værdierne
            this.x = x;					 // om til Keys i stedet for double - eller man kan parse.
            this.y = y;					// Evt. undgå Key
            this.value = value;
        }
    }


  /***********************************************************************
    *  Insert (x, y) into appropriate quadrant
    ***********************************************************************/
    public void insert(Key x, Key y, Value value) { //Navneskift: xCord, yCord, kdvID
        root = insert(root, x, y, value);
    }

    private Node insert(Node root, Key x, Key y, Value value) { //Navneskrift: Node h --> Node root
        if (root == null) return new Node(x, y, value);
        //// if (eq(x, h.x) && eq(y, h.y)) h.value = value;  // duplicate
        //x og y-værdierne er centrum?
        else if ( less(x, root.x) &&  less(y, root.y)) root.SW = insert(root.SW, x, y, value);
        else if ( less(x, root.x) && !less(y, root.y)) root.NW = insert(root.NW, x, y, value);
        else if (!less(x, root.x) &&  less(y, root.y)) root.SE = insert(root.SE, x, y, value);
        else if (!less(x, root.x) && !less(y, root.y)) root.NE = insert(root.NE, x, y, value);
        return root;
    }


  /***********************************************************************
    *  Range search.
    ***********************************************************************/

    public void query2D(Interval2D<Key> rect) {
        query2D(root, rect);
    }

    private void query2D(Node h, Interval2D<Key> rect) { //navneskift Node h --> Node root (to do)
        if (h == null) return;
        Key xmin = rect.intervalX.low; //Kalder helt ned til Interval for at finde low
        Key ymin = rect.intervalY.low;
        Key xmax = rect.intervalX.high;
        Key ymax = rect.intervalY.high;
        if (rect.contains(h.x, h.y)) //Hvis centrum ligger i rektanglen, print centrum-Nodes værdier
            System.out.println("    (" + h.x + ", " + h.y + ") " + h.value);
        //Metoden tjekker for, hvor området strækker sig til
        if ( less(xmin, h.x) &&  less(ymin, h.y)) query2D(h.SW, rect);
        if ( less(xmin, h.x) && !less(ymax, h.y)) query2D(h.NW, rect);
        if (!less(xmax, h.x) &&  less(ymin, h.y)) query2D(h.SE, rect);
        if (!less(xmax, h.x) && !less(ymax, h.y)) query2D(h.NE, rect);
    }


   /*************************************************************************
    *  helper comparison functions
    *************************************************************************/

    private boolean less(Key k1, Key k2) { return k1.compareTo(k2) <  0; } //Tjek for lig med?
    private boolean eq  (Key k1, Key k2) { return k1.compareTo(k2) == 0; }


   /*************************************************************************
    *  test client
    *************************************************************************/
    public static void main(String[] args) {
        //int M = Integer.parseInt(args[0]);   // queries
    	int M = 10;
        //int N = Integer.parseInt(args[1]);   // points
    	int N = 1000;

        QuadTree<Integer, String> st = new QuadTree<Integer, String>();

        // insert N random points in the unit square
        for (int i = 0; i < N; i++) {
            Integer x = (int) (100 * Math.random());
            Integer y = (int) (100 * Math.random());
            // System.out.println("(" + x + ", " + y + ")");
            st.insert(x, y, "P" + i);
        }
        System.out.println("Done preprocessing " + N + " points");

        // do some range searches
        for (int i = 0; i < M; i++) {
            Integer xmin = (int) (100 * Math.random());
            Integer ymin = (int) (100 * Math.random());
            Integer xmax = xmin + (int) (10 * Math.random());
            Integer ymax = ymin + (int) (20 * Math.random());
            Interval<Integer> intX = new Interval<Integer>(xmin, xmax);
            Interval<Integer> intY = new Interval<Integer>(ymin, ymax);
            Interval2D<Integer> rect = new Interval2D<Integer>(intX, intY);
            System.out.println(rect + " : ");
            st.query2D(rect);
        }
    }

}