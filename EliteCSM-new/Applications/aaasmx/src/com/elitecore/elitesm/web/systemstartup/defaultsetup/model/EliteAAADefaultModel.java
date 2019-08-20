package com.elitecore.elitesm.web.systemstartup.defaultsetup.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.rm.concurrentloginpolicy.ConcurrentLoginPolicyBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.subscriberprofile.database.DatabaseSubscriberProfileBLManager;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.ConcurrentLoginPolicyData;
import com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data.ConcurrentLoginPolicyDetailData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data.ClassicCSVAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.data.SubscriberProfileData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.transactionlogger.data.TransactionLoggerData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadiusServicePolicyData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SMConfigInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SessionManagerInstanceData;
import com.elitecore.elitesm.util.ConcurrentLoginPolicyUtility;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.DriverTypeConstants;
import com.elitecore.elitesm.util.constants.PluginTypesConstants;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.driver.CreateDriverConfig;
import com.elitecore.elitesm.web.plugins.CreatePluginConfig;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.controller.EliteAAASetupAction;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.controller.EliteAAASetupAction.MODULE_NAME_CONSTANTS;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.controller.EliteAAASetupAction.MODULE_STATUS;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.form.EliteAAASetupData;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.util.DefaultSetupUtility;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.passwordutil.PasswordEncryption;

public class EliteAAADefaultModel{

	public static final String TRANSACTION_LOGGER = "Transaction Logger";
	public static final String SUBSCRIBER_PROFILE = "subscriber-profile";
	public static final String CONCURRENT_LOGIN_POLICY = "concurrent-login-policy";
	public static final String RADIUS_TRANSACTION_LOGGER = "radius-transaction-logger";
	public static final String RADIUS_SERVICE_POLICY = "radius-service-policy";
	public static final String RADIUS_SESSION_MANAGER = "radius-session-manager";
	public static final String RAD_CLASSIC_CSV_DRIVER = "rad-classic-csv-driver";
	public static final String RAD_DB_AUTH_DRIVER = "rad-db-auth-driver";
	public static final String DATABASE_DATASOURCE = "database-datasource";

