package com.elitecore.netvertexsm.ws.db;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.elitecore.commons.base.Strings;
import com.elitecore.netvertexsm.blmanager.servermgr.sessionmgr.SessionBLManager;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.SessionConfData;
import com.elitecore.netvertexsm.util.EliteUtility;
import com.elitecore.netvertexsm.util.WSConfig;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.ws.logger.Logger;

public class DBConnectionManager {

	private static final String MODULE = DBConnectionManager.class.getSimpleName(); 
	private static DBConnectionManager dbConnectionManager;
	private static final HashMap<String, BasicDataSource> dataSourceCache;
	static {
		dbConnectionManager = new DBConnectionManager();
		dataSourceCache = new HashMap<String, BasicDataSource>(4);
	}
	private DBConnectionManager(){}
	public synchronized static DBConnectionManager getInstance(){
		return dbConnectionManager;
	}
	
	private static final String SM_DB_DATASOURCE_NAME = "SM DATA SOURCE";
	
	private DBConfiguration smDBConfiguration;
	private DBConfiguration dynamicSPRDBConfiguration;
	private DBConfiguration usageMonitoringDBConfiguration;
	private DBConfiguration subscriberProfileDBConfiguration;
	private DBConfiguration sessionPrimaryDBConfig = null;
	private DBConfiguration sessionSecondaryDBConfig = null;
	
	public void initSMDatasurce() {
		Logger.logInfo(MODULE, "Initializing Server Manager Database Datasource");
		FileInputStream fileInputStream = null;
		try {
			Properties properties = new Properties();
			File dbPropsFile = new File(EliteUtility.getSMHome()+ConfigConstant.DATABASE_CONFIG_FILE_LOCATION);
			fileInputStream = new FileInputStream(dbPropsFile);
			properties.load(fileInputStream);
			String dbUrl =  properties.getProperty("hibernate.connection.url");
			String dbUser =  properties.getProperty("hibernate.connection.username");
			String dbPassword =  properties.getProperty("hibernate.connection.password");
			String driverClass = getDriverClass(dbUrl);
			smDBConfiguration = new DBConfiguration(SM_DB_DATASOURCE_NAME ,driverClass,dbUrl,dbUser,dbPassword);
			Logger.logInfo(MODULE, "Server Manager Database Configuration: " + smDBConfiguration);
			createDataSource(smDBConfiguration);
		} catch (Exception e) {
			Logger.logError(MODULE, "Error while initializing Server Manager Database Datasource. Reason: " + e.getMessage());
			Logger.logTrace(MODULE , e);
		}finally{
			try{
				if(fileInputStream != null){ fileInputStream.close(); }
			}catch(Exception e){}
		}
	}

	public Connection getSMDatabaseConection() throws SQLException{
		if(smDBConfiguration != null){
			return getConection(smDBConfiguration);
	}
		Logger.logError(MODULE, "Unable to get connection from SM Database Datasource. Reason: " 
				+ "Database Configuration not found");
		return null;
	}
	
	private Connection getConection(DBConfiguration configuration) throws SQLException{
		BasicDataSource dataSource = dataSourceCache.get(configuration.getName());
		if(dataSource != null){
			return dataSource.getConnection();
		}
		Logger.logError(MODULE, "Unable to get connection from Database: " 
				+ configuration.getName() + " . Reason: Database dataSource not found");
		return null;
	}
	private Connection getConection(String dsName) throws SQLException{
		BasicDataSource dataSource = dataSourceCache.get(dsName);
		if(dataSource != null){
			return dataSource.getConnection();
		}
		Logger.logError(MODULE, "Unable to get connection from Database: " 
				+ dsName + " . Reason: Database dataSource not found");
		return null;
	}
	
