package com.elitecore.nvsmx.system.hibernate;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.db.DBVendors;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.nvsmx.system.db.NVSMXDBConnectionManager;
import com.elitecore.nvsmx.system.exception.SessionFactoryNotFoundException;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Aditya.Shrivastava
 *
 */

public class HibernateSessionFactory {

	private static final String MODULE = "HBR-SESS-FACTRY";


	private static SessionFactory sessionFactory;

	private static boolean initialized=false;

	private static String failReason = null;

	private static  Configuration cfg=null;

	/**
	 * This will initialize & verify the Session factory which will be use through out the application 
	 * when ever the session required it will be given by this session factory 
	 * @return
	 * @throws InitializationFailedException if session factory failed to initialize successfully
	 */
	public static synchronized void  buildSessionFactory(Configuration cfg) throws InitializationFailedException{
		try {
			if(sessionFactory !=null ){
				return ;
			}

			StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(getDatabaseConnetionSettings()).applySettings(cfg.getProperties()).build();
			sessionFactory =cfg.buildSessionFactory(serviceRegistry);

			verifySessionFactory();

			initialized=true;
			failReason = null;
			LogManager.getLogger().debug(MODULE,"Hibernate Session Factory initialized successfully");
		} catch (HibernateException e) {
			sessionFactory = null;
			initialized=false;
			shutdown();
			failReason = e.getMessage();
			throw new InitializationFailedException("Error while initializing Hibernate Session factory,Reason : "+e.getMessage(),e);
		}catch(Exception e){
			sessionFactory = null;
			initialized=false;
			failReason = e.getMessage();
			shutdown();
			throw new InitializationFailedException("Error while initializing Hibernate Session factory,Reason : "+e.getMessage(),e);
		}

	}

	/**
	 *  Returns a new Hibernate Core Session or the Session from the current thread
	 *  if you required a new session than use {@link HibernateSessionFactory#getNewSession()}
	 *  session obtained from getCurrentSession() method doesn't required to be closed explicitly
	 *  Session factory will take care to close all the session when factory itself is shutdown  
	 * @return {@link Session}
	 * @throws SessionFactoryNotFoundException if session factory is null 
	 */
	public  static Session getSession(){
		if(sessionFactory ==null){
			throw new SessionFactoryNotFoundException(failReason);
		}

		return sessionFactory.getCurrentSession();
	}

	/**
	 * Always Returns a new Hibernate Session 
	 * @return {@link Session}
	 * @throws SessionFactoryNotFoundException if session factory is null 
	 */
	public  static Session getNewSession() {
		if(sessionFactory ==null){
			throw new SessionFactoryNotFoundException(failReason);
		}
		return sessionFactory.openSession();
	}
	/**
	 * In Web Applications with Hibernate, when the context is destroyed or reload, it's necessary to destroy the session factory 
	 * to evict problems with connection pools, caches etc.
	 */
	public static void shutdown() {
		LogManager.getLogger().info(MODULE,"Closing Hibernate Session Factory");
		if(sessionFactory !=null && sessionFactory.isClosed()==false){
			sessionFactory.close();
		}
	}

	/**
	 * verify Hibernate SessionFactory
	 *
	 * @throws HibernateException for invalid configuration
	 *
	 */

	public static void verifySessionFactory() throws HibernateException{
		Session session = null;
		try{
			LogManager.getLogger().debug(MODULE, "Verifying Hibernate Session Factory");
			session= getSession();
			if(session.getTransaction().isActive() == false){
				session.beginTransaction();
				//NEED TO CHECK EXISTANCE OF STAFF TABLE BECAUSE IN CASE ONLY SCHEMA IS CREATED BUT SQL FILES ARE NOT EXECUTED
				initialized = CRUDOperationUtil.isStaffTableExist();
			}
		}catch(HibernateException e){
			initialized = false;
			throw e;
		}finally{
			if(session != null && session.isOpen()){
				session.close();
			}
		}
	}

	public static boolean isInitialized() {
		return initialized;
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static Configuration getConfiguration(){

		return cfg;

	}

	private static Map<String, Object> getDatabaseConnetionSettings() throws DatabaseTypeNotSupportedException {

		BasicDataSource basicDataSource = NVSMXDBConnectionManager.getInstance().getBasicDataSource();
		Map<String, Object> settings = new HashMap(2);
		settings.put(Environment.DATASOURCE, basicDataSource);
		settings.put(Environment.DIALECT,getSqlDialect(basicDataSource.getUrl()));
		return settings;
	}

	private static String getSqlDialect(String url) throws DatabaseTypeNotSupportedException {
		DBVendors dbVendors = DBVendors.fromUrl(url);
		return HibernateSupportedVendor.fromDBVendor(dbVendors).getDialect();
	}
}
