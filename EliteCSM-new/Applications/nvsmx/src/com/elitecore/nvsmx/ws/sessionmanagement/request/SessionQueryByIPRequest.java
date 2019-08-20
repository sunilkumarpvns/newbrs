package com.elitecore.nvsmx.ws.sessionmanagement.request;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

public class SessionQueryByIPRequest extends BaseWebServiceRequest {

	private String sessionIP; 
	private String sessionType;
	private String parameter1;
	private String parameter2;
	private String webServiceName;
	private String webServiceMethodName;

	public SessionQueryByIPRequest(String sessionIP, String sessionType, String parameter1, String parameter2,
			String webServiceName, String webServiceMethodName) {
		this.sessionIP = sessionIP;
		this.sessionType = sessionType;
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;
	}

	public String getSessionIP() {
		return sessionIP;
	}

	public void setSessionIP(String sessionIP) {
		this.sessionIP = sessionIP;
	}

	public String getSessionType() {
		return sessionType;
	}

	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}

	public String getParameter1() {
		return parameter1;
	}

	public void setParameter1(String parameter1) {
		this.parameter1 = parameter1;
	}

	public String getParameter2() {
		return parameter2;
	}

	@Override
	public void visit(DiagnosticContextInjector manager) {
		manager.add(this);
	}

	public void setParameter2(String parameter2) {
		this.parameter2 = parameter2;
	}
	
	@XmlTransient
	@Override
	public String getWebServiceName() {
		return webServiceName;
	}

	@XmlTransient
	@Override
	public String getWebServiceMethodName() {
		return webServiceMethodName;
	}
}
