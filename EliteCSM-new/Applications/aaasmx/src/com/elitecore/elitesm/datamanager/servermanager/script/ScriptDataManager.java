package com.elitecore.elitesm.datamanager.servermanager.script;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptTypeData;

/**
 * @author Tejas.P.shah
 *
 */

public interface ScriptDataManager extends DataManager{
	
	@Override
	public String create(Object object) throws DataManagerException;
	public List<ScriptInstanceData> getListOfScriptInstData(List<String> scriptInstanceIdList) throws DataManagerException;
	public PageList search(ScriptInstanceData scriptInstanceData,int requiredPageNo, Integer pageSize) throws DataManagerException;
	public List<ScriptTypeData> getListOfAllScriptTypesData() throws DataManagerException;
	public ScriptTypeData getScriptTypeDataById(String scriptType) throws DataManagerException;
	public ScriptInstanceData getScriptInstanceByScriptId(String scriptInstaceId, boolean isIdOrName) throws DataManagerException;
	public List<ScriptData> getScriptDataByScriptInstanceId(String scriptId) throws DataManagerException;
	public void updateScriptByName(ScriptInstanceData scriptInstanceData, IStaffData staffData, String name) throws DataManagerException;
	public void updateScriptById(ScriptInstanceData scriptInstanceData, IStaffData staffData) throws DataManagerException;
	public ScriptData getScriptFileByName(String scriptFileName, String scriptId)throws DataManagerException;
	public void updateScriptFile(ScriptData scriptDataNewObject, IStaffData staffData, ScriptInstanceData scriptInstanceData) throws DataManagerException;
	public List<String> delete(List<String> scriptList) throws DataManagerException;
	public void updateStatus(List<String> scriptInstanceIds, String status) throws DataManagerException;
	public List<ScriptInstanceData> getScriptInstanceDataByTypeId(String scriptTypeId) throws DataManagerException;
	public void updateScriptBasicDetails(ScriptInstanceData scriptInstanceData, IStaffData staffData, String name) throws DataManagerException;
	public String deleteByName(String strIdOrName) throws DataManagerException;
}
