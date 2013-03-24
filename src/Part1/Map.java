package Part1;

import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JPanel;

public class Map extends JPanel {
	/**
	 * Map is a JPanel with the lines drawn
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<RoadSegment> segments;
	private static Map instance = null;
	
	//Singleton check
	public static Map use() {
		if(instance == null) 
			instance = new Map();
		return instance;
	}
	
    public void paintComponent(Graphics g) {
        for(RoadSegment r : segments) {
        	if(r == null) continue;
            r.paintComponent(g);
        }
    }
    
    //Returns arrayList of road segments
    public ArrayList<RoadSegment> getRoadSegments() {
    	return segments;
    }
    
    public void newArrayList() {
    	segments = new ArrayList<RoadSegment>();
    }
    
    //Adds road segments to arrayList
    public void addRoadSegment(RoadSegment roadSegment) {
    	segments.add(roadSegment);
    }
    
}
