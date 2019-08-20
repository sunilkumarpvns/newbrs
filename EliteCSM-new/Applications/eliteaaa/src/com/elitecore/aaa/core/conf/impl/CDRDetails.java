package com.elitecore.aaa.core.conf.impl;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.ClassicCSVAcctDriverConfiguration;

@XmlType(propOrder={})
public class CDRDetails{
	
	private String header="false";
	private String delimeter="";
	private String multiValueDelimeter="";
	private String cdrTimeStampHeader = ClassicCSVAcctDriverConfiguration.CDRTIMESTAMP_HEADER_DEFAULT_VALUE; 
	private String cdrTimeStampFormat;
	private String cdrTimeStampPosition = ClassicCSVAcctDriverConfiguration.CDRTIMESTAMP_POSITION_DEFAULT_VALUE;
	private String enclosingChar;
	
	public CDRDetails() {
		
	}
	
	public void setHeader(String header) {
		this.header = header;
	}
	public void setDelimeter(String delimeter) {
		this.delimeter = delimeter;
	}
	public void setmultiValueDelimeter(String multiValueDelimeter) {
		this.multiValueDelimeter = multiValueDelimeter;
	}
	public void setCdrTimeStampHeader(String cdrTimeStampHeader) {
		this.cdrTimeStampHeader = cdrTimeStampHeader;
	}
	public void setCDRTimeStampFormat(String cdrTimeStampFormat) {
		this.cdrTimeStampFormat = cdrTimeStampFormat;
	}
	public void setCdrTimeStampPosition(String cdrTimeStampPosition) {
		this.cdrTimeStampPosition = cdrTimeStampPosition;
	}
	
	@XmlElement(name ="header",type =String.class,defaultValue ="false")
	public String getHeader() {
		return this.header;
	}

	@XmlElement(name ="delimeter",type =String.class,defaultValue="")
	public String getDelimeter() {
		return this.delimeter;
	}
	
	@XmlElement(name ="multi-value-delimeter",type =String.class)
	public String getmultiValueDelimeter() {
		return this.multiValueDelimeter;
	}

	@XmlElement(name ="cdr-time-header",type =String.class, defaultValue = ClassicCSVAcctDriverConfiguration.CDRTIMESTAMP_HEADER_DEFAULT_VALUE)
	public @Nonnull String getCdrTimeStampHeader() {
		return cdrTimeStampHeader;
	}
	@XmlElement(name ="cdr-time-format",type =String.class)
	public String getCDRTimeStampFormat() {
		return this.cdrTimeStampFormat;
	}
	@XmlElement(name ="cdr-time-position",type =String.class, defaultValue = ClassicCSVAcctDriverConfiguration.CDRTIMESTAMP_POSITION_DEFAULT_VALUE)
	public @Nonnull String getCdrTimeStampPosition() {
		return cdrTimeStampPosition;
	}
	@XmlElement(name ="enclosing-character",type =String.class)
	public String getEnclosingChar() {
		return enclosingChar;
	}
	public void setEnclosingChar(String enclosingChar) {
		this.enclosingChar = enclosingChar;
	}

}

