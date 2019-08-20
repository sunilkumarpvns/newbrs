package com.elitecore.netvertexsm.datamanager.servermgr.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

public class NetServerInstanceConfMapData implements INetServerInstanceConfMapData,Serializable {
	private long netServerId;
	private long configInstanceId;
	private INetConfigurationInstanceData  netConfigurationInstance;
	public long getConfigInstanceId() {
		return configInstanceId;
	}
	public void setConfigInstanceId(long configInstanceId) {
		this.configInstanceId = configInstanceId;
	}
	public long getNetServerId() {
		return netServerId;
	}
	public void setNetServerId(long netServerId) {
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
