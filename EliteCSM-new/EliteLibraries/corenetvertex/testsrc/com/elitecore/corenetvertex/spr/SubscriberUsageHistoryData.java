package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.corenetvertex.spr.SubscriberUsage.SubscriberUsageBuilder;
import com.elitecore.corenetvertex.spr.data.DMLProvider;
import com.elitecore.corenetvertex.util.QueryBuilder;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity
@Table(name="TBLT_USAGE_HISTORY")
@javax.persistence.Table(name = "TBLT_USAGE_HISTORY")
public class SubscriberUsageHistoryData{

	private String id;
	private String quotaProfileId;

	private String subscriberIdentity;
	private String serviceId;
	private String subscriptionId;

	private long billingCycleTotal;
    private long billingCycleDownload;
    private long billingCycleUpload;
    private long billingCycleTime;

    private long dailyTotal;
    private long dailyDownload;
    private long dailyUpload;
    private long dailyTime;

    private long weeklyTotal;
    private long weeklyDownload;
    private long weeklyUpload;
    private long weeklyTime;

    private long customTotal;
    private long customDownload;
    private long customUpload;
    private long customTime;

	private long customResetTime;
	private long dailyResetTime;
	private long weeklyResetTime;
	private long billingCycleResetTime;

	private long createDate;

	private long lastUpdateTime = System.currentTimeMillis();
	private String packageId;
	private String productOfferId;

	public SubscriberUsageHistoryData() {
	}

	@Id
	@javax.persistence.Column(name = "ID", nullable = false, length = 36)
	@Column(name = "ID", type = Types.VARCHAR)
	public String getId() {
		return id;
	}

	@javax.persistence.Column(name = "QUOTA_PROFILE_ID", nullable = false, length = 36)
	@Column(name = "QUOTA_PROFILE_ID", type = Types.VARCHAR)
	public String getQuotaProfileId() {
		return quotaProfileId;
	}

	@javax.persistence.Column(name = "SUBSCRIBER_ID", nullable = false, length = 36)
	@Column(name = "SUBSCRIBER_ID", type = Types.VARCHAR)
	public String getSubscriberIdentity() {
		return subscriberIdentity;
	}

	@javax.persistence.Column(name = "SERVICE_ID", nullable = false, length = 36)
	@Column(name = "SERVICE_ID", type = Types.VARCHAR)
	public String getServiceId() {
		return serviceId;
	}

	@javax.persistence.Column(name = "SUBSCRIPTION_ID", nullable = true, length = 36)
	@Column(name = "SUBSCRIPTION_ID", type = Types.VARCHAR)
	public String getSubscriptionId() {
		return subscriptionId;
	}

	@javax.persistence.Column(name = "BILLING_CYCLE_TOTAL", nullable = true)
	@Column(name = "BILLING_CYCLE_TOTAL", type = Types.NUMERIC)
	public long getBillingCycleTotal() {
		return billingCycleTotal;
	}

	@javax.persistence.Column(name = "BILLING_CYCLE_DOWNLOAD", nullable = true)
	@Column(name = "BILLING_CYCLE_DOWNLOAD", type = Types.NUMERIC)
	public long getBillingCycleDownload() {
		return billingCycleDownload;
	}

	@javax.persistence.Column(name = "BILLING_CYCLE_UPLOAD", nullable = true)
	@Column(name = "BILLING_CYCLE_UPLOAD", type = Types.NUMERIC)
	public long getBillingCycleUpload() {
		return billingCycleUpload;
	}

	@javax.persistence.Column(name = "BILLING_CYCLE_TIME", nullable = true)
	@Column(name = "BILLING_CYCLE_TIME", type = Types.NUMERIC)
	public long getBillingCycleTime() {
		return billingCycleTime;
	}

	@javax.persistence.Column(name = "DAILY_TOTAL", nullable = true)
	@Column(name = "DAILY_TOTAL", type = Types.NUMERIC)
	public long getDailyTotal() {
		return dailyTotal;
	}

	@javax.persistence.Column(name = "DAILY_DOWNLOAD", nullable = true)
	@Column(name = "DAILY_DOWNLOAD", type = Types.NUMERIC)
	public long getDailyDownload() {
		return dailyDownload;
	}

