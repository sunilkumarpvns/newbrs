package com.elitecore.elitesm.web.diameter.sessionmanager.form;

import java.util.List;
import java.util.Set;

import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerMappingData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.ScenarioMappingData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.SessionOverideActionData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ViewDiameterSessionManagerForm extends BaseWebForm{
	/**
	 * @author nayana.rathod
	 */
	private static final long serialVersionUID = 1L;
	
	/*Basic Information*/
	private String sessionManagerId;
	private String name;
	private Long databaseId;
	private String description;
	private String tablename;
	private String sequenceName;
	private String startTimeField;
	private String lastUpdatedTimeField;
	private Long dbQueryTimeOut;
	private String multiValueDelimeter;
	private String dbFailureAction;
	private boolean batchEnabled;
	private Long batchSize;
	private Long batchInterval;
	private Long batchQueryTimeout;
	private boolean batchedInsert;
	private boolean batchedUpdate;
	private boolean batchedDelete;
	private String action;
	private String databaseDataSourceName;
	/*Mapping Information*/
	private Set<DiameterSessionManagerMappingData> diameterSessionManagerMappingDataSet;
	
	/*Scenario Information*/
	private Set<ScenarioMappingData> scenarioMappingDataSet;
	
	/*Session Override Action*/
	private Set<SessionOverideActionData> sessionOverideActionDataSet; 
	
	/*Other Required Properties for Diameter Session Manager*/
	private List<IDatabaseDSData> lstDatasource;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getDatabaseId() {
		return databaseId;
	}
	public void setDatabaseId(Long databaseId) {
		this.databaseId = databaseId;
	}
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	public Long getDbQueryTimeOut() {
		return dbQueryTimeOut;
	}
	public void setDbQueryTimeOut(Long dbQueryTimeOut) {
		this.dbQueryTimeOut = dbQueryTimeOut;
	}
	public String getMultiValueDelimeter() {
		return multiValueDelimeter;
	}
	public void setMultiValueDelimeter(String multiValueDelimeter) {
		this.multiValueDelimeter = multiValueDelimeter;
	}
	public String getDbFailureAction() {
		return dbFailureAction;
	}
	public void setDbFailureAction(String dbFailureAction) {
		this.dbFailureAction = dbFailureAction;
	}
	public boolean isBatchEnabled() {
		return batchEnabled;
	}
	public void setBatchEnabled(boolean batchEnabled) {
		this.batchEnabled = batchEnabled;
	}
	public Long getBatchSize() {
		return batchSize;
	}
	public void setBatchSize(Long batchSize) {
		this.batchSize = batchSize;
	}
	public Long getBatchInterval() {
		return batchInterval;
	}
	public void setBatchInterval(Long batchInterval) {
		this.batchInterval = batchInterval;
	}
	public Long getBatchQueryTimeout() {
		return batchQueryTimeout;
	}
	public void setBatchQueryTimeout(Long batchQueryTimeout) {
		this.batchQueryTimeout = batchQueryTimeout;
	}
	public boolean isBatchedInsert() {
		return batchedInsert;
	}
	public void setBatchedInsert(boolean batchedInsert) {
		this.batchedInsert = batchedInsert;
	}
	public boolean isBatchedUpdate() {
		return batchedUpdate;
	}
	public void setBatchedUpdate(boolean batchedUpdate) {
		this.batchedUpdate = batchedUpdate;
	}
	public boolean isBatchedDelete() {
		return batchedDelete;
	}
	public void setBatchedDelete(boolean batchedDelete) {
		this.batchedDelete = batchedDelete;
	}
	
	@Override
	public String toString() {
		return "ViewDiameterSessionManagerForm [name=" + name + ", databaseId="
				+ databaseId + ", tablename=" + tablename + ", dbQueryTimeOut="
				+ dbQueryTimeOut + ", multiValueDelimeter="
				+ multiValueDelimeter + ", dbFailureAction=" + dbFailureAction
				+ ", batchEnabled=" + batchEnabled + ", batchSize=" + batchSize
				+ ", batchInterval=" + batchInterval + ", batchQueryTimeout="
				+ batchQueryTimeout + ", batchedInsert=" + batchedInsert
				+ ", batchedUpdate=" + batchedUpdate + ", batchedDelete="
				+ batchedDelete + "]";
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<IDatabaseDSData> getLstDatasource() {
		return lstDatasource;
	}
	public void setLstDatasource(List<IDatabaseDSData> lstDatasource) {
		this.lstDatasource = lstDatasource;
	}
	public String getSequenceName() {
		return sequenceName;
	}
	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
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
	public Set<DiameterSessionManagerMappingData> getDiameterSessionManagerMappingDataSet() {
		return diameterSessionManagerMappingDataSet;
	}
	public void setDiameterSessionManagerMappingDataSet(
			Set<DiameterSessionManagerMappingData> diameterSessionManagerMappingDataSet) {
		this.diameterSessionManagerMappingDataSet = diameterSessionManagerMappingDataSet;
	}
	public Set<ScenarioMappingData> getScenarioMappingDataSet() {
		return scenarioMappingDataSet;
	}
	public void setScenarioMappingDataSet(Set<ScenarioMappingData> scenarioMappingDataSet) {
		this.scenarioMappingDataSet = scenarioMappingDataSet;
	}
	public Set<SessionOverideActionData> getSessionOverideActionDataSet() {
		return sessionOverideActionDataSet;
	}
	public void setSessionOverideActionDataSet(
			Set<SessionOverideActionData> sessionOverideActionDataSet) {
		this.sessionOverideActionDataSet = sessionOverideActionDataSet;
	}
	public String getSessionManagerId() {
		return sessionManagerId;
	}
	public void setSessionManagerId(String sessionManagerId) {
		this.sessionManagerId = sessionManagerId;
	}
	public String getDatabaseDataSourceName() {
		return databaseDataSourceName;
	}
	public void setDatabaseDataSourceName(String databaseDataSourceName) {
		this.databaseDataSourceName = databaseDataSourceName;
	}
}
