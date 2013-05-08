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
	private static final double MERIDIAN = 9*(Math.PI/180);
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

		double p = (lon - MERIDIAN);
		
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
	
	//-----------------------VECTOR FUNCTIONS------------------------------------
	
	/**
	 * Creates a 2D vector based on a set of nodes.
	 * @param fnode		The 'from' node
	 * @param tnode		The 'to' node
	 * @return			The vector between the two
	 */
	public static double[] nodeToVector(Node fnode, Node tnode) {
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
		return vectorLength(nodeToVector(fnode, tnode));
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
	//---------------------------------------------------------------------------
}