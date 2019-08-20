package com.elitecore.elitesm.web.servermgr.service.forms;

import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;

public class ViewNetServiceInstanceForm extends BaseDictionaryForm{
	private String netServiceId;
	private String netServiceTypdId;
	private String name;
	private String displayName;
	private String description;
	private String status;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public String getNetServiceId() {
		return netServiceId;
	}
	public void setNetServiceId(String netServiceId) {
		this.netServiceId = netServiceId;
	}
	public String getNetServiceTypdId() {
		return netServiceTypdId;
	}
	public void setNetServiceTypdId(String netServiceTypdId) {
		this.netServiceTypdId = netServiceTypdId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
