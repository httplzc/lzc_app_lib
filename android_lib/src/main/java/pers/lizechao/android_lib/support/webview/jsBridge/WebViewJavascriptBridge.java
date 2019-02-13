package pers.lizechao.android_lib.support.webview.jsBridge;


public interface WebViewJavascriptBridge {
	
	void send(String data);
	void send(String data, CallBackFunction responseCallback);
	
	

}
