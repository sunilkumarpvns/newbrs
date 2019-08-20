package com.elitecore.elitesm.web.servermgr.server.forms;

import org.apache.struts.upload.FormFile;

import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;

public class UploadNetServerDictionaryForm extends BaseDictionaryForm{
	
	private FormFile configurationFile;
	private String netServerId;
	private String action;	
    private String fileGroup;
    
	public String getNetServerId() {
		return netServerId;
	}

	public void setNetServerId(String netServerId) {
		this.netServerId = netServerId;
	}

	public FormFile getConfigurationFile() {
		return configurationFile;
	}

	public void setConfigurationFile(FormFile configurationFile) {
		this.configurationFile = configurationFile;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getFileGroup() {
		return fileGroup;
	}

	public void setFileGroup(String fileGroup) {
		this.fileGroup = fileGroup;
	}

}
