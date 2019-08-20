package com.elitecore.corenetvertex.spr.ddf;

import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SubscriptionMetadata;

public class SubscriptionParameter {
    private SPRInfo sprInfo;
    private String parentId;
    private String productOfferId;
    private String productOfferName;
    private String bodPackageId;
    private String bodPackageName;
    private Integer subscriptionStatusValue;
    private Long startTime;
    private Long endTime;
    private Integer priority;
    private String param1;
    private String param2;
    private Integer billDay;
    private double subscriptionPrice;
    private MonetaryBalance monetaryBalance;
    private SubscriptionMetadata metadata;

    /**
     * @param sprInfo
     * @param parentId
     * @param subscriptionStatusValue
     * @param productOfferName
     * @param startTime
     * @param endTime
     * @param priority
     */
    public SubscriptionParameter(SPRInfo sprInfo,
                                 String parentId,
                                 String productOfferId,
                                 Integer subscriptionStatusValue,
                                 String productOfferName,
                                 String bodPackageId,
                                 String bodPackageName,
                                 Long startTime,
                                 Long endTime,
                                 Integer priority,
                                 String param1,
                                 String param2,
                                 Integer billDay,
                                 SubscriptionMetadata metadata) {
        this.sprInfo = sprInfo;
        this.parentId = parentId;
        this.productOfferId = productOfferId;
        this.subscriptionStatusValue = subscriptionStatusValue;
        this.productOfferName = productOfferName;
        this.bodPackageId = bodPackageId;
        this.bodPackageName = bodPackageName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priority = priority;
        this.param1 = param1;
        this.param2 = param2;
        this.billDay = billDay;
        this.metadata = metadata;
    }

    public SubscriptionParameter() {

    }

    public SPRInfo getSprInfo() {
        return sprInfo;
    }

    public void setSprInfo(SPRInfo sprInfo) {
        this.sprInfo = sprInfo;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getProductOfferId() {
        return productOfferId;
    }

    public void setProductOfferId(String productOfferId) {
        this.productOfferId = productOfferId;
    }

    public Integer getSubscriptionStatusValue() {
        return subscriptionStatusValue;
    }

    public void setSubscriptionStatusValue(Integer subscriptionStatusValue) {
        this.subscriptionStatusValue = subscriptionStatusValue;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

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

    public Integer getBillDay() {
        return billDay;
    }

    public void setBillDay(Integer billDay) {
        this.billDay = billDay;
    }

    public static SubscriptionParameter create(SPRInfo sprInfo, ProductOffer productOffer) {
        SubscriptionParameter subscriptionParameter = new SubscriptionParameter(sprInfo,
                    null, productOffer.getId(), SubscriptionState.STARTED.state, null, null, null, null, null, null, null, null, sprInfo.getBillingDate(),null);

        subscriptionParameter.setSubscriptionPrice(productOffer.getSubscriptionPrice());


        return subscriptionParameter;
    }

    public double getSubscriptionPrice() {
        return subscriptionPrice;
    }

    public void setSubscriptionPrice(double subscriptionPrice) {
        this.subscriptionPrice = subscriptionPrice;
    }

    public String getProductOfferName() {
        return productOfferName;
    }

    public void setProductOfferName(String productOfferName) {
        this.productOfferName = productOfferName;
    }

    public MonetaryBalance getMonetaryBalance() {
        return monetaryBalance;
    }
    public void setMonetaryBalance(MonetaryBalance monetaryBalance) {
        this.monetaryBalance = monetaryBalance;
    }
    public String getBodPackageId() {
        return bodPackageId;
    }
    public String getBodPackageName() {
        return bodPackageName;
    }

    public SubscriptionMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(SubscriptionMetadata metadata) {
        this.metadata = metadata;
    }
}
