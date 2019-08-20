package com.elitecore.netvertexsm.datamanager.servermgr.data;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 
 * @author dhavalraval
 * NetServerTypeData persistent class generated to map with NetServiceType.hbm.xml file
 * 
 */
public class NetServerTypeData implements INetServerTypeData{
	private String netServerTypeId;
	private String name;
	private String alias;
	private String description;
	private String systemGenerated;
	private String version;
	private String startupScriptName;
	
	public String getStartupScriptName() {
		return startupScriptName;
	}
	public void setStartupScriptName(String startupScriptName) {
		this.startupScriptName = startupScriptName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
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
	public String getSystemGenerated() {
		return systemGenerated;
	}
	public void setSystemGenerated(String systemGenerated) {
		this.systemGenerated = systemGenerated;
	}
	
	@Override
        public String toString() {
            StringWriter out = new StringWriter();
            PrintWriter writer = new PrintWriter(out);
            writer.println();
            writer.println("------------NetServerTypeData-----------------");
            writer.println("name=" +name);
            writer.println("alias=" +alias);
            writer.println("description=" +description);
            writer.println("systemGenerated=" +systemGenerated);
            writer.println("startupScriptName="+startupScriptName);
            writer.println("version=" +version);
            writer.println("----------------------------------------------------");
            writer.close();                                              
            return out.toString();
        }
	
}
