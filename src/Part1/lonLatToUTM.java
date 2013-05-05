package Part1;

//Formular from: http://www.uwgb.edu/dutchs/usefuldata/utmformulas.htm
public class lonLatToUTM {
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
	
	public static CoastPoint convertToUTM(double argLon, double argLat) {

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
	
	public static void main(String[] args) {
		convertToUTM(55.643354, 12.647281);
	}
	
}