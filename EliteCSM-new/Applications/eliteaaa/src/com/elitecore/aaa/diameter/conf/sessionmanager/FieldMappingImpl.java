package com.elitecore.aaa.diameter.conf.sessionmanager;

import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.serverx.sessionx.FieldMapping;

/***
 * 
 * @author malav.desai
 *
 */
@XmlType(propOrder = {})
public class FieldMappingImpl implements FieldMapping{
	
	private String columnName;
	private String propertyName;
	private int type;
	private String logicalField;
	private String defaultValue = "";
	
	public FieldMappingImpl() {
		//for JAXB
	}
	
	public FieldMappingImpl(String columnName, String propertyName, int type, String defaultValue) {
		this.columnName = columnName;
		this.propertyName = propertyName;
		this.type = type;
		this.defaultValue = defaultValue;
	}
	
	@XmlElement(name = "db-field-name", type = String.class)
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	@XmlElement(name = "referring-attribute", type = String.class)
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	
	@XmlElement(name = "data-type", type = int.class)
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	@XmlElement(name = "logical-field", type = String.class)
	public String getField() {
		return logicalField;
	}
	public void setField(String logicalField) {
		this.logicalField = logicalField;
	}
	
	@XmlElement(name = "default-value", type = String.class)
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	@Override
	public String toString() {
		StringWriter out = new StringWriter();
		IndentingPrintWriter writer = new IndentingPrintWriter(out);
		writer.print(StringUtility.fillChar("-", 30));
		writer.print(this.getClass().getName());
		writer.println(StringUtility.fillChar("-", 30));
		writer.incrementIndentation();
		
		writer.println("Coloumn Name : " + columnName);
		writer.println("Property Name : " + propertyName);
		writer.println("DB Type : " + type);
		writer.println("Default Value : " + defaultValue);
		if (Strings.isNullOrBlank(logicalField)) {
			writer.println("Logical Field : " + logicalField);
		}
		writer.decrementIndentation();
		writer.close();
		return out.toString();
	}

	public void format(IndentingPrintWriter writer) {
		writer.incrementIndentation();
		writer.println("Coloumn Name : " + columnName);
		writer.println("Property Name : " + propertyName);
		writer.println("DB Type : " + type);
		writer.println("Default Value : " + defaultValue);
		if (Strings.isNullOrBlank(logicalField)) {
			writer.println("Logical Field : " + logicalField);
		}
		writer.decrementIndentation();
	}
}