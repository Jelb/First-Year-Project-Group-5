package addressParser_2;


import java.io.FileNotFoundException;
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
public class AdresParser
{
    //Hej
    //pattern for zipcode
    private static final String POST = "[0-9]{4}";
    //pattern for house number
    private static final String NR = "[^0-9][0-9]+";
    //pattern for city
    private static final String BY = "[\\wæÆøØåÅ]{3,}";
    //patterns for floor
    private static final String SAL = "sal";
    private static final String ETAGE = "etage";
    

	//test input
    private String INPUT;
    private final String searchString;
    //array to store substrings of the input
    private String[] sub = new String[2];
    //array to store the results
    private final String[] result = new String[7];
    
    private Pattern p1;
    private Matcher m1;
    //Boolean to tell if the proces is done
    private boolean done = false;
    public AdresParser(String INPUT) {
        this.INPUT = INPUT;
        searchString = INPUT;
        INPUT = INPUT.replaceAll("[,.]", "");
        for(int i = 0; i < result.length; i++) {result[i] = "NOT FOUND";}
        randomCheck();
        
        prepositionCheck();

        p1 = Pattern.compile(POST);
        m1 = p1.matcher(INPUT.trim());
        
        //checks if the input contains a 4-digit number ≈ a zipcode
        if(m1.find() && !done) {                                                // CHECK 06
            //checks if the zipcode is placed in the midel of the input
            if(m1.start() > 0 && m1.end() < (INPUT.length() - 1)) {             // CHECK 08
                sub[0] = INPUT.substring(0,(m1.start())).trim() + " ";
                sub[1] = INPUT.substring(m1.end(), INPUT.length()).trim();
                result[5] = INPUT.substring(m1.start(), m1.end());
                
                p1 = Pattern.compile(NR);
                m1 = p1.matcher(sub[0]);
                boolean match1 = m1.find();
                m1 = p1.matcher(sub[1]);
                boolean match2 = m1.find();
                if(match1 && !match2) {                                         // CHECK 09
                    //If a number is placed on the left side of the zipcode and non on 
                    //the right side the right side will be set as city name.
                    result[6] = sub[1];
                } else if(!match1 && match2) {                                  // CHECK 10
                    //If a number is placed on the right side of the zipcode and non on 
                    //the left side the left side will be set as city name.
                    result[6] = sub[0];
                    sub[0] = sub[1];
                } else if(!match1 && !match2) {                                 // CHECK 11
                    //if the input do not contain any numbers except from the zipcode, the
                    //left most part is set to street name and the right most part as city name
                    result[6] = sub[1];
                    result[0] = sub[0]; 
                    done = true;
                } else if(match1 && match2) {                                   // CHECK 12
                    //if a number is placed on each site of the zipcode the system is not 
                    //capable of determind which part is city name and which is streetname
                    System.out.println("--------------------------------------");
                    System.out.println("The system was not able to distinguish");
                    System.out.println("between street name and town name.");
                    System.out.println("--------------------------------------");
                    done = true;
                }
                if(!done) {                                                     // CHECK 13
                    buildingNo();
                }
            } 
        } else if(!m1.find() && !done) {                                        // CHECK 07
            p1 = Pattern.compile(NR);
            m1 = p1.matcher(INPUT);
            if(m1.find()) {                                                     // CHECK 14
                result[0] = INPUT.substring(0, m1.start());
                sub[0] = INPUT.substring(m1.start(), INPUT.length());
                p1 = Pattern.compile(BY);
                m1 = p1.matcher(sub[0]);
                if(m1.find()) {                                                 // CHECK 15
                    result[6] = sub[0].substring(m1.start(), sub[0].length());
                    sub[0] = sub[0].substring(0, m1.start());
                }
                buildingNo();
            } else if(!m1.find()) {                                             // CHECK 25
                result[0] = INPUT.trim();
            }
        }
        
        System.out.println("----------------------------------------");
        System.out.println("Seach stirng: " + searchString);
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
    
    /**
     * Search the substring of INPUT for any numbers and 
     */
    private void buildingNo() {
        p1 = Pattern.compile(NR);
        m1 = p1.matcher(sub[0]);
        if(m1.find() && m1.start() > 0 && m1.end() < (INPUT.length() - 1)) {    // CHECK 16
            result[1] = sub[0].substring(m1.start(), m1.end());
            result[0] = sub[0].substring(0,(m1.start())).trim();
            sub[0] = sub[0].substring(m1.end(), sub[0].length()).trim() +" ";
            sub[0] = sub[0].replaceAll(SAL,"");
            sub[0] = sub[0].replaceAll(ETAGE,"");
            p1 = Pattern.compile("[\\d\\s][a-lA-L]{1}");
            m1 = p1.matcher(" " + sub[0]);
            if(m1.find()) {                                                     // CHECK 17
                result[2] = sub[0].substring(m1.start(),m1.end());
                sub[0] = sub[0].replaceFirst(result[2],"").trim();
            }
            sub = sub[0].split(" ");    
            if(sub.length == 1) {                                               // CHECK 18
                result[3] = sub[0];
            } else if (sub.length == 2) {                                       // CHECK 19
                result[3] = sub[0];
                result[4] = sub[1];
            } else if (sub.length == 3) {                                       // CHECK 20
                if(sub[0].equals(".") || sub[0].equals(",")) {                  // CHECK 21
                    result[3] = sub[1];
                    result[4] = sub[2];
                }
            }
        } else {                                                                
            buildingLetter();
        }
    }
    
    /**
     * Is used to search for building letters. If a building letter is found, it will be
     * stored in the result array and then errased from the INPUT string.
     * <br> After checking for the building letter the INPUT string is spiltted and each 
     * part is placed in the corresponding array index. 
     */
    private void buildingLetter() {
        sub[0] = sub[0].replaceAll(SAL,"");
        sub[0] = sub[0].replaceAll(ETAGE,"");
        p1 = Pattern.compile("[a-lA-L]{1}");
        m1 = p1.matcher(sub[0]);
        if(m1.find()) {                                                         // CHECK 22
            result[2] = sub[0].substring(m1.start(),m1.end());
            sub[0] = sub[0].replaceFirst(result[2],"").trim();
        }
        sub = sub[0].trim().split(" ");
        if(sub.length == 1) {                                                   // CHECK 23
            result[1] = sub[0];
        } else if (sub.length == 2) {                                           // CHECK 24
            result[1] = sub[0];
            result[3] = sub[1];
        }
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
            done = true;
        }
    }
    
