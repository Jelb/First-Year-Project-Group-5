package Part1;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Flag extends DrawableItem {
	private boolean fromBool;
	private double geoXCord;
	private double geoYCord;
	private int pixelXCord;
	private int pixelYCord;
	private BufferedImage icon;
	
	public Flag(boolean fromBool){
		this.fromBool = fromBool;
		createFlag();
	}
	
	public void setPosition(double x, double y) {
		geoXCord = x;
		geoYCord = y;
		updatePosition();
	}
	
	private void createFlag(){
		try {
			if(fromBool){
				icon = ImageIO.read(new File("GreenFlag.PNG"));
			}
			else{
				icon = ImageIO.read(new File("RedFlag.PNG"));
			}
		} 
		
		catch (IOException e) {
			System.out.println("Failed to load flag image");
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
