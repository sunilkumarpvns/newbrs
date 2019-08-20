package com.elitecore.elitesm.datamanager.servermgr.script.data;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

/**
 * @author tejas.shah
 *
 */

public class ScriptTypeData  extends BaseData implements  Serializable{
	
	private static final long serialVersionUID = 1L;
	private String scriptTypeId;
	private String pluginServiceTypeId;
	private String name;
	private String displayName;
	private String alias;
	private long serialNo;
	private String description;
	private String status;
	
	public String getScriptTypeId() {
		return scriptTypeId;
	}
	public void setScriptTypeId(String scriptTypeId) {
		this.scriptTypeId = scriptTypeId;
	}
	public String getPluginServiceTypeId() {
		return pluginServiceTypeId;
	}
	public void setPluginServiceTypeId(String pluginServiceTypeId) {
		this.pluginServiceTypeId = pluginServiceTypeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public long getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(long serialNo) {
		this.serialNo = serialNo;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(padStart(" PluginTypesData : " + getName() , 10, ' '));
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Name", getName()));
		out.println(format("%-30s: %s", "Description", getDescription()));
		out.println(format("%-30s: %s", "Display Name", getDisplayName()));
		out.println(format("%-30s: %s", "Alias", getAlias()));
		out.println(format("%-30s: %s", "Serial Number", getSerialNo()));
		out.println(format("%-30s: %s", "Status", getStatus()));
		out.close();
		return writer.toString();
	}
}
