package com.elitecore.elitesm.hibernate.servicepolicy.auth;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.AuthServicePoilcyDataManager;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyAdditionalDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyAuthMethodRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyBroadcastESIRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyDigestConfRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyExternalSystemRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyMainDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyRMParamsData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicySMRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicySecDriverRelData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ExternalSystemConstants;
import com.elitecore.elitesm.util.logger.Logger;


public class HAuthServicePoilcyDataManager extends HBaseDataManager implements AuthServicePoilcyDataManager{
	private static final String MODULE="HAuthServicePoilcyDataManager";

	public void create(AuthPolicyInstData authPolicyInstData) throws DataManagerException, DuplicateInstanceNameFoundException{
		EliteAssert.notNull(authPolicyInstData,"authPolicyInstData must not be null.");
		try{
			verifyAuthPolicyName(authPolicyInstData);
			Session session = getSession();
			Criteria criteria = session.createCriteria(AuthPolicyInstData.class).addOrder(Order.desc("orderNumber"));
			criteria.setFetchSize(1);
			List<AuthPolicyInstData> list = criteria.list();
			if(list!=null && !list.isEmpty()){
				AuthPolicyInstData data = list.get(0);
				authPolicyInstData.setOrderNumber(data.getOrderNumber()+1);
			}
			
			String auditId= UUIDGenerator.generate();
			
			authPolicyInstData.setAuditUid(auditId);
			
			session.save(authPolicyInstData);

			/* save main drivers relation*/
			if(authPolicyInstData.getMainDriverList()!=null){
				for(Iterator<AuthPolicyMainDriverRelData> iterator = authPolicyInstData.getMainDriverList().iterator();iterator.hasNext();){
					AuthPolicyMainDriverRelData authPolicyMainDriverRelData = iterator.next();
					authPolicyMainDriverRelData.setAuthPolicyId(authPolicyInstData.getAuthPolicyId());
					session.save(authPolicyMainDriverRelData);
					Logger.logInfo(MODULE,"saving main driver relation:"+ authPolicyMainDriverRelData.getDriverInstanceId());
				}
			}
			
			/* save secondary drivers relation*/
			if(authPolicyInstData.getSecondaryDriverList() != null){
				for(Iterator<AuthPolicySecDriverRelData> iterator = authPolicyInstData.getSecondaryDriverList().iterator();iterator.hasNext();){
					AuthPolicySecDriverRelData authPolicySecDriverRelData = iterator.next();
					authPolicySecDriverRelData.setAuthPolicyId(authPolicyInstData.getAuthPolicyId());
					session.save(authPolicySecDriverRelData);
					Logger.logInfo(MODULE,"saving secondary driver relation:"+ authPolicySecDriverRelData.getSecondaryDriverInstId());
				}
			}
			
			/* save additional drivers relation*/
			if(authPolicyInstData.getAdditionalDriverList() != null){
				for(Iterator<AuthPolicyAdditionalDriverRelData> iterator = authPolicyInstData.getAdditionalDriverList().iterator();iterator.hasNext();){
					AuthPolicyAdditionalDriverRelData authPolicyAdditionalDriverRelData = iterator.next();
					Logger.logInfo(MODULE,"Additional driver relation:"+ authPolicyAdditionalDriverRelData.getDriverInstanceId());
					authPolicyAdditionalDriverRelData.setAuthPolicyId(authPolicyInstData.getAuthPolicyId());
					session.save(authPolicyAdditionalDriverRelData);
				}
			}

			/* save external systems */
			saveExternalSystem(authPolicyInstData,authPolicyInstData.getPrepaidServerRelList());
			saveExternalSystem(authPolicyInstData,authPolicyInstData.getIpPoolServerRelList());
			saveExternalSystem(authPolicyInstData,authPolicyInstData.getProxyServerRelList());
			saveExternalSystem(authPolicyInstData,authPolicyInstData.getChargingGatewayServerRelList());
			saveBroadcastingExternalSystem(authPolicyInstData,authPolicyInstData.getBroadcastingServerRelList());

			/* save auth method types */
			if(authPolicyInstData.getAuthMethodRelDataList()!=null){
				for(Iterator<AuthPolicyAuthMethodRelData> iterator = authPolicyInstData.getAuthMethodRelDataList().iterator();iterator.hasNext();){
					AuthPolicyAuthMethodRelData authPolicyAuthMethodRelData = iterator.next();
					authPolicyAuthMethodRelData.setAuthPolicyId(authPolicyInstData.getAuthPolicyId());
					session.save(authPolicyAuthMethodRelData);
					Logger.logInfo(MODULE,"saving auth method type relation:"+ authPolicyAuthMethodRelData.getAuthMethodTypeId());
				}
			}

			if(authPolicyInstData.getIpPoolRMParamsData()!=null){
				AuthPolicyRMParamsData authPolicyRMParamsData = authPolicyInstData.getIpPoolRMParamsData();
				authPolicyRMParamsData.setAuthPolicyId(authPolicyInstData.getAuthPolicyId());
				Logger.logDebug(MODULE, "IP Pool RM Params :" + authPolicyRMParamsData);
				session.save(authPolicyRMParamsData);
			}

			if(authPolicyInstData.getPrepaidRMParamsData()!=null){
				AuthPolicyRMParamsData authPolicyRMParamsData = authPolicyInstData.getPrepaidRMParamsData();
				authPolicyRMParamsData.setAuthPolicyId(authPolicyInstData.getAuthPolicyId());
				Logger.logDebug(MODULE, "Prepaid RM Params :" + authPolicyRMParamsData);
				session.save(authPolicyRMParamsData);
			}
			if(authPolicyInstData.getChargingGatewayRMParamsData()!=null){
				AuthPolicyRMParamsData authPolicyRMParamsData = authPolicyInstData.getChargingGatewayRMParamsData();
				authPolicyRMParamsData.setAuthPolicyId(authPolicyInstData.getAuthPolicyId());
				Logger.logDebug(MODULE, "Charging Gateway RM Params :" + authPolicyRMParamsData);
				session.save(authPolicyRMParamsData);
			}

			//			/* save digest config relation */
			//			AuthPolicyDigestConfRelData authConfRelData =  authPolicyInstData.getAuthPolicyDigestConfRelData();
			//			if(authConfRelData!=null){
			//				authConfRelData.setAuthPolicyId(authPolicyInstData.getAuthPolicyId());
			//				session.save(authConfRelData);
			//				Logger.logInfo(MODULE,"saving digest conf relation:"+ authConfRelData.getDigestConfId());
			//			}


			/* save SM config relation */
			AuthPolicySMRelData authPolicySMRelData =  authPolicyInstData.getAuthPolicySMRelData();
			if(authPolicySMRelData!=null){
				authPolicySMRelData.setAuthPolicyId(authPolicyInstData.getAuthPolicyId());
				session.save(authPolicySMRelData);
				Logger.logInfo(MODULE,"saving sessin manager relation:"+ authPolicySMRelData.getSessionManagerInstanceId());
			}

			session.flush();
		}catch(DuplicateInstanceNameFoundException exp){
			throw new DuplicateInstanceNameFoundException(exp.getMessage(), exp);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}


	}
	private void saveExternalSystem(AuthPolicyInstData authPolicyInstData,List<AuthPolicyExternalSystemRelData> externalSystemRelList){
		Session session = getSession();
		if(externalSystemRelList!=null && !externalSystemRelList.isEmpty()){
			for(Iterator<AuthPolicyExternalSystemRelData> iterator = externalSystemRelList.iterator();iterator.hasNext();){
				AuthPolicyExternalSystemRelData authPolicyExternalSystemRelData = iterator.next();
				authPolicyExternalSystemRelData.setAuthPolicyId(authPolicyInstData.getAuthPolicyId());
				session.save(authPolicyExternalSystemRelData);
				Logger.logInfo(MODULE,"saving external system relation:"+ authPolicyExternalSystemRelData.getEsiInstanceId());
			}
		}

	}
	private void saveBroadcastingExternalSystem(AuthPolicyInstData authPolicyInstData,List<AuthPolicyBroadcastESIRelData> externalSystemRelList){
		Session session = getSession();
		if(externalSystemRelList!=null && !externalSystemRelList.isEmpty()){
			for(Iterator<AuthPolicyBroadcastESIRelData> iterator = externalSystemRelList.iterator();iterator.hasNext();){
				AuthPolicyBroadcastESIRelData authPolicyBroadcastESIRelData = iterator.next();
				authPolicyBroadcastESIRelData.setAuthPolicyId(authPolicyInstData.getAuthPolicyId());
				session.save(authPolicyBroadcastESIRelData);
				Logger.logInfo(MODULE,"saving broadcasting external system relation:"+ authPolicyBroadcastESIRelData.getEsiInstanceId());
			}
		}

	}
	public void updateAuthBasicDetail(AuthPolicyInstData instData,IStaffData staffData,String actionAlias) throws DataManagerException, DuplicateInstanceNameFoundException{
		EliteAssert.notNull(instData,"authPolicyInstData must not be null.");
		try{
			verifyAuthPolicyNameForUpdate(instData);
			Session session = getSession();
			Criteria criteria = session.createCriteria(AuthPolicyInstData.class).add(Restrictions.eq("authPolicyId", instData.getAuthPolicyId()));
			AuthPolicyInstData authPolicyInstData =(AuthPolicyInstData) criteria.uniqueResult();

			setAuthPolicyInstData(authPolicyInstData, session);
			JSONObject oldObject = authPolicyInstData.toJson();
			
			authPolicyInstData.setName(instData.getName());
			authPolicyInstData.setDescription(instData.getDescription());
			authPolicyInstData.setRuleSet(instData.getRuleSet());;
			authPolicyInstData.setStatus(instData.getStatus());
			authPolicyInstData.setResponseAttributes(instData.getResponseAttributes());
			authPolicyInstData.setResponseBehavior(instData.getResponseBehavior());
			authPolicyInstData.setHotlinePolicy(instData.getHotlinePolicy());
			authPolicyInstData.setValidateAuthPacket(instData.getValidateAuthPacket());
			
			  
            if(authPolicyInstData.getAuditUid() == null){
            	String auditId= UUIDGenerator.generate();
				authPolicyInstData.setAuditUid(auditId);
				staffData.setAuditId(auditId);
			}
			
			session.update(authPolicyInstData);
			session.flush();

			session.clear();
			setAuthPolicyInstData(authPolicyInstData, session);
			JSONArray jsonArray=ObjectDiffer.diff(oldObject, authPolicyInstData.toJson());
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		}catch(DuplicateInstanceNameFoundException exp){
			throw new DuplicateInstanceNameFoundException(exp.getMessage(), exp);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}

	public void updateAuthenticateParams(AuthPolicyInstData instData,IStaffData staffData,String actionAlias) throws DataManagerException{
		EliteAssert.notNull(instData,"authPolicyInstData must not be null.");
		try{

			Session session = getSession();
			Criteria criteria = session.createCriteria(AuthPolicyInstData.class).add(Restrictions.eq("authPolicyId", instData.getAuthPolicyId()));
			AuthPolicyInstData authPolicyInstData =(AuthPolicyInstData) criteria.uniqueResult();
			
			setAuthPolicyInstData(authPolicyInstData, session);
			JSONObject oldObject = authPolicyInstData.toJson();
			
			authPolicyInstData.setAuthPolicyId(instData.getAuthPolicyId());
			authPolicyInstData.setCaseSensitiveUserIdentity(instData.getCaseSensitiveUserIdentity());
			authPolicyInstData.setMultipleUserIdentity(instData.getMultipleUserIdentity());

			authPolicyInstData.setStripUserIdentity(instData.getStripUserIdentity());
			authPolicyInstData.setTrimUserIdentity(instData.getTrimUserIdentity());
			authPolicyInstData.setTrimPassword(instData.getTrimPassword());
			authPolicyInstData.setRealmPattern(instData.getRealmPattern());
			authPolicyInstData.setRealmSeparator(instData.getRealmSeparator());
			authPolicyInstData.setEapConfigId(instData.getEapConfigId());
			authPolicyInstData.setGracePolicyId(instData.getGracePolicyId());
			authPolicyInstData.setAuthMethodRelDataList(instData.getAuthMethodRelDataList());
			//authPolicyInstData.setAuthPolicyDigestConfRelData(instData.getAuthPolicyDigestConfRelData());
			authPolicyInstData.setGracePolicyId(instData.getGracePolicyId());
			authPolicyInstData.setDigestConfigId(instData.getDigestConfigId());
			authPolicyInstData.setCui(instData.getCui());
			authPolicyInstData.setCuiResponseAttributes(instData.getCuiResponseAttributes());
			authPolicyInstData.setUserName(instData.getUserName());
			authPolicyInstData.setUserNameResonseAttributes(instData.getUserNameResonseAttributes());
			authPolicyInstData.setAnonymousName(instData.getAnonymousName());
			authPolicyInstData.setRequestType(instData.getRequestType());
			authPolicyInstData.setProxyServerRelList(instData.getProxyServerRelList());
			authPolicyInstData.setProxyScript(instData.getProxyScript());
			authPolicyInstData.setProxyTranslationMapConfigId(instData.getProxyTranslationMapConfigId());
			authPolicyInstData.setAuditUid(instData.getAuditUid());
			
			if(authPolicyInstData.getAuditUid() == null){
				String auditId= UUIDGenerator.generate();
				authPolicyInstData.setAuditUid(auditId);
				staffData.setAuditId(auditId);
			}
			
			session.update(authPolicyInstData);

			/* remove old digest config relation */
			criteria = session.createCriteria(AuthPolicyDigestConfRelData.class).add(Restrictions.eq("authPolicyId",authPolicyInstData.getAuthPolicyId()));

			List<AuthPolicyDigestConfRelData> oldRelList =criteria.list();
			if(oldRelList!=null && !oldRelList.isEmpty()){
				for (Iterator<AuthPolicyDigestConfRelData> iterator = oldRelList.iterator(); iterator.hasNext();) {
					AuthPolicyDigestConfRelData relData =  iterator.next();
					session.delete(relData);
				}
			}
			//			/* save digest config relation */
			//			AuthPolicyDigestConfRelData authConfRelData =  authPolicyInstData.getAuthPolicyDigestConfRelData();
			//			if(authConfRelData!=null){
			//				authConfRelData.setAuthPolicyId(authPolicyInstData.getAuthPolicyId());
			//				session.save(authConfRelData);
			//				Logger.logInfo(MODULE,"saving digest conf relation:"+ authConfRelData.getDigestConfId());
			//			}
			
			updateSupportedAuthMethods(authPolicyInstData);
			updateProxyServers(authPolicyInstData);
			session.flush();
			
			session.clear();
			setAuthPolicyInstData(authPolicyInstData, session);
			JSONArray jsonArray=ObjectDiffer.diff(oldObject, authPolicyInstData.toJson());
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);

		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	private void updateSupportedAuthMethods(AuthPolicyInstData authPolicyInstData){
		Session session = getSession();
		long esiTypeId=ExternalSystemConstants.IPPOOL_COMMUNICATION;
		Criteria  criteria = session.createCriteria(AuthPolicyAuthMethodRelData.class)
								.add(Restrictions.eq("authPolicyId",authPolicyInstData.getAuthPolicyId()));

		List<AuthPolicyAuthMethodRelData> oldRelList =criteria.list();
		if(oldRelList!=null && !oldRelList.isEmpty()){
			deleteObjectList(oldRelList, session);
		}
		List<AuthPolicyAuthMethodRelData> supportedAuthMethodList = authPolicyInstData.getAuthMethodRelDataList();
		if(supportedAuthMethodList!=null && !supportedAuthMethodList.isEmpty()){
			for (Iterator<AuthPolicyAuthMethodRelData> iterator = supportedAuthMethodList.iterator(); iterator.hasNext();) {
				AuthPolicyAuthMethodRelData relData =  iterator.next();
				relData.setAuthPolicyId(authPolicyInstData.getAuthPolicyId());
				Logger.logInfo(MODULE,"update supported auth method external system relation:"+ relData.getAuthPolicyId());
				session.save(relData);
			}
		}
	}
	private void updateProxyServers(AuthPolicyInstData authPolicyInstData){
		updateExternalSystem(authPolicyInstData,ExternalSystemConstants.AUTH_PROXY,authPolicyInstData.getProxyServerRelList());
	}

	private void updateIPPoolServers(AuthPolicyInstData authPolicyInstData){
		updateExternalSystem(authPolicyInstData,ExternalSystemConstants.IPPOOL_COMMUNICATION,authPolicyInstData.getIpPoolServerRelList());
	}
	private void updatePrepaidServers(AuthPolicyInstData authPolicyInstData){
		updateExternalSystem(authPolicyInstData,ExternalSystemConstants.PREPAID_COMMUNICATION,authPolicyInstData.getPrepaidServerRelList());
	}
	private void updateChargingGatewayServers(AuthPolicyInstData authPolicyInstData){
		updateExternalSystem(authPolicyInstData,ExternalSystemConstants.CHARGING_GATEWAY_COMMUNICATION,authPolicyInstData.getChargingGatewayServerRelList());
	}


	private void updateExternalSystem(AuthPolicyInstData authPolicyInstData, long externalSystemTypeId, List<AuthPolicyExternalSystemRelData> externalSystemRelList){
		Session session = getSession();

		Criteria  criteria = session.createCriteria(AuthPolicyExternalSystemRelData.class).add(Restrictions.eq("authPolicyId",authPolicyInstData.getAuthPolicyId()));
		criteria.createCriteria("externalSystemData").add(Restrictions.eq("esiTypeId",(long)externalSystemTypeId));
		List<AuthPolicyExternalSystemRelData> oldRelList =criteria.list();
		if(oldRelList!=null && !oldRelList.isEmpty()){
			deleteObjectList(oldRelList, session);
		}
		if(externalSystemRelList!=null && !externalSystemRelList.isEmpty()){
			for (Iterator<AuthPolicyExternalSystemRelData> iterator = externalSystemRelList.iterator(); iterator.hasNext();) {
				AuthPolicyExternalSystemRelData relData =  iterator.next();
				Logger.logInfo(MODULE,"update prepaid external system relation:"+ relData.getEsiInstanceId());
				relData.setAuthPolicyId(authPolicyInstData.getAuthPolicyId());
				session.save(relData);
			}
		}
	}

	private void updateBroadcastExternalSystem(AuthPolicyInstData authPolicyInstData, long externalSystemTypeId, List<AuthPolicyBroadcastESIRelData> externalSystemRelList){
		Session session = getSession();

		Criteria  criteria = session.createCriteria(AuthPolicyBroadcastESIRelData.class).add(Restrictions.eq("authPolicyId",authPolicyInstData.getAuthPolicyId()));
		criteria.createCriteria("externalSystemData").add(Restrictions.eq("esiTypeId",(long)externalSystemTypeId));
		List<AuthPolicyBroadcastESIRelData> oldRelList =criteria.list();
		if(oldRelList!=null && !oldRelList.isEmpty()){
			deleteObjectList(oldRelList, session);
		}
		if(externalSystemRelList!=null && !externalSystemRelList.isEmpty()){
			for (Iterator<AuthPolicyBroadcastESIRelData> iterator = externalSystemRelList.iterator(); iterator.hasNext();) {
				AuthPolicyBroadcastESIRelData relData =  iterator.next();
				Logger.logInfo(MODULE,"update prepaid external system relation:"+ relData.getEsiInstanceId());
				relData.setAuthPolicyId(authPolicyInstData.getAuthPolicyId());
				session.save(relData);
			}
		}
	}

	public void updateAuthorizationParams(AuthPolicyInstData instData,IStaffData staffData,String actionAlias) throws DataManagerException{
		EliteAssert.notNull(instData,"authPolicyInstData must not be null.");
		try{

			Session session = getSession();
			Criteria criteria = session.createCriteria(AuthPolicyInstData.class).add(Restrictions.eq("authPolicyId", instData.getAuthPolicyId()));
			AuthPolicyInstData authPolicyInstData =(AuthPolicyInstData) criteria.uniqueResult();
			
			setAuthPolicyInstData(authPolicyInstData, session);
			JSONObject oldObject = authPolicyInstData.toJson();
			
			authPolicyInstData.setAuthPolicyId(instData.getAuthPolicyId());
			authPolicyInstData.setDefaultSessionTimeout(instData.getDefaultSessionTimeout());
			authPolicyInstData.setActionOnPolicyNotFound(instData.getActionOnPolicyNotFound());
			authPolicyInstData.setRejectOnCheckItemNotFound(instData.getRejectOnCheckItemNotFound());
			authPolicyInstData.setRejectOnRejectItemNotFound(instData.getRejectOnRejectItemNotFound());
			authPolicyInstData.setWimaxEnabled(instData.getWimaxEnabled());
			authPolicyInstData.setThreeGPPEnabled(instData.getThreeGPPEnabled());
			
			if(authPolicyInstData.getAuditUid() == null){
				String auditId= UUIDGenerator.generate();
				authPolicyInstData.setAuditUid(auditId);
				staffData.setAuditId(auditId);
			}
			
			session.update(authPolicyInstData);


			criteria = session.createCriteria(AuthPolicySMRelData.class).add(Restrictions.eq("authPolicyId", authPolicyInstData.getAuthPolicyId()));
			List<AuthPolicySMRelData> authPolicySMRelDataList = criteria.list();
			
			deleteObjectList(authPolicySMRelDataList, session);
			session.flush();

			/* save SM config relation */
			AuthPolicySMRelData authPolicySMRelData =  instData.getAuthPolicySMRelData();
			
			if(authPolicySMRelData!=null){
				authPolicySMRelData.setAuthPolicyId(authPolicyInstData.getAuthPolicyId());
				session.save(authPolicySMRelData);
				Logger.logInfo(MODULE,"saving sessin manager relation:"+ authPolicySMRelData.getSessionManagerInstanceId());
			}
			session.flush();

			session.clear();
			setAuthPolicyInstData(authPolicyInstData, session);
			JSONArray jsonArray=ObjectDiffer.diff(oldObject, authPolicyInstData.toJson());
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);

		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	public void updateDriverGroup(AuthPolicyInstData instData,IStaffData staffData,String actionAlias) throws DataManagerException{
		EliteAssert.notNull(instData,"authPolicyInstData must not be null.");
		try{

			Session session = getSession();
			/* remove old values for main drivers relation*/
			Criteria authPolicyCriteria = session.createCriteria(AuthPolicyInstData.class).add(Restrictions.eq("authPolicyId", instData.getAuthPolicyId()));
			AuthPolicyInstData authPolicyInstData =(AuthPolicyInstData) authPolicyCriteria.uniqueResult();
			
			setAuthPolicyInstData(authPolicyInstData, session);
			JSONObject oldObject = authPolicyInstData.toJson();
			
			Criteria criteria = session.createCriteria(AuthPolicyMainDriverRelData.class);
			List<AuthPolicyMainDriverRelData> oldMainDriverRelDataList = criteria.add(Restrictions.eq("authPolicyId", instData.getAuthPolicyId())).list();
			
			if(oldMainDriverRelDataList!=null && !oldMainDriverRelDataList.isEmpty()){
				deleteObjectList(oldMainDriverRelDataList, session);
			}		
			
			/* save main drivers relation*/
			List<AuthPolicyMainDriverRelData> newMainDriverRelDataList = instData.getMainDriverList();
			if(newMainDriverRelDataList!=null && !newMainDriverRelDataList.isEmpty()){
				for(Iterator<AuthPolicyMainDriverRelData> iterator = newMainDriverRelDataList.iterator();iterator.hasNext();){
					AuthPolicyMainDriverRelData authPolicyMainDriverRelData = iterator.next();
					authPolicyMainDriverRelData.setAuthPolicyId(instData.getAuthPolicyId());
					session.save(authPolicyMainDriverRelData);
					Logger.logInfo(MODULE,"saving main driver relation:"+ authPolicyMainDriverRelData.getDriverInstanceId());
				}
			}
			
			/* remove old values for sec drivers relation*/
			Criteria secDriverCriteria = session.createCriteria(AuthPolicySecDriverRelData.class);
			List<AuthPolicySecDriverRelData> oldSecDriverRelDataList = secDriverCriteria.add(Restrictions.eq("authPolicyId", instData.getAuthPolicyId())).list();
			
			if(oldSecDriverRelDataList!=null && !oldSecDriverRelDataList.isEmpty()){
				deleteObjectList(oldSecDriverRelDataList, session);
			}
			
			/*save secondary drivers relation*/
			List<AuthPolicySecDriverRelData> authPolicySecDriverRelDataList = instData.getSecondaryDriverList();
			
			
			if(authPolicySecDriverRelDataList!=null && !authPolicySecDriverRelDataList.isEmpty()){
				for(Iterator<AuthPolicySecDriverRelData> iterator = authPolicySecDriverRelDataList.iterator();iterator.hasNext();){
					AuthPolicySecDriverRelData authPolicySecDriverRelData = iterator.next();
					authPolicySecDriverRelData.setAuthPolicyId(instData.getAuthPolicyId());
					session.save(authPolicySecDriverRelData);
					Logger.logInfo(MODULE,"saving sec driver relation:"+ authPolicySecDriverRelData.getSecondaryDriverInstId());
				}
			}
			
			/* remove old values for additional drivers relation*/
			Criteria additionalDriverCriteria = session.createCriteria(AuthPolicyAdditionalDriverRelData.class); 
			List<AuthPolicyAdditionalDriverRelData> oldAdditionalDriverRelDataList = additionalDriverCriteria.add(Restrictions.eq("authPolicyId", instData.getAuthPolicyId())).list();
			
			if(additionalDriverCriteria!=null && !oldAdditionalDriverRelDataList.isEmpty()){
				deleteObjectList(oldAdditionalDriverRelDataList, session);
			}
			
			
			/*save additional drivers relation*/
			List<AuthPolicyAdditionalDriverRelData> additionalDriverList = instData.getAdditionalDriverList();
			
			
			if(additionalDriverList != null) {
				for(AuthPolicyAdditionalDriverRelData additionalDriverRelData : additionalDriverList){
					additionalDriverRelData.setAuthPolicyId(instData.getAuthPolicyId());
					session.save(additionalDriverRelData);
				}
			}
			
			
			authPolicyInstData.setDriverScript(instData.getDriverScript());
			authPolicyInstData.setAuthPolicyId(instData.getAuthPolicyId());
			
			if(authPolicyInstData.getAuditUid() == null){
				String auditId= UUIDGenerator.generate();
				authPolicyInstData.setAuditUid(auditId);
				staffData.setAuditId(auditId);
			}
			session.save(authPolicyInstData);
			session.flush();
			session.clear();
			
			setAuthPolicyInstData(authPolicyInstData, session);
			JSONArray jsonArray=ObjectDiffer.diff(oldObject, authPolicyInstData.toJson());
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
			
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	public void updateRMCommunication(AuthPolicyInstData instData,IStaffData staffData,String actionAlias) throws DataManagerException{
		EliteAssert.notNull(instData,"authPolicyInstData must not be null.");
		try{

			Session session = getSession();
			Criteria criteria = session.createCriteria(AuthPolicyInstData.class).add(Restrictions.eq("authPolicyId", instData.getAuthPolicyId()));
			AuthPolicyInstData authPolicyInstData =(AuthPolicyInstData) criteria.uniqueResult();
			
			setAuthPolicyInstData(authPolicyInstData, session);
			JSONObject oldObject = authPolicyInstData.toJson();
			
			authPolicyInstData.setIpPoolRMParamsData(instData.getIpPoolRMParamsData());
			authPolicyInstData.setPrepaidRMParamsData(instData.getPrepaidRMParamsData());
			authPolicyInstData.setChargingGatewayRMParamsData(instData.getChargingGatewayRMParamsData());

			authPolicyInstData.setIpPoolServerRelList(instData.getIpPoolServerRelList());
			authPolicyInstData.setChargingGatewayServerRelList(instData.getChargingGatewayServerRelList());
			authPolicyInstData.setPrepaidServerRelList(instData.getPrepaidServerRelList());
			
			if(authPolicyInstData.getAuditUid() == null){
				String auditId= UUIDGenerator.generate();
				authPolicyInstData.setAuditUid(auditId);
				staffData.setAuditId(auditId);
			}

			session.update(authPolicyInstData);
			updateIpPoolRMParams(authPolicyInstData);
			updatePrepaidRMParams(authPolicyInstData);
			updateChargingGatewayRMParams(authPolicyInstData);
			updateIPPoolServers(authPolicyInstData);
			updatePrepaidServers(authPolicyInstData);
			updateChargingGatewayServers(authPolicyInstData);
			session.flush();

			session.clear();
			setAuthPolicyInstData(authPolicyInstData, session);
			JSONArray jsonArray=ObjectDiffer.diff(oldObject, authPolicyInstData.toJson());
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
			
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}

	public void updateAdditionalProc(AuthPolicyInstData instData,IStaffData staffData,String actionAlias) throws DataManagerException {
		EliteAssert.notNull(instData,"authPolicyInstData must not be null.");
		try{

			Session session = getSession();
			Criteria criteria = session.createCriteria(AuthPolicyInstData.class).add(Restrictions.eq("authPolicyId", instData.getAuthPolicyId()));
			AuthPolicyInstData authPolicyInstData =(AuthPolicyInstData) criteria.uniqueResult();
			authPolicyInstData.setAuthPolicyId(instData.getAuthPolicyId());
			
			setAuthPolicyInstData(authPolicyInstData, session);
			JSONObject oldObject = authPolicyInstData.toJson();
			
			if(authPolicyInstData.getAuditUid() == null){
				String auditId= UUIDGenerator.generate();
				authPolicyInstData.setAuditUid(auditId);
				staffData.setAuditId(auditId);
			}
			
			authPolicyInstData.setBroadcastingServerRelList(instData.getBroadcastingServerRelList());
			updateBroadcastExternalSystem(authPolicyInstData,ExternalSystemConstants.AUTH_PROXY,authPolicyInstData.getBroadcastingServerRelList());
			session.flush();

			session.clear();
			setAuthPolicyInstData(authPolicyInstData, session);
			JSONArray jsonArray=ObjectDiffer.diff(oldObject, authPolicyInstData.toJson());
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}

	}

	private void updateIpPoolRMParams(AuthPolicyInstData authPolicyInstData){
		updateRMParams(authPolicyInstData,authPolicyInstData.getIpPoolRMParamsData(),ExternalSystemConstants.IPPOOL_COMMUNICATION);
	}
	private void updatePrepaidRMParams(AuthPolicyInstData authPolicyInstData){
		updateRMParams(authPolicyInstData,authPolicyInstData.getPrepaidRMParamsData(),ExternalSystemConstants.PREPAID_COMMUNICATION);
	}
	private void updateChargingGatewayRMParams(AuthPolicyInstData authPolicyInstData){
		updateRMParams(authPolicyInstData,authPolicyInstData.getChargingGatewayRMParamsData(),ExternalSystemConstants.CHARGING_GATEWAY_COMMUNICATION);
	}

	private void updateRMParams(AuthPolicyInstData authPolicyInstData, AuthPolicyRMParamsData rmParamsData, long externalSystemTypeId ){
		Session session = getSession();

		Criteria  criteria = session.createCriteria(AuthPolicyRMParamsData.class)
		.add(Restrictions.eq("authPolicyId",authPolicyInstData.getAuthPolicyId()))
		.add(Restrictions.eq("esiTypeId",externalSystemTypeId));
		List<AuthPolicyRMParamsData> oldRMParamsList =criteria.list();
		if(oldRMParamsList!=null && !oldRMParamsList.isEmpty()){
			deleteObjectList(oldRMParamsList, session);

		}
		if(rmParamsData!=null){
			rmParamsData.setAuthPolicyId(authPolicyInstData.getAuthPolicyId());
			session.save(rmParamsData);
		}
	}
	public  List<AuthMethodTypeData> getAuthMethodTypeList() throws DataManagerException{

		List<AuthMethodTypeData> authMethodTypeList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(AuthMethodTypeData.class).add(Restrictions.eq("status","Y"));

			authMethodTypeList = criteria.list();

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return authMethodTypeList;
	}

	public List<AuthPolicyBroadcastESIRelData> getAuthPolicyBroadcastData(String authPolicyId) throws DataManagerException {
		EliteAssert.notNull(authPolicyId,"authPolicyId must not be null.");
		List<AuthPolicyBroadcastESIRelData> authPolicyBroadcastData = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(AuthPolicyBroadcastESIRelData.class).add(Restrictions.eq("authPolicyId", authPolicyId));
			authPolicyBroadcastData = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return authPolicyBroadcastData;
	}

	public PageList search(AuthPolicyInstData authPolicyInstData ,int pageNo, int pageSize) throws DataManagerException {

		Session session = getSession();
		Criteria criteria = session.createCriteria(AuthPolicyInstData.class);
		PageList pageList = null;

		try{

			if((authPolicyInstData.getName() != null && authPolicyInstData.getName().length()>0 )){
				criteria.add(Restrictions.ilike("name","%"+authPolicyInstData.getName()+"%"));
			}

			if(!(authPolicyInstData.getStatus().equalsIgnoreCase("All")) ){

				criteria.add(Restrictions.ilike("status",authPolicyInstData.getStatus()));
			}


			int totalItems = criteria.list().size();
			criteria.setFirstResult(((pageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}
			criteria.addOrder(Order.asc("orderNumber"));
			List policyList = criteria.list();
			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(policyList, pageNo, totalPages ,totalItems);
			Logger.logDebug(MODULE,"LIST SIZE IS:"+policyList.size());
			return  pageList;

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}

	}

	public void delete(List<String> authPolicyIds) throws DataManagerException {
		try{
			Session session = getSession();
			long authPolicyId = 0;

			for(int i=0;i<authPolicyIds.size();i++){
				authPolicyId = Long.parseLong(authPolicyIds.get(i));

				// from main driver
				Criteria criteria = session.createCriteria(AuthPolicyMainDriverRelData.class);
				List<AuthPolicyMainDriverRelData> authPolicyMainDriverRelDataList = criteria.add(Restrictions.eq("authPolicyId", authPolicyId)).list();	  
				deleteObjectList(authPolicyMainDriverRelDataList, session);
				
				// from sec driver
				criteria = session.createCriteria(AuthPolicySecDriverRelData.class);
				List<AuthPolicySecDriverRelData> authPolicySecDriverRelDataList = criteria.add(Restrictions.eq("authPolicyId", authPolicyId)).list();	  
				deleteObjectList(authPolicySecDriverRelDataList, session);
				
				// from additional driver
				criteria = session.createCriteria(AuthPolicyAdditionalDriverRelData.class);
				criteria.add(Restrictions.eq("authPolicyId", authPolicyId));
				List<AuthPolicyAdditionalDriverRelData> additionalDriverRelDataList =  criteria.list();
				deleteObjectList(additionalDriverRelDataList, session);
				
				// from auth method rel data
				criteria = session.createCriteria(AuthPolicyAuthMethodRelData.class);
				List<AuthPolicyAuthMethodRelData> authMethodRelDataList=criteria.add(Restrictions.eq("authPolicyId", authPolicyId)).list();
				deleteObjectList(authMethodRelDataList, session);

				// from esi
				criteria = session.createCriteria(AuthPolicyExternalSystemRelData.class);
				List<AuthPolicyExternalSystemRelData> authPolicyExternalSystemRelDataList =criteria.add(Restrictions.eq("authPolicyId", authPolicyId)).list();
				deleteObjectList(authPolicyExternalSystemRelDataList, session);

				criteria = session.createCriteria(AuthPolicyBroadcastESIRelData.class);
				List<AuthPolicyBroadcastESIRelData> authPolicyBroadcastESIRelDataList =criteria.add(Restrictions.eq("authPolicyId", authPolicyId)).list();
				deleteObjectList(authPolicyBroadcastESIRelDataList, session);

				// from sm 
				criteria = session.createCriteria(AuthPolicySMRelData.class);
				List<AuthPolicySMRelData> authPolicySMRelDataList=criteria.add(Restrictions.eq("authPolicyId", authPolicyId)).list();
				deleteObjectList(authPolicySMRelDataList, session);


				// from rm params
				criteria = session.createCriteria(AuthPolicyRMParamsData.class);
				List<AuthPolicyRMParamsData> authPolicyRMParamsDataList=criteria.add(Restrictions.eq("authPolicyId", authPolicyId)).list();
				deleteObjectList(authPolicyRMParamsDataList, session);


				// from policy
				criteria = session.createCriteria(AuthPolicyInstData.class);
				List<AuthPolicyInstData> authInstDataList=criteria.add(Restrictions.eq("authPolicyId", authPolicyId)).list();
				deleteObjectList(authInstDataList, session);

			}
		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
	}


	public void updateStatus(List<String> authPolicyIds,String status) throws DataManagerException {

		Long authPolicyId = null;
		Session session = getSession();
		Criteria criteria = null;


		for(int i=0;i<authPolicyIds.size();i++){
			authPolicyId = Long.parseLong(authPolicyIds.get(i));
			criteria = session.createCriteria(AuthPolicyInstData.class);
			AuthPolicyInstData authInstData = (AuthPolicyInstData)criteria.add(Restrictions.eq("authPolicyId",authPolicyId)).uniqueResult();
			if(status.equals(BaseConstant.SHOW_STATUS_ID)){
				if(authInstData.getStatus().equals(BaseConstant.HIDE_STATUS_ID)){
					long orderNumber = authInstData.getOrderNumber();
					Criteria newCriteria = session.createCriteria(AuthPolicyInstData.class); 
					newCriteria.add(Restrictions.eq("status","CST01")).add(Restrictions.eq("orderNumber",new Long(orderNumber))); 					
					List sameOrderNoList = newCriteria.list();
					if(sameOrderNoList != null && sameOrderNoList.size() >0){
						// set the order number to the last number
						criteria = session.createCriteria(AuthPolicyInstData.class);
						criteria.add(Restrictions.eq("status",BaseConstant.SHOW_STATUS_ID));
						List<AuthPolicyInstData> tempList = criteria.addOrder(Order.desc("orderNumber")).list();
						if(tempList != null){
							authInstData.setOrderNumber(tempList.get(0).getOrderNumber() + 1);
						}
					}
				}				
			}
			authInstData.setStatus(status);			
			session.update(authInstData);			
			session.flush();

		}


	}
	private void verifyAuthPolicyName(AuthPolicyInstData authPolicyInstData) throws DuplicateInstanceNameFoundException{
		Criteria criteria = null;
		Session session = getSession();
		criteria = session.createCriteria(AuthPolicyInstData.class);
		List list = criteria.add(Restrictions.eq("name",authPolicyInstData.getName())).list();
		if(list==null || list.isEmpty()){
			return;
		}else{
			throw new DuplicateInstanceNameFoundException("Authentication Poilcy Name Is Duplicated.");
		}
	}

	private void verifyAuthPolicyNameForUpdate(AuthPolicyInstData authPolicyInstData) throws DuplicateInstanceNameFoundException{
		Criteria criteria = null;
		Session session = getSession();
		criteria = session.createCriteria(AuthPolicyInstData.class);
		List list = criteria.add(Restrictions.eq("name",authPolicyInstData.getName())).add(Restrictions.ne("authPolicyId", authPolicyInstData.getAuthPolicyId())).list();
		if(list==null || list.isEmpty()){
			return;
		}else{
			throw new DuplicateInstanceNameFoundException("Authentication Poilcy Name Is Duplicated.");
		}
	}


	public List<AuthPolicyInstData> searchActiveAuthServicePolicy() throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(AuthPolicyInstData.class).addOrder(Order.asc("orderNumber"));
			return criteria.add(Restrictions.eq("status","CST01")).list();

		}catch(HibernateException e){
			throw new DataManagerException(e.getMessage(),e);
		}

	}


	public List<AuthPolicyInstData> getAuthPolicyInstDataList(AuthPolicyInstData authPolicyInstData) throws DataManagerException{
		List<AuthPolicyInstData> authPolicyInstDataList = null;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(AuthPolicyInstData.class);

			if(authPolicyInstData != null){
				if( Strings.isNullOrEmpty(authPolicyInstData.getAuthPolicyId()) == false){
					criteria.add(Restrictions.eq("authPolicyId",authPolicyInstData.getAuthPolicyId()));
				}

				if(authPolicyInstData.getName() != null){
					criteria.add(Restrictions.eq("name",authPolicyInstData.getName()));
				}

				if(authPolicyInstData.getStatus() != null){
					criteria.add(Restrictions.eq("status",authPolicyInstData.getStatus()));
				}

			}
			authPolicyInstDataList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return authPolicyInstDataList;
	}

	public List<AuthPolicySMRelData> getAuthPolicySMRelData(String authPolicyId) throws DataManagerException{
		EliteAssert.notNull(authPolicyId,"authPolicyId must not be null."); 

		List<AuthPolicySMRelData> authPolicySMRelDataList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(AuthPolicySMRelData.class).add(Restrictions.eq("authPolicyId", authPolicyId));
			authPolicySMRelDataList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return authPolicySMRelDataList;

	}


	public List<AuthPolicyBroadcastESIRelData> getBroadcastingESIRelList(AuthPolicyInstData authPolicyInstData, Long externalSystemTypeId) throws DataManagerException {
		List<AuthPolicyBroadcastESIRelData> externalSystemRelList=null; 
		Logger.logDebug(MODULE, "externalSystemTypeId :"+externalSystemTypeId);
		try{

			Session session = getSession();
			Criteria  criteria = session.createCriteria(AuthPolicyBroadcastESIRelData.class).add(Restrictions.eq("authPolicyId",authPolicyInstData.getAuthPolicyId()));
			criteria.createCriteria("externalSystemData").add(Restrictions.eq("esiTypeId",(long)externalSystemTypeId));
			criteria.setFetchMode("translationMappingConfData", FetchMode.JOIN);
			externalSystemRelList=criteria.list();         
			session.clear(); 
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
		return externalSystemRelList;
	}

	public List<AuthPolicyExternalSystemRelData> getExternalSystemRelList(AuthPolicyInstData authPolicyInstData,Long externalSystemTypeId) throws DataManagerException {
		List<AuthPolicyExternalSystemRelData> externalSystemRelList=null; 
		Logger.logDebug(MODULE, "externalSystemTypeId :"+externalSystemTypeId);
		try{

			Session session = getSession();
			Criteria  criteria = session.createCriteria(AuthPolicyExternalSystemRelData.class).add(Restrictions.eq("authPolicyId",authPolicyInstData.getAuthPolicyId()));
			criteria.createCriteria("externalSystemData").add(Restrictions.eq("esiTypeId",(long)externalSystemTypeId));
			externalSystemRelList=criteria.list();         
			session.clear(); 
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
		return externalSystemRelList;
	}
	public List<DriverInstanceData> getMainDriverList(AuthPolicyInstData authPolicyInstData) throws DataManagerException {

		List<DriverInstanceData> mainDriverList = new ArrayList<DriverInstanceData>();
		try{
			Session session = getSession();

			Criteria mainDriverCriteria = session.createCriteria(AuthPolicyMainDriverRelData.class);
			mainDriverCriteria.add(Restrictions.eq("authPolicyId",authPolicyInstData.getAuthPolicyId()));
			List<AuthPolicyMainDriverRelData> mainDriverRelList =mainDriverCriteria.list(); 

			for (Iterator iterator = mainDriverRelList.iterator(); iterator.hasNext();) {
				AuthPolicyMainDriverRelData authPolicyMainDriverRelData = (AuthPolicyMainDriverRelData) iterator.next();

				Criteria driverCriteria = session.createCriteria(DriverInstanceData.class);
				DriverInstanceData data=(DriverInstanceData)driverCriteria.add(Restrictions.eq("driverInstanceId",authPolicyMainDriverRelData.getDriverInstanceId())).uniqueResult();
				mainDriverList.add(data);

			}

		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}

		return mainDriverList;
	}

	public List<AuthPolicyMainDriverRelData> getDriverListPolicyId(AuthPolicyInstData authPolicyInstData) throws DataManagerException {
		try{
			Session session = getSession();

			Criteria mainDriverCriteria = session.createCriteria(AuthPolicyMainDriverRelData.class);
			mainDriverCriteria.add(Restrictions.eq("authPolicyId",authPolicyInstData.getAuthPolicyId()));
			List<AuthPolicyMainDriverRelData> mainDriverRelList = mainDriverCriteria.list();
			return mainDriverRelList;

		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
	}
	
	public List<AuthPolicySecDriverRelData> getAuthPolicySecDriverList(AuthPolicyInstData authPolicyInstData) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria cacheableDriverCriteria = session.createCriteria(AuthPolicySecDriverRelData.class);
			cacheableDriverCriteria.add(Restrictions.eq("authPolicyId",authPolicyInstData.getAuthPolicyId()));
			List<AuthPolicySecDriverRelData> cacheableDriverRelList = cacheableDriverCriteria.list();
			return cacheableDriverRelList;
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
	}

	
	public List<AuthPolicyAdditionalDriverRelData> getAuthPolicyAdditionalDriverList(AuthPolicyInstData authPolicyInstData) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria cacheableAdditionalDriveCriteria = session.createCriteria(AuthPolicyAdditionalDriverRelData.class);
			cacheableAdditionalDriveCriteria.add(Restrictions.eq("authPolicyId",authPolicyInstData.getAuthPolicyId()))
											.setFetchMode("driverInstanceData", FetchMode.JOIN)
											.addOrder(Order.desc("orderNumber"));
			
			List<AuthPolicyAdditionalDriverRelData> cacheableDriverRelList = cacheableAdditionalDriveCriteria.list();
			return cacheableDriverRelList;
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
	}
	
	public List<AuthMethodTypeData> getSupportedAuthMethods(AuthPolicyInstData authPolicyInstData) throws DataManagerException {
		List<AuthMethodTypeData> supportedAuthMethodList =null;

		try{

			Session session = getSession();
			Criteria  criteria = session.createCriteria(AuthPolicyAuthMethodRelData.class).add(Restrictions.eq("authPolicyId",authPolicyInstData.getAuthPolicyId()));
			List<AuthPolicyAuthMethodRelData> list = criteria.list();
			List<Long> authMethodTypeIdList=new ArrayList();
			if(list!=null && !list.isEmpty()){
				Logger.logDebug(MODULE, "getSupportedAuthMethods size of the list :"+list.size());
				for (Iterator<AuthPolicyAuthMethodRelData> iterator = list.iterator(); iterator.hasNext();) {
					AuthPolicyAuthMethodRelData authPolicyAuthMethodRelData =  iterator.next();
					if(!authMethodTypeIdList.contains(authPolicyAuthMethodRelData.getAuthMethodTypeId())){
						authMethodTypeIdList.add(authPolicyAuthMethodRelData.getAuthMethodTypeId());
					}
				}
				criteria = session.createCriteria(AuthMethodTypeData.class).add(Restrictions.in("authMethodTypeId",authMethodTypeIdList));
				supportedAuthMethodList=criteria.list(); 
			}


		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}

		return supportedAuthMethodList;
	}
	public AuthPolicyDigestConfRelData getDigestConfigRelData(AuthPolicyInstData authPolicyInstData) throws DataManagerException {

		AuthPolicyDigestConfRelData digestConfRelData =null;

		try{

			Session session = getSession();
			Criteria  criteria = session.createCriteria(AuthPolicyDigestConfRelData.class).add(Restrictions.eq("authPolicyId",authPolicyInstData.getAuthPolicyId()));
			List<AuthPolicyDigestConfRelData>  digestConfRelList = criteria.list();
			if(digestConfRelList!=null && !digestConfRelList.isEmpty()){
				digestConfRelData =digestConfRelList.get(0);
			}

		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
		return digestConfRelData;		
	}

	public AuthPolicyRMParamsData getRMParamsData(AuthPolicyInstData authPolicyInstData, long externalSystemTypeId)	throws DataManagerException {

		AuthPolicyRMParamsData authPolicyRMParamsData =null;

		try{

			Session session = getSession();
			Criteria  criteria = session.createCriteria(AuthPolicyRMParamsData.class)
			.add(Restrictions.eq("authPolicyId",authPolicyInstData.getAuthPolicyId()))
			.add(Restrictions.eq("esiTypeId", externalSystemTypeId));
			List<AuthPolicyRMParamsData>  rmParamsList = criteria.list();
			if(rmParamsList!=null && !rmParamsList.isEmpty()){
				authPolicyRMParamsData =rmParamsList.get(0);
			}

		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
		return authPolicyRMParamsData;	
	}
	public AuthPolicyInstData getPluginListByAuthPolicyIdForAuth(AuthPolicyInstData authPolicyInstData) throws DataManagerException {

		Session session = getSession();
		Criteria criteria = session.createCriteria(AuthPolicyInstData.class);
		AuthPolicyInstData data = (AuthPolicyInstData)criteria.add(Restrictions.eq("authPolicyId", authPolicyInstData.getAuthPolicyId())).uniqueResult();

		return data;
	}
	public void updateAuthPolicyPlugins(AuthPolicyInstData authPolicyInstData,IStaffData staffData,String actionAlias)throws DataManagerException {

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(AuthPolicyInstData.class);
			AuthPolicyInstData policyData = (AuthPolicyInstData)criteria.add(Restrictions.eq("authPolicyId",authPolicyInstData.getAuthPolicyId())).uniqueResult();

			setAuthPolicyInstData(policyData, session);
			JSONObject oldObject = policyData.toJson();
			
			policyData.setPrePlugins(authPolicyInstData.getPrePlugins());
			policyData.setPostPlugins(authPolicyInstData.getPostPlugins());
			
			if(authPolicyInstData.getAuditUid() == null){
				String auditId= UUIDGenerator.generate();
				authPolicyInstData.setAuditUid(auditId);
				staffData.setAuditId(auditId);
			}
			session.update(policyData);
			session.flush();
			
			session.clear();
			setAuthPolicyInstData(policyData, session);
			JSONArray jsonArray=ObjectDiffer.diff(oldObject, policyData.toJson());
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);

		}catch (HibernateException e) {
			throw new DataManagerException(e.getMessage(),e);
		}

	}
	
	private void setAuthPolicyInstData(AuthPolicyInstData authPolicyInstData, Session session){
		String authPolicyId = authPolicyInstData.getAuthPolicyId();

		//For prepaidServerRelList
        authPolicyInstData.setPrepaidRMParamsData(
        		getAuthPolicyRMParamsDataFromDB(
        				authPolicyId, 
        				ExternalSystemConstants.PREPAID_COMMUNICATION, 
        				session)
        		);
        
        //For ipPoolRMParamsData
        authPolicyInstData.setIpPoolRMParamsData(
        		getAuthPolicyRMParamsDataFromDB(
        				authPolicyId, 
        				ExternalSystemConstants.IPPOOL_COMMUNICATION, 
        				session)
        		);
        
        //For chargingGatewayRMParamsData
        authPolicyInstData.setChargingGatewayRMParamsData(
        		getAuthPolicyRMParamsDataFromDB(
        				authPolicyId, 
        				ExternalSystemConstants.CHARGING_GATEWAY_COMMUNICATION, 
        				session)
        		);
        
        //For authPolicySMRelData
        authPolicyInstData.setAuthPolicySMRelData(
        		(AuthPolicySMRelData)
        		session.createCriteria(AuthPolicySMRelData.class)
        		.add(Restrictions.eq("authPolicyId",authPolicyId))
        		.uniqueResult()
        		);
        
        //For proxyServerRelList
        authPolicyInstData.setProxyServerRelList(
        		getAuthPolicyExternalSystemRelDataList(
        				authPolicyId, 
        				ExternalSystemConstants.AUTH_PROXY, 
        				session)
        		);

        //For ipPoolServerRelList
        authPolicyInstData.setIpPoolServerRelList(
        		getAuthPolicyExternalSystemRelDataList(
        				authPolicyId, 
        				ExternalSystemConstants.IPPOOL_COMMUNICATION, 
        				session)
        		);

        //For prepaidServerRelList
        authPolicyInstData.setPrepaidServerRelList(
        		getAuthPolicyExternalSystemRelDataList(
        				authPolicyId, 
        				ExternalSystemConstants.PREPAID_COMMUNICATION, 
        				session)
        		);

        //For chargingGatewayServerRelList
        authPolicyInstData.setChargingGatewayServerRelList(
        		getAuthPolicyExternalSystemRelDataList(authPolicyId, 
        				ExternalSystemConstants.CHARGING_GATEWAY_COMMUNICATION, 
        				session)
        		);
        
		//For mainDriverList
        authPolicyInstData.setMainDriverList(
        		session.createCriteria(AuthPolicyMainDriverRelData.class)
        		.add(Restrictions.eq("authPolicyId",authPolicyId))
        		.list()
        		);
        session.flush();
        
        //For additionalDrivers
    	authPolicyInstData.setAdditionalDriverList(
    			session.createCriteria(AuthPolicyAdditionalDriverRelData.class)
    			.add(Restrictions.eq("authPolicyId",authPolicyId))
        		.list()
        	);
    	
        //For broadcastingServerRelList
        authPolicyInstData.setBroadcastingServerRelList(
        		session.createCriteria(AuthPolicyBroadcastESIRelData.class)
        		.add(Restrictions.eq("authPolicyId",authPolicyId))
        		.list()
        		);
        session.flush();
        
        //For secondaryDriverList
        authPolicyInstData.setSecondaryDriverList(
        		session.createCriteria(AuthPolicySecDriverRelData.class)
        		.add(Restrictions.eq("authPolicyId",authPolicyId))
        		.list()
        		);
        
        //For authMethodRelDataList
        authPolicyInstData.setAuthMethodRelDataList(
        		session.createCriteria(AuthPolicyAuthMethodRelData.class)
        		.add(Restrictions.eq("authPolicyId",authPolicyId))
        		.list()
        		);
	}
	
	private AuthPolicyRMParamsData getAuthPolicyRMParamsDataFromDB(String policyId, Long esiTypeId, Session session){
		Criteria  criteria = session.createCriteria(AuthPolicyRMParamsData.class)
				.add(Restrictions.eq("authPolicyId",policyId))
				.add(Restrictions.eq("esiTypeId",esiTypeId));
		AuthPolicyRMParamsData authPolicyRMParamsData = (AuthPolicyRMParamsData) criteria.uniqueResult();
		session.flush();
		return authPolicyRMParamsData;
	}
	
	private List<AuthPolicyExternalSystemRelData> getAuthPolicyExternalSystemRelDataList(String policyId, Long esiTypeId, Session session){
		Criteria inValuesCriteria = session.createCriteria(ExternalSystemInterfaceInstanceData.class)
				.add(Restrictions.eq("esiTypeId",esiTypeId))
				.setProjection(Projections.property("esiInstanceId"));
		List instanceId = inValuesCriteria.list();
		session.flush();
		
		List<AuthPolicyExternalSystemRelData> list = null;
		if(!instanceId.isEmpty()){
			Criteria  criteria = session.createCriteria(AuthPolicyExternalSystemRelData.class)
					.add(Restrictions.eq("authPolicyId",policyId))
					.add(Restrictions.in("esiInstanceId", instanceId));
			list = criteria.list();
			session.flush();
		}
		return list;
	}
}
