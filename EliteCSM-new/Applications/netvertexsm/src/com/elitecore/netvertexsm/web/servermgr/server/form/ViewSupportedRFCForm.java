package com.elitecore.netvertexsm.web.servermgr.server.form;


import java.util.List;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class ViewSupportedRFCForm extends BaseWebForm{
	
	private Long netServerId;
	//HashMap supportedRFC = new HashMap();
	private List supportedRFC;
	private String errorCode;


	
	

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public Long getNetServerId() {
		return netServerId;
	}

	public void setNetServerId(Long netServerId) {
		this.netServerId = netServerId;
	}

	public List getSupportedRFC() {
		return supportedRFC;
	}

	public void setSupportedRFC(List supportedRFC) {
		this.supportedRFC = supportedRFC;
	}

	

}
