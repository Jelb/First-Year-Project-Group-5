package Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Part1.Node;
import QuadTree.Element;
import QuadTree.Leaf;
import QuadTree.QuadTree;

public class QuadTreeTest {

	QuadTree QT;
	
	@Before
	public void setUp() {
		QT = new QuadTree(3, 10, 10);
	}
	
	@Test
	public void threeCoordinates() {
		QT.insert(1, 2, 0);
		QT.insert(7, 4, 1);
		QT.insert(9, 3, 2);
		Element actualRoot = QT.getRoot();
		Element expRoot = new Leaf(3, QT, 0, 0, 10, 10);
		expRoot.insert(new Node(1, 2, 0));
		expRoot.insert(new Node(7, 4, 1));
		expRoot.insert(new Node(9, 3, 2));
		assertEquals(expRoot, actualRoot);
	}
	
	@Test
	public void edgeCoordinate() {
		QT.insert(0, 10, 0);
		Element actualRoot = QT.getRoot();
		Element expRoot = new Leaf(3, QT, 0, 0, 10, 10);
		expRoot.insert(new Node(0, 10, 0));
		assertEquals(expRoot, actualRoot);
	}
	
	@Test
	public void fiveCoordinates() {
		QT.insert(3, 7, 0);
		QT.insert(2, 9, 1);
		QT.insert(1, 2, 2);
		QT.insert(8, 4, 3);
		QT.insert(7, 7, 4);
		QT.showTree();
	}
	
	@Test
	public void emptyQuadrant() {
		QT.insert(3, 7, 0);
		QT.insert(2, 9, 1);
		QT.insert(1, 2, 2);
		QT.insert(8, 4, 3);
		QT.showTree();
	}
	
	@Test (expected = Exception.class)
	public void outsideBorders() {
		QT.insert(20, 11, 0);
		QT.showTree();
	}
	
	@Test
	public void querySearchUnpartitioned() {
		List<Node> coordList = new ArrayList<Node>();
		QT.insert(1, 2, 0);
		coordList.add(new Node(1, 2, 0));
		QT.insert(7, 4, 1);
		coordList.add(new Node(7, 4, 1));
		QT.insert(9, 3, 2);
		coordList.add(new Node(9, 3, 2));
		List<Node> list = QT.query(4, 1, 10, 6);
		for (Node c : list) System.out.println(c.getXCord() + ", " + c.getYCord());
	}
	
	@Test
	public void querySearchPartitionedOnce() {
		QT.insert(1, 2, 0);
		QT.insert(7, 4, 1);
		QT.insert(9, 3, 2);
		QT.insert(7, 5, 3);
		List<Node> list = QT.query(4, 1, 10, 6);
		QT.showTree();
		System.out.println();
		for (Node c : list) System.out.println(c.getXCord() + ", " + c.getYCord());
	}
	
	@Test
	public void querySearchEdge() {
		QT.insert(7, 6, 0);
		List<Node> list = QT.query(4, 1, 10, 6);
		for (Node c : list) System.out.println(c.getXCord() + ", " + c.getYCord());
	}
	
	@Test
	public void coordinatesOnSubQuadrantEdge() {
		QT.insert(1, 2, 0);
		QT.insert(7, 4, 1);
		QT.insert(9, 3, 2);
		QT.insert(7, 5, 0);
		QT.showTree();
	}
	
	@Test
	public void coordinatesOnTheMiddle() {
		QT.insert(1, 2, 0);
		QT.insert(7, 4, 1);
		QT.insert(9, 3, 2);
		QT.insert(5, 5, 3);
		QT.showTree();
	}
	
	@Test (expected = Exception.class)
	public void queryPartlyOutsideBorders() {
		QT.insert(1, 2, 0);
		List<Node> list = QT.query(-2, -1, 3, 3);
		for (Node c : list) System.out.println(c.getKdvID());
	}
	
	@Test (expected = Exception.class)
	public void queryTotallyOutsideBorders() {
		QT.insert(1, 2, 0);
		List<Node> list = QT.query(-20, -10, -2, -1);
		for (Node c : list) System.out.println(c.getKdvID());
	}
	
	@Test
	public void queryOnLine() {
		QT.insert(5,5,0);
		List<Node> list = QT.query(3, 5, 7, 5);
		for (Node c : list) System.out.println(c.getKdvID());
	}
	
	@Test
	public void emptyQuerySearch() {
		QT.insert(5,5,0);
		List<Node> list = QT.query(3, 6, 7, 9);
		System.out.println("Length of list: " + list.size());
		assertEquals(0, list.size());
	}

}
