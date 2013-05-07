package Part1;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import javax.swing.JPanel;


public class Map extends JPanel {
	/**
	 * Map is a JPanel with the lines drawn
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<RoadSegment> segments;
	private ArrayList<DrawableItem> path;
	private static Map instance = null;
	private static ArrayList<Polygon> poly;

	
	private Image offScreen = null;
	private Graphics offgc;
	
	private Map() {
	}
	
	//Singleton check
	public static Map use() {
		if(instance == null) {
			instance = new Map();
			instance.path = new ArrayList<DrawableItem>();
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
		g.drawImage(dbImage, Window.use().getMousePanX(), Window.use().getMousePanY(), this);
		RoadSegment.setZoomLevel();
		g.drawImage(offScreen, Window.use().getMousePanX(), Window.use().getMousePanY(), this);
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
	
    public void paintComponent(Graphics g) {
    	poly = new ArrayList<Polygon>();
    	Polygon current = new Polygon();
        g.setColor(Color.green);
    	for(CoastPoint[] c: WindowHandler.getCoast()) {
    		current = new Polygon();
    		for(int i = 0; i < c.length; i++) {
    			current.addPoint(calcPixelX(c[i].getX()-DataReader.getMinX()), calcPixelY(c[i].getY()-DataReader.getMinY()));
    		}
    		poly.add(current);
    	}
    	int po = 0;
    	for(Polygon p : poly) {
    		g.fillPolygon(p);
    		g.drawPolygon(p);
    		po++;
    	}
    	System.out.println("number og polygons: " + po);
        for(RoadSegment r : segments) {
        	if(r == null) continue;
            r.paintComponent(g);
        }
        for(DrawableItem r : path) r.paintComponent(g);
    }
    
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
    }
    
    /**
     * Recalculate position of path
     */
    public void updatePath() {
    	for (DrawableItem r : path) r.updatePosition();
    }
    
    public void setPath(ArrayList<DrawableItem> path) {
    	for(DrawableItem r : path){
    		this.path.add(r);
    	}
    }
    
    public void addDrawableItemToPath(DrawableItem i){
    	path.add(i);
    }
    
    public void resetPath() {
    	path = new ArrayList<DrawableItem>();
    }
}
