package com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

@XmlType(propOrder = {"pluginName", "pluginArgument"})
public class NASPolicyAuthPluginConfig extends BaseData implements  Serializable,Differentiable{

	private static final long serialVersionUID = 1L;
	private String policyConfId;
	private String nasPolicyId;
	
	@NotEmpty(message = "Plugin name must be specified in authentication")
	private String pluginName;
	private String pluginArgument;
	private String pluginType;
	private Integer orderNumber;
	
	@XmlTransient
	public String getPolicyConfId() {
		return policyConfId;
	}

	public void setPolicyConfId(String policyConfId) {
		this.policyConfId = policyConfId;
	}

	@XmlTransient
	public String getNasPolicyId() {
		return nasPolicyId;
	}

	public void setNasPolicyId(String nasPolicyId) {
		this.nasPolicyId = nasPolicyId;
	}

	@XmlElement(name = "plugin-name")
	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	@XmlElement(name = "plugin-argument")
	public String getPluginArgument() {
		return pluginArgument;
	}

	public void setPluginArgument(String pluginArgument) {
		this.pluginArgument = pluginArgument;
	}

	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------ NAS Policy Plugin Config --------------");
		writer.println("Nas Policy Id :"+nasPolicyId);
		writer.println("Plugin Name :"+pluginName);           
		writer.println("Plugin Argument :"+pluginArgument);
		writer.println("----------------------------------------------");
		writer.println();
		writer.close();
		return out.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Plugin Name", pluginName);
		object.put("Plugin Argument", pluginArgument);
		
		return object;
	}

	@XmlTransient
	public String getPluginType() {
		return pluginType;
	}

	public void setPluginType(String pluginType) {
		this.pluginType = pluginType;
	}

	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
}