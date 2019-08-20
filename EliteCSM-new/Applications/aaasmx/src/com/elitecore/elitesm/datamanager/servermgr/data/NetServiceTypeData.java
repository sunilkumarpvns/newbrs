package com.elitecore.elitesm.datamanager.servermgr.data;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

/**
 * 
 * @author dhavalraval
 * NetServiceTypeData persistent class is generated to map with NetServiceType.hbm.xml file
 * 
 */
public class NetServiceTypeData extends BaseData implements INetServiceTypeData{
	private String netServiceTypeId;
	private String netServerTypeId;
	private String name;
	private String alias;
	private String description;
	private int maxInstances;
	private String systemGenerated;
	public String getNetServiceTypeId() {
		return netServiceTypeId;
	}
	public void setNetServiceTypeId(String netServiceTypeId) {
		this.netServiceTypeId = netServiceTypeId;
	}
	public String getNetServerTypeId() {
		return netServerTypeId;
	}
	public void setNetServerTypeId(String netServerTypeId) {
		this.netServerTypeId = netServerTypeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getMaxInstances() {
		return maxInstances;
	}
	public void setMaxInstances(int maxInstances) {
		this.maxInstances = maxInstances;
	}
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
	
}
