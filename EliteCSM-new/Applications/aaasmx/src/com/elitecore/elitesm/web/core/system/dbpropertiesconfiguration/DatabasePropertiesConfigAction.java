package com.elitecore.elitesm.web.core.system.dbpropertiesconfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.system.databaseproperties.DatabasePropertiesBLManager;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.system.databaseproperties.DatabaseProperties;
import com.elitecore.elitesm.hibernate.core.system.util.HibernateSessionFactory;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseDispatchAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.dbpropertiesconfiguration.form.DatabasePropertiesForm;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;
import com.elitecore.elitesm.web.systemstartup.dbsetup.controller.EliteAAAStartupDBSetupAction;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;
import com.google.gson.Gson;

public class DatabasePropertiesConfigAction extends BaseDispatchAction {

	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DATABASEPROPERTIESFILE;
	private static final String MODULE = DatabasePropertiesConfigAction.class.getSimpleName();
	private static final String UPDATE_DATABASE_PROPERTIES_CONFIG = "updateDatabasePropertiesConf";
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String INFORMATION = "information";
	private static final String VIEW_HISTORY = "viewHistory";

	private static final String SERVER_HOME = System.getenv("SM_HOME");
	private static String dbPropsFilePhysicalLocation=  System.getenv("SM_HOME") + "/database.properties";
	private static String dbPropsFileLocation = EliteUtility.getSMHome() + File.separator +ConfigConstant.DATABASE_CONFIG_FILE_LOCATION;
	
