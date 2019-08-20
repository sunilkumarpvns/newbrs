package com.elitecore.core.util.mbean.data.config;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class EliteNetServerData implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String netServerId;
	private List<EliteNetConfigurationData> netConfigurationList;
	private List<EliteNetServiceData> netServiceList;
	private String netServerName;
	private String version;
	private List<EliteNetPluginData> pluginList;
	private Map miscParameterMap; //NOSONAR
	
	
	public Map getMiscParameterMap() {
		return miscParameterMap;
	}
	public void setMiscParameterMap(Map miscParameterMap) {
		this.miscParameterMap = miscParameterMap;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getNetServerName() {
		return netServerName;
	}
	public void setNetServerName(String netServerName) {
		this.netServerName = netServerName;
	}
	public List<EliteNetServiceData> getNetServiceList() {
		return netServiceList;
	}
	public void setNetServiceList(List<EliteNetServiceData> netServiceList) {
		this.netServiceList = netServiceList;
	}
	public List<EliteNetConfigurationData> getNetConfigurationList() {
		return netConfigurationList;
	}
	public void setNetConfigurationList(List<EliteNetConfigurationData> netConfigurationList) {
		this.netConfigurationList = netConfigurationList;
	}
	public String getNetServerId() {
		return netServerId;
	}
	public void setNetServerId(String netServerId) {
		this.netServerId = netServerId;
	}
	public List<EliteNetPluginData> getPluginList() {
		return pluginList;
	}
	public void setPluginList(List<EliteNetPluginData> pluginList) {
		this.pluginList = pluginList;
	}

	
}
