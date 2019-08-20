package com.elitecore.corenetvertex.spr;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.corenetvertex.spr.SubscriberUsage.SubscriberUsageBuilder;
import com.elitecore.corenetvertex.spr.data.DMLProvider;
import com.elitecore.corenetvertex.util.QueryBuilder;
import sun.security.pkcs.ParsingException;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
@Table(name="TBLT_USAGE")
@javax.persistence.Table(name = "TBLT_USAGE")
public class SubscriberUsageData implements DMLProvider{
	
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
	
	private long lastUpdateTime = System.currentTimeMillis();
	private String packageId;
	private String productOfferId;

	public SubscriberUsageData() {
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
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(customResetTime));
	}

	@Transient
	public long getDailyResetTime() {
		return dailyResetTime;
	}

	@javax.persistence.Column(name = "DAILY_RESET_TIME", nullable = true)
	@Column(name = "DAILY_RESET_TIME", type = Types.TIMESTAMP)
	public String getDailyResetTimestamp() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Timestamp(dailyResetTime));
	}


	@Transient
	public long getWeeklyResetTime() {
		return weeklyResetTime;
	}

	@javax.persistence.Column(name = "WEEKLY_RESET_TIME", nullable = true)
	@Column(name = "WEEKLY_RESET_TIME", type = Types.TIMESTAMP)
	public String getWeeklyResetTimestamp() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Timestamp(weeklyResetTime));
	}

	@Transient
	public long getBillingCycleResetTime() {
		return billingCycleResetTime;
	}

	@javax.persistence.Column(name = "BILLING_CYCLE_RESET_TIME", nullable = true)
	@Column(name = "BILLING_CYCLE_RESET_TIME", type = Types.TIMESTAMP)
	public String getBillingCycleResetTimestamp() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Timestamp(billingCycleResetTime));
	}

	@Transient
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	@javax.persistence.Column(name = "LAST_UPDATE_TIME", nullable = true)
	@Column(name = "LAST_UPDATE_TIME", type = Types.TIMESTAMP)
	public String getLastUpdateTimestamp() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Timestamp(lastUpdateTime));
	}

	public SubscriberUsageData setLastUpdateTimestamp(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
		return this;
	}

	public void setLastUpdateTimestamp(String lastUpdateTime) throws ParseException{
		this.lastUpdateTime= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(lastUpdateTime).getTime();
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


	public SubscriberUsageData setBillingCycleTotal(long billingCycleTotal) {
		this.billingCycleTotal = billingCycleTotal;
		return this;
	}

	public SubscriberUsageData setBillingCycleDownload(long billingCycleDownload) {
		this.billingCycleDownload = billingCycleDownload;
		return this;
	}

	public SubscriberUsageData setBillingCycleUpload(long billingCycleUpload) {
		this.billingCycleUpload = billingCycleUpload;
		return this;
	}

	public SubscriberUsageData setBillingCycleTime(long billingCycleTime) {
		this.billingCycleTime = billingCycleTime;
		return this;
	}

	public SubscriberUsageData setDailyTotal(long dailyTotal) {
		this.dailyTotal = dailyTotal;
		return this;
	}

	public SubscriberUsageData setDailyDownload(long dailyDownload) {
		this.dailyDownload = dailyDownload;
		return this;
	}

	public SubscriberUsageData setDailyUpload(long dailyUpload) {
		this.dailyUpload = dailyUpload;
		return this;
	}

	public SubscriberUsageData setDailyTime(long dailyTime) {
		this.dailyTime = dailyTime;
		return this;
	}

	public SubscriberUsageData setWeeklyTotal(long weeklyTotal) {
		this.weeklyTotal = weeklyTotal;
		return this;
	}

	public SubscriberUsageData setWeeklyDownload(long weeklyDownload) {
		this.weeklyDownload = weeklyDownload;
		return this;
	}

	public SubscriberUsageData setWeeklyUpload(long weeklyUpload) {
		this.weeklyUpload = weeklyUpload;
		return this;
	}

	public SubscriberUsageData setWeeklyTime(long weeklyTime) {
		this.weeklyTime = weeklyTime;
		return this;
	}

	public SubscriberUsageData setCustomTotal(long customTotal) {
		this.customTotal = customTotal;
		return this;
	}

	public SubscriberUsageData setCustomDownload(long customDownload) {
		this.customDownload = customDownload;
		return this;
	}

	public SubscriberUsageData setCustomUpload(long customUpload) {
		this.customUpload = customUpload;
		return this;
	}

	public SubscriberUsageData setCustomTime(long customTime) {
		this.customTime = customTime;
		return this;
	}

	public SubscriberUsageData setCustomResetDate(long customResetTime) {
		this.customResetTime = customResetTime;
		return this;
	}

	public void setCustomResetDate(String billingCycleResetTime) throws ParseException{
		this.customResetTime= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(billingCycleResetTime).getTime();
	}

	public SubscriberUsageData setDailyResetTimestamp(long dailyResetTime) {
		this.dailyResetTime = dailyResetTime;
		return this;
	}

	public void setDailyResetTimestamp(String billingCycleResetTime) throws ParseException{
		this.dailyResetTime= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(billingCycleResetTime).getTime();
	}

	public SubscriberUsageData setWeeklyResetTimestamp(long weeklyResetTime) {
		this.weeklyResetTime = weeklyResetTime;
		return this;
	}

	public void setWeeklyResetTimestamp(String billingCycleResetTime) throws ParseException{
		this.weeklyResetTime= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(billingCycleResetTime).getTime();
	}

	public SubscriberUsageData setBillingCycleResetTimestamp(long billingCycleResetTime) {
		this.billingCycleResetTime = billingCycleResetTime;
		return this;
	}

	public void setBillingCycleResetTimestamp(String billingCycleResetTime) throws ParseException{
		this.billingCycleResetTime= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(billingCycleResetTime).getTime();
	}

	public SubscriberUsageData setId(String id) {
		this.id = id;
		return this;
	}

	public SubscriberUsageData setQuotaProfileId(String quotaProfileId) {
		this.quotaProfileId = quotaProfileId;
		return this;
	}

	public SubscriberUsageData setSubscriberIdentity(String subscriberIdentity) {
		this.subscriberIdentity = subscriberIdentity;
		return this;
	}

	public SubscriberUsageData setServiceId(String serviceId) {
		this.serviceId = serviceId;
		return this;
	}

	public SubscriberUsageData setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
		return this;
	}
	

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			SubscriberUsageData subscriberUsage = new SubscriberUsageData();
			subscriberUsage.id = id;
			subscriberUsage.quotaProfileId = quotaProfileId;
			subscriberUsage.subscriberIdentity = subscriberIdentity;
			subscriberUsage.serviceId = serviceId;
			subscriberUsage.subscriptionId = subscriptionId;
			subscriberUsage.billingCycleTotal = billingCycleTotal;
			subscriberUsage.billingCycleDownload = billingCycleDownload;
			subscriberUsage.billingCycleUpload = billingCycleUpload;
			subscriberUsage.billingCycleTime = billingCycleTime;
			subscriberUsage.dailyTotal = dailyTotal;
			subscriberUsage.dailyDownload = dailyDownload;
			subscriberUsage.dailyUpload = dailyUpload;
			subscriberUsage.dailyTime = dailyTime;
			subscriberUsage.weeklyTotal = weeklyTotal;
			subscriberUsage.weeklyDownload = weeklyDownload;
			subscriberUsage.weeklyUpload = weeklyUpload;
			subscriberUsage.weeklyTime = weeklyTime;
			subscriberUsage.customTotal = customTotal;
			subscriberUsage.customDownload = customDownload;
			subscriberUsage.customUpload = customUpload;
			subscriberUsage.customTime = customTime;
			subscriberUsage.customResetTime = customResetTime;
			subscriberUsage.dailyResetTime = dailyResetTime;
			subscriberUsage.weeklyResetTime = weeklyResetTime;
			subscriberUsage.billingCycleResetTime = billingCycleResetTime;
			subscriberUsage.productOfferId = productOfferId;
			
			return subscriberUsage;
		}
	}

	@VisibleForTesting
	@Transient
	Calendar getCurrentDate() {
		return Calendar.getInstance();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubscriberUsageData other = (SubscriberUsageData) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		
		
		StringWriter stringWriter = new StringWriter();
		IndentingPrintWriter out = new IndentingPrintWriter(new PrintWriter(stringWriter));
		
		out.println(" -- SubscriberUsage -- ");
		out.incrementIndentation();
		out.println("ID = " + id); 
		out.println("QuotaProfile ID = " + quotaProfileId);
		out.println("Subscriber Identity = " + subscriberIdentity);
		out.println("Service ID = " + serviceId);
		out.println("SubscriptionId = " + subscriptionId);
		out.println("Billing Cycle Total = " + billingCycleTotal);
		out.println("BillingCycleDownload = " + billingCycleDownload);
		out.println("BillingCycleUpload = " + billingCycleUpload);
		out.println("BillingCycleTime = " + billingCycleTime);
		out.println("DailyTotal = " + dailyTotal);
		out.println("DailyDownload = " + dailyDownload);
		out.println("DailyUpload = " + dailyUpload);
		out.println("DailyTime=" + dailyTime); 
		out.println("WeeklyTotal = " + weeklyTotal);
		out.println("WeeklyDownload = " + weeklyDownload);
		out.println("WeeklyUpload = " + weeklyUpload);
		out.println("WeeklyTime = " + weeklyTime);
		out.println("CustomTotal = " + customTotal);
		out.println("CustomDownload = " + customDownload);
		out.println("CustomUpload = " + customUpload);
		out.println("CustomTime = " + customTime);
		out.println("CustomResetTime = " + customResetTime);
		out.println("DailyResetTime = " + dailyResetTime);
		out.println("WeeklyResetTime = " + weeklyResetTime);
		out.println("BillingCycleResetTime = " + billingCycleResetTime);
		out.println("Product Offer ID = " + productOfferId);
		out.decrementIndentation();
		
		out.close();
		
		return stringWriter.toString();
	}

	public static String createTableQuery() {
		return QueryBuilder.buildCreateQuery(SubscriberUsageData.class);
	}

	@Transient
	public String insertQuery() throws IllegalArgumentException, NullPointerException, IllegalAccessException, InvocationTargetException {
		return QueryBuilder.buildInsertQuery(this);
	}

	public SubscriberUsage newSubscriberUsage() {
		SubscriberUsageBuilder subscriberUsageBuilder = new SubscriberUsage.SubscriberUsageBuilder(id, subscriberIdentity, serviceId, quotaProfileId, getPackageId(),getProductOfferId());
		return subscriberUsageBuilder.withSubscriptionId(subscriptionId).
		withBillingCycleTotalUsage(billingCycleTotal).
		withBillingCycleDownlodUsage(billingCycleDownload).
		withBillingCycleUploadUsage(billingCycleUpload).
		withBillingCycleTime(billingCycleTime).
		withDailyTotalUsage(dailyTotal).
		withDailyDownlodUsage(dailyDownload).
		withDailyUploadUsage(dailyUpload).
		withDailyTime(dailyTime).
		withWeeklyTotalUsage(weeklyTotal).
		withWeeklyDownlodUsage(weeklyDownload).
		withWeeklyUploadUsage(weeklyUpload).
		withWeeklyTime(weeklyTime).
		withCustomTotalUsage(customTotal).
		withCustomDownlodUsage(customDownload).
		withCustomUploadUsage(customUpload).
		withCustomTime(customTime).
		withCustomResetTime(customResetTime).
		withDailyResetTime(dailyResetTime).
		withWeeklyResetTime(weeklyResetTime).
		withBillingCycleResetTime(billingCycleResetTime).build();
	}

	@javax.persistence.Column(name = "PRODUCT_OFFER_ID", nullable = false, length = 36)
	@Column(name = "PRODUCT_OFFER_ID", type = Types.VARCHAR)
	public String getProductOfferId() {
		return productOfferId;
	}

	public void setProductOfferId(String productOfferId) {
		this.productOfferId = productOfferId;
	}


	public static class SubscriberUsageDataBuilder {
		private SubscriberUsageData subscriberUsageData;

		public SubscriberUsageDataBuilder() {
			subscriberUsageData = new SubscriberUsageData();
		}

		public SubscriberUsageData build() {
			return subscriberUsageData;
		}

		
		public SubscriberUsageDataBuilder withBillingCycleTotal(long aValue) {
			subscriberUsageData.setBillingCycleTotal(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withBillingCycleDownload(long aValue) {
			subscriberUsageData.setBillingCycleDownload(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withBillingCycleUpload(long aValue) {
			subscriberUsageData.setBillingCycleUpload(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withBillingCycleTime(long aValue) {
			subscriberUsageData.setBillingCycleTime(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withDailyTotal(long aValue) {
			subscriberUsageData.setDailyTotal(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withDailyDownload(long aValue) {
			subscriberUsageData.setDailyDownload(aValue);

			return  this;
		}

		public SubscriberUsageDataBuilder withPackageId(String packageId) {
			subscriberUsageData.setPackageId(packageId);;

			return  this;
		}

		public SubscriberUsageDataBuilder withDailyUpload(long aValue) {
			subscriberUsageData.setDailyUpload(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withDailyTime(long aValue) {
			subscriberUsageData.setDailyTime(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withWeeklyTotal(long aValue) {
			subscriberUsageData.setWeeklyTotal(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withWeeklyDownload(long aValue) {
			subscriberUsageData.setWeeklyDownload(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withWeeklyUpload(long aValue) {
			subscriberUsageData.setWeeklyUpload(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withWeeklyTime(long aValue) {
			subscriberUsageData.setWeeklyTime(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withCustomTotal(long aValue) {
			subscriberUsageData.setCustomTotal(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withCustomDownload(long aValue) {
			subscriberUsageData.setCustomDownload(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withCustomUpload(long aValue) {
			subscriberUsageData.setCustomUpload(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withCustomTime(long aValue) {
			subscriberUsageData.setCustomTime(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withCustomResetTime(long aValue) {
			subscriberUsageData.setCustomResetDate(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withDailyResetTime(long aValue) {
			subscriberUsageData.setDailyResetTimestamp(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withWeeklyResetTime(long aValue) {
			subscriberUsageData.setWeeklyResetTimestamp(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withBillingCycleResetTime(long aValue) {
			subscriberUsageData.setBillingCycleResetTimestamp(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withId(String aValue) {
			subscriberUsageData.setId(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withQuotaProfileId(String aValue) {
			subscriberUsageData.setQuotaProfileId(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withSubscriberIdentity(String aValue) {
			subscriberUsageData.setSubscriberIdentity(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withServiceId(String aValue) {
			subscriberUsageData.setServiceId(aValue);

			return  this;
		}

		
		public SubscriberUsageDataBuilder withSubscriptionId(String aValue) {
			subscriberUsageData.setSubscriptionId(aValue);

			return  this;
		}

		public SubscriberUsageDataBuilder withProductOfferId(String productOfferId) {
			subscriberUsageData.setProductOfferId(productOfferId);

			return  this;
		}


		public  SubscriberUsageDataBuilder withDefaultUsage(long val){
			withBillingCycleDownload(val)
        	.withBillingCycleUpload(val)
        	.withBillingCycleTotal(val)
        	.withBillingCycleTime(val)
        	

        	.withDailyDownload(val)
        	.withDailyUpload(val)
        	.withDailyTotal(val)
        	.withDailyTime(val)

    		.withWeeklyDownload(val)
    		.withWeeklyUpload(val)
    		.withWeeklyTotal(val)
    		.withWeeklyTime(val)
    
    		.withCustomDownload(val)
    		.withCustomUpload(val)
    		.withCustomTotal(val)
    		.withCustomTime(val);
			return this;
		}
		
		public  SubscriberUsageDataBuilder withDefaultReSetDate(long resetTime){
        	withBillingCycleResetTime(resetTime)
        	.withDailyResetTime(resetTime)
    		.withWeeklyResetTime(resetTime)
    		.withCustomResetTime(resetTime);
			return this;
		}
	}


	public static String dropTableQuery() {
		return QueryBuilder.buildDropQuery(SubscriberUsageData.class);
	}

}
