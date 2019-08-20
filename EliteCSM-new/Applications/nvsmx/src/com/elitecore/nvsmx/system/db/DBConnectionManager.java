package com.elitecore.nvsmx.system.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.kpi.exception.RegistrationFailedException;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.db.DBVendors;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.EliteDBConnectionProperty;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.nvsmx.system.scheduler.EliteScheduler;

public class DBConnectionManager {

	private static final String MODULE = "DB-CONN-MGR";
	private static final boolean DEFAULT_AUTO_COMMIT = false;
	private static final short REMOVE_ABANDONED_TIMEOUT_IN_SECS = (short) TimeUnit.MINUTES.toSeconds(1);
	private static final short MAX_OPEN_PREPARED_STATEMENTS = 500;
	private static final long MIN_EVICTABLE_TIME_IN_MS = TimeUnit.MINUTES.toMillis(60);
	private static final long TIME_BETWEEN_EVICTION_RUNS_IN_MS = TimeUnit.MINUTES.toMillis(60);
	private static final byte VALIDATION_QUERY_TIMEOUT_IN_SECS = 1;
	private static final byte MAX_WAIT_TIME_IN_SECS = 1;
	public static String defaultDatasource = "DefaultDatabaseDatasource";

	private String datasourceName;
	private DBDataSource dbDataSource;
	private BasicDataSource basicDataSource;
	private TransactionFactoryImpl transactionFactory;
	private DBVendors vendor;
	private static Map <String, DBConnectionManager> connectionManagers = new HashMap<String, DBConnectionManager>();


	private DBConnectionManager(DBDataSource dataSource) {
		this.dbDataSource = dataSource;
		this.datasourceName = dbDataSource.getDataSourceName();
	}


	public static @Nullable DBConnectionManager getInstance(String dataSourceName){
		return connectionManagers.get(dataSourceName);
	}

	public Connection getConnection() throws SQLException{
		Connection connection;
		try {
			connection = basicDataSource.getConnection();
		} catch(SQLException e) {
			if (isDBDownSQLException(e)) {
				transactionFactory.markDead();
			}
			throw e;

		}
		return connection;

	}

	public TransactionFactory getTransactionFactory() {
		return transactionFactory;
	}

	public synchronized static void registerDataSource(DBDataSource dbDatasource)
			throws RegistrationFailedException{
		String datasourceName = dbDatasource.getDataSourceName();
		defaultDatasource = datasourceName;
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Registering Database Datasource with name: " + datasourceName);
		}

		if(connectionManagers.containsKey(datasourceName)){
			getLogger().error(MODULE, "Database Datasource registration skipped. "
					+ "Reason: Datasource already registered with name: " +  datasourceName);
			return;
		}

		DBConnectionManager manager = new DBConnectionManager(dbDatasource);

