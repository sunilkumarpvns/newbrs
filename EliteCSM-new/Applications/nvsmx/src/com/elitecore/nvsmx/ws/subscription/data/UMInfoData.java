package com.elitecore.nvsmx.ws.subscription.data;

public class UMInfoData {

	private String packageId;
	private String packageName;
	private String subscriptionId;
	private String quotaProfileId;
	private String quotaProfileName;
	private String serviceId;
	private String serviceName;
	private BillingCycleUsage billingCycleUsage;
	private DailyUsage dailyUsage;
	private WeeklyUsage weeklyUsage;
	private CustomUsage customUsage;
	
	public UMInfoData(){}
	public UMInfoData(String packageId, String packageName, String subscriptionId, String quotaProfileId, String quotaProfileName, String serviceId,
			String serviceName, BillingCycleUsage billingCycleUsage, DailyUsage dailyUsage, WeeklyUsage weeklyUsage, CustomUsage customUsage) {
		super();
		this.packageId = packageId;
		this.packageName = packageName;
		this.subscriptionId = subscriptionId;
		this.quotaProfileId = quotaProfileId;
		this.quotaProfileName = quotaProfileName;
		this.serviceId = serviceId;
		this.serviceName = serviceName;
		this.billingCycleUsage = billingCycleUsage;
		this.dailyUsage = dailyUsage;
		this.weeklyUsage = weeklyUsage;
		this.customUsage = customUsage;
	}


	public String getPackageId() {
		return packageId;
	}


	public String getPackageName() {
		return packageName;
	}


	public String getSubscriptionId() {
		return subscriptionId;
	}


	public String getQuotaProfileId() {
		return quotaProfileId;
	}


	public String getQuotaProfileName() {
		return quotaProfileName;
	}


	public String getServiceId() {
		return serviceId;
	}


	public String getServiceName() {
		return serviceName;
	}


	public BillingCycleUsage getBillingCycleUsage() {
		return billingCycleUsage;
	}


	public DailyUsage getDailyUsage() {
		return dailyUsage;
	}


	public WeeklyUsage getWeeklyUsage() {
		return weeklyUsage;
	}


	public CustomUsage getCustomUsage() {
		return customUsage;
	}


	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}


	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}


	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}


	public void setQuotaProfileId(String quotaProfileId) {
		this.quotaProfileId = quotaProfileId;
	}


	public void setQuotaProfileName(String quotaProfileName) {
		this.quotaProfileName = quotaProfileName;
	}


	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}


	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}


	public void setBillingCycleUsage(BillingCycleUsage billingCycleUsage) {
		this.billingCycleUsage = billingCycleUsage;
	}


	public void setDailyUsage(DailyUsage dailyUsage) {
		this.dailyUsage = dailyUsage;
	}


	public void setWeeklyUsage(WeeklyUsage weeklyUsage) {
		this.weeklyUsage = weeklyUsage;
	}


	public void setCustomUsage(CustomUsage customUsage) {
		this.customUsage = customUsage;
	}

	
	
}
