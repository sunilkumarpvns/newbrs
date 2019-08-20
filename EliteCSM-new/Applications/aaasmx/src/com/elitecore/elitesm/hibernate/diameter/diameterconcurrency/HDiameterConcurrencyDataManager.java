package com.elitecore.elitesm.hibernate.diameter.diameterconcurrency;

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
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.DiameterConcurrencyDataManager;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyFieldMapping;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

import net.sf.json.JSONArray;


public class HDiameterConcurrencyDataManager extends HBaseDataManager implements DiameterConcurrencyDataManager {

	private static final String DIA_CON_NAME = "name";
	private static final String DIA_CON_ID = "diaConConfigId";
	private static final String MODULE = "HDiameterConcurrencyDataManager";

	public PageList search(DiameterConcurrencyData diameterConcurrencyData, int pageNo, int pageSize) throws DataManagerException {
		PageList pageList = null;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterConcurrencyData.class);

			if((diameterConcurrencyData.getName() != null && diameterConcurrencyData.getName().length()>0 )){
				criteria.add(Restrictions.ilike(DIA_CON_NAME,diameterConcurrencyData.getName()));
			}
			
			criteria.addOrder(Order.asc(DIA_CON_ID));

			int totalItems = criteria.list().size();
			criteria.setFirstResult(((pageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}

			List<?> diameterConcurrencyList = criteria.list();

			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(diameterConcurrencyList, pageNo, totalPages ,totalItems);

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}

		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return pageList;
	}

