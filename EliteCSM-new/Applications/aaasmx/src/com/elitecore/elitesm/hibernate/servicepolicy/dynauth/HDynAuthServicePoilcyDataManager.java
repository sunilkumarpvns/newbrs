package com.elitecore.elitesm.hibernate.servicepolicy.dynauth;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.DynAuthServicePoilcyDataManager;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthFieldMapData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthNasClientDetailData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthNasClientsData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynaAuthPolicyESIRelData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

import net.sf.json.JSONArray;

public class HDynAuthServicePoilcyDataManager extends HBaseDataManager implements DynAuthServicePoilcyDataManager{
	private static final String MODULE="HDynAuthServicePoilcyDataManager";
	
	private static final String DYNA_AUTH_POLICY_NAME = "name";
	private static final String DYNA_AUTH_POLICY_ID = "dynAuthPolicyId";
	
	public String create(Object obj) throws DataManagerException {
		DynAuthPolicyInstData dynAuthPolicyInstData = (DynAuthPolicyInstData) obj;
		EliteAssert.notNull(dynAuthPolicyInstData,"dynAuthPolicyInstData must not be null.");
		try{
			Session session = getSession();
			session.clear();
			Criteria criteria = session.createCriteria(DynAuthPolicyInstData.class).addOrder(Order.desc("orderNumber"));
			criteria.setFetchSize(1);
			List<DynAuthPolicyInstData> list = criteria.list();
			if(list!=null && !list.isEmpty()){
				DynAuthPolicyInstData data = list.get(0);
				dynAuthPolicyInstData.setOrderNumber(data.getOrderNumber()+1);
			}else{
				dynAuthPolicyInstData.setOrderNumber(1L);
			}
			
			Set tempfeilMapSet = dynAuthPolicyInstData.getDynAuthFeildMapSet();
			Set nasClientDetails = dynAuthPolicyInstData.getDynAuthNasClientDataSet();
			dynAuthPolicyInstData.setDynAuthFeildMapSet(null);
			dynAuthPolicyInstData.setDynAuthNasClientDataSet(null);
			
			String auditId= UUIDGenerator.generate();
			
			dynAuthPolicyInstData.setAuditUId(auditId);
			
			session.save(dynAuthPolicyInstData);
			
			Iterator<DynAuthFieldMapData> itr = tempfeilMapSet.iterator();
			int orderNumber = 1;
			while(itr.hasNext()){
				DynAuthFieldMapData feildMapData = itr.next();
				feildMapData.setDynAuthPolicyId(dynAuthPolicyInstData.getDynAuthPolicyId());
				feildMapData.setOrderNumber(orderNumber++);
				session.save(feildMapData);
			}
			
			Iterator<DynAuthNasClientsData> nasItr = nasClientDetails.iterator();
			while (nasItr.hasNext()) {
				DynAuthNasClientsData dynAuthNasClientsData = (DynAuthNasClientsData) nasItr.next();
				
				Set<DynAuthNasClientDetailData> nasClientDetailsData =dynAuthNasClientsData.getDynaAuthNasClientDetailsData();
				dynAuthNasClientsData.setDynAuthPolicyId(dynAuthPolicyInstData.getDynAuthPolicyId());
				dynAuthNasClientsData.setDynaAuthNasClientDetailsData(null);
				session.save(dynAuthNasClientsData);
				
				if(nasClientDetailsData != null && nasClientDetailsData.isEmpty() == false){
					for(Iterator<DynAuthNasClientDetailData> iteratorPacketDetail = nasClientDetailsData.iterator(); iteratorPacketDetail.hasNext(); ){
						DynAuthNasClientDetailData dynaAuthNasClientDetailData = iteratorPacketDetail.next();
						dynaAuthNasClientDetailData.setDynaAuthNasId(dynAuthNasClientsData.getDynaAuthNasId());
						session.save(dynaAuthNasClientDetailData);
					}
				}
			}
			
			session.flush();
			session.clear();
		}catch(ConstraintViolationException e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + dynAuthPolicyInstData.getName() +
					REASON + EliteExceptionUtils.extractConstraintName(e.getSQLException()), e);
		}catch(HibernateException hExp){
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException(FAILED_TO_CREATE +dynAuthPolicyInstData.getName() +
					REASON + hExp.getMessage(), hExp);
		}catch(Exception exp){
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(FAILED_TO_CREATE +dynAuthPolicyInstData.getName() +
					REASON + exp.getMessage(),exp);
		}
		return dynAuthPolicyInstData.getName();
	}
	
	@Override
	public void updateById(DynAuthPolicyInstData policyData, IStaffData staffData) throws DataManagerException {
		update(policyData, staffData, DYNA_AUTH_POLICY_ID, policyData.getDynAuthPolicyId());
	}
	
	@Override
	public void updateByName(DynAuthPolicyInstData policyData, IStaffData staffData, String policyName)	throws DataManagerException {
		update(policyData, staffData, DYNA_AUTH_POLICY_NAME, policyName);
	}
	
	private void update(DynAuthPolicyInstData policyData,IStaffData staffData,String columnName, 
			Object policyName) throws DataManagerException {
		
		EliteAssert.notNull(policyData,"dynAuthPolicyInstData must not be null.");
		try{
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(DynAuthPolicyInstData.class)
					.add(Restrictions.eq(columnName, policyName));
			
			DynAuthPolicyInstData dynAuthPolicyInstData =(DynAuthPolicyInstData) criteria.uniqueResult();
			
			if (dynAuthPolicyInstData == null) {
				throw new InvalidValueException("Dynauth Service Policy does not exist");
			}
			
			policyData.setDynAuthFeildMapSet(new HashSet<DynAuthFieldMapData>(policyData.getMappingList()));
			policyData.setDynAuthNasClientDataSet(new LinkedHashSet<DynAuthNasClientsData>(policyData.getDynAuthNasClientDataSet()));
			
			JSONArray jsonArray=ObjectDiffer.diff(dynAuthPolicyInstData, policyData);
			
			String dynAuthPolicyId = dynAuthPolicyInstData.getDynAuthPolicyId();
			
			criteria = session.createCriteria(DynAuthFieldMapData.class);
			List<DynAuthFieldMapData> attrRelDataList = criteria.add(Restrictions.eq("dynAuthPolicyId",dynAuthPolicyId)).list();
			
			if(attrRelDataList != null){
				for(int i=0;i<attrRelDataList.size();i++){
					DynAuthFieldMapData atrRelData = attrRelDataList.get(i);
					session.delete(atrRelData);
					session.flush();
				}
			}
			
			List<DynAuthFieldMapData> mainMappingList = policyData.getMappingList();
			
			int orderNumber = 1;
			if(Collectionz.isNullOrEmpty(mainMappingList) == false) {
				for(int i=0;i<mainMappingList.size();i++){
					DynAuthFieldMapData relData = mainMappingList.get(i);
					relData.setDynAuthPolicyId(dynAuthPolicyId);
					relData.setOrderNumber(orderNumber++);
					session.save(relData);
					session.flush();
				}
			}
			
			criteria = session.createCriteria(DynAuthNasClientsData.class);
			List<DynAuthNasClientsData> dynAuthNasClientsDatas = criteria.add(Restrictions.eq("dynAuthPolicyId", dynAuthPolicyId)).list();
			if(dynAuthNasClientsDatas != null && dynAuthNasClientsDatas.isEmpty() == false){
				for(Iterator<DynAuthNasClientsData> iterator= dynAuthNasClientsDatas.iterator() ; iterator.hasNext();){
					DynAuthNasClientsData instanceData = iterator.next();
					criteria = session.createCriteria(DynAuthNasClientDetailData.class);
					List<DynAuthNasClientDetailData> detailList = criteria.add(Restrictions.eq("dynaAuthNasId", instanceData.getDynaAuthNasId())).list();
					for(Iterator<DynAuthNasClientDetailData> detailIterator = detailList.iterator();detailIterator.hasNext();){
						DynAuthNasClientDetailData detail = detailIterator.next();
						session.delete(detail);
						session.flush();
					}
					instanceData.setDynaAuthNasClientDetailsData(null);
				}
				for(Iterator<DynAuthNasClientsData> iterate = dynAuthNasClientsDatas.iterator() ; iterate.hasNext();){
					DynAuthNasClientsData clientData = iterate.next();
					session.delete(clientData);
				}
			}
			
			LinkedHashSet<DynAuthNasClientsData> nasClientDetailsSet = (LinkedHashSet<DynAuthNasClientsData>) policyData.getDynAuthNasClientDataSet();
			
			Iterator<DynAuthNasClientsData> nasItr = nasClientDetailsSet.iterator();
			while (nasItr.hasNext()) {
				DynAuthNasClientsData dynAuthNasClientsData = (DynAuthNasClientsData) nasItr.next();
				
				Set<DynAuthNasClientDetailData> nasClientDetailsData =dynAuthNasClientsData.getDynaAuthNasClientDetailsData();
				dynAuthNasClientsData.setDynAuthPolicyId(dynAuthPolicyInstData.getDynAuthPolicyId());
				dynAuthNasClientsData.setDynaAuthNasClientDetailsData(null);
				session.save(dynAuthNasClientsData);
				session.flush();
				
				if(nasClientDetailsData != null && nasClientDetailsData.isEmpty() == false){
					for(Iterator<DynAuthNasClientDetailData> iteratorPacketDetail = nasClientDetailsData.iterator(); iteratorPacketDetail.hasNext(); ){
						DynAuthNasClientDetailData dynaAuthNasClientDetailData = iteratorPacketDetail.next();
						dynaAuthNasClientDetailData.setDynaAuthNasId(dynAuthNasClientsData.getDynaAuthNasId());
						session.save(dynaAuthNasClientDetailData);
						session.flush();
					}
				}
			}
			
			//dynAuthPolicyInstData.setDynAuthNasClientDataSet(null);
			
			
			dynAuthPolicyInstData.setName(policyData.getName());
			dynAuthPolicyInstData.setDescription(policyData.getDescription());
			dynAuthPolicyInstData.setRuleSet(policyData.getRuleSet());;
			dynAuthPolicyInstData.setStatus(policyData.getStatus());
			dynAuthPolicyInstData.setResponseAttributes(policyData.getResponseAttributes());
			dynAuthPolicyInstData.setEligibleSession(policyData.getEligibleSession());
			dynAuthPolicyInstData.setEventTimestamp(policyData.getEventTimestamp());
			dynAuthPolicyInstData.setTableName(policyData.getTableName());
			dynAuthPolicyInstData.setDatabaseDatasourceId(policyData.getDatabaseDatasourceId());
			dynAuthPolicyInstData.setValidatePacket(policyData.getValidatePacket());
			dynAuthPolicyInstData.setDbFailureAction(policyData.getDbFailureAction());
			
			if (dynAuthPolicyInstData.getAuditUId() == null) {
				String auditId= UUIDGenerator.generate();
				dynAuthPolicyInstData.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
	
			session.update(dynAuthPolicyInstData);

	        session.flush();
	        
	        staffData.setAuditId(dynAuthPolicyInstData.getAuditUId());
	        staffData.setAuditName(dynAuthPolicyInstData.getName());
	        doAuditingJson(jsonArray.toString(),staffData, ConfigConstant.UPDATE_DYNAUTH_POLICY_BASIC_DETAIL);
	        
		}catch(ConstraintViolationException e){
			throw new DataManagerException("Failed update Dynauth Service Policy, Reason: " + EliteExceptionUtils.extractConstraintName(e.getSQLException()), e);
		}catch(HibernateException hExp){
			throw new DataManagerException("Failed update Dynauth Service Policy, Reason: " +hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException("Failed update Dynauth Service Policy, Reason: " + exp.getMessage(),exp);
		}
	}

	public PageList search(DynAuthPolicyInstData dynAuthPolicyInstData ,int pageNo, int pageSize) throws DataManagerException {

		Session session = getSession();
		Criteria criteria = session.createCriteria(DynAuthPolicyInstData.class);
		PageList pageList = null;

		try{
            
            if((dynAuthPolicyInstData.getName() != null && dynAuthPolicyInstData.getName().length()>0 )){
            	criteria.add(Restrictions.ilike("name","%"+dynAuthPolicyInstData.getName()+"%"));
            }

            if(!(dynAuthPolicyInstData.getStatus().equalsIgnoreCase("All")) ){
            	
            	criteria.add(Restrictions.ilike("status",dynAuthPolicyInstData.getStatus()));
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
	public String deleteById(String policyId) throws DataManagerException {
		return delete(policyId, DYNA_AUTH_POLICY_ID);
	}

	@Override
	public String deleteByName(String name) throws DataManagerException {
		return delete(name, DYNA_AUTH_POLICY_NAME);
	}
	
	private String delete(Object policyToDelete, String columnName) throws DataManagerException {
		String policyName = (DYNA_AUTH_POLICY_NAME.equals(columnName)) ? (String)policyToDelete : "Dynauth Service Policy";
		
		Session session = getSession();
		Criteria criteria = session.createCriteria(DynAuthPolicyInstData.class);
		Criteria dynAuthEsicriteria = session.createCriteria(DynaAuthPolicyESIRelData.class);

		try {
			DynAuthPolicyInstData policyData = (DynAuthPolicyInstData) criteria.add(
					Restrictions.eq(columnName, policyToDelete)).uniqueResult();
			
			if (policyData == null) {
				throw new InvalidValueException("Policy does not exist");
			}
			
			@SuppressWarnings("unchecked")
			List<DynaAuthPolicyESIRelData> dynAuthEsiRelInstDataList = dynAuthEsicriteria
					.add(Restrictions.eq(DYNA_AUTH_POLICY_ID, policyData.getDynAuthPolicyId())).list();
			
			deleteObjectList(dynAuthEsiRelInstDataList, session);
			session.delete(policyData);
			
			return policyData.getName();
		} catch (HibernateException hbe) {
			throw new DataManagerException("Failed to delete " + policyName + hbe.getMessage(), hbe);
		} catch (Exception e) {
			throw new DataManagerException("Failed to delete " + policyName + e.getMessage(), e);
		}
	}
	
	
	public void updateStatus(List<String> dynDynAuthPolicyIds,String status) throws DataManagerException {

		String dynDynAuthPolicyId = null;
		Session session = getSession();
		Criteria criteria = null;


		for(int i=0;i<dynDynAuthPolicyIds.size();i++){
			dynDynAuthPolicyId = dynDynAuthPolicyIds.get(i);
			criteria = session.createCriteria(DynAuthPolicyInstData.class);
			DynAuthPolicyInstData dynAuthPolicyInstData = (DynAuthPolicyInstData)criteria.add(Restrictions.eq("dynAuthPolicyId",dynDynAuthPolicyId)).uniqueResult();
			if(status.equals(BaseConstant.SHOW_STATUS_ID)){
				if(dynAuthPolicyInstData.getStatus().equals(BaseConstant.HIDE_STATUS_ID)){
					long orderNumber = dynAuthPolicyInstData.getOrderNumber();
					Criteria newCriteria = session.createCriteria(DynAuthPolicyInstData.class); 
					newCriteria.add(Restrictions.eq("status","CST01")).add(Restrictions.eq("orderNumber",new Long(orderNumber))); 					
					List sameOrderNoList = newCriteria.list();
					if(sameOrderNoList != null && sameOrderNoList.size() >0){
						// set the order number to the last number
						criteria = session.createCriteria(DynAuthPolicyInstData.class);
						criteria.add(Restrictions.eq("status",BaseConstant.SHOW_STATUS_ID));
						List<DynAuthPolicyInstData> tempList = criteria.addOrder(Order.desc("orderNumber")).list();
						if(tempList != null){
							dynAuthPolicyInstData.setOrderNumber(tempList.get(0).getOrderNumber() + 1);
						}
					}
				}				
			}
			dynAuthPolicyInstData.setStatus(status);			
			session.update(dynAuthPolicyInstData);			
			session.flush();

		}


	}

	public List<DynAuthPolicyInstData> searchActiveDynAuthServicePolicy() throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DynAuthPolicyInstData.class).addOrder(Order.asc("orderNumber"));
			return criteria.add(Restrictions.eq("status","CST01")).list();
		}catch(HibernateException e){
			throw new DataManagerException(e.getMessage(),e);
		}
		
	}

	@Override
	public DynAuthPolicyInstData getDynAuthPolicyInstDataById(String policyId) throws DataManagerException{
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DynAuthPolicyInstData.class).add(Restrictions.eq("dynAuthPolicyId", policyId));
			return (DynAuthPolicyInstData) criteria.uniqueResult();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	public List<DynaAuthPolicyESIRelData> getNASClients(String dynAuthPolicyId) throws DataManagerException {
		List<DynaAuthPolicyESIRelData> nasClients = null;
		try{
			Session session=getSession();
			Criteria criteria = session.createCriteria(DynaAuthPolicyESIRelData.class);
			criteria.add(Restrictions.eq("dynaAuthPolicyId", dynAuthPolicyId));
			nasClients=criteria.list();

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}

		return nasClients;
	}
	
	@Override
	public DynAuthPolicyInstData getDynauthPolicyByName(String name) throws DataManagerException {
		
		Session session = getSession();
		Criteria criteria = session.createCriteria(DynAuthPolicyInstData.class);

		try {
			DynAuthPolicyInstData data = (DynAuthPolicyInstData) criteria.add(
					Restrictions.eq(DYNA_AUTH_POLICY_NAME, name)).uniqueResult();
			if (data == null) {
				throw new InvalidValueException("Policy does not exist");
			}
			return data;
		} catch (HibernateException hExp) {
			throw new DataManagerException("Failed to retrieve Dynauth Service Policy: " + name + ", Reason: " + hExp.getMessage(), hExp);
		} catch (Exception exp) {
			throw new DataManagerException("Failed to retrieve Dynauth Service Policy: " + name + ", Reason: " + exp.getMessage(), exp);
		}
	}
}
