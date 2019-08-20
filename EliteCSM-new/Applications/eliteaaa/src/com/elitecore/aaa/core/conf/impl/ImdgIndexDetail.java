package com.elitecore.aaa.core.conf.impl;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.commons.base.Differentiable;

import net.sf.json.JSONObject;

public class ImdgIndexDetail implements Differentiable{

	private String imdgIndex;
	private String imdgFieldValue;
	private String imdgAttributeValue;
	private List<String> attributeList;
	
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
	
	@XmlTransient
	public List<String> getAttributeList() {
		return attributeList;
	}
	
	public void setAttributeList(List<String> attributeList) {
		this.attributeList = attributeList;
	}
	
	@Override
	public JSONObject toJson() {
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Field Value", imdgFieldValue);
		jsonObject.put("Attribute Value", imdgAttributeValue);
		
		return jsonObject;
	}
}