	public void initDynamicSPRDatasurce() {
		Logger.logInfo(MODULE, "Initializing Dynamic SPR Database Datasource");
		DatabaseDSData databaseDSData = WSConfig.getDynaSPRDatabaseDSData();
		if(databaseDSData == null){
			Logger.logError(MODULE, "Unable to  initialize Dynamic SPR Database Datasource. " +
					"Reason: Database Configuration not found");
			return;
		}
		String driverClass = getDriverClass(databaseDSData.getConnectionUrl());
		dynamicSPRDBConfiguration = new DBConfiguration(databaseDSData.getName(),
				 databaseDSData.getConnectionUrl(),driverClass, databaseDSData.getUserName(), databaseDSData.getPassword(),
				(int)databaseDSData.getMinimumPool() ,(int) databaseDSData.getMaximumPool());
		
		Logger.logInfo(MODULE, "Dynamic SPR Database Datasource: " + dynamicSPRDBConfiguration);

		try {
			createDataSource(dynamicSPRDBConfiguration);
			Logger.logInfo(MODULE, "Dynamic SPR Database Datasource initialized successfully");
		} catch (Exception e) {
			Logger.logError(MODULE, "Error while initializing Dynamic SPR Database Datasource. Reason: " 
					+ e.getMessage());
			Logger.logTrace(MODULE , e);
		}
	}
	
	public Connection getDynamicSPRDatabaseConnection() throws SQLException  {
		if(dynamicSPRDBConfiguration != null){
			return getConection(dynamicSPRDBConfiguration);
	}
		Logger.logError(MODULE, "Unable to get connection from Dynamic SPR Database Datasource. Reason: " 
				+ "Database Configuration not found");
		return null;
	}
	
	public void initUsageMonitringDatasurce() {
		Logger.logInfo(MODULE, "Initializing Usage Monitring Database Datasource");
		DatabaseDSData databaseDSData = WSConfig.getUsageMonitoringDatabaseData();
		if(databaseDSData == null){
			Logger.logError(MODULE, "Unable to  initialize Usage Monitring Database Datasource. " +
					"Reason: Database Configuration not found");
			return;
		}
		String driverClass = getDriverClass(databaseDSData.getConnectionUrl());
		usageMonitoringDBConfiguration = new DBConfiguration(databaseDSData.getName(),
				databaseDSData.getConnectionUrl(),driverClass, databaseDSData.getUserName(), databaseDSData.getPassword(),
				(int)databaseDSData.getMinimumPool() ,(int) databaseDSData.getMaximumPool());
		Logger.logInfo(MODULE, "Usage Monitring Database Datasource: " + usageMonitoringDBConfiguration);
		try {
			createDataSource(usageMonitoringDBConfiguration);
			Logger.logInfo(MODULE, "Usage Monitring Database Datasource initialized successfully");
		} catch (Exception e) {
			Logger.logError(MODULE, "Error while initializing Usage Monitring Database Datasource. Reason: " 
					+ e.getMessage());
			Logger.logTrace(MODULE , e);
		}
	}
	
	public Connection getUsageMonitringDBConnection() throws SQLException {

		if(usageMonitoringDBConfiguration != null){
			return getConection(usageMonitoringDBConfiguration);
	}
		Logger.logError(MODULE, "Unable to get connection from Usage Monitoring Database Datasource. Reason: " 
				+ "Database Configuration not found");
		return null;
	
	}

	private  String getDriverClass(String dbUrl) {
		if(dbUrl!=null && dbUrl.toLowerCase().contains("oracle")){
			return "oracle.jdbc.driver.OracleDriver";
		}
		return null;
	}
	
	private synchronized void createDataSource(DBConfiguration dbConfiguration){
		BasicDataSource tempDataSource = dataSourceCache.get(dbConfiguration.getName());
		if(tempDataSource != null){
			Logger.logInfo(MODULE, "Database Datasource creation skipped. " +
					"Reason: Datasource already exist for Database: " + dbConfiguration.getName());
			return;
		}
		BasicDataSource dataSource = new BasicDataSource();
		if (dbConfiguration.getConnectionUrl() == null || dbConfiguration.getConnectionUrl().isEmpty() == true){
			Logger.logError(MODULE, "Database Datasource creation failed. Reason: Connectin URL not specified");
			return;
		}else{
			dataSource.setUrl(dbConfiguration.getConnectionUrl());
		}
		if (dbConfiguration.getDriverClass() == null || dbConfiguration.getDriverClass().isEmpty() == true){
			Logger.logError(MODULE, "Database Datasource creation failed. Reason: Connectin Driver Class not specified");
			return;
		}else{
			dataSource.setDriverClassName(dbConfiguration.getDriverClass());
		}
		
		if (dbConfiguration.getUserName() == null || dbConfiguration.getUserName().isEmpty() == true){
			Logger.logError(MODULE, "Database Datasource creation failed. Reason: User Name not specified");
			return;
		}else{
			dataSource.setUsername(dbConfiguration.getUserName());
		}
		
		if (dbConfiguration.getPassword() == null || dbConfiguration.getPassword().isEmpty() == true){
			Logger.logError(MODULE, "Database Datasource creation failed. Reason: Password not specified");
			return;
		}else{
			dataSource.setPassword(dbConfiguration.getPassword());
		}
		
		if (dbConfiguration.getMinPoolSize() <= 0){
			Logger.logError(MODULE, "Considering Min Pool Size: " + 10  + " . Reason: Min Pool Size not specified");
			dataSource.setInitialSize(10);
		}else{
			dataSource.setInitialSize(dbConfiguration.getMinPoolSize());
		}
		
		if (dbConfiguration.getMaxPoolSize() <= 0){
			Logger.logError(MODULE, "Considering Max Pool Size: " + 50  + " Reason: Max Pool Size not specified");
			dataSource.setMaxActive(50);
		}else{
			dataSource.setMaxActive(dbConfiguration.getMaxPoolSize());
		}
		dataSource.setValidationQuery("SELECT 1 FROM DUAL");
		dataSource.setValidationQueryTimeout(1);
		dataSource.setTestOnReturn(true);
		Logger.logInfo(MODULE, "Database Datasource: " + dbConfiguration.getName() + " initialized successfully");
		dataSourceCache.put(dbConfiguration.getName(), dataSource);
	}
	
