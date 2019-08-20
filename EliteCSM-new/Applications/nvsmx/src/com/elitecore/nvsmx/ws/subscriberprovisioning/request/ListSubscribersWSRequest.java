package com.elitecore.nvsmx.ws.subscriberprovisioning.request;

import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

import java.util.Map;

import javax.xml.bind.annotation.XmlTransient;

public class ListSubscribersWSRequest extends SubscriberProvisioningWSRequest {

	private Map<String,String> subscriberProfileCriteria;
	private String webServiceName;
	private String webServiceMethodName;
	
	public ListSubscribersWSRequest(Map<String,String> subscriberProfileCriteria, String parameter1, String parameter2, String webServiceName,
			String webServiceMethodName) {
		super(parameter1, parameter2, webServiceName, webServiceMethodName);
		this.subscriberProfileCriteria = subscriberProfileCriteria;
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;
	}
	
	public Map<String, String> getSubscriberProfileCriteria() {
		return subscriberProfileCriteria;
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

	@Override
	public void visit(DiagnosticContextInjector manager) {
		manager.add(this);
	}
}
