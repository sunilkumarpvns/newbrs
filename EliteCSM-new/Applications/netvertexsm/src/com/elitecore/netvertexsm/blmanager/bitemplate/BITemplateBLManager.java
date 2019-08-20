package com.elitecore.netvertexsm.blmanager.bitemplate;

import java.util.List;

import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.bitemplate.BITemplateDataManager;
import com.elitecore.netvertexsm.datamanager.bitemplate.data.BITemplateData;
import com.elitecore.netvertexsm.datamanager.bitemplate.data.BISubKeyData;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;

public class BITemplateBLManager {
	
	public BITemplateDataManager getBITemplateDataManager(IDataManagerSession session) {
		BITemplateDataManager biTemplateDataManager = (BITemplateDataManager) DataManagerFactory.getInstance().getDataManager(BITemplateDataManager.class, session);
		return biTemplateDataManager; 
	}

	public PageList search(BITemplateData biTemplateData,  int pageNo, int pageSize, IStaffData staffData,String actionAlias) throws DataManagerException {                 	   
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		BITemplateDataManager biTemplateDataManager = getBITemplateDataManager(session);
		PageList biTemplateDataList;

		if(biTemplateDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{        	
			biTemplateDataList = biTemplateDataManager.search(biTemplateData, pageNo, pageSize); 
			session.close();
		}catch(Exception e){
			session.close();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}
		return biTemplateDataList;       
	}

	public List<BITemplateData> getBITemplateList() throws DataManagerException {				
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		BITemplateDataManager biTemplateDataManager = getBITemplateDataManager(session);	
		List<BITemplateData> biTemplateDataList = null;

		if(biTemplateDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{        	
			session.beginTransaction();	        
			biTemplateDataList = biTemplateDataManager.getBITemplateList();                                         
			session.commit();
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}		
		return biTemplateDataList ;
	}

	public void deleteBITemplate(List<Long> biTemplateIDList, IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		BITemplateDataManager biTemplateDataManager = getBITemplateDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if (biTemplateDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			//createValidate(quotaManagerData);
			session.beginTransaction();        	        	
//			if(actionAlias.equals("DELETE_QUOTA_MANAGER_ACTION"))
			biTemplateDataManager.deleteBITemplate(biTemplateIDList, actionAlias);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
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

	public void create(BITemplateData biTemplateData, IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		BITemplateDataManager biTemplateDataManager = getBITemplateDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if (biTemplateDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();        	
			biTemplateDataManager.create(biTemplateData);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
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

	public BITemplateData getBITemplateData(BITemplateData biTemplateData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		BITemplateDataManager biTemplateDataManager = getBITemplateDataManager(session);
		@SuppressWarnings("unused")
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		BITemplateData biTempData = null;

		if(biTemplateDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{        	
			session.beginTransaction();	        
			biTempData = biTemplateDataManager.getBITemplateData(biTemplateData);
			
			session.commit();
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}		
		return biTempData;
	}
	public List<BISubKeyData> getBISubKeyList(Long biTemplateId,IStaffData staffData, String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		BITemplateDataManager biTemplateDataManager = getBITemplateDataManager(session);
		List<BISubKeyData> biTempRelList = null;
		if(biTemplateDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{        	
			biTempRelList = biTemplateDataManager.getBISubKeyList(biTemplateId);                  
		}catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}		
		return biTempRelList;
	}
	public List<BISubKeyData> getBISubKeyList(Long biTemplateId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		BITemplateDataManager biTemplateDataManager = getBITemplateDataManager(session);
		List<BISubKeyData> biTempRelList = null;

		if(biTemplateDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{        	
			session.beginTransaction();	        
			biTempRelList = biTemplateDataManager.getBISubKeyList(biTemplateId);                                         
			session.commit();
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}		
		return biTempRelList;
	}

	public void updateBITemplate(BITemplateData biTemplateData, IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		BITemplateDataManager biTemplateDataManager = getBITemplateDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if (biTemplateDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();        	
			biTemplateDataManager.updateBITemplate(biTemplateData);       	
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
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
	
	public void uploadFile(List<BISubKeyData> subKeyList, IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		BITemplateDataManager biTemplateDataManager = getBITemplateDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if (biTemplateDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();        	
			biTemplateDataManager.uploadFile(subKeyList);    
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
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

	public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
		return systemAuditDataManager; 
	}
}