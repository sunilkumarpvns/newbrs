package com.elitecore.elitesm.blmanager.core.system.license;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.license.SMLicenseDataManager;
import com.elitecore.elitesm.datamanager.core.system.license.data.SMLicenseData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerStaffRelDetailData;
import com.elitecore.license.nfv.DaoException;
import com.elitecore.license.nfv.LicenseDAO;
import com.elitecore.license.nfv.LicenseDaoData;
import com.elitecore.license.util.AES;

public class SMLicenseBLManager extends BaseBLManager implements LicenseDAO {

	private final String INSTANCE_NAME = "instanceName";
	private final String LICENSE_STATUS = "status";
	private final String ID = "id";
	private final String INSTANCENAME = "instanceName";
	private SMLicenseDataManager getSMLicenseDataManager(IDataManagerSession session) {
		SMLicenseDataManager  dataManager = (SMLicenseDataManager) DataManagerFactory.getInstance().getDataManager(SMLicenseDataManager.class, session);
		return dataManager;
	}

	private SMLicenseData from(LicenseDaoData licenseDaoData) {
		SMLicenseData smLicenseData = new SMLicenseData();
		smLicenseData.setDigest(licenseDaoData.getDigest());
		smLicenseData.setInstanceName(licenseDaoData.getInstanceName());
		smLicenseData.setLicense(licenseDaoData.getLicense());
		smLicenseData.setStatus(licenseDaoData.getStatus());
		return smLicenseData;
	}

	private List<LicenseDaoData> from(List<SMLicenseData> smLicenseDatas) {
		List<LicenseDaoData> licenseDaoDatas = new ArrayList<LicenseDaoData>();
		for (SMLicenseData smLicenseData : smLicenseDatas) {
			licenseDaoDatas.add(from(smLicenseData));
		}
		return licenseDaoDatas;
	}
	
	private LicenseDaoData from(SMLicenseData smLicenseData) {
		LicenseDaoData licenseDaoData = new LicenseDaoData();
		licenseDaoData.setId(smLicenseData.getId());
		licenseDaoData.setDigest(smLicenseData.getDigest());
		licenseDaoData.setInstanceName(smLicenseData.getInstanceName());
		licenseDaoData.setLicense(smLicenseData.getLicense());
		licenseDaoData.setStatus(smLicenseData.getStatus());
		return licenseDaoData;
	}

	private List<SMLicenseData> fetchRecords (Map<String, Object> propertyNameValues) throws DaoException  {
		IDataManagerSession session = null;
		try { 
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			SMLicenseDataManager dataManager = getSMLicenseDataManager(session);

			if (dataManager == null) {
				throw new DaoException("Data Manager implementation not found for " + getClass().getName());
			}
			return dataManager.fetchRecords(propertyNameValues);
		} catch (Exception e) {
			throw new DaoException(e.getMessage(),e);
		} finally {
			try {
				closeSession(session);
			} catch (DataManagerException e) {
				throw new DaoException(e.getMessage(),e);
			}
		}
	}

