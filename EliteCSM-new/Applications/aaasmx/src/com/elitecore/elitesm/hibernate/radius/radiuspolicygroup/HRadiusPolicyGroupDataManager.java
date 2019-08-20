package com.elitecore.elitesm.hibernate.radius.radiuspolicygroup;

import java.util.List;

import net.sf.json.JSONArray;

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
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.radiuspolicygroup.RadiusPolicyGroupDataManager;
import com.elitecore.elitesm.datamanager.radius.radiuspolicygroup.data.RadiusPolicyGroup;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;


public class HRadiusPolicyGroupDataManager extends HBaseDataManager implements RadiusPolicyGroupDataManager {

	private static final String POLICY_NAME = "policyName";
	private static final String POLICY_ID = "policyId";
	private static final String MODULE = "HRadiusPolicyGroupDataManager";
	

	public boolean verifyRadiusPolicyGroupName(String policyId, String policyName)throws DataManagerException{

		boolean isPolicyName=false;
		List radiusPolicyGroupList = null;

		try{

			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusPolicyGroup.class);
			criteria.add(Restrictions.eq(POLICY_NAME,policyName.trim()));
			criteria.add(Restrictions.ne(POLICY_ID,policyId));              
			radiusPolicyGroupList = criteria.list();
			
			if (Collectionz.isNullOrEmpty(radiusPolicyGroupList) == false) {
				isPolicyName=true;
			}
			
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive " + policyName + ", Reason : " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive " + policyName + ", Reason : " + e.getMessage(), e);
		}

		return isPolicyName;
		
	}
	
	@Override
	public PageList search(RadiusPolicyGroup radiusPolicyGroup, int pageNo, int pageSize) throws DataManagerException {

		PageList pageList = null;

		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusPolicyGroup.class);

			if ((radiusPolicyGroup.getPolicyName() != null && radiusPolicyGroup.getPolicyName().length() > 0)) {
				criteria.add(Restrictions.ilike(POLICY_NAME, radiusPolicyGroup.getPolicyName()));
			}

			criteria.addOrder(Order.asc(POLICY_ID));

			int totalItems = criteria.list().size();
			criteria.setFirstResult(((pageNo - 1) * pageSize));

			if (pageSize > 0) {
				criteria.setMaxResults(pageSize);
			}

			List radiusPolicyGroupList = criteria.list();

			long totalPages = (long) Math.ceil(totalItems / pageSize);
			if (totalItems % pageSize == 0)
				totalPages -= 1;

			pageList = new PageList(radiusPolicyGroupList, pageNo, totalPages, totalItems);

		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive Radius Policy Group list, Reason : " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive Radius Policy Group list, Reason : " + e.getMessage(), e);
		}

		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return pageList;

	}
	

	@Override
	public RadiusPolicyGroup getRadiusPolicyGroupDataById(String radiusPolicyGroupId) throws DataManagerException {
		
		return getRadiusPolicyGroupData(POLICY_ID, radiusPolicyGroupId);
		
	}

	@Override
	public RadiusPolicyGroup getRadiusPolicyGroupDataByName(String radiusPolicyGroupName) throws DataManagerException {
		
		return getRadiusPolicyGroupData(POLICY_NAME, radiusPolicyGroupName);
	}

	private RadiusPolicyGroup getRadiusPolicyGroupData(String propertyName, Object value) throws DataManagerException {
		
		String radiusPolicyGroupName = (POLICY_NAME.equals(propertyName)) ? (String) value : "Radius Policy Group";
		
		try {
			
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(RadiusPolicyGroup.class).add(Restrictions.eq(propertyName, value));
			RadiusPolicyGroup radiusPolicyGroup = (RadiusPolicyGroup) criteria.uniqueResult();
			
			if (radiusPolicyGroup == null) {
				throw new InvalidValueException("Radius Policy Group not found");
			}
			
			radiusPolicyGroupName = radiusPolicyGroup.getPolicyName();
			
			return radiusPolicyGroup;
			
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive " + radiusPolicyGroupName + ", Reason : " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive " + radiusPolicyGroupName + ", Reason : " + e.getMessage(), e);
		}
		
	}

	
	@Override
	public String create(Object obj) throws DataManagerException {
		RadiusPolicyGroup radiusPolicyGroup = (RadiusPolicyGroup) obj;
		String radiusPolicyGroupName = radiusPolicyGroup.getPolicyName();
		
		try{
			
			Session session = getSession();
			session.clear();
			
			String auditId= UUIDGenerator.generate();
			
			radiusPolicyGroup.setAuditUId(auditId);
			
			session.save(radiusPolicyGroup);
			session.flush();
			session.clear();
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + radiusPolicyGroupName + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			Logger.logTrace(MODULE, he);
			throw new DataManagerException(FAILED_TO_CREATE + radiusPolicyGroupName + REASON + he.getMessage(), he);
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + radiusPolicyGroupName + REASON + e.getMessage(), e);
		}
		return radiusPolicyGroupName;
		
	}
	

	@Override
	public void updateRadiusPolicyGroupById(RadiusPolicyGroup radiusPolicyGroup, IStaffData staffData, String radiusPolicyGroupId) throws DataManagerException {
		
		updateRadiusPolicyGroup(radiusPolicyGroup, staffData, POLICY_ID, radiusPolicyGroupId);
		
	}

	@Override
	public void updateRadiusPolicyGroupByName(RadiusPolicyGroup radiusPolicyGroup, IStaffData staffData, String radiusPolicyGroupName) throws DataManagerException {
		
		updateRadiusPolicyGroup(radiusPolicyGroup, staffData, POLICY_NAME, radiusPolicyGroupName);

	}
	
	private void updateRadiusPolicyGroup(RadiusPolicyGroup radiusPolicyGroup, IStaffData staffData, String propertyName, Object value) throws DataManagerException {

		String radiusPolicyGroupName =  (POLICY_NAME.equals(propertyName)) ? (String)value : "Radius Policy Group";
		
		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(RadiusPolicyGroup.class).add(Restrictions.eq(propertyName, value));
			RadiusPolicyGroup radiusPolicyGroupData = (RadiusPolicyGroup) criteria.uniqueResult();
			
			if (radiusPolicyGroupData == null) {
				throw new InvalidValueException("Radius Policy Group not found");
			}
			
			radiusPolicyGroupName = radiusPolicyGroupData.getPolicyName();

			JSONArray jsonArray = ObjectDiffer.diff(radiusPolicyGroupData, radiusPolicyGroup);

			radiusPolicyGroupData.setPolicyName(radiusPolicyGroup.getPolicyName());
			radiusPolicyGroupData.setExpression(radiusPolicyGroup.getExpression());

			session.update(radiusPolicyGroupData);
			session.flush();
			
			staffData.setAuditId(radiusPolicyGroupData.getAuditUId());
			staffData.setAuditName(radiusPolicyGroupData.getPolicyName());

			doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_RADIUS_POLICY_GROUP);

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update " + radiusPolicyGroupName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to update " + radiusPolicyGroupName + ", Reason : " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update " + radiusPolicyGroupName + ", Reason : " + e.getMessage(), e);
		}
		
	}

	
	@Override
	public String deleteRadiusPolicyGroupById(String radiusPolicyGroupId) throws DataManagerException {
		
		return deleteRadiusPolicyGroup(POLICY_ID, radiusPolicyGroupId);
		
	}

	@Override
	public String deleteRadiusPolicyGroupByName(String radiusPolicyGroupName) throws DataManagerException {
		
		return deleteRadiusPolicyGroup(POLICY_NAME, radiusPolicyGroupName);
		
	}

	private String deleteRadiusPolicyGroup(String propertyName, Object value) throws DataManagerException {
		
		String radiusPolicyGroupName = (POLICY_NAME.equals(propertyName)) ? (String)value : "Radius Policy Group";

		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(RadiusPolicyGroup.class).add(Restrictions.eq(propertyName, value));
			RadiusPolicyGroup radiusPolicyGroup = (RadiusPolicyGroup) criteria.uniqueResult();

			if (radiusPolicyGroup == null) {
				throw new InvalidValueException("Radius Policy Group not found");
			}

			radiusPolicyGroupName = radiusPolicyGroup.getPolicyName();
			
			session.delete(radiusPolicyGroup);
			session.flush();
			
			return radiusPolicyGroupName;

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + radiusPolicyGroupName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to delete " + radiusPolicyGroupName + ", Reason : " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to delete " + radiusPolicyGroupName + ", Reason : " + e.getMessage(), e);
		}
		
	}

	
}
