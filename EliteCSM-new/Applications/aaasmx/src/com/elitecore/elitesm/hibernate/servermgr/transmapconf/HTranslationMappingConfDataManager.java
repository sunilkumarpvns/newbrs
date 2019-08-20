package com.elitecore.elitesm.hibernate.servermgr.transmapconf;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.TranslationMappingConfDataManager;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.DummyResponseParameterData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingInstData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingInstDetailData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingInstDetailRelData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslatorTypeData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

import net.sf.json.JSONArray;

public class HTranslationMappingConfDataManager extends HBaseDataManager implements TranslationMappingConfDataManager{

	private static final String TRANSLATION_MAP_CONFIG_NAME = "name";
	private static final String TRANSLATION_MAP_CONFIG_ID = "translationMapConfigId";
	private static String MODULE = HTranslationMappingConfDataManager.class.getSimpleName();
	
	public List<TranslatorTypeData> getToTranslatorTypeDataList() throws DataManagerException {
		try{

			Session session = getSession();

			Criteria criteria = session.createCriteria(TranslatorTypeData.class).add(Restrictions.eq("translateTo", TranslationMappingConfigConstants.YES));

			List<TranslatorTypeData> translationTypeDataList = criteria.list();

			return translationTypeDataList;

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to get To Translation Type list of Translation Mapping, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to get To Translation Type list of Translation Mapping, Reason: " + exp.getMessage(), exp);
		}
	}
	
	public List<TranslatorTypeData> getFromTranslatorTypeDataList() throws DataManagerException {
		try{

			Session session = getSession();

			Criteria criteria = session.createCriteria(TranslatorTypeData.class).add(Restrictions.eq("translateFrom", TranslationMappingConfigConstants.YES));

			List<TranslatorTypeData> translationTypeDataList = criteria.list();

			return translationTypeDataList;

		} catch(HibernateException hExp) { 
			hExp.printStackTrace();
			throw new DataManagerException("Failed to get From Translation Type list of Translation Mapping, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) { 
			exp.printStackTrace();
			throw new DataManagerException("Failed to get From Translation Type list of Translation Mapping, Reason: " + exp.getMessage(), exp);
		}
	}
	
	public TranslatorTypeData getTranslatorTypeData(String translatorTypeId) throws DataManagerException {
		try{

			Session session = getSession();

			Criteria criteria = session.createCriteria(TranslatorTypeData.class).add(Restrictions.eq("translatorTypeId", translatorTypeId));

			TranslatorTypeData translatorTypeData = (TranslatorTypeData) criteria.uniqueResult();

			return translatorTypeData;

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed get Translation Type data of Translation Mapping, Reason: " + hExp.getMessage(), hExp);
		}  catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed get Translation Type data of Translation Mapping, Reason: " + exp.getMessage(), exp);
		}
	}

