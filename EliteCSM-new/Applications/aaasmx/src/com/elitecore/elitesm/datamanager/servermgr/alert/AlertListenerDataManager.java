package com.elitecore.elitesm.datamanager.servermgr.alert;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerRelData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerTypeData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertTypeData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.SYSLogNameValuePoolData;

public interface AlertListenerDataManager extends DataManager {

	@Override
	public String create(Object object) throws DataManagerException;
	public List<AlertListenerTypeData> getAvailableListenerType()throws DataManagerException;
	public List<AlertTypeData> getAlertTypeData()throws DataManagerException;
	public PageList search(AlertListenerData alertListenerData,int requiredPageNo, Integer pageSize) throws DataManagerException;
	public List<AlertListenerRelData> getAlertListenerRelDataList(String listenerId)throws DataManagerException;
	public List<AlertListenerData> getAlertListernerDataList() throws DataManagerException;
	public List<SYSLogNameValuePoolData> getSysLogNameValuePoolList() throws DataManagerException;
	public AlertTypeData getAlertTypeData(String typeId) throws DataManagerException;
	public List<AlertTypeData> getAlertTypeDataList() throws DataManagerException;
	public void updateById(AlertListenerData alertListenerData, IStaffData staffData, String listenerId) throws DataManagerException;
	public void updateByName(AlertListenerData alertListenerData, IStaffData staffData, String trim) throws DataManagerException;
	public List<String> delete(List<String> alertListnerIdList) throws DataManagerException;
	public AlertListenerData getAlertListenerDataById(String searchVal) throws DataManagerException;
	public AlertListenerData getAlertListenerDataByName(String searchVal) throws DataManagerException;
}
