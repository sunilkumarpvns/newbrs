package com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

@XmlRootElement(name = "command-code-wise-response-attribute")
public class NASResponseAttributes extends BaseData implements  Serializable,Differentiable{
	private static final long serialVersionUID = 1L;
	
	private String  respAttrId;
	private String commandCodes;
	private String responseAttributes;
	private String nasPolicyId;
	private Integer orderNumber;
	
	@XmlElement(name = "command-code")
	public String getCommandCodes() {
		return commandCodes;
	}
	
	public void setCommandCodes(String commandCodes) {
		this.commandCodes = commandCodes;
	}
	
	@XmlElement(name = "response-attribute")
	public String getResponseAttributes() {
		return responseAttributes;
	}
	public void setResponseAttributes(String responseAttributes) {
		this.responseAttributes = responseAttributes;
	}
	
	@XmlTransient
	public String getRespAttrId() {
		return respAttrId;
	}
	
	public void setRespAttrId(String respAttrId) {
		this.respAttrId = respAttrId;
	}
	
	@XmlTransient
	public String getNasPolicyId() {
		return nasPolicyId;
	}
	
	public void setNasPolicyId(String nasPolicyId) {
		this.nasPolicyId = nasPolicyId;
	}
	
	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object=new  JSONObject();
		object.put("Command Code", commandCodes);
		object.put("Response Attribute",responseAttributes);
		return object;
	}
}