package Test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import Part1.DataReader;
import Part1.DijkstraSP;
import Part1.DijkstraSP.CompareType;
import Part1.WindowHandler;
import Part1.DijkstraSP.TransportWay;

public class DijkstraSPTest {
	
//	@Before
//	public void setUp() {		
//		DijkstraSP dsp = new DijkstraSP(WindowHandler.getGraph(), 2, TransportWay.CAR, CompareType.SHORTEST, true);
//	}

	@Test
	public void shortestRoute() {
		DijkstraSP dsp = new DijkstraSP(WindowHandler.getGraph(), 2, TransportWay.CAR, CompareType.SHORTEST, true);
		assertEquals(1,dsp.distTo(4), 0.1); //Shortest route between node 2 and 4 should be 1
	}

	@Test
	public void shortestRoute2() {
		DijkstraSP dsp = new DijkstraSP(WindowHandler.getGraph(), 2, TransportWay.CAR, CompareType.SHORTEST, false);
		assertEquals(2,dsp.distTo(4), 0.1); //Shortest route between node 2 and 4, when edge 2-4 is forbidden, should be 2
	}
	
	@Test
	public void bikeRoute() {
		DijkstraSP dsp = new DijkstraSP(WindowHandler.getGraph(), 2, TransportWay.BIKE, CompareType.SHORTEST, false);
		assertEquals(2,dsp.distTo(4), 0.1); //Shortest route between node 2 and 4, when edge 2-4 is forbidden, should be 2
	}
}
