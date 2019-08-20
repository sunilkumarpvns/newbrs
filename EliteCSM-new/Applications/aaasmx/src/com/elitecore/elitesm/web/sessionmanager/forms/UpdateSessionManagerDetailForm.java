package com.elitecore.elitesm.web.sessionmanager.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISMDBFieldMapData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;
import com.elitecore.elitesm.web.servicepolicy.acct.forms.ExternalSystemBean;

public class UpdateSessionManagerDetailForm extends BaseWebForm {
	
	private static final long serialVersionUID = 1L;
	
	private String sminstanceid;
	private String action;
	private Set smConfigInstanceSet;
	
	/*
	 * local property
	 */
	private String smConfigInstanceId;
	private List<IDatabaseDSData> lstDatasource ;
	private String databaseId;
	private List<ISMDBFieldMapData> lstDBFieldMapData = new ArrayList<ISMDBFieldMapData>();
	private List<ISMDBFieldMapData> lstMandatoryFieldMapData = new ArrayList<ISMDBFieldMapData>();

	private String tablename;
	private String autosessioncloser;
	private Long sessionTimeout;
	private Long closeBatchCount;
	private Long sessionThreadSleeptime;
	
	private Integer sessionCloseAction;
	
	private String identityField;
	private String idSequenceName;
	private String startTimeField;
	private String lastUpdatedTimeField;
	private String sessionIdField;
	private String sessionIdRefEntity;
	private String groupNameField;
	private String serviceTypeField;
	private String concurrencyIdentityField;
	private String searchAttribute;
	
	private String dbFieldName;
	private String referenceEntity;
	private String referringEntity;
	private Integer dataType;
	private String defaultValue;
	private int itemIndex;
	private String dbField;
	
	private List<ExternalSystemBean> sessionManagerServerList;
	
	private List<ExternalSystemBean> nasClientList;
	private List<ExternalSystemBean> acctServerList;
	private String[] nasClients;
	private String[] acctServers;
	
	private String[] sessionMangerServers;
	private String batchUpdateEnabled;
	private Integer batchSize;
	private Integer batchUpdateInterval;
	private Integer dbQueryTimeOut;
	private Integer behaviour;
	private Integer sessionOverrideAction;
	private String sessionOverrideColumn;
	
	private List<TranslationMappingConfData> translationMappingConfDataList;
	private String dbfailureaction;
	
	private List<CopyPacketTranslationConfData> copyPacketMappingConfDataList;
	private String configId;
	private String sessionStopAction;
	
	public Integer getBehaviour() {
		return behaviour;
	}

	public void setBehaviour(Integer behaviour) {
		this.behaviour = behaviour;
	}

	public String getBatchUpdateEnabled() {
		return batchUpdateEnabled;
	}
	
	public void setBatchUpdateEnabled(String batchUpdateEnabled) {
		this.batchUpdateEnabled = batchUpdateEnabled;
	}

	public Integer getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	public Integer getBatchUpdateInterval() {
		return batchUpdateInterval;
	}

	public void setBatchUpdateInterval(Integer batchUpdateInterval) {
		this.batchUpdateInterval = batchUpdateInterval;
	}

	public Integer getDbQueryTimeOut() {
		return dbQueryTimeOut;
	}

	public void setDbQueryTimeOut(Integer dbQueryTimeOut) {
		this.dbQueryTimeOut = dbQueryTimeOut;
	}

	public String getSearchAttribute() {
		return searchAttribute;
	}
	
	public void setSearchAttribute(String searchAttributeField) {
		this.searchAttribute = searchAttributeField;
	}
	
	public String getSmConfigInstanceId() {
		return smConfigInstanceId;
	}
	public void setSmConfigInstanceId(String smConfigInstanceId) {
		this.smConfigInstanceId = smConfigInstanceId;
	}
	public String getSminstanceid() {
		return sminstanceid;
	}
	public void setSminstanceid(String sminstanceid) {
		this.sminstanceid = sminstanceid;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

	public Set getSmConfigInstanceSet() {
		return smConfigInstanceSet;
	}
	public void setSmConfigInstanceSet(Set smConfigInstanceSet) {
		this.smConfigInstanceSet = smConfigInstanceSet;
	}
	public List<IDatabaseDSData> getLstDatasource() {
		return lstDatasource;
	}
	public void setLstDatasource(List<IDatabaseDSData> lstDatasource) {
		this.lstDatasource = lstDatasource;
	}
	public String getDatabaseId() {
		return databaseId;
	}
	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}
	public List<ISMDBFieldMapData> getLstDBFieldMapData() {
		return lstDBFieldMapData;
	}
	public void setLstDBFieldMapData(List<ISMDBFieldMapData> lstDBFieldMapData) {
		this.lstDBFieldMapData = lstDBFieldMapData;
	}
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	public String getAutosessioncloser() {
		return autosessioncloser;
	}
	public void setAutosessioncloser(String autosessioncloser) {
		this.autosessioncloser = autosessioncloser;
	}
	public Long getSessionTimeout() {
		return sessionTimeout;
	}
	public void setSessionTimeout(Long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}
	public Long getCloseBatchCount() {
		return closeBatchCount;
	}
	public void setCloseBatchCount(Long closeBatchCount) {
		this.closeBatchCount = closeBatchCount;
	}
	public Long getSessionThreadSleeptime() {
		return sessionThreadSleeptime;
	}
	public void setSessionThreadSleeptime(Long sessionThreadSleeptime) {
		this.sessionThreadSleeptime = sessionThreadSleeptime;
	}
	public Integer getSessionCloseAction() {
		return sessionCloseAction;
	}
	public void setSessionCloseAction(Integer sessionCloseAction) {
		this.sessionCloseAction = sessionCloseAction;
	}
	
