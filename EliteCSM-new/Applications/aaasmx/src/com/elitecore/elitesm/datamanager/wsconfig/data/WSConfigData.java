/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   SsubscriberdbconfigData.java                 		
 * ModualName wsconfig    			      		
 * Created on 23 December, 2010
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.wsconfig.data;
import java.util.Set;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;

import net.sf.json.JSONObject;

public class WSConfigData extends BaseData implements IWSConfigData {
	
    private String wsconfigId;
    private String databasedsId;
	private String tableName;
	private String userIdentityFieldName;
	private Integer recordFetchLimit;
	private String configType;
	private Set<IWSAttrFieldMapData> wsAttrFieldMapSet;
	private Set<IWSDBFieldMapData> wsDBFieldMapSet;
	private DatabaseDSData datasourceConfigInstance;
	private String primaryKeyColumn;
	private String sequenceName;
	private Set<WSKeyMappingData> wsKeyMappingSet;
	/**
	 * @return the wsconfigId
	 */

	public String getWsconfigId() {
		return wsconfigId;
	}

	public Set<WSKeyMappingData> getWsKeyMappingSet() {
		return wsKeyMappingSet;
	}

	public void setWsKeyMappingSet(Set<WSKeyMappingData> wsAddFieldMapSet) {
		this.wsKeyMappingSet = wsAddFieldMapSet;
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
	public void setWsconfigId(String wsconfigId) {
		this.wsconfigId = wsconfigId;
	}
	/**
	 * @return the databasedsId
	 */
	public String getDatabasedsId() {
		return databasedsId;
	}
	/**
	 * @param databasedsId the databasedsId to set
	 */
	public void setDatabasedsId(String databasedsId) {
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
	 * @return the wsAttrFieldMapSet
	 */
	public Set<IWSAttrFieldMapData> getWsAttrFieldMapSet() {
		return wsAttrFieldMapSet;
	}
	/**
	 * @param wsAttrFieldMapSet the wsAttrFieldMapSet to set
	 */
	public void setWsAttrFieldMapSet(Set<IWSAttrFieldMapData> wsAttrFieldMapSet) {
		this.wsAttrFieldMapSet = wsAttrFieldMapSet;
	}
	/**
	 * @return the wsDBFieldMapSet
	 */
	public Set<IWSDBFieldMapData> getWsDBFieldMapSet() {
		return wsDBFieldMapSet;
	}
	/**
	 * @param wsDBFieldMapSet the wsDBFieldMapSet to set
	 */
	public void setWsDBFieldMapSet(Set<IWSDBFieldMapData> wsDBFieldMapSet) {
		this.wsDBFieldMapSet = wsDBFieldMapSet;
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


	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Database Datasource", EliteSMReferencialDAO.fetchDatabaseDatasourceData(databasedsId));
		object.put("Table Name", tableName);
		object.put("Record Fetch Limit", recordFetchLimit);
		JSONObject keyMappingJson = new JSONObject();
		
			if(Collectionz.isNullOrEmpty(wsKeyMappingSet) == false){
				object.put("User Identity Field", userIdentityFieldName);
				object.put("Primary Key Column", primaryKeyColumn);
				object.put("Sequence Name", sequenceName);
				
				for (WSKeyMappingData element : wsKeyMappingSet) {
					keyMappingJson.put(element.getWsKey(), element.toJson());
				}
				
				object.put("WS-Key Mapping Mapping", keyMappingJson);
			}
			if(Collectionz.isNullOrEmpty(wsAttrFieldMapSet) == false){
				
				JSONObject attributeFieldMapJson = new JSONObject();
			
				for(IWSAttrFieldMapData data : wsAttrFieldMapSet){
					attributeFieldMapJson.put(data.getAttribute(), data.toJson());
				}
				
				object.put("Attribute Field Mapping", attributeFieldMapJson);
				
			}
			
			if(Collectionz.isNullOrEmpty(wsDBFieldMapSet) == false){
				
				JSONObject dbFieldMapJson = new JSONObject();
				
				for(IWSDBFieldMapData data : wsDBFieldMapSet) {
					dbFieldMapJson.put(data.getKey(), data.toJson());
				}
				
				object.put("DB Field Mapping", dbFieldMapJson);
			}
			
		return object;
	}
}