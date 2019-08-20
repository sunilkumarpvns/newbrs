package com.elitecore.elitesm.web.servermgr.server.forms;

import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ViewNetServerLiveDetailsForm extends BaseWebForm{
	private String name;
	private String netServerId;
	private String netServerType;
	private String description;
	private String version;
	private String action;
	private List serviceDetails;
	private String errorCode;
	private List internalTasks;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNetServerId() {
		return netServerId;
	}
	public void setNetServerId(String netServerId) {
		this.netServerId = netServerId;
	}
	public String getNetServerType() {
		return netServerType;
	}
	public void setNetServerType(String netServerType) {
		this.netServerType = netServerType;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public List getServiceDetails() {
		return serviceDetails;
	}
	public void setServiceDetails(List serviceDetails) {
		this.serviceDetails = serviceDetails;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public List getInternalTasks() {
		return internalTasks;
	}
	public void setInternalTasks(List internalTasks) {
		this.internalTasks = internalTasks;
	}
}
