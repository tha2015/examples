package org.example.helloapp;

import org.apache.commons.lang3.StringUtils;
import org.example.hellolib.HelloLib;

public class HelloApp {
	public String getGreetings() {
		HelloLib lib = new HelloLib();
		return lib.getGreetingMessage("Alice");
	}
	public static void main(String[] args) {
		System.out.println(StringUtils.capitalize("alice"));
		System.out.println(new HelloApp().getGreetings());
	}
}

