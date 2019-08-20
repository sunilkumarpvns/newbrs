package com.elitecore.elitesm.datamanager.servermgr.data;

import java.io.Serializable;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

/**
 * 
 * @author dhavalraval
 *
 */
public class NetServerConfigMapData extends BaseData implements INetServerConfigMapData,Serializable{
	private String netServerTypeId;
	private String netConfigId;
	private String netServerInstance;
	private String netConfigurationInstance;
	private String netConfigMapTypeId;
	public String getNetConfigId() {
		return netConfigId;
	}
	public void setNetConfigId(String netConfigId) {
		this.netConfigId = netConfigId;
	}
	public String getNetServerTypeId() {
		return netServerTypeId;
	}
	public void setNetServerTypeId(String netServerTypeId) {
		this.netServerTypeId = netServerTypeId;
	}
	public String getNetConfigurationInstance() {
		return netConfigurationInstance;
	}
	public void setNetConfigurationInstance(String netConfigurationInstance) {
		this.netConfigurationInstance = netConfigurationInstance;
	}
	public String getNetServerInstance() {
		return netServerInstance;
	}
	public void setNetServerInstance(String netServerInstance) {
		this.netServerInstance = netServerInstance;
	}
	public String getNetConfigMapTypeId() {
		return netConfigMapTypeId;
	}
	public void setNetConfigMapTypeId(String netConfigMapTypeId) {
		this.netConfigMapTypeId = netConfigMapTypeId;
	}
}
