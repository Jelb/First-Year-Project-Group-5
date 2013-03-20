package Part1;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;

public class RoadSegment extends JComponent {
	/**
	 * The objects of this class are the JComponents which make up the lines on the map
	 */
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	private Color color;
	
	private static final long serialVersionUID = 1L;

	public RoadSegment(int x1,int y1, int x2, int y2, Color color){
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.color = color;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(color);
		g.drawLine(x1, y1, x2, y2);
	}
}
