package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

public class ConcurrencyIMDGHandler {

    private String handlerName;
    private String ruleset;
    private String imdgFieldName;
    private int orderNumber;
    private String isAdditional;
    private String isHandlerEnabled = "false";

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public String getRuleset() {
        return ruleset;
    }

    public void setRuleset(String ruleset) {
        this.ruleset = ruleset;
    }

    public String getImdgFieldName() {
        return imdgFieldName;
    }

    public void setImdgFieldName(String imdgFieldName) {
        this.imdgFieldName = imdgFieldName;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getIsAdditional() {
        return isAdditional;
    }

    public void setIsAdditional(String isAdditional) {
        this.isAdditional = isAdditional;
    }

    public String getIsHandlerEnabled() {
        return isHandlerEnabled;
    }

    public void setIsHandlerEnabled(String isHandlerEnabled) {
        this.isHandlerEnabled = isHandlerEnabled;
    }
}
