package com.elitecore.elitesm.blmanager.servicepolicy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import com.elitecore.aaa.core.constant.AuthMethods;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.digestconf.DigestConfDataManager;
import com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData;
import com.elitecore.elitesm.datamanager.externalsystem.ExternalSystemInterfaceDataManager;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.eap.EAPConfigDataManager;
import com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData;
import com.elitecore.elitesm.datamanager.servicepolicy.acct.AcctServicePoilcyDataManager;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.AuthServicePoilcyDataManager;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyAdditionalDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyBroadcastESIRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyDigestConfRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyExternalSystemRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyRMParamsData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicySMRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicySecDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.DynAuthServicePoilcyDataManager;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynaAuthPolicyESIRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.RadiusServicePolicyDataManager;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.AccountingPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.AcctServicePolicyHandlerData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.AdditionalDriverDetail;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.AsyncCommunicationEntryData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.AuthResponseBehaviors;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.AuthServicePolicyHandlerData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.AuthenticationHandlerData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.AuthenticationPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.BroadcastCommunicationHandlerData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.CdrGenerationHandlerData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.CdrHandlerEntryData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.CoADMGenerationHandlerData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.CoADMHandlerEntryData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.CommunicatorData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.ConcurrencyHandlerData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.ExternalCommunicationEntryData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.PrimaryDriverDetail;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadServicePolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadiusServicePolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadiusSubscriberProfileRepositoryDetails;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.SecondaryAndCacheDriverDetail;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.SynchronousCommunicationHandlerData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.UpdateIdentityParamsDetail;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.XmlNodeUtility;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;


public class ServicePolicyBLManager  extends BaseBLManager{
	
	public AuthServicePoilcyDataManager getAuthServicePolicyDataManager(IDataManagerSession session){
		AuthServicePoilcyDataManager authServicePoilcyDataManager = (AuthServicePoilcyDataManager)DataManagerFactory.getInstance().getDataManager(AuthServicePoilcyDataManager.class, session);
		return authServicePoilcyDataManager;
	}
	
	public DynAuthServicePoilcyDataManager getDynAuthServicePolicyDataManager(IDataManagerSession session){
		DynAuthServicePoilcyDataManager dynAuthServicePoilcyDataManager = (DynAuthServicePoilcyDataManager)DataManagerFactory.getInstance().getDataManager(DynAuthServicePoilcyDataManager.class, session);
		return dynAuthServicePoilcyDataManager;
	}
	
	public EAPConfigDataManager getEAPConfigDataManager(IDataManagerSession session){
		EAPConfigDataManager eapConfigDataManager = (EAPConfigDataManager)DataManagerFactory.getInstance().getDataManager(EAPConfigDataManager.class, session);
		return eapConfigDataManager;
	}
	
	public DigestConfDataManager getDigestConfDataManager(IDataManagerSession session){
		DigestConfDataManager digestConfDataManager = (DigestConfDataManager)DataManagerFactory.getInstance().getDataManager(DigestConfDataManager.class, session);
		return digestConfDataManager;
	}
	
	public AcctServicePoilcyDataManager getAcctServicePolicyDataManager(IDataManagerSession session){
		AcctServicePoilcyDataManager acctServicePoilcyDataManager = (AcctServicePoilcyDataManager)DataManagerFactory.getInstance().getDataManager(AcctServicePoilcyDataManager.class, session);
		return acctServicePoilcyDataManager;
	}
	
	public RadiusServicePolicyDataManager getRadiusServicePolicyDataManager(IDataManagerSession session){
		RadiusServicePolicyDataManager radiusServicePoilcyDataManager = (RadiusServicePolicyDataManager)DataManagerFactory.getInstance().getDataManager(RadiusServicePolicyDataManager.class, session);
		return radiusServicePoilcyDataManager;
	}
	
	public List<AuthMethodTypeData> getAuthMethodTypeList() throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AuthServicePoilcyDataManager authServicePoilcyDataManager = getAuthServicePolicyDataManager(session);
		
		if (authServicePoilcyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
		}
		
		try{
			return authServicePoilcyDataManager.getAuthMethodTypeList();
		}catch(DataManagerException e){
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}
	
	public void createDynAuthPolicy(DynAuthPolicyInstData dynAuthPolicyInstData, IStaffData staffData)throws DataManagerException {
		List<DynAuthPolicyInstData> policy = new ArrayList<DynAuthPolicyInstData>();
		policy.add(dynAuthPolicyInstData);
		createDynaAuthPolicy(policy, staffData, "false");
	}
	
	public Map<String, List<Status>> createDynaAuthPolicy(List<DynAuthPolicyInstData> policies, IStaffData staffData, String partialSuccess) throws DataManagerException {
		return insertRecords(DynAuthServicePoilcyDataManager.class, policies, staffData, ConfigConstant.CREATE_DYNAUTH_POLICY, partialSuccess);
	}
	
	public AuthPolicyInstData getAuthPolicyInstData(AuthPolicyInstData authPolicyInstData,IStaffData staffData, String actionAlias) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();

		AuthServicePoilcyDataManager authServicePoilcyDataManager = getAuthServicePolicyDataManager(session);
		if(authServicePoilcyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
		}
		
