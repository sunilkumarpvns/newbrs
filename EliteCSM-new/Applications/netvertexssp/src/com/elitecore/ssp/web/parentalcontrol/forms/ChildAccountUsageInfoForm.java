package com.elitecore.ssp.web.parentalcontrol.forms;

import java.util.List;

import com.elitecore.ssp.web.core.base.forms.BaseWebForm;
import com.elitecore.ssp.web.parentalcontrol.EnterpriseMemberAccountUsageInfo;
import com.elitecore.ssp.web.parentalcontrol.ServiceUsageInfo;

public class ChildAccountUsageInfoForm extends BaseWebForm {
	private static final long serialVersionUID = 1L;
	private List<ServiceUsageInfo> serviceUsageInfoDataList;
	private Long lastDayTotalOctets;
	private Long lastDayUsageTime;
	private Long lastWeekUsageTime;
	private Long lastMonthUsageTime;
	private Long lastWeekTotalOctets;
	private Long lastMonthTotalOctets;
	private List<EnterpriseMemberAccountUsageInfo> enterpriseMemberAccountUsageInfoDataList;
	
	public List<EnterpriseMemberAccountUsageInfo> getEnterpriseMemberAccountUsageInfoDataList() {
		return enterpriseMemberAccountUsageInfoDataList;
	}

	public void setEnterpriseMemberAccountUsageInfoDataList(
			List<EnterpriseMemberAccountUsageInfo> enterpriseMemberAccountUsageInfoDataList) {
		this.enterpriseMemberAccountUsageInfoDataList = enterpriseMemberAccountUsageInfoDataList;
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

	public List<ServiceUsageInfo> getServiceUsageInfoDataList() {
		return serviceUsageInfoDataList;
	}
	
	public void setServiceUsageInfoDataList(
			List<ServiceUsageInfo> serviceUsageInfoDataList) {
		this.serviceUsageInfoDataList = serviceUsageInfoDataList;
	}

}