	public void createDefaultConfigurationSetup(EliteAAASetupData setupData)  throws DataManagerException{
		try{
			//Fetch Staff Detail
			StaffBLManager staffBLManager = new StaffBLManager();
			IStaffData staffData = staffBLManager.getStaffDataByUserName(DefaultSetupUtility.ADMIN);
			
			//Create Default Database Datasource
			DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
			String databaseDatasource = RestUtitlity.getXML(DATABASE_DATASOURCE);

			DatabaseDSData databaseDSdata = ConfigUtil.deserialize(databaseDatasource, DatabaseDSData.class);
			DefaultSetupUtility.readDatabaseProperties(databaseDSdata);

			databaseDSBLManager.create(databaseDSdata, staffData);
			EliteAAASetupAction.updateStatus(MODULE_NAME_CONSTANTS.DATABASEDATASOURCE, MODULE_STATUS.SUCCESS);

			//Create Default Radius DB Auth Driver
			String radiusDBAuthDriver = RestUtitlity.getXML(RAD_DB_AUTH_DRIVER);
			DriverInstanceData dbAuthDriverInstanceData = ConfigUtil.deserialize(radiusDBAuthDriver, DriverInstanceData.class);

			DriverBLManager authDriverBlManager = new DriverBLManager();

			dbAuthDriverInstanceData.setStatus(BaseConstant.SHOW_STATUS_ID);
			dbAuthDriverInstanceData.setDriverTypeId(DriverTypeConstants.RADIUS_DB_AUTH_DRIVER);
			dbAuthDriverInstanceData.setCreatedByStaffId(staffData.getStaffId());        	
			dbAuthDriverInstanceData.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
			dbAuthDriverInstanceData.setLastModifiedByStaffId(staffData.getStaffId());
			dbAuthDriverInstanceData.setCreateDate(new Timestamp(new Date().getTime()));

			DBAuthDriverData dbAuthDriverData = (DBAuthDriverData)dbAuthDriverInstanceData.getDbdetail().iterator().next();
			dbAuthDriverInstanceData.setCacheable(dbAuthDriverData.getCacheable());

			CreateDriverConfig driverConfig = new CreateDriverConfig();
			driverConfig.setDriverInstanceData(dbAuthDriverInstanceData);
			driverConfig.setDbAuthDriverData(dbAuthDriverData);

			authDriverBlManager.createDBDriver(driverConfig,staffData);
			EliteAAASetupAction.updateStatus(MODULE_NAME_CONSTANTS.RADIUSDBAUTHDRIVER, MODULE_STATUS.SUCCESS);
			
			//Create Default Radius Classic CSV Driver
			String radiusClassicCSVDriver = RestUtitlity.getXML(RAD_CLASSIC_CSV_DRIVER);
			DriverInstanceData classicCSVDriverInstanceData = ConfigUtil.deserialize(radiusClassicCSVDriver, DriverInstanceData.class);
			DriverBLManager acctDriverBlManager = new DriverBLManager();
			classicCSVDriverInstanceData.setStatus(BaseConstant.SHOW_STATUS_ID);
			classicCSVDriverInstanceData.setDriverTypeId(DriverTypeConstants.RADIUS_CLASSICCSV_ACCT_DRIVER);
			classicCSVDriverInstanceData.setCreatedByStaffId(staffData.getStaffId());        	
			classicCSVDriverInstanceData.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
			classicCSVDriverInstanceData.setLastModifiedByStaffId(staffData.getStaffId());
			classicCSVDriverInstanceData.setCreateDate(new Timestamp(new Date().getTime()));

			ClassicCSVAcctDriverData classicCSVAcctDriverData = (ClassicCSVAcctDriverData) classicCSVDriverInstanceData.getCsvset().iterator().next();

			String encryptedPassword = PasswordEncryption.getInstance().crypt(classicCSVAcctDriverData.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
			classicCSVAcctDriverData.setPassword(encryptedPassword);

			acctDriverBlManager.createClassicCSVAcctDriver(classicCSVAcctDriverData, classicCSVDriverInstanceData, staffData);
			EliteAAASetupAction.updateStatus(MODULE_NAME_CONSTANTS.RADIUSCLASSICCSVDRIVER, MODULE_STATUS.SUCCESS);
			
			//Create Radius Session Manager
			String radiusSessionManager = RestUtitlity.getXML(RADIUS_SESSION_MANAGER);
			SMConfigInstanceData smConfigInstanceData = ConfigUtil.deserialize(radiusSessionManager, SMConfigInstanceData.class);
			SessionManagerBLManager sessionManagerBLManager = new SessionManagerBLManager();
			SessionManagerInstanceData sessionManagerInstanceData = new SessionManagerInstanceData();
			sessionManagerInstanceData.setName(smConfigInstanceData.getName());
			sessionManagerInstanceData.setDescription(smConfigInstanceData.getDescription());
			sessionManagerInstanceData.setStatus("CST01");
			sessionManagerInstanceData.setSmConfigInstanceData(smConfigInstanceData);
			sessionManagerBLManager.create(sessionManagerInstanceData,staffData);
			EliteAAASetupAction.updateStatus(MODULE_NAME_CONSTANTS.RADIUSSESSIONMANAGER, MODULE_STATUS.SUCCESS);
			
			//Create Transaction Logger Plugin
			String radiusTransactionLogger = RestUtitlity.getXML(RADIUS_TRANSACTION_LOGGER);
			TransactionLoggerData radiusTransactionLoggerData = ConfigUtil.deserialize(radiusTransactionLogger, TransactionLoggerData.class);
			PluginBLManager pluginBLManager = new PluginBLManager();
			PluginInstData pluginInstData = new PluginInstData();
			
			List<PluginInstData> radiusAuthPlugin = pluginBLManager.getAuthPluginList();
			if(Collectionz.isNullOrEmpty(radiusAuthPlugin) == false){
				for (PluginInstData pluginInstanceData : radiusAuthPlugin) {
					if(PluginTypesConstants.RADIUS_TRANSACTION_LOGGER.equals(pluginInstanceData.getPluginTypeId())){
						EliteAAASetupAction.updateStatus(MODULE_NAME_CONSTANTS.TRANSACTIONLOGGERPLUGIN, MODULE_STATUS.FAILED);
						Logger.logError(TRANSACTION_LOGGER,"System allows you to create only one Radius Transaction Logger.");
						throw new Exception("System allows you to create only one Radius Transaction Logger.");
					}
				}
			}
			
			pluginInstData.setName(radiusTransactionLoggerData.getPluginName()); 
			pluginInstData.setDescription(radiusTransactionLoggerData.getPluginDescription());
			pluginInstData.setStatus(radiusTransactionLoggerData.getPluginStatus());
			pluginInstData.setPluginTypeId(PluginTypesConstants.RADIUS_TRANSACTION_LOGGER);

			CreatePluginConfig pluginConfig = new CreatePluginConfig();
			pluginConfig.setPluginInstData(pluginInstData);
			pluginConfig.setTransactionLoggerData(radiusTransactionLoggerData);
			
			pluginBLManager.createTransactionLogger(pluginConfig,staffData);
			EliteAAASetupAction.updateStatus(MODULE_NAME_CONSTANTS.TRANSACTIONLOGGERPLUGIN, MODULE_STATUS.SUCCESS);
			
			//Create Concurrent Login Policy
			String concurrentLoginPolicy = RestUtitlity.getXML(CONCURRENT_LOGIN_POLICY);
			ConcurrentLoginPolicyData concurrentLoginPolicyData = ConfigUtil.deserialize(concurrentLoginPolicy, ConcurrentLoginPolicyData.class);
			ConcurrentLoginPolicyBLManager concurrentLoginPolicyBLManager = new ConcurrentLoginPolicyBLManager();

			Timestamp timestamp = new Timestamp(new Date().getTime());

			concurrentLoginPolicyData.setSystemGenerated("N");
			concurrentLoginPolicyData.setCreateDate(timestamp);
			concurrentLoginPolicyData.setCreatedByStaffId(staffData.getStaffId());
			concurrentLoginPolicyData.setLastModifiedDate(timestamp);
			concurrentLoginPolicyData.setLastModifiedByStaffId(staffData.getStaffId());
			concurrentLoginPolicyData.setStatusChangeDate(timestamp);
			concurrentLoginPolicyData.setLogin(setupData.getConcurrentLoginLimit());

			if (concurrentLoginPolicyData.getLoginLimit().equalsIgnoreCase("Unlimited")) {
				concurrentLoginPolicyData.setLogin(-1);
			} 

			if (Collectionz.isNullOrEmpty(concurrentLoginPolicyData.getLstConcurrentLoginPolicyDetails()) == false) {
				concurrentLoginPolicyData.setConcurrentLoginPolicyDetail(concurrentLoginPolicyData.getLstConcurrentLoginPolicyDetails());
				concurrentLoginPolicyData.setLstConcurrentLoginPolicyDetails(null);
				List<ConcurrentLoginPolicyDetailData> concurrentLoginPolicyDetails = concurrentLoginPolicyData.getConcurrentLoginPolicyDetail();

				int size = concurrentLoginPolicyDetails.size();
				for ( int i = 0; i < size; i++ ) {
					ConcurrentLoginPolicyDetailData concurrentLoginPolicyDetail = (ConcurrentLoginPolicyDetailData) concurrentLoginPolicyDetails.get(i);
					concurrentLoginPolicyDetail.setSerialNumber(i + 1);
					if (ConcurrentLoginPolicyUtility.getAttributeIdFromAttributeName(concurrentLoginPolicyData.getAttribute(), concurrentLoginPolicyDetail.getAttributeValue()).equals("attribute_list_not_found") == false) {
						concurrentLoginPolicyDetail.setAttributeValue(ConcurrentLoginPolicyUtility.getAttributeIdFromAttributeName(concurrentLoginPolicyData.getAttribute(), concurrentLoginPolicyDetail.getAttributeValue()));
					}
					if (concurrentLoginPolicyDetail.getLoginLimit().equalsIgnoreCase("Unlimited")) {
						concurrentLoginPolicyDetail.setLogin(-1);
					}
				}
			}

			concurrentLoginPolicyBLManager.createConcurrentLoginPolicy(concurrentLoginPolicyData, staffData,false);
			EliteAAASetupAction.updateStatus(MODULE_NAME_CONSTANTS.CONCURRENTLOGINPOLICY, MODULE_STATUS.SUCCESS);
			
			//Create Subscriber Profile
			String subscriberProfile = RestUtitlity.getXML(SUBSCRIBER_PROFILE);
			SubscriberProfileData  subscriberProfileData = ConfigUtil.deserialize(subscriberProfile, SubscriberProfileData.class);
			subscriberProfileData.setUserName(setupData.getUserName());
			subscriberProfileData.setPassword(setupData.getPassword());
			subscriberProfileData.setUserIdentity(setupData.getUserName());

			DatabaseSubscriberProfileBLManager subscriberProfileBLManager = new DatabaseSubscriberProfileBLManager();

			if(subscriberProfileBLManager.getSubscriberProfileData(setupData.getUserName()) != null){
				List<String> userIdentity = new ArrayList<String>();
				userIdentity.add(setupData.getUserName());
				subscriberProfileBLManager.deleteSubscriberProfileRecords(userIdentity, staffData);
			}

			if(concurrentLoginPolicyData != null){
				subscriberProfileData.setConcurrentLoginPolicy(concurrentLoginPolicyData.getName());
			}

			if(setupData.getMacAddress() != null){
				subscriberProfileData.setCallingStationId(setupData.getMacAddress());
			}

			if(setupData.getIpAddress() != null){
				subscriberProfileData.setFramedIPv4Address(setupData.getIpAddress());
			}

			subscriberProfileBLManager.addSubscriberProfileData(subscriberProfileData,staffData);
			EliteAAASetupAction.updateStatus(MODULE_NAME_CONSTANTS.SUBSCRIBERPROFILE, MODULE_STATUS.SUCCESS);
			
			//Create Radius Service Policy
			String radiusServicePolicy = RestUtitlity.getXML(RADIUS_SERVICE_POLICY);
			RadiusServicePolicyData radiusServicePolicyData = ConfigUtil.deserialize(radiusServicePolicy, RadiusServicePolicyData.class);
			ServicePolicyBLManager policyBLManager = new ServicePolicyBLManager();
			policyBLManager.createRadiusServicePolicy(radiusServicePolicyData, staffData);
			EliteAAASetupAction.updateStatus(MODULE_NAME_CONSTANTS.RADIUSSERVICEPOLICY, MODULE_STATUS.SUCCESS);
		}catch(DataManagerException e){
			e.printStackTrace();
			throw e;
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
}
