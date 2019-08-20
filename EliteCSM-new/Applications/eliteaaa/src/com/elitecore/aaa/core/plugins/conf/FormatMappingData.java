package com.elitecore.aaa.core.plugins.conf;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "format-mapping")
public class FormatMappingData {
	
	private String key = null;
	private String format = null;
	
	public FormatMappingData() {
		// for jaxb
	}
	
	public FormatMappingData(String key, String format) {
		this.key = key;
		this.format = format;
	}
	
	@XmlElement(name = "key")
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	@XmlElement(name = "format")
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
}
