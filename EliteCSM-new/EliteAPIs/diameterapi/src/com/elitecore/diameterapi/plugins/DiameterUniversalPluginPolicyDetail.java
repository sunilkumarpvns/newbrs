package com.elitecore.diameterapi.plugins;

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

import com.elitecore.commons.base.Differentiable;
import com.elitecore.diameterapi.plugins.universal.UniversalDiameterPlugin.UniversalDiameterActionConstant;

@XmlType(propOrder ={})
public class DiameterUniversalPluginPolicyDetail implements Differentiable {

	private int policyAction;
	private List<DiameterParamDetail> parameterDetailsForPlugin;
	private String action;
	private String name;
	private Boolean enabled;

	public DiameterUniversalPluginPolicyDetail() {
		
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
	
	@XmlElement(name = "enabled" , type = Boolean.class)
	public Boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@XmlElementWrapper(name ="parameter-list")
	@XmlElement(name ="parameter")
	public List<DiameterParamDetail> getParameterDetailsForPlugin() {
		return parameterDetailsForPlugin;
	}	
	public void setParameterDetailsForPlugin(List<DiameterParamDetail> parameterDetailsForPlugin){
		this.parameterDetailsForPlugin = parameterDetailsForPlugin;
	}
	
	@XmlTransient
	public int getPolicyAction(){
		return policyAction;
	}
	public void setPolicyAction(int policyAction){
		this.policyAction = policyAction;
	}
	
	@Override
	public JSONObject toJson() {
		
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("Name", getName());
		jsonObject.put("Action", UniversalDiameterActionConstant.getName(Integer.parseInt(getAction())));
		jsonObject.put("Enabled", isEnabled());
		
		if( parameterDetailsForPlugin != null ){
			JSONArray array = new JSONArray();
			for (DiameterParamDetail paramDetail : parameterDetailsForPlugin) {
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
		 out.println(" 		Policy Name = "+name);
	     out.println(" 		Action = "+action);
	     out.println("Policy Parameters:");
		for (DiameterParamDetail data : parameterDetailsForPlugin) {
			out.println(data);
		}
		
		out.close();
		return stringBuffer.toString();
	}
}

