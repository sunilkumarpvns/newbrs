package com.elitecore.core.commons.plugins;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class PluginInfo {
	private String pluginName;
	private String pluginClass;
	private String pluginConfClass;
	private String description;
	
	public PluginInfo() {
		
	}
	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}
	@XmlElement(name ="plugin-name",type = String.class)
	public String getPluginName() {
		return pluginName;
	}
	public void setPluginClass(String pluginClass) {
		this.pluginClass = pluginClass;
	}
	
	@XmlElement(name ="class-name",type = String.class)
	public String getPluginClass() {
		return pluginClass;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlElement(name ="description",type = String.class)
	public String getDescription() {
		return description;
	}
	public void setPluginConfClass(String pluginConfClass) {
		this.pluginConfClass = pluginConfClass;
	}
	
	@XmlElement(name ="conf-class-name",type = String.class)
	public String getPluginConfClass() {
		return pluginConfClass;
	}
	
	@Override
	public String toString(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		
		out.println("Plugin name  : " + this.pluginName );		
		out.println("Plugin class : " + this.pluginClass  );
		out.println("Conf Class   : " + this.pluginConfClass);
		out.println("Description  : " + this.description);
		
		return stringBuffer.toString();
	}
}

