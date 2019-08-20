package com.elitecore.elitesm.web.tableorder.form;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class TableOrderForm extends BaseWebForm {
	
	private static final long serialVersionUID = 1L;
	private String[] ids;
	private String className;
	private String responseUrl;
	
	public String[] getIds() {
		return ids;
	}
	public void setIds(String[] ids) {
		this.ids = ids;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getResponseUrl() {
		return responseUrl;
	}
	public void setResponseUrl(String responseUrl) {
		this.responseUrl = responseUrl;
	}
	
}
