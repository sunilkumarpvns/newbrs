package com.elitecore.aaa.core.conf.impl;

import javax.xml.bind.annotation.XmlElement;

import com.elitecore.commons.base.Differentiable;

import net.sf.json.JSONObject;

public class IMDGFieldAndAttribute implements Differentiable{

	private String imdgIndex;
	private String imdgFieldValue;
	private String imdgAttributeValue;
	
	@XmlElement(name = "index-value")
	public String getImdgIndex() {
		return imdgIndex;
	}
	public void setImdgIndex(String imdgIndex) {
		this.imdgIndex = imdgIndex;
	}
	
	@XmlElement(name = "field-value")
	public String getImdgFieldValue() {
		return imdgFieldValue;
	}
	public void setImdgFieldValue(String imdgFieldValue) {
		this.imdgFieldValue = imdgFieldValue;
	}
	
	@XmlElement(name = "attribute-value")
	public String getImdgAttributeValue() {
		return imdgAttributeValue;
	}
	public void setImdgAttributeValue(String imdgAttributeValue) {
		this.imdgAttributeValue = imdgAttributeValue;
	}
	
	@Override
	public JSONObject toJson() {
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Field Value", imdgFieldValue);
		jsonObject.put("Attribute Value", imdgAttributeValue);
		
		return jsonObject;
	}
}
