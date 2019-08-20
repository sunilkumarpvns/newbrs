package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.data.CarryForwardStatus;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.Balance;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.BillingCycleBalance;

import java.io.StringWriter;
import java.sql.Date;

public class NonMonetoryBalance {
    private static final String QUOTA_UNLIMITED = "Unlimited";
    private static final String QUOTA_UNDEFINED = "-";
    private final String id;
    private final long serviceId;
    private final String packageId;
    private final String quotaProfileId;

    private final String subscriberIdentity;
    private final String subscriptionId;

    private long billingCycleTotalVolume;
    private long billingCycleAvailableVolume;
    private long billingCycleTime;
    private long billingCycleAvailableTime;

    private long dailyVolume;
    private long dailyTime;

    private long weeklyVolume;
    private long weeklyTime;

    private long reservationVolume;
    private long reservationTime;

    private long dailyResetTime;
    private long weeklyResetTime;
    private long billingCycleResetTime;
    private long startTime;

    private long carryForwardVolume;
    private long carryForwardTime;


    private final long ratingGroupId;
    private final int level;
    private ResetBalanceStatus status;
    private CarryForwardStatus carryForwardStatus;
    private final String renewalInterval;
    private final String productOfferId;


    public NonMonetoryBalance(String id,
                              long serviceId,
                              String packageId,
                              String quotaProfileId,
                              long ratingGroupId,
                              int level,
                              String subscriberIdentity,
                              String subscriptionId,
                              long billingCycleTotalVolume,
                              long billingCycleAvailableVolume,
                              long billingCycleTime,
                              long billingCycleAvailableTime,
                              long dailyVolume,
                              long dailyTime,
                              long weeklyVolume,
                              long weeklyTime,
                              long reservationVolume,
                              long reservationTime,
                              long dailyResetTime,
                              long weeklyResetTime,
                              long billingCycleResetTime,
                              ResetBalanceStatus status,
                              long carryForwardVolume,
                              long carryForwardTime,
                              CarryForwardStatus carryForwardStatus,
                              String renewalInterval,
                              String productOfferId,
                              long startTime) {
        this.id = id;
        this.serviceId = serviceId;
        this.packageId = packageId;
        this.quotaProfileId = quotaProfileId;
        this.ratingGroupId = ratingGroupId;
        this.level = level;
        this.subscriberIdentity = subscriberIdentity;
        this.subscriptionId = subscriptionId;
        this.billingCycleAvailableVolume = billingCycleAvailableVolume;
        this.billingCycleTotalVolume = billingCycleTotalVolume;
        this.billingCycleTime = billingCycleTime;
        this.billingCycleAvailableTime = billingCycleAvailableTime;
        this.dailyVolume = dailyVolume;
        this.dailyTime = dailyTime;
        this.weeklyVolume = weeklyVolume;
        this.weeklyTime = weeklyTime;
        this.reservationVolume = reservationVolume;
        this.reservationTime = reservationTime;
        this.dailyResetTime = dailyResetTime;
        this.weeklyResetTime = weeklyResetTime;
        this.billingCycleResetTime = billingCycleResetTime;
        this.status = status;
        this.carryForwardVolume = carryForwardVolume;
        this.carryForwardTime = carryForwardTime;
        this.carryForwardStatus = carryForwardStatus;
        this.renewalInterval = renewalInterval;
        this.productOfferId = productOfferId;
        this.startTime = startTime;
    }

    public void setBillingCycleTotalVolume(long billingCycleTotalVolume) {
        this.billingCycleTotalVolume = billingCycleTotalVolume;
    }

    public void setBillingCycleAvailableVolume(long billingCycleAvailableVolume) {
        this.billingCycleAvailableVolume = billingCycleAvailableVolume;
    }

    public void setBillingCycleTime(long billingCycleTime) {
        this.billingCycleTime = billingCycleTime;
    }

    public void setDailyVolume(long dailyVolume) {
        this.dailyVolume = dailyVolume;
    }

    public void setDailyTime(long dailyTime) {
        this.dailyTime = dailyTime;
    }

    public void setWeeklyVolume(long weeklyVolume) {
        this.weeklyVolume = weeklyVolume;
    }

    public void setWeeklyTime(long weeklyTime) {
        this.weeklyTime = weeklyTime;
    }

