package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import java.util.ArrayList;
import java.util.List;

public class StatefulProxySequentialHandler {

    private List<StatefulProHandlerData> sequentialHandlerEntryData = new ArrayList<>();
    private String handlerName;
    private int orderNumber;
    private String isAdditional;
    private String isHandlerEnabled = "false";

    public List<StatefulProHandlerData> getSequentialHandlerEntryData() {
        return sequentialHandlerEntryData;
    }

    public void setSequentialHandlerEntryData(List<StatefulProHandlerData> sequentialHandlerEntryData) {
        this.sequentialHandlerEntryData = sequentialHandlerEntryData;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
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
