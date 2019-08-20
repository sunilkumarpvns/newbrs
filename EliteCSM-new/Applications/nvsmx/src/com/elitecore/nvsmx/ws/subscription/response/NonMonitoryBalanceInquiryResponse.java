package com.elitecore.nvsmx.ws.subscription.response;

import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.elitecore.nvsmx.ws.subscription.data.RnCSubscriptionInformatonData;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlType(propOrder={"parameter1", "parameter2","responseCode","responseMessage","subscriptionInformations"})
@XmlRootElement
public class NonMonitoryBalanceInquiryResponse implements WebServiceResponse {
    private Integer responseCode;
    private String responseMessage;
    private String parameter1;
    private String parameter2;
    private String webServiceName;
    private String webServiceMethodName;

    private List<RnCSubscriptionInformatonData> subscriptionInformations;

    public NonMonitoryBalanceInquiryResponse() {
    }

    public NonMonitoryBalanceInquiryResponse(Integer responseCode, String responseMessage, List<RnCSubscriptionInformatonData> subscriptionInformations,
                                             String parameter1, String parameter2, String webServiceName, String webServiceMethodName) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.webServiceName = webServiceName;
        this.webServiceMethodName = webServiceMethodName;
        this.subscriptionInformations = subscriptionInformations;
    }

    @Override
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

    @Override
    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    @Override
    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    public void setWebServiceName(String webServiceName) {
        this.webServiceName = webServiceName;
    }

    public void setWebServiceMethodName(String webServiceMethodName) {
        this.webServiceMethodName = webServiceMethodName;
    }

    public List<RnCSubscriptionInformatonData> getSubscriptionInformations() {
        return subscriptionInformations;
    }

    public void setSubscriptionInformations(List<RnCSubscriptionInformatonData> subscriptionInformations) {
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
