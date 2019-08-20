package com.elitecore.netvertexsm.datamanager.servermgr.spinterface;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DBFieldMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DatabaseSPInterfaceData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data.LDAPFieldMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data.LDAPSPInterfaceData;

public interface SPInterfaceDataManager extends DataManager {

	  public void create(DriverInstanceData driverInstanceData) throws DataManagerException,DuplicateParameterFoundExcpetion;
	  
	  public PageList search (LDAPSPInterfaceData ldapSpInterfaceDriverData, int pageNo, int pageSize) throws DataManagerException;
	  
	  public PageList search (DriverInstanceData driverInstanceData, int pageNo, int pageSize) throws DataManagerException;
	  
	  public void update(LDAPSPInterfaceData ldapSpInterfaceDriverData,long ldapSPInterfaceId) throws DataManagerException;
	  
	  public void delete(List<Long> driverIdList) throws DataManagerException;
	  
	  public List<DriverTypeData> getSPInterfaceDriverTypeList() throws DataManagerException;
	  
	  public List<DBFieldMapData> getDBFieldMapList() throws DataManagerException;
	 
	  public List<LDAPFieldMapData> getLDAPFieldMapList() throws DataManagerException;
	  
	  public LDAPSPInterfaceData getLDAPSPInterfaceDriverData(LDAPSPInterfaceData ldapspInterfaceDriver) throws DataManagerException;
	  
	  public DatabaseSPInterfaceData getDatabaseSPInterfaceData(DatabaseSPInterfaceData databasespInterfaceDriver) throws DataManagerException;
	  
	  public List<DBFieldMapData> getDBFieldMapData(DBFieldMapData dbFieldMap) throws DataManagerException;
	  
	  public List<LDAPFieldMapData> getLDAPFieldMapData(LDAPFieldMapData ldapFieldMap) throws DataManagerException;
	  
	  public void updateDriverInstanceData(DriverInstanceData driverData,Long oldDriverTypeId) throws DataManagerException;
	  
	  public void create(DatabaseSPInterfaceData databaseSPInterfaceDriverData) throws DataManagerException,DuplicateParameterFoundExcpetion;
	  
	  public PageList search (DatabaseSPInterfaceData databaseSPInterfaceDriverData, int pageNo, int pageSize) throws DataManagerException;
	  
	  public void deleteDBFieldMapData(Long dbspinterfaceId)throws DataManagerException;
	  
	  public void deleteLDAPFieldMapData(Long ldapSPInterfaceId)throws DataManagerException;
	  
	  public void create(DBFieldMapData databaseFieldMapData) throws DataManagerException;
	  
	  public List<DriverInstanceData> getDriverInstanceList() throws DataManagerException; 
	  
	  public DriverInstanceData getDriverInstanceData(DriverInstanceData driverInstanceData) throws DataManagerException;

	  public void updateLDAPDriver(DriverInstanceData driverInstanceData) throws DataManagerException;

      public void updateDBDriver(DriverInstanceData driverInstanceData)  throws DataManagerException;
      
      public List<String> getResourceGroupRelationData(String sprId) throws DataManagerException;
	  
	
}
