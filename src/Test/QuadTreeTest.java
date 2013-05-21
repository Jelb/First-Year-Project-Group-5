package Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pytheas.Node;

import QuadTree.Element;
import QuadTree.Leaf;
import QuadTree.Parent;
import QuadTree.QuadNode;
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
		Element actualRoot = QT.getRoot();
		QuadNode expRoot = new QuadNode(0, 0, 10, 10);
		Element expNW = new Leaf(3, (Parent) expRoot, 0, 5, 5, 10);
		expNW.insert(new Node(3,7,0));
		expNW.insert(new Node(2,9,1));
		Element expNE = new Leaf(3, (Parent) expRoot, 5, 5, 10, 10);
		expNE.insert(new Node(7,7,4));
		Element expSW = new Leaf(3, (Parent) expRoot, 0, 0, 5, 5);
		expSW.insert(new Node(1,2,2));
		Element expSE = new Leaf(3, (Parent) expRoot, 5, 0, 10, 5);
		expSE.insert(new Node(8,4,3));
		expRoot.setNE(expNE);
		expRoot.setNW(expNW);
		expRoot.setSE(expSE);
		expRoot.setSW(expSW);
		assertEquals(expRoot, actualRoot);
	}
	
	@Test
	public void emptyQuadrant() {
		QT.insert(3, 7, 0);
		QT.insert(2, 9, 1);
		QT.insert(1, 2, 2);
		QT.insert(8, 4, 3);
		Element actualRoot = QT.getRoot();
		QuadNode expRoot = new QuadNode(0, 0, 10, 10);
		Element expNW = new Leaf(3, (Parent) expRoot, 0, 5, 5, 10);
		expNW.insert(new Node(3,7,0));
		expNW.insert(new Node(2,9,1));
		Element expNE = new Leaf(3, (Parent) expRoot, 5, 5, 10, 10);
		Element expSW = new Leaf(3, (Parent) expRoot, 0, 0, 5, 5);
		expSW.insert(new Node(1,2,2));
		Element expSE = new Leaf(3, (Parent) expRoot, 5, 0, 10, 5);
		expSE.insert(new Node(8,4,3));
		expRoot.setNE(expNE);
		expRoot.setNW(expNW);
		expRoot.setSE(expSE);
		expRoot.setSW(expSW);
		assertEquals(expRoot, actualRoot);
	}
	
	@Test (expected = Exception.class)
	public void outsideBorders() {
		QT.insert(20, 11, 0);
	}
	
	@Test
	public void querySearchUnpartitioned() {
		List<Node> expList = new ArrayList<Node>();
		QT.insert(1, 2, 0);
		QT.insert(7, 4, 1);
		expList.add(new Node(7, 4, 1));
		QT.insert(9, 3, 2);
		expList.add(new Node(9, 3, 2));
		List<Node> actualList = QT.query(4, 1, 10, 6);
		HashSet<Node> expSet = new HashSet<Node>(expList);
		HashSet<Node> actualSet = new HashSet<Node>(actualList);
		assertEquals(expSet, actualSet);
	}
	
	@Test
	public void querySearchPartitionedOnce() {
		HashSet<Node> expSet = new HashSet<Node>();
		QT.insert(1, 2, 0);
		QT.insert(7, 4, 1);
		expSet.add(new Node(7, 4, 1));
		QT.insert(9, 3, 2);
		expSet.add(new Node(9,3,2));
		QT.insert(7, 5, 3);
		expSet.add(new Node(7,5,3));
		HashSet<Node> actualSet = new HashSet<Node>(QT.query(4, 1, 10, 6));
		assertEquals(expSet, actualSet);
	}
	
	@Test
	public void querySearchEdge() {
		HashSet<Node> expSet = new HashSet<Node>();
		QT.insert(7, 6, 0);
		expSet.add(new Node(7,6,0));
		HashSet<Node> actualSet = new HashSet<Node>(QT.query(4, 1, 10, 6));
		assertEquals(expSet, actualSet);
	}
	
	@Test
	public void coordinatesOnSubQuadrantEdge() {
		QT.insert(1, 2, 0);
		QT.insert(7, 4, 1);
		QT.insert(9, 3, 2);
		QT.insert(7, 5, 3);
		Element actualRoot = QT.getRoot();
		QuadNode expRoot = new QuadNode(0, 0, 10, 10);
		Element expNW = new Leaf(3, (Parent) expRoot, 0, 5, 5, 10);
		Element expNE = new Leaf(3, (Parent) expRoot, 5, 5, 10, 10);
		expNE.insert(new Node(7,5,3));
		Element expSW = new Leaf(3, (Parent) expRoot, 0, 0, 5, 5);
		expSW.insert(new Node(1,2,0));
		Element expSE = new Leaf(3, (Parent) expRoot, 5, 0, 10, 5);
		expSE.insert(new Node(7,4,1));
		expSE.insert(new Node(9,3,2));
		expRoot.setNE(expNE);
		expRoot.setNW(expNW);
		expRoot.setSE(expSE);
		expRoot.setSW(expSW);
		assertEquals(expRoot, actualRoot);
	}
	
	@Test
	public void coordinatesOnTheMiddle() {
		QT.insert(1, 2, 0);
		QT.insert(7, 4, 1);
		QT.insert(9, 3, 2);
		QT.insert(5, 5, 3);
		Element actualRoot = QT.getRoot();
		QuadNode expRoot = new QuadNode(0, 0, 10, 10);
		Element expNW = new Leaf(3, (Parent) expRoot, 0, 5, 5, 10);
		expNW.insert(new Node(5,5,3));
		Element expNE = new Leaf(3, (Parent) expRoot, 5, 5, 10, 10);
		Element expSW = new Leaf(3, (Parent) expRoot, 0, 0, 5, 5);
		expSW.insert(new Node(1,2,0));
		Element expSE = new Leaf(3, (Parent) expRoot, 5, 0, 10, 5);
		expSE.insert(new Node(7,4,1));
		expSE.insert(new Node(9,3,2));
		expRoot.setNE(expNE);
		expRoot.setNW(expNW);
		expRoot.setSE(expSE);
		expRoot.setSW(expSW);
		assertEquals(expRoot, actualRoot);
	}
	
	@Test
	public void queryPartlyOutsideBorders() {
		QT.insert(1, 2, 0);
		HashSet<Node> expSet = new HashSet<Node>();
		expSet.add(new Node(1,2,0));
		HashSet<Node> actualSet = new HashSet<Node>(QT.query(-2, -1, 3, 3));
		assertEquals(expSet, actualSet);
		
	}
	
	@Test 
	public void queryTotallyOutsideBorders() {
		QT.insert(1, 2, 0);
		List<Node> list = QT.query(-20, -10, -2, -1);
		assertEquals(0, list.size());
	}
	
	@Test
	public void queryOnLine() {
		QT.insert(5,5,0);
		HashSet<Node> expSet = new HashSet<Node>();
		expSet.add(new Node(5,5,0));
		HashSet<Node> actualSet = new HashSet<Node>(QT.query(3, 5, 7, 5));
		assertEquals(expSet, actualSet);
	}
	
	@Test
	public void emptyQuerySearch() {
		QT.insert(5,5,0);
		List<Node> list = QT.query(3, 6, 7, 9);
		assertEquals(0, list.size());
	}

}
