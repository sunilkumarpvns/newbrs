package com.elitecore.ssp.web.parentalcontrol;

import java.util.List;

import com.elitecore.ssp.subscriber.SubscriberProfile;

public class ChildAccountUsageInfo {
	private long[] allowedUsageInfo;
	private List<long[]> allowedUsageInfoList;
	private SubscriberProfile childObject;
	
	public long[] getAllowedUsageInfo() {
		return allowedUsageInfo;
	}
	
	public void setAllowedUsageInfo(long[] allowedUsageInfo) {
		this.allowedUsageInfo = allowedUsageInfo;
	}
	
	public List<long[]> getAllowedUsageInfoList() {
		return allowedUsageInfoList;
	}
	
	public void setAllowedUsageInfoList(List<long[]> allowedUsageInfoList) {
		this.allowedUsageInfoList = allowedUsageInfoList;
	}
	
	public SubscriberProfile getChildObject() {
		return childObject;
	}
	
	public void setChildObject(SubscriberProfile childObject) {
		this.childObject = childObject;
	}
}