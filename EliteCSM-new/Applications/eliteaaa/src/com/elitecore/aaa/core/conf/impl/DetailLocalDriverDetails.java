package com.elitecore.aaa.core.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder={})
public class DetailLocalDriverDetails {
	
	private String eventDateFormat="";
	private String writeAttributes="All";
	private boolean useDictonaryVal=true;
	private String avPairSeparator="=";
	
	public DetailLocalDriverDetails() {
	}

	@XmlElement(name="event-date-format",type=String.class)
	public String getEventDateFormat() {
		return eventDateFormat;
	}

	public void setEventDateFormat(String eventDateFormat) {
		this.eventDateFormat = eventDateFormat;
	}

	@XmlElement(name="write-attributes",type=String.class,defaultValue="All")
	public String getWriteAttributes() {
		return writeAttributes;
	}

	public void setWriteAttributes(String writeAttributes) {
		this.writeAttributes = writeAttributes;
	}

	@XmlElement(name="use-dictionary-value",type=boolean.class,defaultValue="true")
	public boolean getIsUseDictonaryVal() {
		return useDictonaryVal;
	}

	public void setIsUseDictonaryVal(boolean useDictonaryVal) {
		this.useDictonaryVal = useDictonaryVal;
	}

	@XmlElement(name="avpair-separator",defaultValue="=")
	public String getAvPairSeparator() {
		return avPairSeparator;
	}

	public void setAvPairSeparator(String avPairSeparator) {
		this.avPairSeparator = avPairSeparator;
	}

}