	public String getIdentityField() {
		return identityField;
	}
	public void setIdentityField(String identityField) {
		this.identityField = identityField;
	}
	public String getIdSequenceName() {
		return idSequenceName;
	}
	public void setIdSequenceName(String idSequenceName) {
		this.idSequenceName = idSequenceName;
	}
	public String getStartTimeField() {
		return startTimeField;
	}
	public void setStartTimeField(String startTimeField) {
		this.startTimeField = startTimeField;
	}
	public String getLastUpdatedTimeField() {
		return lastUpdatedTimeField;
	}
	public void setLastUpdatedTimeField(String lastUpdatedTimeField) {
		this.lastUpdatedTimeField = lastUpdatedTimeField;
	}
	public String getSessionIdField() {
		return sessionIdField;
	}
	public void setSessionIdField(String sessionIdField) {
		this.sessionIdField = sessionIdField;
	}
	public String getSessionIdRefEntity() {
		return sessionIdRefEntity;
	}
	public void setSessionIdRefEntity(String sessionIdRefEntity) {
		this.sessionIdRefEntity = sessionIdRefEntity;
	}
	public String getGroupNameField() {
		return groupNameField;
	}
	public void setGroupNameField(String groupNameField) {
		this.groupNameField = groupNameField;
	}
	public String getServiceTypeField() {
		return serviceTypeField;
	}
	public void setServiceTypeField(String serviceTypeField) {
		this.serviceTypeField = serviceTypeField;
	}
	public String getDbFieldName() {
		return dbFieldName;
	}
	public void setDbFieldName(String dbFieldName) {
		this.dbFieldName = dbFieldName;
	}
	public String getReferenceEntity() {
		return referenceEntity;
	}
	public void setReferenceEntity(String referenceEntity) {
		this.referenceEntity = referenceEntity;
	}
	public String getReferringEntity() {
		return referringEntity;
	}
	public void setReferringEntity(String referringEntity) {
		this.referringEntity = referringEntity;
	}
	public Integer getDataType() {
		return dataType;
	}
	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public int getItemIndex() {
		return itemIndex;
	}
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
	
	public List<ExternalSystemBean> getSessionManagerServerList() {
		return sessionManagerServerList;
	}
	public void setSessionManagerServerList(
			List<ExternalSystemBean> sessionManagerServerList) {
		this.sessionManagerServerList = sessionManagerServerList;
	}
	public String[] getSessionMangerServers() {
		return sessionMangerServers;
	}
	public void setSessionMangerServers(String[] sessionMangerServers) {
		this.sessionMangerServers = sessionMangerServers;
	}

	public List<ExternalSystemBean> getNasClientList() {
		return nasClientList;
	}

	public void setNasClientList(List<ExternalSystemBean> nasClientList) {
		this.nasClientList = nasClientList;
	}

	public List<ExternalSystemBean> getAcctServerList() {
		return acctServerList;
	}

	public void setAcctServerList(List<ExternalSystemBean> acctServerList) {
		this.acctServerList = acctServerList;
	}

	public String[] getNasClients() {
		return nasClients;
	}

	public void setNasClients(String[] nasClients) {
		this.nasClients = nasClients;
	}

	public String[] getAcctServers() {
		return acctServers;
	}

	public void setAcctServers(String[] acctServers) {
		this.acctServers = acctServers;
	}

	public Integer getSessionOverrideAction() {
		return sessionOverrideAction;
	}

	public void setSessionOverrideAction(Integer sessionOverrideAction) {
		this.sessionOverrideAction = sessionOverrideAction;
	}
	
	public String getSessionOverrideColumn() {
		return sessionOverrideColumn;
	}

	public void setSessionOverrideColumn(String sessionOverrideColumn) {
		this.sessionOverrideColumn = sessionOverrideColumn;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<TranslationMappingConfData> getTranslationMappingConfDataList() {
		return translationMappingConfDataList;
	}

	public void setTranslationMappingConfDataList(
			List<TranslationMappingConfData> translationMappingConfDataList) {
		this.translationMappingConfDataList = translationMappingConfDataList;
	}
	
	public List<ISMDBFieldMapData> getLstMandatoryFieldMapData() {
		return lstMandatoryFieldMapData;
	}

	public void setLstMandatoryFieldMapData(
			List<ISMDBFieldMapData> lstMandatoryFieldMapData) {
		this.lstMandatoryFieldMapData = lstMandatoryFieldMapData;
	}
	
	public String getDbField() {
		return dbField;
	}
	public void setDbField(String dbField) {
		this.dbField = dbField;
	}

	public String getDbfailureaction() {
		return dbfailureaction;
	}

	public void setDbfailureaction(String dbfailureaction) {
		this.dbfailureaction = dbfailureaction;
	}

	public List<CopyPacketTranslationConfData> getCopyPacketMappingConfDataList() {
		return copyPacketMappingConfDataList;
	}

	public void setCopyPacketMappingConfDataList(
			List<CopyPacketTranslationConfData> copyPacketMappingConfDataList) {
		this.copyPacketMappingConfDataList = copyPacketMappingConfDataList;
	}

	public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}

	public String getSessionStopAction() {
		return sessionStopAction;
	}

	public void setSessionStopAction(String sessionStopAction) {
		this.sessionStopAction = sessionStopAction;
	}

	public String getConcurrencyIdentityField() {
		return concurrencyIdentityField;
	}

	public void setConcurrencyIdentityField(String concurrencyIdentityField) {
		this.concurrencyIdentityField = concurrencyIdentityField;
	}
	
	
	
}
