package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.corenetvertex.constants.AggregationKey;

import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Date;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class SubscriberUsage implements Cloneable {


	public static final String NEW_ID = "0";

	private static final String MODULE = "SUBSCRIBER-USAGE";
	
	private final String id;
	private final String quotaProfileId;
	private final String serviceId;
	private final String packageId;

	private String subscriberIdentity;
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
	private String productOfferId;



	public SubscriberUsage(String id, String quotaProfileId, String subscriberIdentity, String serviceId, String subscriptionId,
						   String packageId, String productOfferId, long billingCycleTotal, long billingCycleDownload, long billingCycleUpload, long billingCycleTime, long dailyTotal, long dailyDownload,
						   long dailyUpload, long dailyTime, long weeklyTotal, long weeklyDownload, long weeklyUpload, long weeklyTime, long customTotal,
						   long customDownload, long customUpload, long customTime, long customResetTime, long dailyResetTime, long weeklyResetTime,
						   long billingCycleResetTime) {
		super();
		this.id = id;
		this.quotaProfileId = quotaProfileId;
		this.subscriberIdentity = subscriberIdentity;
		this.serviceId = serviceId;
		this.subscriptionId = subscriptionId;
		this.packageId = packageId;
		this.billingCycleTotal = billingCycleTotal;
		this.billingCycleDownload = billingCycleDownload;
		this.billingCycleUpload = billingCycleUpload;
		this.billingCycleTime = billingCycleTime;
		this.dailyTotal = dailyTotal;
		this.dailyDownload = dailyDownload;
		this.dailyUpload = dailyUpload;
		this.dailyTime = dailyTime;
		this.weeklyTotal = weeklyTotal;
		this.weeklyDownload = weeklyDownload;
		this.weeklyUpload = weeklyUpload;
		this.weeklyTime = weeklyTime;
		this.customTotal = customTotal;
		this.customDownload = customDownload;
		this.customUpload = customUpload;
		this.customTime = customTime;
		this.customResetTime = customResetTime;
		this.dailyResetTime = dailyResetTime;
		this.weeklyResetTime = weeklyResetTime;
		this.billingCycleResetTime = billingCycleResetTime;
		this.productOfferId = productOfferId;
	}

	public SubscriberUsage(String id, String quotaProfileId, String subscriberIdentity, String serviceId, @Nullable String subscriptionId, String packageId, String productOfferId) {
		super();
		this.id = id;
		this.quotaProfileId = quotaProfileId;
		this.subscriberIdentity = subscriberIdentity;
		this.serviceId = serviceId;
		this.subscriptionId = subscriptionId;
		this.packageId = packageId;
		this.productOfferId = productOfferId;
	}

	public SubscriberUsage(SubscriberUsage subscriberUsage) {
		this(subscriberUsage.getId(), subscriberUsage.getQuotaProfileId(), subscriberUsage.getSubscriberIdentity(), 
				subscriberUsage.getServiceId(), subscriberUsage.getSubscriptionId(), subscriberUsage.getPackageId(),subscriberUsage.getProductOfferId());
		
		this.billingCycleTotal = subscriberUsage.billingCycleTotal;
		this.billingCycleDownload = subscriberUsage.billingCycleDownload;
		this.billingCycleUpload = subscriberUsage.billingCycleUpload;
		this.billingCycleTime = subscriberUsage.billingCycleTime;
		this.dailyTotal = subscriberUsage.dailyTotal;
		this.dailyDownload = subscriberUsage.dailyDownload;
		this.dailyUpload = subscriberUsage.dailyUpload;
		this.dailyTime = subscriberUsage.dailyTime;
		this.weeklyTotal = subscriberUsage.weeklyTotal;
		this.weeklyDownload = subscriberUsage.weeklyDownload;
		this.weeklyUpload = subscriberUsage.weeklyUpload;
		this.weeklyTime = subscriberUsage.weeklyTime;
		this.customTotal = subscriberUsage.customTotal;
		this.customDownload = subscriberUsage.customDownload;
		this.customUpload = subscriberUsage.customUpload;
		this.customTime = subscriberUsage.customTime;
		this.customResetTime = subscriberUsage.customResetTime;
		this.dailyResetTime = subscriberUsage.dailyResetTime;
		this.weeklyResetTime = subscriberUsage.weeklyResetTime;
		this.billingCycleResetTime = subscriberUsage.billingCycleResetTime;
		this.productOfferId = subscriberUsage.productOfferId;
	}

	public String getId() {
		return id;
	}

	public String getQuotaProfileId() {
		return quotaProfileId;
	}

	public String getSubscriberIdentity() {
		return subscriberIdentity;
	}

	public String getServiceId() {
		return serviceId;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}
	
	
	public long getBillingCycleTotal() {
		
		return billingCycleTotal;
		
	}

	public long getBillingCycleDownload() {
		
		return billingCycleDownload;
		
	}

	public long getBillingCycleUpload() {
			return billingCycleUpload;
		
	}

	public long getBillingCycleTime() {
			return billingCycleTime;
		
	}

	public long getDailyTotal() {
		return dailyTotal;
	}

	public long getDailyDownload() {
		return dailyDownload;
	}

	public long getDailyUpload() {
		return dailyUpload;
	}

	public long getDailyTime() {
		return dailyTime;
	}

	public long getWeeklyTotal() {
		return weeklyTotal;
	}

	public long getWeeklyDownload() {
		return weeklyDownload;
	}

	public long getWeeklyUpload() {
		return weeklyUpload;
	}

	public long getWeeklyTime() {
		return weeklyTime;
	}

	public long getCustomTotal() {
		return customTotal;
	}

	public long getCustomDownload() {
		return customDownload;
	}

	public long getCustomUpload() {
		return customUpload;
	}

	public long getCustomTime() {
		return customTime;
	}

	public long getCustomResetTime() {
		return customResetTime;
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
	
	public void setSubscriberIdentity(String subscriberIdentity) {
		this.subscriberIdentity = subscriberIdentity;
	}
	
	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public long getTotal(AggregationKey aggregationKey){
		long total = 0;
		switch(aggregationKey){
		case BILLING_CYCLE:
			total = getBillingCycleTotal();
			break;
		case CUSTOM:
			total = getCustomTotal();
			break;
		case DAILY:
			total = getDailyTotal();
			break;
		case WEEKLY:
			total = getWeeklyTotal();
			break;
		}
		return total;
	}
	public long getUpload(AggregationKey aggregationKey){
		long upload = 0;
		switch(aggregationKey){
		case BILLING_CYCLE:
			upload = getBillingCycleUpload();
			break;
		case CUSTOM:
			upload = getCustomUpload();
			break;
		case DAILY:
			upload = getDailyUpload();
			break;
		case WEEKLY:
			upload = getWeeklyUpload();
			break;
		}
		return upload;
	}
	
	public long getDownload(AggregationKey aggregationKey){
		long download = 0;
		switch(aggregationKey){
		case BILLING_CYCLE:
			download = getBillingCycleDownload();
			break;
		case CUSTOM:
			download = getCustomDownload();
			break;
		case DAILY:
			download = getDailyDownload();
			break;
		case WEEKLY:
			download = getWeeklyDownload();
			break;
		}
		return download;
	}
	
	public long getTime(AggregationKey aggregationKey){
		long time = 0;
		switch(aggregationKey){
		case BILLING_CYCLE:
			time = getBillingCycleTime();
			break;
		case CUSTOM:
			time = getCustomTime();
			break;
		case DAILY:
			time = getDailyTime();
			break;
		case WEEKLY:
			time = getWeeklyTime();
			break;
		}
		return time;
	}



	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) { 
			SubscriberUsage subscriberUsage = new SubscriberUsage(this.id, this.quotaProfileId,this.subscriberIdentity, this.serviceId, this.subscriptionId, this.packageId,this.productOfferId);
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
			getLogger().trace(MODULE, e);
			return subscriberUsage;
		}
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
		SubscriberUsage other = (SubscriberUsage) obj;
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
		IndentingWriter out = new IndentingPrintWriter(new PrintWriter(stringWriter));
		toString(out);
		out.close();
		return stringWriter.toString();
	}


	public void toString(IndentingWriter out) {



		out.println();
		out.println(" -- SubscriberUsage -- ");
		out.incrementIndentation();
		out.println("ID = " + id); 
		out.println("QuotaProfile ID = " + quotaProfileId);
		out.println("Subscriber Identity = " + subscriberIdentity);
		out.println("Service ID = " + serviceId);
		out.println("Package ID = " + packageId);
		out.println("Product Offer ID = " + productOfferId);
		out.println("SubscriptionId = " + subscriptionId);

		out.println("Billing Cycle = " + billingCycleTotal + "(T) "
				+ billingCycleUpload + "(U) "
				+ billingCycleDownload + "(D) " +
				+billingCycleTime + "(Time) "
				+ new Date(billingCycleResetTime) + "(Reset-time)");
		
		out.println("Daily = " + dailyTotal + "(T) "
				+ dailyUpload + "(U) "
				+ dailyDownload + "(D) "+ 
				+ dailyTime + "(Time) " 
				+ new Date(dailyResetTime) + "(Reset-time)");
		
		out.println("Weekly = " + weeklyTotal + "(T) "
				+ weeklyUpload + "(U) "
				+ weeklyDownload + "(D) "+ 
				+ weeklyTime + "(Time) " 
				+ new Date(weeklyResetTime) + "(Reset-time)");
		
		out.println("Custom = " + customTotal + "(T) "
				+ customUpload + "(U) "
				+ customDownload + "(D) "+ 
				+ customTime + "(Time) " 
				+ new Date(customResetTime) + "(Reset-time)");
		
		out.decrementIndentation();

	}

	public String getPackageId() {
		return packageId;
	}

	public String getProductOfferId() {
		return productOfferId;
	}

	public void setProductOfferId(String productOfferId) {
		this.productOfferId = productOfferId;
	}


	public static class SubscriberUsageBuilder {

		private final String serviceId;
		private final String subscriberIdentity;
		private final String id;
		private final String quotaProfileId;
		private String packageId;
		private String productOfferId;

		private String subscriptionId;

		private long dailyTotal;
		private long dailyDownload;
		private long dailyUpload;
		private long dailyTime;

		private long weeklyTotal;
		private long weeklyDownload;
		private long weeklyUpload;
		private long weeklyTime;

		private long customTotal;
		private long customUpload;
		private long customTime;
		private long customDownload;

		private long billingCycleTotal;
		private long billingCycleDownload;
		private long billingCycleUpload;
		private long billingCycleTime;
		
		private long customResetTime;
		private long dailyResetTime;
		private long weeklyResetTime;
		private long billingCycleResetTime;

		public SubscriberUsageBuilder(String id, 
										String subscriberIdentity, 
										String serviceId,
										String quotaProfileId,
										String packageId,String productOfferId) {
			this.id = id;
			this.subscriberIdentity = subscriberIdentity;
			this.serviceId = serviceId;
			this.quotaProfileId = quotaProfileId;
			this.packageId = packageId;
			this.productOfferId = productOfferId;
		}

		public SubscriberUsageBuilder withSubscriptionId(String subscriptionId) {
			this.subscriptionId = subscriptionId;
			return this;
		}

		public SubscriberUsageBuilder withDailyTotalUsage(long usage) {
			this.dailyTotal = usage;
			return this;
		}

		public SubscriberUsageBuilder withDailyDownlodUsage(long downloadDailyUsage) {
			this.dailyDownload = downloadDailyUsage;
			return this;
		}

		public SubscriberUsageBuilder withDailyUploadUsage(long uploadDailyusage) {
			this.dailyUpload = uploadDailyusage;
			return this;
		}

		public SubscriberUsageBuilder withDailyTime(long dailyTime) {
			this.dailyTime = dailyTime;
			return this;
		}

		public SubscriberUsageBuilder withWeeklyTotalUsage(long totalWeeklyUsage) {
			this.weeklyTotal = totalWeeklyUsage;
			return this;
		}

		public SubscriberUsageBuilder withWeeklyDownlodUsage(long downloadWeeklyUsage) {
			this.weeklyDownload = downloadWeeklyUsage;
			return this;
		}

		public SubscriberUsageBuilder withWeeklyUploadUsage(long uploadWeeklyusage) {
			this.weeklyUpload = uploadWeeklyusage;
			return this;
		}

		public SubscriberUsageBuilder withWeeklyTime(long weeklyTime) {
			this.weeklyTime = weeklyTime;
			return this;
		}

		public SubscriberUsageBuilder withCustomTotalUsage(long customTotlausage) {
			this.customTotal = customTotlausage;
			return this;
		}

		public SubscriberUsageBuilder withCustomDownlodUsage(long downloadCustomUsage) {
			this.customDownload = downloadCustomUsage;
			return this;
		}

		public SubscriberUsageBuilder withCustomUploadUsage(long customUploadUsage) {
			this.customUpload = customUploadUsage;
			return this;
		}

		public SubscriberUsageBuilder withCustomTime(long customTime) {
			this.customTime = customTime;
			return this;
		}

		public SubscriberUsageBuilder withBillingCycleTotalUsage(long billingCycleTotalUsage) {
			this.billingCycleTotal = billingCycleTotalUsage;
			return this;
		}

		public SubscriberUsageBuilder withBillingCycleDownlodUsage(long billingCycleDownloadUsage) {
			this.billingCycleDownload = billingCycleDownloadUsage;
			return this;
		}

		public SubscriberUsageBuilder withBillingCycleUploadUsage(long billingCycleUploadUsage) {
			this.billingCycleUpload = billingCycleUploadUsage;
			return this;
		}

		public SubscriberUsageBuilder withBillingCycleTime(long billingCycleTime) {
			this.billingCycleTime = billingCycleTime;
			return this;
		}
		
		public SubscriberUsageBuilder withBillingCycleResetTime(long billingCycleResetTime) {
			this.billingCycleResetTime = billingCycleResetTime;
			return this;
		}
		
		public SubscriberUsageBuilder withCustomResetTime(long customResetTime) {
			this.customResetTime = customResetTime;
			return this;
		}
		
		public SubscriberUsageBuilder withDailyResetTime(long dailyResetTime) {
			this.dailyResetTime = dailyResetTime;
			return this;
		}
		
		public SubscriberUsageBuilder withWeeklyResetTime(long weeklyResetTime) {
			this.weeklyResetTime = weeklyResetTime;
			return this;
		}
		
		public SubscriberUsageBuilder withDailyUsage(long total, long download, long upload, long time) {
			withDailyTotalUsage(total);
			withDailyDownlodUsage(download);
			withDailyUploadUsage(upload);
			withDailyTime(time);
			return this;
		}
		
		public SubscriberUsageBuilder withWeeklyUsage(long total, long download, long upload, long time) {
			withWeeklyTotalUsage(total);
			withWeeklyDownlodUsage(download);
			withWeeklyUploadUsage(upload);
			withWeeklyTime(time);
			return this;
		}
		
		public SubscriberUsageBuilder withCustomUsage(long total, long download, long upload, long time) {
			withCustomTotalUsage(total);
			withCustomDownlodUsage(download);
			withCustomUploadUsage(upload);
			withCustomTime(time);
			return this;
		}
		
		public SubscriberUsageBuilder withBillingCycleUsage(long total, long download, long upload, long time) {
			withBillingCycleTotalUsage(total);
			withBillingCycleDownlodUsage(download);
			withBillingCycleUploadUsage(upload);
			withBillingCycleTime(time);
			return this;
		}

		public SubscriberUsage build() {


			SubscriberUsage subscriberUsage = new SubscriberUsage(id,quotaProfileId, subscriberIdentity,serviceId,subscriptionId, packageId,productOfferId);
			
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
			
			return subscriberUsage;
		}

		public SubscriberUsageBuilder withAllTypeUsage(long total, long download, long upload, long time) {
			withDailyUsage(total, download, upload, time);
			withWeeklyUsage(total, download, upload, time);
			withBillingCycleUsage(total, download, upload, time);
			withCustomUsage(total, download, upload, time);
			return this;
		}

		public String getProductOfferId() {
			return productOfferId;
		}

		public void setProductOfferId(String productOfferId) {
			this.productOfferId = productOfferId;
		}
	}






	public void addBillingCycleDownload(long billingCycleDownload) {
		this.billingCycleDownload += billingCycleDownload;
	}

	public void addBillingCycleUpload(long billingCycleUpload) {
		this.billingCycleUpload += billingCycleUpload;
	}

	public void addBillingCycleTotal(long billingCycleTotal) {
		this.billingCycleTotal += billingCycleTotal;
	}

	public void addBillingCycleTime(long billingCycleTime) {
		this.billingCycleTime += billingCycleTime;
	}

	public void addDailyDownload(long dailyDownload) {
		this.dailyDownload += dailyDownload;
	}

	public void addDailyUpload(long dailyUpload) {
		this.dailyUpload += dailyUpload;
	}

	public void addDailyTotal(long dailyTotal) {
		this.dailyTotal += dailyTotal;
	}

	public void addDailyTime(long dailyTime) {
		this.dailyTime += dailyTime;
	}

	public void addWeeklyDownload(long weeklyDownload) {
		this.weeklyDownload += weeklyDownload;
	}

	public void addWeeklyUpload(long weeklyUpload) {
		this.weeklyUpload += weeklyUpload;
	}

	public void addWeeklyTotal(long weeklyTotal) {
		this.weeklyTotal += weeklyTotal;
	}

	public void addWeeklyTime(long weeklyTime) {
		this.weeklyTime += weeklyTime;
	}

	public void addCustomDownload(long customDownload) {
		this.customDownload += customDownload;
	}

	public void addCustomUpload(long customUpload) {
		this.customUpload += customUpload;
	}

	public void addCustomTotal(long customTotal) {
		this.customTotal += customTotal;
	}

	public void addCustomTime(long customTime) {
		this.customTime += customTime;
	}

	public void resetDailyUsage() {
		setDailyDownload(0);
		setDailyTime(0);
		setDailyTotal(0);
		setDailyUpload(0);
	}
	
	public void resetBillingCycleUsage() {
		setBillingCycleDownload(0);
		setBillingCycleTime(0);
		setBillingCycleTotal(0);
		setBillingCycleUpload(0);
	}
	
	public void resetWeeklyUsage() {
		setWeeklyDownload(0);
		setWeeklyTime(0);
		setWeeklyTotal(0);
		setWeeklyUpload(0);
	}
	
	public void resetCustomUsage() {
		setCustomDownload(0);
		setCustomTime(0);
		setCustomTotal(0);
		setCustomUpload(0);
	}

}
