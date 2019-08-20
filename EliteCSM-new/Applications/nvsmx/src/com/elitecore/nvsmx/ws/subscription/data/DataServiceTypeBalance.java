package com.elitecore.nvsmx.ws.subscription.data;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"serviceId", "serviceName","ratingGroupId","level","expiryTime","dailyBalance", "weeklyBalance","billingCycleBalance","customBalance","carryForwardBalance"})
public class DataServiceTypeBalance {
    private String serviceId;
    private String serviceName;
    private String ratingGroupId;
    private String level;
    private Long expiryTime;
    private RncBalance dailyBalance;
    private RncBalance weeklyBalance;
    private RncBalance billingCycleBalance;
    private CarryForwardBalance carryForwardBalance;
    private RncBalance customBalance;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getRatingGroupId() {
        return ratingGroupId;
    }

    public void setRatingGroupId(String ratingGroupId) {
        this.ratingGroupId = ratingGroupId;
    }

    public Long getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Long expiryTime) {
        this.expiryTime = expiryTime;
    }

    public RncBalance getDailyBalance() {
        return dailyBalance;
    }

    public void setDailyBalance(RncBalance dailyBalance) {
        this.dailyBalance = dailyBalance;
    }

    public RncBalance getWeeklyBalance() {
        return weeklyBalance;
    }

    public void setWeeklyBalance(RncBalance weeklyBalance) {
        this.weeklyBalance = weeklyBalance;
    }

    public RncBalance getBillingCycleBalance() {
        return billingCycleBalance;
    }

    public void setBillingCycleBalance(RncBalance billingCycleBalance) {
        this.billingCycleBalance = billingCycleBalance;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public RncBalance getCustomBalance() {
        return customBalance;
    }

    public void setCustomBalance(RncBalance customBalance) {
        this.customBalance = customBalance;
    }

    public CarryForwardBalance getCarryForwardBalance() {
        return carryForwardBalance;
    }

    public void setCarryForwardBalance(CarryForwardBalance carryForwardBalance) {
        this.carryForwardBalance = carryForwardBalance;
    }
}
