package com.elitecore.elitesm.web.driver.diameter.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerRelData;
import com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data.HssAuthDriverFieldMapData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthFieldMapData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.httpdriver.data.HttpAuthDriverFieldMapData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateDiameterHSSAuthDriverForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;
	
	private List<LogicalNameValuePoolData> logicalNameList;
	
	private String hssauthdriverid;
	private String userIdentityAttributes;
	private String applicationid;
	private Long requesttimeout;
	private Long commandCode;
	private String driverInstanceId;
	private Long noOfTriplets;
	private String additionalAttributes;
	
	private String name;

 	private int itemIndex;
	private int count;
	private String dbFiled;
	
	//private Set httpFeildMapSet;
	private String logicalName;
	private Long responseParamIndex;
	private String action;
	private String expiryDateFormats="MM/dd/yyyy";
	
	private String defaultValue;
	private String valueMapping;
	private String driverInstanceName;
	private String driverDesp;
	private String driverRelatedId;
	private List<String> logicalNameMultipleAllowList = new ArrayList<String>(0);
	private List<TranslationMappingConfData> translationMappingList;
	private String cacheable;
	private List<DiameterPeerData> diameterPeerDatas;
	private List<DiameterPeerRelData> diameterPeerRelDataList;
	private List<HssAuthDriverFieldMapData> hssAuthDriverFieldMapDataList;
	
	private String driverInstanceDesc;
	private String auditUId;
	
	public List<LogicalNameValuePoolData> getLogicalNameList() {
		return logicalNameList;
	}
	public void setLogicalNameList(List<LogicalNameValuePoolData> logicalNameList) {
		this.logicalNameList = logicalNameList;
	}
	public String getHssauthdriverid() {
		return hssauthdriverid;
	}
	public void setHssauthdriverid(String hssauthdriverid) {
		this.hssauthdriverid = hssauthdriverid;
	}
	public String getUserIdentityAttributes() {
		return userIdentityAttributes;
	}
	public void setUserIdentityAttributes(String userIdentityAttributes) {
		this.userIdentityAttributes = userIdentityAttributes;
	}
	public String getApplicationid() {
		return applicationid;
	}
	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
	}
	public Long getRequesttimeout() {
		return requesttimeout;
	}
	public void setRequesttimeout(Long requesttimeout) {
		this.requesttimeout = requesttimeout;
	}
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getItemIndex() {
		return itemIndex;
	}
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getDbFiled() {
		return dbFiled;
	}
	public void setDbFiled(String dbFiled) {
		this.dbFiled = dbFiled;
	}
	public String getLogicalName() {
		return logicalName;
	}
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	public Long getResponseParamIndex() {
		return responseParamIndex;
	}
	public void setResponseParamIndex(Long responseParamIndex) {
		this.responseParamIndex = responseParamIndex;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getExpiryDateFormats() {
		return expiryDateFormats;
	}
	public void setExpiryDateFormats(String expiryDateFormats) {
		this.expiryDateFormats = expiryDateFormats;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getValueMapping() {
		return valueMapping;
	}
	public void setValueMapping(String valueMapping) {
		this.valueMapping = valueMapping;
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
	
	public List<HssAuthDriverFieldMapData> getDefaultmapping(){
		List<HssAuthDriverFieldMapData> defaultMappingList = new ArrayList<HssAuthDriverFieldMapData>();
		String[] logicalnm = {"User Name","IMSI","MSISDN"};
		String[] fieldMap = {"0:1","10415:1","10415:701"};
		String[] logicalv = {"User-Name","IMSI","MSISDN"};
		for(int index = 0 ; index < logicalnm.length ; index++){
			HssAuthDriverFieldMapData hhsFieldMapData = new HssAuthDriverFieldMapData();
			LogicalNameValuePoolData nameValuePoolData = new LogicalNameValuePoolData();
			nameValuePoolData.setName(logicalnm[index]);
			nameValuePoolData.setValue(logicalv[index]);
			hhsFieldMapData.setNameValuePoolData(nameValuePoolData);
			hhsFieldMapData.setAttributeIds(fieldMap[index]);
			defaultMappingList.add(hhsFieldMapData);
		}
		return defaultMappingList;
	}
	public List<String> getLogicalNameMultipleAllowList() {
		return logicalNameMultipleAllowList;
	}
	public void setLogicalNameMultipleAllowList(
			List<String> logicalNameMultipleAllowList) {
		this.logicalNameMultipleAllowList = logicalNameMultipleAllowList;
	}
	public String getCacheable() {
		return cacheable;
	}
	public void setCacheable(String cacheable) {
		this.cacheable = cacheable;
	}
	public List<TranslationMappingConfData> getTranslationMappingList() {
		return translationMappingList;
	}
	public void setTranslationMappingList(List<TranslationMappingConfData> translationMappingList) {
		this.translationMappingList = translationMappingList;
	}
	public Long getCommandCode() {
		return commandCode;
	}
	public void setCommandCode(Long commandCode) {
		this.commandCode = commandCode;
	}
	public List<DiameterPeerData> getDiameterPeerDatas() {
		return diameterPeerDatas;
	}
	public void setDiameterPeerDatas(List<DiameterPeerData> diameterPeerDatas) {
		this.diameterPeerDatas = diameterPeerDatas;
	}
	public Long getNoOfTriplets() {
		return noOfTriplets;
	}
	public void setNoOfTriplets(Long noOfTriplets) {
		this.noOfTriplets = noOfTriplets;
	}
	public String getAdditionalAttributes() {
		return additionalAttributes;
	}
	public void setAdditionalAttributes(String additionalAttributes) {
		this.additionalAttributes = additionalAttributes;
	}
	public String getDriverInstanceDesc() {
		return driverInstanceDesc;
	}
	public void setDriverInstanceDesc(String driverInstanceDesc) {
		this.driverInstanceDesc = driverInstanceDesc;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	public List<DiameterPeerRelData> getDiameterPeerRelDataList() {
		return diameterPeerRelDataList;
	}
	public void setDiameterPeerRelDataList(List<DiameterPeerRelData> diameterPeerRelDataList) {
		this.diameterPeerRelDataList = diameterPeerRelDataList;
	}
	public List<HssAuthDriverFieldMapData> getHssAuthDriverFieldMapDataList() {
		return hssAuthDriverFieldMapDataList;
	}
	public void setHssAuthDriverFieldMapDataList(
			List<HssAuthDriverFieldMapData> hssAuthDriverFieldMapDataList) {
		this.hssAuthDriverFieldMapDataList = hssAuthDriverFieldMapDataList;
	}
	
}
