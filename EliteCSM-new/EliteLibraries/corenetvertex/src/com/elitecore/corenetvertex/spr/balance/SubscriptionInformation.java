package com.elitecore.corenetvertex.spr.balance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.SubscriptionState;

public class SubscriptionInformation {
	
	private String addonSubscriptionId;
	private String packageName;
	private String packageId;
	private String packageDescription;
	private String packageType;
	private Timestamp startTime;
	private Timestamp endTime;
	private SubscriptionState addOnStatus;
	private QuotaProfileType quotaProfileType;

	private List<QuotaProfileBalance> quotaProfileBalances;
	
	public SubscriptionInformation() {
		this.quotaProfileBalances = new ArrayList<QuotaProfileBalance>();
	}
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public Timestamp getStartTime() {
		return startTime;
	}
	
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	
	public Timestamp getEndTime() {
		return endTime;
	}
	
	public void setEndTime(Timestamp endTime) {
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

	public QuotaProfileType getQuotaProfileType() {
		return quotaProfileType;
	}

	public void setQuotaProfileType(QuotaProfileType quotaProfileType) {
		this.quotaProfileType = quotaProfileType;
	}
}
