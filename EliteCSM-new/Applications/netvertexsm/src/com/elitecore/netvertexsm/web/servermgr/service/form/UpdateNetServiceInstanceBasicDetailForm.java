package com.elitecore.netvertexsm.web.servermgr.service.form;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class UpdateNetServiceInstanceBasicDetailForm extends BaseWebForm{
	private Long netServiceId;
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
	public Long getNetServiceId() {
		return netServiceId;
	}
	public void setNetServiceId(Long netServiceId) {
		this.netServiceId = netServiceId;
	}
	public String getNetServiceType() {
		return netServiceType;
	}
	public void setNetServiceType(String netServiceType) {
		this.netServiceType = netServiceType;
	}
}
