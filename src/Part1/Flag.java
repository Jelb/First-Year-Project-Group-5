package Part1;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Flag extends DrawableItem {
	private boolean fromBool;
	private int type; //can be fromFlag (1), toFlag (2) or findFlag (3)
	private double geoXCord;
	private double geoYCord;
	private int pixelXCord;
	private int pixelYCord;
	private BufferedImage icon;
	
	public Flag(int type){
		this.type = type;
		createFlag();
	}
	
	public void setPosition(double x, double y) {
		geoXCord = x;
		geoYCord = y;
		updatePosition();
	}
	
	private void createFlag(){
		try {
			if(type == 1){ //If we want a from flag (green)
				icon = ImageIO.read(new File("green_flag2.PNG"));
			}
			else if(type == 2){ //To flag (checkered)
				icon = ImageIO.read(new File("checkered_flag2.PNG"));
			}
			else if(type == 3) { //A find flag for single address find (blue)
				icon = ImageIO.read(new File("blue_pin.PNG"));
			}
		} 
		
		catch (IOException e) {
		}
	}
	
	public void updatePosition(){
		pixelXCord = calcPixelX(geoXCord);
		pixelYCord = calcPixelY(geoYCord+ WindowHandler.pixelToGeoY(32));
	}

	public void paintComponent(Graphics g) {
		g.drawImage(icon, pixelXCord , pixelYCord, null);
	}
}
