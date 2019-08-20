package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.systemmapping.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

@XmlType(propOrder = {"pluginName","pluginClass","pluginConfClass","description"})
public class PluginInfoData {
	
	public PluginInfoData() {
		description = RestUtitlity.getDefaultDescription();
	}
	
	private String pluginName;
	private String pluginClass;
	private String pluginConfClass;
	private String description;
	
	@XmlElement(name ="plugin-name")
	public String getPluginName() {
		return pluginName;
	}
	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}
	
	@XmlElement(name ="class-name")
	public String getPluginClass() {
		return pluginClass;
	}
	public void setPluginClass(String pluginClass) {
		this.pluginClass = pluginClass;
	}
	
	@XmlElement(name ="description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlElement(name ="conf-class-name")
	public String getPluginConfClass() {
		return pluginConfClass;
	}
	public void setPluginConfClass(String pluginConfClass) {
		this.pluginConfClass = pluginConfClass;
	}
	
}



