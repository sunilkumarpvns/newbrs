package com.elitecore.elitesm.hibernate.radius.policies.radiuspolicy;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.RadiusPolicyDataManager;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.IRadiusPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.IRadiusPolicyParamDetailData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.RadiusPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.RadiusPolicyTimePeriod;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

import net.sf.json.JSONArray;


public class HRadiusPolicyDataManager extends HBaseDataManager implements RadiusPolicyDataManager {

	
	private static final String RADIUS_POLICY = "radius policy ";
	private static final String RADIUS_POLICY_NAME = "name";
	private static final String RADIUS_POLICY_ID = "radiusPolicyId";
	private static final String MODULE = "HRadiusPolicyDataManager";

	
	public PageList search(IRadiusPolicyData radiusPolicyData, int pageNo, int pageSize) throws DataManagerException {
		PageList pageList = null;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusPolicyData.class)
			.createAlias("commonStatus","commonstatus");

			if((radiusPolicyData.getName() != null && radiusPolicyData.getName().length()>0 )){
				criteria.add(Restrictions.ilike(RADIUS_POLICY_NAME,radiusPolicyData.getName()));
			}

			if(radiusPolicyData.getCommonStatusId() !=null ){
				criteria.add(Restrictions.ilike("commonStatusId",radiusPolicyData.getCommonStatusId()));
			}

			int totalItems = criteria.list().size();
			criteria.setFirstResult(((pageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}

			List radiusPolicyList = criteria.list();


			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(radiusPolicyList, pageNo, totalPages ,totalItems);

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to search radius policy, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to search radius policy, Reason: " + exp.getMessage(), exp);
		}Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return pageList;
	}
	
	@Override
	public String create(Object obj) throws DataManagerException{
		IRadiusPolicyData radiusPolicyData = (IRadiusPolicyData) obj;
		try{
			Session session = getSession();
			session.clear();
			
			String auditId= UUIDGenerator.generate();
			
			radiusPolicyData.setAuditUId(auditId);
			session.save(radiusPolicyData);
			session.flush();
			session.clear();
			
			// Save RadiusPolicyTimePeriodSet
			List<RadiusPolicyTimePeriod> radiusPolicyTimePeriodSet = radiusPolicyData.getRadiusPolicyTimePeriodList();
			int orderNumber = 1;
			if(radiusPolicyTimePeriodSet != null) {
				for(RadiusPolicyTimePeriod radiusPolicyTimePeriod : radiusPolicyTimePeriodSet){
					radiusPolicyTimePeriod.setRadiusPolicyId(radiusPolicyData.getRadiusPolicyId());
					radiusPolicyTimePeriod.setOrderNumber(orderNumber++);
					session.save(radiusPolicyTimePeriod);
					session.flush();
					session.clear();
				}
			}
			
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE,cve);
			throw new DataManagerException(FAILED_TO_CREATE+RADIUS_POLICY + radiusPolicyData.getName() + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException hExp) {
			Logger.logTrace(MODULE,hExp);
			throw new DataManagerException(FAILED_TO_CREATE+RADIUS_POLICY + radiusPolicyData.getName() + REASON + hExp.getMessage(), hExp);
		} catch (Exception exp) {
			Logger.logTrace(MODULE,exp);
			throw new DataManagerException(FAILED_TO_CREATE+RADIUS_POLICY + radiusPolicyData.getName() + REASON + exp.getMessage(), exp);
		}
		return radiusPolicyData.getName();
	}

	public String updateStatus(String radiusPolicyId, String commonStatus, Timestamp statusChangeDate) throws DataManagerException{
		Session session = getSession();			
		RadiusPolicyData radiusPolicyData = null;
		String radiusPolicyName = null;

		try{
			Criteria criteria = session.createCriteria(RadiusPolicyData.class);
			radiusPolicyData = (RadiusPolicyData)criteria.add(Restrictions.eq(RADIUS_POLICY_ID,radiusPolicyId))
			.uniqueResult();
			radiusPolicyName = radiusPolicyData.getName();
			radiusPolicyData.setCommonStatusId(commonStatus);
			radiusPolicyData.setStatusChangeDate(statusChangeDate);	
			radiusPolicyData.setLastUpdated(statusChangeDate);
			session.update(radiusPolicyData);
			radiusPolicyData = null;
			criteria = null;

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update status of radius policy, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to update status of radius policy, Reason: " + exp.getMessage(), exp);
		}
		return radiusPolicyName;
	}

