/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   HIMSIBasedRoutingTableDataManager.java                 		
 * ModualName diameter    			      		
 * Created on 1 March, 2015
 * Last Modified on  16 March, 2015                                   
 * @author :  nayana.rathod
 */                                                     
package com.elitecore.elitesm.hibernate.diameter.imsibasedroutingtable;

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
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.IMSIBasedRoutingTableDataManager;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIFieldMappingData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.diameter.subscriberroutingtable.IMSIMappingData;

import net.sf.json.JSONArray;

public class HIMSIBasedRoutingTableDataManager extends HBaseDataManager implements IMSIBasedRoutingTableDataManager{

	@Override
	public List<IMSIBasedRoutingTableData> searchImsiBasedRoutingTable() throws DataManagerException {
        List<IMSIBasedRoutingTableData> imsiBasedRoutingTableList = new ArrayList<IMSIBasedRoutingTableData>();
        try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(IMSIBasedRoutingTableData.class);
			
            criteria.addOrder(Order.asc("routingTableName"));
    		List list = criteria.list();
			int totalItems = list.size();
			
			imsiBasedRoutingTableList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return imsiBasedRoutingTableList;
	}

	@Override
	public void create(IMSIBasedRoutingTableData imsiBasedRoutingTableData) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = null; 
			criteria = session.createCriteria(IMSIBasedRoutingTableData.class);
			
			//fetch next audit id
			String auditId= UUIDGenerator.generate();
			
			imsiBasedRoutingTableData.setAuditUId(auditId);
			
