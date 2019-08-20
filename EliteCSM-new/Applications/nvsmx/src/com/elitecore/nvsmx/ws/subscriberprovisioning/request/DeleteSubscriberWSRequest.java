package com.elitecore.nvsmx.ws.subscriberprovisioning.request;

import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

public class DeleteSubscriberWSRequest extends SubscriberProvisioningWSRequest {

	private String subscriberID;
	private String alternateId;
	private String webServiceName;
	private String webServiceMethodName;
	
	public DeleteSubscriberWSRequest(String subscriberID, String alternateId, String parameter1, String parameter2, String webServiceName,
			String webServiceMethodName) {
		super(parameter1, parameter2, webServiceName, webServiceMethodName);
		this.subscriberID = subscriberID;
		this.alternateId = alternateId;
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;
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

	public void setSubscriberID(String subscriberID) {
		this.subscriberID = subscriberID;
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
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);
		
		builder.append("Subscriber Id", subscriberID);
		builder.append("Alternate Id", alternateId);
		builder.append("Parameter 1", getParameter1());
		builder.append("Parameter 2", getParameter2());

		return builder.toString();
	}

	@Override
	public void visit(DiagnosticContextInjector manager) {
		manager.add(this);
	}
}
