/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   HDiameterpolicyDataManager.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.hibernate.diameter.sessionmanager;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.SQLGrammarException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.DiameterSessionManagerDataManager;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerMappingData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.ScenarioMappingData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.SessionManagerFieldMappingData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.SessionOverideActionData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.sqlexception.EliteSQLGrammerException;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

import net.sf.json.JSONArray;

public class HDiameterSessionManagerDataManager extends HBaseDataManager implements DiameterSessionManagerDataManager{
	private static final String DIAMETER_SESSION_MANAGER_MAPPING_ID = "mappingId";
	private static final String DIAMETER_SESSION_MANAGER_NAME = "name";
	private static final String DIAMETER_SESSION_MANAGER_ID = "sessionManagerId";
	private static final String MODULE = "HDiameterSessionManagerDataManager";
	
	public PageList search(DiameterSessionManagerData diameterSessionManagerData,int requiredPageNo, Integer pageSize) throws DataManagerException {
		PageList pageList = null;
        int pageNo;
		try{
			pageNo=requiredPageNo;
		    Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterSessionManagerData.class);
			
            if((diameterSessionManagerData.getName() != null && diameterSessionManagerData.getName().trim().length()>0 )){
				criteria.add(Restrictions.ilike(DIAMETER_SESSION_MANAGER_NAME,diameterSessionManagerData.getName(),MatchMode.ANYWHERE));
		    } 
           
            criteria.addOrder(Order.asc(DIAMETER_SESSION_MANAGER_NAME)); 
           
    		List list = criteria.list();
			int totalItems = list.size();
			criteria.setFirstResult(((pageNo-1) * pageSize));
			criteria.setMaxResults(pageSize);
			List<DiameterSessionManagerData> diameterSessionManagerList = criteria.list();
			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(diameterSessionManagerList, pageNo, totalPages ,totalItems);

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
	
		return pageList;
	}