	public List<TranslationMappingConfData> getTranslationMappingConfigList(String toType,String fromType) throws DataManagerException {
		try{

			Session session = getSession();

			Criteria criteria = session.createCriteria(TranslationMappingConfData.class).add(Restrictions.eq("toType", toType)).
																				 add(Restrictions.eq("fromType", fromType));

			List<TranslationMappingConfData> translationMappingConfigList = criteria.list();

			return translationMappingConfigList;

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to get Translation Mapping data for given To and From Translation Type, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to get Translation Mapping data for given To and From Translation Type, Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override 
	public String create(Object obj) throws DataManagerException, DuplicateInstanceNameFoundException {
		
		TranslationMappingConfData translationMappingConfData = (TranslationMappingConfData) obj;
		EliteAssert.notNull(translationMappingConfData,"Translation Mapping Configuration data must not be null.");
		try {
			Session session = getSession();
			session.clear();
			List<TranslationMappingInstData> translationMappingInstDataSet = translationMappingConfData.getTranslationMappingInstDataList();
			List<DummyResponseParameterData> dummyResponseParameterDataSet = translationMappingConfData.getDummyResponseParameterDataList();
			List<TranslationMappingInstDetailData> defaultTranslationMappingDataList = translationMappingConfData.getDefaultTranslationMappingDetailDataList();
			translationMappingConfData.setDefaultTranslationMappingDetailDataList(null);
			translationMappingConfData.setTranslationMappingInstDataList(null);
			translationMappingConfData.setDummyResponseParameterDataList(null);
			
			String auditId= UUIDGenerator.generate();
			
			translationMappingConfData.setAuditUid(auditId);
			
			session.save(translationMappingConfData);
			session.flush();
			session.clear();
			if (Collectionz.isNullOrEmpty(defaultTranslationMappingDataList) == false) {
				long orderNumber = 1;
				for(Iterator<TranslationMappingInstDetailData> iterator = defaultTranslationMappingDataList.iterator();iterator.hasNext();){
					TranslationMappingInstDetailData defaultDetailData = iterator.next();
					defaultDetailData.setTranslationMapConfigId(translationMappingConfData.getTranslationMapConfigId());
					defaultDetailData.setOrderNumber(orderNumber);
					session.save(defaultDetailData);
					session.flush();
					session.clear();
					orderNumber++;
				}
			}
			
			int orderNumber = 1;
			if (Collectionz.isNullOrEmpty(dummyResponseParameterDataSet) == false) {
				for(Iterator<DummyResponseParameterData> iterator = dummyResponseParameterDataSet.iterator();iterator.hasNext();){
					DummyResponseParameterData dummyResponseParameterData = iterator.next();
					dummyResponseParameterData.setTranslationMapConfigId(translationMappingConfData.getTranslationMapConfigId());
					dummyResponseParameterData.setOrderNumber(orderNumber);
					session.save(dummyResponseParameterData);
					session.flush();
					session.clear();
					orderNumber++;
				}
			}

			/* save Mapping Instance*/
			if (Collectionz.isNullOrEmpty(translationMappingInstDataSet) == false) {
				Iterator<TranslationMappingInstData> iterator  = translationMappingInstDataSet.iterator();
				orderNumber = 1;
				while(iterator.hasNext()){
					TranslationMappingInstData instData = iterator.next();
					instData.setTranslationMapConfigId(translationMappingConfData.getTranslationMapConfigId());
					List<TranslationMappingInstDetailData> translationMappingInstDetailDataSet = instData.getTranslationMappingInstDetailDataList();
					instData.setTranslationMappingInstDetailDataList(null);
					instData.setOrderNumber(orderNumber);
					session.save(instData);
					session.flush();
					session.clear();
					orderNumber++;
					if(instData.getDefaultMapping().equals(TranslationMappingConfigConstants.NO)){
						if (Collectionz.isNullOrEmpty(translationMappingInstDetailDataSet) == false) {
							Iterator<TranslationMappingInstDetailData> detailIterator  = translationMappingInstDetailDataSet.iterator();
							long orderNo = 1;
							while(detailIterator.hasNext()){
								TranslationMappingInstDetailData detailData = detailIterator.next();
								detailData.setDefaultMapping(TranslationMappingConfigConstants.NO);
								detailData.setTranslationMapConfigId(translationMappingConfData.getTranslationMapConfigId());
								detailData.setOrderNumber(orderNo);
								session.save(detailData);
								session.flush();
								session.clear();
								
								
								TranslationMappingInstDetailRelData detailRelData = new TranslationMappingInstDetailRelData();
								detailRelData.setDetailId(detailData.getDetailId());
								detailRelData.setMappingInstanceId(instData.getMappingInstanceId());
								detailRelData.setOrderNumber(orderNo);
								session.save(detailRelData);
								session.flush();
								session.clear();
								
								orderNo++;
							}
						}
					}else{
						if (Collectionz.isNullOrEmpty(defaultTranslationMappingDataList) == false) {
							for(Iterator<TranslationMappingInstDetailData> defaultIterator = defaultTranslationMappingDataList.iterator();defaultIterator.hasNext();){
								TranslationMappingInstDetailData defaultDetailData = defaultIterator.next();
								TranslationMappingInstDetailRelData detailRelData = new TranslationMappingInstDetailRelData();
								detailRelData.setDetailId(defaultDetailData.getDetailId());
								detailRelData.setOrderNumber(defaultDetailData.getOrderNumber());
								detailRelData.setMappingInstanceId(instData.getMappingInstanceId());
								session.save(detailRelData);
								session.flush();
								session.clear();
							}
						}
					}
					
				}
				
			}

			session.flush();
			session.clear();
		} catch(ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + translationMappingConfData.getName() + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException hExp) {
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException(FAILED_TO_CREATE + translationMappingConfData.getName() + REASON + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(FAILED_TO_CREATE + translationMappingConfData.getName() + REASON + exp.getMessage(), exp);
		}
		return translationMappingConfData.getName();

	}
	
	public PageList search(TranslationMappingConfData translationMappingConfData ,int pageNo, int pageSize) throws DataManagerException {

		Session session = getSession();
		Criteria criteria = session.createCriteria(TranslationMappingConfData.class);
		PageList pageList = null;

		try{
            
            if((translationMappingConfData.getName() != null && translationMappingConfData.getName().length()>0 )){
            	criteria.add(Restrictions.ilike(TRANSLATION_MAP_CONFIG_NAME,"%"+translationMappingConfData.getName()+"%"));
            }

			int totalItems = criteria.list().size();
			criteria.setFirstResult(((pageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}

			List policyList = criteria.list();
			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(policyList, pageNo, totalPages ,totalItems);
			
			return  pageList;

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to search Translation Mapping, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to search Translation Mapping, Reason: " + exp.getMessage(), exp);
		}

	}
	
	private String delete(String propertyName, Object propertyValue) throws DataManagerException {
		String translationMappingName = (TRANSLATION_MAP_CONFIG_NAME.equals(propertyName)) ? (String) propertyValue : "Translation Mapping";
		TranslationMappingConfData translationMappingConfData = null;
		try{
			Session session = getSession();

			Criteria criteria = null;
			criteria = session.createCriteria(TranslationMappingConfData.class);
			translationMappingConfData=(TranslationMappingConfData) criteria.add(Restrictions.eq(propertyName, propertyValue)).uniqueResult();
			
			if (translationMappingConfData == null) {
				throw new InvalidValueException("Translation Mapping configuration not found");
			}
			
			String translationMappingId = translationMappingConfData.getTranslationMapConfigId();
			
			criteria = session.createCriteria(DummyResponseParameterData.class);
			List<TranslationMappingConfData> dummyResponseParameterDataList =  criteria.add(Restrictions.eq(TRANSLATION_MAP_CONFIG_ID, translationMappingId)).list();
			deleteObjectList(dummyResponseParameterDataList, session);

			criteria = session.createCriteria(TranslationMappingInstData.class);
			List<TranslationMappingInstData> translationMappingInstDataList = criteria.add(Restrictions.eq(TRANSLATION_MAP_CONFIG_ID, translationMappingId)).list();
			if (Collectionz.isNullOrEmpty(translationMappingInstDataList) == false) {
				for (Iterator<TranslationMappingInstData> iterator = translationMappingInstDataList.iterator(); iterator.hasNext();) {
					TranslationMappingInstData translationMappingInstData = iterator.next();
					criteria = session.createCriteria(TranslationMappingInstDetailRelData.class);
					List<TranslationMappingInstDetailRelData> translationMappingInstDetailRelDataList = criteria.add(Restrictions.eq("mappingInstanceId", translationMappingInstData.getMappingInstanceId())).list();
					deleteObjectList(translationMappingInstDetailRelDataList, session);
				}
			}

			criteria = session.createCriteria(TranslationMappingInstDetailData.class);
			List<TranslationMappingInstDetailData> translationMappingInstDetailDataList=criteria.add(Restrictions.eq(TRANSLATION_MAP_CONFIG_ID, translationMappingId)).list();
			deleteObjectList(translationMappingInstDetailDataList, session);
			deleteObjectList(translationMappingInstDataList, session);
			session.delete(translationMappingConfData);
			session.flush();

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + translationMappingName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()));
		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to delete " + translationMappingName + ", Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to delete " + translationMappingName + ", Reason: " + exp.getMessage(), exp);
		}
		
		return translationMappingConfData.getName();
	}
	
	private void update(TranslationMappingConfData translationMappingConfData, IStaffData staffData, String propertyName, Object porpertyValue) throws DataManagerException {
		EliteAssert.notNull(translationMappingConfData,"translationMappingConfData must not be null.");
		try {
			Session session = getSession();
			TranslationMappingConfData translationMappingDetailData = null;

			Criteria  criteria = session.createCriteria(TranslationMappingConfData.class).add(Restrictions.eq(propertyName, porpertyValue));
			translationMappingDetailData =(TranslationMappingConfData)criteria.uniqueResult();

			if (translationMappingDetailData == null) {
				throw new InvalidValueException("Translation Mapping configuration not found");
			}

			String fromValue = translationMappingConfData.getFromType();
			String toValue = translationMappingConfData.getToType();
			if (translationMappingDetailData.getFromType().equals(fromValue) == false || translationMappingDetailData.getToType().equals(toValue) == false) {
				throw new DataValidationException("Translation Type can not be changed during update operation");
			}

			String translationMappingId = translationMappingDetailData.getTranslationMapConfigId();

			//delete all details
			criteria = session.createCriteria(DummyResponseParameterData.class);
			List<DummyResponseParameterData> dummyResponseParameterDataList = criteria.add(Restrictions.eq(TRANSLATION_MAP_CONFIG_ID, translationMappingId)).list();
			List<DummyResponseParameterData> dummyResponseDataList = new LinkedList<DummyResponseParameterData>();
			if(dummyResponseParameterDataList != null){
				for(DummyResponseParameterData dummyData:dummyResponseParameterDataList){
					dummyResponseDataList.add(dummyData);
				}
			}

			//set dummy data to old translation mapping object
			translationMappingDetailData.setDummyResponseParameterDataList(dummyResponseDataList);

			deleteObjectList(dummyResponseParameterDataList, session);

			criteria = session.createCriteria(TranslationMappingInstData.class);
			List<TranslationMappingInstData> translationMappingInstDataList = criteria.add(Restrictions.eq(TRANSLATION_MAP_CONFIG_ID, translationMappingId)).list();
			List<TranslationMappingInstData> translationMappingInstList = new LinkedList<TranslationMappingInstData>();

			if(Collectionz.isNullOrEmpty(translationMappingInstDataList) == false) {
				for (Iterator<TranslationMappingInstData> iterator = translationMappingInstDataList.iterator(); iterator.hasNext();) {
					TranslationMappingInstData translationMappingInstData = iterator.next();
				
					List<TranslationMappingInstDetailData> oldTranslationMappingList= new LinkedList<TranslationMappingInstDetailData>();

					criteria = session.createCriteria(TranslationMappingInstDetailRelData.class);
					List<TranslationMappingInstDetailRelData> translationMappingInstDetailRelDataList=criteria.add(Restrictions.eq("mappingInstanceId", translationMappingInstData.getMappingInstanceId())).list();

					for(TranslationMappingInstDetailRelData translationMappingInstDetailRelData:translationMappingInstDetailRelDataList){
						criteria = session.createCriteria(TranslationMappingInstDetailData.class);

						TranslationMappingInstDetailData translationMappingInstDetailData=(TranslationMappingInstDetailData) criteria.add(Restrictions.eq(TRANSLATION_MAP_CONFIG_ID, translationMappingId)).add(Restrictions.eq("defaultMapping", TranslationMappingConfigConstants.NO)).add(Restrictions.eq("detailId", translationMappingInstDetailRelData.getDetailId())).uniqueResult();
						if(translationMappingInstDetailData!=null){
							oldTranslationMappingList.add(translationMappingInstDetailData);
						}
					}
					translationMappingInstData.setTranslationMappingInstDetailDataList(oldTranslationMappingList);

					translationMappingInstList.add(translationMappingInstData);
					deleteObjectList(translationMappingInstDetailRelDataList, session);
				}
			}

			translationMappingDetailData.setTranslationMappingInstDataList(translationMappingInstList);

			criteria = session.createCriteria(TranslationMappingInstDetailData.class);
			List<TranslationMappingInstDetailData> defaultTranslationMappingDetailDataList = criteria.add(Restrictions.eq(TRANSLATION_MAP_CONFIG_ID, translationMappingId)).add(Restrictions.eq("defaultMapping", TranslationMappingConfigConstants.YES)).list();

			translationMappingDetailData.setDefaultTranslationMappingDetailDataList(defaultTranslationMappingDetailDataList);

			criteria = session.createCriteria(TranslationMappingInstDetailData.class);
			List<TranslationMappingInstDetailData> translationMappingList = criteria.add(Restrictions.eq(TRANSLATION_MAP_CONFIG_ID, translationMappingId)).add(Restrictions.eq("defaultMapping", TranslationMappingConfigConstants.NO)).list();

			translationMappingDetailData.setTranslationMappingList(translationMappingList);

			criteria = session.createCriteria(TranslationMappingInstDetailData.class);
			List<TranslationMappingInstDetailData> translationMappingInstDetailDataList = criteria.add(Restrictions.eq(TRANSLATION_MAP_CONFIG_ID, translationMappingId)).list();

			JSONArray jsonArray=ObjectDiffer.diff(translationMappingDetailData, translationMappingConfData);
			
			if (Strings.isNullOrEmpty(translationMappingConfData.getName()) == false) {
				translationMappingDetailData.setName(translationMappingConfData.getName()); 
			}
			if (Strings.isNullOrEmpty(translationMappingConfData.getScript()) == false) {
				translationMappingDetailData.setScript(translationMappingConfData.getScript());
			}

			translationMappingDetailData.setDummyResponse(translationMappingConfData.getDummyResponse());
			translationMappingDetailData.setBaseTranslationMapConfigId(translationMappingConfData.getBaseTranslationMapConfigId());

			session.save(translationMappingDetailData);
			session.flush();

			deleteObjectList(translationMappingInstDetailDataList, session);

			deleteObjectList(translationMappingInstDataList, session);

			/* update instance */
			List<TranslationMappingInstData> transMappingInstDataList = translationMappingConfData.getTranslationMappingInstDataList();
			List<DummyResponseParameterData> dummyResponseParameterDataSet = translationMappingConfData.getDummyResponseParameterDataList();
			List<TranslationMappingInstDetailData> defaultTranslationMappingDataList = translationMappingConfData.getDefaultTranslationMappingDetailDataList();

			if (Collectionz.isNullOrEmpty(defaultTranslationMappingDataList) == false) {
				long orderNumber = 1;
				for(Iterator<TranslationMappingInstDetailData> iterator = defaultTranslationMappingDataList.iterator();iterator.hasNext();){
					TranslationMappingInstDetailData defaultDetailData = iterator.next();
					defaultDetailData.setTranslationMapConfigId(translationMappingId);
					defaultDetailData.setOrderNumber(orderNumber);
					session.save(defaultDetailData);
					session.flush();
					orderNumber++;
				}
			}

			int orderNumber = 1;
			if (Collectionz.isNullOrEmpty(dummyResponseParameterDataSet) == false) {
				for(Iterator<DummyResponseParameterData> iterator = dummyResponseParameterDataSet.iterator();iterator.hasNext();){
					DummyResponseParameterData dummyResponseParameterData = iterator.next();
					dummyResponseParameterData.setTranslationMapConfigId(translationMappingId);
					dummyResponseParameterData.setOrderNumber(orderNumber);
					session.save(dummyResponseParameterData);
					session.flush();
					orderNumber++;
				}
			}

			/* save Mapping Instance*/
			if (Collectionz.isNullOrEmpty(transMappingInstDataList) == false) {
				Iterator<TranslationMappingInstData> iterator  = transMappingInstDataList.iterator();
				orderNumber = 1;
				while(iterator.hasNext()){
					TranslationMappingInstData instData = iterator.next();
					instData.setTranslationMapConfigId(translationMappingId);
					List<TranslationMappingInstDetailData> transMappingInstDetailDataList = instData.getTranslationMappingInstDetailDataList();
					instData.setTranslationMappingInstDetailDataList(null);
					instData.setOrderNumber(orderNumber);
					session.save(instData);
					session.flush();
					orderNumber++;
					if(instData.getDefaultMapping().equals(TranslationMappingConfigConstants.NO)){
						if (Collectionz.isNullOrEmpty(transMappingInstDetailDataList) == false) {
							Iterator<TranslationMappingInstDetailData> detailIterator  = transMappingInstDetailDataList.iterator();
							long orderNo = 1;
							while(detailIterator.hasNext()){
								TranslationMappingInstDetailData detailData = detailIterator.next();
								detailData.setDefaultMapping(TranslationMappingConfigConstants.NO);
								detailData.setTranslationMapConfigId(translationMappingId);
								detailData.setOrderNumber(orderNo);
								session.save(detailData);
								session.flush();

								TranslationMappingInstDetailRelData detailRelData = new TranslationMappingInstDetailRelData();
								detailRelData.setDetailId(detailData.getDetailId());
								detailRelData.setMappingInstanceId(instData.getMappingInstanceId());
								detailRelData.setOrderNumber(orderNo);
								session.save(detailRelData);
								session.flush();
								orderNo++;
							}
						}
					}else{
						if (Collectionz.isNullOrEmpty(defaultTranslationMappingDataList) == false) {
							for(Iterator<TranslationMappingInstDetailData> defaultIterator = defaultTranslationMappingDataList.iterator();defaultIterator.hasNext();){
								TranslationMappingInstDetailData defaultDetailData = defaultIterator.next();
								TranslationMappingInstDetailRelData detailRelData = new TranslationMappingInstDetailRelData();
								detailRelData.setDetailId(defaultDetailData.getDetailId());
								detailRelData.setMappingInstanceId(instData.getMappingInstanceId());
								detailRelData.setOrderNumber(defaultDetailData.getOrderNumber());
								session.save(detailRelData);
								session.flush();
							}
						}
					}

				}
			}

			//Auditing Parameters
			staffData.setAuditName(translationMappingDetailData.getName());
			staffData.setAuditId(translationMappingDetailData.getAuditUid());

			doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_TRANSLATION_MAPPING_CONFIG);
			session.flush();
		}  catch(ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update Translation Mapping " + translationMappingConfData.getName() + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update Translation Mapping " + translationMappingConfData.getName() + ", Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to update Translation Mapping " + translationMappingConfData.getName() + ", Reason: " + exp.getMessage(), exp);
		}

	}
	
	public void updateBasicDetail(TranslationMappingConfData translationMappingConfData,String translationMappingId,IStaffData staffData,String actionAlias)throws DataManagerException {
		try{
			Session session = getSession(); 
			Criteria criteria = session.createCriteria(TranslationMappingConfData.class).add(Restrictions.eq(TRANSLATION_MAP_CONFIG_ID, translationMappingConfData.getTranslationMapConfigId()));
			List<TranslationMappingConfData> list = criteria.list();
			if(Collectionz.isNullOrEmpty(list) == false){
				TranslationMappingConfData data = list.get(0);
				
				JSONArray jsonArray=ObjectDiffer.diff(data, translationMappingConfData);
				
				data.setName(translationMappingConfData.getName());
				data.setDescription(translationMappingConfData.getDescription());
				data.setScript(translationMappingConfData.getScript());
				
				if (data.getAuditUid() == null) {
					String auditId= UUIDGenerator.generate();
					data.setAuditUid(auditId);
					staffData.setAuditId(auditId);
				}
				
				session.update(data);
				
				doAuditingJson(jsonArray.toString(),staffData,actionAlias);
				
				session.flush();
			}
		} catch(ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update basic detail of Translation Mapping " + translationMappingConfData.getName() + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update basic detail of Translation Mapping " + translationMappingConfData.getName() + ", Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to update basic detail of Translation Mapping " + translationMappingConfData.getName() + ", Reason: " + exp.getMessage(), exp);
		}
		
	}
	
