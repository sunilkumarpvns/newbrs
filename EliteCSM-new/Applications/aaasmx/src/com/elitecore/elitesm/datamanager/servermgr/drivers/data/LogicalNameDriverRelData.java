package com.elitecore.elitesm.datamanager.servermgr.drivers.data;

import java.io.Serializable;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class LogicalNameDriverRelData extends BaseData implements ILogicalNameDriverRelData, Serializable{
	
	private static final long serialVersionUID = 1L;
	
	
	private LogicalNameValuePoolData logicalNameValuePoolData;
	private Long driverTypeId;
	private String logicalNameId;
	
	public String getLogicalNameId() {
		return logicalNameId;
	}
	public void setLogicalNameId(String logicalNameId) {
		this.logicalNameId = logicalNameId;
	}
	public LogicalNameValuePoolData getLogicalNameValuePoolData() {
		return logicalNameValuePoolData;
	}
	public void setLogicalNameValuePoolData(LogicalNameValuePoolData logicalNamealuePoolData) {
		this.logicalNameValuePoolData = logicalNamealuePoolData;
	}
	public Long getDriverTypeId() {
		return driverTypeId;
	}
	public void setDriverTypeId(Long driverTypeId) {
		this.driverTypeId = driverTypeId;
	}
}