package com.elitecore.netvertexsm.web.servermgr.service.form;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class ViewNetServiceInstanceForm extends BaseWebForm{
	private Long netServiceId;
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
	public Long getNetServiceId() {
		return netServiceId;
	}
	public void setNetServiceId(Long netServiceId) {
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
