package com.elitecore.aaa.diameter.conf.impl;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={})
public class AbortSessionTranslationMappingDetail{
	
	private String translationMapping;
	
	public AbortSessionTranslationMappingDetail() {
	}

	@XmlElement(name="ws-mapping-name",type=String.class)
	public String getTranslationMapping() {
		return translationMapping;
	}

	
	public void setTranslationMapping(String translationMapping) {
		this.translationMapping = translationMapping;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println(format("%-30s: %s", "ASR WS Mapping Name", 
				getTranslationMapping() != null ? getTranslationMapping() : ""));
		out.close();
		return stringBuffer.toString();
	}
	
}