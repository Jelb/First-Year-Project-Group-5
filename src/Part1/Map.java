package Part1;

import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.sun.xml.internal.ws.api.server.Container;

public class Map {
	//private RoadSegment[] segmentArray;
	private JComponent segment1;
	private JComponent segment2;
	private JPanel map;
	
	
	public Map(){
		//this.segmentArray = segmentArray;
	}
	
	public void TESTdrawSegments(){
		segment1 = new RoadSegmentJComponent(10,10,100,100,Color.black);
		segment2 = new RoadSegmentJComponent(100,100,200,500,Color.red);
		
		map = new JPanel();
		map.add(segment1);
		map.add(segment2);
	}
	
//	public void drawSegments(){
//		map = new JPanel();
//		for(RoadSegment segment : segmentArray){
//			segment.getX1 = x1;
//			segment.getY1 = y1;
//			segment.getX2 = x2;
//			segment.getY2 = y2;
//			segment.getColor = color;
//			RoadsegmentJComponent tempSegment = new RoadSegmentJComponent(x1, y1, x2, y2, color);
//			map.add(tempSegment);
//		}
//	}
	
	public JPanel getMap(){
		return map;
	}
}