			session.save(imsiBasedRoutingTableData);
			session.flush();
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}
	
	@Override
	public IMSIBasedRoutingTableData getImsiBasedRoutingTableData(String routingTableId) throws DataManagerException {
		try{
			org.hibernate.Session session = getSession();
			Criteria criteria = session.createCriteria(IMSIBasedRoutingTableData.class);
			IMSIBasedRoutingTableData imsiBasedRoutingtableData = (IMSIBasedRoutingTableData)criteria.add(Restrictions.eq("routingTableId",routingTableId)).uniqueResult();
			return imsiBasedRoutingtableData;
		}catch(HibernateException e){
			throw new DataManagerException(e.getMessage(),e);
		}	
	}

	@Override
	public String addEntries(IMSIFieldMappingData imsiFieldMappingData, IStaffData staffdata, String actionAlias)throws DataManagerException {
		String fieldMapId = null;
		try{
			Session session = getSession();
			Criteria criteria = null; 
			criteria = session.createCriteria(IMSIFieldMappingData.class).setProjection(Projections.max("orderNumber")); 
			
			List  maxOrderNumber = criteria.list();
			IMSIFieldMappingData fieldMappingData = new IMSIFieldMappingData();
	
			if(!maxOrderNumber.isEmpty() && maxOrderNumber.get(0) != null){
				Integer orderNumber = (Integer) maxOrderNumber.get(0);
				imsiFieldMappingData.setOrderNumber(++orderNumber);
			} else {
				imsiFieldMappingData.setOrderNumber(1);
			}
			
			JSONArray jsonArray=ObjectDiffer.diff(fieldMappingData, imsiFieldMappingData);
			
			session.save(imsiFieldMappingData);
			session.flush();
			
			fieldMapId = imsiFieldMappingData.getImsiFieldMapId();
			
			doAuditingJson(jsonArray.toString(),staffdata,actionAlias);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return fieldMapId;
		
	}

	@Override
	public void updateEntries(IMSIFieldMappingData imsiFieldMappingData, IStaffData staffData, String actionAlias) throws DataManagerException {
		try{
			org.hibernate.Session session = getSession();
			Criteria criteria = session.createCriteria(IMSIFieldMappingData.class);
			IMSIFieldMappingData data = (IMSIFieldMappingData)criteria.add(Restrictions.eq("imsiFieldMapId",imsiFieldMappingData.getImsiFieldMapId())).uniqueResult();
			
			JSONArray jsonArray=ObjectDiffer.diff(data,imsiFieldMappingData);
			
			data.setImsiRange(imsiFieldMappingData.getImsiRange());
			data.setPrimaryPeerName(imsiFieldMappingData.getPrimaryPeerName());
			data.setSecondaryPeerName(imsiFieldMappingData.getSecondaryPeerName());
			data.setPrimaryPeerId(imsiFieldMappingData.getPrimaryPeerId());
			data.setSecondaryPeerId(imsiFieldMappingData.getSecondaryPeerId());
			data.setTag(imsiFieldMappingData.getTag());
			
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
	public void updateRoutingTable(IMSIBasedRoutingTableData imsiBasedRoutingTableData, IStaffData staffData, String actionAlias) throws DataManagerException {
		try{
			org.hibernate.Session session = getSession();
			Criteria criteria = session.createCriteria(IMSIBasedRoutingTableData.class);
			IMSIBasedRoutingTableData data = (IMSIBasedRoutingTableData)criteria.add(Restrictions.eq("routingTableId",imsiBasedRoutingTableData.getRoutingTableId())).uniqueResult();
			
			JSONArray jsonArray=ObjectDiffer.diff(data,imsiBasedRoutingTableData);
			
			data.setImsiIdentityAttributes(imsiBasedRoutingTableData.getImsiIdentityAttributes());
			data.setRoutingTableName(imsiBasedRoutingTableData.getRoutingTableName());
			data.setAuditUId(imsiBasedRoutingTableData.getAuditUId());
		
			if(imsiBasedRoutingTableData.getAuditUId() == null){
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
	public void deleteEntries(String imsiFieldMapId,IStaffData staffData,String actionAlias) throws DataManagerException {
		try{
			Session session = getSession();
			IMSIFieldMappingData imsiFieldMapData = (IMSIFieldMappingData) session.createCriteria(IMSIFieldMappingData.class).add(Restrictions.eq("imsiFieldMapId", imsiFieldMapId)).uniqueResult();
			session.delete(imsiFieldMapData);
			
			JSONArray jsonArray=ObjectDiffer.diff(imsiFieldMapData,new IMSIFieldMappingData());
			
			if(jsonArray.toString().length() > 2){
				doAuditingJson(jsonArray.toString(),staffData,actionAlias);
			}
			
			Logger.logDebug("IMSI-BASED-ROUTING-TABLE", "Record Deleted Successfully.");
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
			IMSIBasedRoutingTableData imsiBasedRoutingTableData = (IMSIBasedRoutingTableData) session.createCriteria(IMSIBasedRoutingTableData.class).add(Restrictions.eq("routingTableId", routingTableId)).uniqueResult();
			session.delete(imsiBasedRoutingTableData);
			Logger.logDebug("IMSI-BASED-ROUTING-TABLE", "Record Deleted Successfully.");
		}catch(org.hibernate.exception.ConstraintViolationException cve){
			throw cve;	 
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public List<IMSIFieldMappingData> getIMSIConfigDataList(String routingTableId) throws DataManagerException {
		PageList pageList = null;
		List<IMSIFieldMappingData>  imsiConfigDataList=null;
        try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(IMSIFieldMappingData.class);
			if(routingTableId != null)
			{
				criteria.add(Restrictions.eq("routingTableId",routingTableId));
			}	
			criteria.addOrder(Order.asc("imsiFieldMapId"));
            List list = criteria.list();
			int totalItems = list.size();
			 imsiConfigDataList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return imsiConfigDataList;
	}

	@Override
	public void importImsiBasedConfigurtaion( IMSIBasedRoutingTableData imsiBasedRoutingTableData) throws DataManagerException {
	  try{
		 Session session = getSession();
		 Criteria criteria = session.createCriteria(IMSIFieldMappingData.class);
		 String routingTableId = imsiBasedRoutingTableData.getRoutingTableId();

		 if(imsiBasedRoutingTableData.getImsiFieldMappingDataSet() != null && imsiBasedRoutingTableData.getImsiFieldMappingDataSet().size() > 0){
			Set<IMSIFieldMappingData> tempList = imsiBasedRoutingTableData.getImsiFieldMappingDataSet();
			if(tempList != null && tempList.isEmpty() == false){
				int orderNumber = 1;
				for(Iterator<IMSIFieldMappingData> iteratorPacketDetail = tempList.iterator(); iteratorPacketDetail.hasNext(); ){
					IMSIFieldMappingData imsiFieldMapData = iteratorPacketDetail.next();
					imsiFieldMapData.setRoutingTableId(routingTableId);
					imsiFieldMapData.setOrderNumber(orderNumber++);
					session.save(imsiFieldMapData);
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
	public List<IMSIFieldMappingData> getIMSIConfigDataList(String routingTableId, String peerId) throws DataManagerException {
		PageList pageList = null;
		List<IMSIFieldMappingData>  imsiConfigDataList=null;
        try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(IMSIFieldMappingData.class);
			if(routingTableId != null)
			{
				criteria.add(Restrictions.eq("routingTableId",routingTableId)).add(Restrictions.eq("primaryPeerId",peerId));
			}	
			criteria.addOrder(Order.asc("imsiFieldMapId"));
            List list = criteria.list();
			int totalItems = list.size();
			 imsiConfigDataList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return imsiConfigDataList;
	}

	@Override
	public List<IMSIBasedRoutingTableData> getIMSIBasedRoutingTableList() throws DataManagerException {
		PageList pageList = null;
		List<IMSIBasedRoutingTableData>  imsiConfigDataList=null;
        try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(IMSIBasedRoutingTableData.class);
			
			criteria.addOrder(Order.asc("routingTableId"));
            List list = criteria.list();
			int totalItems = list.size();
			imsiConfigDataList = criteria.list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return imsiConfigDataList;
	}

	@Override
	public IMSIBasedRoutingTableData getIMSIDataByName(String routingTableName) throws DataManagerException {
		try{
			org.hibernate.Session session = getSession();
			Criteria criteria = session.createCriteria(IMSIBasedRoutingTableData.class);
			IMSIBasedRoutingTableData imsiBasedRoutingtableData = (IMSIBasedRoutingTableData)criteria.add(Restrictions.eq("routingTableName",routingTableName)).uniqueResult();
			return imsiBasedRoutingtableData;
		}catch(HibernateException e){
			throw new DataManagerException(e.getMessage(),e);
		}	
	}

	@Override
	public PageList searchSubscriberBasedOnImsi(String subscriberDetails, Map infoMap) throws DataManagerException {
		PageList pageList = null; 
        int pageNo;
        int pageSize;
        List<IMSIMappingData> imsiMappingDataList = new ArrayList<IMSIMappingData>();
        List<IMSIFieldMappingData> imsiFieldMappingDataList = new  ArrayList<IMSIFieldMappingData>();
        try{
			pageNo=(Integer)infoMap.get("pageNo");
			pageSize=(Integer)infoMap.get("pageSize");
			Session session = getSession();
			Criteria criteria = session.createCriteria(IMSIFieldMappingData.class);
			criteria.add(Restrictions.ilike("imsiRange", subscriberDetails, MatchMode.ANYWHERE));
			
			int totalItems =  criteria.list().size();
			criteria.setFirstResult(((pageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}

			imsiFieldMappingDataList = criteria.list();
			
			if( imsiFieldMappingDataList != null ){
				for( IMSIFieldMappingData imsiFieldMappingData : imsiFieldMappingDataList ){
					criteria = session.createCriteria(IMSIBasedRoutingTableData.class);
					IMSIBasedRoutingTableData  msisdnBasedRoutingTableData = (IMSIBasedRoutingTableData)criteria.add(Restrictions.eq("routingTableId",imsiFieldMappingData.getRoutingTableId())).uniqueResult();
					
					IMSIMappingData imsiMappingData = new IMSIMappingData();
					imsiMappingData.setImsiFieldMapId(imsiFieldMappingData.getImsiFieldMapId());
					imsiMappingData.setImsiRange(imsiFieldMappingData.getImsiRange());
					imsiMappingData.setPrimaryPeerId(imsiFieldMappingData.getPrimaryPeerId());
					imsiMappingData.setPrimaryPeerName(imsiFieldMappingData.getPrimaryPeerName());
					imsiMappingData.setSecondaryPeerId(imsiFieldMappingData.getSecondaryPeerId());
					imsiMappingData.setSecondaryPeerName(imsiFieldMappingData.getSecondaryPeerName());
					imsiMappingData.setTag(imsiFieldMappingData.getTag());
					imsiMappingData.setRoutingTableId(msisdnBasedRoutingTableData.getRoutingTableId());
					imsiMappingData.setRoutingTableName(msisdnBasedRoutingTableData.getRoutingTableName());
					imsiMappingDataList.add(imsiMappingData);
				}
			}
			
			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(imsiMappingDataList, pageNo, totalPages ,totalItems);

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return pageList;
	}
}
