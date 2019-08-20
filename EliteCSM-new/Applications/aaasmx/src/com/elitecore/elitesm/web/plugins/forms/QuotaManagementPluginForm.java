package com.elitecore.elitesm.web.plugins.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

/**
 * @author nayana.rathod
 *
 */
public class QuotaManagementPluginForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private String pluginId;
	private String pluginType;
	private String pluginInstanceId;
	private byte[] pluginData;
	private String action;
	private String pluginName;
	private String description;
	private String quotaMgtJson;
	private String auditUId;
	private String status; 
	
	public String getPluginId() {
		return pluginId;
	}
	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}
	public String getPluginType() {
		return pluginType;
	}
	public void setPluginType(String pluginType) {
		this.pluginType = pluginType;
	}
	public String getPluginInstanceId() {
		return pluginInstanceId;
	}
	public void setPluginInstanceId(String pluginInstanceId) {
		this.pluginInstanceId = pluginInstanceId;
	}
	public byte[] getPluginData() {
		return pluginData;
	}
	public void setPluginData(byte[] pluginData) {
		this.pluginData = pluginData;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getPluginName() {
		return pluginName;
	}
	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getQuotaMgtJson() {
		return quotaMgtJson;
	}
	public void setQuotaMgtJson(String quotaMgtJson) {
		this.quotaMgtJson = quotaMgtJson;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
