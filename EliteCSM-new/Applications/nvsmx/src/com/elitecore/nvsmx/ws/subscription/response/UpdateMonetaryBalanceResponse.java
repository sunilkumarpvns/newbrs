package com.elitecore.nvsmx.ws.subscription.response;

import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.elitecore.nvsmx.ws.subscription.data.UpdatedMonetaryBalanceData;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"parameter1", "parameter2","responseCode","responseMessage","monetaryBalanceData"})
@XmlRootElement
public class UpdateMonetaryBalanceResponse implements WebServiceResponse {

    private String webServiceMethodName;
    private String webServiceName;
    private String parameter1;
    private String parameter2;
    private Integer responseCode;
    private String responseMessage;
    private UpdatedMonetaryBalanceData monetaryBalanceData;

    public UpdateMonetaryBalanceResponse() {
    }

    public UpdateMonetaryBalanceResponse(Integer responseCode, String responseMessage, UpdatedMonetaryBalanceData monetaryBalanceData, String parameter1, String parameter2, String webServiceName, String webServiceMethodName) {
        this.webServiceMethodName = webServiceMethodName;
        this.webServiceName = webServiceName;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.monetaryBalanceData = monetaryBalanceData;
    }

    @Override
    @XmlTransient
    @JsonIgnore
    public String getWebServiceMethodName() {
        return webServiceMethodName;
    }

    @Override
    @XmlTransient
    @JsonIgnore
    public String getWebServiceName() {
        return webServiceName;
    }

    @Override
    public String getParameter1() {
        return parameter1;
    }

    @Override
    public String getParameter2() {
        return parameter2;
    }

    @Override
    public Integer getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public UpdatedMonetaryBalanceData getMonetaryBalanceData() {
        return monetaryBalanceData;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
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

    public void setMonetaryBalanceData(UpdatedMonetaryBalanceData monetaryBalanceData) {
        this.monetaryBalanceData = monetaryBalanceData;
    }
}
