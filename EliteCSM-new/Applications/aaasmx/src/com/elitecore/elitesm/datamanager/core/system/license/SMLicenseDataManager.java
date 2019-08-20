package com.elitecore.elitesm.datamanager.core.system.license;

import java.util.List;
import java.util.Map;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.license.data.SMLicenseData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;

public interface SMLicenseDataManager extends DataManager {

	public void updateLicenseStatus(String instanceName, String status, IStaffData staffData) throws DataManagerException;

	public void insertRecord(SMLicenseData licenseData, IStaffData staffData) throws DataManagerException;

	public List<SMLicenseData> fetchRecords(Map<String, Object> propertyNameValues) throws DataManagerException;

	public void doAudit(String jsonString , IStaffData staffData) throws DataManagerException;
	
	public String getSystemAuditId(String moduleName) throws DataManagerException;
}
