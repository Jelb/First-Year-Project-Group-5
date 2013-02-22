package HandIn1;

import java.io.FileNotFoundException;

/**
 * Write a description of class test here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class test
{
    /**
     * Constructor for objects of class test
     */
    public test()
    {
        String[] indputs = new String[] {
            "Rued Langgaards Vej 7",
//            "København 2300 Rued Langgaards Vej 7, 5.",
//            "Rued Langgaards Vej 7, 5. sal, København S",
//            "Rued Langgaards Vej 7 2300 København S",
//            "Rued Langgaards Vej 7, 5.",
//            "Rued Langgaards Vej 7A København S",
//            "Rued Langgaards Vej i København",
//            "Rued Langgaards Vej nær 2300 København",
//            "Are you kidding me?",
//            "Hesseløgade 56 i København Ø",
//            "2000 Finsensvej 3",
//            "Finsensvej 2000 Frederiksberg",
//            "Frederikberg 2000 Finsensvej 7",
//        	"Finsensvej 3 2000 700000 Frederiksberg",
//        	"Finsensvej 3, 5. sal, 2. th, Frederiksberg"
        	};
            
        for(String str: indputs) {
            RegExAddress2 rea = new RegExAddress2();
            rea.parseAddress(str);
        }
    }
    
    public static void main(String[] args) {
    	new test();
//    	try {
//    		FileScanner fs = new FileScanner();
//    		fs.scanning("Agerlandet 22 2200 hej");
//    	} catch(FileNotFoundException e) {
//    		System.out.println("FEJL!");
//    	}
    	
    }
}
