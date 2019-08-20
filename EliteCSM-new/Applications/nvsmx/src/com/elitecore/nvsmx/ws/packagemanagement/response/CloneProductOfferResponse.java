package com.elitecore.nvsmx.ws.packagemanagement.response;

import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name="cloneProductOfferResponse")
public class CloneProductOfferResponse implements WebServiceResponse {

    private Integer responseCode;
    private String responseMessage;
    private String parameter1;
    private String parameter2;
    private String webServiceMethodName;
    private String webServiceName;

    public CloneProductOfferResponse(){}

    public CloneProductOfferResponse(Integer responseCode, String responseMessage, String parameter1, String parameter2, String webServiceName, String webServiceMethodName){
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.webServiceName = webServiceName;
        this.webServiceMethodName = webServiceMethodName;
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
}
