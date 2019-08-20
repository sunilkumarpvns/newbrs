package com.elitecore.test.diameter.jaxb;

import javax.xml.bind.annotation.XmlAttribute;

public class ScenarioPacketMappingData {
	private String fileName;
	private String name;
	
	@XmlAttribute(name="file", required=true)
	public String getFileName() {
		return fileName;
	}
	
	@XmlAttribute(name="name", required=true)
	public String getName() {
		return name;
	}
	
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ScenarioPacketMappingData [fileName=" + fileName + ", name="
				+ name + "]";
	}
	
	
	
}
