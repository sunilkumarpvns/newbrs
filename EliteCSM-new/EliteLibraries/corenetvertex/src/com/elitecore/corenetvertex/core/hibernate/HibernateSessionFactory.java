package com.elitecore.corenetvertex.core.hibernate;

import com.elitecore.commons.logging.LogManager;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

/**
 * @author Aditya.Shrivastava
 *
 */

public class HibernateSessionFactory {

	private static final String MODULE = "HBR-SESS-FACTRY";

	

	private SessionFactory sessionFactory;

	public HibernateSessionFactory(SessionFactory sessionFactory) {

		this.sessionFactory = sessionFactory;
	}

	/**
	 * This will initialize & verify the Session factory which will be use through out the application 
	 * when ever the session required it will be given by this session factory 
	 * @return
	 * @throws HibernateException if session factory failed to initialize successfully
	 */
	public static HibernateSessionFactory create(Configuration cfg) throws HibernateException {

		StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
		SessionFactory sessionFactory = cfg.buildSessionFactory(serviceRegistry);

		HibernateSessionFactory hibernateSessionFactory = new HibernateSessionFactory(sessionFactory);

		try {
			hibernateSessionFactory.verifySessionFactory();
			LogManager.getLogger().debug(MODULE, "Hibernate Session Factory initialized successfully");
		} catch (HibernateException e) {
			hibernateSessionFactory.shutdown();
			throw e;
		}
		return hibernateSessionFactory;

	}

	/**
	 *  Returns a new Hibernate Core Session or the Session from the current thread
	 *  if you required a new session than use {@link HibernateSessionFactory#getNewSession()}
	 *  session obtained from getCurrentSession() method doesn't required to be closed explicitly
	 *  Session factory will take care to close all the session when factory itself is shutdown  
	 * @return {@link Session}
	 *
	 */
	public  Session getSession(){
		return sessionFactory.getCurrentSession();
	}

	/**
	 * Always Returns a new Hibernate Session 
	 * @return {@link Session}
	 */
	public Session getNewSession() {
		return sessionFactory.openSession();
	}
	/**
	 * In Web Applications with Hibernate, when the context is destroyed or reload, it's necessary to destroy the session factory 
	 * to evict problems with connection pools, caches etc.
	 */
	public void shutdown() {
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

	private void verifySessionFactory() throws HibernateException {
		Session session = null;
		try {
			LogManager.getLogger().debug(MODULE, "Verifying Hibernate Session Factory");
			session = sessionFactory.openSession();
			if (session.getTransaction().isActive() == false) {
				session.beginTransaction();
			}

		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	public SessionFactory getSessionFactory(){
		return sessionFactory;
	}
	
}
