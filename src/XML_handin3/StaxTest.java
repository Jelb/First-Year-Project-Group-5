package XML_handin3;

public class StaxTest {

	/**
	 * @param args
	 */
	  public static void main(String[] args) {
		    StaxWriter configFile = new StaxWriter();
		    configFile.setFile("config2.xml");
		    try {
		      configFile.saveConfig();
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
	  }
}
