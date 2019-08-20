package com.elitecore.nvsmx.ws.subscription.response;

import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.elitecore.nvsmx.ws.subscriberprovisioning.data.SubscriberProfile;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlType(propOrder = {"parameter1", "parameter2", "responseCode", "responseMessage", "subscriberProfile"})
@XmlRootElement
public class ChangeBillDayResponse implements WebServiceResponse {

    private String webServiceMethodName;
    private String webServiceName;
    private String parameter1;
    private String parameter2;
    private Integer responseCode;
    private String responseMessage;
    private List<SubscriberProfile> subscriberProfile;

    public ChangeBillDayResponse() {
    }

    public ChangeBillDayResponse(Integer responseCode, String responseMessage, List<SubscriberProfile> subscriberProfile, String parameter1, String parameter2, String webServiceName, String webServiceMethodName) {
        this.webServiceMethodName = webServiceMethodName;
        this.webServiceName = webServiceName;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.subscriberProfile = subscriberProfile;
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

    public List<SubscriberProfile> getSubscriberProfile() {
        return subscriberProfile;
    }

    public void setSubscriberProfile(List<SubscriberProfile> subscriberProfile) {
        this.subscriberProfile = subscriberProfile;
    }
}