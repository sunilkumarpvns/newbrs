package com.elitecore.netvertexsm.web.servermgr.server.form;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class ListUserfileDatasourceForm extends BaseWebForm {
	private String []userfiles;
	private String selectedFileName;

	public String[] getUserfiles() {
		return userfiles;
	}

	public void setUserfiles(String[] userfiles) {
		this.userfiles = userfiles;
	}

	public String getSelectedFileName() {
		return selectedFileName;
	}

	public void setSelectedFileName(String selectedFileName) {
		this.selectedFileName = selectedFileName;
	}
	

}
