package com.elitecore.elitesm.hibernate.core.system.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.Closeables;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.startup.EliteSMStartupStatus;
import com.elitecore.elitesm.web.core.system.startup.EliteSMStartupStatus.SM_MODULE_CONSTANTS;
import com.elitecore.elitesm.web.core.system.startup.EliteSMStartupStatus.SM_MODULE_STATUS;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

public class HibernateSessionFactory {
    
    private static String CONFIG_FILE_LOCATION = "config/hibernate.cfg.xml";
    private static String dbPropsFileLocation = EliteUtility.getSMHome() + File.separator +ConfigConstant.DATABASE_CONFIG_FILE_LOCATION;
    
	private static String dbPropsFileSmHome=  System.getenv("SM_HOME") + "/database.properties";
    
    private static final String ENTITY_NAME ="HibernateSessionFactory";
    
    /* The single instance of hibernate configuration */
    private static Configuration cfg = new Configuration();
    /* The  instance of hibernate configuration(reInitalize) */
    private static  Configuration reInitConfiguration = new Configuration();

    /* The single instance of hibernate SessionFactory */	
    private static org.hibernate.SessionFactory sessionFactory ;

    /*
     * Returns the ThreadLocal Session instance.  Lazy initialize
     * the <code>SessionFactory</code> if needed.
     *
     *  @return Session
     *  @throws HibernateException
     */
    public static Session createSession() throws HibernateException {
        
        Session session = null;
        if(sessionFactory == null) {
            synchronized(HibernateSessionFactory.class) {
                if (sessionFactory == null){
                    try {
                    	buildSessionFacotry(cfg);
                    }catch (Exception e) {
                    	EliteSMStartupStatus.updateStatus(SM_MODULE_CONSTANTS.HES, SM_MODULE_STATUS.FAILED);
                        Logger.logError(ENTITY_NAME,"Unable to build hibernate sessionfactory, Reason: ");
                        Logger.logTrace(ENTITY_NAME,e);
                        return null;
                    }
                }
            }
        }
        session=sessionFactory.openSession();
        return session;
      }

    /*
     *  Close the single hibernate session instance.
     *
     *  @throws HibernateException
     */
    public static void closeSession(Session session) throws HibernateException {

        if (session != null) {
            session.close();
        }
    }
    
    private static void buildSessionFacotry(Configuration cfg)throws Exception{
    	FileInputStream fileInputStream = null;
        try {
			EliteSMStartupStatus.updateStatus(SM_MODULE_CONSTANTS.DCC,SM_MODULE_STATUS.PROCESSING);
			Logger.logInfo(ENTITY_NAME,"Reading Hibernate Configuration");
			Properties properties = new Properties();
			
			File dbPropsFile = new File(dbPropsFileSmHome);
			if (dbPropsFile.exists()) {
				
				fileInputStream = new FileInputStream(dbPropsFile);
				Logger.logInfo(ENTITY_NAME, "Loading database properties from "+dbPropsFileSmHome);
				readDbPropertiesFile(cfg, properties, fileInputStream);

			} else {
				
				dbPropsFile = new File(dbPropsFileLocation);
				fileInputStream = new FileInputStream(dbPropsFile);
				Logger.logInfo(ENTITY_NAME, "Loading database properties from "+dbPropsFileLocation);
				
				readDbPropertiesFile(cfg, properties, fileInputStream);
			}
		} catch (FileNotFoundException exception) {
        	EliteSMStartupStatus.updateStatus(SM_MODULE_CONSTANTS.DCC, SM_MODULE_STATUS.FAILED);
        	throw exception;
        }catch (Exception e) {
        	throw e;
        }finally{
        	Closeables.closeQuietly(fileInputStream);
        }
    
    }

	private static void readDbPropertiesFile(Configuration cfg, Properties properties, FileInputStream fileInputStream)
			throws NoSuchEncryptionException, Exception {
		
		EliteSMStartupStatus.updateStatus(SM_MODULE_CONSTANTS.DCC, SM_MODULE_STATUS.SUCCESS);
		EliteSMStartupStatus.updateStatus(SM_MODULE_CONSTANTS.HES, SM_MODULE_STATUS.PROCESSING);
		properties.load(fileInputStream);
		/*
		 * As per security concern with password which is store in plain
		 * format in database.properties file we store it in encrypted
		 * format , so hibernate can't understand that so we first get
		 * encrypted password than decrypt it and give to hibernate session
		 * factory.
		 */
		
		String encryptedPassword = properties.getProperty("hibernate.connection.password");
		if (Strings.isNullOrBlank(encryptedPassword) == false) {
			// set plain password to property
			properties.setProperty("hibernate.connection.password",getPlainPassword(encryptedPassword));
		}
		
		cfg.setProperties(properties);
		cfg.configure(CONFIG_FILE_LOCATION);
		sessionFactory = cfg.buildSessionFactory();
		EliteSMStartupStatus.updateStatus(SM_MODULE_CONSTANTS.HES, SM_MODULE_STATUS.SUCCESS);
	}

	private static String getPlainPassword(String encryptedPassword)
			throws NoSuchEncryptionException, Exception {
		String plainPassoword;
		try {
			plainPassoword = PasswordEncryption.getInstance().decrypt(encryptedPassword,PasswordEncryption.ELITE_PASSWORD_CRYPT);
		} catch (DecryptionNotSupportedException e) {
			throw new Exception("Failed to decrypt password, Reason: "+e.getMessage());
		} catch (DecryptionFailedException e) {
			throw new Exception("Failed to decrypt password, Reason: "+e.getMessage());
		}
		return plainPassoword;
	}
    
    public static void reIntializeSessionFactory(){
    	
    	 try {
			if (sessionFactory != null && sessionFactory.isClosed()) {
				Logger.logInfo(ENTITY_NAME,"Reinitializing hibernate session factory");
				
				reInitConfiguration = new Configuration();
				
				buildSessionFacotry(reInitConfiguration);
				Logger.logInfo(ENTITY_NAME,"Reinitializing of hibernate session factory is completed");
			}
		} catch (Exception e) {
			EliteSMStartupStatus.updateStatus(SM_MODULE_CONSTANTS.HES,SM_MODULE_STATUS.FAILED);
			Logger.logError(ENTITY_NAME,"Unable to build hibernate sessionfactory, Reason: ");
			Logger.logTrace(ENTITY_NAME, e);
		}
    }
    
    public static void closeSessionFactory(){
    	if(sessionFactory != null){
    		Logger.logInfo(ENTITY_NAME,"Closing hibernate session factory");
    		sessionFactory.close();
    		Logger.logInfo(ENTITY_NAME,"Closed hibernate session factory");
    	}
    }
    
    private HibernateSessionFactory(){        
    }
    
}
