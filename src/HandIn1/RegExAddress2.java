package HandIn1;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExAddress2 {
	
	private String[] output;
	private String input;
	private Pattern p;
	private Matcher m;
	
	public RegExAddress2() {
		output = new String[6];
	}
	
	public void parseAddress(String input) {
		this.input = input.trim().toLowerCase();
		checkIllegalChars();
		matchStreetName();
		findRoadNumber();
	}
	
	private void matchStreetName() {
		// tries to find match in road name file
        String match = "";
        try {
        	FileScanner fs = new FileScanner();
        	match = fs.scanning(input);
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        }
        
        // no match if match-string is empty
        if (match.isEmpty()) {
        	throw new InvalidAddressException();
        }
        
        output[0] = match;
        
        input = input.substring(input.lastIndexOf(match, 0)).trim();
	}
	
	private void findRoadNumber() {
		p = Pattern.compile("\\d+");
		m = p.matcher(input);
		if (m.find()) {
			output[1] = m.group();
		} else {
			output[1] = "";
		}
	}
	
	/**
     * Checks if the input contains any illegal characters
     */
    private void checkIllegalChars() {
        p = Pattern.compile("[^\\w\\d\\s.,æÆøØåÅ]");
        m = p.matcher(input);
        if(m.find()) {
            throw new InvalidAddressException();
        }
    }
	
}
