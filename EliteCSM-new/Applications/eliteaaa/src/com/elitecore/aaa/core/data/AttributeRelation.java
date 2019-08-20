package com.elitecore.aaa.core.data;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder ={})
public class AttributeRelation {
	
	public static final String ATTRIBUTE_RELATION = "attribute-relation";
	public static final String ATTRIBUTE_ID = "attribute-id";
	public static final String DEFAULT_VALUE = "defaule-value";
	public static final String USER_DICTIONARY_VALUE = "use-dictionary-value";
	
	private String attributeId;
	private String defaultValue="";
	private String useDictionaryValue;
	
	public AttributeRelation() {
		
	}

	
	public  AttributeRelation(String attributeId,String defaultValue, String useDictionaryValue) {
		this.attributeId = attributeId;
		if(defaultValue!=null)
			this.defaultValue = defaultValue;
		this.useDictionaryValue = useDictionaryValue;
	}

	@XmlElement(name="attribute-id",type=String.class)
	public String getAttributeId() {
		return this.attributeId;
	}

	@XmlElement(name="default-value",type=String.class)
	public String getDefaultVale() {
		return this.defaultValue;
	}

	@XmlElement(name="user-dictionary-value",type=String.class)
	public String getUserDictionaryValue() {
		return this.useDictionaryValue;
	}
	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setUseDictionaryValue(String useDictionaryValue) {
		this.useDictionaryValue = useDictionaryValue;
	}

	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		out.println("    	Attribute Id 	      = " + attributeId);
		out.println("    	Default Value 	 	  = " + defaultValue);
		out.println("    	Use Dictionary Value = " + useDictionaryValue);
		out.close();
		return stringBuffer.toString();
	}
}


