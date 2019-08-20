package com.elitecore.corenetvertex.pm.offer;

import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.pm.service.Service;

import java.io.Serializable;
import java.util.Objects;

public class ProductOfferServicePkgRel implements Serializable{
    private static final long serialVersionUID = -5594238485050373317L;
    private String id;
    private Service serviceData;
    private String rncPackageId;
    private transient PolicyRepository policyRepository;

    public ProductOfferServicePkgRel(String id, Service serviceData, String rncPackageId, PolicyRepository policyRepository) {
        this.id = id;
        this.serviceData = serviceData;
        this.rncPackageId = rncPackageId;
        this.policyRepository = policyRepository;
    }

    public String getId() {
        return id;
    }

    public Service getServiceData() {
        return serviceData;
    }

    public RnCPackage getRncPackageData() {
        return policyRepository.getRnCPackage().byId(rncPackageId);
    }

    public String getRncPackageId() {
        if(getRncPackageData() == null){
            return null;
        }
        return getRncPackageData().getId();
    }

    public String getServiceId() {
        if(getServiceData() == null){
            return null;
        }
        return getServiceData().getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductOfferServicePkgRel)){
            return false;
        }
        ProductOfferServicePkgRel that = (ProductOfferServicePkgRel) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getServiceId(), that.getServiceId()) &&
                Objects.equals(getRncPackageData(), that.getRncPackageId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getServiceId(), getRncPackageId());
    }
}