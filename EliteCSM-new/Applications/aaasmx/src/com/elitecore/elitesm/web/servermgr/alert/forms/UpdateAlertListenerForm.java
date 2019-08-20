package com.elitecore.elitesm.web.servermgr.alert.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerTypeData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.SYSLogNameValuePoolData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateAlertListenerForm extends BaseWebForm {
	private static final long serialVersionUID = 1L;
	private String name;
	private String typeId;
	private String fileName;
	private String enabledAlerts;
	private Long rollingType;
	private Long rollingUnit;
	private Long maxRollingUnit;
	private String compRollingUnit;
	private String trapServer;
	private String trapVersion;
	private String community;
	private List<AlertListenerTypeData> availableListenerTypes;
	private String[] selectedAlertsType;
	private String action;
	private String listenerId;
	private String advanceTrap;
	private String address;
	private String facility;
	private List<SYSLogNameValuePoolData> sysLogNameValuePoolDataList;
	private String auditUId;
	private String repeatedMessageReduction;
	
	private Long maxRollingUnitSizedBased=10L;
	private Long maxRollingUnitTimeBased=10L;
	
	private Long rollingUnitSizedBased=5L;
	private Long rollingUnitTimeBased=5L;
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getFacility() {
		return facility;
	}
	public void setFacility(String facility) {
		this.facility = facility;
	}
	public List<SYSLogNameValuePoolData> getSysLogNameValuePoolDataList() {
		return sysLogNameValuePoolDataList;
	}
	public void setSysLogNameValuePoolDataList(
			List<SYSLogNameValuePoolData> sysLogNameValuePoolDataList) {
		this.sysLogNameValuePoolDataList = sysLogNameValuePoolDataList;
	}
	public String getAdvanceTrap() {
		return advanceTrap;
	}
	public void setAdvanceTrap(String advanceTrap) {
		this.advanceTrap = advanceTrap;
	}
	public String getEnabledAlerts() {
		return enabledAlerts;
	}
	public void setEnabledAlerts(String enabledAlerts) {
		this.enabledAlerts = enabledAlerts;
	}
	public String[] getSelectedAlertsType() {
		return selectedAlertsType;
	}
	public void setSelectedAlertsType(String[] selectedAlertsType) {
		this.selectedAlertsType = selectedAlertsType;
	}
	public String getListenerId() {
		return listenerId;
	}
	public void setListenerId(String listenerId) {
		this.listenerId = listenerId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getTrapServer() {
		return trapServer;
	}
	public void setTrapServer(String trapServer) {
		this.trapServer = trapServer;
	}
	public String getTrapVersion() {
		return trapVersion;
	}
	public void setTrapVersion(String trapVersion) {
		this.trapVersion = trapVersion;
	}
	public String getCommunity() {
		return community;
	}
	public void setCommunity(String community) {
		this.community = community;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public List<AlertListenerTypeData> getAvailableListenerTypes() {
		return availableListenerTypes;
	}
	public void setAvailableListenerTypes(
			List<AlertListenerTypeData> availableListenerTypes) {
		this.availableListenerTypes = availableListenerTypes;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Long getRollingType() {
		return rollingType;
	}
	public void setRollingType(Long rollingType) {
		this.rollingType = rollingType;
	}
	public Long getRollingUnit() {
		return rollingUnit;
	}
	public void setRollingUnit(Long rollingUnit) {
		this.rollingUnit = rollingUnit;
	}
	public Long getMaxRollingUnit() {
		return maxRollingUnit;
	}
	public void setMaxRollingUnit(Long maxRollingUnit) {
		this.maxRollingUnit = maxRollingUnit;
	}
	public String getCompRollingUnit() {
		return compRollingUnit;
	}
	public void setCompRollingUnit(String compRollingUnit) {
		this.compRollingUnit = compRollingUnit;
	}
	public Long getMaxRollingUnitSizedBased() {
		return maxRollingUnitSizedBased;
	}
	public void setMaxRollingUnitSizedBased(Long maxRollingUnitSizedBased) {
		this.maxRollingUnitSizedBased = maxRollingUnitSizedBased;
	}
	public Long getMaxRollingUnitTimeBased() {
		return maxRollingUnitTimeBased;
	}
	public void setMaxRollingUnitTimeBased(Long maxRollingUnitTimeBased) {
		this.maxRollingUnitTimeBased = maxRollingUnitTimeBased;
	}
	public Long getRollingUnitSizedBased() {
		return rollingUnitSizedBased;
	}
	public void setRollingUnitSizedBased(Long rollingUnitSizedBased) {
		this.rollingUnitSizedBased = rollingUnitSizedBased;
	}
	public Long getRollingUnitTimeBased() {
		return rollingUnitTimeBased;
	}
	public void setRollingUnitTimeBased(Long rollingUnitTimeBased) {
		this.rollingUnitTimeBased = rollingUnitTimeBased;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	public String getRepeatedMessageReduction() {
		return repeatedMessageReduction;
	}
	public void setRepeatedMessageReduction(String repeatedMessageReduction) {
		this.repeatedMessageReduction = repeatedMessageReduction;
	}
	
	

}
