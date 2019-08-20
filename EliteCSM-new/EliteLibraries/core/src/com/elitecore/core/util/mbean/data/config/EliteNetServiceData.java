package com.elitecore.core.util.mbean.data.config;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class EliteNetServiceData implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String netServiceId;
	private String netInstanceId;	
	private List<EliteNetConfigurationData> netConfigurationList;
	private List netDriverList; //NOSONAR - Reason: Fields in a "Serializable" class should either be transient or serializable
	private List netSubServiceList; //NOSONAR - Reason: Fields in a "Serializable" class should either be transient or serializable
	private String netServiceName;
	private String netServiceInstanceName;
	private String description;
	private Map miscParameterMap; //NOSONAR - Reason: Fields in a "Serializable" class should either be transient or serializable
	
	
	public Map getMiscParameterMap() {
		return miscParameterMap;
	}
	public void setMiscParameterMap(Map miscParameterMap) {
		this.miscParameterMap = miscParameterMap;
	}
	public List getNetSubServiceList() {
		return netSubServiceList;
	}
	public void setNetSubServiceList(List netSubServiceList) {
		this.netSubServiceList = netSubServiceList;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getNetServiceName() {
		return netServiceName;
	}
	public void setNetServiceName(String netServiceName) {
		this.netServiceName = netServiceName;
	}
	public List<EliteNetConfigurationData> getNetConfigurationList() {
		return netConfigurationList;
	}
	public void setNetConfigurationList(List<EliteNetConfigurationData> netConfigurationList) {
		this.netConfigurationList = netConfigurationList;
	}
	public String getNetInstanceId() {
		return netInstanceId;
	}
	public void setNetInstanceId(String netInstanceId) {
		this.netInstanceId = netInstanceId;
	}
	public String getNetServiceId() {
		return netServiceId;
	}
	public void setNetServiceId(String netServiceId) {
		this.netServiceId = netServiceId;
	}
	public List getNetDriverList() {
		return netDriverList;
	}
	public void setNetDriverList(List netDriverList) {
		this.netDriverList = netDriverList;
	}
	public String getNetServiceInstanceName() {
		return netServiceInstanceName;
	}
	public void setNetServiceInstanceName(String netServiceInstanceName) {
		this.netServiceInstanceName = netServiceInstanceName;
	} 

}
