package com.elitecore.corenetvertex.pd.productoffer;


import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.sm.Replicable;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.DoubleToStringAdapter;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.TimestampToStringAdapter;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OrderBy;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity(name= "com.elitecore.corenetvertex.pd.productoffer.ProductOfferData")
@Table(name="TBLM_PRODUCT_OFFER")
public class ProductOfferData extends ResourceData implements Serializable, Replicable{

    @SerializedName(FieldValueConstants.NAME)private String name;
    @SerializedName(FieldValueConstants.DESCRIPTION)private String description;
    @SerializedName(FieldValueConstants.TYPE)private String type;
    @SerializedName(FieldValueConstants.MODE)private String packageMode;
    @SerializedName(FieldValueConstants.VALIDITIY_PERIOD)private Integer validityPeriod;
    @SerializedName(FieldValueConstants.VALIDITY_PERIOD_UNIT)private String validityPeriodUnit = ValidityPeriodUnit.DAY.name();
    @SerializedName(FieldValueConstants.SUBSCRIPTION_PRICE) @XmlJavaTypeAdapter(DoubleToStringAdapter.class) private Double subscriptionPrice;
    @SerializedName(FieldValueConstants.CREDIT_BALANCE) @XmlJavaTypeAdapter(DoubleToStringAdapter.class) private Double creditBalance;
    @SerializedName(FieldValueConstants.BALANCE) @XmlJavaTypeAdapter(DoubleToStringAdapter.class) private Double balance;
    @XmlJavaTypeAdapter(TimestampToStringAdapter.class) private Timestamp availabilityStartDate;
    @XmlJavaTypeAdapter(TimestampToStringAdapter.class) private Timestamp availabilityEndDate;
    @SerializedName(FieldValueConstants.FNF_OFFER) private Boolean fnFOffer = CommonStatusValues.DISABLE.isBooleanValue();
    @SerializedName(FieldValueConstants.PARAM1)private String param1;
    @SerializedName(FieldValueConstants.PARAM2)private String param2;
    @SerializedName(FieldValueConstants.CURRENCY)private String currency;
    private transient List<ProductOfferServicePkgRelData> productOfferServicePkgRelDataList;
    private transient List<ProductOfferAutoSubscriptionRelData> productOfferAutoSubscriptionRelDatas;
    private PkgData dataServicePkgData;
    private String emailTemplateId;
    private String smsTemplateId;
    private transient NotificationTemplateData emailNotificationTemplateData;
    private transient NotificationTemplateData smsNotificationTemplateData;

