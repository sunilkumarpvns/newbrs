package com.elitecore.elitesm.web.servermgr.transmapconf.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateCrestelRatingTransMapConfigForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;
	private String name;
	private String action;
	private String dummyResponse="false";
	private String[] selectedDefaultMapping;
	private String baseTranslateConfigId;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String[] getSelectedDefaultMapping() {
		return selectedDefaultMapping;
	}

	public void setSelectedDefaultMapping(String[] selectedDefaultMapping) {
		this.selectedDefaultMapping = selectedDefaultMapping;
	}

	public String getDummyResponse() {
		return dummyResponse;
	}

	public void setDummyResponse(String dummyResponse) {
		this.dummyResponse = dummyResponse;
	}

	public String getBaseTranslateConfigId() {
		return baseTranslateConfigId;
	}

	public void setBaseTranslateConfigId(String baseTranslateConfigId) {
		this.baseTranslateConfigId = baseTranslateConfigId;
	}
	
	
}