    public void setReservationVolume(long reservationVolume) {
        this.reservationVolume = reservationVolume;
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

    public void setBillingCycleResetTime(long billingCycleResetTime) {
        this.billingCycleResetTime = billingCycleResetTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getBillingCycleAvailableTime() {
        return billingCycleAvailableTime;
    }

    public void setBillingCycleAvailableTime(long billingCycleAvailableTime) {
        this.billingCycleAvailableTime = billingCycleAvailableTime;
    }

    public long getActualVolumeBalance() {
        return billingCycleAvailableVolume - reservationVolume;
    }

    public long getActualTimeBalance() {
        return billingCycleAvailableTime - reservationTime;
    }

    public ResetBalanceStatus getStatus(){
        return status;
    }

    public void setStatus(ResetBalanceStatus status) {
        this.status = status;
    }

    public void setCarryForwardStatus(CarryForwardStatus carryForwardStatus) {
        this.carryForwardStatus = carryForwardStatus;
    }

    public String getRenewalInterval(){
        return renewalInterval;
    }

    public void addBillingCycleAvailableVolume(long billingCycleAvailableVolume) {
        this.billingCycleAvailableVolume += billingCycleAvailableVolume;
    }

    public void addBillingCycleAvailableTime(long billingCycleAvailableTime) {
        this.billingCycleAvailableTime += billingCycleAvailableTime;
    }

    public void addDaily(long dailyVolume, long dailyTime) {
        this.dailyVolume += dailyVolume;
        this.dailyTime += dailyTime;
    }

    public void addWeekly(long weeklyVolume, long weeklyTime) {
        this.weeklyVolume += weeklyVolume;
        this.weeklyTime += weeklyTime;
    }

    public void addReservationVolume(long reservationVolume) {
        this.reservationVolume += reservationVolume;
    }

    public void addReservationTime(long reservationTime) {
        this.reservationTime += reservationTime;
    }

    public void substractBillingCycle(long billingCycleAvailableVolume, long billingCycleAvailableTime) {
        this.billingCycleAvailableVolume -= billingCycleAvailableVolume;
        this.billingCycleAvailableTime -= billingCycleAvailableTime;
    }

    public void substractReservation(long reservationVolume, long reservationTime) {
        this.reservationVolume -= reservationVolume;
        this.reservationTime -= reservationTime;
    }

    public void resetDailyUsage() {
        setDailyVolume(0);
        setDailyTime(0);
    }

    public void resetWeeklyUsage() {
        setWeeklyVolume(0);
        setWeeklyTime(0);
    }

    public String getId() {
        return id;
    }

    public long getServiceId() {
        return serviceId;
    }

    public String getPackageId() {
        return packageId;
    }

    public long getRatingGroupId() {
        return ratingGroupId;
    }

    public String getQuotaProfileId() {
        return quotaProfileId;
    }

    public int getLevel() {
        return level;
    }

    public String getSubscriberIdentity() {
        return subscriberIdentity;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public long getBillingCycleTotalVolume() {
        return billingCycleTotalVolume;
    }

    public long getBillingCycleAvailableVolume() {
        return billingCycleAvailableVolume;
    }

    public long getBillingCycleTime() {
        return billingCycleTime;
    }

    public long getDailyVolume() {
        return dailyVolume;
    }

    public long getDailyTime() {
        return dailyTime;
    }

    public long getWeeklyVolume() {
        return weeklyVolume;
    }

    public long getWeeklyTime() {
        return weeklyTime;
    }

    public long getReservationVolume() {
        return reservationVolume;
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

    public long getBillingCycleResetTime() {
        return billingCycleResetTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getProductOfferId() {
        return productOfferId;
    }

    public long getCarryForwardVolume() {
        return carryForwardVolume;
    }

    public void setCarryForwardVolume(long carryForwardVolume) {
        this.carryForwardVolume = carryForwardVolume;
    }

    public long getCarryForwardTime() {
        return carryForwardTime;
    }

    public void setCarryForwardTime(long carryForwardTime) {
        this.carryForwardTime = carryForwardTime;
    }

    public CarryForwardStatus getCarryForwardStatus() {
        return carryForwardStatus;
    }

    public NonMonetoryBalance copy() {
            return new NonMonetoryBalance(this.id,
                    this.serviceId,
                    this.packageId,
                    this.quotaProfileId,
                    this.ratingGroupId,
                    this.level,
                    this.subscriberIdentity,
                    this.subscriptionId,
                    billingCycleTotalVolume,
                    billingCycleAvailableVolume,
                    billingCycleTime,
                    billingCycleAvailableTime,
                    dailyVolume,
                    dailyTime,
                    weeklyVolume,
                    weeklyTime,
                    reservationVolume,
                    reservationTime,
                    dailyResetTime,
                    weeklyResetTime,
                    billingCycleResetTime,
                    status,
                    carryForwardVolume,
                    carryForwardTime,
                    carryForwardStatus,
                    renewalInterval,
                    productOfferId,
                    startTime);
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

    public Balance createAggregateBalance(AggregationKey aggregationKey){
        switch (aggregationKey){
            case BILLING_CYCLE:
                return new BillingCycleBalance(billingCycleAvailableVolume, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, billingCycleAvailableTime);
            case WEEKLY:
                return new BillingCycleBalance(weeklyVolume, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, weeklyTime);
            case DAILY:
                return new BillingCycleBalance(dailyVolume, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, dailyTime);
            default:
                return null;
        }
    }

    public boolean isNotExpired(TimeSource timeSource) {
        return timeSource.currentTimeInMillis() <= getBillingCycleResetTime();
    }

    public static class NonMonetaryBalanceBuilder{

        private String id;
        private long serviceId;
        private String packageId;
        private String quotaProfileId;

        private long ratingGroupId;
        private int level;

        private String subscriberIdentity;
        private String subscriptionId;

        private long billingCycleTotalVolume;
        private long billingCycleAvailableVolume;
        private long billingCycleTime;
        private long billingCycleAvailableTime;

        private long dailyVolume;
        private long dailyTime;

        private long weeklyVolume;
        private long weeklyTime;


        private long reservationVolume;
        private long reservationTime;

        private long dailyResetTime;
        private long weeklyResetTime;
        private long billingCycleResetTime;
        private long startTime;
        private ResetBalanceStatus status;

        private long carryForwardVolume;
        private long carryForwardTime;
        private CarryForwardStatus carryForwardStatus;

        private String renewalInterval;
        private String productOfferId;

        public NonMonetaryBalanceBuilder() {
        }

        public NonMonetaryBalanceBuilder(String id,
                                         long serviceId,
                                         String packageId,
                                         long ratingGroupId,
                                         String subscriberIdentity,
                                         String subscriptionId,
                                         int level,
                                         String quotaProfileId,
                                         ResetBalanceStatus status,
                                         String renewalInterval,
                                         String productOfferId) {
            this.id = id;
            this.serviceId = serviceId;
            this.packageId = packageId;
            this.ratingGroupId = ratingGroupId;
            this.subscriberIdentity = subscriberIdentity;
            this.subscriptionId = subscriptionId;
            this.level = level;
            this.quotaProfileId = quotaProfileId;
            this.status = status;
            this.renewalInterval = renewalInterval;
            this.productOfferId = productOfferId;
        }

        public NonMonetaryBalanceBuilder withPackageId(String packageId) {
            this.packageId = packageId;
            return this;
        }

        public NonMonetaryBalanceBuilder withProductOfferId(String productOfferId) {
            this.productOfferId = productOfferId;
            return this;
        }

        public NonMonetaryBalanceBuilder withRenewalInterval(String renewalInterval) {
            this.renewalInterval = renewalInterval;
            return this;
        }

        public NonMonetaryBalanceBuilder withStatus(ResetBalanceStatus status) {
            this.status = status;
            return this;
        }

        public NonMonetaryBalanceBuilder withCarryForwardStatus(CarryForwardStatus carryForwardStatus) {
            this.carryForwardStatus = carryForwardStatus;
            return this;
        }

        public NonMonetaryBalanceBuilder withServiceId(long serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        public NonMonetaryBalanceBuilder withRatingGroupId(long ratingGroupId) {
            this.ratingGroupId = ratingGroupId;
            return this;
        }

        public NonMonetaryBalanceBuilder withSubscriberIdentity(String subscriberIdentity) {
            this.subscriberIdentity = subscriberIdentity;
            return this;
        }

        public NonMonetaryBalanceBuilder withSubscriptionId(String subscriptionId) {
            this.subscriptionId = subscriptionId;
            return this;
        }

        public NonMonetaryBalanceBuilder withLevel(int level) {
            this.level = level;
            return this;
        }

        public NonMonetaryBalanceBuilder withQuotaProfileId(String quotaProfileId) {
            this.quotaProfileId = quotaProfileId;
            return this;
        }

        public NonMonetaryBalanceBuilder withBillingCycleVolumeBalance(long billingCycleTotalVolume, long billingCycleAvailableVolume){
            this.billingCycleTotalVolume = billingCycleTotalVolume;
            this.billingCycleAvailableVolume = billingCycleAvailableVolume;
            return this;
        }

        public NonMonetaryBalanceBuilder withBillingCycleTimeBalance(long billingCycleTime, long billingCycleAvailableTime){
            this.billingCycleTime = billingCycleTime;
            this.billingCycleAvailableTime = billingCycleAvailableTime;
            return this;
        }

        public NonMonetaryBalanceBuilder withCarryForwardVolume(long carryForwardVolume){
            this.carryForwardVolume = carryForwardVolume;
            return this;
        }

        public NonMonetaryBalanceBuilder withCarryForwardTime(long carryForwardTime){
            this.carryForwardTime = carryForwardTime;
            return this;
        }

        public NonMonetaryBalanceBuilder withDailyUsage(long dailyVolume, long dailyTime){
            this.dailyVolume = dailyVolume;
            this.dailyTime = dailyTime;
            return this;
        }

        public NonMonetaryBalanceBuilder withWeeklyUsage(long weeklyVolume, long weeklyTime){
            this.weeklyVolume = weeklyVolume;
            this.weeklyTime = weeklyTime;
            return this;
        }

        public NonMonetaryBalanceBuilder withReservation(long reservationVolume, long reservationTime){
            this.reservationVolume = reservationVolume;
            this.reservationTime = reservationTime;
            return this;
        }

        public NonMonetaryBalanceBuilder withBillingCycleResetTime(long timeInMillies) {
            this.billingCycleResetTime = timeInMillies;
            return this;
        }

        public NonMonetaryBalanceBuilder withStartTime(long timeInMillies) {
            this.startTime = timeInMillies;
            return this;
        }

        public NonMonetaryBalanceBuilder withDailyResetTime(long timeInMillies) {
            this.dailyResetTime = timeInMillies;
            return this;
        }

        public NonMonetaryBalanceBuilder withWeeklyResetTime(long timeInMillies) {
            this.weeklyResetTime = timeInMillies;
            return this;
        }

        public NonMonetoryBalance build(){
            return new NonMonetoryBalance(this.id,
                    this.serviceId,
                    this.packageId,
                    this.quotaProfileId,
                    this.ratingGroupId,
                    this.level,
                    this.subscriberIdentity,
                    this.subscriptionId,
                    this.billingCycleTotalVolume,
                    this.billingCycleAvailableVolume,
                    billingCycleTime,
                    billingCycleAvailableTime,
                    dailyVolume,
                    dailyTime,
                    weeklyVolume,
                    weeklyTime,
                    reservationVolume,
                    reservationTime,
                    dailyResetTime,
                    weeklyResetTime,
                    billingCycleResetTime,
                    status,
                    carryForwardVolume,
                    carryForwardTime,
                    carryForwardStatus,
                    renewalInterval,
                    productOfferId,
                    startTime);
        }

        public static NonMonetaryBalanceBuilder fromBasicInfoOf(NonMonetoryBalance packageBalance) {
            return new NonMonetoryBalance.NonMonetaryBalanceBuilder(packageBalance.id, packageBalance.serviceId, packageBalance.packageId, packageBalance.ratingGroupId, packageBalance.subscriberIdentity, packageBalance.subscriptionId, packageBalance.level, packageBalance.quotaProfileId, packageBalance.status, packageBalance.renewalInterval, packageBalance.productOfferId);
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
        out.println("Service ID = " + serviceId);

        out.println("Billing Cycle Total = " + getDisplayValue(billingCycleTotalVolume) + "(V) " + getDisplayValue(billingCycleTime) + "(Time) ");
        out.println("Billing Cycle Available = " + getDisplayValue(billingCycleAvailableVolume) + "(V) " + getDisplayValue(billingCycleAvailableTime) + "(Time) "
        + new Date(billingCycleResetTime) + "(Reset-time)");

        out.println("Daily = " + getDisplayValue(dailyVolume) + "(V) "
                + getDisplayValue(dailyTime) + "(Time) "
                + new Date(dailyResetTime) + "(Reset-time)");

        out.println("Weekly = " + getDisplayValue(weeklyVolume) + "(V) "
                + getDisplayValue(weeklyTime) + "(Time) "
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
