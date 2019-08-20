package com.elitecore.elitesm.datamanager.servermgr.drivers;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerRelData;
import com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data.HssAuthDriverData;
import com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data.HssAuthDriverFieldMapData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ratingdriver.data.CrestelRatingDriverData;

public interface DiameterDriverDataManager extends DataManager{
	public String createCrestelRatingDriver(DriverInstanceData driverInstanceData ,CrestelRatingDriverData crestelRatingDriverData)throws DataManagerException;

	public CrestelRatingDriverData getCrestelRatingDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException;

	public void updateDiameterRatingTranslationDriverByName(DriverInstanceData driverInstdata,CrestelRatingDriverData driverData,IStaffData staffData,String name, String moduleName) throws DataManagerException;
	
	public void updateDiameterRatingTranslationDriverById(DriverInstanceData driverInstdata,CrestelRatingDriverData driverData,IStaffData staffData, String modulName) throws DataManagerException;
	
	public HssAuthDriverData getHSSDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException;

	public List<HssAuthDriverFieldMapData> getHssFieldMappingData(String hssauthdriverid)throws DataManagerException;

	public List<DiameterPeerRelData> getHssPeerRelDataList(String hssauthdriverid)throws DataManagerException;
}