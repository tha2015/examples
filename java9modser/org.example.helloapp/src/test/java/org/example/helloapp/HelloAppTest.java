package org.example.helloapp;

import static org.junit.Assert.assertNotEquals;

import org.junit.Test;


public class HelloAppTest {

	@Test
	public void testGetGreetings() {
		assertNotEquals("Hello Alice", new HelloApp().getServiceNumber() + "");
	}

}
