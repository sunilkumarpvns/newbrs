package com.elitecore.nvsmx.policydesigner.model.subscriber;

import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.util.commons.gson.adaptor.LongToStringGsonAdapter;
import com.google.gson.annotations.JsonAdapter;

public class NonMonetaryRateCardBalance {


    private static final String QUOTA_UNLIMITED = "Unlimited";
    private static final String QUOTA_UNDEFINED = "-";
    private String id;
    private String packageId;
    private String ratecardId;
    private String ratecardName;

    private String subscriberIdentity;
    private String subscriptionId;

    @JsonAdapter(LongToStringGsonAdapter.class)
    private long billingCycleTotalTime;

    @JsonAdapter(LongToStringGsonAdapter.class)
    private long billingCycleAvailableTime;

    private String displayUsageTime;
    private String displayTotalTime;
    private String displayAvailableTime;

    private long dailyTimeLimit;
    private long weeklyTimeLimit;

    private long reservationTime;

    private long dailyResetTime;
    private long weeklyResetTime;
    private long balanceExpiryTime;

    private ResetBalanceStatus status;
    private String renewalInterval;

    public String getDisplayAvailableTime() {
        return displayAvailableTime;
    }

    public void setDisplayAvailableTime(String displayAvailableTime) {
        this.displayAvailableTime = displayAvailableTime;
    }

    public String getDisplayUsageTime() {
        return displayUsageTime;
    }

    public void setDisplayUsageTime(String displayUsageTime) {
        this.displayUsageTime = displayUsageTime;
    }

    public String getDisplayTotalTime() {
        return displayTotalTime;
    }

    public void setDisplayTotalTime(String displayTotalTime) {
        this.displayTotalTime = displayTotalTime;
    }

    public static String getQuotaUnlimited() {
        return QUOTA_UNLIMITED;
    }

    public static String getQuotaUndefined() {
        return QUOTA_UNDEFINED;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getRatecardId() {
        return ratecardId;
    }

    public void setRatecardId(String ratecardId) {
        this.ratecardId = ratecardId;
    }

    public String getRatecardName() {
        return ratecardName;
    }

    public void setRatecardName(String ratecardName) {
        this.ratecardName = ratecardName;
    }

    public String getSubscriberIdentity() {
        return subscriberIdentity;
    }

    public void setSubscriberIdentity(String subscriberIdentity) {
        this.subscriberIdentity = subscriberIdentity;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public long getBillingCycleTotalTime() {
        return billingCycleTotalTime;
    }

    public void setBillingCycleTotalTime(long billingCycleTotalTime) {
        this.billingCycleTotalTime = billingCycleTotalTime;
    }

    public long getBillingCycleAvailableTime() {
        return billingCycleAvailableTime;
    }

    public void setBillingCycleAvailableTime(long billingCycleAvailableTime) {
        this.billingCycleAvailableTime = billingCycleAvailableTime;
    }

    public long getDailyTimeLimit() {
        return dailyTimeLimit;
    }

    public void setDailyTimeLimit(long dailyTimeLimit) {
        this.dailyTimeLimit = dailyTimeLimit;
    }

    public long getWeeklyTimeLimit() {
        return weeklyTimeLimit;
    }

    public void setWeeklyTimeLimit(long weeklyTimeLimit) {
        this.weeklyTimeLimit = weeklyTimeLimit;
    }

    public long getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(long reservationTime) {
        this.reservationTime = reservationTime;
    }

    public long getDailyResetTime() {
        return dailyResetTime;
    }

    public void setDailyResetTime(long dailyResetTime) {
        this.dailyResetTime = dailyResetTime;
    }

    public long getWeeklyResetTime() {
        return weeklyResetTime;
    }

    public void setWeeklyResetTime(long weeklyResetTime) {
        this.weeklyResetTime = weeklyResetTime;
    }

    public long getBalanceExpiryTime() {
        return balanceExpiryTime;
    }

    public void setBalanceExpiryTime(long balanceExpiryTime) {
        this.balanceExpiryTime = balanceExpiryTime;
    }

    public ResetBalanceStatus getStatus() {
        return status;
    }

    public void setStatus(ResetBalanceStatus status) {
        this.status = status;
    }

    public String getRenewalInterval() {
        return renewalInterval;
    }

    public void setRenewalInterval(String renewalInterval) {
        this.renewalInterval = renewalInterval;
    }
}
