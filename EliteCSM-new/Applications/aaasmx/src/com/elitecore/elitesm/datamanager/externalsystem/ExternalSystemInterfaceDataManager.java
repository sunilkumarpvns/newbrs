package com.elitecore.elitesm.datamanager.externalsystem;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.externalsystem.data.ESITypeAndInstanceData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceTypeData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;

public interface ExternalSystemInterfaceDataManager extends DataManager{
	
	public List<ExternalSystemInterfaceTypeData> getListOfESITypes() throws DataManagerException;
	
	@Override
	public String create(Object object) throws DataManagerException;
	public List getESIInstanceAndType(String name , long esiTypeId) throws DataManagerException;
	public String deleteByInstaceId(String esiInstanceId) throws DataManagerException;
	public ESITypeAndInstanceData getESIInstanceDetail(String esiInstanceId) throws DataManagerException;
	public void updateByInstanceId(ExternalSystemInterfaceInstanceData esiInstanceData,IStaffData staffData, String esiInstanceId) throws DataManagerException;
	
	public void updateByInstanceName(ExternalSystemInterfaceInstanceData esiInstanceData,IStaffData staffData,String esiName) throws DataManagerException;
	public List<ExternalSystemInterfaceInstanceData> getExternalSystemInstanceDataList(long externalSystemTypeId)  throws DataManagerException;
	public PageList search(ESITypeAndInstanceData esiInstanceData, int requiredPageNo,int pageSize) throws DataManagerException;
	public List<ExternalSystemInterfaceInstanceData> getExternalSystemInterfaceInstanceDataList()throws DataManagerException;
	public List<ExternalSystemInterfaceInstanceData> getAcctFlowExternalSystemInstanceDataList(long authProxy)throws DataManagerException;
	public List<ExternalSystemInterfaceInstanceData> getRadiusTypeExternalInterfaceInstanceDataList()throws DataManagerException;

	public ExternalSystemInterfaceInstanceData getExternalSystemInterfaceInstanceDataById(String esiInstanceId)throws DataManagerException ;
	public ExternalSystemInterfaceInstanceData getExternalSystemInterfaceInstanceDataByName(String esiInstanceName) throws DataManagerException;
	public String deleteByInstaceName(String esiInstanceName) throws DataManagerException;
	public String getRadiusESIGroupNameById(String esiId) throws DataManagerException;

	public String getESIInstanceIdByESITypeId(Long primaryESIId)throws DataManagerException;
	public Long getESITypeIdFromName(String esiName)throws DataManagerException;
	public String getESITypeNameFromId(Long esiInstanceId)throws DataManagerException;
	public String getRadiusESIIdByName(String radiusESIName) throws DataManagerException;
	public ExternalSystemInterfaceInstanceData getAcctExternalSystemInterfaceInstanceData(String esiName) throws DataManagerException;
}
