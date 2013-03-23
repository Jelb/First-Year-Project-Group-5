package Test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Part1.*;
import QuadTree.QuadTree;

public class WindowTest {
	private Window window;
	
	@Before
	public void setUp() {
		window = Window.use();
		System.out.println("~~~~~~~~~~~");
	}

}
