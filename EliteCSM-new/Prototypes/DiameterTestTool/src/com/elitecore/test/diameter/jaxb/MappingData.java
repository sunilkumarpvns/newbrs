package com.elitecore.test.diameter.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="mapping")
public class MappingData {
	
	
	private String condition;
	private List<AttributeMappingMappingData> attributeMappingData;

	
	@XmlElement(name="condition")
	public String getCondition() {
		return condition;
	}
	
	@XmlElement(name="attribute-mapping")
	public List<AttributeMappingMappingData> getAttributeMappingData() {
		return attributeMappingData;
	}

	public void setAttributeMappingData(List<AttributeMappingMappingData> attributemappingData) {
		this.attributeMappingData = attributemappingData;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}

}
