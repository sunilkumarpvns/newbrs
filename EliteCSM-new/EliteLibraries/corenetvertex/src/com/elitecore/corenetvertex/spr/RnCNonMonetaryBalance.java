package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pkg.ChargingType;

import java.io.StringWriter;
import java.sql.Date;

public class RnCNonMonetaryBalance {
    private static final String QUOTA_UNLIMITED = "Unlimited";
    private static final String QUOTA_UNDEFINED = "-";
    private final String id;
    private final String packageId;
    private final String ratecardId;
    private final String productOfferId;
    private final ChargingType chargingType;

    private final String subscriberIdentity;
    private final String subscriptionId;

    private long billingCycleTotal;
    private long billingCycleAvailable;

    private long dailyLimit;
    private long weeklyLimit;

    private long reservationTime;

    private long dailyResetTime;
    private long weeklyResetTime;
    private long balanceExpiryTime;

    private final ResetBalanceStatus status;
    private final String renewalInterval;



    public RnCNonMonetaryBalance(String id,
                                 String packageId,
                                 String ratecardId,
                                 String productOfferId,
                                 String subscriberIdentity,
                                 String subscriptionId,
                                 long billingCycleTotal,
                                 long billingCycleAvailable,
                                 long dailyLimit,
                                 long weeklyLimit,
                                 long reservationTime,
                                 long dailyResetTime,
                                 long weeklyResetTime,
                                 long balanceExpiryTime,
                                 ResetBalanceStatus status,
                                 String renewalInterval,
                                 ChargingType chargingType) {
        this.id = id;
        this.packageId = packageId;
        this.ratecardId = ratecardId;
        this.productOfferId = productOfferId;
        this.subscriberIdentity = subscriberIdentity;
        this.subscriptionId = subscriptionId;
        this.billingCycleAvailable = billingCycleAvailable;
        this.billingCycleTotal = billingCycleTotal;
        this.dailyLimit = dailyLimit;
        this.weeklyLimit = weeklyLimit;
        this.reservationTime = reservationTime;
        this.dailyResetTime = dailyResetTime;
        this.weeklyResetTime = weeklyResetTime;
        this.balanceExpiryTime = balanceExpiryTime;
        this.status = status;
        this.renewalInterval = renewalInterval;
        this.chargingType = chargingType;
    }

    public void setBillingCycleTotal(long billingCycleTotal) {
        this.billingCycleTotal = billingCycleTotal;
    }

    public void setBillingCycleAvailable(long billingCycleAvailable) {
        this.billingCycleAvailable = billingCycleAvailable;
    }

