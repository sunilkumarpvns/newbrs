package com.elitecore.elitesm.hibernate.servermgr.copypacket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.CopyPacketTransMapConfDataManager;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketDummyResponseParameterData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationMapData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationMapDetailData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslatorTypeData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

import net.sf.json.JSONArray;

public class HCopyPacketTransMappingDataManager extends HBaseDataManager implements CopyPacketTransMapConfDataManager{

	private static final String COPY_PACKET_CONFIGURATION = "Copy Packet configuration ";
	private static final String COPY_PACKET_CONF_NAME = "name";
	private static final String COPY_PACKET_CONF_ID = "copyPacketTransConfId";
	private static final String MODULE = null;
	
	@Override
	public CopyPacketTranslationConfData getCopyPacketTransMapConfigData(String copyPacketTransConfId) throws DataManagerException {
		CopyPacketTranslationConfData copyPacketMapConfData = null;
		try{

			Session session = getSession();

			Criteria criteria = session.createCriteria(CopyPacketTranslationConfData.class).add(Restrictions.eq(COPY_PACKET_CONF_ID, copyPacketTransConfId));

			List<CopyPacketTranslationConfData> copyPacketTransMapConfList = criteria.list();
			if (Collectionz.isNullOrEmpty(copyPacketTransMapConfList) == false) {
				copyPacketMapConfData = copyPacketTransMapConfList.get(0);
			}
		} catch(HibernateException hExp) { 
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive Copy Packet configuration data, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Copy Packet configuration data, Reason: " + exp.getMessage(), exp);
		}
		return copyPacketMapConfData;

	}
	
