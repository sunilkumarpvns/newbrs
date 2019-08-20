package com.elitecore.netvertexsm.blmanager.servermgr.drivers;

import java.util.List;

import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.DriverDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.ServiceTypeData;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;

public class DriverBLManager {
	private String MODULE="DRIVER-BL-MANAGER";

	public DriverDataManager getDriverInstanceDataManager(IDataManagerSession session) {
		DriverDataManager ldapDriverDataManager = (DriverDataManager) DataManagerFactory.getInstance().getDataManager(DriverDataManager.class, session);
		return ldapDriverDataManager; 
	}

	/**
	 * This Method is generated to create LDAP SPR Driver.
	 * @author Manjil Purohit
	 * @param driverInstanceData
	 * @throws DataManagerException
	 */
	public void create(DriverInstanceData driverInstanceData,IStaffData staffData, String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverInstanceDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if (driverDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			driverDataManager.create(driverInstanceData);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();
		}catch(DuplicateParameterFoundExcpetion dpe){
			dpe.printStackTrace();
			session.rollback();
			throw new DuplicateParameterFoundExcpetion("Duplicate name used.");
		}catch(Exception e){
			session.close();
			throw new DataManagerException("Action Failed. : "+e.getMessage());
		}finally{
			session.close();
		}			
	}

	
	/**
	 * This method will return the search result.
	 * @author Manjil Purohit
	 * @param driverInstanceData
	 * @return pageList
	 */
	public PageList search(DriverInstanceData driverInstanceData, int pageNo, int pageSize, String actionAlias) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverInstanceDataManager(session);
		PageList listOfSearchData = null;		

		if (driverDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			listOfSearchData = driverDataManager.search(driverInstanceData, pageNo, pageSize);		
			session.close();
			return listOfSearchData;
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action Failed. : "+e.getMessage());
		}					
	}

	public List<DriverInstanceData> getDriverInstanceList() throws DataManagerException {				
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverInstanceDataManager(session);	
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

	public List<ServiceTypeData> getServiceTypeList() throws DataManagerException {				
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverInstanceDataManager(session);	
		List<ServiceTypeData> serviceTypeList = null;

		if(driverDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{        	
			session.beginTransaction();	        
			serviceTypeList = driverDataManager.getServiceTypeList();                                         
			session.commit();
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}		
		return serviceTypeList;
	}
	public List<DriverTypeData> getDriverTypeList() throws DataManagerException {				
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverInstanceDataManager(session);	
		List<DriverTypeData> driverTypeList = null;

		if(driverDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{        	
			session.beginTransaction();	        
			driverTypeList = driverDataManager.getDriverTypeList();                                         
			session.commit();
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}		
		return driverTypeList;
	}

	public List<DriverInstanceData> getDriverTypeList(long serviceTypeID) throws DataManagerException {				
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverInstanceDataManager(session);	
		List<DriverInstanceData> driverTypeList = null;

		if(driverDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{        	
			session.beginTransaction();	        
			driverTypeList = driverDataManager.getDriverInstanceList(serviceTypeID);                                         
			session.commit();
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}		
		return driverTypeList;
	}


	public List<LogicalNameValuePoolData> getLogicalNamePoolList() throws DataManagerException {				
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverInstanceDataManager(session);	
		List<LogicalNameValuePoolData> logicalNamePoolList = null;

		if(driverDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{        	
			session.beginTransaction();	        
			logicalNamePoolList = driverDataManager.getLogicalNamePoolList();                                         
			session.commit();
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}		
		return logicalNamePoolList;
	}

	public DriverInstanceData getDriverInstanceData(DriverInstanceData driverInstanceData) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverInstanceDataManager(session);	
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



	public void delete(List<Long> driverInstanceIdList,IStaffData staffData, String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		DriverDataManager driverDataManager = getDriverInstanceDataManager(session);
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
			throw de;
		}catch(DataManagerException de){
			session.rollback();
			throw new DataManagerException("Action Failed .",de);
		}catch(Exception de){
			session.rollback();
			throw new DataManagerException("Action Failed .",de);
		}finally{
			session.close();
		}
	}

	public void update(DriverInstanceData driverInstanceData, IStaffData staffData, String actionAlias) throws DataManagerException {


		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverInstanceDataManager(session);
		SystemAuditDataManager systemAuditDataManager=getSystemAuditDataManager(session);

		if (driverDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			driverDataManager.update(driverInstanceData);
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
	
	public void updateCSVDriver(DriverInstanceData driverInstanceData,IStaffData staffData, String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverInstanceDataManager(session);
		SystemAuditDataManager systemAuditDataManager =  getSystemAuditDataManager(session);
		if (driverDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			driverDataManager.updateCSVDriver(driverInstanceData);
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
	
	public void updateDBCDRDriver(DriverInstanceData driverInstanceData,IStaffData staffData, String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverInstanceDataManager(session);
		SystemAuditDataManager systemAuditDataManager =  getSystemAuditDataManager(session);

		if (driverDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			driverDataManager.updateDBCDRDriver(driverInstanceData);
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
	
	private SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session){
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
		return systemAuditDataManager; 
	}

}