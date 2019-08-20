package com.elitecore.elitesm.hibernate.rm.concurrentloginpolicy;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.ConcurrentLoginPolicyDataManager;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.ConcurrentLoginPolicyData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.ConcurrentLoginPolicyDetailData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.IConcurrentLoginPolicyData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.IConcurrentLoginPolicyDetailData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class HConcurrentLoginPolicyDataManager  extends HBaseDataManager implements ConcurrentLoginPolicyDataManager{
	private static final String CONCURRENT_LOGIN_ID = "concurrentLoginId";
	private static final String NAME = "name";
	
	private static final String GENERAL = "SMS0149";
	private static final String MODULE = "HConcurrentLoginPolicyDataManager";
	
	public List updateStatus(IConcurrentLoginPolicyData concurrentLoginPolicyData,String concurrentLoginPolicyId, String commonStatus,Timestamp statusChangeDate,IStaffData staffData,String actionAlias) throws DataManagerException {
		List concurrentPolicyList = null;
		Session session = getSession();			
		ConcurrentLoginPolicyData concurrentPolicyData = null;
		ConcurrentLoginPolicyData oldConcurrentLoginPolicyData = new ConcurrentLoginPolicyData();
		try {
			Criteria criteria = session.createCriteria(ConcurrentLoginPolicyData.class);
			concurrentPolicyData = (ConcurrentLoginPolicyData)criteria.add(Restrictions.eq(CONCURRENT_LOGIN_ID,concurrentLoginPolicyId)).uniqueResult();
			
			oldConcurrentLoginPolicyData=(ConcurrentLoginPolicyData)concurrentLoginPolicyData;
			
			oldConcurrentLoginPolicyData.setConcurrentLoginId(concurrentLoginPolicyId);
			oldConcurrentLoginPolicyData.setCommonStatusId(commonStatus);
			oldConcurrentLoginPolicyData.setStatusChangeDate(statusChangeDate);
			oldConcurrentLoginPolicyData.setLastModifiedDate(statusChangeDate);
			
			JSONArray jsonArray=ObjectDiffer.diff(concurrentPolicyData, oldConcurrentLoginPolicyData);
			
			concurrentPolicyData.setCommonStatusId(commonStatus);
			concurrentPolicyData.setStatusChangeDate(statusChangeDate);
			concurrentPolicyData.setLastModifiedDate(statusChangeDate);
			
			if(concurrentPolicyData.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
 				concurrentPolicyData.setAuditUId(auditId);
 				staffData.setAuditId(auditId);
			}
		
			if (concurrentPolicyData.getLogin() == -1) {
				concurrentPolicyData.setLoginLimit("Unlimited");
			} else {
				concurrentPolicyData.setLoginLimit("Limited");
			}
			
			session.update(concurrentPolicyData);
			session.flush();
			criteria = null;
			staffData.setAuditName(concurrentPolicyData.getName());
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
			
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update concurrent login policy status, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to update concurrent login policy status, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update concurrent login policy status, Reason: " + e.getMessage(), e);
		}
		return concurrentPolicyList;
	}


	public PageList searchConcurrentLoginPolicy(IConcurrentLoginPolicyData concurrentLoginPolicyData, int pageNo, int pageSize) throws DataManagerException {

		PageList pageList = null;
			try{
					Session session = getSession();
					Criteria criteria = session.createCriteria(ConcurrentLoginPolicyData.class).createAlias("commonStatus","commonstatus");
		            if((concurrentLoginPolicyData.getName() != null && concurrentLoginPolicyData.getName().length()>0 )){
		            	criteria.add(Restrictions.ilike(NAME,concurrentLoginPolicyData.getName()));
		            }

		            if(concurrentLoginPolicyData.getCommonStatusId() !=null ){
		            	criteria.add(Restrictions.ilike("commonStatusId",concurrentLoginPolicyData.getCommonStatusId()));
		            }
		            if(concurrentLoginPolicyData.getConcurrentLoginPolicyTypeId() !=null){
		            	criteria.add(Restrictions.ilike("concurrentLoginPolicyTypeId",concurrentLoginPolicyData.getConcurrentLoginPolicyTypeId()));
		            }
		            int totalItems = criteria.list().size();
	            	criteria.setFirstResult(((pageNo-1) * pageSize));
	            	if (pageSize > 0 ){
	            		criteria.setMaxResults(pageSize);
	            	}
	            	
	            	List concurrentLoginPolicyList = criteria.list();
		            long totalPages = (long)Math.ceil(totalItems/pageSize);
		            if(totalItems % pageSize == 0)
		            totalPages -= 1;	
		            pageList = new PageList(concurrentLoginPolicyList, pageNo, totalPages ,totalItems);
         } catch (HibernateException he) {
 			he.printStackTrace();
 			throw new DataManagerException("Failed to retrive concurrent login policy list, Reason: " + he.getMessage(), he);
 		} catch (Exception e) {
 			e.printStackTrace();
 			throw new DataManagerException("Failed to retrive concurrent login policy list, Reason: " + e.getMessage(), e);
 		}
        return pageList;
	}
	
	public void updateBasicDetail(IConcurrentLoginPolicyData concurrentLoginPolicyData,IStaffData staffData,String actionAlias) throws DataManagerException {
		Session session = getSession();			

		ConcurrentLoginPolicyData newConcurrentLoginPolicyData = null;
			try{
				
					Criteria criteria = session.createCriteria(ConcurrentLoginPolicyData.class);
					newConcurrentLoginPolicyData = (ConcurrentLoginPolicyData)criteria.add(Restrictions.eq(CONCURRENT_LOGIN_ID,concurrentLoginPolicyData.getConcurrentLoginId()))
															.uniqueResult();
					
					ConcurrentLoginPolicyData conLoginPolicyData=new ConcurrentLoginPolicyData();
					
					conLoginPolicyData = setConcurrencyData(conLoginPolicyData,concurrentLoginPolicyData);
					
				    JSONArray jsonArray=ObjectDiffer.diff(newConcurrentLoginPolicyData, conLoginPolicyData);
				
					newConcurrentLoginPolicyData.setDescription(concurrentLoginPolicyData.getDescription());
					newConcurrentLoginPolicyData.setName(concurrentLoginPolicyData.getName());
					newConcurrentLoginPolicyData.setLogin(concurrentLoginPolicyData.getLogin());
					newConcurrentLoginPolicyData.setCommonStatus(concurrentLoginPolicyData.getCommonStatus());
					newConcurrentLoginPolicyData.setConcurrentLoginPolicyModeId(concurrentLoginPolicyData.getConcurrentLoginPolicyModeId());
					newConcurrentLoginPolicyData.setConcurrentLoginPolicyTypeId(concurrentLoginPolicyData.getConcurrentLoginPolicyTypeId());
					String oldAttributeValue = newConcurrentLoginPolicyData.getAttribute();
					newConcurrentLoginPolicyData.setAttribute(concurrentLoginPolicyData.getAttribute());
					
					if(concurrentLoginPolicyData.getAuditUId() == null){
							String auditId= UUIDGenerator.generate();
			 				concurrentLoginPolicyData.setAuditUId(auditId);
			 				staffData.setAuditId(auditId);
			 		}
					
					if (newConcurrentLoginPolicyData.getLogin() == -1) {
						newConcurrentLoginPolicyData.setLoginLimit("Unlimited");
					} else {
						newConcurrentLoginPolicyData.setLoginLimit("Limited");
					}
					
					
					// If the servicewise policy mode is updated to general then 
					// all the NASPorttype detail if exist then that has to be deleted from detail table
					if(newConcurrentLoginPolicyData.getConcurrentLoginPolicyModeId().equalsIgnoreCase(GENERAL) || !newConcurrentLoginPolicyData.getAttribute().equals(oldAttributeValue)){
						
						if(newConcurrentLoginPolicyData.getConcurrentLoginPolicyDetail()!=null &&
								newConcurrentLoginPolicyData.getConcurrentLoginPolicyDetail().size()>0){
								Logger.logInfo(MODULE, "Deleting attribute value detail because of changing in policy mode or change in attribute");
								for(Iterator concPolicyDetailSetIterator=newConcurrentLoginPolicyData.getConcurrentLoginPolicyDetail().iterator();concPolicyDetailSetIterator.hasNext();){
									IConcurrentLoginPolicyDetailData concurrentLoginPolicyDetailData =(IConcurrentLoginPolicyDetailData) concPolicyDetailSetIterator.next();
									concurrentLoginPolicyDetailData.setConcurrentLoginId(newConcurrentLoginPolicyData.getConcurrentLoginId());
									session.delete(concurrentLoginPolicyDetailData);
								
							}
								newConcurrentLoginPolicyData.setConcurrentLoginPolicyDetail(null);
						}
						
					}
					// details deleted
					
					session.update(newConcurrentLoginPolicyData);
					session.flush();
					
					doAuditingJson(jsonArray.toString(),staffData,actionAlias);
					
					criteria = null;
			} catch (ConstraintViolationException cve) {
				cve.printStackTrace();
				throw new DataManagerException("Failed to update concurrent login policy basic detail, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
			} catch (HibernateException he) {
				he.printStackTrace();
				throw new DataManagerException("Failed to update concurrent login policy basic detail, Reason: " + he.getMessage(), he);
			} catch (Exception e) {
				e.printStackTrace();
				throw new DataManagerException("Failed to update concurrent login policy basic detail, Reason: " + e.getMessage(), e);
			}
			
	}
	
	private ConcurrentLoginPolicyData setConcurrencyData(ConcurrentLoginPolicyData conLoginPolicyData,IConcurrentLoginPolicyData concurrentLoginPolicyData) {
		conLoginPolicyData.setConcurrentLoginId(concurrentLoginPolicyData.getConcurrentLoginId());
		conLoginPolicyData.setConcurrentLoginId(concurrentLoginPolicyData.getConcurrentLoginId());
		conLoginPolicyData.setLogin(concurrentLoginPolicyData.getLogin());
		conLoginPolicyData.setName(concurrentLoginPolicyData.getName());
		conLoginPolicyData.setSystemGenerated(concurrentLoginPolicyData.getSystemGenerated());
		conLoginPolicyData.setDescription(concurrentLoginPolicyData.getDescription());
		conLoginPolicyData.setCommonStatusId(concurrentLoginPolicyData.getCommonStatusId());
		conLoginPolicyData.setCreateDate(concurrentLoginPolicyData.getCreateDate());
		conLoginPolicyData.setCreatedByStaffId(concurrentLoginPolicyData.getCreatedByStaffId());
		conLoginPolicyData.setLastModifiedDate(concurrentLoginPolicyData.getLastModifiedDate());
		conLoginPolicyData.setLastModifiedByStaffId(concurrentLoginPolicyData.getLastModifiedByStaffId());
		conLoginPolicyData.setStatusChangeDate(concurrentLoginPolicyData.getStatusChangeDate());
		conLoginPolicyData.setConcurrentLoginPolicyTypeId(concurrentLoginPolicyData.getConcurrentLoginPolicyTypeId());
		conLoginPolicyData.setConcurrentLoginPolicyModeId(concurrentLoginPolicyData.getConcurrentLoginPolicyModeId());
		conLoginPolicyData.setCommonStatus(concurrentLoginPolicyData.getCommonStatus());
		conLoginPolicyData.setPolicyType(concurrentLoginPolicyData.getPolicyType());
		conLoginPolicyData.setConcurrentLoginPolicyDetail(concurrentLoginPolicyData.getConcurrentLoginPolicyDetail());
		conLoginPolicyData.setAttribute(concurrentLoginPolicyData.getAttribute());
		conLoginPolicyData.setAuditUId(concurrentLoginPolicyData.getAuditUId());
		return conLoginPolicyData;
	}

	public void updateAttributeDetail(IConcurrentLoginPolicyData concurrentLoginPolicyData, String concurrentLoginId,IStaffData staffData,String actionAlias) throws DataManagerException{
			try {
				Session session = getSession();
				Criteria criteria = session.createCriteria(ConcurrentLoginPolicyDetailData.class)
										   .add(Restrictions.eq(CONCURRENT_LOGIN_ID,concurrentLoginId));
				
				List<ConcurrentLoginPolicyDetailData> lstConcurrentLoginPolicyList = criteria.list();
				List<ConcurrentLoginPolicyDetailData> oldList=new ArrayList<ConcurrentLoginPolicyDetailData>();
				for(int i=0;i<lstConcurrentLoginPolicyList.size();i++){
					IConcurrentLoginPolicyDetailData concurrentLoginPolicyDetailData =(IConcurrentLoginPolicyDetailData) lstConcurrentLoginPolicyList.get(i);
					oldList.add((ConcurrentLoginPolicyDetailData)concurrentLoginPolicyDetailData);
					session.delete(concurrentLoginPolicyDetailData);
				}
				session.flush();
				
				List<ConcurrentLoginPolicyDetailData> newList=new ArrayList<ConcurrentLoginPolicyDetailData>();
				int orderNumber = 1;
				for(Iterator concPolicyDetailSetIterator=concurrentLoginPolicyData.getConcurrentLoginPolicyDetail().iterator();concPolicyDetailSetIterator.hasNext();){
					IConcurrentLoginPolicyDetailData concurrentLoginPolicyDetailData =(IConcurrentLoginPolicyDetailData) concPolicyDetailSetIterator.next();
					concurrentLoginPolicyDetailData.setConcurrentLoginId(concurrentLoginId);
					concurrentLoginPolicyDetailData.setOrderNumber(orderNumber);
					newList.add((ConcurrentLoginPolicyDetailData)concurrentLoginPolicyDetailData);
					
					session.saveOrUpdate(concurrentLoginPolicyDetailData);
					orderNumber++;
				}

				criteria = null;
				
				JSONArray jsonArray = ObjectDiffer.diff(listOfPolicyDetailDataToJSONObject(oldList),listOfPolicyDetailDataToJSONObject(newList));
				doAuditingJson(jsonArray.toString(), staffData, actionAlias);
				
			} catch (ConstraintViolationException cve) {
				cve.printStackTrace();
				throw new DataManagerException("Failed to update concurrent login policy attribute detail, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
			} catch (HibernateException he) {
				he.printStackTrace();
				throw new DataManagerException("Failed to update concurrent login policy attribute detail, Reason: " + he.getMessage(), he);
			} catch (Exception e) {
				e.printStackTrace();
				throw new DataManagerException("Failed to update concurrent login policy attribute detail, Reason: " + e.getMessage(), e);
			}
			
	}
	
	private JSONObject listOfPolicyDetailDataToJSONObject(List<ConcurrentLoginPolicyDetailData> list) {
		ConcurrentLoginPolicyData loginPolicyData = new ConcurrentLoginPolicyData();
		loginPolicyData.setConcurrentLoginPolicyDetail(list);
		return loginPolicyData.toJson();
	}

	
	@Override
	public ConcurrentLoginPolicyData getConcurrentLoginPolicyById(String concurrentLoginPolicyId) throws DataManagerException {
		
		return getConcurrentLoginPolicy(CONCURRENT_LOGIN_ID, concurrentLoginPolicyId);
		
	}

	@Override
	public ConcurrentLoginPolicyData getConcurrentLoginPolicyByName(String concurrentLoginPolicyName) throws DataManagerException {
		
		return getConcurrentLoginPolicy(NAME, concurrentLoginPolicyName);
		
	}

	private ConcurrentLoginPolicyData getConcurrentLoginPolicy(String propertyName, Object value) throws DataManagerException {
		
		String concurrentLoginPolicyName = (NAME.equals(propertyName)) ? (String) value : "Concurrent Login Policy";
		
		try {
			
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(ConcurrentLoginPolicyData.class).add(Restrictions.eq(propertyName, value));
			ConcurrentLoginPolicyData concurrentLoginPolicy = (ConcurrentLoginPolicyData) criteria.uniqueResult();
			
			if (concurrentLoginPolicy == null) {
				throw new InvalidValueException("Concurrent Login Policy not found");
			}
			
			concurrentLoginPolicyName = concurrentLoginPolicy.getName();
			
			return concurrentLoginPolicy;
			
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive " + concurrentLoginPolicyName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive " + concurrentLoginPolicyName + ", Reason: " + e.getMessage(), e);
		}
		
	}

	
	@Override
	public String create(Object obj) throws DataManagerException {
		ConcurrentLoginPolicyData concurrentLoginPolicy = (ConcurrentLoginPolicyData) obj;
		
		String concurrentLoginPolicyName = concurrentLoginPolicy.getName();
		
		try {
			
			Session session = getSession();
			session.clear();

			String auditId= UUIDGenerator.generate();

			concurrentLoginPolicy.setAuditUId(auditId);

			session.save(concurrentLoginPolicy);
			session.flush();
			session.clear();
			
			if (Collectionz.isNullOrEmpty(concurrentLoginPolicy.getConcurrentLoginPolicyDetail()) == false) {
				
				List<ConcurrentLoginPolicyDetailData> concurrentLoginPolicyDetails = concurrentLoginPolicy.getConcurrentLoginPolicyDetail();
				
				String concurrentLoginId = concurrentLoginPolicy.getConcurrentLoginId();
				int orderNumber = 1;
				for (ConcurrentLoginPolicyDetailData concurrentLoginPolicyDetail : concurrentLoginPolicyDetails) {
					
					concurrentLoginPolicyDetail.setConcurrentLoginId(concurrentLoginId);
					concurrentLoginPolicyDetail.setOrderNumber(orderNumber);
					session.save(concurrentLoginPolicyDetail);
					session.flush();
					session.clear();
					orderNumber++;
					
				}
				
			}
			
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + concurrentLoginPolicyName + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			Logger.logTrace(MODULE, he);
			throw new DataManagerException(FAILED_TO_CREATE + concurrentLoginPolicyName + REASON + he.getMessage(), he);
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + concurrentLoginPolicyName + REASON + e.getMessage(), e);
		}
		return concurrentLoginPolicyName;
		
	}
	
	@Override
	public void updateConcurrentLoginPolicyById(ConcurrentLoginPolicyData concurrentLoginPolicy, IStaffData staffData, String conPolicyId) throws DataManagerException{
		updateCocurrentLoginPolicy(concurrentLoginPolicy, CONCURRENT_LOGIN_ID, conPolicyId, staffData);
	}
	
	@Override
	public void updateConcurrentLoginPolicyByName(ConcurrentLoginPolicyData concurrentLoginPolicy, IStaffData staffData, String conPolicyName) throws DataManagerException {
		updateCocurrentLoginPolicy(concurrentLoginPolicy, NAME, conPolicyName, staffData);
	}
	
	private void updateCocurrentLoginPolicy(ConcurrentLoginPolicyData concurrentLoginPolicy, String propertyName, Object value,IStaffData staffData) throws DataManagerException{
		
		String concurrentLoginPolicyName = (NAME.equals(propertyName)) ? (String) value : "Concurrent Login Policy";

		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(ConcurrentLoginPolicyData.class).add(Restrictions.eq(propertyName, value));
			ConcurrentLoginPolicyData concurrentLoginPolicyData = (ConcurrentLoginPolicyData) criteria.uniqueResult();

			if (concurrentLoginPolicyData == null) {
				throw new InvalidValueException("Concurrent Login Policy not found");
			}

			concurrentLoginPolicyName = concurrentLoginPolicyData.getName();

			JSONArray jsonArray = ObjectDiffer.diff(concurrentLoginPolicyData, concurrentLoginPolicy);
			
			if (Collectionz.isNullOrEmpty(concurrentLoginPolicyData.getConcurrentLoginPolicyDetail()) == false) {
				
				deleteObjectList(concurrentLoginPolicyData.getConcurrentLoginPolicyDetail(), session);
				
			}
			
			if (Collectionz.isNullOrEmpty(concurrentLoginPolicy.getConcurrentLoginPolicyDetail()) == false) {
				
				List<ConcurrentLoginPolicyDetailData> concurrentLoginPolicyDetails = concurrentLoginPolicy.getConcurrentLoginPolicyDetail();
				
				String concurrentLoginId = concurrentLoginPolicyData.getConcurrentLoginId();
				int orderNumber = 1;
				for (ConcurrentLoginPolicyDetailData concurrentLoginPolicyDetail : concurrentLoginPolicyDetails) {
					
					concurrentLoginPolicyDetail.setConcurrentLoginId(concurrentLoginId);
					concurrentLoginPolicyDetail.setOrderNumber(orderNumber);
					session.save(concurrentLoginPolicyDetail);
					session.flush();
					orderNumber++;
					
				}
				
			}
			
			if (concurrentLoginPolicyData.getCommonStatusId().equals(concurrentLoginPolicy.getCommonStatusId()) == false) {
				concurrentLoginPolicyData.setStatusChangeDate(new Timestamp(new Date().getTime()));
			}
			concurrentLoginPolicyData.setLogin(concurrentLoginPolicy.getLogin());
			concurrentLoginPolicyData.setName(concurrentLoginPolicy.getName());
			concurrentLoginPolicyData.setDescription(concurrentLoginPolicy.getDescription());
			concurrentLoginPolicyData.setCommonStatusId(concurrentLoginPolicy.getCommonStatusId());
			concurrentLoginPolicyData.setDescription(concurrentLoginPolicy.getDescription());
			concurrentLoginPolicyData.setLastModifiedDate(concurrentLoginPolicy.getLastModifiedDate());
			concurrentLoginPolicyData.setLastModifiedByStaffId(concurrentLoginPolicy.getLastModifiedByStaffId());
			concurrentLoginPolicyData.setConcurrentLoginPolicyTypeId(concurrentLoginPolicy.getConcurrentLoginPolicyTypeId());
			concurrentLoginPolicyData.setConcurrentLoginPolicyModeId(concurrentLoginPolicy.getConcurrentLoginPolicyModeId());
			concurrentLoginPolicyData.setAttribute(concurrentLoginPolicy.getAttribute());
			concurrentLoginPolicyData.setConcurrentLoginPolicyDetail(concurrentLoginPolicy.getConcurrentLoginPolicyDetail());
			concurrentLoginPolicyData.setLoginLimit(concurrentLoginPolicy.getLoginLimit());
			
			session.update(concurrentLoginPolicyData);
			session.flush();
			
			staffData.setAuditId(concurrentLoginPolicyData.getAuditUId());
			staffData.setAuditName(concurrentLoginPolicyData.getName());

			doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_CONCURRENT_LOGIN_POLICY_ACTION);

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update " + concurrentLoginPolicyName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to update " + concurrentLoginPolicyName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update " + concurrentLoginPolicyName + ", Reason: " + e.getMessage(), e);
		}
	}

	
	@Override
	public String deleteConcurrentLoginPolicyById(String concurrentLoginPolicyId) throws DataManagerException {
		
		return deleteConcurrentLoginPolicy(CONCURRENT_LOGIN_ID, concurrentLoginPolicyId);

	}
	
	@Override
	public String deleteConcurrentLoginPolicyByName(String concurrentLoginPolicyName) throws DataManagerException {

		return deleteConcurrentLoginPolicy(NAME, concurrentLoginPolicyName);

	}

	private String deleteConcurrentLoginPolicy(String propertyName, Object value) throws DataManagerException {
		
		String concurrentLoginPolicyName = (NAME.equals(propertyName)) ? (String) value : "Concurrent Login Policy";

		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(ConcurrentLoginPolicyData.class).add(Restrictions.eq(propertyName, value));
			ConcurrentLoginPolicyData concurrentLoginPolicy = (ConcurrentLoginPolicyData) criteria.uniqueResult();

			if (concurrentLoginPolicy == null) {
				throw new InvalidValueException("Concurrent Login Policy not found");
			}

			concurrentLoginPolicyName = concurrentLoginPolicy.getName();
			
			session.delete(concurrentLoginPolicy);
			session.flush();

			return concurrentLoginPolicyName;

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + concurrentLoginPolicyName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to delete " + concurrentLoginPolicyName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to delete " + concurrentLoginPolicyName + ", Reason: " + e.getMessage(), e);
		} 

	}
	
	
}
