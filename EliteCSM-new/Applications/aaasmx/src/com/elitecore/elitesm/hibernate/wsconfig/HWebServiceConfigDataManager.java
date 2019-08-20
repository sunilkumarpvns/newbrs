/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   HWebServiceConfigDataManager.java                 		
 * ModualName wsconfig    			      		
 * Created on 23 December, 2010
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.hibernate.wsconfig;
   
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.wsconfig.WebServiceConfigDataManager;
import com.elitecore.elitesm.datamanager.wsconfig.data.IWSAttrFieldMapData;
import com.elitecore.elitesm.datamanager.wsconfig.data.IWSConfigData;
import com.elitecore.elitesm.datamanager.wsconfig.data.IWSDBFieldMapData;
import com.elitecore.elitesm.datamanager.wsconfig.data.WSConfigData;
import com.elitecore.elitesm.datamanager.wsconfig.data.WSDBFieldMapData;
import com.elitecore.elitesm.datamanager.wsconfig.data.WSKeyMappingData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.WebServiceConstant;
      
public class HWebServiceConfigDataManager extends HBaseDataManager implements WebServiceConfigDataManager{
	
	private static final String WSCONFIG_ID = "wsconfigId";
	private static final String CONFIG_TYPE = "configType";

	public void updateSubscriberConfiguration(IWSConfigData subscriberConfigData,IStaffData staffData) throws DataManagerException {
		Session session = getSession();

		if(subscriberConfigData != null){
			try {
				Criteria criteria = session.createCriteria(WSConfigData.class);
				criteria.add(Restrictions.eq(CONFIG_TYPE,WebServiceConstant.SUBSCRIBER_PROFILE_TYPE));
				WSConfigData data = (WSConfigData)criteria.add(Restrictions.eq(WSCONFIG_ID,subscriberConfigData.getWsconfigId())).uniqueResult();
				if(data == null) {
					throw new InvalidValueException("Subscriber Data not found.");
				}
				
				JSONArray jsonArray=ObjectDiffer.diff(data,(WSConfigData)subscriberConfigData);
				
				data.setDatabasedsId(subscriberConfigData.getDatabasedsId());
				data.setUserIdentityFieldName(subscriberConfigData.getUserIdentityFieldName());
				data.setTableName(subscriberConfigData.getTableName());
				data.setRecordFetchLimit(subscriberConfigData.getRecordFetchLimit());
				data.setPrimaryKeyColumn(subscriberConfigData.getPrimaryKeyColumn());
				data.setSequenceName(subscriberConfigData.getSequenceName());
				
				session.update(data);
				session.flush();
				if(WebServiceConstant.SUBSCRIBER_PROFILE_TYPE.equalsIgnoreCase(data.getConfigType())){
					List<WSKeyMappingData> wsKeyMappingDataList = new ArrayList<WSKeyMappingData>(data.getWsKeyMappingSet());
					if(Collectionz.isNullOrEmpty(wsKeyMappingDataList) == false){   
						deleteObjectList(wsKeyMappingDataList, session);
					}
					
					List<WSKeyMappingData> tempWsKeyMappingDataList = new ArrayList<WSKeyMappingData>(subscriberConfigData.getWsKeyMappingSet());
					
					int size = tempWsKeyMappingDataList.size();
					int orderNumber = 1;
					for(int i=0;i<size;i++){
						WSKeyMappingData wsKeyMappingData = tempWsKeyMappingDataList.get(i);
						wsKeyMappingData.setWsConfigId(subscriberConfigData.getWsconfigId());
						wsKeyMappingData.setOrderNumber(orderNumber++);
						session.save(wsKeyMappingData);
						session.flush();
					}
				}
				
				
				staffData.setAuditId(getAuditId(ConfigConstant.SUBSCRIBER_PROFILE_WEB_SERVICE_CONFIG));
				staffData.setAuditName(ConfigConstant.HYPHEN);
				doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_SUBSCRIBER_PROFILE_WSCONFIG);
			} catch (HibernateException hExp) {
				hExp.printStackTrace();
				throw new DataManagerException(hExp.getMessage(),hExp);
			} catch (Exception exp){
				exp.printStackTrace();
				throw new DataManagerException(exp.getMessage(),exp);
			}
		}else{
			throw new DataManagerException();
		}
	}

	public IWSConfigData getSubscriberConfiguration() throws DataManagerException {
		IWSConfigData subscriberConfigData=null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(WSConfigData.class);
            criteria.add(Restrictions.eq(CONFIG_TYPE,WebServiceConstant.SUBSCRIBER_PROFILE_TYPE));
			List list  = criteria.list();
			if(list!=null && !list.isEmpty()) {
				subscriberConfigData = (IWSConfigData) list.get(0);
			}
			
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
			
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return subscriberConfigData;
	}

	public List<IWSDBFieldMapData> getSubscriberDBFeildMapList(Integer dbConfigId) throws DataManagerException {
		
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(WSDBFieldMapData.class).add(Restrictions.eq("dbConfigId",dbConfigId));

			List<IWSDBFieldMapData> list  =(List<IWSDBFieldMapData>)criteria.list();
			
			return list;
			
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
			
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}

	public IWSConfigData getSessionMgmtConfiguration()	throws DataManagerException {
		
		IWSConfigData sessionMgmtConfigData=null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(WSConfigData.class);
            criteria.add(Restrictions.eq(CONFIG_TYPE,WebServiceConstant.SESSION_MANAGER_TYPE));
			List list  = criteria.list();
			if(list!=null && !list.isEmpty()) {
				sessionMgmtConfigData = (IWSConfigData) list.get(0);
			}

			
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
			
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return sessionMgmtConfigData;

	}

	public void updateSessionMgmtConfiguration(IWSConfigData sessionMgmtConfigData,IStaffData staffData) throws DataManagerException {
		
	Session session = getSession();
		

		if(sessionMgmtConfigData != null){
			try {
				Criteria criteria = session.createCriteria(WSConfigData.class);
				criteria.add(Restrictions.eq(CONFIG_TYPE,WebServiceConstant.SESSION_MANAGER_TYPE));
				WSConfigData data = (WSConfigData) criteria.add(Restrictions.eq(WSCONFIG_ID, sessionMgmtConfigData.getWsconfigId())).uniqueResult();

				JSONArray jsonArray=ObjectDiffer.diff(data,(WSConfigData)sessionMgmtConfigData);
				
				data.setDatabasedsId(sessionMgmtConfigData.getDatabasedsId());
				//data.setUserIdentityFieldName(sessionMgmtConfigData.getUserIdentityFieldName());
				data.setTableName(sessionMgmtConfigData.getTableName());
				data.setRecordFetchLimit(sessionMgmtConfigData.getRecordFetchLimit());
				
				session.update(data);
				session.flush();
				
				if(WebServiceConstant.SESSION_MANAGER_TYPE.equalsIgnoreCase(data.getConfigType())){
					
					List<IWSDBFieldMapData> oldFieldMapList = new ArrayList<IWSDBFieldMapData>(data.getWsDBFieldMapSet());
					
					if(Collectionz.isNullOrEmpty(oldFieldMapList) == false) {
						deleteObjectList(oldFieldMapList, session);
					}
					
					List<IWSDBFieldMapData> newFieldMapList = new ArrayList<IWSDBFieldMapData>(sessionMgmtConfigData.getWsDBFieldMapSet());
					int orderNumber = 1;
					if(Collectionz.isNullOrEmpty(newFieldMapList) == false){
						int size = newFieldMapList.size();
						for (int i = 0; i < size; i++) {
							IWSDBFieldMapData newData =  newFieldMapList.get(i);
							newData.setWsConfigId(sessionMgmtConfigData.getWsconfigId());
							newData.setOrderNumber(orderNumber);
							session.save(newData);
							orderNumber++;
						}
					}
					
					List<IWSAttrFieldMapData> oldAttrFieldMapList= new ArrayList<IWSAttrFieldMapData>(data.getWsAttrFieldMapSet());
					
					if(oldAttrFieldMapList != null && !oldAttrFieldMapList.isEmpty()){
						
						deleteObjectList(oldAttrFieldMapList, session);
					}
					
					List<IWSAttrFieldMapData> newAttrFieldMapList = new ArrayList<IWSAttrFieldMapData>(sessionMgmtConfigData.getWsAttrFieldMapSet());
					orderNumber = 1;
					if(newAttrFieldMapList!=null && !newAttrFieldMapList.isEmpty()){
						for (int i = 0; i < newAttrFieldMapList.size(); i++) {
							IWSAttrFieldMapData newData =  newAttrFieldMapList.get(i);
							newData.setWsConfigId(sessionMgmtConfigData.getWsconfigId());
							newData.setOrderNumber(orderNumber);
							session.save(newData);
							orderNumber++;
						}
					}	
				}
				
				staffData.setAuditId(getAuditId(ConfigConstant.SESSION_MANAGEMENT_WEB_SERVICE_CONFIG));
				staffData.setAuditName(ConfigConstant.HYPHEN);
				
				doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_SESSION_MANAGEMENT_WSCONFIG);
			} catch (HibernateException hExp) {
				hExp.printStackTrace();
				throw new DataManagerException(hExp.getMessage(),hExp);
			} catch (Exception exp){
				exp.printStackTrace();
				throw new DataManagerException(exp.getMessage(),exp);
			}
		}else{
			throw new DataManagerException();
		}		
	}	
}
