package com.elitecore.core.util.mbean.data.config;

import java.io.Serializable;
import java.util.Map;

public class EliteNetConfigurationData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String netConfigurationId;
	private String netConfigurationKey;	
	private byte[] netConfigurationData;
	private Map miscParameterMap; //NOSONAR
	
	
	public Map getMiscParameterMap() {
		return miscParameterMap;
	}
	public void setMiscParameterMap(Map miscParameterMap) {
		this.miscParameterMap = miscParameterMap;
	}
	public byte[] getNetConfigurationData() {
		return netConfigurationData;
	}
	public void setNetConfigurationData(byte[] netConfigurationData) {
		this.netConfigurationData = netConfigurationData;
	}
	public String getNetConfigurationId() {
		return netConfigurationId;
	}
	public void setNetConfigurationId(String netConfigurationId) {
		this.netConfigurationId = netConfigurationId;
	}
	public String getNetConfigurationKey() {
		return netConfigurationKey;
	}
	public void setNetConfigurationKey(String netConfigurationKey) {
		this.netConfigurationKey = netConfigurationKey;
	} 
	
}
