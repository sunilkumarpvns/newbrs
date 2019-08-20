/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   HGracePolicyDataManager.java                 		
 * ModualName GracePolicy    			      		
 * Created on 23 December, 2010
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.hibernate.servermgr.gracepolicy;
   
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.GracePolicyDataManager;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

import net.sf.json.JSONArray;
      
public class HGracePolicyDataManager extends HBaseDataManager implements GracePolicyDataManager{

	private static final String GRACE_POLICY_ID = "gracePolicyId";
	private static final String NAME = "name";
	private static final String MODULE = "HGracePolicyDataManager";

	
	public List<GracepolicyData> getGracePolicyList() throws DataManagerException {
		
		try{
			
			Session session = getSession();
			
			Criteria criteria =session.createCriteria(GracepolicyData.class);
			
			List<GracepolicyData> gracePolicyList = criteria.list();
			
			return gracePolicyList;
			
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive Grace Policy List, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive Grace Policy List, Reason: " + e.getMessage(), e);
		}
	}
	

	@Override
	public GracepolicyData getGracePolicyById(String gracePolicyId) throws DataManagerException {
		
		return getGracePolicy(GRACE_POLICY_ID, gracePolicyId);
		
	}

	@Override
	public GracepolicyData getGracePolicyByName(String gracePolicyName) throws DataManagerException {
	
		return getGracePolicy(NAME, gracePolicyName);
		
	}

	private GracepolicyData getGracePolicy(String propertyName, Object value) throws DataManagerException {
		
		String gracePolicyName = (NAME.equals(propertyName)) ? (String) value : "Grace Policy";
		
		try {
			
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(GracepolicyData.class).add(Restrictions.eq(propertyName, value));
			GracepolicyData gracePolicy = (GracepolicyData) criteria.uniqueResult();
			
			if (gracePolicy == null) {
				throw new InvalidValueException("Grace Policy not found");
			}
			
			gracePolicyName = gracePolicy.getName();
			
			return gracePolicy;
			
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive " + gracePolicyName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive " + gracePolicyName + ", Reason: " + e.getMessage(), e);
		}
		
	}
	

	@Override
	public String create(Object obj) throws DataManagerException {
		GracepolicyData gracePolicy = (GracepolicyData) obj;
		String gracePolicyName = gracePolicy.getName();
		
		try {
			
			Session session = getSession();
			session.clear();
			session.save(gracePolicy);
			
			session.flush();
			session.clear();
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + gracePolicyName + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			Logger.logTrace(MODULE, he);
			throw new DataManagerException(FAILED_TO_CREATE + gracePolicyName + REASON + he.getMessage(), he);
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + gracePolicyName + REASON + e.getMessage(), e);
		}
		return gracePolicyName;
		
	}
	
	
	@Override
	public void updateGracePolicyById(GracepolicyData gracePolicy, String gracePolicyId, IStaffData staffData) throws DataManagerException {
		
		updateGracePolicy(gracePolicy, GRACE_POLICY_ID, gracePolicyId, staffData);
		
	}

	@Override
	public void updateGracePolicyByName(GracepolicyData gracePolicy, String gracePolicyName, IStaffData staffData) throws DataManagerException {
		
		updateGracePolicy(gracePolicy, NAME, gracePolicyName, staffData);
		
	}

	private void updateGracePolicy(GracepolicyData gracePolicy, String propertyName, Object value,IStaffData staffData) throws DataManagerException {

		String gracePolicyName = (NAME.equals(propertyName)) ? (String) value : "Grace Policy";

		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(GracepolicyData.class).add(Restrictions.eq(propertyName, value));
			GracepolicyData gracePolicyData = (GracepolicyData) criteria.uniqueResult();

			if (gracePolicyData == null) {
				throw new InvalidValueException("Grace Policy not found");
			}
			
			JSONArray jsonArray=ObjectDiffer.diff(gracePolicyData,gracePolicy);

			gracePolicyName = gracePolicyData.getName();
			
			gracePolicyData.setName(gracePolicy.getName());
			gracePolicyData.setValue(gracePolicy.getValue());
			
			session.update(gracePolicyData);
			session.flush();
			
			String auditId = getAuditId(ConfigConstant.GRACE_POLICY);
			staffData.setAuditId(auditId);
			staffData.setAuditName(gracePolicy.getName());
			
			doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_GRACE_POLICY);
			
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update " + gracePolicyName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to update " + gracePolicyName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update " + gracePolicyName + ", Reason: " + e.getMessage(), e);
		}
		
	}

	
	@Override
	public String deleteGracePolicyById(String gracePolicyId, IStaffData staffData) throws DataManagerException {
		
		return deleteGracePolicy(GRACE_POLICY_ID, gracePolicyId, staffData);

	}

	@Override
	public String deleteGracePolicyByName(String gracePolicyName, IStaffData staffData) throws DataManagerException {

		return deleteGracePolicy(NAME, gracePolicyName, staffData);

	}
	
	private String deleteGracePolicy(String propertyName, Object value, IStaffData staffData) throws DataManagerException {
		
		String gracePolicyName = (NAME.equals(propertyName)) ? (String) value : "Grace Policy";

		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(GracepolicyData.class).add(Restrictions.eq(propertyName, value));
			GracepolicyData gracePolicy = (GracepolicyData) criteria.uniqueResult();
			
			JSONArray jsonArray=ObjectDiffer.diff(gracePolicy,new GracepolicyData());
			if (gracePolicy == null) {
				throw new InvalidValueException("Grace Policy not found");
			}

			gracePolicyName = gracePolicy.getName();
			
			session.delete(gracePolicy);
			session.flush();
			
			String auditId = getAuditId(ConfigConstant.GRACE_POLICY);
			staffData.setAuditId(auditId);
			staffData.setAuditName(gracePolicyName);
			
			doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.DELETE_GRACE_POLICY);
			return gracePolicyName;

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + gracePolicyName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to delete " + gracePolicyName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to delete " + gracePolicyName + ", Reason: " + e.getMessage(), e);
		} 

	}

	
}
