package com.elitecore.elitesm.hibernate.servicepolicy.diameter.creditcontrolpolicy;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.DatabaseConnectionException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.CreditControlPolicyDataManager;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CCPolicyPluginConfig;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CCResponseAttributes;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CreditControlDriverRelationData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CreditControlPolicyData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

import net.sf.json.JSONArray;

public class HCreditControlPolicyDataManager extends HBaseDataManager implements  CreditControlPolicyDataManager{
	
	private static final String POLICY_ID = "policyId";
	private static final String NAME = "name";
	private static final String MODULE = "HCreditControlPolicyDataManager";

	@Override
	public String create(Object obj) throws DataManagerException {
		CreditControlPolicyData data = (CreditControlPolicyData) obj;
		try {
			Session session = getSession();
			session.clear();
			Criteria criteria = session.createCriteria(CreditControlPolicyData.class);
			criteria.setFetchSize(1);
			List<CreditControlPolicyData> ccDataList = criteria.addOrder(Order.desc("orderNumber")).list();

			if (Collectionz.isNullOrEmpty(ccDataList) == false) {
				data.setOrderNumber(ccDataList.get(0).getOrderNumber()+ 1);
			} else {
				data.setOrderNumber(1L);
			}

			String auditId= UUIDGenerator.generate();
			data.setAuditUId(auditId);
			session.save(data);
			
			String ccPolicyId = data.getPolicyId();
			
			List<CreditControlDriverRelationData> tempDriverList = data.getDriverList();
			
			criteria = session.createCriteria(CreditControlDriverRelationData.class);
			
			if (Collectionz.isNullOrEmpty(tempDriverList) == false) {
				for (int i=0; i<tempDriverList.size(); i++) {
					CreditControlDriverRelationData driverData = tempDriverList.get(i);
					driverData.setPolicyId(ccPolicyId);
					session.save(driverData);
				}
				session.flush();
				session.clear();
			}
			List<CCPolicyPluginConfig> cCPolicyPluginConfigList = data.getCcPolicyPluginConfigList();
			
			/* Save credit control pre-plugins and post-plugins*/
			int orderNumber = 1;
			if(cCPolicyPluginConfigList != null && cCPolicyPluginConfigList.isEmpty() == false){
				for (CCPolicyPluginConfig ccPolicyPluginConfig : cCPolicyPluginConfigList) {
					ccPolicyPluginConfig.setCcPolicyId(ccPolicyId);
					ccPolicyPluginConfig.setOrderNumber(orderNumber++);
					session.save(ccPolicyPluginConfig);
				}
			}
			
			if(data.getCcResponseAttributesSet() != null && data.getCcResponseAttributesSet().isEmpty() == false){
				orderNumber = 1;
				for(Iterator<CCResponseAttributes> iteratorPacketDetail = data.getCcResponseAttributesSet().iterator(); iteratorPacketDetail.hasNext(); ){
					CCResponseAttributes ccResponseAttribute = iteratorPacketDetail.next();
					ccResponseAttribute.setPolicyId(ccPolicyId);
					ccResponseAttribute.setOrderNumber(orderNumber++);
					session.save(ccResponseAttribute);
				}
				session.flush();
				session.clear();
			}
		} catch (ConstraintViolationException cve) {
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
		return data.getName();
	}
	
	public PageList searchCreditControlPolicy(CreditControlPolicyData creditControlPolicyData ,int pageNo, int pageSize) throws DataManagerException {

		Session session = getSession();
		Criteria criteria = session.createCriteria(CreditControlPolicyData.class);
		PageList pageList = null;

		try{
            
			if((creditControlPolicyData.getName() != null && creditControlPolicyData.getName().length()>0 )){
            	criteria.add(Restrictions.ilike(NAME,"%"+creditControlPolicyData.getName()+"%"));
            }

            if(!(creditControlPolicyData.getStatus().equalsIgnoreCase("All")) ){
            	
            	criteria.add(Restrictions.ilike("status",creditControlPolicyData.getStatus()));
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

	@Override
	public String deleteId(String policyIds) throws DataManagerException {
		return delete(policyIds, POLICY_ID);
	}
	
	@Override
	public String deleteByName(String policyName) throws DataManagerException {
		return delete(policyName, NAME);
	}

	private String delete(Object policiesToDelete, String property) throws DataManagerException {
		String policyName = (NAME.equals(property)) ? (String)policiesToDelete : "Credit Control Policy";
		Session session = getSession();
		try {
			Criteria criteria = session.createCriteria(CreditControlPolicyData.class);
			CreditControlPolicyData data = (CreditControlPolicyData)criteria.add(Restrictions.eq(property, policiesToDelete)).uniqueResult();

			if (data == null) {
				throw new InvalidValueException("policy does not exist");
			}
			criteria = session.createCriteria(CreditControlDriverRelationData.class);
			List<CreditControlDriverRelationData> ccPolicyDriverRelDataList = criteria.add(Restrictions.eq(POLICY_ID, data.getPolicyId())).list();	  
			deleteObjectList(ccPolicyDriverRelDataList, session);

			session.delete(data);
			session.flush();
			return data.getName();
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
	
	@Override
	public String updateById(CreditControlPolicyData policyData,IStaffData staffData)throws DataManagerException {
		return update(policyData, staffData, POLICY_ID, policyData.getPolicyId());
	}

	@Override
	public String updateByName(CreditControlPolicyData policyData,IStaffData staffData, String policyToUpdate) throws DataManagerException {
		return update(policyData, staffData, NAME, policyToUpdate);
	}
	
	private String update(CreditControlPolicyData policyData, IStaffData staffData, String columnName, Object policyToUpdate) throws DataManagerException {
		Session session = getSession();
		try {
			
			Criteria criteria = session.createCriteria(CreditControlPolicyData.class);
			
			CreditControlPolicyData data = (CreditControlPolicyData)criteria.add(Restrictions.eq(columnName, policyToUpdate)).uniqueResult();
			
			if (data == null) {
				throw new InvalidValueException("policy does not exist");
			}
			
			policyData.setCcPolicyDriverRelDataSet(new HashSet<CreditControlDriverRelationData>(policyData.getDriverList()));
			
			JSONArray jsonArray=ObjectDiffer.diff(data,policyData);
			
			data.setDescription(policyData.getDescription());
			data.setName(policyData.getName());
			data.setRuleSet(policyData.getRuleSet());
			data.setSessionManagement(policyData.getSessionManagement());
			data.setScript(policyData.getScript());
			data.setDefaultResponseBehaviorArgument(policyData.getDefaultResponseBehaviorArgument());
			data.setDefaultResponseBehaviour(policyData.getDefaultResponseBehaviour());
			data.setStatus(policyData.getStatus());
			
			session.update(data);
			session.flush();
			
			criteria = session.createCriteria(CreditControlDriverRelationData.class);
			List<CreditControlDriverRelationData> driveRelationalDataList = criteria.add(Restrictions.eq(POLICY_ID, data.getPolicyId())).list();
			deleteObjectList(driveRelationalDataList, session);
			
			if(policyData.getDriverList() != null && policyData.getDriverList().size() > 0) {
				criteria = session.createCriteria(CreditControlDriverRelationData.class);
				for (int i = 0; i < policyData.getDriverList().size(); i++) {
					CreditControlDriverRelationData relData = policyData.getDriverList().get(i);
					relData.setPolicyId(data.getPolicyId());
					session.save(relData);
					session.flush();
				}
			}
			
			criteria = session.createCriteria(CCResponseAttributes.class);
			List<CCResponseAttributes> oldParamList = criteria.add(Restrictions.eq(POLICY_ID, data.getPolicyId())).list();
			
			deleteObjectList(oldParamList, session);
			
			Set<CCResponseAttributes> ccResponseAttributes = policyData.getCcResponseAttributesSet();
			int orderNumber = 1;
			if (Collectionz.isNullOrEmpty(ccResponseAttributes) == false) {
				Iterator<CCResponseAttributes> iteratorForDummyResp = ccResponseAttributes.iterator();
				while (iteratorForDummyResp.hasNext()) {
					CCResponseAttributes ccResponseAttributesData = iteratorForDummyResp.next();
					ccResponseAttributesData.setPolicyId(data.getPolicyId());
					ccResponseAttributesData.setOrderNumber(orderNumber++);
					session.save(ccResponseAttributesData);
					session.flush();
				}
			}
			
			List<CCPolicyPluginConfig> cCPolicyPluginConfigList = getPolicyPluginConfigList(data.getPolicyId());
			deleteObjectList(cCPolicyPluginConfigList,session);
			cCPolicyPluginConfigList = policyData.getCcPolicyPluginConfigList();	
			orderNumber = 1;
			if(Collectionz.isNullOrEmpty(cCPolicyPluginConfigList) == false){
				for (CCPolicyPluginConfig ccPolicyPluginConfig : cCPolicyPluginConfigList) {
					ccPolicyPluginConfig.setCcPolicyId(data.getPolicyId());
					ccPolicyPluginConfig.setOrderNumber(orderNumber++);
					session.save(ccPolicyPluginConfig);
				}
			}
			
			doAuditingJson(jsonArray.toString(),staffData, ConfigConstant.UPDATE_CREDIT_CONTROL_SERVICE_POLICY);
			return policyData.getName();
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
	
	public void changePolicyOrder(String[] order) throws DataManagerException {		
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(CreditControlPolicyData.class);
			List<CreditControlPolicyData> policyList = criteria.add(Restrictions.eq("status", "CST01")).list();
			if(order != null){
				for(int i=0;i<order.length;i++){
					String name = order[i];
					int size = policyList.size();
					for (int j=0 ; j < size; j++) {
						CreditControlPolicyData tempPolicyData = policyList.get(j);
						if(tempPolicyData.getName().equals(name)){
							tempPolicyData.setOrderNumber(i+1L);
							session.update(tempPolicyData);
							break;
						}
					}
				}
			}
			
		}catch(HibernateException he){
			he.printStackTrace();
			throw new DataManagerException("Failed to change the policy order, reason: " + he.getMessage(), he);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to change the policy order, reason: " + e.getMessage(), e);
		}
	}
	
	public List getCreditControlPolicies() throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(CreditControlPolicyData.class).addOrder(Order.asc("orderNumber"));
			return criteria.add(Restrictions.eq("status","CST01")).list();
			
		}catch(HibernateException e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}	
	}
	
	public List getCreditControlPolicyList() throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(CreditControlPolicyData.class).addOrder(Order.asc("orderNumber"));
			return criteria.list();
			
		}catch(HibernateException e){
			throw new DataManagerException("Failed to retrieve policy(ies), reasons: " + e.getMessage(), e);
		}	
	}
	
	public void updateStatus(List<String> creditControlPolicyIds,String status) throws DataManagerException {

		try {
			String creditControlPolicyId = null;
			Session session = getSession();
			Criteria criteria = null;

			if(Collectionz.isNullOrEmpty(creditControlPolicyIds) == false) {
				for(int i=0;i<creditControlPolicyIds.size();i++){
					creditControlPolicyId = creditControlPolicyIds.get(i);
					criteria = session.createCriteria(CreditControlPolicyData.class);
					CreditControlPolicyData creditControlPolicyData = (CreditControlPolicyData)criteria.add(Restrictions.eq(POLICY_ID,creditControlPolicyId)).uniqueResult();
					if(status.equals(BaseConstant.SHOW_STATUS_ID)){
						if(creditControlPolicyData.getStatus().equals(BaseConstant.HIDE_STATUS_ID)){
							long orderNumber = creditControlPolicyData.getOrderNumber();
							Criteria newCriteria = session.createCriteria(CreditControlPolicyData.class); 
							newCriteria.add(Restrictions.eq("status","CST01")).add(Restrictions.eq("orderNumber",new Long(orderNumber))); 					
							List sameOrderNoList = newCriteria.list();
							if(sameOrderNoList != null && sameOrderNoList.size() >0){
								// set the order number to the last number
								criteria = session.createCriteria(CreditControlPolicyData.class);
								criteria.add(Restrictions.eq("status",BaseConstant.SHOW_STATUS_ID));
								List<CreditControlPolicyData> tempList = criteria.addOrder(Order.desc("orderNumber")).list();
								if(tempList != null){
									creditControlPolicyData.setOrderNumber(tempList.get(0).getOrderNumber() + 1);
								}
							}
						}				
					}
					creditControlPolicyData.setStatus(status);			
					session.update(creditControlPolicyData);			
					session.flush();
				}
			}
		} catch (HibernateException hex) {
			hex.printStackTrace();
			throw new DataManagerException("Failed to change the status of policy, reason: " + hex.getMessage(), hex);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new DataManagerException("Failed to change the status of policy, reason: " + ex.getMessage(), ex);
		}
	}

	@Override
	public List<CCPolicyPluginConfig> getPolicyPluginConfigList(String policyId) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(CCPolicyPluginConfig.class);
			List<CCPolicyPluginConfig> data = criteria.add(Restrictions.eq("ccPolicyId", policyId)).addOrder(Order.asc("orderNumber")).list();
			return data;
		}catch(Exception e){
			e.printStackTrace();
			throw new DatabaseConnectionException("Failed to retrieve plugin list for policy, reason: " + e.getMessage(), e);
		}
	}
	
	@Override
	public CreditControlPolicyData getCcPolicyDataByPolicyId(String policyId)throws DataManagerException {
		return getPolicyDataUsing(POLICY_ID, policyId);
	}

	@Override
	public CreditControlPolicyData getCcPolicyDataByPolicyName(String policyName) throws DataManagerException {
		return getPolicyDataUsing(NAME, policyName);
	}
	
	private CreditControlPolicyData getPolicyDataUsing(String columnName, Object nameOrIdValue) throws DataManagerException {
		String policyName = (NAME.equals(columnName)) ? (String)nameOrIdValue : "Credit Control Policy";
		Criteria criteria = getSession().createCriteria(CreditControlPolicyData.class);
		
		try {
			CreditControlPolicyData policyData = (CreditControlPolicyData) criteria.add(Restrictions.eq(columnName, nameOrIdValue)).uniqueResult();
			criteria = getSession().createCriteria(CreditControlDriverRelationData.class);
			
			if (policyData == null) {
				throw new InvalidValueException("policy does not exist");
			}
			
			List<CreditControlDriverRelationData> relationalDataList = criteria.add(Restrictions.eq(POLICY_ID,policyData.getPolicyId())).list();
			policyData.setDriverList(relationalDataList);
			return policyData;
			
		} catch(DataManagerException exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrieve " + policyName +", Reason: " + exp.getMessage(), exp);
		} catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrieve " + policyName +", Reason: " + exp.getMessage(), exp);
		}
	}
}