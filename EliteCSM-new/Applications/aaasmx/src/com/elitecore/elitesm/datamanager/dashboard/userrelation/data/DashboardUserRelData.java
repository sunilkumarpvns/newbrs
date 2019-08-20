package com.elitecore.elitesm.datamanager.dashboard.userrelation.data;

import java.io.Serializable;
import java.util.Comparator;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class DashboardUserRelData extends BaseData implements Serializable,Comparator<DashboardUserRelData>{
	
	private static final long serialVersionUID = 1L;
	private String dashboardRelId;
	private String isActive;
	private String dashboardId;
	private String staffId;
	private java.lang.Long orderNumber;
	
	public String getDashboardRelId() {
		return dashboardRelId;
	}
	public void setDashboardRelId(String dashboardRelId) {
		this.dashboardRelId = dashboardRelId;
	}
	public String getDashboardId() {
		return dashboardId;
	}
	public void setDashboardId(String dashboardId) {
		this.dashboardId = dashboardId;
	}
	public String getStaffId() {
		return staffId;
	}
	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	public java.lang.Long getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(java.lang.Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@Override
	public int compare(DashboardUserRelData o1, DashboardUserRelData o2) {
		if(o1.getOrderNumber() > o2.getOrderNumber()){
			return 1;
		}else{
			return -1;
		}
	}
	
}
