package addressParser_2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileScanner   {

private static FileInputStream street, zip;
private static DataInputStream inStreet, inZip;
private static BufferedReader brStreet, brZip;
	
	public FileScanner() throws FileNotFoundException{
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
