package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;

@XmlRootElement(name = "plugin-entry")
@XmlType(propOrder = {"ruleset", "pluginName", "pluginArgument", "onResponse"})
public class PluginEntryData implements Differentiable {
	private String ruleset;
	private String pluginName;
	private String pluginArgument;
	private boolean onResponse;
	
	@XmlElement(name = "ruleset")
	public String getRuleset() {
		return ruleset;
	}
	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}
	
	@XmlElement(name = "plugin-name")
	public String getPluginName() {
		return pluginName;
	}
	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}
	
	@XmlAttribute(name = "onResponse")
	public boolean isOnResponse() {
		return onResponse;
	}
	public void setOnResponse(boolean onResponse) {
		this.onResponse = onResponse;
	}
	
	@XmlElement(name = "plugin-argument")
	public String getPluginArgument() {
		return pluginArgument;
	}
	public void setPluginArgument(String pluginArgument) {
		this.pluginArgument = pluginArgument;
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(format("%-30s: %s", "Ruleset", getRuleset()));
		out.println(format("%-30s: %s", "Plugin Name", getPluginName()));
		out.println(format("%-30s: %s", "Plugin Argument", getPluginArgument()));
		out.println(format("%-30s: %s", "Request/Response",
				isOnResponse() ? "Response" : "Request"));
		out.close();
		return writer.toString();
	}
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Ruleset", ruleset);
		object.put("Plugin", pluginName);
		object.put("Argument", pluginArgument);
		
		if(onResponse){
			object.put("Request/Response", "Response");
		} else {
			object.put("Request/Response", "Request");
		}
		return object;
	}
}