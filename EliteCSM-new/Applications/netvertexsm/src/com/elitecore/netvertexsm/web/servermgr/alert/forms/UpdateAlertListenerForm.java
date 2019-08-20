package com.elitecore.netvertexsm.web.servermgr.alert.forms;

import java.util.List;

import org.apache.struts.action.ActionForm;

import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerTypeData;

public class UpdateAlertListenerForm extends ActionForm {
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
	private Long listenerId;
	private String advanceTrap;
	private Byte snmpRequestType;
	private Integer timeout;
	private Byte retryCount;
	
	private Long maxRollingUnitSizedBased=10L;
	private Long maxRollingUnitTimeBased=10L;
	
	private Long rollingUnitSizedBased=5L;
	private Long rollingUnitTimeBased=5L;
	private List<String> selectedFloodControl;
	private List<String> selectedAlertsTypeList;
	
	public List<String> getSelectedAlertsTypeList() {
		return selectedAlertsTypeList;
	}
	public void setSelectedAlertsTypeList(List<String> selectedAlertsTypeList) {
		this.selectedAlertsTypeList = selectedAlertsTypeList;
	}
	public List<String> getSelectedFloodControl() {
		return selectedFloodControl;
	}
	public void setSelectedFloodControl(List<String> selectedFloodControl) {
		this.selectedFloodControl = selectedFloodControl;
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
	public Long getListenerId() {
		return listenerId;
	}
	public void setListenerId(Long listenerId) {
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
	public Byte getSnmpRequestType() {
		return snmpRequestType;
	}
	public void setSnmpRequestType(Byte snmpRequestType) {
		this.snmpRequestType = snmpRequestType;
	}
	public Integer getTimeout() {
		return timeout;
	}
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	public Byte getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(Byte retryCount) {
		this.retryCount = retryCount;
	}
	
	

}