	public TranslationMappingConfData getTranslationMappingConfData(String translationMapConfigId) throws DataManagerException {
		
		TranslationMappingConfData translationMappingConfData = null;
		try{

			Session session = getSession();

			Criteria criteria = session.createCriteria(TranslationMappingConfData.class).add(Restrictions.eq(TRANSLATION_MAP_CONFIG_ID, translationMapConfigId));
			
			List<TranslationMappingConfData> translationMappingConfigList = criteria.list();
			if(translationMappingConfigList!=null && !translationMappingConfigList.isEmpty()){
				translationMappingConfData = translationMappingConfigList.get(0);
			}
		} catch(HibernateException hExp) { 
			hExp.printStackTrace();
			throw new DataManagerException("Failed to get Translation Mapping data, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to get Translation Mapping data, Reason: " + exp.getMessage(), exp);
		}
		return translationMappingConfData;
	}
	
	private TranslationMappingConfData getTranslationMappingConfDetailData(Object propertyValue, String propertyName) throws DataManagerException {
		String translationMappingName = (TRANSLATION_MAP_CONFIG_NAME.equals(propertyName)) ? (String) propertyValue : "Translation Mapping";
			TranslationMappingConfData translationMappingConfData = null;
			try{

				Session session = getSession();

				Criteria criteria = session.createCriteria(TranslationMappingConfData.class).add(Restrictions.eq(propertyName, propertyValue));

				translationMappingConfData = (TranslationMappingConfData) criteria.uniqueResult();
				
				if (translationMappingConfData == null) {
					throw new InvalidValueException("Translation Mapping configuration not found");
				}
				String translationMappingId = translationMappingConfData.getTranslationMapConfigId();

				criteria = session.createCriteria(TranslationMappingInstDetailData.class).add(Restrictions.eq(TRANSLATION_MAP_CONFIG_ID, translationMappingId)).add(Restrictions.eq("defaultMapping", TranslationMappingConfigConstants.YES)).addOrder(Order.asc("orderNumber"));
				translationMappingConfData.setDefaultTranslationMappingDetailDataList(criteria.list());

				criteria = session.createCriteria(DummyResponseParameterData.class).add(Restrictions.eq(TRANSLATION_MAP_CONFIG_ID, translationMappingId)).addOrder(Order.asc("orderNumber"));
				translationMappingConfData.setDummyResponseParameterDataList(new LinkedList<DummyResponseParameterData>(criteria.list()));

				criteria = session.createCriteria(TranslationMappingInstData.class).add(Restrictions.eq(TRANSLATION_MAP_CONFIG_ID, translationMappingId)).addOrder(Order.asc("orderNumber"));
				List<TranslationMappingInstData> translationMappingInstDataList  = criteria.list();

				translationMappingConfData.setTranslationMappingInstDataList(new LinkedList<TranslationMappingInstData>(translationMappingInstDataList));

				if (Collectionz.isNullOrEmpty(translationMappingInstDataList) == false) {

					for(TranslationMappingInstData translationMappingInstData : translationMappingInstDataList) {

						if(translationMappingInstData.getDefaultMapping() != null && TranslationMappingConfigConstants.NO.equals(translationMappingInstData.getDefaultMapping())){
							criteria = session.createCriteria(TranslationMappingInstDetailRelData.class).add(Restrictions.eq("mappingInstanceId", translationMappingInstData.getMappingInstanceId()));
							ProjectionList projectionList=Projections.projectionList();
							projectionList.add(Projections.property("detailId"));
							criteria.setProjection(projectionList);
							List detailIds = criteria.list();

							if (Collectionz.isNullOrEmpty(detailIds) == false) {
								criteria = session.createCriteria(TranslationMappingInstDetailData.class).add(Restrictions.in("detailId", detailIds)).addOrder(Order.asc("orderNumber"));
								List<TranslationMappingInstDetailData> detailDataList = criteria.list();
								translationMappingInstData.setTranslationMappingInstDetailDataList(new LinkedList<TranslationMappingInstDetailData>(detailDataList));
							} else {
								translationMappingInstData.setTranslationMappingInstDetailDataList(new LinkedList<TranslationMappingInstDetailData>());
							}

						}
					}
				}


			} catch (HibernateException hExp) {
				hExp.printStackTrace();
				throw new DataManagerException("Failed to retive "+ translationMappingName + ", Reason: "+ hExp.getMessage(), hExp);
			} catch(Exception exp) {
				exp.printStackTrace();
				throw new DataManagerException("Failed to retive "+ translationMappingName + ", Reason: "+ exp.getMessage(), exp);
			}
			return translationMappingConfData;
	}
	
