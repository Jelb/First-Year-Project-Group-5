package pytheas;

/**
 * This class handles all pan inputs, checks if they comply with the 
 * outside bounds of the viewable map are, changes them if they don't
 * and eventually calls the search method in the WindowHandler class.
 * 
 */
public class PanHandler {

	/**
	 * A general pan method that takes as parameter how much the viewport has
	 * moved on the x-axis and y-axis
	 */
	public static void pan(double deltaX, double deltaY) {
		// If there is less than deltaY to the NORTH border
		double offsetX = WindowHandler.getOffsetX();
		double offsetY = WindowHandler.getOffsetY();
		double geoHeight = WindowHandler.getGeoHeight();
		double geoWidth = WindowHandler.getGeoWidth();
		double maxMapHeight = WindowHandler.getMaxMapHeight();
		double maxMapWidth = WindowHandler.getMaxMapWidth();

		if (maxMapHeight - (offsetY + geoHeight) < deltaY) {
			deltaY = maxMapHeight - (offsetY + geoHeight);
		}
		// if there is less than deltaX to the EAST border
		if (maxMapWidth - (offsetX + geoWidth) < deltaX) {
			deltaX = maxMapWidth - (offsetX + geoWidth);
		}
		// if there is less than deltaY to the SOUTH border
		if (-deltaY > offsetY) {
			deltaY = -offsetY;
		}
		// if there is less than deltaX to the WEST border
		if (-deltaX > offsetX) {
			deltaX = -offsetX;
		}
		WindowHandler.search(deltaX, WindowHandler.getGeoWidth() + deltaX,
				deltaY, WindowHandler.getGeoHeight() + deltaY);
	}

	/**
	 * Translates a pan in pixels to a pan in geo coordinates.
	 * 
	 * @param deltaX	Pixel X-axis offset
	 * @param deltaY	Pixel Y-axis offset
	 */
	public static void pixelPan(int deltaX, int deltaY) {
		double x = WindowHandler.pixelToGeoX(deltaX);
		double y = WindowHandler.pixelToGeoY(deltaY);
		pan(x, y);
	}

	/**
	 * Pans the map 10% in the direction specified.
	 */
	public static void directionPan(Direction d) {
		switch (d) {
			case NORTH: {
				pan(0, WindowHandler.getGeoHeight() * 0.1);
				break;
			}
			case SOUTH: {
				pan(0, -WindowHandler.getGeoHeight() * 0.1);
				break;
			}
			case WEST: {
				pan(-WindowHandler.getGeoWidth() * 0.1, 0);
				break;
			}
			case EAST: {
				pan(WindowHandler.getGeoWidth() * 0.1, 0);
				break;
			}
		}
	}
}
