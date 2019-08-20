package com.elitecore.netvertexsm.web.servermgr.service.form;

import org.apache.struts.upload.FormFile;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class ImportNetServiceConfigurationForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private FormFile configurationFile;
	private Long netServiceId;
	public FormFile getConfigurationFile() {
		return configurationFile;
	}
	public void setConfigurationFile(FormFile configurationFile) {
		this.configurationFile = configurationFile;
	}
	public Long getNetServiceId() {
		return netServiceId;
	}
	public void setNetServiceId(Long netServiceId) {
		this.netServiceId = netServiceId;
	}
	
	
}