	@javax.persistence.Column(name = "DAILY_UPLOAD", nullable = true)
	@Column(name = "DAILY_UPLOAD", type = Types.NUMERIC)
	public long getDailyUpload() {
		return dailyUpload;
	}

	@javax.persistence.Column(name = "DAILY_TIME", nullable = true)
	@Column(name = "DAILY_TIME", type = Types.NUMERIC)
	public long getDailyTime() {
		return dailyTime;
	}

	@javax.persistence.Column(name = "WEEKLY_TOTAL", nullable = true)
	@Column(name = "WEEKLY_TOTAL", type = Types.NUMERIC)
	public long getWeeklyTotal() {
		return weeklyTotal;
	}

	@javax.persistence.Column(name = "WEEKLY_DOWNLOAD", nullable = true)
	@Column(name = "WEEKLY_DOWNLOAD", type = Types.NUMERIC)
	public long getWeeklyDownload() {
		return weeklyDownload;
	}

	@javax.persistence.Column(name = "WEEKLY_UPLOAD", nullable = true)
	@Column(name = "WEEKLY_UPLOAD", type = Types.NUMERIC)
	public long getWeeklyUpload() {
		return weeklyUpload;
	}

	@javax.persistence.Column(name = "WEEKLY_TIME", nullable = true)
	@Column(name = "WEEKLY_TIME", type = Types.NUMERIC)
	public long getWeeklyTime() {
		return weeklyTime;
	}

	@javax.persistence.Column(name = "CUSTOM_TOTAL", nullable = true)
	@Column(name = "CUSTOM_TOTAL", type = Types.NUMERIC)
	public long getCustomTotal() {
		return customTotal;
	}

	@javax.persistence.Column(name = "CUSTOM_DOWNLOAD", nullable = true)
	@Column(name = "CUSTOM_DOWNLOAD", type = Types.NUMERIC)
	public long getCustomDownload() {
		return customDownload;
	}

	@javax.persistence.Column(name = "CUSTOM_UPLOAD", nullable = true)
	@Column(name = "CUSTOM_UPLOAD", type = Types.NUMERIC)
	public long getCustomUpload() {
		return customUpload;
	}

	@javax.persistence.Column(name = "CUSTOM_TIME", nullable = true)
	@Column(name = "CUSTOM_TIME", type = Types.NUMERIC)
	public long getCustomTime() {
		return customTime;
	}


	@Transient
	public long getCustomResetTime() {
		return customResetTime;
	}

