package com.elitecore.elitesm.web.servermgr.transmapconf.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslatorTypeData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateTranslationMappingConfigForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;
	
	private String action;
	private String name;
	private String description;
	private String selectedFromTranslatorType;
	private String selectedToTranslatorType;
	private TranslatorTypeData selectedFromTranslatorTypeData;
	private TranslatorTypeData selectedToTranslatorTypeData;
	private String script;
	
	private List<TranslatorTypeData> fromTranslatorTypeList;
	private List<TranslatorTypeData> toTranslatorTypeList;

	private List<TranslationMappingConfData> baseTranslationMappingConfDataList;
	private List<ScriptInstanceData> scriptDataList;

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

	public String getSelectedFromTranslatorType() {
		return selectedFromTranslatorType;
	}

	public void setSelectedFromTranslatorType(String selectedFromTranslatorType) {
		this.selectedFromTranslatorType = selectedFromTranslatorType;
	}

	public String getSelectedToTranslatorType() {
		return selectedToTranslatorType;
	}

	public void setSelectedToTranslatorType(String selectedToTranslatorType) {
		this.selectedToTranslatorType = selectedToTranslatorType;
	}

	public List<TranslatorTypeData> getFromTranslatorTypeList() {
		return fromTranslatorTypeList;
	}

	public void setFromTranslatorTypeList(
			List<TranslatorTypeData> fromTranslatorTypeList) {
		this.fromTranslatorTypeList = fromTranslatorTypeList;
	}

	public List<TranslatorTypeData> getToTranslatorTypeList() {
		return toTranslatorTypeList;
	}

	public void setToTranslatorTypeList(
			List<TranslatorTypeData> toTranslatorTypeList) {
		this.toTranslatorTypeList = toTranslatorTypeList;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TranslatorTypeData getSelectedFromTranslatorTypeData() {
		return selectedFromTranslatorTypeData;
	}

	public void setSelectedFromTranslatorTypeData(
			TranslatorTypeData selectedFromTranslatorTypeData) {
		this.selectedFromTranslatorTypeData = selectedFromTranslatorTypeData;
	}

	public TranslatorTypeData getSelectedToTranslatorTypeData() {
		return selectedToTranslatorTypeData;
	}

	public void setSelectedToTranslatorTypeData(TranslatorTypeData selectedToTranslatorTypeData) {
		this.selectedToTranslatorTypeData = selectedToTranslatorTypeData;
	}


	public List<TranslationMappingConfData> getBaseTranslationMappingConfDataList() {
		return baseTranslationMappingConfDataList;
	}

	public void setBaseTranslationMappingConfDataList(
			List<TranslationMappingConfData> baseTranslationMappingConfDataList) {
		this.baseTranslationMappingConfDataList = baseTranslationMappingConfDataList;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public List<ScriptInstanceData> getScriptDataList() {
		return scriptDataList;
	}

	public void setScriptDataList(List<ScriptInstanceData> scriptDataList) {
		this.scriptDataList = scriptDataList;
	}
}
