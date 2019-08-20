package com.elitecore.nvsmx.system.util.migrate;

import javax.xml.bind.annotation.XmlElement;

public class MappingData {
	private String oldValue;
	private String newValue;
	
	public MappingData() {
		
	}

	@XmlElement(name="old")
	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	@XmlElement(name="new")
	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
}