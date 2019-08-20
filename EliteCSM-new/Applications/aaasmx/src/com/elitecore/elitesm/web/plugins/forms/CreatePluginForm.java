package com.elitecore.elitesm.web.plugins.forms;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.plugins.data.IPluginServiceTypeData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginTypesData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

/**
 * @author nayana.rathod
 *
 */
public class CreatePluginForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private List<IPluginServiceTypeData> pluginServiceList;
	private String selectedPlugin;
	private long pluginTypeId;
	private String action;
	private String status;
	private boolean radiusTransationLoggerEnabled;
	private boolean diameterTransactionLoggerEnabled;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List getPluginServiceList() {
		return pluginServiceList;
	}
	public void setPluginServiceList(List pluginServiceList) {
		this.pluginServiceList = pluginServiceList;
	}
	public String getSelectedPlugin() {
		return selectedPlugin;
	}
	public void setSelectedPlugin(String selectedPlugin) {
		this.selectedPlugin = selectedPlugin;
	}
	public long getPluginTypeId() {
		return pluginTypeId;
	}
	public void setPluginTypeId(long pluginTypeId) {
		this.pluginTypeId = pluginTypeId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
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
		out.println(padStart(" Plugin : " + getName() , 10, ' '));
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Name", getName()));
		out.println(format("%-30s: %s", "Description", getDescription()));
		out.println(format("%-30s: %s", "Status", getStatus()));
		out.println(format("%-30s: %s", "Selected Plugin", getSelectedPlugin()));
		out.println(format("%-30s: %s", "Plugin Type Id", getPluginTypeId()));
		out.println(format("%-30s: %s", "Action", getAction()));
		
		out.close();
		return writer.toString();
	}
	public boolean isRadiusTransationLoggerEnabled() {
		return radiusTransationLoggerEnabled;
	}
	public void setRadiusTransationLoggerEnabled(
			boolean radiusTransationLoggerEnabled) {
		this.radiusTransationLoggerEnabled = radiusTransationLoggerEnabled;
	}
	public boolean isDiameterTransactionLoggerEnabled() {
		return diameterTransactionLoggerEnabled;
	}
	public void setDiameterTransactionLoggerEnabled(
			boolean diameterTransactionLoggerEnabled) {
		this.diameterTransactionLoggerEnabled = diameterTransactionLoggerEnabled;
	}
	
}
