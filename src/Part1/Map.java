package Part1;

import java.awt.Graphics;
import java.awt.Image;
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
	
	private Image dbImage;
	private Graphics dbg;
	
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
	
	public void paint(Graphics g) {
		RoadSegment.setZoomLevel();
		g.drawImage(dbImage, Window.use().getMousePanX(), Window.use().getMousePanY(), this);
	}
	
	public void flipImageBuffer() {
		dbImage = createImage(getWidth(), getHeight());
		dbg = dbImage.getGraphics();
		paintComponent(dbg);
	}
	
    public void paintComponent(Graphics g) {
        for(RoadSegment r : segments) {
        	if(r == null) continue;
            r.paintComponent(g);
        }
        for(DrawableItem r : path) r.paintComponent(g);
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
    	this.path = path;
    }
    
    public void addDrawableItemToPath(DrawableItem i){
    	path.add(i);
    }
}
