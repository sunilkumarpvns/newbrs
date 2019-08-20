package com.elitecore.nvsmx.ws.interceptor;

public interface WebServiceResponse {
	public String getWebServiceMethodName();
	public String getWebServiceName();
	public String getParameter1();
	public String getParameter2();
	public Integer getResponseCode();
}
