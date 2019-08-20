package com.elitecore.elitesm.ws.ytl.cal.data;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
public class Response {

	private String version;
	private ResponseTarget respTarget;
	
	@XmlAttribute(name = "version")
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	@XmlElement(name = "target")
	public ResponseTarget getRespUser() {
		return respTarget;
	}

	public void setRespUser(ResponseTarget respTarget) {
		this.respTarget = respTarget;
	}
	@Override
	public String toString() {
		StringWriter out  = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
	    writer.println("\n  Version =" +getVersion());  
		writer.close();
		return out.toString();
	}
}
