package com.elitecore.nvsmx.ws.subscriberprovisioning.request;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;
import com.elitecore.nvsmx.ws.subscriberprovisioning.data.SubscriberProfileData;

public class AddSubscriberProfileBulkWSRequest extends SubscriberProvisioningWSRequest {
	
	private List<SubscriberProfileData> subscriberProfiles;
	private String webServiceName;
	private String webServiceMethodName;
	
	public AddSubscriberProfileBulkWSRequest(List<SubscriberProfileData> subscriberProfiles,String parameter1, String parameter2, String webServiceName,
			String webServiceMethodName) {
		super(parameter1, parameter2, webServiceName, webServiceMethodName);
		this.subscriberProfiles = subscriberProfiles;
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;
	}
	
	public List<SubscriberProfileData> getSubscriberProfiles() {
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