	public void initSubscriberProfileDatasurce() {/*
		Logger.logInfo(MODULE, "Initializing Subscriber Profile Database Datasource");
		WebServiceConfigBLManager configBLManager = new WebServiceConfigBLManager();
		WSConfigData subscriberDBConfigData;
		try {
			subscriberDBConfigData = configBLManager.getSubscriberConfiguration();
		} catch (DataManagerException e) {
			Logger.logError(MODULE, "Error while initializing Subscriber Profile Database Datasource. Reason: " + e.getMessage());
			Logger.logTrace(MODULE , e);
			return;
		}

		DatabaseDSData datasource = subscriberDBConfigData.getDatasourceConfigInstance();
		String dbUrl = datasource.getConnectionUrl();
		String dbUser = datasource.getUserName();
		String dbPassword = datasource.getPassword();
		Integer recordFetchLimit = subscriberDBConfigData.getRecordFetchLimit();
		String driverClass = getDriverClass(dbUrl);
		Logger.logInfo(MODULE, "Database Url       :"+dbUrl
				+ "\n\tDatabase Class     :"+driverClass
				+ "\n\tDatabase Username  :"+dbUser
				+ "\n\tDatabase Password  :"+dbPassword
				+ "\n\tDatabase Tablename :"+SubscriberProfileWSBLManager.DBTABLE
				+ "\n\tRecord Fetch Limit :"+recordFetchLimit);

		SubscriberProfileDBConfiguration dbConfiguration = new SubscriberProfileDBConfiguration(
				driverClass,dbUrl,dbUser,dbPassword,
				SubscriberProfileWSBLManager.DBTABLE,
				SubscriberProfileWSBLManager.USERIDFIELD,
				SubscriberProfileWSBLManager.PRIMARYKEYCOLUMN,
				SubscriberProfileWSBLManager.SEQUENCENAME);
		
		dbConfiguration.setTableName(SubscriberProfileWSBLManager.DBTABLE);
		if(recordFetchLimit!=null){
			dbConfiguration.setRecordFetchLimit(recordFetchLimit);
		}
		try {
			subscriberProfileDBConfiguration = new DBConfiguration(datasource.getName() ,
					driverClass,dbUrl,dbUser,dbPassword);
			Logger.logInfo(MODULE, "Subscriber Profile Database Configuration: " + subscriberProfileDBConfiguration);
			createDataSource(subscriberProfileDBConfiguration);
		} catch (Exception e) {
			Logger.logError(MODULE, "Error while initializing Subscriber Profile Database Datasource. Reason: " + e.getMessage());
			Logger.logTrace(MODULE , e);
		}
	*/}

	public Connection getSubscriberProfileDBConection() throws SQLException{
		if(subscriberProfileDBConfiguration != null){
			return getConection(subscriberProfileDBConfiguration);
	}
		Logger.logError(MODULE, "Unable to get connection from Subscriber Profile Database Datasource. Reason: " 
				+ "Database Configuration not found");
		return null;
	}
	
