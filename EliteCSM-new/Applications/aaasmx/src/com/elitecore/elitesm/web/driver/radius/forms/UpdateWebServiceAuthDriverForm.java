package com.elitecore.elitesm.web.driver.radius.forms;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebMethodKeyData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateWebServiceAuthDriverForm extends BaseWebForm{
	
	private static final long serialVersionUID = 8379372527122966749L;
	// driver instance related properties
	private String driverInstanceName;
	private String driverInstanceDesc;
	private String imsiAttribute;
	
	// user file driver details
	private String userFileDriverId;
	private String serviceAddress;
	private String driverInstanceId;
	private String action;
	
	private String logicalName;
	private String wsMethodKey;
	private String defaultValue;
	private String valueMapping;
	private Long maxQueryTimeoutCnt;
	private Long statusChkDuration;
	private List<LogicalNameValuePoolData> logicalNameList;
	private List<WebMethodKeyData> webMethodKeyList;
	private String auditUId;
	
	List<String> logicalNameMultipleAllowList = new ArrayList<String>(0);

	private String userIdentityAttributes;

	
	public Long getMaxQueryTimeoutCnt() {
		return maxQueryTimeoutCnt;
	}
	public void setMaxQueryTimeoutCnt(Long maxQueryTimeoutCnt) {
		this.maxQueryTimeoutCnt = maxQueryTimeoutCnt;
	}
	public Long getStatusChkDuration() {
		return statusChkDuration;
	}
	public void setStatusChkDuration(Long statusChkDuration) {
		this.statusChkDuration = statusChkDuration;
	}
	public String getDriverInstanceName() {
		return driverInstanceName;
	}
	public void setDriverInstanceName(String driverInstanceName) {
		this.driverInstanceName = driverInstanceName;
	}
	public String getDriverInstanceDesc() {
		return driverInstanceDesc;
	}
	public void setDriverInstanceDesc(String driverInstanceDesc) {
		this.driverInstanceDesc = driverInstanceDesc;
	}
	public String getUserFileDriverId() {
		return userFileDriverId;
	}
	public void setUserFileDriverId(String userFileDriverId) {
		this.userFileDriverId = userFileDriverId;
	}
	public String getServiceAddress() {
		return serviceAddress;
	}
	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getImsiAttribute() {
		return imsiAttribute;
	}
	public void setImsiAttribute(String imsiAttribute) {
		this.imsiAttribute = imsiAttribute;
	}
	public String getLogicalName() {
		return logicalName;
	}
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	public String getWsMethodKey() {
		return wsMethodKey;
	}
	public void setWsMethodKey(String wsMethodKey) {
		this.wsMethodKey = wsMethodKey;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getValueMapping() {
		return valueMapping;
	}
	public void setValueMapping(String valueMapping) {
		this.valueMapping = valueMapping;
	}
	public List<LogicalNameValuePoolData> getLogicalNameList() {
		return logicalNameList;
	}
	public void setLogicalNameList(List<LogicalNameValuePoolData> logicalNameList) {
		this.logicalNameList = logicalNameList;
	}
	public List<WebMethodKeyData> getWebMethodKeyList() {
		return webMethodKeyList;
	}
	public void setWebMethodKeyList(List<WebMethodKeyData> webMethodKeyList) {
		this.webMethodKeyList = webMethodKeyList;
	}

	public List<String> getLogicalNameMultipleAllowList() {
		return logicalNameMultipleAllowList;
	}
	public void setLogicalNameMultipleAllowList(
			List<String> logicalNameMultipleAllowList) {
		this.logicalNameMultipleAllowList = logicalNameMultipleAllowList;
	}

	public String getUserIdentityAttributes() {
		return userIdentityAttributes;
	}
	public void setUserIdentityAttributes(String userIdentityAttributes) {
		this.userIdentityAttributes = userIdentityAttributes;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
}