	@javax.persistence.Column(name = "CUSTOM_RESET_TIME", nullable = true)
	@Column(name = "CUSTOM_RESET_TIME", type = Types.TIMESTAMP)
	public String getCustomResetDate() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(customResetTime));
	}

	@Transient
	public long getDailyResetTime() {
		return dailyResetTime;
	}

	@javax.persistence.Column(name = "DAILY_RESET_TIME", nullable = true)
	@Column(name = "DAILY_RESET_TIME", type = Types.TIMESTAMP)
	public String getDailyResetTimestamp() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(dailyResetTime));
	}

	@Transient
	public long getCreateDate() {
		return createDate;
	}

	@javax.persistence.Column(name = "CREATE_DATE", nullable = true)
	@Column(name = "CREATE_DATE", type = Types.TIMESTAMP)
	public String getCreateDateTimestamp() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(createDate));
	}


	@Transient
	public long getWeeklyResetTime() {
		return weeklyResetTime;
	}

	@javax.persistence.Column(name = "WEEKLY_RESET_TIME", nullable = true)
	@Column(name = "WEEKLY_RESET_TIME", type = Types.TIMESTAMP)
	public String getWeeklyResetTimestamp() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(weeklyResetTime));
	}

	@Transient
	public long getBillingCycleResetTime() {
		return billingCycleResetTime;
	}

	@javax.persistence.Column(name = "BILLING_CYCLE_RESET_TIME", nullable = true)
	@Column(name = "BILLING_CYCLE_RESET_TIME", type = Types.TIMESTAMP)
	public String getBillingCycleResetTimestamp() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(billingCycleResetTime));
	}

	@Transient
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	@javax.persistence.Column(name = "LAST_UPDATE_TIME", nullable = true)
	@Column(name = "LAST_UPDATE_TIME", type = Types.TIMESTAMP)
	public String getLastUpdateTimestamp() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(lastUpdateTime));
	}

	public SubscriberUsageHistoryData setLastUpdateTimestamp(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
		return this;
	}

	@javax.persistence.Column(name = "PACKAGE_ID", nullable = true)
	@Column(name = "PACKAGE_ID", type = Types.VARCHAR)
	public String getPackageId() {
		return packageId;
	}

	public void setCustomResetTime(long customResetTime) {
		this.customResetTime = customResetTime;
	}

	public void setDailyResetTime(long dailyResetTime) {
		this.dailyResetTime = dailyResetTime;
	}

	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}

	public void setWeeklyResetTime(long weeklyResetTime) {
		this.weeklyResetTime = weeklyResetTime;
	}

	public void setBillingCycleResetTime(long billingCycleResetTime) {
		this.billingCycleResetTime = billingCycleResetTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}


	public void setBillingCycleTotal(long billingCycleTotal) {
		this.billingCycleTotal = billingCycleTotal;
	}

	public void setBillingCycleDownload(long billingCycleDownload) {
		this.billingCycleDownload = billingCycleDownload;
	}

	public void setBillingCycleUpload(long billingCycleUpload) {
		this.billingCycleUpload = billingCycleUpload;
	}

	public void setBillingCycleTime(long billingCycleTime) {
		this.billingCycleTime = billingCycleTime;
	}

	public void setDailyTotal(long dailyTotal) {
		this.dailyTotal = dailyTotal;
	}

	public void setDailyDownload(long dailyDownload) {
		this.dailyDownload = dailyDownload;
	}

	public void setDailyUpload(long dailyUpload) {
		this.dailyUpload = dailyUpload;
	}

	public void setDailyTime(long dailyTime) {
		this.dailyTime = dailyTime;
	}

	public void setWeeklyTotal(long weeklyTotal) {
		this.weeklyTotal = weeklyTotal;
	}

	public void setWeeklyDownload(long weeklyDownload) {
		this.weeklyDownload = weeklyDownload;
	}

	public void setWeeklyUpload(long weeklyUpload) {
		this.weeklyUpload = weeklyUpload;
	}

	public void setWeeklyTime(long weeklyTime) {
		this.weeklyTime = weeklyTime;
	}

	public void setCustomTotal(long customTotal) {
		this.customTotal = customTotal;
	}

	public void setCustomDownload(long customDownload) {
		this.customDownload = customDownload;
	}

	public void setCustomUpload(long customUpload) {
		this.customUpload = customUpload;
	}

	public void setCustomTime(long customTime) {
		this.customTime = customTime;
	}

	public void setCustomResetDate(long customResetTime) {
		this.customResetTime = customResetTime;
	}

	public void setDailyResetTimestamp(long dailyResetTime) {
		this.dailyResetTime = dailyResetTime;
	}

	public void setCreateDateTimestamp(long dailyResetTime) {
		this.dailyResetTime = dailyResetTime;
	}

	public void setWeeklyResetTimestamp(long weeklyResetTime) {
		this.weeklyResetTime = weeklyResetTime;
	}

	public void setBillingCycleResetTimestamp(long billingCycleResetTime) {
		this.billingCycleResetTime = billingCycleResetTime;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setQuotaProfileId(String quotaProfileId) {
		this.quotaProfileId = quotaProfileId;
	}

	public void setSubscriberIdentity(String subscriberIdentity) {
		this.subscriberIdentity = subscriberIdentity;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@javax.persistence.Column(name = "PRODUCT_OFFER_ID", nullable = false, length = 36)
	@Column(name = "PRODUCT_OFFER_ID", type = Types.VARCHAR)
	public String getProductOfferId() {
		return productOfferId;
	}

	public void setProductOfferId(String productOfferId) {
		this.productOfferId = productOfferId;
	}
}
