package com.elitecore.netvertexsm.blmanager.servermgr.spinterface;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.datasource.database.DatabaseDSDataManager;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.SPInterfaceDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DBFieldMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DatabaseSPInterfaceData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data.LDAPFieldMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data.LDAPSPInterfaceData;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;

public class SPInterfaceBLManager extends BaseBLManager{
	private String MODULE="SP-INTERFACE-DRIVER-BL-MANAGER";
	
	public SPInterfaceDataManager getSPInterfaceDataManager(IDataManagerSession session) {
		SPInterfaceDataManager sprDriverDataManager = (SPInterfaceDataManager) DataManagerFactory.getInstance().getDataManager(SPInterfaceDataManager.class, session);
		return sprDriverDataManager; 
	}
	
	
	/**
	 * This Method is generated to create LDAP SP Interface Driver.
	 * @author Manjil Purohit
	 * @param driverInstanceData
	 * @param staffData 
	 * @param actionAlias 
	 * @throws DataManagerException
	 */
	public void create(DriverInstanceData driverInstanceData, IStaffData staffData, String actionAlias) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPInterfaceDataManager driverDataManager = getSPInterfaceDataManager(session);
		SystemAuditDataManager systemAuditDataManager=getSystemAuditDataManager(session);
		
		if (driverDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try{
			session.beginTransaction();
			driverDataManager.create(driverInstanceData);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();
		}catch(DuplicateParameterFoundExcpetion dpe){
	       	session.rollback();
	       	throw new DuplicateParameterFoundExcpetion("Duplicate name used.");
		}catch(Exception e){
	       	session.rollback();
	       	throw new DataManagerException("Action Failed. : "+e.getMessage());
		}finally{
			session.close();
		}			
	}
	
	/**
	 * This Method is generated to create Database SP Interface Driver.
	 * @author Manjil Purohit
	 * @param databaseSPInterfaceDriverData
	 * @throws DataManagerException
	 */
	public void create(DatabaseSPInterfaceData databaseSPInterfaceDriverData) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPInterfaceDataManager driverDataManager = getSPInterfaceDataManager(session);
		
		if (driverDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try{
			session.beginTransaction();
			driverDataManager.create(databaseSPInterfaceDriverData);			
			session.commit();
		}catch(DuplicateParameterFoundExcpetion dpe){
	       	session.rollback();
	       	throw new DuplicateParameterFoundExcpetion("Duplicate name used.");
		}catch(Exception e){
	       	session.rollback();
	       	throw new DataManagerException("Action Failed. : "+e.getMessage());
		}finally{
			session.close();
		}			
	}
	
	public void create(DBFieldMapData databaseFieldMapData) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPInterfaceDataManager driverDataManager = getSPInterfaceDataManager(session);
		
		if (driverDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try{
			session.beginTransaction();
			driverDataManager.create(databaseFieldMapData);			
			session.commit();
		}catch(DuplicateParameterFoundExcpetion dpe){
	       	session.rollback();
	       	throw new DuplicateParameterFoundExcpetion("Duplicate name used.");
		}catch(Exception e){
	       	session.rollback();
	       	throw new DataManagerException("Action Failed. : "+e.getMessage());
		}finally{
			session.close();
		}			
	}
	
	/**
	 * This method will return the search result.
	 * @author Manjil Purohit
	 * @param ldapSpInterfaceDriverData
	 * @return pageList
	 */
	public PageList search(LDAPSPInterfaceData ldapSpInterfaceDriverData, int pageNo, int pageSize, String actionAlias) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPInterfaceDataManager driverDataManager = getSPInterfaceDataManager(session);
		PageList listOfSearchData = null;		
		
