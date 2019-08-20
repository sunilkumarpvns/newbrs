package com.elitecore.elitesm.blmanager.servermgr.script;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermanager.script.ScriptDataManager;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptTypeData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.ws.rest.data.Status;

public class ScriptBLManager extends BaseBLManager{
	private static final String MODULE = "ScriptBLManager";

	private ScriptDataManager getScriptDataManager(IDataManagerSession session) {
		ScriptDataManager scriptDataManager = (ScriptDataManager)DataManagerFactory.getInstance().getDataManager(ScriptDataManager.class, session);
		return scriptDataManager;	
	}
	
	public Map<String, List<Status>> createScriptInstance(List<ScriptInstanceData> scriptInstDataList, IStaffData staffData, String partialSuccess) throws DataManagerException {
		return insertRecords(ScriptDataManager.class, scriptInstDataList, staffData, ConfigConstant.CREATE_SCRIPT, partialSuccess);
	}
	
	public void createScriptInstance(ScriptInstanceData scriptInstData, IStaffData staffData) throws DataManagerException {
		List<ScriptInstanceData> scriptInstanceDataList = new ArrayList<ScriptInstanceData>();
		scriptInstanceDataList.add(scriptInstData);
		createScriptInstance(scriptInstanceDataList, staffData, "false");
	}	
	
	public List<ScriptInstanceData> getListOfScriptInstData(List<String> scriptInstanceIdList) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ScriptDataManager scriptDataManager = getScriptDataManager(session);

