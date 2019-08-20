package com.elitecore.nvsmx.ws.subscription.response;

import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.elitecore.nvsmx.ws.subscription.data.BoDPackageData;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

@XmlRootElement
public class BoDQueryResponse implements WebServiceResponse{

	private Integer responseCode;
	private String responseMessage;
	private String parameter1;
	private String parameter2;

	private List<BoDPackageData> bodPackages;
	private String webServiceName;
	private String webServiceMethodName;

	public BoDQueryResponse(){}
	public BoDQueryResponse(Integer code, String responseMessage,
							List<BoDPackageData> bodPackages ,
							String parameter1, String parameter2, String webServiceName, String webServiceMethodName) {
		this.responseCode = code;
		this.responseMessage = responseMessage;
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
		this.bodPackages = bodPackages;
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
	public List<BoDPackageData> getBodPackages() {
		return bodPackages;
	}
	public void setBodPackages(List<BoDPackageData> bodPackages) {
		this.bodPackages = bodPackages;
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

