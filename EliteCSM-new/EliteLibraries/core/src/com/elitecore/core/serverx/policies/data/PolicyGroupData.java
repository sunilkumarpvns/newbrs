package com.elitecore.core.serverx.policies.data;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "policy-group")
public class PolicyGroupData {

	private String name;
	private String expression;
	
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	
	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name="expression")
	public String getExpression() {
		return expression;
	}
	
	/**
	 * @param expression
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(format("%-30s: %s", "Group Name: ", getName() != null ? getName() : ""));
		out.println(format("%-30s: %s", "Expression: ", getExpression() != null ? getExpression() : ""));
		out.close();
		return writer.toString();
	}
}