	@Override
	public String create(Object obj) throws DataManagerException{
		DiameterConcurrencyData diameterConcurrencyData = (DiameterConcurrencyData) obj;
		try{
			Session session = getSession();
			session.clear();

			String auditId= UUIDGenerator.generate();
			diameterConcurrencyData.setAuditUId(auditId);

			session.save(diameterConcurrencyData);
			
			List<DiameterConcurrencyFieldMapping> diameterConcurrencyFieldMapList = diameterConcurrencyData.getDiameterConcurrencyFieldMappingList();
			if(diameterConcurrencyFieldMapList!=null && !diameterConcurrencyFieldMapList.isEmpty()){
				int size = diameterConcurrencyFieldMapList.size();
				int orderNumber = 1;
				for(int index=0;index<size;index++){

					DiameterConcurrencyFieldMapping diameterConcurrencyFieldMap = diameterConcurrencyFieldMapList.get(index);
					diameterConcurrencyFieldMap.setDiaConConfigId(diameterConcurrencyData.getDiaConConfigId());
					diameterConcurrencyFieldMap.setOrderNumber(orderNumber++);
					session.save(diameterConcurrencyFieldMap);
				}
			}
			session.flush();
			session.clear();
		}
		catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + diameterConcurrencyData.getName() + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException(FAILED_TO_CREATE +diameterConcurrencyData.getName()+ REASON +hExp.getMessage(), hExp);
		}catch(Exception exp){
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(FAILED_TO_CREATE +diameterConcurrencyData.getName()+ REASON +exp.getMessage(), exp);
		}
		return diameterConcurrencyData.getName();
	}
		
	public boolean verifyDiameterConcurrencyName(String diaConConfId, String name)throws DataManagerException{

		boolean isConcurrencyName=false;
		List<?> diameterConcurrencyList = null;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterConcurrencyData.class);
			criteria.add(Restrictions.eq(DIA_CON_NAME,name.trim()));
			criteria.add(Restrictions.ne(DIA_CON_ID,diaConConfId));              
			diameterConcurrencyList = criteria.list();
			if(diameterConcurrencyList != null && diameterConcurrencyList.size() > 0){
				isConcurrencyName=true;
			}

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}

		return isConcurrencyName;
	}
	
	@Override
	public void update(DiameterConcurrencyData diameterConcurrencyData,IStaffData staffData, String actionAlias) throws DataManagerException {
		Session session = getSession();	
		if(diameterConcurrencyData != null){
			try{
				Criteria criteria = session.createCriteria(DiameterConcurrencyData.class);
				DiameterConcurrencyData diameterConcurrencyDetails = (DiameterConcurrencyData)criteria.add(Restrictions.eq(DIA_CON_ID,diameterConcurrencyData.getDiaConConfigId())).uniqueResult();
				
				if(diameterConcurrencyDetails == null){
					throw new InvalidValueException("Diameter Concurrency Data not found");
				}
				
				JSONArray jsonArray=ObjectDiffer.diff(diameterConcurrencyDetails,diameterConcurrencyData);
				
				if(diameterConcurrencyDetails.getAuditUId() == null){
					String auditId= UUIDGenerator.generate();
					diameterConcurrencyDetails.setAuditUId(auditId);
					staffData.setAuditId(auditId);
				}
				
				diameterConcurrencyDetails.setDiaConConfigId(diameterConcurrencyData.getDiaConConfigId());
				diameterConcurrencyDetails.setName(diameterConcurrencyData.getName());
				diameterConcurrencyDetails.setDescription(diameterConcurrencyData.getDescription());
				diameterConcurrencyDetails.setDatabaseDsId(diameterConcurrencyData.getDatabaseDsId());
				diameterConcurrencyDetails.setTableName(diameterConcurrencyData.getTableName());
				diameterConcurrencyDetails.setStartTimeField(diameterConcurrencyData.getStartTimeField());
				diameterConcurrencyDetails.setLastUpdateTimeField(diameterConcurrencyData.getLastUpdateTimeField());
				diameterConcurrencyDetails.setConcurrencyIdentityField(diameterConcurrencyData.getConcurrencyIdentityField());
				diameterConcurrencyDetails.setDbFailureAction(diameterConcurrencyData.getDbFailureAction());
				diameterConcurrencyDetails.setSessionOverrideAction(diameterConcurrencyData.getSessionOverrideAction());
				diameterConcurrencyDetails.setSessionOverrideFields(diameterConcurrencyData.getSessionOverrideFields());
				diameterConcurrencyDetails.setAuditUId(diameterConcurrencyData.getAuditUId());
			
				session.update(diameterConcurrencyDetails);
				session.flush();
				
				// delete Existing DiameterConcurrencyFieldMapping
				deleteObjectList(diameterConcurrencyDetails.getDiameterConcurrencyFieldMappingList(), session);
				
				//insert new DiameterConcurrencyFieldMapping
				List<DiameterConcurrencyFieldMapping> diameterConcurrencyFieldMappingList = diameterConcurrencyData.getDiameterConcurrencyFieldMappingList();
				int orderNumber = 1;
				for(DiameterConcurrencyFieldMapping diameterConcurrencyFieldMapData : diameterConcurrencyFieldMappingList) {
					diameterConcurrencyFieldMapData.setDiaConConfigId(diameterConcurrencyData.getDiaConConfigId());
					diameterConcurrencyFieldMapData.setOrderNumber(orderNumber++);
					session.save(diameterConcurrencyFieldMapData);
				}
				session.flush();
				doAuditingJson(jsonArray.toString(),staffData,actionAlias);
				
			}catch(HibernateException hExp){
				throw new DataManagerException(hExp.getMessage(), hExp);
			}catch(Exception exp){
				throw new DataManagerException(exp.getMessage(), exp);
			}
		}else{
			throw new DataManagerException();			
		}
	}

	@Override
	public List<DiameterConcurrencyData> getDiameterConcurrencyDataList() throws DataManagerException {
		  List<DiameterConcurrencyData> diameterConcurrencyDataList = null;
	      try {
	            Session session = getSession();
	            Criteria criteria = session.createCriteria(DiameterConcurrencyData.class);
	            diameterConcurrencyDataList = criteria.list();
	        }
	        catch (HibernateException hExp) {
	            throw new DataManagerException(hExp.getMessage(), hExp);
	        }
	        catch (Exception exp) {
	            throw new DataManagerException(exp.getMessage(), exp);
	        }
	    return diameterConcurrencyDataList;
	}

	@Override
	public DiameterConcurrencyData getDiameterConcurrencyDataById(String diameterConcurrencyId) throws DataManagerException {
		return getDiameterConcurrencyDataByIdOrName(DIA_CON_ID, diameterConcurrencyId);
	}

	@Override
	public DiameterConcurrencyData getDiameterConcurrencyDataByName(String diameterConcurrencyName) throws DataManagerException {
		return getDiameterConcurrencyDataByIdOrName(DIA_CON_NAME, diameterConcurrencyName);
	}
	
	private DiameterConcurrencyData getDiameterConcurrencyDataByIdOrName(String propertyName, Object value) throws DataManagerException{
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterConcurrencyData.class);
			criteria.add(Restrictions.eq(propertyName,value));
			DiameterConcurrencyData diameterConcurrencyData = (DiameterConcurrencyData)criteria.uniqueResult();
			if(diameterConcurrencyData == null){
				throw new InvalidValueException("Diameter Concurrency Data not found");
			}
			return diameterConcurrencyData;
		}catch(HibernateException hExp){
			throw new DataManagerException("Failed to retrive Diameter Concurrency, Reason: "+hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException("Failed to retrive Diameter Concurrency, Reason: "+exp.getMessage(), exp);
		}
	}

	@Override
	public void updateById(DiameterConcurrencyData diameterConcurrencyData,IStaffData staffData, String diaConId) throws DataManagerException {
		update(diameterConcurrencyData, staffData, DIA_CON_ID,diaConId);
	}

	@Override
	public void updateByName(DiameterConcurrencyData diameterConcurrencyData,IStaffData staffData, String queryOrPathParam) throws DataManagerException {
		update(diameterConcurrencyData, staffData, DIA_CON_NAME,queryOrPathParam);
	}

	private void update(DiameterConcurrencyData diameterConcurrencyData,IStaffData staffData,String propertyName,Object value) throws DataManagerException {

		Session session = getSession();	
		try{
			Criteria criteria = session.createCriteria(DiameterConcurrencyData.class);
			DiameterConcurrencyData diameterConcurrencyDetails = (DiameterConcurrencyData)criteria.add(Restrictions.eq(propertyName,value)).uniqueResult();

			if(diameterConcurrencyDetails == null){
				throw new InvalidValueException("Diameter Concurrency Data not found");
			}
			JSONArray jsonArray=ObjectDiffer.diff(diameterConcurrencyDetails,diameterConcurrencyData);

			diameterConcurrencyDetails.setName(diameterConcurrencyData.getName());
			diameterConcurrencyDetails.setDescription(diameterConcurrencyData.getDescription());
			diameterConcurrencyDetails.setDatabaseDsId(diameterConcurrencyData.getDatabaseDsId());
			diameterConcurrencyDetails.setTableName(diameterConcurrencyData.getTableName());
			diameterConcurrencyDetails.setStartTimeField(diameterConcurrencyData.getStartTimeField());
			diameterConcurrencyDetails.setLastUpdateTimeField(diameterConcurrencyData.getLastUpdateTimeField());
			diameterConcurrencyDetails.setConcurrencyIdentityField(diameterConcurrencyData.getConcurrencyIdentityField());
			diameterConcurrencyDetails.setDbFailureAction(diameterConcurrencyData.getDbFailureAction());
			diameterConcurrencyDetails.setSessionOverrideAction(diameterConcurrencyData.getSessionOverrideAction());
			diameterConcurrencyDetails.setSessionOverrideFields(diameterConcurrencyData.getSessionOverrideFields());

			session.update(diameterConcurrencyDetails);
			session.flush();

			// delete Existing DiameterConcurrencyFieldMapping
			deleteObjectList(diameterConcurrencyDetails.getDiameterConcurrencyFieldMappingList(), session);

			//insert new DiameterConcurrencyFieldMapping
			List<DiameterConcurrencyFieldMapping> diameterConcurrencyFieldMappingList = diameterConcurrencyData.getDiameterConcurrencyFieldMappingList();
			int orderNumber = 1;
			for(DiameterConcurrencyFieldMapping diameterConcurrencyFieldMapData : diameterConcurrencyFieldMappingList) {
				diameterConcurrencyFieldMapData.setDiaConConfigId(diameterConcurrencyDetails.getDiaConConfigId());
				diameterConcurrencyFieldMapData.setOrderNumber(orderNumber++);
				session.save(diameterConcurrencyFieldMapData);
			}
			session.flush();

			staffData.setAuditId(diameterConcurrencyDetails.getAuditUId());
			staffData.setAuditName(diameterConcurrencyData.getName());

			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_DIAMETER_CONCURRENCY);
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update "+ diameterConcurrencyData.getName() +", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update "+ diameterConcurrencyData.getName() +", Reason: "+ hExp.getMessage(),hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update "+ diameterConcurrencyData.getName() +", Reason: "+ exp.getMessage(),exp);
		}
	}

	@Override
	public String deleteById(String diaConId) throws DataManagerException {
		return delete(DIA_CON_ID, diaConId);
	}

	@Override
	public String deleteByName(String diaConName) throws DataManagerException {
		return delete(DIA_CON_NAME, diaConName);
	}
	
	private String delete(String propertyName,Object value) throws DataManagerException{
		String diaConName = (DIA_CON_NAME.equals(propertyName)) ? propertyName : "Diameter Concurrency";
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterConcurrencyData.class).add(Restrictions.eq(propertyName,value));
			DiameterConcurrencyData diaConData = (DiameterConcurrencyData) criteria.uniqueResult();
			
			if(diaConData != null){
				session.delete(diaConData);
			}else{
				throw new InvalidValueException("Diameter Concurrency Data not found");
			}
			return diaConData.getName();
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update "+ diaConName +", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to delete "+diaConName+", Reason: ," + hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to delete "+diaConName+", Reason: ," + exp.getMessage(), exp);
		}
	}
}
