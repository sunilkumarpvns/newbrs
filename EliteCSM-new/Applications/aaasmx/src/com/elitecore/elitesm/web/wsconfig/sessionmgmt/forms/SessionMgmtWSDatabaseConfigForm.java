package com.elitecore.elitesm.web.wsconfig.sessionmgmt.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class SessionMgmtWSDatabaseConfigForm extends BaseWebForm{

	private static final long serialVersionUID = 1L;
	
	private String wsConfigId;
	
	private String databaseId;
	private List<IDatabaseDSData> lstDatasource;
	
	private String tableName;
	private String userIdentityFieldName;
	private Integer recordFetchLimit;
	private String key;
	private String field;
	private String attribute;
	private String attrField;
	
    private String checkAction="";
    private int itemIndex;
    
	
	
	
	/**
	 * @return the wsConfigId
	 */
	public String getWsConfigId() {
		return wsConfigId;
	}
	/**
	 * @param wsConfigId the wsConfigId to set
	 */
	public void setWsConfigId(String wsConfigId) {
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
	public String getDatabaseId() {
		return databaseId;
	}
	/**
	 * @param databaseId the databaseId to set
	 */
	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}
	/**
	 * @return the lstDatasource
	 */
	public List<IDatabaseDSData> getLstDatasource() {
		return lstDatasource;
	}
	/**
	 * @param lstDatasource the lstDatasource to set
	 */
	public void setLstDatasource(List<IDatabaseDSData> lstDatasource) {
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
	/**
	 * @return the attribute
	 */
	public String getAttribute() {
		return attribute;
	}
	/**
	 * @param attribute the attribute to set
	 */
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	/**
	 * @return the attrField
	 */
	public String getAttrField() {
		return attrField;
	}
	/**
	 * @param attrField the attrField to set
	 */
	public void setAttrField(String attrField) {
		this.attrField = attrField;
	}
	
	
	
	 

	
}
