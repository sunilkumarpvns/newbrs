package com.elitecore.elitesm.hibernate.diameter.routingconf;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.DiameterRoutingConfDataManager;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterPeerGroupData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterPeerGroupRelData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfigFailureParam;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingTableData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

import net.sf.json.JSONArray;

public class HDiameterRoutingConfDataManager extends HBaseDataManager implements DiameterRoutingConfDataManager {

	private static final String ROUTING_TABLE_ID = "routingTableId";
	private static final String ROUTING_TABLE_NAME = "routingTableName";
	
	private static final String ROUTING_CONFIG_ID = "routingConfigId";
	private static final String NAME = "name";
	private static final String MODULE = "HDiameterRoutingConfDataManager";

	public PageList search(DiameterRoutingConfData diameterRoutingConfData, Map infoMap) throws DataManagerException{
		PageList pageList = null;
        int pageNo;
        int pageSize;
		try{
			pageNo=(Integer)infoMap.get("pageNo");
			pageSize=(Integer)infoMap.get("pageSize");
			DiameterRoutingTableData diameterRoutingTableData=diameterRoutingConfData.getDiameterRoutingTableData();
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterRoutingConfData.class);
			criteria.createAlias("diameterRoutingTableData", "drt");
			if(diameterRoutingTableData != null && diameterRoutingTableData.getRoutingTableName() != null && diameterRoutingTableData.getRoutingTableName().trim().length()>0 ){
				criteria.add(Restrictions.ilike("drt.routingTableName",diameterRoutingTableData.getRoutingTableName(),MatchMode.ANYWHERE));
			}	
           if((diameterRoutingConfData.getName() != null && diameterRoutingConfData.getName().trim().length()>0 )){
				criteria.add(Restrictions.ilike(NAME,diameterRoutingConfData.getName(),MatchMode.ANYWHERE));
			}
           if((diameterRoutingConfData.getRealmName() != null && diameterRoutingConfData.getRealmName().trim().length()>0 )){
				criteria.add(Restrictions.ilike("realmName",diameterRoutingConfData.getRealmName(),MatchMode.ANYWHERE));
			}
           if((diameterRoutingConfData.getOriginHost() != null && diameterRoutingConfData.getOriginHost().trim().length()>0 )){
				criteria.add(Restrictions.ilike("originHost",diameterRoutingConfData.getOriginHost(),MatchMode.ANYWHERE));
			}
           if((diameterRoutingConfData.getOriginRealm() != null && diameterRoutingConfData.getOriginRealm().trim().length()>0 )){
				criteria.add(Restrictions.ilike("originRealm",diameterRoutingConfData.getOriginRealm(),MatchMode.ANYWHERE));
			}
           if((diameterRoutingConfData.getAppIds() != null && diameterRoutingConfData.getAppIds().trim().length()>0 )){
				criteria.add(Restrictions.ilike("appIds",diameterRoutingConfData.getAppIds(),MatchMode.ANYWHERE));
			}
           criteria.addOrder(Order.asc("drt.routingTableName")); 
           criteria.addOrder(Order.asc(NAME));
           
    		List list = criteria.list();
			int totalItems = list.size();
			criteria.setFirstResult(((pageNo-1) * pageSize));
		    criteria.setMaxResults(pageSize);
		
			List<DiameterRoutingConfData> diameterRoutingConfDataList = criteria.list();
			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(diameterRoutingConfDataList, pageNo, totalPages ,totalItems);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter routing table entry list, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter routing table entry list, Reason: " + e.getMessage(), e);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return pageList;
	}
	
	public PageList searchRoutingTable(Map infoMap) throws DataManagerException{
		PageList pageList = null;
        int pageNo;
        int pageSize;
    	try{
			pageNo=(Integer)infoMap.get("pageNo");
			pageSize=(Integer)infoMap.get("pageSize");
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterRoutingTableData.class);
			criteria.addOrder(Order.asc(ROUTING_TABLE_NAME));
            List list = criteria.list();
			int totalItems = list.size();
			List<DiameterRoutingTableData> diameterRoutingTableDataList = criteria.list();
			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(diameterRoutingTableDataList, pageNo, totalPages ,totalItems);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter routing table list, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter routing table list, Reason: " + e.getMessage(), e);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return pageList;
	}
	
