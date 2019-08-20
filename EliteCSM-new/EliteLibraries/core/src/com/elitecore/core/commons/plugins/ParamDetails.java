package com.elitecore.core.commons.plugins;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={})
public class ParamDetails {
	
	private String active;
	private int packet_type;
	private String attr_id;
	private String attribute_value;
	private String parameter_usage;
	
	public ParamDetails() {
	}
	
	@XmlElement(name ="active",type = String.class)
	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	@XmlElement(name ="packet-type",type = int.class)
	public int getPacket_type() {
		return packet_type;
	}

	public void setPacket_type(int packet_type) {
		this.packet_type = packet_type;
	}

	@XmlElement(name ="attr-id",type = String.class)
	public String getAttr_id() {
		return attr_id;
	}

	public void setAttr_id(String attr_id) {
		this.attr_id = attr_id;
	}

	@XmlElement(name ="attribute-value",type = String.class)
	public String getAttribute_value() {
		return attribute_value;
	}

	public void setAttribute_value(String attribute_value) {
		this.attribute_value = attribute_value;
	}
	
	@XmlElement(name ="parameter-usage",type = String.class)
	public String getParameter_usage() {
		return parameter_usage;
	}

	public void setParameter_usage(String parameter_usage) {
		this.parameter_usage = parameter_usage;
	}
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
        out.println("	Parameter: { Active = "+active+",Packet Type = "+packet_type+",Attribute Id = "+attr_id+",Attribute Value = "+attribute_value+",Parameter Usage = "+parameter_usage+"}");
        return stringBuffer.toString();
}
}
