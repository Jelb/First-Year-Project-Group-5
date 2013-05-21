package Test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pytheas.DataReader;
import pytheas.DijkstraSP;
import pytheas.Graph;
import pytheas.WindowHandler;
import pytheas.DijkstraSP.CompareType;
import pytheas.DijkstraSP.TransportWay;


public class DijkstraSPTest {
	Graph graph;
	
	@Before
	public void setUp() {		
		DataReader dataReader = DataReader.use("testNodes.txt","testEdges.txt");
		dataReader.createNodeList();
		graph = dataReader.createGraphAndLongestRoadsList(10000);
	}

	@Test
	public void shortestRoute() {
		DijkstraSP dsp = new DijkstraSP(graph, 2, TransportWay.CAR, CompareType.SHORTEST, true);
		assertEquals(87,dsp.distTo(6), 0.1); //Shortest route between node 2 and 6 should be 46+41 (edge 2-4 + edge 4-6)
	}

	@Test
	public void shortestRoute2() {
		DijkstraSP dsp = new DijkstraSP(graph, 2, TransportWay.CAR, CompareType.SHORTEST, false);
		assertEquals(92,dsp.distTo(4), 0.1); //Shortest route between node 2 and 4, when edge 2-4 is forbidden, should be 45+47 (edge 2-3 + edge 3-4)
	}
	
	@Test
	public void bikeRoute() {
		DijkstraSP dsp = new DijkstraSP(graph, 11, TransportWay.BIKE, CompareType.SHORTEST, false);
		assertEquals(92,dsp.distTo(12), 0.1); //Shortest route between node 11 and 12, when edge 11-12 is forbidden, should be 48+44 (edge 11-10 + edge 10-12)
	}
	
	@Test
	public void fastestRoute() {
		DijkstraSP dsp = new DijkstraSP(graph, 8, TransportWay.CAR, CompareType.FASTEST, true);
		assertEquals(60,dsp.distTo(6), 0.1); //Fastest route between node 8 and 6 should take route 8-7-6 (time 30+30, dist 67), and note 8-6 (shortest, time 90, dist 50)
	}
	
	@Test
	public void noRoute() {
		DijkstraSP dsp = new DijkstraSP(graph, 4, TransportWay.BIKE, CompareType.SHORTEST, true);
		assertEquals(false,dsp.hasPathTo(5));
	}
	
	@Test
	public void oneDirection() {
		DijkstraSP dsp = new DijkstraSP(graph, 2, TransportWay.BIKE, CompareType.SHORTEST, true);
		assertEquals(false,dsp.hasPathTo(1)); //Edge 1-2 is one-way
	}
}
