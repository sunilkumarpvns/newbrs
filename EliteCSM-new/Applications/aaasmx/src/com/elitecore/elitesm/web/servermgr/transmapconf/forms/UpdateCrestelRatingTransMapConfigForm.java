package com.elitecore.elitesm.web.servermgr.transmapconf.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateCrestelRatingTransMapConfigForm  extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private String action;
	private String dummyResponse;
	private String translationMapConfigId;
	private TranslationMappingConfData translationMappingConfData;

	private String baseTranslateConfigId;
	private List<TranslationMappingConfData> baseTranslationMappingConfDataList;
	 
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getDummyResponse() {
		return dummyResponse;
	}

	public void setDummyResponse(String dummyResponse) {
		this.dummyResponse = dummyResponse;
	}

	public String getTranslationMapConfigId() {
		return translationMapConfigId;
	}

	public void setTranslationMapConfigId(String translationMapConfigId) {
		this.translationMapConfigId = translationMapConfigId;
	}

	public TranslationMappingConfData getTranslationMappingConfData() {
		return translationMappingConfData;
	}

	public void setTranslationMappingConfData(TranslationMappingConfData translationMappingConfData) {
		this.translationMappingConfData = translationMappingConfData;
	}

	public String getBaseTranslateConfigId() {
		return baseTranslateConfigId;
	}

	public void setBaseTranslateConfigId(String baseTranslateConfigId) {
		this.baseTranslateConfigId = baseTranslateConfigId;
	}

	public List<TranslationMappingConfData> getBaseTranslationMappingConfDataList() {
		return baseTranslationMappingConfDataList;
	}

	public void setBaseTranslationMappingConfDataList(
			List<TranslationMappingConfData> baseTranslationMappingConfDataList) {
		this.baseTranslationMappingConfDataList = baseTranslationMappingConfDataList;
	}
	
	
	
}
