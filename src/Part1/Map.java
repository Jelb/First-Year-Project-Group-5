package Part1;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;


public class Map extends JPanel {
	/**
	 * Map is a JPanel with the lines drawn
	 */
	private ArrayList<RoadSegment> segments;
	private ArrayList<DrawableItem> path;
	private static ArrayList<CoastPoint[]> coast, lake, island, border; 
	private static Map instance = null;
	private ArrayList<Flag> flags;
	
	private Image offScreen = null;
	private Graphics offgc;
	private String toolTip;
	
	private Map() {
	}

	//Singleton check
	public static Map use() {
		if(instance == null) {
			instance = new Map();
			instance.path = new ArrayList<DrawableItem>();
			instance.flags = new ArrayList<Flag>();
			ToolTipManager.sharedInstance().setInitialDelay(0);
			ToolTipManager.sharedInstance().setDismissDelay(800);
			ToolTipManager.sharedInstance().setReshowDelay(0);
		}
		return instance;
	}
	
	public void scaleBufferedImage(int width, int height) {
		
	}
	
	/**
	 * This method flips the buffered image onto the screen
	 * based on its current displacement.
	 */
	public void paint(Graphics g) {
		g.drawImage(offScreen, Window.use().getMousePanX(), Window.use().getMousePanY(), this);
	}

	public void paintComponent(Graphics g) {
		//Draw coast line, lakes, islands, and borders.
		drawShore(coast, UIManager.getColor("Panel.background"), g);
		drawShore(lake, Window.use().getBackground(), g);
		drawShore(island, UIManager.getColor("Panel.background"), g);
		drawBorder(border, Color.RED, g);
		
		//Draw roads
		for(RoadSegment r : segments) {
			if(r == null) continue;
			r.paintComponent(g);
		}
		//Draw the path
		for(DrawableItem r : path) r.paintComponent(g);
		for(Flag f: flags) f.paintComponent(g);
	}

	private void drawShore(ArrayList<CoastPoint[]> arg, Color c, Graphics g) {
		g.setColor(c);
		ArrayList<Polygon> poly = new ArrayList<Polygon>();
		Polygon current = new Polygon();
		for(CoastPoint[] cp : arg) {
			current = new Polygon();
			for(int i = 0; i < cp.length; i++) {
				current.addPoint(calcPixelX(cp[i].getX()-DataReader.getMinX()), calcPixelY(cp[i].getY()-DataReader.getMinY()));
			}
			poly.add(current);
		}
		for(Polygon fp : poly) {
			g.fillPolygon(fp);
		}
		g.setColor(Color.GRAY);
		for(Polygon op : poly) {
			g.drawPolygon(op);
		}
	}
	
	private void drawBorder(ArrayList<CoastPoint[]> arrBorder, Color c, Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
		// enable anti-aliasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		// set road color
		g2.setColor(c);
		// setting stroke type
		g2.setStroke(new BasicStroke(borderWidth(RoadSegment.zoomLevel), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		for (int i = 0; i < arrBorder.size(); i++) {
			for (int j = 1; j < arrBorder.get(i).length; j++) {
				g2.drawLine(
						calcPixelX(arrBorder.get(i)[j-1].getX()-DataReader.getMinX()),
						calcPixelY(arrBorder.get(i)[j-1].getY()-DataReader.getMinY()),
						calcPixelX(arrBorder.get(i)[j].getX()-DataReader.getMinX()),
						calcPixelY(arrBorder.get(i)[j].getY()-DataReader.getMinY()));
				
			}
		}
	}
	
	private float borderWidth(int zoomLevel) {
		switch(zoomLevel) {
			case 1 : return 2.5f;
			case 2 : return 3.0f;
			case 3 : return 3.2f;
			case 4 : return 3.6f;
			case 5 : return 4.4f;
			default: return 2.5f;
		}
	}


	
	/**
	 * Creates an blank off-screen image, which Graphics object is then used to draw an
	 * image to be flipped in later.
	 */
	public void createBufferImage() {
		offScreen = createImage(getWidth(), getHeight());		// Creates a new empty Image object and saves it to the buffer.
		offgc = offScreen.getGraphics();						// The Graphics object of this Image is extracted,
		paintComponent(offgc);									// and the paintComponent() method is called using this Graphics object,
	}															// thus 'flipping' the new map into view.
    
	public int calcPixelX(double geoCord){
		double diffX = (DrawableItem.geoMaxX - DrawableItem.geoMinX);
		int width = Window.use().getMapWidth();		
		int x =(int)(((geoCord-DrawableItem.geoMinX)/diffX)*width);
		return x;
	}

	public int calcPixelY(double geoCord){
		double diffY = (DrawableItem.geoMaxY - DrawableItem.geoMinY);
		int height = Window.use().getMapHeight();		
		int y =(int)(height-(((geoCord-DrawableItem.geoMinY)/diffY)*height));
		return y;
	}
    
    /**
     * Getter method for the segments field.
     * 
     * @return Returns the current value of the segment field. (ArrayList\<RoadSegment\>)
     */
    public ArrayList<RoadSegment> getRoadSegments() {
    	return segments;
    }
    
    /**
     * Changes the <br>segments<br> ArrayList to a new empty one. 
     */
    public void newArrayList() {
    	segments = new ArrayList<RoadSegment>();
    }
    
    /**
     * Adds a single roadSegment to the map.
     * 
     * @param roadSegment The roadSegment which are to be added to the map.
     */
    public void addRoadSegment(RoadSegment roadSegment) {
    	segments.add(roadSegment);
    }
    
    /**
     * Recalculate the position of each roadSegment
     * within the map.
     */
    public void updatePix(){
    	for(RoadSegment r: segments){
    		if(r == null) continue;
    		r.updatePosition();
    	}
    	for(DrawableItem r : path) r.updatePosition();
    	for (Flag f : flags) f.updatePosition();
    }
    
    /**
     * Recalculate position of path
     */
    public void updatePath() {
    	for (DrawableItem r : path) r.updatePosition();
    	for (Flag f : flags) f.updatePosition();
    }
    
    public void setPath(ArrayList<DrawableItem> path) {
    	this.path = path;
    }
    
    public void addDrawableItemToPath(DrawableItem i){
    	path.add(i);
    }
    
    public void resetPath() {
    	path = new ArrayList<DrawableItem>();
    	flags = new ArrayList<Flag>();
    }
    
    public void addFlag(Flag f) {
    	for (Flag flag : flags) if (flag == f) return;
    	flags.add(f);
    }
    
    public static void setCoast(ArrayList<CoastPoint[]> argCoast, ArrayList<CoastPoint[]> argLake, ArrayList<CoastPoint[]> argIsland) {
    	coast = argCoast;
    	lake = argLake;
    	island = argIsland;
    }
    
    public static void setBorder(ArrayList<CoastPoint[]> argBorder) {
    	border = argBorder;  	
    }   
    
}
