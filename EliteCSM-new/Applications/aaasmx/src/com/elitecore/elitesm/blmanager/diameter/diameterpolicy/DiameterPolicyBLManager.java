/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DiameterpolicyBLManager.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.blmanager.diameter.diameterpolicy;

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
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.EliteGenericValidator;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicy.DiameterPolicyDataManager;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;

public class DiameterPolicyBLManager extends BaseBLManager {
	private static final String EMPTY = "";
	private static final String MODULE = "DIAMETERPOLICY";

	/**
	 * @return Returns Data Manager instance for Diameterpolicy data.
	 */
	public DiameterPolicyDataManager getDiameterPolicyDataManager(IDataManagerSession session) { 
		DiameterPolicyDataManager diameterPolicyDataManager = (DiameterPolicyDataManager) DataManagerFactory 
				.getInstance().getDataManager(DiameterPolicyDataManager.class, session);
		return diameterPolicyDataManager;
	}
	
	public void create(DiameterPolicyData diamePolicyData, IStaffData staffData, boolean caseSensitivity) throws DataManagerException {
		List<DiameterPolicyData> diameterPolicyDataList = new ArrayList<DiameterPolicyData>();
		diameterPolicyDataList.add(diamePolicyData);
		create(diameterPolicyDataList, staffData, EMPTY,caseSensitivity);
	}
	
	public PageList search(DiameterPolicyData diameterPolicyData, IStaffData staffData, int pageNo, int pageSize) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPolicyDataManager diameterPolicyDataManager = getDiameterPolicyDataManager(session);
		
		PageList lstDiameterPolicyList;

