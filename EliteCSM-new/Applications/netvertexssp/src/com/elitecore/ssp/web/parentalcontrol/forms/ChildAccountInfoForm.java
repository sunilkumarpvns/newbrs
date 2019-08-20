package com.elitecore.ssp.web.parentalcontrol.forms;

import java.util.List;
import com.elitecore.ssp.subscriber.SubscriberProfile;
import com.elitecore.ssp.web.core.base.forms.BaseWebForm;
import com.elitecore.ssp.web.parentalcontrol.ChildAccountUsageInfo;

public class ChildAccountInfoForm extends BaseWebForm {
	private static final long serialVersionUID = 1L;
	
	private long[] allowedUsageInfo;
	private List<long[]> allowedUsageInfoList;
	private List<ChildAccountUsageInfo> childAccountUsageInfoDataList;
	
	private String blockedUrlString;
	
	private int transferData;
	
	private SubscriberProfile profile;
	
	private String actionPerformed;
	
	private String quotaRollOver;
	
	private int notificationPercentage;

	public List<ChildAccountUsageInfo> getChildAccountUsageInfoDataList() {
		return childAccountUsageInfoDataList;
	}

	public void setChildAccountUsageInfoDataList(
			List<ChildAccountUsageInfo> childAccountUsageInfoDataList) {
		this.childAccountUsageInfoDataList = childAccountUsageInfoDataList;
	}

	public List<long[]> getAllowedUsageInfoList() {
		return allowedUsageInfoList;
	}

	public void setAllowedUsageInfoList(List<long[]> allowedUsageInfoList) {
		this.allowedUsageInfoList = allowedUsageInfoList;
	}

	public long[] getAllowedUsageInfo() {
		return allowedUsageInfo;
	}

	public void setAllowedUsageInfo(long[] allowedUsageInfo) {
		this.allowedUsageInfo = allowedUsageInfo;
	}

	public String getBlockedUrlString() {
		return blockedUrlString;
	}

	public void setBlockedUrlString(String blockedUrlString) {
		this.blockedUrlString = blockedUrlString;
	}

	public int getTransferData() {
		return transferData;
	}

	public void setTransferData(int transferData) {
		this.transferData = transferData;
	}

	public SubscriberProfile getProfile() {
		return profile;
	}

	public void setProfile(SubscriberProfile profile) {
		this.profile = profile;
	}

	public String getActionPerformed() {
		return actionPerformed;
	}

	public void setActionPerformed(String actionPerformed) {
		this.actionPerformed = actionPerformed;
	}

	public String getQuotaRollOver() {
		return quotaRollOver;
	}

	public void setQuotaRollOver(String quotaRollOver) {
		this.quotaRollOver = quotaRollOver;
	}

	public int getNotificationPercentage() {
		return notificationPercentage;
	}

	public void setNotificationPercentage(int notificationPercentage) {
		this.notificationPercentage = notificationPercentage;
	}
}