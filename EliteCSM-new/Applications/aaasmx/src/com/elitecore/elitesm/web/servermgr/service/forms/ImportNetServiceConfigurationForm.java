package com.elitecore.elitesm.web.servermgr.service.forms;

import org.apache.struts.upload.FormFile;

import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;

public class ImportNetServiceConfigurationForm extends BaseDictionaryForm{
	
	private FormFile configurationFile;
	private String netServiceId;
	public FormFile getConfigurationFile() {
		return configurationFile;
	}
	public void setConfigurationFile(FormFile configurationFile) {
		this.configurationFile = configurationFile;
	}
	public String getNetServiceId() {
		return netServiceId;
	}
	public void setNetServiceId(String netServiceId) {
		this.netServiceId = netServiceId;
	}
	
	
}
