package com.elitecore.elitesm.ws.rest.serverconfig.server.services.data;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;

@XmlRootElement(name = "plugin-entry")
@XmlType(propOrder = {"pluginName", "pluginArgument"})
public class PluginEntryDetail implements Differentiable{
	
	private String pluginName;
	
	private String pluginArgument;

	@XmlTransient
	private PluginCallerIdentity callerId;
	
	public PluginEntryDetail() {
		pluginName = new String();
		pluginArgument = new String();
	}
	
	@XmlElement(name = "plugin-name")
	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	@XmlElement(name = "argument")
	public String getPluginArgument() {
		return pluginArgument;
	}

	public void setPluginArgument(String pluginArgument) {
		this.pluginArgument = pluginArgument;
	}

	@XmlTransient
	public PluginCallerIdentity getCallerId() {
		return callerId;
	}
	
	public void setCallerId(PluginCallerIdentity callerId) {
		this.callerId = callerId;
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(format("%-30s: %s", "Plugin Name", getPluginName()));
		out.println(format("%-30s: %s", "Plugin Arguments", getPluginArgument()));
		out.close();
		return writer.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Plug-in", pluginName);
		object.put("Argument", pluginArgument);
		return object;
	}
}
