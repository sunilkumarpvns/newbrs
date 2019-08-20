package com.elitecore.elitesm.datamanager.servermgr.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class NetServerInstanceConfMapData extends BaseData implements INetServerInstanceConfMapData,Serializable {
	private String netServerId;
	private String configInstanceId;
	private INetConfigurationInstanceData  netConfigurationInstance;
	public String getConfigInstanceId() {
		return configInstanceId;
	}
	public void setConfigInstanceId(String configInstanceId) {
		this.configInstanceId = configInstanceId;
	}
	public String getNetServerId() {
		return netServerId;
	}
	public void setNetServerId(String netServerId) {
		this.netServerId = netServerId;
	}
	public INetConfigurationInstanceData getNetConfigurationInstance() {
		return netConfigurationInstance;
	}
	public void setNetConfigurationInstance(
			INetConfigurationInstanceData netConfigurationInstance) {
		this.netConfigurationInstance = netConfigurationInstance;
	}
	
	@Override
        public String toString() {
            StringWriter out = new StringWriter();
            PrintWriter writer = new PrintWriter(out);
            writer.println();
            writer.println("------------NetServerInstanceConfMapData-----------------");
            writer.println("netServerId=" +netServerId);                                     
            writer.println("configInstanceId=" +configInstanceId);   
            writer.println("----------------------------------------------------");
            writer.close();
            return out.toString();
        }

}