	public DiameterRoutingConfData getDiameterRoutingConfData(String routingConfId) throws DataManagerException{
		DiameterRoutingConfData diameterRoutingConfData = null;
		try{
			Session session = getSession();			
			Criteria criteria = session.createCriteria(DiameterRoutingConfData.class).add(Restrictions.eq(ROUTING_CONFIG_ID,routingConfId));
			criteria.setFetchMode("diameterRoutingConfigFailureParamSet", FetchMode.JOIN);
			criteria.addOrder(Order.asc("orderNumber"));  
			diameterRoutingConfData = (DiameterRoutingConfData)criteria.uniqueResult();
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter routing table entry, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter routing table entry, Reason: " + e.getMessage(), e);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return diameterRoutingConfData;
	}
	
	public void update(DiameterRoutingConfData diameterRoutingConfData,IStaffData staffData,String actionAlias) throws DataManagerException{
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterRoutingConfData.class);
			DiameterRoutingConfData data = (DiameterRoutingConfData)criteria.add(Restrictions.eq(ROUTING_CONFIG_ID,diameterRoutingConfData.getRoutingConfigId())).uniqueResult();
			
			JSONArray jsonArray=ObjectDiffer.diff(data,diameterRoutingConfData);
						
			data.setName(diameterRoutingConfData.getName());
			data.setDescription(diameterRoutingConfData.getDescription());
			data.setRealmName(diameterRoutingConfData.getRealmName());
			data.setAppIds(diameterRoutingConfData.getAppIds());
			data.setOriginHost(diameterRoutingConfData.getOriginHost());
			data.setOriginRealm(diameterRoutingConfData.getOriginRealm());
			data.setRuleset(diameterRoutingConfData.getRuleset());
			if(diameterRoutingConfData.getTransMapConfId()!=null){
					data.setTransMapConfId(diameterRoutingConfData.getTransMapConfId());
					data.setCopyPacketMapId(null);
			}else if(diameterRoutingConfData.getCopyPacketMapId()!=null){
					data.setTransMapConfId(null);
					data.setCopyPacketMapId(diameterRoutingConfData.getCopyPacketMapId());
			}
			else{
				data.setTransMapConfId(null);
				data.setCopyPacketMapId(null);
			}
			data.setRoutingAction(diameterRoutingConfData.getRoutingAction());
			data.setStatefulRouting(diameterRoutingConfData.getStatefulRouting());
			data.setAttachedRedirection(diameterRoutingConfData.getAttachedRedirection());
			data.setOrderNumber(diameterRoutingConfData.getOrderNumber());
			data.setLastModifiedDate(diameterRoutingConfData.getLastModifiedDate());
			data.setLastModifiedByStaffId(diameterRoutingConfData.getLastModifiedByStaffId());
			data.setRoutingTableId(diameterRoutingConfData.getRoutingTableId());
			data.setTransactionTimeout(diameterRoutingConfData.getTransactionTimeout());
			data.setProtocolFailureAction(diameterRoutingConfData.getProtocolFailureAction());
			data.setProtocolFailureArguments(diameterRoutingConfData.getProtocolFailureArguments());
			data.setTransientFailureAction(diameterRoutingConfData.getTransientFailureAction());
			data.setTransientFailureArguments(diameterRoutingConfData.getTransientFailureArguments());
			data.setPermanentFailureAction(diameterRoutingConfData.getPermanentFailureAction());
			data.setPermanentFailureArguments(diameterRoutingConfData.getPermanentFailureArguments());
			data.setTimeOutAction(diameterRoutingConfData.getTimeOutAction());
			data.setTimeOutArguments(diameterRoutingConfData.getTimeOutArguments());
			data.setImsiBasedRoutingTableId(diameterRoutingConfData.getImsiBasedRoutingTableId());
			data.setSubsciberMode(diameterRoutingConfData.getSubsciberMode());
			data.setImsiBasedRoutingTableId(diameterRoutingConfData.getImsiBasedRoutingTableId());
			data.setMsisdnBasedRoutingTableId(diameterRoutingConfData.getMsisdnBasedRoutingTableId());
			
			if(data.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
 				data.setAuditUId(auditId);
 				staffData.setAuditId(auditId);
 			}
			session.update(data);
			
			// delete Existing DiameterRoutingConfigFailureParam
			Set<DiameterRoutingConfigFailureParam> diameterRoutingConfigFailureParamSet = data.getDiameterRoutingConfigFailureParamSet();
			for(DiameterRoutingConfigFailureParam diameterRoutingConfigFailureParam : diameterRoutingConfigFailureParamSet) {
				session.delete(diameterRoutingConfigFailureParam);
			}
			//insert new DiameterRoutingConfigFailureParams
			diameterRoutingConfigFailureParamSet = diameterRoutingConfData.getDiameterRoutingConfigFailureParamSet();
			Set<DiameterRoutingConfigFailureParam> newRoutingConfSet=new LinkedHashSet<DiameterRoutingConfigFailureParam>();
			int orderNumber = 1;
			for(DiameterRoutingConfigFailureParam diameterRoutingConfigFailureParam : diameterRoutingConfigFailureParamSet) {
				diameterRoutingConfigFailureParam.setDiameterRoutingConfData(data);
				diameterRoutingConfigFailureParam.setOrderNumber(orderNumber++);
				newRoutingConfSet.add(diameterRoutingConfigFailureParam);
				session.save(diameterRoutingConfigFailureParam);
			}
			
			data.setDiameterRoutingConfigFailureParamSet(newRoutingConfSet);
			
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
			session.flush();
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update diameter routing table entry, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to update diameter routing table entry, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update diameter routing table entry, Reason: " + e.getMessage(), e);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
	}
	
