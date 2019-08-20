package com.elitecore.nvsmx.ws.subscriberprovisioning.request;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;
import com.elitecore.nvsmx.ws.subscriberprovisioning.data.SubscriberProfile;

public class AddBulkSubscriberWSRequest extends SubscriberProvisioningWSRequest{

	private List<SubscriberProfile> subscriberProfiles;
	private String webServiceName;
	private String webServiceMethodName;
	
	public AddBulkSubscriberWSRequest(List<SubscriberProfile> subscriberProfiles, String parameter1, String parameter2, String webServiceName,
			String webServiceMethodName) {
		super(parameter1, parameter2, webServiceName, webServiceMethodName);
		this.subscriberProfiles = subscriberProfiles;
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;
	}
	
	public List<SubscriberProfile> getSubscriberProfiles() {
		return subscriberProfiles;
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
