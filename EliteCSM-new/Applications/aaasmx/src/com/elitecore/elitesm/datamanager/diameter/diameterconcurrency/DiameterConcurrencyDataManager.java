package com.elitecore.elitesm.datamanager.diameter.diameterconcurrency;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;

public interface DiameterConcurrencyDataManager extends DataManager {
    
    public PageList search(DiameterConcurrencyData diameterConcurrencyData, int pageNo, int pageSize) throws DataManagerException;
    @Override
    public String create(Object object) throws DataManagerException;
    public boolean verifyDiameterConcurrencyName(String diameterPolicyId, String policyName) throws DataManagerException;
	public void update(DiameterConcurrencyData diameterConcurrencyData,IStaffData staffData, String actionAlias) throws DataManagerException;
	public List<DiameterConcurrencyData> getDiameterConcurrencyDataList()throws DataManagerException;
	public DiameterConcurrencyData getDiameterConcurrencyDataById(String diameterConcurrencyId) throws DataManagerException;
	public DiameterConcurrencyData getDiameterConcurrencyDataByName(String diameterConcurrencyName) throws DataManagerException;
	public void updateById(DiameterConcurrencyData diameterConcurrencyData,IStaffData staffData, String diaConConfigId) throws DataManagerException;
	public void updateByName(DiameterConcurrencyData diameterConcurrencyData,IStaffData staffData, String queryOrPathParam) throws DataManagerException;
	public String deleteById(String parseLong) throws DataManagerException;
	public String deleteByName(String idOrName)throws DataManagerException;
}
