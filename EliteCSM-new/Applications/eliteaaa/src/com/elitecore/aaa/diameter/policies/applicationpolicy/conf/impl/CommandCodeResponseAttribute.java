package com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl;

import javax.xml.bind.annotation.XmlElement;

import net.sf.json.JSONObject;

import com.elitecore.commons.base.Differentiable;

public class CommandCodeResponseAttribute implements Differentiable {

	private String responseAttributes;
	private String commandCodes;
	
	@XmlElement(name = "response-attributes", type = String.class)
	public String getResponseAttributes() {
		return responseAttributes;
	}
	
	public void setResponseAttributes(String responseAttributes) {
		this.responseAttributes = responseAttributes;
	}
	
	@XmlElement(name = "command-codes", type = String.class)
	public String getCommandCodes() {
		return commandCodes;
	}
	
	public void setCommandCodes(String commandCodes) {
		this.commandCodes = commandCodes;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object=new  JSONObject();
		object.put("Command Code", commandCodes);
		object.put("Response Attribute", responseAttributes);
		return object;
	}
}
