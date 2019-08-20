package com.elitecore.aaa.diameter.conf.sessionmanager;

import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.core.commons.util.StringUtility;

/***
 * 
 * @author malav.desai
 *
 */
@XmlType(propOrder = {})
public class SessionScenarioDetails{
	private String name;
	private String expression;
	private String fieldMappingName;
	private String criteria;
	private List<String> criteriaParams;
	
	@XmlElement(name = "expression", type = String.class)
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@XmlElement(name = "field-mapping-list-name", type = String.class)
	public String getFieldMappingName() {
		return fieldMappingName;
	}
	public void setFieldMappingName(String fieldMappingName) {
		this.fieldMappingName = fieldMappingName;
	}
	
	@XmlElement(name = "criteria", type = String.class)
	public String getCriteria() {
		return criteria;
	}
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}
	
	@XmlTransient
	public List<String> getCriteriaParams() {
		return this.criteriaParams;
	}
	public void setCriteriaParams(List<String> criteriaParams) {
		this.criteriaParams = criteriaParams;
	}
	
	@XmlElement(name = "name", type = String.class)
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		StringWriter out = new StringWriter();
		IndentingPrintWriter writer = new IndentingPrintWriter(out);
		writer.print(StringUtility.fillChar("-", 30));
		writer.print(this.getClass().getName());
		writer.println(StringUtility.fillChar("-", 30));
		writer.incrementIndentation();
		
		writer.println("Scenario Name : " + name);
		writer.println("Scenario Expression : " + expression);
		writer.println("Scenario Mapping Name : " + fieldMappingName);
		writer.println("Session Criteria : " + criteria);
		writer.decrementIndentation();
		writer.close();
		return out.toString();
	}
	
	public void format(IndentingPrintWriter writer) {
		writer.incrementIndentation();
		
		writer.println("Scenario Name : " + name);
		writer.println("Scenario Expression : " + expression);
		writer.println("Scenario Mapping Name : " + fieldMappingName);
		writer.println("Session Criteria : " + criteria);
		writer.decrementIndentation();
	}
}