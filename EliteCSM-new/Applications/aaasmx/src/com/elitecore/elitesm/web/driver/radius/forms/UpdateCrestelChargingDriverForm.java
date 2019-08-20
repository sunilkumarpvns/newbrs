package com.elitecore.elitesm.web.driver.radius.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateCrestelChargingDriverForm extends BaseWebForm {
	private static final long serialVersionUID = 1L;
	private String driverInstanceId;
	private String action;
	private String driverInstanceName;
	private String driverInstanceDesp;
	private String driverRelatedId;
	private String translationMapConfigId;
	private Integer instanceNumber;
	private List<TranslationMappingConfData> translationMappingConfDataList;
	private TranslationMappingConfData selectedTranslationMappingConfData;
	private String auditUId;
	
	public Integer getInstanceNumber() {
		return instanceNumber;
	}
	
	public void setInstanceNumber(Integer instanceNumber) {
		this.instanceNumber = instanceNumber;
	}
	
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public String getDriverInstanceName() {
		return driverInstanceName;
	}
	
	public void setDriverInstanceName(String driverInstanceName) {
		this.driverInstanceName = driverInstanceName;
	}
	
	public String getDriverInstanceDesp() {
		return driverInstanceDesp;
	}
	
	public void setDriverInstanceDesp(String driverInstanceDesp) {
		this.driverInstanceDesp = driverInstanceDesp;
	}
	
	public String getDriverRelatedId() {
		return driverRelatedId;
	}
	
	public void setDriverRelatedId(String driverRelatedId) {
		this.driverRelatedId = driverRelatedId;
	}
	
	public String getTranslationMapConfigId() {
		return translationMapConfigId;
	}
	
	public void setTranslationMapConfigId(String translationMapConfigId) {
		this.translationMapConfigId = translationMapConfigId;
	}
	
	public List<TranslationMappingConfData> getTranslationMappingConfDataList() {
		return translationMappingConfDataList;
	}
	
	public void setTranslationMappingConfDataList(
			List<TranslationMappingConfData> translationMappingConfDataList) {
		this.translationMappingConfDataList = translationMappingConfDataList;
	}
	
	public TranslationMappingConfData getSelectedTranslationMappingConfData() {
		return selectedTranslationMappingConfData;
	}
	
	public void setSelectedTranslationMappingConfData(TranslationMappingConfData selectedTranslationMappingConfData) {
		this.selectedTranslationMappingConfData = selectedTranslationMappingConfData;
	}

	public String getAuditUId() {
		return auditUId;
	}

	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
}
