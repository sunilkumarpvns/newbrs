package com.elitecore.elitesm.web.servermgr.server.forms;

import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ViewNetServerServicesForm extends BaseWebForm{ 
	private String netServerId;
	private Long netServiceId;
	private String name;
	private String description;
	private String displayName;
	private String netServiceTypeId;
	private String action;
	private String status;
	private List listServices;
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
	public String getNetServiceTypeId() {
		return netServiceTypeId;
	}
	public void setNetServiceTypeId(String netServiceTypeId) {
		this.netServiceTypeId = netServiceTypeId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List getListServices() {
		return listServices;
	}
	public void setListServices(List listServices) {
		this.listServices = listServices;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNetServerId() {
		return netServerId;
	}
	public void setNetServerId(String netServerId) {
		this.netServerId = netServerId;
	}
}
