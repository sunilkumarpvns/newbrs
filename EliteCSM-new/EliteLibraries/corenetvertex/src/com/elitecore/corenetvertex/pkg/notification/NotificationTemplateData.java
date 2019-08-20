package com.elitecore.corenetvertex.pkg.notification;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.corenetvertex.sm.ResourceData;
import com.google.gson.JsonObject;

@Entity
@Table(name = "TBLM_NOTIFICATION_TEMPLATE")
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificationTemplateData extends ResourceData implements Serializable, Cloneable{

	private static final long serialVersionUID = 1L;
	private String name;
	@XmlTransient private String subject;
	@XmlTransient private String groups;
	private transient String templateData;
	@XmlTransient private NotificationTemplateType templateType;

	@Column(name = "NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	@Column(name = "STATUS")
	public String getStatus() {
		return super.getStatus();
	}

	@Override
	public void setStatus(String status) {
		super.setStatus(status);
	}
	
	@Column(name = "SUBJECT")
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	@Column(name = "TEMPLATE_DATA")
	public String getTemplateData() {
		return templateData;
	}
	public void setTemplateData(String templateData) {
		this.templateData = templateData;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name = "TEMPLATE_TYPE")
	public NotificationTemplateType getTemplateType() {
		return templateType;
	}
	public void setTemplateType(NotificationTemplateType templateType) {
		this.templateType = templateType;
	}
	
	@Transient
	@Override
	public String getHierarchy() {
		return getId() + "<br>"+ name;
	}

	@Override
	@Column(name="GROUPS")
	public String getGroups(){
		return super.getGroups();
	}

	@Override
	public void setGroups(String groups){
		super.setGroups(groups);
	}


	@Override
	public JsonObject toJson(){
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("Name", name);
		jsonObject.addProperty("Subject", subject);
		jsonObject.addProperty("Template Data", templateData);
		jsonObject.addProperty("Template Type", templateType.name());
		return jsonObject;
	}
	
	public NotificationTemplateData deepClone() throws CloneNotSupportedException {
		NotificationTemplateData newData = (NotificationTemplateData) this.clone();		
		newData.templateData = templateData;
		return newData;
	}
	@Override
	@Transient
	public String getResourceName() {
		return getName();
	}
}
