package com.elitecore.netvertexsm.web.servermgr.server.form;

import org.apache.struts.upload.FormFile;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class ImportNetServerConfigurationForm extends BaseWebForm{
	
	private FormFile configurationFile;
	private long netServerId;
	public FormFile getConfigurationFile() {
		return configurationFile;
	}
	public void setConfigurationFile(FormFile configurationFile) {
		this.configurationFile = configurationFile;
	}
	public long getNetServerId() {
		return netServerId;
	}
	public void setNetServerId(long netServerId) {
		this.netServerId = netServerId;
	}	
	
	
}
