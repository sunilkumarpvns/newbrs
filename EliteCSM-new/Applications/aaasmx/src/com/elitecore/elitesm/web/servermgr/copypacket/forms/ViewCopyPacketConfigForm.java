package com.elitecore.elitesm.web.servermgr.copypacket.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslatorTypeData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ViewCopyPacketConfigForm extends BaseWebForm{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String action;
	private String name;
	private String description;
	private String selectedFromTranslatorType;
	private String selectedToTranslatorType;
	private String dummyResponse;
	private String copyPacketTransConfId;
	
	private List<TranslatorTypeData> fromTranslatorTypeList;
	private List<TranslatorTypeData> toTranslatorTypeList;
	
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

	public String getDummyResponse() {
		return dummyResponse;
	}

	public void setDummyResponse(String dummyResponse) {
		this.dummyResponse = dummyResponse;
	}

	public String getCopyPacketTransConfId() {
		return copyPacketTransConfId;
	}

	public void setCopyPacketTransConfId(String copyPacketTransConfId) {
		this.copyPacketTransConfId = copyPacketTransConfId;
	}

	
	
}
