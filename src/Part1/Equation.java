package Part1;

public class Equation {
	
	//--------------------Fields for convertLonLatToUTM--------------------
	//Scale along meridian
	private static final double SCALE = 0.9996;
	//Equatorial Radius
	private static final double A = 6378137.137; 
	//Polar Radius
	private static final double B = 6356752.3;
	private static final double E = Math.sqrt(1-(B*B)/(A*A));
	private static final double EE = Math.pow(E, 2)/(1-Math.pow(E, 2));
	private static final double N = ((A-B)/(A+B));
	private static final double MERIDIAN32 = 9*(Math.PI/180);
	private static final double MERIDIAN33 = 15*(Math.PI/180); 
	private static double p;
	//---------------------------------------------------------------------
	
	/**
	 * Method used to convert longitude and latitude to UTM coordinates.
	 * The calculation is based on UTM zone 32.
	 * The equation is found at http://www.uwgb.edu/dutchs/usefuldata/utmformulas.htm.
	 * 
	 * @param argLat The latitude coordinate formated as degrees DDD.dddddd° 
	 * @param argLon The latitude coordinate formated as degrees DDD.dddddd° 
	 * @return A CoastPoint object containing the calculated UTM coordinates.
	 */
	public static CoastPoint convertLonLatToUTM(double argLat, double argLon) {
		double lon 	= argLon*(Math.PI/180); 
		double lat 	= argLat*(Math.PI/180); 

		double nu 	= A/Math.pow((1-Math.pow(E, 2)*Math.pow(Math.sin(lat), 2)), 1/2);

		if(lon < 12.0) {
			p = (lon - MERIDIAN32);
		} else {
			p = (lon - MERIDIAN33);
		}
		
		double AP 	= A*(1-N+(5*N*N/4)*(1-N)+(81*Math.pow(N, 4)/64)*(1-N));
		double BP 	= (3*A*N/2)*(1-N-(7*N*N/8)*(1-N)+(55*Math.pow(N, 4)/64));
		double CP 	= (A*15*N*N/16)*(1-N)+(3*N*N/4)*(1-N);
		double DP 	= (35*A*Math.pow(N, 3)/48)*(1-N)+(11*N*N/16);
		double EP 	= (315*A*Math.pow(N, 4)/51)*(1-N);
		double S	= (AP*lat)-(BP*Math.sin(2*lat))+(CP*Math.sin(4*lat))-(DP*Math.sin(6*lat))+(EP*Math.sin(8*lat));
		
		double K1	= S*SCALE;
		double K2	= SCALE*nu*Math.sin(2*lat)/4;
		double K3	= (nu*Math.sin(lat)*Math.pow(Math.cos(lat), 3)/24)*((5*Math.pow(Math.tan(lat), 2))+9*EE*Math.pow(Math.cos(lat), 2)+4*EE*EE*Math.pow(Math.cos(lat),4))*SCALE;
		
		double K4 	= SCALE * nu * Math.cos(lat);
		double K5 	= Math.pow(Math.cos(lat), 3)*(nu/6)*((1-Math.pow(Math.tan(lat), 2)+EE*Math.pow(Math.cos(lat), 2)))*SCALE;
		
		double x 	= 500000+(K4*p+K5*Math.pow(p, 3));
		double y	= K1+K2*p*p+K3*Math.pow(p, 4);
		double dx = (4.051*Math.pow(10, -9)*Math.pow(x, 2))-(0.0028*x)+437;
		if(dx > 0.0) x += dx;
		return new CoastPoint(x, y);
	}
	
	/**
	 * Method used to calculate the on-screen pixel-coordinate of a UTM 
	 * x-coordinate.
	 * 
	 * @param geoCord The value of the coordinate that should be calculated.
	 * @return The pixel value corresponding to the UTM coordinate on screen.
	 */
	public static int calcPixelX(double geoCord){
		double diffX = (DrawableItem.getGeoMaxX() - DrawableItem.getGeoMinX());
		int width = Window.use().getMapWidth();		
		int x =(int)(((geoCord-DrawableItem.getGeoMinX())/diffX)*width);
		return x;
	}
	

	
	/**
	 * Method used to calculate the on-screen pixel-coordinate of a UTM 
	 * y-coordinate.
	 * 
	 * @param geoCord The value of the coordinate that should be calculated.
	 * @return The pixel value corresponding to the UTM coordinate on screen.
	 */
	public static int calcPixelY(double geoCord){
		double diffY = (DrawableItem.getGeoMaxY() - DrawableItem.getGeoMinY());
		int height = Window.use().getMapHeight();		
		int y =(int)(height-(((geoCord-DrawableItem.getGeoMinY())/diffY)*height));
		return y;
	}
	
	/**
	 * Returns an on-screen geo-distance measured in pixels.
	 * @param dist		The coordinate based geo distance
	 * @return			The distance in number if pixels
	 */
	public static double onscreenPixelDistance(double dist) {
		double diffX = (DrawableItem.getGeoMaxX() - DrawableItem.getGeoMinX());
		int width = Window.use().getMapWidth();
		double pixelSize = diffX / width;
		double pixelDistance = dist / pixelSize;
		return pixelDistance;
	}
	
	//-----------------------VECTOR FUNCTIONS------------------------------------
	
