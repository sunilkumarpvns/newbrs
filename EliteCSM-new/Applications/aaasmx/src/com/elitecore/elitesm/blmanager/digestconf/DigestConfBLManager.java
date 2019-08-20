package com.elitecore.elitesm.blmanager.digestconf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.digestconf.DigestConfDataManager;
import com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData;
import com.elitecore.elitesm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.ws.rest.data.Status;

public class DigestConfBLManager extends BaseBLManager{

	private static final String MODULE = "DIGEST CONFIG BLMANAGER";


	public DigestConfDataManager getDigestConfDataManager(IDataManagerSession session) { 
		DigestConfDataManager digestConfDataManager = (DigestConfDataManager) DataManagerFactory.getInstance().getDataManager(DigestConfDataManager.class, session);
		return digestConfDataManager;
	}

	public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
		return systemAuditDataManager; 
	}

	public List<DigestConfigInstanceData> getDigestConfigInstanceList() throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		List<DigestConfigInstanceData> digestConfigInstanceDataList=null;
		try{
			DigestConfDataManager digestConfDataManager = getDigestConfDataManager(session);
			if(digestConfDataManager==null){
				throw new DataManagerException("Data Manager Not Found: DigestConfDataManager.");
			}
			digestConfigInstanceDataList = digestConfDataManager.getDigestConfigInstanceList();
			return digestConfigInstanceDataList;
		}catch(DataManagerException e){
			Logger.logTrace(MODULE, "Error in during data operation : "+e.getMessage());
			Logger.logTrace(MODULE, e);
			throw e;
		}catch(Exception e){
			Logger.logTrace(MODULE, "Error : "+e.getMessage());
			Logger.logTrace(MODULE, e);
			e.printStackTrace();
			new DataManagerException(e.getMessage(),e);
		}
		return digestConfigInstanceDataList;
	}
	
	public void create(DigestConfigInstanceData digestConfInstanceData, IStaffData staffData) throws DataManagerException{
		List<DigestConfigInstanceData> digestConfigInstanceDataList = new ArrayList<DigestConfigInstanceData>();
		digestConfigInstanceDataList.add(digestConfInstanceData);
		create(digestConfigInstanceDataList, staffData, "");
	}
	
	public Map<String, List<Status>> create(List<DigestConfigInstanceData> digestConfigInstanceDataList, IStaffData staffData, String partialSuccess) throws DataManagerException{
		return insertRecords(DigestConfDataManager.class, digestConfigInstanceDataList, staffData, ConfigConstant.CREATE_DIGEST_CONFIGURATION, partialSuccess);
	}

	public PageList search(DigestConfigInstanceData digestConfigInstanceData,int requiredPageNo, Integer pageSize) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DigestConfDataManager digestConfDataManager = getDigestConfDataManager(session);

		PageList pageList = null;
		try{
			if (digestConfDataManager == null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
			pageList = digestConfDataManager.search(digestConfigInstanceData,requiredPageNo,pageSize); 

		} catch(DataManagerException exp){
			rollbackSession(session);
			Logger.logError(MODULE, "Search Action failed. Reason : " + exp.getMessage());
			throw exp;
		} catch(Exception exp) {
			rollbackSession(session);
			Logger.logError(MODULE, "Search Action failed. Reason : " + exp.getMessage());
			exp.printStackTrace();
			throw new DataManagerException("Failed to search Digest Configuration, Reason:"+exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
		return pageList;
	}

	public void deleteByName(List<String> digestConfs, IStaffData staffData) throws DataManagerException{
		delete(digestConfs, staffData, BY_NAME);
	}
	
	public void deleteById(List<String> digestConfs, IStaffData staffData) throws DataManagerException{
		delete(digestConfs, staffData, BY_ID);
	}
	
	private void delete(List<String> digestConfs, IStaffData staffData, boolean byIdOrName) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DigestConfDataManager dataManager = getDigestConfDataManager(session);
		String digestConfigurationName = null;

		if (dataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();

			if (Collectionz.isNullOrEmpty(digestConfs) == false) {
				for (int i=0;i<digestConfs.size();i++) {
					if (digestConfs.get(i) != null) {
						String idOrName = digestConfs.get(i).toString().trim();
						if ( byIdOrName ) {
							digestConfigurationName = dataManager.deleteById(idOrName);
						} else {
							digestConfigurationName = dataManager.deleteByName(idOrName);			
						}
						staffData.setAuditName(digestConfigurationName);
						AuditUtility.doAuditing(session,staffData, ConfigConstant.DELETE_DIGEST_CONFIGURATION);
					}
				}
				commit(session);
			}
		} catch ( DataManagerException dme ){
			rollbackSession(session);
			throw dme;
		} catch (Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(),exp);
		} finally { 
			closeSession(session);
		}
	}
	
	public DigestConfigInstanceData getDigestConfigDataById(String digestConfigId) throws DataManagerException{
		return getDigestConfigData(digestConfigId,BY_ID);
	}
	
	public DigestConfigInstanceData getDigestConfigDataByName(String digestConfigName) throws DataManagerException{
		return getDigestConfigData(digestConfigName,BY_NAME);
	}
	
	private DigestConfigInstanceData getDigestConfigData(Object digestConfigInstanceIdOrName,boolean byIdOrName) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DigestConfDataManager digestConfDataManager = getDigestConfDataManager(session);
		DigestConfigInstanceData instanceData = null;

		if (digestConfDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			if( byIdOrName ){
				instanceData = digestConfDataManager.getDigestConfigInstDataById((String)(digestConfigInstanceIdOrName)); 
			}else{
				instanceData = digestConfDataManager.getDigestConfigInstDataByName((String)digestConfigInstanceIdOrName); 
			}
		} catch(DataManagerException exp){
			Logger.logError(MODULE, "Data Manager implementation not found for Digest Configuration. Reason : " + exp.getMessage());
			throw exp;
		} catch(Exception exp) {
			Logger.logError(MODULE, "Failed to search Digest Configration. Reason : " + exp.getMessage());
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
		return instanceData;
	}

	public void updateDigestConfigurationByName(DigestConfigInstanceData digestConfigInstanceData,IStaffData staffData, String queryOrPathParam) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DigestConfDataManager digestConfDataManager = getDigestConfDataManager(session);

		if (digestConfDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			session.beginTransaction();
			
			if(queryOrPathParam == null){
				digestConfDataManager.updateById(digestConfigInstanceData, staffData, digestConfigInstanceData.getDigestConfId());
			}else{
				digestConfDataManager.updateByName(digestConfigInstanceData, staffData, queryOrPathParam);
			}
			commit(session);
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	public void updateDigestConfigurationById(DigestConfigInstanceData digestConfigInstanceData,IStaffData staffData) throws DataManagerException,DuplicateInstanceNameFoundException {
		updateDigestConfigurationByName(digestConfigInstanceData, staffData, null);
	}
	
	public String getDigestConfigInstDataNameFormId(String digestConfId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DigestConfDataManager digestConfDataManager = getDigestConfDataManager(session);
			return digestConfDataManager.getDigestConfigInstDataNameFormId(digestConfId);
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
}
