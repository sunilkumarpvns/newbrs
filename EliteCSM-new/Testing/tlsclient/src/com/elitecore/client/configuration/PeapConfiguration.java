package com.elitecore.client.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Read PEAP method Configuration from eap-config.xml file
 * 
 * @author Kuldeep Panchal
 * @author Malav Desai
 *
 */
@XmlRootElement(name = "peap")
@XmlType(propOrder = {})
public class PeapConfiguration {
	private String innerMethod;
	private String innerIdentity;
	private String innerPasswd;
	
	@XmlElement(name = "inner-method")
	public String getInnerMethod() {
		return innerMethod;
	}
	public void setInnerMethod(String innerMethod) {
		this.innerMethod = innerMethod;
	}
	
	@XmlElement(name = "inner-identity")
	public String getInnerIdentity() {
		return innerIdentity;
	}
	public void setInnerIdentity(String innerIdentity) {
		this.innerIdentity = innerIdentity;
	}
	
	@XmlElement(name = "inner-passwd")
	public String getInnerPasswd() {
		return innerPasswd;
	}
	public void setInnerPasswd(String innerPasswd) {
		this.innerPasswd = innerPasswd;
	}
	
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		
		out.println("\n\tInner Method   : " + innerMethod);
		out.println("\n\tInner Identity : " + innerIdentity);
		out.println("\n\tInner Password : " + innerPasswd);
		out.close();
		
		return writer.toString();
	}
}
