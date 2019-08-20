package com.elitecore.nvsmx.ws.subscription.response;

import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlTransient;

public class MonetaryRechargeSubscriptionResponse implements WebServiceResponse {
    private Integer responseCode;
    private String responseMessage;
    private String parameter1;
    private String parameter2;
    private String subscriberId;
    private String monetaryRechargePlanId;
    private String monetaryRechargePlanName;
    private double previousAvailableBalance;
    private double currentBalance;
    private long previousValidToDate;
    private long newValidToDate;
    private String webServiceName;
    private String webServiceMethodName;

    public MonetaryRechargeSubscriptionResponse(Integer responseCode,
            String responseMessage,
            String parameter1,
            String parameter2,
            String subscriberId,
            String monetaryRechargePlanId,
            String monetaryRechargePlanName,
            double previousAvailableBalance,
            double currentBalance,
            long previousValidToDate,
            long newValidToDate,
            String webServiceName,
            String webServiceMethodName) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.subscriberId = subscriberId;
        this.monetaryRechargePlanId = monetaryRechargePlanId;
        this.monetaryRechargePlanName = monetaryRechargePlanName;
        this.previousAvailableBalance = previousAvailableBalance;
        this.currentBalance = currentBalance;
        this.previousValidToDate = previousValidToDate;
        this.newValidToDate = newValidToDate;
        this.webServiceName = webServiceName;
        this.webServiceMethodName = webServiceMethodName;

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

    public double getPreviousAvailableBalance() {
        return previousAvailableBalance;
    }

    public void setPreviousAvailableBalance(double previousAvailableBalance) {
        this.previousAvailableBalance = previousAvailableBalance;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public long getPreviousValidToDate() {
        return previousValidToDate;
    }

    public void setPreviousValidToDate(long previousValidToDate) {
        this.previousValidToDate = previousValidToDate;
    }

    public long getNewValidToDate() {
        return newValidToDate;
    }

    public void setNewValidToDate(long newValidToDate) {
        this.newValidToDate = newValidToDate;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getMonetaryRechargePlanId() {
        return monetaryRechargePlanId;
    }

    public void setMonetaryRechargePlanId(String monetaryRechargePlanId) {
        this.monetaryRechargePlanId = monetaryRechargePlanId;
    }

    public String getMonetaryRechargePlanName() {
        return monetaryRechargePlanName;
    }

    public void setMonetaryRechargePlanName(String monetaryRechargePlanName) {
        this.monetaryRechargePlanName = monetaryRechargePlanName;
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
