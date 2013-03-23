package Part1;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

public class RoadSegment extends JComponent {
	/**
	 * The objects of this class are the JComponents which make up the lines on the map
	 */
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	private int TYP;
	
	private static final long serialVersionUID = 1L;

	public RoadSegment(int x1,int y1, int x2, int y2, int TYP){
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.TYP = TYP;
	}
	
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		// determine paint color by road type
		switch(TYP) {
			//Case motervej
			case 1:
				g2.setColor(Color.orange);			
				break;
			//Case mototrafikvej
			case 2:
				g2.setColor(Color.yellow);
				break;
			//Case mean roads
			case 3:
			case 4:
				g2.setColor(Color.yellow);
				break;
			//Case landevej
			case Landevej:
				g2.setColor(Color.black);
				break;
			//Case gade
			case Gade:
				g2.setColor(Color.white);
				break;
			//Casse sti
			case Sti:
				g2.setColor(Color.lightGray);
				break;
		}
	
		// determine road width by zoom factor
		int roadWidth = 1;
		if(Window.zoomFactor > 1.5) roadWidth = 10;
		if(Window.zoomFactor > 3)   roadWidth = 10;
		if(Window.zoomFactor > 5)   roadWidth = 10;
		
		
		// setting stroke type
		g2.setStroke(new BasicStroke(roadWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		// draw the road segment
		g2.drawLine(x1, y1, x2, y2);
	}
}
