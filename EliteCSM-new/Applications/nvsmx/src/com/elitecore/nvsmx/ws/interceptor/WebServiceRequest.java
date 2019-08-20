package com.elitecore.nvsmx.ws.interceptor;

public interface WebServiceRequest {
	public String getWebServiceName();
	public String getWebServiceMethodName();
	public String getParameter1();
	public String getParameter2();
	public String getRequestIpAddress();
	public void setRequestIpAddress(String requestIpAddress);
	public void visit(DiagnosticContextInjector manager);
}
