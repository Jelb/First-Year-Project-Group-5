package Part1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * This class parses the users address input, preparing it for address lookup.
 * 
 * regexadress is used to split a given address input into each of its components.<br>
 * 1. [0] Street name <br>
 * 2. [1] Building no.<br>
 * 3. [2] Building letter<br>
 * 4. [3] Floor<br>
 * 6. [4] Zipcode<br>
 * 7. [5] City<br>
 * 
 * If any of the components are omitted the class will try to determind which of the components 
 * is omitted and split the rest of the input.
 */
public class AddressParser {
	
	private StreetNameReader fs;
	private String[] result = new String[6];
	private String INPUT;
    private Pattern p1;
    private Matcher m1;
    //pattern for zipcode
    private static final String POST = "[0-9]{4}";
    //pattern for house number
    private static final String NR = "[0-9]+";
    //pattern for city
    private static final String BY = "[\\wæÆøØåÅ]{3,}";
    //patterns for floor
    private static final String SAL = "sal";
    private static final String ETAGE = "etage";
    private static AddressParser instance=null;
    
    /**
     * Singleton design patter.
     * If no instance exists, create an return on. Otherwise,
     * return the existing instance.
     * @return	The one, existing instance of the class.
     */
	public static AddressParser use() {
		if(instance == null) {
			instance = new AddressParser();
		}
		return instance;
	}
	
	/**
	 * Constructor.
	 */
	private AddressParser() {
		for(int i = 0; i < result.length; i++){
			result[i] = "";
		}
		try {
			fs = new StreetNameReader();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Clears the result variable of input.
	 */
	public void clearResults(){
		result = new String[6];
		for (int i = 0; i < result.length; i++) result[i] = "";
	}
	
	/**
	 * Parse a single line of input, returning a String array
	 * containing the matching address variables.
	 * @param input		User text input
	 * @return			String array containing the parsed address
	 */
	public String[] parseAddress(String input) {
		clearResults();
		try {
			fs = new StreetNameReader();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		INPUT = input.toLowerCase().trim(); 
		
		INPUT = INPUT.replaceAll("[.,]", "");
				
		randomCheck();
		findStreetName();
		
		// Searches for zip-code if search string contains one
		// else searches for city-name
		p1 = Pattern.compile(POST);
		m1 = p1.matcher(INPUT);
		if (m1.find()) {
			findZip(); 
		}
		else {
			findCity();
		}

		buildingNo();
				
		findBuildingLetter();
				
		findFloorNumber();

		return result;
	}
	
	/**
	 * Searches the input String for a street name.
	 */
	private void findStreetName() {
		String streetName = "";
		try {
			streetName = fs.streetScan(INPUT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		result[0] = streetName.trim();
		INPUT = INPUT.replaceFirst(streetName.toLowerCase(), "").trim();
	}
	
	/**
	 * Searches the input String for a 4-digit postal code.
	 * Will ONLY find postal codes of 4 digits.
	 */
	private void findZip() {
		String[] zipNameArr = new String[] {"",""};
		String zipCodeAndCityName = "";
		try {
			zipCodeAndCityName = fs.zipScan(INPUT).trim();
			zipNameArr = zipCodeAndCityName.split(" ", 2);
			if(zipNameArr.length == 1) return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		result[4] = zipNameArr[0].trim();
		result[5] = zipNameArr[1].trim();
		INPUT = INPUT.replaceFirst(zipNameArr[0].trim().toLowerCase(), "").trim();
		INPUT = INPUT.replaceFirst(zipNameArr[1].trim().toLowerCase(), "").trim();
	}
	
	/**
	 * Searches the input String for a city name.
	 */
	private void findCity() {
		String cityName = "";
		try {
			cityName = fs.cityNameScan(INPUT).trim();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		result[4] = "";
		result[5] = cityName.trim();
		INPUT = INPUT.replaceFirst(cityName.toLowerCase(), "").trim();
		
	}
	
	/**
	 * Searches the input String for building number.
	 */
	private void buildingNo() {
		p1 = Pattern.compile(NR);
		m1 = p1.matcher(INPUT);
		
		if (m1.find()) {
			String number = m1.group();
			result[1] = number;
			INPUT = INPUT.replaceFirst(number, "").trim();
		}
		
    }
	
	/**
	 * Searches the input String for a building letter.
	 */
	private void findBuildingLetter() {
		p1 = Pattern.compile("\\b[a-z]\\b");
		m1 = p1.matcher(INPUT);
		
		if (m1.find()) {
			String letter = m1.group().toUpperCase();
			result[2] = letter;
			INPUT = INPUT.replaceFirst(letter.toLowerCase(), "").trim();
		}
	}
	
	/**
	 * Searches the input String for a floor number.
	 */
	private void findFloorNumber() {
		p1 = Pattern.compile("\\d+");
		m1 = p1.matcher(INPUT);
		
		if (m1.find()) {
			result[3] = m1.group();
			INPUT = INPUT.replaceFirst(m1.group(), "").trim();
		}
	}
    
    /**
     * Checks if the input contains any illegal characters.
     */
    private void randomCheck() {
        p1 = Pattern.compile("[^\\w\\d\\s.,æÆøØåÅéÉ]");
        m1 = p1.matcher(INPUT.trim());
        if(m1.find()) {                                                         // CHECK 01
            throw new RuntimeException();
        }
    }

    /**
     * Test method, used for debugging purposes.
     * @return	A String containing the parsed results, separated by a hash tag
     */
    public String Test() {
    	String strBuilder = "";
    	for (String s : result) {
    		strBuilder += s + "#";
    	}
    	return strBuilder.substring(0, strBuilder.length()-1);
    }
}
