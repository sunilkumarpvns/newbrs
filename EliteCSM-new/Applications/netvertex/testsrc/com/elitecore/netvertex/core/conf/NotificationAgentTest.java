package com.elitecore.netvertex.core.conf;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.db.Transaction;
import com.elitecore.core.commons.utilx.db.TransactionException;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.systemx.esix.FakeTaskScheduler;
import com.elitecore.corenetvertex.constants.NotificationRecipient;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.service.notification.Template;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.util.DerbyUtil;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.Notification;
import com.elitecore.netvertex.core.NotificationAgent;
import com.elitecore.netvertex.core.conf.impl.DBDataSourceImpl;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.constant.NotificationConstants;
import com.elitecore.netvertex.core.notification.NotificationAgentDBOperation;
import com.elitecore.netvertex.core.notification.NotificationServiceFactory;
import com.elitecore.netvertex.service.notification.EmailSender;
import com.elitecore.netvertex.service.notification.NotificationSenderFactory;
import com.elitecore.netvertex.service.notification.NotificationServiceContext;
import com.elitecore.netvertex.service.notification.NotificationServiceCounters;
import com.elitecore.netvertex.service.notification.SMSSender;
import com.elitecore.netvertex.service.notification.conf.EmailAgentConfiguration;
import com.elitecore.netvertex.service.notification.conf.SMSAgentConfiguration;
import com.elitecore.netvertex.service.notification.conf.impl.NotificationServiceConfigurationImpl;
import com.elitecore.netvertex.service.notification.data.NotificationEntity;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class NotificationAgentTest {

    private static DummyNetvertexServerContextImpl serverContext;
    private static NetvertexServerGroupConfiguration netvertexServerGroupConfiguration;
    private DummyNetvertexServerConfiguration serverConfiguration;
    private EmailAgentConfiguration emailAgentConfiguration;
    private SMSAgentConfiguration smsAgentConfiguration;
    private NotificationAgent notificationAgent;
    private PCRFResponse pcrfResponse ;
    private static TransactionFactory transactionFactory;
    private NotificationServiceCounters notificationServiceCounters;


    @Rule public ExpectedException exception = ExpectedException.none();

    @Mock private NotificationSenderFactory senderFactory;
    @Mock private EmailSender emailSender;
    @Mock private SMSSender smsSender;

    private static final String DS_NAME = "NetvertexServerDB";
    private static final String CREATE_QUERY = "CREATE TABLE TBLT_NOTIFICATION_QUEUE(\n" +
            "  ID               VARCHAR(36),\n" +
            "  TO_EMAIL_ADDRESS VARCHAR(256),\n" +
            "  TO_SMS_ADDRESS   VARCHAR(256),\n" +
            "  EMAIL_SUBJECT    VARCHAR(256),\n" +
            "  EMAIL_DATA       VARCHAR(4000),\n" +
            "  SMS_DATA         VARCHAR(4000),\n" +
            "  EMAIL_STATUS     VARCHAR(128),\n" +
            "  SMS_STATUS       VARCHAR(128),\n" +
            "  SOURCE_ID        VARCHAR(256),\n" +
            "  PCRF_RESPONSE     BLOB,\n" +
            "  SERVER_INSTANCE_ID   VARCHAR(36),\n" +
            "  VALIDITY_DATE        TIMESTAMP,\n" +
            "  NOTIFICATION_RECIPIENT NUMERIC,\n" +
            "  SUBSCRIBER_IDENTITY    VARCHAR(256),\n" +
            "  TIMESTAMP              TIMESTAMP,\n" +
            "  CONSTRAINT PK_MNOTIFICATION_QUEUE PRIMARY KEY (ID)\n" +
            ")";

    private static final String CREATE_HISTORY_QUERY = "CREATE TABLE TBLT_NOTIFICATION_HISTORY(\n" +
                    " ID                     VARCHAR(36),\n" +
                    " TO_EMAIL_ADDRESS       VARCHAR(256),\n" +
                    " TO_SMS_ADDRESS         VARCHAR(256),\n" +
                    " EMAIL_SUBJECT          VARCHAR(256),\n" +
                    " EMAIL_DATA             VARCHAR(4000),\n" +
                    " SMS_DATA               VARCHAR(4000),\n" +
                    " EMAIL_STATUS           VARCHAR(128),\n" +
                    " SMS_STATUS             VARCHAR(128),\n" +
                    " SOURCE_ID              VARCHAR(256),\n" +
                    " PCRF_RESPONSE          BLOB,\n" +
                    " SERVER_INSTANCE_ID     VARCHAR(36),\n" +
                    " VALIDITY_DATE          TIMESTAMP,\n" +
                    " NOTIFICATION_RECIPIENT NUMERIC,\n" +
                    " SUBSCRIBER_IDENTITY    VARCHAR(256),\n" +
                    " TIMESTAMP              TIMESTAMP\n" +
            ")";

    private static final String  SELECT_QUERY = "SELECT * FROM TBLT_NOTIFICATION_QUEUE WHERE EMAIL_STATUS = ? AND SMS_STATUS = ? AND SERVER_INSTANCE_ID = ? ";

    private static final String SELECT_HISTORY_QUERY = "SELECT * FROM TBLT_NOTIFICATION_HISTORY WHERE EMAIL_STATUS = ? AND SMS_STATUS = ? AND SERVER_INSTANCE_ID = ? ";

    @BeforeClass
    public static void setUpDB() throws DatabaseTypeNotSupportedException, DatabaseInitializationException, TransactionException, SQLException {

        serverContext = spy(new DummyNetvertexServerContextImpl());
        serverContext.setServerInstanceId(UUID.randomUUID().toString());

        String url = "jdbc:derby:memory:TestingDB;create=true";

        DBDataSource dbDataSource = new DBDataSourceImpl("0", DS_NAME, url,
                "", "", 3, 3, 3000, 10000, 2000);

        DBConnectionManager.getInstance(dbDataSource.getDataSourceName()).init(dbDataSource, new FakeTaskScheduler());

        DummyNetvertexServerConfiguration serverConfiguration = serverContext.getServerConfiguration();
        netvertexServerGroupConfiguration = serverConfiguration.spyGroupConfiguration();
        when(netvertexServerGroupConfiguration.getNotificationDS()).thenReturn(dbDataSource);


        transactionFactory = DBConnectionManager.getInstance(dbDataSource.getDataSourceName()).getTransactionFactory();
        Transaction transaction = transactionFactory.createTransaction();
        transaction.begin();

        PreparedStatement preparedStatement = transaction.prepareStatement(CREATE_QUERY);
        preparedStatement.execute();

        preparedStatement = transaction.prepareStatement(CREATE_HISTORY_QUERY);
        preparedStatement.execute();

        transaction.commit();
        transaction.end();
    }

    @AfterClass
    public static void tearDown(){
        DerbyUtil.closeDerby("TestingDB");
    }

    @After
    public void cleanup(){
       notificationAgent.stop();

    }
    @Before
    public void setUp() throws TransactionException, SQLException, DatabaseTypeNotSupportedException, DatabaseInitializationException, IOException, InitializationFailedException {
        MockitoAnnotations.initMocks(this);

        pcrfResponse = new PCRFResponseImpl();
        serverConfiguration = serverContext.getServerConfiguration();

        emailAgentConfiguration = new EmailAgentConfiguration(null, "127.0.0.1", 1000, null, null);
        smsAgentConfiguration = new SMSAgentConfiguration("https://1.1.1.1", "");
        serverConfiguration.setNotificationServiceConfiguration(createDummyNotificationServiceConfiguration(emailAgentConfiguration, smsAgentConfiguration));

        NotificationServiceContext serviceContext = new NotificationServiceContext() {

            @Override
            public NetVertexServerContext getServerContext() {
                return serverContext;
            }

            @Override
            public NotificationServiceConfigurationImpl getNotificationServiceConfiguration() {
                return serverContext.getServerConfiguration().getNotificationServiceConfiguration();
            }
        };
        notificationServiceCounters = spy(new NotificationServiceCounters(serviceContext));
        notificationServiceCounters.init();

        NotificationServiceFactory factory = new NotificationServiceFactory(serverContext,senderFactory);
        notificationAgent =  factory.createAgent();

        doReturn(smsSender).when(senderFactory).createSMSNotificationFactory(any(NotificationServiceContext.class), any(NotificationServiceCounters.class));
        doReturn(emailSender).when(senderFactory).createEmailNotificationFactory(any(NotificationServiceContext.class), any(NotificationServiceCounters.class));
        
    }

    @Test
    public void sendEmailAndSMSNotificationThroughDB() throws TransactionException, SQLException, DatabaseTypeNotSupportedException, DatabaseInitializationException, IOException, CloneNotSupportedException {

        serverConfiguration.setNotificationServiceConfiguration(createDummyNotificationServiceConfiguration(null, null));
        SPRInfo sprInfo = new SPRInfoImpl.SPRInfoBuilder()
                .withEmail(pcrfResponse.getAttribute(PCRFKeyConstants.SUB_EMAIL.val))
                .withPhone(pcrfResponse.getAttribute(PCRFKeyConstants.SUB_PHONE.val))
                .build();

        when(serverContext.getSPRInfo(pcrfResponse.getAttribute(PCRFKeyConstants.SUB_PARENTID.getVal()))).thenReturn(sprInfo);
        notificationAgent.sendNotification(getTemplate(), getTemplate(), getPcrfResponse(), getValidityDate(), NotificationRecipient.SELF);

        Transaction transaction = transactionFactory.createTransaction();
        transaction.begin();

        PreparedStatement preparedStatement = transaction.prepareStatement(SELECT_QUERY);
        preparedStatement.setString(1, NotificationConstants.PENDING);
        preparedStatement.setString(2, NotificationConstants.PENDING);
        preparedStatement.setString(3, serverContext.getServerInstanceId());
        ResultSet resultSet = preparedStatement.executeQuery();

        Assert.assertTrue(resultSet.getFetchSize() > 0);
        transaction.commit();
        transaction.end();
    }

    @Test
    public void sendNotificationReturnFalseWhenCloneNotSupportedExceptionOccurWhileCloningPCRFResponse() throws  CloneNotSupportedException {

        serverConfiguration.setNotificationServiceConfiguration(createDummyNotificationServiceConfiguration(null, null));
        SPRInfo sprInfo = new SPRInfoImpl.SPRInfoBuilder()
                .withEmail(pcrfResponse.getAttribute(PCRFKeyConstants.SUB_EMAIL.val))
                .withPhone(pcrfResponse.getAttribute(PCRFKeyConstants.SUB_PHONE.val))
                .build();

        when(serverContext.getSPRInfo(pcrfResponse.getAttribute(PCRFKeyConstants.SUB_PARENTID.getVal()))).thenReturn(sprInfo);

        pcrfResponse = spy(getPcrfResponse());
        doThrow(new CloneNotSupportedException()).when(pcrfResponse).clone();

        assertFalse(notificationAgent.sendNotification(getTemplate(), getTemplate(), getPcrfResponse(), getValidityDate(), NotificationRecipient.SELF));

    }

    @Test
    public void whenInMemoryNotificationSendFailsThenSendEmailAndSMSNotificationThroughDB() throws TransactionException, SQLException, DatabaseTypeNotSupportedException, DatabaseInitializationException, IOException {
        prepareNotificationStatusOfSenders(false);

        notificationAgent.sendNotification(getTemplate(), getTemplate(), getPcrfResponse(), getValidityDate(), NotificationRecipient.SELF);

        Transaction transaction = transactionFactory.createTransaction();
        transaction.begin();

        PreparedStatement preparedStatement = transaction.prepareStatement(SELECT_QUERY);
        preparedStatement.setString(1, NotificationConstants.PENDING);
        preparedStatement.setString(2, NotificationConstants.PENDING);
        preparedStatement.setString(3, serverContext.getServerInstanceId());
        ResultSet resultSet = preparedStatement.executeQuery();

        Assert.assertTrue(resultSet.getFetchSize() > 0);
        transaction.commit();
        transaction.end();
    }

    @Test
    public void onSuccessfulNotificationSentInsertNotificatonIntoHistory() throws TransactionException, SQLException {
        prepareNotificationStatusOfSenders(true);

        notificationAgent.sendNotification(getTemplate(), getTemplate(), getPcrfResponse(), getValidityDate(), NotificationRecipient.SELF);

        Transaction transaction = transactionFactory.createTransaction();
        transaction.begin();

        PreparedStatement preparedStatement = transaction.prepareStatement(SELECT_HISTORY_QUERY);
        preparedStatement.setString(1, NotificationConstants.FAILED);
        preparedStatement.setString(2, NotificationConstants.SENT);
        preparedStatement.setString(3, serverContext.getServerInstanceId());
        ResultSet resultSet = preparedStatement.executeQuery();

        Assert.assertTrue(resultSet.getFetchSize() > 0);
        transaction.commit();
        transaction.end();
    }

    @Test
    public void onGracefulShutdownInsertInMemoryNotificationsIntoNotificationQueueInDb() throws TransactionException, SQLException, InitializationFailedException {

        doReturn(true).when(emailSender).sendEmail(any(NotificationEntity.class));
        doReturn(true).when(smsSender).sendSMS(any(NotificationEntity.class));

        int NOTIFICATION_QUEUE_SIZE = 5;
        LinkedBlockingQueue<Runnable> notificationQueue = new LinkedBlockingQueue<>(NOTIFICATION_QUEUE_SIZE);
        for(int i=0; i<NOTIFICATION_QUEUE_SIZE; i++){
            notificationQueue.add(notificationAgent.new InMemoryOperationTask(createNotification()));
        }
        ThreadPoolExecutor notificationThreadPoolExecutor = mock(ThreadPoolExecutor.class);
        NotificationAgentDBOperation notificationAgentDBOperation = mock(NotificationAgentDBOperation.class);
        NotificationAgent notificationAgent = new NotificationAgent(serverContext,  notificationQueue, notificationThreadPoolExecutor, notificationAgentDBOperation,null,null,null,emailSender,smsSender);
        notificationAgent.init();
        notificationAgent.stop();

        verify(notificationAgentDBOperation, times(5)).insertIntoNotificationQueue(any());

   }


    @Test
    public void onFailOverItShouldInsertNotificationToQueueByDBOperation() throws TransactionException, SQLException, InitializationFailedException {

        prepareDisableNotificationService();

        prepareNotificationStatusOfSenders(false);


        LinkedBlockingQueue<Runnable> notificationQueue = new LinkedBlockingQueue<>(1);

        FakeExecutor fakeExecutor = new FakeExecutor();

        NotificationAgentDBOperation notificationAgentDBOperation = mock(NotificationAgentDBOperation.class);
        NotificationAgent notificationAgent = new NotificationAgent(serverContext,  notificationQueue, fakeExecutor, notificationAgentDBOperation,null,null,notificationServiceCounters,emailSender,smsSender);

        notificationAgent.init();
        notificationAgent.sendNotification(getTemplate(), getTemplate(), getPcrfResponse(), getValidityDate(), NotificationRecipient.SELF);

        notificationAgent.stop();
        verify(notificationAgentDBOperation, times(1)).insertIntoNotificationQueue(any());

    }

    @Test
    public void notProcessNotificationIfAddressAreNotConfiguredInPCRFResponse() throws TransactionException, SQLException, InitializationFailedException {

        prepareDisableNotificationService();

        prepareNotificationStatusOfSenders(false);


        LinkedBlockingQueue<Runnable> notificationQueue = new LinkedBlockingQueue<>(1);

        FakeExecutor fakeExecutor = new FakeExecutor();

        NotificationAgentDBOperation notificationAgentDBOperation = mock(NotificationAgentDBOperation.class);
        NotificationAgent notificationAgent = new NotificationAgent(serverContext,  notificationQueue, fakeExecutor, notificationAgentDBOperation,null,null,notificationServiceCounters,emailSender,smsSender);

        notificationAgent.init();
        notificationAgent.sendNotification(getTemplate(), getTemplate(),pcrfResponse, getValidityDate(), NotificationRecipient.SELF);

        notificationAgent.stop();
        verifyZeroInteractions(notificationAgentDBOperation);
    }

    private void prepareNotificationStatusOfSenders(boolean status) {
        doReturn(status).when(emailSender).sendEmail(any(NotificationEntity.class));
        doReturn(status).when(smsSender).sendSMS(any(NotificationEntity.class));
    }

    private void prepareDisableNotificationService() {
        NotificationServiceConfigurationImpl notificationServiceConfiguration = spy(serverContext.getServerConfiguration().getNotificationServiceConfiguration());

        when(notificationServiceConfiguration.isSMSNotificationEnabled()).thenReturn(false);
        when(notificationServiceConfiguration.isEmailNotificationEnabled()).thenReturn(false);
    }


    public Template getTemplate(){
        return new Template(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
    }

    public PCRFResponse getPcrfResponse() {
        pcrfResponse.setAttribute(PCRFKeyConstants.SUB_EMAIL.val, "a@a.com");
        pcrfResponse.setAttribute(PCRFKeyConstants.SUB_PHONE.val, "1234567899");
        return pcrfResponse;
    }

    public Timestamp getValidityDate(){
        return new Timestamp(System.currentTimeMillis());
    }

    private NotificationServiceConfigurationImpl createDummyNotificationServiceConfiguration(EmailAgentConfiguration emailAgentConfiguration, SMSAgentConfiguration smsAgentConfiguration){
        return new NotificationServiceConfigurationImpl(nextInt(1, Integer.MAX_VALUE), nextInt(1, Integer.MAX_VALUE), nextInt(10, Integer.MAX_VALUE), emailAgentConfiguration, smsAgentConfiguration);
    }

    public Notification createNotification() {
        Template emailTemplate = new Template(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        Template smsTemplate = new Template(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        pcrfResponse.setAttribute(PCRFKeyConstants.SUB_EMAIL.val, "a@a.com");
        pcrfResponse.setAttribute(PCRFKeyConstants.SUB_PHONE.val, "1234567899");
        return new Notification(emailTemplate, smsTemplate, pcrfResponse, new Timestamp(System.currentTimeMillis()), NotificationRecipient.SELF, NotificationConstants.PENDING.toString(), NotificationConstants.PENDING.toString());
    }



    private class FakeExecutor implements ExecutorService{


        @Override
        public void shutdown() {

        }

        @Override
        public List<Runnable> shutdownNow() {
            return null;
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            return null;
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            return null;
        }

        @Override
        public Future<?> submit(Runnable task) {
            return null;
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
            return null;
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
            return null;
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
            return null;
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }

        @Override
        public void execute(Runnable command) {
                command.run();
        }
    }
}
