package com.elitecore.elitesm.datamanager.core.system.profilemanagement;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ActionData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.BISModelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IActionData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModuleData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModuleTypeData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IConfigurationProfileData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ISubBISModuleData;

public interface ProfileDataManager extends DataManager{
	public List getBusinessModelList() throws DataManagerException;
	public List getBusinessModuleList() throws DataManagerException;
	public List getSubBusinessModuleList() throws DataManagerException;
	public List getBISModelModuleRelList(String BISModelId) throws DataManagerException;
	public List getBISModuleSubBISModuleRelList(String BISModuleId) throws DataManagerException;
	public List getSubBISModuleActionRelList(String subBusinessModuleId) throws DataManagerException;
	public List getBISModelList(IBISModelData bisModelData) throws DataManagerException;
	public List getBISModuleList(IBISModuleData bisModuleData) throws DataManagerException;
	public List getSubBISModuleList(ISubBISModuleData subBISModuledata) throws DataManagerException;
	public List getBISModuleTypeList(IBISModuleTypeData bisModuleTypeData) throws DataManagerException;
	public ActionData getActionList(IActionData actionData) throws DataManagerException;
	public List getBusinessModuleTypeList() throws DataManagerException;
	public List getSubBusinessModuleTypeList() throws DataManagerException;
	public List search(String businessModelId,String businessModuleTypeId,String businessModuleName) throws DataManagerException;
	public List search(String businessModelId,String businessModuleId,String subBusinessModuleTypeId,String subBusinessModuleName) throws DataManagerException;
	public List getBusinessModelModuleRelList(String businessModuleId) throws DataManagerException;
	public List getBISModuleSubBISModuleRel(String subBusinessModuleId) throws DataManagerException;
	public List getConfigurationProfileData(String profileName) throws DataManagerException;
	public List getProfileBISModelRelList(String configurationProfileId) throws DataManagerException;
	public void updateSubBusinessModuleProfileStatus(String subBusinessModuleId,String subBusinessModuleStatus) throws DataManagerException;
	public void updateBusinessModuleProfileStatus(String businessModuleId,String businessModuleProfileStatus) throws DataManagerException;
	public void updateActionProfileStatus(String actionId,String actionProfileStatus) throws DataManagerException;
	public void updateModelProfileStatus(String businessModelId,String businessModelProfileStatus) throws DataManagerException;
	public void updateFreezeConfigurationProfile(String configurationProfileId,byte[] profileData) throws DataManagerException;
	public void updateFreezeModelProfile() throws DataManagerException;
	public void updateFreezeModuleProfile() throws DataManagerException;
	public void updateFreezeSubModuleProfile() throws DataManagerException;
	public void updateFreezeActionProfile() throws DataManagerException;
	public IConfigurationProfileData getConfigurationProfile(String profileAlias) throws DataManagerException;
	public void updateModelStatus(String modelAlias,String modelStatus) throws DataManagerException;
	public void updateModuleStatus(String moduleAlias,String moduleStatus) throws DataManagerException;
	public void updateSubModuleStatus(String subModuleAlias,String subModuleStatus) throws DataManagerException;
	public void updateActionStatus(String actionAlias,String actionStatus) throws DataManagerException;
	public void updateConfigurationProfile(IConfigurationProfileData configurationProfileData) throws DataManagerException;
	public void updateLastProfile(String currentProfile,String lastProfileAlias) throws DataManagerException;
	public List getConfigurationProfileList() throws DataManagerException;
	public List getConfigurationProfileName(String configurationProfileId) throws DataManagerException;
	public void updateParameter(String currentProfile,String lastProfileAlias) throws DataManagerException;
	public List getProfileSpecificList(String configurationProfileId, String mode) throws DataManagerException;
	public void executeProfileScripts(String configurationProfileId, String mode) throws DataManagerException;
	
	public List getActionSubBISModuleRelList(String actionId) throws DataManagerException;
	public List getSubBISModuleBISModuleRelList(String subBusinessModuleId) throws DataManagerException;
	public List getBISModuleModelRelList(String businessModuleId) throws DataManagerException;
	public List getBISModelModuleRelList() throws DataManagerException;
	public List getSubBISModuleAlias(ISubBISModuleData subBISModuledata) throws DataManagerException;
	public List getBISModuleAlias(IBISModuleData bisModuleData) throws DataManagerException;
	public List getBISModelAlias(IBISModelData bisModelData) throws DataManagerException;
	public List getActionAlias(IActionData actionData) throws DataManagerException;
	
	
	public List getActionList() throws DataManagerException;
	public List getActionList(String[] actionIds) throws DataManagerException;
	public BISModelData getBISModel(String buisenessModelId)throws DataManagerException;
}
