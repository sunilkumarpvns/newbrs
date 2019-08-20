package com.elitecore.elitesm.hibernate.sessionmanager;

import java.util.ArrayList;
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
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.sessionmanager.SessionManagerDataManager;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISMConfigInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SMConfigInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SMDBFieldMapData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SMSessionCloserESIRelData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SessionManagerInstanceData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ExternalSystemConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

import net.sf.json.JSONArray;

public class HSessionManagerDataManager extends HBaseDataManager implements SessionManagerDataManager {

	private static final String SM_NAME = "name";
	private static final String SM_CONFIG_ID = "smConfigId";
	private static final String SM_INSTANCE_ID = "smInstanceId";
	private static final String MODULE = "HSessionManagerDataManager";

	@Override
	public String create(Object obj) throws DataManagerException
	{
		ISessionManagerInstanceData sessionManagerInstanceData = (ISessionManagerInstanceData) obj;
		try{
			// entry into tblmsessionmanagerinstance
			Session session=getSession();
			session.clear();
			sessionManagerInstanceData.setStatus(BaseConstant.SHOW_STATUS_ID);

			String auditId= UUIDGenerator.generate();
			sessionManagerInstanceData.setAuditUId(auditId);

			session.save(sessionManagerInstanceData);
			session.flush();
			session.clear();

			// entry into tblmsmconfiginstance data 

			ISMConfigInstanceData smConfigInstanceData = sessionManagerInstanceData.getSmConfigInstanceData();
			smConfigInstanceData.setSmInstanceId(sessionManagerInstanceData.getSmInstanceId());
			session.save(smConfigInstanceData);
			
			// entry into ESI
			List<SMSessionCloserESIRelData> smSessionCloserESIRelDataList = smConfigInstanceData.getSmSessionCloserESIRelDataList();

			if(Collectionz.isNullOrEmpty(smSessionCloserESIRelDataList) == false){
				for(int index=0;index<smSessionCloserESIRelDataList.size();index++){

					SMSessionCloserESIRelData smSessionCloserESIRelData = smSessionCloserESIRelDataList.get(index);
					smSessionCloserESIRelData.setSmConfigId(smConfigInstanceData.getSmConfigId());
					session.save(smSessionCloserESIRelData);
				}
			}
			
			// entry into tblmsmdbfieldmap;

			List<SMDBFieldMapData> smDBFieldMapList = smConfigInstanceData.getDbFieldMapDataList();
			int orderNumber = 1;
			if(Collectionz.isNullOrEmpty(smDBFieldMapList) == false){
				for(int index=0;index<smDBFieldMapList.size();index++){

					SMDBFieldMapData smDBFieldMap = smDBFieldMapList.get(index);
					smDBFieldMap.setSmConfigId(smConfigInstanceData.getSmConfigId());
					smDBFieldMap.setOrderNumber(orderNumber++);
					session.save(smDBFieldMap);
				}
			}
			
			

			List<SMDBFieldMapData> smMandatoryFieldMapList = smConfigInstanceData.getLstMandatoryFieldMapData();
			if(Collectionz.isNullOrEmpty(smMandatoryFieldMapList) == false){
				orderNumber = 1;
				int size = smMandatoryFieldMapList.size();
				for(int index=0;index<size;index++){
					SMDBFieldMapData smDBFieldMap = smMandatoryFieldMapList.get(index);
					smDBFieldMap.setSmConfigId(smConfigInstanceData.getSmConfigId());
					smDBFieldMap.setOrderNumber(orderNumber++);
					session.save(smDBFieldMap);
				}
			}

			session.flush();
			session.clear();

		}catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE+ sessionManagerInstanceData.getName() + REASON 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve.getCause());
		}catch(HibernateException hExp){
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException(FAILED_TO_CREATE+ sessionManagerInstanceData.getName() + REASON + hExp.getMessage(), hExp);
		}catch(Exception exp){
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(FAILED_TO_CREATE+ sessionManagerInstanceData.getName() + REASON + exp.getMessage(),exp);
		}
		return sessionManagerInstanceData.getName();
	}

	public PageList search(ISessionManagerInstanceData sessionManagerInstance,int requiredPageNo, Integer pageSize) throws DataManagerException
	{
		PageList pageList = null;

		try{
			Session session = getSession();

			Criteria criteria = session.createCriteria(SessionManagerInstanceData.class);


			if((sessionManagerInstance.getName() != null && sessionManagerInstance.getName().length()>0 )){
				criteria.add(Restrictions.ilike(SM_NAME,sessionManagerInstance.getName()));
			}

			int totalItems = criteria.list().size();
			criteria.setFirstResult(((requiredPageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}

			List sessionManagerList = criteria.list();


			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(sessionManagerList, requiredPageNo, totalPages ,totalItems);

		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to search Session Manager, Reason: "+ hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to search Session Manager, Reason: "+ exp.getMessage(), exp);
		}
		return pageList;
	}

	public List<ISessionManagerInstanceData> getSessionManagerInstanceList()
	throws DataManagerException {
		List<ISessionManagerInstanceData> sessionManagerInastanceDataList;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(SessionManagerInstanceData.class);
			sessionManagerInastanceDataList= criteria.list();
			return sessionManagerInastanceDataList;
		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
	}

	public ISMConfigInstanceData getSMConfigInstanceData(String smConfigId) throws DataManagerException {
		ISMConfigInstanceData smConfigInstanceData=null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(SMConfigInstanceData.class).add(Restrictions.eq(SM_CONFIG_ID, smConfigId));
			List smConfigInstanceDataList= criteria.list();
			if(smConfigInstanceDataList!=null && !smConfigInstanceDataList.isEmpty()){
				smConfigInstanceData = (ISMConfigInstanceData)smConfigInstanceDataList.get(0);
			}
		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
		return smConfigInstanceData;
	}
	public ISMConfigInstanceData getSMConfigInstanceData(ISessionManagerInstanceData sessionManagerInstanceData) throws DataManagerException{
		ISMConfigInstanceData smConfigInstanceData=null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(SMConfigInstanceData.class).add(Restrictions.eq(SM_INSTANCE_ID, sessionManagerInstanceData.getSmInstanceId()));
			List smConfigInstanceDataList= criteria.list();
			if(smConfigInstanceDataList!=null && !smConfigInstanceDataList.isEmpty()){
				smConfigInstanceData = (ISMConfigInstanceData)smConfigInstanceDataList.get(0);
			}
		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
		return smConfigInstanceData;
	}

	public void updateSessionManagerBasicDetails(ISessionManagerInstanceData sessionManagerInstanceData,IStaffData staffData,String actionAlias) throws DataManagerException {
		SessionManagerInstanceData smInstanceData = null;
		try{
			Session session=getSession();
			Criteria criteria=session.createCriteria(SessionManagerInstanceData.class);
			criteria.add(Restrictions.eq(SM_INSTANCE_ID,sessionManagerInstanceData.getSmInstanceId()));
			SessionManagerInstanceData sessionManagerData=(SessionManagerInstanceData)sessionManagerInstanceData;
			
			smInstanceData=(SessionManagerInstanceData)criteria.uniqueResult();
		
			if(smInstanceData == null){
				throw new InvalidValueException("Session Manager Data not found");
			}
			JSONArray jsonArray=ObjectDiffer.diff(smInstanceData, sessionManagerData);
			
			smInstanceData.setLastmodifiedbystaffid(sessionManagerInstanceData.getLastmodifiedbystaffid());
			smInstanceData.setLastmodifieddate(sessionManagerInstanceData.getLastmodifieddate());
			smInstanceData.setName(sessionManagerInstanceData.getName());
			smInstanceData.setDescription(sessionManagerInstanceData.getDescription());
			
			session.update(smInstanceData);
			session.flush();
			
			staffData.setAuditId(smInstanceData.getAuditUId());
			staffData.setAuditName(smInstanceData.getName());
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);

		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update " + smInstanceData.getName() + ", Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}
		catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update " + smInstanceData.getName() + ", Reason: " +hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update " + smInstanceData.getName() + ", Reason: " +exp.getMessage(), exp);
		}
	}

	public void updateSessionManagerDetails(ISessionManagerInstanceData sessionManagerInstanceData,IStaffData staffData,String actionAlias)	throws DataManagerException  {
		SessionManagerInstanceData smInstanceData = null;
		try{
			Session session=getSession();
			Criteria criteria=session.createCriteria(SessionManagerInstanceData.class);
			
			criteria.add(Restrictions.eq(SM_INSTANCE_ID,sessionManagerInstanceData.getSmInstanceId()));
			SessionManagerInstanceData newData=(SessionManagerInstanceData)sessionManagerInstanceData;
			
			smInstanceData=(SessionManagerInstanceData)criteria.uniqueResult();
			
			if(smInstanceData == null){
				throw new InvalidValueException("Session Manager Data not found");
			}

			SMConfigInstanceData smConfigInstanceData = (SMConfigInstanceData) session.createCriteria(SMConfigInstanceData.class)
					.add(Restrictions.eq(SM_INSTANCE_ID,smInstanceData.getSmInstanceId())).uniqueResult();
			
			if(smConfigInstanceData == null){
				throw new InvalidValueException("Session Manager Data not found");
			}
			smInstanceData.setSmConfigInstanceData(smConfigInstanceData);
			
			JSONArray jsonArray=ObjectDiffer.diff(smInstanceData, newData);
			
			smInstanceData.setLastmodifiedbystaffid(sessionManagerInstanceData.getLastmodifiedbystaffid());
			smInstanceData.setLastmodifieddate(sessionManagerInstanceData.getLastmodifieddate());
			

			Criteria dbFieldmapCriteria = session.createCriteria(SMDBFieldMapData.class);
			dbFieldmapCriteria.add(Restrictions.eq(SM_CONFIG_ID,sessionManagerInstanceData.getSmConfigInstanceData().getSmConfigId()));

			//delete
			List<SMDBFieldMapData> fieldMapList=dbFieldmapCriteria.list();
			deleteObjectList(fieldMapList, session);
			session.flush();	
			
			//save
			List<SMDBFieldMapData> mandatoryMappingFieldList = sessionManagerInstanceData
					.getSmConfigInstanceData().getLstMandatoryFieldMapData();

			int orderNumber = 1;
			for (Iterator iterator = mandatoryMappingFieldList.iterator(); iterator.hasNext();) {
				SMDBFieldMapData fieldMapData = (SMDBFieldMapData) iterator.next();
				fieldMapData.setSmConfigId(smConfigInstanceData.getSmConfigId());
				fieldMapData.setOrderNumber(orderNumber++);
				session.save(fieldMapData);
				session.flush();
			}
			
			List<SMDBFieldMapData> additionalMappingFieldList = sessionManagerInstanceData
					.getSmConfigInstanceData().getDbFieldMapDataList();
			orderNumber = 1;
			for (Iterator iterator = additionalMappingFieldList.iterator(); iterator.hasNext();) {
				SMDBFieldMapData fieldMapData = (SMDBFieldMapData) iterator.next();
				fieldMapData.setOrderNumber(orderNumber++);
				session.save(fieldMapData);
				session.flush();
			}
			
			//update smconfiginstance data
			Criteria sessionCloserESIRelCriteria=session.createCriteria(SMSessionCloserESIRelData.class);
			sessionCloserESIRelCriteria.add(Restrictions.eq(SM_CONFIG_ID,sessionManagerInstanceData.getSmConfigInstanceData().getSmConfigId()));
			List<SMDBFieldMapData> esiRelList=sessionCloserESIRelCriteria.list();
			deleteObjectList(esiRelList, session);
			
			//save
			List<SMSessionCloserESIRelData> sessionCloserESIRelList =sessionManagerInstanceData.getSmConfigInstanceData().getSmSessionCloserESIRelDataList();
			if(Collectionz.isNullOrEmpty(sessionCloserESIRelList) == false){
				for (Iterator<SMSessionCloserESIRelData> iterator = sessionCloserESIRelList.iterator(); iterator.hasNext();) {
					SMSessionCloserESIRelData relData =  iterator.next();
					relData.setSmConfigId(smConfigInstanceData.getSmConfigId());
					session.save(relData);
					session.flush();
				}
			} 
			
			session.flush();
			
			Criteria smConfigInstanceCriteria=session.createCriteria(SMConfigInstanceData.class);
			smConfigInstanceCriteria.add(Restrictions.eq(SM_CONFIG_ID,sessionManagerInstanceData.getSmConfigInstanceData().getSmConfigId()));
			ISMConfigInstanceData updatedSMConfigData=sessionManagerInstanceData.getSmConfigInstanceData();
			SMConfigInstanceData oldSMConfigdata=(SMConfigInstanceData)smConfigInstanceCriteria.uniqueResult();
			
			oldSMConfigdata.setDatabaseDatasourceId(updatedSMConfigData.getDatabaseDatasourceId());
			oldSMConfigdata.setTablename(updatedSMConfigData.getTablename());
			oldSMConfigdata.setAutoSessionCloser(updatedSMConfigData.getAutoSessionCloser());
			oldSMConfigdata.setSessiontimeout(updatedSMConfigData.getSessiontimeout());
			oldSMConfigdata.setCloseBatchCount(updatedSMConfigData.getCloseBatchCount());
			oldSMConfigdata.setSessionThreadSleepTime(updatedSMConfigData.getSessionThreadSleepTime());
			oldSMConfigdata.setSessionCloseAction(updatedSMConfigData.getSessionCloseAction());
			oldSMConfigdata.setIdentityField(updatedSMConfigData.getIdentityField());
			oldSMConfigdata.setIdSequenceName(updatedSMConfigData.getIdSequenceName());
			oldSMConfigdata.setStartTimeField(updatedSMConfigData.getStartTimeField());
			oldSMConfigdata.setLastUpdatedTimeField(updatedSMConfigData.getLastUpdatedTimeField());
			oldSMConfigdata.setSessionIdField(updatedSMConfigData.getSessionIdField());
			oldSMConfigdata.setSessionIdRefEntity(updatedSMConfigData.getSessionIdRefEntity());
			oldSMConfigdata.setServiceTypeField(updatedSMConfigData.getServiceTypeField());
			oldSMConfigdata.setGroupNameField(updatedSMConfigData.getGroupNameField());
			oldSMConfigdata.setSmInstanceId(updatedSMConfigData.getSmInstanceId());
			oldSMConfigdata.setConcurrencyIdentityField(updatedSMConfigData.getConcurrencyIdentityField());
			oldSMConfigdata.setSearchAttribute(updatedSMConfigData.getSearchAttribute());
			oldSMConfigdata.setBatchUpdateEnabled(updatedSMConfigData.getBatchUpdateEnabled());
			oldSMConfigdata.setBatchSize(updatedSMConfigData.getBatchSize());
			oldSMConfigdata.setBatchUpdateInterval(updatedSMConfigData.getBatchUpdateInterval());
			oldSMConfigdata.setDbQueryTimeOut(updatedSMConfigData.getDbQueryTimeOut());
			oldSMConfigdata.setBehaviour(updatedSMConfigData.getBehaviour());
			oldSMConfigdata.setDbfailureaction(updatedSMConfigData.getDbfailureaction());
			oldSMConfigdata.setSessionOverrideAction(updatedSMConfigData.getSessionOverrideAction());
			oldSMConfigdata.setSessionOverrideColumn(updatedSMConfigData.getSessionOverrideColumn());
			oldSMConfigdata.setSessionStopAction(updatedSMConfigData.getSessionStopAction());
			oldSMConfigdata.setDbFieldMapDataList(updatedSMConfigData.getDbFieldMapDataList());
			oldSMConfigdata.setSmSessionCloserESIRelDataList(updatedSMConfigData.getSmSessionCloserESIRelDataList());
			session.update(oldSMConfigdata);
			session.flush();
				
			staffData.setAuditId(smInstanceData.getAuditUId());
			staffData.setAuditName(smInstanceData.getName());
				
			session.update(smInstanceData);
			session.flush();

			if(jsonArray.size() != 0 ){
				doAuditingJson(jsonArray.toString(),staffData,actionAlias);
			}
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update " + smInstanceData.getName() + ", Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}
		catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update " + smInstanceData.getName() + ", Reason: " +hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update " + smInstanceData.getName() + ", Reason: " +exp.getMessage(), exp);
		}
	}

	@Override
	public void updateSessionManagerData(ISessionManagerInstanceData sessionManagerInstanceData,StaffData staffData,String name) throws DataManagerException {
		SessionManagerInstanceData smInstanceData = null;
		try{
			Session session=getSession();
			Criteria criteria=session.createCriteria(SessionManagerInstanceData.class);
			criteria.add(Restrictions.eq(SM_NAME,name));
			SessionManagerInstanceData sessionManagerData=(SessionManagerInstanceData)sessionManagerInstanceData;
			
			smInstanceData=(SessionManagerInstanceData)criteria.uniqueResult();
			
			if(smInstanceData == null){
				throw new InvalidValueException("Session Manager Data not found");
			}
			
			SMConfigInstanceData smConfigInstanceData = (SMConfigInstanceData) session.createCriteria(SMConfigInstanceData.class)
					.add(Restrictions.eq(SM_INSTANCE_ID,smInstanceData.getSmInstanceId())).uniqueResult();
			
			if(smConfigInstanceData == null){
				throw new InvalidValueException("Session Manager Data not found");
			}
			smInstanceData.setSmConfigInstanceData(smConfigInstanceData);
			
			List<SMDBFieldMapData> addtionalDBField = smInstanceData.getSmConfigInstanceData().getDbFieldMapDataList();
			List<SMDBFieldMapData> mandatoryDBFieldData = new ArrayList<SMDBFieldMapData>();
			if(Collectionz.isNullOrEmpty(addtionalDBField) == false){
				for (SMDBFieldMapData smdbFieldMapData : addtionalDBField) {
					if(smdbFieldMapData.getField() != null){
						mandatoryDBFieldData.add(smdbFieldMapData);
					}
				}
			}
			
			smInstanceData.getSmConfigInstanceData().setLstMandatoryFieldMapData(mandatoryDBFieldData);
		
			JSONArray jsonArray=ObjectDiffer.diff(smInstanceData, sessionManagerData);
			
			smInstanceData.setLastmodifiedbystaffid(sessionManagerInstanceData.getLastmodifiedbystaffid());
			smInstanceData.setLastmodifieddate(sessionManagerInstanceData.getLastmodifieddate());
			smInstanceData.setName(sessionManagerInstanceData.getName());
			smInstanceData.setDescription(sessionManagerInstanceData.getDescription());
			
			Criteria dbFieldmapCriteria = session.createCriteria(SMDBFieldMapData.class);
			dbFieldmapCriteria.add(Restrictions.eq(SM_CONFIG_ID,smConfigInstanceData.getSmConfigId()));

			//delete
			List<SMDBFieldMapData> fieldMapList=dbFieldmapCriteria.list();
			deleteObjectList(fieldMapList, session);
			session.flush();
				
			//save
			List<SMDBFieldMapData> mandatoryMappingFieldList = sessionManagerInstanceData
					.getSmConfigInstanceData().getLstMandatoryFieldMapData();
			 
			int orderNumber = 1;
			for (Iterator iterator = mandatoryMappingFieldList.iterator(); iterator.hasNext();) {
				SMDBFieldMapData fieldMapData = (SMDBFieldMapData) iterator.next();
				fieldMapData.setSmConfigId(smConfigInstanceData.getSmConfigId());
				fieldMapData.setOrderNumber(orderNumber++);
				session.save(fieldMapData);
				session.flush();
			}
			
			List<SMDBFieldMapData> additionalDBMappingFieldList = sessionManagerInstanceData
					.getSmConfigInstanceData().getDbFieldMapDataList();

			orderNumber = 1;
			for (Iterator iterator = additionalDBMappingFieldList.iterator(); iterator.hasNext();) {
				SMDBFieldMapData fieldMapData = (SMDBFieldMapData) iterator.next();
				fieldMapData.setSmConfigId(smConfigInstanceData.getSmConfigId());
				fieldMapData.setOrderNumber(orderNumber++);
				session.save(fieldMapData);
				session.flush();
			}

			//update smconfiginstance data
			
			Criteria sessionCloserESIRelCriteria=session.createCriteria(SMSessionCloserESIRelData.class);
			sessionCloserESIRelCriteria.add(Restrictions.eq(SM_CONFIG_ID,smInstanceData.getSmConfigInstanceData().getSmConfigId()));
			List<SMDBFieldMapData> esiRelList=sessionCloserESIRelCriteria.list();
			deleteObjectList(esiRelList, session);
			
			//save
			List<SMSessionCloserESIRelData> sessionCloserESIRelList =sessionManagerInstanceData.getSmConfigInstanceData().getSmSessionCloserESIRelDataList();
			if(Collectionz.isNullOrEmpty(sessionCloserESIRelList) == false){
				for (Iterator<SMSessionCloserESIRelData> iterator = sessionCloserESIRelList.iterator(); iterator.hasNext();) {
					SMSessionCloserESIRelData relData =  iterator.next();
					relData.setSmConfigId(smConfigInstanceData.getSmConfigId());
					session.save(relData);
					session.flush();
				}
			}
			
			session.flush();
			
			Criteria smConfigInstanceCriteria = session.createCriteria(SMConfigInstanceData.class);
			smConfigInstanceCriteria.add(Restrictions.eq(SM_CONFIG_ID,smInstanceData.getSmConfigInstanceData().getSmConfigId()));
			ISMConfigInstanceData updatedSMConfigData = sessionManagerInstanceData.getSmConfigInstanceData();
			SMConfigInstanceData oldSMConfigdata = (SMConfigInstanceData)smConfigInstanceCriteria.uniqueResult();
			
			oldSMConfigdata.setDatabaseDatasourceId(updatedSMConfigData.getDatabaseDatasourceId());
			oldSMConfigdata.setTablename(updatedSMConfigData.getTablename());
			oldSMConfigdata.setAutoSessionCloser(updatedSMConfigData.getAutoSessionCloser());
			oldSMConfigdata.setSessiontimeout(updatedSMConfigData.getSessiontimeout());
			oldSMConfigdata.setCloseBatchCount(updatedSMConfigData.getCloseBatchCount());
			oldSMConfigdata.setSessionThreadSleepTime(updatedSMConfigData.getSessionThreadSleepTime());
			oldSMConfigdata.setSessionCloseAction(updatedSMConfigData.getSessionCloseAction());
			oldSMConfigdata.setIdentityField(updatedSMConfigData.getIdentityField());
			oldSMConfigdata.setIdSequenceName(updatedSMConfigData.getIdSequenceName());
			oldSMConfigdata.setStartTimeField(updatedSMConfigData.getStartTimeField());
			oldSMConfigdata.setLastUpdatedTimeField(updatedSMConfigData.getLastUpdatedTimeField());
			oldSMConfigdata.setSessionIdField(updatedSMConfigData.getSessionIdField());
			oldSMConfigdata.setSessionIdRefEntity(updatedSMConfigData.getSessionIdRefEntity());
			oldSMConfigdata.setServiceTypeField(updatedSMConfigData.getServiceTypeField());
			oldSMConfigdata.setGroupNameField(updatedSMConfigData.getGroupNameField());
			oldSMConfigdata.setConcurrencyIdentityField(updatedSMConfigData.getConcurrencyIdentityField());
			oldSMConfigdata.setSearchAttribute(updatedSMConfigData.getSearchAttribute());
			oldSMConfigdata.setBatchUpdateEnabled(updatedSMConfigData.getBatchUpdateEnabled());
			oldSMConfigdata.setBatchSize(updatedSMConfigData.getBatchSize());
			oldSMConfigdata.setBatchUpdateInterval(updatedSMConfigData.getBatchUpdateInterval());
			oldSMConfigdata.setDbQueryTimeOut(updatedSMConfigData.getDbQueryTimeOut());
			oldSMConfigdata.setBehaviour(updatedSMConfigData.getBehaviour());
			oldSMConfigdata.setDbfailureaction(updatedSMConfigData.getDbfailureaction());
			oldSMConfigdata.setSessionOverrideAction(updatedSMConfigData.getSessionOverrideAction());
			oldSMConfigdata.setSessionOverrideColumn(updatedSMConfigData.getSessionOverrideColumn());
			oldSMConfigdata.setSessionStopAction(updatedSMConfigData.getSessionStopAction());
			oldSMConfigdata.setDbFieldMapDataList(updatedSMConfigData.getDbFieldMapDataList());
			oldSMConfigdata.setLstMandatoryFieldMapData(updatedSMConfigData.getLstMandatoryFieldMapData());
			oldSMConfigdata.setSmSessionCloserESIRelDataList(updatedSMConfigData.getSmSessionCloserESIRelDataList());
			session.update(oldSMConfigdata);
			session.flush();
				
			session.update(smInstanceData);
			session.flush();
			
			staffData.setAuditId(smInstanceData.getAuditUId());
			staffData.setAuditName(smInstanceData.getName());
			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_SESSION_MANAGER);
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update Session Manager , Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update Session Manager , Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update Session Manager , Reason: " + exp.getMessage(), exp);
		}
	}
	
	public List<SMSessionCloserESIRelData> getNASSessionCloserESIRelList(String smConfigId) throws DataManagerException {
		List<SMSessionCloserESIRelData> sessionCloserESIRelDataList= null;
		try{
			Session session=getSession();
			Criteria criteria=session.createCriteria(SMSessionCloserESIRelData.class).add(Restrictions.eq(SM_CONFIG_ID,smConfigId));
			criteria.createCriteria("externalSystemData").add(Restrictions.eq("esiTypeId",ExternalSystemConstants.NAS));
			sessionCloserESIRelDataList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return sessionCloserESIRelDataList;
	}

	public List<SMSessionCloserESIRelData> getAcctSessionCloserESIRelList(String smConfigId) throws DataManagerException {
		List<SMSessionCloserESIRelData> sessionCloserESIRelDataList= null;
		try{
			Session session=getSession();
			Criteria criteria=session.createCriteria(SMSessionCloserESIRelData.class).add(Restrictions.eq(SM_CONFIG_ID,smConfigId));
			criteria.createCriteria("externalSystemData").add(Restrictions.eq("esiTypeId",ExternalSystemConstants.ACCT_PROXY));
			sessionCloserESIRelDataList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return sessionCloserESIRelDataList;
	}

	public List<ISessionManagerInstanceData> getSessionManagerInstanceList(String smtype) throws DataManagerException {
		List<ISessionManagerInstanceData> sessionManagerInastanceDataList;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(SessionManagerInstanceData.class).add(Restrictions.eq("smtype", smtype));
			sessionManagerInastanceDataList= criteria.list();
			return sessionManagerInastanceDataList;
		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
	}

	@Override
	public ISessionManagerInstanceData getSessionManagerInstanceDataById(String sessionManagerDataById) throws DataManagerException {
		return getSessionManagerInstanceDataByIdOrName(SM_INSTANCE_ID,sessionManagerDataById);
	}

	@Override
	public ISessionManagerInstanceData getSessionManagerInstanceDataByName(String sessionManagerDataByName) throws DataManagerException {
		return getSessionManagerInstanceDataByIdOrName(SM_NAME,sessionManagerDataByName.trim());
	}
	
	private ISessionManagerInstanceData getSessionManagerInstanceDataByIdOrName(String propertyName, Object value) throws DataManagerException{
		ISessionManagerInstanceData sessionManagerInastanceData = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(SessionManagerInstanceData.class).add(Restrictions.eq(propertyName, value));
		    sessionManagerInastanceData= (ISessionManagerInstanceData) criteria.uniqueResult();
			
			if(sessionManagerInastanceData == null){
				throw new InvalidValueException("Session Manager Data not found");
			}
			
			SMConfigInstanceData smConfigInstanceData = (SMConfigInstanceData) session.createCriteria(SMConfigInstanceData.class)
					.add(Restrictions.eq(SM_INSTANCE_ID,sessionManagerInastanceData.getSmInstanceId())).uniqueResult();
			sessionManagerInastanceData.setSmConfigInstanceData(smConfigInstanceData);
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive Session Manager, Reason: "+ hbe.getMessage(), hbe);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Session Manager, Reason: "+ exp.getMessage(), exp);
		}
		return sessionManagerInastanceData;
	}

	@Override
	public String deleteById(String sessionManagerId) throws DataManagerException {
		return delete(SM_INSTANCE_ID, sessionManagerId);
	}

	@Override
	public String deleteByName(String sessionManagerName) throws DataManagerException {
		return delete(SM_NAME, sessionManagerName);
	}
	
	private String delete(String propertyName, Object value) throws DataManagerException{
		String sessionManagerName = ((SM_NAME.equals(propertyName)) ? (String) value : "Session Manager");
		
		try{
			Session session = getSession();

			Criteria criteria = session.createCriteria(SessionManagerInstanceData.class).add(Restrictions.eq(propertyName,value));
			SessionManagerInstanceData sessionManagerInstanceData = (SessionManagerInstanceData)criteria.uniqueResult();

			if(sessionManagerInstanceData == null){
				throw new InvalidValueException("Session Manager not found");
			}
			
			Criteria smConfigCriteria = session.createCriteria(SMConfigInstanceData.class).add(Restrictions.eq(SM_INSTANCE_ID, sessionManagerInstanceData.getSmInstanceId()));
			SMConfigInstanceData configInstanceData=(SMConfigInstanceData)smConfigCriteria.uniqueResult();

			if(configInstanceData == null){
				throw new InvalidValueException("Session Manager not found");
			}
			
			Criteria dbFieldmapCriteria = session.createCriteria(SMDBFieldMapData.class);
			dbFieldmapCriteria.add(Restrictions.eq(SM_CONFIG_ID,configInstanceData.getSmConfigId()));

			//delete
			List<SMDBFieldMapData> fieldMapList=dbFieldmapCriteria.list();
			deleteObjectList(fieldMapList, session);
				
			Criteria sessionCloserESIRelCriteria = session.createCriteria(SMSessionCloserESIRelData.class);
			sessionCloserESIRelCriteria.add(Restrictions.eq(SM_CONFIG_ID,configInstanceData.getSmConfigId()));

			//delete
			List<SMDBFieldMapData> esiRelList=sessionCloserESIRelCriteria.list();
			deleteObjectList(esiRelList, session);
				
			session.delete(configInstanceData);

			session.delete(sessionManagerInstanceData);
			session.flush();
			return sessionManagerInstanceData.getName();
		}catch (ConstraintViolationException cve){
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + sessionManagerName + ", Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to delete " + sessionManagerName + ", Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to delete " + sessionManagerName + ", Reason: " + exp.getMessage(), exp);
		}
	}
}
