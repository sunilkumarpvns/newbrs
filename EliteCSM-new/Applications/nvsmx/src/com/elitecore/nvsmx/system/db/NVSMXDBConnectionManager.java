package com.elitecore.nvsmx.system.db;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.kpi.exception.InitializationFailedException;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.scheduler.EliteScheduler;
import com.elitecore.nvsmx.system.util.PasswordUtility;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory;

import javax.naming.NamingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * NVSMXDBConnectionManager maintains NVSMX default datasource connection
 *
 * @author Chetan.Sankhala
 */
public class NVSMXDBConnectionManager {

	private static final String MODULE = "NVSMX-DB-CONN-MGR";
	private static final String DEFAULT_DS_NAME = "NetvertexNVSMXDB";
	private static final String PROPERTY_ENCRYPTED_PASSWORD = "encryptedPassword";
	private static final String PROPERTY_PASSWORD = "password";
	private DBDataSource dataSource;
	private BasicDataSource basicDataSource;

	private static NVSMXDBConnectionManager manager;

	static {
		manager = new NVSMXDBConnectionManager();
	}

	public void init(String deploymentPath) throws InitializationFailedException {
		if(getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Initializing NVSMX DB Connection Manager");
		}

		try {

			File file = new File(deploymentPath+ File.separator + NVSMXCommonConstants.DATABASE_CONFIG_FILE_LOCATION);
			Properties databaseProperties = new Properties();
			databaseProperties.load(new FileInputStream(file));

			String passwordProperty = databaseProperties.getProperty(PROPERTY_PASSWORD);
			String encryptedPasswordProperty= databaseProperties.getProperty(PROPERTY_ENCRYPTED_PASSWORD);

			if(Strings.isNullOrBlank(encryptedPasswordProperty)) {
				//Set default false if encryptedPassword property not define in database.properties
				encryptedPasswordProperty = CommonStatusValues.DISABLE.getStringNameBoolean();
			} else if(Strings.isNullOrBlank(passwordProperty) == false && CommonStatusValues.ENABLE.getStringNameBoolean().equals(encryptedPasswordProperty)) {
				databaseProperties.put(PROPERTY_PASSWORD, PasswordUtility.getDecryptedPassword(passwordProperty));
			}

			this.basicDataSource = BasicDataSourceFactory.createDataSource(databaseProperties);

			if(Strings.isNullOrBlank(passwordProperty) == false && CommonStatusValues.DISABLE.getStringNameBoolean().equals(encryptedPasswordProperty)) {
				databaseProperties.put(PROPERTY_ENCRYPTED_PASSWORD,CommonStatusValues.ENABLE.getStringNameBoolean());
				databaseProperties.put(PROPERTY_PASSWORD,PasswordUtility.getEncryptedPassword(passwordProperty));
				databaseProperties.store(new FileOutputStream(file),"");
			}

			String connectionUrl = this.basicDataSource.getUrl();
			String userName = this.basicDataSource.getUsername();
			String password = this.basicDataSource.getPassword();
			this.dataSource = new DBDatasourceImpl(DEFAULT_DS_NAME, connectionUrl, userName, password);
			DBConnectionManager.getInstance(DEFAULT_DS_NAME).init(dataSource, EliteScheduler.getInstance().TASK_SCHEDULER);

			if(getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Initializing NVSMX DB Connection Manager completed");
			}


		} catch (NamingException | DatabaseTypeNotSupportedException e) {
			throw new InitializationFailedException(e);
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while initializing NVSMX DB Connection Manager. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
		}
	}

	public DBDataSource getDataSource(){
		return dataSource;
	}

	public static NVSMXDBConnectionManager getInstance(){
		return manager;
	}

	public Connection getConnection() throws DataSourceException {
		return DBConnectionManager.getInstance(DEFAULT_DS_NAME).getConnection();
	}

	/**
	 * Established the connection using defined DB parameters
	 * @param connectionUrl
	 * @param dbUsername
	 * @param dbUserPassword
	 * @param driverClass
	 * @return Connection
	 * @throws SQLException
	 */
	public Connection getDBConnection(String connectionUrl,String dbUsername, String dbUserPassword, String driverClass) throws SQLException{
		try {
			Class.forName(driverClass);
		} catch (ClassNotFoundException e) {
			getLogger().error(MODULE,"jdbc driver class not found, Reason: "+e.getMessage());
			throw new SQLException(e.getMessage(),e);
		}
		return DriverManager.getConnection(connectionUrl, dbUsername, dbUserPassword);
	}

	public TransactionFactory getTransactionFactory(){
		return DBConnectionManager.getInstance(DEFAULT_DS_NAME).getTransactionFactory();
	}

	public boolean isDBDownSQLException(SQLException ex){
		return DBConnectionManager.getInstance(DEFAULT_DS_NAME).isDBDownSQLException(ex);
	}

	public BasicDataSource getBasicDataSource() {
		return basicDataSource;
	}
}
