package com.elitecore.elitesm.blmanager.sessionmanager;

import java.util.ArrayList;
import java.util.Iterator;
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
import com.elitecore.elitesm.datamanager.datasource.database.DatabaseDSDataManager;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.sessionmanager.ASMDataManager;
import com.elitecore.elitesm.datamanager.sessionmanager.SessionManagerDataManager;
import com.elitecore.elitesm.datamanager.sessionmanager.data.IASMData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISMConfigInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SMDBFieldMapData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SMSessionCloserESIRelData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SessionManagerDBConfiguration;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SessionManagerInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.exception.DataNotInSyncException;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.PasswordEncryption;


public class SessionManagerBLManager extends BaseBLManager{

	private static final String MODULE = "SESSION MANAGER BLMANAGER";


	private SessionManagerDataManager getSessionManagerDataManager(IDataManagerSession session) { 
		SessionManagerDataManager sessionManagerDataManager = (SessionManagerDataManager) DataManagerFactory.getInstance().getDataManager(SessionManagerDataManager.class, session);
		return sessionManagerDataManager;
	}
	private DatabaseDSDataManager getDatabaseDSDataManager(IDataManagerSession session) { 
		DatabaseDSDataManager sessionManagerDataManager = (DatabaseDSDataManager) DataManagerFactory.getInstance().getDataManager(DatabaseDSDataManager.class, session);
		return sessionManagerDataManager;
	}


	public ASMDataManager getASMDataManager(IDataManagerSession session) { 
		ASMDataManager ASMDataManager = (ASMDataManager) DataManagerFactory .getInstance().getDataManager(ASMDataManager.class, session);
		return ASMDataManager;
	}

	public void create(SessionManagerInstanceData sessionManagerInstanceData, IStaffData staffData) throws DataManagerException {
		List<SessionManagerInstanceData> sessionManagerInstanceDataList = new ArrayList<SessionManagerInstanceData>();
		sessionManagerInstanceDataList.add(sessionManagerInstanceData);
		create(sessionManagerInstanceDataList, staffData, "false");
	}

	public Map<String, List<Status>> create(List<SessionManagerInstanceData> sessionMangerInstanceDataList, IStaffData staffData, String partialSuccess) throws DataManagerException{

		for (SessionManagerInstanceData sessionManagerInstanceData : sessionMangerInstanceDataList) {

			sessionManagerInstanceData.setCreatedbystaffid(staffData.getStaffId());
			sessionManagerInstanceData.setCreatedate(getCurrentTimeStemp());
		}
		return insertRecords(SessionManagerDataManager.class, sessionMangerInstanceDataList, staffData, ConfigConstant.CREATE_SESSION_MANAGER, partialSuccess);
	}

	public PageList search(ISessionManagerInstanceData sessionManagerInstance,int requiredPageNo, Integer pageSize,IStaffData staffData)throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SessionManagerDataManager sessionManagerDataManager = getSessionManagerDataManager(session);

		PageList lstSessionManagerList;

