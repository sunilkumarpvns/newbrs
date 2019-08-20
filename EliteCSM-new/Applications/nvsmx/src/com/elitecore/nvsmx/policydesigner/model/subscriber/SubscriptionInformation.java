package com.elitecore.nvsmx.policydesigner.model.subscriber;

import com.elitecore.corenetvertex.constants.SubscriptionState;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionInformation {

	private String addonSubscriptionId;
	private String packageName;
	private String packageId;
	private String packageDescription;
	private String packageType;


	private String pkgTypeDisplayValue;
	private String startTime;
	private String endTime;
	private SubscriptionState addOnStatus;
	private List<QuotaProfileBalance> quotaProfileBalances;

	private String productOfferName;
	private String productOfferId;
	private String priority;
	private String chargingType;

	//TODO change here as well
	private List<NonMonetaryRateCardBalance> rncBalances;
	private List<String> groupIds;

	public SubscriptionInformation() {
		this.quotaProfileBalances = new ArrayList<QuotaProfileBalance>();
	}

	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
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

	public List<String> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(List<String> groupIds) {
		this.groupIds = groupIds;
	}


	public String getPkgTypeDisplayValue() {
		return pkgTypeDisplayValue;
	}

	public void setPkgTypeDisplayValue(String pkgTypeDisplayValue) {
		this.pkgTypeDisplayValue = pkgTypeDisplayValue;
	}

	public List<NonMonetaryRateCardBalance> getRncBalances() {
		return rncBalances;
	}

	public void setRncBalances(List<NonMonetaryRateCardBalance> rncBalances) {
		this.rncBalances = rncBalances;
	}

	public String getProductOfferName() {
		return productOfferName;
	}

	public void setProductOfferName(String productOfferName) {
		this.productOfferName = productOfferName;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getProductOfferId() {
		return productOfferId;
	}

	public void setProductOfferId(String productOfferId) {
		this.productOfferId = productOfferId;
	}

	public String getChargingType() {
		return chargingType;
	}

	public void setChargingType(String chargingType) {
		this.chargingType = chargingType;
	}
}
