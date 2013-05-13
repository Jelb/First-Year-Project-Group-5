package Part1;

public class PanHandler {

	/**
	 * A general pan method that takes as parameter how much the viewport has
	 * moved on the x-axis and y-axis
	 */
	// TODO: Optimize pan to take advantage of the points we already have loaded
	public static void pan(double deltaX, double deltaY) {
		// Hvis der er mindre end deltaY ud til kanten NORD
		double offsetX = WindowHandler.getOffsetX();
		double offsetY = WindowHandler.getOffsetY();
		double geoHeight = WindowHandler.getGeoHeight();
		double geoWidth = WindowHandler.getGeoWidth();
		double maxMapHeight = WindowHandler.getMaxMapHeight();
		double maxMapWidth = WindowHandler.getMaxMapWidth();

		if (maxMapHeight - (offsetY + geoHeight) < deltaY) {
			deltaY = maxMapHeight - (offsetY + geoHeight);
		}
		// Hvis der er mindre end deltaX ud til kanten ØST
		if (maxMapWidth - (offsetX + geoWidth) < deltaX) {
			deltaX = maxMapWidth - (offsetX + geoWidth);
		}
		// Hvis der er mindre end deltaY ud til kanten SYD
		if (-deltaY > offsetY) {
			deltaY = -offsetY;
		}
		// Hvis der er mindre end deltaX ud til kanten VEST
		if (-deltaX > offsetX) {
			deltaX = -offsetX;
		}
		WindowHandler.search(deltaX, WindowHandler.getGeoWidth() + deltaX,
				deltaY, WindowHandler.getGeoHeight() + deltaY);
	}

	public static void pixelPan(int deltaX, int deltaY) {
		double x = WindowHandler.pixelToGeoX(deltaX);
		double y = WindowHandler.pixelToGeoY(deltaY);
		pan(x, y);
	}

	/*
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
