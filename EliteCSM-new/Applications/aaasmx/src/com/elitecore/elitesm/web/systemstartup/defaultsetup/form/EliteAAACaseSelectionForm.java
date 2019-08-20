package com.elitecore.elitesm.web.systemstartup.defaultsetup.form;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class EliteAAACaseSelectionForm extends BaseWebForm{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String caseSensitivityForSubscriber;
	private String caseSensitivityForPolicy;
	
	public String getCaseSensitivityForSubscriber() {
		return caseSensitivityForSubscriber;
	}
	public void setCaseSensitivityForSubscriber(String caseSensitivityForSubscriber) {
		this.caseSensitivityForSubscriber = caseSensitivityForSubscriber;
	}
	public String getCaseSensitivityForPolicy() {
		return caseSensitivityForPolicy;
	}
	public void setCaseSensitivityForPolicy(String caseSensitivityForPolicy) {
		this.caseSensitivityForPolicy = caseSensitivityForPolicy;
	}
}
