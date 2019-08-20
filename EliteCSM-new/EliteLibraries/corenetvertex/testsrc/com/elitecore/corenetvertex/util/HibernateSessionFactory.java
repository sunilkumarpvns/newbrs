package com.elitecore.corenetvertex.util;

import com.elitecore.corenetvertex.pm.HibernateConfigurationUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.context.internal.ThreadLocalSessionContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Properties;

public class HibernateSessionFactory {
    private static final String MODULE = "HIBERNATE-SESS-FACTORY";
    private com.elitecore.corenetvertex.core.hibernate.HibernateSessionFactory hibernateSessionFactory;

    public HibernateSessionFactory(com.elitecore.corenetvertex.core.hibernate.HibernateSessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;

    }

    public static HibernateSessionFactory create(String configFile, @Nullable Properties hibernateProperties) {
        Configuration cfg = new Configuration();
        cfg.configure(configFile);
        if(hibernateProperties != null) {
            cfg.addProperties(hibernateProperties);
        }
        HibernateConfigurationUtil.setConfigurationClasses(cfg);
        return new HibernateSessionFactory(com.elitecore.corenetvertex.core.hibernate.HibernateSessionFactory.create(cfg));
    }

    public static HibernateSessionFactory newInMemorySessionFactory(String ssid) {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.connection.url", "jdbc:h2:mem:" + ssid);
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        hibernateProperties.setProperty("hibernate.show_sql", "true");
        return create("hibernate/test-hibernate.cfg.xml", hibernateProperties);
    }

    public Session getSession() {
        Session session = hibernateSessionFactory.getSession();
        return session;
    }

    public SessionFactory getSessionFactory() {
        return hibernateSessionFactory.getSessionFactory();
    }


    private void syncWithDB(Session newSession) {
        newSession.flush();
        newSession.clear();
    }

    private <T> T load(Session newSession, String id, Class<T> object) {
        return (T) newSession.get(object,id);
    }

    public void shutdown() {
        try {
            if(getSession().isOpen() && getSession().getTransaction().isActive()) {
                getSession().getTransaction().rollback();
                getSession().close();
            }
        } finally {
            hibernateSessionFactory.shutdown();
            ThreadLocalSessionContext.unbind(hibernateSessionFactory.getSessionFactory());
        }


    }

    public void save(Object dataBalanceEntity) {
        Session newSession = hibernateSessionFactory.getNewSession();
        save(newSession, dataBalanceEntity);
        newSession.close();

    }

    public void save(Session session, Object dataBalanceEntity) {

        session.beginTransaction();

        session.save(dataBalanceEntity);

        syncWithDB(session);

        session.getTransaction().commit();

    }

    public <T> List<T> get(Class<?> tblmDataBalanceEntityClass) {
        Session session = hibernateSessionFactory.getNewSession();

        session.beginTransaction();

        return session.createCriteria(tblmDataBalanceEntityClass).list();

    }

}
