package com.elitecore.elitesm.datamanager.servermgr.drivers;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data.HssAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.chargingdriver.data.CrestelChargingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data.ClassicCSVAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameDriverRelData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.ProfileFieldValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.detailacctlocal.data.DetailLocalAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.httpdriver.data.HttpAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ldapdriver.data.LDAPAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.mapgatewaydriver.data.MappingGatewayAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.pcdriver.data.ParleyChargingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.userfiledriver.data.UserFileAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebMethodKeyData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebServiceAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.service.data.IServiceTypeData;
import com.elitecore.elitesm.web.driver.CreateDriverConfig;

public interface DriverDataManager extends DataManager{
	
	public String createDBDriver(CreateDriverConfig driverConfig) throws DataManagerException;
	public String createHttpAuthDriver(CreateDriverConfig driverConfigs) throws DataManagerException;
	public List<LogicalNameValuePoolData> getLogicalNameValuePoolList() throws DataManagerException;
	public List<ProfileFieldValuePoolData> getProfileFieldValuePoolList() throws DataManagerException;
	public String createLDAPDriver(CreateDriverConfig driverConfig) throws DataManagerException;
	public String createMAPGatewayAuthDriver(DriverInstanceData driverInstanceData,MappingGatewayAuthDriverData mappingGatewayAuthDriverData) throws DataManagerException;
	public String createUserFileDriver(CreateDriverConfig driverConfig) throws DataManagerException;
	public List<DriverInstanceData>  getDriverInstanceList(long serviceTypeId) throws DataManagerException;
	public List<IServiceTypeData> getListOfAllServices() throws DataManagerException;
	public PageList search(DriverInstanceData driverInstanceData, int requiredPageNo, Integer pageSize) throws DataManagerException;
	public DriverInstanceData getDriverInstanceById(String driverInstanceId)throws DataManagerException;
	public DBAuthDriverData getDbDriverByDriverInstanceId(String driverInstanceId)throws DataManagerException;
	public HttpAuthDriverData getHttpDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException;
	public void updateDBAuthDriverById(DriverInstanceData driverInstance,DBAuthDriverData dbAuthDriverData,IStaffData staffData)throws DataManagerException;
	public void updateDBAuthDriverByName(DriverInstanceData driverInstance,DBAuthDriverData dbAuthDriverData,IStaffData staffData,String name)throws DataManagerException;
	public void updateHttpAuthDriverById(DriverInstanceData driverInstance,HttpAuthDriverData httpAuthDriverData,IStaffData staffData)throws DataManagerException;
	public void updateHttpAuthDriverByName(DriverInstanceData driverInstance,HttpAuthDriverData httpAuthDriverData,IStaffData staffData,String name)throws DataManagerException;
	public UserFileAuthDriverData getUserFileDriverByDriverInstanceId(String driverInstanceId)throws DataManagerException;
	public LDAPAuthDriverData getLdapAuthDriverDataByDriverInstanceId(String driverInstaceId) throws DataManagerException;
	public void updateLDAPAuthDriverByName(DriverInstanceData driverInstance,LDAPAuthDriverData updatedLDAPAuthDriverData, IStaffData staffData,String name) throws DataManagerException;
	public void updateLDAPAuthDriverById(DriverInstanceData driverInstance, LDAPAuthDriverData updatedLDAPAuthDriverData, IStaffData staffData) throws DataManagerException;
	public void createDetailLocalAcctDriver(DriverInstanceData driverInstanceData , DetailLocalAcctDriverData detailLocalAcctDriverData)throws DataManagerException;
	public String createDBAcctDriver(DriverInstanceData driverInstanceData ,DBAcctDriverData dbAcctDriverData)throws DataManagerException;
	public String createClassicCSVDriverData(ClassicCSVAcctDriverData classicCSVData,DriverInstanceData driverInstanceData)throws DataManagerException;
	public boolean getDriverInstancesByName(String name) throws DataManagerException;
	public DetailLocalAcctDriverData getDetailLocalDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException;
	public void updateDetailLocalDriverData(DriverInstanceData driverInstance,DetailLocalAcctDriverData detailLocalDriverData,IStaffData staffData,String actionAlias) throws DataManagerException;
	public DBAcctDriverData getDbAcctDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException;
	public void updateDBAcctDriverByName(DriverInstanceData driverInstance,DBAcctDriverData dbAcctDriverData,IStaffData staffData, String name) throws DataManagerException;
	public void updateDBAcctDriverById(DriverInstanceData driverInstance,DBAcctDriverData dbAcctDriverData,IStaffData staffData) throws DataManagerException;
	public void updateClassicCSVDriverByName(DriverInstanceData driverInstance,ClassicCSVAcctDriverData classicCSVAcctDriverData,IStaffData staffData,String idOrName) throws DataManagerException;
	public void updateClassicCSVDriverById(DriverInstanceData driverInstance,ClassicCSVAcctDriverData classicCSVAcctDriverData,IStaffData staffData) throws DataManagerException;
	public void updateMapGWAuthDriverDataByName(DriverInstanceData driverInstance,MappingGatewayAuthDriverData updatedMappingGatewayAuthDriverData,IStaffData staffData, String idOrName)throws DataManagerException;
	public void updateMapGWAuthDriverDataById(DriverInstanceData driverInstance,MappingGatewayAuthDriverData updatedMappingGatewayAuthDriverData,IStaffData staffData)throws DataManagerException;
	public void updateDCDriverData(DriverInstanceData driverInstance,DiameterChargingDriverData diameterChargingDriverData,IStaffData staffData,String actionAlias) throws DataManagerException;
	public ClassicCSVAcctDriverData getClassicCsvDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException;
	public MappingGatewayAuthDriverData getMappingGWDataByDriverInstanceId(String driverInstanceId) throws DataManagerException;
	public DBAuthDriverData getDBAuthDriverData(Long dbAuthId)throws DataManagerException;
	public List<DriverInstanceData> getDriverInstanceByDriverTypeList(Long driverTypeId) throws DataManagerException;
	public void createRadiusPCDriver(DriverInstanceData driverInstanceData,ParleyChargingDriverData parleyChargingDriverData) throws DataManagerException;
	public String createDiameterChargingDriver(DriverInstanceData driverInstanceData,DiameterChargingDriverData diameterChargingDriverData) throws DataManagerException;
	public DiameterChargingDriverData getDiameterChargingDataByDriverInstanceId(String driverInstanceId) throws DataManagerException;
	public String createWebServiceAuthDriver(DriverInstanceData driverInstance,WebServiceAuthDriverData webServiceAuthDriverData)throws DataManagerException;
	public WebServiceAuthDriverData getWebServiceDriverByDriverInstanceId(String driverInstanceId)throws DataManagerException;
	public String createCrestelChargingDriver(DriverInstanceData driverInstanceData,CrestelChargingDriverData crestelChargingDriverData) throws DataManagerException;
	public CrestelChargingDriverData getCrestelChargingDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException;
	public List<WebMethodKeyData> getWebMethodKeyDataList() throws DataManagerException; 
	public List<DriverInstanceData> getCacheableDriverData() throws DataManagerException;
	public List<LogicalNameDriverRelData> getLogicalNameDriverRelData(Long driverTypeId) throws DataManagerException;
	public DriverTypeData getDriverTypeData(long driverTypeId) throws DataManagerException;
	public List<ServerCertificateData> getServerCertificateDataList() throws DataManagerException ;
	public String createHssAuthDriver(CreateDriverConfig driverConfig) throws DataManagerException;
	public List<DriverTypeData> getDriverTypeList(Long serviceTypeId)throws DataManagerException;
	public List<DriverTypeData> getAcctDriverTypeList(Long serviceTypeId)throws DataManagerException;
	public String getDriverNameById(String driverInstanceId) throws DataManagerException;
	public DriverInstanceData getDriverInstanceByName(String name) throws DataManagerException;
	/**
	 * This method is used to get driver type based on it ID. If driver is not found it will return null.
	 * @param driverInstanceId Id of driver
	 * @return type of driver
	 * @throws DataManagerException
	 */
	public Long getDriverTypeById(String driverInstanceId) throws DataManagerException;
	public void updateUserFileAuthDriverDataByName(DriverInstanceData driverInstance,UserFileAuthDriverData userFileAuthDriverData, IStaffData staffData, String idOrName) throws DataManagerException;
	public void updateUserFileAuthDriverDataById(DriverInstanceData driverInstance,UserFileAuthDriverData userFileAuthDriverData, IStaffData staffData) throws DataManagerException;
	public List<String> delete(List<String> driverInstanceIds)throws DataManagerException;
	public void updateHSSDataByName(DriverInstanceData updatedDriverInstance, HssAuthDriverData updateHSSAuthDriverData, IStaffData staffData, String name) throws DataManagerException;
	public void updateHSSDataById(DriverInstanceData updatedDriverInstance, HssAuthDriverData updateHSSAuthDriverData, IStaffData staffData) throws DataManagerException;
	public void updateWebServiceAuthDriverDataByName(DriverInstanceData driverInstance, WebServiceAuthDriverData updatedWebServiceAuthDriverData, IStaffData staffData, String name) throws DataManagerException;
	public void updateWebServiceAuthDriverDataById(DriverInstanceData driverInstance, WebServiceAuthDriverData updatedWebServiceAuthDriverData, IStaffData staffData) throws DataManagerException;
	public void updateCrestelChargingDriverByName(DriverInstanceData driverInstance,CrestelChargingDriverData updatedCrestelChargingDriverData,StaffData staffData, String  name) throws DataManagerException;
	public void updateCrestelChargingDriverById(DriverInstanceData driverInstance,CrestelChargingDriverData updatedCrestelChargingDriverData,StaffData staffData) throws DataManagerException;
	public void updateDiameterChargingDriverDataByName(DriverInstanceData driverInstance,DiameterChargingDriverData updatedDiameterChargingDriverData,StaffData staffData, String  name) throws DataManagerException;
	public void updateDiameterChargingDriverDataById(DriverInstanceData driverInstance,DiameterChargingDriverData updatedDiameterChargingDriverData,StaffData staffData) throws DataManagerException;
}