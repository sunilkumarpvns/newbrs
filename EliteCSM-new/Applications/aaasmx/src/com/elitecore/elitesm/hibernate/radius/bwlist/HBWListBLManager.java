package com.elitecore.elitesm.hibernate.radius.bwlist;

import java.util.List;

import net.sf.json.JSONArray;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateEntityFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.bwlist.BWListDataManager;
import com.elitecore.elitesm.datamanager.radius.bwlist.data.BWListData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.ws.logger.Logger;

public class HBWListBLManager extends HBaseDataManager implements BWListDataManager {

	private static final String BW_ID = "bwId";
	private static final String MODULE = "HBWListBLManager";

	public BWListData create(BWListData bwlistData) throws DataManagerException {
		try {
			Session session = getSession();

			Integer maxNumber = getMaxOrderNumber(session, bwlistData);
			bwlistData.setOrderNumber(maxNumber);
			bwlistData.setAuditUid(UUIDGenerator.generate());

			session.save(bwlistData);
			session.flush();
			return bwlistData;
		} catch (ConstraintViolationException hExp) {
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException("Failed to create Blacklist Candidates, Reason: "  + EliteExceptionUtils.extractConstraintName(hExp.getSQLException()), hExp);
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch (Exception exp) {
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	public PageList search(BWListData bwListData, int requiredPageNo,Integer pageSize) throws DataManagerException {
		PageList pageList = null;

		try{
			Session session = getSession();
			Criteria criteria=session.createCriteria(BWListData.class).addOrder(Order.asc("orderNumber"));
		
			if(bwListData.getAttributeId() != null && bwListData.getAttributeId().length()>0){
				 boolean checkSpecialChar = bwListData.getAttributeId().indexOf('%')!=-1;
				 if(checkSpecialChar){
					 criteria.add(Restrictions.ilike("attributeId",bwListData.getAttributeId()));
				 }else{
					 criteria.add(Restrictions.eq("attributeId", bwListData.getAttributeId()));
				 }
				 
			}
			
			if(bwListData.getAttributeValue() != null && bwListData.getAttributeValue().length()>0){
				 boolean checkSpecialChar = bwListData.getAttributeValue().indexOf('%')!=-1;
				 if(checkSpecialChar){
					 criteria.add(Restrictions.ilike("attributeValue",bwListData.getAttributeValue()));
				 }else{
					 criteria.add(Restrictions.eq("attributeValue", bwListData.getAttributeValue()));
				 }
				 
			}
			
			if(bwListData.getCommonStatusId() != null && bwListData.getCommonStatusId().length()>0){
				criteria.add(Restrictions.ilike("commonStatusId", bwListData.getCommonStatusId()));
			}
			
			int totalItems = criteria.list().size();

			criteria.setFirstResult(((requiredPageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}

			List<BWListData> bwlist = criteria.list();

			long totalPages = totalItems/pageSize;
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(bwlist, requiredPageNo, totalPages ,totalItems);
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
	}

	public void delete(String bwId) throws DataManagerException {
		BWListData bwListData;
		try{
			 Session session = getSession();
			 Criteria criteria = session.createCriteria(BWListData.class);
			 bwListData = (BWListData)criteria.add(Restrictions.like(BW_ID,bwId)).uniqueResult();

			 session.delete(bwListData);
		 }catch(HibernateException hExp){
			 throw new DataManagerException(hExp.getMessage(), hExp);
		 }catch(Exception exp){
			 Logger.logTrace(MODULE, exp);
			 throw new DataManagerException(exp.getMessage(),exp);
		 }
	}

	public void updateStatus(String bwId, String commonStatusId) throws DataManagerException{
		
		Session session = getSession();			
		BWListData bwlistData = null;
			
		try{
				Criteria criteria = session.createCriteria(BWListData.class);
				bwlistData = (BWListData)criteria.add(Restrictions.eq(BW_ID,bwId)).uniqueResult();
				bwlistData.setCommonStatusId(commonStatusId);
				session.update(bwlistData);
				session.flush();

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}
	
	@Override
	public BWListData getBWListData(String bwId) throws DataManagerException {
		BWListData bwListData;
		try{
			 Session session = getSession();
			 Criteria criteria = session.createCriteria(BWListData.class);
			 bwListData = (BWListData)criteria.add(Restrictions.like(BW_ID,bwId)).uniqueResult();

			return bwListData;
		 }catch(HibernateException hExp){
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException(hExp.getMessage(), hExp);
		 }catch(Exception exp){
			 throw new DataManagerException(exp.getMessage(),exp);
		 }
	}
	
	public void update(BWListData bwlistOldData, BWListData bwlistData, IStaffData staffData) throws DataManagerException, DuplicateEntityFoundException {
		try {
			Session session = getSession();
		
			if( Strings.isNullOrBlank(bwlistData.getAuditUid()) ){
				bwlistData.setAuditUid(UUIDGenerator.generate());
			}
			
			JSONArray jsonArray=ObjectDiffer.diff(bwlistOldData, bwlistData);   
			
			session.update(bwlistData);
			
			staffData.setAuditId(bwlistData.getAuditUid());
			staffData.setAuditName(ConfigConstant.BLACKLIST_CANDIDATES_LABEL);
			doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_BLACKLIST_CANDIDATES_ACTION);
			
			session.flush();
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException("Failed to update Blacklist Candidates, Reason: "  + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException hExp) {
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch (Exception exp) {
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}
}
 