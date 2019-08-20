package com.elitecore.elitesm.blmanager.servicepolicy.diameter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.eap.EAPConfigDataManager;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.EAPPolicyDataManager;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyAdditionalDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyAuthDriverRelationData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyPluginConfig;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;

public class EAPPolicyBLManager extends BaseBLManager {
	
	public EAPPolicyDataManager getDiameterEAPDataManager(IDataManagerSession session) {
		EAPPolicyDataManager eapPolicyDataManager = (EAPPolicyDataManager) DataManagerFactory.getInstance().getDataManager(EAPPolicyDataManager.class, session);
		return eapPolicyDataManager;
	}
	
	public EAPConfigDataManager getEAPConfigDataManager(IDataManagerSession session){
		EAPConfigDataManager eapConfigDataManager = (EAPConfigDataManager)DataManagerFactory.getInstance().getDataManager(EAPConfigDataManager.class, session);
		return eapConfigDataManager;
	}
	
	public void create(EAPPolicyData eapPolicyData, IStaffData staffData) throws DataManagerException {
		List<EAPPolicyData> policies = new ArrayList<EAPPolicyData>();
		policies.add(eapPolicyData);
		create(policies, staffData, "false");
	}
	
	public Map<String, List<Status>> create(List<EAPPolicyData> eapPolicies, IStaffData staffData, String partialSuccess) throws DataManagerException {
		return insertRecords(EAPPolicyDataManager.class, eapPolicies, staffData, ConfigConstant.CREATE_DIAMETER_EAP_POLICY, partialSuccess);
	}
	
