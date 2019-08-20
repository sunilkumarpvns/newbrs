package com.elitecore.elitesm.blmanager.servicepolicy.diameter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.CreditControlPolicyDataManager;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CCPolicyPluginConfig;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CreditControlPolicyData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;

public class CreditControlPolicyBLManager extends BaseBLManager{

	public CreditControlPolicyDataManager getDiameterCreditControlDataManager(IDataManagerSession session) {
		CreditControlPolicyDataManager diameterCreditControlPolicyDataManager = (CreditControlPolicyDataManager) DataManagerFactory.getInstance().getDataManager(CreditControlPolicyDataManager.class, session);
		return diameterCreditControlPolicyDataManager;
	}

	public void create(CreditControlPolicyData ccPolicyData, IStaffData staffData) throws DataManagerException {
		List<CreditControlPolicyData> policies = new ArrayList<CreditControlPolicyData>();
		policies.add(ccPolicyData);
		create(policies, staffData, "false");
	}

	public Map<String, List<Status>> create(List<CreditControlPolicyData> policyData, IStaffData staffData, String partialSuccess) throws DataManagerException {
		return insertRecords(CreditControlPolicyDataManager.class, policyData, staffData, ConfigConstant.CREATE_CREDIT_CONTROL_SERVICE_POLICY, partialSuccess);
	}

	public CreditControlPolicyData getPolicyDataByPolicyId(String ccPolicyId, IStaffData staffData) throws DataManagerException {
		return getCCServicePolicy(ccPolicyId, staffData, BY_ID);
	}
	
	public CreditControlPolicyData getCCServicePolicyByName(String policyName, IStaffData staffData) throws DataManagerException {
		return getCCServicePolicy(policyName, staffData, BY_NAME);
	}

	private CreditControlPolicyData getCCServicePolicy(Object policyToGet, IStaffData staffData, boolean getByNameOrId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		CreditControlPolicyDataManager creditControlDataManager = getDiameterCreditControlDataManager(session);

		if(creditControlDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();
			CreditControlPolicyData data;
			if(getByNameOrId) {
				data = creditControlDataManager.getCcPolicyDataByPolicyId((String)policyToGet);
			} else {
				data = creditControlDataManager.getCcPolicyDataByPolicyName((String)policyToGet);
			}
			return data;
		}catch(DataManagerException exp){
			throw exp;
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	public PageList searchCreditControlPolicy(CreditControlPolicyData creditControlPolicyData ,int pageNo, Integer pageSize) throws DataManagerException{

		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{				     
			CreditControlPolicyDataManager creditControlPolicyDataManager = getDiameterCreditControlDataManager(session);

			if(creditControlPolicyDataManager == null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
			session.beginTransaction();
			PageList pageList = creditControlPolicyDataManager.searchCreditControlPolicy(creditControlPolicyData, pageNo, pageSize);
			return pageList;
		}catch(DataManagerException e){
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public void deleteByID(String[] policiesToBeDelete, IStaffData staffData) throws DataManagerException {
		delete(policiesToBeDelete, staffData, BY_ID);
	}

	public void deleteByName(String[] policyNames, StaffData staffData) throws DataManagerException {
		delete(policyNames, staffData, BY_NAME);
	}
	
	private void delete(String[] policiesToBeDelete, IStaffData staffData, boolean deleteByIDorName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		CreditControlPolicyDataManager creditControlDataManager = getDiameterCreditControlDataManager(session);

		if(creditControlDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();
			for (String policyToDelete : policiesToBeDelete) {
				if (Strings.isNullOrBlank(policyToDelete) == false) {
					String policy = policyToDelete.trim();
					String deletedPolicy;
					if (deleteByIDorName) {
						deletedPolicy = creditControlDataManager.deleteId(policy);
					} else {
						deletedPolicy = creditControlDataManager.deleteByName(policy);
					}
					staffData.setAuditName(deletedPolicy);
					AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_CREDIT_CONTROL_SERVICE_POLICY);
				}
			}
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	public void updateByID(CreditControlPolicyData policyData,IStaffData staffData) throws DataManagerException {
		update(policyData, staffData, null);
	}
	
	public void update(CreditControlPolicyData policyData,IStaffData staffData, String policyToUpdate) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		CreditControlPolicyDataManager creditControlDataManager = getDiameterCreditControlDataManager(session);

		if(creditControlDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();
			if (policyToUpdate == null) {
				creditControlDataManager.updateById(policyData,staffData);
			} else {
				creditControlDataManager.updateByName(policyData, staffData, policyToUpdate);
			}
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			session.close();
		}
	}
	
	public void changePolicyOrder(String[] order,IStaffData staffData,String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		CreditControlPolicyDataManager creditControlDataManager = getDiameterCreditControlDataManager(session);

		if (creditControlDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();
			creditControlDataManager.changePolicyOrder(order);
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}

	public List<CreditControlPolicyData> getCreditControlPolicies() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		CreditControlPolicyDataManager creditControlDataManager = getDiameterCreditControlDataManager(session);

		if (creditControlDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();
			List<CreditControlPolicyData> policyList = creditControlDataManager.getCreditControlPolicies();
			return policyList;
		}catch(DataManagerException exp){
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	/**
	 * This is used for get all CC service policy included with Active and Inactive status .
	 * @return List of CC policy
	 * @throws DataManagerException
	 */
	public List<CreditControlPolicyData> getCreditControlPolicyList() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		CreditControlPolicyDataManager creditControlDataManager = getDiameterCreditControlDataManager(session);

		if (creditControlDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			List<CreditControlPolicyData> policyList = creditControlDataManager.getCreditControlPolicyList();
			return policyList;
		} catch(DataManagerException exp){
			throw exp;
		} catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}

	public void updateCreditControlPolicyStatus(List<String> creditControlPolicyIds,String status) throws DataManagerException{

		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		CreditControlPolicyDataManager creditControlDataManager = getDiameterCreditControlDataManager(session);
		
		try{				     
			if (creditControlDataManager==null) {
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
			
			session.beginTransaction();
			creditControlDataManager.updateStatus(creditControlPolicyIds, status);
			commit(session);
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	public List<CCPolicyPluginConfig> getPolicyPluginConfigList(String policyId) throws DataManagerException{
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		CreditControlPolicyDataManager creditControlDataManager = getDiameterCreditControlDataManager(session);
		
		if(creditControlDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {				     
			return creditControlDataManager.getPolicyPluginConfigList(policyId);
		} catch(DataManagerException e) {
			throw e;
		} catch(Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	/**
	 * This method is used to get binded plugin-names with CC service policies .
	 * @return set of plugin-names 
	 * @throws DataManagerException
	 */
	public Set<String> getBindedPluginNames() throws DataManagerException {
		Set<String> bindedPluginNames = new HashSet<String>();
		try{
			List<CreditControlPolicyData> CCPolicyDataList = getCreditControlPolicyList();
			if (Collectionz.isNullOrEmpty(CCPolicyDataList) == false) {
				for(CreditControlPolicyData CCPolicyData : CCPolicyDataList){
					List<CCPolicyPluginConfig> ccPolicyPluginConfigList=CCPolicyData.getCcPolicyPluginConfigList();

					if (Collectionz.isNullOrEmpty(ccPolicyPluginConfigList) == false) {
						for(CCPolicyPluginConfig ccPluginConfig: ccPolicyPluginConfigList){
							bindedPluginNames.add(ccPluginConfig.getPluginName());
						}
					}
				}
			}
			return bindedPluginNames;
		}catch (DataManagerException e) { 
			throw e;
		}
	}
}