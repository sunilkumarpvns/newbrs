/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   HDiameterpolicyDataManager.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.hibernate.diameter.msisdnbasedroutingtable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.MSISDNBasedRoutingTableDataManager;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNFieldMappingData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.diameter.subscriberroutingtable.MSISDNMappingData;

import net.sf.json.JSONArray;

public class HMSISDNIBasedRoutingTableDataManager extends HBaseDataManager implements MSISDNBasedRoutingTableDataManager{

	@Override
	public List<MSISDNBasedRoutingTableData> searchMSISDNBasedRoutingTable() throws DataManagerException {
		List<MSISDNBasedRoutingTableData> msisdnBasedRoutingTableList = new ArrayList<MSISDNBasedRoutingTableData>();
		
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(MSISDNBasedRoutingTableData.class);
			
            criteria.addOrder(Order.asc("routingTableName"));
    		List list = criteria.list();
			int totalItems = list.size();
			
			msisdnBasedRoutingTableList = criteria.list();

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return msisdnBasedRoutingTableList;
	}

	@Override
	public void create(MSISDNBasedRoutingTableData msisdnBasedRoutingTableData)throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = null; 
			criteria = session.createCriteria(MSISDNBasedRoutingTableData.class);
			
			//fetch next audit id
			String auditId= UUIDGenerator.generate();
			
			msisdnBasedRoutingTableData.setAuditUId(auditId);
			
