/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   HDiameterpolicyDataManager.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.hibernate.diameter.diameterpolicy;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicy.DiameterPolicyDataManager;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyData;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyTimePeriod;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

import net.sf.json.JSONArray;

public class HDiameterPolicyDataManager extends HBaseDataManager implements DiameterPolicyDataManager{
	


	private static final String DIAMETER_POLICY = "diameter policy ";
	private static final String DIAMETER_POLICY_NAME = "name";
	private static final String DIAMETER_POLICY_ID = "diameterPolicyId";
	private static final String MODULE = "HDiameterPolicyDataManager";

	public String create(Object obj) throws DataManagerException {
		DiameterPolicyData diameterPolicyData = (DiameterPolicyData) obj;
		try{
			Session session = getSession();
			session.clear();
			Criteria criteria = null;
			criteria = session.createCriteria(DiameterPolicyData.class);
			
			//fetch next audit id
			String auditId= UUIDGenerator.generate();
			
			diameterPolicyData.setAuditUId(auditId);
			
			session.save(diameterPolicyData);
			session.flush();
			session.clear();
			
			
			// Save DiameterPolicyTimePeriodSet
			List<DiameterPolicyTimePeriod> diameterPolicyTimePeriodList = diameterPolicyData.getDiameterPolicyTimePeriodList();
			if(diameterPolicyTimePeriodList != null) {
				int orderNumber = 1;
				for(DiameterPolicyTimePeriod diameterPolicyTimePeriod : diameterPolicyTimePeriodList){
					diameterPolicyTimePeriod.setDiameterPolicyId(diameterPolicyData.getDiameterPolicyId());
					diameterPolicyTimePeriod.setOrderNumber(orderNumber++);
					session.save(diameterPolicyTimePeriod);
					session.flush();
					session.clear();
				}
			}
			
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE+DIAMETER_POLICY + diameterPolicyData.getName() + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException hExp) {
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException(FAILED_TO_CREATE+DIAMETER_POLICY + diameterPolicyData.getName() + REASON + hExp.getMessage(), hExp);
		} catch (Exception exp) {
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(FAILED_TO_CREATE+DIAMETER_POLICY + diameterPolicyData.getName() + REASON + exp.getMessage(), exp);
		}
		return diameterPolicyData.getName();
	}

	public PageList search(DiameterPolicyData diameterPolicyData, int pageNo, int pageSize) throws DataManagerException {
		PageList pageList = null;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterPolicyData.class)
			.createAlias("commonStatus","commonStatus");

			if((diameterPolicyData.getName() != null && diameterPolicyData.getName().length()>0 )){
				criteria.add(Restrictions.ilike(DIAMETER_POLICY_NAME,diameterPolicyData.getName()));
			}

			if(diameterPolicyData.getCommonStatusId() !=null ){
				criteria.add(Restrictions.ilike("commonStatusId",diameterPolicyData.getCommonStatusId()));
			}

