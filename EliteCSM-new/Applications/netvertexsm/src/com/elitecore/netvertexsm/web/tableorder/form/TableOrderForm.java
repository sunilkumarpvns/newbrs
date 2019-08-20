package com.elitecore.netvertexsm.web.tableorder.form;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class TableOrderForm extends BaseWebForm {
	
	private static final long serialVersionUID = 1L;
	private Long[] ids;
	private String className;
	private String responseUrl;
	
	public Long[] getIds() {
		return ids;
	}
	public void setIds(Long[] ids) {
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
