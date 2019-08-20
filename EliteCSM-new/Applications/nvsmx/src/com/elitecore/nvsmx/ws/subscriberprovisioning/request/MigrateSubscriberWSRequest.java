package com.elitecore.nvsmx.ws.subscriberprovisioning.request;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

public class MigrateSubscriberWSRequest extends BaseWebServiceRequest {

	private String currentSubscriberIdentity;
	private String newSubscriberIdentity;
	private String parameter1;
	private String parameter2;
	private String webServiceName;
	private String webServiceMethodName;
	
	public MigrateSubscriberWSRequest(String currentSubscriberIdentity, String newSubscriberIdentity, String parameter1, String parameter2, String webServiceName,
			String webServiceMethodName) {
		this.currentSubscriberIdentity = currentSubscriberIdentity;
		this.newSubscriberIdentity = newSubscriberIdentity;
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;
	}
	public String getCurrentSubscriberIdentity() {
		return currentSubscriberIdentity;
	}
	public void setCurrentSubscriberIdentity(String currentSubscriberIdentity) {
		this.currentSubscriberIdentity = currentSubscriberIdentity;
	}
	public String getNewSubscriberIdentity() {
		return newSubscriberIdentity;
	}
	public void setNewSubscriberIdentity(String newSubscriberIdentity) {
		this.newSubscriberIdentity = newSubscriberIdentity;
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
