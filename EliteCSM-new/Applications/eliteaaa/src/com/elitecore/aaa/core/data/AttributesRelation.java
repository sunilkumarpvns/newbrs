package com.elitecore.aaa.core.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
@XmlType(propOrder ={})
public class AttributesRelation {
	
	
	private String attributeId;
	private String defaultValue="";
	private boolean bUseDictionaryValue;
	private String header;
	private List<String> attributeList = new ArrayList<String>();

	public AttributesRelation() {
	
	}
	public  AttributesRelation(String attributeId,String defaultValue,boolean bUseDictionaryValue,String header,List<String> attributeList) {
		this.attributeId =attributeId;
		if(defaultValue!=null)
			this.defaultValue = defaultValue;
		this.bUseDictionaryValue = bUseDictionaryValue;
		this.header = header;
		if(attributeList!=null)
			this.attributeList = attributeList;
	}
	
	
	
	@XmlElement(name ="attribute-id",type =String.class)
	public String getAttributeId() {
		return this.attributeId;
	}
	
	@XmlElement(name ="default-value",type =String.class,defaultValue ="")
	public String getDefaultValue() {
		return this.defaultValue;
	}
	@XmlElement(name ="use-dictionary-value",type =boolean.class)
	public boolean getUserDictionaryValue() {
		return this.bUseDictionaryValue;
	}
	@XmlElement(name ="header",type =String.class)
	public String getHeader() {
		return this.header;
	}
	@XmlTransient
	public List<String> getAttributeList() {
		return attributeList;
	}
	
	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public void setUserDictionaryValue(boolean bUseDictionaryValue) {
		this.bUseDictionaryValue = bUseDictionaryValue;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public void setAttributeList(List<String> attributeList) {
		this.attributeList = attributeList;
	}


	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		out.println("    	Attribute Id 	      = " + attributeId);
		out.println("    	Default Value 	 	  = " + defaultValue);
		out.println("    	Use Dictionary Value  = " + bUseDictionaryValue);
		out.println("    	Header	 	          = " + header);
		out.close();
		return stringBuffer.toString();
	}

}
