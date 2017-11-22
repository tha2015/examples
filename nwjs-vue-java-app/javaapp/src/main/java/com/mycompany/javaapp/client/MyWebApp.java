package com.mycompany.javaapp.client;

import java.util.function.Consumer;

import com.google.gwt.core.client.EntryPoint;


public class MyWebApp implements EntryPoint {

  @Override
  public void onModuleLoad() {
    exportCalculate();
  }

  public static int calculate(int a) {


	  return a * 2;
  }

  public native void exportCalculate() /*-{
    var fn = $entry(@com.mycompany.javaapp.client.MyWebApp::calculate(*));
    if (typeof module !== 'undefined' && module.exports) {
      module.exports = fn;
    } else if (typeof self === 'object') {
      self.calculate = fn;
    } else {
      window.calculate = fn;
    }
  }-*/;

}
