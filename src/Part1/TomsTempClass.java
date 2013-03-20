package Part1;

import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;

public class TomsTempClass {
	
	private double roadSeg[][]	= {
										{ 450000, 6030000, 440000, 6060000 },
										{ 440000, 6060000, 480000, 6090000 },
										{ 440000, 6060000, 450000, 6120000 },
										{ 480000, 6090000, 450000, 6120000 },
										{ 430000, 6180000, 450000, 6120000 },
										{ 460000, 6150000, 450000, 6120000 },
										{ 460000, 6150000, 500000, 6140000 },
										{ 510000, 6145000, 500000, 6140000 },
										{ 510000, 6145000, 565000, 6130000 },
										{ 560000, 6170000, 565000, 6130000 },
										{ 525000, 6095000, 565000, 6130000 },
										{ 550000, 6090000, 565000, 6130000 },
									}; 

	public ArrayList<RoadSegment> mapTestMethod() {
		ArrayList<RoadSegment> rs = new ArrayList<RoadSegment>();
		
		for(int i = 0; i < 12; i++) {
			RoadSegment segment = 
			new RoadSegment(geoXToPixel(roadSeg[i][0]), geoYToPixel(roadSeg[i][1]), geoXToPixel(roadSeg[i][2]), geoYToPixel(roadSeg[i][3]), Color.red);
			rs.add(segment);
		}
		
		return rs;
	}
	
	private int geoXToPixel(double geoX) {
		return (int) Math.round(( (geoX - 400000 + Window.origoX) * (Window.windowSize / 2) * Window.zoomFactor) / 100000);
	}
	
	private int geoYToPixel(double geoY) {
		return (int) Math.round((( geoY - 6000000 + Window.origoY) * (Window.windowSize / 2) * Window.zoomFactor) / 100000);
	}
}