	@Override
	public String create(Object obj) throws DataManagerException {
		DiameterSessionManagerData diameterSessionManagerData = (DiameterSessionManagerData) obj;
		try{
			Session session = getSession();
			session.clear();
			String auditId= UUIDGenerator.generate();
			diameterSessionManagerData.setAuditUId(auditId);

			Set<DiameterSessionManagerMappingData> diameterSessionManagerMappingDatas = diameterSessionManagerData.getDiameterSessionManagerMappingData();
			Set<ScenarioMappingData> scenarioMappingDataSet = diameterSessionManagerData.getScenarioMappingDataSet();
			Set<SessionOverideActionData> sessionOverideActionDataSet=  diameterSessionManagerData.getSessionOverideActionDataSet();

			diameterSessionManagerData.setDiameterSessionManagerMappingData(null);
			diameterSessionManagerData.setScenarioMappingDataSet(null);
			diameterSessionManagerData.setSessionOverideActionDataSet(null);

			session.save(diameterSessionManagerData);
			session.flush();
			session.clear();
			
			//Adding Session Manager Mapping data
			if(diameterSessionManagerMappingDatas != null && diameterSessionManagerMappingDatas.isEmpty() == false){
				int orderNumber = 1;
				for(Iterator<DiameterSessionManagerMappingData> iteratorInst = diameterSessionManagerMappingDatas.iterator();iteratorInst.hasNext();){
					DiameterSessionManagerMappingData diameterSessionManagerMappingData = iteratorInst.next();
					diameterSessionManagerMappingData.setSessionManagerId(diameterSessionManagerData.getSessionManagerId());

					//Adding Copy Packet Map Instance Detail Data
					Set<SessionManagerFieldMappingData> fieldMappingDataDetail = diameterSessionManagerMappingData.getSessionManagerFieldMappingData();
					diameterSessionManagerMappingData.setSessionManagerFieldMappingData(null);
					diameterSessionManagerMappingData.setOrderNumber(orderNumber++);
					session.save(diameterSessionManagerMappingData);
					session.flush();
					session.clear();
					
					if(Collectionz.isNullOrEmpty(fieldMappingDataDetail) == false){
						int fieldOrderNumber = 1;
						for(Iterator<SessionManagerFieldMappingData> iteratorPacketDetail = fieldMappingDataDetail.iterator(); iteratorPacketDetail.hasNext(); ){
							SessionManagerFieldMappingData sessionManagerFieldMappingData = iteratorPacketDetail.next();
							sessionManagerFieldMappingData.setMappingId(diameterSessionManagerMappingData.getMappingId());
							sessionManagerFieldMappingData.setOrderNumber(fieldOrderNumber++);
							session.save(sessionManagerFieldMappingData);
							session.flush();
							session.clear();
						}
					}
				}
			}

			if(Collectionz.isNullOrEmpty(scenarioMappingDataSet) == false){
				int orderNumber = 1;
				for(Iterator<ScenarioMappingData> iteratorForDummyResp = scenarioMappingDataSet.iterator();iteratorForDummyResp.hasNext();){
					ScenarioMappingData scenarioMappingData = iteratorForDummyResp.next();
					scenarioMappingData.setSessionManagerId(diameterSessionManagerData.getSessionManagerId());
					scenarioMappingData.setOrderNumber(orderNumber++);
					session.save(scenarioMappingData);
					session.flush();
					session.clear();
				}
			}

			if(Collectionz.isNullOrEmpty(sessionOverideActionDataSet) == false){
				int orderNumber = 1;
				for(Iterator<SessionOverideActionData> iteratorForDummyResp = sessionOverideActionDataSet.iterator();iteratorForDummyResp.hasNext();){
					SessionOverideActionData sessionOverideActionData = iteratorForDummyResp.next();
					sessionOverideActionData.setSessionManagerId(diameterSessionManagerData.getSessionManagerId());
					sessionOverideActionData.setOrderNumber(orderNumber++);
					session.save(sessionOverideActionData);
					session.flush();
					session.clear();
				}

			}
		}catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE+ diameterSessionManagerData.getName() +REASON 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve.getCause());
		}catch(HibernateException hExp){
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException(FAILED_TO_CREATE+ diameterSessionManagerData.getName() +REASON+ hExp.getMessage(), hExp);
		}catch(Exception exp){
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(FAILED_TO_CREATE+ diameterSessionManagerData.getName() +REASON+ exp.getMessage(),exp);
		}
		return diameterSessionManagerData.getName();
	}
	
	@Override
	public DiameterSessionManagerData getDiameterSessionManagerDataById(String sessionManagerId) throws DataManagerException {
		try{
			org.hibernate.Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterSessionManagerData.class);
			DiameterSessionManagerData diameterSessionManagerData = (DiameterSessionManagerData)criteria.add(Restrictions.eq(DIAMETER_SESSION_MANAGER_ID,sessionManagerId)).uniqueResult();
			if(diameterSessionManagerData == null){
				throw new InvalidValueException("Diameter Session Manager Data not found");
			}
			return diameterSessionManagerData;
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive Diameter Session Manager, Reason: "+ hbe.getMessage(), hbe);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Diameter Session Manager, Reason: "+ exp.getMessage(), exp);
		}	
	}
	
	@Override
	public DiameterSessionManagerData getDiameterSessionManagerDataByName(String sessionManagerName) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterSessionManagerData.class);
			DiameterSessionManagerData diameterSessionManagerData = (DiameterSessionManagerData)criteria.add(Restrictions.eq(DIAMETER_SESSION_MANAGER_NAME,sessionManagerName)).uniqueResult();
			
			if(diameterSessionManagerData == null){
				throw new InvalidValueException("Diameter Session Manager not found");
			}
			
			return diameterSessionManagerData;
			
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive Diameter Session Manager, Reason: "+ hbe.getMessage(), hbe);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Diameter Session Manager, Reason: "+ exp.getMessage(), exp);
		}
	}

	@Override
	public List<DiameterSessionManagerData> getDiameterSessionManagerDatas()throws DataManagerException {
		List<DiameterSessionManagerData> diameterSessionManagerData;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterSessionManagerData.class);
			criteria.addOrder(Order.asc(DIAMETER_SESSION_MANAGER_ID));
			diameterSessionManagerData = criteria.list();
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive Diameter Session Manager, Reason: "+ hbe.getMessage(), hbe);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Diameter Session Manager, Reason: "+ exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		
		return diameterSessionManagerData;  
	}

	@Override
	public DiameterSessionManagerMappingData getDiameterSessionManagerMappingData(String mappingId) throws DataManagerException {
		try{
			org.hibernate.Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterSessionManagerMappingData.class);
			DiameterSessionManagerMappingData diameterSessionManagerMappingData = (DiameterSessionManagerMappingData)criteria.add(Restrictions.eq(DIAMETER_SESSION_MANAGER_MAPPING_ID,mappingId)).uniqueResult();
			if(diameterSessionManagerMappingData == null){
				throw new InvalidValueException("Diameter Session Manager Data not found");
			}
			
			return diameterSessionManagerMappingData;
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive Diameter Session Manager, Reason: "+ hbe.getMessage(), hbe);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Diameter Session Manager, Reason: "+ exp.getMessage(), exp);
		}	
	}

	@Override
	public void update(DiameterSessionManagerData diameterSessionManagerData,IStaffData staffData, String actionAlias)throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterSessionManagerData.class);
			DiameterSessionManagerData data = (DiameterSessionManagerData)criteria.add(Restrictions.eq(DIAMETER_SESSION_MANAGER_ID,diameterSessionManagerData.getSessionManagerId())).uniqueResult();
			
			if(data == null){
				throw new InvalidValueException("Diameter Session Manager Data not found");
			}
			
			data.setScenarioMappingDataSet(null);
			data.setSessionOverideActionDataSet(null);
			diameterSessionManagerData.setScenarioMappingDataSet(null);
			diameterSessionManagerData.setSessionOverideActionDataSet(null);

			JSONArray jsonArray=ObjectDiffer.diff(data,diameterSessionManagerData);

			data.setName(diameterSessionManagerData.getName());
			data.setDatabaseDatasourceId(diameterSessionManagerData.getDatabaseDatasourceId());
			data.setDescription(diameterSessionManagerData.getDescription());
			data.setTableName(diameterSessionManagerData.getTableName());
			data.setSequenceName(diameterSessionManagerData.getSequenceName());
			data.setStartTimeField(diameterSessionManagerData.getStartTimeField());
			data.setLastUpdatedTimeField(diameterSessionManagerData.getLastUpdatedTimeField());
			data.setDbQueryTimeout(diameterSessionManagerData.getDbQueryTimeout());
			data.setDelimeter(diameterSessionManagerData.getDelimeter());
			data.setDbFailureAction(diameterSessionManagerData.getDbFailureAction());
			data.setBatchEnabled(diameterSessionManagerData.getBatchEnabled());
			data.setBatchSize(diameterSessionManagerData.getBatchSize());
			data.setBatchInterval(diameterSessionManagerData.getBatchInterval());
			data.setBatchQueryTimeout(diameterSessionManagerData.getBatchQueryTimeout());
			data.setBatchedInsert(diameterSessionManagerData.getBatchedInsert());
			data.setBatchedDelete(diameterSessionManagerData.getBatchedDelete());
			data.setBatchedUpdate(diameterSessionManagerData.getBatchedUpdate());
			data.setDiameterSessionManagerMappingData(diameterSessionManagerData.getDiameterSessionManagerMappingData());
			data.setAuditUId(diameterSessionManagerData.getAuditUId());

			session.saveOrUpdate(data);
			session.flush();

			criteria = session.createCriteria(DiameterSessionManagerMappingData.class);
			List<DiameterSessionManagerMappingData> oldDiameterSessionManagerList = criteria.add(Restrictions.eq(DIAMETER_SESSION_MANAGER_ID,data.getSessionManagerId())).list();
			if(Collectionz.isNullOrEmpty(oldDiameterSessionManagerList) == false){
				for(Iterator<DiameterSessionManagerMappingData> iterate = oldDiameterSessionManagerList.iterator() ; iterate.hasNext();){
					DiameterSessionManagerMappingData instanceData = iterate.next();
					criteria = session.createCriteria(SessionManagerFieldMappingData.class);
					List<SessionManagerFieldMappingData> detailList = criteria.add(Restrictions.eq(DIAMETER_SESSION_MANAGER_MAPPING_ID, instanceData.getMappingId())).list();
					for(Iterator<SessionManagerFieldMappingData> detailIterator = detailList.iterator();detailIterator.hasNext();){
						SessionManagerFieldMappingData detail = detailIterator.next();
						session.delete(detail);
						session.flush();
					}
					instanceData.setSessionManagerFieldMappingData(null);
				}

				for(Iterator<DiameterSessionManagerMappingData> iterate = oldDiameterSessionManagerList.iterator() ; iterate.hasNext();){
					DiameterSessionManagerMappingData instanceData = iterate.next();
					session.delete(instanceData);
				}
			}

			//Save Diameter Session Manager Data

			Set<DiameterSessionManagerMappingData> diameterSessionManagerMappingDatas = diameterSessionManagerData.getDiameterSessionManagerMappingData();

			//Adding Session Manager Mapping data
			if(Collectionz.isNullOrEmpty(diameterSessionManagerMappingDatas) == false){
				int orderNumber = 1;
				for(Iterator<DiameterSessionManagerMappingData> iteratorInst = diameterSessionManagerMappingDatas.iterator();iteratorInst.hasNext();){
					DiameterSessionManagerMappingData diameterSessionManagerMappingData = iteratorInst.next();
					diameterSessionManagerMappingData.setSessionManagerId(diameterSessionManagerData.getSessionManagerId());

					//Adding Copy Packet Map Instance Detail Data
					Set<SessionManagerFieldMappingData> fieldMappingDataDetail = diameterSessionManagerMappingData.getSessionManagerFieldMappingData();
					diameterSessionManagerMappingData.setSessionManagerFieldMappingData(null);
					diameterSessionManagerMappingData.setOrderNumber(orderNumber++);
					session.save(diameterSessionManagerMappingData);
					session.flush();
					
					if(Collectionz.isNullOrEmpty(fieldMappingDataDetail) == false){
						int fieldOrderNumber = 1;
						for(Iterator<SessionManagerFieldMappingData> iteratorPacketDetail = fieldMappingDataDetail.iterator(); iteratorPacketDetail.hasNext(); ){
							SessionManagerFieldMappingData sessionManagerFieldMappingData = iteratorPacketDetail.next();
							sessionManagerFieldMappingData.setMappingId(diameterSessionManagerMappingData.getMappingId());
							sessionManagerFieldMappingData.setOrderNumber(fieldOrderNumber++);
							session.save(sessionManagerFieldMappingData);
							session.flush();
						}
					}
				}
			}
				
			staffData.setAuditId(data.getAuditUId());
			staffData.setAuditName(data.getName());

			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update Diameter Session Manager , Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update Diameter Session Manager , Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update Diameter Session Manager , Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override
	public void updateScenarioData(DiameterSessionManagerData diameterSessionManagerData,IStaffData staffData, String actionAlias) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterSessionManagerData.class);
			DiameterSessionManagerData data = (DiameterSessionManagerData)criteria.add(Restrictions.eq(DIAMETER_SESSION_MANAGER_ID,diameterSessionManagerData.getSessionManagerId())).uniqueResult();

			if(data == null){
				throw new InvalidValueException("Diameter Scenario Mapping Data not found");
			}
			
			JSONArray jsonArray=ObjectDiffer.diff(data,diameterSessionManagerData);

			if(data.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
				data.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}

			criteria = session.createCriteria(ScenarioMappingData.class);
			List<ScenarioMappingData> oldParamList = criteria.add(Restrictions.eq(DIAMETER_SESSION_MANAGER_ID, data.getSessionManagerId())).list();
			if( Collectionz.isNullOrEmpty(oldParamList) == false ){
				for(Iterator<ScenarioMappingData> dummyParamItrIterator = oldParamList.iterator(); dummyParamItrIterator.hasNext();){
					ScenarioMappingData dummyParam = dummyParamItrIterator.next();
					session.delete(dummyParam);
					session.flush();
				}
			}

			criteria = session.createCriteria(SessionOverideActionData.class);
			List<SessionOverideActionData> oldParamSessionOverideActionList = criteria.add(Restrictions.eq(DIAMETER_SESSION_MANAGER_ID, data.getSessionManagerId())).list();
			if( Collectionz.isNullOrEmpty(oldParamSessionOverideActionList) == false ){
				for(Iterator<SessionOverideActionData> dummyParamItrIteratorList = oldParamSessionOverideActionList.iterator(); dummyParamItrIteratorList.hasNext();){
					SessionOverideActionData dummyParam = dummyParamItrIteratorList.next();
					session.delete(dummyParam);
					session.flush();
				}
			}

			Set<ScenarioMappingData> scenarioMappingDataSet = diameterSessionManagerData.getScenarioMappingDataSet();
			Set<SessionOverideActionData> sessionOverideActionDataSet = diameterSessionManagerData.getSessionOverideActionDataSet();

			//Save data
			if(Collectionz.isNullOrEmpty(scenarioMappingDataSet) == false){
				int orderNumber = 1;
				for(Iterator<ScenarioMappingData> iteratorForDummyResp = scenarioMappingDataSet.iterator();iteratorForDummyResp.hasNext();){
					ScenarioMappingData scenarioMappingData = iteratorForDummyResp.next();
					scenarioMappingData.setSessionManagerId(data.getSessionManagerId());
					scenarioMappingData.setOrderNumber(orderNumber++);
					session.save(scenarioMappingData);
					session.flush();
				}
			}

			if(Collectionz.isNullOrEmpty(sessionOverideActionDataSet) == false){
				int orderNumber = 1;
				for(Iterator<SessionOverideActionData> iteratorForDummyResp = sessionOverideActionDataSet.iterator();iteratorForDummyResp.hasNext();){
					SessionOverideActionData sessionOverideActionData = iteratorForDummyResp.next();
					sessionOverideActionData.setSessionManagerId(data.getSessionManagerId());
					sessionOverideActionData.setOrderNumber(orderNumber++);
					session.save(sessionOverideActionData);
					session.flush();
				}
			}
			
			staffData.setAuditId(data.getAuditUId());
			staffData.setAuditName(data.getName());
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update Diameter Session Manager , Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update Diameter Session Manager , Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update Diameter Session Manager , Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override
	public List<DiameterSessionManagerMappingData> getDiameterSessionManagerMappingDataList(String sessionManagerId) throws DataManagerException {
		List<DiameterSessionManagerMappingData> diameterSessionManagerData;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterSessionManagerMappingData.class).add(Restrictions.eq(DIAMETER_SESSION_MANAGER_ID,sessionManagerId));
			diameterSessionManagerData = criteria.list();
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive Diameter Session Manager, Reason: "+ hbe.getMessage(), hbe);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Diameter Session Manager, Reason: "+ exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		
		return diameterSessionManagerData;  
	}

	@Override
	public PageList getASMDataByColumnName(String searchColumnList,String tableName,int requiredPageNumber, Integer pageSize, IStaffData staffData, String actionAlias) throws DataManagerException, EliteSQLGrammerException {
		PageList pageList = null;
		List totalDatalist = new ArrayList<String>();
		List paginationList = new ArrayList<String>();
		try{
			Session session = getSession();
			SessionImplementor sim = (SessionImplementor) session;
			Connection con = sim.connection();
			
			String sql = "SELECT CONCUSERID,"+ searchColumnList +",PROTOCOLTYPE FROM "+ tableName;
			String paginationQuery = null;
			if( con != null){
				if(con.getMetaData().getURL().contains(ConfigConstant.ORACLE)){
					paginationQuery = " select * from ( select row_.*, rownum rownum_ from (" +sql+ " ) row_ where rownum <= "+ (requiredPageNumber*pageSize) + " ) where rownum_ >=" + ((requiredPageNumber*pageSize) - (pageSize)+ 1) ;
				}else if(con.getMetaData().getURL().contains(ConfigConstant.POSTGRESQL)){
					paginationQuery = "select CONCUSERID, " + searchColumnList + ", PROTOCOLTYPE from "+ tableName +" limit "+ pageSize+" offset "+pageSize+"*("+ requiredPageNumber +"-1)";
				}
			}
			
			SQLQuery query = session.createSQLQuery(sql);
			query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			totalDatalist = query.list();
			
			SQLQuery paginationQuerySQL = session.createSQLQuery(paginationQuery);
			paginationQuerySQL.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			paginationList= paginationQuerySQL.list();
			
			int totalItems = totalDatalist.size();
			long totalPages = (long) Math.ceil(totalItems / pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;
			
			pageList = new PageList(paginationList, requiredPageNumber, totalPages,totalItems);
		}catch(HibernateException hExp){
		    if(hExp instanceof SQLGrammarException ){
				throw new EliteSQLGrammerException("Action failed : "+hExp.getMessage(),hExp);
		    }
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		
		return pageList;  
	}
	
	@Override
	public void closeSelectedSession(List<String> asList, String tableName) throws DataManagerException {
		try{
			Session session = getSession();
			for(int i=0;i<asList.size();i++){
				String sessionId = asList.get(i);
				SQLQuery q = session.createSQLQuery("DELETE FROM "+ tableName +" where CONCUSERID = " + sessionId);
				q.executeUpdate();
			}
		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}		
	}
	
	@Override
	public List getASMDataByColumnName(String activeSessionId,String tableName) throws DataManagerException {
		PageList pageList = null;
		List list = new ArrayList<String>();
		try{
			Session session = getSession();
			String sql = "SELECT * FROM "+ tableName +" where CONCUSERID="+activeSessionId;
			SQLQuery query = session.createSQLQuery(sql);
			query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			list = query.list();
			
		}catch(HibernateException hExp){
		    throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		
		return list;  
	}

	@Override
	public void resetViewableColumnsValue(String searchColumnName, String sessionManagerId) throws DataManagerException {
		try{
			org.hibernate.Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterSessionManagerData.class);
			DiameterSessionManagerData data = (DiameterSessionManagerData)criteria.add(Restrictions.eq(DIAMETER_SESSION_MANAGER_ID,sessionManagerId)).uniqueResult();
			if(data ==  null){
				throw new DataManagerException("Diameter Session Manager Data not found");
			}
			data.setViewableColumns(searchColumnName);
			
			session.update(data);
			session.flush();
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}
		
	}

	@Override
	public String deleteById(String diameterSessionManagerID) throws DataManagerException {
		return delete(DIAMETER_SESSION_MANAGER_ID, diameterSessionManagerID);
	}

	@Override
	public String deleteByName(String diameterSessionManagerName) throws DataManagerException {
		return delete(DIAMETER_SESSION_MANAGER_NAME, diameterSessionManagerName);
	}
	
	private String delete(String propertyName, Object value) throws DataManagerException{
		String diameterSessionManagerName = ((DIAMETER_SESSION_MANAGER_NAME.equals(propertyName)) ? (String) value : "Diameter Session Manager");
		DiameterSessionManagerData diameterSessionManagerData = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterSessionManagerData.class);
			criteria.add(Restrictions.eq(propertyName,value));
			diameterSessionManagerData = (DiameterSessionManagerData) criteria.uniqueResult();
			
			if(diameterSessionManagerData == null){
				throw new InvalidValueException("Diameter Session Manager Not Found");
			}
			session.delete(diameterSessionManagerData);
			session.flush();
			return diameterSessionManagerData.getName();
		}catch (ConstraintViolationException cve){
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + diameterSessionManagerName + ", Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to delete " + diameterSessionManagerName + ", Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to delete " + diameterSessionManagerName + ", Reason: " + exp.getMessage(), exp);
		}
	}

	@Override
	public void updateDiameterSessionManagerData(DiameterSessionManagerData diameterSessionManagerData,StaffData staffData, String name) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterSessionManagerData.class);
			DiameterSessionManagerData data = (DiameterSessionManagerData)criteria.add(Restrictions.eq(DIAMETER_SESSION_MANAGER_NAME,name)).uniqueResult();

			if(data == null){
				throw new InvalidValueException("Diameter Session Manager Data not found");
			}

			JSONArray jsonArray=ObjectDiffer.diff(data,diameterSessionManagerData);

			data.setName(diameterSessionManagerData.getName());
			data.setDatabaseDatasourceId(diameterSessionManagerData.getDatabaseDatasourceId());
			data.setDescription(diameterSessionManagerData.getDescription());
			data.setTableName(diameterSessionManagerData.getTableName());
			data.setSequenceName(diameterSessionManagerData.getSequenceName());
			data.setStartTimeField(diameterSessionManagerData.getStartTimeField());
			data.setLastUpdatedTimeField(diameterSessionManagerData.getLastUpdatedTimeField());
			data.setDbQueryTimeout(diameterSessionManagerData.getDbQueryTimeout());
			data.setDelimeter(diameterSessionManagerData.getDelimeter());
			data.setDbFailureAction(diameterSessionManagerData.getDbFailureAction());
			data.setBatchEnabled(diameterSessionManagerData.getBatchEnabled());
			data.setBatchSize(diameterSessionManagerData.getBatchSize());
			data.setBatchInterval(diameterSessionManagerData.getBatchInterval());
			data.setBatchQueryTimeout(diameterSessionManagerData.getBatchQueryTimeout());
			data.setBatchedInsert(diameterSessionManagerData.getBatchedInsert());
			data.setBatchedDelete(diameterSessionManagerData.getBatchedDelete());
			data.setBatchedUpdate(diameterSessionManagerData.getBatchedUpdate());
			data.setDiameterSessionManagerMappingData(diameterSessionManagerData.getDiameterSessionManagerMappingData());
			data.setScenarioMappingDataSet(diameterSessionManagerData.getScenarioMappingDataSet());
			data.setSessionOverideActionDataSet(diameterSessionManagerData.getSessionOverideActionDataSet());

			session.saveOrUpdate(data);
			session.flush();

			criteria = session.createCriteria(DiameterSessionManagerMappingData.class);
			List<DiameterSessionManagerMappingData> oldDiameterSessionManagerList = criteria.add(Restrictions.eq(DIAMETER_SESSION_MANAGER_ID,data.getSessionManagerId())).list();
			if( Collectionz.isNullOrEmpty(oldDiameterSessionManagerList) == false){
				for(Iterator<DiameterSessionManagerMappingData> iterate = oldDiameterSessionManagerList.iterator() ; iterate.hasNext();){
					DiameterSessionManagerMappingData instanceData = iterate.next();
					criteria = session.createCriteria(SessionManagerFieldMappingData.class);
					List<SessionManagerFieldMappingData> detailList = criteria.add(Restrictions.eq(DIAMETER_SESSION_MANAGER_MAPPING_ID, instanceData.getMappingId())).list();
					for(Iterator<SessionManagerFieldMappingData> detailIterator = detailList.iterator();detailIterator.hasNext();){
						SessionManagerFieldMappingData detail = detailIterator.next();
						session.delete(detail);
						session.flush();
					}
					instanceData.setSessionManagerFieldMappingData(null);
				}

				for(Iterator<DiameterSessionManagerMappingData> iterate = oldDiameterSessionManagerList.iterator() ; iterate.hasNext();){
					DiameterSessionManagerMappingData instanceData = iterate.next();
					session.delete(instanceData);
				}
			}

			//Save Diameter Session Manager Data

			Set<DiameterSessionManagerMappingData> diameterSessionManagerMappingDatas = diameterSessionManagerData.getDiameterSessionManagerMappingData();

			//Adding Session Manager Mapping data
			if( Collectionz.isNullOrEmpty(diameterSessionManagerMappingDatas) == false){
				int orderNumber = 1;
				for(Iterator<DiameterSessionManagerMappingData> iteratorInst = diameterSessionManagerMappingDatas.iterator();iteratorInst.hasNext();){
					DiameterSessionManagerMappingData diameterSessionManagerMappingData = iteratorInst.next();
					diameterSessionManagerMappingData.setSessionManagerId(data.getSessionManagerId());
					diameterSessionManagerMappingData.setOrderNumber(orderNumber++);

					//Adding Copy Packet Map Instance Detail Data
					Set<SessionManagerFieldMappingData> fieldMappingDataDetail = diameterSessionManagerMappingData.getSessionManagerFieldMappingData();
					diameterSessionManagerMappingData.setSessionManagerFieldMappingData(null);
					session.save(diameterSessionManagerMappingData);
					session.flush();
					if(Collectionz.isNullOrEmpty(fieldMappingDataDetail) == false){
						int fieldOrderNumber = 1;
						for(Iterator<SessionManagerFieldMappingData> iteratorPacketDetail = fieldMappingDataDetail.iterator(); iteratorPacketDetail.hasNext(); ){
							SessionManagerFieldMappingData sessionManagerFieldMappingData = iteratorPacketDetail.next();
							sessionManagerFieldMappingData.setMappingId(diameterSessionManagerMappingData.getMappingId());
							sessionManagerFieldMappingData.setOrderNumber(fieldOrderNumber++);
							session.save(sessionManagerFieldMappingData);
							session.flush();
						}
					}
				}
			}

			criteria = session.createCriteria(ScenarioMappingData.class);
			List<ScenarioMappingData> oldParamList = criteria.add(Restrictions.eq(DIAMETER_SESSION_MANAGER_ID, data.getSessionManagerId())).list();
			if( Collectionz.isNullOrEmpty(oldParamList) == false ){
				for(Iterator<ScenarioMappingData> dummyParamItrIterator = oldParamList.iterator(); dummyParamItrIterator.hasNext();){
					ScenarioMappingData dummyParam = dummyParamItrIterator.next();
					session.delete(dummyParam);
					session.flush();
				}
			}

			criteria = session.createCriteria(SessionOverideActionData.class);
			List<SessionOverideActionData> oldParamSessionOverideActionList = criteria.add(Restrictions.eq(DIAMETER_SESSION_MANAGER_ID, data.getSessionManagerId())).list();
			if( Collectionz.isNullOrEmpty(oldParamSessionOverideActionList) == false ){
				for(Iterator<SessionOverideActionData> dummyParamItrIteratorList = oldParamSessionOverideActionList.iterator(); dummyParamItrIteratorList.hasNext();){
					SessionOverideActionData dummyParam = dummyParamItrIteratorList.next();
					session.delete(dummyParam);
					session.flush();
				}
			}

			Set<ScenarioMappingData> scenarioMappingDataSet = diameterSessionManagerData.getScenarioMappingDataSet();
			Set<SessionOverideActionData> sessionOverideActionDataSet = diameterSessionManagerData.getSessionOverideActionDataSet();

			//Save data
			if( Collectionz.isNullOrEmpty(scenarioMappingDataSet) == false){
				int orderNumber = 1;
				for(Iterator<ScenarioMappingData> iteratorForDummyResp = scenarioMappingDataSet.iterator();iteratorForDummyResp.hasNext();){
					ScenarioMappingData scenarioMappingData = iteratorForDummyResp.next();
					scenarioMappingData.setSessionManagerId(data.getSessionManagerId());
					scenarioMappingData.setOrderNumber(orderNumber++);
					session.save(scenarioMappingData);
					session.flush();
				}
			}

			if( Collectionz.isNullOrEmpty(sessionOverideActionDataSet) == false){
				int orderNumber = 1;
				for(Iterator<SessionOverideActionData> iteratorForDummyResp = sessionOverideActionDataSet.iterator();iteratorForDummyResp.hasNext();){
					SessionOverideActionData sessionOverideActionData = iteratorForDummyResp.next();
					sessionOverideActionData.setSessionManagerId(data.getSessionManagerId());
					sessionOverideActionData.setOrderNumber(orderNumber++);
					session.save(sessionOverideActionData);
					session.flush();
				}
			}
			staffData.setAuditId(data.getAuditUId());
			staffData.setAuditName(data.getName());
			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_DIAMETER_SESSION_MANAGER);
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update Diameter Session Manager , Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update Diameter Session Manager , Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update Diameter Session Manager , Reason: " + exp.getMessage(), exp);
		}
	}
}
