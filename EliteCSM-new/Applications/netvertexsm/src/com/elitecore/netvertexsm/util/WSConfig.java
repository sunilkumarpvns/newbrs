package com.elitecore.netvertexsm.util;

import com.elitecore.netvertexsm.blmanager.wsconfig.WebServiceConfigBLManager;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.wsconfig.data.ProvisioningAPIConfiguration;
import com.elitecore.netvertexsm.datamanager.wsconfig.data.WSConfigData;
import com.elitecore.netvertexsm.util.logger.Logger;

public class WSConfig {

	private static final String MODULE = "WEB-SRV-CNF";
    private static int databasedsId;
	private static String tableName;
	private static String userIdentityFieldName;
	private static int recordFetchLimit;
	private static String primaryKeyColumn;
	private static String sequenceName;
	private static int bodCDRDriverId;
	private static int dynaSPRDatabaseDSId;
	private static ProvisioningAPIConfiguration provisioningAPIConfigData;
	private static String subscriberIdentity;
	private static DatabaseDSData dynaSPRDatabaseDSData;
	private static DatabaseDSData usageMonitoringDatabaseData;

	public static void init() {
		Logger.logInfo(MODULE, "Reading web service configuration");
		try {
			WebServiceConfigBLManager blManager = new WebServiceConfigBLManager();
			WSConfigData wsConfigData = blManager.getSubscriberConfiguration();
			if(wsConfigData.getDatabasedsId()!=null){
				databasedsId = wsConfigData.getDatabasedsId().intValue();
			}
			recordFetchLimit = wsConfigData.getRecordFetchLimit();
			if(wsConfigData.getBodCDRDriverId()!=null){
				bodCDRDriverId = wsConfigData.getBodCDRDriverId().intValue();
			}
			tableName = wsConfigData.getTableName();
			userIdentityFieldName = wsConfigData.getUserIdentityFieldName();
			primaryKeyColumn = wsConfigData.getPrimaryKeyColumn();
			sequenceName = wsConfigData.getSequenceName();
			if(wsConfigData.getDynaSprDatabaseId()!=null){
				dynaSPRDatabaseDSId =  wsConfigData.getDynaSprDatabaseId().intValue();
				dynaSPRDatabaseDSData = wsConfigData.getDynaSPRDatabaseDSData();
			}
			
			subscriberIdentity=wsConfigData.getSubscriberIdentity();
			usageMonitoringDatabaseData = wsConfigData.getUsageMonitoringDatabaseData();
			
			Logger.logInfo(MODULE, "Web Service configuration reading completed");
		} catch(Exception e) {
			Logger.logError(MODULE, "Error while reading web service configuration");
			Logger.logTrace(MODULE, e);
		} 
	}

	public static String getSubscriberIdentity() {
		return subscriberIdentity;
	}

	public static int getDatabasedsId() {
		return databasedsId;
	}

	public static String getTableName() {
		return tableName;
	}

	public static String getUserIdentityFieldName() {
		return userIdentityFieldName;
	}

	public static int getRecordFetchLimit() {
		return recordFetchLimit;
	}

	public static String getPrimaryKeyColumn() {
		return primaryKeyColumn;
	}

	public static String getSequenceName() {
		return sequenceName;
	}

	public static int getBodCDRDriverId() {
		return bodCDRDriverId;
	}
	
	public static int getDynaSPRDatabaseDatasourceId() {
		return dynaSPRDatabaseDSId;
	}
	
	public static ProvisioningAPIConfiguration getProvisioningAPIConfigData(){
		return provisioningAPIConfigData;
	}
	
	public static DatabaseDSData getDynaSPRDatabaseDSData() {
		return dynaSPRDatabaseDSData;
	}

	public static DatabaseDSData getUsageMonitoringDatabaseData() {
		return usageMonitoringDatabaseData;
	}
}