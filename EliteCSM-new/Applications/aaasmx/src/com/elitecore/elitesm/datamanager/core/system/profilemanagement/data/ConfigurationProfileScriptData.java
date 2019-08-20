package com.elitecore.elitesm.datamanager.core.system.profilemanagement.data;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class ConfigurationProfileScriptData extends BaseData{
	private String profileScriptId;
	private String configurationProfileId;
	private String netScript;
	private String executionMode;
	public String getConfigurationProfileId() {
		return configurationProfileId;
	}
	public void setConfigurationProfileId(String configurationProfileId) {
		this.configurationProfileId = configurationProfileId;
	}
	public String getExecutionMode() {
		return executionMode;
	}
	public void setExecutionMode(String executionMode) {
		this.executionMode = executionMode;
	}
	public String getNetScript() {
		return netScript;
	}
	public void setNetScript(String netScript) {
		this.netScript = netScript;
	}
	public String getProfileScriptId() {
		return profileScriptId;
	}
	public void setProfileScriptId(String profileScriptId) {
		this.profileScriptId = profileScriptId;
	}
}
