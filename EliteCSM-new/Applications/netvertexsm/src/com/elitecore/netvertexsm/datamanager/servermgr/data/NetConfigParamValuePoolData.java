
package com.elitecore.netvertexsm.datamanager.servermgr.data;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author dhavalraval
 */
public class NetConfigParamValuePoolData implements INetConfigParamValuePoolData, Comparable<NetConfigParamValuePoolData> {
    
    private String paramPoolId;
    private String parameterId;
    private String name;
    private String value;
    private String configId;
    private static final String NONE_VALUE = "none";
    public String getParamPoolId( ) {
        return paramPoolId;
    }
    
    public void setParamPoolId( String paramPoolId ) {
        this.paramPoolId = paramPoolId;
    }
    
    public String getParameterId( ) {
        return parameterId;
    }
    
    public void setParameterId( String parameterId ) {
        this.parameterId = parameterId;
    }
    
    public String getName( ) {
        return name;
    }
    
    public void setName( String name ) {
        this.name = name;
    }
    
    public String getValue( ) {
        return value;
    }
    
    public void setValue( String value ) {
        this.value = value;
    }
    
    public String getConfigId( ) {
        return configId;
    }
    
    public void setConfigId( String configId ) {
        this.configId = configId;
    }
    
    
    @Override
    public String toString() {
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        writer.println();
        writer.println("------------NetConfigParamValuePoolData-----------------");
        writer.println("paramPoolId=" +paramPoolId);                                     
        writer.println("parameterId=" +parameterId);                                     
        writer.println("name=" +name);                                     
        writer.println("value=" +value);                                     
        writer.println("configId=" +configId);
        writer.println("----------------------------------------------------");
        writer.close();
        return out.toString();
    }

	public int compareTo(NetConfigParamValuePoolData configParamValuePoolData) {
		// TODO Auto-generated method stub
		if(configParamValuePoolData!=null){
			if(configParamValuePoolData.getName().equalsIgnoreCase(NONE_VALUE)){
				return 1; 
			}else{
				return this.getName().compareTo(configParamValuePoolData.getName());
			}
		}
		return 0;
	}
    
}
