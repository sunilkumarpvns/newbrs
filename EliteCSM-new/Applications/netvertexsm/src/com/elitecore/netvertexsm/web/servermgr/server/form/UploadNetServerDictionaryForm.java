package com.elitecore.netvertexsm.web.servermgr.server.form;

import org.apache.struts.upload.FormFile;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class UploadNetServerDictionaryForm extends BaseWebForm{
	
	private FormFile configurationFile;
	private Long netServerId;
	private String action;	
    private String fileGroup;
    
	public Long getNetServerId() {
		return netServerId;
	}

	public void setNetServerId(Long netServerId) {
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
