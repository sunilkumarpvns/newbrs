package com.elitecore.aaa.core.data;

import java.util.ArrayList;
import java.util.List;

public class AttributeRelationBuilder {
	private String attributeId;
	private String defaultValue = "";
	private boolean bUseDictionaryValue;
	private String header;
	private List<String> attributeList = new ArrayList<String>();

	public static AttributeRelationBuilder attributeRelation(String attributeId) {
		return new AttributeRelationBuilder(attributeId);
	}

	public AttributeRelationBuilder(String attributeId) {
		this.attributeId = attributeId;
	}
	
	public String getAttributeId() {
		return attributeId;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public boolean isbUseDictionaryValue() {
		return bUseDictionaryValue;
	}

	public String getHeader() {
		return header;
	}

	public List<String> getAttributeList() {
		return attributeList;
	}

	public AttributeRelationBuilder defaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	public AttributeRelationBuilder useDictionaryValue() {
		this.bUseDictionaryValue = true;
		return this;
	}

	public AttributeRelationBuilder header(String header) {
		this.header = header;
		return this;
	}
}