		if (driverDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			
		try{
			session.beginTransaction();
			listOfSearchData = driverDataManager.search(ldapSpInterfaceDriverData, pageNo, pageSize);		
			session.close();
			return listOfSearchData;
		}catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action Failed. : "+e.getMessage());
		}finally{
			session.close();
		}					
	}
	
	/**
	 * This method will return the search result.
	 * @author Manjil Purohit
	 * @param databaseSPInterfaceDriverData
	 * @return pageList
	 */
	public PageList search(DatabaseSPInterfaceData databaseSPInterfaceDriverData, int pageNo, int pageSize, String actionAlias) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPInterfaceDataManager driverDataManager = getSPInterfaceDataManager(session);
		PageList listOfSearchData = null;		
		
		if (driverDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			
		try{
			session.beginTransaction();
			listOfSearchData = driverDataManager.search(databaseSPInterfaceDriverData, pageNo, pageSize);		
			return listOfSearchData;
		}catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action Failed. : "+e.getMessage());
		}finally{
			session.close();
		}					
	}
	
		
	
	public List<DBFieldMapData> getDBFieldMapList() throws DataManagerException {				
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPInterfaceDataManager driverDataManager = getSPInterfaceDataManager(session);	
		List<DBFieldMapData> dbFieldMapList = null;
		
		if(driverDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	dbFieldMapList = driverDataManager.getDBFieldMapList();                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
			session.close();
		}		
		return dbFieldMapList;
	}
	
	public List<LDAPFieldMapData> getLDAPFieldMapList() throws DataManagerException {				
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPInterfaceDataManager driverDataManager = getSPInterfaceDataManager(session);	
		List<LDAPFieldMapData> ldapFieldMapDataList = null;
		
		if(driverDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	ldapFieldMapDataList = driverDataManager.getLDAPFieldMapList();                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
			session.close();
		}		
		return ldapFieldMapDataList;
	}
	
	
	
	public LDAPSPInterfaceData getLDAPSPInterfaceDriverData(LDAPSPInterfaceData ldapspInterfaceDriver) throws DataManagerException {	
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPInterfaceDataManager driverDataManager = getSPInterfaceDataManager(session);	
		LDAPSPInterfaceData ldapSpInterfaceDriverData = null;
		
		if(driverDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	ldapSpInterfaceDriverData = driverDataManager.getLDAPSPInterfaceDriverData(ldapspInterfaceDriver);                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
			session.close();
		}		
		return ldapSpInterfaceDriverData;
	}
	public DatabaseSPInterfaceData getDatabaseSPInterfaceData(DatabaseSPInterfaceData databasesprDriver) throws DataManagerException {	
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPInterfaceDataManager driverDataManager = getSPInterfaceDataManager(session);	
		DatabaseSPInterfaceData databaseSprDriverData = null;
		
		if(driverDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	databaseSprDriverData = driverDataManager.getDatabaseSPInterfaceData(databasesprDriver);                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
			session.close();
		}		
		return databaseSprDriverData;
	}
	public List<DriverTypeData> getSPInterfaceTypeList() throws DataManagerException {				
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPInterfaceDataManager driverDataManager = getSPInterfaceDataManager(session);	
		List<DriverTypeData> driverTypeList = null;

		if(driverDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{        	
			session.beginTransaction();	        
			driverTypeList = driverDataManager.getSPInterfaceDriverTypeList();                                         
			session.commit();
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}		
		return driverTypeList;
}
	public List<DBFieldMapData> getDBFieldMapData(DBFieldMapData dbFieldMap) throws DataManagerException {	
		List<DBFieldMapData> fieldMapList = null;
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPInterfaceDataManager driverDataManager = getSPInterfaceDataManager(session);
       
		
		if(driverDataManager == null )
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{	
        	session.beginTransaction();	        
        	fieldMapList = driverDataManager.getDBFieldMapData(dbFieldMap);                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
			session.close();
		}		
		return fieldMapList;
	}
	
	public List<LDAPFieldMapData> getLDAPFieldMapData(LDAPFieldMapData ldapFieldMap) throws DataManagerException {	
		List<LDAPFieldMapData> ldapfieldMapList = null;
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPInterfaceDataManager driverDataManager = getSPInterfaceDataManager(session);
       
		
		if(driverDataManager == null )
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{	
        	session.beginTransaction();	        
        	ldapfieldMapList = driverDataManager.getLDAPFieldMapData(ldapFieldMap);                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
			session.close();
		}		
		return ldapfieldMapList;
	}
	
	public LDAPDatasourceData getLDAPData(long name) throws DataManagerException{
		return null;	
	}
	
	public void update(LDAPDatasourceData ldapDatasourceData , long ldapDsId) throws DataManagerException{
		
	}
	
	public void delete(List<Long> driverInstanceIdList,IStaffData staffData, String actionAlias ) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		SPInterfaceDataManager driverDataManager = getSPInterfaceDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if (driverDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for DriverDataManager");

		try{
			session.beginTransaction();
			driverDataManager.delete(driverInstanceIdList);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();		
		}catch(ConstraintViolationException de){
			session.rollback();
			throw new ConstraintViolationException(de.getMessage(),de.getSQLException(),de.getConstraintName());
		}catch(Exception de){
			session.rollback();
			throw new DataManagerException("Action Failed .",de.getMessage());
		}finally{
			session.close();
		}
	}
	

	
	public void updateDriverInstanceData(DriverInstanceData driverData,Long oldDriverTypeId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		SPInterfaceDataManager driverDataManager = getSPInterfaceDataManager(session);
       
        if (driverDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	
        	driverDataManager.updateDriverInstanceData(driverData,oldDriverTypeId);
        	if(oldDriverTypeId==1){
        		driverDataManager.deleteDBFieldMapData(driverData.getDatabaseDriverData().getDatabaseSpInterfaceId());
        		ArrayList<DBFieldMapData> oldListDBFieldMap=driverData.getDbFieldMapList();
            	
            	driverData.setDbFieldMapList(oldListDBFieldMap);
            	driverDataManager.create(driverData);
        	}else if(oldDriverTypeId==2){
        		driverDataManager.deleteLDAPFieldMapData(driverData.getLdapDriverData().getLdapSPInterfaceId());
        		ArrayList<LDAPFieldMapData> oldListLDAPFieldMap=driverData.getLdapFieldMapList();
            	
            	driverData.setLdapFieldMapList(oldListLDAPFieldMap);
            	driverDataManager.create(driverData);
        	}
        	session.commit();
        }catch(DuplicateParameterFoundExcpetion exp){
        	session.rollback();
        	throw new DuplicateParameterFoundExcpetion("Duplicate Gateway Name. : "+exp.getMessage());
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
			session.close();
		}
    }
	
	
	public PageList search(DriverInstanceData driverInstanceData, int pageNo, int pageSize, String actionAlias) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPInterfaceDataManager driverDataManager = getSPInterfaceDataManager(session);
		PageList listOfSearchData = null;		
		
		if (driverDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			
		try{
			session.beginTransaction();
			listOfSearchData = driverDataManager.search(driverInstanceData, pageNo, pageSize);		
			return listOfSearchData;
		}catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action Failed. : "+e.getMessage());
		}finally{
			session.close();
		}
		
	}
	
	public List<DriverInstanceData> getDriverInstanceList() throws DataManagerException {				
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPInterfaceDataManager driverDataManager = getSPInterfaceDataManager(session);	
		List<DriverInstanceData> driverInstanceList = null;
		
		if(driverDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	driverInstanceList = driverDataManager.getDriverInstanceList();                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
			session.close();
		}		
		return driverInstanceList;
	}
	public DriverInstanceData getDriverInstanceData(DriverInstanceData driverInstanceData) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPInterfaceDataManager driverDataManager = getSPInterfaceDataManager(session);	
		DriverInstanceData driverInstance = null;
		
		if(driverDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	driverInstance = driverDataManager.getDriverInstanceData(driverInstanceData);                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
			session.close();
		}		
		return driverInstance;
	}
	
	public List<String> getGroupResourceRelationData(String sprId) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPInterfaceDataManager driverDataManager = getSPInterfaceDataManager(session);	
		List<String> resourceGroupRelationDatas = Collectionz.newArrayList();
		
		if(driverDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
        try{        	
        	session.beginTransaction();	  
        	resourceGroupRelationDatas.addAll(driverDataManager.getResourceGroupRelationData(sprId));
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage(),e);
        }finally{
			session.close();
		}		
		return resourceGroupRelationDatas;
	}
	private DatabaseDSDataManager getDatabaseDSManager(IDataManagerSession session) {		
		DatabaseDSDataManager databaseDSDataManager = (DatabaseDSDataManager) DataManagerFactory.getInstance().getDataManager(DatabaseDSDataManager.class, session);
		return databaseDSDataManager;		
	}

	public void updateDBDriver(DriverInstanceData driverInstanceData, IStaffData staffData, String actionAlias) throws DataManagerException {
		
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPInterfaceDataManager driverDataManager = getSPInterfaceDataManager(session);
		SystemAuditDataManager systemAuditDataManager=getSystemAuditDataManager(session);
		
		if (driverDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try{
			session.beginTransaction();
			driverDataManager.updateDBDriver(driverInstanceData);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();
		}catch(DuplicateParameterFoundExcpetion dpe){
	       	session.rollback();
	       	throw new DuplicateParameterFoundExcpetion("Duplicate name used.");
		}catch(Exception e){
	       	session.rollback();
	       	throw new DataManagerException("Action Failed. : "+e.getMessage());
		}finally{
			session.close();
		}		
	}
	public void updateLDAPDriver(DriverInstanceData driverInstanceData, IStaffData staffData, String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SPInterfaceDataManager driverDataManager = getSPInterfaceDataManager(session);
		SystemAuditDataManager systemAuditDataManager=getSystemAuditDataManager(session);
		
		if (driverDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try{
			session.beginTransaction();
			driverDataManager.updateLDAPDriver(driverInstanceData);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();
		}catch(DuplicateParameterFoundExcpetion dpe){
	       	session.rollback();
	       	throw new DuplicateParameterFoundExcpetion("Duplicate name used.");
		}catch(Exception e){
	       	session.rollback();
	       	throw new DataManagerException("Action Failed. : "+e.getMessage());
		}finally{
			session.close();
		}		
	}
}