	public void updateRadiusPolicyParamByItems(List lstRadiusPolicyParamDetails, String radiusPolicyId, String parameterUsage) throws DataManagerException{
		Session session = getSession();			
		if(lstRadiusPolicyParamDetails != null && Strings.isNullOrBlank(radiusPolicyId) == false){
			try{
				Criteria criteria = session.createCriteria(IRadiusPolicyParamDetailData.class);
				criteria.add(Restrictions.eq(RADIUS_POLICY_ID,radiusPolicyId));
				criteria.add(Restrictions.eq("parameterUsage",parameterUsage));					
				List lstRadiusPolicyParamDetailsData = criteria.list();

				int size = lstRadiusPolicyParamDetailsData.size();
				for(int i=0;i<size;i++){
					IRadiusPolicyParamDetailData radPolDetailData = (IRadiusPolicyParamDetailData)lstRadiusPolicyParamDetailsData.get(i);
					session.delete(radPolDetailData);
				}

				int sizeOfList = lstRadiusPolicyParamDetails.size();
				for(int i=0;i<sizeOfList;i++){
					IRadiusPolicyParamDetailData radPolDetailData = (IRadiusPolicyParamDetailData)lstRadiusPolicyParamDetails.get(i);
					session.save(radPolDetailData);
				}

				session.flush();
				criteria = null;

			}catch(HibernateException hExp){
				hExp.printStackTrace();
				throw new DataManagerException("Failed to update radius policy parameters by items, Reason: "+ hExp.getMessage(), hExp);
			}catch(Exception exp){
				exp.printStackTrace();
				throw new DataManagerException("Failed to update radius policy parameters by items, Reason: " + exp.getMessage(), exp);
			}
		}else{
			throw new DataManagerException("Radius policy parameters not found");			
		}
	}

	@Override
	public String deleteById(String radiusPolicyId) throws DataManagerException{
		return delete(RADIUS_POLICY_ID, radiusPolicyId);
	}
	
	@Override
	public String deleteByName(String radiusPolicyName) throws DataManagerException{
		return delete(RADIUS_POLICY_NAME, radiusPolicyName);
	}
	
	private String delete(String propertyName, Object propertyValue) throws DataManagerException {
		String radiusPolicyName = (RADIUS_POLICY_NAME.equals(propertyName)) ? (String) propertyValue : "Radius Policy";
		RadiusPolicyData radiusPolicyData = null;
		try {
			Session session = getSession();

			// Remove child RadiusPolicyTimePeriod before RadiusPolicyData
			Criteria criteria = session.createCriteria(RadiusPolicyData.class).add(Restrictions.eq(propertyName,propertyValue));
			radiusPolicyData  = (RadiusPolicyData) criteria.uniqueResult();
			
			if(radiusPolicyData == null){
				throw new InvalidValueException("Radius Policy not found");
			}
			
			List<RadiusPolicyTimePeriod> radiusPolicyTimePeriodList = radiusPolicyData.getRadiusPolicyTimePeriodList();
			deleteObjectList(radiusPolicyTimePeriodList, session);
			session.delete(radiusPolicyData);
			session.flush();

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + radiusPolicyName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to delete " + radiusPolicyName + ", Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to delete " + radiusPolicyName + ", Reason: " + exp.getMessage(), exp);
		}
		
		return radiusPolicyData.getName();
	}
	
