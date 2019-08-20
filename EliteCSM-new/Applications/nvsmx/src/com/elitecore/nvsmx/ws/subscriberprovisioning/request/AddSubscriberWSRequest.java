package com.elitecore.nvsmx.ws.subscriberprovisioning.request;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;
import com.elitecore.nvsmx.ws.interceptor.WebServiceRequest;
import com.elitecore.nvsmx.ws.subscriberprovisioning.data.SubscriberProfile;
import com.elitecore.nvsmx.ws.subscriberprovisioning.request.SubscriberProvisioningWSRequest;

public class AddSubscriberWSRequest extends SubscriberProvisioningWSRequest
		implements WebServiceRequest {

	private String webServiceName;
	private String webServiceMethodName;

	private SubscriberProfile subscriberProfile;

	public AddSubscriberWSRequest(SubscriberProfile subscriberProfile,
			String parameter1, String parameter2, String webServiceName,
			String webServiceMethodName) {
		super(parameter1, parameter2, webServiceName, webServiceMethodName);
		this.subscriberProfile = subscriberProfile;
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;
	}

	public SubscriberProfile getSubscriberProfile() {
		return subscriberProfile;
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