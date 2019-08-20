package com.elitecore.elitesm.web.driver.radius.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.drivers.chargingdriver.data.CrestelChargingDriverPropsData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CrestelOCSv2DriverForm  extends BaseWebForm{

	private static final long serialVersionUID = 1L;
	private String action;
	private String driverInstanceId;
	private String driverInstanceName;
	private String driverInstanceDesp;
	private String driverRelatedId;
	private String translationMapConfigId;
	private Integer instanceNumber = 5;
	private List<TranslationMappingConfData> translationMappingConfDataList;
	private List<CrestelChargingDriverPropsData> defaultChargingDriverPropsDataList;
	private String auditUId;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
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
	public Integer getInstanceNumber() {
		return instanceNumber;
	}
	public void setInstanceNumber(Integer instanceNumber) {
		this.instanceNumber = instanceNumber;
	}
	public List<TranslationMappingConfData> getTranslationMappingConfDataList() {
		return translationMappingConfDataList;
	}
	public void setTranslationMappingConfDataList(
			List<TranslationMappingConfData> translationMappingConfDataList) {
		this.translationMappingConfDataList = translationMappingConfDataList;
	}
	public List<CrestelChargingDriverPropsData> getDefaultChargingDriverPropsDataList() {
		return defaultChargingDriverPropsDataList;
	}
	public void setDefaultChargingDriverPropsDataList(
			List<CrestelChargingDriverPropsData> defaultChargingDriverPropsDataList) {
		this.defaultChargingDriverPropsDataList = defaultChargingDriverPropsDataList;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	
}
