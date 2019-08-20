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
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.DiameterNASPolicyDataManager;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAcctDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAdditionalDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAuthDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAuthMethodRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAuthPluginConfig;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASResponseAttributes;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;


public class DiameterNASPolicyBLManager extends BaseBLManager {
	
		public DiameterNASPolicyDataManager getDiameterNASPolicyDataManager(IDataManagerSession session) {
			DiameterNASPolicyDataManager diameterNASPolicyDataManager = (DiameterNASPolicyDataManager) DataManagerFactory.getInstance().getDataManager(DiameterNASPolicyDataManager.class, session);
			return diameterNASPolicyDataManager;
		}

		public void createPolicy(NASPolicyInstData nasPolicyInstData, IStaffData staffData) throws DataManagerException {
			List<NASPolicyInstData> policies = new ArrayList<NASPolicyInstData>();
			policies.add(nasPolicyInstData);
			create(policies, staffData, "false");
		}
		
		public Map<String, List<Status>> createPolicies(List<NASPolicyInstData> list, IStaffData staffData, String partialSuccess) throws DataManagerException {
			return create(list, staffData, partialSuccess);
		}

		private Map<String, List<Status>> create(List<NASPolicyInstData> list, IStaffData staffData, String partialSuccess) throws DataManagerException {
			return insertRecords(DiameterNASPolicyDataManager.class, list, staffData, ConfigConstant.CREATE_NAS_SERVICE_POLICY, partialSuccess);
		}
		
		public void deleteNASPolicy(List<String> policiesToBeDelete, IStaffData staffData) throws DataManagerException {
			delete(policiesToBeDelete, staffData, BY_ID);
		}

		public void deleteNASPolicyByName(List<String> policyNames, IStaffData staffData) throws DataManagerException {
			delete(policyNames, staffData, BY_NAME);
		}

