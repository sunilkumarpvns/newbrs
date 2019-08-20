package com.elitecore.aaa.core.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class DataFieldMappingImpl implements DataFieldMapping{

	private String fieldName;
	private String defaultValue;
	private Map<String,String> valueMapping;
	private String logicalName;
	private String valueMappings;
	
	public DataFieldMappingImpl(){
		// required By Jaxb.
	}	

	public DataFieldMappingImpl(String fieldName, String defaultValue, String valueMapping,String logicalName){
		this.fieldName = fieldName;
		this.defaultValue = defaultValue;
		this.logicalName =logicalName;
		this.valueMappings =valueMapping;
		setValueMapping(valueMapping);
		
	}
	
	@XmlElement(name = "value-mapping",type = String.class)
	public String getValueMappings() {
		return valueMappings;
	}
	public void setValueMappings(String valueMappings) {
		this.valueMappings = valueMappings;
	}
	
	@XmlElement(name = "logical-name",type=String.class)
	public String getLogicalName() {
		return logicalName;
	}
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	
	@XmlElement(name = "field",type = String.class)
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	@XmlElement(name = "default-value",type = String.class)
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	@XmlTransient
	public Map<String, String> getValueMapping() {
		return valueMapping;
	}
	public String getValue(String inValue) {
		String retValue = inValue;
		if (retValue != null && retValue.trim().length() > 0){
			String mappedValue = valueMapping!=null ? valueMapping.get(inValue) : null;
			if (mappedValue != null && mappedValue.trim().length() > 0){
				retValue = mappedValue;
			}
		} else {
			retValue = defaultValue;
		}
		return retValue;
	}
	public void setValueMapping(Map<String, String> valueMapping) {
		this.valueMapping = valueMapping;
	}
	public void setValueMapping(String valueMapping) {
		if(valueMapping!=null){
			String[] keyPairArray = valueMapping.split(",");
			if(keyPairArray.length>0){
				this.valueMapping = new HashMap<String,String>();				
				for (int i = 0; i < keyPairArray.length; i++) {
					String[] keyPair = keyPairArray[i].split("=");
					if(keyPair.length==2){
						this.valueMapping.put(keyPair[0].trim(),keyPair[1].trim());
					}
				}
			}
		}
	}
	
	
	
	public String toString(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.print(fieldName);
		out.print(" (Default-Value= " + ((defaultValue != null)?defaultValue:"\"\""));
		out.print(", Value-Mapping= " + ((valueMapping!= null)?valueMapping:"\"\""));
		out.print(")");
		out.println();
		out.close();
		return stringBuffer.toString();
	}

}
