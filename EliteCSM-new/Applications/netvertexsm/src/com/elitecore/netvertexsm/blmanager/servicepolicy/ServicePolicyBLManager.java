package com.elitecore.netvertexsm.blmanager.servicepolicy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servicepolicy.ServicePolicyDataManager;
import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFPolicyCDRDriverRelData;
import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicyData;
import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicySyGatewayRelData;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.netvertexsm.ws.db.DBConnectionManager;

public class ServicePolicyBLManager {
	private String MODULE="SERVICE-POLICY";
	
	public ServicePolicyDataManager getServicePolicyDataManager(IDataManagerSession session) {
		ServicePolicyDataManager servicePolicyDataManager = (ServicePolicyDataManager) DataManagerFactory.getInstance().getDataManager(ServicePolicyDataManager.class, session);
		return servicePolicyDataManager; 
	}
	
	/**
	 * This Method is generated to create PCRF Service Policy.
	 * @author Manjil Purohit
	 * @param pcrfServicePolicyData
	 * @throws DataManagerException
	 */
	public void create(PCRFServicePolicyData pcrfServicePolicyData,IStaffData staffData, String actionAlias) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ServicePolicyDataManager servicePolicyDataManager = getServicePolicyDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if (servicePolicyDataManager == null && systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try{
			session.beginTransaction();
			servicePolicyDataManager.create(pcrfServicePolicyData);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();
		}catch(DuplicateParameterFoundExcpetion dpe){
			session.rollback();
	       	throw new DuplicateParameterFoundExcpetion("Duplicate name used.",dpe);
		}catch(Exception e){
	       	session.rollback();
	       	throw new DataManagerException("Action Failed. : "+e.getMessage(),e);
		}finally{
			session.close();
		}			
	}
	
	
	/**
	 * This method will return the search result.
	 * @author Manjil Purohit
	 * @param pcrfServicePolicyData
	 * @param actionAlias
	 * @return pageList
	 */
	public PageList search(PCRFServicePolicyData pcrfServicePolicyData, int pageNo, int pageSize, IStaffData staffData, String actionAlias) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ServicePolicyDataManager servicePolicyDataManager = getServicePolicyDataManager(session);
		PageList listOfSearchData = null;		
		
