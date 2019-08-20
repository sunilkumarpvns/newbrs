package com.elitecore.netvertexsm.web.servermgr.service.form;

import java.util.List;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class RemoveNetServiceDriverInstanceForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private String netServiceId;
	private String netDriverId;
	private String name;
	private String description;
	private String displayName;
	private String netDriverTypeId;
	private String action;
	private String status;
	private List listDrivers;
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
	public int getItemIndex() {
		return itemIndex;
	}
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
	public List getListDrivers() {
		return listDrivers;
	}
	public void setListDrivers(List listDrivers) {
		this.listDrivers = listDrivers;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNetDriverId() {
		return netDriverId;
	}
	public void setNetDriverId(String netDriverId) {
		this.netDriverId = netDriverId;
	}
	public String getNetDriverTypeId() {
		return netDriverTypeId;
	}
	public void setNetDriverTypeId(String netDriverTypeId) {
		this.netDriverTypeId = netDriverTypeId;
	}
	public String getNetServiceId() {
		return netServiceId;
	}
	public void setNetServiceId(String netServiceId) {
		this.netServiceId = netServiceId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
