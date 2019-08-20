package com.elitecore.netvertexsm.web.servermgr.server.form;

import java.util.List;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class SearchDictionaryPopUpForm extends BaseWebForm{
	
	List dictionaryListInCombo;
	List dictionaryListByCriteria;
	String searchByName;
	long dictionaryId;
	String status;
	String action;
	String fieldValue;
	

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getDictionaryId() {
		return dictionaryId;
	}

	public void setDictionaryId(long dictionaryId) {
		this.dictionaryId = dictionaryId;
	}

	public List getDictionaryListInCombo() {
		return dictionaryListInCombo;
	}

	public void setDictionaryListInCombo(List dictionaryListInCombo) {
		this.dictionaryListInCombo = dictionaryListInCombo;
	}

	public String getSearchByName() {
		return searchByName;
	}

	public void setSearchByName(String searchByName) {
		this.searchByName = searchByName;
	}

	public List getDictionaryListByCriteria() {
		return dictionaryListByCriteria;
	}

	public void setDictionaryListByCriteria(List dictionaryListByCriteria) {
		this.dictionaryListByCriteria = dictionaryListByCriteria;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