	public boolean verifyRadiusPolicyName(String radiusPolicyId, String policyName)throws DataManagerException{

		boolean isPolicyName=false;
		List radiusPolicyList = null;

		try{

			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusPolicyData.class);
			criteria.add(Restrictions.eq(RADIUS_POLICY_NAME,policyName.trim()));
			criteria.add(Restrictions.ne(RADIUS_POLICY_ID,radiusPolicyId));              
			radiusPolicyList = criteria.list();
			if(radiusPolicyList != null && radiusPolicyList.size() > 0){
				isPolicyName=true;
			}

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to verify radius policy name, Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to verify radius policy name, Reason: " + exp.getMessage(), exp);
		}

		return isPolicyName;
	}

	public void updateCheckItem(String radiusPolicyId, String checkItem)
	throws DataManagerException {

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusPolicyData.class);
			criteria.add(Restrictions.eq(RADIUS_POLICY_ID, radiusPolicyId));
			RadiusPolicyData radiusPolicyData = (RadiusPolicyData)criteria.uniqueResult();
			radiusPolicyData.setCheckItem(checkItem);
			session.update(radiusPolicyData);
		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update check item of radius policy, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to update check item of radius policy, Reason: " + exp.getMessage(), exp);
		}

	}

	public void updateRejectItem(String radiusPolicyId, String rejectItem)
	throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusPolicyData.class);
			criteria.add(Restrictions.eq(RADIUS_POLICY_ID, radiusPolicyId));
			RadiusPolicyData radiusPolicyData = (RadiusPolicyData)criteria.uniqueResult();
			radiusPolicyData.setRejectItem(rejectItem);
			session.update(radiusPolicyData);
		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update reject item of radius policy, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to update reject item of radius policy, Reason: " + exp.getMessage(), exp);
		}

	}

	public void updateReplyItem(String radiusPolicyId, String replyItem)
	throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusPolicyData.class);
			criteria.add(Restrictions.eq(RADIUS_POLICY_ID, radiusPolicyId));
			RadiusPolicyData radiusPolicyData = (RadiusPolicyData)criteria.uniqueResult();
			radiusPolicyData.setReplyItem(replyItem);
			session.update(radiusPolicyData);
		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("failed to update reply item of radius policy, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("failed to update reply item of radius policy, Reason: " + exp.getMessage(), exp);
		}

	}
	
	@Override
	public IRadiusPolicyData getRadiusPolicyDataByName(String radiusPolicyName) throws DataManagerException {
		return getRadiusPolicyData(radiusPolicyName, RADIUS_POLICY_NAME);
	}
	
	@Override
	public IRadiusPolicyData getRadiusPolicyDataById(String radiusPolicyId) throws DataManagerException {
		return getRadiusPolicyData(radiusPolicyId, RADIUS_POLICY_ID);
	}
	
	private IRadiusPolicyData getRadiusPolicyData(Object propertyValue, String propertyName) throws DataManagerException {
		
		String radiusPolicyName = (RADIUS_POLICY_NAME.equals(propertyName)) ? (String) propertyValue : "Radius Policy";
		IRadiusPolicyData radiusPolicyData = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(RadiusPolicyData.class).add(Restrictions.eq(propertyName, propertyValue));
			radiusPolicyData = (IRadiusPolicyData) criteria.uniqueResult();
			
			if(radiusPolicyData == null){
				throw new InvalidValueException("Radius Policy not found");
			}

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retive "+ radiusPolicyName + ", Reason: "+ hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retive "+ radiusPolicyName + ", Reason: "+ exp.getMessage(), exp);
		}
		return radiusPolicyData;
	}

	@Override
	public void updateByName(IRadiusPolicyData radiusPolicyData, IStaffData staffData, String name) throws DataManagerException {
		updateBasicDetails(radiusPolicyData,staffData,RADIUS_POLICY_NAME,name);
	}
	
	@Override
	public void updateById(IRadiusPolicyData radiusPolicyData, IStaffData staffData, String radiusPolicyId) throws DataManagerException {
		updateBasicDetails(radiusPolicyData,staffData,RADIUS_POLICY_ID,radiusPolicyId);
	}
	
	private void updateBasicDetails(IRadiusPolicyData radiusPolicyData, IStaffData staffData, String propertyName, Object porpertyValue) throws DataManagerException {

		Session session = getSession();	
			try{
				
				Criteria criteria = session.createCriteria(RadiusPolicyData.class).add(Restrictions.eq(propertyName,porpertyValue));
				RadiusPolicyData radiusPolicyDataDetails = (RadiusPolicyData) criteria.uniqueResult();
				
				if(radiusPolicyDataDetails == null){
					throw new InvalidValueException("Radius Policy not found");
				}
				JSONArray jsonArray=ObjectDiffer.diff(radiusPolicyDataDetails,(RadiusPolicyData)radiusPolicyData);
				
				String radiusPolicyId = radiusPolicyDataDetails.getRadiusPolicyId();
				
				if (radiusPolicyData.getCommonStatusId().equals(radiusPolicyDataDetails.getCommonStatusId()) == false) {
					radiusPolicyDataDetails.setStatusChangeDate(new Timestamp(System.currentTimeMillis()));
				}
				
				radiusPolicyDataDetails.setAddItem(radiusPolicyData.getAddItem());
				radiusPolicyDataDetails.setCheckItem(radiusPolicyData.getCheckItem());
				radiusPolicyDataDetails.setCommonStatus(radiusPolicyData.getCommonStatus());
				radiusPolicyDataDetails.setDescription(radiusPolicyData.getDescription());
				radiusPolicyDataDetails.setEditable(radiusPolicyData.getEditable());
				radiusPolicyDataDetails.setName(radiusPolicyData.getName());
				radiusPolicyDataDetails.setRejectItem(radiusPolicyData.getRejectItem());
				radiusPolicyDataDetails.setReplyItem(radiusPolicyData.getReplyItem());
				radiusPolicyDataDetails.setLastUpdated(new Timestamp(System.currentTimeMillis()));
				radiusPolicyDataDetails.setCommonStatusId(radiusPolicyData.getCommonStatusId());

				session.update(radiusPolicyDataDetails);
				session.flush();

				// delete Existing  RadiusPolicyTimePeriod
				List<RadiusPolicyTimePeriod> radiusPolicyTimePeriodList = radiusPolicyDataDetails.getRadiusPolicyTimePeriodList();
				deleteObjectList(radiusPolicyTimePeriodList, session);
			
				// Save RadiusPolicyTimePeriodSet
				List<RadiusPolicyTimePeriod> radiusPolicyTimePeriodSet = radiusPolicyData.getRadiusPolicyTimePeriodList();
				int orderNumber = 1;
				if(radiusPolicyTimePeriodSet != null) {
					for(RadiusPolicyTimePeriod radiusPolicyTimePeriod : radiusPolicyTimePeriodSet){
						radiusPolicyTimePeriod.setRadiusPolicyId(radiusPolicyId);
						radiusPolicyTimePeriod.setOrderNumber(orderNumber++);
						session.save(radiusPolicyTimePeriod);
						session.flush();
					}
				}
				
				//Auditing Parameters
				staffData.setAuditName(radiusPolicyDataDetails.getName());
				staffData.setAuditId(radiusPolicyDataDetails.getAuditUId());
				
				doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_RADIUS_POLICY_ACTION);
			} catch (ConstraintViolationException cve) {
				cve.printStackTrace();
				throw new DataManagerException("Failed to update " + radiusPolicyData.getName() + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
			} catch (HibernateException hExp) {
				hExp.printStackTrace();
				throw new DataManagerException("Failed to update "+ radiusPolicyData.getName()  + ", Reason: "+ hExp.getMessage(), hExp);
			} catch (Exception exp) {
				exp.printStackTrace();
				throw new DataManagerException("Failed to update "+ radiusPolicyData.getName()  + ", Reason: "+ exp.getMessage(),exp);
			}
	}
	
}
