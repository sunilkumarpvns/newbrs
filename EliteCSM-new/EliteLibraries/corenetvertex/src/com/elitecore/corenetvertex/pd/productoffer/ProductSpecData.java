package com.elitecore.corenetvertex.pd.productoffer;


import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.DoubleToStringAdapter;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.TimestampToStringAdapter;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Entity(name= "com.elitecore.corenetvertex.pd.productoffer.ProductSpecData")
@Table(name="TBLM_PRODUCT_OFFER")
public class ProductSpecData extends ResourceData implements Serializable{

    @SerializedName(FieldValueConstants.NAME)private String name;
    @SerializedName(FieldValueConstants.DESCRIPTION)private String description;
    @SerializedName(FieldValueConstants.TYPE)private String type;
    @SerializedName(FieldValueConstants.MODE)private String packageMode;
    @SerializedName(FieldValueConstants.VALIDITIY_PERIOD)private Integer validityPeriod;
    @SerializedName(FieldValueConstants.VALIDITY_PERIOD_UNIT)private String validityPeriodUnit = ValidityPeriodUnit.DAY.name();
    @SerializedName(FieldValueConstants.SUBSCRIPTION_PRICE) @XmlJavaTypeAdapter(DoubleToStringAdapter.class) private Double subscriptionPrice;
    @SerializedName(FieldValueConstants.BALANCE) @XmlJavaTypeAdapter(DoubleToStringAdapter.class) private Double balance;
    @SerializedName(FieldValueConstants.SUBSCRIPTION_CURRENCY)private String subscriptionCurrency;
    @XmlJavaTypeAdapter(TimestampToStringAdapter.class) private Timestamp availabilityStartDate;
    @XmlJavaTypeAdapter(TimestampToStringAdapter.class) private Timestamp availabilityEndDate;
    @SerializedName(FieldValueConstants.PARAM1)private String param1;
    @SerializedName(FieldValueConstants.PARAM2)private String param2;
    private List<ProductSpecServicePkgRelData> productOfferServicePkgRelDataList;
    private PkgData dataServicePkgData;

    public ProductSpecData() {
        this.productOfferServicePkgRelDataList = new ArrayList<>();
    }

    @Column(name="NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="TYPE")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name="PACKAGE_MODE")
    public String getPackageMode() {
        return packageMode;
    }

    public void setPackageMode(String packageMode) {
        this.packageMode = packageMode;
    }

    public void setStatus(String status) {//NOSONAR
        super.setStatus(status);
    }

    @Column(name="VALIDITY_PERIOD")
    public Integer getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(Integer validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    @Column(name="VALIDITY_PERIOD_UNIT")
    public String getValidityPeriodUnit() {
        return validityPeriodUnit;
    }

    public void setValidityPeriodUnit(String validityPeriodUnit) {
        this.validityPeriodUnit = validityPeriodUnit;
    }

    @Column(name="SUBSCRIPTION_PRICE")
    public Double getSubscriptionPrice() {
        return subscriptionPrice;
    }

    public void setSubscriptionPrice(Double subscriptionPrice) {
        this.subscriptionPrice = subscriptionPrice;
    }

    @Transient
    @Column(name="BALANCE")
    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Transient
    @Column(name="SUBSCRIPTION_CURRENCY")
    public String getSubscriptionCurrency() {
        return subscriptionCurrency;
    }

    public void setSubscriptionCurrency(String subscriptionCurrency) {
        this.subscriptionCurrency = subscriptionCurrency;
    }

    @Column(name="AVAILABILITY_START_DATE")
    public Timestamp getAvailabilityStartDate() {
        return availabilityStartDate;
    }

    public void setAvailabilityStartDate(Timestamp availabilityStartDate) {
        this.availabilityStartDate = availabilityStartDate;
    }

    @Column(name="AVAILABILITY_END_DATE")
    public Timestamp getAvailabilityEndDate() {
        return availabilityEndDate;
    }

    public void setAvailabilityEndDate(Timestamp availabilityEndDate) {
        this.availabilityEndDate = availabilityEndDate;
    }

    @Column(name="PARAM_1")
    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    @Column(name="PARAM_2")
    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }


    @Override
    @Column(name="GROUPS")
    public String getGroups() {
        return super.getGroups();
    }

    @Override
    @Column(name = "STATUS")
    public String getStatus() {
        return super.getStatus();
    }

    @Override
    @Transient
    @JsonIgnore
    public String getResourceName() {
        return getName();
    }


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "productOfferData", orphanRemoval = true, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    public List<ProductSpecServicePkgRelData> getProductOfferServicePkgRelDataList() {
        return productOfferServicePkgRelDataList;
    }

    public void setProductOfferServicePkgRelDataList(List<ProductSpecServicePkgRelData> productOfferServicePkgRelDataList) {
        this.productOfferServicePkgRelDataList = productOfferServicePkgRelDataList;
    }


    @ManyToOne
    @JoinColumn(name = "DATA_SERVICE_PKG_ID")
    @JsonIgnore
    public PkgData getDataServicePkgData() {
        return dataServicePkgData;
    }

    public void setDataServicePkgData(PkgData pkgData) {
        this.dataServicePkgData = pkgData;
    }

    @Transient
    public String getDataServicePkgId(){

        if (getDataServicePkgData() == null) {
            return null;
        }
        return getDataServicePkgData().getId();
    }


    public void setDataServicePkgId(String dataServicePkgId){
        if(Strings.isNullOrBlank(dataServicePkgId)){
            return;
        }
        PkgData pkgData = new PkgData();
        pkgData.setId(dataServicePkgId);
        this.dataServicePkgData = pkgData;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) {
            return false;
        }
        if (!ProductOffer.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final ProductOffer other = (ProductOffer) obj;
        return (getId() == null) ? (other.getId() == null) : getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public JsonObject toJson() {
        JsonObject productSpecJson = new JsonObject();
        productSpecJson.addProperty(FieldValueConstants.NAME,name);
        productSpecJson.addProperty(FieldValueConstants.DESCRIPTION,description);
        productSpecJson.addProperty(FieldValueConstants.TYPE,type);
        productSpecJson.addProperty(FieldValueConstants.MODE,packageMode);
        productSpecJson.addProperty(FieldValueConstants.SUBSCRIPTION_PRICE,subscriptionPrice);
        productSpecJson.addProperty(FieldValueConstants.BALANCE,balance);
        productSpecJson.addProperty(FieldValueConstants.SUBSCRIPTION_CURRENCY,subscriptionCurrency);
        productSpecJson.addProperty(FieldValueConstants.VALIDITIY_PERIOD,validityPeriod);
        productSpecJson.addProperty(FieldValueConstants.VALIDITY_PERIOD_UNIT, validityPeriodUnit);
        productSpecJson.addProperty(FieldValueConstants.AVAILABILITY_START_DATE,availabilityStartDate == null ? "": availabilityStartDate.toLocalDateTime().toString());
        productSpecJson.addProperty(FieldValueConstants.AVAILABILITY_END_DATE,availabilityEndDate == null ? "":availabilityEndDate.toLocalDateTime().toString());
        productSpecJson.addProperty(FieldValueConstants.GROUPS,getGroups());
        productSpecJson.addProperty(FieldValueConstants.STATUS,getStatus());
        productSpecJson.addProperty(FieldValueConstants.PARAM1,param1);
        productSpecJson.addProperty(FieldValueConstants.PARAM2,param2);
        return productSpecJson;
    }
}
