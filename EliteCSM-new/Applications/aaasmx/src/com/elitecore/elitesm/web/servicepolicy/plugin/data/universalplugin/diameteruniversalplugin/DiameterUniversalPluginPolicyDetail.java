package com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.diameteruniversalplugin;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.NotEmpty;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.diameterapi.plugins.universal.UniversalDiameterPlugin.UniversalDiameterActionConstant;

public class DiameterUniversalPluginPolicyDetail extends UniversalPluginPolicyDetail<DiameterParamDetail> implements Differentiable {

	private int policyAction;
	protected List<DiameterParamDetail> parameterDetailsForPlugin;

	public DiameterUniversalPluginPolicyDetail() {
		
	}
	
	@XmlElementWrapper(name ="parameter-list")
	@XmlElement(name ="parameter", type = DiameterParamDetail.class)
	@NotEmpty(message = "Atleast one mapping is required")
	@Valid
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
		jsonObject.put("Enabled", getEnabled());
		
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
		
		for (DiameterParamDetail data : parameterDetailsForPlugin) {
			out.println(data);
		}
		
		out.close();
		return stringBuffer.toString();
	}
}

