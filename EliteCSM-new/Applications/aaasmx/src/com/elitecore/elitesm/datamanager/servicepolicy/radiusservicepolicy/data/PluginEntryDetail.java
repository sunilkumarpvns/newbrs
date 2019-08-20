package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name = "plugin-entry")
@XmlType(propOrder = {"pluginName", "pluginArgument"})
@ValidObject
public class PluginEntryDetail implements Differentiable, Validator{

	@NotEmpty(message="Plugin Name must be specified")
	private String pluginName;
	private String pluginArgument;

	@XmlTransient
	private PluginCallerIdentity callerId;
	
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

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		
		if( Strings.isNullOrBlank(this.pluginName) == false ){
			try{
				PluginBLManager pluginBLManager = new PluginBLManager();
				PluginInstData pluginInstData = pluginBLManager.getPluginInstDataByName(this.pluginName.trim());
			}catch(Exception e){
				RestUtitlity.setValidationMessage(context, "Invalid Plugin Name");
				isValid = false;
			}
		}
		
		return isValid;
	}
}
