package Part1;

public class PanHandler {

	/**
	 * pixelPan shifts the pixels of the nodes and redraws the map. If parts of
	 * the map that are not loaded is reached a new search is done in the quad
	 * tree.
	 */
	// public static void pixelPan(int x, int y) {
	// //Checks if the shiftPixel will move the view area outside the map
	// //by comparing the difference between the view area and the maxMax sizes
	// and the distance we want to move
	// boolean isInside = true;
	// double deltaX = pixelToGeoX(x);
	// double deltaY = pixelToGeoY(y);
	// //Hvis der er mindre end deltaY ud til kanten NORD
	// if(maxMapHeight - (offsetY + geoHeight) < deltaY) {
	// deltaY = maxMapHeight - (offsetY + geoHeight);
	// pan(deltaX, deltaY);
	// isInside = false;
	// }
	// //Hvis der er mindre end deltaX ud til kanten ØST
	// if(maxMapWidth - (offsetX + geoWidth) < deltaX) {
	// deltaX = maxMapWidth - (offsetX + geoWidth);
	// pan(deltaX, deltaY);
	// isInside = false;
	// }
	// //Hvis der er mindre end deltaY ud til kanten SYD
	// if(-deltaY > offsetY){
	// deltaY = -offsetY;
	// pan(deltaX, deltaY);
	// isInside = false;
	// }
	// //Hvis der er mindre end deltaX ud til kanten VEST
	// if(-deltaX > offsetX) {
	// deltaX = -offsetX;
	// pan(deltaX, deltaY);
	// isInside = false;
	// }
	// //If the new view area is inside the map
	// if(isInside == true){
	// RoadSegment.shiftPixel(0-x, y);
	// Window.use().updateMap();
	//
	// offsetY += deltaY;
	// offsetX += deltaX;
	// RoadSegment.shiftMapSize(deltaX, deltaY);
	//
	// // Er vi nået til kanten af det vi kender?
	// System.out.println("tidligere offset y: " + prevOffsetY);
	// System.out.println("Nuværende offset y: " + offsetY);
	// if (Math.abs(prevOffsetX-offsetX) > longestRoadsFloor ||
	// Math.abs(prevOffsetY-offsetY) > longestRoadsFloor) {
	// System.out.println("Nu skal der hentes punkter i quadtræet");
	// RoadSegment.shiftPixel(0, 0);
	// offsetX -= deltaX;
	// offsetY -= deltaY;
	// pan(deltaX, deltaY);
	// Window.use().updateMap();
	// }
	// }
	// }

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
//			Window.use().setMousePanY((int) (Window.use().getMousePanY() + Window.use().getHeight() * 0.1));
//			Window.use().repaint();
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
