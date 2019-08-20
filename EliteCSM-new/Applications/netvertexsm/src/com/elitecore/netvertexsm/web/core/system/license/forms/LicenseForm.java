package com.elitecore.netvertexsm.web.core.system.license.forms;

import java.util.List;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;


public class LicenseForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;

	List<String> lstLicenseFile = null;

	public List<String> getLstLicenseFile() {
		return lstLicenseFile;
	}

	public void setLstLicenseFile(List<String> lstLicenseFile) {
		this.lstLicenseFile = lstLicenseFile;
	}

}
