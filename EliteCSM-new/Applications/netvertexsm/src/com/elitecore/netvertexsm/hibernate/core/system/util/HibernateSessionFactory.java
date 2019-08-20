package com.elitecore.netvertexsm.hibernate.core.system.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.elitecore.netvertexsm.util.EliteUtility;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.logger.Logger;

public class HibernateSessionFactory {
    
    private static String CONFIG_FILE_LOCATION = "config/hibernate.cfg.xml";
    private static String dbPropsFileLocation = EliteUtility.getSMHome() + File.separator +ConfigConstant.DATABASE_CONFIG_FILE_LOCATION;
    
    private static final String ENTITY_NAME ="HibernateSessionFactory";

    
    /* The single instance of hibernate configuration */
    private static final Configuration cfg = new Configuration();

    /* The single instance of hibernate SessionFactory */
    private static org.hibernate.SessionFactory sessionFactory;

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
                	FileInputStream fileInputStream = null;
                    try {
                        Logger.logInfo(ENTITY_NAME,"Reading Hibernate Configuration");
                        Properties properties = new Properties();
                        Logger.logInfo(ENTITY_NAME, "Loading database properties from "+dbPropsFileLocation);
                        File dbPropsFile = new File(dbPropsFileLocation);
                        fileInputStream = new FileInputStream(dbPropsFile);
                        properties.load(fileInputStream);
                        cfg.setProperties(properties);
                        cfg.configure(CONFIG_FILE_LOCATION);
                        cfg.setInterceptor(new NetvertexHibernateInterceptor());
                        sessionFactory = cfg.buildSessionFactory();
                    }catch (Exception e) {
                        Logger.logError(ENTITY_NAME,"Error Creating SessionFactory");
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

    public static void setDBPropsLocation(String location){
    	dbPropsFileLocation = location;
    }
    
    private HibernateSessionFactory(){        
    }

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}