	@Override
	public List<TranslationMappingConfData> getTranslationMappingConfigList(TranslationMappingConfData translationMappingConfData) throws DataManagerException {
		try{

			Session session = getSession();

			Criteria criteria = session.createCriteria(TranslationMappingConfData.class)
										.add(Restrictions.ne(TRANSLATION_MAP_CONFIG_ID, translationMappingConfData.getTranslationMapConfigId()))
										.add(Restrictions.eq("toType", translationMappingConfData.getToType()))
										.add(Restrictions.eq("fromType", translationMappingConfData.getFromType()));

			List<TranslationMappingConfData> translationMappingConfigList = criteria.list();

			return translationMappingConfigList;

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to get Translation Mapping list, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to get Translation Mapping list, Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override
	public List<TranslationMappingConfData> getRadiusToRadiusTranslationMappingList()
			throws DataManagerException {
		try{

			Session session = getSession();

			Criteria criteria = session.createCriteria(TranslationMappingConfData.class)
									   .createAlias("translatorTypeTo", "translatorTypeTo")
									   .add(Restrictions.eq("translatorTypeTo.name", "Radius"))
									   .createAlias("translatorTypeFrom", "translatorTypeFrom")
									   .add(Restrictions.eq("translatorTypeFrom.name", "Radius"));
										
			
			List<TranslationMappingConfData> translationMappingConfigList = criteria.list();

			return translationMappingConfigList;

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to get Radius to Radius Translation Mapping list, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to get Radius to Radius Translation Mapping list, Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override
	public List<TranslationMappingConfData> getDiaToDiaTranslationMapping() throws DataManagerException {
		try{

			Session session = getSession();

			Criteria criteria = session.createCriteria(TranslationMappingConfData.class)
									   .createAlias("translatorTypeTo", "translatorTypeTo")
									   .add(Restrictions.eq("translatorTypeTo.name", "Diameter"))
									   .createAlias("translatorTypeFrom", "translatorTypeFrom")
									   .add(Restrictions.eq("translatorTypeFrom.name", "Diameter"));
										
			
			List<TranslationMappingConfData> translationMappingConfigList = criteria.list();

			return translationMappingConfigList;

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to get Diameter to Diameter Translation Mapping list, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to get Diameter to Diameter Translation Mapping list, Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override
	public List<TranslationMappingConfData> getTranslationMappingList() throws DataManagerException {
		try{

			Session session = getSession();

			Criteria criteria = session.createCriteria(TranslationMappingConfData.class) ;
			List<TranslationMappingConfData> translationMappingConfigList = criteria.list();

			return translationMappingConfigList;

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to get Translation Mapping list, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to get Translation Mapping list, Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override
	public List<TranslationMappingConfData> getDiaToRadTranslationMappingList() throws DataManagerException {
		try{

			Session session = getSession();

			Criteria criteria = session.createCriteria(TranslationMappingConfData.class)
									   .add(Restrictions.eq("toType", "TTI0004"))
									   .add(Restrictions.eq("fromType", "TTI0001"));
										
			
			List<TranslationMappingConfData> translationMappingConfigList = criteria.list();
			
			Logger.getLogger().info(MODULE, "List Size is  :" + translationMappingConfigList.size());

			return translationMappingConfigList;

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to get Diameter to Radius Translation Mapping list, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to get Diameter to Radius Translation Mapping list, Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override
	public TranslationMappingConfData getTranslationMappingConfDataById(String translationMappingId) throws DataManagerException {
		return getTranslationMappingConfDetailData(translationMappingId, TRANSLATION_MAP_CONFIG_ID);
	}
	
	@Override
	public TranslationMappingConfData getTranslationMappingConfDataByName(String translationMappingName) throws DataManagerException {
		return getTranslationMappingConfDetailData(translationMappingName, TRANSLATION_MAP_CONFIG_NAME);
	}
	
	@Override
	public String deleteById(String translationMappingId) throws DataManagerException {
		return delete(TRANSLATION_MAP_CONFIG_ID, translationMappingId);
	}
	
	@Override
	public String deleteByName(String translationMappingName) throws DataManagerException {
		return delete(TRANSLATION_MAP_CONFIG_NAME, translationMappingName);
	}
	
	@Override
	public void updateById(TranslationMappingConfData translationMappingData, IStaffData staffData, String translationMapConfigId) throws DataManagerException {
		update(translationMappingData, staffData, TRANSLATION_MAP_CONFIG_ID, translationMapConfigId);
	}
	
	@Override
	public void updateByName(TranslationMappingConfData translationMappingData, IStaffData staffData, String translationMapConfigName) throws DataManagerException {
		update(translationMappingData, staffData, TRANSLATION_MAP_CONFIG_NAME, translationMapConfigName);
	}
}
