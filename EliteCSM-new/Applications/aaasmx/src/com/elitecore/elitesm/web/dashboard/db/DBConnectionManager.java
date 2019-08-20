package com.elitecore.elitesm.web.dashboard.db;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.DatabaseConnectionException;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.BaseDBConfiguration;
import com.elitecore.elitesm.ws.logger.Logger;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

public class DBConnectionManager {
	private static final String MODULE = DBConnectionManager.class.getSimpleName(); 
	private static DBConnectionManager dbConnectionManager;
	private DBConfiguration smDBConfiguration;
	private DBConfiguration quotaMgmtConfiguration;
	
	private DBConnectionManager(){
		smDBConfiguration =getSMDBConfiguration();
		//quotaMgmtConfiguration = getQuotaMgmtDBConfiguration();
	}
	
	public static synchronized DBConnectionManager getInstance(){
		if(dbConnectionManager==null){
			dbConnectionManager =	new DBConnectionManager();
		}
		return dbConnectionManager;
	}

	private DBConfiguration getSMDBConfiguration() {

		String dbUrl=null;
		String dbUser = null;
		String dbPassword = null;


		FileInputStream fileInputStream = null;
		DBConfiguration dbConfiguration = null;
		try {

			Properties properties = new Properties();
			File dbPropsFile = new File(EliteUtility.getSMHome()+ConfigConstant.DATABASE_CONFIG_FILE_LOCATION);
			fileInputStream = new FileInputStream(dbPropsFile);
			properties.load(fileInputStream);
			dbUrl =  properties.getProperty("hibernate.connection.url");
			dbUser =  properties.getProperty("hibernate.connection.username");
			dbPassword =  properties.getProperty("hibernate.connection.password");
			String driverClass = getDriverClass(dbUrl);
			dbConfiguration =new DBConfiguration(driverClass,dbUrl,dbUser,dbPassword);
			Logger.logDebug(MODULE, "Server Manager Database Configuration :"+dbConfiguration);

		}catch (Exception e) {
			Logger.logError(MODULE,"Error while reading Server Manager Database Configuration, reason:"+e.getMessage());
			e.printStackTrace();
		}finally{
			try{
				if(fileInputStream!=null){
					fileInputStream.close();
				}
			}catch(Exception e){

			}
		}
		return dbConfiguration;
	}
	
	private DBConfiguration getQuotaMgmtDBConfiguration(){

		String dbUrl=null;
		String dbUser = null;
		String dbPassword = null;

		DBConfiguration dbConfiguration =null;
		FileInputStream fileInputStream = null;
		try {

			Properties properties = new Properties();
			File dbPropsFile = new File(EliteUtility.getSMHome()+ConfigConstant.DATABASE_CONFIG_FILE_LOCATION);
			fileInputStream = new FileInputStream(dbPropsFile);
			properties.load(fileInputStream);
			dbUrl =  properties.getProperty("quotamgmt.connection.url");
			dbUser =  properties.getProperty("quotamgmt.connection.username");
			dbPassword =  properties.getProperty("quotamgmt.connection.password");

			String driverClass = getDriverClass(dbUrl);
			dbConfiguration = new DBConfiguration(driverClass,dbUrl,dbUser,dbPassword);
			Logger.logDebug(MODULE, "Quota Management DB Configuration :"+dbConfiguration);
			
		}catch (Exception e) {
			Logger.logError(MODULE,"Error while reading Quota Management DB Configuration, reason:"+e.getMessage());
			e.printStackTrace();
		}finally{
			try{
				if(fileInputStream!=null){
					fileInputStream.close();
				}
			}catch(Exception e){

			}
		}
		return dbConfiguration;
	}

	private  String getDriverClass(String dbUrl) {
		if(dbUrl!=null && dbUrl.toLowerCase().contains("oracle")){
			return "oracle.jdbc.driver.OracleDriver";
		}
		return null;
	}
	
