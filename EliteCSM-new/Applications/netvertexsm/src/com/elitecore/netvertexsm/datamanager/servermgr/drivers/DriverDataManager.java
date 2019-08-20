package com.elitecore.netvertexsm.datamanager.servermgr.drivers;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.ServiceTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DBFieldMapData;

public interface DriverDataManager extends DataManager {

	  public void create(DriverInstanceData driverInstanceData) throws DataManagerException,DuplicateParameterFoundExcpetion;
	
	  public PageList search (DriverInstanceData driverInstanceData, int pageNo, int pageSize) throws DataManagerException;
	  
	  public void delete(List<Long> driverIdList) throws DataManagerException;
	  
	  public List<DriverInstanceData> getDriverInstanceList() throws DataManagerException ;
	  
	  public List<ServiceTypeData> getServiceTypeList() throws DataManagerException ;
	  
	  public List<DriverTypeData> getDriverTypeList() throws DataManagerException;
	  
	  public List<DriverInstanceData> getDriverInstanceList(long serviceTypeID) throws DataManagerException;
	  
	  public List<LogicalNameValuePoolData> getLogicalNamePoolList() throws DataManagerException;
	  
	  public DriverInstanceData getDriverInstanceData(DriverInstanceData driverInstanceData ) throws DataManagerException;
	  
	  public void updateDriverInstanceData(DriverInstanceData driverData,Long oldDriverTypeId) throws DataManagerException;
	  
	  public void create(DBFieldMapData databaseFieldMapData) throws DataManagerException;

	  public void updateCSVDriver(DriverInstanceData driverInstanceData) throws DataManagerException;
	  
	  public void updateDBCDRDriver(DriverInstanceData driverInstanceData) throws DataManagerException;
	  
	  public void update(DriverInstanceData driverInstanceData) throws DataManagerException;
	
}
