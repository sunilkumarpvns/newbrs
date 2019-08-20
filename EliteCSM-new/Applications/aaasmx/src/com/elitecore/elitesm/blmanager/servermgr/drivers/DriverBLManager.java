package com.elitecore.elitesm.blmanager.servermgr.drivers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.TGPPAAAPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.DatabaseConnectionException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.TableDoesNotExistException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.datasource.database.DatabaseDSDataManager;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerRelData;
import com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data.HssAuthDriverData;
import com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data.HssAuthDriverFieldMapData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.DiameterDriverDataManager;
import com.elitecore.elitesm.datamanager.servermgr.drivers.DriverDataManager;
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
import com.elitecore.elitesm.datamanager.servermgr.drivers.ratingdriver.data.CrestelRatingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.DatabaseSubscriberProfileDataManager;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatasourceSchemaData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.userfiledriver.data.UserFileAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebMethodKeyData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebServiceAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.service.data.IServiceTypeData;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.ResultObject;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.DriverTypeConstants;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.driver.CreateDriverConfig;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

public class DriverBLManager extends BaseBLManager{

	
	private static final String CRESTEL_RATING_DRIVER = "CRESTEL_RATING_DRIVER";
	private static final String DIAMETER_CHARGING_DRIVER = "DIAMETER_CHARGING_DRIVER";
	private static final String CRESTEL_CHARGING_DRIVER = "CRESTEL_CHARGING_DRIVER";
	private static final String DB_ACCT_DRIVER = "DB_ACCT_DRIVER";
	private static final String ERROR_MESSAGE = "ERROR_MESSAGE";
	private static final String DB_AUTH_DRIVER = "DB_AUTH_DRIVER";
	private static final String HSS_AUTH_DRIVER = "HSS_AUTH_DRIVER";
	private static final String WEB_SER_AUTH_DRIVER = "WEB_SER_AUTH_DRIVER";
	private static final String HTTP_AUTH_DRIVER = "HTTP_AUTH_DRIVER";
	private static final String LDAP_DRIVER = "LDAP_DRIVER";
	private static final String MAP_GATEWAY_DRIVER = "MAP_GATEWAY_DRIVER";
	private static final String CLASSIC_CSV_DRIVER = "CLASSIC_CSV_DRIVER";
	private static final String USERFILE_AUTH_DRIVER = "UERFILE_AUTH_DRIVER";
	private static final String DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR = "Data Manager implementation not found for ";
	private String MODULE="DriverBLManager";
	private static final String FAILURE = "FAILURE";
	private static final String SUCCESS = "SUCCESS";

	private DriverDataManager getDriverDataManager(IDataManagerSession session) {		
		DriverDataManager driverDataManager = (DriverDataManager) DataManagerFactory.getInstance().getDataManager(DriverDataManager.class, session);
		return driverDataManager;		
	}
	
	private DiameterDriverDataManager getDiameterDriverDataManager(IDataManagerSession session) {		
		DiameterDriverDataManager diameterDriverDataManager = (DiameterDriverDataManager) DataManagerFactory.getInstance().getDataManager(DiameterDriverDataManager.class, session);
		return diameterDriverDataManager;		
	}

	private DatabaseSubscriberProfileDataManager getDatabaseSusbscriberProfileDataManager(IDataManagerSession session) {		
		DatabaseSubscriberProfileDataManager databaseSubscriberProfileDataManager = (DatabaseSubscriberProfileDataManager) DataManagerFactory.getInstance().getDataManager(DatabaseSubscriberProfileDataManager.class, session);
		return databaseSubscriberProfileDataManager;		
	}

	private DatabaseDSDataManager getDatabaseDSManager(IDataManagerSession session) {		
		DatabaseDSDataManager databaseDSDataManager = (DatabaseDSDataManager) DataManagerFactory.getInstance().getDataManager(DatabaseDSDataManager.class, session);
		return databaseDSDataManager;		
	}

	public String createDBDriver(CreateDriverConfig driverConfig, IStaffData staffData) throws  DataManagerException {
		List<CreateDriverConfig> driverConfigList =  new ArrayList<CreateDriverConfig>();
		driverConfigList.add(driverConfig);
		
		String responseMsg = null;
		Map<String, List<Status>>  errorMsg = createDBDriver(driverConfigList,staffData, "false");
		for (Entry<String, List<Status>> map : errorMsg.entrySet()) {
			if(map.getKey().equalsIgnoreCase(ERROR_MESSAGE)){
				 responseMsg = map.getValue().get(0).getMessage();
			}
		}
		return responseMsg;
	}

	public Map<String, List<Status>>  createDBDriver(List<CreateDriverConfig> configList, IStaffData staffData, String partialSuccess) 
		throws DataManagerException{
		return insertDriverRecords(configList, staffData, partialSuccess, DB_AUTH_DRIVER);
	}
	
	public void createHssAuthDriver(CreateDriverConfig driverConfig, IStaffData staffData) throws TableDoesNotExistException, DatabaseConnectionException, DataManagerException{
		List<CreateDriverConfig> driverConfigList =  new ArrayList<CreateDriverConfig>();
		driverConfigList.add(driverConfig);
		createHssAuthDriver(driverConfigList,staffData, "false");
	}
	
	public Map<String, List<Status>> createHssAuthDriver(List<CreateDriverConfig> driverConfigList, IStaffData staffData, String partialSuccess) throws DataManagerException {
		return insertDriverRecords(driverConfigList, staffData, partialSuccess, HSS_AUTH_DRIVER);
	}
	
	public void createHttpAuthDriver(CreateDriverConfig driverConfig,IStaffData staffData) throws DataManagerException{
		List<CreateDriverConfig> createDriverConfigList = new ArrayList<CreateDriverConfig>();
		createDriverConfigList.add(driverConfig);
		createHttpAuthDriver(createDriverConfigList, staffData, "false");
		
	}
	
