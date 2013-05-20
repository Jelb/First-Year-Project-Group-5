package Part1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.swing.JOptionPane;

/**
 *
 */
public class StreetNameReader {

	private InputStreamReader street, zip;
	private BufferedReader brStreet, brZip;

	/**
	 * Constructor of the class. 
	 * The constructor initializes the BufferedReaders used to perform 
	 * the methods within the class.
	 *  
	 * @throws FileNotFoundException If the files aren't found.
	 */
	public StreetNameReader() throws FileNotFoundException{
		try {
			street = new InputStreamReader(new FileInputStream("names.dat"), "UTF-8");
			zip = new InputStreamReader(new FileInputStream("post.dat"), "UTF-8");
			brStreet = new BufferedReader(street);
			brZip = new BufferedReader(zip);
		} catch (UnsupportedEncodingException e) {
			// Risks that the special characters aren't read properly 
			brStreet = new BufferedReader(new FileReader("names.dat"));
			brZip = new BufferedReader(new FileReader("post.dat"));
			JOptionPane.showMessageDialog(null, "An encoding error ocured and it might affect the system." +
											"\nIf yout experience any problem we sugest that you restart the Program.",
											"WARNING", JOptionPane.WARNING_MESSAGE);
		}
	}

	public String streetScan(String input) throws IOException {
		String strLine;
		String street = ""; 
		while((strLine = brStreet.readLine()) != null) {
			String regex = ".*?\\b" + strLine.toLowerCase().replaceAll("é", "e") + "\\b.*?";
			if(input.replaceAll("é", "e").matches(regex) && strLine.length() > street.length())
				street = strLine;
		}
		return street;
	}

	/**
	 * 
	 * @param input The whole search string
	 * @return
	 * @throws IOException
	 */
	public String zipScan(String input) throws IOException {
		String strLine;
		String zipAndName = "";
		while((strLine = brZip.readLine()) != null) {
			String zipCode = strLine.split(" ", 2)[0];

			if(input.contains(zipCode) && zipCode.length() > 3)
				zipAndName = strLine;
		}
		return zipAndName;
	}	

	public HashMap<String, String> zipToCityMap() throws IOException{
		HashMap<String, String> zipToCityMap = new HashMap<String, String>();
		String strLine;
		while((strLine = brZip.readLine()) != null) {
			String zipCode = strLine.split(" ", 2)[0];
			String cityName = strLine.split(" ", 2)[1];

			zipToCityMap.put(zipCode, cityName);
		}
		return zipToCityMap;
	}

	public String cityNameScan(String input) throws IOException {
		String strLine;
		String inputName = "";
		while((strLine = brZip.readLine()) != null) {
			String name = strLine.split(" ", 2)[1];
			if(input.equals(name.toLowerCase()))
				inputName = name;
		}
		return inputName;
	}
}