	public void updateDiameterPeer(DiameterRoutingConfData diameterRoutingConfData,IStaffData staffData,String actionAlias) throws DataManagerException{
		try{
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(DiameterRoutingConfData.class);
			DiameterRoutingConfData existingDiameterRoutingConfData = (DiameterRoutingConfData)criteria.add(Restrictions.eq(ROUTING_CONFIG_ID,diameterRoutingConfData.getRoutingConfigId())).uniqueResult();
			
			JSONArray jsonArray=ObjectDiffer.diff(existingDiameterRoutingConfData,diameterRoutingConfData);
						
			existingDiameterRoutingConfData.setLastModifiedByStaffId(diameterRoutingConfData.getLastModifiedByStaffId());
			existingDiameterRoutingConfData.setLastModifiedDate(diameterRoutingConfData.getLastModifiedDate());
			
			if(existingDiameterRoutingConfData.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
	 			existingDiameterRoutingConfData.setAuditUId(auditId);
	 			staffData.setAuditId(auditId);
			}
			
			session.save(existingDiameterRoutingConfData);
			session.flush();
			
			Iterator<DiameterPeerGroupData> diameterPeerGroupItr = existingDiameterRoutingConfData.getDiameterPeerGroupDataSet().iterator();
			while(diameterPeerGroupItr.hasNext()){
				DiameterPeerGroupData diameterPeerGroupData = diameterPeerGroupItr.next();
				if(diameterPeerGroupData != null){
					Iterator<DiameterPeerGroupRelData> diameterPeerGroupRelItr = diameterPeerGroupData.getDiameterPeerGroupRelDataSet().iterator();

					while(diameterPeerGroupRelItr.hasNext()){
						DiameterPeerGroupRelData diameterPeerGroupRelData = diameterPeerGroupRelItr.next();			
						if(diameterPeerGroupRelData != null){
							session.delete(diameterPeerGroupRelData);
							session.flush();
						}
					}
					session.delete(diameterPeerGroupData);
					session.flush();
				}											
			}
			
			String diameterRoutingConfigId = diameterRoutingConfData.getRoutingConfigId();			
			Iterator<DiameterPeerGroupData> itr = diameterRoutingConfData.getDiameterPeerGroupDataSet().iterator();
			int orderNumber = 1;
			while(itr.hasNext()){
				DiameterPeerGroupData diameterPeerGroupData = itr.next();
				if(diameterPeerGroupData != null){
					diameterPeerGroupData.setRoutingConfigId(diameterRoutingConfigId);
					diameterPeerGroupData.setOrderNumber(orderNumber++);
					session.save(diameterPeerGroupData);
				}
				
				String diameterPeerGroupId = diameterPeerGroupData.getPeerGroupId();
				Iterator<DiameterPeerGroupRelData> peerGroupItr = diameterPeerGroupData.getDiameterPeerGroupRelDataSet().iterator();
				
				while(peerGroupItr.hasNext()){
					DiameterPeerGroupRelData diameterPeerGroupRelData = peerGroupItr.next();			
					if(diameterPeerGroupRelData != null){
						diameterPeerGroupRelData.setPeerGroupId(diameterPeerGroupId);
						session.save(diameterPeerGroupRelData);
					}
				}
			}
			
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update diameter routing table entry, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to update diameter routing table entry, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update diameter routing table entry, Reason: " + e.getMessage(), e);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
	}
	
	public PageList searchRoutingFromRoutingTable(String routingTableId) throws DataManagerException{
		PageList pageList = null;
        try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterRoutingConfData.class);
			if(routingTableId != null)
			{
				criteria.add(Restrictions.eq(ROUTING_TABLE_ID,routingTableId));
			}	
			criteria.addOrder(Order.asc("orderNumber"));
            List list = criteria.list();
			int totalItems = list.size();
			List<DiameterRoutingConfData> diameterRoutingConfDataList = criteria.list();
			pageList = new PageList(diameterRoutingConfDataList,0,0,totalItems);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter routing table list, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter routing table list, Reason: " + e.getMessage(), e);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return pageList;
	}
	
