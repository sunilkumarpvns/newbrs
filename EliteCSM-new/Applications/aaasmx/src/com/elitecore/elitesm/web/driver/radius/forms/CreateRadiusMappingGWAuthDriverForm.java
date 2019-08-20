package com.elitecore.elitesm.web.driver.radius.forms;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.ProfileFieldValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthFieldMapData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.mapgatewaydriver.data.GatewayFieldMapData;
import com.elitecore.elitesm.util.constants.AccountFieldConstants;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateRadiusMappingGWAuthDriverForm extends BaseWebForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String localHostId="AAA-ULTICOM";
	private int localHostPort;
	private String localHostIp="127.0.0.1";
	private String remoteHostId="AAA-ULTICOM";;
	private int remoteHostPort=10090;
	private String remoteHostIp="127.0.0.1";
	private String action;
	private String driverInstanceName;
	private String driverDesp;
	private String driverRelatedId;
	private List<LogicalNameValuePoolData> logicalNameList;
	private List<ProfileFieldValuePoolData> profileFieldList;
	private String logicalName;
	private String profileField;
	private String defaultValue;
	private String valueMapping;
	private Long maxQueryTimeoutCount = 100L;
	private Integer mapGwConnPoolSize=10;
	private Integer requestTimeout=1000;
	private Integer statusCheckDuration=60;
	List<String> logicalNameMultipleAllowList = new ArrayList<String>(0);
	private String userIdentityAttributes;
	private String sendAuthInfo;
	private String numberOfTriplets="3";
	
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

	public List<LogicalNameValuePoolData> getLogicalNameList() {
		return logicalNameList;
	}

	public void setLogicalNameList(List<LogicalNameValuePoolData> logicalNameList) {
		this.logicalNameList = logicalNameList;
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

	public String getDriverRelatedId() {
		return driverRelatedId;
	}

	public void setDriverRelatedId(String driverRelatedId) {
		this.driverRelatedId = driverRelatedId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
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
	
	public List<GatewayFieldMapData> getDefaultmapping(){
		List<GatewayFieldMapData> defaultMappingList = new ArrayList<GatewayFieldMapData>();
		String[] logicalnm = {AccountFieldConstants.IMSI,AccountFieldConstants.MSISDN,AccountFieldConstants.CUSTOMER_STATUS};
		String[] fieldMap = {"IMSI","MSISDN","CustomerStatus"};
		String[] logicalv = {AccountFieldConstants.IMSI,AccountFieldConstants.MSISDN,AccountFieldConstants.CUSTOMER_STATUS};
		for(int index = 0 ; index < logicalnm.length ; index++){
			GatewayFieldMapData gatewayFieldMapData = new GatewayFieldMapData();
			LogicalNameValuePoolData nameValuePoolData = new LogicalNameValuePoolData();
			ProfileFieldValuePoolData profileFieldValuePoolData=new ProfileFieldValuePoolData();
			nameValuePoolData.setName(logicalnm[index]);
			nameValuePoolData.setValue(logicalv[index]);
			profileFieldValuePoolData.setName(fieldMap[index]);
			profileFieldValuePoolData.setValue(logicalv[index]);
			gatewayFieldMapData.setNameValuePoolData(nameValuePoolData);
			gatewayFieldMapData.setProfileFieldValuePoolData(profileFieldValuePoolData);
			gatewayFieldMapData.setProfileField(fieldMap[index]);
			defaultMappingList.add(gatewayFieldMapData);
		}
		return defaultMappingList;
	}
}