			session.save(msisdnBasedRoutingTableData);
			session.flush();
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}


	@Override
	public MSISDNBasedRoutingTableData getMsisdnBasedRoutingTableData(String routingTableId) throws DataManagerException {
		try{
			org.hibernate.Session session = getSession();
			Criteria criteria = session.createCriteria(MSISDNBasedRoutingTableData.class);
			MSISDNBasedRoutingTableData msisdnBasedRoutingtableData = (MSISDNBasedRoutingTableData)criteria.add(Restrictions.eq("routingTableId",routingTableId)).uniqueResult();
			return msisdnBasedRoutingtableData;
		}catch(HibernateException e){
			throw new DataManagerException(e.getMessage(),e);
		}	
	}

	@Override
	public String addEntries(MSISDNFieldMappingData msisdnFieldMappingData, IStaffData staffData, String actionAlias)throws DataManagerException {
		String fieldMapId = null;
		try{
			Session session = getSession();
			Criteria criteria = null; 
			
			criteria = session.createCriteria(MSISDNFieldMappingData.class).setProjection(Projections.max("orderNumber")); 
			
			List  maxOrderNumber = criteria.list();
			MSISDNFieldMappingData fieldMappingData = new MSISDNFieldMappingData();
	
			if(!maxOrderNumber.isEmpty() && maxOrderNumber.get(0) != null){
				Integer orderNumber = (Integer) maxOrderNumber.get(0);
				msisdnFieldMappingData.setOrderNumber(++orderNumber);
			} else {
				msisdnFieldMappingData.setOrderNumber(1);
			}
			
			JSONArray jsonArray=ObjectDiffer.diff(fieldMappingData, msisdnFieldMappingData);
			
			session.save(msisdnFieldMappingData);
			session.flush();
			
			fieldMapId = msisdnFieldMappingData.getMsisdnFieldMapId();
			
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return fieldMapId;
	}

	@Override
	public void updateEntries(MSISDNFieldMappingData msisdnFieldMappingData,IStaffData staffData, String actionAlias) throws DataManagerException {
		try{
			org.hibernate.Session session = getSession();
			Criteria criteria = session.createCriteria(MSISDNFieldMappingData.class);
			MSISDNFieldMappingData data = (MSISDNFieldMappingData)criteria.add(Restrictions.eq("msisdnFieldMapId",msisdnFieldMappingData.getMsisdnFieldMapId())).uniqueResult();
			
			JSONArray jsonArray=ObjectDiffer.diff(data,msisdnFieldMappingData);
			
			data.setMsisdnRange(msisdnFieldMappingData.getMsisdnRange());
			data.setPrimaryPeerName(msisdnFieldMappingData.getPrimaryPeerName());
			data.setSecondaryPeerName(msisdnFieldMappingData.getSecondaryPeerName());
			data.setPrimaryPeerId(msisdnFieldMappingData.getPrimaryPeerId());
			data.setSecondaryPeerId(msisdnFieldMappingData.getSecondaryPeerId());
			data.setTag(msisdnFieldMappingData.getTag());
			
			session.update(data);
			session.flush();
			
			if(jsonArray.toString().length() > 2){
				doAuditingJson(jsonArray.toString(),staffData,actionAlias);
			}
			
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}
	}

	@Override
	public void updateRoutingTable( MSISDNBasedRoutingTableData msisdnBasedRoutingTableData, IStaffData staffData, String actionAlias) throws DataManagerException {
		try{
			org.hibernate.Session session = getSession();
			Criteria criteria = session.createCriteria(MSISDNBasedRoutingTableData.class);
			MSISDNBasedRoutingTableData data = (MSISDNBasedRoutingTableData)criteria.add(Restrictions.eq("routingTableId",msisdnBasedRoutingTableData.getRoutingTableId())).uniqueResult();
			
			JSONArray jsonArray=ObjectDiffer.diff(data,msisdnBasedRoutingTableData);
			
			data.setMsisdnIdentityAttributes(msisdnBasedRoutingTableData.getMsisdnIdentityAttributes());
			data.setRoutingTableName(msisdnBasedRoutingTableData.getRoutingTableName());
			data.setAuditUId(msisdnBasedRoutingTableData.getAuditUId());
			data.setMcc(msisdnBasedRoutingTableData.getMcc());
			data.setMsisdnLength(msisdnBasedRoutingTableData.getMsisdnLength());
		

			if(msisdnBasedRoutingTableData.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
	 			data.setAuditUId(auditId);
			}
			
			session.update(data);
			session.flush();
			
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
			
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}
		
	}

	@Override
	public void deleteEntries(String fieldMappingId,IStaffData staffData,String actionAlias) throws DataManagerException {
		try{
			Session session = getSession();
			MSISDNFieldMappingData msisdnFieldMapData = (MSISDNFieldMappingData) session.createCriteria(MSISDNFieldMappingData.class).add(Restrictions.eq("msisdnFieldMapId", fieldMappingId)).uniqueResult();
			session.delete(msisdnFieldMapData);
			Logger.logDebug("MSISDN-BASED-ROUTING-TABLE", "Record Deleted Successfully.");
			
			JSONArray jsonArray=ObjectDiffer.diff(msisdnFieldMapData,new MSISDNFieldMappingData());
			
			if(jsonArray.toString().length() > 2){
				doAuditingJson(jsonArray.toString(),staffData,actionAlias);
			}
			
		}catch(org.hibernate.exception.ConstraintViolationException cve){
			throw cve;	 
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public void deleteRoutingTable(String routingTableId) throws DataManagerException {
		try{
			Session session = getSession();
			MSISDNBasedRoutingTableData msisdnBasedRoutingTableData = (MSISDNBasedRoutingTableData) session.createCriteria(MSISDNBasedRoutingTableData.class).add(Restrictions.eq("routingTableId", routingTableId)).uniqueResult();
			session.delete(msisdnBasedRoutingTableData);
			Logger.logDebug("MSISDN-BASED-ROUTING-TABLE", "Record Deleted Successfully.");
		}catch(org.hibernate.exception.ConstraintViolationException cve){
			throw cve;	 
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public List<MSISDNFieldMappingData> getMSISDNConfigDataList(String routingTableId) throws DataManagerException {
		PageList pageList = null;
		List<MSISDNFieldMappingData>  msisdnConfigDataList=null;
        try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(MSISDNFieldMappingData.class);
			if(routingTableId != null)
			{
				criteria.add(Restrictions.eq("routingTableId",routingTableId));
			}	
			criteria.addOrder(Order.asc("msisdnFieldMapId"));
            List list = criteria.list();
			int totalItems = list.size();
			msisdnConfigDataList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return msisdnConfigDataList;
	}

	@Override
	public void importMsisdnBasedConfigurtaion( MSISDNBasedRoutingTableData msisdnBasedRoutingTableData) throws DataManagerException {
		 try{
			 Session session = getSession();
			 Criteria criteria = session.createCriteria(MSISDNFieldMappingData.class);
			 String routingTableId = msisdnBasedRoutingTableData.getRoutingTableId();

			 if(msisdnBasedRoutingTableData.getMsisdnFieldMappingDataSet() != null && msisdnBasedRoutingTableData.getMsisdnFieldMappingDataSet().size() > 0){
				Set<MSISDNFieldMappingData> tempList = msisdnBasedRoutingTableData.getMsisdnFieldMappingDataSet();
				int orderNumber = 1;
				if(tempList != null && tempList.isEmpty() == false){
					for(Iterator<MSISDNFieldMappingData> iteratorPacketDetail = tempList.iterator(); iteratorPacketDetail.hasNext(); ){
						MSISDNFieldMappingData msisdnFieldMapData = iteratorPacketDetail.next();
						msisdnFieldMapData.setRoutingTableId(routingTableId);
						msisdnFieldMapData.setOrderNumber(orderNumber++);
						session.save(msisdnFieldMapData);
						session.flush();
					}
				}
			}
		 }catch(HibernateException hExp){
				throw new DataManagerException(hExp.getMessage(), hExp);
		 }catch(Exception exp){
				throw new DataManagerException(exp.getMessage(), exp);
		 }
	}

	@Override
	public List<MSISDNFieldMappingData> getMSISDNConfigDataList( String routingTableId, String peerId) throws DataManagerException {
		PageList pageList = null;
		List<MSISDNFieldMappingData>  msisdnConfigDataList=null;
        try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(MSISDNFieldMappingData.class);
			if(routingTableId != null)
			{
				criteria.add(Restrictions.eq("routingTableId",routingTableId)).add(Restrictions.eq("primaryPeerId",peerId));
			}	
			criteria.addOrder(Order.asc("msisdnFieldMapId"));
            List list = criteria.list();
			int totalItems = list.size();
			 msisdnConfigDataList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return msisdnConfigDataList;
	}

	@Override
	public List<MSISDNBasedRoutingTableData> getMSISDNBasedRoutingTableList() throws DataManagerException {
		PageList pageList = null;
		List<MSISDNBasedRoutingTableData>  msisdnConfigDataList=null;
        try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(MSISDNBasedRoutingTableData.class);
			
			criteria.addOrder(Order.asc("routingTableId"));
            List list = criteria.list();
			int totalItems = list.size();
			msisdnConfigDataList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return msisdnConfigDataList;
	}

	@Override
	public MSISDNBasedRoutingTableData getMSISDNDataByName(String routingTableName) throws DataManagerException {
		try{
			org.hibernate.Session session = getSession();
			Criteria criteria = session.createCriteria(MSISDNBasedRoutingTableData.class);
			MSISDNBasedRoutingTableData msisdnBasedRoutingtableData = (MSISDNBasedRoutingTableData)criteria.add(Restrictions.eq("routingTableName",routingTableName)).uniqueResult();
			return msisdnBasedRoutingtableData;
		}catch(HibernateException e){
			throw new DataManagerException(e.getMessage(),e);
		}	
	}

	@Override
	public PageList searchSubscriberBasedOnMsisdn(String subscriberDetails, Map infoMap) throws DataManagerException {
		PageList pageList = null; 
        int pageNo;
        int pageSize;
        List<MSISDNMappingData> msisdnMappingDataList = new ArrayList<MSISDNMappingData>();
        List<MSISDNFieldMappingData> msisdnFieldMappingDataList = new  ArrayList<MSISDNFieldMappingData>();
        try{
			pageNo=(Integer)infoMap.get("pageNo");
			pageSize=(Integer)infoMap.get("pageSize");
			Session session = getSession();
			Criteria criteria = session.createCriteria(MSISDNFieldMappingData.class);
			criteria.add(Restrictions.ilike("msisdnRange", subscriberDetails, MatchMode.ANYWHERE));
			
			int totalItems =  criteria.list().size();
			criteria.setFirstResult(((pageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}

			msisdnFieldMappingDataList = criteria.list();
			
			if( msisdnFieldMappingDataList != null ){
				for( MSISDNFieldMappingData msisdnFieldMappingData : msisdnFieldMappingDataList ){
					criteria = session.createCriteria(MSISDNBasedRoutingTableData.class);
					MSISDNBasedRoutingTableData  msisdnBasedRoutingTableData = (MSISDNBasedRoutingTableData)criteria.add(Restrictions.eq("routingTableId",msisdnFieldMappingData.getRoutingTableId())).uniqueResult();
					MSISDNMappingData msisdnMappingData = new MSISDNMappingData();
					msisdnMappingData.setMsisdnFieldMapId(msisdnFieldMappingData.getMsisdnFieldMapId());
					msisdnMappingData.setMsisdnRange(msisdnFieldMappingData.getMsisdnRange());
					msisdnMappingData.setPrimaryPeerId(msisdnFieldMappingData.getPrimaryPeerId());
					msisdnMappingData.setPrimaryPeerName(msisdnFieldMappingData.getPrimaryPeerName());
					msisdnMappingData.setSecondaryPeerId(msisdnFieldMappingData.getSecondaryPeerId());
					msisdnMappingData.setSecondaryPeerName(msisdnFieldMappingData.getSecondaryPeerName());
					msisdnMappingData.setTag(msisdnFieldMappingData.getTag());
					msisdnMappingData.setRoutingTableId(msisdnBasedRoutingTableData.getRoutingTableId());
					msisdnMappingData.setRoutingTableName(msisdnBasedRoutingTableData.getRoutingTableName());
					msisdnMappingDataList.add(msisdnMappingData);
					
				}
			}
			
			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(msisdnMappingDataList, pageNo, totalPages ,totalItems);

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return pageList;
	}
}