		try {
			manager.init();
			connectionManagers.put(datasourceName, manager);

			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE,"Database Datasource: " + datasourceName + " registered successfully");
			}

		} catch (DatabaseInitializationException e) {
			throw new RegistrationFailedException("Failed to register Database Datasource with name : " + datasourceName + "Reason: " + e.getMessage(), e);
		}

	}

	private void init() throws DatabaseInitializationException{
		createDatasource();
		TaskScheduler taskScheduler = EliteScheduler.getInstance().TASK_SCHEDULER;
		TransactionFactoryImpl transactionFactory;
		if (dbDataSource.getStatusCheckDuration() != 0) {
			transactionFactory = new TransactionFactoryImpl(taskScheduler, datasourceName, dbDataSource.getStatusCheckDuration());
		} else {
			transactionFactory = new TransactionFactoryImpl(taskScheduler, datasourceName);
		}

		this.transactionFactory = transactionFactory;
		try {
			transactionFactory.init();
		} catch (InitializationFailedException e) {
			transactionFactory.markDead();
			getLogger().error(MODULE, "Error in initializing transaction factory for Database Datasource " + datasourceName + ". Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}

	}


	private void createDatasource() throws DatabaseInitializationException {

		String url = dbDataSource.getConnectionURL().toLowerCase();
		if(Strings.isNullOrBlank(url)) {
			throw new DatabaseInitializationException("Database Datasource with name: "+ datasourceName +" cannot be initialized. Reason: Connection URL missing");
		}

		int minPoolSize = dbDataSource.getMinimumPoolSize();
		int maxPoolSize = dbDataSource.getMaximumPoolSize();

		String driverClassName;
		String validationQuery;
		String networkReadTimeoutProperty;

		if (url.contains(DBVendors.ORACLE.name().toLowerCase())) {
			this.vendor = DBVendors.ORACLE;
			driverClassName = DBVendors.ORACLE.driverClassName;
			validationQuery = DBVendors.ORACLE.validationQuery;
			networkReadTimeoutProperty = DBVendors.ORACLE.getVendorConnectionProperty(EliteDBConnectionProperty.NETWORK_READ_TIMEOUT);
		} else if (url.contains(DBVendors.POSTGRESQL.name().toLowerCase())) {
			driverClassName = DBVendors.POSTGRESQL.driverClassName;
			validationQuery = DBVendors.POSTGRESQL.validationQuery;
			networkReadTimeoutProperty = DBVendors.POSTGRESQL.getVendorConnectionProperty(EliteDBConnectionProperty.NETWORK_READ_TIMEOUT);
		} else if (url.contains(DBVendors.MYSQL.name().toLowerCase())) {
			driverClassName = DBVendors.MYSQL.driverClassName;
			validationQuery = DBVendors.MYSQL.validationQuery;
			networkReadTimeoutProperty = DBVendors.MYSQL.getVendorConnectionProperty(EliteDBConnectionProperty.NETWORK_READ_TIMEOUT);
		} else {
			throw new DatabaseInitializationException("Datasource cannot be initialized. Reason: Requested Database URL " + url + " is not supported");
		}

		BasicDataSource tempDataSource = new BasicDataSource(); //NOSONAR
		tempDataSource.setUrl(dbDataSource.getConnectionURL());
		tempDataSource.setDriverClassName(driverClassName);
		tempDataSource.setUsername(dbDataSource.getUsername());
		tempDataSource.setPassword(dbDataSource.getPassword());
		tempDataSource.setValidationQuery(validationQuery);

		tempDataSource.setDefaultAutoCommit(DEFAULT_AUTO_COMMIT);
		tempDataSource.setInitialSize(minPoolSize);
		tempDataSource.setMaxTotal(maxPoolSize);
		tempDataSource.setMaxIdle(maxPoolSize);
		tempDataSource.setMinIdle(minPoolSize);
		tempDataSource.setMaxWaitMillis(TimeUnit.SECONDS.toMillis(MAX_WAIT_TIME_IN_SECS));

		tempDataSource.setValidationQueryTimeout(VALIDATION_QUERY_TIMEOUT_IN_SECS);
		tempDataSource.setTestOnBorrow(false);
		tempDataSource.setTestOnReturn(false);
		tempDataSource.setTestWhileIdle(true);
		tempDataSource.setTimeBetweenEvictionRunsMillis(TIME_BETWEEN_EVICTION_RUNS_IN_MS);
		tempDataSource.setNumTestsPerEvictionRun(minPoolSize);
		tempDataSource.setMinEvictableIdleTimeMillis(MIN_EVICTABLE_TIME_IN_MS);

		tempDataSource.setPoolPreparedStatements(true);
		tempDataSource.setMaxOpenPreparedStatements(MAX_OPEN_PREPARED_STATEMENTS);

		tempDataSource.setRemoveAbandonedOnMaintenance(true);
		tempDataSource.setRemoveAbandonedTimeout(REMOVE_ABANDONED_TIMEOUT_IN_SECS);
		tempDataSource.setLogAbandoned(true);


		int networkReadTimeout = dbDataSource.getNetworkReadTimeout();
		if(networkReadTimeout > 0){
			if(networkReadTimeout < 1000){
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Consider: "+EliteDBConnectionProperty.NETWORK_READ_TIMEOUT.defaultValue +" ms as default network-read-timeout for datasouce: "+ datasourceName  +". Reason: network-read-timeout value must be greater than 1000 ms");
				}
				tempDataSource.addConnectionProperty(networkReadTimeoutProperty, String.valueOf(EliteDBConnectionProperty.NETWORK_READ_TIMEOUT.defaultValue));
			}else{
				tempDataSource.addConnectionProperty(networkReadTimeoutProperty, String.valueOf(networkReadTimeout));
			}
		}else{
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Ignore the network-read-timeout property for datasource: "+datasourceName+". Reason: Invalid network-read-timeout value: "+networkReadTimeout);
			}

		}

		Connection connection = null;
		try {
			connection = tempDataSource.getConnection();
		} catch (SQLException e) {
			getLogger().error( MODULE, "Unable to initialize Database Datasource with name: " + datasourceName + ". Reason: "	+ e.getMessage());
			getLogger().trace(e);
		} finally {
			DBUtility.closeQuietly(connection);
		}

		this.basicDataSource = tempDataSource;
	}

	private static ILogger getLogger() {
		return LogManager.getLogger();
	}

	public boolean isDBDownSQLException(SQLException e) {
		return this.vendor.isDBDownSQLException(e);
	}


	public DBVendors getVendor() {
		return vendor;
	}

}
