/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   SsubscriberdbconfigData.java                 		
 * ModualName wsconfig    			      		
 * Created on 23 December, 2010
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.netvertexsm.datamanager.wsconfig.data;
import java.util.Set;

import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;

public class WSConfigData{
	
    private Integer wsconfigId;
    private Long databasedsId;
	private String tableName;
	private String userIdentityFieldName;
	private Integer recordFetchLimit;
	private String configType;
	private DatabaseDSData datasourceConfigInstance;
	private String primaryKeyColumn;
	private String sequenceName;
	private Integer bodCDRDriverId;
	private DriverInstanceData driverInstanceData;
	private Set wsAddFieldMapSet;
	private Integer dynaSprDatabaseId;
	private String subscriberIdentity;
	private Long usageMonitoringDatabaseId;
	private DatabaseDSData usageMonitoringDatabaseData;
	private DatabaseDSData dynaSPRDatabaseDSData;
	
	private String logLevel = "INFO";
	private Long rollingType = 1L;
	private Long rollingUnit = 1L;
	private Long maxRollingUnit = 1L;
	private String logFileName = "filename";
	
	public DriverInstanceData getDriverInstanceData() {
		return driverInstanceData;
	}

	public void setDriverInstanceData(DriverInstanceData driverInstanceData) {
		this.driverInstanceData = driverInstanceData;
	}

	public Integer getWsconfigId() {
		return wsconfigId;
	}

	public Integer getBodCDRDriverId() {
		return bodCDRDriverId;
	}

	public void setBodCDRDriverId(Integer bodCDRDriverId) {
		this.bodCDRDriverId = bodCDRDriverId;
	}

	public Set getWsAddFieldMapSet() {
		return wsAddFieldMapSet;
	}

	public void setWsAddFieldMapSet(Set wsAddFieldMapSet) {
		this.wsAddFieldMapSet = wsAddFieldMapSet;
	}

	public String getPrimaryKeyColumn() {
		return primaryKeyColumn;
	}
	
	public void setPrimaryKeyColumn(String primaryKeyColumn) {
		this.primaryKeyColumn = primaryKeyColumn;
	}
	
	public String getSequenceName() {
		return sequenceName;
	}
	
	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}
	
	/**
	 * @param wsconfigId the wsconfigId to set
	 */
	public void setWsconfigId(Integer wsconfigId) {
		this.wsconfigId = wsconfigId;
	}
	/**
	 * @return the databasedsId
	 */
	public Long getDatabasedsId() {
		return databasedsId;
	}
	/**
	 * @param databasedsId the databasedsId to set
	 */
	public void setDatabasedsId(Long databasedsId) {
		this.databasedsId = databasedsId;
	}
	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}
	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	/**
	 * @return the userIdentityFieldName
	 */
	public String getUserIdentityFieldName() {
		return userIdentityFieldName;
	}
	/**
	 * @param userIdentityFieldName the userIdentityFieldName to set
	 */
	public void setUserIdentityFieldName(String userIdentityFieldName) {
		this.userIdentityFieldName = userIdentityFieldName;
	}
	/**
	 * @return the recordFetchLimit
	 */
	public Integer getRecordFetchLimit() {
		return recordFetchLimit;
	}
	/**
	 * @param recordFetchLimit the recordFetchLimit to set
	 */
	public void setRecordFetchLimit(Integer recordFetchLimit) {
		this.recordFetchLimit = recordFetchLimit;
	}
	/**
	 * @return the configType
	 */
	public String getConfigType() {
		return configType;
	}
	/**
	 * @param configType the configType to set
	 */
	public void setConfigType(String configType) {
		this.configType = configType;
	}
	/**
	 * @return the datasourceConfigInstance
	 */
	public DatabaseDSData getDatasourceConfigInstance() {
		return datasourceConfigInstance;
	}
	/**
	 * @param datasourceConfigInstance the datasourceConfigInstance to set
	 */
	public void setDatasourceConfigInstance(DatabaseDSData datasourceConfigInstance) {
		this.datasourceConfigInstance = datasourceConfigInstance;
	}

	public Integer getDynaSprDatabaseId() {
		return dynaSprDatabaseId;
	}

	public void setDynaSprDatabaseId(Integer dynaSprDatabaseId) {
		this.dynaSprDatabaseId = dynaSprDatabaseId;
	}

	public String getSubscriberIdentity() {
		return subscriberIdentity;
	}

	public void setSubscriberIdentity(String subscriberIdentity) {
		this.subscriberIdentity = subscriberIdentity;
	}

	public Long getUsageMonitoringDatabaseId() {
		return usageMonitoringDatabaseId;
	}

	public void setUsageMonitoringDatabaseId(Long usageMonitoringDatabaseId) {
		this.usageMonitoringDatabaseId = usageMonitoringDatabaseId;
	}

	public DatabaseDSData getUsageMonitoringDatabaseData() {
		return usageMonitoringDatabaseData;
	}

	public void setUsageMonitoringDatabaseData(
			DatabaseDSData usageMonitoringDatabaseData) {
		this.usageMonitoringDatabaseData = usageMonitoringDatabaseData;
	}

	public DatabaseDSData getDynaSPRDatabaseDSData() {
		return dynaSPRDatabaseDSData;
	}

	public void setDynaSPRDatabaseDSData(DatabaseDSData dynaSPRDatabaseDSData) {
		this.dynaSPRDatabaseDSData = dynaSPRDatabaseDSData;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public Long getRollingType() {
		return rollingType;
	}

	public void setRollingType(Long rollingType) {
		this.rollingType = rollingType;
	}

	public Long getRollingUnit() {
		return rollingUnit;
	}

	public void setRollingUnit(Long rollingUnit) {
		this.rollingUnit = rollingUnit;
	}

	public Long getMaxRollingUnit() {
		return maxRollingUnit;
	}

	public void setMaxRollingUnit(Long maxRollingUnit) {
		this.maxRollingUnit = maxRollingUnit;
	}

	public String getLogFileName() {
		return logFileName;
	}

	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}
}