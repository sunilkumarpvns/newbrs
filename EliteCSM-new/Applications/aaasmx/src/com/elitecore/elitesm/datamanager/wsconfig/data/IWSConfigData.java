/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   ISsubscriberdbconfigData.java                 		
 * ModualName wsconfig    			      		
 * Created on 23 December, 2010
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.wsconfig.data;

import java.util.Set;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData;
   
public interface IWSConfigData extends Differentiable{

	public String getWsconfigId();
	public void setWsconfigId(String wsconfigId);
	
	public String getDatabasedsId();
	public void setDatabasedsId(String databasedsId);
	
	public String getTableName();
	public void setTableName(String tableName);
	
	public String getUserIdentityFieldName();
	public void setUserIdentityFieldName(String userIdentityFieldName);
	
	public Integer getRecordFetchLimit();
	public void setRecordFetchLimit(Integer recordFetchLimit);
	
	public String getConfigType();
	public void setConfigType(String configType);
	
	public Set<IWSAttrFieldMapData> getWsAttrFieldMapSet();
	public void setWsAttrFieldMapSet(Set<IWSAttrFieldMapData> wsAttrFieldMapSet);
	
	public Set<IWSDBFieldMapData> getWsDBFieldMapSet();
	public void setWsDBFieldMapSet(Set<IWSDBFieldMapData> wsDBFieldMapSet);
   
	public DatabaseDSData getDatasourceConfigInstance();
	public void setDatasourceConfigInstance(DatabaseDSData datasourceConfigInstance);
	
	public String getPrimaryKeyColumn();
	public void setPrimaryKeyColumn(String primaryKeyColumn);
	
	public String getSequenceName();
	public void setSequenceName(String sequenceName);
	
	public Set<WSKeyMappingData> getWsKeyMappingSet();
	public void setWsKeyMappingSet(Set<WSKeyMappingData> wsAddFieldMapSet);
}
