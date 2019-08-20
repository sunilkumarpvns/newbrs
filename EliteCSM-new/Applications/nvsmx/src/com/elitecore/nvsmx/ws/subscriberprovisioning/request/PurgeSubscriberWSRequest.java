package com.elitecore.nvsmx.ws.subscriberprovisioning.request;

import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

import javax.xml.bind.annotation.XmlTransient;

public class PurgeSubscriberWSRequest extends SubscriberProvisioningWSRequest {

	private String subscriberId;
	private String alternateId;
	private String webServiceName;
	private String webServiceMethodName;

	
	public PurgeSubscriberWSRequest(String subscriberId, String alternateId, String parameter1, String parameter2, String webServiceName, String webServiceMethodName) {
		super(parameter1, parameter2, webServiceName, webServiceMethodName);
		this.subscriberId = subscriberId;
		this.alternateId = alternateId;
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;
	}

	public String getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}

	public String getAlternateId() {
		return alternateId;
	}

	public void setAlternateId(String alternateId) {
		this.alternateId = alternateId;
	}

	@Override
	public void visit(DiagnosticContextInjector manager) {
		manager.add(this);
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);
		
		builder.append("Subscriber Id", subscriberId);
		builder.append("Alternate Id", alternateId);
		builder.append("Parameter 1", getParameter1());
		builder.append("Parameter 2", getParameter2());

		return builder.toString();
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
