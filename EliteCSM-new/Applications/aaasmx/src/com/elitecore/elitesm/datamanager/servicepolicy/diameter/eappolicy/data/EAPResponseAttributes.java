package com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

@XmlRootElement(name = "command-code-wise-response-attribute")
public class EAPResponseAttributes extends BaseData implements Differentiable{

	private String  respAttrId;
	private String commandCodes;
	private String responseAttributes;
	private String eapPolicyId;
	private Integer orderNumber;
	
	@XmlTransient
	public String getRespAttrId() {
		return respAttrId;
	}

	public void setRespAttrId(String respAttrId) {
		this.respAttrId = respAttrId;
	}
	
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
	public String getEapPolicyId() {
		return eapPolicyId;
	}

	public void setEapPolicyId(String eapPolicyId) {
		this.eapPolicyId = eapPolicyId;
	}

	@Override
	public JSONObject toJson() {
		JSONObject object=new  JSONObject();
		object.put("Command Code", commandCodes);
		object.put("Response Attribute",responseAttributes);
		return object;
	}

	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
}
