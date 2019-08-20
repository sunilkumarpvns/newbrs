package com.elitecore.elitesm.web.servermgr.server.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ServerEnvironmentForm extends BaseWebForm{
	private String name;
	private String netServerId;
	private String netServerType;
	private String description;
	private String action;
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
}
