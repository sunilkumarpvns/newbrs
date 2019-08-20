package com.elitecore.nvsmx.ws.subscription.request;

import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

import javax.xml.bind.annotation.XmlTransient;


public class ListSubscriptionWSRequest extends BaseWebServiceRequest {

	private String subscriberId;
	private String alternateId;
	private String subscriptionStatusValue;
	private String subscriptionStatusName;
	private String parameter1;
	private String parameter2;
	private String webServiceName;
	private String webServiceMethodName;

	public ListSubscriptionWSRequest(String subscriberId, String alternateId, String subscriptionStatusValue, String subscriptionStatusName, String parameter1, String parameter2,
									 String webServiceName, String webServiceMethodName) {

		this.subscriberId = subscriberId;
		this.alternateId = alternateId;
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;
		this.subscriptionStatusValue = subscriptionStatusValue;
		this.subscriptionStatusName = subscriptionStatusName;
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

	public String getSubscriptionStatusValue() {
		return subscriptionStatusValue;
	}

	public void setSubscriptionStatusValue(String subscriptionStatusValue) {
		this.subscriptionStatusValue = subscriptionStatusValue;
	}

	public String getSubscriptionStatusName() {
		return subscriptionStatusName;
	}

	public void setSubscriptionStatusName(String subscriptionStatusName) {
		this.subscriptionStatusName = subscriptionStatusName;
	}

	public String getParameter1() {return parameter1;}
	public String getParameter2() {return parameter2;}

	@Override
	public void visit(DiagnosticContextInjector manager) {
		manager.add(this);
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);
		
		builder.append("Subscriber Id", subscriberId);
		builder.append("Alternate Id", alternateId);
		builder.append("Subscription Status Name", subscriptionStatusName);
		builder.append("Subscription Status Value", subscriptionStatusValue);
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
