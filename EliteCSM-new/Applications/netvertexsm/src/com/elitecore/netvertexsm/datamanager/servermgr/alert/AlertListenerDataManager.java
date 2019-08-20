package com.elitecore.netvertexsm.datamanager.servermgr.alert;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.DuplicateEntityFoundException;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerRelData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertTypeData;


public interface AlertListenerDataManager extends DataManager {

	public void create(AlertListenerData alertListenerForm) throws DataManagerException,DuplicateEntityFoundException;
	public void delete(List alertListenerIds) throws DataManagerException;
	public List<AlertListenerTypeData> getAvailableListenerType()throws DataManagerException;
	public List<AlertTypeData> getAlertTypeData()throws DataManagerException;
	public List<AlertListenerData> getAlertListenerList(String name,String typeId) throws DataManagerException;
	public PageList search(AlertListenerData alertListenerData,int requiredPageNo, Integer pageSize) throws DataManagerException;
	public void delete(long alertListenerId) throws DataManagerException;
	public AlertListenerData getAlertListener(long listenerId)throws DataManagerException;
	public List<AlertListenerRelData> getAlertListenerRelDataList(long listenerId)throws DataManagerException;
	public void updateAlertListener(AlertListenerData alertListenerData) throws DataManagerException,DuplicateEntityFoundException;
	public List<AlertTypeData> getEnabledAlertTypeData(String[] enabledAlertsArray) throws DataManagerException;
	public List<AlertListenerData> getAlertListernerDataList() throws DataManagerException;
}
