package com.mycompany.javaapp.client;

import jsinterop.annotations.JsType;


// @JsType to preserve class name and instance method names
@JsType
public class MyJavaScriptClass {

	public MyJavaScriptClass() {
	}

	public float add(float a, float b) {
		return a + b;
	}

	public float multiply(float a, float b) {
		return a * b;
	}
}
