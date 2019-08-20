package com.elitecore.core.commons.util.db;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.util.db.datasource.IConnectionDataSource;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.systemx.esix.statistics.ESIStatistics;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class DBConnectionManager{

	private static final int COUNTER_VALUE_FOR_UNINITIALIZED_DS = -1;

	private static final String MODULE = "DB-CONN-MNGR";
	private static final String DEAD = "DEAD";
	private static final String ALIVE = "ALIVE";
	private static final String NOT_INITIALIZED = "NOT_INITIALIZED";
	
	private static Map <String, DBConnectionManager> connectionManagers = new HashMap<String, DBConnectionManager>();

	private IConnectionDataSource connectionDataSource;
	private boolean isInitCalled = false;
	private String cacheName;
	private DBDataSource dbDataSource;
	
	private TransactionFactory transactionFactory;
	
	DBConnectionManager(String cacheName) {
		if (cacheName != null) {
			this.cacheName = cacheName;
		} else {
			cacheName = "Elite Default Cache";
		}
	}

	public static DBConnectionManager getInstance(String dataSourceKey){

		DBConnectionManager managerInstance = connectionManagers.get(dataSourceKey);
	
		if (managerInstance == null) {
			synchronized (DBConnectionManager.class) {
				//Following check is required, do not remove it.
				managerInstance = connectionManagers.get(dataSourceKey);
				if (managerInstance == null) {
					managerInstance = new DBConnectionManager(dataSourceKey);
					connectionManagers.put(dataSourceKey, managerInstance);
				}
			}
		}
		return managerInstance;
	}
	
	/**
	 *
	 * @param DBDataSource the data source which contains parameters require to initialize the  database connection (url, username, password, maximum pool size, minimum pool size)
	 * @param TaskScheduler
	 * @throws DatabaseInitializationException - if data source initialization failed due to any reason
	 * @throws DatabaseTypeNotSupportedException - if url in DBDataSource does not contain the known database. Known databases are Oracle, PostgreSQL, MySQL and TimesTen. 
	 */
	public synchronized void init(DBDataSource dataSource,TaskScheduler taskScheduler)
			throws DatabaseInitializationException, DatabaseTypeNotSupportedException {
		if (this.isInitCalled) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Datasource: " + dataSource.getDataSourceName() + " is already initialized.");
			}
			return;
		}
		this.isInitCalled = true;
		
		this.dbDataSource = dataSource;
		
		if(taskScheduler != null){
			if(dbDataSource.getStatusCheckDuration() > 0){
				transactionFactory = new TransactionFactory(dataSource, taskScheduler,dbDataSource.getStatusCheckDuration());
				
			} else{
				transactionFactory = new TransactionFactory(dataSource, taskScheduler, ESCommunicator.NO_SCANNER_THREAD, DBConnectionManager.this);
			}
		}
		
		try {
			if(transactionFactory != null){
				transactionFactory.init();
			}
			
			init();
			
		} catch(DatabaseTypeNotSupportedException e){
			if(transactionFactory != null){
				transactionFactory.markDead();
			}
			throw e;
		}catch (Exception e) {
			this.isInitCalled = false;
			if(transactionFactory != null){
				transactionFactory.markDead();
			}
			throw new DatabaseInitializationException(dataSource.getDataSourceName() + " initialization failed, Reason: "+ e.getMessage() ,e);
		}
		
	}
	
	public synchronized void reInit() throws DatabaseInitializationException{
		
		if(isInitCalled == false){
			throw new DatabaseInitializationException("DataSource is never initialized");
		}
		
		/*
		 * 
		 * In case of DatabaseTypeNotSupportedException connectionDataSource will be null
		 * 
		 */
		try {
			if (connectionDataSource != null) {
				close();
				connectionDataSource.init();
			} else {
				init();				
			}
		} catch(Exception e) {
			throw new DatabaseInitializationException(dbDataSource.getDataSourceName() + " initialization failed. Reason: "+ e.getMessage() ,e);
		}
	}

	public void close() {
		try {
            connectionDataSource.close();
        } catch (SQLException ex) {
            LogManager.getLogger().trace(MODULE, ex);
            LogManager.getLogger().error(MODULE, "Error while closing data source for DB "+ DBConnectionManager.this.cacheName);
        }
	}

	private void init() throws DatabaseTypeNotSupportedException, DataSourceException{
		String url = dbDataSource.getConnectionURL();
		String username = dbDataSource.getUsername();
		String password = dbDataSource.getPlainTextPassword();
		int networkReadTimeout = dbDataSource.getNetworkReadTimeout();
		int minPoolSize = dbDataSource.getMinimumPoolSize();
		int maxPoolSize = dbDataSource.getMaximumPoolSize();
		
		Map<EliteDBConnectionProperty, String> properties = new EnumMap<>(EliteDBConnectionProperty.class);
		
		if(networkReadTimeout > 0){	
			if(networkReadTimeout < 1000){
				LogManager.getLogger().warn(MODULE, "Consider: "+EliteDBConnectionProperty.NETWORK_READ_TIMEOUT.defaultValue +" ms as default network-read-timeout for datasouce: "+dbDataSource.getDataSourceName()+". Reason: network-read-timeout value must be greater than 1000 ms");
				properties.put(EliteDBConnectionProperty.NETWORK_READ_TIMEOUT, String.valueOf(EliteDBConnectionProperty.NETWORK_READ_TIMEOUT.defaultValue));
			}else{
				properties.put(EliteDBConnectionProperty.NETWORK_READ_TIMEOUT, String.valueOf(networkReadTimeout));
			}
		}else{
			LogManager.getLogger().warn(MODULE, "Ignore the network-read-timeout property for datasource: "+dbDataSource.getDataSourceName()+". Reason: Invalid network-read-timeout value: "+networkReadTimeout);
		}
		
		LogManager.getLogger().info(MODULE, "Initializing database connection");
		LogManager.getLogger().info(MODULE, "Connection URL      : " + url);
		LogManager.getLogger().info(MODULE, "Username            : " + username);
		LogManager.getLogger().info(MODULE, "Network Read Timeout (ms) : " + networkReadTimeout);
		LogManager.getLogger().info(MODULE, "Minimum Connections : " + minPoolSize);
		LogManager.getLogger().info(MODULE, "Maximum Connections : " + maxPoolSize);

		connectionDataSource = new DBConnectionFactory().createDataBaseConnection(
				url, username, password, minPoolSize, maxPoolSize, properties);
		try{
		connectionDataSource.init();
		} catch (DataSourceException e) {
			throw e;
		}
		LogManager.getLogger().info(MODULE, "Database connection established successfully");
	}
	
	/**
	 * 
	 * 
	 * @return Connection  
	 * @throws DataSourceNotInitializedException -> if init() method for the data source is not called
	 * @throws DataSourceException -> if issues occurs while connection establishment such as connection is unavailable or data source is down 
	 */
	public Connection getConnection() throws DataSourceException {
		if(isInitCalled == false){
			throw new DataSourceNotInitializedException("DB connection manager has not been initialized for datasource: " + this.dbDataSource.getDataSourceName());
		}
		try {
			return this.connectionDataSource.getConnection();
		} catch (DataSourceException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Error while fetching connection object from DB "+ getCacheName() + ", Reason: "+ e.getMessage());
			}
			LogManager.getLogger().trace(MODULE, e);
			
			if(isDBDownSQLException(e)){
				if(transactionFactory != null){
					transactionFactory.markDead();
				}
			}
			throw e;
		}
	}
	
	public TransactionFactory getTransactionFactory(){
		return transactionFactory;
	}
	
	public int getNumberOfActiveConnections() {
		if(this.isInitCalled == false){
			return COUNTER_VALUE_FOR_UNINITIALIZED_DS;
		}
		return this.connectionDataSource.getNumberOfActiveConnections();
	}

	public String getGraphData(String dataSource) {
	    String data = "";   

	   	data = (System.currentTimeMillis() / 1000) + "," + (connectionManagers.get(dataSource).connectionDataSource.getNumberOfActiveConnections()) 
	    			+ "," + connectionManagers.get(dataSource).connectionDataSource.getPoolSize() + "\n";   			
		return data;
	}

	public static List<String> getDataSources() {

		Iterator<String> iterator = connectionManagers.keySet().iterator();
		List<String> datasources = new ArrayList<String>(); 
		while(iterator.hasNext()){
			DBConnectionManager connectionManager = connectionManagers.get(iterator.next());
			datasources.add(connectionManager.cacheName);
		}
		return datasources;
	}
	
	/**
	 * Checks dataSourceName is initialized in DBConnectionManager or not
	 * 
	 * @param dataSourceName
	 * @return
	 */
	public static boolean isExist(String dataSourceName) {
		return connectionManagers.keySet().contains(dataSourceName);
	}

	public String toString() {
		if(isInitCalled == false){
			return cacheName + " " +"Connection URL : " + dbDataSource.getConnectionURL();
		}
		return cacheName + " " + connectionDataSource;
	}
	
	public boolean isInitilized(){
		return isInitCalled;
	}
	
	public String getCacheName() {
		return cacheName;
	}
	
	public ESIStatistics getStatistics(){
		if(transactionFactory != null){
			return transactionFactory.getStatistics();
		}
		return null;
	}
	
	public void scan() throws Exception{
		if(transactionFactory != null){
			transactionFactory.scan();
		}else {
			reInit();
		}
	}
	
	public boolean isDBDownSQLException(SQLException ex){
		if(isInitCalled == false){
			return false;
		}
		return connectionDataSource.isDBDownSQLException(ex);
	}
	
	public String getValidationQuery() {
		return connectionDataSource.getVendor().validationQuery;
	}
	
	/**
	 * @return if the datasource represented using this manager is alive or dead.
	 * Use {@link #init(DBDataSource, TaskScheduler)} instead.
	 */
	public boolean isAlive() {
		if (transactionFactory == null) {
			throw new UnsupportedOperationException("isAlive() is not supported if taskScheduler is not provided while initializing the manager.");
		}
		return transactionFactory.isAlive();
	}

	public String checkDSStatus() {

		if(isInitCalled == false){
			return NOT_INITIALIZED;
		}


		return isAlive() ? ALIVE : DEAD;

	}

	public DBVendors getVendor() {
		return connectionDataSource.getVendor();
	}
}