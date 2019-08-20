package com.elitecore.core.util.mbean.data.config;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class EliteNetDriverData implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String netDriverId;
    private List netConfigurationList; //NOSONAR
    private String netDriverName;
    private String netDriverInstanceName;
    private String description; 
    private String netDriverInstanceMode;
    private Map miscParameterMap; //NOSONAR
	
	
	public Map getMiscParameterMap() {
		return miscParameterMap;
	}
	public void setMiscParameterMap(Map miscParameterMap) {
		this.miscParameterMap = miscParameterMap;
	}
    
    public String getNetDriverName() {
        return netDriverName;
    }
    public void setNetDriverName(String netDriverName) {
        this.netDriverName = netDriverName;
    }
    public List getNetConfigurationList() {
        return netConfigurationList;
    }
    public void setNetConfigurationList(List netConfigurationList) {
        this.netConfigurationList = netConfigurationList;
    }
    public String getNetDriverId() {
        return netDriverId;
    }
    public void setNetDriverId(String netDriverId) {
        this.netDriverId = netDriverId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getNetDriverInstanceName() {
        return netDriverInstanceName;
    }
    public void setNetDriverInstanceName(String netDriverInstanceName) {
        this.netDriverInstanceName = netDriverInstanceName;
    }
    public String getNetDriverInstanceMode() {
        return netDriverInstanceMode;
    }
    public void setNetDriverInstanceMode(String netDriverInstanceMode) {
        this.netDriverInstanceMode = netDriverInstanceMode;
    } 
}
