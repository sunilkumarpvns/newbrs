package com.elitecore.elitesm.ws.ytl.cal.data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "target")
public class ResponseTarget {
	
	private String name;
	private String operation;
	private SuccessResult result;
	private ErrorMessage error;
	
	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlAttribute(name = "operation")
	public String getOperation() {
		return operation;
	}
	
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	@XmlElement(name = "result")
	public SuccessResult getResult() {
		return result;
	}
	
	public void setResult(SuccessResult result) {
		this.result = result;
	}
	
	@XmlElement(name = "error")
	public ErrorMessage getError() {
		return error;
	}
	
	public void setError(ErrorMessage error) {
		this.error = error;
	}
}