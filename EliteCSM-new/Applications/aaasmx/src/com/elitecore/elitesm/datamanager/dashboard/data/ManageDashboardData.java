package com.elitecore.elitesm.datamanager.dashboard.data;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Set;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.dashboard.userrelation.data.DashboardUserRelData;
import com.elitecore.elitesm.web.dashboard.json.WidgetData;

public class ManageDashboardData extends BaseData implements Serializable,Comparator<ManageDashboardData>{
	
	private static final long serialVersionUID = 1L;
	private String dashboardRelId;
	private String dashboardId;
	private String dashboardName;
	private String addShares;
	private String author;
	private String isActive;
	private Long orderNumber;
	private String staffId;
	
	public String getDashboardId() {
		return dashboardId;
	}
	public void setDashboardId(String dashboardId) {
		this.dashboardId = dashboardId;
	}
	public String getDashboardName() {
		return dashboardName;
	}
	public void setDashboardName(String dashboardName) {
		this.dashboardName = dashboardName;
	}
	public String getAddShares() {
		return addShares;
	}
	public void setAddShares(String addShares) {
		this.addShares = addShares;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	public Long getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getDashboardRelId() {
		return dashboardRelId;
	}
	public void setDashboardRelId(String dashboardRelId) {
		this.dashboardRelId = dashboardRelId;
	}
	@Override
	public int compare(ManageDashboardData o1, ManageDashboardData o2) {
		if(o1.getOrderNumber() > o2.getOrderNumber()){
			return 1;
		}else{
			return -1;
		}
	}
	public String getStaffId() {
		return staffId;
	}
	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}
	
}
