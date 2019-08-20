package com.elitecore.aaa.core.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={})
public class DBAcctDriverCDRTimestampDetails {
	private boolean enabled=true;
	private String dbDateField="PARAM_DATE1";
	
	public DBAcctDriverCDRTimestampDetails() {
		// TODO Auto-generated constructor stub
	}


	public void setIsEnabled(boolean enebled) {
		this.enabled = enebled;
	}

	public void setDbDateField(String dbDateField) {
		this.dbDateField = dbDateField;
	}

	@XmlElement(name="timestamp-field",type=String.class,defaultValue="PARAM_DATE1")
	public String getDbDateField() {
		return dbDateField;
	}
	@XmlElement(name="timestamp-enabled",type=boolean.class,defaultValue="true")
	public boolean getIsEnabled() {
		return enabled;
	}

}
