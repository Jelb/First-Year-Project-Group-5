package Part1;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Part1.Window.TextType;

/**
 * Class containing the green and red 'flag pins' denoting the start and finish
 * positions of the search path in the GUI.
 */
public class Flag extends DrawableItem {
	private double geoXCord;
	private double geoYCord;
	private int pixelXCord;
	private int pixelYCord;
	private BufferedImage icon;

	public Flag(TextType Type){
		createFlag(Type);
	}

	/**
	 * Constructor setting the geo (UTM) position of the flag pin,
	 * as well as updating the position in terms of on-screen pixel coordinate.
	 * @param x		X-axis UTM value
	 * @param y		Y-axis UTM value
	 */
	public void setPosition(double x, double y) {
		geoXCord = x;
		geoYCord = y;
		updatePosition();
	}

	/**
	 * Loads the flag pin image file.
	 * @param t		The type (color) of the flag pin
	 */
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

	/**
	 * Calculates the pixel position of the flag pin.
	 */
	public void updatePosition(){
		pixelXCord = calcPixelX(geoXCord) - 17;
		pixelYCord = calcPixelY(geoYCord) - 35;
	}

	/**
	 * Paints the flag.
	 */
	public void paintComponent(Graphics g) {
		g.drawImage(icon, pixelXCord, pixelYCord, null);
	}	
}
