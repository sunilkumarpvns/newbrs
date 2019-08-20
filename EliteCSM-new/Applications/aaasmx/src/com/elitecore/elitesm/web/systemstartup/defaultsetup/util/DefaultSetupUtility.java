package com.elitecore.elitesm.web.systemstartup.defaultsetup.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.hibernate.Session;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.Files;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.HibernateSessionFactory;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.systemstartup.defaultsetup.model.EliteAAADefaultModel;

public class DefaultSetupUtility {

	public static final String MODULE = "DEFAULT-SETUP";
	public static final String DEFAULT_SETUP = "defaultEliteAAASetup";
	public static final String FAILURE_FORWARD = "failure";
	public static final String SYSTEM_STARTUP = "startup";
	public static final String SETUP_CREATED = "setupCreated";
	public static final String ADMIN = "admin";
	private static String dbPropsFileLocation = EliteUtility.getSMHome() + File.separator +ConfigConstant.DATABASE_CONFIG_FILE_LOCATION;

	public static boolean checkForFreshInstance() throws DataManagerException, SQLException {
		NetServerBLManager serverBLManagaer = new NetServerBLManager();
		List<NetServerInstanceData> serverList = serverBLManagaer.getNetServerInstanceList();
		if (Collectionz.isNullOrEmpty(serverList) == false) {
			Logger.logError(MODULE,"Instance Already Created, skiping EliteAAA Default Setup installation");
			return true;
		} else {
			return false;
		}
	}

	public static List<String> checkForXmlFiles() {

		List<String> fileNotFoundValues = getNameOfMissingXMLFiles();
		if (Collectionz.isNullOrEmpty(fileNotFoundValues) == false) {
			Logger.logError(MODULE, "File " + fileNotFoundValues+ " not found for Default Configuration Setup");
		}
		return fileNotFoundValues;
	}

	public static List<String> verifyAndCollectDuplicateModule() throws DataManagerException {
		List<String> duplicateInstance = new LinkedList<String>();

		HBaseDataManager dataManager = new HBaseDataManager();

		Session session = null; 
		try {

			session =  HibernateSessionFactory.createSession();

			for (DefaultSetupClassNameAndProperty value : DefaultSetupClassNameAndProperty.values()) {
				Object obj = dataManager.verifyInstance(value.className,value.propertyName,session);
				if(obj != null){
					duplicateInstance.add(value.propertyName);
				}
			}
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			if( session != null ){
				session.close();
			}
		}
		return duplicateInstance;
	}

	public static List<String> getNameOfMissingXMLFiles() {
		List<String> errorMEssage = new ArrayList<String>();
		String[] fileNames = { EliteAAADefaultModel.DATABASE_DATASOURCE,
				EliteAAADefaultModel.RAD_DB_AUTH_DRIVER,
				EliteAAADefaultModel.RAD_CLASSIC_CSV_DRIVER,
				EliteAAADefaultModel.RADIUS_SERVICE_POLICY,
				EliteAAADefaultModel.RADIUS_TRANSACTION_LOGGER,
				EliteAAADefaultModel.CONCURRENT_LOGIN_POLICY,
				EliteAAADefaultModel.SUBSCRIBER_PROFILE,
				EliteAAADefaultModel.RADIUS_SESSION_MANAGER};

		for (String fileName : Arrays.asList(fileNames)) {
			String path = EliteUtility.getSMHome() + File.separator + "startupdata" + File.separator + "xmldata"+ File.separator + fileName + ".xml";
			File xmlFile = new File(path);
			try {
				if (xmlFile.exists()) {
					byte[] fileBytes = Files.readFully(xmlFile);
				} else {
					errorMEssage.add(fileName);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return errorMEssage;
	}

	public static DatabaseDSData readDatabaseProperties(DatabaseDSData databaseDSdata) throws DataManagerException {

		try {
			Properties dbProperties = ConfigManager.getDbProperties();
			
			String dbConnectionUrl = dbProperties.getProperty("hibernate.connection.url");
			String userName = dbProperties.getProperty("hibernate.connection.username");
			String encryptedPassword = dbProperties.getProperty("hibernate.connection.password");
			
			if (Strings.isNullOrEmpty(encryptedPassword) == false && Strings.isNullOrEmpty(dbConnectionUrl) == false && Strings.isNullOrEmpty(userName) == false) {
				databaseDSdata.setConnectionUrl(dbConnectionUrl);
				databaseDSdata.setUserName(userName);
				databaseDSdata.setPassword(encryptedPassword);
			} else {
				throw new DataManagerException("System is not ready for Default Configuration.");
			}
			
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to read database properties, Reason: " + exp);
		}
		return databaseDSdata;
	}

}
