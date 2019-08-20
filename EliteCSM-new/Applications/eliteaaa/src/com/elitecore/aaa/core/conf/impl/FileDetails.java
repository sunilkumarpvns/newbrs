package com.elitecore.aaa.core.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

public @XmlType(propOrder={})
class FileDetails{
	private String fileName="CDRs.csv";
	private String location="data/csvfiles";
	private boolean bCreateBlankFile;
	private String prefixFileName="";
	private String folderName="";
	private String defaultDirName="no_nas_ip_address";
	
	public FileDetails() {
	
	}
	
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setFileLocation(String location) {
		this.location = location;
	}
	public void setDefaultDirName(String defaultDirName) {
		this.defaultDirName = defaultDirName;
	}
	
	public void setPrefixFileName(String prefixFileName) {
		this.prefixFileName = prefixFileName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public void setIsCreateBlankFile(boolean bCreateBlankFile) {
		this.bCreateBlankFile = bCreateBlankFile;
	}
	
	@XmlElement(name ="file-name",type =String.class,defaultValue ="CDRs.csv")
	public String getFileName() {
		return this.fileName;
	}

	@XmlElement(name ="location",type =String.class,defaultValue ="data/csvfiles")
	public String getFileLocation() {
		return this.location;
	}

	@XmlElement(name ="default-driver-name",type =String.class,defaultValue ="no_nas_ip_address")
	public String getDefaultDirName() {
		return this.defaultDirName;
	}
	
	@XmlElement(name ="create-blank-file",type =boolean.class)
	public boolean getIsCreateBlankFile() {
		return this.bCreateBlankFile;
	}
	
	@XmlElement(name ="prefix-filename",type =String.class)
	public String getPrefixFileName() {
		return this.prefixFileName;
	}
	@XmlElement(name="folder-name",type =String.class)
	public String getFolderName() {
		return this.folderName;
	}
	
}
