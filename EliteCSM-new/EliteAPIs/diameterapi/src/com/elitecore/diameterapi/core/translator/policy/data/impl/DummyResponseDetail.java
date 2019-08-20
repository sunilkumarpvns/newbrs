package com.elitecore.diameterapi.core.translator.policy.data.impl;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class DummyResponseDetail {

	private String outfield;
	private String value;

	public DummyResponseDetail(){
		//required by Jaxb.
	}
	
	@XmlElement(name = "out-field",type = String.class)
	public String getOutfield() {
		return outfield;
	}
	public void setOutfield(String outfield) {
		this.outfield = outfield;
	}
	@XmlElement(name ="dummy-value",type = String.class)
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println(format("%-30s: %s", "Attribute-Id", getOutfield()));
		out.println(format("%-30s: %s", "Value", getValue()));
		out.close();
		return stringBuffer.toString();
	}

}
