package Part1;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Part1.Window.TextType;

public class Flag extends DrawableItem {
	private double geoXCord;
	private double geoYCord;
	private int pixelXCord;
	private int pixelYCord;
	private BufferedImage icon;
	
	public Flag(TextType Type){
		createFlag(Type);
	}
	
	public void setPosition(double x, double y) {
		geoXCord = x;
		geoYCord = y;
		updatePosition();
	}
	
	private void createFlag(TextType t){
		try {
			switch(t) {
			case FROM : 
			case FIND : 
				icon = ImageIO.read(new File("green_pin.PNG"));
				break;
			case TO : 
				icon = ImageIO.read(new File("red_pin.PNG"));
				break;
			}
		} 
		
		catch (IOException e) {
		}
	}
	
	public void updatePosition(){
		pixelXCord = calcPixelX(geoXCord) - 17;
		pixelYCord = calcPixelY(geoYCord) - 35;
	}

	public void paintComponent(Graphics g) {
		g.drawImage(icon, pixelXCord, pixelYCord, null);
	}
	
	public double Y() {
		return geoYCord;
	}
}
