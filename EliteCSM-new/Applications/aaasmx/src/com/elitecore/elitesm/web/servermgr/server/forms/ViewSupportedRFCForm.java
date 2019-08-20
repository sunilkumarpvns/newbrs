package com.elitecore.elitesm.web.servermgr.server.forms;


import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ViewSupportedRFCForm extends BaseWebForm{
	
	private String netServerId;
	//HashMap supportedRFC = new HashMap();
	private List supportedRFC;
	private String errorCode;


	
	

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getNetServerId() {
		return netServerId;
	}

	public void setNetServerId(String netServerId) {
		this.netServerId = netServerId;
	}

	public List getSupportedRFC() {
		return supportedRFC;
	}

	public void setSupportedRFC(List supportedRFC) {
		this.supportedRFC = supportedRFC;
	}

	

}
