package com.elitecore.netvertex.core.notification;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DBConnectionManagerSpy;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.DerbyConnectionProvider;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.db.TransactionException;
import com.elitecore.core.systemx.esix.FakeTaskScheduler;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.corenetvertex.util.DerbyUtil;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.conf.NetvertexServerGroupConfiguration;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.util.DbConnectionManagerRepository;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class InstanceCreationTest {

    private DummyNetvertexServerContextImpl serverContext;
    private NetvertexServerGroupConfiguration netvertexServerGroupConfiguration;
    private  TaskScheduler notificationExecutor = spy(new FakeTaskScheduler());
    private  TaskScheduler historyExecutor = spy(new FakeTaskScheduler());
    private DerbyConnectionProvider derbyConnectionProvider;
    private DBConnectionManagerSpy dbConnectionManagerSpy;
    private DbConnectionManagerRepository dbConnectionManagerRepository;
    private DBDataSource notificationDataSource;
    @Rule public ExpectedException exception = ExpectedException.none();


    @Before
    public  void setUpDB() throws DatabaseTypeNotSupportedException, DatabaseInitializationException  {


        serverContext = DummyNetvertexServerContextImpl.spy();

        derbyConnectionProvider = new DerbyConnectionProvider(new FakeTaskScheduler());
        dbConnectionManagerSpy = DBConnectionManagerSpy.create(derbyConnectionProvider.getConnectionManager());

        DummyNetvertexServerConfiguration serverConfiguration = serverContext.getServerConfiguration();
        netvertexServerGroupConfiguration = serverConfiguration.spyGroupConfiguration();
        notificationDataSource = derbyConnectionProvider.getDataSource();
        when(netvertexServerGroupConfiguration.getNotificationDS()).thenReturn(notificationDataSource);

        dbConnectionManagerRepository = dataSourceKey -> dbConnectionManagerSpy.getSpiedInstance();

    }

    @After
    public void tearDown() {
        DerbyUtil.closeDerby("TestingDB");
    }


    public class FailedToCreateInstanceOfNotififcationAgentDBOperationWhen{


        @Test
        public void notificationDataSourceNotConfigured() throws InitializationFailedException {
            when(netvertexServerGroupConfiguration.getNotificationDS()).thenReturn(null);
            exception.expect(InitializationFailedException.class);
            exception.expectMessage("Notification DB datasource is not configured");
            NotificationAgentDBOperation.create(serverContext,notificationExecutor,historyExecutor,dbConnectionManagerRepository);

        }


        @Test
        public void dbConnectionMangerThrowsDataBaseTypeNotSupportedException() throws DatabaseInitializationException, DatabaseTypeNotSupportedException {
            doReturn(false).when(dbConnectionManagerSpy.getSpiedInstance()).isInitilized();
            dbConnectionManagerSpy.simulateDatabaseTypeUnsupported();
            try {
                NotificationAgentDBOperation.create(serverContext, notificationExecutor, historyExecutor, dbConnectionManagerRepository);
            }catch (InitializationFailedException e){

            }
            verify(dbConnectionManagerSpy.getSpiedInstance()).init(notificationDataSource,serverContext.getTaskScheduler());
        }
    }


    public class CreateInstanceOfNotificationAgentDBOperationWhenNotificationDataSourceIsConfiguredAndDBConnectionManager{

        @Test
        public void isAlreadyInitialized() throws InitializationFailedException, DatabaseTypeNotSupportedException, DatabaseInitializationException {
            doReturn(true).when(dbConnectionManagerSpy.getSpiedInstance()).isInitilized();
            assertNotNull(NotificationAgentDBOperation.create(serverContext, notificationExecutor, historyExecutor, dbConnectionManagerRepository));
            verify(dbConnectionManagerSpy.getSpiedInstance(),times(0)).init(notificationDataSource,serverContext.getTaskScheduler());
        }

        @Test
        public void notInitializedAndInitializesDataSource() throws InitializationFailedException, DatabaseTypeNotSupportedException, DatabaseInitializationException {
            doReturn(false).when(dbConnectionManagerSpy.getSpiedInstance()).isInitilized();
            assertNotNull(NotificationAgentDBOperation.create(serverContext, notificationExecutor, historyExecutor, dbConnectionManagerRepository));
            verify(dbConnectionManagerSpy.getSpiedInstance()).init(notificationDataSource,serverContext.getTaskScheduler());
        }


        @Test
        public void notInitializedAndInitializesDataSourceAndThrowsDatabaseInitializationException() throws Exception {
            doReturn(false).when(dbConnectionManagerSpy.getSpiedInstance()).isInitilized();
            dbConnectionManagerSpy.simulateDatabaseInitializationFailure();
            assertNotNull(NotificationAgentDBOperation.create(serverContext, notificationExecutor, historyExecutor, dbConnectionManagerRepository));
            verify(dbConnectionManagerSpy.getSpiedInstance()).init(notificationDataSource,serverContext.getTaskScheduler());
        }

    }
}