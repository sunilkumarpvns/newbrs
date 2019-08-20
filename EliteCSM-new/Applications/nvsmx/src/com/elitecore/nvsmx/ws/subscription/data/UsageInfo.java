package com.elitecore.nvsmx.ws.subscription.data;


/**
 * 
 * @author Jay Trivedi
 *
 */

public class UsageInfo {
	private String serviceId;
	private String serviceName;
	private String aggregationKey;
	private Usage curretUsage;
	private Usage HSQLimit;
	private Usage balance;
	
	public UsageInfo(){}
	public UsageInfo(String serviceId, String serviceName, String aggregationKey, Usage curretUsage, Usage HSQLimit, Usage balance) {
		super();
		this.serviceId = serviceId;
		this.serviceName = serviceName;
		this.aggregationKey = aggregationKey;
		this.curretUsage = curretUsage;
		this.HSQLimit = HSQLimit;
		this.balance = balance;
	}
	public String getAggregationKey() {
		return aggregationKey;
	}
	public void setAggregationKey(String aggregationKey) {
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
	public Usage getHSQLimit() {
		return HSQLimit;
	}
	public Usage getBalance() {
		return balance;
	}
	public void setCurretUsage(Usage curretUsage) {
		this.curretUsage = curretUsage;
	}
	public void setHSQLimit(Usage HSQLimit) {
		this.HSQLimit = HSQLimit;
	}
	public void setBalance(Usage balance) {
		this.balance = balance;
	}
}
