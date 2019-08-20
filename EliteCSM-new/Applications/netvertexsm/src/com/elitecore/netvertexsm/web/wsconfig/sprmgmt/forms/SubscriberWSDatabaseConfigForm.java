package com.elitecore.netvertexsm.web.wsconfig.sprmgmt.forms;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class SubscriberWSDatabaseConfigForm extends BaseWebForm{
	private static final long serialVersionUID = 1L;
	private Integer wsConfigId;
	private Long databaseId;
	private List<DatabaseDSData> lstDatasource;
	private List<DriverInstanceData> driverInstanceDatas;
	
	private String tableName;
	private String userIdentityFieldName;
	private Integer recordFetchLimit;
	private String key;
	private String field;
    private String checkAction="";
    private int itemIndex;
    private String primaryKeyColumn;
    private Integer bodCDRDriverId;
    private String sequenceName;
    private Integer dynaSprDatabaseId;
    private String subscriberIdentity;
    private Long usageMonitoringDatabaseId;
	
	
	public List<DriverInstanceData> getDriverInstanceDatas() {
		return driverInstanceDatas;
	}

	public void setDriverInstanceDatas(List<DriverInstanceData> driverInstanceDatas) {
		this.driverInstanceDatas = driverInstanceDatas;
	}

	public Integer getBodCDRDriverId() {
		return bodCDRDriverId;
	}

	public void setBodCDRDriverId(Integer bodCDRDriverId) {
		this.bodCDRDriverId = bodCDRDriverId;
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
	 * @return the wsConfigId
	 */
	public Integer getWsConfigId() {
		return wsConfigId;
	}
	/**
	 * @param wsConfigId the wsConfigId to set
	 */
	public void setWsConfigId(Integer wsConfigId) {
		this.wsConfigId = wsConfigId;
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
	 * @return the checkAction
	 */
	public String getCheckAction() {
		return checkAction;
	}
	/**
	 * @param checkAction the checkAction to set
	 */
	public void setCheckAction(String checkAction) {
		this.checkAction = checkAction;
	}
	/**
	 * @return the itemIndex
	 */
	public int getItemIndex() {
		return itemIndex;
	}
	/**
	 * @param itemIndex the itemIndex to set
	 */
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
	/**
	 * @return the databaseId
	 */
	public Long getDatabaseId() {
		return databaseId;
	}
	/**
	 * @param databaseId the databaseId to set
	 */
	public void setDatabaseId(Long databaseId) {
		this.databaseId = databaseId;
	}
	/**
	 * @return the lstDatasource
	 */
	public List<DatabaseDSData> getLstDatasource() {
		return lstDatasource;
	}
	/**
	 * @param lstDatasource the lstDatasource to set
	 */
	public void setLstDatasource(List<DatabaseDSData> lstDatasource) {
		this.lstDatasource = lstDatasource;
	}
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}
	/**
	 * @param field the field to set
	 */
	public void setField(String field) {
		this.field = field;
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

	
}
