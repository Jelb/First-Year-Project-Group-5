package Test;

import static org.junit.Assert.*;

import org.junit.Test;

import Part1.DataReader;
import Part1.Edge;
import Part1.Equation;

public class EquationTest {

	DataReader DR;
	
	public void setUp() {
		
	}
	
	@Test
	public void distanceBetweenPointAndLine() {
		DR = DataReader.use("equationTestNodes.txt", "equationTestEdges.txt");
		DR.createNodeList();
		Edge edge =  new Edge(DR.nodes.get(1), DR.nodes.get(2), 0, "", 0, "0", "0", 0.1, 0, 0, 0, 0, true);
		double x = 1.0;
		double y = 7.5;
		assertEquals(5.0, Equation.distanceBetweenPointAndLine(edge, x, y), 0.01);
	}
	
	@Test
	public void getNormalVector() {
		DR = DataReader.use("equationTestNodes.txt", "equationTestEdges.txt");
		DR.createNodeList();
		Edge edge =  new Edge(DR.nodes.get(1), DR.nodes.get(2), 0, "", 0, "0", "0", 0.1, 0, 0, 0, 0, true);
		double[] expectedArr = new double[] { -3.0 , 4.0 };
		assertArrayEquals(expectedArr, Equation.getNormalVector(edge), 0.01);
	}
	
	@Test
	public void pointsToVector() {
		double x1 = 2.0;
		double y1 = 2.0;
		double x2 = 6.0;
		double y2 = 5.0;
		double[] expArr = new double[] { 4.0 , 3.0 };
		assertArrayEquals(expArr, Equation.pointsToVector(x1,y1,x2,y2), 0.01);
	}
}
