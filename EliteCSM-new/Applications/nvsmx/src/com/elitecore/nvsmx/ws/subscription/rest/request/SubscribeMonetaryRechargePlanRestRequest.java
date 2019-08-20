package com.elitecore.nvsmx.ws.subscription.rest.request;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SubscribeMonetaryRechargePlanRestRequest {
    private String subscriberId;
    private String alternateId;
    private String cui;
    private String monetaryRechargePlanId;
    private String monetaryRechargePlanName;
    private String updateAction;
    private String updateBalanceIndication;
    private String price;



    private long expiryDate;

    public long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getAlternateId() {
        return alternateId;
    }

    public void setAlternateId(String alternateId) {
        this.alternateId = alternateId;
    }

    public String getCui() {
        return cui;
    }

    public void setCui(String cui) {
        this.cui = cui;
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

    public String getUpdateAction() {
        return updateAction;
    }

    public void setUpdateAction(String updateAction) {
        this.updateAction = updateAction;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUpdateBalanceIndication() {
        return updateBalanceIndication;
    }

    public void setUpdateBalanceIndication(String updateBalanceIndication) {
        this.updateBalanceIndication = updateBalanceIndication;
    }
}
