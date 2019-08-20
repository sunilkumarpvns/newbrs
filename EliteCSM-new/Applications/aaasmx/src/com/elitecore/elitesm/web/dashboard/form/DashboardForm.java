package com.elitecore.elitesm.web.dashboard.form;

import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class DashboardForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private String dashboardId;
	private String dashboardName;
	private List dashboardList;
	private List dashboardDataList;
	private String dashboardDesc;
	private String startFrom;
	private String addShares;
	private List listAccessGroup;
	private List listSearchStaff;
	private String author;
	private String isActive;
	private List listServer;
	private boolean accessGroupId=true;
	private boolean manageAccessGroupId=true;
	
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
	public List getDashboardList() {
		return dashboardList;
	}
	public void setDashboardList(List dashboardList) {
		this.dashboardList = dashboardList;
	}
	public String getDashboardDesc() {
		return dashboardDesc;
	}
	public void setDashboardDesc(String dashboardDesc) {
		this.dashboardDesc = dashboardDesc;
	}
	public String getStartFrom() {
		return startFrom;
	}
	public void setStartFrom(String startFrom) {
		this.startFrom = startFrom;
	}
	public String getAddShares() {
		return addShares;
	}
	public void setAddShares(String addShares) {
		this.addShares = addShares;
	}
	public List getListAccessGroup() {
		return listAccessGroup;
	}
	public void setListAccessGroup(List listAccessGroup) {
		this.listAccessGroup = listAccessGroup;
	}
	public List getListSearchStaff() {
		return listSearchStaff;
	}
	public void setListSearchStaff(List listSearchStaff) {
		this.listSearchStaff = listSearchStaff;
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
	public List getListServer() {
		return listServer;
	}
	public void setListServer(List listServer) {
		this.listServer = listServer;
	}
	public boolean isAccessGroupId() {
		return accessGroupId;
	}
	public void setAccessGroupId(boolean accessGroupId) {
		this.accessGroupId = accessGroupId;
	}
	public boolean isManageAccessGroupId() {
		return manageAccessGroupId;
	}
	public void setManageAccessGroupId(boolean manageAccessGroupId) {
		this.manageAccessGroupId = manageAccessGroupId;
	}
	public List getDashboardDataList() {
		return dashboardDataList;
	}
	public void setDashboardDataList(List dashboardDataList) {
		this.dashboardDataList = dashboardDataList;
	}
	
}
