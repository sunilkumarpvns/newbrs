package com.elitecore.aaa.core.conf.impl;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.commons.base.Differentiable;

import net.sf.json.JSONObject;

public class RadiusSessionFieldMapping implements Differentiable{

	private String fieldName;
	private String attributes;
	private List<String> attributeList;
	
	@XmlElement(name = "field-name")
	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	@XmlElement(name = "attributes")
	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	@XmlTransient
	public List<String> getAttributeList() {
		return this.attributeList;
	}
	
	public void setAttributeList(List<String> attributeList) {
		this.attributeList = attributeList;
	}
	
	@Override
	public JSONObject toJson() {
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Field Name", fieldName);
		jsonObject.put("Attribute Value", attributes);
		
		return jsonObject;
	}
}
