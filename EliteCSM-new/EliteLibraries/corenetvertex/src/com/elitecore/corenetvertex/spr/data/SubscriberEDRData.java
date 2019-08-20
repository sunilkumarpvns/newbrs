package com.elitecore.corenetvertex.spr.data;

import com.elitecore.corenetvertex.spr.data.SPRInfo;

/**
 * @author Prakashkumar Pala
 * @since 22-Oct-2018
 * EDR data object for Subscriber
 */
public class SubscriberEDRData {
    private SPRInfo sprInfo;
    private String requestIpAddress;
    private String operation;
    private String action;
    private String currency;

    public SPRInfo getSprInfo() {
        return sprInfo;
    }

    public void setSprInfo(SPRInfo sprInfo) {
        this.sprInfo = sprInfo;
    }

    public String getRequestIpAddress() {
        return requestIpAddress;
    }

    public void setRequestIpAddress(String requestIpAddress) {
        this.requestIpAddress = requestIpAddress;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