		if (diameterPolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			lstDiameterPolicyList = diameterPolicyDataManager.search(diameterPolicyData, pageNo, pageSize);
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_AUTHORIZATION_POLICY);
			commit(session);
		} 	catch(DataManagerException dme) {
			dme.printStackTrace();
			throw dme;
		}	catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}

		return lstDiameterPolicyList; 
	}
	
	public void updateStatus(List<String> lstDiameterPolicyIds,IStaffData staffData, String commonStatusId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPolicyDataManager diameterPolicyDataManager = getDiameterPolicyDataManager(session);

		Date currentDate = new Date();

		if(diameterPolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

		try{
			updateStatusValidate(lstDiameterPolicyIds,commonStatusId);
			session.beginTransaction();

			if (lstDiameterPolicyIds != null) { 
				int noOfDiameterPolicy = lstDiameterPolicyIds.size();
				for (int i=0; i < noOfDiameterPolicy; i++) {
					if(Strings.isNullOrBlank(lstDiameterPolicyIds.get(i)) == false){
						String diameterPolicyId =lstDiameterPolicyIds.get(i);
						String diameterPolicyName = diameterPolicyDataManager.updateStatus(diameterPolicyId,commonStatusId, new Timestamp(currentDate.getTime()));
						staffData.setAuditName(diameterPolicyName);
						AuditUtility.doAuditing(session,staffData, ConfigConstant.UPDATE_AUTHORIZATION_POLICY_STATUS);
					}
				}

				commit(session);
			}
		} catch (DataManagerException dme) {
			rollbackSession(session);
			dme.printStackTrace();
			throw dme;
		} catch(Exception exp) {
			rollbackSession(session);
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}
	
	public void updateStatusValidate(List<String> lstRadiusPolicyIds,String commonStatusId)throws DataValidationException{
		// commonStatusId
		if(EliteGenericValidator.isBlankOrNull(commonStatusId)){
			throw (new DataValidationException("Invalid RadiusPolicy commonStatusId",(MODULE+"."+"commonStatusId").toLowerCase()));
		}
	}
	
	public void deleteById(List<String> diameterPolicyIdList, IStaffData staffData) throws DataManagerException {
		delete(diameterPolicyIdList, staffData, BY_ID, false);
	}
	
	public void deleteByName(List<String> diameterPolicyNameList, IStaffData staffData, boolean caseSenstitivity) throws DataManagerException {
		delete(diameterPolicyNameList, staffData, BY_NAME, caseSenstitivity);
	}
	
	private void delete(List<String> diameterPolicyIdOrNameList, IStaffData staffData , Boolean byIdOrName, boolean caseSensitivity) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPolicyDataManager diameterPolicyDataManager = getDiameterPolicyDataManager(session);
		
		if(diameterPolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

		try{
			session.beginTransaction();
			if (Collectionz.isNullOrEmpty(diameterPolicyIdOrNameList) == false) {
				int noOfDiameterPolicy = diameterPolicyIdOrNameList.size();
				for (int i=0; i < noOfDiameterPolicy; i++) {
					if(Strings.isNullOrEmpty(diameterPolicyIdOrNameList.get(i)) == false) {
						String diameterPolicyIdOrName =  diameterPolicyIdOrNameList.get(i).trim();
						String name = null;
						if(byIdOrName) {
							name  = diameterPolicyDataManager.deleteById(diameterPolicyIdOrName);
						} else {
							if(caseSensitivity){
								DiameterPolicyData diameterPolicyDataIgnoreCaseData = (DiameterPolicyData) verifyNameWithIgnoreCase(DiameterPolicyData.class, (String) diameterPolicyIdOrName, true);
								name  = diameterPolicyDataManager.deleteById(diameterPolicyDataIgnoreCaseData.getDiameterPolicyId());
							} else {
								name = diameterPolicyDataManager.deleteByName(diameterPolicyIdOrName);
							}
						}
						staffData.setAuditName(name);
						AuditUtility.doAuditing(session,staffData, ConfigConstant.DELETE_AUTHORIZATION_POLICY);
					}
				}
				commit(session);
			} 
			
		} 	catch (DataManagerException dme) {
			dme.printStackTrace();
			rollbackSession(session);
			throw dme;
		}	catch (Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}
	
	public DiameterPolicyData getDiameterPolicyDataById(String diameterPolicyId) throws DataManagerException {
		return getDiameterPolicyData(diameterPolicyId, BY_ID, false);
	}

	public DiameterPolicyData getDiameterPolicyDataByName(String diameterPolicyName, boolean caseSensitivity) throws DataManagerException {
		return getDiameterPolicyData(diameterPolicyName.trim(), BY_NAME, caseSensitivity);
	}

	private DiameterPolicyData getDiameterPolicyData(Object searchVal, boolean isByIdOrName, boolean caseSensitivity) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPolicyDataManager diameterPolicyDataManager = getDiameterPolicyDataManager(session);

		if (diameterPolicyDataManager == null ) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			DiameterPolicyData diameterPolicyData = null;
			if (isByIdOrName) {
				diameterPolicyData = diameterPolicyDataManager.getDiameterPolicyDataById((String)searchVal);
			} else {
				if(caseSensitivity){
					diameterPolicyData = (DiameterPolicyData) verifyNameWithIgnoreCase(DiameterPolicyData.class, (String) searchVal, true);
				} else {
					diameterPolicyData = diameterPolicyDataManager.getDiameterPolicyDataByName((String) searchVal);
				}
			}

			return diameterPolicyData;

		} 	catch (DataManagerException dme) {
			dme.printStackTrace();
			throw dme;
		} 	catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		} 	finally {
			closeSession(session);
		}
	}
	
	public void updateById(DiameterPolicyData diameterPolicyData,IStaffData staffData) throws DataManagerException {
		updateByName( diameterPolicyData, staffData, null, false);
	}
	
	public void updateByName(DiameterPolicyData diameterPolicyData,IStaffData staffData,String queryOrPathParam, boolean caseSensitivity) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterPolicyDataManager diameterPolicyDataManager = getDiameterPolicyDataManager(session);

		if(diameterPolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

		try {
			session.beginTransaction();

			if (queryOrPathParam == null) {
				diameterPolicyDataManager.updateById(diameterPolicyData, staffData, diameterPolicyData.getDiameterPolicyId());
			} else {
				if(caseSensitivity){
					DiameterPolicyData diameterPolicyDataIgnoreCaseData = (DiameterPolicyData) verifyNameWithIgnoreCase(DiameterPolicyData.class, (String) queryOrPathParam, true);
					diameterPolicyDataManager.updateById(diameterPolicyData, staffData, diameterPolicyDataIgnoreCaseData.getDiameterPolicyId());
				} else {
					diameterPolicyDataManager.updateByName(diameterPolicyData, staffData, queryOrPathParam.trim());
				}
			}
			commit(session);

		} catch(DataManagerException dme) {
			dme.printStackTrace();
			rollbackSession(session);
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}

	public Map<String, List<Status>> create(List<DiameterPolicyData> diameterPolicyList, IStaffData staffData, String partialSuccess, boolean caseSensitivity) throws DataManagerException {

		for (DiameterPolicyData diameterPolicyData : diameterPolicyList) {
			staffData.setAuditName(diameterPolicyData.getName());
			
			if(caseSensitivity){
				verifyNameWithIgnoreCase(DiameterPolicyData.class, diameterPolicyData.getName(), false);
			}
		}
		return insertRecords(DiameterPolicyDataManager.class, diameterPolicyList, staffData, ConfigConstant.CREATE_AUTHORIZATION_POLICY, partialSuccess);
	}
	
}
