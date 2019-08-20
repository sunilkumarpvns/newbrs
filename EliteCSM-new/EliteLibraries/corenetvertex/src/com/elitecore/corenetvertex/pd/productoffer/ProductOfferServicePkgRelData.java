package com.elitecore.corenetvertex.pd.productoffer;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pd.service.ServiceData;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "TBLM_PRDCT_OFR_SRV_PKG_REL")
@Entity(name="com.elitecore.corenetvertex.pd.productoffer.ProductOfferServicePkgRelData")
public class ProductOfferServicePkgRelData implements Serializable{
    private static final long serialVersionUID = -5594238485050373317L;
    private String id;
    private transient ProductOfferData productOfferData;
    private transient ServiceData serviceData;
    private transient RncPackageData rncPackageData;
    private String rncPackageName;
    private String serviceName;
    private String displayPackageId;

    @Id
    @Column(name="ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PRODUCT_OFFER_ID")
    @JsonIgnore
    public ProductOfferData getProductOfferData() {
        return productOfferData;
    }

    public void setProductOfferData(ProductOfferData productOfferData) {
        this.productOfferData = productOfferData;
    }

    @ManyToOne
    @JoinColumn(name="SERVICE_ID")
    @JsonIgnore
    public ServiceData getServiceData() {
        return serviceData;
    }

    public void setServiceData(ServiceData serviceData) {
        this.serviceData = serviceData;
    }

    @Transient
    public String getProductOfferId() {
        if(getProductOfferData() == null){
            return null;
        }
        return getProductOfferData().getId();
    }

    @ManyToOne
    @JoinColumn(name="RNC_PACKAGE_ID")
    @JsonIgnore
    public RncPackageData getRncPackageData() {
        return rncPackageData;
    }

    public void setRncPackageData(RncPackageData rncPackageData) {
        this.rncPackageData = rncPackageData;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Transient
    public String getRncPackageId() {
        if(getRncPackageData() == null){
            return null;
        }
        return getRncPackageData().getId();
    }

    public void setRncPackageId(String pkgId) {
        if(Strings.isNullOrBlank(pkgId) == false){
            RncPackageData rncPackageData = new RncPackageData();
            rncPackageData.setId(pkgId);
            this.rncPackageData = rncPackageData;
        }
    }

    @Transient
    public String getServiceName() {
        return serviceName;
    }

    public void setRncPackageName(String rncPackageName) {
        this.rncPackageName = rncPackageName;

    }

    @Transient
    public String getRncPackageName() {
        return rncPackageName;
    }

    @Transient
    public String getServiceId() {
        if(getServiceData() == null){
            return null;
        }
        return getServiceData().getId();
    }

    public void setServiceId(String serviceId) {
        if(Strings.isNullOrBlank(serviceId) == false){
            ServiceData serviceData = new ServiceData();
            serviceData.setId(serviceId);
            this.serviceData = serviceData;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductOfferServicePkgRelData)){
            return false;
        }
        ProductOfferServicePkgRelData that = (ProductOfferServicePkgRelData) o;
        return Objects.equals(getProductOfferId(), that.getProductOfferId()) &&
                Objects.equals(getServiceId(), that.getServiceId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductOfferId(), getServiceId());
    }

    public ProductOfferServicePkgRelData copyModel() {
        ProductOfferServicePkgRelData newData = new ProductOfferServicePkgRelData();
        newData.rncPackageData = this.rncPackageData;
        newData.rncPackageName = this.rncPackageName;
        newData.serviceData = this.serviceData;
        newData.serviceName = this.serviceName;
        return newData;
    }

    public void setDisplayPackageId(String displayPackageId) {
        this.displayPackageId = displayPackageId;
    }

    @Transient
    @JsonIgnore
    public String getDisplayPackageId() {
        return displayPackageId;
    }
}