package Part1;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JPanel;

public class Map extends JPanel {
	/**
	 * Map is a JPanel with the lines drawn
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<RoadSegment> segments;
	private TomsTempClass test;
		
	public Map(){
		test = new TomsTempClass();
		segments = test.mapTestMethod();
		zoomWithBox();
	}

	public void zoomWithBox(){
		addMouseListener(new MouseAdapter(){
			
			public void mousePressed(MouseEvent e){				
				int pressedX = e.getX();
				int pressedY = e.getY();
				
				System.out.println("Pressed X : "+ pressedX);
				System.out.println("Pressed Y : "+ pressedY);
			}
			
			public void mouseReleased(MouseEvent e){
				int releasedX = e.getX();
				int releasedY =  e.getY();
				System.out.println("Released X : "+ releasedX);
				System.out.println("Released Y : "+ releasedY);
			}
		});		
	}
	
//	public void createSegmentArray(){
//		RoadSegment segment1 = new RoadSegment(10,10,100,100,Color.red);
//		RoadSegment segment2 = new RoadSegment(200,300,500,500,Color.black);
//		
//		segments = new ArrayList<RoadSegment>();
//		segments.add(segment1);
//		segments.add(segment2);		
//	}
	
    public void paintComponent(Graphics g) {
        for(RoadSegment r : segments) {
            r.paintComponent(g);
        }
    }
    
    public void getMapTestMethod() {
    	segments = test.mapTestMethod();
    }
    
}
