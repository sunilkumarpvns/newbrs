package com.elitecore.aaa.core.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={})
public class DBAcctDriverMandatoryFieldDetails {

	private String callStartFieldName="call_start";
	private String callEndFieldName="call_end";
	private String createDateFieldName="create_date";
	private String lastModifiedDateFieldName="last_modified_date";
	
	public DBAcctDriverMandatoryFieldDetails() {
		// TODO Auto-generated constructor stub
	}

	public void setCallStartFieldName(String callStartFieldName) {
		this.callStartFieldName = callStartFieldName;
	}

	public void setCallEndFieldName(String callEndFieldName) {
		this.callEndFieldName = callEndFieldName;
	}

	public void setCreateDateFieldName(String createDateFieldName) {
		this.createDateFieldName = createDateFieldName;
	}

	public void setLastModifiedDateFieldName(String lastModifiedDateFieldName) {
		this.lastModifiedDateFieldName = lastModifiedDateFieldName;
	}

	@XmlElement(name="call-start-field-name",type=String.class,defaultValue="call_start")
	public String getCallStartFieldName() {
		return callStartFieldName;
	}

	@XmlElement(name="call-end-field-name",type=String.class,defaultValue="call_end")
	public String getCallEndFieldName() {
		return callEndFieldName;
	}

	@XmlElement(name="create-date-field-name ",type=String.class,defaultValue="create_date")
	public String getCreateDateFieldName() {
		return createDateFieldName;
	}

	@XmlElement(name="last-modified-date-field-name",type=String.class,defaultValue="last_modified_date")
	public String getLastModifiedDateFieldName() {
		return lastModifiedDateFieldName;
	}

}
