package com.elitecore.ssp.web.parentalcontrol;

import com.elitecore.ssp.subscriber.SubscriberProfile;


public class EnterpriseMemberAccountUsageInfo {
	private Long lastDayTotalOctets;
	private Long lastDayUsageTime;
	private Long lastWeekUsageTime;
	private Long lastMonthUsageTime;
	private Long lastWeekTotalOctets;
	private Long lastMonthTotalOctets;
	private SubscriberProfile SubscriberProfile;
	
	public SubscriberProfile getSubscriberProfile() {
		return SubscriberProfile;
	}
	public void setSubscriberProfile(SubscriberProfile SubscriberProfile) {
		this.SubscriberProfile = SubscriberProfile;
	}
	public Long getLastDayTotalOctets() {
		return lastDayTotalOctets;
	}
	public void setLastDayTotalOctets(Long lastDayTotalOctets) {
		this.lastDayTotalOctets = lastDayTotalOctets;
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
	public Long getLastWeekTotalOctets() {
		return lastWeekTotalOctets;
	}
	public void setLastWeekTotalOctets(Long lastWeekTotalOctets) {
		this.lastWeekTotalOctets = lastWeekTotalOctets;
	}
	public Long getLastMonthTotalOctets() {
		return lastMonthTotalOctets;
	}
	public void setLastMonthTotalOctets(Long lastMonthTotalOctets) {
		this.lastMonthTotalOctets = lastMonthTotalOctets;
	}
}