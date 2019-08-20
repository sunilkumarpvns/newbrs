package com.elitecore.corenetvertex.spr.balance;

import com.elitecore.corenetvertex.constants.AggregationKey;

/**
 * 
 * @author Jay Trivedi
 *
 */

public class UsageInfo {
	private String serviceId;
	private String serviceName;
	private AggregationKey aggregationKey;
	private Usage curretUsage;
	private Usage allowedUsage;
	private Usage balance;
	
	public AggregationKey getAggregationKey() {
		return aggregationKey;
	}
	public void setAggregationKey(AggregationKey aggregationKey) {
		this.aggregationKey = aggregationKey;
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
	public Usage getCurretUsage() {
		return curretUsage;
	}
	public Usage getAllowedUsage() {
		return allowedUsage;
	}
	public Usage getBalance() {
		return balance;
	}
	public void setCurretUsage(Usage curretUsage) {
		this.curretUsage = curretUsage;
	}
	public void setAllowedUsage(Usage allowedUsage) {
		this.allowedUsage = allowedUsage;
	}
	public void setBalance(Usage balance) {
		this.balance = balance;
	}
}
