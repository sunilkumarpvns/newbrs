package com.elitecore.elitesm.web.servermgr.server.forms;

import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;

public class ShowNetServerDictionaryForm extends BaseDictionaryForm{
	
	private String netServerId;
	private String fileName;
	private String fileGroup;
	
	public String getNetServerId() {
		return netServerId;
	}

	public void setNetServerId(String netServerId) {
		this.netServerId = netServerId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileGroup() {
		return fileGroup;
	}

	public void setFileGroup(String fileGroup) {
		this.fileGroup = fileGroup;
	}

	
	
}
