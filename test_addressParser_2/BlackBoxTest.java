package test_addressParser_2;
import addressParser_2.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import addressParser_2.AdresParser;


public class BlackBoxTest {
//private String input;
private AdresParser ap;

	@Before
	public void setUp() {
		String input = "Rued Langgaards Vej 7A 1. mf. 2860 Søborg";
		ap = new AdresParser(input);
		
		
	}

	@Test
	public void noStringTest() {
		String exp = "Street name:     Rued Langgaards Vej" + 
				"Building no:     7" +
				"Building letter: A" +
				"Floor:           1" +
				"Door:            mf" +
				"Zip code:        2860" +
				"City:            Søborg" ;
		
		assertEquals(exp, ap.getResult());
	}
	
	@After 
	public void after() {
		ap = null;
	}
}
