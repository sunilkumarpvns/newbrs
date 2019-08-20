package com.elitecore.aaa.core.data;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class AttributeDetails{

	private String id;
	private String value;
	private Date validity; 
	private boolean isValid = true;
	private String type;

	public AttributeDetails(){
		// required By Jaxb.
	}
	
	@XmlElement(name ="validity",type= Date.class)
	public Date getValidity() {
		return validity;
	}

	public void setValidity(Date validity) {
		this.validity = validity;
	}
	
	@XmlElement(name ="type",type= String.class)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlElement(name ="id",type= String.class)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@XmlElement(name ="value",type=String.class)
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@XmlTransient
	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

}