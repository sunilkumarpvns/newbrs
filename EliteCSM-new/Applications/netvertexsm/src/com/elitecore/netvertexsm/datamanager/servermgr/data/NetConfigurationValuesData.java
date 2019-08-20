package com.elitecore.netvertexsm.datamanager.servermgr.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import com.elitecore.netvertexsm.util.logger.Logger;

/**
 * 
 * @author dhavalraval
 *
 */
public class NetConfigurationValuesData implements INetConfigurationValuesData{
    private long parameterValueId;
    private String parameterId;
    private long  configInstanceId;
    private String instanceId;
    private String value;
    private String configId;
    
    @Deprecated
    private NetConfigurationParameterData netConfigurationParameterData;
    
    public long getParameterValueId() {
        return parameterValueId;
    }
    public void setParameterValueId(long parameterValueId) {
        this.parameterValueId = parameterValueId;
    }
    public String getParameterId() {
        return parameterId;
    }
    public void setParameterId(String parameterId) {
        this.parameterId = parameterId;
    }
    public long getConfigInstanceId() {
        return configInstanceId;
    }
    public void setConfigInstanceId(long configInstanceId) {
        this.configInstanceId = configInstanceId;
    }
    public String getInstanceId() {
        return instanceId;
    }
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    
    public int compareTo(Object arg0) {
        int retValue=0;
        NetConfigurationValuesData netConfArg = (NetConfigurationValuesData )arg0;
        StringTokenizer stThis = new StringTokenizer(instanceId,".");
        StringTokenizer stNetConfArg = new StringTokenizer(netConfArg.instanceId,".");
        int loop = stThis.countTokens()<stNetConfArg.countTokens() ? stThis.countTokens():stNetConfArg.countTokens();
        try {
            while(loop-- >0){
                int thisToken= Integer.parseInt(stThis.nextToken());
                int iNetConfArg = Integer.parseInt(stNetConfArg.nextToken());
                if(thisToken < iNetConfArg){
                    return retValue-1;
                }
                if(thisToken > iNetConfArg){
                    return retValue+1;
                }
                if(thisToken == iNetConfArg){
                    continue;
                }
            }	
        }catch(Exception e) {
            Logger.logTrace("NetConfigurationValuesData",e);
        }
        return stThis.countTokens() == stNetConfArg.countTokens() ?0 : stThis.countTokens() < stNetConfArg.countTokens()? -1 : 1;
        
    }
    
    @Override
    public INetConfigurationValuesData clone() {
        INetConfigurationValuesData tempConfigurationValuesData = new NetConfigurationValuesData();
        tempConfigurationValuesData.setConfigId(this.configId);
        tempConfigurationValuesData.setConfigInstanceId(this.configInstanceId);
        tempConfigurationValuesData.setParameterId(this.parameterId);
        tempConfigurationValuesData.setValue(this.value);
        tempConfigurationValuesData.setInstanceId(this.instanceId);
        return tempConfigurationValuesData;
    }
    
    public String getConfigId( ) {
        return configId;
    }
    
    public void setConfigId( String configId ) {
        this.configId = configId;
    }
    
    
    
    
    @Deprecated
    public NetConfigurationParameterData getNetConfigurationParameterData( ) {
        return netConfigurationParameterData;
    }
    
    @Deprecated
    public void setNetConfigurationParameterData( NetConfigurationParameterData netConfigurationParameterData ) {
        this.netConfigurationParameterData = netConfigurationParameterData;
    }
    
    @Override
    public String toString() {
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        writer.println();
        writer.println("------------NetConfigurationValuesData-----------------");
        writer.println("parameterValueId=" +parameterValueId);
        writer.println("parameterId=" +parameterId);
        writer.println("configInstanceId=" +configInstanceId);
        writer.println("instanceId=" +instanceId);
        writer.println("value=" +value);
        writer.println("configId=" +configId);
        writer.println("----------------------------------------------------");
        writer.close();
        return out.toString();
    }
    
    
}

