package org.example.helloapp;

import java.util.ServiceLoader;
import java.util.function.IntSupplier;


public class HelloApp {
	public int getServiceNumber() {
		ServiceLoader<IntSupplier> loader =  ServiceLoader.load(java.util.function.IntSupplier.class);
		return loader.iterator().next().getAsInt();
	}
	public static void main(String[] args) {
		System.out.println(new HelloApp().getServiceNumber());
	}
}