	@Override
	public String create(Object obj) throws DataManagerException {
		CopyPacketTranslationConfData copyPacketTranslationConfData =  (CopyPacketTranslationConfData) obj;
		EliteAssert.notNull(copyPacketTranslationConfData, "Copy Packet Mapping Config Data can not be NULL");
		try {
			Session session = getSession();
			session.clear();
			List<CopyPacketTranslationMapData> copyPacketTransMapDataList = copyPacketTranslationConfData.getCopyPacketTransMapData();
			List<CopyPacketDummyResponseParameterData> dummyResponseParamList = copyPacketTranslationConfData.getDummyParameterData();

			String auditId= UUIDGenerator.generate();
			copyPacketTranslationConfData.setAuditUid(auditId);
			session.save(copyPacketTranslationConfData);
			
			//Adding dummyParameterResponse
			if (Collectionz.isNullOrEmpty(dummyResponseParamList) == false) {
				Integer orderNo = 1;
				for(Iterator<CopyPacketDummyResponseParameterData> iteratorForDummyResp = dummyResponseParamList.iterator();iteratorForDummyResp.hasNext();){
					CopyPacketDummyResponseParameterData dummyParamResponse = iteratorForDummyResp.next();
					dummyParamResponse.setCopyPacketTransConfId(copyPacketTranslationConfData.getCopyPacketTransConfId());
					dummyParamResponse.setOrderNumber(orderNo);
					session.save(dummyParamResponse);
					orderNo++;
				}
				copyPacketTranslationConfData.setDummyParameterData(dummyResponseParamList);
			}
			
			//Adding Copy Packet Mapping Instance Data
			if (Collectionz.isNullOrEmpty(copyPacketTransMapDataList) == false) {
				Integer orderNo = 1;
				for(Iterator<CopyPacketTranslationMapData> iteratorInst = copyPacketTransMapDataList.iterator();iteratorInst.hasNext();){
					CopyPacketTranslationMapData copyPacketMapInstData = iteratorInst.next();
					copyPacketMapInstData.setCopyPacketTransConfId(copyPacketTranslationConfData.getCopyPacketTransConfId());
					//Adding Copy Packet Map Instance Detail Data
					List<CopyPacketTranslationMapDetailData> copyPacketTransInstDetail = copyPacketMapInstData.getCopyPacketTransMapDetail();
					copyPacketMapInstData.setCopyPacketTransMapDetail(null);
					copyPacketMapInstData.setOrderNumber(orderNo);
					session.save(copyPacketMapInstData);
					orderNo++;
					
					if (Collectionz.isNullOrEmpty(copyPacketTransInstDetail) == false) {
						for(Iterator<CopyPacketTranslationMapDetailData> iteratorPacketDetail = copyPacketTransInstDetail.iterator(); iteratorPacketDetail.hasNext(); ){
							CopyPacketTranslationMapDetailData copyPacketTransMapDetailData = iteratorPacketDetail.next();
							copyPacketTransMapDetailData.setCopyPacketMappingId(copyPacketMapInstData.getCopyPacketMappingId());
							session.save(copyPacketTransMapDetailData);
						}
						copyPacketMapInstData.setCopyPacketTransMapDetail(copyPacketTransInstDetail);
					}
				}
				copyPacketTranslationConfData.setCopyPacketTransMapData(copyPacketTransMapDataList);
				
			}
			session.flush();
			session.clear();
		} catch(ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE+COPY_PACKET_CONFIGURATION + copyPacketTranslationConfData.getName() + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException hExp) {
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException(FAILED_TO_CREATE+COPY_PACKET_CONFIGURATION + copyPacketTranslationConfData.getName() + REASON + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(FAILED_TO_CREATE+COPY_PACKET_CONFIGURATION + copyPacketTranslationConfData.getName() + REASON + exp.getMessage(), exp);
		}
		return copyPacketTranslationConfData.getName();
	}
	
	@Override
	public PageList search(CopyPacketTranslationConfData copyPacketTranslationConfData, int pageNo, int pageSize) throws DataManagerException {
		Session session = getSession();
		Criteria criteria = session.createCriteria(CopyPacketTranslationConfData.class);
		PageList pageList = null;
		try{
			if (Strings.isNullOrEmpty(copyPacketTranslationConfData.getName()) == false) {
				criteria.add(Restrictions.ilike(COPY_PACKET_CONF_NAME, copyPacketTranslationConfData.getName(), MatchMode.ANYWHERE));
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
			throw new DataManagerException("Failed to search Copy Packet configuration, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to search Copy Packet configuration, Reason: " + exp.getMessage(), exp);
		}
	}

	private String delete(String propertyName, Object propertyValue) throws DataManagerException {
		String CopyPacketConfigName = (COPY_PACKET_CONF_NAME.equals(propertyName)) ? (String) propertyValue : "Copy Packet configuration";
		CopyPacketTranslationConfData copyPacketConfigData = null;

		try {
			Session session = getSession();
			String copyPacketMapConfId = null;
			Criteria criteria = null;	
			criteria = session.createCriteria(CopyPacketTranslationConfData.class);
			copyPacketConfigData = (CopyPacketTranslationConfData) criteria.add(Restrictions.eq(propertyName, propertyValue)).uniqueResult();

			if (copyPacketConfigData == null) {
				throw new InvalidValueException("Copy Packet configuration not found");
			}

			copyPacketMapConfId = copyPacketConfigData.getCopyPacketTransConfId();
			criteria = session.createCriteria(CopyPacketDummyResponseParameterData.class);
			List<CopyPacketDummyResponseParameterData> dummyResponseParameterDataList=criteria.add(Restrictions.eq(COPY_PACKET_CONF_ID, copyPacketMapConfId)).list();
			for(Iterator<CopyPacketDummyResponseParameterData> dummyIterator = dummyResponseParameterDataList.iterator();dummyIterator.hasNext();){
				CopyPacketDummyResponseParameterData dummyResponse = dummyIterator.next();
				session.delete(dummyResponse);
				session.flush();
			}
			criteria = session.createCriteria(CopyPacketTranslationMapData.class);
			List<CopyPacketTranslationMapData> copyPacketMappingInstDataList=criteria.add(Restrictions.eq(COPY_PACKET_CONF_ID, copyPacketMapConfId)).list();
			if (Collectionz.isNullOrEmpty(copyPacketMappingInstDataList) == false) {
				for (Iterator<CopyPacketTranslationMapData> iterator = copyPacketMappingInstDataList.iterator(); iterator.hasNext();) {
					CopyPacketTranslationMapData copyPacketTransMapInstData = iterator.next();
					criteria = session.createCriteria(CopyPacketTranslationMapDetailData.class);
					List<CopyPacketTranslationMapDetailData> copyPacketMapDetailData=criteria.add(Restrictions.eq("copyPacketMappingId", copyPacketTransMapInstData.getCopyPacketMappingId())).list();
					for(Iterator<CopyPacketTranslationMapDetailData> detailIterator = copyPacketMapDetailData.iterator();detailIterator.hasNext();){
						CopyPacketTranslationMapDetailData detailData = detailIterator.next();
						session.delete(detailData);
						session.flush();
					}
				}
			}
			for (Iterator<CopyPacketTranslationMapData> iteratorInstance = copyPacketMappingInstDataList.iterator(); iteratorInstance.hasNext();) {
				CopyPacketTranslationMapData instance = iteratorInstance.next();
				instance.setCopyPacketTransMapDetail(null);
				session.delete(instance);

			}

			copyPacketConfigData.setCopyPacketTransMapData(null);
			copyPacketConfigData.setDefaultMapping(null);
			copyPacketConfigData.setDummyParameterData(null);
			session.delete(copyPacketConfigData);
			session.flush();

		} catch(ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + CopyPacketConfigName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()));
		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to delete " + CopyPacketConfigName + ", Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to delete " + CopyPacketConfigName + ", Reason: " + exp.getMessage(), exp);
		}
		return copyPacketConfigData.getName();

	}