	public void closeAllDatasource(){
		for(Entry<String, BasicDataSource> basicDataSourceEntry : dataSourceCache.entrySet()){
			try {
				Logger.logInfo(MODULE, "Closing Database Datasource: " + basicDataSourceEntry.getKey());
				basicDataSourceEntry.getValue().close();
				Logger.logInfo(MODULE, "Database Datasource: " + basicDataSourceEntry.getKey() + " closed successfully");
			} catch (Exception e) {
				Logger.logError(MODULE, "Error while closing Database Datasource: " + basicDataSourceEntry.getKey() 
						+". Reason: " + e.getMessage());
				Logger.logTrace(MODULE , e);
			}
		}
	}
	
	public Connection getSessionDBConnection(String dsName){
		Logger.logDebug(MODULE, "Fetching connection for ActiveSessions ");		
		Connection connection = null;
		if( Strings.isNullOrBlank(dsName) == false ){
			try {
				connection = getConection(dsName);		
				Logger.logDebug(MODULE, "Connection Found from datasource : "+dsName);
			} catch (SQLException e) {
				Logger.logError(MODULE, "Error while getting Database Datasource Connection. Reason: " + e.getMessage());
				Logger.logTrace(MODULE, e);				
			}		
		}else{
			if(connection == null){
				Logger.logError(MODULE, "Found null connection from Datasource :  "+dsName);
				Logger.logDebug(MODULE, "Taking Connection from primary/secondary Database Datasource ");
				connection = getPrimaryOrSecondaryDSConnection();
			}	
		}
		return connection;		
	}
	
	public void initSessionLookupDataSource() {
		try {

			SessionBLManager 	sessionBLManager = new SessionBLManager();
			SessionConfData		sessionConfData  = sessionBLManager.getSessionConfData();
			if(sessionConfData==null){
				Logger.logError(MODULE, "Error while initializing Session Lookup Primary/Secondary Database Datasource. Reason: Session Configuration not found");
				return ;
			}
			DatabaseDSData 		databaseDSData 	 = sessionConfData.getDatabaseDS();
			
			if(databaseDSData != null){
				sessionPrimaryDBConfig =  new DBConfiguration(
											databaseDSData.getName(), 
											getDriverClass(databaseDSData.getConnectionUrl()),
											databaseDSData.getConnectionUrl(),
											databaseDSData.getUserName(),
											databaseDSData.getPassword()
											);
				if(dataSourceCache.get(databaseDSData.getName()) == null){					 		
					createDataSource(sessionPrimaryDBConfig);
				}
			} else {
				Logger.logError(MODULE, "Primary Datasource not configured");
				return;
			}
			
			databaseDSData 	 = sessionConfData.getSecondaryDatabaseDS();				
			if(databaseDSData != null){
				sessionSecondaryDBConfig =  new DBConfiguration(
											databaseDSData.getName(), 
											getDriverClass(databaseDSData.getConnectionUrl()),
											databaseDSData.getConnectionUrl(),
											databaseDSData.getUserName(),
											databaseDSData.getPassword()
											); 				
				if(dataSourceCache.get(databaseDSData.getName()) == null){					 		
					createDataSource(sessionSecondaryDBConfig);
				}
				
			}
			
		} catch (Exception e) {
			Logger.logError(MODULE, "Error while initializing Session Lookup Primary/Secondary Database Datasource. Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);		
		}  
	}
	
	public Connection getPrimaryOrSecondaryDSConnection() {
		Logger.logDebug(MODULE, "Called getSessionLookupConnection()");
		
		if(sessionPrimaryDBConfig == null) {
			Logger.logWarn(MODULE, "Primary Datasource not configured");
			return null;
		}
		
		Connection connection = null;
		try {
			connection = getConection(sessionPrimaryDBConfig);	
		} catch (SQLException e) {
			Logger.logError(MODULE, "Error while Fetching Primary Database Datasource Connection. Reason: " + e.getMessage());
			Logger.logTrace(MODULE, e);			
		}
		
		if(connection == null && sessionSecondaryDBConfig != null){
			Logger.logDebug(MODULE, "Primary Datasource connection not found. Looking for Secondary datasource connection.");
			try {
				connection = getConection(sessionSecondaryDBConfig);
			} catch (SQLException e) {
				Logger.logError(MODULE, "Error while Fetching Secondary Database Datasource Connection. Reason: " + e.getMessage());
				Logger.logTrace(MODULE, e);			
			}
		}
		return connection;
		
	}
}