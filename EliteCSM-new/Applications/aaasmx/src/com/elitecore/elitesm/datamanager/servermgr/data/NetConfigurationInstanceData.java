
package com.elitecore.elitesm.datamanager.servermgr.data;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

/**
 * @author dhavalraval
 */
public class NetConfigurationInstanceData extends BaseData implements INetConfigurationInstanceData {
    
    private String configInstanceId;
    private String configId;
    private INetConfigurationData netConfiguration;
    
    
    public NetConfigurationInstanceData() {
        super();
    }
    
    public NetConfigurationInstanceData(String configInstanceId) {
        this(configInstanceId,null,null);
    }
    
    public NetConfigurationInstanceData(String configInstanceId,String configId,INetConfigurationData netConfiguration) {
        this.configInstanceId = configInstanceId;
        this.configId = configId;
        this.netConfiguration = netConfiguration;
    }
    
    public String getConfigId( ) {
        return configId;
    }
    
    public void setConfigId( String configId ) {
        this.configId = configId;
    }
    
    public String getConfigInstanceId( ) {
        return configInstanceId;
    }
    
    public void setConfigInstanceId( String configInstanceId ) {
        this.configInstanceId = configInstanceId;
    }
    
    public INetConfigurationData getNetConfiguration( ) {
        return netConfiguration;
    }
    
    public void setNetConfiguration( INetConfigurationData netConfiguration ) {
        this.netConfiguration = netConfiguration;
    }
    
    @Override
    public String toString() {
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        writer.println();
        writer.println("------------NetConfigurationInstanceData-----------------");
        writer.println("configInstanceId=" + configInstanceId);                                     
        writer.println("configId=" + configId);    
        writer.println("----------------------------------------------------");
        writer.close();
        return out.toString();
    }

	public int compareTo(INetConfigurationInstanceData netConfigurationInstanceData) {
		if(netConfigurationInstanceData.getNetConfiguration()!=null & this.getNetConfiguration()==null){
			if(netConfiguration.getSerialNo()>this.getNetConfiguration().getSerialNo()){
				return 1;
			}else if(netConfiguration.getSerialNo()<this.getNetConfiguration().getSerialNo()){
				return -1;
			}
		}
		return 0;
	}
    
}