			int totalItems = criteria.list().size();
			criteria.setFirstResult(((pageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}

			List<DiameterPolicyData> diameterPolicyList = criteria.list();


			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(diameterPolicyList, pageNo, totalPages ,totalItems);

		}catch(HibernateException hExp){
			throw new DataManagerException("Failed to search diameter policy, Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException("Failed to search diameter policy, Reason: " + exp.getMessage(), exp);
		}Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return pageList;
	}

	public String updateStatus(String diameterPolicyId, String commonStatus, Timestamp statusChangeDate) throws DataManagerException{
		Session session = getSession();			
		DiameterPolicyData diameterPolicyData = null;

		try{
			Criteria criteria = session.createCriteria(DiameterPolicyData.class);
			diameterPolicyData = (DiameterPolicyData)criteria.add(Restrictions.eq(DIAMETER_POLICY_ID,diameterPolicyId)).uniqueResult();
			diameterPolicyData.setCommonStatusId(commonStatus);
			diameterPolicyData.setStatusChangeDate(statusChangeDate);	
			diameterPolicyData.setLastModifiedDate(statusChangeDate);
			session.update(diameterPolicyData);
			String diameterPolicyName = diameterPolicyData.getName();
			return diameterPolicyName;
		}catch(HibernateException hExp){
			throw new DataManagerException("Failed to update status of diameter policy, Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException("Failed to update status of diameter policy, Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override
	public String deleteById(String diameterPolicyId) throws DataManagerException {
		return delete(DIAMETER_POLICY_ID, diameterPolicyId) ;
	}

	@Override
	public String deleteByName(String diameterPolicyIdOrName) throws DataManagerException {
		return delete(DIAMETER_POLICY_NAME, diameterPolicyIdOrName);
	}
	
	private String delete(String propertyName, Object propertyValue) throws DataManagerException {
		String diameterPolicyName = (DIAMETER_POLICY_NAME.equals(propertyName)) ? (String) propertyValue : "Diameter Policy";
		DiameterPolicyData diameterPolicyData = null;
		try {
			Session session = getSession();

			// Remove child DiameterPolicyTimePeriod before DiameterPolicyData
			Criteria criteria = session.createCriteria(DiameterPolicyData.class).add(Restrictions.eq(propertyName,propertyValue));
			diameterPolicyData  = (DiameterPolicyData) criteria.uniqueResult();
			
			if(diameterPolicyData == null){
				throw new InvalidValueException("Diameter Policy not found");
			}
			
			List<DiameterPolicyTimePeriod> diameterPolicyTimePeriodList = diameterPolicyData.getDiameterPolicyTimePeriodList();
			deleteObjectList(diameterPolicyTimePeriodList, session);
			session.delete(diameterPolicyData);
			session.flush();

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + diameterPolicyName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to delete " + diameterPolicyName + ", Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to delete " + diameterPolicyName + ", Reason: " + exp.getMessage(), exp);
		}
		
		return diameterPolicyData.getName();
	}
	
	public DiameterPolicyData getDiameterPolicyDataById(String diameterPolicyId) throws DataManagerException {
		return getDiameterPolicyData(diameterPolicyId, DIAMETER_POLICY_ID);			
	}
	
	@Override
	public DiameterPolicyData getDiameterPolicyDataByName(String diameterPolicyName) throws DataManagerException {
		return getDiameterPolicyData(diameterPolicyName, DIAMETER_POLICY_NAME);
	}
	
	private DiameterPolicyData getDiameterPolicyData(Object propertyValue, String propertyName) throws DataManagerException {

		String diameterPolicyName = (DIAMETER_POLICY_NAME.equals(propertyName)) ? (String) propertyValue : "Diameter Policy";
		DiameterPolicyData diameterPolicyData = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterPolicyData.class).add(Restrictions.eq(propertyName, propertyValue));
			diameterPolicyData = (DiameterPolicyData) criteria.uniqueResult();

			if (diameterPolicyData == null) {
				throw new InvalidValueException("Diameter Policy not found");
			}

		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retive "+ diameterPolicyName + ", Reason: "+ hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retive "+ diameterPolicyName + ", Reason: "+ exp.getMessage(), exp);
		}
		return diameterPolicyData;
	}
	
	private void update(DiameterPolicyData diameterPolicyData, IStaffData staffData, String propertyName, Object porpertyValue) throws DataManagerException {

		Session session = getSession();	
		verifyPolicyNameForUpdate(diameterPolicyData);
		try{

			Criteria criteria = session.createCriteria(DiameterPolicyData.class).add(Restrictions.eq(propertyName,porpertyValue));
			DiameterPolicyData diameterPolicyDataDetails = (DiameterPolicyData) criteria.uniqueResult();

			if(diameterPolicyDataDetails == null){
				throw new InvalidValueException("Diameter Policy not found");
			}
			JSONArray jsonArray=ObjectDiffer.diff(diameterPolicyDataDetails,(DiameterPolicyData)diameterPolicyData);

			String diameterPolicyId = diameterPolicyDataDetails.getDiameterPolicyId();

			if (diameterPolicyData.getCommonStatusId().equals(diameterPolicyDataDetails.getCommonStatusId()) == false) {
				diameterPolicyDataDetails.setStatusChangeDate(new Timestamp(System.currentTimeMillis()));
			}

			diameterPolicyDataDetails.setCheckItem(diameterPolicyData.getCheckItem());
			diameterPolicyDataDetails.setCommonStatus(diameterPolicyData.getCommonStatus());
			diameterPolicyDataDetails.setDescription(diameterPolicyData.getDescription());
			diameterPolicyDataDetails.setEditable(diameterPolicyData.getEditable());
			diameterPolicyDataDetails.setName(diameterPolicyData.getName());
			diameterPolicyDataDetails.setRejectItem(diameterPolicyData.getRejectItem());
			diameterPolicyDataDetails.setReplyItem(diameterPolicyData.getReplyItem());
			diameterPolicyDataDetails.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
			diameterPolicyDataDetails.setCommonStatusId(diameterPolicyData.getCommonStatusId());

			session.update(diameterPolicyDataDetails);
			session.flush();

			// delete Existing  DiameterPolicyTimePeriod
			List<DiameterPolicyTimePeriod> diameterPolicyTimePeriodList = diameterPolicyDataDetails.getDiameterPolicyTimePeriodList();
			deleteObjectList(diameterPolicyTimePeriodList, session);

			// Save DiameterPolicyTimePeriodSet
			List<DiameterPolicyTimePeriod> diameterPolicyTimePeriodSet = diameterPolicyData.getDiameterPolicyTimePeriodList();
			if(diameterPolicyTimePeriodSet != null) {
				int orderNumber = 1;
				for(DiameterPolicyTimePeriod diameterPolicyTimePeriod : diameterPolicyTimePeriodSet){
					diameterPolicyTimePeriod.setDiameterPolicyId(diameterPolicyId);
					diameterPolicyTimePeriod.setOrderNumber(orderNumber++);
					session.save(diameterPolicyTimePeriod);
					session.flush();
				}
			}

			//Auditing Parameters
			staffData.setAuditName(diameterPolicyDataDetails.getName());
			staffData.setAuditId(diameterPolicyDataDetails.getAuditUId());

			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_AUTHORIZATION_POLICY_BASIC_DETAIL);
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update " + diameterPolicyData.getName() + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update "+ diameterPolicyData.getName()  + ", Reason: "+ hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to update "+ diameterPolicyData.getName()  + ", Reason: "+ exp.getMessage(),exp);
		}
	}

	private void verifyPolicyNameForUpdate(DiameterPolicyData diameterPolicyData) throws DuplicateInstanceNameFoundException{
		Criteria criteria = null;
		Session session = getSession();
		criteria = session.createCriteria(DiameterPolicyData.class);
		List<DiameterPolicyData> list = criteria.add(Restrictions.eq(DIAMETER_POLICY_NAME,diameterPolicyData.getName())).add(Restrictions.ne(DIAMETER_POLICY_ID, diameterPolicyData.getDiameterPolicyId())).list();
		if(list==null || list.isEmpty()){
			return;
		}else{
			throw new DuplicateInstanceNameFoundException("Diameter Poilcy Name Is Duplicated.");
		}
	}

	@Override
	public void updateByName(DiameterPolicyData diameterPolicyData, IStaffData staffData, String diameterPolicyName) throws DataManagerException {
		update(diameterPolicyData, staffData, DIAMETER_POLICY_NAME, diameterPolicyName);
	}

	@Override
	public void updateById(DiameterPolicyData diameterPolicyData, IStaffData staffData, String diameterPolicyId) throws DataManagerException {
		update(diameterPolicyData, staffData, DIAMETER_POLICY_ID, diameterPolicyId);
	}

}
