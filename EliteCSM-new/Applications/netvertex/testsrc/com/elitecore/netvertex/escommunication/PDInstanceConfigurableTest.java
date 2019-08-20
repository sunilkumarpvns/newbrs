package com.elitecore.netvertex.escommunication;

import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.pm.HibernateConfigurationUtil;
import com.elitecore.corenetvertex.systeminformation.PDContextInformation;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PDInstanceConfigurableTest {
    private static final String DS_NAME = "test-DB";
    private int rowCount = 1;

    @Mock
    private ServerContext serverContext;
    @Mock
    private SessionFactory mockSessionFactory;

    private SessionFactory sessionFactory;
    private HibernateSessionFactory hibernateSessionFactory;
    @Before
    public void setup() throws Exception{
        serverContext = mock(ServerContext.class);
        prepareDB();
        insertRecordsInDB();
    }

    public void prepareDB() throws Exception{
        String ssid = UUID.randomUUID().toString();
        String url = "jdbc:h2:mem:" + ssid;
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.connection.url", url);
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        hibernateProperties.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        Configuration configuration = new Configuration();
        configuration.setProperties(hibernateProperties);
        HibernateConfigurationUtil.setConfigurationClasses(configuration);
        hibernateSessionFactory = HibernateSessionFactory.create("hibernate/test-hibernate.cfg.xml", hibernateProperties);
        sessionFactory = hibernateSessionFactory.getSessionFactory();

        mockSessionFactory = mock(SessionFactory.class);
    }

    @After
    public void tearDownConnection() throws Exception {
        hibernateSessionFactory.shutdown();;

    }

    public void insertRecordsInDB(){
        String address1 = "foo.bar.hello.world.fail.case,something.erie.com";
        String address2 = "0.0.0.0,1.2.3.4";

        PDContextInformation configuration1 =
                new PDContextInformation("host"+rowCount,address1,"80","contextPath"+rowCount);
        configuration1.setId(Integer.toString(rowCount++));

        PDContextInformation configuration2 =
                new PDContextInformation("host"+rowCount,address2,"90","contextPath"+rowCount);
        configuration2.setId(Integer.toString(rowCount++));

        Session session = sessionFactory.getCurrentSession();

        Transaction transaction = session.beginTransaction();
        session.save(configuration1);
        session.save(configuration2);

        transaction.commit();
    }

    @Test(expected = LoadConfigurationException.class)
    public void when_sql_or_any_other_exception_is_thrown_it_must_throw_LoadConfigurationException() throws LoadConfigurationException{
        try{
            when(mockSessionFactory.openSession()).thenThrow(new SQLException());
        } catch (Exception e){ }
        PDInstanceConfigurable pdInstanceConfigurable = new PDInstanceConfigurable(serverContext, mockSessionFactory);
        pdInstanceConfigurable.readConfiguration();
    }

    @Test
    public void create_and_init_configuration_successfully() throws LoadConfigurationException {
        PDInstanceConfigurable pdInstanceConfigurable = new PDInstanceConfigurable(serverContext, sessionFactory);
        pdInstanceConfigurable.readConfiguration();

        System.out.println(pdInstanceConfigurable.toString());
        Assert.assertEquals(2,pdInstanceConfigurable.getPDInstanceConfigurations().size());
    }
}
