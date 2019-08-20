package com.elitecore.elitesm.datamanager.digestconf;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData;

public interface DigestConfDataManager extends DataManager {

	@Override
	public String create(Object object) throws DataManagerException;
    
	public List<DigestConfigInstanceData> getDigestConfigInstanceList() throws DataManagerException;

	public PageList search(DigestConfigInstanceData digestConfigInstanceData,int requiredPageNo, Integer pageSize) throws DataManagerException;

	public String deleteById(String digestConfId) throws DataManagerException;

	public String deleteByName(String digestConfigurationName) throws DataManagerException;
	
	public void updateById(DigestConfigInstanceData digestConfigInstanceData,IStaffData staffData, String digestConfigId)throws DataManagerException;
	
	public void updateByName(DigestConfigInstanceData digestConfigInstanceData,IStaffData staffData, String byName)throws DataManagerException;
	
	public String getDigestConfigInstDataNameFormId(String digestConfId) throws DataManagerException;
	
	public DigestConfigInstanceData getDigestConfigInstDataByName(String digestCofuguration) throws DataManagerException;
	
	public DigestConfigInstanceData getDigestConfigInstDataById(String digestConfId) throws DataManagerException;
	
}

