package com.elitecore.elitesm.hibernate.diameter.diameterpolicygroup;

import java.util.List;

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
import com.elitecore.elitesm.datamanager.diameter.diameterpolicygroup.DiameterPolicyGroupDataManager;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicygroup.data.DiameterPolicyGroup;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

import net.sf.json.JSONArray;


public class HDiameterPolicyGroupDataManager extends HBaseDataManager implements DiameterPolicyGroupDataManager {

	private static final String POLICY_NAME = "policyName";
	private static final String POLICY_ID = "policyId";
	private static final String MODULE = "HDiameterPolicyGroupDataManager";
	
	public PageList search(DiameterPolicyGroup diameterPolicyGroup, int pageNo, int pageSize) throws DataManagerException {
		PageList pageList = null;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterPolicyGroup.class);

			if((diameterPolicyGroup.getPolicyName() != null && diameterPolicyGroup.getPolicyName().length()>0 )){
				criteria.add(Restrictions.ilike(POLICY_NAME,diameterPolicyGroup.getPolicyName()));
			}
			
			criteria.addOrder(Order.asc(POLICY_ID));

			int totalItems = criteria.list().size();
			criteria.setFirstResult(((pageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}

			List diameterPolicyGroupList = criteria.list();

			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(diameterPolicyGroupList, pageNo, totalPages ,totalItems);

		} catch (HibernateException hbe) {
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive Diameter Policy Group list, Reason : " + hbe.getMessage(), hbe);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive Diameter Policy Group list, Reason : " + e.getMessage(), e);
		}

		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return pageList;
	}
	
	@Override
	public String create(Object obj) throws DataManagerException{
		DiameterPolicyGroup diameterPolicyGroup = (DiameterPolicyGroup) obj;
		try{
			Session session = getSession();
			session.clear();
			
			String auditId= UUIDGenerator.generate();
			
			diameterPolicyGroup.setAuditUId(auditId);
			
			session.save(diameterPolicyGroup);
			session.flush();
			session.clear();
		
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + diameterPolicyGroup.getPolicyName() + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException hExp) {
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException(FAILED_TO_CREATE + diameterPolicyGroup.getPolicyName() + REASON + hExp.getMessage(), hExp);
		} catch (Exception exp) {
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(FAILED_TO_CREATE + diameterPolicyGroup.getPolicyName() + REASON + exp.getMessage(), exp);
		}
		return diameterPolicyGroup.getPolicyName();
	}
	
	
	public boolean verifyDiameterPolicyGroupName(String policyId, String policyName)throws DataManagerException{

		boolean isPolicyName=false;
		List diameterPolicyGroupList = null;

		try{

			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterPolicyGroup.class);
			criteria.add(Restrictions.eq(POLICY_NAME,policyName.trim()));
			criteria.add(Restrictions.ne(POLICY_ID,policyId));              
			diameterPolicyGroupList = criteria.list();
			if(diameterPolicyGroupList != null && diameterPolicyGroupList.size() > 0){
				isPolicyName=true;
			}

		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}

		return isPolicyName;
	}
	
	@Override
	public DiameterPolicyGroup getDiameterPolicyGroupDataById(String diameterPolicyGroupId) throws DataManagerException {
		return getDiameterPolicyGroupData(POLICY_ID, diameterPolicyGroupId);
	}

	@Override
	public DiameterPolicyGroup getDiameterPolicyGroupDataByName(String diameterPolicyGroupName) throws DataManagerException {
		return getDiameterPolicyGroupData(POLICY_NAME, diameterPolicyGroupName);
	}
	
	private DiameterPolicyGroup getDiameterPolicyGroupData(String propertyName, Object value) throws DataManagerException {
		String diameterPolicyGroupName = (POLICY_NAME.equals(propertyName)) ? (String) value : "Diameter Policy Group";
		
		try {
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(DiameterPolicyGroup.class).add(Restrictions.eq(propertyName, value));
			DiameterPolicyGroup diameterPolicyGroup = (DiameterPolicyGroup) criteria.uniqueResult();
	
			if (diameterPolicyGroup == null) {
				throw new InvalidValueException("Diameter Policy Group not found");
			}
			
			diameterPolicyGroupName = diameterPolicyGroup.getPolicyName();
	
			return diameterPolicyGroup;
		} catch (HibernateException e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive " + diameterPolicyGroupName + ", Reason : " + e.getMessage(), e);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive " + diameterPolicyGroupName + ", Reason : " + exp.getMessage(), exp);
		}
		
	}
	

	@Override
	public void updateDiameterPolicyGroupById(DiameterPolicyGroup diameterPolicyGroup, IStaffData staffData, String diameterPolicyGroupId) throws DataManagerException {
		updateDiameterPolicyGroup(diameterPolicyGroup, staffData, POLICY_ID, diameterPolicyGroupId);
		
	}

	@Override
	public void updateDiameterPolicyGroupByName(DiameterPolicyGroup diameterPolicyGroup, IStaffData staffData, String diameterPolicyGroupName) throws DataManagerException {
		updateDiameterPolicyGroup(diameterPolicyGroup, staffData, POLICY_NAME, diameterPolicyGroupName);
		
	}
	
	private void updateDiameterPolicyGroup(DiameterPolicyGroup diameterPolicyGroup, IStaffData staffData, String propertyName, Object value) throws DataManagerException {
		
		String diameterPolicyGroupName = (POLICY_NAME.equals(propertyName)) ? (String) value : "Diameter Policy Group";
		Session session = getSession();
			try {
				Criteria criteria = session.createCriteria(DiameterPolicyGroup.class).add(Restrictions.eq(propertyName, value));
				DiameterPolicyGroup diameterPolicyGroupData = (DiameterPolicyGroup) criteria.uniqueResult();
				
				if (diameterPolicyGroupData == null) {
					throw new InvalidValueException("Diameter Policy Group not found");
				}
				
				diameterPolicyGroupName = diameterPolicyGroupData.getPolicyName();
				
				JSONArray jsonArray = ObjectDiffer.diff(diameterPolicyGroupData, diameterPolicyGroup);

				diameterPolicyGroupData.setPolicyName(diameterPolicyGroup.getPolicyName());
				diameterPolicyGroupData.setExpression(diameterPolicyGroup.getExpression());

				session.update(diameterPolicyGroupData);
				session.flush();
				
				staffData.setAuditId(diameterPolicyGroupData.getAuditUId());
				staffData.setAuditName(diameterPolicyGroupData.getPolicyName());

				doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_DIAMETER_POLICY_GROUP);

			} catch (ConstraintViolationException cve) {
				cve.printStackTrace();
				throw new DataManagerException("Failed to update " + diameterPolicyGroupName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
			} catch(HibernateException he){
				he.printStackTrace();
				throw new DataManagerException("Failed to update " + diameterPolicyGroupName + ", Reason : " + he.getMessage(), he);
			} catch (Exception exp) {
				exp.printStackTrace();
				throw new DataManagerException("Failed to update " + diameterPolicyGroupName + ", Reason : " + exp.getMessage(), exp);
			}
	}


	@Override
	public String deleteDiameterPolicyGroupById(String diameterPolicyGroupId) throws DataManagerException {
		return delete(POLICY_ID, diameterPolicyGroupId);
	}

	@Override
	public String deleteDiameterPolicyGroupByName(String diameterPolicyGroupName) throws DataManagerException {
		return delete(POLICY_NAME, diameterPolicyGroupName);
	}
	
	private String delete(String propertyName, Object value) throws DataManagerException {
		String diameterPolicyGroupName = (POLICY_NAME.equals(propertyName)) ? (String) value : "Diameter Policy Group";
	
		try {
			Session session = getSession();
	
			Criteria criteria = session.createCriteria(DiameterPolicyGroup.class).add(Restrictions.eq(propertyName, value));
			DiameterPolicyGroup diameterPolicyGroup = (DiameterPolicyGroup) criteria.uniqueResult();
			
			if (diameterPolicyGroup == null) {
				throw new InvalidValueException("Diameter Policy Group not found");
			}
			
			diameterPolicyGroupName = diameterPolicyGroup.getPolicyName();
			
			session.delete(diameterPolicyGroup);
			session.flush();
			
			return diameterPolicyGroupName;
	
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + diameterPolicyGroupName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to delete " + diameterPolicyGroupName + ", Reason : " + hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to delete " + diameterPolicyGroupName + ", Reason : " + exp.getMessage(), exp);
		}
	}
	

}
