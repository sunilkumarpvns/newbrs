package com.elitecore.test.diameter.jaxb;

import javax.xml.bind.annotation.XmlElement;


public class AttributeMappingMappingData {
	
	private String source;
	private String destination;
	private String defaultMapping;
	private String valueMapping;
	
	@XmlElement(name="src")
	public String getSource() {
		return source;
	}
	
	@XmlElement(name="dst")
	public String getDestination() {
		return destination;
	}
	
	@XmlElement(name="default-val")
	public String getDefaultMapping() {
		return defaultMapping;
	}
	
	@XmlElement(name="value-mapping")
	public String getValueMapping() {
		return valueMapping;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public void setDefaultMapping(String defaultMapping) {
		this.defaultMapping = defaultMapping;
	}
	public void setValueMapping(String valueMapping) {
		this.valueMapping = valueMapping;
	}

}
