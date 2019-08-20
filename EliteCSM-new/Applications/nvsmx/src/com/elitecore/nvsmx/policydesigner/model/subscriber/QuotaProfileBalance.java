package com.elitecore.nvsmx.policydesigner.model.subscriber;

import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuotaProfileBalance {
	private String quotaProfileId;
	private String quotaProfileName;
	private BalanceInfo allServiceBalance;
	private List<BalanceInfo> balanceInfos;
	
	public QuotaProfileBalance() {
		balanceInfos = new ArrayList<BalanceInfo>();
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

	public void addBalanceInformation(BalanceInfo balanceInformation) {
		if(CommonConstants.ALL_SERVICE_ID.equals(balanceInformation.getServiceId()) && balanceInformation.getAggregationKey() == AggregationKey.BILLING_CYCLE && balanceInformation.getLevel()== BalanceLevel.HSQ.fupLevel){
			this.setAllServiceBalance(balanceInformation);
		}else{
			balanceInfos.add(balanceInformation);
			Collections.sort(balanceInfos, (BalanceInfo o1, BalanceInfo o2)->{
				int result = o1.getServiceName().compareTo(o2.getServiceName());
				if(result == 0) {
					result = o1.getAggregationKey().compareTo(o2.getAggregationKey());
					if(result==0){
						return o1.getLevel()-o2.getLevel();
					}
				}
				return result;
			});
		}
	}

	public BalanceInfo getAllServiceBalance() {
		return allServiceBalance;
	}

	public void setAllServiceBalance(BalanceInfo allServiceBalance) {
		this.allServiceBalance = allServiceBalance;
	}

	public List<BalanceInfo> getBalanceInfos() {
		return balanceInfos;
	}

	public void setBalanceInformations(List<BalanceInfo> balanceInfos) {
		this.balanceInfos = balanceInfos;
	}
}
