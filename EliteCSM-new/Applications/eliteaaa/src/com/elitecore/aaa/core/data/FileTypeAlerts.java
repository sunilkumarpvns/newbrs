package com.elitecore.aaa.core.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder = {})
public class FileTypeAlerts {

	private List<FileAlertConfiguration> fileAlertConfigurationsList;

	public FileTypeAlerts(){
		//required by Jaxb.
		fileAlertConfigurationsList = new ArrayList<FileAlertConfiguration>();
	}

	@XmlElement(name = "file-listner")
	public List<FileAlertConfiguration> getFileAlertConfigurations() {
		return fileAlertConfigurationsList;
	}

	public void setFileAlertConfigurations(List<FileAlertConfiguration> fileAlertConfigurations) {
		this.fileAlertConfigurationsList = fileAlertConfigurations;
	}

}
