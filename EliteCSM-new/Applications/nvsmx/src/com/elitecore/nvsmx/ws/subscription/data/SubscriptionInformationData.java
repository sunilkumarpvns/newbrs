package com.elitecore.nvsmx.ws.subscription.data;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.corenetvertex.constants.SubscriptionState;

public class SubscriptionInformationData {
	
	private String addonSubscriptionId;
	private String packageName;
	private String packageId;
	private String packageDescription;
	private String packageType;
	private Long startTime;
	private Long endTime;
	private SubscriptionState addOnStatus;
	private List<QuotaProfileBalance> quotaProfileBalances;

	public SubscriptionInformationData(String addonSubscriptionId, String packageName, String packageId, String packageDescription, String packageType,
			Long startTime, Long endTime, SubscriptionState addOnStatus,
			List<QuotaProfileBalance> quotaProfileBalances) {
		super();
		this.addonSubscriptionId = addonSubscriptionId;
		this.packageName = packageName;
		this.packageId = packageId;
		this.packageDescription = packageDescription;
		this.packageType = packageType;
		this.startTime = startTime;
		this.endTime = endTime;
		this.addOnStatus = addOnStatus;
		this.quotaProfileBalances = quotaProfileBalances;
	}
	
	public SubscriptionInformationData() {
		this.quotaProfileBalances = new ArrayList<QuotaProfileBalance>();
	}
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public Long getStartTime() {
		return startTime;
	}
	
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	
	public Long getEndTime() {
		return endTime;
	}
	
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	public SubscriptionState getAddOnStatus() {
		return addOnStatus;
	}
	public void setAddOnStatus(SubscriptionState addOnStatus) {
		this.addOnStatus = addOnStatus;
	}
	
	public String getAddonSubscriptionId() {
		return addonSubscriptionId;
	}
	public void setAddonSubscriptionId(String addonSubscriptionId) {
		this.addonSubscriptionId = addonSubscriptionId;
	}
	public List<QuotaProfileBalance> getQuotaProfileBalances() {
		return quotaProfileBalances;
	}
	public void setQuotaProfileBalances(List<QuotaProfileBalance> quotaProfileBalance) {
		this.quotaProfileBalances = quotaProfileBalance;
	}
	public void addQuotaProfileBalance(QuotaProfileBalance quotaProfileBalance){
		this.quotaProfileBalances.add(quotaProfileBalance);
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public String getPackageDescription() {
		return packageDescription;
	}

	public void setPackageDescription(String packageDescription) {
		this.packageDescription = packageDescription;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

}