	@Override
	public void insert(LicenseDaoData data) throws DaoException {
		IDataManagerSession session = null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			SMLicenseDataManager dataManager = getSMLicenseDataManager(session);
			if (dataManager == null){
				throw new DaoException("Data Manager implementation not found for " + getClass().getName());
			}

			session.beginTransaction();
			String staffUserName = getStaffUserName(AES.decrypt(data.getInstanceName()));
			IStaffData staffData = getStaffData(staffUserName);
			dataManager.insertRecord(from(data), staffData);
			commit(session);
		} catch(Exception e){
			try {
				rollbackSession(session);
			} catch (DataManagerException de) {
				throw new DaoException(de.getMessage(), de);
			}
			throw new DaoException(e.getMessage(), e);
		} finally{
			try {
				closeSession(session);
			} catch (DataManagerException e) {
				throw new DaoException(e.getMessage(), e);
			}
		}
	}


	@Override
	public void updateStatus(String instanceName, String status, String decryptedInstanceName) throws DaoException {
		IDataManagerSession session = null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			SMLicenseDataManager dataManager = getSMLicenseDataManager(session);
			if (dataManager == null){
				throw new DaoException("Data Manager implementation not found for " + getClass().getName());
			}

			session.beginTransaction();
			String staffUserName = getStaffUserName(decryptedInstanceName);
			IStaffData staffData = getStaffData(staffUserName);
			
			dataManager.updateLicenseStatus(instanceName, status, staffData);
			commit(session);
		} catch(Exception e){
			try {
				rollbackSession(session);
			} catch (DataManagerException de) {
				throw new DaoException(de.getMessage(), de);
			}
			throw new DaoException(e.getMessage(), e);
		} finally{
			try {
				closeSession(session);
			} catch (DataManagerException e) {
				throw new DaoException(e.getMessage(), e);
			}
		}
	}

	@Override
	public List<LicenseDaoData> fetchAll(String status) throws DaoException {
		IDataManagerSession session = null;
		List<LicenseDaoData> licenseDaoDataList = null;
		try {
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			SMLicenseDataManager  dataManager = getSMLicenseDataManager(session);

			if (dataManager == null){
				throw new DaoException("Data Manager implementation not found for " + getClass().getName());
			}
			Map<String, Object> propertyNameValues = new HashMap<String, Object>();
			propertyNameValues.put(LICENSE_STATUS, status);
			licenseDaoDataList = from(dataManager.fetchRecords(propertyNameValues));
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			try {
				closeSession(session);
			} catch (DataManagerException e) {
				throw new DaoException(e.getMessage(), e);
			}
		}
		
		return licenseDaoDataList;
	}

	@Override
	public List<LicenseDaoData> fetchBy(String instanceName, String status) throws DaoException {
		Map<String, Object> propertyNameValues = new HashMap<String, Object>();
		propertyNameValues.put(INSTANCE_NAME, instanceName);
		propertyNameValues.put(LICENSE_STATUS, status);
		List<SMLicenseData> records;
		records = fetchRecords(propertyNameValues);
		return from(records);
	}

	@Override
	public int count(String status) throws DaoException {
		Map<String, Object> propertyNameValues = new HashMap<String, Object>();
		propertyNameValues.put(LICENSE_STATUS, status);
		List<SMLicenseData> records;
		records = fetchRecords(propertyNameValues);
		return records.size();
	}

	@Override
	public LicenseDaoData fetchBy(String id) throws DaoException {
		Map<String, Object> propertyNameValues = new HashMap<String, Object>();
		propertyNameValues.put(ID, id);
		LicenseDaoData licenseDaoData = null;
		List<SMLicenseData> fetchedRecords = fetchRecords(propertyNameValues);
		if (fetchedRecords.isEmpty() == false) {
			licenseDaoData = from(fetchedRecords.get(0));
		}
		
		return licenseDaoData;
	}
	
	public List<LicenseDaoData> fetchByName(String name) throws DaoException {
		Map<String, Object> propertyNameValues = new HashMap<String, Object>();
		propertyNameValues.put(INSTANCENAME, name);
		List<LicenseDaoData> licenseDaoDataList = null;
		List<SMLicenseData> fetchedRecords = fetchRecords(propertyNameValues);
		if (fetchedRecords.isEmpty() == false) {
			licenseDaoDataList= from(fetchedRecords);
		}
		return licenseDaoDataList;
	}
	
	public void doAudit(String jsonString , IStaffData staffData) throws DataManagerException {
		IDataManagerSession session = null;
		try { 
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			SMLicenseDataManager dataManager = getSMLicenseDataManager(session);

			if (dataManager == null) {
				throw new DaoException("Data Manager implementation not found for " + getClass().getName());
			}
			session.beginTransaction();
			dataManager.doAudit(jsonString, staffData);
			commit(session);
		} catch(DataManagerException de){
			throw de;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}		
	}
	
	public String getAuditId(String moduleName) throws DataManagerException {
		IDataManagerSession session = null;
		try { 
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			SMLicenseDataManager dataManager = getSMLicenseDataManager(session);

			if (dataManager == null) {
				throw new DaoException("Data Manager implementation not found for " + getClass().getName());
			}
			session.beginTransaction();
			return dataManager.getSystemAuditId(moduleName);
		} catch(DataManagerException de){
				throw de;
		} catch (Exception e) {
				e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
				closeSession(session);
		}
	}
	
	private String getStaffUserName(String instanceName) throws DataManagerException {
		NetServerBLManager netServerBLManager = new NetServerBLManager();
		NetServerStaffRelDetailData staffRelData = netServerBLManager.getInstanceStaffRelationDataByName(instanceName);
		return staffRelData.getStaffUser();
	}
	private IStaffData getStaffData (String userName)  throws DataManagerException  {
		StaffBLManager staffBLManager = new StaffBLManager();
		IStaffData staffData = staffBLManager.getStaffDataByUserName(userName);
		return staffData;
	}
	
	
}
