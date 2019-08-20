package com.elitecore.elitesm.web.dashboard.widget.json;


public class WidgetJSON {
	private Header header;
	private String body;
	private String exceptionDetails;
	private boolean isError=false;
	
	public WidgetJSON() {
		
	}
	
	public WidgetJSON(Header header, String body) {
		this.header = header;
		this.body = body;
	}
	
	public WidgetJSON(Header header, String body,String exceptionDetails) {
		this.header = header;
		this.body = body;
		this.exceptionDetails=exceptionDetails;
		this.isError=true;
	}
	
	public Header getHeader() {
		return header;
	}
	public void setHeader(Header header) {
		this.header = header;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}

	public String getExceptionDetails() {
		return exceptionDetails;
	}

	public void setExceptionDetails(String exceptionDetails) {
		this.exceptionDetails = exceptionDetails;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}
}
