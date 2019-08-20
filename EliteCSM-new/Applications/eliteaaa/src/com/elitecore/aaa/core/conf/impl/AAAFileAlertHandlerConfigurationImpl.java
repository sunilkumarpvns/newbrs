package com.elitecore.aaa.core.conf.impl;

import com.elitecore.aaa.core.conf.AAAFileAlertHandlerConfiguration;

public class AAAFileAlertHandlerConfigurationImpl implements AAAFileAlertHandlerConfiguration{

	private String name;
	
	private String fileName;
	private String filePath;
	
	
	public void setName(String name) {

		this.name = name;
	}
	
	public void setFileName(String fileName) {
		
		this.fileName = fileName;
	}

	public void setFilePath(String filePath) {
		
		this.filePath = filePath;
	}


	public String getFileName() {
		
		return this.fileName;
	}

	
	public String getFilePath() {
	
		return this.filePath;
	}
	
	public String getName() {

		return this.name;
	}



	
}
