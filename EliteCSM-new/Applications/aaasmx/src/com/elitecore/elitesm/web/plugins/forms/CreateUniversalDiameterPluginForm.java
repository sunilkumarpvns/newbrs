package com.elitecore.elitesm.web.plugins.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

/**
 * @author nayana.rathod
 *
 */
public class CreateUniversalDiameterPluginForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private long pluginId;
	private String pluginType;
	private long pluginInstanceId;
	private byte[] pluginData;
	private String action;
	private String universalPluginPolicyJson;
	private String pluginName;
	private String description;
	private String status;
	
	public long getPluginId() {
		return pluginId;
	}
	public void setPluginId(long pluginId) {
		this.pluginId = pluginId;
	}
	public String getPluginType() {
		return pluginType;
	}
	public void setPluginType(String pluginType) {
		this.pluginType = pluginType;
	}
	public long getPluginInstanceId() {
		return pluginInstanceId;
	}
	public void setPluginInstanceId(long pluginInstanceId) {
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
	public String getUniversalPluginPolicyJson() {
		return universalPluginPolicyJson;
	}
	public void setUniversalPluginPolicyJson(String universalPluginPolicyJson) {
		this.universalPluginPolicyJson = universalPluginPolicyJson;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
