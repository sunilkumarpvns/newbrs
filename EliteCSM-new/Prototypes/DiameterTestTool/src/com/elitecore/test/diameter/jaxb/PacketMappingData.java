package com.elitecore.test.diameter.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="packet-mapping")
public class PacketMappingData {
	
	private String name;
	private List<MappingData> dynamicMappingData;
	private List<MappingData> staticMappingData;
	private MappingData mappingData;

	@XmlAttribute(name="name", required=true)
	public String getName() {
		return name;
	}
	
	@XmlElement(name="mapping", required=true)
	public MappingData getMappingData() {
		return mappingData;
	}
	
	@XmlElement(name="dynamic-mapping", required=false)
	public List<MappingData> getDynamicMappingData() {
		return dynamicMappingData;
	}

	@XmlElement(name="static-mapping", required=false)
	public List<MappingData> getStaticMappingData() {
		return staticMappingData;
	}

	public void setMappingData(MappingData mappingData) {
		this.mappingData = mappingData;
	}

	public void setDynamicMappingData(List<MappingData> dynamicMappingData) {
		this.dynamicMappingData = dynamicMappingData;
	}

	public void setStaticMappingData(List<MappingData> staticMappingData) {
		this.staticMappingData = staticMappingData;
	}
	
	public void setName(String name) {
		this.name = name;
	}

}
