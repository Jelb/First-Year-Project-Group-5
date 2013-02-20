package HandIn1;

import java.io.FileNotFoundException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.swing.JOptionPane;
 /**
 * regexadress is use to spilt a given adress input into each of its components.<br>
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
public class regexadress
{

    //pattern for zipcode
    private String POST = "[0-9]{4}";
    //pattern for house number
    private String NR = "[^0-9][0-9]+";
    private String BY = "[\\wæÆøØåÅ]{4,}";
    private String SAL = "sal";
    private String DOT = "\\s[.,]\\s";
    
    //test input
    private String INPUT;
    //array to store substrings of the input
    private String[] sub = new String[2];
    //array to store the results
    private String[] result = new String[7];
    
    private Pattern p1;
    private Matcher m1;
    private boolean done = false;
    public regexadress() {
        
    }
    
    public void parseAddress(String INPUT) {
    	this.INPUT = INPUT;
        randomCheck();
        
        // tries to find match in road name file
        String match = "";
        try {
        	FileScanner fs = new FileScanner();
        	match = fs.scanning(INPUT);
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        }
        
        // no match if match-string is empty
        if (match.isEmpty()) {
        	throw new InvalidAddressException();
        }
        
        prepositionCheck();
        p1 = Pattern.compile(POST);
        m1 = p1.matcher(INPUT.trim());
        
        //checks if the input contains a 4-digit number ��������� a zipcode
        if(m1.find() && !done) {
            //checks if the zipcode is placed in the midel of the input
            if(m1.start() > 0 && m1.end() < (INPUT.length() - 1)) {
                sub[0] = INPUT.substring(0,(m1.start())).trim() + " ";
                sub[1] = INPUT.substring(m1.end(), INPUT.length()).trim();
                result[5] = INPUT.substring(m1.start(), m1.end());
                
                p1 = Pattern.compile(NR);
                m1 = p1.matcher(sub[0]);
                boolean match1 = m1.find();
                m1 = p1.matcher(sub[1]);
                boolean match2 = m1.find();
                if(match1 && !match2) {
                    //If a number is placed on the left side of the zipcode and non on 
                    //the right side the right side will be set as city name.
                    result[6] = sub[1];
                } else if(!match1 && match2) {
                    //If a number is placed on the right side of the zipcode and non on 
                    //the left side the left side will be set as city name.
                    result[6] = sub[0];
                    sub[0] = sub[1];
                } else if(!match1 && !match2) {
                    //if the input do not contain any numbers except from the zipcode, the
                    //left most part is set to street name and the right most part as city name
                    result[6] = sub[1];
                    result[0] = sub[0]; 
                    done = true;
                } else if(match1 && match2) { 
                    //if a number is placed on each site of the zipcode the system is not 
                    //capable of determind which part is city name and which is streetname
                    JOptionPane.showMessageDialog(null, "The system was not able to distinguish between street name and town name", "ERROR", JOptionPane.ERROR_MESSAGE);
                    done = true;
                }
                if(!done) {
                    buildingNo();
                }
            } 
        } else if(!m1.find() && !done) {
            p1 = Pattern.compile(NR);
            m1 = p1.matcher(INPUT);
            if(m1.find()) {
                result[0] = INPUT.substring(0, m1.start());
                sub[0] = INPUT.substring(m1.start(), INPUT.length());
                p1 = Pattern.compile(BY);
                m1 = p1.matcher(sub[0]);
                if(m1.find()) {
                    result[6] = sub[0].substring(m1.start(), sub[0].length());
                    sub[0] = sub[0].substring(0, m1.start());
                }
                buildingNo();
            } else if(!m1.find()) {
                result[0] = INPUT.trim();
            }
        }
        
        result[0] = match;
        
        System.out.println("----------------------------------------");
        System.out.println(INPUT);
        for(String s: result) {
            if(s != null) {
                System.out.print(s.trim() + "#");
            } else {
                System.out.print("#");
            }
        }
        System.out.println("");
        System.out.println("----------------------------------------");
        
    }
    
    private void buildingNo() {
        p1 = Pattern.compile(NR);
        m1 = p1.matcher(sub[0]);
        if(m1.find() && m1.start() > 0 && m1.end() < (INPUT.length() - 1)) {
            result[1] = sub[0].substring(m1.start(), m1.end());
            result[0] = sub[0].substring(0,(m1.start())).trim();
            sub[0] = sub[0].substring(m1.end(), sub[0].length()).trim() +" ";
            sub[0] = sub[0].replaceAll(SAL,"");
            sub[0] = sub[0].replaceAll(DOT," ");
            p1 = Pattern.compile("[\\d\\s][a-lA-L]{1}");
            m1 = p1.matcher(" " + sub[0]);
            if(m1.find()) {
                result[2] = sub[0].substring(m1.start(),m1.end());
                sub[0] = sub[0].replaceFirst(result[2],"").trim();
            }
            sub = sub[0].split(" ");
            if(sub.length == 1) {
                result[3] = sub[0];
            } else if (sub.length == 2) {
                result[3] = sub[0];
                result[4] = sub[1];
            } else if (sub.length == 3) {
                if(sub[0].equals(".") || sub[0].equals(",")) {
                    result[3] = sub[1];
                    result[4] = sub[2];
                }
            }
        } else {
            buildingLetter();
        }
    }
    
    private void buildingLetter() {
        sub[0] = sub[0].replaceAll(SAL,"");
        sub[0] = sub[0].replaceAll(DOT," ");
        p1 = Pattern.compile("[a-lA-L]{1}");
        m1 = p1.matcher(sub[0]);
        if(m1.find()) {
            result[2] = sub[0].substring(m1.start(),m1.end());
            sub[0] = sub[0].replaceFirst(result[2],"").trim();
        }
        sub = sub[0].trim().split(" ");
        if(sub.length == 1) {
            result[1] = sub[0];
        } else if (sub.length == 2) {
            result[1] = sub[0];
            result[3] = sub[1];
        }
    }
    
    /**
     * Checks if the input contains any 
     */
    private void randomCheck() {
        p1 = Pattern.compile("[^\\w\\d\\s.,æÆøØåÅ]");
        m1 = p1.matcher(INPUT.trim());
        if(m1.find()) {
            throw new InvalidAddressException();
        }
    }
    
    private void prepositionCheck() {
        //Checks for " i " in the input
        p1 = Pattern.compile("\\si\\s");
        m1 = p1.matcher(INPUT.trim());
        if(m1.find()) {
            prepositionSplit(m1.start(), m1.end());
        }
        
        //Checks for " n������r " in the input
        p1 = Pattern.compile("\\snær\\s");
        m1 = p1.matcher(INPUT.trim());
        if(m1.find()) {
            prepositionSplit(m1.start(), m1.end());
        }
    } 
    
    private void prepositionSplit(int stat, int end) {
        sub[0] = INPUT.substring(0, m1.start());
        sub[1] = INPUT.substring(m1.end(), INPUT.length());
        p1 = Pattern.compile(POST);
        m1 = p1.matcher(sub[1].trim());
        if(m1.find()) {
            result[5] = sub[1].substring(m1.start(), m1.end()).trim();
            result[6] = sub[1].substring(m1.end(), sub[1].length()).trim();
        } else {
            result[6] = sub[1].trim();
        }
        
        p1 = Pattern.compile(NR);
        m1 = p1.matcher(sub[0].trim());
        if(m1.find()) {
            result[1] = sub[0].substring(m1.start(), m1.end()).trim();
            result[0] = sub[0].substring(0, m1.start());
        } else {
            result[0] = sub[0].trim();
        }
        
        done = true;
    }
}