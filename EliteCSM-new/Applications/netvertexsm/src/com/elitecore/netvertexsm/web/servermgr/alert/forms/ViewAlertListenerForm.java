package com.elitecore.netvertexsm.web.servermgr.alert.forms;

import java.util.List;

import org.apache.struts.action.ActionForm;

import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertTypeData;

public class ViewAlertListenerForm extends ActionForm {
	private static final long serialVersionUID = 1L;
	private String name;
	private String typeId;
	private String fileName;
	private String filePath;
	private String trapServer;
	private String trapVersion;
	private String community;
	private Byte snmpRequestType;
	private Integer timeout;
	private Byte retryCount;	
	private List<AlertListenerTypeData> availableListenerTypes;
	private Long listenerId;
	private String enabledAlerts;
	private List<AlertTypeData> enabledAlertsList;
	private List<String> selectedFloodControl;
	private List<String> selectedAlertsTypeList;
	private String[] selectedAlertsType;
	
	public List<AlertTypeData> getEnabledAlertsList() {
		return enabledAlertsList;
	}
	public void setEnabledAlertsList(List<AlertTypeData> enabledAlertsList) {
		this.enabledAlertsList = enabledAlertsList;
	}
	public String getEnabledAlerts() {
		return enabledAlerts;
	}
	public void setEnabledAlerts(String enabledAlerts) {
		this.enabledAlerts = enabledAlerts;
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
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
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
	
	public List<String> getSelectedFloodControl() {
		return selectedFloodControl;
	}

	public List<String> getSelectedAlertsTypeList() {
		return selectedAlertsTypeList;
	}
	
	public void setSelectedFloodControl(List<String> selectedFloodControl) {
		this.selectedFloodControl = selectedFloodControl;
	}

	public void setSelectedAlertsTypeList(List<String> selectedAlertsTypeList) {
		this.selectedAlertsTypeList = selectedAlertsTypeList;
	}
  

	public String[] getSelectedAlertsType() {
		return selectedAlertsType;
	}
	public void setSelectedAlertsType(String[] selectedAlertsType) {
		this.selectedAlertsType = selectedAlertsType;
	}
	
}
