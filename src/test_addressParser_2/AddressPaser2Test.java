package test_addressParser_2;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import addressParser_2.AddressPaser2;

public class AddressPaser2Test {
	
	private AddressPaser2 ap;

	//Black Box Tests
	
	@Before
	public void newObjectClass() {
		ap = new AddressPaser2();
	}
	
	@Test (expected = Exception.class)
	public void nullString() throws Exception {
		ap.parseAddress(null);
		ap.Test();
	}
	
	@Test (expected = Exception.class)
	public void emptyString() throws Exception {
		ap.parseAddress("");
		ap.Test();
	}
	
	@Test
	public void fullAddressWithDots() {
		ap.parseAddress("Rued Langgaards Vej 7, 5. sal th. 2300 København S");
		assertEquals("Rued Langgaards Vej#7##5#2300#København S", ap.Test());
	}
	
	@Test
	public void fullAddressWithoutDots() {
		ap.parseAddress("Rued Langgaards Vej 7 5 sal th 2300 København S");
		assertEquals("Rued Langgaards Vej#7##5#2300#København S", ap.Test());
	}
	
	@Test
	public void noStreetOrFloor() {
		ap.parseAddress("Rued Langgaards Vej 2300 København S");
		assertEquals("Rued Langgaards Vej####2300#København S", ap.Test());
	}
	
	@Test
	public void streetNameOnly() {
		ap.parseAddress("Rued Langgaards Vej");
		assertEquals("Rued Langgaards Vej#####", ap.Test());
	}
	
	@Test (expected = Exception.class)
	public void uncompleteStreetName() throws Exception {
		ap.parseAddress("Rued Langgaards");
		ap.Test();
	}
	
	@Test
	public void noCityName() {
		ap.parseAddress("Rued Langgaards Vej 2300");
		assertEquals("Rued Langgaards Vej####2300#København S", ap.Test());
	}
	
	@Test
	public void noCityNameOrStreet() {
		ap.parseAddress("2300");
		assertEquals("####2300#København S", ap.Test());
	}
	
	@Test
	public void noZip() {
		ap.parseAddress("Rued Langgaards Vej København S");
		assertEquals("Rued Langgaards Vej#####København S", ap.Test());
	}
	
	@Test (expected = Exception.class)
	public void badSpelling() throws Exception {
		ap.parseAddress("Rued Langgårds Vej 7");
		ap.Test();
	}
	
	@Test (expected = Exception.class)
	public void wrongAddress() throws Exception {
		ap.parseAddress("24! Hej");
	}
	
	@Test
	public void wrongOrder() {
		ap.parseAddress("2300 København S Rued Langgaards Vej 123");
		assertEquals("Rued Langgaards Vej#123###2300#København S", ap.Test());
	}
	
	@Test (expected = Exception.class)
	public void twoZips1() throws Exception {
		ap.parseAddress("Rued Langgaards Vej 23 8450 3400");
		ap.Test();
	}
	
	@Test (expected = Exception.class)
	public void twoZips2() throws Exception {
		ap.parseAddress("Rued Langgaards Vej 23 8450 3400 København");
		ap.Test();
	}
	
	@Test
	public void twoZips3() {
		ap.parseAddress("Rued Langgaards Vej 23 8450 2300 København S");
		assertEquals("Rued Langgaards Vej#23###2300#København S", ap.Test());
	}
	
	@Test (expected = Exception.class)
	public void twoZips4() throws Exception {
		ap.parseAddress("Rued Langgaards Vej 23 2300 3400 København");
		ap.Test();
	}
	
//	@After 
//	public void after() {
//		System.out.println(ap.Test());
//	}
}
