package com.elitecore.elitesm.web.systemstartup.dbsetup.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.Closeables;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.startup.EliteSMStartupStatus;
import com.elitecore.elitesm.web.systemstartup.dbsetup.form.EliteAAAStartupDBSetupForm;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

public class EliteAAAStartupDBSetupAction extends BaseWebAction{
	
	private static final String IS_VALID_DB_PARAMETERS = "isValidForConfigureDbParameters";
	private static final String SUCCESS="success";
	private static final String CHANGE_DATABASE_PROPERTIES = "change_database_properties";
	private static final String EXECUTE_SQL = "execute_sql";
	private static final String CREATE_DATABASE_PROPERTIES = "create_database_properties";
	private static String dbPropsFileLocation = EliteUtility.getSMHome() + File.separator +ConfigConstant.DATABASE_CONFIG_FILE_LOCATION;
	public static final String ELITEAAA_SQL_NOT_FOUND = "Required SQL files(eliteaaa.sql, kpis.sql) is not executed in given Database user, kindly execute SQL files first";
	private static final String MODULE = "ELITEAAA-STARTUP-DBSETUP-ACTION";
	private static String dbPropsFilePhysicalLocation=  System.getenv("SM_HOME") + "/database.properties";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {

		if(EliteSMStartupStatus.DB_SETUP_COMPLETED == true){
			request.setAttribute("isSetupAlreadyCompleted", "true");
			return mapping.findForward(SUCCESS);
		}
		
		Logger.logDebug(MODULE, "In side EliteCSM Database configuration setup: "+ getClass().getName());
		EliteAAAStartupDBSetupForm eliteAAAStartupDBSetupForm = (EliteAAAStartupDBSetupForm) form;
		
		/* read file from SM_HOME environment path */
		File dbProps = new File(dbPropsFilePhysicalLocation);
		if( dbProps.exists()) {
			return checkDBcredentials(mapping, request, eliteAAAStartupDBSetupForm, dbProps);
		} else {
			/* read file from aaasmx/WEB-INF/database.properties */
			File dbPropsFile = new File(dbPropsFileLocation);
			
			if (dbPropsFile.exists()) {
				 return checkDBcredentials(mapping, request, eliteAAAStartupDBSetupForm, dbPropsFile);
			} else {
				Logger.logInfo(MODULE, "database.properties file does not exist");
				eliteAAAStartupDBSetupForm.setInvalidParamters(true);
				eliteAAAStartupDBSetupForm.setErrorMsg("database.properties file does not exist, Please provide Database connection parameters");
				request.setAttribute(IS_VALID_DB_PARAMETERS, "true");
				return mapping.findForward(CREATE_DATABASE_PROPERTIES);
			}
		}
		
	}

	private static ActionForward checkDBcredentials(ActionMapping mapping, HttpServletRequest request,
			EliteAAAStartupDBSetupForm eliteAAAStartupDBSetupForm, File dbProps) throws IOException {
		FileInputStream dbPropertiesStream = null;
		Connection dbConnection = null;
		try {
			Properties dbProperties = new Properties();
			dbPropertiesStream = new FileInputStream(dbProps);
			dbProperties.load(dbPropertiesStream);
			Logger.logInfo(MODULE, "database.properties file path: "+dbPropsFileLocation);
			
			eliteAAAStartupDBSetupForm.setDbProperties(dbProperties);
			String dbConnectionUrl = dbProperties.getProperty("hibernate.connection.url");
			String userName = dbProperties.getProperty("hibernate.connection.username");
			String driverClass = dbProperties.getProperty("hibernate.connection.driver_class");
			
			if(Strings.isNullOrBlank(driverClass)){
				driverClass = "oracle.jdbc.driver.OracleDriver";
			}
			
			String encryptedPassword = dbProperties.getProperty("hibernate.connection.password");
			String plainPassoword = getPlainPasswordFromEncrypted(encryptedPassword);
			
			dbConnection = getDBConnection(dbConnectionUrl, userName, plainPassoword, driverClass);
			dbConnection.setAutoCommit(false);
			
			checkTblmVersionExist(mapping, request, eliteAAAStartupDBSetupForm, dbConnection, userName, plainPassoword);
			
		} catch (SQLException sql) {
			Logger.logTrace(MODULE,"In Database.properties file DB user connection parameter is wrong, Reason: "+sql.getMessage());
			eliteAAAStartupDBSetupForm.setInvalidParamters(true);
			eliteAAAStartupDBSetupForm.setErrorMsg("Invalid Database connection paramters, Reason: "+sql.getMessage());
			request.setAttribute("eliteAAAStartupDBSetupForm", eliteAAAStartupDBSetupForm);
			request.setAttribute(IS_VALID_DB_PARAMETERS, "true");
			return mapping.findForward(CHANGE_DATABASE_PROPERTIES);
		} catch (Exception e) {
			Logger.logTrace(MODULE, "Invalid Database connection parameters, Reason: "+e.getMessage());
			eliteAAAStartupDBSetupForm.setInvalidParamters(true);
			eliteAAAStartupDBSetupForm.setErrorMsg("Invalid Database connection paramters, Reason: " + e.getMessage());
			request.setAttribute("eliteAAAStartupDBSetupForm", eliteAAAStartupDBSetupForm);
			request.setAttribute(IS_VALID_DB_PARAMETERS, "true");
			return mapping.findForward(CHANGE_DATABASE_PROPERTIES);
		} finally {
			Closeables.closeQuietly(dbPropertiesStream);
			DBUtility.closeQuietly(dbConnection);
		}
		return mapping.findForward(SUCCESS);
	}

