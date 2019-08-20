package com.elitecore.elitesm.web.sessionmanager.forms;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISMDBFieldMapData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CreateSessionManagerDetailForm extends BaseWebForm {
	
	private static final long serialVersionUID = 1L;
	
	//datasource info
	private List<IDatabaseDSData> lstDatasource = null;
	private String databaseId;
	private List<ISMDBFieldMapData> lstDBFieldMapData = new ArrayList<ISMDBFieldMapData>();
	private List<ISMDBFieldMapData> lstMandatoryFieldMapData = new ArrayList<ISMDBFieldMapData>();
	private String tablename="tblmconcurrentusers";
	private String autosessioncloser="false";
	private Long sessionTimeout=120L;
	private Long closeBatchCount=50L;
	private Long sessionThreadSleeptime=10L;
	
	private Integer sessionCloseAction=3;
	
	private String idSequenceName="SEQ_TBLMCONCURRENTUSERS";
	private String startTimeField="START_TIME";
	private String lastUpdatedTimeField="LAST_UPDATED_TIME";
	
	
	private String sessionIdRefEntity="0:44";
	private String groupNameField="GROUPNAME";
	private String serviceTypeField="NAS_PORT_TYPE";
	
	private String field;
	private String dbField;
	private String referingAttrib;
	private String mandatoryFieldDataType;
	private String defaultVal;
	
	private String dbFieldName;
	private String referenceEntity;
	private String referringEntity;
	private Integer dataType;
	private String defaultValue;
	private String checkAction;
	private int itemIndex;
	private String concurrencyIdentityField="GROUPNAME";
	private String searchAttribute;
	private String batchUpdateEnabled;
	private Integer batchSize=1000;
	private Integer batchUpdateInterval=100;
	private Integer dbQueryTimeOut=1;
	private Integer behaviour;
	private Integer sessionOverrideAction=0;
	private String sessionOverrideColumn="CALLING_STATION_ID";
	private String dbfailureaction;
	private String sessionStopAction;
	
	public Integer getSessionOverrideAction() {
		return sessionOverrideAction;
	}
	public void setSessionOverrideAction(Integer sessionOverrideAction) {
		this.sessionOverrideAction = sessionOverrideAction;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
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
	public String getCheckAction() {
		return checkAction;
	}
	public void setCheckAction(String checkAction) {
		this.checkAction = checkAction;
	}
	public int getItemIndex() {
		return itemIndex;
	}
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}

	public List<IDatabaseDSData> getLstDatasource() {
		return lstDatasource;
	}
	public void setLstDatasource(List<IDatabaseDSData> lstDatasource) {
		this.lstDatasource = lstDatasource;
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
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
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
	
	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}
	public String getDatabaseId() {
		return databaseId;
	}
	public Integer getDataType() {
		return dataType;
	}
	
	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}
	public void setLstDBFieldMapData(List<ISMDBFieldMapData> lstDBFieldMapData) {
		this.lstDBFieldMapData = lstDBFieldMapData;
	}
	public List<ISMDBFieldMapData> getLstDBFieldMapData() {
		return lstDBFieldMapData;
	}
	
	public String getSessionOverrideColumn() {
		return sessionOverrideColumn;
	}
	public void setSessionOverrideColumn(String sessionOverrideColumn) {
		this.sessionOverrideColumn = sessionOverrideColumn;
	}
	public List<ISMDBFieldMapData> getLstMandatoryFieldMapData() {
		return lstMandatoryFieldMapData;
	}
	public void setLstMandatoryFieldMapData(
			List<ISMDBFieldMapData> lstMandatoryFieldMapData) {
		this.lstMandatoryFieldMapData = lstMandatoryFieldMapData;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getDbField() {
		return dbField;
	}
	public void setDbField(String dbField) {
		this.dbField = dbField;
	}
	public String getReferingAttrib() {
		return referingAttrib;
	}
	public void setReferingAttrib(String referingAttrib) {
		this.referingAttrib = referingAttrib;
	}
	public String getMandatoryFieldDataType() {
		return mandatoryFieldDataType;
	}
	public void setMandatoryFieldDataType(String mandatoryFieldDataType) {
		this.mandatoryFieldDataType = mandatoryFieldDataType;
	}
	public String getDefaultVal() {
		return defaultVal;
	}
	public void setDefaultVal(String defaultVal) {
		this.defaultVal = defaultVal;
	}
	public String getDbfailureaction() {
		return dbfailureaction;
	}
	public void setDbfailureaction(String dbfailureaction) {
		this.dbfailureaction = dbfailureaction;
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
