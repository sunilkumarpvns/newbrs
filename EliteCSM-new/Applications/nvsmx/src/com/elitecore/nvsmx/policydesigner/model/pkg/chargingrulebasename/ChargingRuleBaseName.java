package com.elitecore.nvsmx.policydesigner.model.pkg.chargingrulebasename;

/**
 * This class is used to display charging rule base name information in session
 * Created by dhyani on 10/5/17.
 */
public class ChargingRuleBaseName {

    private String name;
    private String monitoringKey;
    private String serviceType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMonitoringKey() {
        return monitoringKey;
    }

    public void setMonitoringKey(String monitoringKey) {
        this.monitoringKey = monitoringKey;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
}

