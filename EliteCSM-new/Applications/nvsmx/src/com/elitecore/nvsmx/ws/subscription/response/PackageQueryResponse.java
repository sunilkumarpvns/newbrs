package com.elitecore.nvsmx.ws.subscription.response;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.elitecore.nvsmx.ws.subscription.data.PackageInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by aditya on 9/6/16.
 */
@XmlRootElement
public class PackageQueryResponse implements WebServiceResponse {

    private Integer responseCode;
    private String responseMessage;
    private String parameter1;
    private String parameter2;
	private String webServiceName;
	private String webServiceMethodName;

    private List<PackageInfo> packages;

    public PackageQueryResponse(){}
    public PackageQueryResponse(Integer responseCode, String responseMessage, String parameter1, String parameter2, List<PackageInfo> packages
   		,String webServiceName, String webServiceMethodName) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.packages = packages;
		this.webServiceName = webServiceName;
		this.webServiceMethodName = webServiceMethodName;

    }

    public Integer getResponseCode() {
        return responseCode;
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

    public List<PackageInfo> getPackages() {
        return packages;
    }

    public void setPackages(List<PackageInfo> packages) {
        this.packages = packages;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
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
