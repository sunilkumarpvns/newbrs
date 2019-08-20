package com.elitecore.elitesm.web.driver.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class SearchDriverInstanceForm extends BaseWebForm{
	
	private String name;
	private String action;
	private List serviceList;
	private String selecteDriver;
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	private List<DriverInstanceData> driverList;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List getServiceList() {
		return serviceList;
	}
	public void setServiceList(List serviceList) {
		this.serviceList = serviceList;
	}
	public String getSelecteDriver() {
		return selecteDriver;
	}
	public void setSelecteDriver(String selecteDriver) {
		this.selecteDriver = selecteDriver;
	}
	public long getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
	}
	public long getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
	}
	public long getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}
	public List<DriverInstanceData> getDriverList() {
		return driverList;
	}
	public void setDriverList(List<DriverInstanceData> driverList) {
		this.driverList = driverList;
	}
	
	
	
	

}
