package com.elitecore.corenetvertex.service.notification;

import java.io.Serializable;

public class Template implements Serializable{

	private static final long serialVersionUID = 1L;
	private String templateId;
	private String name;
	private String subject;
	private String data;
	
	public Template(String templateId, String name, String subject, String data) {
		this.templateId = templateId;
		this.name = name;
		this.subject = subject;
		this.data = data;
	}
	
	public String getTemplateId() {
		return templateId;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public String getTemplateData() {
		return data;
	}
	
}
