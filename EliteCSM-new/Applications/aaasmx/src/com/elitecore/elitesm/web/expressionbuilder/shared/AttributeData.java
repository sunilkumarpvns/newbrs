package com.elitecore.elitesm.web.expressionbuilder.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AttributeData implements IsSerializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private String        name;
	private String        predefinedValues;
	private String        dataTypeId;
	private String        attributeId;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPredefinedValues() {
		return predefinedValues;
	}
	public void setPredefinedValues(String predefinedValues) {
		this.predefinedValues = predefinedValues;
	}
	public String getDataTypeId() {
		return dataTypeId;
	}
	public void setDataTypeId(String dataTypeId) {
		this.dataTypeId = dataTypeId;
	}
	public String getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}
	
	
	
	
}
