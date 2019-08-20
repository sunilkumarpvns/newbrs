package com.elitecore.netvertexsm.web.servermgr.drivers.dbcdrdriver.form;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.dbcdrdriver.data.DBCDRFieldMappingData;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class DBCDRDriverForm extends BaseWebForm{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long dbCDRDriverID;
	private Long driverInstanceId; 
	private Long databaseDSID;
	private Long dbQueryTimeout; 
	private Long maxQueryTimeoutCount; 
	private String isBatchUpdate; 
	private Long batchSize; 
	private Long batchUpdateInterval; 
	private Long queryTimeout; 
	private String tableName; 
	private String identityField; 
	private String sequenceName; 
	private String storeAllCDR; 
	private String timeStampformat; 
	private String reportingType; 
	private String sessionIDFieldName;
	private String createDateFieldName;
	private String lastModifiedFieldName;
	private String inputOctetsFieldName; 
	private String outputOctetsFieldName; 
	private String totalOctetsFieldName; 
	private String usageTimeFieldName; 
	private String usageKeyFieldName;
	private String dataType;
	private String dataTypeArray;
	private List<DatabaseDSData> databaseDSList;
	private List<DBCDRFieldMappingData> dbcdrFieldMappingDataList;
	
	public String getDataTypeArray() {
		return dataTypeArray;
	}
	public void setDataTypeArray(String dataTypeArray) {
		this.dataTypeArray = dataTypeArray;
	}
	public List<DBCDRFieldMappingData> getDbcdrFieldMappingDataList() {
		return dbcdrFieldMappingDataList;
	}
	public void setDbcdrFieldMappingDataList(
			List<DBCDRFieldMappingData> dbcdrFieldMappingDataList) {
		this.dbcdrFieldMappingDataList = dbcdrFieldMappingDataList;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public List<DatabaseDSData> getDatabaseDSList() {
		return databaseDSList;
	}
	public void setDatabaseDSList(List<DatabaseDSData> databaseDSList) {
		this.databaseDSList = databaseDSList;
	}
	public Long getDbCDRDriverID() {
		return dbCDRDriverID;
	}
	public void setDbCDRDriverID(Long dbCDRDriverID) {
		this.dbCDRDriverID = dbCDRDriverID;
	}
	public Long getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(Long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public Long getDatabaseDSID() {
		return databaseDSID;
	}
	public void setDatabaseDSID(Long databaseDSID) {
		this.databaseDSID = databaseDSID;
	}
	public Long getDbQueryTimeout() {
		return dbQueryTimeout;
	}
	public void setDbQueryTimeout(Long dbQueryTimeout) {
		this.dbQueryTimeout = dbQueryTimeout;
	}
	public Long getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}
	public void setMaxQueryTimeoutCount(Long maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
	}
	public String getIsBatchUpdate() {
		return isBatchUpdate;
	}
	public void setIsBatchUpdate(String isBatchUpdate) {
		this.isBatchUpdate = isBatchUpdate;
	}
	public Long getBatchSize() {
		return batchSize;
	}
	public void setBatchSize(Long batchSize) {
		this.batchSize = batchSize;
	}
	public Long getBatchUpdateInterval() {
		return batchUpdateInterval;
	}
	public void setBatchUpdateInterval(Long batchUpdateInterval) {
		this.batchUpdateInterval = batchUpdateInterval;
	}
	public Long getQueryTimeout() {
		return queryTimeout;
	}
	public void setQueryTimeout(Long queryTimeout) {
		this.queryTimeout = queryTimeout;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getIdentityField() {
		return identityField;
	}
	public void setIdentityField(String identityField) {
		this.identityField = identityField;
	}
	public String getSequenceName() {
		return sequenceName;
	}
	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}
	public String getStoreAllCDR() {
		return storeAllCDR;
	}
	public void setStoreAllCDR(String storeAllCDR) {
		this.storeAllCDR = storeAllCDR;
	}
	public String getTimeStampformat() {
		return timeStampformat;
	}
	public void setTimeStampformat(String timeStampformat) {
		this.timeStampformat = timeStampformat;
	}
	public String getReportingType() {
		return reportingType;
	}
	public void setReportingType(String reportingType) {
		this.reportingType = reportingType;
	}
	public String getSessionIDFieldName() {
		return sessionIDFieldName;
	}
	public void setSessionIDFieldName(String sessionIDFieldName) {
		this.sessionIDFieldName = sessionIDFieldName;
	}
	public String getCreateDateFieldName() {
		return createDateFieldName;
	}
	public void setCreateDateFieldName(String createDateFieldName) {
		this.createDateFieldName = createDateFieldName;
	}
	public String getLastModifiedFieldName() {
		return lastModifiedFieldName;
	}
	public void setLastModifiedFieldName(String lastModifiedFieldName) {
		this.lastModifiedFieldName = lastModifiedFieldName;
	}
	public String getInputOctetsFieldName() {
		return inputOctetsFieldName;
	}
	public void setInputOctetsFieldName(String inputOctetsHeader) {
		this.inputOctetsFieldName = inputOctetsHeader;
	}
	public String getOutputOctetsFieldName() {
		return outputOctetsFieldName;
	}
	public void setOutputOctetsFieldName(String outputOctetsHeader) {
		this.outputOctetsFieldName = outputOctetsHeader;
	}
	public String getTotalOctetsFieldName() {
		return totalOctetsFieldName;
	}
	public void setTotalOctetsFieldName(String totalOctetsHeader) {
		this.totalOctetsFieldName = totalOctetsHeader;
	}
	public String getUsageTimeFieldName() {
		return usageTimeFieldName;
	}
	public void setUsageTimeFieldName(String usageTimeHeader) {
		this.usageTimeFieldName = usageTimeHeader;
	}
	public String getUsageKeyFieldName() {
		return usageKeyFieldName;
	}
	public void setUsageKeyFieldName(String usageKeyHeader) {
		this.usageKeyFieldName = usageKeyHeader;
	}
}