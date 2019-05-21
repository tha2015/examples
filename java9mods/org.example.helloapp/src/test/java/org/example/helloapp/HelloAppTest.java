package org.example.helloapp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class HelloAppTest {

	@Test
	public void testGetGreetings() {
		assertEquals("Hello Alice", new HelloApp().getGreetings());
	}

}
