package org.example.hellolib;

import static org.junit.Assert.*;

import org.junit.Test;

public class HelloLibTest {

	@Test
	public void testGetGreetingMessage() {
		assertEquals("Hello JJ", new HelloLib().getGreetingMessage("JJ"));
	}

}
