package com.elitecore.aaa.core.data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.core.commons.config.core.annotations.Reloadable;
/**
 * This class is used to store the name and value pair that are in miscellaneous configuration
 * 
 * @author Kuldeep Panchal
 *
 */
@Reloadable(type = ParamsDetail.class)
@XmlType(propOrder = {})
public class ParamsDetail{
	private String name;
	private String value;
	
	public ParamsDetail(){
		//required by Jaxb.
	}
	
	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlAttribute(name = "value")
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}