	/**
	 * Determines if a point lies within the 'channel' of a given edge, meaning the points
	 * normal vector actually touches the edge itself.
	 * @param point		The point, given as a node
	 * @param edge		The edge we are testing
	 * @return			True, if the point is inside the channel
	 */
	public static boolean pointWithinChannel(double x, double y, Edge edge) {
		double[] vectorA = pointsToVector(edge.getFromNode().getAbsoluteXCoordinate(),
										  edge.getFromNode().getAbsoluteYCoordinate(),
										  x, y);
		double[] vectorB = pointsToVector(edge.getFromNode().getAbsoluteXCoordinate(),
				  						  edge.getFromNode().getAbsoluteYCoordinate(),
				  						  edge.getToNode().getAbsoluteXCoordinate(),
				  						  edge.getToNode().getAbsoluteYCoordinate());
		double[] vectorC = pointsToVector(edge.getToNode().getAbsoluteXCoordinate(),
										  edge.getToNode().getAbsoluteYCoordinate(),
										  x, y);
		double[] vectorD = pointsToVector(edge.getToNode().getAbsoluteXCoordinate(),
				  						  edge.getToNode().getAbsoluteYCoordinate(),
				  						  edge.getFromNode().getAbsoluteXCoordinate(),
				  						  edge.getFromNode().getAbsoluteYCoordinate());
		
		if (cosVectorAngle(vectorA, vectorB) > 0 && cosVectorAngle(vectorC, vectorD) > 0) {
			return true;
		}
		else {
			return false;
		}
		
	}
	
	/**
	 * Calculates the normal vector of an edge. 
	 * @param edge	The given edge
	 * @return		The vector expressed as a double[2]
	 */
	public static double[] getNormalVector(Edge edge) {
		double[] vector = edgeToVector(edge);
		double x = vector[0];
		double y = vector[1];
		double[] normal = new double[] {-y , x} ;
		return normal;
	}
	
	/**
	 * calculates the distance between a point and a line.
	 * @param edge	The edge from which the aforementioned line is defered
	 * @param x		X value of the point
	 * @param y		Y value of the point
	 * @return		The distance between line and point
	 */
	public static double distanceBetweenPointAndLine(Edge edge, double x, double y) {
		// a coordinate set on the line
		double x1 = edge.getFromNode().getAbsoluteXCoordinate();
		double y1 = edge.getFromNode().getAbsoluteYCoordinate();
		
		// the coordinate set of the point
		double x2 = x;
		double y2 = y;
		
		// the values of the lines normal vector
		double a = getNormalVector(edge)[0];
		double b = getNormalVector(edge)[1];
		double c = (-a * x1) - (b * y1);
		
		// calculating the distance between the point and the line
		double dist = (Math.abs(a * x2 + b * y2 + c)) / (Math.sqrt(a * a + b * b));
		return dist;
	}
		
	/**
	 * Creates a 2D vector from two points
	 * @param x1		X coord, first point
	 * @param y1		Y coord, first point
	 * @param x2		X coord, second point
	 * @param y2		Y coord, second point
	 * @return			The resulting vector between the two
	 */
	public static double[] pointsToVector(double x1, double y1, double x2, double y2) {
		double[] vector = new double[2];
		vector[0] = x2 - x1;
		vector[1] = y2 - y1;
		return vector;
	}
	
	/**
	 * Creates a 2D vector based on a set of nodes.
	 * @param fnode		The 'from' node
	 * @param tnode		The 'to' node
	 * @return			The vector between the two
	 */
	public static double[] nodesToVector(Node fnode, Node tnode) {
		double[] vector = new double[2];
		vector[0] = tnode.getAbsoluteXCoordinate() - fnode.getAbsoluteXCoordinate();
		vector[1] = tnode.getAbsoluteYCoordinate() - fnode.getAbsoluteYCoordinate();
		return vector;
	}
	
	/**
	 * Calculates the length of a 2D vector.
	 * @param vector	A double array of 2 values, describing the vector
	 * @return			The length of the vector
	 */
	public static double vectorLength(double[] vector) {
		return Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2));
	}
	
	/**
	 * Calculates the distance between two nodes.
	 * @param fnode		The 'from' node
	 * @param tnode		The 'to' node
	 * @return			The distance between the two
	 */
	public static double distanceBetweenNodes(Node fnode, Node tnode) {
		return vectorLength(nodesToVector(fnode, tnode));
	}
	
	/**
	 * Calculates the distance between two points.
	 * @param x1		X coord, first point
	 * @param y1		Y coord, first point
	 * @param x2		X coord, second point
	 * @param y2		Y coord, second point
	 * @return			The distance between the two points
	 */
	public static double distanceBetweenPoints(double x1, double y1, double x2, double y2) {
		return vectorLength(pointsToVector(x1, y1, x2, y2));
	}
	
	/** 
	 * Calculates the scalar product of the 2D vectors.
	 * @param vectorA	A double array of 2 values, describing the first vector
	 * @param vectorB	A double array of 2 values, describing the second vector
	 * @return			The scalar product
	 */
	public static double scalarProduct(double[] vectorA, double[] vectorB) {
		return (vectorA[0] * vectorB[0]) + (vectorA[1] * vectorB[1]);
	}
	
	/**
	 * Calculates the cosine value of the angle between 2 vectors.
	 * @param vectorA	A double array of 2 values, describing the first vector
	 * @param vectorB	A double array of 2 values, describing the second vector
	 * @return			The cosine angle
	 */
	public static double cosVectorAngle(double[] vectorA, double[] vectorB) {
		return (scalarProduct(vectorA, vectorB) / (vectorLength(vectorA) * vectorLength(vectorB)));
	}
	
	/**
	 * Creates a vector from a given edge.
	 * @param edge		The given edge
	 * @return			The resulting vector 
	 */
	public static double[] edgeToVector(Edge edge) {
		return pointsToVector(edge.getFromNode().getAbsoluteXCoordinate(),
							  edge.getFromNode().getAbsoluteYCoordinate(),
							  edge.getToNode().getAbsoluteXCoordinate(),
							  edge.getToNode().getAbsoluteYCoordinate()
							 );
	}
	
	//------------------------------------------------------------------------------
}
	