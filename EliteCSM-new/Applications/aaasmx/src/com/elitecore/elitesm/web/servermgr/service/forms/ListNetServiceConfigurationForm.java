package com.elitecore.elitesm.web.servermgr.service.forms;

import java.util.List;

import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;

public class ListNetServiceConfigurationForm extends BaseDictionaryForm{
	private String netServiceId;
	private String netConfigId;
	private String netConfigInstanceId;
	private String name;
	private String displayName;
	private String action;
	private List configInstanceList;
	private List driverConfigInstanceList;	
	private List subConfigInstanceList;
	private List subDriverConfigInstanceList;	
	
	public List getSubConfigInstanceList() {
		return subConfigInstanceList;
	}
	public void setSubConfigInstanceList(List subConfigInstanceList) {
		this.subConfigInstanceList = subConfigInstanceList;
	}
	public List getSubDriverConfigInstanceList() {
		return subDriverConfigInstanceList;
	}
	public void setSubDriverConfigInstanceList(List subDriverConfigInstanceList) {
		this.subDriverConfigInstanceList = subDriverConfigInstanceList;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
	public String getNetServiceId() {
		return netServiceId;
	}
	public void setNetServiceId(String netServiceId) {
		this.netServiceId = netServiceId;
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
	public List getDriverConfigInstanceList() {
		return driverConfigInstanceList;
	}
	public void setDriverConfigInstanceList(List driverConfigInstanceList) {
		this.driverConfigInstanceList = driverConfigInstanceList;
	}
}
