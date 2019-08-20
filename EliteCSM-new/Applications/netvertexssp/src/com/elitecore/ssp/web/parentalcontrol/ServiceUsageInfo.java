package com.elitecore.ssp.web.parentalcontrol;

public class ServiceUsageInfo {
	private long lastDayTotalOctets;
	private long lastWeekTotalOctets;
	private long lastMonthTotalOctets;
	private long lastDayUsageTime;
	private long lastWeekUsageTime;
	private long lastMonthUsageTime;
	
	private String serviceName;
	
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Long getLastDayTotalOctets() {
		return lastDayTotalOctets;
	}

	public void setLastDayTotalOctets(Long lastDay) {
		this.lastDayTotalOctets = lastDay;
	}

	public Long getLastWeekTotalOctets() {
		return lastWeekTotalOctets;
	}

	public void setLastWeekTotalOctets(Long lastWeek) {
		this.lastWeekTotalOctets = lastWeek;
	}

	public Long getLastMonthTotalOctets() {
		return lastMonthTotalOctets;
	}

	public void setLastMonthTotalOctets(Long lastMonth) {
		this.lastMonthTotalOctets = lastMonth;
	}

	public Long getLastDayUsageTime() {
		return lastDayUsageTime;
	}

	public void setLastDayUsageTime(Long lastDayUsageTime) {
		this.lastDayUsageTime = lastDayUsageTime;
	}

	public Long getLastWeekUsageTime() {
		return lastWeekUsageTime;
	}

	public void setLastWeekUsageTime(Long lastWeekUsageTime) {
		this.lastWeekUsageTime = lastWeekUsageTime;
	}

	public Long getLastMonthUsageTime() {
		return lastMonthUsageTime;
	}

	public void setLastMonthUsageTime(Long lastMonthUsageTime) {
		this.lastMonthUsageTime = lastMonthUsageTime;
	}
	
	@Override
	public String toString() {
		return serviceName;
	}
}