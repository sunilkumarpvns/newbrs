package com.elitecore.nvsmx.policydesigner.model.subscriber;

import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BalanceLevel;

public class BalanceInfo {
	private String serviceId;
	private String serviceName;
	private AggregationKey aggregationKey;
	private long uploadOctets;
	private long downloadOctets;
	private long totalOctets;
	private long time;
	private long timeLimit;
	private long usageLimit = -1;
	private String quotaUsageType;


	private String usage;
	private String usageLimitStr;
	private int level;
	private String balanceLevelStr;

	public AggregationKey getAggregationKey() {
		return aggregationKey;
	}
	public void setAggregationKey(AggregationKey aggregationKey) {
		this.aggregationKey = aggregationKey;
	}
	public long getUploadOctets() {
		return uploadOctets;
	}
	public void setUploadOctets(long uploadOctets) {
		this.uploadOctets = uploadOctets;
	}
	public long getDownloadOctets() {
		return downloadOctets;
	}
	public void setDownloadOctets(long downloadOctets) {
		this.downloadOctets = downloadOctets;
	}
	public long getTotalOctets() {
		return totalOctets;
	}
	public void setTotalOctets(long totalOctets) {
		this.totalOctets = totalOctets;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getUsage() {
		return usage;
	}
	public void setUsage(String usage) {
		this.usage = usage;
	}
	public String getUsageLimitStr() {
		return usageLimitStr;
	}
	public void setUsageLimitStr(String usageLimit) {
		this.usageLimitStr = usageLimit;
	}
	public long getUsageLimit() {
		return usageLimit;
	}
	public void setUsageLimit(long usageLimit) {
		this.usageLimit = usageLimit;
	}

	public long getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(long timeLimit) {
		this.timeLimit = timeLimit;
	}

	public String getBalanceLevelStr() {
		return balanceLevelStr;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
		for(BalanceLevel balanceLevel:BalanceLevel.values()){
			if(balanceLevel.fupLevel==level){
				balanceLevelStr = balanceLevel.displayVal;
			}
		}
	}

	public String getQuotaUsageType() {
		return quotaUsageType;
	}

	public void setQuotaUsageType(String quotaUsageType) {
		this.quotaUsageType = quotaUsageType;
	}
}
