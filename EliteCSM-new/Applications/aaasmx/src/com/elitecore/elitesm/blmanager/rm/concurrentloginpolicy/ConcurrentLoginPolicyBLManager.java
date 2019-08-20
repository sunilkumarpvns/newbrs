package com.elitecore.elitesm.blmanager.rm.concurrentloginpolicy;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.ConcurrentLoginPolicyDataManager;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.ConcurrentLoginPolicyData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.IConcurrentLoginPolicyData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;

public class ConcurrentLoginPolicyBLManager extends BaseBLManager{
	
	private static final String EMPTY = "";

	/**
	 * @return Returns Data Manager instance for concurrent login policy data.
	 */
	public ConcurrentLoginPolicyDataManager getConcurrentLoginPolicyDataManager(IDataManagerSession session) {
		ConcurrentLoginPolicyDataManager concurrentLoginPolcyDataManager = (ConcurrentLoginPolicyDataManager)DataManagerFactory.getInstance().getDataManager(ConcurrentLoginPolicyDataManager.class,session);
		return concurrentLoginPolcyDataManager; 
	}
	
	public void updateStatus(IConcurrentLoginPolicyData concurrentLoginPolicyData, List<String> lstConcurrentLoginPolicyIds, String commonStatusId, String reason,IStaffData staffData,String actionAlias) throws DataManagerException{
			IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			ConcurrentLoginPolicyDataManager concurrentLoginPolicyDataManager = getConcurrentLoginPolicyDataManager(session);
			
			if (concurrentLoginPolicyDataManager == null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
			
			try{
				session.beginTransaction();
				Date currentDate = new Date();
				
				if(lstConcurrentLoginPolicyIds!= null){
					for(int i=0;i<lstConcurrentLoginPolicyIds.size();i++){
						if(lstConcurrentLoginPolicyIds.get(i) != null){
							String concurrentPolicyId =  lstConcurrentLoginPolicyIds.get(i);
						    concurrentLoginPolicyDataManager.updateStatus(concurrentLoginPolicyData, concurrentPolicyId, commonStatusId, new Timestamp(currentDate.getTime()), staffData, actionAlias);  
						}
					}
				}
				commit(session);
			} catch (DataManagerException dme) {
				rollbackSession(session);
				throw dme;
			} catch (Exception e) {
				rollbackSession(session);
				e.printStackTrace();
				throw new DataManagerException(e.getMessage(), e);
			} finally {
				closeSession(session);
			}
		}
	
	public void updateBasicDetail(IConcurrentLoginPolicyData concurrentLoginPolicyData,IStaffData staffData,String actionAlias)throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ConcurrentLoginPolicyDataManager concurrentLoginPolicyDataManager = getConcurrentLoginPolicyDataManager(session);
		
		if(concurrentLoginPolicyDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}
		
		try{
			session.beginTransaction();
		
			concurrentLoginPolicyDataManager.updateBasicDetail(concurrentLoginPolicyData,staffData,actionAlias);
			
			commit(session);
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	public void updateAttributeDetail(IConcurrentLoginPolicyData concurrentLoginPolicyData, String concurrentLoginId,IStaffData staffData,String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ConcurrentLoginPolicyDataManager concurrentLoginPolicyDataManager = getConcurrentLoginPolicyDataManager(session);
		
		if(concurrentLoginPolicyDataManager == null){
    		throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}
		
		try{
			session.beginTransaction();
			
			concurrentLoginPolicyDataManager.updateAttributeDetail(concurrentLoginPolicyData,concurrentLoginId,staffData,actionAlias);
			
			commit(session);
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}

	
	public PageList searchConcurrentLoginPolicy(IConcurrentLoginPolicyData concurrentLoginPolicyData, IStaffData staffData, int pageNo, int pageSize) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ConcurrentLoginPolicyDataManager concurrentLoginPolicyDataManager = getConcurrentLoginPolicyDataManager(session);
		
		PageList lstConcurrentPolicyList; 
		
		if (concurrentLoginPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {
			session.beginTransaction();

			lstConcurrentPolicyList = concurrentLoginPolicyDataManager.searchConcurrentLoginPolicy(concurrentLoginPolicyData, pageNo, pageSize);

			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_CONCURRENT_LOGIN_POLICY_ACTION);

			commit(session);
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
		return lstConcurrentPolicyList;
	}
	
	
	public ConcurrentLoginPolicyData getConcurrentLoginPolicyById(String concurrentLoginPolicyId) throws DataManagerException {
		
		return getConcurrentLoginPolicy(concurrentLoginPolicyId, BY_ID, false);
		
	}
	
	public ConcurrentLoginPolicyData getConcurrentLoginPolicyByName(String concurrentLoginPolicyName, boolean caseSensitivity) throws DataManagerException {
		
		return getConcurrentLoginPolicy(concurrentLoginPolicyName.trim(), BY_NAME, caseSensitivity);
		
	}

	private ConcurrentLoginPolicyData getConcurrentLoginPolicy(Object concurrentLoginPolicyIdOrName, boolean isIdOrName, boolean caseSensitivity) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ConcurrentLoginPolicyDataManager concurrentLoginPolicyDataManager = getConcurrentLoginPolicyDataManager(session);

		if (concurrentLoginPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			
			ConcurrentLoginPolicyData concurrentLoginPolicy = null;
			
			if (isIdOrName) {
				concurrentLoginPolicy = concurrentLoginPolicyDataManager.getConcurrentLoginPolicyById((String) concurrentLoginPolicyIdOrName);
			} else {
				if(caseSensitivity){
					concurrentLoginPolicy = (ConcurrentLoginPolicyData) verifyNameWithIgnoreCase(ConcurrentLoginPolicyData.class, (String) concurrentLoginPolicyIdOrName, true);
				} else {
					concurrentLoginPolicy = concurrentLoginPolicyDataManager.getConcurrentLoginPolicyByName((String) concurrentLoginPolicyIdOrName);
				}
			}
			
			return concurrentLoginPolicy;
			
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}

	
	public void createConcurrentLoginPolicy(ConcurrentLoginPolicyData concurrentLoginPolicy, IStaffData staffData, boolean caseSensitivity) throws DataManagerException {

		List<ConcurrentLoginPolicyData> concurrentLoginPolicies = new ArrayList<ConcurrentLoginPolicyData>();
		concurrentLoginPolicies.add(concurrentLoginPolicy);
		createConcurrentLoginPolicy(concurrentLoginPolicies, staffData, EMPTY,caseSensitivity);
		
	}
	
	public Map<String, List<Status>> createConcurrentLoginPolicy(List<ConcurrentLoginPolicyData> concurrentLoginPolicies, IStaffData staffData, String partialSuccess, boolean caseSensitivity) throws DataManagerException {
		
		if(caseSensitivity){
			for (ConcurrentLoginPolicyData concurrentLoginPolicy : concurrentLoginPolicies) {
					verifyNameWithIgnoreCase(ConcurrentLoginPolicyData.class, concurrentLoginPolicy.getName(), false);
			}
		}
		return insertRecords(ConcurrentLoginPolicyDataManager.class, concurrentLoginPolicies, staffData, ConfigConstant.CREATE_CONCURRENT_LOGIN_POLICY_ACTION, partialSuccess);
	}

	
	public void updateConcurrentLoginPolicyByName(ConcurrentLoginPolicyData concurrentLoginPolicy, IStaffData staffData, String concurrentLoginPolicyName, boolean caseSensitivity) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ConcurrentLoginPolicyDataManager concurrentLoginPolicyDataManager = getConcurrentLoginPolicyDataManager(session);

		if (concurrentLoginPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			session.beginTransaction();
			if(caseSensitivity){
				ConcurrentLoginPolicyData concurrentLoginPolicyIgnoreCaseData = (ConcurrentLoginPolicyData) verifyNameWithIgnoreCase(ConcurrentLoginPolicyData.class, (String) concurrentLoginPolicyName, true);
				concurrentLoginPolicyDataManager.updateConcurrentLoginPolicyById(concurrentLoginPolicy, staffData, concurrentLoginPolicyIgnoreCaseData.getConcurrentLoginId());
			}else {
				concurrentLoginPolicyDataManager.updateConcurrentLoginPolicyByName(concurrentLoginPolicy, staffData, concurrentLoginPolicyName.trim());
			}

			commit(session);
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}
	
	
	public void deleteConcurrentLoginPolicyById(List<String> concurrentLoginPolicyIds, IStaffData staffData) throws DataManagerException {
		
		deleteConcurrentLoginPolicy(concurrentLoginPolicyIds, staffData, BY_ID, false);

	}
	
	public void deleteConcurrentLoginPolicyByName(List<String> concurrentLoginPolicyNames, IStaffData staffData, boolean caseSensitivity) throws DataManagerException {
		
		deleteConcurrentLoginPolicy(concurrentLoginPolicyNames, staffData, BY_NAME, caseSensitivity);
		
	}

	private void deleteConcurrentLoginPolicy(List<String> concurrentLoginPolicyIdOrNames, IStaffData staffData, boolean isIdOrName, boolean caseSensitivity) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ConcurrentLoginPolicyDataManager concurrentLoginPolicyDataManager = getConcurrentLoginPolicyDataManager(session);

		if (concurrentLoginPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			
			session.beginTransaction();

			if (Collectionz.isNullOrEmpty(concurrentLoginPolicyIdOrNames) == false) {

				int size = concurrentLoginPolicyIdOrNames.size();
				for (int i = 0; i < size; i++) {

					if (Strings.isNullOrBlank(concurrentLoginPolicyIdOrNames.get(i)) == false) {

						String concurrentLoginPolicyIdOrName = concurrentLoginPolicyIdOrNames.get(i).trim();
						
						String concurrentLoginPolicyName = null;

						if (isIdOrName) {
							concurrentLoginPolicyName = concurrentLoginPolicyDataManager.deleteConcurrentLoginPolicyById(concurrentLoginPolicyIdOrName);
						} else {
							if(caseSensitivity){
								ConcurrentLoginPolicyData concurrentLoginPolicyIgnoreCaseData = (ConcurrentLoginPolicyData) verifyNameWithIgnoreCase(ConcurrentLoginPolicyData.class, (String) concurrentLoginPolicyIdOrName, true);
								concurrentLoginPolicyName  = concurrentLoginPolicyDataManager.deleteConcurrentLoginPolicyById(concurrentLoginPolicyIgnoreCaseData.getConcurrentLoginId());
							} else {
								concurrentLoginPolicyName = concurrentLoginPolicyDataManager.deleteConcurrentLoginPolicyByName(concurrentLoginPolicyIdOrName);
							}
						}

						staffData.setAuditName(concurrentLoginPolicyName);
						AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_CONCURRENT_LOGIN_POLICY_ACTION);

					}

				}

				commit(session);
			}
			
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}
	
	
}