	public Connection getSMDatabaseConection() throws DatabaseConnectionException{
		return getDBConnection(smDBConfiguration);
		
	}
	
	public Connection getQuotaMgmtDatabaseConnection() throws DatabaseConnectionException{
		return getDBConnection(quotaMgmtConfiguration);
	}
	
	public Connection getWebServiceDatabaseConnection(BaseDBConfiguration baseDBConfiguration) throws com.elitecore.elitesm.ws.exception.DatabaseConnectionException {
		return getWebServiceDBConnection(baseDBConfiguration);
	}
	
	private Connection getWebServiceDBConnection(BaseDBConfiguration dbConfiguration) throws com.elitecore.elitesm.ws.exception.DatabaseConnectionException {
		
		if(dbConfiguration!=null){
			
			try{
				Class.forName(dbConfiguration.getDriverClass()).newInstance();
				return DriverManager.getConnection(dbConfiguration.getConnectionUrl(), dbConfiguration.getUserName(), decryptPassword(dbConfiguration.getPassword()));
			}catch(ClassNotFoundException e){
				throw new com.elitecore.elitesm.ws.exception.DatabaseConnectionException("Driver class is not found["+dbConfiguration.getDriverClass()+"]", e);
			} catch (InstantiationException e) {
				throw new com.elitecore.elitesm.ws.exception.DatabaseConnectionException("Error while instantiation of driver class ["+dbConfiguration.getDriverClass()+"]", e);
			} catch (IllegalAccessException e) {
				throw new com.elitecore.elitesm.ws.exception.DatabaseConnectionException("Error while getting connection,"+e.getMessage(), e);
			} catch (SQLException e) {
				throw new com.elitecore.elitesm.ws.exception.DatabaseConnectionException("Error while getting connection,"+e.getMessage(), e);
			} catch (NoSuchEncryptionException e) {
				throw new com.elitecore.elitesm.ws.exception.DatabaseConnectionException("No such encryption exist during decrypting password, Reason : "+e.getMessage(), e);
			} catch (DecryptionNotSupportedException e) {
				throw new com.elitecore.elitesm.ws.exception.DatabaseConnectionException("Unsupported decryption during decrypting password, Reason : "+e.getMessage(), e);
			} catch (DecryptionFailedException e) {
				throw new com.elitecore.elitesm.ws.exception.DatabaseConnectionException("Decryption failed, Reason : "+e.getMessage(), e);
			}
		}else{
			throw new com.elitecore.elitesm.ws.exception.DatabaseConnectionException("Database Configuration is not found");
		}
		
	}

	private String decryptPassword(String password) throws DecryptionNotSupportedException, DecryptionFailedException, NoSuchEncryptionException{
		return PasswordEncryption.getInstance().decrypt(password,PasswordEncryption.ELITE_PASSWORD_CRYPT);
	}

	private  Connection getDBConnection(DBConfiguration dbConfiguration) throws DatabaseConnectionException{
		if(dbConfiguration!=null){
			try{
				Class.forName(dbConfiguration.getDriverClass()).newInstance();
				return DriverManager.getConnection(dbConfiguration.getConnectionUrl(), dbConfiguration.getUserName(), dbConfiguration.getPassword());
			}catch(ClassNotFoundException e){
				throw new DatabaseConnectionException("Driver class is not found["+dbConfiguration.getDriverClass()+"]",e);
			} catch (InstantiationException e) {
				throw new DatabaseConnectionException("Error while instantiation of driver class ["+dbConfiguration.getDriverClass()+"]",e);
			} catch (IllegalAccessException e) {
				throw new DatabaseConnectionException("Error while getting connection,"+e.getMessage(),e);
			} catch (SQLException e) {
				throw new DatabaseConnectionException("Error while getting connection,"+e.getMessage(),e);
			}
		}else{
			throw new DatabaseConnectionException("Database Configuration is not found");
		}
	}
}