	@Override
	public DiameterRoutingTableData getDiameterRoutingTableData(
			String routingTableId) throws DataManagerException {
		DiameterRoutingTableData diameterRoutingTableData = null;
		try{	
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterRoutingTableData.class).add(Restrictions.eq(ROUTING_TABLE_ID, routingTableId));
			List<DiameterRoutingTableData> diameterRoutingTableDataList = criteria.list();
			if(diameterRoutingTableDataList != null && diameterRoutingTableDataList.isEmpty() == false){
				diameterRoutingTableData = diameterRoutingTableDataList.get(0);
			}
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter routing table entry, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive diameter routing table entry, Reason: " + e.getMessage(), e);
		}
		return diameterRoutingTableData;
	}
	
	@Override
	public DiameterRoutingTableData getDiameterRoutingTableDataByName(String routingTableName) throws DataManagerException{
		DiameterRoutingTableData diameterRoutingTableData = null;
		try{	
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterRoutingTableData.class).add(Restrictions.eq("routingTableName", routingTableName));
			diameterRoutingTableData = (DiameterRoutingTableData)criteria.uniqueResult();
			if(diameterRoutingTableData == null){
				throw new InvalidValueException("Diameter Routing Table not found");
			}
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive Diameter Routing table by routing table name, Reason: " +hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Diameter Routing table by routing table name, Reason: " +exp.getMessage(), exp);
		}
		return diameterRoutingTableData;
	}

	@Override
	public void updateOverloadConfiguration(
			DiameterRoutingTableData diameterRoutingTableData,
			IStaffData staffData, String actionAlias)
			throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterRoutingTableData.class);
			DiameterRoutingTableData data = (DiameterRoutingTableData)criteria.add(Restrictions.eq(ROUTING_TABLE_ID,diameterRoutingTableData.getRoutingTableId())).uniqueResult();
			data.setRoutingTableName(diameterRoutingTableData.getRoutingTableName());
			data.setOverloadAction(diameterRoutingTableData.getOverloadAction());
			data.setResultCode(diameterRoutingTableData.getResultCode());
			data.setRoutingScript(diameterRoutingTableData.getRoutingScript());
			session.update(data);
			session.flush();

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update diameter routing table, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to update diameter routing table, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update diameter routing table, Reason: " + e.getMessage(), e);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
	}

	@Override
	public String getDiameterPeerNameById(String peerId) throws DataManagerException {
		DiameterPeerData diameterPeerData = null;
		try{	
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterPeerData.class).add(Restrictions.eq("peerUUID", peerId));
			List<DiameterPeerData> diameterPeerDataList = criteria.list();
			if(diameterPeerDataList != null && diameterPeerDataList.isEmpty() == false){
				diameterPeerData = diameterPeerDataList.get(0);
			}
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive peer name, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive peer name, Reason: " + e.getMessage(), e);
		}
		return diameterPeerData.getName();
	}

	
	@Override
	public DiameterRoutingTableData getDiameterRoutingTableById(String diameterRoutingTableId) throws DataManagerException {
		
		return getDiameterRoutingTable(ROUTING_TABLE_ID, diameterRoutingTableId);
		
	}

	@Override
	public DiameterRoutingTableData getDiameterRoutingTableByName(String diameterRoutingTableName) throws DataManagerException {
	
		return getDiameterRoutingTable(ROUTING_TABLE_NAME, diameterRoutingTableName);
		
	}

	private DiameterRoutingTableData getDiameterRoutingTable(String propertyName, Object value) throws DataManagerException {
		
		String diameterRoutingTableName = (ROUTING_TABLE_NAME.equals(propertyName)) ? (String) value : "Diameter Routing Table";
		
		try {
			
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(DiameterRoutingTableData.class).add(Restrictions.eq(propertyName, value));
			DiameterRoutingTableData diameterRoutingTable = (DiameterRoutingTableData) criteria.uniqueResult();
			
			if (diameterRoutingTable == null) {
				throw new InvalidValueException("Diameter Routing Table not found");
			}
			
			diameterRoutingTableName = diameterRoutingTable.getRoutingTableName();
			
			criteria = session.createCriteria(DiameterRoutingConfData.class).add(Restrictions.eq(ROUTING_TABLE_ID, diameterRoutingTable.getRoutingTableId()));
			List<DiameterRoutingConfData> diameterRoutingTableEntries = criteria.list();
			
			if (Collectionz.isNullOrEmpty(diameterRoutingTableEntries) == false) {
				diameterRoutingTable.setDiameterRoutingTableEntries(diameterRoutingTableEntries);
			} else {
				diameterRoutingTable.setDiameterRoutingTableEntries(null);
			}
			
			return diameterRoutingTable;
			
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive " + diameterRoutingTableName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive " + diameterRoutingTableName + ", Reason: " + e.getMessage(), e);
		}
		
	}
	

	@Override
	public String create(Object obj) throws DataManagerException {
		
		DiameterRoutingTableData diameterRoutingTable = (DiameterRoutingTableData) obj;
		String diameterRoutingTableName = diameterRoutingTable.getRoutingTableName();
		
		try {
			
			Session session = getSession();
			session.clear();

			session.save(diameterRoutingTable);
			session.flush();
			session.clear();
			
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + diameterRoutingTableName + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			Logger.logTrace(MODULE, he);
			throw new DataManagerException(FAILED_TO_CREATE + diameterRoutingTableName + REASON + he.getMessage(), he);
		} catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + diameterRoutingTableName + REASON + e.getMessage(), e);
		}
		return diameterRoutingTableName;
		
	}
	
	@Override
	public void updateRoutingTableById(DiameterRoutingTableData diameterRoutingTable, String diameterRoutingTableId) throws DataManagerException {
		
		updateRoutingTable(diameterRoutingTable, ROUTING_TABLE_ID, diameterRoutingTableId);
		
	}

	@Override
	public void updateRoutingTableByName(DiameterRoutingTableData diameterRoutingTable, String diameterRoutingTableName) throws DataManagerException {
		
		updateRoutingTable(diameterRoutingTable, ROUTING_TABLE_NAME, diameterRoutingTableName);
		
	}

	private void updateRoutingTable(DiameterRoutingTableData diameterRoutingTable, String propertyName, Object value) throws DataManagerException {

		String diameterRoutingTableName = (ROUTING_TABLE_NAME.equals(propertyName)) ? (String) value : "Diameter Routing Table";

		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(DiameterRoutingTableData.class).add(Restrictions.eq(propertyName, value));
			DiameterRoutingTableData diameterRoutingTableData = (DiameterRoutingTableData) criteria.uniqueResult();

			if (diameterRoutingTableData == null) {
				throw new InvalidValueException("Diameter Routing Table not found");
			}

			diameterRoutingTableName = diameterRoutingTableData.getRoutingTableName();
			
			diameterRoutingTableData.setRoutingTableName(diameterRoutingTable.getRoutingTableName());
			diameterRoutingTableData.setOverloadAction(diameterRoutingTable.getOverloadAction());
			diameterRoutingTableData.setResultCode(diameterRoutingTable.getResultCode());
			
			session.update(diameterRoutingTableData);
			session.flush();
			
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update " + diameterRoutingTableName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to update " + diameterRoutingTableName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update " + diameterRoutingTableName + ", Reason: " + e.getMessage(), e);
		}
		
	}

	
	@Override
	public void updateRoutingEntryById(DiameterRoutingConfData diameterRoutingTableEntry, IStaffData staffData, String diameterRoutingTableEntryId) throws DataManagerException {
		
		updateRoutingEntry(diameterRoutingTableEntry, staffData, ROUTING_CONFIG_ID, diameterRoutingTableEntryId);
		
	}

	@Override
	public void updateRoutingEntryByName(DiameterRoutingConfData diameterRoutingTableEntry, IStaffData staffData, String diameterRoutingTableEntryName) throws DataManagerException {
		
		updateRoutingEntry(diameterRoutingTableEntry, staffData, NAME, diameterRoutingTableEntryName);
		
	}

	private void updateRoutingEntry(DiameterRoutingConfData diameterRoutingTableEntry, IStaffData staffData, String propertyName, Object value) throws DataManagerException {

		String diameterRoutingTableEntryName = (NAME.equals(propertyName)) ? (String) value : "Diameter Routing Table Entry";

		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(DiameterRoutingConfData.class).add(Restrictions.eq(propertyName, value));
			DiameterRoutingConfData diameterRoutingTableDataEntry = (DiameterRoutingConfData) criteria.uniqueResult();

			if (diameterRoutingTableDataEntry == null) {
				throw new InvalidValueException("Diameter Routing Table Entry not found");
			}

			diameterRoutingTableEntryName = diameterRoutingTableDataEntry.getName();
			
			JSONArray jsonArray = ObjectDiffer.diff(diameterRoutingTableDataEntry, diameterRoutingTableEntry);
			
			diameterRoutingTableDataEntry.setName(diameterRoutingTableEntry.getName());
			diameterRoutingTableDataEntry.setRoutingTableId(diameterRoutingTableEntry.getRoutingTableId());
			diameterRoutingTableDataEntry.setDescription(diameterRoutingTableEntry.getDescription());
			diameterRoutingTableDataEntry.setRealmName(diameterRoutingTableEntry.getRealmName());
			diameterRoutingTableDataEntry.setAppIds(diameterRoutingTableEntry.getAppIds());
			diameterRoutingTableDataEntry.setOriginHost(diameterRoutingTableEntry.getOriginHost());
			diameterRoutingTableDataEntry.setOriginRealm(diameterRoutingTableEntry.getOriginRealm());
			diameterRoutingTableDataEntry.setRuleset(diameterRoutingTableEntry.getRuleset());
			if(diameterRoutingTableEntry.getTransMapConfId()!=null){
				diameterRoutingTableDataEntry.setTransMapConfId(diameterRoutingTableEntry.getTransMapConfId());
				diameterRoutingTableDataEntry.setCopyPacketMapId(null);
			}else if(diameterRoutingTableEntry.getCopyPacketMapId()!=null){
				diameterRoutingTableDataEntry.setTransMapConfId(null);
				diameterRoutingTableDataEntry.setCopyPacketMapId(diameterRoutingTableEntry.getCopyPacketMapId());
			}
			else{
				diameterRoutingTableDataEntry.setTransMapConfId(null);
				diameterRoutingTableDataEntry.setCopyPacketMapId(null);
			}
			diameterRoutingTableDataEntry.setRoutingAction(diameterRoutingTableEntry.getRoutingAction());
			diameterRoutingTableDataEntry.setStatefulRouting(diameterRoutingTableEntry.getStatefulRouting());
			diameterRoutingTableDataEntry.setTransactionTimeout(diameterRoutingTableEntry.getTransactionTimeout());
			diameterRoutingTableDataEntry.setAttachedRedirection(diameterRoutingTableEntry.getAttachedRedirection());
			diameterRoutingTableDataEntry.setImsiBasedRoutingTableId(diameterRoutingTableEntry.getImsiBasedRoutingTableId());
			diameterRoutingTableDataEntry.setMsisdnBasedRoutingTableId(diameterRoutingTableEntry.getMsisdnBasedRoutingTableId());
			diameterRoutingTableDataEntry.setOrderNumber(diameterRoutingTableEntry.getOrderNumber());
			diameterRoutingTableDataEntry.setLastModifiedDate(diameterRoutingTableEntry.getLastModifiedDate());
			diameterRoutingTableDataEntry.setLastModifiedByStaffId(diameterRoutingTableEntry.getLastModifiedByStaffId());
			diameterRoutingTableDataEntry.setSubsciberMode(diameterRoutingTableEntry.getSubsciberMode());
			
			if (Collectionz.isNullOrEmpty(diameterRoutingTableDataEntry.getDiameterPeerGroupDataSet()) == false) {
				Set<DiameterPeerGroupData> diameterPeerGroupDatas = diameterRoutingTableDataEntry.getDiameterPeerGroupDataSet();
				for (DiameterPeerGroupData diameterPeerGroupData : diameterPeerGroupDatas) {
					if (Collectionz.isNullOrEmpty(diameterPeerGroupData.getDiameterPeerGroupRelDataSet()) == false) {
						deleteObjectSet(diameterPeerGroupData.getDiameterPeerGroupRelDataSet(), session);
					}
				}
				deleteObjectSet(diameterRoutingTableDataEntry.getDiameterPeerGroupDataSet(), session);
			}
			
			if (Collectionz.isNullOrEmpty(diameterRoutingTableEntry.getDiameterPeerGroupDataSet()) == false) {
				Set<DiameterPeerGroupData> diameterPeerGroupDatas = diameterRoutingTableEntry.getDiameterPeerGroupDataSet();
				int orderNumber = 1;
				for (DiameterPeerGroupData diameterPeerGroupData : diameterPeerGroupDatas) {
					diameterPeerGroupData.setRoutingConfigId(diameterRoutingTableDataEntry.getRoutingConfigId());
					diameterPeerGroupData.setOrderNumber(orderNumber++);
					session.save(diameterPeerGroupData);
					if (Collectionz.isNullOrEmpty(diameterPeerGroupData.getDiameterPeerGroupRelDataSet()) == false) {
						Set<DiameterPeerGroupRelData> diameterPeerGroupRelDatas = diameterPeerGroupData.getDiameterPeerGroupRelDataSet();
						for (DiameterPeerGroupRelData diameterPeerGroupRelData : diameterPeerGroupRelDatas) {
							diameterPeerGroupRelData.setPeerGroupId(diameterPeerGroupData.getPeerGroupId());
							session.save(diameterPeerGroupRelData);
							session.flush();
						}
					}
				}
			}
			
			if (Collectionz.isNullOrEmpty(diameterRoutingTableDataEntry.getDiameterRoutingConfigFailureParamSet()) == false) {
				deleteObjectSet(diameterRoutingTableDataEntry.getDiameterRoutingConfigFailureParamSet(), session);
			}
			
			if (Collectionz.isNullOrEmpty(diameterRoutingTableEntry.getDiameterRoutingConfigFailureParamSet()) == false) {
				Set<DiameterRoutingConfigFailureParam> diameterRoutingConfigFailureParams = diameterRoutingTableEntry.getDiameterRoutingConfigFailureParamSet();
				int orderNumber = 1;
				for (DiameterRoutingConfigFailureParam diameterRoutingConfigFailureParam : diameterRoutingConfigFailureParams) {
					diameterRoutingConfigFailureParam.setDiameterRoutingConfData(diameterRoutingTableDataEntry);
					diameterRoutingConfigFailureParam.setOrderNumber(orderNumber++);
					session.save(diameterRoutingConfigFailureParam);
					session.flush();
				}
			}
			
			session.update(diameterRoutingTableDataEntry);
			session.flush();
			
			staffData.setAuditId(diameterRoutingTableDataEntry.getAuditUId());
			staffData.setAuditName(diameterRoutingTableDataEntry.getName());
			
			doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_DIAMETER_ROUTING_TABLE);
			
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update " + diameterRoutingTableEntryName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to update " + diameterRoutingTableEntryName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update " + diameterRoutingTableEntryName + ", Reason: " + e.getMessage(), e);
		}
		
	}

	
	@Override
	public String deleteRoutingTableById(String diameterRoutingTableId) throws DataManagerException {
		
		return deleteRoutingTable(ROUTING_TABLE_ID, diameterRoutingTableId);

	}

	@Override
	public String deleteRoutingTableByName(String diameterRoutingTableName) throws DataManagerException {

		return deleteRoutingTable(ROUTING_TABLE_NAME, diameterRoutingTableName);

	}
	
	private String deleteRoutingTable(String propertyName, Object value) throws DataManagerException {
		
		String diameterRoutingTableName = (ROUTING_TABLE_NAME.equals(propertyName)) ? (String) value : "Diameter Routing Table";

		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(DiameterRoutingTableData.class).add(Restrictions.eq(propertyName, value));
			DiameterRoutingTableData diameterRoutingTable = (DiameterRoutingTableData) criteria.uniqueResult();

			if (diameterRoutingTable == null) {
				throw new InvalidValueException("Diameter Routing Table not found");
			}

			diameterRoutingTableName = diameterRoutingTable.getRoutingTableName();
			
			criteria = session.createCriteria(DiameterRoutingConfData.class).add(Restrictions.eq(ROUTING_TABLE_ID, diameterRoutingTable.getRoutingTableId()));
			List<DiameterRoutingConfData> diameterRoutingTableEntries = criteria.list();
			
			if (Collectionz.isNullOrEmpty(diameterRoutingTableEntries) == false) {
				for (DiameterRoutingConfData diameterRoutingTableEntry : diameterRoutingTableEntries) {
					if (Collectionz.isNullOrEmpty(diameterRoutingTableEntry.getDiameterPeerGroupDataSet()) == false) {
						Set<DiameterPeerGroupData> diameterPeerGroupDatas = diameterRoutingTableEntry.getDiameterPeerGroupDataSet();
						for (DiameterPeerGroupData diameterPeerGroupData : diameterPeerGroupDatas) {
							if (Collectionz.isNullOrEmpty(diameterPeerGroupData.getDiameterPeerGroupRelDataSet()) == false) {
								deleteObjectSet(diameterPeerGroupData.getDiameterPeerGroupRelDataSet(), session);
							}
						}
						deleteObjectSet(diameterRoutingTableEntry.getDiameterPeerGroupDataSet(), session);
					}
					if (Collectionz.isNullOrEmpty(diameterRoutingTableEntry.getDiameterRoutingConfigFailureParamSet()) == false) {
						deleteObjectSet(diameterRoutingTableEntry.getDiameterRoutingConfigFailureParamSet(), session);
					}
				}
				deleteObjectList(diameterRoutingTableEntries, session);
			} 
			
			session.delete(diameterRoutingTable);
			session.flush();

			return diameterRoutingTableName;

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + diameterRoutingTableName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to delete " + diameterRoutingTableName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to delete " + diameterRoutingTableName + ", Reason: " + e.getMessage(), e);
		} 

	}

	
	@Override
	public String deleteRoutingEntryById(String diameterRoutingTableEntryId) throws DataManagerException {
		
		return deleteRoutingEntry(ROUTING_CONFIG_ID, diameterRoutingTableEntryId);

	}

	@Override
	public String deleteRoutingEntryByName(String diameterRoutingTableEntryName) throws DataManagerException {
		
		return deleteRoutingEntry(NAME, diameterRoutingTableEntryName);

	}

	private String deleteRoutingEntry(String propertyName, Object value) throws DataManagerException {
		
		String diameterRoutingTableEntryName = (NAME.equals(propertyName)) ? (String) value : "Diameter Routing Table Entry";

		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(DiameterRoutingConfData.class).add(Restrictions.eq(propertyName, value));
			DiameterRoutingConfData diameterRoutingTableEntry = (DiameterRoutingConfData) criteria.uniqueResult();
			
			if (diameterRoutingTableEntry == null) {
				throw new InvalidValueException("Diameter Routing Table Entry not found");
			}
			
			diameterRoutingTableEntryName = diameterRoutingTableEntry.getName();
			
			if (Collectionz.isNullOrEmpty(diameterRoutingTableEntry.getDiameterPeerGroupDataSet()) == false) {
				Set<DiameterPeerGroupData> diameterPeerGroupDatas = diameterRoutingTableEntry.getDiameterPeerGroupDataSet();
				for (DiameterPeerGroupData diameterPeerGroupData : diameterPeerGroupDatas) {
					if (Collectionz.isNullOrEmpty(diameterPeerGroupData.getDiameterPeerGroupRelDataSet()) == false) {
						deleteObjectSet(diameterPeerGroupData.getDiameterPeerGroupRelDataSet(), session);
					}
				}
				deleteObjectSet(diameterRoutingTableEntry.getDiameterPeerGroupDataSet(), session);
			}
			
			if (Collectionz.isNullOrEmpty(diameterRoutingTableEntry.getDiameterRoutingConfigFailureParamSet()) == false) {
				deleteObjectSet(diameterRoutingTableEntry.getDiameterRoutingConfigFailureParamSet(), session);
			}
			
			session.delete(diameterRoutingTableEntry);
			session.flush();
			
			return diameterRoutingTableEntryName;

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + diameterRoutingTableEntryName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to delete " + diameterRoutingTableEntryName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to delete " + diameterRoutingTableEntryName + ", Reason: " + e.getMessage(), e);
		} 

	}

	
}