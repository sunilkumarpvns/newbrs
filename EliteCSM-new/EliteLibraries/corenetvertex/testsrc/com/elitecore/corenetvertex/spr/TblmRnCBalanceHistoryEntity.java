package com.elitecore.corenetvertex.spr;


import java.sql.Timestamp;
import java.sql.Types;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import com.elitecore.corenetvertex.util.QueryBuilder;

@Entity
@Table(name = "TBLM_RNC_BALANCE_HISTORY")
@com.elitecore.corenetvertex.spr.Table(name="TBLM_RNC_BALANCE_HISTORY")
public class TblmRnCBalanceHistoryEntity {
    private String id;
    private String subscriberId;
    private String packageId;
    private String subscriptionId;
    private String ratecardId;
    private Long billingCycleTotal;
    private Long billingCycleAvailable;
    private Long dailyLimit;
    private Long weeklyLimit;
    private Timestamp dailyResetTime;
    private Timestamp weeklyResetTime;
    private Timestamp balanceExpiryTime;
    private Long reservationTime;
    private Timestamp lastUpdateTime;
    private Timestamp startTime;
    private String status;
    private String renewalInterval;
    private String productOfferId;
	private Timestamp createDate;
	private String chargingType;

    public TblmRnCBalanceHistoryEntity(){
        //Purposefully Empty
    }
    public TblmRnCBalanceHistoryEntity(String id,
									   String subscriberId,
									   String packageId,
									   String subscriptionId,
									   String ratecardId,
									   Long billingCycleTotal,
									   Long billingCycleAvailable,
									   Long dailyLimit,
									   Long weeklyLimit,
									   Timestamp dailyResetTime,
									   Timestamp weeklyResetTime,
									   Timestamp balanceExpiryTime,
									   Timestamp startTime,
									   Long reservationTime,
									   Timestamp lastUpdateTime,
									   String status,
									   String renewalInterval,
									   String productOfferId,
                                       String chargingType) {
        this.id = id;
        this.subscriberId = subscriberId;
        this.packageId = packageId;
        this.subscriptionId = subscriptionId;
        this.ratecardId = ratecardId;
        this.billingCycleTotal = billingCycleTotal;
        this.billingCycleAvailable = billingCycleAvailable;
        this.dailyLimit = dailyLimit;
        this.weeklyLimit = weeklyLimit;
        this.dailyResetTime = dailyResetTime;
        this.weeklyResetTime = weeklyResetTime;
        this.balanceExpiryTime = balanceExpiryTime;
        this.startTime = startTime;
        this.reservationTime = reservationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.status = status;
        this.renewalInterval = renewalInterval;
        this.productOfferId = productOfferId;
        this.chargingType = chargingType;
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


    @Column(name = "SUBSCRIBER_ID", nullable = true, length = 255)
    @com.elitecore.commons.kpi.annotation.Column(name = "SUBSCRIBER_ID", type = Types.VARCHAR)
    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    
    @Column(name = "PACKAGE_ID", nullable = true, length = 36)
    @com.elitecore.commons.kpi.annotation.Column(name = "PACKAGE_ID", type = Types.VARCHAR)
    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
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
    @Column(name = "SUBSCRIPTION_ID", nullable = true, length = 36)
    @com.elitecore.commons.kpi.annotation.Column(name = "SUBSCRIPTION_ID", type = Types.VARCHAR)
    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    @Basic
    @Column(name = "RATECARD_ID", nullable = true, length = 36)
    @com.elitecore.commons.kpi.annotation.Column(name = "RATECARD_ID", type = Types.VARCHAR)
    public String getRatecardId() {
        return ratecardId;
    }

    public void setRatecardId(String ratecardId) {
        this.ratecardId = ratecardId;
    }

    @Basic
    @Column(name = "BILLING_CYCLE_TOTAL", nullable = true, precision = 0)
    @com.elitecore.commons.kpi.annotation.Column(name = "BILLING_CYCLE_TOTAL", type = Types.NUMERIC)
    public Long getBillingCycleTotal() {
        return billingCycleTotal;
    }

    public void setBillingCycleTotal(Long billingCycleTotal) {
        this.billingCycleTotal = billingCycleTotal;
    }

    @Basic
    @Column(name = "DAILY_LIMIT", nullable = true, precision = 0)
    @com.elitecore.commons.kpi.annotation.Column(name = "DAILY_LIMIT", type = Types.NUMERIC)
    public Long getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(Long dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    @Basic
    @Column(name = "WEEKLY_LIMIT", nullable = true, precision = 0)
    @com.elitecore.commons.kpi.annotation.Column(name = "WEEKLY_LIMIT", type = Types.NUMERIC)
    public Long getWeeklyLimit() {
        return weeklyLimit;
    }

    public void setWeeklyLimit(Long weeklyLimit) {
        this.weeklyLimit = weeklyLimit;
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
    @Column(name = "BALANCE_EXPIRY_TIME", nullable = true)
    @com.elitecore.commons.kpi.annotation.Column(name = "BALANCE_EXPIRY_TIME", type = Types.TIMESTAMP)
    public Timestamp getBalanceExpiryTime() {
        return balanceExpiryTime;
    }

    public void setBalanceExpiryTime(Timestamp balanceExpiryTime) {
        this.balanceExpiryTime = balanceExpiryTime;
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
    @Column(name = "BILLING_CYCLE_AVAIL", nullable = true, precision = 0)
    @com.elitecore.commons.kpi.annotation.Column(name = "BILLING_CYCLE_AVAIL", type = Types.NUMERIC)
    public Long getBillingCycleAvailable() {
        return billingCycleAvailable;
    }

    public void setBillingCycleAvailable(Long billingCycleAvailable) {
        this.billingCycleAvailable = billingCycleAvailable;
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

	@Column(name = "CREATE_DATE", nullable = true, length = 36)
	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

    @Basic
    @Column(name = "CHARGING_TYPE", nullable = true, length = 36)
    @com.elitecore.commons.kpi.annotation.Column(name = "CHARGING_TYPE", type = Types.VARCHAR)
    public String getChargingType() {
        return chargingType;
    }

    public void setChargingType(String chargingType) {
        this.chargingType = chargingType;
    }

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return false;
    }

	public static String createTableQuery() {
		return QueryBuilder.buildCreateQuery(TblmRnCBalanceHistoryEntity.class);
	}

	public static String dropTableQuery() {
		return QueryBuilder.buildDropQuery(TblmDataBalanceEntity.class);
	}
}
