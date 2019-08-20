package com.brs.beans.common;

public class Mail {
	private String to;
	private String from;
	private String subject;
	private Object data;
	private String provider;
	
	@Override
	public String toString() {
		return "Mail [To=" + to + ", From=" + from + ", Subject=" + subject + ",Data="+data+"]";
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	

}
