package Test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Test;

import org.junit.Before;

import pytheas.DataReader;
import pytheas.Edge;
import pytheas.Graph;
import pytheas.Node;

public class DataReaderTest {
	
	DataReader DR;
	DataReader DR2;
	Graph graph;
	
	@Before
	public void setUp() {
		DR = DataReader.use("10nodestest.txt", "10edgestest.txt");
		DR.createNodeList();
		graph = DR.createGraphAndLongestRoadsList(10000);
		System.out.println("~~~~~~~~~~~");
	}

	@Test
	public void numberOfNodes() {
		//DR.createNodeList();
		//System.out.println("Number of nodes:" + DR.numberOfNodes);
		assertEquals(10, DR.getNodes().size()-1);
	}
	
	@Test
	public void graphSize() {
		//Graph graph = DR.createGraphAndLongestRoadsList(10000);
		//System.out.println("Number of nodes in graph:" + graph.getV()+1);
		assertEquals(11, graph.getV());
	}
	
	@Test
	public void numberOfEdges() {
		//Graph graph = DR.createGraphAndLongestRoadsList(10000);
		assertEquals(10, graph.getE());
	}
	
	@Test
	public void severalNeighbours() {
		//Graph graph = DR.createGraphAndLongestRoadsList(10000);
		//System.out.println("Graph adj(1): " + graph.adj(1));
		ArrayList<Edge> arrTest = new ArrayList<Edge>();
		Edge edge1 = new Edge(DR.getNodes().get(1), DR.getNodes().get(2), 58.63325, "", 8, "0", "0", 0.135, 0, 0, 0, 0, true);
		Edge edge2 = new Edge(DR.getNodes().get(1), DR.getNodes().get(3), 136.81998, "", 8, "0", "0", 0.315, 0, 0, 0, 0, true);
		arrTest.add(edge1);
		arrTest.add(edge2);
		//Edge(fromNode, toNode, length, vejnavn, type, v_postnr, h_postnr, true);
		assertEquals(arrTest, graph.adjArr(1));
	}
	
	@Test
	public void oneNeighbour() {
		//Graph graph = DR.createGraphAndLongestRoadsList(10000);
		//System.out.println("Graph adj(3): " + graph.adj(3));
		ArrayList<Edge> arrTest = new ArrayList<Edge>();
		Edge edge = new Edge(DR.getNodes().get(3), DR.getNodes().get(4), 20.01746, "", 8, "0", "0", 0.046, 0, 0, 0, 0, true);
		arrTest.add(edge);
		ArrayList<Edge> adjArr = graph.adjArr(3);
		for (int i = 0; i < arrTest.size(); i++) {
			Edge testEdge = arrTest.get(i);
			Edge realEdge = adjArr.get(i);
			System.out.println("Test edge: " + testEdge);
			System.out.println("Real edge: " + realEdge);
			assertEquals(arrTest.get(i), adjArr.get(i));
		}
	}
	
	@Test
	public void noNeighbours() {
		//Graph graph = DR.createGraphAndLongestRoadsList(10000);
		//System.out.println("Graph adj(4): " + graph.adj(4));
		ArrayList<Edge> arrTest = new ArrayList<Edge>();
		assertEquals(arrTest, graph.adjArr(2));
	}
	
	@Test
	public void maxAndMinValues() {
		System.out.println("Dr  minX :" + DataReader.getMinX());
		assertEquals(595308.61090-DR.getIncrease(), DataReader.getMinX(), 0.1);
		assertEquals(596860.47877+DR.getIncrease(), DataReader.getMaxX(), 0.1);
		assertEquals(6401221.77721-DR.getIncrease(), DataReader.getMinY(), 0.1);
		assertEquals(6402050.98297+DR.getIncrease(), DataReader.getMaxY(), 0.1);
	}
	
	@After
	public void breakDown() {
		DR = null;
		DR2 = null;
		DataReader.resetInstance();
		graph = null;
	}
}
