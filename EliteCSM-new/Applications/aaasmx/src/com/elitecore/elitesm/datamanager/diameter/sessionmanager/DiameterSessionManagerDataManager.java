package com.elitecore.elitesm.datamanager.diameter.sessionmanager;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerMappingData;
import com.elitecore.elitesm.sqlexception.EliteSQLGrammerException;

public interface DiameterSessionManagerDataManager extends DataManager{
	
	public PageList search(DiameterSessionManagerData diameterSessionManagerData,int requiredPageNo, Integer pageSize) throws DataManagerException;

	@Override
	public String create(Object object) throws DataManagerException;
	
	public DiameterSessionManagerData getDiameterSessionManagerDataById(String sessionManagerId)throws DataManagerException;
	
	public DiameterSessionManagerData getDiameterSessionManagerDataByName(String sessionManagerName)throws DataManagerException;

	public List<DiameterSessionManagerData> getDiameterSessionManagerDatas()throws DataManagerException;

	public DiameterSessionManagerMappingData getDiameterSessionManagerMappingData(String mappingId)throws DataManagerException;

	public void update(DiameterSessionManagerData diameterSessionManagerData,IStaffData staffData, String actionAlias)throws DataManagerException;

	public List<DiameterSessionManagerMappingData> getDiameterSessionManagerMappingDataList(String sessionManagerId)throws DataManagerException;

	public void updateScenarioData(DiameterSessionManagerData diameterSessionManagerData,IStaffData staffData, String actionAlias)throws DataManagerException;

	public PageList getASMDataByColumnName(String searchColumnList,String tablename ,int requiredPageNumber, Integer pageSize, IStaffData staffData, String actionAlias)throws DataManagerException,EliteSQLGrammerException;

	public void closeSelectedSession(List<String> asList, String tableName)throws DataManagerException;

	public List getASMDataByColumnName(String activeSessionId, String tableName)throws DataManagerException;

	public void resetViewableColumnsValue(String searchColumnName, String sessionManagerId)throws DataManagerException;

	public String deleteById(String parseLong) throws DataManagerException;

	public String deleteByName(String idOrName) throws DataManagerException;

	public void updateDiameterSessionManagerData(DiameterSessionManagerData diameterSessionManagerData,StaffData staffData, String name) throws DataManagerException;
	
}
