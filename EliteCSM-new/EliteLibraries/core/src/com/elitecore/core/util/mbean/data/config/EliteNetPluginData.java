package com.elitecore.core.util.mbean.data.config;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class EliteNetPluginData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private String pluginName;	
	private List<EliteNetConfigurationData> netConfigurationDataList;
	private Map miscParameterMap; //NOSONAR
	
	
	public Map getMiscParameterMap() {
		return miscParameterMap;
	}
	public void setMiscParameterMap(Map miscParameterMap) {
		this.miscParameterMap = miscParameterMap;
	}
	
	public String getPluginName() {
		return pluginName;
	}
	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	public List<EliteNetConfigurationData> getNetConfigurationDataList() {
		return netConfigurationDataList;
	}
	public void setNetConfigurationDataList(List<EliteNetConfigurationData> netConfigurationDataList) {
		this.netConfigurationDataList = netConfigurationDataList;
	}
	
	
	
	
	
}
