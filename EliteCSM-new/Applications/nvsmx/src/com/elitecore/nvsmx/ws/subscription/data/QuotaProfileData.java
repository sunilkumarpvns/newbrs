package com.elitecore.nvsmx.ws.subscription.data;

import java.util.List;

import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CounterPresence;
import com.elitecore.corenetvertex.constants.QuotaProfileType;

public class QuotaProfileData {
	
	private  String name;
	private  String id;
	private  QuotaProfileType quotaProfileType;
	private  CounterPresence usagePresence;
	private  BalanceLevel balanceLevel;
	
	private  List<QuotaProfileDetailData> quotaProfileDetails;
	private  QuotaProfileBalanceInformation quotaProfileBalance;
	
	
	public List<QuotaProfileDetailData> getQuotaProfileDetails() {
		return quotaProfileDetails;
	}

	public void setQuotaProfileDetails(
			List<QuotaProfileDetailData> quotaProfileDetails) {
		this.quotaProfileDetails = quotaProfileDetails;
	}

	
	
	public void setName(String name) {
		this.name = name;
	}

	
	public void setId(String id) {
		this.id = id;
	}

	public void setQuotaProfileType(QuotaProfileType quotaProfileType) {
		this.quotaProfileType = quotaProfileType;
	}



	public void setUsagePresence(CounterPresence usagePresence) {
		this.usagePresence = usagePresence;
	}

	public void setBalanceLevel(BalanceLevel balanceLevel) {
		this.balanceLevel = balanceLevel;
	}

	public QuotaProfileType getQuotaProfileType() {
		return quotaProfileType;
	}


	

	public QuotaProfileData(String name, 
			String id, 
			QuotaProfileType quotaProfileType,
			CounterPresence usagePresence, BalanceLevel balanceLevel,List<QuotaProfileDetailData> quotaProfileDetails) {
		super();
		this.name = name;
		this.id = id;
		this.quotaProfileType = quotaProfileType;
		this.usagePresence = usagePresence;
		this.balanceLevel = balanceLevel;
		this.quotaProfileDetails = quotaProfileDetails;
	}

	public QuotaProfileData(){}

	public String getName() {
		return name;
	}

	
	public String getId() {
		return id;
	}
	
	public QuotaProfileType getType() {
		return quotaProfileType;
	}

	public CounterPresence getUsagePresence() {
		return usagePresence;
	}

	public BalanceLevel getBalanceLevel() {
		return balanceLevel;
	}

	public QuotaProfileBalanceInformation getQuotaProfileBalance() {
		return quotaProfileBalance;
	}

	public void setQuotaProfileBalance(QuotaProfileBalanceInformation quotaProfileBalance) {
		this.quotaProfileBalance = quotaProfileBalance;
	}
	
}
