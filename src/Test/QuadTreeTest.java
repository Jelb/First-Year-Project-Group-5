package Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import QuadTree.Coordinate;
import QuadTree.QuadTree;

public class QuadTreeTest {

	QuadTree QT;
	
	@Before
	public void setUp() {
		QT = new QuadTree(3, 10, 10);
		System.out.println("~~~~~~~~~~~");
	}
	
	@Test
	public void threeCoordinates() {
		QT.insert(1, 2, 0);
		QT.insert(7, 4, 1);
		QT.insert(9, 3, 2);
		QT.showTree();
	}
	
	@Test
	public void edgeCoordinate() {
		QT.insert(0, 10, 0);
		QT.showTree();
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
		List<Coordinate> coordList = new ArrayList<Coordinate>();
		QT.insert(1, 2, 0);
		coordList.add(new Coordinate(1, 2, 0));
		QT.insert(7, 4, 1);
		coordList.add(new Coordinate(7, 4, 1));
		QT.insert(9, 3, 2);
		coordList.add(new Coordinate(9, 3, 2));
		List<Coordinate> list = QT.query(4, 1, 10, 6);
		for (Coordinate c : list) System.out.println(c.getX() + ", " + c.getY());
	}
	
	@Test
	public void querySearchPartitionedOnce() {
		QT.insert(1, 2, 0);
		QT.insert(7, 4, 1);
		QT.insert(9, 3, 2);
		QT.insert(7, 5, 3);
		List<Coordinate> list = QT.query(4, 1, 10, 6);
		QT.showTree();
		System.out.println();
		for (Coordinate c : list) System.out.println(c.getX() + ", " + c.getY());
	}
	
	@Test
	public void querySearchEdge() {
		QT.insert(7, 6, 0);
		List<Coordinate> list = QT.query(4, 1, 10, 6);
		for (Coordinate c : list) System.out.println(c.getX() + ", " + c.getY());
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
		List<Coordinate> list = QT.query(-2, -1, 3, 3);
		for (Coordinate c : list) System.out.println(c.getID());
	}
	
	@Test (expected = Exception.class)
	public void queryTotallyOutsideBorders() {
		QT.insert(1, 2, 0);
		List<Coordinate> list = QT.query(-20, -10, -2, -1);
		for (Coordinate c : list) System.out.println(c.getID());
	}
	
	@Test
	public void queryOnLine() {
		QT.insert(5,5,0);
		List<Coordinate> list = QT.query(3, 5, 7, 5);
		for (Coordinate c : list) System.out.println(c.getID());
	}
	
	@Test
	public void emptyQuerySearch() {
		QT.insert(5,5,0);
		List<Coordinate> list = QT.query(3, 6, 7, 9);
		System.out.println("Length of list: " + list.size());
		assertEquals(0, list.size());
	}
	
	@After
	public void breakDown() {
		System.out.println("~~~~~~~~~~~");
	}

}
