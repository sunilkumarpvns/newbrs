package com.elitecore.nvsmx.ws.subscription.response;

import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.elitecore.nvsmx.ws.subscription.data.MonetaryBalanceData;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"parameter1", "parameter2","responseCode","responseMessage","monetaryBalanceData"})
@XmlRootElement
public class MonetoryOperationResponse implements WebServiceResponse {

    private Integer responseCode;
    private String responseMessage;
    private String parameter1;
    private String parameter2;

    private String webServiceName;
    private String webServiceMethodName;
    private MonetaryBalanceData monetaryBalanceData;

    public MonetoryOperationResponse(){}

    public MonetoryOperationResponse(Integer responseCode, String responseMessage, MonetaryBalanceData monetaryBalanceData, String parameter1, String parameter2, String webServiceName, String webServiceMethodName) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.monetaryBalanceData = monetaryBalanceData;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.webServiceName = webServiceName;
        this.webServiceMethodName = webServiceMethodName;
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

    public Integer getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public MonetaryBalanceData getMonetaryBalanceData() {
        return monetaryBalanceData;
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

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public void setMonetaryBalanceData(MonetaryBalanceData monetaryBalanceData) {
        this.monetaryBalanceData = monetaryBalanceData;
    }
}