    /**
     * Checks for the prepositions "i" (in) and "nær" (near).
     */
    private void prepositionCheck() {
        //Checks for " i " in the input
        p1 = Pattern.compile("\\si\\s");
        m1 = p1.matcher(INPUT.trim());
        if(m1.find()) {                                                         // CHECK 02
            prepositionSplit();
        }
        
        //Checks for " nær " in the input
        p1 = Pattern.compile("\\snær\\s");
        m1 = p1.matcher(INPUT.trim());
        if(m1.find()) {                                                         // CHECK 03  
            prepositionSplit();
        }
    } 
    
    /**
     * Splits the stirng if any prepositions is found.
     */
    private void prepositionSplit() {
        sub[0] = INPUT.substring(0, m1.start());
        sub[1] = INPUT.substring(m1.end(), INPUT.length());
        p1 = Pattern.compile(POST);
        m1 = p1.matcher(sub[1].trim());
        if(m1.find()) {                                                         // CHECK 04
            result[5] = sub[1].substring(m1.start(), m1.end()).trim();
            result[6] = sub[1].substring(m1.end(), sub[1].length()).trim();
        } else {
            result[6] = sub[1].trim();
        }
        
        p1 = Pattern.compile(NR);
        m1 = p1.matcher(sub[0].trim());
        if(m1.find()) {                                                         // CHECK 05
            result[1] = sub[0].substring(m1.start(), m1.end()).trim();
            result[0] = sub[0].substring(0, m1.start());
        } else {
            result[0] = sub[0].trim();
        }
        
        done = true;
    }
    public String getResult() {
    	String temp = "";
    	for(String s: result){if(s != null) temp += s.trim();}
		return temp;
	}
    public static void main(String[]  args) {
    	AdresParser ad = new AdresParser("Rued Langgaards Vej 7A 1. mf. 4735 Mern");
    }
}