    public void setDailyLimit(long dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public void setWeeklyLimit(long weeklyLimit) {
        this.weeklyLimit = weeklyLimit;
    }

    public void setReservationTime(long reservationTime) {
        this.reservationTime = reservationTime;
    }

    public void setDailyResetTime(long dailyResetTime) {
        this.dailyResetTime = dailyResetTime;
    }

    public void setWeeklyResetTime(long weeklyResetTime) {
        this.weeklyResetTime = weeklyResetTime;
    }

    public void setBalanceExpiryTime(long balanceExpiryTime) {
        this.balanceExpiryTime = balanceExpiryTime;
    }

    public ResetBalanceStatus getStatus(){
        return status;
    }

    public String getRenewalInterval(){
        return renewalInterval;
    }

    public void addBillingCycleAvailableTime(long billingCycleAvailableTime) {
        this.billingCycleAvailable += billingCycleAvailableTime;
    }

    public void addDaily(long dailyTime) {
        this.dailyLimit += dailyTime;
    }

    public void addWeekly(long weeklyTime) {
        this.weeklyLimit += weeklyTime;
    }

    public void addReservationTime(long reservationTime) {
        this.reservationTime += reservationTime;
    }

    public void substractBillingCycle(long billingCycleAvailableTime) {
        this.billingCycleAvailable -= billingCycleAvailableTime;
    }

    public void substractReservation( long reservationTime) {
        this.reservationTime -= reservationTime;
    }

    public void resetDailyUsage() {
        setDailyLimit(0);
    }

    public void resetWeeklyUsage() {
        setWeeklyLimit(0);
    }

    public String getId() {
        return id;
    }

    public String getPackageId() {
        return packageId;
    }

    public String getRatecardId() {
        return ratecardId;
    }

    public String getSubscriberIdentity() {
        return subscriberIdentity;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public long getBillingCycleTotal() {
        return billingCycleTotal;
    }

    public long getBillingCycleAvailable() {
        return billingCycleAvailable;
    }

    public long getDailyLimit() {
        return dailyLimit;
    }

    public long getWeeklyLimit() {
        return weeklyLimit;
    }

    public long getReservationTime() {
        return reservationTime;
    }

    public long getDailyResetTime() {
        return dailyResetTime;
    }

    public long getWeeklyResetTime() {
        return weeklyResetTime;
    }

    public long getBalanceExpiryTime() {
        return balanceExpiryTime;
    }

    public boolean isExist() {
        return getActualBalance() > 0;
    }

    public long getActualBalance() {
        return billingCycleAvailable - reservationTime;
    }

    public String getProductOfferId() {
        return productOfferId;
    }

    public ChargingType getChargingType() { return chargingType; }

    public RnCNonMonetaryBalance copy() {
        return new RnCNonMonetaryBalance(this.id,
                this.packageId,
                this.ratecardId,
                this.productOfferId,
                this.subscriberIdentity,
                this.subscriptionId,
                billingCycleTotal,
                billingCycleAvailable,
                dailyLimit,
                weeklyLimit,
                reservationTime,
                dailyResetTime,
                weeklyResetTime,
                balanceExpiryTime,
                status,
                renewalInterval,
                chargingType);
    }

    public boolean isPreviousDayUsage(long currentTime) {
        return currentTime > dailyResetTime;
    }

    public boolean isPreviousWeekUsage(long currentTime) {
        return currentTime > weeklyResetTime;
    }

    public void setNextWeeklyResetTime() {
        weeklyResetTime += java.util.concurrent.TimeUnit.DAYS.toMillis(7);
    }

    public void setNextDailyResetTime() {
        dailyResetTime += java.util.concurrent.TimeUnit.DAYS.toMillis(1);
    }

    public static class RnCNonMonetaryBalanceBuilder{

        private String id;
        private String packageId;
        private String ratecardId;
        private String productOfferId;
        private String subscriberIdentity;
        private String subscriptionId;
        private ChargingType chargingType;

        private long billingCycleTotalTime;
        private long billingCycleAvailableTime;

        private long dailyTimeLimit;
        private long weeklyTimeLimit;

        private long reservationTime;

        private long dailyResetTime;
        private long weeklyResetTime;
        private long balanceExpiryTime;
        private ResetBalanceStatus status;
        private String renewalInterval;

        public RnCNonMonetaryBalanceBuilder() {
        }

        public RnCNonMonetaryBalanceBuilder(String id,
                                            String packageId,
                                            String productOfferId,
                                            String subscriberIdentity,
                                            String subscriptionId,
                                            String ratecardId,
                                            ResetBalanceStatus status,
                                            String renewalInterval,
                                            ChargingType chargingType) {
            this.id = id;
            this.packageId = packageId;
            this.productOfferId = productOfferId;
            this.subscriberIdentity = subscriberIdentity;
            this.subscriptionId = subscriptionId;
            this.ratecardId = ratecardId;
            this.status = status;
            this.renewalInterval = renewalInterval;
            this.chargingType = chargingType;
        }

        public RnCNonMonetaryBalanceBuilder withBillingCycleTimeBalance(long billingCycleTotalTime, long billingCycleAvailableTime){
            this.billingCycleTotalTime = billingCycleTotalTime;
            this.billingCycleAvailableTime = billingCycleAvailableTime;
            return this;
        }

        public RnCNonMonetaryBalanceBuilder withDailyUsage(long dailyTime){
            this.dailyTimeLimit = dailyTime;
            return this;
        }

        public RnCNonMonetaryBalanceBuilder withWeeklyUsage(long weeklyTime){
            this.weeklyTimeLimit = weeklyTime;
            return this;
        }

        public RnCNonMonetaryBalanceBuilder withReservation(long reservationTime){
            this.reservationTime = reservationTime;
            return this;
        }

        public RnCNonMonetaryBalanceBuilder withBillingCycleResetTime(long timeInMillies) {
            this.balanceExpiryTime = timeInMillies;
            return this;
        }

        public RnCNonMonetaryBalanceBuilder withDailyResetTime(long timeInMillies) {
            this.dailyResetTime = timeInMillies;
            return this;
        }

        public RnCNonMonetaryBalanceBuilder withWeeklyResetTime(long timeInMillies) {
            this.weeklyResetTime = timeInMillies;
            return this;
        }

        public RnCNonMonetaryBalance build(){
            return new RnCNonMonetaryBalance(this.id,
                    this.packageId,
                    this.ratecardId,
                    this.productOfferId,
                    this.subscriberIdentity,
                    this.subscriptionId,
                    this.billingCycleTotalTime,
                    this.billingCycleAvailableTime,
                    dailyTimeLimit,
                    weeklyTimeLimit,
                    reservationTime,
                    dailyResetTime,
                    weeklyResetTime,
                    balanceExpiryTime,
                    status,
                    renewalInterval,
                    chargingType);
        }

        public static RnCNonMonetaryBalanceBuilder fromBasicInfoOf(RnCNonMonetaryBalance packageBalance) {
            return new RnCNonMonetaryBalance.RnCNonMonetaryBalanceBuilder(packageBalance.id, packageBalance.packageId, packageBalance.getProductOfferId(), packageBalance.subscriberIdentity, packageBalance.subscriptionId, packageBalance.ratecardId, packageBalance.status, packageBalance.renewalInterval, packageBalance.chargingType);
        }

    }

    @Override
    public String toString() {

        StringWriter stringWriter = new StringWriter();

        toString(new IndentingPrintWriter(stringWriter));
        return stringWriter.toString();

    }

    public void toString(IndentingPrintWriter out) {

        out.incrementIndentation();
        out.println("ID = " + id);

        out.println("Billing Cycle Total Balance = " + getDisplayValue(billingCycleTotal));
        out.println("Billing Cycle Available Balance = " + getDisplayValue(billingCycleAvailable)
                + new Date(balanceExpiryTime) + "(Reset-time)");

        out.println("Daily = " + getDisplayValue(dailyLimit)
                + new Date(dailyResetTime) + "(Reset-time)");

        out.println("Weekly = " + getDisplayValue(weeklyLimit)
                + new Date(weeklyResetTime) + "(Reset-time)");
        out.println();
        out.decrementIndentation();

    }

    private String getDisplayValue(long allowedUsage) {

        if (allowedUsage == CommonConstants.QUOTA_UNLIMITED) {
            return QUOTA_UNLIMITED + " ";
        } else if (allowedUsage == CommonConstants.QUOTA_UNDEFINED) {
            return QUOTA_UNDEFINED + " ";
        }
        return allowedUsage + " ";
    }

}
