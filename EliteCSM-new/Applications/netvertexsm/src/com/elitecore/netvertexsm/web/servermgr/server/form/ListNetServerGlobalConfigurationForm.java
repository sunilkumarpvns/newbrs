package com.elitecore.netvertexsm.web.servermgr.server.form;

import java.util.List;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class ListNetServerGlobalConfigurationForm extends BaseWebForm{
	private String action;
	private long netServerId;
	private String netConfigMapTypeId;
	private String name;
	private String description;
	private String alias;
	private List configInstanceList;
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
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
	public String getNetConfigMapTypeId() {
		return netConfigMapTypeId;
	}
	public void setNetConfigMapTypeId(String netConfigMapTypeId) {
		this.netConfigMapTypeId = netConfigMapTypeId;
	}
	public long getNetServerId() {
		return netServerId;
	}
	public void setNetServerId(long netServerId) {
		this.netServerId = netServerId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List getConfigInstanceList() {
		return configInstanceList;
	}
	public void setConfigInstanceList(List configInstanceList) {
		this.configInstanceList = configInstanceList;
	}
}