		if (scriptDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			List<ScriptInstanceData> scriptInstDataList = scriptDataManager.getListOfScriptInstData(scriptInstanceIdList);
			return scriptInstDataList;
		}catch (DataManagerException de) {
			throw de;
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public PageList search(ScriptInstanceData scriptInstanceData,int requiredPageNo, Integer pageSize, IStaffData staffData) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ScriptDataManager scriptDataManager = getScriptDataManager(session);

		if (scriptDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		PageList scriptList = null;
		try{				
			scriptList = scriptDataManager.search(scriptInstanceData,requiredPageNo,pageSize);
			staffData.setAuditName(scriptInstanceData.getName());
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_SCRIPT);
			commit(session);
		}catch (DataManagerException de) {
			rollbackSession(session);
			throw de;
		} catch(Exception e){
			Logger.logTrace(MODULE, e);
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return scriptList;
	}
	
	public List<ScriptTypeData> getListOfAllScriptTypesData() throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();

		ScriptDataManager scriptDataManager = getScriptDataManager(session);

		if (scriptDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		List<ScriptTypeData> scriptTypeList = null;
		
		try {
			session.beginTransaction();
			scriptTypeList = scriptDataManager.getListOfAllScriptTypesData();
		} catch (DataManagerException de) {
			throw de;
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		
		return scriptTypeList;
	}

	public ScriptTypeData getScriptTypeDataById(String scriptTypeId) throws DataManagerException {
		 IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();

		ScriptDataManager scriptDataManager = getScriptDataManager(session);

		if (scriptDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		ScriptTypeData scriptTypeData = null;
		
		try {
			session.beginTransaction();
			scriptTypeData = scriptDataManager.getScriptTypeDataById(scriptTypeId);
		} catch (DataManagerException de) {
			throw de;
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		
		return scriptTypeData;
	}

	public ScriptInstanceData getScriptInstanceByName(String scriptName) throws DataManagerException {
		return getScriptInstanceData(scriptName.trim() , BY_NAME);
	}
	
	public ScriptInstanceData getScriptInstanceByScriptId(String scriptName) throws DataManagerException {
		return getScriptInstanceData(scriptName.trim(), BY_ID);
	}
	
	private ScriptInstanceData getScriptInstanceData(String scriptInstaceId, boolean getByIdOrName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ScriptDataManager scriptDataManager = getScriptDataManager(session);

		if (scriptDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			session.beginTransaction();
			
			return scriptDataManager.getScriptInstanceByScriptId(scriptInstaceId, getByIdOrName);
			
			
		}catch (DataManagerException de) {
			throw de;
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public List<ScriptData> getScriptDataByScriptInstanceId(String scriptId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ScriptDataManager scriptDataManager = getScriptDataManager(session);

		if (scriptDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			session.beginTransaction();
			List<ScriptData> scriptDataList = scriptDataManager.getScriptDataByScriptInstanceId(scriptId);
			return scriptDataList;
		}catch (DataManagerException de) {
			throw de;
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}

	}
	public void update(ScriptInstanceData scriptInstanceData, IStaffData staffData) throws DataManagerException {
		updateScriptByName(scriptInstanceData, staffData, null);
	}

	public void updateScriptByName(ScriptInstanceData scriptInstanceData, IStaffData staffData ,String name) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ScriptDataManager scriptDataManager = getScriptDataManager(session);

		if (scriptDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {
			session.beginTransaction();
			if(name != null) {
				scriptDataManager.updateScriptByName(scriptInstanceData, staffData, name);
			} else {
				scriptDataManager.updateScriptById(scriptInstanceData, staffData);
			}
			commit(session);
		}catch (DataManagerException de) {
			throw de;
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	public void updateScriptBasicDetails(ScriptInstanceData scriptInstanceData, IStaffData staffData ,String name) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ScriptDataManager scriptDataManager = getScriptDataManager(session);

		if (scriptDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {
			session.beginTransaction();
			
			scriptDataManager.updateScriptBasicDetails(scriptInstanceData, staffData, name);
			
			commit(session);
		}catch (DataManagerException de) {
			throw de;
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public ScriptData getScriptFileByName(String scriptFileName, String scriptId ) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ScriptDataManager scriptDataManager = getScriptDataManager(session);

		if (scriptDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {
			session.beginTransaction();
			return scriptDataManager.getScriptFileByName(scriptFileName, scriptId);
		}catch (DataManagerException de) {
			throw de;
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public void updateScriptFile(ScriptData scriptDataNewObject, IStaffData staffData, ScriptInstanceData scriptInstanceData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ScriptDataManager scriptDataManager = getScriptDataManager(session);

		if (scriptDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {
			session.beginTransaction();
			scriptDataManager.updateScriptFile(scriptDataNewObject, staffData, scriptInstanceData);
			commit(session);
		}catch (DataManagerException de) {
			throw de;
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public void deleteScriptByName(List<String> listOfName, IStaffData staffData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ScriptDataManager scriptDataManager = getScriptDataManager(session);
		
		if(scriptDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}

		try{
			session.beginTransaction();
			if(Collectionz.isNullOrEmpty(listOfName) == false){
				int size = listOfName.size();
				for(int i=0;i<size;i++){
					if(Strings.isNullOrBlank(listOfName.get(i)) == false){
						String strIdOrName = listOfName.get(i).trim();

						String name = null;
						name = scriptDataManager.deleteByName(strIdOrName);
						
						staffData.setAuditName(name);
						AuditUtility.doAuditing(session,staffData, ConfigConstant.DELETE_SCRIPT);
					}
				}
			}
			
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception exp){
			Logger.logTrace(MODULE, exp);
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(),exp);
		}finally{
			closeSession(session);
		}
	}
	
	public void deleteScriptById(List<String> asList, IStaffData staffData) throws DataManagerException {
		delete(asList, staffData, BY_ID);
	}

	public void delete(List<String> scriptInstanceIdOrName, IStaffData staffData, boolean deleteByIdOrName) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ScriptDataManager scriptDataManager = getScriptDataManager(session);

		if (scriptDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();
			List<String> scriptList = new ArrayList<String>();
			for (String scriptToDelete : scriptInstanceIdOrName) {
				if (deleteByIdOrName) {
					scriptList.add(scriptToDelete);
				} 
			}
			
			if (Collectionz.isNullOrEmpty(scriptList) == false) {
				
				List<String> scriptNames = scriptDataManager.delete(scriptList);
				if(Collectionz.isNullOrEmpty(scriptNames) == false){
					
					for(String strDriverName : scriptNames){
						staffData.setAuditName(strDriverName);
						AuditUtility.doAuditing(session,staffData, ConfigConstant.DELETE_SCRIPT);
					}
				}
			}
			
			commit(session);
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	
	public void updateScriptStatus(List<String> asList, String status) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{				     
			ScriptDataManager scriptDataManager = getScriptDataManager(session);

			if(scriptDataManager==null){
				throw new DataManagerException("Data Manager Not Found for " + getClass().getName());
			}

			session.beginTransaction();
			scriptDataManager.updateStatus(asList, status);
			commit(session);
		}catch (DataManagerException de) {
			rollbackSession(session);
			throw de;
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	/**
	 * @param scriptTypeId
	 * @return
	 * @throws DataManagerException
	 */
	public List<ScriptInstanceData> getScriptInstanceDataByTypeId(String scriptTypeId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();

		ScriptDataManager scriptDataManager = getScriptDataManager(session);

		if (scriptDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		List<ScriptInstanceData> scriptInstanceDataList = null;
		
		try {
			session.beginTransaction();
			scriptInstanceDataList = scriptDataManager.getScriptInstanceDataByTypeId(scriptTypeId);
		} catch (DataManagerException de) {
			throw de;
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		
		return scriptInstanceDataList;
	}
}