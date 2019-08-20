
package com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

import net.sf.json.JSONObject;

@XmlRootElement(name = "command-code-wise-response-attribute")
public class CCResponseAttributes extends BaseData implements Differentiable{

	private String  respAttrId;
	private String commandCodes;
	private String responseAttributes;
	private String policyId;
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
	public String getPolicyId() {
		return policyId;
	}
	public void setPolicyId(String policyId) {
		this.policyId = policyId;
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