package com.mycompany.javaapp.client;

import com.google.gwt.core.client.EntryPoint;


public class MyWebApp implements EntryPoint {

  @Override
  public void onModuleLoad() {
	  exportMyJavaCtor();
  }

  // Export a factory method which will create an instance of MyJavaScriptClass (with preserved method names b/c @JsType)
  public native void exportMyJavaCtor() /*-{
    var fn = @com.mycompany.javaapp.client.MyJavaScriptClass::new();
    if (typeof module !== 'undefined' && module.exports) {
      module.exports = fn;
    } else if (typeof self === 'object') {
      self.getInstance = fn;
    } else {
      window.getInstance = fn;
    }
  }-*/;

}
