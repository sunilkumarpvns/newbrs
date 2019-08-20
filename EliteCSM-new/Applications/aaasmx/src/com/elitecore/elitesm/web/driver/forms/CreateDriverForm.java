package com.elitecore.elitesm.web.driver.forms;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateDriverForm extends BaseWebForm{
	
	private String name;
	private String description;
	private List serviceList;
	private Long selecteDriver;
	private long driverTypeId;
	private String action;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List getServiceList() {
		return serviceList;
	}
	public void setServiceList(List serviceList) {
		this.serviceList = serviceList;
	}
	
	public Long getSelecteDriver() {
		return selecteDriver;
	}
	public void setSelecteDriver(Long selecteDriver) {
		this.selecteDriver = selecteDriver;
	}
	public long getDriverTypeId() {
		return driverTypeId;
	}
	public void setDriverTypeId(long driverTypeId) {
		this.driverTypeId = driverTypeId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
}
