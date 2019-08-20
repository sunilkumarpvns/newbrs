package com.elitecore.netvertexsm.web.core.system.help.forms;

import java.util.List;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;


public class HelpForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;

	List<String> lstHelpFile = null;

	public List<String> getLstHelpFile() {
		return lstHelpFile;
	}

	public void setLstHelpFile(List<String> lstHelpFile) {
		this.lstHelpFile = lstHelpFile;
	}

	
}
