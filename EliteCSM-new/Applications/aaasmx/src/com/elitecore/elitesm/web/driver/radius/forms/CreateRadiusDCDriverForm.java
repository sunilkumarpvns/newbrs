package com.elitecore.elitesm.web.driver.radius.forms;

import java.util.List;

import com.elitecore.core.commons.tls.cipher.CipherSuites;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingTableData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateRadiusDCDriverForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private String translationMapConfigId;
	private List<TranslationMappingConfData> translationMappingConfDataList;
	private String action;
	private String driverInstanceName;
	private String driverDesp;
	private String driverRelatedId;
	private String disConnectUrl;
	
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
	public String getDriverDesp() {
		return driverDesp;
	}
	public void setDriverDesp(String driverDesp) {
		this.driverDesp = driverDesp;
	}
	public String getDriverRelatedId() {
		return driverRelatedId;
	}
	public void setDriverRelatedId(String driverRelatedId) {
		this.driverRelatedId = driverRelatedId;
	}
	public String getDisConnectUrl() {
		return disConnectUrl;
	}
	public void setDisConnectUrl(String disConnectUrl) {
		this.disConnectUrl = disConnectUrl;
	}
}
