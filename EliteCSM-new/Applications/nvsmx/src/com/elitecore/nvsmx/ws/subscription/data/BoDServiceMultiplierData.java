package com.elitecore.nvsmx.ws.subscription.data;

public class BoDServiceMultiplierData {
    private String serviceId;
    private String serviceName;
    private Double multiplier;

    public BoDServiceMultiplierData(String serviceId, String serviceName, Double multiplier) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.multiplier = multiplier;
    }

    public String getServiceId() {
        return serviceId;
    }

    public Double getMultiplier() {
        return multiplier;
    }

    public String getServiceName() {
        return serviceName;
    }
}
