package com.elitecore.nvsmx.ws.packagemanagement.response;

import com.elitecore.corenetvertex.core.validator.Reason;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
@XmlRootElement(name="packageResponse")
public class PackageManagementResponse {
	private Integer responseCode;
	private String responseMessage;
	private List<Reason> packages;
	private String parameter1;
	private String parameter2;

	public PackageManagementResponse(){}
	
	public PackageManagementResponse(Integer responseCode, String responseMessage,List<Reason> packages, String parameter1, String parameter2){
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
		this.packages = packages;
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
	}

	@XmlElement(name="responseCode")
	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}

	@XmlElement(name="responseMessage")
	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	@XmlElement(name="parameter1")
	public String getParameter1() {
		return parameter1;
	}

	public void setParameter1(String parameter1) {
		this.parameter1 = parameter1;
	}

	@XmlElement(name="parameter2")
	public String getParameter2() {
		return parameter2;
	}

	public void setParameter2(String parameter2) {
		this.parameter2 = parameter2;
	}

	@XmlElement(name="packages")
	public List<Reason> getPackages() {
		return packages;
	}

	public void setPackages(List<Reason> packages) {
		this.packages = packages;
	}
}
