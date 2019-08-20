package com.elitecore.nvsmx.system.util.migrate;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class NV648SubscriberInfo {
	
	@SerializedName("SPR")
	private AccountDataInfo accountDataInfo;
	@SerializedName("SUBSCRIPTION ADDONS")
	private List<NV648SubscriptionInfo> subscriptionInfos;
	@SerializedName("SUBSCRIPTION BASE PACKAGE")
	private List<NV648BaseUsageInfo> baseUsageInfos;
	
	public NV648SubscriberInfo() {
	
	}

	public AccountDataInfo getAccountDataInfo() {
		return accountDataInfo;
	}

	public void setAccountDataInfo(AccountDataInfo accountDataInfo) {
		this.accountDataInfo = accountDataInfo;
	}

	public List<NV648SubscriptionInfo> getSubscriptionInfos() {
		return subscriptionInfos;
	}

	public void setSubscriptionInfos(List<NV648SubscriptionInfo> subscriptionInfos) {
		this.subscriptionInfos = subscriptionInfos;
	}
	
	public List<NV648BaseUsageInfo> getBaseUsageInfos() {
		return baseUsageInfos;
	}

	public void setBaseUsageInfos(List<NV648BaseUsageInfo> baseUsageInfos) {
		this.baseUsageInfos = baseUsageInfos;
	}

}
