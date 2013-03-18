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
	private ArrayList<RoadSegmentJComponent> segments;
		
	public Map(){
		//createSegmentArray();
		TomsTempClass test = new TomsTempClass();
		segments = test.mapTestMethod();
	}

	
	public void createSegmentArray(){
		RoadSegmentJComponent segment1 = new RoadSegmentJComponent(10,10,100,100,Color.red);
		RoadSegmentJComponent segment2 = new RoadSegmentJComponent(200,300,500,500,Color.black);
		
		segments = new ArrayList<RoadSegmentJComponent>();
		segments.add(segment1);
		segments.add(segment2);		
	}
	
    public void paintComponent(Graphics g) {
        for(RoadSegmentJComponent r : segments) {
            r.paintComponent(g);
        }
    }
    
}