	private static String getPlainPasswordFromEncrypted(String encryptedPassword)
			throws NoSuchEncryptionException, Exception {
		String plainPassoword = "";
		
		if (Strings.isNullOrBlank(encryptedPassword) == false) {
			try {
				plainPassoword = PasswordEncryption.getInstance().decrypt(encryptedPassword,PasswordEncryption.ELITE_PASSWORD_CRYPT);
			} catch (DecryptionNotSupportedException e) {
				throw new Exception("Error while decrypting password");
			} catch (DecryptionFailedException e) {
				throw new Exception("Error while decrypting password");
			}
		} else {
			throw new Exception("password is not specified in database.properties file");
		}
		return plainPassoword;
	}

	private static ActionForward checkTblmVersionExist(ActionMapping mapping, HttpServletRequest request,
			EliteAAAStartupDBSetupForm eliteAAAStartupDBSetupForm, Connection dbConnection, String userName,
			String plainPassoword) throws SQLException {
		Statement statementExecutor = dbConnection.createStatement();
		String fetchTBLMVersionQry = "select * from tblmversion";
		ResultSet tblmversionsResultSet = null;
		try {
			tblmversionsResultSet = statementExecutor.executeQuery(fetchTBLMVersionQry);
			request.setAttribute("isSetupAlreadyCompleted", "true");
			EliteSMStartupStatus.DB_SETUP_COMPLETED = true;
			return mapping.findForward(SUCCESS);
		} catch (SQLException sqe) {
			// if table not found than gives table or view does not exist
			Logger.logTrace(MODULE,"In Given DB user TBLMVERSION table not found so we forward to executing DB setup sql files");
			eliteAAAStartupDBSetupForm.setErrorMsg(ELITEAAA_SQL_NOT_FOUND);
			eliteAAAStartupDBSetupForm.setInvalidParamters(true);
			eliteAAAStartupDBSetupForm.setNewUserName(userName);
			eliteAAAStartupDBSetupForm.setPassWord(plainPassoword);
			request.setAttribute("eliteAAAStartupDBSetupForm", eliteAAAStartupDBSetupForm);
			request.setAttribute("isValidForExecutingSQL", "true");
			return mapping.findForward(EXECUTE_SQL);
		} finally {
			DBUtility.closeQuietly(tblmversionsResultSet);
			DBUtility.closeQuietly(statementExecutor);
		}
		
	}
	
	public static Connection getDBConnection(String connectionUrl,String dbUsername, String dbUserpassword, String driverClass) throws SQLException{
			try {
				Class.forName(driverClass);
			} catch (ClassNotFoundException e) {
				Logger.logError(MODULE,"jdbc driver class not found, Reason: "+e.getMessage());
				throw new SQLException(e.getMessage(),e);
			}
		return DriverManager.getConnection(connectionUrl, dbUsername, dbUserpassword);
	}
}