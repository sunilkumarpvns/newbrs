package com.elitecore.elitesm.web.systemstartup.dbsetup.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.MappingDispatchAction;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.Files;
import com.elitecore.elitesm.hibernate.core.system.util.HibernateSessionFactory;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.startup.EliteSMStartupStatus;
import com.elitecore.elitesm.web.systemstartup.dbsetup.form.EliteAAAStartupDBSetupForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class ConfiguredDatabaseConnectionParamtersAction  extends  MappingDispatchAction{
	private static final String UPDATE_ACTION = "update";
	private static final String HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
	private static final String HIBERNATE_SHOW_SQL = "hibernate.show_sql";
	private static final String HIBERNATE_CONNECTION_DRIVER_CLASS = "hibernate.connection.driver_class";
	private static final String HIBERNATE_DIALECT = "hibernate.dialect";
	private static final String HIBERNATE_CONNECTION_PASSWORD = "hibernate.connection.password";
	private static final String HIBERNATE_CONNECTION_USERNAME = "hibernate.connection.username";
	private static final String HIBERNATE_CONNECTION_URL = "hibernate.connection.url";
	private static final String MODULE = "CONFIGURE-DATABASE-CONNECTION-PARAMETER";
	private static final String SUCCESS="success";
	private static final String UPDATE_DATABASE_PROPERIES = "change_database_properties";
	private static final String SERVER_HOME = System.getenv("SM_HOME");
	private static String dbPropsFilePhysicalLocation=  SERVER_HOME + "/database.properties";
	private static String dbPropsFileTomcatWorkingDirLocation= EliteUtility.getSMHome() +ConfigConstant.DATABASE_CONFIG_FILE_LOCATION;;
	private static final String EXECUTE_SQL = "execute_sql_file";
	public static final String USER_CREDENTIAL_WRONG = "DB User Credential(username, password) or connection URL is wrong , Kindly provide valid credential";
	public static final String ELIGIBLE_TO_START = "DB User is Eligible to startup EliteAAA SM";
	public static final String ELITEAAA_SQL_NOT_FOUND = "Required SQL files(eliteaaa.sql, eliteaaa-oracle.sql/eliteaaa-postgres.sql and kpis.sql) is not executed in given Database user, kindly execute SQL files first";
	private static final String ELITEAAA_ORACLE_SCHEMA_FILE_LOCATION = EliteUtility.getSMHome()+ "/setup/database/fullsetup/eliteaaa-schema.sql";
	private static final String ELITEAAA_POSTGRES_SCHEMA_FILE_LOCATION = EliteUtility.getSMHome()+ "/setup/database/postgres/eliteaaa-schema.sql";
	private static final String ELITEAAA_SQL_FILE_LOCATION = EliteUtility.getSMHome()+ "/setup/database/fullsetup/eliteaaa.sql";
	private static final String POSTGRES_ELITEAAA_CERTIFICATE_LOCATION = EliteUtility.getSMHome()+ "/setup/database/postgres/eliteaaa-postgres.sql";
	private static final String ORACLE_ELITEAAA_CERTIFICATE_LOCATION = EliteUtility.getSMHome()+ "/setup/database/fullsetup/eliteaaa-oracle.sql";
	private static final String KPIS_SQL_FILE_LOCATION = EliteUtility.getSMHome()+ "/setup/database/fullsetup/kpis.sql";
	private static final String ORACLE_GRANTED_ROLE_QUERY = "select * from dba_role_privs where granted_role='DBA'";
	private static final String POSTGRES_GRANTED_ROLE_QUERY = "select current_user";
	private static final String POSTGRES_AGENT_FILE_LOCATION = EliteUtility.getSMHome()+ "/setup/database/postgres/pgagent.sql";
	private static final String PG_AGENT_EXIST_QUERY = "select exists (select * from pg_catalog.pg_namespace where nspname = 'pgagent')";
	
	private static String DRIVER_CLASS = null;
	
	static {
		FileInputStream dbPropertiesStream = null;
		try{
			Properties dbProperties = new Properties();
			dbPropertiesStream = new FileInputStream(dbPropsFileTomcatWorkingDirLocation);
			dbProperties.load(dbPropertiesStream);
			DRIVER_CLASS = dbProperties.getProperty("hibernate.connection.driver_class");
			
			if(Strings.isNullOrBlank(DRIVER_CLASS)){
				DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";
			}
		}catch(Exception e){
			DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver" ;
		}finally{
			if(dbPropertiesStream != null){
				try {
					dbPropertiesStream.close();
				} catch (IOException e) {
					Logger.logTrace(MODULE,e.getMessage());
				}
			}
		}
	
	}
	
	public ActionForward updateDBProperties(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		
		if(EliteSMStartupStatus.DB_SETUP_COMPLETED == true){
			request.setAttribute("isSetupAlreadyCompleted", "true");
			return mapping.findForward(SUCCESS);
		}
		
		String isValidForConfigureDbParameters = (String)request.getAttribute("isValidForConfigureDbParameters");
		
		if(Strings.isNullOrBlank(isValidForConfigureDbParameters)){
			isValidForConfigureDbParameters = "false";
		}
		
		if("false".equals(isValidForConfigureDbParameters)){
			return mapping.findForward("forwardToDefaultStartup");
		}
		
		EliteAAAStartupDBSetupForm eliteAAAStartupDBSetupForm = (EliteAAAStartupDBSetupForm) form;
		String action = eliteAAAStartupDBSetupForm.getAction();
		
		if (Strings.isNullOrBlank(SERVER_HOME) == false) {
			Logger.logDebug(MODULE,"physical path of database.properties file: "+dbPropsFilePhysicalLocation);
		}
		
		Logger.logDebug(MODULE,"Tomcat working directory path of database.properties file: "+dbPropsFileTomcatWorkingDirLocation);
		if (action == null) {
			Properties dbProperties = eliteAAAStartupDBSetupForm.getDbProperties();
			/** set database.properties all parameters to form for display purpose */
			if (dbProperties != null) {
				Logger.logDebug(MODULE,"Reading database.properties file, set its data to form");
				setPropertiesToForm(dbProperties,eliteAAAStartupDBSetupForm);
			}
		}
		
		try {
			if (action != null && UPDATE_ACTION.equals(action)) {
				
				String connectionURL = eliteAAAStartupDBSetupForm.getConnectionUrl();
				String userName = eliteAAAStartupDBSetupForm.getUserName();
				String password = eliteAAAStartupDBSetupForm.getPassWord();
				
				if( Strings.isNullOrBlank(connectionURL) == false){
					
					DRIVER_CLASS = getDriverClass(connectionURL);
					
					if (eliteAAAStartupDBSetupForm.getCreateNewUserSelected()) {
						
						String dbAdminUsername = eliteAAAStartupDBSetupForm.getDbAdminUsername().trim();
						String dbAdminPassword = eliteAAAStartupDBSetupForm.getDbAdminPassword().trim();
						String dbfFileLocation = eliteAAAStartupDBSetupForm.getDbfFileLocation().trim();
						
						Connection dbConnection = null;
						Statement sqlStatementExecutor = null;
						boolean isDBAUser = true;
						String fileLocation =  getSchemaFileLocation(connectionURL);
						
						if(Strings.isNullOrBlank(fileLocation) == false){
						
							File eliteaaSchemaSql = new File(fileLocation);
							
							Logger.logDebug(MODULE, "eliteaaa-schema.sql file path: " + eliteaaSchemaSql.getAbsolutePath());
							
							if (eliteaaSchemaSql.exists()) {
								
								byte[] schemaFileContent = Files.readFully(eliteaaSchemaSql);
								String schemaSqlFilesStatement = new String(schemaFileContent,"UTF-8");
								
								schemaSqlFilesStatement = replaceSchemaStatement(schemaSqlFilesStatement, connectionURL, userName, password, dbfFileLocation);
								
								eliteAAAStartupDBSetupForm.setSchemaSql(schemaSqlFilesStatement);
								
							} else {
								Logger.logError(MODULE,"eliteaaa-schema.sql does not found");
								eliteAAAStartupDBSetupForm.setErrorMsg("Failed to create new DB user, Reason: eliteaaa-schema.sql file does not found");
								eliteAAAStartupDBSetupForm.setInvalidParamters(true);
								request.setAttribute("eliteAAAStartupDBSetupForm", eliteAAAStartupDBSetupForm);
								request.setAttribute("isValidForConfigureDbParameters", "true");
								return mapping.findForward(UPDATE_DATABASE_PROPERIES);
							}
							
							
							try {
								dbConnection = EliteAAAStartupDBSetupAction.getDBConnection(connectionURL, dbAdminUsername, dbAdminPassword, DRIVER_CLASS);
								
								sqlStatementExecutor = dbConnection.createStatement();
								
								/** check whether entered db admin user has administrator permission for creating new user or not */
								String query = getQueryFromConnectionURL(connectionURL);
								
								try{
									sqlStatementExecutor.executeQuery(query);
								} catch (SQLException se) {
									isDBAUser = false;
									throw new SQLException("Failed to create new DB user, Reason: Specified DB admin user does not have permission for creating new user");
								}
								
								if( connectionURL.contains(ConfigConstant.POSTGRESQL)){
									
									try{
									
										ResultSet rs = sqlStatementExecutor.executeQuery(PG_AGENT_EXIST_QUERY);
									    while (rs.next()) {
									    	
									    	String result = rs.getString("exists");
									    	
									    	if("f".equalsIgnoreCase(result)){
									    	
									    		File pgAgentSchemaFile = new File(POSTGRES_AGENT_FILE_LOCATION);
									 			
									 			Logger.logDebug(MODULE, "pgagent.sql file path: " + pgAgentSchemaFile.getAbsolutePath());
									 			
									 			if (pgAgentSchemaFile.exists()) {
									 				
									 				byte[] schemaFileContent = Files.readFully(pgAgentSchemaFile);
									 				String pgAgentSchemaFileStatement = new String(schemaFileContent,"UTF-8");
									 			
									 				String fileStatements = eliteAAAStartupDBSetupForm.getSchemaSql();
									 				
									 				fileStatements = pgAgentSchemaFileStatement.concat(fileStatements);
									 				eliteAAAStartupDBSetupForm.setSchemaSql(fileStatements);
									 			}
									    	 }
									    }
										
									} catch(SQLException se){
										throw new SQLException("Failed to retrive information of PG Agent already exist or not, Reason : " + se.getMessage());
									}
								}
								
								if (isDBAUser) {
									
									/** executing eliteaaa-schema.sql file's statement */
									List<String> sqlStatements = new LinkedList<String>();
									sqlStatements.addAll(getSqlStatements(new StringReader(eliteAAAStartupDBSetupForm.getSchemaSql().trim())));
									String invalidSqlStatement = null ;
									
									if (Collectionz.isNullOrEmpty(sqlStatements) == false) {
										try {
											Logger.logDebug(MODULE,"Executed SQL statement:");
											for (String sqlStatement : sqlStatements) {
												if (Strings.isNullOrBlank(sqlStatement) == false) {
													invalidSqlStatement = sqlStatement;
													sqlStatementExecutor.execute(sqlStatement);
													Logger.logDebug(MODULE,sqlStatement);
												}
												
											}
										} catch (SQLException sqe) {
											Logger.logError(MODULE,"Failed to execute SQL statement ' "+invalidSqlStatement +" ', Reason: " +sqe.getMessage());
											throw new SQLException("Failed to execute SQL statement ' "+invalidSqlStatement +" ', Reason: " +sqe.getMessage());
										}finally{
											DBUtility.closeQuietly(sqlStatementExecutor);
										}
										
									Logger.logInfo(MODULE,"eliteaaa-schema.sql executed successfully");
									eliteAAAStartupDBSetupForm.setNewUserName(userName);
									eliteAAAStartupDBSetupForm.setInvalidParamters(false);
									eliteAAAStartupDBSetupForm.setErrorMsg(null);
									request.setAttribute("eliteAAAStartupDBSetupForm", eliteAAAStartupDBSetupForm);
									request.setAttribute("isValidForExecutingSQL", "true");
									return mapping.findForward(EXECUTE_SQL);
								}
							}
						} catch (SQLException sqe) {
							Logger.logTrace(MODULE,"Invalid database connection Parameter, Reason: "+sqe.getMessage());
							eliteAAAStartupDBSetupForm.setErrorMsg(sqe.getMessage());
							eliteAAAStartupDBSetupForm.setInvalidParamters(true);
							request.setAttribute("eliteAAAStartupDBSetupForm", eliteAAAStartupDBSetupForm);
							request.setAttribute("isValidForConfigureDbParameters", "true");
							return mapping.findForward(UPDATE_DATABASE_PROPERIES);
						} finally {
							if(dbConnection != null){
								DBUtility.closeQuietly(dbConnection);
							}
						}
					}
				}else{
						
						Properties dbProperties = new Properties();
						
						String dbCheckResponse = checkDBUserIsReadyToStartEliteAAASM(userName,password, connectionURL);
						
						if(ELIGIBLE_TO_START.equals(dbCheckResponse)){
							setFormToProperties(eliteAAAStartupDBSetupForm, dbProperties);
							EliteSMStartupStatus.DB_SETUP_COMPLETED = true;
							request.setAttribute("isSetupFirstTime", "true");
							return mapping.findForward(SUCCESS);
							
						} else if(ELITEAAA_SQL_NOT_FOUND.equals(dbCheckResponse)){
							
							eliteAAAStartupDBSetupForm.setErrorMsg(dbCheckResponse);
							eliteAAAStartupDBSetupForm.setInvalidParamters(true);
							eliteAAAStartupDBSetupForm.setNewUserName(eliteAAAStartupDBSetupForm.getUserName());
							request.setAttribute("eliteAAAStartupDBSetupForm", eliteAAAStartupDBSetupForm);
							request.setAttribute("isValidForExecutingSQL", "true");
							return mapping.findForward(EXECUTE_SQL);
							
						}else{
							Logger.logDebug(MODULE,"Given db user credential is not eligible for start EliteAAA SM");
							eliteAAAStartupDBSetupForm.setErrorMsg(dbCheckResponse);
							eliteAAAStartupDBSetupForm.setInvalidParamters(true);
							request.setAttribute("eliteAAAStartupDBSetupForm", eliteAAAStartupDBSetupForm);
							request.setAttribute("isValidForConfigureDbParameters", "true");
							return mapping.findForward(UPDATE_DATABASE_PROPERIES);
						}
						
					}
				}
			}
		}catch(Exception e){
			request.setAttribute("isValidForConfigureDbParameters", "true");
			eliteAAAStartupDBSetupForm.setErrorMsg(e.getMessage());
			eliteAAAStartupDBSetupForm.setInvalidParamters(true);
		}
		
		request.setAttribute("isValidForConfigureDbParameters", "true");
		request.setAttribute("eliteAAAStartupDBSetupForm", eliteAAAStartupDBSetupForm);
		return mapping.findForward(UPDATE_DATABASE_PROPERIES);
		
	}
	
	private String getQueryFromConnectionURL(String connectionURL) {
		String query = "";
		if (connectionURL.contains(ConfigConstant.ORACLE)) {
			query = ORACLE_GRANTED_ROLE_QUERY;
		} else if (connectionURL.contains(ConfigConstant.POSTGRESQL)) {
			query = POSTGRES_GRANTED_ROLE_QUERY;
		}
		return query;
	}

	private String replaceSchemaStatement(String schemaSqlFilesStatement,
			String connectionURL, String dbUsername, String dbUserPassword, String dbfFileLocation) {
		
		if( connectionURL.contains(ConfigConstant.ORACLE)){
			
			schemaSqlFilesStatement = schemaSqlFilesStatement.replaceAll("(?i)eliteaaa", dbUsername);
			schemaSqlFilesStatement = schemaSqlFilesStatement.replaceFirst("IDENTIFIED BY "+dbUsername,"IDENTIFIED BY "+dbUserPassword);
			schemaSqlFilesStatement = replaceDBFFileLocation(schemaSqlFilesStatement, dbfFileLocation, "DATAFILE '" , "' size 100M AUTOEXTEND ON;");
			
			
		}else if(connectionURL.contains(ConfigConstant.POSTGRESQL)){
			
			schemaSqlFilesStatement = schemaSqlFilesStatement.replaceAll("(?i)eliteaaa", dbUsername);
			schemaSqlFilesStatement = schemaSqlFilesStatement.replaceFirst("LOGIN PASSWORD '"+dbUsername+"'", "LOGIN PASSWORD '"+dbUserPassword+"'");
			schemaSqlFilesStatement = replaceDBFFileLocation(schemaSqlFilesStatement, dbfFileLocation, "LOCATION '" , "';");
			schemaSqlFilesStatement = replaceDBFFileLocation(schemaSqlFilesStatement, dbfFileLocation, "-p ", "/';");
		}
	
		return schemaSqlFilesStatement;
	}

	private String replaceDBFFileLocation(String schemaSqlFilesStatement, String dbfFileLocation, String firstDelimeter, String lastDelimeter) {
		int p1 = schemaSqlFilesStatement.indexOf(firstDelimeter);
		int p2 = schemaSqlFilesStatement.indexOf(lastDelimeter, p1);       
		String replacement = dbfFileLocation;
		if (p1 >= 0 && p2 > p1) {
			String res = schemaSqlFilesStatement.substring(0, p1 + firstDelimeter.length())
		               + replacement
		               + schemaSqlFilesStatement.substring(p2);
		    return res;
		}
		return schemaSqlFilesStatement;
	}

	private String getSchemaFileLocation(String connectionURL) {
		String fileLocation = null;
		if(connectionURL.contains(ConfigConstant.ORACLE)){
			fileLocation = ELITEAAA_ORACLE_SCHEMA_FILE_LOCATION;
		}else if(connectionURL.contains(ConfigConstant.POSTGRESQL)){
			fileLocation = ELITEAAA_POSTGRES_SCHEMA_FILE_LOCATION;
		}
		return fileLocation;
	}
	
	private String getCertificateFileLocation(String connectionURL, String userName) throws IOException {
		String fileLocation = null;
		if(connectionURL.contains(ConfigConstant.ORACLE)){
			fileLocation = ORACLE_ELITEAAA_CERTIFICATE_LOCATION;
		}else if(connectionURL.contains(ConfigConstant.POSTGRESQL)){

			File postgresFile = new File(POSTGRES_ELITEAAA_CERTIFICATE_LOCATION);
			
			String fileContent = null;
			StringBuilder fileContents = new StringBuilder((int) postgresFile.length());
			
			Scanner scanner = new Scanner(postgresFile);
			String lineSeparator = System.getProperty("line.separator");

			try {
				while (scanner.hasNextLine()) {
					fileContents.append(scanner.nextLine() + lineSeparator);
				}
				fileContent = fileContents.toString();
			} finally {
				scanner.close();
			}
			
			String updatedSQL = fileContent.replaceAll("'set search_path=eliteaaa;", "'set search_path="+userName+";");
			
			FileWriter writer = new FileWriter(POSTGRES_ELITEAAA_CERTIFICATE_LOCATION);
			writer.write(updatedSQL);
			writer.close();
			
			fileLocation = POSTGRES_ELITEAAA_CERTIFICATE_LOCATION;
		}
		return fileLocation;
	}


	private void setPropertiesToForm(Properties dbProperties,EliteAAAStartupDBSetupForm eliteAAAStartupDBSetupForm){
		
		if(dbProperties != null){
			eliteAAAStartupDBSetupForm.setConnectionUrl(dbProperties.getProperty(HIBERNATE_CONNECTION_URL));
			eliteAAAStartupDBSetupForm.setUserName(dbProperties.getProperty(HIBERNATE_CONNECTION_USERNAME));
			eliteAAAStartupDBSetupForm.setDialect(dbProperties.getProperty(HIBERNATE_DIALECT));
			eliteAAAStartupDBSetupForm.setJdbcDriver(dbProperties.getProperty(HIBERNATE_CONNECTION_DRIVER_CLASS));
			eliteAAAStartupDBSetupForm.setShowSql(dbProperties.getProperty(HIBERNATE_SHOW_SQL));
			eliteAAAStartupDBSetupForm.setFormatSql(dbProperties.getProperty(HIBERNATE_FORMAT_SQL));
		}
	}
	
	private void setFormToProperties(EliteAAAStartupDBSetupForm eliteAAAStartupDBSetupForm , Properties dbProperties) throws IOException{
		
		FileOutputStream dbPropertieWriter = null; 
		FileOutputStream dbPropertieWriterAtSMPhysicalDirectory = null ; 
		FileOutputStream dbPropertieWriterAtTomcatWorkingDirectory = null ;
		
		try {
			/** store Driver class by which JDBC connection configured **/
			dbProperties.setProperty(HIBERNATE_CONNECTION_DRIVER_CLASS, DRIVER_CLASS);
			dbProperties.setProperty(HIBERNATE_CONNECTION_URL, eliteAAAStartupDBSetupForm.getConnectionUrl());
			dbProperties.setProperty(HIBERNATE_CONNECTION_USERNAME, eliteAAAStartupDBSetupForm.getUserName());
			// encrypt password
			String encryptedPassword = "";
			String plainPassoword = eliteAAAStartupDBSetupForm.getPassWord();
			if (Strings.isNullOrBlank(plainPassoword) == false) {
				encryptedPassword = PasswordEncryption.getInstance().crypt(plainPassoword,PasswordEncryption.ELITE_PASSWORD_CRYPT);
			}
			
			if(Strings.isNullOrEmpty(eliteAAAStartupDBSetupForm.getConnectionUrl()) == false && EliteUtility.getLowerCaseString(eliteAAAStartupDBSetupForm.getConnectionUrl()).contains("postgresql")){
				eliteAAAStartupDBSetupForm.setDialect("org.hibernate.dialect.PostgreSQLDialect");
			}
			
			dbProperties.setProperty(HIBERNATE_CONNECTION_PASSWORD, encryptedPassword);
			dbProperties.setProperty(HIBERNATE_DIALECT, eliteAAAStartupDBSetupForm.getDialect());
			dbProperties.setProperty(HIBERNATE_SHOW_SQL, eliteAAAStartupDBSetupForm.getShowSql());
			dbProperties.setProperty(HIBERNATE_FORMAT_SQL, eliteAAAStartupDBSetupForm.getFormatSql());

			
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
			
			File dbPropsFileInTomcat = new File(dbPropsFileTomcatWorkingDirLocation);
		
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
	}
	
	public String checkDBUserIsReadyToStartEliteAAASM(String userName, String plainPassword, String connectionURL) throws SQLException{
		
		Connection dbConnection = null;
		try{
			dbConnection = EliteAAAStartupDBSetupAction.getDBConnection(connectionURL, userName, plainPassword,DRIVER_CLASS);
			if(dbConnection != null){
				Statement statementExecutor = dbConnection.createStatement();
				
				String query = "select * from tblmversion";
				ResultSet versionsResultSet = null;
				try{
					versionsResultSet = statementExecutor.executeQuery(query);
					if(versionsResultSet != null){
						return ELIGIBLE_TO_START;
						
					}
				}catch(SQLException sqe){
					/** if table not found than gives table or view does not exist */
					return ELITEAAA_SQL_NOT_FOUND;
				}finally{
					DBUtility.closeQuietly(statementExecutor);
					DBUtility.closeQuietly(versionsResultSet);
				}
			}
		}catch(SQLException sqe){
			return sqe.getMessage();
		}finally{
			if(dbConnection != null){
				DBUtility.closeQuietly(dbConnection);
			}
		}
		return ELIGIBLE_TO_START;
		
	}
	
	public ActionForward executeSQL(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		EliteAAAStartupDBSetupForm eliteAAAStartupDBSetupForm = (EliteAAAStartupDBSetupForm) form;
		String action = eliteAAAStartupDBSetupForm.getAction();
		
		if(EliteSMStartupStatus.DB_SETUP_COMPLETED == true){
			/** if database configuration parameter already set */
			request.setAttribute("isSetupAlreadyCompleted", "true");
			return mapping.findForward(SUCCESS);
		}
		
		if("execute".equals(action)){
			request.setAttribute("isValidForExecutingSQL", "true");
		}
		
		String isValidForExecutingSQL = (String)request.getAttribute("isValidForExecutingSQL");
		
		if(Strings.isNullOrBlank(isValidForExecutingSQL)){
			isValidForExecutingSQL = "false";
		}
		
		if("false".equals(isValidForExecutingSQL)){
			return mapping.findForward("forwardToDefaultStartup");
		}
		
		request.setAttribute("isValidForExecutingSQL", "true");
		
		if(Strings.isNullOrBlank(action)){
			Properties dbProperties = eliteAAAStartupDBSetupForm.getDbProperties();
			/** (pre)set properties to form */
			setPropertiesToForm(dbProperties,eliteAAAStartupDBSetupForm);
		}
		
		if("execute".equals(action)){
			Logger.logDebug(MODULE,"Entered in executing sql file");
			String connectionURL = eliteAAAStartupDBSetupForm.getConnectionUrl();
			String userName = eliteAAAStartupDBSetupForm.getNewUserName();
			String password = eliteAAAStartupDBSetupForm.getPassWord();
			
			if (Strings.isNullOrBlank(connectionURL) == false) {
				
				DRIVER_CLASS = getDriverClass(connectionURL);
				List<String> sqlStatements = new LinkedList<String>();
				String certificateFileLocation = getCertificateFileLocation(connectionURL, userName);
				
				File eliteaaaSqlFile = new File(ELITEAAA_SQL_FILE_LOCATION);
				File kpisSqlFile = new File(KPIS_SQL_FILE_LOCATION);
				File certificateFile = new File(certificateFileLocation);
				
				try{
					if(eliteaaaSqlFile.exists()){
						
						if(connectionURL.contains(ConfigConstant.ORACLE)){
							sqlStatements.addAll(getSqlStatements(new BufferedReader(new FileReader(eliteaaaSqlFile))));
						}else if(connectionURL.contains(ConfigConstant.POSTGRESQL)){

							String fileContent = null;
							StringBuilder fileContents = new StringBuilder((int) eliteaaaSqlFile.length());
							
							Scanner scanner = new Scanner(eliteaaaSqlFile);
							String lineSeparator = System.getProperty("line.separator");

							try {
								while (scanner.hasNextLine()) {
									fileContents.append(scanner.nextLine() + lineSeparator);
								}
								fileContent = fileContents.toString();
							} finally {
								scanner.close();
							}
							
							String updatedSQL = fileContent.replaceAll("BLOB", "BYTEA");
							updatedSQL = updatedSQL.replaceAll("BLOB", "BYTEA");
							
							FileWriter writer = new FileWriter(ELITEAAA_SQL_FILE_LOCATION);
							writer.write(updatedSQL);
							writer.close();
							
							File updatedFile = new File(ELITEAAA_SQL_FILE_LOCATION);
							
							sqlStatements.addAll(getSqlStatements(new BufferedReader(new FileReader(updatedFile))));
						}
						Logger.logDebug(MODULE,"Read all SQL statement from eliteaaa.sql file");
					}else{
						throw new Exception("Failed to execute sql file, Reason: Database Setup(eliteaaa.sql) file does not found");
					}
					
					if(kpisSqlFile.exists()){
						sqlStatements.addAll(getSqlStatements(new BufferedReader(new FileReader(kpisSqlFile))));
						Logger.logDebug(MODULE,"Read all SQL statement from kpis.sql file");
					}else{
						throw new Exception("Failed to execute sql file, Reason: KPI setup(kpis.sql) file does not found");
					}
					
					if(certificateFile.exists()){
						sqlStatements.addAll(getSqlStatements(new BufferedReader(new FileReader(certificateFile))));
						Logger.logDebug(MODULE,"Read all SQL statement from kpis.sql file");
					}else{
						throw new Exception("Failed to execute sql file, Reason: Certificate setup(eliteaaa-postgres.sql/eliteaaa-oracle.sql) file does not found");
					}
				}catch(Exception e){
					Logger.logTrace(MODULE,e.getMessage());
					eliteAAAStartupDBSetupForm.setInvalidParamters(true);
					eliteAAAStartupDBSetupForm.setErrorMsg(e.getMessage());
					return mapping.findForward(EXECUTE_SQL);
				}
				
				/** reading completed all statement in list now check connection */
				Logger.logInfo(MODULE,"eliteaaa.sql, eliteaaa-oracle.sql/eliteaaa-postgres.sql and kpis.sql read successfully, now going to execute it");
				Connection dbConnection = null;
				Statement sqlStatementExecutor = null;
			
				try{
					dbConnection = EliteAAAStartupDBSetupAction.getDBConnection(connectionURL, userName, password, DRIVER_CLASS);
					
					Logger.logDebug(MODULE,"Connection established  successfully with Given DB user");
					sqlStatementExecutor = dbConnection.createStatement();
					String invalidSqlStatement = null;
					
					if(Collectionz.isNullOrEmpty(sqlStatements) == false){
						try{
							Logger.logDebug(MODULE,"Executed SQL Statement: ");
							for(String sqlStatement : sqlStatements){
								if(sqlStatement != null && sqlStatement.isEmpty() == false){
									invalidSqlStatement = sqlStatement;
									sqlStatementExecutor.execute(sqlStatement);
									Logger.logDebug(MODULE, sqlStatement);
								}
								
							}
						}catch(SQLException sqe){
							Logger.logTrace(MODULE,"Failed to execute SQL statement ' "+invalidSqlStatement +" ', Reason: " +sqe.getMessage());
							throw new SQLException("Failed to execute SQL statement ' "+invalidSqlStatement +" ', Reason: " +sqe.getMessage());
						}finally{
							DBUtility.closeQuietly(sqlStatementExecutor);
						}
				
				
					Logger.logDebug(MODULE,"eliteaaa.sql, eliteaaa-postgres.sql/eliteaa-oracle.sql and kpis.sql file has been executed successfully");
					
					/** update database properties with new user credential */
					try{
						Properties dbProperties = new Properties();
						eliteAAAStartupDBSetupForm.setConnectionUrl(connectionURL);
						eliteAAAStartupDBSetupForm.setUserName(userName);
						eliteAAAStartupDBSetupForm.setPassWord(password);
						setFormToProperties(eliteAAAStartupDBSetupForm, dbProperties);
						
					}catch(IOException e){
						Logger.logTrace(MODULE,"Failed to update database.properties file after SQL file executed, Reason: "+e.getMessage());
					}
					eliteAAAStartupDBSetupForm.setSqlExecuted(true);
					EliteSMStartupStatus.DB_SETUP_COMPLETED = true;
					request.setAttribute("isSetupFirstTime", "true");
					return mapping.findForward(EXECUTE_SQL);
				 }
					
				} catch (SQLException sqe) {
					eliteAAAStartupDBSetupForm.setInvalidParamters(true);
					eliteAAAStartupDBSetupForm.setErrorMsg(sqe.getMessage());
					return mapping.findForward(EXECUTE_SQL);
				} finally {
					if(dbConnection != null){
						DBUtility.closeQuietly(dbConnection);
					}
				}
			}
		}
		
		return mapping.findForward(EXECUTE_SQL);
	}
	
	private List<String> getSqlStatements(Reader sqlStatementReader) throws IOException{
		StringBuilder sqlStatementsBlder = new StringBuilder();
		String sqlStatement = new String();
		List<String> sqlStamentsLst = new LinkedList<String>();
		boolean isMultipleLineStatement = false;
		BufferedReader sqlFileBufferedReader = new BufferedReader(sqlStatementReader);
		try{
			if(sqlStatementReader != null){
				 
	            while((sqlStatement = sqlFileBufferedReader.readLine()) != null)
	            {
 
	            	if(sqlStatement.startsWith("/*")){
	            		do{
	            			sqlStatement = sqlFileBufferedReader.readLine();
	            		}while(sqlStatement != null && sqlStatement.trim().endsWith("*/") == false);
	            		
	            	}else{
	            		
	            		if(sqlStatement != null && sqlStatement.trim().isEmpty() == false && sqlStatement.trim().startsWith("--") == false && sqlStatement.trim().startsWith("/") == false && sqlStatement.trim().startsWith("#") == false){
	            			
	            			if(sqlStatement.trim().endsWith(";")){
	            				if(isMultipleLineStatement){
	            				}else{
	            					/** remove semi-colon from last position */
	            					String trimmedSqlStatement = sqlStatement.trim();
	            					sqlStatement = trimmedSqlStatement.substring(0,trimmedSqlStatement.length()-1);
	            					sqlStamentsLst.add(sqlStatement.trim());
	            					sqlStatementsBlder.append(sqlStatement.trim()+"\n");
	            				}
	            				
	            			}else{
	            				isMultipleLineStatement = true;
	            				if(sqlStatement.trim().toUpperCase().startsWith("BEGIN") || sqlStatement.trim().toUpperCase().startsWith("DECLARE")){
	            					/** check if statement is pl-sql block or procedure or function */
	            					StringBuilder plSqlBlockBlder = new StringBuilder();
	            					plSqlBlockBlder.append(sqlStatement.trim() +"\n");
	            					/** iterate buffer reader upto END; */
	            					do{
	            						sqlStatement = sqlFileBufferedReader.readLine();
	            						if(sqlStatement != null && sqlStatement.trim().isEmpty() == false && sqlStatement.trim().startsWith("--") == false && sqlStatement.trim().startsWith("#") == false){
	            							plSqlBlockBlder.append(sqlStatement.trim()+"\n");
	            						}
	            					}while(sqlStatement != null && sqlStatement.trim().toUpperCase().endsWith("END;") == false);
	            					
	            					sqlStatement = plSqlBlockBlder.toString();
	            					sqlStamentsLst.add(sqlStatement);
	            					sqlStatementsBlder.append(sqlStatement+"\n");
	            					isMultipleLineStatement = false;
	            				}else if(sqlStatement.toUpperCase().contains("CREATE OR REPLACE FUNCTION PROC_AUTO_SESSION_CLOSER") || sqlStatement.toUpperCase().contains("CREATE OR REPLACE FUNCTION PGAGENT.")){
	            					StringBuilder plSqlBlockBlder = new StringBuilder();
	            					plSqlBlockBlder.append(sqlStatement.trim() +"\n");
	            					/** iterate buffer reader upto END; */
	            					do{
	            						sqlStatement = sqlFileBufferedReader.readLine();
	            						if(sqlStatement != null && sqlStatement.trim().isEmpty() == false && sqlStatement.trim().startsWith("--") == false && sqlStatement.trim().startsWith("#") == false){
	            							plSqlBlockBlder.append(sqlStatement.trim()+"\n");
	            						}
	            					}while(sqlStatement != null && (sqlStatement.trim().toUpperCase().endsWith("VOLATILE;") == false || sqlStatement.trim().toUpperCase().endsWith("IMMUTABLE;") == false));
	            					sqlStatement = plSqlBlockBlder.toString();
	            					sqlStamentsLst.add(sqlStatement);
	            					sqlStatementsBlder.append(sqlStatement+"\n");
	            					isMultipleLineStatement = false;
	            				}else if(sqlStatement.toUpperCase().contains("CREATE OR REPLACE PROCEDURE") || sqlStatement.toUpperCase().contains("CREATE OR REPLACE FUNCTION") || sqlStatement.toUpperCase().contains("CREATE OR REPLACE TRIGGER")){
	            					StringBuilder plSqlBlockBlder = new StringBuilder();
	            					plSqlBlockBlder.append(sqlStatement.trim() +"\n");
	            					/** iterate buffer reader upto END; */
	            					do{
	            						sqlStatement = sqlFileBufferedReader.readLine();
	            						if(sqlStatement != null && sqlStatement.trim().isEmpty() == false && sqlStatement.trim().startsWith("--") == false && sqlStatement.trim().startsWith("#") == false){
	            							plSqlBlockBlder.append(sqlStatement.trim()+"\n");
	            						}
	            					}while(sqlStatement != null && sqlStatement.trim().toUpperCase().endsWith("END;") == false);
	            					sqlStatement = plSqlBlockBlder.toString();
	            					sqlStamentsLst.add(sqlStatement);
	            					sqlStatementsBlder.append(sqlStatement+"\n");
	            					isMultipleLineStatement = false;
	            					
	            				}else if(sqlStatement.toUpperCase().contains("CREATE OR REPLACE PACKAGE BODY")){
	            					StringBuilder plsqlPackageBodyBlder = new StringBuilder();
	            					plsqlPackageBodyBlder.append(sqlStatement.trim() +"\n");
	            					do{
	            						sqlStatement = sqlFileBufferedReader.readLine();
	            						if(sqlStatement != null && sqlStatement.trim().isEmpty() == false && sqlStatement.trim().startsWith("--") == false && sqlStatement.trim().startsWith("#") == false){
	            							plsqlPackageBodyBlder.append(sqlStatement.trim()+"\n");
	            						}
	            							            						
	            					}while(sqlStatement != null && isPackageBodyEnd(sqlStatement.trim().toUpperCase()) == false);
	            					sqlStatement = plsqlPackageBodyBlder.toString();
	            					sqlStamentsLst.add(sqlStatement);
	            					sqlStatementsBlder.append(sqlStatement+"\n");
	            					isMultipleLineStatement = false;
	            				}else if(sqlStatement.toUpperCase().contains("CREATE OR REPLACE PACKAGE")){
	            					StringBuilder plsqlPackageBlder = new StringBuilder();
	            					plsqlPackageBlder.append(sqlStatement.trim() +"\n");
	            					do{
	            						sqlStatement = sqlFileBufferedReader.readLine();
	            						
	            						if(sqlStatement != null && sqlStatement.trim().isEmpty() == false && sqlStatement.trim().startsWith("--") == false && sqlStatement.trim().startsWith("#") == false){
	            							plsqlPackageBlder.append(sqlStatement.trim()+"\n");
	            						}
	            						
	            					}while(sqlStatement != null && isPackageBodyEnd(sqlStatement.trim().toUpperCase()) == false);
	            					sqlStatement = plsqlPackageBlder.toString();
	            					sqlStamentsLst.add(sqlStatement);
	            					sqlStatementsBlder.append(sqlStatement+"\n");
	            					isMultipleLineStatement = false;
	            				} else if (sqlStatement.toUpperCase().startsWith("DO $$") ){
	            					StringBuilder plsqlPackageBlder = new StringBuilder();
	            					plsqlPackageBlder.append(sqlStatement.trim() +"\n");
	            					do{
	            						sqlStatement = sqlFileBufferedReader.readLine();
	            						
	            						if(sqlStatement != null && sqlStatement.trim().isEmpty() == false && sqlStatement.trim().startsWith("--") == false && sqlStatement.trim().startsWith("#") == false){
	            							plsqlPackageBlder.append(sqlStatement.trim()+"\n");
	            						}
	            						
	            					}while(sqlStatement != null && sqlStatement.trim().toUpperCase().endsWith("$$;") == false);
	            					sqlStatement = plsqlPackageBlder.toString();
	            					sqlStamentsLst.add(sqlStatement);
	            					sqlStatementsBlder.append(sqlStatement+"\n");
	            					isMultipleLineStatement = false;
	            				} else{
	            					if(isMultipleLineStatement){
	            						StringBuilder mulipleLineQuery = new StringBuilder();
	            						mulipleLineQuery.append(sqlStatement.trim());
	            						/** multiple line query only(DDl ,DML operation) */
	            						do{
		            						sqlStatement = sqlFileBufferedReader.readLine();
		            						if(sqlStatement != null && sqlStatement.trim().isEmpty() == false && sqlStatement.trim().startsWith("--") == false && sqlStatement.trim().startsWith("#") == false){
		            							mulipleLineQuery.append(" "+sqlStatement.trim());
		            						}
		            					}while(sqlStatement != null && sqlStatement.trim().endsWith(";") == false);
		            					
		            					sqlStatement = mulipleLineQuery.toString();
		            					/** remove last character is semi colon */
		            					String tempString = sqlStatement.trim();
		            					sqlStatement = tempString.substring(0,tempString.length()-1);
		            					sqlStamentsLst.add(sqlStatement);
		            					sqlStatementsBlder.append(sqlStatement+"\n");
		            					isMultipleLineStatement = false;
	            					}
	            					
	            				}
	            				
	            			}
	            			
	            		}
	            	}
	            }
				
			}
			
		}catch(IOException ioException){
			Logger.logError(MODULE,"Problem in Reading File(eliteaaa.sql or eliteaaa-postgres.sql/eliteaaa-oracle.sql or kpis.sql), Reason: "+ ioException.getMessage());
		}catch (Exception e) {
			Logger.logError(MODULE,e.getMessage());
		}finally{
			
			if(sqlStatementReader != null){
				sqlStatementReader.close();
			}

			if(sqlFileBufferedReader != null){
				sqlFileBufferedReader.close();
			}
			
		}
		return sqlStamentsLst;

	}
	private static boolean isPackageBodyEnd(String sqlStatement){
		boolean isEnd = false;
		if(sqlStatement.matches("END\\s+(.*);")){
			if("END IF;".equals(sqlStatement)){
				isEnd = false;
			}else if("END CASE;".equals(sqlStatement)){
				isEnd = false;
			}else if("END LOOP;".equals(sqlStatement)){
				isEnd = false;
			}else{
				isEnd = true;
			}
		}
		return isEnd;
	}
	
	private String getDriverClass(String dbUrl) {
		
		dbUrl = EliteUtility.getLowerCaseString(dbUrl);
		
		if(Strings.isNullOrBlank(dbUrl) == false && dbUrl.contains("oracle")){
			return "oracle.jdbc.driver.OracleDriver";
		}else if(Strings.isNullOrBlank(dbUrl) == false && dbUrl.contains("postgres")){
			return "org.postgresql.Driver";
		}else if(Strings.isNullOrBlank(dbUrl) == false && dbUrl.contains("mariadb")){
			return "org.mariadb.jdbc.Driver";
		}else if(Strings.isNullOrBlank(dbUrl) == false && dbUrl.contains("mysql")){
			return "com.mysql.jdbc.Driver";
		}
		return null;
	}
}