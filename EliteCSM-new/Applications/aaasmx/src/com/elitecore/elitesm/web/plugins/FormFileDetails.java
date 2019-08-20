package com.elitecore.elitesm.web.plugins;

import java.sql.Timestamp;

import org.apache.struts.upload.FormFile;

public class FormFileDetails {
	
	private FormFile formFile;
	private Timestamp lastUpdatedTime;
	private String fileName;
	
	public FormFile getFormFile() {
		return formFile;
	}
	public void setFormFile(FormFile formFile) {
		this.formFile = formFile;
	}
	public Timestamp getLastUpdatedTime() {
		return lastUpdatedTime;
	}
	public void setLastUpdatedTime(Timestamp lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
