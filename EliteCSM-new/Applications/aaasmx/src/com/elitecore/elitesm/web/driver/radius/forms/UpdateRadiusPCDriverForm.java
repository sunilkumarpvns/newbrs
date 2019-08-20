package com.elitecore.elitesm.web.driver.radius.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateRadiusPCDriverForm extends BaseWebForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long driverInstanceId;
	private String driverRelatedId;
	private Long translationMapConfigId;
	private Long pcDriverId;
	private String wsAddress;
	private String smServiceName;
	private String parleyServiceName;
	private String userName;
	private String password;
	private String action;
	private String driverInstanceName;
	private String driverDesp;
	private List<TranslationMappingConfData> translationMappingConfDataList;
	
	public long getDriverInstanceId() {
		return driverInstanceId;
	}
	
	public void setDriverInstanceId(long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	public String getDriverRelatedId() {
		return driverRelatedId;
	}
	
	public void setDriverRelatedId(String driverRelatedId) {
		this.driverRelatedId = driverRelatedId;
	}
	
	public Long getTranslationMapConfigId() {
		return translationMapConfigId;
	}
	
	public void setTranslationMapConfigId(Long translationMapConfigId) {
		this.translationMapConfigId = translationMapConfigId;
	}
	
	public Long getPcDriverId() {
		return pcDriverId;
	}
	
	public void setPcDriverId(Long pcDriverId) {
		this.pcDriverId = pcDriverId;
	}
	
	public String getWsAddress() {
		return wsAddress;
	}
	
	public void setWsAddress(String wsAddress) {
		this.wsAddress = wsAddress;
	}
	
	public String getSmServiceName() {
		return smServiceName;
	}
	
	public void setSmServiceName(String smServiceName) {
		this.smServiceName = smServiceName;
	}
	
	public String getParleyServiceName() {
		return parleyServiceName;
	}
	
	public void setParleyServiceName(String parleyServiceName) {
		this.parleyServiceName = parleyServiceName;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
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
	
	public List<TranslationMappingConfData> getTranslationMappingConfDataList() {
		return translationMappingConfDataList;
	}
	
	public void setTranslationMappingConfDataList(
			List<TranslationMappingConfData> translationMappingConfDataList) {
		this.translationMappingConfDataList = translationMappingConfDataList;
	}
}
