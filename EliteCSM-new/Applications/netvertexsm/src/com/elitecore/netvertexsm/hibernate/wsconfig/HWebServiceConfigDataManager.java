/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   HWebServiceConfigDataManager.java                 		
 * ModualName wsconfig    			      		
 * Created on 23 December, 2010
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.netvertexsm.hibernate.wsconfig;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.wsconfig.WebServiceConfigDataManager;
import com.elitecore.netvertexsm.datamanager.wsconfig.data.WSConfigData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;

public class HWebServiceConfigDataManager extends HBaseDataManager implements WebServiceConfigDataManager{

	private static final String MODULE="HWebServiceConfigDataManager";

	public void updateSubscriberConfiguration(WSConfigData subscriberConfigData) throws DataManagerException {
		Session session = getSession();

		if(subscriberConfigData != null){
			try {
				Criteria criteria = session.createCriteria(WSConfigData.class);
				//criteria.add(Restrictions.eq("configType",WebServiceConstant.SUBSCRIBER_PROFILE_TYPE));
				WSConfigData data = (WSConfigData)criteria.add(Restrictions.eq("wsconfigId",subscriberConfigData.getWsconfigId())).uniqueResult();
				if(data!=null){
					data.setDatabasedsId(subscriberConfigData.getDatabasedsId());
					data.setUserIdentityFieldName(subscriberConfigData.getUserIdentityFieldName());
					data.setTableName(subscriberConfigData.getTableName());
					data.setRecordFetchLimit(subscriberConfigData.getRecordFetchLimit());
					data.setPrimaryKeyColumn(subscriberConfigData.getPrimaryKeyColumn());
					data.setSequenceName(subscriberConfigData.getSequenceName());
					data.setBodCDRDriverId(subscriberConfigData.getBodCDRDriverId());
					data.setDynaSprDatabaseId(subscriberConfigData.getDynaSprDatabaseId());
			    	data.setSubscriberIdentity(subscriberConfigData.getSubscriberIdentity());
			    	data.setUsageMonitoringDatabaseId(subscriberConfigData.getUsageMonitoringDatabaseId());
					session.update(data);
				}else{
					session.save(subscriberConfigData);	
				}
				session.flush();

				
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

	public WSConfigData getSubscriberConfiguration() throws DataManagerException {
		WSConfigData subscriberConfigData=null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(WSConfigData.class);
			//criteria.add(Restrictions.eq("configType",WebServiceConstant.SUBSCRIBER_PROFILE_TYPE));
			List list  = criteria.list();
			if(list!=null && !list.isEmpty()) {
				subscriberConfigData = (WSConfigData) list.get(0);
			}


		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);

		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return subscriberConfigData;
	}


	public WSConfigData getSessionMgmtConfiguration()	throws DataManagerException {

		WSConfigData sessionMgmtConfigData=null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(WSConfigData.class);
			//criteria.add(Restrictions.eq("configType",WebServiceConstant.SESSION_MANAGER_TYPE));
			List list  = criteria.list();
			if(list!=null && !list.isEmpty()) {
				sessionMgmtConfigData = (WSConfigData) list.get(0);
			}


		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);

		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return sessionMgmtConfigData;

	}


}
