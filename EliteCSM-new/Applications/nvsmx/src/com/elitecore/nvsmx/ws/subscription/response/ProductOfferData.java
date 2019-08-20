package com.elitecore.nvsmx.ws.subscription.response;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;

import java.sql.Timestamp;
import java.util.List;

public class ProductOfferData {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String description;
    private PkgType type;
    private PkgMode packageMode;
    private String currency;
    private Integer validityPeriod;
    private ValidityPeriodUnit validityPeriodUnit;
    private double subscriptionPrice;
    private Double creditBalance;
    private Timestamp availabilityStartDate;
    private Timestamp availabilityEndDate;
    private boolean isFnFOffer;
    private String param1;
    private String param2;
    private PkgStatus status;
    private List<ProductOfferServicePkgRelData> offerServicePkgRel;
    private List<ProductOfferAutoSubscriptionData> productOfferAutoSubscriptions;
    private String dataServicePkgId;
    private String dataServicePkgName;
    private List<String> groupIds;
    private PolicyStatus policyStatus;


    public ProductOfferData(String id, String name, String description, PkgType type, PkgMode packageMode, String currency,
                            Integer validityPeriod, ValidityPeriodUnit validityPeriodUnit, double subscriptionPrice,
                            Double creditBalance, Timestamp availabilityStartDate, Timestamp availabilityEndDate,
                            boolean isFnFOffer, String param1, String param2, PkgStatus status,
                            List<ProductOfferServicePkgRelData> offerServicePkgRel,
                            List<ProductOfferAutoSubscriptionData> productOfferAutoSubscriptions, String dataServicePkgId,
                            String dataServicePkgName, List<String> groupIds, PolicyStatus policyStatus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.packageMode = packageMode;
        this.currency=currency;
        this.validityPeriod = validityPeriod;
        this.validityPeriodUnit = validityPeriodUnit;
        this.subscriptionPrice = subscriptionPrice;
        this.creditBalance = creditBalance;
        this.availabilityStartDate = availabilityStartDate;
        this.availabilityEndDate = availabilityEndDate;
        this.isFnFOffer = isFnFOffer;
        this.param1 = param1;
        this.param2 = param2;
        this.status = status;
        this.offerServicePkgRel = offerServicePkgRel;
        this.productOfferAutoSubscriptions = productOfferAutoSubscriptions;
        this.dataServicePkgId = dataServicePkgId;
        this.dataServicePkgName = dataServicePkgName;
        this.groupIds = groupIds;
        this.policyStatus = policyStatus;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PkgType getType() {
        return type;
    }

    public void setType(PkgType type) {
        this.type = type;
    }

    public String getCurrency() { return currency; }

    public void setCurrency(String currency) { this.currency = currency; }

    public PkgMode getPackageMode() {
        return packageMode;
    }

    public void setPackageMode(PkgMode packageMode) {
        this.packageMode = packageMode;
    }



    public Integer getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(Integer validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public ValidityPeriodUnit getValidityPeriodUnit() {
        return validityPeriodUnit;
    }

    public void setValidityPeriodUnit(ValidityPeriodUnit validityPeriodUnit) {
        this.validityPeriodUnit = validityPeriodUnit;
    }

    public double getSubscriptionPrice() {
        return subscriptionPrice;
    }

    public void setSubscriptionPrice(double subscriptionPrice) {
        this.subscriptionPrice = subscriptionPrice;
    }

    public Double getCreditBalance() {
        return creditBalance;
    }

    public void setCreditBalance(Double creditBalance) {
        this.creditBalance = creditBalance;
    }

    public Timestamp getAvailabilityStartDate() {
        return availabilityStartDate;
    }

    public void setAvailabilityStartDate(Timestamp availabilityStartDate) {
        this.availabilityStartDate = availabilityStartDate;
    }

    public Timestamp getAvailabilityEndDate() {
        return availabilityEndDate;
    }

    public void setAvailabilityEndDate(Timestamp availabilityEndDate) {
        this.availabilityEndDate = availabilityEndDate;
    }

    public boolean isFnFOffer() { return isFnFOffer; }

    public void setFnFOffer(boolean fnFOffer) { isFnFOffer = fnFOffer; }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public PkgStatus getStatus() {
        return status;
    }

    public void setStatus(PkgStatus status) {
        this.status = status;
    }

    public List<ProductOfferServicePkgRelData> getOfferServicePkgRel() {
        return offerServicePkgRel;
    }

    public void setOfferServicePkgRel(List<ProductOfferServicePkgRelData> offerServicePkgRel) {
        this.offerServicePkgRel = offerServicePkgRel;
    }

    public List<ProductOfferAutoSubscriptionData> getProductOfferAutoSubscriptions() {
        return productOfferAutoSubscriptions;
    }

    public void setProductOfferAutoSubscriptions(List<ProductOfferAutoSubscriptionData> productOfferAutoSubscriptions) {
        this.productOfferAutoSubscriptions = productOfferAutoSubscriptions;
    }

    public String getDataServicePkgId() {
        return dataServicePkgId;
    }

    public void setDataServicePkgId(String dataServicePkgId) {
        this.dataServicePkgId = dataServicePkgId;
    }

    public String getDataServicePkgName() {
        return dataServicePkgName;
    }

    public void setDataServicePkgName(String dataServicePkgName) {
        this.dataServicePkgName = dataServicePkgName;
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<String> groupIds) {
        this.groupIds = groupIds;
    }

    public PolicyStatus getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(PolicyStatus policyStatus) {
        this.policyStatus = policyStatus;
    }

}
