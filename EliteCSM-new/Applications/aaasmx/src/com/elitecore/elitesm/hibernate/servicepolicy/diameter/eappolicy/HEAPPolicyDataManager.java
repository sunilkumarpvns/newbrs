package com.elitecore.elitesm.hibernate.servicepolicy.diameter.eappolicy;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.DatabaseConnectionException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.EAPPolicyDataManager;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyAdditionalDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyAuthDriverRelationData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyPluginConfig;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPResponseAttributes;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

import net.sf.json.JSONArray;

public class HEAPPolicyDataManager extends HBaseDataManager implements EAPPolicyDataManager {

	private static final String EAP_POLICY_NAME = "name";
	private static final String EAP_POLICY_ID = "eapPolicyId";
	private static final String MODULE = "HEAPPolicyDataManager";

	@Override
	public String create(Object obj) throws DataManagerException {
		EAPPolicyData data = (EAPPolicyData) obj;
		try{
			org.hibernate.Session session = getSession();
			session.clear();
			Criteria criteria = session.createCriteria(EAPPolicyData.class);
			EAPPolicyData eapPolicyData = (EAPPolicyData)criteria.add(Restrictions.eq(EAP_POLICY_NAME,data.getName())).uniqueResult();					

			criteria = session.createCriteria(EAPPolicyData.class);
			criteria.setFetchSize(1);
			List<EAPPolicyData> eapDataList = criteria.addOrder(Order.desc("orderNumber")).list();

			if(eapDataList != null && eapDataList.size() > 0){
				data.setOrderNumber(eapDataList.get(0).getOrderNumber()+ 1);
			}else{
				data.setOrderNumber(1L);
			}
			
			String auditId= UUIDGenerator.generate();
			
			data.setAuditUId(auditId);
			
			session.save(data);

			String eapPolicyId = data.getEapPolicyId();

			List<EAPPolicyAuthDriverRelationData> tempDriverList = data.getDriverList();

			criteria = session.createCriteria(EAPPolicyAuthDriverRelationData.class);
			if(tempDriverList != null && tempDriverList.size() >0){
				for(int i=0;i<tempDriverList.size();i++){
					EAPPolicyAuthDriverRelationData driverData = tempDriverList.get(i);
					driverData.setPolicyId(eapPolicyId);
					session.save(driverData);
			
				}	
			}
			
			/* save eap additional driverData */
			if(data.getEapAdditionalDriverRelDataList() != null) {
				List<EAPPolicyAdditionalDriverRelData> eapAdditionalDriverRelDataList = data.getEapAdditionalDriverRelDataList();
				for(EAPPolicyAdditionalDriverRelData eapPolicyAdditionalDriverRelData : eapAdditionalDriverRelDataList) {
					eapPolicyAdditionalDriverRelData.setEapPolicyId(data.getEapPolicyId());
					session.save(eapPolicyAdditionalDriverRelData);
				}
			}
			
			/* save EAP Response Attribute data */
			if(data.getEapResponseAttributesSet() != null) {
				int orderNumber = 1;
				Set<EAPResponseAttributes> eapAdditionalDriverRelDataList = data.getEapResponseAttributesSet();
				for(EAPResponseAttributes eapResponseAttributeData : eapAdditionalDriverRelDataList) {
					eapResponseAttributeData.setEapPolicyId(data.getEapPolicyId());
					eapResponseAttributeData.setOrderNumber(orderNumber++);
					session.save(eapResponseAttributeData);
				}
			}
			
			/*save Eap plugins*/
			saveEAPPlugins(data.getEapPolicyId(), data.getEapPolicyPluginConfigList(), session);
			
			session.flush();
			session.clear();
			return data.getName();
		}catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + data.getName() + REASON 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + data.getName() + REASON + e.getMessage(), e);
		} catch (Exception exp) {
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(FAILED_TO_CREATE + data.getName() + REASON + exp.getMessage(), exp);
		}
	}

	public String deleteById(String policyId) throws DataManagerException {
		return delete(policyId, EAP_POLICY_ID);
	}
	
	@Override
	public String deleteByName(String policyName) throws DataManagerException {
		return delete(policyName, EAP_POLICY_NAME);
	}

	private String delete(Object policiesToDelete, String property) throws DataManagerException {
		String policyName = (EAP_POLICY_NAME.equals(property)) ? (String)policiesToDelete : "EAP Service Policy";
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(EAPPolicyData.class);
			EAPPolicyData eapPolicyData = (EAPPolicyData)criteria.add(Restrictions.eq(property, policiesToDelete)).uniqueResult();
			
			if (eapPolicyData == null) {
				throw new InvalidValueException("policy does not exist");
			}
			
			Criteria driverCriteria = session.createCriteria(EAPPolicyAuthDriverRelationData.class);
			List<EAPPolicyAuthDriverRelationData> eapPolicyDriverRelList = driverCriteria.add(Restrictions.eq("policyId", eapPolicyData.getEapPolicyId())).list();
			deleteObjectList(eapPolicyDriverRelList, session);

			driverCriteria = session.createCriteria(EAPPolicyAdditionalDriverRelData.class).add(Restrictions.eq(EAP_POLICY_ID, eapPolicyData.getEapPolicyId())); 
			deleteObjectList(driverCriteria.list(), session);

			//From Policy
			session.delete(eapPolicyData);
			return eapPolicyData.getName();
			
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + policyName + ", Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException hExp) {
			hExp.printStackTrace();
            throw new DataManagerException("Failed to delete " + policyName + ", Reason: " + hExp.getMessage(), hExp);
        } catch(Exception exp) {
        	exp.printStackTrace();
        	throw new DataManagerException("Failed to delete " + policyName + ", Reason: " + exp.getMessage(), exp);
        }
	}
	
	public PageList searchEAPPolicy(EAPPolicyData eapPolicyData ,int pageNo, int pageSize) throws DataManagerException {

		Session session = getSession();
		Criteria criteria = session.createCriteria(EAPPolicyData.class);
		PageList pageList = null;

		try{
            
			if((eapPolicyData.getName() != null && eapPolicyData.getName().length()>0 )){
            	criteria.add(Restrictions.ilike(EAP_POLICY_NAME,"%"+eapPolicyData.getName()+"%"));
            }

            if(!(eapPolicyData.getStatus().equalsIgnoreCase("All")) ){
            	
            	criteria.add(Restrictions.ilike("status",eapPolicyData.getStatus()));
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
			return  pageList;

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}

	}
	
	
	public void updateBasicDetail(EAPPolicyData policyData,IStaffData staffData,String actionAlias)throws DataManagerException{
		try{
			Session session = getSession();
			EAPPolicyData data =  (EAPPolicyData) session.get(EAPPolicyData.class, policyData.getEapPolicyId());

			JSONArray jsonArray=ObjectDiffer.diff(data, policyData);
			
			data.setName(policyData.getName());
			data.setDescription(policyData.getDescription());
			data.setRuleSet(policyData.getRuleSet());
			data.setSessionManagement(policyData.getSessionManagement());
			data.setRequestType(policyData.getRequestType());
			data.setDefaultResponseBehaviorArgument(policyData.getDefaultResponseBehaviorArgument());
			data.setDefaultResponseBehaviour(policyData.getDefaultResponseBehaviour());
			if(data.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
				data.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
			session.update(data);
			
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}	
	}
	
	public void updateAuthenticationDetails(EAPPolicyData policyData,IStaffData staffData,String actionAlias) throws DataManagerException {
		try{
			Session session = getSession();
			EAPPolicyData data =  (EAPPolicyData) session.get(EAPPolicyData.class, policyData.getEapPolicyId());
			
			JSONArray jsonArray=ObjectDiffer.diff(data, policyData);
			
			data.setEapConfigId(policyData.getEapConfigId());
			data.setMultipleUserIdentity(policyData.getMultipleUserIdentity());
			data.setCaseSensitiveUserIdentity(policyData.getCaseSensitiveUserIdentity());
			data.setStripUserIdentity(policyData.getStripUserIdentity());
			data.setRealmSeparator(policyData.getRealmSeparator());
			data.setRealmPattern(policyData.getRealmPattern());
			data.setTrimUserIdentity(policyData.getTrimUserIdentity());
			data.setTrimPassword(policyData.getTrimPassword());
			data.setAnonymousProfileIdentity(policyData.getAnonymousProfileIdentity());
			
			if(data.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
				data.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
			
			session.update(data);
			
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}
		
	}
	
	public void updateAuthorizationDetails(EAPPolicyData policyData,IStaffData staffData,String actionAlias) throws DataManagerException {
		try{
			Session session = getSession();
			EAPPolicyData data =  (EAPPolicyData) session.get(EAPPolicyData.class, policyData.getEapPolicyId());
			JSONArray jsonArray=ObjectDiffer.diff(data, policyData);
			
			data.setWimax(policyData.getWimax());
			data.setRejectOnCheckItemNotFound(policyData.getRejectOnCheckItemNotFound());
			data.setRejectOnRejectItemNotFound(policyData.getRejectOnRejectItemNotFound());
			data.setActionOnPolicyNotFound(policyData.getActionOnPolicyNotFound());
			data.setGracePolicy(policyData.getGracePolicy());
			data.setDiameterConcurrency(policyData.getDiameterConcurrency());
			data.setAdditionalDiameterConcurrency(policyData.getAdditionalDiameterConcurrency());
			data.setDefaultSessionTimeout(policyData.getDefaultSessionTimeout());
			
			if(data.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
				data.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
			
			session.update(data);
			
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}
		
	}
	
	public void updateDriverProfiles(EAPPolicyData policyData,IStaffData staffData,String actionAlias) throws DataManagerException{
		try{
			Session session = getSession();
			/* Driver */ 
			EAPPolicyData data = (EAPPolicyData) session.get(EAPPolicyData.class, policyData.getEapPolicyId()); 
			
			JSONArray jsonArray=ObjectDiffer.diff(data, policyData);
			
			
			Criteria criteria = session.createCriteria(EAPPolicyAuthDriverRelationData.class);
			
			List<EAPPolicyAuthDriverRelationData> driveRelationalDataList = criteria.add(Restrictions.eq("policyId",policyData.getEapPolicyId())).list();
			for(int i=0;i<driveRelationalDataList.size();i++){
				EAPPolicyAuthDriverRelationData driverReldt = driveRelationalDataList.get(i);
				session.delete(driverReldt);
				session.flush();
			}

			if(policyData.getDriverList() != null && policyData.getDriverList().size() > 0){				
				for(int i=0;i<policyData.getDriverList().size();i++){					
					criteria = session.createCriteria(EAPPolicyAuthDriverRelationData.class);
					EAPPolicyAuthDriverRelationData relData = policyData.getDriverList().get(i);
					relData.setPolicyId(policyData.getEapPolicyId());
					session.save(relData);
					session.flush();
				}
			}
			
			/* Additional Driver */
			criteria = session.createCriteria(EAPPolicyAdditionalDriverRelData.class);
			List<EAPPolicyAdditionalDriverRelData> eapAdditionalDriverRelDataList = criteria.add(Restrictions.eq(EAP_POLICY_ID,policyData.getEapPolicyId())).list();
			for(EAPPolicyAdditionalDriverRelData eapPolicyAdditionalDriverRelData : eapAdditionalDriverRelDataList) {
				session.delete(eapPolicyAdditionalDriverRelData);
				session.flush();
			}
			if(policyData.getEapAdditionalDriverRelDataList() != null) {
				List<EAPPolicyAdditionalDriverRelData> eapAdditionalDriverRelDatas = policyData.getEapAdditionalDriverRelDataList();
				for(EAPPolicyAdditionalDriverRelData eapPolicyAdditionalDriverRelData : eapAdditionalDriverRelDatas) {
					eapPolicyAdditionalDriverRelData.setEapPolicyId(policyData.getEapPolicyId());
					session.save(eapPolicyAdditionalDriverRelData);
					session.flush();
				}
			}
			data.setScript(policyData.getScript());
			
			//Update plugins
			List<EAPPolicyPluginConfig> eapPolicyPluginConfigList = getPolicyPluginConfigList(policyData.getEapPolicyId());
			if(!eapPolicyPluginConfigList.isEmpty()){
				deleteObjectList(eapPolicyPluginConfigList,session);
			}
			saveEAPPlugins(policyData.getEapPolicyId(), policyData.getEapPolicyPluginConfigList(), session);
			
			if(data.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
				data.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
			
			session.update(data);
			
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}
	}

	public EAPPolicyData getEAPPolicyById(String eapPolicyId) throws DataManagerException {
		return getEapPolicy(EAP_POLICY_ID, eapPolicyId);
	}
	
	@Override
	public EAPPolicyData getEAPPolicyByName(String eapPolicyName) throws DataManagerException {
		return getEapPolicy(EAP_POLICY_NAME, eapPolicyName);
	}
	
	private EAPPolicyData getEapPolicy(String columnName, Object nameOrIdValue) throws DataManagerException {
		String policyName = (EAP_POLICY_NAME.equals(columnName)) ? (String)nameOrIdValue : "EAP Service Policy";
		try{
			org.hibernate.Session session = getSession();
			Criteria criteria = session.createCriteria(EAPPolicyData.class);
			EAPPolicyData data = (EAPPolicyData)criteria.add(Restrictions.eq(columnName, nameOrIdValue)).uniqueResult();
			
			if (data == null) {
				throw new InvalidValueException("policy does not exist");
			}
			
			criteria = session.createCriteria(EAPPolicyAuthDriverRelationData.class);
			List<EAPPolicyAuthDriverRelationData> relationalDataList = criteria.add(Restrictions.eq("policyId", data.getEapPolicyId())).list();
			data.setDriverList(relationalDataList);
			
			// Additional Driver
			criteria = session.createCriteria(EAPPolicyAdditionalDriverRelData.class);
			criteria.add(Restrictions.eq(EAP_POLICY_ID, data.getEapPolicyId())).setFetchMode("driverInstanceData", FetchMode.JOIN);
			List<EAPPolicyAdditionalDriverRelData> eapAdditionalDriverRelDataList = criteria.list();;
			data.setEapAdditionalDriverRelDataList(eapAdditionalDriverRelDataList);
			
			return data;
			
		} catch(DataManagerException exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrieve " + policyName +", Reason: " + exp.getMessage(), exp);
		} catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrieve " + policyName +", Reason: " + exp.getMessage(), exp);
		}
	}
	
	public void changePolicyOrder(String[] order) throws DataManagerException {		
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(EAPPolicyData.class);
			List<EAPPolicyData> policyList = criteria.add(Restrictions.eq("status", "CST01")).list();
			if(order != null){
				for(int i=0;i<order.length;i++){
					String name = order[i];
					for(int j=0;j<policyList.size();j++){
						EAPPolicyData tempPolicyData = policyList.get(j);
						if(tempPolicyData.getName().equals(name)){
							tempPolicyData.setOrderNumber(i+1L);
							session.update(tempPolicyData);
							break;
						}
					}
				}
			}
			
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
	}
	
	public List getEAPPolicies() throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(EAPPolicyData.class).addOrder(Order.asc("orderNumber"));
			return criteria.add(Restrictions.eq("status","CST01")).list();
			
		}catch(HibernateException e){
			throw new DataManagerException(e.getMessage(),e);
		}
	}
	
	public List getEAPPolicyList() throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(EAPPolicyData.class).addOrder(Order.asc("orderNumber"));
			return criteria.list();
			
		}catch(HibernateException e){
			throw new DataManagerException(e.getMessage(),e);
		}
	}
	
	public void updateStatus(List<String> eapPolicyIds,String status) throws DataManagerException {

		String eapPolicyId = null;
		Session session = getSession();
		Criteria criteria = null;

		if(eapPolicyIds != null && eapPolicyIds.size() > 0) {
			for(int i=0;i<eapPolicyIds.size();i++){
				eapPolicyId = eapPolicyIds.get(i);
				criteria = session.createCriteria(EAPPolicyData.class);
				EAPPolicyData eapPolicyData = (EAPPolicyData)criteria.add(Restrictions.eq(EAP_POLICY_ID,eapPolicyId)).uniqueResult();
				if(status.equals(BaseConstant.SHOW_STATUS_ID)){
					if(eapPolicyData.getStatus().equals(BaseConstant.HIDE_STATUS_ID)){
						long orderNumber = eapPolicyData.getOrderNumber();
						Criteria newCriteria = session.createCriteria(EAPPolicyData.class); 
						newCriteria.add(Restrictions.eq("status","CST01")).add(Restrictions.eq("orderNumber",new Long(orderNumber))); 					
						List sameOrderNoList = newCriteria.list();
						if(sameOrderNoList != null && sameOrderNoList.size() >0){
							// set the order number to the last number
							criteria = session.createCriteria(EAPPolicyData.class);
							criteria.add(Restrictions.eq("status",BaseConstant.SHOW_STATUS_ID));
							List<EAPPolicyData> tempList = criteria.addOrder(Order.desc("orderNumber")).list();
							if(tempList != null){
								eapPolicyData.setOrderNumber(tempList.get(0).getOrderNumber() + 1);
							}
						}
					}				
				}
				eapPolicyData.setStatus(status);			
				session.update(eapPolicyData);			
				session.flush();
			}
		}
	}

	@Override
	public void updateResponseAttributes(EAPPolicyData eapPolicyData,IStaffData staffData, String actionAlias)throws DataManagerException {
		Session session = getSession();
		/* Driver */ 
		EAPPolicyData data = (EAPPolicyData) session.get(EAPPolicyData.class, eapPolicyData.getEapPolicyId()); 
		
		JSONArray jsonArray=ObjectDiffer.diff(data, eapPolicyData);
		
		Criteria criteria = session.createCriteria(EAPResponseAttributes.class);
		
		List<EAPResponseAttributes> eapResponseAttributeList = criteria.add(Restrictions.eq(EAP_POLICY_ID,eapPolicyData.getEapPolicyId())).list();
		for(int i=0;i<eapResponseAttributeList.size();i++){
			EAPResponseAttributes eapResponseAttributes = eapResponseAttributeList.get(i);
			session.delete(eapResponseAttributes);
			session.flush();
		}
		
		Set<EAPResponseAttributes> eapResponseAttributes = eapPolicyData.getEapResponseAttributesSet();

		//Save data
		if(eapResponseAttributes != null && eapResponseAttributes.isEmpty() == false){
			int orderNumber = 1;
			for(Iterator<EAPResponseAttributes> iteratorForDummyResp = eapResponseAttributes.iterator();iteratorForDummyResp.hasNext();){
				EAPResponseAttributes eapResponseAttributesData = iteratorForDummyResp.next();
				eapResponseAttributesData.setEapPolicyId(data.getEapPolicyId());
				eapResponseAttributesData.setOrderNumber(orderNumber++);
				session.save(eapResponseAttributesData);
				session.flush();
			}
		}
		
		doAuditingJson(jsonArray.toString(),staffData,actionAlias);
	}

	@Override
	public void updateRFC4372CUIDetails(EAPPolicyData eapPolicyData, IStaffData staffData, String actionAlias) throws DataManagerException {
		try{
			Session session = getSession();
			EAPPolicyData data =  (EAPPolicyData) session.get(EAPPolicyData.class, eapPolicyData.getEapPolicyId());
			JSONArray jsonArray=ObjectDiffer.diff(data, eapPolicyData);
			
			data.setCui(eapPolicyData.getCui());
			data.setCuiResponseAttributes(eapPolicyData.getCuiResponseAttributes());
			data.setAdvancedCuiExpression(eapPolicyData.getAdvancedCuiExpression());
			
			if(data.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
				data.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
			
			session.update(data);
			
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}
		
	}
	
	private void saveEAPPlugins(String eapPolicyId,List<EAPPolicyPluginConfig> eapPolicyPluginConfigList,Session session){
		/* Save credit control pre-plugins and post-plugins*/
		if(eapPolicyPluginConfigList != null && eapPolicyPluginConfigList.isEmpty() == false){
			int orderNumber = 1;
			for (EAPPolicyPluginConfig eapPolicyPluginConfig : eapPolicyPluginConfigList) {
				eapPolicyPluginConfig.setEapPolicyId(eapPolicyId);
				eapPolicyPluginConfig.setOrderNumber(orderNumber++);
				session.save(eapPolicyPluginConfig);
			}
		}
	}

	@Override
	public List<EAPPolicyPluginConfig> getPolicyPluginConfigList(String policyId) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(EAPPolicyPluginConfig.class);
			List<EAPPolicyPluginConfig> data = criteria.add(Restrictions.eq(EAP_POLICY_ID, policyId)).addOrder(Order.asc("orderNumber")).list();
			return data;
		}catch(Exception e){
			throw new DatabaseConnectionException(e.getMessage(), e);
		}
	}
	
	@Override
	public void updateEAPServicePolicy(EAPPolicyData policyData, String policyName, IStaffData staffData) throws DataManagerException {
		try{
			Session session = getSession();

			Criteria criteria = session.createCriteria(EAPPolicyData.class);
			
			EAPPolicyData data = (EAPPolicyData)criteria.add(Restrictions.eq(EAP_POLICY_NAME, policyName)).uniqueResult();
			
			if (data == null) {
				throw new InvalidValueException("policy does not exist");
			}
			
			JSONArray jsonArray=ObjectDiffer.diff(data, policyData);
			
			data.setName(policyData.getName());
			data.setDescription(policyData.getDescription());
			data.setRuleSet(policyData.getRuleSet());
			data.setSessionManagement(policyData.getSessionManagement());
			data.setRequestType(policyData.getRequestType());
			data.setDefaultResponseBehaviorArgument(policyData.getDefaultResponseBehaviorArgument());
			data.setDefaultResponseBehaviour(policyData.getDefaultResponseBehaviour());
			data.setStatus(policyData.getStatus());
			
			if(data.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
				data.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
			
			data.setEapConfigId(policyData.getEapConfigId());
			data.setMultipleUserIdentity(policyData.getMultipleUserIdentity());
			data.setCaseSensitiveUserIdentity(policyData.getCaseSensitiveUserIdentity());
			data.setStripUserIdentity(policyData.getStripUserIdentity());
			data.setRealmSeparator(policyData.getRealmSeparator());
			data.setRealmPattern(policyData.getRealmPattern());
			data.setTrimUserIdentity(policyData.getTrimUserIdentity());
			data.setTrimPassword(policyData.getTrimPassword());
			data.setAnonymousProfileIdentity(policyData.getAnonymousProfileIdentity());
			
			data.setWimax(policyData.getWimax());
			data.setRejectOnCheckItemNotFound(policyData.getRejectOnCheckItemNotFound());
			data.setRejectOnRejectItemNotFound(policyData.getRejectOnRejectItemNotFound());
			data.setActionOnPolicyNotFound(policyData.getActionOnPolicyNotFound());
			data.setGracePolicy(policyData.getGracePolicy());
			data.setDiameterConcurrency(policyData.getDiameterConcurrency());
			data.setAdditionalDiameterConcurrency(policyData.getAdditionalDiameterConcurrency());
			data.setDefaultSessionTimeout(policyData.getDefaultSessionTimeout());
			
			Criteria driverCriteria = session.createCriteria(EAPPolicyAuthDriverRelationData.class);
			
			List<EAPPolicyAuthDriverRelationData> driveRelationalDataList = driverCriteria.add(Restrictions.eq("policyId", data.getEapPolicyId())).list();
			for(int i=0;i<driveRelationalDataList.size();i++){
				EAPPolicyAuthDriverRelationData driverReldt = driveRelationalDataList.get(i);
				session.delete(driverReldt);
				session.flush();
			}

			if(policyData.getDriverList() != null && policyData.getDriverList().size() > 0){				
				for(int i=0;i<policyData.getDriverList().size();i++){					
					driverCriteria = session.createCriteria(EAPPolicyAuthDriverRelationData.class);
					EAPPolicyAuthDriverRelationData relData = policyData.getDriverList().get(i);
					relData.setPolicyId(data.getEapPolicyId());
					session.save(relData);
					session.flush();
				}
			}
			
			/* Additional Driver */
			driverCriteria = session.createCriteria(EAPPolicyAdditionalDriverRelData.class);
			List<EAPPolicyAdditionalDriverRelData> eapAdditionalDriverRelDataList = driverCriteria.add(Restrictions.eq(EAP_POLICY_ID, data.getEapPolicyId())).list();
			for(EAPPolicyAdditionalDriverRelData eapPolicyAdditionalDriverRelData : eapAdditionalDriverRelDataList) {
				session.delete(eapPolicyAdditionalDriverRelData);
				session.flush();
			}
			if(policyData.getEapAdditionalDriverRelDataList() != null) {
				List<EAPPolicyAdditionalDriverRelData> eapAdditionalDriverRelDatas = policyData.getEapAdditionalDriverRelDataList();
				for(EAPPolicyAdditionalDriverRelData eapPolicyAdditionalDriverRelData : eapAdditionalDriverRelDatas) {
					eapPolicyAdditionalDriverRelData.setEapPolicyId(data.getEapPolicyId());
					session.save(eapPolicyAdditionalDriverRelData);
					session.flush();
				}
			}
			
			data.setScript(policyData.getScript());
			
			//Update plugins
			List<EAPPolicyPluginConfig> eapPolicyPluginConfigList = getPolicyPluginConfigList(data.getEapPolicyId());
			if(!eapPolicyPluginConfigList.isEmpty()){
				deleteObjectList(eapPolicyPluginConfigList,session);
			}

			saveEAPPlugins(data.getEapPolicyId(), policyData.getEapPolicyPluginConfigList(), session);
			
			Criteria reponseAttributeCriteria = session.createCriteria(EAPResponseAttributes.class);
			List<EAPResponseAttributes> eapResponseAttributeList = reponseAttributeCriteria.add(Restrictions.eq("eapPolicyId", data.getEapPolicyId())).list();
			for(int i=0;i<eapResponseAttributeList.size();i++){
				EAPResponseAttributes eapResponseAttributes = eapResponseAttributeList.get(i);
				session.delete(eapResponseAttributes);
				session.flush();
			}
			
			Set<EAPResponseAttributes> eapResponseAttributes = policyData.getEapResponseAttributesSet();

			//Save data
			if(eapResponseAttributes != null && eapResponseAttributes.isEmpty() == false){
				int orderNumber = 1;
				for(Iterator<EAPResponseAttributes> iteratorForDummyResp = eapResponseAttributes.iterator();iteratorForDummyResp.hasNext();){
					EAPResponseAttributes eapResponseAttributesData = iteratorForDummyResp.next();
					eapResponseAttributesData.setEapPolicyId(data.getEapPolicyId());
					eapResponseAttributesData.setOrderNumber(orderNumber++);
					session.save(eapResponseAttributesData);
					session.flush();
				}
			}
			
			data.setCui(policyData.getCui());
			data.setCuiResponseAttributes(policyData.getCuiResponseAttributes());
			data.setAdvancedCuiExpression(policyData.getAdvancedCuiExpression());
			
			session.update(data);
			
			doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_DIAMETER_EAP_POLICY);
		}catch(ConstraintViolationException e){
			throw new DataManagerException("Failed update EAP Service Policy, Reason: " + EliteExceptionUtils.extractConstraintName(e.getSQLException()), e);
		}catch(HibernateException hExp){
			throw new DataManagerException("Failed update EAP Service Policy, Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException("Failed update EAP Service Policy, Reason: " + exp.getMessage(),exp);
		}
	}
	
}
