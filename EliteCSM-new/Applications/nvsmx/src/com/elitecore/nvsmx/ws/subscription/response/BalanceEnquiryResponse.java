package com.elitecore.nvsmx.ws.subscription.response;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.elitecore.nvsmx.ws.subscription.data.SubscriptionInformationData;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author Jay Trivedi
 *
 */
@XmlRootElement
public class BalanceEnquiryResponse implements WebServiceResponse {

	private Integer responseCode;
	private String responseMessage;
	private String parameter1;
	private String parameter2;
	private String webServiceName;
	private String webServiceMethodName;

	private List<SubscriptionInformationData> subscriptionInformations;
	
	public BalanceEnquiryResponse(){}
	public BalanceEnquiryResponse(Integer responseCode,String responseMessage, List<SubscriptionInformationData> subscriptionInformations,
			String parameter1, String parameter2,String webServiceName, String webServiceMethodName) {
				this.responseCode = responseCode;
				this.responseMessage = responseMessage;
				this.subscriptionInformations = subscriptionInformations;
				this.parameter1 = parameter1;
				this.parameter2 = parameter2;
				this.webServiceName = webServiceName;
				this.webServiceMethodName = webServiceMethodName;
	}

	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
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

	public void setParameter2(String parameter2) {
		this.parameter2 = parameter2;
	}

	public List<SubscriptionInformationData> getSubscriptionInformations() {
		return subscriptionInformations;
	}

	public void setSubscriptionInformations(List<SubscriptionInformationData> subscriptionInformations) {
		this.subscriptionInformations = subscriptionInformations;
	}

	@XmlTransient
	@Override
	@JsonIgnore
	public String getWebServiceMethodName() {
		return webServiceMethodName;
	}

	@XmlTransient
	@Override
	@JsonIgnore
	public String getWebServiceName() {
		return webServiceName;
	}

}
