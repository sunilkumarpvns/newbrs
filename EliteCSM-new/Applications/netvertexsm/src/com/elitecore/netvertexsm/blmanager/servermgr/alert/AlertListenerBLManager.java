package com.elitecore.netvertexsm.blmanager.servermgr.alert;

import java.util.List;

import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.DuplicateEntityFoundException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.AlertListenerDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerRelData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertTypeData;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.netvertexsm.datamanager.wsconfig.data.WSConfigData;


public class AlertListenerBLManager extends com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager {
	private final static String MODULE="ALERT_LISTENER_BL_MANAGER";

	private AlertListenerDataManager getAlertListenerDataManager(IDataManagerSession  session){
		AlertListenerDataManager alertListenerDataManager = (AlertListenerDataManager)DataManagerFactory.getInstance().getDataManager(AlertListenerDataManager.class, session);
		return alertListenerDataManager;
	}
	
	public void create(AlertListenerData alertListenerForm,IStaffData staffData, String actionAlias) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		if(systemAuditDataManager==null){
			throw new DataManagerException("Data Manager Not Found: SystemAuditDataManager.");
		}

		if(alertListenerDataManager==null){
			throw new DataManagerException("Data Manager Not Found: AlertListenerDataManager.");
		}
		
		try{
			session.beginTransaction();
			alertListenerDataManager.create(alertListenerForm);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();
		}catch(DuplicateEntityFoundException e){
			session.rollback();
			throw new DuplicateEntityFoundException("Action failed :"+e.getMessage());	
		}catch(DataManagerException e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}

	}

	public List<AlertListenerTypeData> getAvailableListenerType() throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		List<AlertListenerTypeData> alertTypeList= null;
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);
		if(alertListenerDataManager==null){
			throw new DataManagerException("Data Manager Not Found: AlertListenerDataManager.");
		}
		try{
			alertTypeList = alertListenerDataManager.getAvailableListenerType();
		}catch(DataManagerException e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
		return alertTypeList;
	}

	public List<AlertTypeData> getAlertTypeData() throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		List<AlertTypeData> alertTypeDataList= null;
		
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);
		if(alertListenerDataManager==null){
			throw new DataManagerException("Data Manager Not Found: AlertListenerDataManager.");
		}
		
		try{
			alertTypeDataList = alertListenerDataManager.getAlertTypeData();
		}catch(DataManagerException e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
		return alertTypeDataList;
	}
	
	public List<AlertTypeData> getEnabledAlertTypeData(String[] enabledAlertsArray) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		List<AlertTypeData> alertTypeDataList= null;
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);
		if(alertListenerDataManager==null){
			throw new DataManagerException("Data Manager Not Found: AlertListenerDataManager.");
		}

		try{
			alertTypeDataList = alertListenerDataManager.getEnabledAlertTypeData(enabledAlertsArray);
		}catch(DataManagerException e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
		return alertTypeDataList;
	}

	public List<AlertListenerData> getAlertListenerList(String name,String typeId) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		List<AlertListenerData> alertListenerList = null;			
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);
		if(alertListenerDataManager==null){
			throw new DataManagerException("Data Manager Not Found: AlertListenerDataManager.");
		}
		
		try{
			alertListenerList = alertListenerDataManager.getAlertListenerList(name,typeId);
		}catch(DataManagerException e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
		return alertListenerList;
	}
	public void delete(List alertListenerIds) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);
		if(alertListenerDataManager==null){
			throw new DataManagerException("Data Manager Not Found: AlertListenerDataManager.");
		}
		try{			
			session.beginTransaction();
			alertListenerDataManager.delete(alertListenerIds);
			session.commit();
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action Failed .",e);
		}finally{
			session.close();
		}

	}



	public PageList search(AlertListenerData alertListenerData,int requiredPageNo, Integer pageSize) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();

		PageList lstDatabaseDsList;
		
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);
		if(alertListenerDataManager==null){
			throw new DataManagerException("Data Manager Not Found: AlertListenerDataManager.");
		}
		
		try{
			lstDatabaseDsList = alertListenerDataManager.search(alertListenerData, requiredPageNo, pageSize);
		}
		catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}

		return lstDatabaseDsList;
	}


	public void delete(List<String> alertListenerIds, IStaffData staffData,String actionAlias) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();

		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		if(alertListenerDataManager==null){
			throw new DataManagerException("Data Manager Not Found: AlertListenerDataManager.");
		}
		if(systemAuditDataManager==null){
			throw new DataManagerException("Data Manager Not Found: SystemAuditDataManager.");
		}
		
		try{

			session.beginTransaction();
			if(alertListenerIds != null){
				for(int i=0;i<alertListenerIds.size();i++){
					if(alertListenerIds.get(i) != null){
						String transactionId = alertListenerIds.get(i).toString();
						long alertlistenerId = Long.parseLong(transactionId);
						alertListenerDataManager.delete(alertlistenerId);				    	

					}
				}
				systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			}else{
				throw new DataManagerException("Data Manager implementation not found for ");    		
			}
			session.commit();
		}catch(ConstraintViolationException exp){
			session.rollback();
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
		}catch(Exception exp){
			session.rollback();
			throw new DataManagerException("Action failed :"+exp.getMessage(),exp);
		}finally{
			session.close();
		}
	}

	public AlertListenerData getAlertListenerData(AlertListenerData alertListenerData, IStaffData staffData,String actionAlias) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);
		if(alertListenerDataManager==null){
			throw new DataManagerException("Data Manager Not Found: AlertListenerDataManager.");
		}
		
		try{
			alertListenerData = alertListenerDataManager.getAlertListener(alertListenerData.getListenerId());
		}catch(DataManagerException e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
		return alertListenerData;
	}
	
	public List<AlertListenerRelData> getAlertListenerRelDataList(long listenerId, IStaffData staffData,String actionAlias) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		List<AlertListenerRelData> alertListenerRelDataList;
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);
		if(alertListenerDataManager==null){
			throw new DataManagerException("Data Manager Not Found: AlertListenerDataManager.");
		}
		
		try{
			alertListenerRelDataList = alertListenerDataManager.getAlertListenerRelDataList(listenerId);
		}catch(DataManagerException e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
		return alertListenerRelDataList;
	}
	
	public void updateAlertListener(AlertListenerData alertListenerData,IStaffData staffData, String actionAlias) throws DataManagerException {

		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		if(alertListenerDataManager == null){
			throw new DataManagerException("Data Manager Not Found: AlertListenerDataManager.");
		}
		if(systemAuditDataManager == null){
			throw new DataManagerException("Data Manager Not Found: SystemAuditDataManager.");
		}
	 
		try{
			session.beginTransaction();
			alertListenerDataManager.updateAlertListener(alertListenerData);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();

		}catch(DuplicateEntityFoundException e){
			session.rollback();
			throw new DuplicateEntityFoundException("Action failed :"+e.getMessage());	
		}catch(DataManagerException e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}

	}
	public List<AlertListenerData> getAlertListernerDataList() throws DataManagerException{
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		List<AlertListenerData> alertListenerList = null;			
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);
		if(alertListenerDataManager==null){
			throw new DataManagerException("Data Manager Not Found: AlertListenerDataManager.");
		}
		
		try{
			alertListenerList = alertListenerDataManager.getAlertListernerDataList();
		}catch(DataManagerException e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
		return alertListenerList;
	}

}