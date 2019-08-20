package com.elitecore.nvsmx.ws.subscriberprovisioning.request;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;
import com.elitecore.nvsmx.ws.subscriberprovisioning.data.SubscriberProfile;

public class UpdateSubscriberWSRequest extends SubscriberProvisioningWSRequest {

	private SubscriberProfile subscriberProfile;
	private String subscriberID;
	private String alternateId;
	private Integer updateAction;
	private String webServiceName;
	private String webServiceMethodName;
	
	public UpdateSubscriberWSRequest(SubscriberProfile subscriberProfile, String subscriberID, String alternateId,
			Integer updateAction, String parameter1, String parameter2, String webServiceName,
			String webServiceMethodName) {
		super(parameter1, parameter2, webServiceName, webServiceMethodName);
		this.subscriberProfile = subscriberProfile;
		this.subscriberID = subscriberID;
		this.alternateId = alternateId;
		this.updateAction = updateAction;
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;
	}

	public Integer getUpdateAction() {
		return updateAction;
	}

	public void setUpdateAction(Integer updateAction) {
		this.updateAction = updateAction;
	}

	public SubscriberProfile getSubscriberProfile() {
		return subscriberProfile;
	}

	public String getSubscriberID() {
		return subscriberID;
	}
	
	public String getAlternateId() {
		return alternateId;
	}

	public void setAlternateId(String alternateId) {
		this.alternateId = alternateId;
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
	
	public void setSubscriberProfile(SubscriberProfile subscriberProfile) {
		this.subscriberProfile = subscriberProfile;
	}

	public void setSubscriberID(String subscriberID) {
		this.subscriberID = subscriberID;
	}

	@Override
	public void visit(DiagnosticContextInjector manager) {
		manager.add(this);
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);
		
		builder.append("Subscriber Id", subscriberID);
		builder.append("Alternate Id", alternateId);
		//builder.append("Subscriber Profile", subscriberProfile);
		builder.append("Parameter 1", getParameter1());
		builder.append("Parameter 2", getParameter2());
		
		return builder.toString();
	}
	
}
