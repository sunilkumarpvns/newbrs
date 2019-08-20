package com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.diameteruniversalplugin;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;
@XmlType(propOrder={"active","packet_type","attr_id","attribute_value","parameter_usage"})
public class ParamDetails {
	
	@NotEmpty(message = "Active parameter must be specified")
	private String active;
	@NotEmpty(message = "Packet Type must be specified")
	private String packet_type;
	private String attr_id;
	private String attribute_value;
	@NotEmpty(message = "Parameter Usage must be specified")
	private String parameter_usage;
	
	public ParamDetails() {
	}
	
	@Pattern(regexp = "^$|YES|NO", message = "Invalid active parameter. It can be YES or NO.")
	@XmlElement(name ="active")
	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	@XmlElement(name ="packet-type")
	@Pattern(regexp = "^$|Request|Answer", message = "Invalid Packet Type parameter. It can be Request or Answer")
	public String getPacket_type() {
		return packet_type;
	}

	public void setPacket_type(String packet_type) {
		this.packet_type = packet_type;
	}

	@XmlElement(name ="attr-id")
	public String getAttr_id() {
		return attr_id;
	}

	public void setAttr_id(String attr_id) {
		this.attr_id = attr_id;
	}

	@XmlElement(name ="attribute-value")
	public String getAttribute_value() {
		return attribute_value;
	}

	public void setAttribute_value(String attribute_value) {
		this.attribute_value = attribute_value;
	}
	
	@XmlElement(name ="parameter-usage")
	@Pattern(regexp = "^$|Check Item|Dynamical Assign Item|Filter Item|Reject Item|Reply Item|Update Item|Value Replace Item", message = "Invalid Parameter Usage. It can be one of this Check Item,Dynamical Assign Item,Filter Item,Reject Item,Reply Item,Update Item,Value Replace Item.")
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