		try{
			List<AuthPolicyInstData> authPolicyInstDataList =authServicePoilcyDataManager.getAuthPolicyInstDataList(authPolicyInstData);
			if(Collectionz.isNullOrEmpty(authPolicyInstDataList) == false){
				return authPolicyInstDataList.get(0);
			}else{
				return null;
			}
		}catch(DataManagerException e){
        	throw e;
		}catch(Exception e){
			e.printStackTrace();
        	throw new DataManagerException(e.getMessage(),e);
		}finally{
			session.close();
		}
	}
	
	public DynAuthPolicyInstData getDynAuthPolicyInstDataDetails(String dynAuthPolicyId) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();

		DynAuthServicePoilcyDataManager dynAuthServicePoilcyDataManager = getDynAuthServicePolicyDataManager(session);
		if(dynAuthServicePoilcyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
		}
		
		try{
			return dynAuthServicePoilcyDataManager.getDynAuthPolicyInstDataById(dynAuthPolicyId);
		}catch(DataManagerException e){
        	throw e;
		}catch(Exception e){
			e.printStackTrace();
        	throw new DataManagerException(e.getMessage(),e);
		}finally{
			session.close();
		}

	}
	public DynAuthPolicyInstData getDynAuthPolicyInstData(String policyId) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();

		DynAuthServicePoilcyDataManager dynAuthServicePoilcyDataManager = getDynAuthServicePolicyDataManager(session);
		if(dynAuthServicePoilcyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
		}
		
		try{
			return dynAuthServicePoilcyDataManager.getDynAuthPolicyInstDataById(policyId);
		}catch(DataManagerException e){
        	throw e;
		}catch(Exception e){
			e.printStackTrace();
        	throw new DataManagerException(e.getMessage(),e);
		}finally{
			session.close();
		}

	}
	
	public void updateDynAuthPolicyById(DynAuthPolicyInstData policyData,IStaffData staffData) throws DataManagerException ,DuplicateInstanceNameFoundException{
		updateDynauthServicePolicy(policyData, staffData, null);
	}
	
	public void updateDynAuthPolicyByName(DynAuthPolicyInstData dynAuthPolicyInstData, IStaffData staffData, String policyName) throws DataManagerException ,DuplicateInstanceNameFoundException{
		updateDynauthServicePolicy(dynAuthPolicyInstData, staffData, policyName);
	}

	private void updateDynauthServicePolicy(DynAuthPolicyInstData policyData, IStaffData staffData, String updateByPolicyNameOrId) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		
		DynAuthServicePoilcyDataManager dynAuthServicePoilcyDataManager = getDynAuthServicePolicyDataManager(session);
		if (dynAuthServicePoilcyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
		}

		try{
			session.beginTransaction();
			if (updateByPolicyNameOrId == null) {
				dynAuthServicePoilcyDataManager.updateById(policyData, staffData);
			} else {
				dynAuthServicePoilcyDataManager.updateByName(policyData, staffData, updateByPolicyNameOrId);
			}
			commit(session);
		} catch(DataManagerException e) {
        	rollbackSession(session);
        	throw e;
		}catch(Exception e){
			e.printStackTrace();
        	rollbackSession(session);
        	throw new DataManagerException(e.getMessage(), e);
		}finally {
			closeSession(session);
		}	
	}
	
	public PageList searchDynAuthServicePolicy(DynAuthPolicyInstData dynAuthPolicyInstData ,int pageNo, Integer pageSize, IStaffData staffData) throws DataManagerException{
		
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();

		DynAuthServicePoilcyDataManager dynAuthServicePoilcyDataManager = getDynAuthServicePolicyDataManager(session);
		if(dynAuthServicePoilcyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
		}
		try{				     
			session.beginTransaction();
		 	
			PageList pageList = dynAuthServicePoilcyDataManager.search(dynAuthPolicyInstData, pageNo, pageSize);
		 	AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_DYNAUTH_POLICY);
		 	commit(session);
		 	
		 	return pageList;
		}catch(DataManagerException e){
			rollbackSession(session);
        	throw e;
		}catch(Exception e){
			rollbackSession(session);
        	throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}

	public void deleteDynAuthPolicyById(List<String> dynAuthPolicyIds, IStaffData staffData) throws DataManagerException{
		delete(dynAuthPolicyIds, staffData, BY_ID);
	}
	
	public void deleteDynAuthPolicyByName(List<String> name, StaffData staffData) throws DataManagerException {
		delete(name, staffData, BY_NAME);
	}
	
	private void delete(List<String> policiesToDelete, IStaffData staffData, boolean deleteByIDorName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();

		DynAuthServicePoilcyDataManager dynAuthServicePoilcyDataManager = getDynAuthServicePolicyDataManager(session);
		if (dynAuthServicePoilcyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			session.beginTransaction();
			for (String policyToDelete : policiesToDelete) {
				String deletedPolicy;
				if (deleteByIDorName) {
					deletedPolicy = dynAuthServicePoilcyDataManager.deleteById(policyToDelete);
				} else {
					deletedPolicy = dynAuthServicePoilcyDataManager.deleteByName(policyToDelete);
				}
				staffData.setAuditName(deletedPolicy);
				AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_DYNAUTH_POLICY);
			}
			commit(session);
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(), e);
		}finally {
			closeSession(session);
		}
	}

	public void updateDynAuthPolicyStatus(List<String> authPolicyIds,String status) throws DataManagerException{
		
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		
		DynAuthServicePoilcyDataManager dynAuthServicePoilcyDataManager = getDynAuthServicePolicyDataManager(session);
		if(dynAuthServicePoilcyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for: " + getClass().getName());
		}
		
		try{				     
			session.beginTransaction();
			dynAuthServicePoilcyDataManager.updateStatus(authPolicyIds, status);
			commit(session);
		}catch(DataManagerException e){
        	rollbackSession(session);
        	throw new DataManagerException(e.getMessage(),e);
		}catch(Exception e){
			e.printStackTrace();
        	rollbackSession(session);
        	throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}
	
	public List<DynAuthPolicyInstData> searchDynAuthServicePolicy(IStaffData staffData) throws DataManagerException{
		
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();

		DynAuthServicePoilcyDataManager dynAuthServicePoilcyDataManager = getDynAuthServicePolicyDataManager(session);
		if(dynAuthServicePoilcyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{				     
			session.beginTransaction();
			List<DynAuthPolicyInstData> policies = dynAuthServicePoilcyDataManager.searchActiveDynAuthServicePolicy();
		 	AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_DYNAUTH_POLICY);
		 	commit(session);
		 	return policies;
		}catch(DataManagerException e){
        	throw e;
		}catch(Exception e){
			e.printStackTrace();
        	throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}
	
	public List<AuthPolicySecDriverRelData> getAuthPolicySecDriverList(AuthPolicyInstData authPolicyInstData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();

		AuthServicePoilcyDataManager authServicePolicyDataManager = getAuthServicePolicyDataManager(session);
		if(authServicePolicyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			return authServicePolicyDataManager.getAuthPolicySecDriverList(authPolicyInstData);
		}catch(DataManagerException e){
        	throw e;
		}catch(Exception e){
			e.printStackTrace();
        	throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}
	
	public List<AuthPolicyAdditionalDriverRelData> getAuthPolicyAdditionalDriverList(AuthPolicyInstData authPolicyInstData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AuthServicePoilcyDataManager authServicePolicyDataManager = getAuthServicePolicyDataManager(session);
		if(authServicePolicyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			return authServicePolicyDataManager.getAuthPolicyAdditionalDriverList(authPolicyInstData);
		}catch(DataManagerException e){
        	throw e;
		}catch(Exception e){
			e.printStackTrace();
        	throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}
	
	public List<AuthPolicySMRelData> getAuthPolicySMRelData(String authPolicyId) throws DataManagerException{
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();

		AuthServicePoilcyDataManager authServicePoilcyDataManager = getAuthServicePolicyDataManager(session);
		if(authServicePoilcyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{				     
			return authServicePoilcyDataManager.getAuthPolicySMRelData(authPolicyId);
		}catch(DataManagerException e){
        	throw e;
		}catch(Exception e){
			e.printStackTrace();
        	throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}
	
	public List<AuthPolicyExternalSystemRelData> getExternalSystemRelList(AuthPolicyInstData authPolicyInstData,long externalSystemTypeId) throws DataManagerException{
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();

		AuthServicePoilcyDataManager authServicePoilcyDataManager = getAuthServicePolicyDataManager(session);
		if(authServicePoilcyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			return authServicePoilcyDataManager.getExternalSystemRelList(authPolicyInstData, externalSystemTypeId);
		}catch(DataManagerException e){
        	throw e;
		}catch(Exception e){
			e.printStackTrace();
        	throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}
	
	public List<AuthPolicyBroadcastESIRelData> getBroadcastingESIRelList(AuthPolicyInstData authPolicyInstData,long externalSystemTypeId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		
		AuthServicePoilcyDataManager authServicePoilcyDataManager = getAuthServicePolicyDataManager(session);
		if(authServicePoilcyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			return authServicePoilcyDataManager.getBroadcastingESIRelList(authPolicyInstData, externalSystemTypeId);
		}catch(DataManagerException e){
        	throw e;
		}catch(Exception e){
			e.printStackTrace();
        	throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}
	
	public List<DriverInstanceData> getMainDriverList(AuthPolicyInstData authPolicyInstData) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		
		AuthServicePoilcyDataManager authServicePoilcyDataManager = getAuthServicePolicyDataManager(session);
		if(authServicePoilcyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			return authServicePoilcyDataManager.getMainDriverList(authPolicyInstData);
		}catch(DataManagerException e){
        	throw e;
		}catch(Exception e){
			e.printStackTrace();
        	throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}

	public List<AuthMethodTypeData> getSupportedAuthMethods(AuthPolicyInstData authPolicyInstData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		
		AuthServicePoilcyDataManager authServicePoilcyDataManager = getAuthServicePolicyDataManager(session);
		if(authServicePoilcyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			return authServicePoilcyDataManager.getSupportedAuthMethods(authPolicyInstData);
		}catch(DataManagerException e){
        	throw e;
		}catch(Exception e){
			e.printStackTrace();
        	throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}
	
	public DigestConfigInstanceData getHttpDigestInstanceData(AuthPolicyInstData authPolicyInstData) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
	
		DigestConfDataManager digestConfDataManager = getDigestConfDataManager(session);
		AuthServicePoilcyDataManager authServicePoilcyDataManager = getAuthServicePolicyDataManager(session);
		if (authServicePoilcyDataManager == null
				|| digestConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try {
			AuthPolicyDigestConfRelData digestConfRelData = authServicePoilcyDataManager.getDigestConfigRelData(authPolicyInstData);
			return digestConfDataManager.getDigestConfigInstDataById(digestConfRelData.getDigestConfId());
		} catch (DataManagerException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	public EAPConfigData getEAPConfigInstance(AuthPolicyInstData authPolicyInstData) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		EAPConfigDataManager eapConfigDataManager = getEAPConfigDataManager(session);

		if(eapConfigDataManager==null){
			throw new DataManagerException("Data Manager Not Found: AuthServicePoilcyDataManager/EAPConfigDataManager.");
		}
		
		try{
			return eapConfigDataManager.getEAPConfigDataById(authPolicyInstData.getEapConfigId());
		}catch(DataManagerException e){
        	throw e;
		}catch(Exception e){
			e.printStackTrace();
        	throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}
	
	public AuthPolicyRMParamsData getRMParamsData(AuthPolicyInstData authPolicyInstData,long externalSystemTypeId) throws DataManagerException{
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		AuthServicePoilcyDataManager authServicePoilcyDataManager = getAuthServicePolicyDataManager(session);
		if(authServicePoilcyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			return authServicePoilcyDataManager.getRMParamsData(authPolicyInstData,externalSystemTypeId);
		}catch(DataManagerException e){
        	throw e;
		}catch(Exception e){
			e.printStackTrace();
        	throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	public List<DynaAuthPolicyESIRelData> getNASClients(String dynAuthPolicyId) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		
		DynAuthServicePoilcyDataManager dynAuthServicePoilcyDataManager = getDynAuthServicePolicyDataManager(session);
		if(dynAuthServicePoilcyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			return dynAuthServicePoilcyDataManager.getNASClients(dynAuthPolicyId);
		}catch(DataManagerException e){
        	throw e;
		}catch(Exception e){
			e.printStackTrace();
        	throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	public void createRadiusServicePolicy(RadiusServicePolicyData radiusServicePolicyData,IStaffData staffData) throws DataManagerException{
		List<RadiusServicePolicyData> policyData = new ArrayList<RadiusServicePolicyData>();
		policyData.add(radiusServicePolicyData);
		createRadiusServicePolicy(policyData, staffData, "false");
	}
	
	public Map<String, List<Status>> createRadiusServicePolicy(List<RadiusServicePolicyData> policyData, IStaffData staffData, String partialSuccess) throws DataManagerException {

		List<RadServicePolicyData> radServicePolicyDatas = new ArrayList<RadServicePolicyData>();
		for (RadiusServicePolicyData data : policyData) {
			if(AuthResponseBehaviors.HOTLINE.name().equals(data.getDefaultAuthResponseBehavior()) == false) {
				data.setHotlinePolicy(null);
			}
			radServicePolicyDatas.add(convertToRadiusServicePolicyDataBeanFrom(data));
		}
		return insertRecords(RadiusServicePolicyDataManager.class, radServicePolicyDatas, staffData, ConfigConstant.CREATE_RADIUS_SERVICE_POLICY, partialSuccess);
	}
	
	private RadServicePolicyData convertToRadiusServicePolicyDataBeanFrom(RadiusServicePolicyData policyData) throws DataManagerException {
		RadServicePolicyData data = new RadServicePolicyData();
		data.setRadiusPolicyId(policyData.getPolicyId());
		data.setName(policyData.getName());
		data.setDescription(policyData.getDescription());
		
		if(policyData.getStatus().equalsIgnoreCase("ACTIVE")){
			data.setStatus("CST01");
		} else if(policyData.getStatus().equalsIgnoreCase("INACTIVE")) {
			data.setStatus("CST02");
		}
		
		data.setAuthMsg(((policyData.getSupportedMessages()!= null && policyData.getSupportedMessages()
				.getAuthenticationMessageEnabled() != null && policyData
				.getSupportedMessages().getAuthenticationMessageEnabled()
				.equals("true"))) ? "true" : "false");
		data.setAcctMsg(((policyData.getSupportedMessages() != null && policyData.getSupportedMessages()
				.getAccountingMessageEnabled() != null && policyData
				.getSupportedMessages().getAccountingMessageEnabled()
				.equals("true"))) ? "true" : "false");
		data.setAuthRuleset(policyData.getAuthenticationRuleset());
		data.setAcctRuleset(policyData.getAccountingRuleset());
		data.setValidatepacket((policyData.getValidatePacket() != null && policyData.getValidatePacket().equals("true")) ? "true" : "false");
		data.setDefaultAuthResBehavior(policyData.getDefaultAuthResponseBehavior());
		data.setHotlinePolicy(policyData.getHotlinePolicy());
		data.setDefaultAcctResBehavior(policyData.getDefaultAcctResponseBehavior());
		
		if (Strings.isNullOrBlank(policyData.getSessionManagerId()) == false &&
				"0".equalsIgnoreCase(policyData.getSessionManagerId()) == false && "--None--".equals(policyData.getSessionManagerId()) == false) {
			String sessioManagerId =(getSessionManagerName(policyData.getSessionManagerId(), BY_NAME));
			data.setSessionManagerId(sessioManagerId);
			policyData.setSessionManagerId(sessioManagerId);
		} else {
			data.setSessionManagerId(null);
			policyData.setSessionManagerId(null);
		}
		
		data.setUserIdentity(policyData.getUserIdentity());
		data.setAuthResponseAttributes(policyData.getAuthResponseAttributes());
		data.setAcctResponseAttributes(policyData.getAcctResponseAttributes());
		
		//CUI Configuration
		data.setCui(policyData.getCuiConfiguration().getCui());
		data.setAdvancedCuiExpression(policyData.getCuiConfiguration().getExpression());
		data.setAuthAttribute(policyData.getCuiConfiguration().getAuthenticationCuiAttribute());
		
		data.setAcctAttribute(policyData.getCuiConfiguration().getAccountingCuiAttribute());
		
		if( policyData.getAuthenticationPolicyData() != null ){
			convertAuthFlowData(policyData.getAuthenticationPolicyData());
		}
		
		if( policyData.getAccountingPolicyData() != null ){
			convertAcctFlowData(policyData.getAccountingPolicyData());
		}

		try {
			byte[] convertToXml = convertToXml(policyData);
			data.setRadiusPolicyXml(convertToXml);
			String xmlDatas = new String(convertToXml);
			System.out.println("************************Created XML Data*******************************");
			System.out.println(xmlDatas);
			System.out.println("***********************************************************************");
			System.out.println("XML Length : "+ xmlDatas.length());
			System.out.println("***********************************************************************");
		} catch(JAXBException e) {
			throw new DataManagerException(e.getMessage(), e);
		}
		
		return data;
	}
	
	private void convertAuthFlowData(AuthenticationPolicyData authenticationPolicyData) throws DataManagerException {
		for (AuthServicePolicyHandlerData handlerData : authenticationPolicyData.getHandlersData()) {
 			
			if (handlerData instanceof AuthenticationHandlerData) {
				AuthenticationHandlerData authenticationHandlerData = (AuthenticationHandlerData)handlerData;
				List<String> supportedMethods = authenticationHandlerData.getSupportedMethods();
				List<String> supportedMethodsId = new ArrayList<String>();
				
				for (String authMethod : supportedMethods) {
					if ("PAP".equalsIgnoreCase(authMethod)) {
						supportedMethodsId.add(String.valueOf(AuthMethods.PAP));
					} else if ("CHAP".equalsIgnoreCase(authMethod)) {
						supportedMethodsId.add(String.valueOf(AuthMethods.CHAP));
					} else if ("EAP".equalsIgnoreCase(authMethod)) {
						supportedMethodsId.add(String.valueOf(AuthMethods.EAP));
					} else if ("DIGEST".equalsIgnoreCase(authMethod)) {
						supportedMethodsId.add(String.valueOf(AuthMethods.DIGEST));
					} else if ("PROXY".equalsIgnoreCase(authMethod)) {
						supportedMethodsId.add(String.valueOf(AuthMethods.PROXY));
					}
				}
				setDigestConfiguraton(((AuthenticationHandlerData) handlerData));
				setEapConfiguration(((AuthenticationHandlerData) handlerData));
				authenticationHandlerData.setSupportedMethods(supportedMethodsId);
				
			} else if (handlerData instanceof RadiusSubscriberProfileRepositoryDetails) {
				setProfileDriverDetail((RadiusSubscriberProfileRepositoryDetails)handlerData);
			} else if (handlerData instanceof CdrGenerationHandlerData) {
				setCdrHandlerData(((CdrGenerationHandlerData)handlerData));
			} else if (handlerData instanceof ConcurrencyHandlerData) {
				setConcurrencyHandlerData(((ConcurrencyHandlerData)handlerData));
			} else if (handlerData instanceof SynchronousCommunicationHandlerData) {
				setProxyCommunicationHandlerData(((SynchronousCommunicationHandlerData) handlerData));
			} else if (handlerData instanceof BroadcastCommunicationHandlerData) {
				setBroadCaseHandlerData(((BroadcastCommunicationHandlerData) handlerData));
			} else if (handlerData instanceof CoADMGenerationHandlerData) {
				setCoADMGenerateionHandlerData(((CoADMGenerationHandlerData) handlerData));
			}
		}
		
		if (authenticationPolicyData.getPostResponseHandlerData() != null) {
			if( Collectionz.isNullOrEmpty(authenticationPolicyData.getPostResponseHandlerData().getHandlersData()) == false ){
				for (AuthServicePolicyHandlerData handlerData : authenticationPolicyData.getPostResponseHandlerData().getHandlersData()){
					if (handlerData instanceof CdrGenerationHandlerData) {
						setCdrHandlerData(((CdrGenerationHandlerData)handlerData));
					} else if (handlerData instanceof CoADMGenerationHandlerData) {
						setCoADMGenerateionHandlerData(((CoADMGenerationHandlerData) handlerData));
					}
				}
			}
			
			if (authenticationPolicyData.getPostResponseHandlerData().getEnabled() == null) {
				authenticationPolicyData.getPostResponseHandlerData().setEnabled("true");
			}
		}
	}
	 
	private void setCoADMGenerateionHandlerData(CoADMGenerationHandlerData coaDMGenerationHandlerData) {
		List<CoADMHandlerEntryData> datas = coaDMGenerationHandlerData.getEntries();
		for (CoADMHandlerEntryData data : datas) {
			if ( "Disconnect Message".equals(data.getPacketType()) ) {
				data.setPacketType(String.valueOf(RadiusConstants.DISCONNECTION_REQUEST_MESSAGE));
			} else if ( "COA Request".equals(data.getPacketType()) ) {
				data.setPacketType(String.valueOf(RadiusConstants.COA_REQUEST_MESSAGE));
			}
		}
	}

	private void setEapConfiguration(AuthenticationHandlerData authenticationHandlerData) throws DataManagerException {
		String eapConfId = getEAPConfigId(authenticationHandlerData.getEapConfigId());
		authenticationHandlerData.setEapConfigId(eapConfId);
	}

	private void setDigestConfiguraton(AuthenticationHandlerData authenticationHandlerData) throws DataManagerException {
		String digestConfId = getDigestConfId( authenticationHandlerData.getDigestConfigId());
		authenticationHandlerData.setDigestConfigId(digestConfId);		
	}

	private void setBroadCaseHandlerData(BroadcastCommunicationHandlerData broadcastCommunicationHandlerData) throws DataManagerException {
		List<AsyncCommunicationEntryData> communicationEntryDatas =  broadcastCommunicationHandlerData.getProxyCommunicationEntries();
		if(Collectionz.isNullOrEmpty(communicationEntryDatas) == false){
			for(ExternalCommunicationEntryData communicationEntryData :communicationEntryDatas) {
				if( communicationEntryData.getCommunicatorGroupData() != null ){
					List<CommunicatorData> communicatorDatas = communicationEntryData.getCommunicatorGroupData().getCommunicatorDataList();
					for(CommunicatorData communicatorData : communicatorDatas) {
						communicatorData.setId(getRadiusESIIdByName(communicatorData.getId()));
					}
				}
			}
		}
	}

	private void setProxyCommunicationHandlerData(SynchronousCommunicationHandlerData synchronousCommunicationHandlerData) throws DataManagerException {
		List<ExternalCommunicationEntryData> communicationEntryDatas = synchronousCommunicationHandlerData.getProxyCommunicatioEntries();
		if(Collectionz.isNullOrEmpty(communicationEntryDatas) == false){ 
			for(ExternalCommunicationEntryData communicationEntryData : communicationEntryDatas) {
				if( communicationEntryData.getCommunicatorGroupData() != null ){
					List<CommunicatorData> communicatorDataList = communicationEntryData.getCommunicatorGroupData().getCommunicatorDataList();
					if(Collectionz.isNullOrEmpty(communicationEntryDatas) == false){
						for(CommunicatorData communicatorData : communicatorDataList) {
							communicatorData.setId(getRadiusESIIdByName(communicatorData.getId()));
						}
					}
				}
			}		
		}
	}

	private void setConcurrencyHandlerData(	ConcurrencyHandlerData concurrencyHandlerData) throws DataManagerException {
		String sessionManagerID = concurrencyHandlerData.getSessionManagerId();
		concurrencyHandlerData.setSessionManagerId(getSessionManagerName(sessionManagerID,false));		
	}

	private void setCdrHandlerData(CdrGenerationHandlerData cdrGenerationHandlerData) throws DataManagerException {
		List<CdrHandlerEntryData>  cdrHandlerGroupList =cdrGenerationHandlerData.getCdrHandlers();
		if(Collectionz.isNullOrEmpty(cdrHandlerGroupList) == false) {
			for(CdrHandlerEntryData cdrHandlerEntryData : cdrHandlerGroupList){
				List<PrimaryDriverDetail> primaryDriverGroupList = cdrHandlerEntryData.getDriverDetails().getPrimaryDriverGroup();
				if(Collectionz.isNullOrEmpty(primaryDriverGroupList) == false) {
					for(PrimaryDriverDetail primaryDriverDetail :primaryDriverGroupList) {
						String primaryDriverId = getDriverId(primaryDriverDetail.getDriverInstanceId());
						primaryDriverDetail.setDriverInstanceId(primaryDriverId);
						primaryDriverDetail.setWeightage(1);
					}
				}
				
				List<SecondaryAndCacheDriverDetail> secondaryDriverGroupList = cdrHandlerEntryData.getDriverDetails().getSecondaryDriverGroup();
				if(Collectionz.isNullOrEmpty(secondaryDriverGroupList) == false) {
					for(SecondaryAndCacheDriverDetail secondaryAndCacheDriverDetail : secondaryDriverGroupList) {
						String secondaryDriverId = getDriverId(secondaryAndCacheDriverDetail.getSecondaryDriverId());
						secondaryAndCacheDriverDetail.setSecondaryDriverId(secondaryDriverId);
					}
				}
			}
		}
	}

	private void setProfileDriverDetail(RadiusSubscriberProfileRepositoryDetails handlerData) 
			throws DataManagerException {
		
		List<PrimaryDriverDetail> primaryDriverGroupList = handlerData.getDriverDetails().getPrimaryDriverGroup();
		List<SecondaryAndCacheDriverDetail> secondaryDriverGroupList = handlerData.getDriverDetails().getSecondaryDriverGroup();
		List<AdditionalDriverDetail> additionalDriverGroupList = handlerData.getDriverDetails().getAdditionalDriverList();
		if(Collectionz.isNullOrEmpty(primaryDriverGroupList) == false){ 
			for(PrimaryDriverDetail primaryDriverDetail :primaryDriverGroupList) {
				String primaryDriverId = getDriverId(primaryDriverDetail.getDriverInstanceId());
				primaryDriverDetail.setDriverInstanceId(primaryDriverId);
			}
		}
		
		if(Collectionz.isNullOrEmpty(secondaryDriverGroupList) == false) {
			for(SecondaryAndCacheDriverDetail secondaryAndCacheDriverDetail : secondaryDriverGroupList) {
				String secondaryDriverId = getDriverId(secondaryAndCacheDriverDetail.getSecondaryDriverId());
				secondaryAndCacheDriverDetail.setSecondaryDriverId(secondaryDriverId);
				// code for handle invalid value of cache driver value. 
				String cacheDriverName = secondaryAndCacheDriverDetail.getCacheDriverId();
				
				if( cacheDriverName.equalsIgnoreCase("--None--") || cacheDriverName.equalsIgnoreCase("0")){
					secondaryAndCacheDriverDetail.setCacheDriverId("0");
				}else{
					String cacheDriverId = getDriverId(secondaryAndCacheDriverDetail.getCacheDriverId());
					secondaryAndCacheDriverDetail.setCacheDriverId(cacheDriverId);
				}
			}
		}

		if(Collectionz.isNullOrEmpty(additionalDriverGroupList) == false) {
			for(AdditionalDriverDetail additionalDriverDetail : additionalDriverGroupList) {
				String additionalDriverId = getDriverId(additionalDriverDetail.getDriverId());
				additionalDriverDetail.setDriverId(additionalDriverId);
			}
		}

		setProfileIdentityDetails(handlerData);
		
	}

	private void setProfileIdentityDetails( RadiusSubscriberProfileRepositoryDetails handlerData) {
		UpdateIdentityParamsDetail updateIdentityParamsDetail = handlerData.getUpdateIdentity();
		
		if( updateIdentityParamsDetail != null ){
			String stripIdentity = updateIdentityParamsDetail.getStripIdentity();
			if (Strings.isNullOrBlank(stripIdentity) == false) {
				if(stripIdentity.equals("None")){
					handlerData.getUpdateIdentity().setStripIdentity("0");
				}else if(stripIdentity.equals("Prefix")){
					handlerData.getUpdateIdentity().setStripIdentity("prefix");
				}else if(stripIdentity.equals("Suffix")){
					handlerData.getUpdateIdentity().setStripIdentity("suffix");
				}
			}
			
			String idetityFormat = updateIdentityParamsDetail.getStripIdentity();
			if( Strings.isNullOrBlank(idetityFormat) == false){
				if(stripIdentity.equals("No Change")){
					handlerData.getUpdateIdentity().setStripIdentity("1");
				}else if(stripIdentity.equals("Lower Case")){
					handlerData.getUpdateIdentity().setStripIdentity("2");
				}else if(stripIdentity.equals("Upper Case")){
					handlerData.getUpdateIdentity().setStripIdentity("3");
				}
			}
		}		
	}

	private void convertAcctFlowData(AccountingPolicyData accountingPolicyData) throws DataManagerException {
		for (AcctServicePolicyHandlerData handlerData : accountingPolicyData.getHandlersData()) {
			if (handlerData instanceof RadiusSubscriberProfileRepositoryDetails) { 
				setProfileDriverDetail((RadiusSubscriberProfileRepositoryDetails)handlerData);
			} else if (handlerData instanceof CdrGenerationHandlerData) {
				setCdrHandlerData((CdrGenerationHandlerData)handlerData);
			} else if (handlerData instanceof SynchronousCommunicationHandlerData) {
				setProxyCommunicationHandlerData(((SynchronousCommunicationHandlerData) handlerData));
			} else if (handlerData instanceof BroadcastCommunicationHandlerData) {
				setBroadCaseHandlerData(((BroadcastCommunicationHandlerData) handlerData));
			} else if (handlerData instanceof CoADMGenerationHandlerData) {
				setCoADMGenerateionHandlerData(((CoADMGenerationHandlerData) handlerData));
			}
		}
		
		if (accountingPolicyData.getPostResponseHandlerData() != null) {
			if( Collectionz.isNullOrEmpty(accountingPolicyData.getPostResponseHandlerData().getHandlersData()) == false ){
				for (AcctServicePolicyHandlerData handlerData : accountingPolicyData.getPostResponseHandlerData().getHandlersData()){
					if (handlerData instanceof CdrGenerationHandlerData) {
						setCdrHandlerData(((CdrGenerationHandlerData)handlerData));
					} else if (handlerData instanceof CoADMGenerationHandlerData) {
						setCoADMGenerateionHandlerData(((CoADMGenerationHandlerData) handlerData));
					}
				}
			}
			
			if (accountingPolicyData.getPostResponseHandlerData().getEnabled() == null) {
				accountingPolicyData.getPostResponseHandlerData().setEnabled("true");
			}
		}
	}
	private String getRadiusESIIdByName(String esiName) throws DataManagerException {
		return getRadiusESIGroupData(esiName,BY_NAME);
	}
	
	private String getRadiusESIGroupNameById (String esiId) throws DataManagerException {
		return getRadiusESIGroupData(esiId,BY_ID);
	}
	
	private String getRadiusESIGroupData (String esiIdOrName , boolean idOrName ) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		ExternalSystemInterfaceDataManager esiInstanceDataManager = (ExternalSystemInterfaceDataManager) DataManagerFactory.getInstance().getDataManager(ExternalSystemInterfaceDataManager.class, session);
		try {
			if(idOrName) {
				esiInstanceDataManager = (ExternalSystemInterfaceDataManager) DataManagerFactory.getInstance().getDataManager(ExternalSystemInterfaceDataManager.class, session);
				return esiInstanceDataManager.getRadiusESIGroupNameById(esiIdOrName);
			} else {
				esiInstanceDataManager = (ExternalSystemInterfaceDataManager) DataManagerFactory.getInstance().getDataManager(ExternalSystemInterfaceDataManager.class, session);
				return String.valueOf(esiInstanceDataManager.getRadiusESIIdByName(esiIdOrName));
			}
		} catch (DataManagerException dme) {
			throw dme;
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}finally{
			closeSession(session);
		}
	}
	
	private String getDigestConfId(String digestConfid) throws DataManagerException {
		return getDigestConfData(digestConfid, BY_ID);
	}
	
	private String getDigestConfName(String digestConfName) throws DataManagerException {
		return getDigestConfData(digestConfName, BY_NAME);
	} 
	
	private	String getDigestConfData(String digestConfidOrName, boolean idOrName) throws DataManagerException {
		if( Strings.isNullOrBlank(digestConfidOrName) || "0".equalsIgnoreCase(digestConfidOrName) ){
			return "";
		}
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DigestConfDataManager digestConfDataManager = getDigestConfDataManager(session);
		try {
			DigestConfigInstanceData digestConfigInstanceData = null;
			if(idOrName) {
				digestConfigInstanceData = digestConfDataManager.getDigestConfigInstDataByName(digestConfidOrName);
				return  String.valueOf(digestConfigInstanceData.getDigestConfId());
			} else {
				 digestConfigInstanceData = digestConfDataManager.getDigestConfigInstDataById(digestConfidOrName);
				 return  digestConfigInstanceData.getName();
			}
		} catch (DataManagerException dme) {
			throw dme;
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}finally{
			closeSession(session);
		}
	}
	
	public String getEAPConfName(String eapConfName) throws DataManagerException {
		return getEAPConfData(eapConfName, BY_NAME);
	}
	 
	public String getEAPConfigId(String eapConfId) throws DataManagerException {
		return getEAPConfData(eapConfId, BY_ID);
	}
	
	private	String getEAPConfData(String eapConfidOrName, boolean idOrName) throws DataManagerException {
		if(Strings.isNullOrBlank(eapConfidOrName) || "0".equalsIgnoreCase(eapConfidOrName)){
			return "0";
		}
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		EAPConfigDataManager eapConfigDataManager  = getEAPConfigDataManager(session);
		
		try {
			EAPConfigData eapConfigData  = null;
			if(idOrName) {
				eapConfigData = eapConfigDataManager.getEAPConfigDataByName(eapConfidOrName);
				return String.valueOf(eapConfigData.getEapId());
			} else {
				eapConfigData =  eapConfigDataManager.getEAPConfigDataById(eapConfidOrName);
				return eapConfigData.getName();
			}
		} catch (DataManagerException dme) {
			throw dme;
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}finally{
			closeSession(session);
		}
	}
	
	public String getDriverId(String driverName) throws DataManagerException {
		return getDriverData(driverName,BY_ID);
	}
	
	public String getDriverName(String driverId) throws DataManagerException {
		return getDriverData(driverId,BY_NAME);
	}
	
	private String getDriverData(String driverIdOrName, boolean idOrName) throws DataManagerException {
		if(Strings.isNullOrBlank(driverIdOrName) || "0".equalsIgnoreCase(driverIdOrName)) {
			return "0";
		}

		try {
			DriverBLManager driverBLManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = null;
			if(idOrName) {
				driverInstanceData = driverBLManager.getDriverInstanceByName(driverIdOrName);
				return String.valueOf(driverInstanceData.getDriverInstanceId());
			} else {
				driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(driverIdOrName);
				return driverInstanceData.getName();
			}
		} catch (DataManagerException dme) {
			throw dme;
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	 
	public String getSessionManagerName(String sessionManagerId, boolean byIdOrName) throws DataManagerException {
		SessionManagerBLManager smBlManager = new SessionManagerBLManager();
		ISessionManagerInstanceData sessionManagerData;
		if (byIdOrName) {
			sessionManagerData = smBlManager.getSessionManagerDataById(sessionManagerId);
			return sessionManagerData.getName();
		} else {
			sessionManagerData = smBlManager.getSessionManagerDataByName(sessionManagerId);
			return String.valueOf(sessionManagerData.getSmInstanceId());
		}
	}

	private byte[] convertToXml(RadiusServicePolicyData policyData) throws JAXBException {
		String serialize = ConfigUtil.serialize(RadiusServicePolicyData.class, policyData);
		return serialize.getBytes();
	}
	public PageList searchRadiusServicePolicy(RadServicePolicyData radServicePolicyData, IStaffData staffData, int requiredPageNo,Integer pageSize) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusServicePolicyDataManager radiusServicePolicyDataManager = getRadiusServicePolicyDataManager(session);
		if(radiusServicePolicyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{				     
			session.beginTransaction();
			PageList pageList = radiusServicePolicyDataManager.search(radServicePolicyData, requiredPageNo, pageSize);
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_RADIUS_POLICY_ACTION);
			commit(session);
			return pageList;
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

	public void deleteRadiusServicePolicyById(List<String> radiusServicePolicyIds, IStaffData staffData) throws DataManagerException {
		deleteRadiusServicePolicy(radiusServicePolicyIds, staffData, BY_ID);
	}
	
	public void deleteRadiusServicePolicyByName(List<String> name, StaffData staffData) throws DataManagerException {
		deleteRadiusServicePolicy(name, staffData, BY_NAME);
	}
	
	private void deleteRadiusServicePolicy(List<String> name, IStaffData staffData, boolean deleteByNameOrId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusServicePolicyDataManager policyDataManager = getRadiusServicePolicyDataManager(session);
		
		if(policyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{
			session.beginTransaction();
			
			for (String policyToDelete : name) {
				String policyName;
				if (deleteByNameOrId) {
					policyName = policyDataManager.deleteById(policyToDelete);
				} else {
					policyName = policyDataManager.deleteByName(policyToDelete.trim());
				}
				staffData.setAuditName(policyName);
				AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_RADIUS_SERVICE_POLICY);
			}
			commit(session);	
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(), e);
		}finally {
			closeSession(session);
		}
	}
	
	public RadServicePolicyData getRadiusServicePolicyInstData(String policyId, IStaffData staffData,
			String actionAlias) throws DataManagerException {
		return getRadiusServicePolicy(policyId, BY_ID);
	}
	
	public RadServicePolicyData getRadiusServicePolicyByName(String policyName) throws DataManagerException {
		return getRadiusServicePolicy(policyName.trim(), BY_NAME);
	}
	
	private RadServicePolicyData getRadiusServicePolicy(Object policyToGet, boolean byNameOrId) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		
		RadiusServicePolicyDataManager radiusServicePolicyDataManager = getRadiusServicePolicyDataManager(session);
		if (radiusServicePolicyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for "	+ getClass().getName());
		}

		try {
			RadServicePolicyData policyData;
			if (byNameOrId) {
				policyData = radiusServicePolicyDataManager.getRadiusServPolicyInstDataById((String)policyToGet);
			} else {
				policyData = radiusServicePolicyDataManager.getRadiusServicePolicyDataByName((String)policyToGet);
			}
			convertPolicyData(policyData);
			return policyData;
		} catch (DataManagerException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	private void convertPolicyData(RadServicePolicyData policyData) throws JAXBException, DataManagerException {
		byte[] policyXml = policyData.getRadiusPolicyXml();
		RadiusServicePolicyData data = (RadiusServicePolicyData)ConfigUtil.deserialize(new String(policyXml), RadiusServicePolicyData.class);
		String sessionManagerId = policyData.getSessionManagerId();
		if (Strings.isNullOrBlank(sessionManagerId) == false) {
			
			String sessionMgrName = getSessionManagerName(policyData.getSessionManagerId(), BY_ID);
			
			data.setSessionManagerId(getSessionManagerName(policyData.getSessionManagerId(), BY_ID));
			policyData.setSessionManagerId(sessionMgrName);
			
		}else{
			data.setSessionManagerId("--None--");
		}
		
		if(BaseConstant.SHOW_STATUS_ID.equalsIgnoreCase(policyData.getStatus())){
			data.setStatus(BaseConstant.ACTIVE_STATUS);
			policyData.setStatus(data.getStatus());
		}else if(BaseConstant.HIDE_STATUS_ID.equalsIgnoreCase(policyData.getStatus())){
			data.setStatus(BaseConstant.INACTIVE_STATUS);
			policyData.setStatus(data.getStatus());
		}
		
		AuthenticationPolicyData authPolicyData = data.getAuthenticationPolicyData();
		if (authPolicyData != null) {
			convertAuthPolicyData(authPolicyData);
		}
		
		AccountingPolicyData acctPolicyData = data.getAccountingPolicyData();
		if (acctPolicyData != null) {
			convertAcctPolicyData(acctPolicyData);
		}
		
		String changedPolicyData = ConfigUtil.serialize(RadiusServicePolicyData.class, data);
		policyData.setRadiusPolicyXml(changedPolicyData.getBytes());
	}

	private void convertAcctPolicyData(AccountingPolicyData acctPolicyData) throws DataManagerException {
		for (AcctServicePolicyHandlerData handlerData : acctPolicyData.getHandlersData()) {
			 if (handlerData instanceof RadiusSubscriberProfileRepositoryDetails) { 
				getProfileDriverDetail((RadiusSubscriberProfileRepositoryDetails)handlerData);
			} else if (handlerData instanceof CdrGenerationHandlerData) {
				getCdrHandlerData((CdrGenerationHandlerData)handlerData);
			} else if (handlerData instanceof SynchronousCommunicationHandlerData) {
				getProxyHandlerDetail((SynchronousCommunicationHandlerData) handlerData);
			} else if (handlerData instanceof BroadcastCommunicationHandlerData) {
				getBroadCastHandlerData((BroadcastCommunicationHandlerData) handlerData);
			}  else if (handlerData instanceof CoADMGenerationHandlerData) {
				getCoADMGenerationHandlerData((CoADMGenerationHandlerData) handlerData);
			}
		}
		
		if( acctPolicyData.getPostResponseHandlerData() != null ){
			if( Collectionz.isNullOrEmpty(acctPolicyData.getPostResponseHandlerData().getHandlersData()) == false ){
				for (AcctServicePolicyHandlerData handlerData : acctPolicyData.getPostResponseHandlerData().getHandlersData()) {
					 if (handlerData instanceof CdrGenerationHandlerData) {
						getCdrHandlerData((CdrGenerationHandlerData)handlerData);
					 } else if (handlerData instanceof CoADMGenerationHandlerData) {
					 	getCoADMGenerationHandlerData((CoADMGenerationHandlerData) handlerData);
					 }
				}
			}
		}
	}

	private void convertAuthPolicyData(AuthenticationPolicyData authPolicyData) throws DataManagerException {
		for (AuthServicePolicyHandlerData handlerData : authPolicyData.getHandlersData()) {
			if (handlerData instanceof AuthenticationHandlerData) {
				AuthenticationHandlerData authenticationHandlerData = (AuthenticationHandlerData)handlerData;
				List<String> supportedMethods = authenticationHandlerData.getSupportedMethods();
				List<String> supportedMethodsId = new ArrayList<String>();

				for (String authMethod : supportedMethods) {
					if ("1".equalsIgnoreCase(authMethod)) {
						supportedMethodsId.add("PAP");
					} else if ("2".equalsIgnoreCase(authMethod)) {
						supportedMethodsId.add("CHAP");
					} else if ("3".equalsIgnoreCase(authMethod)) {
						supportedMethodsId.add("EAP");
					} else if ("4".equalsIgnoreCase(authMethod)) {
						supportedMethodsId.add("DIGEST");
					} else if ("5".equalsIgnoreCase(authMethod)) {
						supportedMethodsId.add("PROXY");
					}
				}

				authenticationHandlerData.setSupportedMethods(supportedMethodsId);
				/**
				 * Here digestConfigId contains the digest configuration name, as single property is used
				 * for id and name as well.
				 * 
				 * So while providing response for rest call and view for SM it must display the name of 
				 * digest configuration rather than id, so conversion is required. 
				 */
				getDigetstConfiguration(authenticationHandlerData);
				
				getEapConfiguration(authenticationHandlerData);

				
			} else if (handlerData instanceof RadiusSubscriberProfileRepositoryDetails) { 
				getProfileDriverDetail((RadiusSubscriberProfileRepositoryDetails)handlerData);
			} else if (handlerData instanceof CdrGenerationHandlerData) {
				getCdrHandlerData((CdrGenerationHandlerData)handlerData);
			} else if (handlerData instanceof ConcurrencyHandlerData) {
				getSessionManagerDetail((ConcurrencyHandlerData)handlerData);
			} else if (handlerData instanceof SynchronousCommunicationHandlerData) {
				getProxyHandlerDetail((SynchronousCommunicationHandlerData) handlerData);
			} else if (handlerData instanceof BroadcastCommunicationHandlerData) {
				getBroadCastHandlerData((BroadcastCommunicationHandlerData) handlerData);
			} else if (handlerData instanceof CoADMGenerationHandlerData) {
				getCoADMGenerationHandlerData((CoADMGenerationHandlerData) handlerData);
			}
		}
		
		if( authPolicyData.getPostResponseHandlerData() != null ) {
			if( Collectionz.isNullOrEmpty(authPolicyData.getPostResponseHandlerData().getHandlersData()) == false ){
				for (AuthServicePolicyHandlerData handlerData : authPolicyData.getPostResponseHandlerData().getHandlersData()) {
					 if (handlerData instanceof CdrGenerationHandlerData) {
						getCdrHandlerData((CdrGenerationHandlerData)handlerData);
					 } else if (handlerData instanceof CoADMGenerationHandlerData) {
					 	getCoADMGenerationHandlerData((CoADMGenerationHandlerData) handlerData);
					 }
				}
			}
		}
	}

	private void getCoADMGenerationHandlerData(CoADMGenerationHandlerData handlerData) {
		List<CoADMHandlerEntryData> datas = handlerData.getEntries();
		for (CoADMHandlerEntryData data : datas) {
			if (data.getPacketType().equals(String.valueOf(RadiusConstants.DISCONNECTION_REQUEST_MESSAGE))) {
				data.setPacketType("Disconnect Request");
			} else if (data.getPacketType().equals(String.valueOf(RadiusConstants.COA_REQUEST_MESSAGE))) {
				data.setPacketType("COA Request");
			}
		}		
	}

	private void getBroadCastHandlerData(BroadcastCommunicationHandlerData broadCasthandlerData) throws DataManagerException {
		List<AsyncCommunicationEntryData> communicationEntryDataList = ((BroadcastCommunicationHandlerData) broadCasthandlerData).getProxyCommunicationEntries();
		if(Collectionz.isNullOrEmpty(communicationEntryDataList) == false) { 
			for(ExternalCommunicationEntryData communicationEntryData :communicationEntryDataList) {
				List<CommunicatorData> communicatorDataList = communicationEntryData.getCommunicatorGroupData().getCommunicatorDataList();
				if(Collectionz.isNullOrEmpty(communicatorDataList) == false) {
					for(CommunicatorData communicatorData : communicatorDataList) {
						communicatorData.setId(getRadiusESIGroupNameById(communicatorData.getId()));
					}
				}
			}		
		}
	}

	private void getProxyHandlerDetail(SynchronousCommunicationHandlerData proxyHandlerData) throws DataManagerException {
		List<ExternalCommunicationEntryData> communicationEntryDataList = ((SynchronousCommunicationHandlerData) proxyHandlerData).getProxyCommunicatioEntries();
		if(Collectionz.isNullOrEmpty(communicationEntryDataList) == false) {
			for(ExternalCommunicationEntryData communicationEntryData : communicationEntryDataList) {
				List<CommunicatorData> communicatorDataList = communicationEntryData.getCommunicatorGroupData().getCommunicatorDataList();
				if(Collectionz.isNullOrEmpty(communicatorDataList) == false) {
					for(CommunicatorData communicatorData : communicatorDataList) {
						communicatorData.setId(getRadiusESIGroupNameById(communicatorData.getId()));
					}
				}
			}		
			
		}
	}

	private void getSessionManagerDetail(ConcurrencyHandlerData concurrencyHandlerData) throws DataManagerException {
		String sessionManagerName = ((ConcurrencyHandlerData)concurrencyHandlerData).getSessionManagerId();
		((ConcurrencyHandlerData)concurrencyHandlerData).setSessionManagerId(getSessionManagerName(sessionManagerName,true));		
	}

	private void getCdrHandlerData(CdrGenerationHandlerData cdrHandlerData) throws DataManagerException {
		List<CdrHandlerEntryData>  cdrHandlerGroupList = ((CdrGenerationHandlerData)cdrHandlerData).getCdrHandlers();
		if(Collectionz.isNullOrEmpty(cdrHandlerGroupList) == false) {
			for(CdrHandlerEntryData cdrHandlerEntryData : cdrHandlerGroupList){
				List<PrimaryDriverDetail> primaryDriverGroupList = cdrHandlerEntryData.getDriverDetails().getPrimaryDriverGroup();
				if(Collectionz.isNullOrEmpty(primaryDriverGroupList) == false) {
					for(PrimaryDriverDetail primaryDriverDetail :primaryDriverGroupList) {
						String primaryDriverName = getDriverName(primaryDriverDetail.getDriverInstanceId());
						primaryDriverDetail.setDriverInstanceId(primaryDriverName);
						primaryDriverDetail.setWeightage(null);
					}
				}

				List<SecondaryAndCacheDriverDetail> secondaryDriverGroupList = cdrHandlerEntryData.getDriverDetails().getSecondaryDriverGroup();
				if(Collectionz.isNullOrEmpty(secondaryDriverGroupList) == false) {
					for(SecondaryAndCacheDriverDetail secondaryAndCacheDriverDetail : secondaryDriverGroupList) {
						String secondaryDriverName = getDriverName(secondaryAndCacheDriverDetail.getSecondaryDriverId());
						
						if(secondaryDriverName.equals("0")){
							secondaryAndCacheDriverDetail.setSecondaryDriverId("");
							secondaryAndCacheDriverDetail.setCacheDriverId(null);
						}else{
							secondaryAndCacheDriverDetail.setSecondaryDriverId(secondaryDriverName);
							secondaryAndCacheDriverDetail.setCacheDriverId(null);
						}
					}
				}
			}		
			
		}
	}

	private void getProfileDriverDetail(RadiusSubscriberProfileRepositoryDetails profileLookupDriverHandler) throws DataManagerException {
		List<PrimaryDriverDetail> primaryDriverGroupList = profileLookupDriverHandler.getDriverDetails().getPrimaryDriverGroup();
		List<SecondaryAndCacheDriverDetail> secondaryDriverGroupList = ((RadiusSubscriberProfileRepositoryDetails)profileLookupDriverHandler).getDriverDetails().getSecondaryDriverGroup();
		List<AdditionalDriverDetail> additionalDriverGroupList = ((RadiusSubscriberProfileRepositoryDetails)profileLookupDriverHandler).getDriverDetails().getAdditionalDriverList();
		
		if(Collectionz.isNullOrEmpty(primaryDriverGroupList) == false) {
			for(PrimaryDriverDetail primaryDriverDetail :primaryDriverGroupList) {
				String primaryDriverName = getDriverName(primaryDriverDetail.getDriverInstanceId());
				primaryDriverDetail.setDriverInstanceId(primaryDriverName);
			}
		}
		
		if(Collectionz.isNullOrEmpty(secondaryDriverGroupList) == false) {
			for(SecondaryAndCacheDriverDetail secondaryAndCacheDriverDetail : secondaryDriverGroupList) {
				String secondaryDriverName = getDriverName(secondaryAndCacheDriverDetail.getSecondaryDriverId());
				secondaryAndCacheDriverDetail.setSecondaryDriverId(secondaryDriverName);
				String cacheDriverName = secondaryAndCacheDriverDetail.getCacheDriverId();
				
				if (cacheDriverName.equalsIgnoreCase("0")) {
					secondaryAndCacheDriverDetail.setCacheDriverId("--None--");
				}else{
					String cacheDriverId = getDriverName(secondaryAndCacheDriverDetail.getCacheDriverId());
					secondaryAndCacheDriverDetail.setCacheDriverId(cacheDriverId);
				}
			}
		}
		
		if(Collectionz.isNullOrEmpty(additionalDriverGroupList) == false) {
			for(AdditionalDriverDetail additionalDriverDetail : additionalDriverGroupList) {
				String additionalDriverName = getDriverName(additionalDriverDetail.getDriverId());
				additionalDriverDetail.setDriverId(additionalDriverName);
			}		
		}
		
		getProfileIdentityDetails(profileLookupDriverHandler);
	}

	private void getProfileIdentityDetails( RadiusSubscriberProfileRepositoryDetails profileLookupDriverHandler) {
		UpdateIdentityParamsDetail updateIdentityParamsDetail = profileLookupDriverHandler.getUpdateIdentity();
		
		if( updateIdentityParamsDetail != null ){
			String stripIdentity = updateIdentityParamsDetail.getStripIdentity();
			if (Strings.isNullOrBlank(stripIdentity) == false) {
				if(stripIdentity.equals("0")){
					profileLookupDriverHandler.getUpdateIdentity().setStripIdentity("None");
				}else if(stripIdentity.equals("prefix")){
					profileLookupDriverHandler.getUpdateIdentity().setStripIdentity("Prefix");
				}else if(stripIdentity.equals("suffix")){
					profileLookupDriverHandler.getUpdateIdentity().setStripIdentity("Suffix");
				}
			}
			
			String idetityFormat = updateIdentityParamsDetail.getStripIdentity();
			if( Strings.isNullOrBlank(idetityFormat) == false){
				if(stripIdentity.equals("1")){
					profileLookupDriverHandler.getUpdateIdentity().setStripIdentity("No Change");
				}else if(stripIdentity.equals("2")){
					profileLookupDriverHandler.getUpdateIdentity().setStripIdentity("Lower Case");
				}else if(stripIdentity.equals("3")){
					profileLookupDriverHandler.getUpdateIdentity().setStripIdentity("Upper Case");
				}
			}
		}		
	}

	private void getEapConfiguration(AuthenticationHandlerData authenticationHandlerData) throws DataManagerException {
		String eapConfName = getEAPConfName(authenticationHandlerData.getEapConfigId());
		
		if( eapConfName.equalsIgnoreCase("0") == false){
			authenticationHandlerData.setEapConfigId(eapConfName);		
		} else {
			authenticationHandlerData.setEapConfigId("");		
		}
	}

	private void getDigetstConfiguration(AuthenticationHandlerData authenticationHandlerData) throws DataManagerException {
		String digestConfName = getDigestConfName(authenticationHandlerData.getDigestConfigId());
		
		if( digestConfName.equalsIgnoreCase("0") == false){
			authenticationHandlerData.setDigestConfigId(digestConfName);	
		} else {
			authenticationHandlerData.setEapConfigId("");		
		}
	}

	public void updateRadiusServicePolicyById(RadiusServicePolicyData policyData, IStaffData staffData,String actionAlias) throws DataManagerException {
		updateRadiusServicePolicy(policyData, null, staffData, actionAlias);
	}
	
	public void updateRadiusServicePolicyByName(RadiusServicePolicyData policyData, String policyToUpdate, IStaffData staffData) throws DataManagerException {
		updateRadiusServicePolicy(policyData, policyToUpdate, staffData, ConfigConstant.UPDATE_RADIUS_SERVICE_POLICY_BASIC_DETAILS);
	}
	
	private void updateRadiusServicePolicy(RadiusServicePolicyData policyData, String policyToUpdate, IStaffData staffData, String actionAlias) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();

		RadiusServicePolicyDataManager radiusServicePoilcyDataManager = getRadiusServicePolicyDataManager(session);
		if(radiusServicePoilcyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();
			
			if (policyToUpdate == null) {
				radiusServicePoilcyDataManager.updateRadiusServicePolicyById(convertToRadiusServicePolicyDataBeanFrom(policyData), 
						staffData, actionAlias);
			} else {				
				radiusServicePoilcyDataManager.updateRadiusServicePolicyName(convertToRadiusServicePolicyDataBeanFrom(policyData),
						policyToUpdate.trim(),	staffData, actionAlias);
			}
			
			commit(session);
		}catch(DataManagerException e){
			rollbackSession(session);
        	throw e;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
        	throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}
	
	public List<RadServicePolicyData> searchRadiusServicePolicy(IStaffData staffData) throws DataManagerException{
		
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();

		RadiusServicePolicyDataManager radiusServicePoilcyDataManager = getRadiusServicePolicyDataManager(session);
		if(radiusServicePoilcyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();
			List<RadServicePolicyData> policies = radiusServicePoilcyDataManager.searchActiveRadiusServicePolicy();
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_RADIUS_POLICY_ACTION);
			commit(session);
			return policies;
		}catch(DataManagerException e){
			rollbackSession(session);
        	throw e;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
        	throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}
	
	/**
	 * This is used for get all Radius service policy included with Active and Inactive status .
	 * @return List of Radius service policy
	 * @throws DataManagerException
	 */
	public List<RadServicePolicyData> getRadiusServicePolicyList() throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();

		RadiusServicePolicyDataManager radiusServicePoilcyDataManager = getRadiusServicePolicyDataManager(session);
		if(radiusServicePoilcyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{				     
		    return radiusServicePoilcyDataManager.getRadiusServicePolicyList();
		}catch(DataManagerException e){
        	throw e;
		}catch(Exception e){
			e.printStackTrace();
        	throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}

	public void updateRadiusServicePolicyStatus(List<String> radiusPolicyIds,String status) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();

		RadiusServicePolicyDataManager radiusServicePoilcyDataManager = getRadiusServicePolicyDataManager(session);
		if(radiusServicePoilcyDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		try{				     
			session.beginTransaction();
			radiusServicePoilcyDataManager.updateStatus(radiusPolicyIds, status);
			commit(session);
		}catch(DataManagerException e){
			rollbackSession(session);
        	throw e;
		}catch(Exception e){
			e.printStackTrace();
	       	rollbackSession(session);
        	throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
	}
	
	/**
	 * This method is used to get binded plugin-names with Radius service policies .
	 * @param tagName
	 * @return Set<String> of values
	 * @throws DataManagerException
	 */
	public Set<String> getBindedPluginNames(String tagName) throws DataManagerException {
		Set<String> bindendPluginNames = new HashSet<String>();
		try{
			List<RadServicePolicyData> radServicePolicyDatasList = getRadiusServicePolicyList();
			if(Collectionz.isNullOrEmpty(radServicePolicyDatasList) == false){
				for(RadServicePolicyData radServicePolicyData : radServicePolicyDatasList){
					String xmlDatas = new String(radServicePolicyData.getRadiusPolicyXml());
					bindendPluginNames.addAll(XmlNodeUtility.getElementValueByTagName(xmlDatas,tagName));
				}
			}
			return bindendPluginNames;
		}catch (DataManagerException e){
			throw e;
		}
	}
	
	/**
	 * This method is used to get values of the list of tag-names in Radius Service Policy
	 * @param tagNames List<String> that contains tag names
	 * @return Set<String> of values
	 * @throws DataManagerException
	 */
	public Set<String> getBindedPluginNames(List<String> tagNames) throws DataManagerException {
		Set<String> bindendPluginNames = new HashSet<String>();
		try{
			List<RadServicePolicyData> radServicePolicyDatasList = getRadiusServicePolicyList();
			if(Collectionz.isNullOrEmpty(radServicePolicyDatasList) == false){
				for(RadServicePolicyData radServicePolicyData : radServicePolicyDatasList){
					String xmlDatas = new String(radServicePolicyData.getRadiusPolicyXml());
					bindendPluginNames.addAll(XmlNodeUtility.getElementValueByTagName(xmlDatas,tagNames));
				}
			}
			return bindendPluginNames;
		}catch (DataManagerException e){
			throw e;
		}
	}

	public DynAuthPolicyInstData getDynAuthPolicyByName(String name) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();

		DynAuthServicePoilcyDataManager dynAuthServicePoilcyDataManager = getDynAuthServicePolicyDataManager(session);
		if (dynAuthServicePoilcyDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			return dynAuthServicePoilcyDataManager.getDynauthPolicyByName(name);
		} catch (DataManagerException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			session.close();
		}	
	}
}