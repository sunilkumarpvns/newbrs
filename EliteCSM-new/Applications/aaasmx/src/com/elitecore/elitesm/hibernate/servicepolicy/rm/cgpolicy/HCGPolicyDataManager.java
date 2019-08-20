package com.elitecore.elitesm.hibernate.servicepolicy.rm.cgpolicy;

import java.util.List;

import net.sf.json.JSONArray;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.CGPolicyDataManager;
import com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyDriverRelationData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

public class HCGPolicyDataManager extends HBaseDataManager implements CGPolicyDataManager{

	private static final String CG_POLICY_NAME = "name";
	private static final String CG_POLICY_ID = "policyId";
	private static final String MODULE = "HCGPolicyDataManager";
	
	@Override
	public String create(Object obj) throws DataManagerException {
		CGPolicyData cgPolicyData = (CGPolicyData) obj;
		try{
			org.hibernate.Session session = getSession();
			session.clear();
			Criteria criteria = session.createCriteria(CGPolicyData.class);

			String auditId= UUIDGenerator.generate();
			
			cgPolicyData.setAuditUId(auditId);
			
			criteria = session.createCriteria(CGPolicyData.class);
			criteria.setFetchSize(1);
			List<CGPolicyData> cgPolicyDataList = criteria.addOrder(Order.desc("orderNumber")).list();

			if(cgPolicyDataList != null && cgPolicyDataList.size() > 0){
				cgPolicyData.setOrderNumber(cgPolicyDataList.get(0).getOrderNumber()+ 1);
			}else{
				cgPolicyData.setOrderNumber(1L);
			}
			session.save(cgPolicyData);

			String cgPolicyId = cgPolicyData.getPolicyId();
			List<CGPolicyDriverRelationData> cgPolicyDriverRelationList = cgPolicyData.getDriverList();
			criteria = session.createCriteria(CGPolicyDriverRelationData.class);
			if(cgPolicyDriverRelationList != null && cgPolicyDriverRelationList.size() >0){
				for(int i=0;i<cgPolicyDriverRelationList.size();i++){
					CGPolicyDriverRelationData driverData = cgPolicyDriverRelationList.get(i);
					driverData.setPolicyId(cgPolicyId);
					session.save(driverData);
				}	
			}
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + cgPolicyData.getName() + REASON 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + cgPolicyData.getName() + REASON + e.getMessage(), e);
		} catch (Exception exp) {
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(FAILED_TO_CREATE + cgPolicyData.getName() + REASON + exp.getMessage(), exp);
		}
		return cgPolicyData.getName();
	}

	@Override
	public String deleteById(String policyIds) throws DataManagerException {
		return delete(policyIds, CG_POLICY_ID);
	}
	
	public String deleteByName(String policyName) throws DataManagerException {
		return delete(policyName, CG_POLICY_NAME);
	}
	
	private String delete(Object policiesToDelete, String property) throws DataManagerException {
		String policyName = (CG_POLICY_NAME.equals(property)) ? (String)policiesToDelete : "Charging Service Policy";
		Session session = getSession();
		try {

			Criteria criteria = session.createCriteria(CGPolicyData.class);

			CGPolicyData cgPolicy = (CGPolicyData) criteria.add(Restrictions.eq(property, policiesToDelete)).uniqueResult();

			if (cgPolicy == null) {
				throw new InvalidValueException("policy does not exist");
			}

			Criteria cgDriverRelcriteria = session.createCriteria(CGPolicyDriverRelationData.class);
			List<CGPolicyDriverRelationData> cgPolicyDriverRelList = cgDriverRelcriteria.add(Restrictions.eq(CG_POLICY_ID, cgPolicy.getPolicyId())).list();
			deleteObjectList(cgPolicyDriverRelList, session);

			session.delete(cgPolicy);
			return cgPolicy.getName();
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

	public PageList searchCGPolicy(CGPolicyData cgPolicyData ,int pageNo, int pageSize) throws DataManagerException {
		Session session = getSession();
		Criteria criteria = session.createCriteria(CGPolicyData.class);
		PageList pageList = null;

		try{

			if((cgPolicyData.getName() != null && cgPolicyData.getName().length()>0 )){
				criteria.add(Restrictions.ilike(CG_POLICY_NAME,"%"+cgPolicyData.getName()+"%"));
			}

			if(!(cgPolicyData.getStatus().equalsIgnoreCase("All")) ){            	
				criteria.add(Restrictions.ilike("status",cgPolicyData.getStatus()));
			}            

			int totalItems = criteria.list().size();
			criteria.setFirstResult(((pageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}
			criteria.addOrder(Order.asc("orderNumber"));
			List<CGPolicyData> policyList = criteria.list();
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
	public String updateById(CGPolicyData policyData, IStaffData staffData, String policyToUpdate) throws DataManagerException {
		return update(policyData, staffData, CG_POLICY_ID, policyToUpdate);
	}

	@Override
	public String updateByName(CGPolicyData policyData,IStaffData staffData, String policyToUpdate) throws DataManagerException {
		return update(policyData, staffData, CG_POLICY_NAME, policyToUpdate);
	}
	
	private String update(CGPolicyData policyData, IStaffData staffData, String property, Object policyToUpdate) throws DataManagerException {
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(CGPolicyData.class);
			
			CGPolicyData data = (CGPolicyData)criteria.add(Restrictions.eq(property, policyToUpdate)).uniqueResult();
			
			if (data == null) {
				throw new InvalidValueException("policy doest not exist");
			}

			JSONArray jsonArray=ObjectDiffer.diff(data, policyData);
			
			data.setDescription(policyData.getDescription());
			data.setName(policyData.getName());
			data.setStatus(policyData.getStatus());
			data.setRuleSet(policyData.getRuleSet());
			data.setScript(policyData.getScript());
			
			if(data.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
				data.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
			
			session.update(data);
			session.flush();

			criteria = session.createCriteria(CGPolicyDriverRelationData.class);
			List<CGPolicyDriverRelationData> driveRelationalDataList = criteria.add(Restrictions.eq(CG_POLICY_ID,data.getPolicyId())).list();
			for(int i=0;i<driveRelationalDataList.size();i++){
				CGPolicyDriverRelationData driverReldt = driveRelationalDataList.get(i);
				session.delete(driverReldt);
				session.flush();
			}

			if(policyData.getDriverList() != null && policyData.getDriverList().size() > 0){				
				for(int i=0;i<policyData.getDriverList().size();i++){					
					criteria = session.createCriteria(CGPolicyDriverRelationData.class);
					CGPolicyDriverRelationData cgPolicyrelData = policyData.getDriverList().get(i);
					cgPolicyrelData.setPolicyId(data.getPolicyId());
					session.save(cgPolicyrelData);
					session.flush();
				}
			}
			staffData.setAuditId(data.getAuditUId());
	        staffData.setAuditName(data.getName());
			doAuditingJson(jsonArray.toString(),staffData, ConfigConstant.UPDATE_CG_POLICY);
			return policyData.getName();
		} catch(ConstraintViolationException e){
			throw new DataManagerException("Failed update Charging Service Policy, Reason: " + EliteExceptionUtils.extractConstraintName(e.getSQLException()), e);
		} catch(HibernateException hExp){
			throw new DataManagerException("Failed update Charging Service Policy, Reason: " +hExp.getMessage(), hExp);
		} catch(Exception exp){
			throw new DataManagerException("Failed update Charging Service Policy, Reason: " + exp.getMessage(),exp);
		}
	}

	public CGPolicyData getCGPolicyDataById(String cgPolicyId) throws DataManagerException {
		return getCGPolicy(CG_POLICY_ID, cgPolicyId);
	}
	
	public CGPolicyData getCGPolicyDataByName(String policyName) throws DataManagerException {
		return getCGPolicy(CG_POLICY_NAME, policyName);
	}

	private CGPolicyData getCGPolicy(String columnName, Object nameOrIdValue) throws DataManagerException {
		String policyName = (CG_POLICY_NAME.equals(columnName)) ? (String)nameOrIdValue : "Charging Service Policy";
		try{
			org.hibernate.Session session = getSession();
			Criteria criteria = session.createCriteria(CGPolicyData.class);
			CGPolicyData data = (CGPolicyData)criteria.add(Restrictions.eq(columnName, nameOrIdValue)).uniqueResult();

			if (data == null) {
				throw new InvalidValueException("policy does not exist");
			}
			criteria = session.createCriteria(CGPolicyDriverRelationData.class);
			List<CGPolicyDriverRelationData> relationalDataList = criteria.add(Restrictions.eq(CG_POLICY_ID, data.getPolicyId())).list();
			data.setDriverList(relationalDataList);
			return data;

		} catch (HibernateException hExp) {
			throw new DataManagerException("Failed to retrieve " + policyName + ", Reason: " + hExp.getMessage(), hExp);
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
			Criteria criteria = session.createCriteria(CGPolicyData.class);
			List<CGPolicyData> policyList = criteria.add(Restrictions.eq("status", "CST01")).list();
			if(order != null){
				for(int i=0;i<order.length;i++){
					String name = order[i];
					for(int j=0;j<policyList.size();j++){
						CGPolicyData tempPolicyData = policyList.get(j);
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

	public List<CGPolicyData> getCGPolicies() throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(CGPolicyData.class).addOrder(Order.asc("orderNumber"));
			return criteria.add(Restrictions.eq("status","CST01")).list();
		}catch(HibernateException e){
			throw new DataManagerException(e.getMessage(),e);
		}
	}

	public void updateStatus(List<String> cgPolicyIds,String status) throws DataManagerException {
		String cgPolicyId = null;
		Session session = getSession();
		Criteria criteria = null;

		if(cgPolicyIds != null && cgPolicyIds.size() > 0) {
			for(int i=0;i<cgPolicyIds.size();i++){
				cgPolicyId = cgPolicyIds.get(i);
				criteria = session.createCriteria(CGPolicyData.class);
				CGPolicyData cgPolicyData = (CGPolicyData)criteria.add(Restrictions.eq(CG_POLICY_ID,cgPolicyId)).uniqueResult();
				if(status.equals(BaseConstant.SHOW_STATUS_ID)){
					if(cgPolicyData.getStatus().equals(BaseConstant.HIDE_STATUS_ID)){
						long orderNumber = cgPolicyData.getOrderNumber();
						Criteria newCriteria = session.createCriteria(CGPolicyData.class); 
						newCriteria.add(Restrictions.eq("status","CST01")).add(Restrictions.eq("orderNumber",new Long(orderNumber))); 					
						List<CGPolicyData> sameOrderNoList = newCriteria.list();
						if(sameOrderNoList != null && sameOrderNoList.size() >0){
							// set the order number to the last number
							criteria = session.createCriteria(CGPolicyData.class);
							criteria.add(Restrictions.eq("status",BaseConstant.SHOW_STATUS_ID));
							List<CGPolicyData> tempList = criteria.addOrder(Order.desc("orderNumber")).list();
							if(tempList != null){
								cgPolicyData.setOrderNumber(tempList.get(0).getOrderNumber() + 1);
							}
						}
					}				
				}
				cgPolicyData.setStatus(status);			
				session.update(cgPolicyData);			
				session.flush();
			}
		}
	}
}
