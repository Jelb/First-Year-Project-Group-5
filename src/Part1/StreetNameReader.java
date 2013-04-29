package Part1;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class StreetNameReader   {

private FileInputStream street, zip;
private DataInputStream inStreet, inZip;
private BufferedReader brStreet, brZip;
private static HashMap<String, String> zipToCityMap;
	
	public StreetNameReader() throws FileNotFoundException{
		street = new FileInputStream("src/names.txt");
		zip = new FileInputStream("src/post.txt");
		inStreet = new DataInputStream(street);
		inZip = new DataInputStream(zip);
		brStreet = new BufferedReader(new InputStreamReader(inStreet));
		brZip = new BufferedReader(new InputStreamReader(inZip));

	}
	
	public String streetScan(String input) throws IOException {
		String strLine;
		String street = ""; 
		while((strLine = brStreet.readLine()) != null)
			if(input.contains(strLine.toLowerCase()) && strLine.length() > street.length())
				street = strLine;
		return street;
	}
	
	public static HashMap<String, String> getZipToCityMap(){
		return zipToCityMap;
	}
	
	/**
	 * 
	 * @param input The whole search string
	 * @return
	 * @throws IOException
	 */
	public String zipScan(String input) throws IOException {
		zipToCityMap = new HashMap<String, String>();
		String strLine;
		String zipAndName = "";
		while((strLine = brZip.readLine()) != null) {
			String zipCode = strLine.split(" ", 2)[0];
			String cityName = strLine.split(" ", 2)[1];

			zipToCityMap.put(zipCode, cityName);
			
			if(input.contains(zipCode) && zipCode.length() > 3)
				zipAndName = strLine;
		}
		return zipAndName;
	}	
	
	public HashMap<String, String> zipToCityMap() throws IOException{
		zipToCityMap = new HashMap<String, String>();
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
			if(input.contains(name.toLowerCase()))
				inputName = name;
		}
		return inputName;
	}
}
