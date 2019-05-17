package org.example.helloapp;

import org.example.hellolib.HelloLib;

public class HelloApp {
	public static void main(String[] args) {
		HelloLib lib = new HelloLib();
		System.out.println(lib.getGreetingMessage("Alice"));
	}
}

