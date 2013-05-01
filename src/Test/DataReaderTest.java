package Test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
import Part1.DataReader;
import Part1.Edge;
import Part1.Graph;

import org.junit.Before;

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
		assertEquals(10, DR.nodes.size()-1);
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
		Edge edge1 = new Edge(DR.nodes.get(1), DR.nodes.get(2), 58.63325, "", 8, "0", "0", 0.135, true);
		Edge edge2 = new Edge(DR.nodes.get(1), DR.nodes.get(3), 136.81998, "", 8, "0", "0", 0.315, true);
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
		Edge edge = new Edge(DR.nodes.get(3), DR.nodes.get(4), 20.01746, "", 8, "0", "0", 0.046, true);
		arrTest.add(edge);
		assertEquals(arrTest.iterator(), graph.adjArr(3));
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
		assertEquals(595308.61090, DR.minX, 0.1);
		assertEquals(596860.47877, DR.maxX, 0.1);
		assertEquals(6401221.77721, DR.minY, 0.1);
		assertEquals(6402050.98297, DR.maxY, 0.1);
	}
	
	//TODO: Evt. tjek hvis forkert fil
	@Test 
	public void wrongNodeFile() {
		DR.setInstance();
		DR2 = DataReader.use("wrongFile.txt", "10edgestest.txt");
		DR2.createNodeList();
	}
	
	@Test 
	public void wrongEdgeFile() {
		DR2.setInstance();
		DataReader DR3 = DataReader.use("10nodestest.txt", "wrongFile.txt");
		DR2.createGraphAndLongestRoadsList(10000);
		//The program will cloose down and do nothing
	}
}
