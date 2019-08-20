package com.elitecore.aaa.core.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder ={})
public class StripAttributeRelation {
	
	private String attributeId;
	private String pattern = "SUFFIX";
	private String separator;
	
	public StripAttributeRelation() {
		
	}
	public  StripAttributeRelation(String attributeId,String pattern,String separator) {
		this.attributeId = attributeId;
		if(pattern!=null && pattern.length()>0)
			this.pattern = pattern;
		this.separator = separator;
	}
	

	@XmlElement(name ="attribute-id",type =String.class)
	public String getAttributeId() {
		return this.attributeId;
	}
	@XmlElement(name ="pattern",type =String.class,defaultValue ="SUFFIX")
	public String getPattern() {
		return this.pattern;
	}
	@XmlElement(name ="separator",type =String.class)
	public String getSeparator() {
		return this.separator;
	}

	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}
}
