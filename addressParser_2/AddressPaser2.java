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
	private final String[] result = new String[7];
	private String INPUT;
    private Pattern p1;
    private Matcher m1;
    //pattern for zipcode
    private static final String POST = "[0-9]{4}";
    //pattern for house number
    private static final String NR = "[^0-9][0-9]+";
    //pattern for city
    private static final String BY = "[\\wæÆøØåÅ]{3,}";
    //patterns for floor
    private static final String SAL = "sal";
    private static final String ETAGE = "etage";
    
	public AddressPaser2() {
		for(int i = 0; i < result.length; i++){
			result[i] = "NOT FOUND";
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
		INPUT = INPUT.replaceAll("[.,]", "");
		randomCheck();
		findStreetName();
		findZip();
        System.out.println(INPUT);

		buildingNo();
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
	
	private void findZip() {
		String[] zipCode = new String[] {"",""};
		String temp = "";
		try {
			temp = fs.zipScan(INPUT).trim();
			//System.out.println(temp);
			zipCode = temp.split(" ");
			if(zipCode.length == 1) return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		result[5] = zipCode[0].trim();
		result[6] = zipCode[1].trim();
		INPUT = INPUT.replaceFirst(temp.toLowerCase(), "").trim();

	}
	
	private void buildingNo() {
		
    }
    
    /**
     * Checks if the input contains any illegal characters.
     */
    private void randomCheck() {
        p1 = Pattern.compile("[^\\w\\d\\s.,øØåÅæÆ]");
        m1 = p1.matcher(INPUT.trim());
        if(m1.find()) {                                                         // CHECK 01
            System.out.println("----------------------------------------");
            System.out.println(INPUT);
            System.out.println("MALFORMED ADDRESS");
            System.out.println("----------------------------------------");
        }
    }
    
    public static void main(String[] arg) {
    	AddressPaser2 ap = new AddressPaser2();
    	ap.parseAddress("Rued Langgaards Vej 7A 1. mf. 4735 Mern");
    	String temp= null;
    	System.out.println(temp);
    	
    }
    
    private void print() {
        System.out.println("----------------------------------------");
        System.out.println("Seach stirng: ");
        System.out.println();
        System.out.println("Street name:     " + result[0].trim());
        System.out.println("Building no:     " + result[1].trim());
        System.out.println("Building letter: " + result[2].trim());
        System.out.println("Floor:           " + result[3].trim());
        System.out.println("Door:            " + result[4].trim());
        System.out.println("Zip code:        " + result[5].trim());
        System.out.println("City:            " + result[6].trim());
        System.out.println("----------------------------------------");
    }
}
