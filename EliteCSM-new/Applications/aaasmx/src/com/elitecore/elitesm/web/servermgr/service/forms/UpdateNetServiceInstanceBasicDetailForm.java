package com.elitecore.elitesm.web.servermgr.service.forms;

import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;

public class UpdateNetServiceInstanceBasicDetailForm extends BaseDictionaryForm{
	private String netServiceId;
	private String name;
	private String description;
	private String action;
	private String netServiceType;
	
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
	public String getNetServiceId() {
		return netServiceId;
	}
	public void setNetServiceId(String netServiceId) {
		this.netServiceId = netServiceId;
	}
	public String getNetServiceType() {
		return netServiceType;
	}
	public void setNetServiceType(String netServiceType) {
		this.netServiceType = netServiceType;
	}
}