	public PageList searchEAPPolicy(EAPPolicyData eapPolicyData ,int pageNo, Integer pageSize) throws DataManagerException{

		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		EAPPolicyDataManager eapPolicyDataManager = getDiameterEAPDataManager(session);
		
		if(eapPolicyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for: " +  getClass());
		}
		
		try{				     
			PageList pageList = eapPolicyDataManager.searchEAPPolicy(eapPolicyData, pageNo, pageSize);
			return pageList;
		}catch(DataManagerException e){
			throw e;
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	public void delete(String[] policyIds, IStaffData staffData) throws DataManagerException {
		delete(policyIds, staffData, BY_ID);
	}
	
	public void deleteByName(String[] policyNames, IStaffData staffData) throws DataManagerException {
		delete(policyNames, staffData, BY_NAME);
	}
	
	private void delete(String[] policiesToBeDelete, IStaffData staffData, boolean deleteByIDorName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		EAPPolicyDataManager eapPolicyDataManager = getDiameterEAPDataManager(session);
		
		if (eapPolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for: " +  getClass());
		}

		try{
			session.beginTransaction();
			for (String policyToDelete : policiesToBeDelete) {
				if (Strings.isNullOrBlank(policyToDelete) == false) {
					String policy = policyToDelete.trim();
					String deletedPolicy;
					if (deleteByIDorName) {
						deletedPolicy = eapPolicyDataManager.deleteById(policy);
					} else {
						deletedPolicy = eapPolicyDataManager.deleteByName(policy);
					}
					staffData.setAuditName(deletedPolicy);
					AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_DIAMETER_EAP_POLICY);
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
	
	public void updateBasicDetails(EAPPolicyData policyData,IStaffData staffData, String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		EAPPolicyDataManager eapPolicyDataManager = getDiameterEAPDataManager(session);
		
		if(eapPolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			eapPolicyDataManager.updateBasicDetail(policyData,staffData,actionAlias);
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception e){
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	public void updateAuthenticationDetails(EAPPolicyData policyData,IStaffData staffData, String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		EAPPolicyDataManager eapPolicyDataManager = getDiameterEAPDataManager(session);
		
		if(eapPolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();
			eapPolicyDataManager.updateAuthenticationDetails(policyData,staffData,actionAlias);
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception e){
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	public void updateAuthorizationDetails(EAPPolicyData policyData,IStaffData staffData, String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		EAPPolicyDataManager eapPolicyDataManager = getDiameterEAPDataManager(session);
		
		if(eapPolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();
			eapPolicyDataManager.updateAuthorizationDetails(policyData,staffData,actionAlias);
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception e){
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	public void updateDriverProfile(EAPPolicyData policyData,IStaffData staffData, String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		EAPPolicyDataManager eapPolicyDataManager = getDiameterEAPDataManager(session);
		
		if(eapPolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();
			eapPolicyDataManager.updateDriverProfiles(policyData,staffData,actionAlias);
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception e){
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	public EAPPolicyData getEAPPolicyById(String eapPolicyId) throws DataManagerException {
		return getEapPolicy(eapPolicyId, BY_ID);
	}

	public EAPPolicyData getEAPPolicyByName(String eapPolicyName) throws DataManagerException {
		EAPPolicyData eapPolicy = getEapPolicy(eapPolicyName, BY_NAME);
		convertPolicyData(eapPolicy);
		return eapPolicy;
	}
	
	private void convertPolicyData(EAPPolicyData eapPolicy) {
		List<EAPPolicyAuthDriverRelationData> primaryDrivers = eapPolicy.getDriverList();
		List<EAPPolicyAdditionalDriverRelData> additionalDrivers = eapPolicy.getEapAdditionalDriverRelDataList();

		DriverBLManager blManager = new DriverBLManager();
		for (EAPPolicyAuthDriverRelationData primaryDriver : primaryDrivers) {
			String driverId = primaryDriver.getDriverInstanceId();
			try {
				String driverName = blManager.getDriverNameById(driverId);
				primaryDriver.setDriverName(driverName);
			} catch (DataManagerException e) {
				e.printStackTrace();
			}
		}
		for (EAPPolicyAdditionalDriverRelData additionalDriver : additionalDrivers) {
			String driverId = additionalDriver.getDriverInstanceId();
			try {
				String driverName = blManager.getDriverNameById(driverId);
				additionalDriver.setDriverName(driverName);
			} catch (DataManagerException e) {
				e.printStackTrace();
			}
		}
	}
	
	private EAPPolicyData getEapPolicy(Object policyToGet, boolean getByNameOrId) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		EAPPolicyDataManager eapPolicyDataManager = getDiameterEAPDataManager(session);
		if(eapPolicyDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for: " +  getClass());
		}
		try{
			if (getByNameOrId) {
				return eapPolicyDataManager.getEAPPolicyById((String)policyToGet);
			} else {
				return eapPolicyDataManager.getEAPPolicyByName(((String)policyToGet).trim());
			}
		}catch(DataManagerException e){
        	throw e;
		}catch(Exception e){
			e.printStackTrace();
        	throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	public void changePolicyOrder(String[] order,IStaffData staffData,String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		EAPPolicyDataManager eapPolicyDataManager = getDiameterEAPDataManager(session);
          
		if(eapPolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try{
			session.beginTransaction();
			eapPolicyDataManager.changePolicyOrder(order);
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception e){
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	public List<EAPPolicyData> getEAPPolicies() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		EAPPolicyDataManager eapPolicyDataManager = getDiameterEAPDataManager(session);
          
		if(eapPolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try{
			List<EAPPolicyData> policyList = eapPolicyDataManager.getEAPPolicies();
			return policyList;
		}catch(DataManagerException exp){
			throw exp;
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	/**
	 * This is used for get all EAP service policy included with Active and Inactive status .
	 * @return List of EAP policy
	 * @throws DataManagerException
	 */
	public List<EAPPolicyData> getEAPPolicyList() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		EAPPolicyDataManager eapPolicyDataManager = getDiameterEAPDataManager(session);
          
		if(eapPolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try{
			session.beginTransaction();
			List<EAPPolicyData> policyList = eapPolicyDataManager.getEAPPolicyList();
			return policyList;
		}catch(DataManagerException exp){
			throw exp;
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	public EAPConfigData getEAPConfigInstance(EAPPolicyData eapPolicyData) throws DataManagerException{
		EAPConfigData eapConfigData = null;
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		EAPConfigDataManager eapConfigDataManager = getEAPConfigDataManager(session);
		if(eapConfigDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for: " +  getClass().getName());
		}
		try{
			eapConfigData = eapConfigDataManager.getEAPConfigDataById(eapPolicyData.getEapConfigId());
			return eapConfigData;
		}catch(DataManagerException e){
        	throw e;
		}catch(Exception e){
        	throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	public void updateEAPPolicyStatus(List<String> eapPolicyIds,String status) throws DataManagerException{

		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		EAPPolicyDataManager eapPolicyDataManager = getDiameterEAPDataManager(session);
		if(eapPolicyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
		}
		try{				     
			session.beginTransaction();
			eapPolicyDataManager.updateStatus(eapPolicyIds, status);
			commit(session);
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception e){
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public void updateResponseAttributes(EAPPolicyData eapPolicyData,IStaffData staffData, String actionAlias) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		EAPPolicyDataManager eapPolicyDataManager = getDiameterEAPDataManager(session);
		if(eapPolicyDataManager==null){
			throw new DataManagerException("Data Manager Not Found: CGDataManager.");
		}
		try{				     
			session.beginTransaction();
			eapPolicyDataManager.updateResponseAttributes(eapPolicyData,staffData,actionAlias);
			commit(session);
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception e){
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public void updateRFC4372CUIDetails(EAPPolicyData eapPolicyData, IStaffData staffData, String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		EAPPolicyDataManager eapPolicyDataManager = getDiameterEAPDataManager(session);
		
		if(eapPolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			
			session.beginTransaction();
			eapPolicyDataManager.updateRFC4372CUIDetails(eapPolicyData,staffData,actionAlias);
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception e){
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	public void updateEAPPolicy(EAPPolicyData eapPolicyData, String policyName, IStaffData staffData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         
		EAPPolicyDataManager eapPolicyDataManager = getDiameterEAPDataManager(session);
		
		if(eapPolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try{
			session.beginTransaction();
			eapPolicyDataManager.updateEAPServicePolicy(eapPolicyData, policyName, staffData);
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
	}
	
	public List<EAPPolicyPluginConfig> getPolicyPluginConfigList(String policyId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		EAPPolicyDataManager eapPolicyDataManager = getDiameterEAPDataManager(session);
		if(eapPolicyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		try{				     
			
			List<EAPPolicyPluginConfig> policyList = eapPolicyDataManager.getPolicyPluginConfigList(policyId);
			return policyList;
		}catch(DataManagerException e){
			throw e;
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	/**
	 * This method is used to get binded plugin-names with EAP service policies .
	 * @return set of plugin-names .
	 * @throws DataManagerException
	 */
	public Set<String> getBindedPluginNames()throws DataManagerException {
		Set<String> bindedPluginNames = new HashSet<String>();
		try{
			List<EAPPolicyData> EAPPolicyDataList = getEAPPolicyList();
			
			if (EAPPolicyDataList != null && EAPPolicyDataList.isEmpty() == false) {
	        	 for(EAPPolicyData eapPolicyData : EAPPolicyDataList){
	        		 List<EAPPolicyPluginConfig> eapPolicyPluginConfigList = eapPolicyData.getEapPolicyPluginConfigList();
	        		 
	        		 if (eapPolicyPluginConfigList != null && eapPolicyPluginConfigList.isEmpty() == false) {
	        			 for(EAPPolicyPluginConfig eapPluginConfig: eapPolicyPluginConfigList){
	        				bindedPluginNames.add(eapPluginConfig.getPluginName());
	        			 }
	        		 }
	        	 }
	         }
		}catch (DataManagerException e) { 
			throw e;
		}
		return bindedPluginNames;
    }
}
