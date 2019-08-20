package com.elitecore.nvsmx.ws.resetusage.response;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement
public class ResetBillingCycleResponse  implements WebServiceResponse {

	private Integer responseCode;
	private String responseMessage;
	private String parameter1;
	private String parameter2;
	private String webServiceName;
	private String webServiceMethodName;

	public ResetBillingCycleResponse(){}
	public ResetBillingCycleResponse(Integer responseCode, String responseMessage,
									 String parameter1, String parameter2,String webServiceName, String webServiceMethodName) {
		super();
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
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

	@XmlTransient
	@Override
	@JsonIgnore
	public String getWebServiceName() {
		return webServiceName;
	}

	@XmlTransient
	@Override
	@JsonIgnore
	public String getWebServiceMethodName() {
		return webServiceMethodName;
	}

	public String toString(){
		ToStringBuilder toStringBuilder = new ToStringBuilder(this,
				ToStringStyle.CUSTOM_TO_STRING_STYLE).append("Response Code",responseCode)
				.append("Response Message", responseMessage)
				.append("Parameter1", parameter1)
				.append("Parameter2", parameter2);
		return toStringBuilder.toString();
	}
}