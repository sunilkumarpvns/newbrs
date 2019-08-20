package com.elitecore.elitesm.web.servermgr.alert.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerTypeData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertTypeData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.SYSLogNameValuePoolData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ViewAlertListenerForm extends BaseWebForm {
	private static final long serialVersionUID = 1L;
	private String name;
	private String typeId;
	private String fileName;
	private String filePath;
	private String trapServer;
	private String trapVersion;
	private String community;
	private String address;
	private String facility;
	private List<AlertListenerTypeData> availableListenerTypes;
	private List<SYSLogNameValuePoolData> sysLogNameValuePoolDataList;
	private String listenerId;
	private String enabledAlerts;
	private List<AlertTypeData> enabledAlertsList;
	
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

}
