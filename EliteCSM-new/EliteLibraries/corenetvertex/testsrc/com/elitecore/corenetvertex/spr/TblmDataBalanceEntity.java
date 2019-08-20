package com.elitecore.corenetvertex.spr;


import com.elitecore.corenetvertex.util.QueryBuilder;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Objects;

@Entity
@Table(name = "TBLM_DATA_BALANCE")
@com.elitecore.corenetvertex.spr.Table(name="TBLM_DATA_BALANCE")
public class TblmDataBalanceEntity {
    private String id;
    private String subscriberId;
    private String packageId;
    private String subscriptionId;
    private String quotaProfileId;
    private long serviceId;
    private int balanceLevel;
    private long ratingGroup;
    private Long billingCycleTotalVolume;
    private Long billingCycleAvailableVolume;
    private Long billingCycleTime;
    private Long billingCycleAvailableTime;
    private Long dailyVolume;
    private Long dailyTime;
    private Long weeklyVolume;
    private Long weeklyTime;
    private Timestamp dailyResetTime;
    private Timestamp weeklyResetTime;
    private Timestamp quotaExpiryTime;
    private Long reservationVolume;
    private Long reservationTime;
    private Timestamp lastUpdateTime;
    private Timestamp startTime;
    private String status;
    private String renewalInterval;
    private String productOfferId;
    private Long carryForwardVolume;
    private Long carryForwardTime;
    private String carryForwardStatus;

    public TblmDataBalanceEntity(){
        //Purposefully Empty
    }
    public TblmDataBalanceEntity(String id,
                                 String subscriberId,
                                 String packageId,
                                 String subscriptionId,
                                 String quotaProfileId,
                                 long serviceId,
                                 int balanceLevel,
                                 long ratingGroup,
                                 Long billingCycleTotalVolume,
                                 Long billingCycleAvailableVolume,
                                 Long billingCycleTime,
                                 Long billingCycleAvailableTime,
                                 Long dailyVolume,
                                 Long dailyTime,
                                 Long weeklyVolume,
                                 Long weeklyTime,
                                 Timestamp dailyResetTime,
                                 Timestamp weeklyResetTime,
                                 Timestamp quotaExpiryTime,
                                 Timestamp startTime,
                                 Long reservationVolume,
                                 Long reservationTime,
                                 Timestamp lastUpdateTime,
                                 String status,
                                 String renewalInterval, String productOfferId, String carryForwardStatus) {
        this.id = id;
        this.subscriberId = subscriberId;
        this.packageId = packageId;
        this.subscriptionId = subscriptionId;
        this.quotaProfileId = quotaProfileId;
        this.serviceId = serviceId;
        this.balanceLevel = balanceLevel;
        this.ratingGroup = ratingGroup;
        this.billingCycleTotalVolume = billingCycleTotalVolume;
        this.billingCycleAvailableVolume = billingCycleAvailableVolume;
        this.billingCycleTime = billingCycleTime;
        this.billingCycleAvailableTime = billingCycleAvailableTime;
        this.dailyVolume = dailyVolume;
        this.dailyTime = dailyTime;
        this.weeklyVolume = weeklyVolume;
        this.weeklyTime = weeklyTime;
        this.dailyResetTime = dailyResetTime;
        this.weeklyResetTime = weeklyResetTime;
        this.quotaExpiryTime = quotaExpiryTime;
        this.startTime = startTime;
        this.reservationVolume = reservationVolume;
        this.reservationTime = reservationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.status = status;
        this.renewalInterval = renewalInterval;
        this.productOfferId = productOfferId;
        this.carryForwardStatus = carryForwardStatus;
    }


