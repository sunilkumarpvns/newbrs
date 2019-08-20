package com.elitecore.core.serverx.sessionx.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.core.commons.config.core.UserDefined;
import com.elitecore.core.commons.config.core.annotations.Reloadable;
import com.elitecore.core.serverx.sessionx.FieldMapping;


@XmlType(propOrder = {})
@XmlRootElement(name = "field-mapping")
public class FieldMappingImpl implements FieldMapping,UserDefined {
	
	private String columnName;
	private String propertyName;
	private int type;
	private String field;
	private String defaultValue = "";
	private List<String> dummyList = new ArrayList<String>();

	public FieldMappingImpl(){
		//Required by JAXB
	}
	
	
	public FieldMappingImpl(int type,String propertyName,String columnName) {
		this.type = type;
		this.propertyName = propertyName;
		this.columnName = columnName;
	}
	
	public FieldMappingImpl(int type, String propertyName, String columnName, String defaultValue){
		this(type,propertyName,columnName);
		this.defaultValue = defaultValue;
	}
	public FieldMappingImpl(int type, String propertyName, String columnName, String defaultValue,String field){
		this(type,propertyName,columnName,defaultValue);
		this.field = field;
	}
	
	@XmlElementWrapper(name = "dummy-list")
	@XmlElement(name = "dummy-entry")
	public List<String> getDummyList() {
		return dummyList;
	}

	public void setDummyList(List<String> dummyList) {
		this.dummyList = dummyList;
	}	
	
	@Reloadable(type = String.class)
	@XmlElement(name = "column-name",type = String.class)
	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	@Reloadable(type = String.class)
	@XmlElement(name = "property-type", type = String.class)
	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	@Reloadable(type = int.class)
	@XmlElement(name = "type", type = int.class)
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Reloadable(type = String.class)
	@XmlElement(name = "default-value", type = String.class)
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	
	@Override 
	public String toString(){
		return "";
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof FieldMappingImpl))
			return false;
		
		FieldMappingImpl other = (FieldMappingImpl) obj;
		return columnName.equals(other.columnName);
	}

	@Override
	@XmlElement(name = "field", type = String.class)
	public String getField() {
		return field;
	}
	
	
	public void setField(String field) {
		this.field = field;
	}
}