	private void update(CopyPacketTranslationConfData copyPacketTranslationConfData,IStaffData staffData, String propertyName, Object porpertyValue) throws DataManagerException {
		EliteAssert.notNull(copyPacketTranslationConfData,"Copy Packet Mapping Config Data can not be NULL");
		try{
			Session session = getSession();
			CopyPacketTranslationConfData copyPacketConfData = null;
			String copyPacketTransConfId = null;
			Criteria  criteria = session.createCriteria(CopyPacketTranslationConfData.class).add(Restrictions.eq(propertyName, porpertyValue));
			copyPacketConfData =(CopyPacketTranslationConfData)criteria.uniqueResult();
			
			if (copyPacketConfData == null) {
				throw new InvalidValueException("Copy Packet configuration not found");
			}
			
			String fromValue = copyPacketTranslationConfData.getTransFromType();
			String toValue = copyPacketTranslationConfData.getTransToType();
			
			if (copyPacketConfData.getTransFromType().equals(fromValue) == false || copyPacketConfData.getTransToType().equals(toValue) == false) {
				throw new DataValidationException("Translation Type can not be changed during update operation");
			}
			
			JSONArray jsonArray=ObjectDiffer.diff(copyPacketConfData, copyPacketTranslationConfData);
			
			copyPacketTransConfId = copyPacketConfData.getCopyPacketTransConfId();
			
			if (Strings.isNullOrEmpty(copyPacketTranslationConfData.getName()) == false) {
				copyPacketConfData.setName(copyPacketTranslationConfData.getName()); 
			}
			if (Strings.isNullOrEmpty(copyPacketTranslationConfData.getScript()) == false) {
				copyPacketConfData.setScript(copyPacketTranslationConfData.getScript());
			}
			
			criteria = session.createCriteria(CopyPacketDummyResponseParameterData.class);
			
			List<CopyPacketDummyResponseParameterData> oldParamList = criteria.add(Restrictions.eq(COPY_PACKET_CONF_ID, copyPacketTransConfId)).list();
			if (Collectionz.isNullOrEmpty(oldParamList) == false) {
				for(Iterator<CopyPacketDummyResponseParameterData> dummyParamItrIterator = oldParamList.iterator(); dummyParamItrIterator.hasNext();){
					CopyPacketDummyResponseParameterData dummyParam = dummyParamItrIterator.next();
					session.delete(dummyParam);
					session.flush();
				}
			}
			copyPacketConfData.setDummyParameterData(null);
			criteria = session.createCriteria(CopyPacketTranslationMapData.class);
			List<CopyPacketTranslationMapData> oldCopyPacketMapInstanceList = criteria.add(Restrictions.eq(COPY_PACKET_CONF_ID,copyPacketTransConfId)).list();
			if (Collectionz.isNullOrEmpty(oldCopyPacketMapInstanceList) == false) {
				for(Iterator<CopyPacketTranslationMapData> iterate = oldCopyPacketMapInstanceList.iterator() ; iterate.hasNext();){
					CopyPacketTranslationMapData instanceData = iterate.next();
					criteria = session.createCriteria(CopyPacketTranslationMapDetailData.class);
					List<CopyPacketTranslationMapDetailData> detailList = criteria.add(Restrictions.eq("copyPacketMappingId", instanceData.getCopyPacketMappingId())).list();
					for(Iterator<CopyPacketTranslationMapDetailData> detailIterator = detailList.iterator();detailIterator.hasNext();){
						CopyPacketTranslationMapDetailData detail = detailIterator.next();
						session.delete(detail);
						session.flush();
					}
					instanceData.setCopyPacketTransMapDetail(null);
				}
				
				for(Iterator<CopyPacketTranslationMapData> iterate = oldCopyPacketMapInstanceList.iterator() ; iterate.hasNext();){
					CopyPacketTranslationMapData instanceData = iterate.next();
					session.delete(instanceData);
				}
				
			}
			copyPacketConfData.setCopyPacketTransMapData(null);
			
			List<CopyPacketDummyResponseParameterData> dummyParamList = copyPacketTranslationConfData.getDummyParameterData();
			if (Collectionz.isNullOrEmpty(dummyParamList) == false) {
				Integer orderNo = 1;
				for(CopyPacketDummyResponseParameterData paramData : dummyParamList){
					paramData.setCopyPacketTransConfId(copyPacketTransConfId);
					paramData.setOrderNumber(orderNo);
					session.save(paramData);
					orderNo++;
				}
				copyPacketConfData.setDummyParameterData(dummyParamList);
			}
			List<CopyPacketTranslationMapData> copyPacketTransMapDataList = copyPacketTranslationConfData.getCopyPacketTransMapData();
			if (Collectionz.isNullOrEmpty(copyPacketTransMapDataList) == false) {
				Integer orderNo = 1;
				for(Iterator<CopyPacketTranslationMapData> iteratorInst = copyPacketTransMapDataList.iterator();iteratorInst.hasNext();){
					CopyPacketTranslationMapData copyPacketMapInstData = iteratorInst.next();
					List<CopyPacketTranslationMapDetailData> copyPacketTransInstDetail = copyPacketMapInstData.getCopyPacketTransMapDetail();
					copyPacketMapInstData.setCopyPacketTransConfId(copyPacketTransConfId);
					copyPacketMapInstData.setOrderNumber(orderNo);
					session.save(copyPacketMapInstData);
					orderNo++;

					if (Collectionz.isNullOrEmpty(copyPacketTransInstDetail) == false) {
						for(Iterator<CopyPacketTranslationMapDetailData> iteratorPacketDetail = copyPacketTransInstDetail.iterator(); iteratorPacketDetail.hasNext(); ){
							CopyPacketTranslationMapDetailData copyPacketTransMapDetailData = iteratorPacketDetail.next();
							copyPacketTransMapDetailData.setCopyPacketMappingId(copyPacketMapInstData.getCopyPacketMappingId());
							session.save(copyPacketTransMapDetailData);
						}
						copyPacketMapInstData.setCopyPacketTransMapDetail(copyPacketTransInstDetail);
					}
				}
				copyPacketConfData.setCopyPacketTransMapData(copyPacketTransMapDataList);
			}
			
			session.flush();
			
			//Auditing Parameters
			staffData.setAuditName(copyPacketConfData.getName());
			staffData.setAuditId(copyPacketConfData.getAuditUid());
			doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_COPY_PACKET_TRANSLATION_MAPPING_CONFIG);
			
		} catch(ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update Copy Packet configuration " + copyPacketTranslationConfData.getName() + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update Copy Packet configuration " + copyPacketTranslationConfData.getName() + ", Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to update Copy Packet configuration " + copyPacketTranslationConfData.getName() + ", Reason: " + exp.getMessage(), exp);
		}

	}

	@Override
	public void updateBasicDetail(CopyPacketTranslationConfData copyPacketTranslationConfData, String copyPacketTransConfId, IStaffData staffData, String actionAlias) throws DataManagerException {
		try {
		Session session = getSession();
		Criteria criteria = session.createCriteria(CopyPacketTranslationConfData.class);
		List<CopyPacketTranslationConfData> list = criteria.add(Restrictions.eq(COPY_PACKET_CONF_ID, copyPacketTranslationConfData.getCopyPacketTransConfId())).list();
		if (Collectionz.isNullOrEmpty(list) == false) {
			CopyPacketTranslationConfData copyPacketTransMapConfData = list.get(0);
			JSONArray array = ObjectDiffer.diff(copyPacketTransMapConfData, copyPacketTranslationConfData);
			copyPacketTransMapConfData.setName(copyPacketTranslationConfData.getName());
			copyPacketTransMapConfData.setDescription(copyPacketTranslationConfData.getDescription());
			copyPacketTransMapConfData.setScript(copyPacketTranslationConfData.getScript());
			if(copyPacketTransMapConfData.getAuditUid() == null){
				String auditId= UUIDGenerator.generate();
				copyPacketTransMapConfData.setAuditUid(auditId);
				staffData.setAuditId(auditId);
			}
			session.update(copyPacketTransMapConfData);
			doAuditingJson(array.toString(), staffData, actionAlias);
			session.flush();
		}
		} catch(ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update basic detail of Copy Packet configuration " + copyPacketTranslationConfData.getName() + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update basic detail of Copy Packet configuration " + copyPacketTranslationConfData.getName() + ", Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to update basic detail of Copy Packet configuration " + copyPacketTranslationConfData.getName() + ", Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override
	public List<CopyPacketTranslationConfData> getCopyPacketTransMapConfigList(CopyPacketTranslationConfData copyPacketTranslationConfData) throws DataManagerException {
		try{

			Session session = getSession();

			Criteria criteria = session.createCriteria(CopyPacketTranslationConfData.class)
										.add(Restrictions.ne(COPY_PACKET_CONF_ID, copyPacketTranslationConfData.getCopyPacketTransConfId()))
										.add(Restrictions.eq("transToType", copyPacketTranslationConfData.getTransToType()))
										.add(Restrictions.eq("transFromType", copyPacketTranslationConfData.getTransFromType()));

			List<CopyPacketTranslationConfData> copyPacketMappingConfigList = criteria.list();

			return copyPacketMappingConfigList;

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive Copy Packet configuration list, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Copy Packet configuration list, Reason: " + exp.getMessage(), exp);
		}
	}

	private CopyPacketTranslationConfData getCopyPacketTransMapConfDetailData(Object propertyValue, String propertyName) throws DataManagerException {
		String CopyPacketConfigName = (COPY_PACKET_CONF_NAME.equals(propertyName)) ? (String) propertyValue : "Copy Packet configuration";
		CopyPacketTranslationConfData copyPacketMappingConfData = null;
		
		try {
			Session session = getSession();

			Criteria criteria = session.createCriteria(CopyPacketTranslationConfData.class).add(Restrictions.eq(propertyName, propertyValue));
			
			copyPacketMappingConfData = (CopyPacketTranslationConfData) criteria.uniqueResult();
			
			if (copyPacketMappingConfData == null) {
				throw new InvalidValueException("Copy Packet configuration not found");
			}
			
			String copyPacketTransConfId = copyPacketMappingConfData.getCopyPacketTransConfId();
			criteria = session.createCriteria(CopyPacketDummyResponseParameterData.class).add(Restrictions.eq(COPY_PACKET_CONF_ID, copyPacketTransConfId )).addOrder(Order.asc("orderNumber"));
			copyPacketMappingConfData.setDummyParameterData(new LinkedList<CopyPacketDummyResponseParameterData>(criteria.list()));
			
			criteria = session.createCriteria(CopyPacketTranslationMapData.class).add(Restrictions.eq(COPY_PACKET_CONF_ID, copyPacketTransConfId)).addOrder(Order.asc("orderNumber"));
			List<CopyPacketTranslationMapData> copyPacketMappingInstDataList  = criteria.list();
			
			copyPacketMappingConfData.setCopyPacketTransMapData(new LinkedList<CopyPacketTranslationMapData>(copyPacketMappingInstDataList));
			
			if (Collectionz.isNullOrEmpty(copyPacketMappingInstDataList) == false) {
				
				for (CopyPacketTranslationMapData copyPacketMappingInstData : copyPacketMappingInstDataList) {
					criteria = session.createCriteria(CopyPacketTranslationMapDetailData.class);
					List<CopyPacketTranslationMapDetailData> copyPacketInstDetailList = criteria.add(Restrictions.eq("copyPacketMappingId", copyPacketMappingInstData.getCopyPacketMappingId())).addOrder(Order.asc("orderNumber")).list();
					if (Collectionz.isNullOrEmpty(copyPacketInstDetailList) == false) {
						copyPacketMappingInstData.setCopyPacketTransMapDetail(new LinkedList<CopyPacketTranslationMapDetailData>(copyPacketInstDetailList));
					}

				}
			}
			
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive "+ CopyPacketConfigName + ", Reason: "+ hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive "+ CopyPacketConfigName + ", Reason: "+ exp.getMessage(), exp);
		}
		return copyPacketMappingConfData;
	}
	
	@Override
	public List<CopyPacketTranslationConfData> getCopyPacketTransMapConfigList(String toType, String fromType) throws DataManagerException {
		try{
			Session session = getSession();

			Criteria criteria = session.createCriteria(CopyPacketTranslationConfData.class).add(Restrictions.eq("transToType", toType)).
																				 add(Restrictions.eq("transFromType", fromType));

			List<CopyPacketTranslationConfData> copyPacketMappingConfigList = criteria.list();

			return copyPacketMappingConfigList;

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive Copy Packet configuration data for given To and From Translation Type, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Copy Packet configuration data data for given To and From Translation Type, Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override
	public TranslatorTypeData getTranslatorTypeData(String translatorTypeId) throws DataManagerException {
		try{

			Session session = getSession();
			Criteria criteria = session.createCriteria(TranslatorTypeData.class).add(Restrictions.eq("translatorTypeId", translatorTypeId));
			TranslatorTypeData translatorTypeData = (TranslatorTypeData) criteria.uniqueResult();
			return translatorTypeData;

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed retrive Translation Type data of Copy Packet configuration, Reason: " + hExp.getMessage(), hExp);
		}  catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed retrive Translation Type data of Copy Packet configuration, Reason: " + exp.getMessage(), exp);
		}
	}
	
	public List<TranslatorTypeData> getToTranslatorTypeData() throws DataManagerException {
		try{

			Session session = getSession();

			Criteria criteria = session.createCriteria(TranslatorTypeData.class).add(Restrictions.eq("translateTo", "Y"));
			List<TranslatorTypeData> translationTypeDataList = criteria.list();
			List<TranslatorTypeData> translatorTypeData = new ArrayList<TranslatorTypeData>();
			for(TranslatorTypeData toType : translationTypeDataList){
				if(toType.getValue().equals("DIAMETER") || toType.getValue().equals("RADIUS")){
					translatorTypeData.add(toType);
				}
			}
			return translatorTypeData;
		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive To Translation Type list of Copy Packet configuration, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive To Translation Type list of Copy Packet configuration, Reason: " + exp.getMessage(), exp);
		}
	}
	
	public List<TranslatorTypeData> getFromTranslatorTypeData() throws DataManagerException {
		try{

			Session session = getSession();

			Criteria criteria = session.createCriteria(TranslatorTypeData.class).add(Restrictions.eq("translateFrom", "Y"));
			
			List<TranslatorTypeData> translationTypeDataList = criteria.list();
			
			List<TranslatorTypeData> translatorTypeData = new ArrayList<TranslatorTypeData>();
			for(TranslatorTypeData forType : translationTypeDataList){
				if(forType.getValue().equals("DIAMETER") || forType.getValue().equals("RADIUS")){
					translatorTypeData.add(forType);
				}
			}
			return translatorTypeData;

		} catch(HibernateException hExp) { 
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive From Translation Type list of Copy Packet configuration, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) { 
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive From Translation Type list of Copy Packet configuration, Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override
	public List<CopyPacketTranslationConfData> getCopyPacketMappingList() throws DataManagerException {
		try{

			Session session = getSession();

			Criteria criteria = session.createCriteria(CopyPacketTranslationConfData.class) ;
			List<CopyPacketTranslationConfData> copyPacketTranslationMappingConfigList = criteria.list();

			return copyPacketTranslationMappingConfigList;

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive Copy Packet configuration list, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Copy Packet configuration list, Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override
	public List<CopyPacketTranslationConfData> getdiaTodiaCopyPacketMapping() throws DataManagerException {
		try{

			Session session = getSession();

			Criteria criteria = session.createCriteria(CopyPacketTranslationConfData.class)
									   .createAlias("translatorTypeTo", "translatorTypeTo")
									   .add(Restrictions.eq("translatorTypeTo.name", "Diameter"))
									   .createAlias("translatorTypeFrom", "translatorTypeFrom")
									   .add(Restrictions.eq("translatorTypeFrom.name", "Diameter"));
										
			
			List<CopyPacketTranslationConfData> copyPacketMappingConfigList = criteria.list();
			return copyPacketMappingConfigList;

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive Diameter to Diameter Copy Packet configuration list, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Diameter to Diameter Copy Packet configuration list, Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override
	public List<CopyPacketTranslationConfData> getdiaToradCopyPacketMapping() throws DataManagerException {
		try{

			Session session = getSession();

			Criteria criteria = session.createCriteria(CopyPacketTranslationConfData.class)
									   .createAlias("translatorTypeTo", "translatorTypeTo")
									   .add(Restrictions.eq("translatorTypeTo.name", "Diameter"))
									   .createAlias("translatorTypeFrom", "translatorTypeFrom")
									   .add(Restrictions.eq("translatorTypeFrom.name", "Radius"));
										
			
			List<CopyPacketTranslationConfData> copyPacketMappingConfigList = criteria.list();
			return copyPacketMappingConfigList;

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive Diameter to Radius Copy Packet configuration list, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Diameter to Radius Copy Packet configuration list, Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override
	public CopyPacketTranslationConfData getCopyPacketTransMapConfDetailDataById(String copyPacketConfigId) throws DataManagerException {
		return getCopyPacketTransMapConfDetailData(copyPacketConfigId, COPY_PACKET_CONF_ID);
	}
	
	@Override
	public CopyPacketTranslationConfData getCopyPacketTransMapConfDetailDataByName(String copyPacketConfigName) throws DataManagerException {
		return getCopyPacketTransMapConfDetailData(copyPacketConfigName, COPY_PACKET_CONF_NAME);
	}
	
	@Override
	public String deleteById(String copyPacketConfigId) throws DataManagerException {
		return delete(COPY_PACKET_CONF_ID, copyPacketConfigId);
	}
	
	@Override
	public String deleteByName(String copyPacketConfigName) throws DataManagerException {
		return delete(COPY_PACKET_CONF_NAME, copyPacketConfigName);
	}
	
	@Override
	public void updateById(CopyPacketTranslationConfData copyPacketData, IStaffData staffData, String copyPacketConfigId) throws DataManagerException {
		update(copyPacketData, staffData, COPY_PACKET_CONF_ID, copyPacketConfigId);
		
	}
	
	@Override
	public void updateByName(CopyPacketTranslationConfData copyPacketData, IStaffData staffData, String copyPacketConfigName) throws DataManagerException {
		update(copyPacketData, staffData, COPY_PACKET_CONF_NAME, copyPacketConfigName);
	}

}
