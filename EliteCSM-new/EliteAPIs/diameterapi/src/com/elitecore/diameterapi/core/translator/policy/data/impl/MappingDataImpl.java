package com.elitecore.diameterapi.core.translator.policy.data.impl;

 import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.diameterapi.core.translator.policy.data.MappingData;

@XmlType(propOrder = {})
public class MappingDataImpl implements MappingData {

	
	private String checkExpression="";
	private String mappingExpression="";
	private String defaultValue="";
	private String valueMapping="";
	
	public MappingDataImpl(){
		// required For JAXB
	}
	
	@XmlElement(name = "checked-expression", type = String.class)
	public String getCheckExpression() {
		return checkExpression;
	}

	public void setCheckExpression(String checkExpression) {
		this.checkExpression = checkExpression;
	}

	@XmlElement(name = "default-value",type = String.class)
	public String getDefaultValue() {
		return defaultValue;
	}
	
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@XmlElement(name = "mapping-expression", type = String.class)
	public String getMappingExpression() {
		return mappingExpression;
	}
	
	public void setMappingExpression(String mappingExpression) {
		this.mappingExpression = mappingExpression;
	}

	@XmlElement(name = "value-mapping",type = String.class)
	public String getValueMapping() {
		return valueMapping;
	}
	
	public void setValueMapping(String valueMapping) {
		this.valueMapping = valueMapping;
	}
	
	@Override
	public String toString() {
		
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("      -- Mapping Data Configuration -- ");
		out.println("      CheckExpression    = " + checkExpression);
		out.println("      MappingExpression  = " + mappingExpression);
		out.println("      DefaultValue       = " + defaultValue);
		out.println("      ValueMapping       = " + valueMapping);
		out.close();
		return stringBuffer.toString();
	}

}
