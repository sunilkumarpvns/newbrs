package com.elitecore.netvertexsm.web.servermgr.server.form;

import java.util.List;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class RemoveNetServerServiceInstanceForm extends BaseWebForm{
	private Long netServerId;
	private Long netServiceId;
	private String name;
	private String description;
	private String displayName;
	private String netServiceTypeId;
	private String action;
	private String status;
	private List listServices;
	private int itemIndex;
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
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public List getListServices() {
		return listServices;
	}
	public void setListServices(List listServices) {
		this.listServices = listServices;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getNetServerId() {
		return netServerId;
	}
	public void setNetServerId(Long netServerId) {
		this.netServerId = netServerId;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getItemIndex() {
		return itemIndex;
	}
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
}
