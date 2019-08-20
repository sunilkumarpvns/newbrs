package com.elitecore.nvsmx.ws.subscriberprovisioning.request;

import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

import javax.xml.bind.annotation.XmlTransient;

public class GetSubscriberWSRequest extends SubscriberProvisioningWSRequest {

	private String subscriberID;
	private String alternateId;
	private String webServiceName;
	private String webServiceMethodName;
	
	public GetSubscriberWSRequest(String subscriberID, String alternateId, String parameter1, String parameter2, String webServiceName,
			String webServiceMethodName) {
		super(parameter1, parameter2,webServiceName, webServiceMethodName);
		this.subscriberID = subscriberID;
		this.alternateId = alternateId;
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;
	}
	
	public String getSubscriberId() {
		return subscriberID;
	}
	
	public String getAlternateId() {
		return alternateId;
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
