package com.elitecore.elitesm.web.driver.radius.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateRadiusHttpAuthDriverForm extends BaseWebForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String driverInstanceName;
	private String driverInstanceDesc;
	private String httpAuthDriverId;
	private String driverInstanceId;
	private String logicalName;
	private String http_url;
	private long maxQueryTimeoutCount;
	private Long statusCheckDuration;
	private Long responseParamIndex;
	private String expiryDateFormats="MM/dd/yyyy";
	private List<LogicalNameValuePoolData> logicalNameList;
	private String action;
	private Set httpFeildMapSet;
	private String defaultValue;
	private String valueMapping;
	private String userIdentityAttributes;
	List<String> logicalNameMultipleAllowList = new ArrayList<String>(0);
	private String auditUId;
	
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
	
	public String getHttpAuthDriverId() {
		return httpAuthDriverId;
	}
	
	public void setHttpAuthDriverId(String httpAuthDriverId) {
		this.httpAuthDriverId = httpAuthDriverId;
	}
	
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	public String getLogicalName() {
		return logicalName;
	}
	
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	
	public String getHttp_url() {
		return http_url;
	}
	
	public void setHttp_url(String httpUrl) {
		http_url = httpUrl;
	}
	
	public long getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}
	
	public void setMaxQueryTimeoutCount(long maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
	}
	
	public Long getStatusCheckDuration() {
		return statusCheckDuration;
	}
	
	public void setStatusCheckDuration(Long statusCheckDuration) {
		this.statusCheckDuration = statusCheckDuration;
	}
	
	public Long getResponseParamIndex() {
		return responseParamIndex;
	}
	
	public void setResponseParamIndex(Long responseParamIndex) {
		this.responseParamIndex = responseParamIndex;
	}
	
	public String getExpiryDateFormats() {
		return expiryDateFormats;
	}
	
	public void setExpiryDateFormats(String expiryDateFormats) {
		this.expiryDateFormats = expiryDateFormats;
	}
	
	public List<LogicalNameValuePoolData> getLogicalNameList() {
		return logicalNameList;
	}
	
	public void setLogicalNameList(List<LogicalNameValuePoolData> logicalNameList) {
		this.logicalNameList = logicalNameList;
	}
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public Set getHttpFeildMapSet() {
		return httpFeildMapSet;
	}
	
	public void setHttpFeildMapSet(Set httpFeildMapSet) {
		this.httpFeildMapSet = httpFeildMapSet;
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
