package com.elitecore.elitesm.web.driver.radius.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.ProfileFieldValuePoolData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateRadiusMappingGWAuthDriverForm extends BaseWebForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String localHostId;
	private int localHostPort;
	private String localHostIp;
	private String remoteHostId;
	private int remoteHostPort;
	private String remoteHostIp;
	private String action;
	private String driverInstanceName;
	private String driverDesp;
	private String driverRelatedId;
	private String driverInstanceId;
	private Set profileFeildMapSet;
	private List<LogicalNameValuePoolData> logicalNameList;
	private List<ProfileFieldValuePoolData> profileFieldList;
	private String logicalName;
	private String profileField;
	private String defaultValue;
	private String valueMapping;
	private Long maxQueryTimeoutCount;
	private Integer mapGwConnPoolSize;
	private Integer requestTimeout;
	private Integer statusCheckDuration;
	List<String> logicalNameMultipleAllowList = new ArrayList<String>(0);
	private String userIdentityAttributes;
	private String sendAuthInfo;
	private String numberOfTriplets="3";
	private String auditUId;
	
	public Integer getMapGwConnPoolSize() {
		return mapGwConnPoolSize;
	}
	public void setMapGwConnPoolSize(Integer mapGwConnPoolSize) {
		this.mapGwConnPoolSize = mapGwConnPoolSize;
	}
	public Integer getRequestTimeout() {
		return requestTimeout;
	}
	public void setRequestTimeout(Integer requestTimeout) {
		this.requestTimeout = requestTimeout;
	}
	public Integer getStatusCheckDuration() {
		return statusCheckDuration;
	}
	public void setStatusCheckDuration(Integer statusCheckDuration) {
		this.statusCheckDuration = statusCheckDuration;
	}
	public Long getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}
	public void setMaxQueryTimeoutCount(Long maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
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
	public List<ProfileFieldValuePoolData> getProfileFieldList() {
		return profileFieldList;
	}
	public void setProfileFieldList(List<ProfileFieldValuePoolData> profileFieldList) {
		this.profileFieldList = profileFieldList;
	}
	public String getLogicalName() {
		return logicalName;
	}
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	public String getProfileField() {
		return profileField;
	}
	public void setProfileField(String profileField) {
		this.profileField = profileField;
	}
	public Set getProfileFeildMapSet() {
		return profileFeildMapSet;
	}
	public void setProfileFeildMapSet(Set profileFeildMapSet) {
		this.profileFeildMapSet = profileFeildMapSet;
	}
	public String getDriverRelatedId() {
		return driverRelatedId;
	}
	public void setDriverRelatedId(String driverRelatedId) {
		this.driverRelatedId = driverRelatedId;
	}
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public String getLocalHostId() {
		return localHostId;
	}
	public void setLocalHostId(String localHostId) {
		this.localHostId = localHostId;
	}
	public int getLocalHostPort() {
		return localHostPort;
	}
	public void setLocalHostPort(int localHostPort) {
		this.localHostPort = localHostPort;
	}
	public String getLocalHostIp() {
		return localHostIp;
	}
	public void setLocalHostIp(String localHostIp) {
		this.localHostIp = localHostIp;
	}
	public String getRemoteHostId() {
		return remoteHostId;
	}
	public void setRemoteHostId(String remoteHostId) {
		this.remoteHostId = remoteHostId;
	}
	public int getRemoteHostPort() {
		return remoteHostPort;
	}
	public void setRemoteHostPort(int remoteHostPort) {
		this.remoteHostPort = remoteHostPort;
	}
	public String getRemoteHostIp() {
		return remoteHostIp;
	}
	public void setRemoteHostIp(String remoteHostIp) {
		this.remoteHostIp = remoteHostIp;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getDriverInstanceName() {
		return driverInstanceName;
	}
	public void setDriverInstanceName(String driverInstanceName) {
		this.driverInstanceName = driverInstanceName;
	}
	public String getDriverDesp() {
		return driverDesp;
	}
	public void setDriverDesp(String driverDesp) {
		this.driverDesp = driverDesp;
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
	public String getSendAuthInfo() {
		return sendAuthInfo;
	}
	public void setSendAuthInfo(String sendAuthInfo) {
		this.sendAuthInfo = sendAuthInfo;
	}
	public String getNumberOfTriplets() {
		return numberOfTriplets;
	}
	public void setNumberOfTriplets(String numberOfTriplets) {
		this.numberOfTriplets = numberOfTriplets;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	
	
}
