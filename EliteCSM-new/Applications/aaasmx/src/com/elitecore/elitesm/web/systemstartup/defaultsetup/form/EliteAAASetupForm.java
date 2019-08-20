package com.elitecore.elitesm.web.systemstartup.defaultsetup.form;

import java.util.LinkedHashMap;
import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.controller.EliteAAASetupAction.MODULE_NAME_CONSTANTS;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.controller.EliteAAASetupAction.MODULE_STATUS;

public class EliteAAASetupForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private String databaseDatasourceURL;
	private String databasePassword;
	private String databaseUsername;
	private String userName;
	private String password;
	private String confirmPassword;
	private String MACAddress; 
	private String IPAddress;
	private Integer concurrentLoginLimit;
	private List<String> duplicateInstances;
	private String success;
	private LinkedHashMap<MODULE_NAME_CONSTANTS,MODULE_STATUS> moduleStatusMap = new LinkedHashMap<MODULE_NAME_CONSTANTS, MODULE_STATUS>();;
	
	public String getDatabaseDatasourceURL() {
		return databaseDatasourceURL;
	}
	public void setDatabaseDatasourceURL(String databaseDatasourceURL) {
		this.databaseDatasourceURL = databaseDatasourceURL;
	}
	public String getDatabasePassword() {
		return databasePassword;
	}
	public void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}
	public String getDatabaseUsername() {
		return databaseUsername;
	}
	public void setDatabaseUsername(String databaseUsername) {
		this.databaseUsername = databaseUsername;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public String getMACAddress() {
		return MACAddress;
	}
	public void setMACAddress(String mACAddress) {
		MACAddress = mACAddress;
	}
	public String getIPAddress() {
		return IPAddress;
	}
	public void setIPAddress(String iPAddress) {
		IPAddress = iPAddress;
	}
	public Integer getConcurrentLoginLimit() {
		return concurrentLoginLimit;
	}
	public void setConcurrentLoginLimit(Integer concurrentLoginLimit) {
		this.concurrentLoginLimit = concurrentLoginLimit;
	}
	public List<String> getDuplicateInstances() {
		return duplicateInstances;
	}
	public void setDuplicateInstances(List<String> duplicateInstances) {
		this.duplicateInstances = duplicateInstances;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public LinkedHashMap<MODULE_NAME_CONSTANTS, MODULE_STATUS> getModuleStatusMap() {
		return moduleStatusMap;
	}
	public void setModuleStatusMap(
			LinkedHashMap<MODULE_NAME_CONSTANTS, MODULE_STATUS> moduleStatusMap) {
		this.moduleStatusMap = moduleStatusMap;
	}
}