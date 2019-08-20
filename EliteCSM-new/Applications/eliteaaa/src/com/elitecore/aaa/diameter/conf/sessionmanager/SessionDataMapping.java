package com.elitecore.aaa.diameter.conf.sessionmanager;

import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.core.commons.util.StringUtility;

/***
 * 
 * @author malav.desai
 *
 */
@XmlType(propOrder = {})
public class SessionDataMapping{
	private String name;
	private List<FieldMappingImpl> fieldMappings;
	
	@XmlElement(name = "name", type = String.class)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElementWrapper(name = "field-mappings")
	@XmlElement(name = "field-mapping")
	public List<FieldMappingImpl> getFeildMappings() {
		return fieldMappings;
	}
	public void setFeildMappings(List<FieldMappingImpl> feildMappings) {
		this.fieldMappings = feildMappings;
	}
	
	@Override
	public String toString() {
		StringWriter out = new StringWriter();
		IndentingPrintWriter writer = new IndentingPrintWriter(out);
		writer.print(StringUtility.fillChar("-", 30));
		writer.print(this.getClass().getName());
		writer.println(StringUtility.fillChar("-", 30));
		writer.incrementIndentation();
		
		writer.println("SM Data Mapping Name : " + name);
		writer.println("Feild Mappings : ");
		for (FieldMappingImpl fieldMapping : fieldMappings) {
			writer.println(fieldMapping);
		}
		writer.decrementIndentation();
		writer.close();
		return out.toString();
	}
	public void format(IndentingPrintWriter writer) {
		writer.incrementIndentation();
		writer.println("SM Data Mapping Name : " + name);
		writer.println("Feild Mappings : ");
		for (FieldMappingImpl fieldMapping : fieldMappings) {
			fieldMapping.format(writer);
		}
		writer.decrementIndentation();
	}
	
}