	public ActionForward getDatabasePropertiesData(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered getDatabasePropertiesData method of : "+ getClass().getName());
	
		try {
			   
			checkActionPermission(request, ACTION_ALIAS);
			DatabasePropertiesForm databasePropertiesForm = (DatabasePropertiesForm) form;
				   
			/* read file from SM_HOME environment path */
			File dbProps = new File(dbPropsFilePhysicalLocation);
			if( dbProps.exists()) {
				
				convertPropertiesToForm(dbPropsFilePhysicalLocation, databasePropertiesForm);
				
			} else {
				/* read file from aaasmx/WEB-INF/database.properties */
				File dbPropsFile = new File(dbPropsFileLocation);
				
				if (dbPropsFile.exists()) {
					convertPropertiesToForm(dbPropsFileLocation, databasePropertiesForm);
				} 
			}
		
			return mapping.findForward(UPDATE_DATABASE_PROPERTIES_CONFIG); 
	
		} catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add(INFORMATION, message);
			saveErrors(request, messages);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	public ActionForward update(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered update method of : "+ getClass().getName());
		
		try {
			checkActionPermission(request,ACTION_ALIAS);
			DatabasePropertiesForm databasePropertiesForm = (DatabasePropertiesForm) form;
			DatabaseProperties databaseProperties = new DatabaseProperties();
			DatabasePropertiesBLManager databasePropertiesBLManager = new DatabasePropertiesBLManager();
			Connection dbConnection = null;
			
			IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
			
			String driverClass = ConfigManager.getDriverClass(databasePropertiesForm.getConnectionUrl());

			dbConnection = EliteAAAStartupDBSetupAction.getDBConnection(databasePropertiesForm.getConnectionUrl(), databasePropertiesForm.getDbUsername(), databasePropertiesForm.getDbPassword(), driverClass);
			dbConnection.setAutoCommit(false);
			
			/* read file from SM_HOME environment path */
			File dbProps = new File(dbPropsFilePhysicalLocation);
			if( dbProps.exists()) {
				
				convertPropertiesToForm(dbPropsFilePhysicalLocation, databaseProperties);
				
			} else {
				/* read file from aaasmx/WEB-INF/database.properties */
				File dbPropsFile = new File(dbPropsFileLocation);
				
				if (dbPropsFile.exists()) {
					convertPropertiesToForm(dbPropsFileLocation, databaseProperties);
				} 
			}
			
			DatabaseProperties databasePropertiesData = convertFormToBean(databasePropertiesForm);
			updateDatabaseProperties(databasePropertiesForm);
			JSONArray jsonArray = ObjectDiffer.diff(databaseProperties, databasePropertiesData);
			
			databasePropertiesBLManager.doAudit(jsonArray.toString(), staffData);
			
			request.setAttribute("responseUrl", "/databaseProperties.do?method=getDatabasePropertiesData");
			ActionMessage message = new ActionMessage("databaseproperties.update.success");
			ActionMessages messages = new ActionMessages();
			messages.add(INFORMATION, message);
			saveMessages(request, messages);
			
			return mapping.findForward(SUCCESS_FORWARD);
		
		} catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}catch (SQLException se) {
			Logger.logError(MODULE,"Unable to establish connection , reason : "+ se.getMessage());
			Logger.logTrace(MODULE, se);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(se);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("databaseproperties.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}

	private DatabaseProperties convertFormToBean( DatabasePropertiesForm databasePropertiesForm) {
		DatabaseProperties databaseProperties = new DatabaseProperties();
		
		databaseProperties.setConnectionUrl(databasePropertiesForm.getConnectionUrl());
		databaseProperties.setDbUsername(databasePropertiesForm.getDbUsername());
		databaseProperties.setDbPassword(databasePropertiesForm.getDbPassword());
		databaseProperties.setShowSQL(databasePropertiesForm.getShowSQL());
		databaseProperties.setFormatSQL(databasePropertiesForm.getFormatSQL());
		
		if( Strings.isNullOrEmpty(databasePropertiesForm.getConnectionUrl()) == false ){
			if(EliteUtility.getLowerCaseString(databasePropertiesForm.getConnectionUrl()).contains("postgresql")){
				databaseProperties.setDialect("org.hibernate.dialect.PostgreSQLDialect");
			}else if(EliteUtility.getLowerCaseString(databasePropertiesForm.getConnectionUrl()).contains("oracle")){
				databaseProperties.setDialect("org.hibernate.dialect.Oracle9Dialect");
			}
			databaseProperties.setDriverClass(ConfigManager.getDriverClass(databasePropertiesForm.getConnectionUrl()));
		}
		return databaseProperties;
	}

	public ActionForward validate(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered validate method of : "+ getClass().getName());
		
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		String responseStr = null;
		try {
			Connection dbConnection = null;
			
			String connectionUrl = (request.getParameter("connectionUrl")).trim();
			String dbUsername = (request.getParameter("dbUsername")).trim();
			String dbPassword = (request.getParameter("dbPassword")).trim();
			
			String driverClass = ConfigManager.getDriverClass(connectionUrl);
			
			if( Strings.isNullOrBlank(driverClass) == false ){
				dbConnection = EliteAAAStartupDBSetupAction.getDBConnection(connectionUrl, dbUsername, dbPassword, driverClass);
				dbConnection.setAutoCommit(false);
				
				responseStr = gson.toJson(new DBResponse(true, null));
				out.print(responseStr);
			}
			
		} catch (SQLException sqe) {

			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + sqe.getMessage());
			Logger.logTrace(MODULE, sqe);
			
			responseStr = gson.toJson(new DBResponse(false, sqe.getMessage()));
			out.print(responseStr);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(null);
	}
	
	private void updateDatabaseProperties(DatabasePropertiesForm databasePropertiesForm) throws IOException{
		
		FileOutputStream dbPropertieWriter = null; 
		FileOutputStream dbPropertieWriterAtSMPhysicalDirectory = null ; 
		FileOutputStream dbPropertieWriterAtTomcatWorkingDirectory = null ;
		Properties dbProperties = new Properties();
		
		try {
			// encrypt password
			String encryptedPassword = "";
			String plainPassoword = databasePropertiesForm.getDbPassword();
			if (Strings.isNullOrBlank(plainPassoword) == false) {
				encryptedPassword = PasswordEncryption.getInstance().crypt(plainPassoword,PasswordEncryption.ELITE_PASSWORD_CRYPT);
			}
			
			if( Strings.isNullOrEmpty(databasePropertiesForm.getConnectionUrl()) == false ){
				if(EliteUtility.getLowerCaseString(databasePropertiesForm.getConnectionUrl()).contains("postgresql")){
					databasePropertiesForm.setDialect("org.hibernate.dialect.PostgreSQLDialect");
				}else if(EliteUtility.getLowerCaseString(databasePropertiesForm.getConnectionUrl()).contains("oracle")){
					databasePropertiesForm.setDialect("org.hibernate.dialect.Oracle9Dialect");
				}
				databasePropertiesForm.setDriverClass(ConfigManager.getDriverClass(databasePropertiesForm.getConnectionUrl()));
			}
			
			/** store Driver class by which JDBC connection configured **/
			dbProperties.setProperty("hibernate.connection.url", databasePropertiesForm.getConnectionUrl());
			dbProperties.setProperty("hibernate.connection.username", databasePropertiesForm.getDbUsername());
			dbProperties.setProperty("hibernate.connection.password", encryptedPassword);
			dbProperties.setProperty("hibernate.connection.driver_class", databasePropertiesForm.getDriverClass());
			dbProperties.setProperty("hibernate.dialect", databasePropertiesForm.getDialect());
			dbProperties.setProperty("hibernate.show_sql", databasePropertiesForm.getShowSQL());
			dbProperties.setProperty("hibernate.format_sql", databasePropertiesForm.getFormatSQL());
			
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
	}
	
	public ActionForward viewHistory(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered view History method of : "+ getClass().getName());
		try {
			HistoryBLManager historyBlManager = new HistoryBLManager();
			List<DatabaseHistoryData> lstDatabaseDSHistoryDatas = historyBlManager.getHistoryDataByModuleName(ConfigConstant.DATABASEPROPERTIES);

			request.setAttribute("lstDatabaseDSHistoryDatas",lstDatabaseDSHistoryDatas);
			return mapping.findForward(VIEW_HISTORY);
		} catch (Exception e) {
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE, e);
			Object [] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessages messages = new ActionMessages();
			ActionMessage message1 = new ActionMessage("databaseds.viewdatasource.failure");
			messages.add("information", message1);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
	}
	
	private void convertPropertiesToForm(String dbProps, DatabaseProperties databaseProperties) throws NoSuchEncryptionException, Exception {
		Properties dbProperties = readDatabaseProperties(dbProps);
		
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
	
	private void convertPropertiesToForm(String dbProps, DatabasePropertiesForm databaseProperties) throws NoSuchEncryptionException, Exception {
		
		Properties dbProperties = readDatabaseProperties(dbProps);
		
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

	private Properties readDatabaseProperties(String dbProps) throws IOException {
		
		FileInputStream dbPropertiesStream = null;
		Properties dbProperties = new Properties();
		dbPropertiesStream = new FileInputStream(dbProps);
		dbProperties.load(dbPropertiesStream);
		
		Logger.logInfo(MODULE, "database.properties file path: "+dbPropsFileLocation);
		
		return dbProperties;
		
	}
}