		if (servicePolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			listOfSearchData = servicePolicyDataManager.search(pcrfServicePolicyData, pageNo, pageSize);
			return listOfSearchData;
		}catch(Exception e){
        	throw new DataManagerException("Action Failed. : "+e.getMessage(),e);
		}finally{
			session.close();
		}					
	}
	
	public List searchServicePolicy() throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ServicePolicyDataManager servicePolicyDataManager = getServicePolicyDataManager(session);
		List listOfSearchData = null;		
		if (servicePolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();
			listOfSearchData = servicePolicyDataManager.searchServiceServicePolicy();		
			return listOfSearchData;
		}catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action Failed. : "+e.getMessage(),e);
		}finally{
			session.close();
		}					
	}
	
	public void changeServicePolicyOrder(String[]order,IStaffData staffData, String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ServicePolicyDataManager servicePolicyDataManager = getServicePolicyDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		try{				     
			if(servicePolicyDataManager==null && systemAuditDataManager == null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
			session.beginTransaction();
			servicePolicyDataManager.changeServicePolicyOrder(order);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();
		}catch(DataManagerException e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
	}
	
	public PCRFServicePolicyData getPCRFServicePolicyData(PCRFServicePolicyData pcrfPolicyData,IStaffData staffData, String actionAlias) throws DataManagerException {
		PCRFServicePolicyData pcrfPolicyInfo = null;
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ServicePolicyDataManager servicePolicyDataManager = getServicePolicyDataManager(session);
		if(servicePolicyDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{        	
        	pcrfPolicyInfo = servicePolicyDataManager.getPCRFServicePolicyData(pcrfPolicyData);
        }catch(Exception e){
        	throw new DataManagerException("Action failed :"+e.getMessage(),e);
        }finally{
        	session.close();
        }		
		return pcrfPolicyInfo;
	}
	
	public PCRFServicePolicyData getPCRFServicePolicyData(PCRFServicePolicyData pcrfPolicyData) throws DataManagerException {
		PCRFServicePolicyData pcrfPolicyInfo = null;
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ServicePolicyDataManager servicePolicyDataManager = getServicePolicyDataManager(session);
		if(servicePolicyDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{        	
        	session.beginTransaction();	        
        	pcrfPolicyInfo = servicePolicyDataManager.getPCRFServicePolicyData(pcrfPolicyData);
        	session.commit();
        }catch(Exception e){
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }		
		return pcrfPolicyInfo;
	}
	
	public DriverInstanceData getDriverInstanceData(long driverInstanceId) throws DataManagerException {
		DriverInstanceData driverInstanceInfo = null;
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ServicePolicyDataManager servicePolicyDataManager = getServicePolicyDataManager(session);
		if(servicePolicyDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{        	
        	session.beginTransaction();	        
        	driverInstanceInfo = servicePolicyDataManager.getDriverInstanceData(driverInstanceId);                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }		
		return driverInstanceInfo;
	}
	
	
	public List<PCRFServicePolicySyGatewayRelData> getPCRFPolicySyGatewayList(long pcrfID) throws DataManagerException {
		List<PCRFServicePolicySyGatewayRelData> pcrfServicePolicySyGatewayRelDataList = null;
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ServicePolicyDataManager servicePolicyDataManager = getServicePolicyDataManager(session);
		if(servicePolicyDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{        	
        	session.beginTransaction();	
        	pcrfServicePolicySyGatewayRelDataList = servicePolicyDataManager.getPCRFPolicySyGatewayList(pcrfID);
        }catch(Exception e){
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }
        
		return pcrfServicePolicySyGatewayRelDataList;
	}

	public List<PCRFPolicyCDRDriverRelData> getPCRFPolicyCDRDriverList(long pcrfID) throws DataManagerException {
		List<PCRFPolicyCDRDriverRelData> pcrfPolicyCDRDriverRelDataList = null;
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ServicePolicyDataManager servicePolicyDataManager = getServicePolicyDataManager(session);
        		
		if(servicePolicyDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	pcrfPolicyCDRDriverRelDataList = servicePolicyDataManager.getPCRFPolicyCDRDriverList(pcrfID);                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }		
		return pcrfPolicyCDRDriverRelDataList;
	}
	
	public void update(PCRFServicePolicyData pcrfPolicyData,IStaffData staffData, String actionAlias) throws DataManagerException{
 		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ServicePolicyDataManager servicePolicyDataManager = getServicePolicyDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if(servicePolicyDataManager == null && systemAuditDataManager == null){
			 throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		try{
			session.beginTransaction();
			servicePolicyDataManager.update(pcrfPolicyData);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();
		}catch(Exception exp){
			session.rollback();
			throw new DataManagerException("Action failed :"+exp.getMessage(),exp);
		}finally{
			session.close();
		}
	}
	
	
	public void delete(List<Long> pcrfPolicyIdList, IStaffData staffData, String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ServicePolicyDataManager servicePolicyDataManager = getServicePolicyDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if(servicePolicyDataManager == null && systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

		try{
			session.beginTransaction();
			if(pcrfPolicyIdList != null){
				servicePolicyDataManager.delete(pcrfPolicyIdList,actionAlias);				    	
				systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			}
			session.commit();
		}catch(ConstraintViolationException exp){
			try{
				session.rollback();
			}catch(Exception e){

			}
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
		}catch(Exception exp){
			session.rollback();
			throw new DataManagerException("Action failed :"+exp.getMessage(),exp);
		}finally{
			session.close();
		}
	}
	
	public void updatePCRFPolicyStatus(List<String> pcrfPolicyIds,String status,IStaffData staffData,String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ServicePolicyDataManager servicePolicyDataManager = getServicePolicyDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if(servicePolicyDataManager == null && systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		
		try{				     
			session.beginTransaction();
			servicePolicyDataManager.updateStatus(pcrfPolicyIds, status);
            systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();
		}catch(DataManagerException e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}catch(Exception e){
	       	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
	}
	
	public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
		return systemAuditDataManager; 
	}
	
	public List<PCRFServicePolicyData> getPCRFServicePolicyList()throws DataManagerException  {
		List<PCRFServicePolicyData> pcrfServicePolicyDataList = null;
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ServicePolicyDataManager servicePolicyDataManager = getServicePolicyDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if(servicePolicyDataManager == null && systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		
		try{				     
        	pcrfServicePolicyDataList = servicePolicyDataManager.getPCRFServicePolicyList();                                         
		}catch(DataManagerException e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}catch(Exception e){
	       	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
		return pcrfServicePolicyDataList;
	}
	/**
	 * reads Basic PkgData
	 * @return list of PkgData
	 * @throws DataManagerException
	 */
	public List<PkgData> getPkgDataList()throws DataManagerException{
		Connection connection = null;
		List<PkgData> pkgDataList = null;
		PreparedStatement preparedStmt = null;
		ResultSet resultSet = null;
		
		try {
			connection = DBConnectionManager.getInstance().getSMDatabaseConection();
			if(connection==null){
				throw new DataManagerException("Database connection not found");
			}
			String query = "SELECT ID,NAME FROM TBLM_PACKAGE  WHERE TYPE = ? AND PACKAGE_MODE IN (?,?) AND STATUS !=?" ;
				
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, PkgType.BASE.name());
			preparedStmt.setString(2, PkgMode.LIVE.val.toUpperCase());
			preparedStmt.setString(3, PkgMode.LIVE2.val.toUpperCase());
			preparedStmt.setString(4, CommonConstants.STATUS_DELETED);
			pkgDataList = new ArrayList<PkgData>();
			resultSet = preparedStmt.executeQuery();
			while(resultSet.next()){
				PkgData pkgData = new PkgData();
				pkgData.setId(resultSet.getString("ID"));
				pkgData.setName(resultSet.getString("NAME"));
				pkgDataList.add(pkgData);
			}
		}catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStmt);
			DBUtility.closeQuietly(connection);
		}
		return pkgDataList;
	}
}