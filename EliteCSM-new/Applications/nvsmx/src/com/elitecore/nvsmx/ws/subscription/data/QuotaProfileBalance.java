package com.elitecore.nvsmx.ws.subscription.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.elitecore.nvsmx.ws.subscription.data.AggregationKey;
import com.elitecore.corenetvertex.constants.CommonConstants;

public class QuotaProfileBalance {
	private String quotaProfileId;
	private String quotaProfileName;
	private UsageInfo allServiceBalance;
	private List<UsageInfo> usageInfos;
	
	
	public QuotaProfileBalance(String quotaProfileId, String quotaProfileName, UsageInfo allServiceBalance, List<UsageInfo> balanceInfos) {
		this.quotaProfileId = quotaProfileId;
		this.quotaProfileName = quotaProfileName;
		this.allServiceBalance = allServiceBalance;
		this.usageInfos = balanceInfos;
	}

	public QuotaProfileBalance() {
		usageInfos = new ArrayList<UsageInfo>();
	}
	
	public String getQuotaProfileId() {
		return quotaProfileId;
	}
	public void setQuotaProfileId(String quotaProfileId) {
		this.quotaProfileId = quotaProfileId;
	}
	public String getQuotaProfileName() {
		return quotaProfileName;
	}
	public void setQuotaProfileName(String quotaProfileName) {
		this.quotaProfileName = quotaProfileName;
	}

	public void addBalanceInformation(UsageInfo balanceInformation) {
		if(CommonConstants.ALL_SERVICE_ID.equals(balanceInformation.getServiceId()) && balanceInformation.getAggregationKey() == AggregationKey.BILLING_CYCLE.getVal()){
			this.setAllServiceBalance(balanceInformation);
		}else{
			usageInfos.add(balanceInformation);
			Collections.sort(usageInfos, new Comparator<UsageInfo>(){
				@Override
				public int compare(UsageInfo o1, UsageInfo o2) {
					int result = o1.getServiceName().compareTo(o2.getServiceName());
					if(result == 0)
						return o1.getAggregationKey().compareTo(o2.getAggregationKey());
					return result;
				}
			});
		}
	}

	public UsageInfo getAllServiceBalance() {
		return allServiceBalance;
	}

	public void setAllServiceBalance(UsageInfo allServiceBalance) {
		this.allServiceBalance = allServiceBalance;
	}

	public List<UsageInfo> getUsageInfos() {
		return usageInfos;
	}

	public void setUsageInfos(List<UsageInfo> balanceInfos) {
		this.usageInfos = balanceInfos;
	}
}
