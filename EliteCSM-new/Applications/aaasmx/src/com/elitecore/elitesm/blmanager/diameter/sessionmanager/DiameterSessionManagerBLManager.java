/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DiameterpolicyBLManager.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.blmanager.diameter.sessionmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.DiameterSessionManagerDataManager;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerMappingData;
import com.elitecore.elitesm.sqlexception.EliteSQLGrammerException;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;

public class DiameterSessionManagerBLManager extends BaseBLManager {
	
	public DiameterSessionManagerDataManager getDiameterSessionManagerDataManager(IDataManagerSession session) { 
		DiameterSessionManagerDataManager diameterSessionManagerDataManager = (DiameterSessionManagerDataManager) DataManagerFactory 
				.getInstance().getDataManager(DiameterSessionManagerDataManager.class, session);
		return diameterSessionManagerDataManager;
	}
	
	public PageList search(DiameterSessionManagerData diameterSessionManagerData,int requiredPageNo, Integer pageSize, IStaffData staffData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterSessionManagerDataManager diameterSessionManagerDataManager = getDiameterSessionManagerDataManager(session);
		PageList diameterSessionManagerList;
		
		if (diameterSessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			session.beginTransaction();	
			diameterSessionManagerList = diameterSessionManagerDataManager.search(diameterSessionManagerData, requiredPageNo,pageSize);
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_DIAMETER_SESSION_MANAGER);
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		} catch(Exception exp) {
			rollbackSession(session);
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}finally{
			closeSession(session);
		}
		return diameterSessionManagerList; 
	}
	
	public void create(DiameterSessionManagerData diameterSessionManagerData, IStaffData staffData) throws DataManagerException {
		List<DiameterSessionManagerData> sessionManagerInstanceDataList = new ArrayList<DiameterSessionManagerData>();
		sessionManagerInstanceDataList.add(diameterSessionManagerData);
		create(sessionManagerInstanceDataList, staffData, "false");
	}
	
	
	public Map<String, List<Status>> create(List<DiameterSessionManagerData> diameterSessionManagerDataList, IStaffData staffData, String partialSuccess) throws DataManagerException{
		return insertRecords(DiameterSessionManagerDataManager.class, diameterSessionManagerDataList, staffData, ConfigConstant.CREATE_DIAMETER_SESSION_MANAGER, partialSuccess);
	}
	
	
	public void deleteDiameterSessionManagerByID(List<String> sessionManagerIDs,IStaffData staffData) throws DataManagerException {
		deleteDiameterSessionManager(sessionManagerIDs, staffData, BY_ID);
	}

	public void deleteDiameterSessionManagerByName(List<String> sessionManagerNames,IStaffData staffData) throws DataManagerException {
		deleteDiameterSessionManager(sessionManagerNames, staffData, BY_NAME);
	}
	
	private void deleteDiameterSessionManager(List<String> diameterSessionManagerIdOrName, IStaffData staffData , boolean byIdOrName) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterSessionManagerDataManager diameterSessionManagerDataManager = getDiameterSessionManagerDataManager(session);
		String diameterSessionManagerName = null;
		if(diameterSessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName()); 
		}

		try{
			session.beginTransaction();
			if (Collectionz.isNullOrEmpty(diameterSessionManagerIdOrName) == false) {
				int size = diameterSessionManagerIdOrName.size();
				for (int i=0;i<size;i++) {
					if (diameterSessionManagerIdOrName.get(i) != null) {
						String idOrName = diameterSessionManagerIdOrName.get(i).toString().trim();
						if ( byIdOrName ) {
							diameterSessionManagerName = diameterSessionManagerDataManager.deleteById(idOrName);
						} else {
							diameterSessionManagerName = diameterSessionManagerDataManager.deleteByName(idOrName);			
						}
						staffData.setAuditName(diameterSessionManagerName);
						AuditUtility.doAuditing(session,staffData, ConfigConstant.DELETE_SESSION_MANAGER);
					}
				}
				commit(session);
			}
		}catch(DataManagerException dme){
			rollbackSession(session);
			throw dme;
		}catch(Exception exp){
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
	}
	
	public DiameterSessionManagerData getDiameterSessionManagerDataById(String sessionManagerId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		DiameterSessionManagerDataManager diameterSessionManagerDataManager = getDiameterSessionManagerDataManager(session);
		
		if(diameterSessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			DiameterSessionManagerData diameterSessionManagerData = diameterSessionManagerDataManager.getDiameterSessionManagerDataById(sessionManagerId);
			return diameterSessionManagerData;
		}catch(DataManagerException exp){
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			closeSession(session);
		}
	}
	
	public DiameterSessionManagerData getDiameterSessionManagerDataByName(String sessionManagerName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		DiameterSessionManagerDataManager diameterSessionManagerDataManager = getDiameterSessionManagerDataManager(session);
		
		if(diameterSessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			DiameterSessionManagerData diameterSessionManagerData = diameterSessionManagerDataManager.getDiameterSessionManagerDataByName(sessionManagerName.trim());
			return diameterSessionManagerData;
		}catch(DataManagerException exp){
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			closeSession(session);
		}
	}
	
	public DiameterSessionManagerMappingData getDiameterSessionManagerMappingDataById(String mappingId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		DiameterSessionManagerDataManager diameterSessionManagerDataManager = getDiameterSessionManagerDataManager(session);
		
		if(diameterSessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			DiameterSessionManagerMappingData diameterSessionManagerMappingData = diameterSessionManagerDataManager.getDiameterSessionManagerMappingData(mappingId);
			return diameterSessionManagerMappingData;
		}catch(DataManagerException exp){
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			closeSession(session);
		}
	}
	
	public List<DiameterSessionManagerData> getDiameterSessionManagerDatas() throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterSessionManagerDataManager diameterSessionManagerDataManager = getDiameterSessionManagerDataManager(session);
			
		List<DiameterSessionManagerData> diameterSessionManagerData;

		if (diameterSessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			diameterSessionManagerData = diameterSessionManagerDataManager.getDiameterSessionManagerDatas();
		}catch(DataManagerException exp){
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			closeSession(session);
		}

		return diameterSessionManagerData; 
	}

	public void update(DiameterSessionManagerData diameterSessionManagerData,IStaffData staffData, String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		DiameterSessionManagerDataManager diameterSessionManagerDataManager = getDiameterSessionManagerDataManager(session);
		
		if(diameterSessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			session.beginTransaction();
			diameterSessionManagerDataManager.update(diameterSessionManagerData,staffData,actionAlias);
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
	}

	public void updateScenarioData(DiameterSessionManagerData diameterSessionManagerData,IStaffData staffData, String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		DiameterSessionManagerDataManager diameterSessionManagerDataManager = getDiameterSessionManagerDataManager(session);

		if(diameterSessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		try{
			session.beginTransaction();
			diameterSessionManagerDataManager.updateScenarioData(diameterSessionManagerData,staffData,actionAlias);
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
	}
	
	public void updataDiameterSessionManager(DiameterSessionManagerData diameterSessionManagerData,
			StaffData staffData, String name) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		DiameterSessionManagerDataManager diameterSessionManagerDataManager = getDiameterSessionManagerDataManager(session);

		if(diameterSessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			session.beginTransaction();
			diameterSessionManagerDataManager.updateDiameterSessionManagerData(diameterSessionManagerData,staffData,name.trim());
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
	}
	
	public List<DiameterSessionManagerMappingData> getDiameterSessionManagerMappingList(String sessionManagerId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		DiameterSessionManagerDataManager diameterSessionManagerDataManager = getDiameterSessionManagerDataManager(session);
		
		if(diameterSessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			List<DiameterSessionManagerMappingData> diameterSessionManagerMappingDataList = diameterSessionManagerDataManager.getDiameterSessionManagerMappingDataList(sessionManagerId);
			return diameterSessionManagerMappingDataList;
		}catch(DataManagerException exp){
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			closeSession(session);
		}
	}

	public PageList getASMDataByColumnName(String searchColumnList,String tableName ,int requiredPageNumber, Integer pageSize, IStaffData staffData, String actionAlias) throws DataManagerException,EliteSQLGrammerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		DiameterSessionManagerDataManager diameterSessionManagerDataManager = getDiameterSessionManagerDataManager(session);
		PageList pageList;

		if(diameterSessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			pageList = diameterSessionManagerDataManager.getASMDataByColumnName(searchColumnList, tableName ,requiredPageNumber, pageSize, staffData, actionAlias);
		}catch(DataManagerException exp){
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			closeSession(session);
		}
		return pageList;
	}

	public void closeSelectedSession(List<String> closedSessionSelectedList, String tableName) throws DataManagerException{
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterSessionManagerDataManager diameterSessionManagerDataManager = getDiameterSessionManagerDataManager(session);
		
		if(diameterSessionManagerDataManager==null){
			throw new DataManagerException("Data Manager Not Found: DiameterSessionManagerDataManager.");
		}
		
		try{
			session.beginTransaction();
			diameterSessionManagerDataManager.closeSelectedSession(closedSessionSelectedList, tableName);
			commit(session);;
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
	}

	public List<?> getASMDataByColumnName(String activeSessionId, String tableName) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		DiameterSessionManagerDataManager diameterSessionManagerDataManager = getDiameterSessionManagerDataManager(session);
		
		List<?> list;
		
		if(diameterSessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			list = diameterSessionManagerDataManager.getASMDataByColumnName(activeSessionId, tableName);
		}catch(DataManagerException exp){
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			closeSession(session);
		}
		return list;
	}

	public void resetViewableColumnsValue(String searchColumnName, String sessionManagerId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		DiameterSessionManagerDataManager diameterSessionManagerDataManager = getDiameterSessionManagerDataManager(session);
		
		if(diameterSessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();
			diameterSessionManagerDataManager.resetViewableColumnsValue(searchColumnName, sessionManagerId);
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
	}
}
