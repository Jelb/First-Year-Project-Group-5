package Test;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.io.IOException;
import java.lang.reflect.Array;

import org.junit.After;
import org.junit.Test;

import Part1.DataReader;
import Part1.DrawableItem;
import Part1.Edge;
import Part1.Equation;
import Part1.Map;
import Part1.Node;
import Part1.Window;
import Part1.WindowHandler;

public class EquationTest {

	DataReader DR;
	
	public void setUp() {
		
	}
	
	@Test
	public void pointWithinChannel() {
		DR = DataReader.use("equationTestNodes.txt", "equationTestEdges.txt");
		DR.createNodeList();
		Edge edge =  new Edge(DR.nodes.get(1), DR.nodes.get(2), 0, "", 0, "0", "0", 0.1, 0, 0, 0, 0, true);
		assertFalse(Equation.pointWithinChannel(-3.0, 4.0, edge));
		assertTrue(Equation.pointWithinChannel(4.0, 5.0, edge));
	}
	
	@Test
	public void getNormalVector() {
		DR = DataReader.use("equationTestNodes.txt", "equationTestEdges.txt");
		DR.createNodeList();
		Edge edge =  new Edge(DR.nodes.get(1), DR.nodes.get(2), 0, "", 0, "0", "0", 0.1, 0, 0, 0, 0, true);
		double[] expArr = new double[] { -3.0 , 4.0 };
		double[] expArr2 = new double[] { -3.3 , 4.1 };
		assertArrayEquals(expArr, Equation.getNormalVector(edge), 0.001);
		assertFalse(Math.abs(expArr2[0] - Equation.getNormalVector(edge)[0]) < 0.001);
		assertFalse(Math.abs(expArr2[1] - Equation.getNormalVector(edge)[1]) < 0.001);
	}
	
	@Test
	public void distanceBetweenPointAndLine() {
		DR = DataReader.use("equationTestNodes.txt", "equationTestEdges.txt");
		DR.createNodeList();
		Edge edge =  new Edge(DR.nodes.get(1), DR.nodes.get(2), 0, "", 0, "0", "0", 0.1, 0, 0, 0, 0, true);
		double x1 = 1.0;
		double y1 = 7.5;
		assertEquals(5.0, Equation.distanceBetweenPointAndLine(edge, x1, y1), 0.001);
		double x2 = 1.0;
		double y2 = 9.5;
		assertFalse(Math.abs(Equation.distanceBetweenPointAndLine(edge, x2, y2) - 7.0) < 0.001);
	}
	
	@Test
	public void pointsToVector() {
		double x1 = 2.0;
		double y1 = 2.0;
		double x2 = 6.0;
		double y2 = 5.0;
		double[] expArr = new double[] { 4.0 , 3.0 };
		assertArrayEquals(expArr, Equation.pointsToVector(x1,y1,x2,y2), 0.01);
		double x3 = 1.0;
		double y3 = 2.0;
		double x4 = 6.0;
		double y4 = 5.0;
		double[] expArr2 = new double[] { 4.3 , 3.2 };
		assertFalse(Math.abs(expArr2[0] - Equation.pointsToVector(x3,y3,x4,y4)[0]) < 0.001);
		assertFalse(Math.abs(expArr2[1] - Equation.pointsToVector(x3,y3,x4,y4)[1]) < 0.001);
	}

	@Test
	public void nodesToVector() {
		DR = DataReader.use("equationTestNodes.txt", "equationTestEdges.txt");
		DR.createNodeList();
		Node node5 = DR.nodes.get(5);
		Node node1 = DR.nodes.get(1);
		double[] expArr = new double[] { -3.0 , 1.0 };
		assertArrayEquals(expArr, Equation.nodesToVector(node5,node1), 0.01);
		Node node2 = DR.nodes.get(2);
		Node node3 = DR.nodes.get(3);
		double[] expArr2 = new double[] { 4.3 , 3.2 };
		assertFalse(Math.abs(expArr2[0] - Equation.nodesToVector(node2,node3)[0]) < 0.001);
		assertFalse(Math.abs(expArr2[1] - Equation.nodesToVector(node2,node3)[1]) < 0.001);
	}
	
	@Test
	public void vectorLength() {
		double[] expVector1 = new double[] { -5.0 , 0.0 };
		double[] expVector2 = new double[] { 3.0 , -1.0 };
		assertEquals(5.0, Equation.vectorLength(expVector1), 0.001);
		assertFalse(Math.abs(Equation.vectorLength(expVector2) - 5.0) < 0.001);
	}
	
	@Test
	public void distanceBetweenNodes() {
		DR = DataReader.use("equationTestNodes.txt", "equationTestEdges.txt");
		DR.createNodeList();
		Node node1 = DR.nodes.get(1);
		Node node2 = DR.nodes.get(2);
		Node node5 = DR.nodes.get(5);
		assertEquals(5.0, Equation.distanceBetweenNodes(node1, node2), 0.001);
		assertFalse(Math.abs(Equation.distanceBetweenNodes(node2,node5) - 5.0) < 0.001);
	}

	@Test
	public void distanceBetweenPoints() {
		double x1 = 1.0;
		double y1 = 7.5;
		double x2 = 6.0;
		double y2 = 5.0;
		double x3 = 1.0;
		double y3 = 9.5;
		assertEquals(2.0, Equation.distanceBetweenPoints(x1, y1, x3, y3), 0.001);
		assertFalse(Math.abs(Equation.distanceBetweenPoints(x1,y1,x2,y2) - 5.0) < 0.001);
	}
	
	
//	@Test
//	public void onscreenPixelDistance() {
//		try {
//			WindowHandler.main(null);
//		}
//		catch (IOException e) {
//			System.out.println("Main method failed to start");
//		}
//		Window.use().setPreferredSize(new Dimension(500, 500));
//		Map.use().setSize(Window.use().getSize());
//		DrawableItem.setMapSize(10000.0, 10000.0, 0.0, 0.0);
//		double dist = 1000.0;
//		assertEquals(50.0, Equation.onscreenPixelDistance(dist), 0.01);
//		assertFalse(Math.abs(55.0 - Equation.onscreenPixelDistance(dist)) < 0.001);
//	}
	
	@After
	public void breakDown() {
		DR = null;
		DataReader.resetInstance();
	}
}