    @Id
    @Column(name = "ID", nullable = false, length = 36)
    @com.elitecore.commons.kpi.annotation.Column(name = "ID", type = Types.VARCHAR)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "SUBSCRIBER_ID", nullable = true, length = 255)
    @com.elitecore.commons.kpi.annotation.Column(name = "SUBSCRIBER_ID", type = Types.VARCHAR)
    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    @Basic
    @Column(name = "PRODUCT_OFFER_ID", nullable = true, length = 36)
    @com.elitecore.commons.kpi.annotation.Column(name = "PRODUCT_OFFER_ID", type = Types.VARCHAR)
    public String getProductOfferId() {
        return productOfferId;
    }

    public void setProductOfferId(String productOfferId) {
        this.productOfferId = productOfferId;
    }

    @Basic
    @Column(name = "PACKAGE_ID", nullable = true, length = 36)
    @com.elitecore.commons.kpi.annotation.Column(name = "PACKAGE_ID", type = Types.VARCHAR)
    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    @Basic
    @Column(name = "SUBSCRIPTION_ID", nullable = true, length = 36)
    @com.elitecore.commons.kpi.annotation.Column(name = "SUBSCRIPTION_ID", type = Types.VARCHAR)
    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    @Basic
    @Column(name = "QUOTA_PROFILE_ID", nullable = true, length = 36)
    @com.elitecore.commons.kpi.annotation.Column(name = "QUOTA_PROFILE_ID", type = Types.VARCHAR)
    public String getQuotaProfileId() {
        return quotaProfileId;
    }

    public void setQuotaProfileId(String quotaProfileId) {
        this.quotaProfileId = quotaProfileId;
    }

    @Basic
    @Column(name = "DATA_SERVICE_TYPE_ID", nullable = true, length = 36)
    @com.elitecore.commons.kpi.annotation.Column(name = "DATA_SERVICE_TYPE_ID", type = Types.NUMERIC)
    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    @Basic
    @Column(name = "RATING_GROUP_ID", nullable = true, length = 36)
    @com.elitecore.commons.kpi.annotation.Column(name = "RATING_GROUP_ID", type = Types.NUMERIC)
    public long getRatingGroup() {
        return ratingGroup;
    }

    public void setRatingGroup(long ratingGroup) {
        this.ratingGroup = ratingGroup;
    }

    @Basic
    @Column(name = "BALANCE_LEVEL", nullable = true, precision = 0)
    @com.elitecore.commons.kpi.annotation.Column(name = "BALANCE_LEVEL", type = Types.NUMERIC)
    public int getBalanceLevel() {
        return balanceLevel;
    }

    public void setBalanceLevel(int balanceLevel) {
        this.balanceLevel = balanceLevel;
    }

    @Basic
    @Column(name = "BILLING_CYCLE_TOTAL_VOLUME", nullable = true, precision = 0)
    @com.elitecore.commons.kpi.annotation.Column(name = "BILLING_CYCLE_TOTAL_VOLUME", type = Types.NUMERIC)
    public Long getBillingCycleTotalVolume() {
        return billingCycleTotalVolume;
    }

    public void setBillingCycleTotalVolume(Long billingCycleTotalVolume) {
        this.billingCycleTotalVolume = billingCycleTotalVolume;
    }


    @Basic
    @Column(name = "BILLING_CYCLE_TOTAL_TIME", nullable = true, precision = 0)
    @com.elitecore.commons.kpi.annotation.Column(name = "BILLING_CYCLE_TOTAL_TIME", type = Types.NUMERIC)
    public Long getBillingCycleTime() {
        return billingCycleTime;
    }

    public void setBillingCycleTime(Long billingCycleTime) {
        this.billingCycleTime = billingCycleTime;
    }

    @Basic
    @Column(name = "DAILY_VOLUME", nullable = true, precision = 0)
    @com.elitecore.commons.kpi.annotation.Column(name = "DAILY_VOLUME", type = Types.NUMERIC)
    public Long getDailyVolume() {
        return dailyVolume;
    }

    public void setDailyVolume(Long dailyVolume) {
        this.dailyVolume = dailyVolume;
    }


    @Basic
    @Column(name = "DAILY_TIME", nullable = true, precision = 0)
    @com.elitecore.commons.kpi.annotation.Column(name = "DAILY_TIME", type = Types.NUMERIC)
    public Long getDailyTime() {
        return dailyTime;
    }

    public void setDailyTime(Long dailyTime) {
        this.dailyTime = dailyTime;
    }

    @Basic
    @Column(name = "WEEKLY_VOLUME", nullable = true, precision = 0)
    @com.elitecore.commons.kpi.annotation.Column(name = "WEEKLY_VOLUME", type = Types.NUMERIC)
    public Long getWeeklyVolume() {
        return weeklyVolume;
    }

    public void setWeeklyVolume(Long weeklyVolume) {
        this.weeklyVolume = weeklyVolume;
    }

    @Basic
    @Column(name = "WEEKLY_TIME", nullable = true, precision = 0)
    @com.elitecore.commons.kpi.annotation.Column(name = "WEEKLY_TIME", type = Types.NUMERIC)
    public Long getWeeklyTime() {
        return weeklyTime;
    }

    public void setWeeklyTime(Long weeklyTime) {
        this.weeklyTime = weeklyTime;
    }

    @Basic
    @Column(name = "DAILY_RESET_TIME", nullable = true)
    @com.elitecore.commons.kpi.annotation.Column(name = "DAILY_RESET_TIME", type = Types.TIMESTAMP)
    public Timestamp getDailyResetTime() {
        return dailyResetTime;
    }

    public void setDailyResetTime(Timestamp dailyResetTime) {
        this.dailyResetTime = dailyResetTime;
    }

    @Basic
    @Column(name = "WEEKLY_RESET_TIME", nullable = true)
    @com.elitecore.commons.kpi.annotation.Column(name = "WEEKLY_RESET_TIME", type = Types.TIMESTAMP)
    public Timestamp getWeeklyResetTime() {
        return weeklyResetTime;
    }

    public void setWeeklyResetTime(Timestamp weeklyResetTime) {
        this.weeklyResetTime = weeklyResetTime;
    }

    @Basic
    @Column(name = "QUOTA_EXPIRY_TIME", nullable = true)
    @com.elitecore.commons.kpi.annotation.Column(name = "QUOTA_EXPIRY_TIME", type = Types.TIMESTAMP)
    public Timestamp getQuotaExpiryTime() {
        return quotaExpiryTime;
    }

    public void setQuotaExpiryTime(Timestamp quotaExpiryTime) {
        this.quotaExpiryTime = quotaExpiryTime;
    }

    @Basic
    @Column(name = "START_TIME", nullable = true)
    @com.elitecore.commons.kpi.annotation.Column(name = "START_TIME", type = Types.TIMESTAMP)
    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    @Basic
    @Column(name = "RESERVATION_VOLUME", nullable = true, precision = 0)
    @com.elitecore.commons.kpi.annotation.Column(name = "RESERVATION_VOLUME", type = Types.NUMERIC)
    public Long getReservationVolume() {
        return reservationVolume;
    }

    public void setReservationVolume(Long reservationVolume) {
        this.reservationVolume = reservationVolume;
    }

    @Basic
    @Column(name = "RESERVATION_TIME", nullable = true, precision = 0)
    @com.elitecore.commons.kpi.annotation.Column(name = "RESERVATION_TIME", type = Types.NUMERIC)
    public Long getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(Long reservationTime) {
        this.reservationTime = reservationTime;
    }

    @Basic
    @Column(name = "LAST_UPDATE_TIME", nullable = true)
    @com.elitecore.commons.kpi.annotation.Column(name = "LAST_UPDATE_TIME", type = Types.TIMESTAMP)
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }


    @Basic
    @Column(name = "BILLING_CYCLE_AVAIL_VOLUME", nullable = true, precision = 0)
    @com.elitecore.commons.kpi.annotation.Column(name = "BILLING_CYCLE_AVAIL_VOLUME", type = Types.NUMERIC)
    public Long getBillingCycleAvailableVolume() {
        return billingCycleAvailableVolume;
    }

    public void setBillingCycleAvailableVolume(Long billingCycleAvailableVolume) {
        this.billingCycleAvailableVolume = billingCycleAvailableVolume;
    }

    @Basic
    @Column(name = "CARRY_FORWARD_VOLUME", nullable = true, precision = 0)
    @com.elitecore.commons.kpi.annotation.Column(name = "CARRY_FORWARD_VOLUME", type = Types.NUMERIC)
    public Long getCarryForwardVolume() {
        return carryForwardVolume;
    }

    public void setCarryForwardVolume(Long carryForwardVolume) {
        this.carryForwardVolume = carryForwardVolume;
    }

    @Basic
    @Column(name = "CARRY_FORWARD_TIME", nullable = true, precision = 0)
    @com.elitecore.commons.kpi.annotation.Column(name = "CARRY_FORWARD_TIME", type = Types.NUMERIC)
    public Long getCarryForwardTime() {
        return carryForwardTime;
    }

    public void setCarryForwardTime(Long carryForwardTime) {
        this.carryForwardTime = carryForwardTime;
    }

    @Basic
    @Column(name = "CARRY_FORWARD_STATUS", nullable = true, precision = 0)
    @com.elitecore.commons.kpi.annotation.Column(name = "CARRY_FORWARD_STATUS", type = Types.VARCHAR)
    public String getCarryForwardStatus() {
        return carryForwardStatus;
    }

    public void setCarryForwardStatus(String carryForwardStatus) {
        this.carryForwardStatus = carryForwardStatus;
    }

    @Basic
    @Column(name = "BILLING_CYCLE_AVAIL_TIME", nullable = true, precision = 0)
    @com.elitecore.commons.kpi.annotation.Column(name = "BILLING_CYCLE_AVAIL_TIME", type = Types.NUMERIC)
    public Long getBillingCycleAvailableTime() {
        return billingCycleAvailableTime;
    }

    public void setBillingCycleAvailableTime(Long billingCycleAvailableTime) {
        this.billingCycleAvailableTime = billingCycleAvailableTime;
    }

    @Basic
    @Column(name = "STATUS", nullable = true, length = 36)
    @com.elitecore.commons.kpi.annotation.Column(name = "STATUS", type = Types.VARCHAR)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Basic
    @Column(name = "RENEWAL_INTERVAL", nullable = true, length = 36)
    @com.elitecore.commons.kpi.annotation.Column(name = "RENEWAL_INTERVAL", type = Types.VARCHAR)
    public String getRenewalInterval() {
        return renewalInterval;
    }

    public void setRenewalInterval(String renewalInterval) {
        this.renewalInterval = renewalInterval;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TblmDataBalanceEntity that = (TblmDataBalanceEntity) o;
        return balanceLevel == that.balanceLevel &&
                ratingGroup == that.ratingGroup &&
                Objects.equals(id, that.id) &&
                Objects.equals(subscriberId, that.subscriberId) &&
                Objects.equals(packageId, that.packageId) &&
                Objects.equals(productOfferId, that.productOfferId) &&
                Objects.equals(subscriptionId, that.subscriptionId) &&
                Objects.equals(quotaProfileId, that.quotaProfileId) &&
                Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(billingCycleTotalVolume, that.billingCycleTotalVolume) &&
                Objects.equals(billingCycleAvailableVolume, that.billingCycleAvailableVolume) &&
                Objects.equals(billingCycleTime, that.billingCycleTime) &&
                Objects.equals(billingCycleAvailableTime, that.billingCycleAvailableTime) &&
                Objects.equals(dailyVolume, that.dailyVolume) &&
                Objects.equals(dailyTime, that.dailyTime) &&
                Objects.equals(weeklyVolume, that.weeklyVolume) &&
                Objects.equals(weeklyTime, that.weeklyTime) &&
                Objects.equals(dailyResetTime, that.dailyResetTime) &&
                Objects.equals(weeklyResetTime, that.weeklyResetTime) &&
                Objects.equals(quotaExpiryTime, that.quotaExpiryTime) &&
                Objects.equals(reservationVolume, that.reservationVolume) &&
                Objects.equals(reservationTime, that.reservationTime) &&
                Objects.equals(status, that.status) &&
                Objects.equals(lastUpdateTime, that.lastUpdateTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, subscriberId, packageId, productOfferId, subscriptionId, quotaProfileId, serviceId, balanceLevel, ratingGroup, billingCycleTotalVolume, billingCycleAvailableVolume, billingCycleTime, billingCycleAvailableTime, dailyVolume, dailyTime, weeklyVolume, weeklyTime, dailyResetTime, weeklyResetTime, quotaExpiryTime, reservationVolume, reservationTime, lastUpdateTime, status);
    }

    public static String createTableQuery() {
        return QueryBuilder.buildCreateQuery(TblmDataBalanceEntity.class);
    }
    public static String dropTableQuery() {
        return QueryBuilder.buildDropQuery(TblmDataBalanceEntity.class);
    }

	public TblmDataBalanceEntity copy() {
		TblmDataBalanceEntity copy = new TblmDataBalanceEntity();
		copy.setId(getId());
		copy.setDailyVolume(getDailyVolume());
		copy.setDailyTime(getDailyTime());
		copy.setWeeklyVolume(getWeeklyVolume());
		copy.setWeeklyTime(getWeeklyTime());
		copy.setBillingCycleTotalVolume(getBillingCycleTotalVolume());
		copy.setBillingCycleAvailableVolume(getBillingCycleAvailableVolume());
		copy.setBillingCycleTime(getBillingCycleTime());
		copy.setBillingCycleAvailableTime(getBillingCycleAvailableTime());
		copy.setReservationVolume(getReservationVolume());
		copy.setReservationTime(getReservationTime());
		copy.setSubscriberId(getSubscriberId());
		copy.setProductOfferId(getProductOfferId());
		return copy;
	}
}
