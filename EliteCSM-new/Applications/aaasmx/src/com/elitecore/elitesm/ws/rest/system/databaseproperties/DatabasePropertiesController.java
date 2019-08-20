package com.elitecore.elitesm.ws.rest.system.databaseproperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import net.sf.json.JSONArray;

import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.system.databaseproperties.DatabasePropertiesBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.system.databaseproperties.DatabaseProperties;
import com.elitecore.elitesm.hibernate.core.system.util.HibernateSessionFactory;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.systemstartup.dbsetup.controller.EliteAAAStartupDBSetupAction;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

public class DatabasePropertiesController {
	
	private static final String MODULE = "Database Properties";

	private static final String SERVER_HOME = System.getenv("SM_HOME");
	private static String dbPropsFilePhysicalLocation=  System.getenv("SM_HOME") + "/database.properties";
	private static String dbPropsFileLocation = EliteUtility.getSMHome() + File.separator +ConfigConstant.DATABASE_CONFIG_FILE_LOCATION;

	@GET
	public Response getByNameFromQuery() throws DataManagerException {
		
		DatabaseProperties databaseProperties = new DatabaseProperties();
		
		/* read file from SM_HOME environment path */
		File dbProps = new File(dbPropsFilePhysicalLocation);
		if( dbProps.exists()) {
			
			try {
				convertPropertiesToBean(dbPropsFilePhysicalLocation, databaseProperties);
			} catch (Exception e) {
				throw new DataManagerException("Failed to read database.properties, Reason: "+e.getMessage());
			}
			
		} else {
			/* read file from aaasmx/WEB-INF/database.properties */
			File dbPropsFile = new File(dbPropsFileLocation);
			
			if (dbPropsFile.exists()) {
				try {
					convertPropertiesToBean(dbPropsFileLocation, databaseProperties);
				} catch (Exception e) {
					throw new DataManagerException("Failed to read database.properties, Reason: "+e.getMessage());
				}
			} 
		}
		
		return Response.ok(databaseProperties).build();
	}

	@GET
	@Path("/")
	public Response getByNameFromPath() throws DataManagerException {
		return getByNameFromQuery();
	}

	@PUT
	public Response updateByQueryParam(@Valid DatabaseProperties databaseProperties) 
			throws DataManagerException, NoSuchEncryptionException, EncryptionFailedException{
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DatabaseProperties databasePropertiesOld = new DatabaseProperties(); 
		DatabasePropertiesBLManager databasePropertiesBLManager = new  DatabasePropertiesBLManager();
		
		Connection dbConnection = null;
		String driverClass = ConfigManager.getDriverClass(databaseProperties.getConnectionUrl());
		try {
			dbConnection = EliteAAAStartupDBSetupAction.getDBConnection(databaseProperties.getConnectionUrl(), databaseProperties.getDbUsername(), databaseProperties.getDbPassword(), driverClass);
			dbConnection.setAutoCommit(false);
		} catch (SQLException e) {
			return Response.ok(RestUtitlity.getResponse("Failed to established connection, Reason: " + e.getMessage())).build();
		}
		
		/* read file from SM_HOME environment path */
		File dbProps = new File(dbPropsFilePhysicalLocation);
		if( dbProps.exists()) {
			try {
				convertPropertiesToBean(dbPropsFilePhysicalLocation, databasePropertiesOld);
			} catch (Exception e) {
				return Response.ok(RestUtitlity.getResponse("Failed to update database.properties, Reason: " + e.getMessage())).build();
			}
			
		} else {
			/* read file from aaasmx/WEB-INF/database.properties */
			File dbPropsFile = new File(dbPropsFileLocation);
			
			if (dbPropsFile.exists()) {
				try {
					convertPropertiesToBean(dbPropsFileLocation, databasePropertiesOld);
				} catch (Exception e) {
					return Response.ok(RestUtitlity.getResponse("Failed to update database.properties, Reason: " + e.getMessage())).build();
				}
			} 
		}
		
		/*DatabaseProperties databasePropertiesData = convertFormToBean(databasePropertiesForm);*/
		DatabaseProperties databasePropertiesData = null;
		try {
			databasePropertiesData = updateDatabaseProperties(databaseProperties);
		} catch (IOException e) {
			return Response.ok(RestUtitlity.getResponse("Failed to update database.properties, Reason: " + e.getMessage())).build();
		}
		JSONArray jsonArray = ObjectDiffer.diff(databasePropertiesOld, databasePropertiesData);
		
		databasePropertiesBLManager.doAudit(jsonArray.toString(), staffData);

		return Response.ok(RestUtitlity.getResponse("Database Properties file updated successfully")).build();
	}
	
