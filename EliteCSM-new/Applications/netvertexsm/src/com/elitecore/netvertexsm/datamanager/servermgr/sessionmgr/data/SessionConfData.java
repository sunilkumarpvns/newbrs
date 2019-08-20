package com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data;

import java.util.List;
import java.util.Set;

import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.web.core.base.BaseData;

public class SessionConfData extends BaseData{
	
	private int sessionConfID;
	private Integer dataSourceID;
	private Integer secondaryDataSourceID;
	private String batchUpdate;
	private int batchSize;
	private int batchUpdateInterval;
	private int dbQueryTimeout;
	private Set<SessionFieldMapData> sessionFieldMapDataset;
	private List<SessionFieldMapData> coreSessionList;
	private List<SessionFieldMapData> subSessionList;
	private DatabaseDSData databaseDS;
	private DatabaseDSData secondaryDatabaseDS;
	private List<DatabaseDSData> databaseDSList;
	
	public Integer getSecondaryDataSourceID() {
		return secondaryDataSourceID;
	}
	public DatabaseDSData getSecondaryDatabaseDS() {
		return secondaryDatabaseDS;
	}
	public void setSecondaryDatabaseDS(DatabaseDSData secondaryDatabaseDS) {
		this.secondaryDatabaseDS = secondaryDatabaseDS;
	}
	public void setSecondaryDataSourceID(Integer secondaryDataSourceID) {
		this.secondaryDataSourceID = secondaryDataSourceID;
	}
	public List<DatabaseDSData> getDatabaseDSList() {
		return databaseDSList;
	}
	public void setDatabaseDSList(List<DatabaseDSData> databaseDSList) {
		this.databaseDSList = databaseDSList;
	}
	
	public String getBatchUpdate() {
		return batchUpdate;
	}
	public void setBatchUpdate(String batchUpdate) {
		this.batchUpdate = batchUpdate;
	}
	public int getSessionConfID() {
		return sessionConfID;
	}
	public void setSessionConfID(int sessionConfID) {
		this.sessionConfID = sessionConfID;
	}
	
	public Integer getDataSourceID() {
		return dataSourceID;
	}
	public void setDataSourceID(Integer dataSourceID) {
		this.dataSourceID = dataSourceID;
	}
	
	public int getBatchSize() {
		return batchSize;
	}
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
	public int getBatchUpdateInterval() {
		return batchUpdateInterval;
	}
	public void setBatchUpdateInterval(int batchUpdateInterval) {
		this.batchUpdateInterval = batchUpdateInterval;
	}
	public int getDbQueryTimeout() {
		return dbQueryTimeout;
	}
	public void setDbQueryTimeout(int dbQueryTimeout) {
		this.dbQueryTimeout = dbQueryTimeout;
	}
	public Set<SessionFieldMapData> getSessionFieldMapDataset() {
		return sessionFieldMapDataset;
	}
	public void setSessionFieldMapDataset(
			Set<SessionFieldMapData> sessionFieldMapDataset) {
		this.sessionFieldMapDataset = sessionFieldMapDataset;
	}
	public List<SessionFieldMapData> getCoreSessionList() {
		return coreSessionList;
	}
	public void setCoreSessionList(List<SessionFieldMapData> coreSessionList) {
		this.coreSessionList = coreSessionList;
	}
	public List<SessionFieldMapData> getSubSessionList() {
		return subSessionList;
	}
	public void setSubSessionList(List<SessionFieldMapData> subSessionList) {
		this.subSessionList = subSessionList;
	}
	public DatabaseDSData getDatabaseDS() {
		return databaseDS;
	}
	public void setDatabaseDS(DatabaseDSData databaseDS) {
		this.databaseDS = databaseDS;
	}
}