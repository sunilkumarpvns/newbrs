package com.elitecore.netvertex.core.notification;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.db.DBConnectionManagerSpy;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.DerbyConnectionProvider;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.db.Transaction;
import com.elitecore.core.commons.utilx.db.TransactionErrorCode;
import com.elitecore.core.commons.utilx.db.TransactionException;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.systemx.esix.FakeTaskScheduler;
import com.elitecore.corenetvertex.constants.NotificationRecipient;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.service.notification.Template;
import com.elitecore.corenetvertex.util.DerbyUtil;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.Notification;
import com.elitecore.netvertex.core.conf.NetvertexServerGroupConfiguration;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.constant.NotificationConstants;
import com.elitecore.netvertex.core.util.DbConnectionManagerRepository;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class NotificationHistoryQueueOperationTest {


    private DummyNetvertexServerContextImpl serverContext;
    private NetvertexServerGroupConfiguration netvertexServerGroupConfiguration;
    private FakeTaskScheduler notificationExecutor = spy(new FakeTaskScheduler());
    private FakeTaskScheduler historyExecutor = spy(new FakeTaskScheduler());
    private DerbyConnectionProvider derbyConnectionProvider;
    private DBConnectionManagerSpy dbConnectionManagerSpy;
    private DbConnectionManagerRepository dbConnectionManagerRepository;
    private DBDataSource notificationDataSource;
    private NotificationAgentDBOperation notificationAgentDBOperation;
    private TransactionFactory transactionFactory;
    private static final int QUEUE_SIZE = 5;

    @Rule
    public ExpectedException exception = ExpectedException.none();
    private static final String SELECT_COUNT_QUERY = "SELECT COUNT(1) COUNT FROM TBLT_NOTIFICATION_HISTORY ";
    private static final String CREATE_QUERY = "CREATE TABLE TBLT_NOTIFICATION_HISTORY(\n" +
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


    private static final String CONNECTION_NOT_FOUND = "CONNECTION_NOT_FOUND";
    private static final String BEGIN = "BEGIN";
    private static final String COMMIT = "COMMIT";
    private static final String PREPARED_STATEMENT = "PREPARED_STATEMENT";
    private PCRFResponse pcrfResponse;


    @Before
    public  void setUpDB() throws DatabaseTypeNotSupportedException, DatabaseInitializationException, InitializationFailedException, TransactionException, SQLException {


        serverContext = DummyNetvertexServerContextImpl.spy();

        derbyConnectionProvider = new DerbyConnectionProvider(new FakeTaskScheduler());
        dbConnectionManagerSpy = DBConnectionManagerSpy.create(derbyConnectionProvider.getConnectionManager());

        DummyNetvertexServerConfiguration serverConfiguration = serverContext.getServerConfiguration();
        netvertexServerGroupConfiguration = serverConfiguration.spyGroupConfiguration();
        notificationDataSource = derbyConnectionProvider.getDataSource();
        when(netvertexServerGroupConfiguration.getNotificationDS()).thenReturn(notificationDataSource);

        dbConnectionManagerRepository = dataSourceKey -> dbConnectionManagerSpy.getSpiedInstance();
        notificationAgentDBOperation = spy(NotificationAgentDBOperation.create(serverContext,notificationExecutor,historyExecutor,dbConnectionManagerRepository,QUEUE_SIZE));
        pcrfResponse = new PCRFResponseImpl();

        transactionFactory = dbConnectionManagerSpy.getSpiedInstance().getTransactionFactory();
        Transaction transaction = transactionFactory.createTransaction();
        transaction.begin();

        PreparedStatement preparedStatement = transaction.prepareStatement(CREATE_QUERY);
        preparedStatement.execute();

        transaction.commit();
        transaction.end();


    }


    @After
    public void tearDown() {
        DerbyUtil.closeDerby("TestingDB");
    }



    public class NoNotificationWillbeInsertedToDBWhen{


        @Test
        public void transactionFactoryIsNotAlive() throws TransactionException, SQLException {
            doReturn(false).when(transactionFactory).isAlive();
            notificationAgentDBOperation.insertIntoNotificationHistory(createNotification());
            historyExecutor.tick();
            verifyNoInsertionInDB();
        }


        public class TransactionExceptionOccurOn{
            @Test
            public void beginingOfTransaction() throws TransactionException, SQLException {
                Transaction transaction = prepareTransactionWithException(BEGIN);
                notificationAgentDBOperation.insertIntoNotificationHistory(createNotification());
                historyExecutor.tick();
                verifyTransactionMustEnd(transaction);
                verifyNoInsertionInDB();
            }


            @Test
            public void creatingPreparedStatement() throws TransactionException, SQLException {
                Transaction transaction = prepareTransactionWithException(PREPARED_STATEMENT);
                notificationAgentDBOperation.insertIntoNotificationHistory(createNotification());
                historyExecutor.tick();
                verifyTransactionMustEnd(transaction);
                verifyNoInsertionInDB();
            }


            @Test
            public void committingTransaction() throws TransactionException, SQLException {
                Transaction transaction = prepareTransactionWithException(COMMIT);
                notificationAgentDBOperation.insertIntoNotificationHistory(createNotification());
                historyExecutor.tick();
                verifyTransactionMustEnd(transaction);
                verifyNoInsertionInDB();

            }


            @Test
            public void connectionNotFound() throws TransactionException, SQLException {
                Transaction transaction = prepareTransactionWithException(CONNECTION_NOT_FOUND);
                notificationAgentDBOperation.insertIntoNotificationHistory(createNotification());
                historyExecutor.tick();
                verifyTransactionMustEnd(transaction);
                verifyNoInsertionInDB();
            }
        }

        public class SQLExceptionOccurOn{

            @Test
            public void executingBatchOfPreparedStatement() throws TransactionException, SQLException {
                PreparedStatement preparedStatement = mock(PreparedStatement.class);
                SQLException sqlException = new SQLException();
                doThrow(sqlException).when(preparedStatement).executeBatch();
                Transaction transaction = spy(transactionFactory.createTransaction());
                when(transaction.isDBDownSQLException(sqlException)).thenReturn(true);
                when(transactionFactory.createTransaction()).thenReturn(transaction);
                doReturn(preparedStatement).when(transaction).prepareStatement(anyString());
                notificationAgentDBOperation.insertIntoNotificationHistory(createNotification());
                historyExecutor.tick();
                verify(transaction).rollback();
                verify(transaction).markDead();
                verify(transaction).end();
                verifyNoInsertionInDB();
            }
        }

    }


    @Test
    public void notInsertInDBIfNoNotificationQueuedTest() throws TransactionException,SQLException{
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        Transaction transaction = spy(transactionFactory.createTransaction());
        when(transactionFactory.createTransaction()).thenReturn(transaction);
        doReturn(preparedStatement).when(transaction).prepareStatement(anyString());
        verify(preparedStatement,times(0)).executeBatch();
        historyExecutor.tick();
        verifyTransactionMustEnd(transaction);
        verifyNoInsertionInDB();
    }



    @Test
    public void notInsertInDBIfNoNotificationQueued() throws TransactionException,SQLException{
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        Transaction transaction = spy(transactionFactory.createTransaction());
        when(transactionFactory.createTransaction()).thenReturn(transaction);
        doReturn(preparedStatement).when(transaction).prepareStatement(anyString());
        verify(preparedStatement,times(0)).executeBatch();
        historyExecutor.tick();
        verifyTransactionMustEnd(transaction);
        verifyNoInsertionInDB();
    }



    @Test
    public void executeBatchWhenQueueIsFullAndMoreNotificationAddedToQueueWhileExecuting() throws TransactionException, SQLException {
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        Transaction transaction = spy(transactionFactory.createTransaction());
        when(transactionFactory.createTransaction()).thenReturn(transaction);
        doNothing().when(transaction).commit();
        doNothing().when(transaction).end();

        doAnswer(invocation -> {
            notificationAgentDBOperation.insertIntoNotificationHistory(createNotification());
            return null;
        }).when(preparedStatement).executeBatch();

        doReturn(preparedStatement).when(transaction).prepareStatement(anyString());

        for(int i =0 ; i <= QUEUE_SIZE; i++ ){
            notificationAgentDBOperation.insertIntoNotificationHistory(createNotification());
        }
        historyExecutor.tick();
        verify(preparedStatement,times(2)).executeBatch();
        verifyTransactionMustEnd(transaction);

    }

    @Test
    public void notInsertsNotificationIntoDBWhenIOExceptionOccurWhilePreparingBatch() throws TransactionException, SQLException {
        Transaction transaction = getSpiedTransaction();
        when(transactionFactory.createTransaction()).thenReturn(transaction);
        PCRFResponseWritable pcrfResponseWritable = new PCRFResponseWritable();
        notificationAgentDBOperation.insertIntoNotificationHistory(createNotification(pcrfResponseWritable));
        historyExecutor.tick();
        verifyTransactionMustEnd(transaction);
        verifyNoInsertionInDB();
    }

    private void verifyTransactionMustEnd(Transaction transaction) throws TransactionException {
        verify(transaction).end();
    }


    @Test
    public void insertsNotificationIntoDB() throws TransactionException, SQLException {
        Transaction transaction = getSpiedTransaction();
        notificationAgentDBOperation.insertIntoNotificationHistory(createNotification());
        historyExecutor.tick();
        verifyTransactionMustEnd(transaction);
        verifyNoOfNotificationInDB(1);
    }

    private Template getTemplate(){
        return new Template(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
    }

    private PCRFResponse getPcrfResponse() {
        pcrfResponse.setAttribute(PCRFKeyConstants.SUB_EMAIL.val, "a@a.com");
        pcrfResponse.setAttribute(PCRFKeyConstants.SUB_PHONE.val, "1234567899");
        return pcrfResponse;
    }


    private Notification createNotification(PCRFResponse pcrfResponse) {
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



    private Notification createNotification() {
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


    private Transaction prepareTransactionWithException(String on) throws TransactionException {
        Transaction transaction = mock(Transaction.class);

        doReturn(transaction).when(transactionFactory).createTransaction();
        doReturn(true).when(transactionFactory).isAlive();
        when(transactionFactory.createTransaction()).thenReturn(transaction);

        if (on.equals(PREPARED_STATEMENT)) {
            doThrow(new TransactionException("Test Exception")).when(transaction).prepareStatement(Mockito.anyString());
        } else if (on.equals(BEGIN)) {
            doThrow(new TransactionException("Test Exception")).when(transaction).begin();
        } else if (on.equals(COMMIT)) {
            doThrow(new TransactionException("Test Exception")).when(transaction).commit();
        } else if (on.equals(CONNECTION_NOT_FOUND)) {
            doThrow(new TransactionException("Test Exception", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).begin();
        }
        return transaction;
    }


    private void verifyNoInsertionInDB() throws TransactionException, SQLException {
        verifyNoOfNotificationInDB(0);
    }
    private void verifyNoOfNotificationInDB(int count) throws  TransactionException, SQLException{

        reset(transactionFactory);
        Transaction transaction = transactionFactory.createTransaction();
        transaction.begin();
        PreparedStatement preparedStatement = transaction.prepareStatement(SELECT_COUNT_QUERY);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        Assert.assertTrue(resultSet.getInt(1) == count);
        transaction.commit();
        transaction.end();
    }




    private class PCRFResponseWritable extends PCRFResponseImpl {

        private void writeObject(java.io.ObjectOutputStream out) throws IOException {
            throw new IOException("throw from test");
        }
    }

    private Transaction getSpiedTransaction(){
        Transaction transaction = spy(transactionFactory.createTransaction());
        when(transactionFactory.createTransaction()).thenReturn(transaction);
        return transaction;
    }
}
