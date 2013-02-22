package addressParser_2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
/**
 * @author Jonas E. Jørgensen
 * 
 * regexadress is used to spilt a given adress input into each of its components.<br>
 * 1. [0] Street name <br>
 * 2. [1] Building no.<br>
 * 3. [2] Building letter<br>
 * 4. [3] Floor<br>
 * 5. [4] Door no.<br>
 * 6. [5] Zipcode<br>
 * 7. [6] City<br>
 * 
 * If any of the components are omitted the class will try to determind which of the components 
 * is omitted and spilt the rest of the input.
 */

public class AddressPaser2 {
	
	private FileScanner fs;
	private final String[] result = new String[6];
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
    
	public AddressPaser2() {
		for(int i = 0; i < result.length; i++){
			result[i] = "";
		}
		try {
			fs = new FileScanner();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String[] parseAddress(String input) {
		
		INPUT = input.toLowerCase().trim(); 
		
//		System.out.println("plain input: " + INPUT);
		
		INPUT = INPUT.replaceAll("[.,]", "");
		
//		System.out.println("without period and comma: " + INPUT);
		
		randomCheck();
		findStreetName();
		
//		System.out.println("without street name: " + INPUT);
		
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
//        System.out.println("without zip and/or city name: " + INPUT);

		buildingNo();
		
//		System.out.println("without building number: " + INPUT);
		
		findBuildingLetter();
		
//		System.out.println("without building letter: " + INPUT);
		
		findFloorNumber();
		
//		System.out.println("without floor number: " + INPUT);
		
		print();
		return result;
	}
	
	private void findStreetName() {
		String streetName = "";
		try {
			streetName = fs.streetScan(INPUT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result[0] = streetName.trim();
		INPUT = INPUT.replaceFirst(streetName.toLowerCase(), "").trim();
	}
	
	// Only finds zip-codes with 4-digits
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
	
	// At this point INPUT only includes the building number, building letter and floor
	private void buildingNo() {
		p1 = Pattern.compile(NR);
		m1 = p1.matcher(INPUT);
		
		if (m1.find()) {
			String number = m1.group();
			result[1] = number;
			INPUT = INPUT.replaceFirst(number, "").trim();
		}
		
    }
	
	private void findBuildingLetter() {
		p1 = Pattern.compile("[a-z]^[a-z]");
		m1 = p1.matcher(INPUT);
		
		if (m1.find()) {
			String letter = m1.group().toUpperCase();
			result[2] = letter;
			INPUT = INPUT.replaceFirst(letter.toLowerCase(), "").trim();
		}
	}
	
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
        p1 = Pattern.compile("[^\\w\\d\\s.,æÆøØåÅ]");
        m1 = p1.matcher(INPUT.trim());
        if(m1.find()) {                                                         // CHECK 01
            throw new RuntimeException();
        }
    }
    
    public static void main(String[] arg) {
    	AddressPaser2 ap = new AddressPaser2();
    	ap.parseAddress("Rued Langgaards Vej 77A 1. 4735 Mern");
    	ap.parseAddress("Hesseløgade 56 i København Ø");
    	
    }
    
    private void print() {
//        System.out.println("----------------------------------------");
//        System.out.println("Search string: ");
//        System.out.println();
//        System.out.println("Street name:     " + result[0].trim());
//        System.out.println("Building no:     " + result[1].trim());
//        System.out.println("Building letter: " + result[2].trim());
//        System.out.println("Floor:           " + result[3].trim());
////        System.out.println("Door:            " + result[-].trim());
//        System.out.println("Zip code:        " + result[4].trim());
//        System.out.println("City:            " + result[5].trim());
//        System.out.println("----------------------------------------");
    }
    
    public String Test() {
    	String strBuilder = "";
    	for (String s : result) {
    		strBuilder += s + "#";
    	}
    	System.out.println("Test: " + strBuilder.substring(0, strBuilder.length()-1));
    	return strBuilder.substring(0, strBuilder.length()-1);
    }
}