		private void delete(List<String> policiesToBeDelete, IStaffData staffData, boolean deleteByIDorName) throws DataManagerException {
			IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
			DiameterNASPolicyDataManager nasServicePoilcyDataManager = getDiameterNASPolicyDataManager(session);
			
			if (nasServicePoilcyDataManager == null) {
				throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
			}

			try {
				session.beginTransaction();
				for (String policyToDelete : policiesToBeDelete) {
					if (Strings.isNullOrBlank(policyToDelete) == false) {
						String policy = policyToDelete.trim();
						String deletedPolicy;
						if (deleteByIDorName) {
							deletedPolicy = nasServicePoilcyDataManager.deleteById(policy);
						} else {
							deletedPolicy = nasServicePoilcyDataManager.deleteByName(policy);
						}
						staffData.setAuditName(deletedPolicy);
						AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_CREDIT_CONTROL_SERVICE_POLICY);
					}
				}
				commit(session);
			}catch(DataManagerException e){
				rollbackSession(session);
				throw e;
			}catch(Exception e){
				e.printStackTrace();
				rollbackSession(session);
				throw new DataManagerException(e.getMessage(),e);
			}finally{
				closeSession(session);
			}
		}
		
		public void updateNASPolicyStatus(List<String> nasPolicyIds,String showStatusId) throws DataManagerException{
			IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
			DiameterNASPolicyDataManager diameterNASServicePoilcyDataManager = getDiameterNASPolicyDataManager(session);
			if(diameterNASServicePoilcyDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
			}

			try{				     
				session.beginTransaction();
				diameterNASServicePoilcyDataManager.updateStatus(nasPolicyIds, showStatusId);
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

		public PageList searchNASServicePolicy(NASPolicyInstData nasPolicyInstData, int requiredPageNo,Integer pageSize) throws DataManagerException {

			IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
			DiameterNASPolicyDataManager diameterNASServicePoilcyDataManager = getDiameterNASPolicyDataManager(session);
			if(diameterNASServicePoilcyDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
			}
			try{				     
			    PageList pageList = diameterNASServicePoilcyDataManager.search(nasPolicyInstData, requiredPageNo, pageSize);
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

		public List<NASPolicyInstData> searchNASServicePolicy() throws DataManagerException{
			IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
			DiameterNASPolicyDataManager diameterNASPolicyDataManager=getDiameterNASPolicyDataManager(session);
			
			if(diameterNASPolicyDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
			}
			try{				     
			    List<NASPolicyInstData> list = diameterNASPolicyDataManager.searchActiveNASServicePolicy();
				return list;
			}catch(DataManagerException e){
	        	throw e;
			}catch(Exception e){
				e.printStackTrace();
	        	throw new DataManagerException(e.getMessage(),e);
			} finally {
				closeSession(session);
			}
		}
	
		public NASPolicyInstData getDiameterServicePolicyDataByPolicyId(String nasServicePolicyId) throws DataManagerException {
			IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			DiameterNASPolicyDataManager diameterNASServicePoilcyDataManager = getDiameterNASPolicyDataManager(session);
			
			if(diameterNASServicePoilcyDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
			}				

			try{				     
				NASPolicyInstData policyInstData = diameterNASServicePoilcyDataManager.getDiameterServicePolicyByPolicyId(nasServicePolicyId);			    				
				return policyInstData;
			}catch(DataManagerException e){
	        	throw e;
			}catch(Exception e){
				e.printStackTrace();
	        	throw new DataManagerException(e.getMessage(),e);
			} finally {
				closeSession(session);
			}
			
		}
		
		public NASPolicyInstData getDiameterServicePolicyDataByPolicyByName(String nasServicePolicyId) throws DataManagerException {
			IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			DiameterNASPolicyDataManager diameterNASServicePoilcyDataManager = getDiameterNASPolicyDataManager(session);
			
			if(diameterNASServicePoilcyDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
			}				
			try{				     
				NASPolicyInstData policyInstData = diameterNASServicePoilcyDataManager.getDiameterServicePolicyByName(nasServicePolicyId.trim());
				convertPolicyData(policyInstData);
				return policyInstData;
			}catch(DataManagerException e){
	        	throw e;
			}catch(Exception e){
				e.printStackTrace();
	        	throw new DataManagerException(e.getMessage(),e);
			} finally {
				closeSession(session);
			}
			
		}
		
		private void convertPolicyData(NASPolicyInstData policyInstData) {
			List<NASPolicyAuthDriverRelData> primaryDrivers = policyInstData.getNasPolicyAuthDriverRelList();
			List<NASPolicyAdditionalDriverRelData> additionalDrivers = policyInstData.getNasPolicyAdditionalDriverRelDataList();
			List<NASPolicyAcctDriverRelData> acctDrivers = policyInstData.getNasPolicyAcctDriverRelList();

			DriverBLManager blManager = new DriverBLManager();
			for (NASPolicyAuthDriverRelData primaryDriver : primaryDrivers) {
				String driverId = primaryDriver.getDriverInstanceId();
				try {
					String driverName = blManager.getDriverNameById(driverId);
					primaryDriver.setDriverName(driverName);
				} catch (DataManagerException e) {
					e.printStackTrace();
				}
			}
			for (NASPolicyAdditionalDriverRelData additionalDriver : additionalDrivers) {
				String driverId = additionalDriver.getDriverInstanceId();
				try {
					String driverName = blManager.getDriverNameById(driverId);
					additionalDriver.setDriverName(driverName);
				} catch (DataManagerException e) {
					e.printStackTrace();
				}
			}
			for (NASPolicyAcctDriverRelData acctDriver : acctDrivers) {
				String driverId = acctDriver.getDriverInstanceId();
				try {
					String driverName = blManager.getDriverNameById(driverId);
					acctDriver.setDriverName(driverName);
				} catch (DataManagerException e) {
					e.printStackTrace();
				}
			}
		}

		public void updateNasPolicyBasicDetails(NASPolicyInstData policyData,IStaffData staffData,String actionAlias) throws DataManagerException {
			IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			DiameterNASPolicyDataManager diameterNASServicePoilcyDataManager = getDiameterNASPolicyDataManager(session);
			
			if(diameterNASServicePoilcyDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
			}				
			try{				     
				session.beginTransaction();
				diameterNASServicePoilcyDataManager.updateNasPolicyBasicDetails(policyData,staffData,actionAlias);			    				
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
		
		public List<NASPolicyAuthMethodRelData> getDiameterServicePolicyAuthMethodRel(String nasServicePolicyId) throws DataManagerException {
			IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			DiameterNASPolicyDataManager diameterNASServicePoilcyDataManager = getDiameterNASPolicyDataManager(session);
			
			if(diameterNASServicePoilcyDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
			}				
			try{				     
				List<NASPolicyAuthMethodRelData> methodRelData = diameterNASServicePoilcyDataManager.getDiameterAuthMethodRel(nasServicePolicyId);			    				
				return methodRelData;
			}catch(DataManagerException e){
	        	throw e;
			}catch(Exception e){
				e.printStackTrace();
	        	throw new DataManagerException(e.getMessage(),e);
			} finally {
				closeSession(session);
			}
			
		}
		
		public List<NASPolicyAuthDriverRelData> getDiameterServicePolicyAuthDriverRel(String nasServicePolicyId) throws DataManagerException {
			IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			DiameterNASPolicyDataManager diameterNASServicePoilcyDataManager = getDiameterNASPolicyDataManager(session);
			
			if(diameterNASServicePoilcyDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
			}				
			try{				     
				List<NASPolicyAuthDriverRelData> policyInstData = diameterNASServicePoilcyDataManager.getDiameterAuthDriverRel(nasServicePolicyId);
				return policyInstData;
			}catch(DataManagerException e){
	        	throw e;
			}catch(Exception e){
				e.printStackTrace();
	        	throw new DataManagerException(e.getMessage(),e);
			} finally {
				closeSession(session);
			}
		}
		
		public List<NASPolicyAdditionalDriverRelData> getDiameterServicePolicyAdditionalDriver(String nasServicePolicyId) throws DataManagerException {
			IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			DiameterNASPolicyDataManager diameterNASServicePoilcyDataManager = getDiameterNASPolicyDataManager(session);
			if(diameterNASServicePoilcyDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
			}				
			try{
				List<NASPolicyAdditionalDriverRelData> nasPolicyAdditionalDriverRelDataList = diameterNASServicePoilcyDataManager.getDiameterAdditionalDriverRel(nasServicePolicyId);
				return nasPolicyAdditionalDriverRelDataList;
			}catch(DataManagerException e){
	        	throw e;
			}catch(Exception e){
				e.printStackTrace();
	        	throw new DataManagerException(e.getMessage(),e);
			} finally {
				closeSession(session);
			}
		}
			
		
		
		public List<NASPolicyAcctDriverRelData> getDiameterServicePolicyAcctDriverRel(String nasServicePolicyId) throws DataManagerException {
			IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			DiameterNASPolicyDataManager diameterNASServicePoilcyDataManager = getDiameterNASPolicyDataManager(session);
			
			if(diameterNASServicePoilcyDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
			}				
			try{				     
				List<NASPolicyAcctDriverRelData> policyInstData = diameterNASServicePoilcyDataManager.getDiameterAcctDriverRel(nasServicePolicyId);
				return policyInstData;
			}catch(DataManagerException e){
	        	throw e;
			}catch(Exception e){
				e.printStackTrace();
	        	throw new DataManagerException(e.getMessage(),e);
			} finally {
				closeSession(session);
			}
			
		}		
		
		public void updateAuthenticationParams(NASPolicyInstData data,IStaffData staffData,String actionAlias) throws DataManagerException {
			IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			DiameterNASPolicyDataManager diameterNASServicePoilcyDataManager = getDiameterNASPolicyDataManager(session);
			
			if(diameterNASServicePoilcyDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
			}				
			try{				     
				session.beginTransaction();
				diameterNASServicePoilcyDataManager.updateAuthenticationParams(data,staffData,actionAlias);
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
		
		public void updateAuthorizationParams(NASPolicyInstData data,IStaffData staffData,String actionAlias) throws DataManagerException {
			IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			DiameterNASPolicyDataManager diameterNASServicePoilcyDataManager = getDiameterNASPolicyDataManager(session);
			
			if(diameterNASServicePoilcyDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
			}				
			try{				     
				session.beginTransaction();
				diameterNASServicePoilcyDataManager.updateAuthorizationParams(data,staffData,actionAlias);
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
		
		public void updateAccountingParams(NASPolicyInstData data,IStaffData staffData,String actionAlias) throws DataManagerException {
			IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			DiameterNASPolicyDataManager diameterNASServicePoilcyDataManager = getDiameterNASPolicyDataManager(session);
			
			if(diameterNASServicePoilcyDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
			}				
			try{				     
				session.beginTransaction();
				diameterNASServicePoilcyDataManager.updateAccountingParams(data,staffData,actionAlias);
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

		public List<NASResponseAttributes> getDiameterServicePolicyNASResponseAttributes(String nasPolicyId)throws DataManagerException { {
			IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			DiameterNASPolicyDataManager diameterNASServicePoilcyDataManager = getDiameterNASPolicyDataManager(session);
			
			if(diameterNASServicePoilcyDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
			}				
			try{				     
				List<NASResponseAttributes> nasResponseAttributesData = diameterNASServicePoilcyDataManager.getResponseAttributes(nasPolicyId);
				return nasResponseAttributesData;
			}catch(DataManagerException e){
	        	throw e;
			}catch(Exception e){
				e.printStackTrace();
	        	throw new DataManagerException(e.getMessage(),e);
			} finally {
				closeSession(session);
			}
			
		}		
		
	}

		public void updateNasResponseAttribute(NASPolicyInstData policyInstData, IStaffData staffData,String actionAlias) throws DataManagerException {
			IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			DiameterNASPolicyDataManager diameterNASServicePoilcyDataManager = getDiameterNASPolicyDataManager(session);
			
			if(diameterNASServicePoilcyDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
			}				
			try{		
				session.beginTransaction();
				diameterNASServicePoilcyDataManager.updateNasResponseAttribute(policyInstData,staffData,actionAlias);
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

		public void updateRFC4372CUIParams(NASPolicyInstData policyData, IStaffData staffData, String actionAlias) throws DataManagerException {
			IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			DiameterNASPolicyDataManager diameterNASServicePoilcyDataManager = getDiameterNASPolicyDataManager(session);
			
			if(diameterNASServicePoilcyDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
			}				
			try{				     
				session.beginTransaction();
				diameterNASServicePoilcyDataManager.updateRFC4372CUIParams(policyData,staffData,actionAlias);
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
		
		/**
		 * This is used for get all NAS service policy included with Active and Inactive status .
		 * @return List of NAS policy
		 * @throws DataManagerException
		 */
		public List<NASPolicyInstData> getNASServicePolicyList() throws DataManagerException{
			IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
			DiameterNASPolicyDataManager diameterNASPolicyDataManager=getDiameterNASPolicyDataManager(session);
			
			if(diameterNASPolicyDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
			}
			try{				     
				
				List<NASPolicyInstData> list = diameterNASPolicyDataManager.getNASServicePolicyList();
				return list;
			}catch(DataManagerException e){
	        	throw e;
			}catch(Exception e){
				e.printStackTrace();
				throw new DataManagerException(e.getMessage(),e);
			} finally {
				closeSession(session);
			}

		}
		/**
		 * This method is used to get binded plugin-names with NAS service policies .
		 * @return set of plugin-names .
		 * @throws DataManagerException
		 */
		public Set<String> getBindedPluginNames() throws DataManagerException{
			Set<String> bindedPluginNames = new HashSet<String>();
			try{
				List<NASPolicyInstData> NasPolicyDataList = getNASServicePolicyList();
				if (NasPolicyDataList != null && NasPolicyDataList.isEmpty() == false) {
		        	 for(NASPolicyInstData nasPolicyInstData : NasPolicyDataList){
		        		 List<NASPolicyAuthPluginConfig> authPluginConfigs = nasPolicyInstData.getNasPolicyAuthPluginConfigList();
		        		 if (authPluginConfigs != null && authPluginConfigs.isEmpty() == false) {
		        			 for (NASPolicyAuthPluginConfig authPluginConfig: authPluginConfigs) {
		        				bindedPluginNames.add(authPluginConfig.getPluginName()); 
		        			 }
		        		 }
		        	 }
		         }
			}catch (DataManagerException e) { 
				throw new DataManagerException(e.getMessage(),e);
			}
			return bindedPluginNames;
	    }
		
	public void update(NASPolicyInstData policyData, String policyName, IStaffData staffData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterNASPolicyDataManager diameterNASServicePoilcyDataManager = getDiameterNASPolicyDataManager(session);
		
		if(diameterNASServicePoilcyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
		}				
		try{				     
			session.beginTransaction();
			
			diameterNASServicePoilcyDataManager.updateByName(policyData, policyName.trim(), staffData, ConfigConstant.UPDATE_NAS_SERVICE_POLICY);
			
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
}