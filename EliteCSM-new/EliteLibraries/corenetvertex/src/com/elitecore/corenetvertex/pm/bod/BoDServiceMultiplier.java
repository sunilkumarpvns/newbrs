package com.elitecore.corenetvertex.pm.bod;

import java.io.Serializable;

public class BoDServiceMultiplier implements Serializable {
    private static final long serialVersionUID = 1L;
    private String dataServiceTypeID;
    private String dataServictyTypeName;
    private double multiplier;
    private long serviceIdentifier;

    public BoDServiceMultiplier(String dataServiceTypeID, String dataServictyTypeName, double multiplier, long serviceIdentifier) {
        this.dataServiceTypeID = dataServiceTypeID;
        this.dataServictyTypeName = dataServictyTypeName;
        this.multiplier = multiplier;
        this.serviceIdentifier = serviceIdentifier;
    }

    public String getDataServiceTypeID() {
        return dataServiceTypeID;
    }

    public String getDataServicetyTypeName() {
        return dataServictyTypeName;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public long getServiceIdentifier() {
        return serviceIdentifier;
    }
}