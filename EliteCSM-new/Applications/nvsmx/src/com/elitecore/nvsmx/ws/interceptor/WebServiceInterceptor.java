package com.elitecore.nvsmx.ws.interceptor;


public interface WebServiceInterceptor {
	void requestReceived(WebServiceRequest request);
	void responseReceived(WebServiceResponse request);
}
