package com.elitecore.aaa.core.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={})
public class DetailLocalFileDetails {
	private String fileName="detail.local";
	private String location="data/detail-local-files";
	private String defaultDirName="no_nas_ip_address";
	
	private String prefixFileName="";
	private String folderName="";
	private boolean bCreateBlankFile;
	
	
	public DetailLocalFileDetails() {
	}

	@XmlElement(name="file-name",type=String.class,defaultValue="detail.local")
	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@XmlElement(name="location",type=String.class,defaultValue="data/detail-local-files")
	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	@XmlElement(name="default-folder-name",type=String.class,defaultValue="no_nas_ip_address")
	public String getDefaultDirName() {
		return defaultDirName;
	}


	public void setDefaultDirName(String defaultDirName) {
		this.defaultDirName = defaultDirName;
	}

	@XmlElement(name="prefix-file-name",type=String.class)
	public String getPrefixFileName() {
		return prefixFileName;
	}


	public void setPrefixFileName(String prefixFileName) {
		this.prefixFileName = prefixFileName;
	}


	@XmlElement(name="folder-name",type=String.class)
	public String getFolderName() {
		return folderName;
	}


	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}


	@XmlElement(name="create-blank-file",type=boolean.class)
	public boolean getIsCreateBlankFile() {
		return bCreateBlankFile;
	}


	public void setIsCreateBlankFile(boolean bCreateBlankFile) {
		this.bCreateBlankFile = bCreateBlankFile;
	}

}
