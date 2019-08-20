package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;


public class PluginDetails {
	private boolean requestType;
	private String pluginName;
	private String pluginArgument;
	private String ruleset;

	public String getRuleset() {
		return ruleset;
	}
	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}
	public boolean isRequestType() {
		return requestType;
	}
	public void setRequestType(boolean requestType) {
		this.requestType = requestType;
	}
	public String getPluginArgument() {
		return pluginArgument;
	}
	public void setPluginArgument(String pluginArgument) {
		this.pluginArgument = pluginArgument;
	}
	public String getPluginName() {
		return pluginName;
	}
	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}
}