	@PUT
	@Path("/")
	public Response updateByPathParam(@Valid DatabaseProperties databaseProperties,
			@PathParam(value = "name") String name) 
			throws DataManagerException, NoSuchEncryptionException, EncryptionFailedException {
		return updateByQueryParam(databaseProperties);
	}
	
	@GET
	@Path("/help")
	public Response getHelp() throws DataManagerException, FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.DATABASE_PROPERTIES);
	}
	
	private DatabaseProperties updateDatabaseProperties(DatabaseProperties databaseProperties) throws IOException{
		
		FileOutputStream dbPropertieWriter = null; 
		FileOutputStream dbPropertieWriterAtSMPhysicalDirectory = null ; 
		FileOutputStream dbPropertieWriterAtTomcatWorkingDirectory = null ;
		Properties dbProperties = new Properties();
		
		try {
			// encrypt password
			String encryptedPassword = "";
			String plainPassoword = databaseProperties.getDbPassword();
			if (Strings.isNullOrBlank(plainPassoword) == false) {
				encryptedPassword = PasswordEncryption.getInstance().crypt(plainPassoword,PasswordEncryption.ELITE_PASSWORD_CRYPT);
			}
			
			if( Strings.isNullOrEmpty(databaseProperties.getConnectionUrl()) == false ){
				if(EliteUtility.getLowerCaseString(databaseProperties.getConnectionUrl()).contains("postgresql")){
					databaseProperties.setDialect("org.hibernate.dialect.PostgreSQLDialect");
				}else if(EliteUtility.getLowerCaseString(databaseProperties.getConnectionUrl()).contains("oracle")){
					databaseProperties.setDialect("org.hibernate.dialect.Oracle9Dialect");
				}
				databaseProperties.setDriverClass(ConfigManager.getDriverClass(databaseProperties.getConnectionUrl()));
			}
			
			/** store Driver class by which JDBC connection configured **/
			dbProperties.setProperty("hibernate.connection.url", databaseProperties.getConnectionUrl());
			dbProperties.setProperty("hibernate.connection.username", databaseProperties.getDbUsername());
			dbProperties.setProperty("hibernate.connection.password", encryptedPassword);
			dbProperties.setProperty("hibernate.connection.driver_class", databaseProperties.getDriverClass());
			dbProperties.setProperty("hibernate.dialect", databaseProperties.getDialect());
			dbProperties.setProperty("hibernate.show_sql", databaseProperties.getShowSQL());
			dbProperties.setProperty("hibernate.format_sql", databaseProperties.getFormatSQL());
			
			if(Strings.isNullOrBlank(SERVER_HOME) == false){
				File dbPropsFile = new File(dbPropsFilePhysicalLocation);
				if(dbPropsFile.exists() == false){
					/** create database properties if not exist */
					Logger.logInfo(MODULE,"database.properties file does not exist, creating new database.properties file");
					dbPropsFile.createNewFile();
					
					dbPropertieWriter = new FileOutputStream(dbPropsFile,false);
					/** write database properties file at physical location for development */
					dbProperties.store(dbPropertieWriter,"#Database Connection Properties");
				}else{
					dbPropertieWriterAtSMPhysicalDirectory = new FileOutputStream(dbPropsFile,false);
					dbProperties.store(dbPropertieWriterAtSMPhysicalDirectory,"#Database Connection Properties");
					Logger.logDebug(MODULE,"Updated database.properties file successfully in physical path.");
				}
			}
			
			File dbPropsFileInTomcat = new File(dbPropsFileLocation);
		
			if(dbPropsFileInTomcat.exists() == false){
				Logger.logInfo(MODULE,"database.properties file does not exist, creating new database.properties file in tomcat working directory");
				dbPropsFileInTomcat.createNewFile();
				dbPropertieWriterAtTomcatWorkingDirectory = new FileOutputStream(dbPropsFileInTomcat, false);
				/** write database.properties file at tomcat working location */
				dbProperties.store(dbPropertieWriterAtTomcatWorkingDirectory,"#Database Connection Properties");
			}else{
				/** write database.properties file at tomcat working location */
				dbPropertieWriterAtTomcatWorkingDirectory = new FileOutputStream(dbPropsFileInTomcat, false);
				dbProperties.store(dbPropertieWriterAtTomcatWorkingDirectory,"#Database Connection Properties");
				Logger.logDebug(MODULE,"Updated database.properties file successfully in tomcat working directory.");
			}

			
			/** after changes we need to reIntialize hibernate session factory no need to restart tomcat */
			HibernateSessionFactory.closeSessionFactory();
			HibernateSessionFactory.reIntializeSessionFactory();
			
			ConfigManager.init();
			
		} catch (FileNotFoundException e) {
			Logger.logError(MODULE,"Failed to update database.properties, Reason: "+ e.getMessage());
		} catch (IOException e) {
			Logger.logError(MODULE,"Failed to update database.properties, Reason: "+ e.getMessage());
		}catch (Exception e) {
			Logger.logError(MODULE,"Failed to update database.properties, Reason: "+ e.getMessage());
		}finally {
			if(dbPropertieWriter != null){
				dbPropertieWriter.close();
			}
			if(dbPropertieWriterAtSMPhysicalDirectory != null){
				dbPropertieWriterAtSMPhysicalDirectory.close();
			}
			if(dbPropertieWriterAtTomcatWorkingDirectory != null){
				dbPropertieWriterAtTomcatWorkingDirectory.close();
			}
		}
		
		return databaseProperties;
	}
	
	private void convertPropertiesToBean(String dbProps, DatabaseProperties databaseProperties) throws NoSuchEncryptionException, Exception {
			
		FileInputStream dbPropertiesStream = null;
		Properties dbProperties = new Properties();
		dbPropertiesStream = new FileInputStream(dbProps);
		dbProperties.load(dbPropertiesStream);
		Logger.logInfo(MODULE, "database.properties file path: "+dbPropsFileLocation);
		
		String dbConnectionUrl = dbProperties.getProperty("hibernate.connection.url");
		String userName = dbProperties.getProperty("hibernate.connection.username");
		String dbPassword = dbProperties.getProperty("hibernate.connection.password");
		String formatSql = dbProperties.getProperty("hibernate.format_sql");
		String showSql = dbProperties.getProperty("hibernate.show_sql");
		
		String password = ConfigManager.getPlainPasswordFromEncrypted(dbPassword);
		
		if( Strings.isNullOrEmpty(dbConnectionUrl) == false ){
			if(EliteUtility.getLowerCaseString(dbConnectionUrl).contains("postgresql")){
				databaseProperties.setDialect("org.hibernate.dialect.PostgreSQLDialect");
			}else if(EliteUtility.getLowerCaseString(dbConnectionUrl).contains("oracle")){
				databaseProperties.setDialect("org.hibernate.dialect.Oracle9Dialect");
			}
			databaseProperties.setDriverClass(ConfigManager.getDriverClass(dbConnectionUrl));
		}
		
		databaseProperties.setConnectionUrl(dbConnectionUrl);
		databaseProperties.setDbUsername(userName);
		databaseProperties.setDbPassword(password);
		databaseProperties.setFormatSQL(formatSql);
		databaseProperties.setShowSQL(showSql);
	}
		
}
