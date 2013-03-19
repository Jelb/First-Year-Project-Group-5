package QuadTree;

/**
 * 
 * Describes the method(s) that are shared amongst classes of the QuadTree that can be parents of other elements in the tree.
 */
public interface Parent {
	public void changeChild(Element oldChild, Element newChild);
}