    public ProductOfferData() {

        this.productOfferServicePkgRelDataList = new ArrayList<>();
        this.productOfferAutoSubscriptionRelDatas = new ArrayList<>();
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

    @Column(name = "CREDIT_BALANCE")
    public Double getCreditBalance() {
        return creditBalance;
    }

    public void setCreditBalance(Double creditBalance) {
        this.creditBalance = creditBalance;
    }

    @Transient
    @JsonIgnore
    public Double getBalance() {
        return null;
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

    @Column(name = "IS_FNF_OFFER")
    public Boolean isFnFOffer() {
        return fnFOffer;
    }

    public void setFnFOffer(Boolean fnFOffer) {
        if(fnFOffer != null) {
            this.fnFOffer = fnFOffer;
        }
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

    @Column(name="CURRENCY")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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
    public List<ProductOfferServicePkgRelData> getProductOfferServicePkgRelDataList() {
        return productOfferServicePkgRelDataList;
    }

    public void setProductOfferServicePkgRelDataList(List<ProductOfferServicePkgRelData> productOfferServicePkgRelDataList) {
        if(Objects.isNull(productOfferServicePkgRelDataList)){
            return;
        }
        this.productOfferServicePkgRelDataList = productOfferServicePkgRelDataList;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DATA_SERVICE_PKG_ID")
    @JsonIgnore
    public PkgData getDataServicePkgData() {
        return dataServicePkgData;
    }


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentProductOfferData", orphanRemoval = true, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy(clause="ORDER_NO asc")
    public List<ProductOfferAutoSubscriptionRelData> getProductOfferAutoSubscriptionRelDatas() {
        return productOfferAutoSubscriptionRelDatas;
    }

    public void setProductOfferAutoSubscriptionRelDatas(List<ProductOfferAutoSubscriptionRelData> productOfferAutoSubscriptionRelDatas) {
        if(Objects.isNull(productOfferAutoSubscriptionRelDatas)){
            return;
        }
        this.productOfferAutoSubscriptionRelDatas = productOfferAutoSubscriptionRelDatas;
    }


    public void setDataServicePkgData(PkgData pkgData) {
        this.dataServicePkgData = pkgData;
    }

    @Transient
    public String getDataServicePkgId(){
            return dataServicePkgData==null?null:dataServicePkgData.getId();
    }

    public void setDataServicePkgId(String dataServicePkgId){
        if(Strings.isNullOrBlank(dataServicePkgId)){
            return;
        }
        PkgData pkgData = new PkgData();
        pkgData.setId(dataServicePkgId);
        this.dataServicePkgData = pkgData;
    }

    @Transient
    public String getEmailTemplateId() {
        return emailTemplateId;
    }

    public void setEmailTemplateId(String emailTemplateId) {
        this.emailTemplateId = emailTemplateId;
    }

    @Transient
    public String getSmsTemplateId() {
        return smsTemplateId;
    }

    public void setSmsTemplateId(String smsTemplateId) {
        this.smsTemplateId = smsTemplateId;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EMAIL_TEMPLATE_ID")
    @JsonIgnore
    public NotificationTemplateData getEmailNotificationTemplateData() {
        return emailNotificationTemplateData;
    }

    public void setEmailNotificationTemplateData(NotificationTemplateData emailNotificationTemplateData) {
        if(emailNotificationTemplateData != null){
            setEmailTemplateId(emailNotificationTemplateData.getId());
        }
        this.emailNotificationTemplateData = emailNotificationTemplateData;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SMS_TEMPLATE_ID")
    @JsonIgnore
    public NotificationTemplateData getSmsNotificationTemplateData() {
        return smsNotificationTemplateData;
    }

    public void setSmsNotificationTemplateData(NotificationTemplateData smsNotificationTemplateData) {
        if(smsNotificationTemplateData != null){
            setSmsTemplateId(smsNotificationTemplateData.getId());
        }
        this.smsNotificationTemplateData = smsNotificationTemplateData;
    }

    @Override
    public JsonObject toJson() {
        JsonObject productOfferJson = new JsonObject();
        productOfferJson.addProperty(FieldValueConstants.NAME, name);
        productOfferJson.addProperty(FieldValueConstants.DESCRIPTION, description);
        productOfferJson.addProperty(FieldValueConstants.TYPE, type);
        productOfferJson.addProperty(FieldValueConstants.MODE, packageMode);
        productOfferJson.addProperty(FieldValueConstants.CURRENCY, currency);
        productOfferJson.addProperty(FieldValueConstants.SUBSCRIPTION_PRICE, subscriptionPrice);
        productOfferJson.addProperty(FieldValueConstants.CREDIT_BALANCE,creditBalance);
        if (dataServicePkgData != null) {
            productOfferJson.addProperty(FieldValueConstants.DATA_SERVICE_PACKAGE, dataServicePkgData.getName());
        }
        if (emailNotificationTemplateData != null) {
            productOfferJson.addProperty(FieldValueConstants.EMAIL_TEMPLATE, emailNotificationTemplateData.getName());
        }
        if (smsNotificationTemplateData != null) {
            productOfferJson.addProperty(FieldValueConstants.SMS_TEMPLATE, smsNotificationTemplateData.getName());
        }
        if (productOfferServicePkgRelDataList != null && productOfferServicePkgRelDataList.isEmpty() == false) {
            for (ProductOfferServicePkgRelData productOfferServicePkgRelData : productOfferServicePkgRelDataList) {
                JsonObject productOfferServicePkgRelDataListJson = new JsonObject();
                productOfferServicePkgRelDataListJson.addProperty(productOfferServicePkgRelData.getServiceData().getName(), productOfferServicePkgRelData.getRncPackageData().getName());
                productOfferJson.add(FieldValueConstants.RNC_PACKAGE +"-"+productOfferServicePkgRelData.getRncPackageData().getName() + "-" + productOfferServicePkgRelData.getServiceData().getName(), productOfferServicePkgRelDataListJson);
            }
        }

        if (CollectionUtils.isNotEmpty(productOfferAutoSubscriptionRelDatas)) {
            for (ProductOfferAutoSubscriptionRelData autoSubscriptionRelData : productOfferAutoSubscriptionRelDatas) {
                JsonObject autoSubscription = new JsonObject();
                String advanceCondition = autoSubscriptionRelData.getAdvanceCondition();
                autoSubscription.addProperty(FieldValueConstants.ADVANCE_CONDITION, Objects.nonNull(advanceCondition) ? advanceCondition : "");
                autoSubscription.addProperty(FieldValueConstants.PRODUCT_OFFER, autoSubscriptionRelData.getAddOnProductOfferData().getName());
                productOfferJson.add(FieldValueConstants.AUTO_SUBSCRIPTION + "-" + autoSubscriptionRelData.getAddOnProductOfferData().getName() , autoSubscription);
            }

        }
        productOfferJson.addProperty(FieldValueConstants.VALIDITIY_PERIOD, validityPeriod);
        productOfferJson.addProperty(FieldValueConstants.VALIDITY_PERIOD_UNIT, validityPeriodUnit);
        productOfferJson.addProperty(FieldValueConstants.AVAILABILITY_START_DATE, availabilityStartDate == null ? "" : availabilityStartDate.toLocalDateTime().toString());
        productOfferJson.addProperty(FieldValueConstants.AVAILABILITY_END_DATE, availabilityEndDate == null ? "" : availabilityEndDate.toLocalDateTime().toString());
        productOfferJson.addProperty(FieldValueConstants.GROUPS, getGroupNames());
        productOfferJson.addProperty(FieldValueConstants.STATUS, getStatus());
        productOfferJson.addProperty(FieldValueConstants.FNF_OFFER, fnFOffer);
        productOfferJson.addProperty(FieldValueConstants.PARAM1, param1);
        productOfferJson.addProperty(FieldValueConstants.PARAM2, param2);
        return productOfferJson;
    }

    @Transient
    @XmlTransient
    @JsonIgnore
    @Override
    public  String getHierarchy(){ return getId() + "<br>" + getName(); }

    @Override
    public ProductOfferData copyModel(){
        ProductOfferData newData = new ProductOfferData();
        newData.availabilityEndDate = this.availabilityEndDate;
        newData.availabilityStartDate = this.availabilityStartDate;
        newData.description = this.description;
        newData.packageMode = PkgMode.DESIGN.name();
        newData.currency = this.currency;
        newData.param1 = this.param1;
        newData.param2 = this.param2;
        newData.subscriptionPrice = this.subscriptionPrice;
        newData.creditBalance=this.creditBalance;
        newData.type = this.type;
        newData.validityPeriod = this.validityPeriod;
        newData.validityPeriodUnit = this.validityPeriodUnit;

        newData.dataServicePkgData = this.dataServicePkgData;
        newData.emailNotificationTemplateData = this.emailNotificationTemplateData;
        newData.smsNotificationTemplateData = this.smsNotificationTemplateData;
        newData.fnFOffer = this.fnFOffer;
        newData.setGroupNames(this.getGroupNames());
        newData.setGroups(this.getGroups());

        List<ProductOfferAutoSubscriptionRelData> productOfferAutoSubscriptionRelDatasList = Collectionz.newArrayList();
        for (ProductOfferAutoSubscriptionRelData productOfferAutoSubscriptionRelData : this.productOfferAutoSubscriptionRelDatas) {
            ProductOfferAutoSubscriptionRelData productOfferAutoSubscriptionRelDataCopy = productOfferAutoSubscriptionRelData.copyModel();
            productOfferAutoSubscriptionRelDataCopy.setParentProductOfferData(newData);
            productOfferAutoSubscriptionRelDataCopy.setId(null);
            productOfferAutoSubscriptionRelDatasList.add(productOfferAutoSubscriptionRelDataCopy);
        }
        newData.setProductOfferAutoSubscriptionRelDatas(productOfferAutoSubscriptionRelDatasList);

        List<ProductOfferServicePkgRelData> productOfferServicePkgRelDatas = Collectionz.newArrayList();
        for (ProductOfferServicePkgRelData productOfferServicePkgRelData : this.productOfferServicePkgRelDataList) {
            ProductOfferServicePkgRelData productOfferServicePkgRelDataCopy = productOfferServicePkgRelData.copyModel();
            productOfferServicePkgRelDataCopy.setId(null);
            productOfferServicePkgRelDataCopy.setProductOfferData(newData);
            productOfferServicePkgRelDatas.add(productOfferServicePkgRelDataCopy);
        }
        newData.setProductOfferServicePkgRelDataList(productOfferServicePkgRelDatas);
        return newData;
    }

    @Override
    public void setResourceName(String name){
        setName(name);
    }

}
