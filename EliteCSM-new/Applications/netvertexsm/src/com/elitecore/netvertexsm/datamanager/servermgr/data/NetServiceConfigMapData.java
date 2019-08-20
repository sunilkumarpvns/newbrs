package com.elitecore.netvertexsm.datamanager.servermgr.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

public class NetServiceConfigMapData implements INetServiceConfigMapData,Serializable{
	private String netServiceTypeId;
	private String netConfigId;
	private String netServiceInstance;
	private String netConfigurationInstance;
	private String netConfigMapTypeId;
	public String getNetConfigId() {
		return netConfigId;
	}
	public void setNetConfigId(String netConfigId) {
		this.netConfigId = netConfigId;
	}
	public String getNetServiceTypeId() {
		return netServiceTypeId;
	}
	public void setNetServiceTypeId(String netServiceTypeId) {
		this.netServiceTypeId = netServiceTypeId;
	}
	public String getNetConfigurationInstance() {
		return netConfigurationInstance;
	}
	public void setNetConfigurationInstance(String netConfigurationInstance) {
		this.netConfigurationInstance = netConfigurationInstance;
	}
	public String getNetServiceInstance() {
		return netServiceInstance;
	}
	public void setNetServiceInstance(String netServiceInstance) {
		this.netServiceInstance = netServiceInstance;
	}
	public String getNetConfigMapTypeId() {
		return netConfigMapTypeId;
	}
	public void setNetConfigMapTypeId(String netConfigMapTypeId) {
		this.netConfigMapTypeId = netConfigMapTypeId;
	}
	
	
	@Override
        public String toString() {
            StringWriter out = new StringWriter();
            PrintWriter writer = new PrintWriter(out);
            writer.println();
            writer.println("------------NetServiceConfigMapData-----------------");
            writer.println("netServiceTypeId=" +netServiceTypeId); 
            writer.println("netConfigId=" +netConfigId); 
            writer.println("netConfigMapTypeId=" +netConfigMapTypeId);
            writer.println("----------------------------------------------------");
            writer.close();
            return out.toString();
        }
	
	
	
}
