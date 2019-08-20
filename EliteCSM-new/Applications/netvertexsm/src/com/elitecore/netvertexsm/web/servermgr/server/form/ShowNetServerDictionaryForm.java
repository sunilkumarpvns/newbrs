package com.elitecore.netvertexsm.web.servermgr.server.form;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;


public class ShowNetServerDictionaryForm extends BaseWebForm{
	
	private Long netServerId;
	private String fileName;
	private String fileGroup;
	
	public Long getNetServerId() {
		return netServerId;
	}

	public void setNetServerId(Long netServerId) {
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
