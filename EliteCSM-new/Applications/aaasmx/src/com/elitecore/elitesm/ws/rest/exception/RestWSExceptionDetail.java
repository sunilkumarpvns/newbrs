package com.elitecore.elitesm.ws.rest.exception;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Exception-Detail")
public class RestWSExceptionDetail {

	private String errorMessage;
	private String metadata;
//	private StackTraceElement[] stacktrace;
	
	@XmlElement(name =  "Error-Message")
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	@XmlElement(name =  "Metadata")
	public String getMetadata() {
		return metadata;
	}
	
	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
	
//	@XmlElement(name = "Stack-trace")
//	public StackTraceElement[] getStacktrace() {
//		return stacktrace;
//	}
//	
//	public void setStacktrace(StackTraceElement[] stacktrace) {
//		this.stacktrace = stacktrace;
//	}
}
