package com.elitecore.elitesm.web.sessionmanager.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ViewSessionManagerForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;
	private String sessionManagerId;
	
	public String getSessionManagerId() {
		return sessionManagerId;
	}
	public void setSessionManagerId(String sessionManagerId) {
		this.sessionManagerId = sessionManagerId;
	}
	
}
