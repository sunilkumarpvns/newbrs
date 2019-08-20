package com.elitecore.elitesm.web.servermgr.server.forms;

import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ListNetServerConfigurationForm extends BaseWebForm{
	private String netServerId;
	private String netConfigId;
	private String netConfigInstanceId;
	private String name;
	private String action;
	private List configInstanceList;
	private List pluginConfigInstanceList;
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	
	public List getPluginConfigInstanceList() {
		return pluginConfigInstanceList;
	}
	public void setPluginConfigInstanceList(List pluginConfigInstanceList) {
		this.pluginConfigInstanceList = pluginConfigInstanceList;
	}
	public List getConfigInstanceList() {
		return configInstanceList;
	}
	public void setConfigInstanceList(List configInstanceList) {
		this.configInstanceList = configInstanceList;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNetConfigId() {
		return netConfigId;
	}
	public void setNetConfigId(String netConfigId) {
		this.netConfigId = netConfigId;
	}
	public String getNetConfigInstanceId() {
		return netConfigInstanceId;
	}
	public void setNetConfigInstanceId(String netConfigInstanceId) {
		this.netConfigInstanceId = netConfigInstanceId;
	}
	public String getNetServerId() {
		return netServerId;
	}
	public void setNetServerId(String netServerId) {
		this.netServerId = netServerId;
	}
}
