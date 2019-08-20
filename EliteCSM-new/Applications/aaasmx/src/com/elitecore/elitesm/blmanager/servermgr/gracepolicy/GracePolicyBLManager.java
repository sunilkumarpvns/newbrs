/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   GracePolicyBLManager.java                 		
 * ModualName GracePolicy    			      		
 * Created on 23 December, 2010
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.blmanager.servermgr.gracepolicy;

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
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.GracePolicyDataManager;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;

public class GracePolicyBLManager extends BaseBLManager {
	
	private static final String EMPTY = "";

	/**
	 * @return Returns Data Manager instance for GracePolicy data.
	 */
	public  GracePolicyDataManager getGracePolicyDataManager(IDataManagerSession  session){
		return (GracePolicyDataManager)DataManagerFactory.getInstance().getDataManager(GracePolicyDataManager.class, session);
	}
	
	
	public List<GracepolicyData> getGracePolicyList() throws DataManagerException {
		
		return getGracePolicies(null, false);
		
	}
	
	public List<GracepolicyData> getGracePolicyList( IStaffData staffData) throws DataManagerException {
		
		return getGracePolicies(staffData, true);
		
	}
	
	private List<GracepolicyData> getGracePolicies(IStaffData staffData, boolean isDoAudit) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GracePolicyDataManager gracePolicyDataManager = getGracePolicyDataManager(session);
		
		if (gracePolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for DataManager: " + getClass().getName());
		}
		
		try {
			
			List<GracepolicyData> gracePolicies = gracePolicyDataManager.getGracePolicyList();
			
			if (isDoAudit) {
				session.beginTransaction();
				AuditUtility.doAuditing(session, staffData, ConfigConstant.LIST_GRACE_POLICY);
				commit(session);
			} 
			
			return gracePolicies;

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
	
	
	public GracepolicyData getGracePolicyById(String gracePolicyId) throws DataManagerException {
		
		return getGracePolicy(gracePolicyId, BY_ID, false);
		
	}
	
	public GracepolicyData getGracePolicyByName(String gracePolicyName, boolean caseSenstivity) throws DataManagerException {
		
		return getGracePolicy(gracePolicyName.trim(), BY_NAME, caseSenstivity);
		
	}
	
	private GracepolicyData getGracePolicy(Object gracePolicyIdOrName, boolean isIdOrName, boolean caseSensitivity) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GracePolicyDataManager gracePolicyDataManager = getGracePolicyDataManager(session);
		
		if (gracePolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for DataManager: " + getClass().getName());
		}
		
		try {
			
			GracepolicyData gracePolicy = null;

			if (isIdOrName) {
				gracePolicy = gracePolicyDataManager.getGracePolicyById((String) gracePolicyIdOrName);
			} else {
				if(caseSensitivity){
					gracePolicy = (GracepolicyData) verifyNameWithIgnoreCase(GracepolicyData.class, (String) gracePolicyIdOrName, true);
				} else {
					gracePolicy = gracePolicyDataManager.getGracePolicyByName((String) gracePolicyIdOrName);
				}
			}
			
			return gracePolicy;

		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}

	
	public void createGracePolicy(GracepolicyData gracePolicy, IStaffData staffData, boolean caseSensitivity) throws DataManagerException {
		
		List<GracepolicyData> gracePolicies = new ArrayList<GracepolicyData>();
		gracePolicies.add(gracePolicy);
		createGracePolicy(gracePolicies, staffData, EMPTY, caseSensitivity);
		
	}

	public Map<String, List<Status>> createGracePolicy(List<GracepolicyData> gracePolicies, IStaffData staffData, String partialSuccess, boolean caseSensitivity) throws DataManagerException {
		
		if(caseSensitivity){
			for (GracepolicyData gracePolicy : gracePolicies) {
				verifyNameWithIgnoreCase(GracepolicyData.class, gracePolicy.getName(), false);
			}
		}
		return insertRecords(GracePolicyDataManager.class, gracePolicies, staffData, ConfigConstant.CREATE_GRACE_POLICY, partialSuccess);
	}
	
	
	public void updateGracePolicyById(GracepolicyData gracePolicy, IStaffData staffData) throws DataManagerException {
		
		updateGracePolicy(gracePolicy, staffData, null, false);
		
	}

	public void updateGracePolicyByName(GracepolicyData gracePolicy, IStaffData staffData, String gracePolicyName, boolean caseSensitivity) throws DataManagerException {
		
		updateGracePolicy(gracePolicy, staffData, gracePolicyName, caseSensitivity);
		
	}
	
	private void updateGracePolicy(GracepolicyData gracePolicy, IStaffData staffData, String gracePolicyName, boolean caseSensitivity) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GracePolicyDataManager gracePolicyDataManager = getGracePolicyDataManager(session);
		
		if (gracePolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for DataManager: " + getClass().getName());
		}

		try {
			
			session.beginTransaction();
			
			if (gracePolicyName == null) {
				gracePolicyDataManager.updateGracePolicyById(gracePolicy, gracePolicy.getGracePolicyId(),staffData);
			} else {
				if(caseSensitivity){
					GracepolicyData gracePolicyIgnoreCaseData = (GracepolicyData) verifyNameWithIgnoreCase(GracepolicyData.class, (String) gracePolicyName, true);
					gracePolicyDataManager.updateGracePolicyById(gracePolicy, gracePolicyIgnoreCaseData.getGracePolicyId(),staffData);
				}else {
					gracePolicyDataManager.updateGracePolicyByName(gracePolicy, gracePolicyName.trim(),staffData);
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

	
	public void deleteGracePolicyById(List<String> gracePolicyIds, IStaffData staffData) throws DataManagerException {
		
		deleteGracePolicy(gracePolicyIds, staffData, BY_ID, false);

	}
	
	public void deleteGracePolicyByName(List<String> gracePolicyNames, IStaffData staffData, boolean caseSensitivity) throws DataManagerException {
		
		deleteGracePolicy(gracePolicyNames, staffData, BY_NAME, caseSensitivity);
		
	}
	
	private void deleteGracePolicy(List<String> gracePolicyIdOrNames, IStaffData staffData, boolean isIdOrName, boolean caseSensitivity) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GracePolicyDataManager gracePolicyDataManager = getGracePolicyDataManager(session);
		
		if (gracePolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for DataManager: " + getClass().getName());
		}

		try {
			
			session.beginTransaction();

			if (Collectionz.isNullOrEmpty(gracePolicyIdOrNames) == false) {

				int size = gracePolicyIdOrNames.size(); 
				for (int i = 0; i < size; i++) {

					if (Strings.isNullOrBlank(gracePolicyIdOrNames.get(i)) == false) {

						String gracePolicyIdOrName = gracePolicyIdOrNames.get(i).trim();
						
						String gracePolicyName;
						if (isIdOrName) {
							gracePolicyName = gracePolicyDataManager.deleteGracePolicyById(gracePolicyIdOrName, staffData);
						} else {
							if(caseSensitivity){
								GracepolicyData gracePolicyIgnoreCaseData = (GracepolicyData) verifyNameWithIgnoreCase(GracepolicyData.class, (String) gracePolicyIdOrName, true);
								gracePolicyName  = gracePolicyDataManager.deleteGracePolicyById(gracePolicyIgnoreCaseData.getGracePolicyId(),staffData);
							} else {
								gracePolicyName = gracePolicyDataManager.deleteGracePolicyByName(gracePolicyIdOrName, staffData);
							}
						}
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
