package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

public class PluginData {
	private String prePluginList;
	private String[] pluginList;
	
	
	public String getPrePluginList() {
		return prePluginList;
	}
	public void setPrePluginList(String prePluginList) {
		if(prePluginList != null && prePluginList.length() > 0){
			String[] strPlugin = prePluginList.split(", ");
			this.pluginList = strPlugin;
		}
		this.prePluginList = prePluginList;
	}
	
	@Override
	public String toString() {
		return "----------------PrePluginData------------------------\n  ruleset = "
				+ "\n  prePluginList = "
				+ prePluginList
				+ "\n----------------PrePluginData-------------------------\n";
	}
	
	public String[] getPluginList() {
		return pluginList;
	}
	public void setPluginList(String[] pluginList) {
		this.pluginList = pluginList;
	}
	
}
