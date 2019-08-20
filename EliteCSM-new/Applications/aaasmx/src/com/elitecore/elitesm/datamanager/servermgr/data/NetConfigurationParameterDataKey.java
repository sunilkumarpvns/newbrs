/**
 * 
 */
package com.elitecore.elitesm.datamanager.servermgr.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

/**
 * @author rakeshkachhadiya
 *
 */
public class NetConfigurationParameterDataKey implements INetConfigurationParameterDataKey,Serializable{
	
//	private String id;
	private String configId;
	private String parameterId;

	public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}

	public String getParameterId() {
		return parameterId;
	}

	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}
	
	@Override
        public String toString() {
            StringWriter out = new StringWriter();
            PrintWriter writer = new PrintWriter(out);
            writer.println();
            writer.println("------------NetConfigurationParameterDataKey-----------------");
            writer.println("configId=" +configId);
            writer.println("parameterId=" +parameterId);
            writer.println("----------------------------------------------------");
            writer.close();
            return out.toString();
        }



}
