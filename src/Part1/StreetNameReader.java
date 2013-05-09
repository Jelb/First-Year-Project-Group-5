package Part1;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class StreetNameReader   {

private FileInputStream street, zip;
private DataInputStream inStreet, inZip;
private BufferedReader brStreet, brZip;
	
	public StreetNameReader() throws FileNotFoundException{
		//street = new FileInputStream("src/names.txt");
		street = new FileInputStream("names.dat");
		//zip = new FileInputStream("src/post.txt");
		zip = new FileInputStream("post.dat");
		inStreet = new DataInputStream(street);
		inZip = new DataInputStream(zip);
		brStreet = new BufferedReader(new InputStreamReader(inStreet));
		brZip = new BufferedReader(new InputStreamReader(inZip));

	}
	
	public String streetScan(String input) throws IOException {
		String strLine;
		String street = ""; 
		while((strLine = brStreet.readLine()) != null) {
			String regex = ".*?\\b" + strLine.toLowerCase() + "\\b.*?";
			if(input.matches(regex) && strLine.length() > street.length())
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
