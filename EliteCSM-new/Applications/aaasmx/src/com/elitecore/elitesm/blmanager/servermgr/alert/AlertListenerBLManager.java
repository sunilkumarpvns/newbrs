package com.elitecore.elitesm.blmanager.servermgr.alert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.alert.AlertListenerDataManager;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerRelData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerTypeData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertTypeData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.SYSLogNameValuePoolData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;


public class AlertListenerBLManager extends BaseBLManager {

	private AlertListenerDataManager getAlertListenerDataManager(IDataManagerSession  session) {
		AlertListenerDataManager alertListenerDataManager = (AlertListenerDataManager)DataManagerFactory.getInstance().getDataManager(AlertListenerDataManager.class, session);
		return alertListenerDataManager;
	}
	
	public void create(AlertListenerData alertListenerData, IStaffData staffData) throws DataManagerException {
		List<AlertListenerData> alertListenerDataList = new ArrayList<AlertListenerData>();
		alertListenerDataList.add(alertListenerData);
		create(alertListenerDataList, staffData, "false");
		
	}

	public List<AlertListenerTypeData> getAvailableListenerType() throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);
		List<AlertListenerTypeData> alertTypeList= null;
		
		if (alertListenerDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for "+ getClass().getName());
		}
		
		try {
			alertTypeList = alertListenerDataManager.getAvailableListenerType();
		} catch(DataManagerException dme) {
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
		return alertTypeList;
	}

	public List<AlertTypeData> getAlertTypeData() throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);
		List<AlertTypeData> alertTypeDataList= null;
		
		if (alertListenerDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for "+ getClass().getName());
		}
		
		try {
			alertTypeDataList = alertListenerDataManager.getAlertTypeData();
		} catch(DataManagerException dme) {
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
		return alertTypeDataList;
	}
	
	public PageList search(AlertListenerData alertListenerData,int requiredPageNo, Integer pageSize, IStaffData staffData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);

		PageList lstDatabaseDsList;
		
		if (alertListenerDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for "+ getClass().getName());
		}
		
		try {

			session.beginTransaction();	
			lstDatabaseDsList = alertListenerDataManager.search(alertListenerData, requiredPageNo, pageSize);
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_ALERT_LISTENER);
			commit(session);
		} catch(DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch(Exception exp) {
			rollbackSession(session);
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
		
		return lstDatabaseDsList;
	}

	public void deleteById(List<String> alertListenerIds, IStaffData staffData) throws DataManagerException {
		delete(alertListenerIds, staffData, BY_ID);
	}

	public AlertListenerData getAlertListenerDataById(String listenerId) throws DataManagerException {
		return getAlertListenerData(listenerId, BY_ID);
	}
	
	public AlertListenerData getAlertListenerDataByName(String alertConfigurationName) throws DataManagerException {
		return getAlertListenerData(alertConfigurationName.trim(), BY_NAME);
	}

	private AlertListenerData getAlertListenerData(Object searchVal, boolean isByIdOrName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);
		AlertListenerData alertListenerData = null;

		if (alertListenerDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {	
			
			if (isByIdOrName) {
				alertListenerData = alertListenerDataManager.getAlertListenerDataById((String) searchVal);
			} else {
				alertListenerData = alertListenerDataManager.getAlertListenerDataByName((String) searchVal);
			}

		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
		return alertListenerData;
	}
	
	public List<AlertListenerRelData> getAlertListenerRelDataList(String listenerId) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);

		List<AlertListenerRelData> alertListenerRelDataList = null;
		
		if (alertListenerDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for "+ getClass().getName());
		}

		try {
			alertListenerRelDataList = alertListenerDataManager.getAlertListenerRelDataList(listenerId);
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
		return alertListenerRelDataList;
	}
	
	public void updateById(AlertListenerData alertListenerData,IStaffData staffData) throws DataManagerException {
		updateByName(alertListenerData, staffData, null);
	}
	
	public List<AlertListenerData> getAlertListernerDataList() throws DataManagerException{
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);
		
		List<AlertListenerData> alertListenerList = null;			
		
		if (alertListenerDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for "+ getClass().getName());
		}

		try {
			alertListenerList = alertListenerDataManager.getAlertListernerDataList();
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
		return alertListenerList;
	}
	
	public List<SYSLogNameValuePoolData> getSysLogNameValuePoolList() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);
		
		if (alertListenerDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for "+ getClass().getName());
		}

		try {
			List<SYSLogNameValuePoolData> dataList = alertListenerDataManager.getSysLogNameValuePoolList();
			return dataList;
			
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
	}
	
	public AlertTypeData getAlertTypeData(String typeId) throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);
		AlertTypeData alertTypeData = new AlertTypeData();
		
		if (alertListenerDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for "+ getClass().getName());
		}

		try {
			alertTypeData = alertListenerDataManager.getAlertTypeData(typeId);
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
		return alertTypeData;
	}
	
	public ArrayList<String> getSysLogNames() throws DataManagerException {
		ArrayList<String> sysLogNames = new ArrayList<String>();
		List<SYSLogNameValuePoolData> nameValuePoolDatas = getSysLogNameValuePoolList();
		for (SYSLogNameValuePoolData logNameValuePoolData: nameValuePoolDatas) {
			sysLogNames.add(logNameValuePoolData.getName());
		}
		return sysLogNames;
	}

	public List<AlertTypeData> getAlertTypeDataList() throws DataManagerException {
		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);

		List<AlertTypeData> alertTypeDataList= null;

		if (alertListenerDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for "+ getClass().getName());
		}

		try {
			alertTypeDataList = alertListenerDataManager.getAlertTypeDataList();
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
		return alertTypeDataList;
	}

	public AlertListenerData getAlertListenerDetailDataByName(String alertConfigurationName) throws DataManagerException {

		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();

		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);
		AlertListenerData alertListenerData = null;

		if (alertListenerDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try {
			alertListenerData = getAlertListenerDataByName(alertConfigurationName);
			List<AlertListenerRelData> alertListData = getAlertListenerRelDataList(alertListenerData.getListenerId());
			alertListenerData.setAlertListenerRelDataList(alertListData);
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}

		return alertListenerData;
	}

	public void updateByName(AlertListenerData alertListenerData, IStaffData staffData, String queryOrPathParam) throws DataManagerException {

		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);

		if (alertListenerDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for "+ getClass().getName());
		}

		try {
			session.beginTransaction();
			if (queryOrPathParam == null) {
				alertListenerDataManager.updateById(alertListenerData, staffData, alertListenerData.getListenerId());
			} else {
				alertListenerDataManager.updateByName(alertListenerData, staffData, queryOrPathParam.trim());
			}
			commit(session);

		} catch(DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	
	}
	
	private void delete(List<String> alertListenerIds, IStaffData staffData, boolean byIdOrName) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		AlertListenerDataManager alertListenerDataManager = getAlertListenerDataManager(session);

		if (alertListenerDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for "+ getClass().getName());
		}

		try {

			session.beginTransaction();
			if(Collectionz.isNullOrEmpty(alertListenerIds) == false) {
				
				List<String> alertListenerList = new ArrayList<String>();
				int noOfAlertListener = alertListenerIds.size();
				
				for (int i=0; i< noOfAlertListener; i++) {
					if (Strings.isNullOrEmpty(alertListenerIds.get(i)) == false) {
						String alertListenerIdOrName = alertListenerIds.get(i).toString();
						if (byIdOrName) {
							alertListenerList.add(alertListenerIdOrName);
						} else {
							AlertListenerData alertListenerData = alertListenerDataManager.getAlertListenerDataByName(alertListenerIdOrName.trim());
							alertListenerList.add(alertListenerData.getListenerId());
						}
					}
				}
				
				if (Collectionz.isNullOrEmpty(alertListenerList) == false) {
					
					List<String> alertListenerNames = alertListenerDataManager.delete(alertListenerList);
					if(Collectionz.isNullOrEmpty(alertListenerNames) == false){
						
						for(String strDriverName : alertListenerNames){
							staffData.setAuditName(strDriverName);
							AuditUtility.doAuditing(session,staffData, ConfigConstant.DELETE_ALERT_LISTENER);
						}
					}
				}
			}
			
			commit(session);
		} catch(DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}
	
	public void deleteByName(List<String> alertListenerNameList, StaffData staffData) throws DataManagerException {
		delete(alertListenerNameList, staffData, BY_NAME);
	}

	public Map<String, List<Status>> create(List<AlertListenerData> alertListnerDataList, IStaffData staffData, String partialSuccess) throws DataManagerException {
		return insertRecords(AlertListenerDataManager.class, alertListnerDataList, staffData, ConfigConstant.CREATE_ALERT_LISTENER, partialSuccess);
	}
}