		if (sessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();	

			lstSessionManagerList = sessionManagerDataManager.search(sessionManagerInstance, requiredPageNo, pageSize);
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_SESSION_MANAGER);
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return lstSessionManagerList; 
	}

	public List<ISessionManagerInstanceData> getSessionManagerInstanceList() throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SessionManagerDataManager sessionManagerDataManager = getSessionManagerDataManager(session);
		
		List<ISessionManagerInstanceData> sessionManagerInstanceDataList=null;

		if (sessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			sessionManagerInstanceDataList = sessionManagerDataManager.getSessionManagerInstanceList();
		}catch(DataManagerException e){
			Logger.logTrace(MODULE, "Error in during data operation : "+e.getMessage());
			Logger.logTrace(MODULE, e);
			throw e;
		}catch(Exception e){
			Logger.logTrace(MODULE, "Error : "+e.getMessage());
			Logger.logTrace(MODULE, e);
			e.printStackTrace();
			new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return sessionManagerInstanceDataList;
	}

	public List<ISessionManagerInstanceData> getSessionManagerInstanceList(String smtype) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SessionManagerDataManager sessionManagerDataManager = getSessionManagerDataManager(session);
		
		List<ISessionManagerInstanceData> sessionManagerInstanceDataList=null;
		
		if (sessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			sessionManagerInstanceDataList = sessionManagerDataManager.getSessionManagerInstanceList(smtype);
		}catch(DataManagerException e){
			Logger.logTrace(MODULE, "Error in during data operation : "+e.getMessage());
			Logger.logTrace(MODULE, e);
			throw e;
		}catch(Exception e){
			Logger.logTrace(MODULE, "Error : "+e.getMessage());
			Logger.logTrace(MODULE, e);
			e.printStackTrace();
			new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return sessionManagerInstanceDataList;
	}

	public void closeSession(Long userId,SessionManagerDBConfiguration dbConfiguration,IStaffData staffData) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ASMDataManager asmDataManager = getASMDataManager(session);

		if(asmDataManager == null ){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			session.beginTransaction();
			asmDataManager.closeSession(userId,dbConfiguration);
			AuditUtility.doAuditing(session, staffData, ConfigConstant.CLOSE_SESSION);
			commit(session);
		}catch(DataNotInSyncException dnisExp){
			dnisExp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException("Database Not in Sync., Lock exists on some database rows : " + dnisExp.getMessage());
		}catch (DataManagerException exp) {
			rollbackSession(session);
			throw exp;
		}catch (Exception e) {
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	public void closeSession(String userIds[],SessionManagerDBConfiguration dbConfiguration,IStaffData staffData) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ASMDataManager asmDataManager = getASMDataManager(session);

		if(asmDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			session.beginTransaction();

			asmDataManager.closeSession(userIds,dbConfiguration);
			AuditUtility.doAuditing(session, staffData, ConfigConstant.CLOSE_SESSION);
			commit(session);
		}catch(DataNotInSyncException dnisExp){
			dnisExp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException("Database Not in Sync., Lock exists on some database rows : " + dnisExp.getMessage());
		}catch (DataManagerException exp) {
			rollbackSession(session);
			throw exp;
		}catch (Exception e) {
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public void closeAllSession(IASMData asmData,SessionManagerDBConfiguration dbConfiguration,IStaffData staffData) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ASMDataManager asmDataManager = getASMDataManager(session);

		if(asmDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			session.beginTransaction();	

			asmDataManager.closeAllSession(asmData,dbConfiguration);
			AuditUtility.doAuditing(session, staffData,ConfigConstant.CLOSE_ALL_SESSION);
			commit(session);
		}catch(DataNotInSyncException dnisExp){
			dnisExp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException("Database Not in Sync., Lock exists on some database rows : " + dnisExp.getMessage());
		}catch (DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch (Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public PageList search(IASMData asmData, int pageNo, int pageSize,SessionManagerDBConfiguration dbConfiguration) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ASMDataManager asmDataManager = getASMDataManager(session);
		PageList lstASMList;

		if (asmDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			lstASMList = asmDataManager.search(asmData, pageNo, pageSize,dbConfiguration);
			return lstASMList;
		}catch(DataManagerException e) {
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public PageList searchASM(IASMData asmData, int pageNo, int pageSize,SessionManagerDBConfiguration dbConfiguration ,IStaffData staffData) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ASMDataManager asmDataManager = getASMDataManager(session);
		PageList lstASMList;

		if (asmDataManager == null ){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			session.beginTransaction();

			lstASMList = asmDataManager.search(asmData, pageNo, pageSize,dbConfiguration);
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_ACTIVE_SESSION);
			commit(session);
		}catch(DataManagerException e) {
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return lstASMList;
	}

	public PageList search(IASMData asmData,SessionManagerDBConfiguration dbConfiguration) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ASMDataManager asmDataManager = getASMDataManager(session);
		PageList lstASMList;

		if(asmDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {
			lstASMList = asmDataManager.search(asmData,dbConfiguration);
			return lstASMList;
		}catch(DataManagerException e) {
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public PageList searchGroupByCriteria(IASMData asmData, int pageNo, int pageSize,IStaffData staffData, String actionAlias,SessionManagerDBConfiguration dbConfiguration) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ASMDataManager asmDataManager = getASMDataManager(session);
		PageList lstASMListGroupBy;

		if ( asmDataManager == null ){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			session.beginTransaction();
			lstASMListGroupBy = asmDataManager.searchGroupByCriteria(asmData, pageNo, pageSize,dbConfiguration);

			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_SESSION);
			commit(session);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return lstASMListGroupBy;
	}

	public List<Map<String,Object>> purgeClosedSession(SessionManagerDBConfiguration dbConfiguration,IStaffData staffData) throws DataManagerException{
		List<Map<String,Object>> purgedSessionsList = new ArrayList<Map<String,Object>>();
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ASMDataManager asmDataManager = getASMDataManager(session);

		if(asmDataManager == null ){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			session.beginTransaction();

			purgedSessionsList = asmDataManager.purgeClosedSession(dbConfiguration);
			AuditUtility.doAuditing(session, staffData, ConfigConstant.PURGE_CLOSED_SESSION);
			commit(session);
		}catch(DataNotInSyncException dnisExp){
			dnisExp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException("Database Not in Sync., Lock exists on some database rows : " + dnisExp.getMessage());
		}catch (DataManagerException exp) {
			rollbackSession(session);
			throw exp;
		}catch (Exception e) {
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return purgedSessionsList;
	}

	public void purgeAllSession(SessionManagerDBConfiguration dbConfiguration,IStaffData staffData) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ASMDataManager asmDataManager = getASMDataManager(session);

		if(asmDataManager == null ){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			session.beginTransaction();

			asmDataManager.purgeAllSession(dbConfiguration);
			AuditUtility.doAuditing(session, staffData, ConfigConstant.PURGE_ALL_SESSION);
			commit(session);
		}catch(DataNotInSyncException dnisExp){
			dnisExp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException("Database Not in Sync., Lock exists on some database rows : " + dnisExp.getMessage());
		}catch (DataManagerException exp) {
			rollbackSession(session);
			throw exp;
		}catch (Exception e) {
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public SessionManagerDBConfiguration getSessionManagerDBConfiguration(String smInstanceId) throws DataManagerException, Exception, DecryptionNotSupportedException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SessionManagerDBConfiguration sessionManagementDBConfiguration=null;
		
		SessionManagerDataManager sessionDataManager = getSessionManagerDataManager(session);
		DatabaseDSDataManager databaseDataManager = getDatabaseDSDataManager(session);
		
		if(sessionDataManager== null || databaseDataManager==null){
			throw new DataManagerException("Data Manager implementation not found.");
		}
		
		ISessionManagerInstanceData sessionManagerInstanceData = sessionDataManager.getSessionManagerInstanceDataById(smInstanceId);
		ISMConfigInstanceData smConfigInstanceData = sessionDataManager.getSMConfigInstanceData(sessionManagerInstanceData);
		
		if(smConfigInstanceData==null){
			throw new DataManagerException("Session Manager Data not found");
		}
		try{

			IDatabaseDSData databaseDSData = databaseDataManager.getDatabaseDSDataById(smConfigInstanceData.getDatabaseDatasourceId());

			sessionManagementDBConfiguration=new SessionManagerDBConfiguration();

			sessionManagementDBConfiguration.setConnectionUrl(databaseDSData.getConnectionUrl());
			sessionManagementDBConfiguration.setUserName(databaseDSData.getUserName());

			/* decrypt User password */
			String decryptedPassword = PasswordEncryption.getInstance().decrypt(databaseDSData.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
			databaseDSData.setPassword(decryptedPassword);

			sessionManagementDBConfiguration.setPassword(databaseDSData.getPassword());
			sessionManagementDBConfiguration.setIdentityField(smConfigInstanceData.getIdentityField());
			sessionManagementDBConfiguration.setLastUpdateTimeField(smConfigInstanceData.getLastUpdatedTimeField());
			sessionManagementDBConfiguration.setStartTimeField(smConfigInstanceData.getStartTimeField());
			sessionManagementDBConfiguration.setSequenceName(smConfigInstanceData.getIdSequenceName());
			sessionManagementDBConfiguration.setSessionIdField(smConfigInstanceData.getSessionIdField());
			sessionManagementDBConfiguration.setTableName(smConfigInstanceData.getTablename());
			sessionManagementDBConfiguration.setGroupNameField(smConfigInstanceData.getGroupNameField());
			sessionManagementDBConfiguration.setNasPortTypeField(smConfigInstanceData.getServiceTypeField());    

			List tempList = new ArrayList();
			Iterator<SMDBFieldMapData> itr = smConfigInstanceData.getDbFieldMapDataList().iterator();

			while(itr.hasNext()){
				tempList.add(itr.next());
			}    		
			sessionManagementDBConfiguration.setFieldMappingList(tempList);

		}catch(DataManagerException de){
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return sessionManagementDBConfiguration;
	}

	public void updateSessionManagerBasicDetails(ISessionManagerInstanceData sessionManagerInstanceData,IStaffData staffData, String actionAlias) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SessionManagerDataManager sessionManagerDataManager = getSessionManagerDataManager(session);

		if (sessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			sessionManagerInstanceData.setLastmodifiedbystaffid(staffData.getStaffId());

			session.beginTransaction();
			sessionManagerDataManager.updateSessionManagerBasicDetails(sessionManagerInstanceData,staffData,actionAlias);
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			Logger.logError(MODULE, "Failed to update Session Manager. Reason : " + exp.getMessage());
			throw exp;
		}catch(Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			Logger.logError(MODULE, "Failed to update Session Manager. Reason : " + exp.getMessage());
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
	}
	
	public void updateSessionManagerDetails(ISessionManagerInstanceData sessionManagerInstanceData,IStaffData staffData, String actionAlias) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SessionManagerDataManager sessionManagerDataManager = getSessionManagerDataManager(session);

		if (sessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			sessionManagerInstanceData.setLastmodifiedbystaffid(staffData.getStaffId());

			session.beginTransaction();
			sessionManagerDataManager.updateSessionManagerDetails(sessionManagerInstanceData,staffData,actionAlias);
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			Logger.logError(MODULE, "Failed to update Session Manager. Reason : " + exp.getMessage());
			throw exp;
		}catch(Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			Logger.logError(MODULE, "Failed to update Session Manager. Reason : " + exp.getMessage());
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
	}

	public List<SMSessionCloserESIRelData> getNASSessionCloserESIRelList(String smConfigId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SessionManagerDataManager sessionManagerDataManager = getSessionManagerDataManager(session);
		
		List<SMSessionCloserESIRelData> sessionCloserESIRelList=null;
		
		if (sessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			sessionCloserESIRelList = sessionManagerDataManager.getNASSessionCloserESIRelList(smConfigId);
		}catch(DataManagerException e){
			Logger.logTrace(MODULE, "Error in during data operation : "+e.getMessage());
			Logger.logTrace(MODULE, e);
			throw e;
		}catch(Exception e){
			Logger.logTrace(MODULE, "Error : "+e.getMessage());
			Logger.logTrace(MODULE, e);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return sessionCloserESIRelList;
	}

	public List<SMSessionCloserESIRelData> getAcctSessionCloserESIRelList(String smConfigId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SessionManagerDataManager sessionManagerDataManager = getSessionManagerDataManager(session);
		
		List<SMSessionCloserESIRelData> sessionCloserESIRelList=null;
		
		if (sessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			sessionCloserESIRelList = sessionManagerDataManager.getAcctSessionCloserESIRelList(smConfigId);
		}catch(DataManagerException e){
			Logger.logTrace(MODULE, "Error in during data operation : "+e.getMessage());
			Logger.logTrace(MODULE, e);
			throw e;
		}catch(Exception e){
			Logger.logTrace(MODULE, "Error : "+e.getMessage());
			Logger.logTrace(MODULE, e);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return sessionCloserESIRelList;
	}

	public String getSessionManagerInstanceDataNameFromId(String sessionManagerId) throws DataManagerException {
		ISessionManagerInstanceData sessionManagerInstanceData = getSessionManagerDataById(sessionManagerId);

		String name = "";
		if(sessionManagerInstanceData != null){
			name = sessionManagerInstanceData.getName();
		}
		return name;
	}

	public ISessionManagerInstanceData getSessionManagerDataById(String sessionManagerId) throws DataManagerException {
		return getSessionManagerData(sessionManagerId, BY_ID);
	}
	public ISessionManagerInstanceData getSessionManagerDataByName(String sessionManagerName) throws DataManagerException {
		return getSessionManagerData(sessionManagerName, BY_NAME);
	}

	private ISessionManagerInstanceData getSessionManagerData(Object sessionManagerDataByIdOrName,boolean byIdOrName) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SessionManagerDataManager sessionManagerDataManager = getSessionManagerDataManager(session);
		
		ISessionManagerInstanceData sessionManagerInstanceData =null;
		
		if (sessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			if( byIdOrName ){
				sessionManagerInstanceData = sessionManagerDataManager.getSessionManagerInstanceDataById((String)sessionManagerDataByIdOrName);
			} else {
				sessionManagerInstanceData = sessionManagerDataManager.getSessionManagerInstanceDataByName((String)sessionManagerDataByIdOrName); 
			}
			
			return sessionManagerInstanceData;
			
		}catch(DataManagerException exp){
			Logger.logError(MODULE, "Faield to retrive Session Manager data. Reason : " + exp.getMessage());
			throw exp;
		}catch(Exception exp) {
			exp.printStackTrace();
			Logger.logError(MODULE, "Faield to retrive Session Manager data. Reason : " + exp.getMessage());
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
	}

	public void updateSessionManagerInstanceDataByName(SessionManagerInstanceData sessionManagerInstaceData,
			StaffData staffData, String byName) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		SessionManagerDataManager sessionManagerDataManager = getSessionManagerDataManager(session);

		if (sessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			sessionManagerInstaceData.setLastmodifiedbystaffid(staffData.getStaffId());
			session.beginTransaction();
			sessionManagerDataManager.updateSessionManagerData(sessionManagerInstaceData,staffData,byName.trim());
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			Logger.logError(MODULE, "Failed to update Session Manager. Reason : " + exp.getMessage());
			throw exp;
		}catch(Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			Logger.logError(MODULE, "Failed to update Session Manager. Reason : " + exp.getMessage());
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
	}

	public void deleteById(List<String> sessionManagerNames,IStaffData staffData) throws DataManagerException {
		delete(sessionManagerNames, staffData, BY_ID);
	}

	public void deleteByName(List<String> sessionManagerNames,IStaffData staffData) throws DataManagerException {
		delete(sessionManagerNames, staffData, BY_NAME);
	}

	private void delete(List<String> sessionManagerNames,IStaffData staffData, boolean byIdOrName) throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		SessionManagerDataManager sessionManagerDataManager= getSessionManagerDataManager(session);
		String sessionManagerName = null;

		if(sessionManagerDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName()); 
		}

		try{
			session.beginTransaction();
			if (Collectionz.isNullOrEmpty(sessionManagerNames) == false) {
				int size = sessionManagerNames.size();
				for (int i=0;i<size;i++) {
					if (sessionManagerNames.get(i) != null) {
						String idOrName = sessionManagerNames.get(i).toString().trim();
						if ( byIdOrName ) {
							sessionManagerName = sessionManagerDataManager.deleteById(idOrName);
						} else {
							sessionManagerName = sessionManagerDataManager.deleteByName(idOrName);			
						}
						staffData.setAuditName(sessionManagerName);
						AuditUtility.doAuditing(session,staffData, ConfigConstant.DELETE_SESSION_MANAGER);
					}
				}
				commit(session);
			}
		}catch(DataManagerException dme){
			Logger.logError(MODULE, "Failed to delete Session Manager. Reason : " + dme.getMessage());
			rollbackSession(session);
			throw dme;
		}catch(Exception exp){
			exp.printStackTrace();
			Logger.logError(MODULE, "Failed to delete Session Manager. Reason : " + exp.getMessage());
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
	}
}
