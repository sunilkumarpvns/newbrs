package com.elitecore.aaa.diameter.conf.sessionmanager;

import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;

import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.core.commons.util.StringUtility;

public class OperationOverrideDetail {
	private String name;
	private String expression;
	private String overrideAction;
	
	@XmlElement(name = "name")
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = "expression")
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@XmlElement(name = "override-action")
	public String getOverrideAction() {
		return overrideAction;
	}
	public void setOverrideAction(String overrideAction) {
		this.overrideAction = overrideAction;
	}
	
	@Override
	public String toString() {
		StringWriter out = new StringWriter();
		IndentingPrintWriter writer = new IndentingPrintWriter(out);
		writer.print(StringUtility.fillChar("-", 30));
		writer.print(this.getClass().getName());
		writer.println(StringUtility.fillChar("-", 30));
		writer.incrementIndentation();
		
		writer.println("Name : " + name);
		writer.println("Expression : " + expression);
		writer.println("Action : " + overrideAction);
		writer.decrementIndentation();
		writer.close();
		return out.toString();
	}
	
	public void format(IndentingPrintWriter writer) {
		writer.incrementIndentation();
		
		writer.println("Name : " + name);
		writer.println("Expression : " + expression);
		writer.println("Action : " + overrideAction);
		writer.decrementIndentation();
	}
}
