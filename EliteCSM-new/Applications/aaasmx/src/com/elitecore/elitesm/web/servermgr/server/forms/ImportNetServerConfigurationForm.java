package com.elitecore.elitesm.web.servermgr.server.forms;

import org.apache.struts.upload.FormFile;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ImportNetServerConfigurationForm extends BaseWebForm{
	
	private FormFile configurationFile;
	private String netServerId;
	public FormFile getConfigurationFile() {
		return configurationFile;
	}
	public void setConfigurationFile(FormFile configurationFile) {
		this.configurationFile = configurationFile;
	}
	public String getNetServerId() {
		return netServerId;
	}
	public void setNetServerId(String netServerId) {
		this.netServerId = netServerId;
	}	
	
	
}
