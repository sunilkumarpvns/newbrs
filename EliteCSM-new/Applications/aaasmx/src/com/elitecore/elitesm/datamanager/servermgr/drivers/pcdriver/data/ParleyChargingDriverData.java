package com.elitecore.elitesm.datamanager.servermgr.drivers.pcdriver.data;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;



public class ParleyChargingDriverData extends BaseData{
	private String pcDriverId;
	private String driverInstanceId;
	private Long transMapConfId;
	private String wsAddress;
	private String smServiceName;
	private String parleyServiceName;
	private String userName;
	private String password;
	private TranslationMappingConfData translationMappingConfData;
	
	public TranslationMappingConfData getTranslationMappingConfData() {
		return translationMappingConfData;
	}

	public void setTranslationMappingConfData(
			TranslationMappingConfData translationMappingConfData) {
		this.translationMappingConfData = translationMappingConfData;
	}

	public String getPcDriverId() {
		return pcDriverId;
	}
	
	public void setPcDriverId(String pcDriverId) {
		this.pcDriverId = pcDriverId;
	}
	
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	public Long getTransMapConfId() {
		return transMapConfId;
	}
	
	public void setTransMapConfId(Long transMapConfId) {
		this.transMapConfId = transMapConfId;
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
}