	public Map<String, List<Status>> createHttpAuthDriver(List<CreateDriverConfig> driverConfigList,IStaffData staffData, String partialSuccess) throws DataManagerException {
		return insertDriverRecords(driverConfigList, staffData, partialSuccess, HTTP_AUTH_DRIVER);
	}
	public void createMAPGatewayAuthDriver(DriverInstanceData driverInstance, MappingGatewayAuthDriverData mapAuthData, StaffData staffData) throws DataManagerException {
		List<ResultObject> resultObjectList = new ArrayList<ResultObject>();
		
		ResultObject resultObject = new ResultObject();
		resultObject.setDriverInstance(driverInstance);
		resultObject.setMappingGarewayAuthData(mapAuthData);
		
		resultObjectList.add(resultObject);
		createMAPGatewayAuthDriver(resultObjectList, staffData, "false");
	}
	public Map<String, List<Status>> createMAPGatewayAuthDriver(List<ResultObject> resultObjectList,StaffData staffData, String partialSuccess) throws DataManagerException {
		return insertDriverRecords(resultObjectList, staffData, partialSuccess, MAP_GATEWAY_DRIVER);
	}

	
	public void createRadiusPCDriver(DriverInstanceData driverInstanceData,ParleyChargingDriverData parleyChargingDriverData) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		
		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}		
		try{
			session.beginTransaction();
			driverDataManager.createRadiusPCDriver(driverInstanceData,parleyChargingDriverData);
			commit(session);
		}catch(DuplicateParameterFoundExcpetion dpe){
			rollbackSession(session);			
			throw new DuplicateParameterFoundExcpetion("Duplicate name used.");
		}catch(DataManagerException de){
			rollbackSession(session);
			throw de;
		}catch(Exception de){
			de.printStackTrace();
			rollbackSession(session);			
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}
	
	public void createDiameterChargingDriver(DriverInstanceData driverInstanceData,DiameterChargingDriverData diameterChargingDriverData, IStaffData staffData) throws DataManagerException{
		ResultObject resultObject = new ResultObject();
		List<ResultObject> resultObjectList = new ArrayList<ResultObject>();
		resultObject.setDiameterChargingDriverData(diameterChargingDriverData);
		resultObject.setDriverInstance(driverInstanceData);
		resultObjectList.add(resultObject);
		createDiameterChargingDriver(resultObjectList, staffData, "false");
		}
	
	public Map<String, List<Status>> createDiameterChargingDriver(List<ResultObject> resultObjectList, IStaffData staffData, String partialSuccess) throws DataManagerException{
		return insertDriverRecords(resultObjectList, staffData, partialSuccess, DIAMETER_CHARGING_DRIVER);
	}
	
	public void updateDiameterChargingDriverDataById(DriverInstanceData driverInstance,DiameterChargingDriverData diameterChargingDriverData,IStaffData staffData)throws DataManagerException {
		updateDiameterChargingDriverDataByName(driverInstance, diameterChargingDriverData, staffData, null);
	}
	
	public void updateDiameterChargingDriverDataByName(DriverInstanceData driverInstance,DiameterChargingDriverData diameterChargingDriverData,IStaffData staffData, 
			String idOrName)throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}
		
		try{
			session.beginTransaction();
			if(idOrName != null){
				driverDataManager.updateDiameterChargingDriverDataByName(driverInstance, diameterChargingDriverData, (StaffData) staffData, idOrName);
			} else {
				driverDataManager.updateDiameterChargingDriverDataById(driverInstance, diameterChargingDriverData, (StaffData) staffData);
			}
			commit(session);
		}catch(DataManagerException de){
			rollbackSession(session);
			throw de;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);			
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			closeSession(session);
		}
	}

	public List<IDatasourceSchemaData> getSchemaList(DBAuthDriverData dbAuthDriverData) throws DataManagerException,TableDoesNotExistException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		DatabaseSubscriberProfileDataManager databaseSubscriberProfileDataManager = getDatabaseSusbscriberProfileDataManager(session);
		DatabaseDSDataManager databaseDSDataManager = getDatabaseDSManager(session);
		List<IDatasourceSchemaData> schemaList = null; 
		Connection con=null;
		
		if (driverDataManager == null || databaseSubscriberProfileDataManager==null || databaseDSDataManager==null){
			throw new DataManagerException("Data Manager implementation not found for DriverDataManager/DatabaseSubscriberProfileDataManager/DatabaseDSDataManager ");
		}
		try{
			IDatabaseDSData databaseDSData = databaseDSDataManager.getDatabaseDSDataById(dbAuthDriverData.getDatabaseId());
			
			con = databaseDSDataManager.getConnection(databaseDSData, true);
			schemaList = databaseSubscriberProfileDataManager.getDatabaseSchemaList(dbAuthDriverData, con);
		}catch(DatabaseConnectionException dpe){
			throw dpe;
		}catch(TableDoesNotExistException tde){
			throw tde;
		}catch(DataManagerException de){
			throw de;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					Logger.logDebug(MODULE,"Error while closing Connection object. Reason: "+ e.getMessage());
				}
			}
			closeSession(session);
		}
		return schemaList;
	}
	
	public List<LogicalNameValuePoolData> getLogicalNameValuePoolList() throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}
		
		try{
			List<LogicalNameValuePoolData> dataList = driverDataManager.getLogicalNameValuePoolList();
			return dataList;
		}catch(DataManagerException de){
			throw de;
		}catch(Exception de){
			 de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}
	
	public List<LogicalNameDriverRelData> getLogicalNameList(Long driverTypeId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}
		
		try{
			List<LogicalNameDriverRelData> dataList = driverDataManager.getLogicalNameDriverRelData(driverTypeId);
			return dataList;
		}catch(DataManagerException de ){
			throw de;
		}catch(Exception de){
			 de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}
	
	public List<ProfileFieldValuePoolData> getProfileFieldValuePoolList() throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}
		
		try{
			List<ProfileFieldValuePoolData> dataList = driverDataManager.getProfileFieldValuePoolList();
			return dataList;
		}catch(DataManagerException de){
			throw de;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}

	public void createLDAPDriver(CreateDriverConfig createDriverConfig, IStaffData staffData) throws DataManagerException{
		List<CreateDriverConfig> configList =new ArrayList<CreateDriverConfig>();
		configList.add(createDriverConfig);
		createLDAPDriver(configList, staffData, "false");
	}
	public Map<String, List<Status>> createLDAPDriver(List<CreateDriverConfig> configList,IStaffData staffData, String partialSuccess) throws DataManagerException{
		return insertDriverRecords(configList, staffData, partialSuccess, LDAP_DRIVER);
	}

	public void createUserFileAuthDriver(CreateDriverConfig driverConfig,IStaffData staffData) throws DataManagerException{
		List<CreateDriverConfig> driverConfigList =  new ArrayList<CreateDriverConfig>();
		driverConfigList.add(driverConfig);
		createUserFileAuthDriver(driverConfigList,staffData, "false");
	}
	
	public Map<String, List<Status>> createUserFileAuthDriver(List<CreateDriverConfig> configList, IStaffData staffData, String partialSuccess) throws DataManagerException {
		return insertDriverRecords(configList,staffData,partialSuccess,USERFILE_AUTH_DRIVER);
	}
	
	public List<DriverInstanceData> getDriverInstanceList(long serviceTypeId) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}
		
		try{
			List<DriverInstanceData> driverInstanceList = driverDataManager.getDriverInstanceList(serviceTypeId);
			return driverInstanceList;
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}
	
	public List<DriverInstanceData> getDriverInstanceByDriverTypeList(Long driverTypeId) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}
		
		try{
			List<DriverInstanceData> driverInstanceList = driverDataManager.getDriverInstanceByDriverTypeList(driverTypeId);
			return driverInstanceList;
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}	
	

	public List<IServiceTypeData> getListOfAllServices() throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}
		
		try{
			List<IServiceTypeData> serviceList = driverDataManager.getListOfAllServices();
			return serviceList;
		}catch(DataManagerException de){
			throw de;
		}catch(Exception de){
			 de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}
	public PageList search(DriverInstanceData driverInstanceData, IStaffData staffData, int requiredPageNo, Integer pageSize) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}

		try{	
			PageList driverList = driverDataManager.search(driverInstanceData,requiredPageNo,pageSize);	
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_DRIVER);
			
			commit(session);
			return driverList;
			
		}catch(DataManagerException de){
			rollbackSession(session);
			throw de;
		}catch(Exception de){
			de.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}
	public void deleteById(List<String> driverInstanceIds,IStaffData staffData,List<String> driverInstanceNameList) throws DataManagerException{
		delete(driverInstanceIds,staffData,BY_ID, driverInstanceNameList);
	}
	public void deleteByName(List<String> driverInstanceNames,IStaffData staffData,List<String> driverInstanceNameList) throws DataManagerException{
		delete(driverInstanceNames,staffData,BY_NAME,driverInstanceNameList);
	}
	public void delete(List<String> driverInstanceIdsOrName, IStaffData staffData, boolean isIdorName, List<String> driverInstanceNameList) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		String driverName = null;
		
		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}
		
		try{
			List<String> tagNameList = new ArrayList<String>();
			List<String> driverInstIds = new ArrayList<String>();

			tagNameList.add("driver-instace-id");
			tagNameList.add("secondary-driver-id");
			tagNameList.add("additional-driver-id");
			session.beginTransaction();
			if(Collectionz.isNullOrEmpty(driverInstanceIdsOrName) == false){
				int size = driverInstanceIdsOrName.size();
				for(int i=0;i<size;i++){
					if(Strings.isNullOrBlank(driverInstanceIdsOrName.get(i)) == false){
						String strIdOrName = driverInstanceIdsOrName.get(i).trim();

						String driverId=null;
						Long driverType = null;
						if(isIdorName){
							driverType = getDriverTypeById(strIdOrName);
							driverName = getDriverNameById(strIdOrName);
							driverId = strIdOrName;
							driverInstIds.add(strIdOrName);
						}else{
							DriverInstanceData instanceData = getDriverInstanceByName(strIdOrName);
							driverType = instanceData.getDriverTypeId();
							driverId = instanceData.getDriverInstanceId()+"";
							driverName = strIdOrName;
							driverInstIds.add(instanceData.getDriverInstanceId());
						}

						if(driverType != null){
							if(driverType.equals(DriverTypeConstants.RADIUS_DB_AUTH_DRIVER) || 
									driverType.equals(DriverTypeConstants.RADIUS_DB_ACCT_DRIVER) || 
									driverType.equals(DriverTypeConstants.RADIUS_LDAP_AUTH_DRIVER) || 
									driverType.equals(DriverTypeConstants.RADIUS_USERFILE_AUTH_DRIVER) ||
									driverType.equals(DriverTypeConstants.RADIUS_CLASSICCSV_ACCT_DRIVER) || 
									driverType.equals(DriverTypeConstants.RADIUS_MAPGATEWAY_AUTH_DRIVER) ||
									driverType.equals(DriverTypeConstants.RADIUS_AIRCEL_WEBSERVICE_AUTH_DRIVER) || 
									driverType.equals(DriverTypeConstants.RADIUS_HTTP_AUTH_DRIVER) || 
									driverType.equals(DriverTypeConstants.RADIUS_HSS_AUTH_DRIVER)){

								tagNameList.add("cache-driver-id");

								ServicePolicyBLManager  blManager = new ServicePolicyBLManager();
								if(blManager.getBindedPluginNames(tagNameList).contains(driverId)){
										throw new ConstraintViolationException("Child Record Found - Delete "+driverName+" driver from Radius service policy first !!!");
								}
							} else if (driverType.equals(DriverTypeConstants.DIAMETER_DB_AUTH_DRIVER) || 
									driverType.equals(DriverTypeConstants.DIAMETER_LDAP_AUTH_DRIVER) || 
									driverType.equals(DriverTypeConstants.DIAMETER_USERFILE_AUTH_DRIVER) || 
									driverType.equals(DriverTypeConstants.DIAMETER_DB_ACCT_DRIVER) ||
									driverType.equals(DriverTypeConstants.DIAMETER_CLASSICCSV_ACCT_DRIVER) || 
									driverType.equals(DriverTypeConstants.DIAMETER_RATING_TRANSLATION_DRIVER) || 
									driverType.equals(DriverTypeConstants.DIAMETER_MAP_GWAUTH_FORWARD) || 
									driverType.equals(DriverTypeConstants.DIAMETER_HTTP_AUTH_FORWARD) || 
									driverType.equals(DriverTypeConstants.DIAMETER_CRESTEL_OCSv2_DRIVER) || 
									driverType.equals(DriverTypeConstants.DIAMETER_HSS_AUTH_DRIVER)){

								TGPPAAAPolicyBLManager tgppaaaPolicyBLManager = new TGPPAAAPolicyBLManager();
								if(tgppaaaPolicyBLManager.getBindedValues(tagNameList).contains(driverId)){
									throw new ConstraintViolationException("Child Record Found - Delete "+driverName+" driver from TGPP service policy first !!!");
								}
							} 
						}
					}
				}
				
				if(Collectionz.isNullOrEmpty(driverInstIds) == false){
					
					List<String> driverInstNames = driverDataManager.delete(driverInstIds);
					
					if(Collectionz.isNullOrEmpty(driverInstNames) == false){
						
						for(String strDriverName : driverInstNames){
							staffData.setAuditName(strDriverName);
							AuditUtility.doAuditing(session,staffData, ConfigConstant.DELETE_DRIVER);
						}
					}
				}
				
			}
			commit(session);	
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception e){
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}finally{
			closeSession(session);
		}
	}	

	public DriverInstanceData getListOfDriverInstanceByDriverInstanceId(String driverInstanceId) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}
		
		try{				
			DriverInstanceData driverInstanceData = driverDataManager.getDriverInstanceById(driverInstanceId);
			return driverInstanceData;
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			 de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}

	public DBAuthDriverData getDBDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}
		
		try{				
			DBAuthDriverData authDriverData = driverDataManager.getDbDriverByDriverInstanceId(driverInstanceId);
			return authDriverData;
		}catch(DataManagerException de){
			throw de;
		}catch(Exception de){
			 de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}
	
	public HttpAuthDriverData getHttpDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		
		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}
		
		try{				
			HttpAuthDriverData httpAuthDriverData = driverDataManager.getHttpDriverByDriverInstanceId(driverInstanceId);
			return httpAuthDriverData;
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}

	public void updateHttpAuthDriverById(DriverInstanceData driverInstanceData,HttpAuthDriverData httpAuthDriverData,IStaffData staffData) throws DataManagerException{
		updateHttpAuthDriverByName(driverInstanceData, httpAuthDriverData, staffData, null);
	}
	
	public void updateHttpAuthDriverByName(DriverInstanceData driverInstanceData,HttpAuthDriverData httpAuthDriverData,IStaffData staffData,String idOrName) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}
		try{
			session.beginTransaction();
			if(idOrName != null){
				driverDataManager.updateHttpAuthDriverByName(driverInstanceData, httpAuthDriverData, staffData, idOrName);
			}else{
				driverDataManager.updateHttpAuthDriverById(driverInstanceData, httpAuthDriverData, staffData);
			}
			commit(session);
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception de){
			rollbackSession(session);
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}
	
	public UserFileAuthDriverData getUserFileDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
		}
		try{				
			UserFileAuthDriverData driverData = driverDataManager.getUserFileDriverByDriverInstanceId(driverInstanceId);
			return driverData;
		}catch(DataManagerException de){
			throw de;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}
	public WebServiceAuthDriverData getWebServiceDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
		}
		
		try{				
			WebServiceAuthDriverData driverData = driverDataManager.getWebServiceDriverByDriverInstanceId(driverInstanceId);
			return driverData;
		}catch(DataManagerException de){
			throw de;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}

	private void updateUserFileAuthDriver(DriverInstanceData driverInstance,UserFileAuthDriverData userFileAuthDriverData,IStaffData staffData,String name) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		
		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR +getClass().getName());
		}

		try{
			session.beginTransaction();
			if(name != null){
				driverDataManager.updateUserFileAuthDriverDataByName(driverInstance, userFileAuthDriverData,staffData,name);
			}else{
				driverDataManager.updateUserFileAuthDriverDataById(driverInstance, userFileAuthDriverData,staffData);
			}
			commit(session);
		}catch(DataManagerException de){
			rollbackSession(session);
			throw de;
		}catch(Exception de){
			de.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}
	
	public void updateUserFileAuthDriverByName(DriverInstanceData driverInstance,UserFileAuthDriverData userFileAuthDriverData,IStaffData staffData,String driverName) throws DataManagerException{
		updateUserFileAuthDriver(driverInstance,userFileAuthDriverData,staffData,driverName);
	}
	public void updateUserFileAuthDriverById(DriverInstanceData driverInstance,UserFileAuthDriverData userFileAuthDriverData,IStaffData staffData) throws DataManagerException{
		updateUserFileAuthDriver(driverInstance,userFileAuthDriverData,staffData,null);
	}
	
	public LDAPAuthDriverData getLdapDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}
		try{				
			LDAPAuthDriverData ldapauthDriverData = driverDataManager.getLdapAuthDriverDataByDriverInstanceId(driverInstanceId);
			return ldapauthDriverData;
		}catch(DataManagerException de){
			throw de;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}

	}

	public void updateLdapAuthDriverById(DriverInstanceData driverInstance,LDAPAuthDriverData updatedLDAPAuthDriverData,IStaffData staffData)
			throws DataManagerException{
		updateLDAPAuthDriverByName(driverInstance, updatedLDAPAuthDriverData,  staffData, null);
		
	}
	
	public void updateLDAPAuthDriverByName(DriverInstanceData driverInstance,LDAPAuthDriverData updatedLDAPAuthDriverData, IStaffData staffData, String idOrName) 
			throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		
		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
		}
		try{
			session.beginTransaction();
			if(idOrName != null){
				driverDataManager.updateLDAPAuthDriverByName(driverInstance, updatedLDAPAuthDriverData, staffData, idOrName.trim());
			}else{
				driverDataManager.updateLDAPAuthDriverById(driverInstance, updatedLDAPAuthDriverData, staffData);
			}
			commit(session);
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception de){
			 de.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}

	public void createDetailLocalAcctDriver(DriverInstanceData driverInstance,DetailLocalAcctDriverData detailLocalAcctDriverData) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		
		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
		}
		try{
			session.beginTransaction();
			driverDataManager.createDetailLocalAcctDriver(driverInstance, detailLocalAcctDriverData);
			commit(session);
		}catch(DuplicateParameterFoundExcpetion dpe){
			dpe.printStackTrace();
			rollbackSession(session);
			throw dpe;
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception de){
			rollbackSession(session);
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}
	public void createDBAcctDriver( DBAcctDriverData dbAcctDriverData, DriverInstanceData driverInstanceData,IStaffData staffData ) throws DataManagerException{
		List<ResultObject> resultObjectList = new ArrayList<ResultObject>();
		
		ResultObject resultObject = new ResultObject();
		resultObject.setDriverInstance(driverInstanceData);
		resultObject.setDbAcctDriverData(dbAcctDriverData);;
		
		resultObjectList.add(resultObject);
		createDBAcctDriver(resultObjectList, staffData, "false");
		
	}
	public Map<String, List<Status>> createDBAcctDriver(List<ResultObject> resultObjectList,IStaffData staffData, String partialSuccess)
			throws DataManagerException{
		return insertDriverRecords(resultObjectList, staffData, partialSuccess, DB_ACCT_DRIVER);
	}

	public void createClassicCSVAcctDriver(ClassicCSVAcctDriverData classicCSVDriverData, DriverInstanceData driverInstanceData, IStaffData staffData) throws DataManagerException{
			List<ResultObject> resultObjectList = new ArrayList<ResultObject>();
	
			ResultObject resultObject = new ResultObject();
			resultObject.setDriverInstance(driverInstanceData);
			resultObject.setClassicCSVAcctDriverData(classicCSVDriverData);
	
			resultObjectList.add(resultObject);
			createClassicCSVAcctDriver(resultObjectList, staffData, "false");
			
	}
	public Map<String, List<Status>> createClassicCSVAcctDriver(List<ResultObject> resultObjectList,IStaffData staffData, String partialSuccess) throws DataManagerException {
		return insertDriverRecords(resultObjectList, staffData, partialSuccess, CLASSIC_CSV_DRIVER);
	}
	public DetailLocalAcctDriverData getDetailLocalDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
		}
		try{				
			DetailLocalAcctDriverData detailLocalDriverData = driverDataManager.getDetailLocalDriverByDriverInstanceId(driverInstanceId);
			return detailLocalDriverData;
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			 de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}


	public void updateDetailLocalDriver(DriverInstanceData driverInstance,DetailLocalAcctDriverData detailLocalDriverData,IStaffData staffData,String actionAlias) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		
		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
		}
		try{
			session.beginTransaction();
			driverDataManager.updateDetailLocalDriverData(driverInstance, detailLocalDriverData,staffData,actionAlias);
			commit(session);

		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception de){
			 de.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}

	public DBAcctDriverData getDbAcctDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}
		try{				
			DBAcctDriverData dbAcctDriverData = driverDataManager.getDbAcctDriverByDriverInstanceId(driverInstanceId);
			return dbAcctDriverData;
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			 de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}

	public void updateDBAcctDriverById(DriverInstanceData driverInstance,DBAcctDriverData dbAcctDriver,IStaffData staffData) throws DataManagerException{
		updateDBAcctDriverByName(driverInstance, dbAcctDriver, staffData, null);
	}

	public void updateDBAcctDriverByName(DriverInstanceData driverInstance, DBAcctDriverData updatedDBAcctDriver
			, IStaffData staffData, String idOrName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		
		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
		}
		try{
			session.beginTransaction();
			if(idOrName != null){
				driverDataManager.updateDBAcctDriverByName(driverInstance, updatedDBAcctDriver, staffData, idOrName);
			}else{
				driverDataManager.updateDBAcctDriverById(driverInstance, updatedDBAcctDriver, staffData);
			}
			
			commit(session);
		}catch (DatabaseConnectionException dc) {
			commit(session);
			throw dc;
		}catch (TableDoesNotExistException tde) {
			commit(session);
			throw tde;
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception de){
			de.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}

	public ClassicCSVAcctDriverData getClassicCsvDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}
		
		try{				
			ClassicCSVAcctDriverData classicCsvDriverData = driverDataManager.getClassicCsvDriverByDriverInstanceId(driverInstanceId);
			return classicCsvDriverData;
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}
	
	public MappingGatewayAuthDriverData getMappingGWDataByDriverInstanceId(String driverInstanceId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}
		try{				
			MappingGatewayAuthDriverData mappingGatewayAuthDriverData = driverDataManager.getMappingGWDataByDriverInstanceId(driverInstanceId);
			return mappingGatewayAuthDriverData;
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}
	
	public DiameterChargingDriverData getDiameterChargingDataByDriverInstanceId(String driverInstanceId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}
		try{				
			DiameterChargingDriverData diameterChargingDriverData = driverDataManager.getDiameterChargingDataByDriverInstanceId(driverInstanceId);
			return diameterChargingDriverData;
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}

	public void updateClassicCsvDriverData(DriverInstanceData driverInstance,ClassicCSVAcctDriverData classicCsvDriverData,IStaffData staffData) throws DataManagerException{
		updateRadiusClassicCSVAcctDriverByName(driverInstance, classicCsvDriverData, staffData, null);
	}

	public void updateRadiusClassicCSVAcctDriverByName( DriverInstanceData driverInstance, ClassicCSVAcctDriverData classicCSVAcctDriverData,IStaffData staffData, String idOrName) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		
		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
		}
		try{
			session.beginTransaction();
			if(idOrName != null){
				driverDataManager.updateClassicCSVDriverByName(driverInstance, classicCSVAcctDriverData, staffData, idOrName);
			}else{
				driverDataManager.updateClassicCSVDriverById(driverInstance, classicCSVAcctDriverData, staffData);
			}
			commit(session);			
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception de){
			rollbackSession(session);
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}
	public void updateMapGWAuthDriverDataById(DriverInstanceData driverInstance,MappingGatewayAuthDriverData mappingGatewayAuthDriverData,IStaffData staffData) throws DataManagerException{
		updateMapGWAuthDriverDataByName(driverInstance, mappingGatewayAuthDriverData, staffData, null);
	}

	public void updateMapGWAuthDriverDataByName( DriverInstanceData driverInstance,MappingGatewayAuthDriverData updatedMappingGatewayAuthDriverData,
			IStaffData staffData, String idOrName) throws DataManagerException{
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		
		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
		}
		try{
			session.beginTransaction();
			if(idOrName != null){
				driverDataManager.updateMapGWAuthDriverDataByName(driverInstance, updatedMappingGatewayAuthDriverData, staffData, idOrName.trim());
			}else{
				driverDataManager.updateMapGWAuthDriverDataById(driverInstance, updatedMappingGatewayAuthDriverData, staffData);
			}
			commit(session);			
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception de){
			rollbackSession(session);
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}
	public void updateDCDriverData(DriverInstanceData driverInstance,DiameterChargingDriverData diameterChargingDriverData,IStaffData staffData,String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
		}
		try{
			session.beginTransaction();
			driverDataManager.updateDCDriverData(driverInstance, diameterChargingDriverData,staffData,actionAlias);
			commit(session);
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception de){
			rollbackSession(session);
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}
	
	public void createCrestelRatingDriver(CrestelRatingDriverData crestelRatingDriverData,DriverInstanceData driverInstanceData, IStaffData staffData) throws DataManagerException{
		List<ResultObject> resultObjectList = new ArrayList<ResultObject>();
		
		ResultObject resultObject = new ResultObject();
		resultObject.setDriverInstance(driverInstanceData);
		resultObject.setCrestelRatingDriverData(crestelRatingDriverData);
		
		resultObjectList.add(resultObject);
		createCrestelRatingDriver(resultObjectList, staffData, "false");
	}

	public Map<String, List<Status>> createCrestelRatingDriver(List<ResultObject> resultObjectList, IStaffData staffData, String partialSuccess) throws DataManagerException {
		return insertDriverRecords(resultObjectList, staffData, partialSuccess, CRESTEL_RATING_DRIVER);
	}
	public CrestelRatingDriverData getCrestelRatingDriverData(String driverInstanceId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterDriverDataManager diameterDriverDataManager = getDiameterDriverDataManager(session);

		if (diameterDriverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
		}
		try{				
			CrestelRatingDriverData classicCsvDriverData = diameterDriverDataManager.getCrestelRatingDriverByDriverInstanceId(driverInstanceId);
			return classicCsvDriverData;
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}
	
	public HssAuthDriverData getHSSDriverData(String driverInstanceId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterDriverDataManager diameterDriverDataManager = getDiameterDriverDataManager(session);

		if (diameterDriverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
		}
		try{				
			HssAuthDriverData hssAuthDriverData = diameterDriverDataManager.getHSSDriverByDriverInstanceId(driverInstanceId);
			return hssAuthDriverData;
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}

	}

	public void updateDiameterRatingTranslationDriverDataById(DriverInstanceData driverInstance,CrestelRatingDriverData crestelRating,IStaffData staffData, String moduleName) throws DataManagerException {
		updateDiameterRatingTranslationDriverDataByName(driverInstance, crestelRating, staffData, null, moduleName);
	}

	public void updateDiameterRatingTranslationDriverDataByName(DriverInstanceData driverInstance,CrestelRatingDriverData crestelRating,IStaffData staffData,String idOrName, String moduleName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterDriverDataManager diameterDriverDataManager = getDiameterDriverDataManager(session);

		if (diameterDriverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
		}
		try{
			session.beginTransaction();
			if(idOrName != null) {
				diameterDriverDataManager.updateDiameterRatingTranslationDriverByName(driverInstance, crestelRating, staffData, idOrName, moduleName);
				
			}else {
				diameterDriverDataManager.updateDiameterRatingTranslationDriverById(driverInstance, crestelRating, staffData, moduleName);
			}
			commit(session);
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception de){
			rollbackSession(session);
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
		
	}
	
	public void createWebServiceAuthDriver(DriverInstanceData driverInstance,WebServiceAuthDriverData webServiceAuthDriverData, IStaffData staffData)throws DataManagerException {
		List<ResultObject> resultObjList = new ArrayList<ResultObject>();
		
		ResultObject resultObject = new ResultObject();
		resultObject.setDriverInstance(driverInstance);
		resultObject.setWebServiceAuthDriverData(webServiceAuthDriverData);
		resultObjList.add(resultObject);
		
		createWebServiceAuthDriver(resultObjList, staffData, "false");
	}
	
	public Map<String, List<Status>> createWebServiceAuthDriver(List<ResultObject> resultObjectList, IStaffData staffData, String partialSuccess) throws DataManagerException {
		return insertDriverRecords(resultObjectList, staffData, partialSuccess, WEB_SER_AUTH_DRIVER);
	}

	public void updateWebServiceAuthDriverById(DriverInstanceData driverInstance,WebServiceAuthDriverData webServiceAuthDriverData,IStaffData staffData)throws DataManagerException {
		updateWebServiceAuthDriverByName(driverInstance, webServiceAuthDriverData, staffData, null);
	}	
	
	public void updateWebServiceAuthDriverByName(DriverInstanceData driverInstance, WebServiceAuthDriverData updatedWebServiceAuthDriverData, IStaffData staffData, String idOrName) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		
		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
		}
		try{
			session.beginTransaction();
			if(idOrName != null){
				driverDataManager.updateWebServiceAuthDriverDataByName(driverInstance, updatedWebServiceAuthDriverData, staffData, idOrName.trim());
			}else{
				driverDataManager.updateWebServiceAuthDriverDataById(driverInstance, updatedWebServiceAuthDriverData, staffData);
			}		
			commit(session);			
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception de){
			rollbackSession(session);
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}	
	
	public void createCrestelChargingDriver(CrestelChargingDriverData crestelChargingDriverData,DriverInstanceData driverInstanceData, IStaffData staffData) throws DataManagerException{
		
		List<ResultObject> crestelChargingDriverDataList = new ArrayList<ResultObject>();
		ResultObject resultObject = new ResultObject();
		resultObject.setCrestelChargingDriverData(crestelChargingDriverData);
		resultObject.setDriverInstance(driverInstanceData);
		crestelChargingDriverDataList.add(resultObject);

		createCrestelChargingDriver(crestelChargingDriverDataList, staffData, "false");
	}
	
	public Map<String, List<Status>> createCrestelChargingDriver(List<ResultObject> crestelChargingDriverDataList, IStaffData staffData, String partialSuccess) throws DataManagerException {
		return insertDriverRecords(crestelChargingDriverDataList, staffData, partialSuccess, CRESTEL_CHARGING_DRIVER);
	}

	public CrestelChargingDriverData getCrestelChargingDriverData(String driverInstanceId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		
		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
		}
		try{				
			CrestelChargingDriverData crestleChargingDriverData = driverDataManager.getCrestelChargingDriverByDriverInstanceId(driverInstanceId);
			return crestleChargingDriverData;
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}

	public void updateCrestelChargingDriverDataById(DriverInstanceData driverInstdata,CrestelChargingDriverData crestelChargingDriverData, IStaffData staffData ) throws DataManagerException {
		
		updateCrestelChargingDriverDataByName(driverInstdata, crestelChargingDriverData, (StaffData) staffData, null);
	}	
	
	public void updateCrestelChargingDriverDataByName(DriverInstanceData driverInstance,CrestelChargingDriverData updatedCrestelChargingDriverData,
			StaffData staffData, String idOrName) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
		}
		try{
			session.beginTransaction();
			if(idOrName != null){
				driverDataManager.updateCrestelChargingDriverByName(driverInstance, updatedCrestelChargingDriverData, staffData, idOrName);
			}else{
				driverDataManager.updateCrestelChargingDriverById(driverInstance, updatedCrestelChargingDriverData, staffData);
			}
			commit(session);
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception de){
			rollbackSession(session);
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}

	public List<WebMethodKeyData> getWebMethodKeyDataList() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}
		try{				
			List<WebMethodKeyData> webMethodKeyDataList = driverDataManager.getWebMethodKeyDataList();
			return webMethodKeyDataList;
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}
	public List<ServerCertificateData> getListOfServerCertificate() throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		
		List<ServerCertificateData> lstServerCertificateData;

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}
		try{
			lstServerCertificateData = driverDataManager.getServerCertificateDataList();
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
		return lstServerCertificateData; 
	}
	
	public List<DriverInstanceData> getCacheableDriverData() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		List<DriverInstanceData> cacheableDriverInstanceDataList = null;
		
		try{
			DriverDataManager driverDataManager = getDriverDataManager(session);
			if(driverDataManager==null){
				throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
			}
			cacheableDriverInstanceDataList = driverDataManager.getCacheableDriverData();
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
		return cacheableDriverInstanceDataList;
	}
	
	
	public List<String> getLogicalValueDriverRelList(Long driverTypeId) throws DataManagerException{
	  
	  List<LogicalNameDriverRelData> logicalNameDriverRelDataList = getLogicalNameList(driverTypeId);
	  
	  ArrayList<String> logicalNameList = new ArrayList<String>();
	  for(LogicalNameDriverRelData logicalNameDriverRelData: logicalNameDriverRelDataList) {
		  logicalNameList.add(logicalNameDriverRelData.getLogicalNameValuePoolData().getValue());
	  }
	  return logicalNameList;
  }
 
  public DriverTypeData getDriverTypeDataById(long driverTypeId) throws DataManagerException{
	  	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		DriverTypeData driverTypeData =  driverDataManager.getDriverTypeData(driverTypeId);
		return driverTypeData;
  }
  
  public String getLogicalNameJson(List<LogicalNameValuePoolData> nameValuePoolList){
	  return getLogicalNameJson(nameValuePoolList, null);
  }
  
  public String getLogicalNameJson(List<LogicalNameValuePoolData> nameValuePoolList, List<String> logicalNameMultipleAllowList){
		StringBuilder logicalNameData = new StringBuilder();
		logicalNameData.append("[");
		for(LogicalNameValuePoolData logicalNameValuePoolData : nameValuePoolList){
			logicalNameData.append("{");
			logicalNameData.append("\"name\":\""+logicalNameValuePoolData.getName()+"\",");
			logicalNameData.append("\"value\":\""+logicalNameValuePoolData.getValue()+"\",");
			logicalNameData.append("\"multipleAllow\":\""+ (logicalNameMultipleAllowList == null ? Boolean.FALSE : logicalNameMultipleAllowList.contains(logicalNameValuePoolData.getValue()))+"\"");
			logicalNameData.append("},");
		}
		logicalNameData = new StringBuilder(logicalNameData.substring(0,logicalNameData.length()-1));
		logicalNameData.append("]");
		Logger.logInfo("LogicalNameJson", logicalNameData);
		return logicalNameData.toString();
	}

	public List<DriverTypeData> getDriverTypeList(Long serviceTypeId)  throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		List<DriverTypeData> driverTypeDataList = null;
		try{
			DriverDataManager driverDataManager = getDriverDataManager(session);
			if(driverDataManager==null){
				throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
			}
			driverTypeDataList = driverDataManager.getDriverTypeList(serviceTypeId);
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
		return driverTypeDataList;
	}

	public List<HssAuthDriverFieldMapData> getHSSFieldMapData(String hssauthdriverid) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterDriverDataManager diameterDriverDataManager = getDiameterDriverDataManager(session);
		List<HssAuthDriverFieldMapData> fieldMappingDataList;
		
		if (diameterDriverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
		}
		try{				
			fieldMappingDataList = diameterDriverDataManager.getHssFieldMappingData(hssauthdriverid);
			return fieldMappingDataList;
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}

	public List<DiameterPeerRelData> getDiameterPeerRelData(String hssauthdriverid) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterDriverDataManager diameterDriverDataManager = getDiameterDriverDataManager(session);
		List<DiameterPeerRelData> diameterPeerRelDataList;
		
		if (diameterDriverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
		}
		try{				
			diameterPeerRelDataList = diameterDriverDataManager.getHssPeerRelDataList(hssauthdriverid);
			return diameterPeerRelDataList;
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}

	public void updateHSSDataById(DriverInstanceData updatedDriverInstance,HssAuthDriverData hssAuthDriverData, IStaffData staffData) throws DataManagerException {
		updateHSSDataByName(updatedDriverInstance, hssAuthDriverData, staffData, null);
	}
	public void updateHSSDataByName(DriverInstanceData updatedDriverInstance, HssAuthDriverData updateHSSAuthDriverData, IStaffData staffData, String idOrName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager diameterDriverDataManager = getDriverDataManager(session);
		
		if (diameterDriverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
		}
		try{
			session.beginTransaction();
			if(idOrName != null){
				diameterDriverDataManager.updateHSSDataByName(updatedDriverInstance, updateHSSAuthDriverData, staffData, idOrName);
			}else{
				diameterDriverDataManager.updateHSSDataById(updatedDriverInstance, updateHSSAuthDriverData, staffData);
			}
			commit(session);
		}catch(DataManagerException e){
			rollbackSession(session);
			throw e;
		}catch(Exception de){
			rollbackSession(session);
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}		
}
		

	/**
	 * @param serviceTypeId
	 * @return List<DriverTypeData>
	 * @throws DataManagerException
	 */
	public List<DriverTypeData> getAcctDriverTypeList(Long serviceTypeId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		List<DriverTypeData> driverTypeDataList = null;
		try{
			DriverDataManager driverDataManager = getDriverDataManager(session);
			if(driverDataManager==null){
				throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
			}
			driverTypeDataList = driverDataManager.getAcctDriverTypeList(serviceTypeId);
			
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
		return driverTypeDataList;
	}
  
	public String getDriverNameById(String driverInstanceId) throws DataManagerException {
		String driverName = null;
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		
		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
		}
		try{
			driverName = driverDataManager.getDriverNameById(driverInstanceId);
		}catch(DataManagerException e){
			throw e;
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
		
		return driverName;
	}
	
	public Long getDriverTypeById(String driverInstanceId) throws DataManagerException {
		return getDriverTypeData(driverInstanceId, BY_ID);
	}
	
	private Long getDriverTypeData(Object driverInstanceIdOrName, boolean byIdOrName) throws DataManagerException {
		Long driverType = null;
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		if (driverDataManager == null) {
			throw new DataManagerException("driver data not found ");
		}
		try{
			if(byIdOrName){
				driverType = driverDataManager.getDriverTypeById((String)driverInstanceIdOrName);
			}
			return driverType;
		}catch (DataManagerException exp) {
			throw exp;
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}finally{
			closeSession(session);
		}
	}
	
	public DriverInstanceData getDriverInstanceByName(String driverName) throws DataManagerException {
		return getDriverInstanceData(driverName.trim(), BY_NAME);
	}
	
	public DriverInstanceData getDriverInstanceByDriverInstanceId(String driverInstanceId) throws DataManagerException{
		return getDriverInstanceData(driverInstanceId, BY_ID);
	}
	
	private DriverInstanceData getDriverInstanceData(Object driverInstanceIdOrName, boolean byIdOrName) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		
		if (driverDataManager == null) {
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+ getClass().getName());
		}
		
		try{
			if(byIdOrName){
				return driverDataManager.getDriverInstanceById((String)driverInstanceIdOrName);
			}else{
				return driverDataManager.getDriverInstanceByName((String)driverInstanceIdOrName);
			}
		}catch (DataManagerException exp) {
			throw exp;
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}finally{
			closeSession(session);
		}
	}

	public void updateDBAuthDriverById(DriverInstanceData driverInstance,DBAuthDriverData dbAuthDriverData, IStaffData staffData)throws DataManagerException {
		updateDBAuthDriverByName(driverInstance, dbAuthDriverData, staffData, null);
	}
	
	public void updateDBAuthDriverByName(DriverInstanceData driverInstance, DBAuthDriverData dbAuthDriverData, 
			IStaffData staffData, String idOrName)throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);
		
		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR+getClass().getName());
		}
		try{
			session.beginTransaction();
			
			if(idOrName != null){
				driverDataManager.updateDBAuthDriverByName(driverInstance, dbAuthDriverData,staffData,idOrName);
			}else{
				driverDataManager.updateDBAuthDriverById(driverInstance, dbAuthDriverData,staffData);
			}
			commit(session);
		} catch (DatabaseConnectionException de) {
			commit(session);
			throw de;
		} catch (TableDoesNotExistException te) {
			commit(session);
			throw te;
		}catch(DataManagerException de){
			rollbackSession(session);
			throw de;
		}catch(Exception de){
			de.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(de.getMessage(),de);
		}finally{
			closeSession(session);
		}
	}
	
	public List<String> getLogicalNameDriverRelList(Long driverTypeId) throws DataManagerException{
		List<LogicalNameDriverRelData> logicalNameDriverRelDataList = getLogicalNameList(driverTypeId);
		ArrayList<String> logicalNameList = new ArrayList<String>();

		for(LogicalNameDriverRelData logicalNameDriverRelData: logicalNameDriverRelDataList) {
			logicalNameList.add(logicalNameDriverRelData.getLogicalNameValuePoolData().getName());
		}
		return logicalNameList;
	}
		  
		 
	public List<LogicalNameValuePoolData> getMultipleLogicalNameRelList(Long driverTypeId) throws DataManagerException{
		List<LogicalNameDriverRelData> logicalNameDriverRelDataList = getLogicalNameList(driverTypeId);
		List<LogicalNameValuePoolData> data = new ArrayList<LogicalNameValuePoolData>();

		for(LogicalNameDriverRelData rel : logicalNameDriverRelDataList ){
			LogicalNameValuePoolData logicalNameValuePoolData = rel.getLogicalNameValuePoolData();
			data.add(logicalNameValuePoolData);
		}
		return data;
	}
	
	private Map<String, List<Status>> insertDriverRecords(List<?> records, IStaffData staffData, String partialSuccess, String driverType) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DriverDataManager driverDataManager = getDriverDataManager(session);

		ArrayList<Status> successRecordList = new ArrayList<Status>();
		ArrayList<Status> failureRecordList = new ArrayList<Status>();

		Map<String, List<Status>> responseMap  = new HashMap<String,List<Status>>();
		
		/*for DB AUTH Driver*/
		boolean checkDbDead = false; 
		boolean checkTableExist = false;

		if (driverDataManager == null){
			throw new DataManagerException(DATA_MANAGER_IMPLEMENTATION_NOT_FOUND_FOR + getClass().getName());
		}

		try{
			if(Collectionz.isNullOrEmpty(records) == false){

				for (Iterator<?> iterator = records.iterator(); iterator.hasNext();) {
					session.beginTransaction();
					Object object =  iterator.next();
					try {
						String name = "";
						if (USERFILE_AUTH_DRIVER.equalsIgnoreCase(driverType)) {
							name = driverDataManager.createUserFileDriver((CreateDriverConfig) object);
						} else if(CLASSIC_CSV_DRIVER.equalsIgnoreCase(driverType)){
							name = driverDataManager.createClassicCSVDriverData(((ResultObject) object).getClassicCSVAcctDriverData(),((ResultObject) object).getDriverInstance());
						} else if(MAP_GATEWAY_DRIVER.equalsIgnoreCase(driverType)){
							name = driverDataManager.createMAPGatewayAuthDriver(((ResultObject) object).getDriverInstance(),((ResultObject) object).getMappingGarewayAuthData()); 
						} else if(LDAP_DRIVER.equalsIgnoreCase(driverType)){
							name = driverDataManager.createLDAPDriver((CreateDriverConfig) object);
						} else if(HTTP_AUTH_DRIVER.equalsIgnoreCase(driverType)){
							name = driverDataManager.createHttpAuthDriver((CreateDriverConfig) object);
						} else if(WEB_SER_AUTH_DRIVER.equalsIgnoreCase(driverType)){
							name = driverDataManager.createWebServiceAuthDriver(((ResultObject) object).getDriverInstance(), ((ResultObject) object).getWebServiceAuthDriverData());
						} else if(HSS_AUTH_DRIVER.equalsIgnoreCase(driverType)){
							name = driverDataManager.createHssAuthDriver((CreateDriverConfig) object);
						} else if(DB_AUTH_DRIVER.equalsIgnoreCase(driverType)){
							List<Status> driverErrorMessage = new ArrayList<Status>();
							name = driverDataManager.createDBDriver((CreateDriverConfig) object);
							
							try{
								List<IDatasourceSchemaData> datasourceSchemaList = getSchemaList(((CreateDriverConfig) object).getDbAuthDriverData());
								/*create schema*/
								if(Collectionz.isNullOrEmpty(datasourceSchemaList) == false) {
									DatabaseSubscriberProfileDataManager databaseSubscriberProfileDataManager = getDatabaseSusbscriberProfileDataManager(session);
									databaseSubscriberProfileDataManager.createSchema(datasourceSchemaList);
								}
							} catch (DatabaseConnectionException e) {
								checkDbDead = true;
							} catch (TableDoesNotExistException te) {
								checkTableExist = true;
							}
							
							if(checkDbDead) {
								driverErrorMessage.add(new Status(ERROR_MESSAGE, ConfigConstant.DATABASE_CONNECTION_FAILED));
							} else if(checkTableExist) {
								driverErrorMessage.add(new Status(ERROR_MESSAGE,ConfigConstant.TABLE_DOES_NOT_EXIST));
							}else {
								driverErrorMessage.add(new Status(ERROR_MESSAGE,ConfigConstant.SUCCESS));
							}
							responseMap.put(ERROR_MESSAGE, driverErrorMessage);
						} else if(DB_ACCT_DRIVER.equalsIgnoreCase(driverType)){
							DBAcctDriverData dbAcctDriverData = ((ResultObject) object).getDbAcctDriverData();
							name = driverDataManager.createDBAcctDriver(((ResultObject) object).getDriverInstance(), dbAcctDriverData);
						} else if(CRESTEL_CHARGING_DRIVER.equalsIgnoreCase(driverType)){
							name = driverDataManager.createCrestelChargingDriver(((ResultObject) object).getDriverInstance(), ((ResultObject) object).getCrestelChargingDriverData());
						} else if(DIAMETER_CHARGING_DRIVER.equalsIgnoreCase(driverType)){
							name = driverDataManager.createDiameterChargingDriver(((ResultObject) object).getDriverInstance(),((ResultObject) object).getDiameterChargingDriverData());
						} else if(CRESTEL_RATING_DRIVER.equalsIgnoreCase(driverType)){
							DiameterDriverDataManager diameterDataManager = getDiameterDriverDataManager(session);
							name = diameterDataManager.createCrestelRatingDriver(((ResultObject) object).getDriverInstance(), ((ResultObject) object).getCrestelRatingDriverData());
						}
						staffData.setAuditName(name);
						AuditUtility.doAuditing(session, staffData, ConfigConstant.CREATE_DRIVER);

						if(partialSuccess.equalsIgnoreCase("true")){
							commit(session);
							if(checkDbDead || checkTableExist){
								successRecordList.add(new Status(ResultCode.SUCCESS.responseCode, name
										+ " created successfully.Either database connection is not found or Subscriber Profiler Table is not found. Subscriber Profile Management will not work properly."));
							}else{
								successRecordList.add(new Status(ResultCode.SUCCESS.responseCode, name + " created successfully"));
							}
						}
					} catch (DataManagerException e) {
						if(partialSuccess.equalsIgnoreCase("true")){

							failureRecordList.add(new Status(RestUtitlity.getResultCode(e), e.getMessage()));
							rollbackSession(session);
						} else {
							rollbackSession(session);
							throw e;
						}
					}
				}
				responseMap.put(SUCCESS, successRecordList);
				responseMap.put(FAILURE, failureRecordList);
				if(partialSuccess.equalsIgnoreCase("false") || partialSuccess.isEmpty()){
					commit(session);
				}
			}
		}catch(DataManagerException exp){
			Logger.logTrace(MODULE, exp);
			throw exp;
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException("failed to perform bulk operation, Reason: "+e.getMessage(),e);
		}finally{
			closeSession(session);
		}
		return responseMap;
	}
}