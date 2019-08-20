package com.elitecore.elitesm.datamanager.sessionmanager.data;

import java.util.List;

public interface ISMConfigInstanceData{
	
	public String getSmConfigId();
	public void setSmConfigId(String smConfigId);
	
	public String getSmInstanceId();
	public void setSmInstanceId(String smInstanceId);
	
	public String getDatabaseDatasourceId();
	public void setDatabaseDatasourceId(String databaseDatasourceId);
	
	public String getTablename();
	public void setTablename(String tablename) ;
	
	public String getAutoSessionCloser() ;
	public void setAutoSessionCloser(String autoSessionCloser);
	
	public Long getSessiontimeout() ;
	public void setSessiontimeout(Long sessiontimeout) ;
	
	public Long getCloseBatchCount();
	public void setCloseBatchCount(Long closeBatchCount) ;
	
	public Long getSessionThreadSleepTime();
	public void setSessionThreadSleepTime(Long sessionThreadSleepTime) ;
	
	public Integer getSessionCloseAction();
	public void setSessionCloseAction(Integer sessionCloseAction);
	
	public String getIdentityField();
	public void setIdentityField(String identityField);
	
	public String getIdSequenceName();
	public void setIdSequenceName(String idSequenceName);
	
	public String getStartTimeField();
	public void setStartTimeField(String startTimeField);
	
	public String getLastUpdatedTimeField();
	public void setLastUpdatedTimeField(String lastUpdatedTimeField);
	
	public String getSessionIdField();
	public void setSessionIdField(String sessionIdField);
	
	public String getSessionIdRefEntity();
	public void setSessionIdRefEntity(String sessionIdRefEntity);
	
	public String getGroupNameField();
	public void setGroupNameField(String groupNameField);
	
	public String getServiceTypeField();
	public void setServiceTypeField(String serviceTypeField);
	
	public String getSearchAttribute();
	public void setSearchAttribute(String searchAttribute);
	
	public List<SMDBFieldMapData> getDbFieldMapDataList();
	public void setDbFieldMapDataList(List<SMDBFieldMapData> dbFieldMapDataList);
	
	public List<SMDBFieldMapData> getLstMandatoryFieldMapData();
	public void setLstMandatoryFieldMapData(List<SMDBFieldMapData> lstMandatoryFieldMapData);
	
	public List<SMSessionCloserESIRelData> getSmSessionCloserESIRelDataList();
	public void setSmSessionCloserESIRelDataList(List<SMSessionCloserESIRelData> smSessionCloserESIRelDataList);
	
	public String getBatchUpdateEnabled();
	public void setBatchUpdateEnabled(String batchUpdateEnabled);
	
	public Integer getBatchSize();
	public void setBatchSize(Integer batchSize);
	
	public Integer getBatchUpdateInterval();
	public void setBatchUpdateInterval(Integer batchUpdateInterval);
	
	public Integer getDbQueryTimeOut();
	public void setDbQueryTimeOut(Integer dbQueryTimeOut);
	
	public Integer getBehaviour();
	public void setBehaviour(Integer behaviour);
	
	public Integer getSessionOverrideAction();
	public void setSessionOverrideAction(Integer sessionOverrideAction);
	
	public String getSessionOverrideColumn();
	public void setSessionOverrideColumn(String sessionOverrideColumn);
	
	public String getDbfailureaction();
	public void setDbfailureaction(String dbfailureaction);
	
	public String getSessionStopAction();
	public void setSessionStopAction(String sessionStopAction);
	
	public String getConcurrencyIdentityField();
	public void setConcurrencyIdentityField(String concurrencyIdentityField);
}
