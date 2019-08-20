package com.elitecore.aaa.core.plugins;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.aaa.core.plugins.BaseUniversalPlugin.UniversalPluginActionConstants;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Predicate;
import com.elitecore.core.commons.config.core.annotations.Reloadable;


@XmlType(propOrder={})
@Reloadable(type = AAAUniversalPluginPolicyDetail.class)
public class AAAUniversalPluginPolicyDetail implements Differentiable{
	
	private int policyAction;
	private Boolean rejectOnCheckedItemNotFound;
	protected List<RadiusParamDetails> parameterDetailsForPlugin;
	private String action;
	private String name;
	private Boolean enabled;
	
	public AAAUniversalPluginPolicyDetail() {
		
	}
	

	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name ="action",type =String.class)
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
					   
	@XmlElement(name ="reject-on-check-item-not-found", type = Boolean.class)
	public Boolean isRejectOnCheckedItemNotFound() {
		return rejectOnCheckedItemNotFound;
	}
	
	public void setRejectOnCheckedItemNotFound(
			Boolean rejectOnCheckedItemNotFound) {
		this.rejectOnCheckedItemNotFound = rejectOnCheckedItemNotFound;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("Name", name);
		jsonObject.put("Action", UniversalPluginActionConstants.getName(Integer.parseInt(action)));
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
	
	@XmlElement(name = "enabled" , type = Boolean.class)
	public Boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
        out.println("Action: " + getAction());
        out.println("Name: " + getName());
        out.println("Reject on check item not found: " + isRejectOnCheckedItemNotFound());
        out.println("Enabled: " + isEnabled());
        out.println("Parameter: " + getParameterDetailsForPlugin());
		return stringBuffer.toString();
	}
public static final Predicate<AAAUniversalPluginPolicyDetail> POLICY_ENABLED = new Predicate<AAAUniversalPluginPolicyDetail>() {

	@Override
	public boolean apply(AAAUniversalPluginPolicyDetail policyDetail) {
		return policyDetail.isEnabled();
	}
};
}
