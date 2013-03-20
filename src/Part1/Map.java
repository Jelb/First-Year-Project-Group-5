package Part1;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Map extends JPanel {
	/**
	 * Map is a JPanel with the lines drawn
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<RoadSegment> segments;
	private TomsTempClass test;
		
	public Map(){
		//createSegmentArray();
		test = new TomsTempClass();
		segments = test.mapTestMethod();
	}

	
	public void createSegmentArray(){
		RoadSegment segment1 = new RoadSegment(10,10,100,100,Color.red);
		RoadSegment segment2 = new RoadSegment(200,300,500,500,Color.black);
		
		segments = new ArrayList<RoadSegment>();
		segments.add(segment1);
		segments.add(segment2);		
	}
	
    public void paintComponent(Graphics g) {
        for(RoadSegment r : segments) {
            r.paintComponent(g);
        }
    }
    
    public void getMapTestMethod() {
    	segments = test.mapTestMethod();
    }
    
}
