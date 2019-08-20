package com.elitecore.nvsmx.ws.subscription.response;

import com.elitecore.corenetvertex.pm.service.Service;

public class ProductOfferServicePkgRelData {
    private String id;
    private String serviceId;
    private String serviceName;
    private String rncPackageId;
    private String rncPackageName;

    public ProductOfferServicePkgRelData(String id, String serviceId, String serviceName, String rncPackageId, String rncPackageName) {
        this.id = id;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.rncPackageId = rncPackageId;
        this.rncPackageName = rncPackageName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceId() { return serviceId; }

    public void setServiceId(String serviceId) { this.serviceId = serviceId; }

    public String getServiceName() { return serviceName; }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getRncPackageId() {
        return rncPackageId;
    }

    public void setRncPackageId(String rncPackageId) {
        this.rncPackageId = rncPackageId;
    }

    public String getRncPackageName() {
        return rncPackageName;
    }

    public void setRncPackageName(String rncPackageName) {
        this.rncPackageName = rncPackageName;
    }
}
