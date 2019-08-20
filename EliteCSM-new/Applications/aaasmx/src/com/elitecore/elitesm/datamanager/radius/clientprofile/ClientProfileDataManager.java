package com.elitecore.elitesm.datamanager.radius.clientprofile;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.ClientTypeData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.VendorData;


public interface ClientProfileDataManager extends DataManager{	
    
	@Override
	public String create(Object object) throws DataManagerException;
	
	public List<VendorData> getVendorList() throws DataManagerException;
	
	public VendorData getVendorData(String vendorId) throws DataManagerException;
    
	public List<ClientTypeData> getClientTypeList() throws DataManagerException;

	public VendorData getVendorData(Long vendorInstanceId) throws DataManagerException;

	public ClientTypeData getClientTypeData(Long clientTypeId) throws DataManagerException;

	public PageList search(RadiusClientProfileData radiusClientProfileData,int requiredPageNo, Integer pageSize) throws DataManagerException;
	
	public List<RadiusClientProfileData> getRadiusClientProfileList() throws DataManagerException;

	public String deleteById(String clientProfileId) throws DataManagerException;
	
	public String deleteByName(String clientProfileName) throws DataManagerException;

	public void updateBasicDetails(RadiusClientProfileData radiusClientProfileData,IStaffData staffData,String actionAlias) throws DataManagerException;
	
	public void update(RadiusClientProfileData radiusClientProfileData,IStaffData staffData,String actionAlias,String byName) throws DataManagerException;

	public void updateAdvanceDetails(RadiusClientProfileData radiusClientProfileData,IStaffData staffData,String actionAlias) throws DataManagerException;

	public void createVendor(VendorData vendorData) throws DataManagerException;

	public RadiusClientProfileData getClientProfileDataById(String clientProfileId)throws DataManagerException;
	
	public RadiusClientProfileData getClientProfileByName(String clientprofileName)throws DataManagerException;
	
	public long getClientTypeIdFromName(String clientType) throws DataManagerException;
	
	public String getVendorIdFromName(String vendorName) throws DataManagerException;
}
