package com.elitecore.elitesm.hibernate.servicepolicy.diameter.naspolicy;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.DiameterNASPolicyDataManager;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAcctDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAcctPluginConfig;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAdditionalDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAuthDriverRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAuthMethodRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAuthPluginConfig;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASResponseAttributes;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.PolicyPluginConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

import net.sf.json.JSONArray;

public class HDiameterNASPolicyDataManager extends HBaseDataManager implements DiameterNASPolicyDataManager {
	
	private static final String NAS_POLICY_NAME = "name";
	private static final String NAS_POLICY_ID = "nasPolicyId";
	private static final String MODULE="HDiameterNASPolicyDataManager";

	@Override
	public String create(Object obj) throws DataManagerException {
		NASPolicyInstData nasPolicyInstData = (NASPolicyInstData) obj;
		EliteAssert.notNull(nasPolicyInstData,"nasPolicyInstData must not be null.");
		try {
			Session session = getSession();
			session.clear();
			Criteria criteria = session.createCriteria(NASPolicyInstData.class).addOrder(Order.desc("orderNumber"));
			criteria.setFetchSize(1);
			List<NASPolicyInstData> list = criteria.list();
			nasPolicyInstData.setOrderNumber(1);
			if(list!=null && !list.isEmpty()){
				NASPolicyInstData data = list.get(0);
				nasPolicyInstData.setOrderNumber(data.getOrderNumber()+1);
			}
			
			String auditId= UUIDGenerator.generate();
			
			nasPolicyInstData.setAuditUId(auditId);
			
			session.save(nasPolicyInstData);
		//	session.flush();
			String nasPolicyId=nasPolicyInstData.getNasPolicyId();
			/* save nas  auth  drivers relation*/
			if(nasPolicyInstData.getNasPolicyAuthDriverRelList()!=null){
				
				List<NASPolicyAuthDriverRelData> nasAuthDriverRelList=nasPolicyInstData.getNasPolicyAuthDriverRelList();
				
				for (Iterator iterator = nasAuthDriverRelList.iterator(); iterator.hasNext();) {
					NASPolicyAuthDriverRelData policyAuthDriverRelData = (NASPolicyAuthDriverRelData) iterator.next();
					policyAuthDriverRelData.setNasPolicyId(nasPolicyId);
					session.save(policyAuthDriverRelData);
				//	session.flush();
				} 
				
			}
			
			/* save nas additional driverData */
			if(nasPolicyInstData.getNasPolicyAdditionalDriverRelDataList() != null) {
				List<NASPolicyAdditionalDriverRelData> nasPolicyAdditionalDriverRelDataList = nasPolicyInstData.getNasPolicyAdditionalDriverRelDataList();
				for(NASPolicyAdditionalDriverRelData nasPolicyAdditionalDriverRelData : nasPolicyAdditionalDriverRelDataList) {
					nasPolicyAdditionalDriverRelData.setNasPolicyId(nasPolicyId);
					session.save(nasPolicyAdditionalDriverRelData);
					//session.flush();
				}
			}
			
			
			/* save nas acct driver relation*/
			
            if(nasPolicyInstData.getNasPolicyAcctDriverRelList()!=null){
				
				List<NASPolicyAcctDriverRelData> nasAcctDriverRelList=nasPolicyInstData.getNasPolicyAcctDriverRelList();
				for (Iterator iterator = nasAcctDriverRelList.iterator(); iterator.hasNext();) {
					NASPolicyAcctDriverRelData policyAcctDriverRelData = (NASPolicyAcctDriverRelData) iterator.next();
					policyAcctDriverRelData.setNasPolicyId(nasPolicyId);
					session.save(policyAcctDriverRelData);
					//session.flush();
				} 
				
			}
			
			/* save auth method types */
			if(nasPolicyInstData.getNasPolicyAuthMethodRelList()!=null){
		         List<NASPolicyAuthMethodRelData> nasPolicyAuthMethodRelList=nasPolicyInstData.getNasPolicyAuthMethodRelList();		
				 
		         for (Iterator iterator = nasPolicyAuthMethodRelList.iterator(); iterator.hasNext();) {
					NASPolicyAuthMethodRelData policyAuthMethodRelData = (NASPolicyAuthMethodRelData) iterator.next();
					policyAuthMethodRelData.setNasPolicyId(nasPolicyId);
					session.save(policyAuthMethodRelData);
					//session.flush();
				}
			}
			
			/* save Response Attributes */
			int orderNumber = 1;
			if(nasPolicyInstData.getNasResponseAttributesSet() != null && nasPolicyInstData.getNasResponseAttributesSet().isEmpty() == false){
				for(Iterator<NASResponseAttributes> iteratorPacketDetail = nasPolicyInstData.getNasResponseAttributesSet().iterator(); iteratorPacketDetail.hasNext(); ){
					NASResponseAttributes nasResponseAttribute = iteratorPacketDetail.next();
					nasResponseAttribute.setNasPolicyId(nasPolicyId);
					nasResponseAttribute.setOrderNumber(orderNumber++);
					session.save(nasResponseAttribute);
					session.flush();
					session.clear();
				}
			}
			
			/* Save Auth Plugin Information */
			orderNumber = 1;
			if(nasPolicyInstData.getNasPolicyAuthPluginConfigList() != null && nasPolicyInstData.getNasPolicyAuthPluginConfigList().isEmpty() == false){
				for(Iterator<NASPolicyAuthPluginConfig> iteratorPacketDetail = nasPolicyInstData.getNasPolicyAuthPluginConfigList().iterator(); iteratorPacketDetail.hasNext(); ){
					NASPolicyAuthPluginConfig nasPolicyPluginConfig = iteratorPacketDetail.next();
					nasPolicyPluginConfig.setNasPolicyId(nasPolicyId);
					nasPolicyPluginConfig.setOrderNumber(orderNumber++);
					session.save(nasPolicyPluginConfig);
					session.flush();
					session.clear();
				}
			}
			
			/* Save Auth Plugin Information */
			orderNumber = 1;
			if(nasPolicyInstData.getNasPolicyAcctPluginConfigList() != null && nasPolicyInstData.getNasPolicyAcctPluginConfigList().isEmpty() == false){
				for(Iterator<NASPolicyAcctPluginConfig> iteratorPacketDetail = nasPolicyInstData.getNasPolicyAcctPluginConfigList().iterator(); iteratorPacketDetail.hasNext(); ){
					NASPolicyAcctPluginConfig nasPolicyPluginConfig = iteratorPacketDetail.next();
					nasPolicyPluginConfig.setNasPolicyId(nasPolicyId);
					nasPolicyPluginConfig.setOrderNumber(orderNumber++);
					session.save(nasPolicyPluginConfig);
					session.flush();
					session.clear();
				}
			}
			session.flush();
			session.clear();
			
			return nasPolicyInstData.getName();
			
		}catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + nasPolicyInstData.getName() + REASON 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + nasPolicyInstData.getName() + REASON + e.getMessage(), e);
		} catch (Exception exp) {
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(FAILED_TO_CREATE + nasPolicyInstData.getName() + REASON + exp.getMessage(), exp);
		}
	}
	
	public PageList search(NASPolicyInstData nasPolicyInstData ,int pageNo, int pageSize) throws DataManagerException {

		Session session = getSession();
		Criteria criteria = session.createCriteria(NASPolicyInstData.class);
		PageList pageList = null;

		try{
            
            if((nasPolicyInstData.getName() != null && nasPolicyInstData.getName().length()>0 )){
            	criteria.add(Restrictions.ilike(NAS_POLICY_NAME,"%"+nasPolicyInstData.getName()+"%"));
            }

            if(!(nasPolicyInstData.getStatus().equalsIgnoreCase("All")) ){
            	criteria.add(Restrictions.ilike("status",nasPolicyInstData.getStatus()));
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
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrieve: " + nasPolicyInstData.getName() + ", Reason: " + hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrieve: " + nasPolicyInstData.getName() + ", Reason: " + e.getMessage(),e);
		}

	}

	public String deleteById(String nasPolicyIds) throws DataManagerException {
		return delete(nasPolicyIds, NAS_POLICY_ID);
	}

	public String deleteByName(String nasPolicyName) throws DataManagerException {
		return delete(nasPolicyName, NAS_POLICY_NAME);
	}
	
	private String delete(Object policiesToDelete, String property) throws DataManagerException {
		String policyName = (NAS_POLICY_NAME.equals(property)) ? (String)policiesToDelete : "Nas Service Policy";
		try{
			Session session = getSession();

			Criteria criteria = session.createCriteria(NASPolicyInstData.class);
			NASPolicyInstData policyData = (NASPolicyInstData)criteria.add(Restrictions.eq(property, policiesToDelete)).uniqueResult();

			if (policyData == null) {
				throw new InvalidValueException("policy does not exist");
			}

			Criteria authDrivercriteria = session.createCriteria(NASPolicyAuthDriverRelData.class);
			List<NASPolicyAuthDriverRelData> nasPolicyAuthDriverRelDataList = authDrivercriteria.add(Restrictions.eq(NAS_POLICY_ID, policyData.getNasPolicyId())).list();	  
			deleteObjectList(nasPolicyAuthDriverRelDataList, session);

			Criteria acctDrivercriteria = session.createCriteria(NASPolicyAcctDriverRelData.class);
			List<NASPolicyAcctDriverRelData> nasPolicyAcctDriverRelDataList = acctDrivercriteria.add(Restrictions.eq(NAS_POLICY_ID, policyData.getNasPolicyId())).list();	  
			deleteObjectList(nasPolicyAcctDriverRelDataList,session);


			Criteria authMethodcriteria = session.createCriteria(NASPolicyAuthMethodRelData.class);
			List<NASPolicyAuthMethodRelData> authMethodRelDataList=authMethodcriteria.add(Restrictions.eq(NAS_POLICY_ID,policyData.getNasPolicyId())).list();
			deleteObjectList(authMethodRelDataList, session);

			Criteria additionalMethodcriteria = session.createCriteria(NASPolicyAdditionalDriverRelData.class);
			List<NASPolicyAdditionalDriverRelData> nasPolicyAdditionalDriverRelDataList=additionalMethodcriteria.add(Restrictions.eq(NAS_POLICY_ID,policyData.getNasPolicyId())).list();
			deleteObjectList(nasPolicyAdditionalDriverRelDataList, session);

			// from policy
			session.delete(policyData);
			session.flush();
			return policyData.getName();
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
	
	public void updateStatus(List<String> nasPolicyIds,String status) throws DataManagerException {

		String nasPolicyId = null;
		Session session = getSession();
		Criteria criteria = null;

		for(int i=0;i<nasPolicyIds.size();i++){
			nasPolicyId =nasPolicyIds.get(i);
			criteria = session.createCriteria(NASPolicyInstData.class);
			NASPolicyInstData nasInstData = (NASPolicyInstData)criteria.add(Restrictions.eq(NAS_POLICY_ID,nasPolicyId)).uniqueResult();
			if(status.equals(BaseConstant.SHOW_STATUS_ID)){
				if(nasInstData.getStatus().equals(BaseConstant.HIDE_STATUS_ID)){
					Criteria newCriteria = session.createCriteria(NASPolicyInstData.class); 
					newCriteria.add(Restrictions.eq("status","CST01")).add(Restrictions.eq("orderNumber",nasInstData.getOrderNumber())); 					
					List sameOrderNoList = newCriteria.list();
					if(sameOrderNoList != null && sameOrderNoList.size() >0){
						// set the order number to the last number
						criteria = session.createCriteria(NASPolicyInstData.class);
						criteria.add(Restrictions.eq("status",BaseConstant.SHOW_STATUS_ID));
						List<NASPolicyInstData> tempList = criteria.addOrder(Order.desc("orderNumber")).list();
						if(tempList != null){
							nasInstData.setOrderNumber(tempList.get(0).getOrderNumber() + 1);
						}
					}
				}				
			}
			nasInstData.setStatus(status);			
			session.update(nasInstData);			
			session.flush();

		}

	}

	public List<NASPolicyInstData> searchActiveNASServicePolicy() throws DataManagerException {
		
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(NASPolicyInstData.class).addOrder(Order.asc("orderNumber"));
			return criteria.add(Restrictions.eq("status","CST01")).list();
			
		}catch(HibernateException e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrieve active nas service policy, Reason: " + e.getMessage(),e);
		}
		
	}
	
	public List<NASPolicyInstData> getNASServicePolicyList() throws DataManagerException {
		
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(NASPolicyInstData.class).addOrder(Order.asc("orderNumber"));
			return criteria.list();
			
		}catch(HibernateException e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrieve active nas service policy, Reason: " + e.getMessage(),e);
		}
		
	}

	public NASPolicyInstData getDiameterServicePolicyByPolicyId(String servicePolicyId)throws DataManagerException {
		return getPolicyData(NAS_POLICY_ID, servicePolicyId);
	}

	public NASPolicyInstData getDiameterServicePolicyByName(String servicePolicyId)throws DataManagerException {
		return getPolicyData(NAS_POLICY_NAME, servicePolicyId);
	}

	private NASPolicyInstData getPolicyData(String columnName, Object policyToGet) throws DataManagerException {
		
		String policyName = (NAS_POLICY_NAME.equals(columnName)) ? (String)policyToGet : "Nas Service Policy";
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(NASPolicyInstData.class);
			NASPolicyInstData policyData = (NASPolicyInstData)criteria.add(Restrictions.eq(columnName, policyToGet)).uniqueResult();
			if (policyData == null) {
				throw new InvalidValueException("policy does not exist");
			}
			return policyData;
		} catch(DataManagerException exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrieve " + policyName +", Reason: " + exp.getMessage(), exp);
		} catch(HibernateException he){
			he.printStackTrace();
			throw new DataManagerException("Failed to retrieve " + policyName +", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrieve " + policyName +", Reason: " + e.getMessage(), e);
		}
	}

	public void updateNasPolicyBasicDetails(NASPolicyInstData policyData,IStaffData staffData,String actionAlias)throws DataManagerException {
		
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(NASPolicyInstData.class);
			NASPolicyInstData actualPolicyData = (NASPolicyInstData)criteria.add(Restrictions.eq(NAS_POLICY_ID,policyData.getNasPolicyId())).uniqueResult();
		
			if (actualPolicyData == null) {
				throw new DataManagerException("Policy does not exist");
			}
			
			JSONArray jsonArray=ObjectDiffer.diff(actualPolicyData, policyData);
			
			actualPolicyData.setName(policyData.getName());
			actualPolicyData.setDescription(policyData.getDescription());
			actualPolicyData.setRuleSet(policyData.getRuleSet());
			actualPolicyData.setSessionManagement(policyData.getSessionManagement());
			actualPolicyData.setRequestType(policyData.getRequestType());
			actualPolicyData.setDefaultResponseBehaviourArgument(policyData.getDefaultResponseBehaviourArgument());
			actualPolicyData.setDefaultResponseBehaviour(policyData.getDefaultResponseBehaviour());
			if(actualPolicyData.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
				actualPolicyData.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
			
			session.update(actualPolicyData);
			session.flush();
			
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update " + policyData.getName() + ", Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException he){
			he.printStackTrace();
			throw new DataManagerException("Failed update: " + policyData.getName() + 
					", Reason: " + he.getMessage(),he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed update: " + policyData.getName() + 
					", Reason: " + e.getMessage(),e);
		}
	}

	public List<NASPolicyAcctDriverRelData> getDiameterAcctDriverRel(String nasPolicyId)throws DataManagerException {
		
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(NASPolicyAcctDriverRelData.class);
			List<NASPolicyAcctDriverRelData> data = criteria.add(Restrictions.eq(NAS_POLICY_ID, nasPolicyId)).list();
			return data;
		}catch(HibernateException e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}
		
	}

	public List<NASPolicyAuthDriverRelData> getDiameterAuthDriverRel(String nasPolicyId)throws DataManagerException {
		// TODO Auto-generated method stub
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(NASPolicyAuthDriverRelData.class);
			List<NASPolicyAuthDriverRelData> data = criteria.add(Restrictions.eq(NAS_POLICY_ID, nasPolicyId)).list();
			return data;
		}catch(HibernateException e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}		
	}

	public List<NASPolicyAdditionalDriverRelData>  getDiameterAdditionalDriverRel(String nasPolicyId) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(NASPolicyAdditionalDriverRelData.class);
			criteria.add(Restrictions.eq(NAS_POLICY_ID, nasPolicyId))
					.setFetchMode("driverInstanceData", FetchMode.JOIN)
					.addOrder(Order.asc("orderNumber"));
			List<NASPolicyAdditionalDriverRelData> data = criteria.list();												 
			return data;
		}catch(HibernateException e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}
	}
	
	public List<NASPolicyAuthMethodRelData> getDiameterAuthMethodRel(String nasPolicyId)throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(NASPolicyAuthMethodRelData.class);
			List<NASPolicyAuthMethodRelData> data = criteria.add(Restrictions.eq(NAS_POLICY_ID, nasPolicyId)).list();
			return data;
		}catch(HibernateException e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}		
	}


	public void updateAuthenticationParams(NASPolicyInstData data,IStaffData staffData,String actionAlias)throws DataManagerException {
		
		try{

			Session session = getSession();
			Criteria criteria = session.createCriteria(NASPolicyInstData.class);
			NASPolicyInstData actualData = (NASPolicyInstData)criteria.add(Restrictions.eq(NAS_POLICY_ID, data.getNasPolicyId())).uniqueResult();
			actualData.setFlowType(PolicyPluginConstants.AUTH_FLOW);
			
			JSONArray jsonArray=ObjectDiffer.diff(actualData, data);
			
			actualData.setCaseSensitiveUserIdentity(data.getCaseSensitiveUserIdentity());
			actualData.setMultipleUserIdentity(data.getMultipleUserIdentity());
			actualData.setRealmPattern(data.getRealmPattern());
			actualData.setRealmSeparator(data.getRealmSeparator());
			actualData.setStripUserIdentity(data.getStripUserIdentity().toString());
			actualData.setTrimPassword(data.getTrimPassword().toString());
			actualData.setTrimUserIdentity(data.getTrimUserIdentity().toString());
			actualData.setUserName(data.getUserName());
			actualData.setUserNameResonseAttributes(data.getUserNameResonseAttributes());
			actualData.setAuthScript(data.getAuthScript());
			actualData.setAnonymousProfileIdentity(data.getAnonymousProfileIdentity());
			
			if(actualData.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
				actualData.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
			
			session.update(actualData);
			session.flush();
			
			List<NASPolicyAuthMethodRelData> methodList = data.getNasPolicyAuthMethodRelList();
			if(methodList != null && methodList.size() > 0){
				
				for(int i=0;i<methodList.size();i++){				
					NASPolicyAuthMethodRelData methodRelData = methodList.get(i);
					criteria = session.createCriteria(NASPolicyAuthMethodRelData.class);
					List<NASPolicyAuthMethodRelData> methodDataList = criteria.add(Restrictions.eq(NAS_POLICY_ID, data.getNasPolicyId())).list();
					
					if(methodDataList != null && methodDataList.size() > 0){
						for(int j=0;j<methodDataList.size();j++){
							NASPolicyAuthMethodRelData actualMethodRelData = methodDataList.get(j);						
							session.delete(actualMethodRelData);
							session.flush();
						}
					}
				}
				for(int i=0;i<methodList.size();i++){				
					NASPolicyAuthMethodRelData methodRelData = methodList.get(i);
					criteria = session.createCriteria(NASPolicyAuthMethodRelData.class);				
					session.save(methodRelData);
					session.flush();				
				}
				
				
			}else{
				criteria = session.createCriteria(NASPolicyAuthMethodRelData.class);
				List<NASPolicyAuthMethodRelData> methodDataList = criteria.add(Restrictions.eq(NAS_POLICY_ID, data.getNasPolicyId())).list();
				
				for(int i=0;i<methodDataList.size();i++){
					NASPolicyAuthMethodRelData data1 = methodDataList.get(i);
					session.delete(data1);
					session.flush();
				}
			}
			
			// drivers related 
			
			List<NASPolicyAuthDriverRelData> driverRelDataList = data.getNasPolicyAuthDriverRelList();
			if(driverRelDataList != null && driverRelDataList.size() > 0){			
					criteria = session.createCriteria(NASPolicyAuthDriverRelData.class);
					List<NASPolicyAuthDriverRelData> actualDriverList = criteria.add(Restrictions.eq(NAS_POLICY_ID, data.getNasPolicyId())).list();
					
					for(int j=0;j<actualDriverList.size();j++){
						
						NASPolicyAuthDriverRelData actualDriverData = actualDriverList.get(j);					
						session.delete(actualDriverData);
						session.flush();
					}
				for(int i=0;i<driverRelDataList.size();i++){
						
						NASPolicyAuthDriverRelData driverRelData = driverRelDataList.get(i);
						criteria = session.createCriteria(NASPolicyAuthDriverRelData.class);
					
					driverRelData.setNasPolicyId(data.getNasPolicyId());
					session.save(driverRelData);
					session.flush();	
				}
			}else{
				criteria = session.createCriteria(NASPolicyAuthDriverRelData.class);
				List<NASPolicyAuthDriverRelData> driverlst = criteria.add(Restrictions.eq(NAS_POLICY_ID, data.getNasPolicyId())).list();
				
				for(int i=0;i<driverlst.size();i++){
					NASPolicyAuthDriverRelData data1 = driverlst.get(i);
					session.delete(data1);
					session.flush();
				}
			}
			
			
			// Additional Driver Data
			Criteria additionalDriverCriteria = session.createCriteria(NASPolicyAdditionalDriverRelData.class);
			List<NASPolicyAdditionalDriverRelData> nasPolicyAdditionalDriverRelDataList = additionalDriverCriteria.add(Restrictions.eq(NAS_POLICY_ID,data.getNasPolicyId())).list();
			if(additionalDriverCriteria != null && !nasPolicyAdditionalDriverRelDataList.isEmpty()){
				deleteObjectList(nasPolicyAdditionalDriverRelDataList,session);
			}
			
			List<NASPolicyAdditionalDriverRelData> additionalDriverRelDataList = data.getNasPolicyAdditionalDriverRelDataList();
			if(additionalDriverRelDataList != null) {
				for(NASPolicyAdditionalDriverRelData nasPolicyAdditionalDriverRelData : additionalDriverRelDataList) {
					nasPolicyAdditionalDriverRelData.setNasPolicyId(data.getNasPolicyId());
					session.save(nasPolicyAdditionalDriverRelData);
				}
			}
			
			//Plugin Related Data
			Criteria pluginCriteria = session.createCriteria(NASPolicyAuthPluginConfig.class);
			
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.eq("pluginType", PolicyPluginConstants.IN_PLUGIN));
			disjunction.add(Restrictions.eq("pluginType", PolicyPluginConstants.OUT_PLUGIN));
			
			List<NASPolicyAuthPluginConfig> nasPolicyPluginConfigDataList = pluginCriteria.add(Restrictions.eq(NAS_POLICY_ID,data.getNasPolicyId())).add(disjunction).list();
			if(pluginCriteria != null && !nasPolicyPluginConfigDataList.isEmpty()){
				deleteObjectList(nasPolicyPluginConfigDataList,session);
			}
			
			List<NASPolicyAuthPluginConfig> nasPolicyPluginConfigList = data.getNasPolicyAuthPluginConfigList();
			int orderNumber = 1;
			if(nasPolicyPluginConfigList != null) {
				for(NASPolicyAuthPluginConfig nasPolicyPluginConfigData : nasPolicyPluginConfigList) {
					nasPolicyPluginConfigData.setNasPolicyId(data.getNasPolicyId());
					nasPolicyPluginConfigData.setOrderNumber(orderNumber++);
					session.save(nasPolicyPluginConfigData);
				}
			}
			
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed update: " + data.getName() + 
					", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed update: " + data.getName() + 
					", Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed update: " + data.getName() + 
					", Reason: " + exp.getMessage(), exp);
		}
	}

	
	@Override
	public void updateAuthorizationParams(NASPolicyInstData data,IStaffData staffData,String actionAlias)throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(NASPolicyInstData.class);
			NASPolicyInstData actualData = (NASPolicyInstData)criteria.add(Restrictions.eq(NAS_POLICY_ID,data.getNasPolicyId())).uniqueResult();
			
			JSONArray jsonArray=ObjectDiffer.diff(actualData, data);
			
			actualData.setRejectOnCheckItemNotFound(data.getRejectOnCheckItemNotFound());
			actualData.setRejectOnRejectItemNotFound(data.getRejectOnRejectItemNotFound());
			actualData.setActionOnPolicyNotFound(data.getActionOnPolicyNotFound());
			actualData.setWimax(data.getWimax());
			actualData.setGracePolicy(data.getGracePolicy());
			actualData.setDiameterConcurrency(data.getDiameterConcurrency());
			actualData.setAdditionalDiameterConcurrency(data.getAdditionalDiameterConcurrency());
			actualData.setDefaultSessionTimeout(data.getDefaultSessionTimeout());
			
			if(actualData.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
				actualData.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
			
			session.update(actualData);
			session.flush();
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		}catch(HibernateException e){
			e.printStackTrace();
			throw new DataManagerException("Failed update: " + data.getName() + 
					", Reason: " + e.getMessage(),e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed update: " + data.getName() + 
					", Reason: " + e.getMessage(),e);
		}
	}	
	
	public void updateAccountingParams(NASPolicyInstData data,IStaffData staffData,String actionAlias)throws DataManagerException {
		
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(NASPolicyInstData.class);
			NASPolicyInstData actualData = (NASPolicyInstData)criteria.add(Restrictions.eq(NAS_POLICY_ID,data.getNasPolicyId())).uniqueResult();
			actualData.setFlowType(PolicyPluginConstants.ACCT_FLOW);
			
			JSONArray jsonArray=ObjectDiffer.diff(actualData, data);
			
			actualData.setAcctScript(data.getAcctScript());
			
			if(actualData.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
				actualData.setAuditUId(auditId);
				//staffData.setAuditId(auditId);
			}
			
			session.update(actualData);
			session.flush();
			
			// drivers related 
			
			List<NASPolicyAcctDriverRelData> driverRelDataList = data.getNasPolicyAcctDriverRelList();
			if(driverRelDataList != null && driverRelDataList.size() > 0){			
					criteria = session.createCriteria(NASPolicyAcctDriverRelData.class);
					List<NASPolicyAcctDriverRelData> actualDriverList = criteria.add(Restrictions.eq(NAS_POLICY_ID, data.getNasPolicyId())).list();
					
					for(int j=0;j<actualDriverList.size();j++){
						
						NASPolicyAcctDriverRelData actualDriverData = actualDriverList.get(j);					
						session.delete(actualDriverData);
						session.flush();
					}
				for(int i=0;i<driverRelDataList.size();i++){
						
					NASPolicyAcctDriverRelData driverRelData = driverRelDataList.get(i);
						criteria = session.createCriteria(NASPolicyAcctDriverRelData.class);
					
					driverRelData.setNasPolicyId(data.getNasPolicyId());
					session.save(driverRelData);
					session.flush();	
				}
			}else{
				criteria = session.createCriteria(NASPolicyAcctDriverRelData.class);
				List<NASPolicyAcctDriverRelData> driverlst = criteria.add(Restrictions.eq(NAS_POLICY_ID, data.getNasPolicyId())).list();
				
				for(int i=0;i<driverlst.size();i++){
					NASPolicyAcctDriverRelData data1 = driverlst.get(i);
					session.delete(data1);
					session.flush();
				}
			}
			
			//Plugin Related Data
			Criteria pluginCriteria = session.createCriteria(NASPolicyAcctPluginConfig.class);
			
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.eq("pluginType", PolicyPluginConstants.IN_PLUGIN));
			disjunction.add(Restrictions.eq("pluginType", PolicyPluginConstants.OUT_PLUGIN));
			
			List<NASPolicyAcctPluginConfig> nasPolicyPluginConfigDataList = pluginCriteria.add(Restrictions.eq(NAS_POLICY_ID,data.getNasPolicyId())).add(disjunction).list();
			if(pluginCriteria != null && !nasPolicyPluginConfigDataList.isEmpty()){
				deleteObjectList(nasPolicyPluginConfigDataList,session);
			}
			
			List<NASPolicyAcctPluginConfig> nasPolicyPluginConfigList = data.getNasPolicyAcctPluginConfigList();
			int orderNumber = 1;
			if(nasPolicyPluginConfigList != null) {
				for(NASPolicyAcctPluginConfig nasPolicyPluginConfigData : nasPolicyPluginConfigList) {
					nasPolicyPluginConfigData.setNasPolicyId(data.getNasPolicyId());
					nasPolicyPluginConfigData.setOrderNumber(orderNumber++);
					session.save(nasPolicyPluginConfigData);
				}
			}
			
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		}catch(HibernateException e){
			e.printStackTrace();
			throw new DataManagerException("Failed update: " + data.getName() + 
					", Reason: " + e.getMessage(),e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed update: " + data.getName() + 
					", Reason: " + e.getMessage(),e);
		}
			
	}

	@Override
	public List<NASResponseAttributes> getResponseAttributes(String nasPolicyId) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(NASResponseAttributes.class);
			List<NASResponseAttributes> data = criteria.add(Restrictions.eq(NAS_POLICY_ID, nasPolicyId)).addOrder(Order.asc("orderNumber")).list();
			return data;
		}catch(HibernateException e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(e.getMessage(),e);
		}
	}

	@Override
	public void updateNasResponseAttribute(NASPolicyInstData policyInstData,IStaffData staffData, String actionAlias)throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(NASPolicyInstData.class);
			NASPolicyInstData actualData = (NASPolicyInstData)criteria.add(Restrictions.eq(NAS_POLICY_ID,policyInstData.getNasPolicyId())).uniqueResult();
			
			//Response Attribute related 
			
			JSONArray jsonArray=ObjectDiffer.diff(actualData,policyInstData);
			
			if(actualData.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
				actualData.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
			
			Set<NASResponseAttributes> nasResponseAttributes = policyInstData.getNasResponseAttributesSet();
			
			criteria = session.createCriteria(NASResponseAttributes.class);
			List<NASResponseAttributes> oldParamList = criteria.add(Restrictions.eq(NAS_POLICY_ID, actualData.getNasPolicyId())).list();
			if(oldParamList !=null && oldParamList.isEmpty() == false ){
				for(Iterator<NASResponseAttributes> dummyParamItrIterator = oldParamList.iterator(); dummyParamItrIterator.hasNext();){
					NASResponseAttributes dummyParam = dummyParamItrIterator.next();
					session.delete(dummyParam);
					session.flush();
				}
			}
			
			//Save data
			int orderNumber = 1;
			if(nasResponseAttributes != null && nasResponseAttributes.isEmpty() == false){
				for(Iterator<NASResponseAttributes> iteratorForDummyResp = nasResponseAttributes.iterator();iteratorForDummyResp.hasNext();){
					NASResponseAttributes nasResponseAttributesData = iteratorForDummyResp.next();
					nasResponseAttributesData.setNasPolicyId(actualData.getNasPolicyId());
					nasResponseAttributesData.setOrderNumber(orderNumber++);
					session.save(nasResponseAttributesData);
					session.flush();
				}
			}
			
			staffData.setAuditId(actualData.getAuditUId());
			staffData.setName(actualData.getName());
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		}catch(HibernateException e){
			e.printStackTrace();
			throw new DataManagerException("Failed update: " + policyInstData.getName() + 
					", Reason: " + e.getMessage(),e);
		}catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed update: " + policyInstData.getName() + 
					", Reason: " + e.getMessage(),e);
		}
		
	}

	@Override
	public void updateRFC4372CUIParams(NASPolicyInstData policyData, IStaffData staffData, String actionAlias) throws DataManagerException {
		try{
			// TODO Auto-generated method stubtry{
			Session session = getSession();
			Criteria criteria = session.createCriteria(NASPolicyInstData.class);
			NASPolicyInstData actualData = (NASPolicyInstData)criteria.add(Restrictions.eq(NAS_POLICY_ID,policyData.getNasPolicyId())).uniqueResult();
			
			JSONArray jsonArray=ObjectDiffer.diff(actualData, policyData);
			
			actualData.setCui(policyData.getCui());
			actualData.setCuiResponseAttributes(policyData.getCuiResponseAttributes());
			actualData.setAdvancedCuiExpression(policyData.getAdvancedCuiExpression());
			
			if(actualData.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
				actualData.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
			
			session.update(actualData);
			session.flush();
			
			staffData.setAuditId(actualData.getAuditUId());
			staffData.setName(actualData.getName());
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		}catch(HibernateException e){
			e.printStackTrace();
			throw new DataManagerException("Failed update: " + policyData.getName() + 
					", Reason: " + e.getMessage(),e);
		}catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed update: " + policyData.getName() + 
					", Reason: " + e.getMessage(),e);
		}
	}
	
	@Override
	public void updateByName(NASPolicyInstData policyData, String policyName, IStaffData staffData, String actionAlias) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(NASPolicyInstData.class);
			NASPolicyInstData actualPolicyData = (NASPolicyInstData)criteria.add(Restrictions.eq(NAS_POLICY_NAME, policyName)).uniqueResult();

			if (actualPolicyData == null) {
				throw new InvalidValueException("policy does not exist");
			}
			
			JSONArray jsonArray=ObjectDiffer.diff(actualPolicyData, policyData);

			actualPolicyData.setName(policyData.getName());
			actualPolicyData.setDescription(policyData.getDescription());
			actualPolicyData.setRuleSet(policyData.getRuleSet());
			actualPolicyData.setSessionManagement(policyData.getSessionManagement());
			actualPolicyData.setRequestType(policyData.getRequestType());
			actualPolicyData.setDefaultResponseBehaviourArgument(policyData.getDefaultResponseBehaviourArgument());
			actualPolicyData.setDefaultResponseBehaviour(policyData.getDefaultResponseBehaviour());
			actualPolicyData.setStatus(policyData.getStatus());

			// Authentication Details

			actualPolicyData.setFlowType(PolicyPluginConstants.AUTH_FLOW);

			actualPolicyData.setCaseSensitiveUserIdentity(policyData.getCaseSensitiveUserIdentity());
			actualPolicyData.setMultipleUserIdentity(policyData.getMultipleUserIdentity());
			actualPolicyData.setRealmPattern(policyData.getRealmPattern());
			actualPolicyData.setRealmSeparator(policyData.getRealmSeparator());
			actualPolicyData.setStripUserIdentity(policyData.getStripUserIdentity().toString());
			actualPolicyData.setTrimPassword(policyData.getTrimPassword().toString());
			actualPolicyData.setTrimUserIdentity(policyData.getTrimUserIdentity().toString());
			actualPolicyData.setUserName(policyData.getUserName());
			actualPolicyData.setUserNameResonseAttributes(policyData.getUserNameResonseAttributes());
			actualPolicyData.setAuthScript(policyData.getAuthScript());
			actualPolicyData.setAnonymousProfileIdentity(policyData.getAnonymousProfileIdentity());

			if(actualPolicyData.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
				actualPolicyData.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}

			List<NASPolicyAuthMethodRelData> methodList = policyData.getNasPolicyAuthMethodRelList();
			if(Collectionz.isNullOrEmpty(methodList) == false){
				int size = methodList.size();

				for(int i=0;i<size;i++){				
					criteria = session.createCriteria(NASPolicyAuthMethodRelData.class);
					List<NASPolicyAuthMethodRelData> methodDataList = criteria.add(Restrictions.eq(NAS_POLICY_ID, actualPolicyData.getNasPolicyId())).list();
					if(Collectionz.isNullOrEmpty(methodDataList) == false){
						int authMethodListSize = methodDataList.size();
						for(int j=0;j<authMethodListSize;j++){
							NASPolicyAuthMethodRelData actualMethodRelData = methodDataList.get(j);						
							session.delete(actualMethodRelData);
							session.flush();
						}
					}
				}
				
				for(int i=0;i<size;i++){				
					NASPolicyAuthMethodRelData methodRelData = methodList.get(i);
					methodRelData.setNasPolicyId(actualPolicyData.getNasPolicyId());
					criteria = session.createCriteria(NASPolicyAuthMethodRelData.class);				
					session.save(methodRelData);
				}


			}else{
				criteria = session.createCriteria(NASPolicyAuthMethodRelData.class);
				List<NASPolicyAuthMethodRelData> methodDataList = criteria.add(Restrictions.eq(NAS_POLICY_ID, actualPolicyData.getNasPolicyId())).list();
				if (Collectionz.isNullOrEmpty(methodDataList) == false) {
					int size = methodDataList.size();
					for(int i=0;i<size;i++){
						NASPolicyAuthMethodRelData data1 = methodDataList.get(i);
						session.delete(data1);
						session.flush();
					}
				}
			}

			List<NASPolicyAuthDriverRelData> driverRelDataList = policyData.getNasPolicyAuthDriverRelList();
			if(Collectionz.isNullOrEmpty(driverRelDataList) == false){			
				criteria = session.createCriteria(NASPolicyAuthDriverRelData.class);

				List<NASPolicyAuthDriverRelData> actualDriverList = criteria.add(Restrictions.eq(NAS_POLICY_ID, actualPolicyData.getNasPolicyId())).list();
				if (Collectionz.isNullOrEmpty(actualDriverList) == false) {
					for(int j=0;j<actualDriverList.size();j++){
						NASPolicyAuthDriverRelData actualDriverData = actualDriverList.get(j);					
						session.delete(actualDriverData);
						session.flush();
					}
				}
				int size = driverRelDataList.size();
				for(int i=0;i<size;i++){

					NASPolicyAuthDriverRelData driverRelData = driverRelDataList.get(i);
					criteria = session.createCriteria(NASPolicyAuthDriverRelData.class);
					driverRelData.setNasPolicyId(actualPolicyData.getNasPolicyId());
					session.save(driverRelData);
					session.flush();	
				}
			}else{
				criteria = session.createCriteria(NASPolicyAuthDriverRelData.class);
				List<NASPolicyAuthDriverRelData> driverlst = criteria.add(Restrictions.eq(NAS_POLICY_ID, actualPolicyData.getNasPolicyId())).list();

				if (Collectionz.isNullOrEmpty(driverlst) == false) {
					int size = driverlst.size();
					for(int i=0;i<size;i++){
						NASPolicyAuthDriverRelData data1 = driverlst.get(i);
						session.delete(data1);
						session.flush();
					}
				}
			}


			Criteria additionalDriverCriteria = session.createCriteria(NASPolicyAdditionalDriverRelData.class);
			List<NASPolicyAdditionalDriverRelData> nasPolicyAdditionalDriverRelDataList = additionalDriverCriteria.add(Restrictions.eq(NAS_POLICY_ID,actualPolicyData.getNasPolicyId())).list();
			if(Collectionz.isNullOrEmpty(nasPolicyAdditionalDriverRelDataList) == false){
				deleteObjectList(nasPolicyAdditionalDriverRelDataList,session);
			}

			List<NASPolicyAdditionalDriverRelData> additionalDriverRelDataList = policyData.getNasPolicyAdditionalDriverRelDataList();
			if(Collectionz.isNullOrEmpty(additionalDriverRelDataList) == false) {
				for(NASPolicyAdditionalDriverRelData nasPolicyAdditionalDriverRelData : additionalDriverRelDataList) {
					nasPolicyAdditionalDriverRelData.setNasPolicyId(actualPolicyData.getNasPolicyId());
					session.save(nasPolicyAdditionalDriverRelData);
				}
			}

			Criteria pluginCriteria = session.createCriteria(NASPolicyAuthPluginConfig.class);

			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.eq("pluginType", PolicyPluginConstants.IN_PLUGIN));
			disjunction.add(Restrictions.eq("pluginType", PolicyPluginConstants.OUT_PLUGIN));

			List<NASPolicyAuthPluginConfig> nasPolicyPluginConfigDataList = pluginCriteria.add(Restrictions.eq(NAS_POLICY_ID,actualPolicyData.getNasPolicyId())).add(disjunction).list();
			if(Collectionz.isNullOrEmpty(nasPolicyPluginConfigDataList) == false){
				deleteObjectList(nasPolicyPluginConfigDataList,session);
			}

			List<NASPolicyAuthPluginConfig> nasPolicyPluginConfigList = policyData.getNasPolicyAuthPluginConfigList();
			int orderNumber = 1;
			if(Collectionz.isNullOrEmpty(nasPolicyPluginConfigList) == false) {
				for(NASPolicyAuthPluginConfig nasPolicyPluginConfigData : nasPolicyPluginConfigList) {
					nasPolicyPluginConfigData.setNasPolicyId(actualPolicyData.getNasPolicyId());
					nasPolicyPluginConfigData.setOrderNumber(orderNumber++);
					session.save(nasPolicyPluginConfigData);
				}
			}

			// Authorization 
			
			actualPolicyData.setRejectOnCheckItemNotFound(policyData.getRejectOnCheckItemNotFound());
			actualPolicyData.setRejectOnRejectItemNotFound(policyData.getRejectOnRejectItemNotFound());
			actualPolicyData.setActionOnPolicyNotFound(policyData.getActionOnPolicyNotFound());
			actualPolicyData.setWimax(policyData.getWimax());
			actualPolicyData.setGracePolicy(policyData.getGracePolicy());
			actualPolicyData.setDiameterConcurrency(policyData.getDiameterConcurrency());
			actualPolicyData.setAdditionalDiameterConcurrency(policyData.getAdditionalDiameterConcurrency());
			actualPolicyData.setDefaultSessionTimeout(policyData.getDefaultSessionTimeout());

			List<NASPolicyAcctDriverRelData> acctDriverRelDataList = policyData.getNasPolicyAcctDriverRelList();
			if(Collectionz.isNullOrEmpty(acctDriverRelDataList) == false){			
				criteria = session.createCriteria(NASPolicyAcctDriverRelData.class);
				List<NASPolicyAcctDriverRelData> actualDriverList = criteria.add(Restrictions.eq(NAS_POLICY_ID, actualPolicyData.getNasPolicyId())).list();

				for(int j=0;j<actualDriverList.size();j++){

					NASPolicyAcctDriverRelData actualDriverData = actualDriverList.get(j);					
					session.delete(actualDriverData);
					session.flush();
				}
				for(int i=0;i<acctDriverRelDataList.size();i++){

					NASPolicyAcctDriverRelData driverRelData = acctDriverRelDataList.get(i);
					criteria = session.createCriteria(NASPolicyAcctDriverRelData.class);

					driverRelData.setNasPolicyId(actualPolicyData.getNasPolicyId());
					session.save(driverRelData);
					session.flush();	
				}
			}else{
				criteria = session.createCriteria(NASPolicyAcctDriverRelData.class);
				List<NASPolicyAcctDriverRelData> driverlst = criteria.add(Restrictions.eq(NAS_POLICY_ID, actualPolicyData.getNasPolicyId())).list();

				if (Collectionz.isNullOrEmpty(driverlst) == false) {

					for(int i=0;i<driverlst.size();i++){
						NASPolicyAcctDriverRelData data1 = driverlst.get(i);
						session.delete(data1);
						session.flush();
					}
				}
			}

			// Accouting

			actualPolicyData.setFlowType(PolicyPluginConstants.ACCT_FLOW);
			actualPolicyData.setAcctScript(policyData.getAcctScript());

			pluginCriteria = session.createCriteria(NASPolicyAcctPluginConfig.class);

			disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.eq("pluginType", PolicyPluginConstants.IN_PLUGIN));
			disjunction.add(Restrictions.eq("pluginType", PolicyPluginConstants.OUT_PLUGIN));

			List<NASPolicyAcctPluginConfig> nasAcctPrePluginConfigDataList = pluginCriteria.add(Restrictions.eq(NAS_POLICY_ID, actualPolicyData.getNasPolicyId())).add(disjunction).list();
			if(Collectionz.isNullOrEmpty(nasAcctPrePluginConfigDataList) == false){
				deleteObjectList(nasAcctPrePluginConfigDataList,session);
			}

			List<NASPolicyAcctPluginConfig> nasAcctPostPluginConfigList = policyData.getNasPolicyAcctPluginConfigList();
			orderNumber = 1;
			if(Collectionz.isNullOrEmpty(nasAcctPostPluginConfigList) == false) {
				for(NASPolicyAcctPluginConfig nasPolicyPluginConfigData : nasAcctPostPluginConfigList) {
					nasPolicyPluginConfigData.setNasPolicyId(actualPolicyData.getNasPolicyId());
					nasPolicyPluginConfigData.setOrderNumber(orderNumber++);
					session.save(nasPolicyPluginConfigData);
				}
			}

			// Response Attribute
			criteria = session.createCriteria(NASResponseAttributes.class);
			List<NASResponseAttributes> oldParamList = criteria.add(Restrictions.eq(NAS_POLICY_ID, actualPolicyData.getNasPolicyId())).list();
			if(Collectionz.isNullOrEmpty(oldParamList) == false){
				for(Iterator<NASResponseAttributes> dummyParamItrIterator = oldParamList.iterator(); dummyParamItrIterator.hasNext();){
					NASResponseAttributes dummyParam = dummyParamItrIterator.next();
					session.delete(dummyParam);
					session.flush();
				}
			}

			Set<NASResponseAttributes> nasResponseAttributes = policyData.getNasResponseAttributesSet();
			orderNumber = 1;
			if(Collectionz.isNullOrEmpty(nasResponseAttributes) == false){
				for(Iterator<NASResponseAttributes> iteratorForDummyResp = nasResponseAttributes.iterator();iteratorForDummyResp.hasNext();){
					NASResponseAttributes nasResponseAttributesData = iteratorForDummyResp.next();
					nasResponseAttributesData.setNasPolicyId(actualPolicyData.getNasPolicyId());
					nasResponseAttributesData.setOrderNumber(orderNumber++);
					session.save(nasResponseAttributesData);
					session.flush();
				}
			}

			actualPolicyData.setCui(policyData.getCui());
			actualPolicyData.setCuiResponseAttributes(policyData.getCuiResponseAttributes());
			actualPolicyData.setAdvancedCuiExpression(policyData.getAdvancedCuiExpression());
			
			session.update(actualPolicyData);
			session.flush();

			staffData.setAuditId(policyData.getAuditUId());
			staffData.setName(policyData.getName());
			doAuditingJson(jsonArray.toString(),staffData, ConfigConstant.UPDATE_NAS_SERVICE_POLICY);

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed update: " + policyData.getName() + 
					", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed update: " + policyData.getName() + 
					", Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed update: " + policyData.getName() + 
					", Reason: " + exp.getMessage(), exp);
		}
	}
}
