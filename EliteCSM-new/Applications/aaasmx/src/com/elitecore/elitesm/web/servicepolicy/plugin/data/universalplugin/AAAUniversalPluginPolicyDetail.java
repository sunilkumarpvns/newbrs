package com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.util.constants.RestValidationMessages;

@XmlType(propOrder={"name","action","enabled","rejectOnCheckedItemNotFound","parameterDetailsForPlugin"})
public class AAAUniversalPluginPolicyDetail implements Differentiable{
	
	private int policyAction;
	@NotEmpty(message = "Reject On Check Item Not Found parameter must be specified")
	@Pattern(regexp = "^$|true|false", message = "Invalid value of Reject On Check Item Not Found field. Value could be 'true' or 'false'.")
	private String rejectOnCheckedItemNotFound;
	
	@Valid
	protected List<RadiusParamDetails> parameterDetailsForPlugin;
	
	@NotEmpty(message = "Plugin Action must be specified")
	@Pattern(regexp = "^$|Accept|Reject|Drop|none|Stop", message = "Invalid action parameter. It can be one of this Accept,Reject,Drop,none,Stop.")
	private String action;
	
	@NotEmpty(message = "Universal Plugin Policy name must be specified")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	private String name;
	
	@NotEmpty(message = "Enabled Field must be specified")
	@Pattern(regexp = "^$|true|false", message = "Invalid value of enabled field. Value could be 'true' or 'false'.")
	private String enabled;
	
	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name ="action")
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@XmlElementWrapper(name ="parameter-list")
	@XmlElement(name ="parameter")
	public List<RadiusParamDetails> getParameterDetailsForPlugin() {
		return parameterDetailsForPlugin;
	}	
	public void setParameterDetailsForPlugin(List<RadiusParamDetails> parameterDetailsForPlugin){
		this.parameterDetailsForPlugin = parameterDetailsForPlugin;
	}
	
	@XmlTransient
	public int getPolicyAction(){
		return policyAction;
	}
	public void setPolicyAction(int policyAction){
		this.policyAction = policyAction;
	}
					   
	@XmlElement(name ="reject-on-check-item-not-found")
	public String getRejectOnCheckedItemNotFound() {
		return rejectOnCheckedItemNotFound;
	}

	public void setRejectOnCheckedItemNotFound(String rejectOnCheckedItemNotFound) {
		this.rejectOnCheckedItemNotFound = rejectOnCheckedItemNotFound;
	}
	
	@XmlElement(name = "enabled")
	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("Name", name);
		jsonObject.put("Action", action);
		jsonObject.put("Reject On Check Item Not Found", rejectOnCheckedItemNotFound);
		jsonObject.put("Enabled", enabled);

		if( parameterDetailsForPlugin != null ){
			JSONArray array = new JSONArray();
			for (RadiusParamDetails paramDetail : parameterDetailsForPlugin) {
				array.add(paramDetail.toJson());
			}
			jsonObject.put("Parameter Lists ", array);
		}
		
		return jsonObject;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
        out.println("Action: " + getAction());
        out.println("Name: " + getName());
        out.println("Reject on check item not found: " + getRejectOnCheckedItemNotFound());
        out.println("Enabled: " + getEnabled());
        out.println("Parameter: " + getParameterDetailsForPlugin());
		return stringBuffer.toString();
	}
}
