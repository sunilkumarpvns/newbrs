package com.elitecore.elitesm.datamanager.sessionmanager;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISMConfigInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SMSessionCloserESIRelData;

public interface SessionManagerDataManager extends DataManager {

	@Override
	public String create(Object object) throws DataManagerException;
	
	public PageList search(ISessionManagerInstanceData sessionManagerInstance,int requiredPageNo, Integer pageSize) throws DataManagerException;

	public List<ISessionManagerInstanceData> getSessionManagerInstanceList() throws DataManagerException;
	
	public List<ISessionManagerInstanceData> getSessionManagerInstanceList(	String smtype) throws DataManagerException;

	public ISMConfigInstanceData getSMConfigInstanceData(String smConfigId) throws DataManagerException;
	
	public ISMConfigInstanceData getSMConfigInstanceData(ISessionManagerInstanceData sessionManagerInstanceData) throws DataManagerException;

	public void updateSessionManagerBasicDetails(ISessionManagerInstanceData sessionManagerInstanceData,IStaffData staffData,String actioAlias) throws DataManagerException;

	public void updateSessionManagerDetails(ISessionManagerInstanceData sessionManagerInstanceData,IStaffData staffData,String actionAlias) throws DataManagerException;

	public List<SMSessionCloserESIRelData> getNASSessionCloserESIRelList(String smConfigId) throws DataManagerException;

	public List<SMSessionCloserESIRelData> getAcctSessionCloserESIRelList(String smConfigId) throws DataManagerException;

	public ISessionManagerInstanceData getSessionManagerInstanceDataById(String sessionManagerDataByIdOrName) throws DataManagerException;

	public ISessionManagerInstanceData getSessionManagerInstanceDataByName(String sessionManagerDataByIdOrName) throws DataManagerException;

	public void updateSessionManagerData (ISessionManagerInstanceData sessionManagerInstanceData,StaffData staffData, String byName) throws DataManagerException;

	public String deleteById(String parseLong) throws DataManagerException;

	public String deleteByName(String idOrName) throws DataManagerException;
}

