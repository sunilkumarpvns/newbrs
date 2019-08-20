package com.elitecore.netvertexsm.web.servermgr.drivers.form;

import java.util.List;

import org.apache.struts.action.ActionForm;

import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.ServiceTypeData;

public class CreateDriverInstanceForm extends ActionForm {

	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private String status = "CST01";
	private Long driverTypeId;
	private List<ServiceTypeData> serviceTypeList;
	private List<DriverTypeData> driverTypeList;
	
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getDriverTypeId() {
		return driverTypeId;
	}
	public void setDriverTypeId(Long driverTypeId) {
		this.driverTypeId = driverTypeId;
	}
	public List<ServiceTypeData> getServiceTypeList() {
		return serviceTypeList;
	}
	
	public void setServiceTypeList(List<ServiceTypeData> serviceTypeList) {
		this.serviceTypeList = serviceTypeList;
	}
	public List<DriverTypeData> getDriverTypeList() {
		return driverTypeList;
	}
	public void setDriverTypeList(List<DriverTypeData> driverTypeList) {
		this.driverTypeList = driverTypeList;
	}
	
	

}
