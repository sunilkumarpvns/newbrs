package com.elitecore.corenetvertex.spr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.DummyTransaction;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.ConcurrentLinkedQueue;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.unitils.reflectionassert.ReflectionAssert;


import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(HierarchicalContextRunner.class)
public class ABMFBatchOperationTest {
    private ABMFOperation abmfOperation;
    private static final String DS_NAME = "test-DB";
    private ABMFBatchOperation abmfBatchOperation;
    private PolicyRepository policyRepository;
    private DummyTransactionFactory transactionFactory;
    private RecordProcessor<ABMFBatchOperation.BatchOperationData> recordProcessor;
    private DBSingleRecordFailoverOperation<ABMFBatchOperation.BatchOperationData> failOverOperation;
    private ScheduledExecutorService batchExecutor;
    private CSVFailOverOperation<ABMFBatchOperation.BatchOperationData> csvFailOverOperation;
    private HibernateSessionFactory hibernateSessionFactory;
    ConcurrentLinkedQueue<ABMFBatchOperation.BatchOperationData> batchOperationQueue;


    @Before
    public void init() throws Exception {
        policyRepository = mock(PolicyRepository.class);
        abmfOperation = new ABMFOperation(mock(AlertListener.class), policyRepository,200, 300);
        batchExecutor = mock(ScheduledExecutorService.class);
        recordProcessor = mock(RecordProcessor.class);
        Executor executor = mock(Executor.class);
        csvFailOverOperation = spy(new CSVFailOverOperation<ABMFBatchOperation.BatchOperationData>(null, executor));
        batchOperationQueue = spy(new ConcurrentLinkedQueue<ABMFBatchOperation.BatchOperationData>(1004));
        failOverOperation = spy(new DBSingleRecordFailoverOperation<ABMFBatchOperation.BatchOperationData>(new SingleRecordOperation<ABMFBatchOperation.BatchOperationData>() {

            @Override
            public void process(ABMFBatchOperation.BatchOperationData dataToProcess, TransactionFactory transactionFactory) throws OperationFailedException {

            }
        }, null, transactionFactory, executor));

        doReturn(true).when(batchExecutor).awaitTermination(5, TimeUnit.SECONDS);
        setUpABMFBatch();
    }

    @After
    public void tearDownConnection() throws Exception {
        Connection connection = transactionFactory.getConnection();
        if(Objects.nonNull(connection)) {
            DBUtility.closeQuietly(connection);
        }
        hibernateSessionFactory.shutdown();

    }

    private void setUpABMFBatch() throws Exception {
        Properties hibernateProperties = new Properties();
        String ssid = "testDB";
        String url = "jdbc:h2:mem:" + ssid;
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, "jdbc:h2:mem:" + ssid, "", "", 1, 5000, 3000);
        transactionFactory =spy(new DummyTransactionFactory(dbDataSource));
        transactionFactory.createTransaction();
        Executor executor = mock(Executor.class);
        failOverOperation = spy(new DBSingleRecordFailoverOperation<>((SingleRecordOperation<ABMFBatchOperation.BatchOperationData>) (dataToProcess, transactionFactory) -> {

        }, null,transactionFactory, executor));

        abmfBatchOperation = new ABMFBatchOperation(transactionFactory, mock(AlertListener.class), null, failOverOperation, csvFailOverOperation,
                4, 1000, batchExecutor, 200, batchOperationQueue);
        abmfBatchOperation.init();
        abmfBatchOperation.reportBalance(createDummyBalance().getSubscriberIdentity(), Arrays.asList(createDummyBalance()), transactionFactory);
        hibernateProperties.setProperty("hibernate.connection.url", url);
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        hibernateSessionFactory = HibernateSessionFactory.create("hibernate/test-hibernate.cfg.xml", hibernateProperties);

    }

    public class OnTransactionException {

        private static final String CONNECTION_NOT_FOUND = "CONNECTION_NOT_FOUND";
        private static final String BEGIN = "BEGIN";
        private static final String COMMIT = "COMMIT";
        private static final String PREPARED_STATEMENT = "PREPARED_STATEMENT";


        @Test
        public void failOverWhilePreparedStatementCall() throws InterruptedException, TransactionException {

            setUpTransactionFailedException(PREPARED_STATEMENT);

            runBatch();
            verify(failOverOperation, times(1)).doFailover(Mockito.<Collection>any());
        }

        @Test
        public void csvFailOverWhileConnectionNotFound() throws InterruptedException, TransactionException {

            setUpTransactionFailedException(CONNECTION_NOT_FOUND);

            runBatch();
            verify(csvFailOverOperation, times(1)).doFailover(Mockito.<Collection>any());
        }

        @Test
        public void failOverWhileCommitCall() throws InterruptedException, TransactionException {

            setUpTransactionFailedException(COMMIT);

            runBatch();
            verify(failOverOperation, times(1)).doFailover(Mockito.<Collection>any());
        }

        private void setUpTransactionFailedException(String on) throws TransactionException {
            Transaction transaction = spy(new DummyTransaction(mock(Connection.class), null));

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
        }

    }

    public class OnDataSourceUnavailable {

        @Test
        public void csvFailOverWhileTransactionFactoryIsNotAlive() throws InterruptedException, TransactionException, SQLException {

            doReturn(false).when(transactionFactory).isAlive();
            runBatch();
            verify(csvFailOverOperation, times(1)).doFailover(Mockito.<Collection<ABMFBatchOperation.BatchOperationData>> any());
        }

    }


    public class OnQueueFull {

        private static final String SUBSCRIBER_ID = "SUB_ID";

        @Before
        public void setUp() {
            doReturn(false).when(batchOperationQueue).add(Mockito.any());
        }

        public class WhenTransactionFactoryAlive {

            @Before
            public void setUp() {
                when(transactionFactory.isAlive()).thenReturn(true);
            }

            @Test
            public void failoverOnReserve() throws Exception {
                List<NonMonetoryBalance> balances = createBalances();

                abmfBatchOperation.reserveBalance(SUBSCRIBER_ID, balances, transactionFactory);
                verify(failOverOperation, times(1)).doFailover(Mockito.<Collection<ABMFBatchOperation.BatchOperationData>> any());
            }

            @Test
            public void failoverOnReport() throws Exception {
                List<NonMonetoryBalance> balances = createBalances();
                abmfBatchOperation.reportBalance(SUBSCRIBER_ID, balances, transactionFactory);
                verify(failOverOperation, times(1)).doFailover(Mockito.<Collection<ABMFBatchOperation.BatchOperationData>> any());
            }

            @Test
            public void failoverOnReserveAndReport() throws Exception {
                List<NonMonetoryBalance> balances = createBalances();
                abmfBatchOperation.reportAndReserveBalance(SUBSCRIBER_ID, balances, transactionFactory);
                verify(failOverOperation, times(1)).doFailover(Mockito.<Collection<ABMFBatchOperation.BatchOperationData>> any());
            }

            @Test
            public void failoverOnReset() throws Exception {
                List<NonMonetoryBalance> balances = createBalances();
                abmfBatchOperation.resetBalance(SUBSCRIBER_ID, balances, transactionFactory);
                verify(failOverOperation, times(1)).doFailover(Mockito.<Collection<ABMFBatchOperation.BatchOperationData>> any());
            }

            @Test
            public void failoverOnDirectDebit() throws Exception {
                List<NonMonetoryBalance> balances = createBalances();
                abmfBatchOperation.directDebitBalance(SUBSCRIBER_ID, balances, transactionFactory);
                verify(failOverOperation, times(1)).doFailover(Mockito.<Collection<ABMFBatchOperation.BatchOperationData>> any());
            }

        }

        public class WhenTransactionFactoryNotAlive {

            @Before
            public void setUp() {
                when(transactionFactory.isAlive()).thenReturn(false);
            }

            @Test
            public void failoverReserve() throws Exception {

                List<NonMonetoryBalance> balances = createBalances();
                abmfBatchOperation.reserveBalance(SUBSCRIBER_ID, balances, transactionFactory);
                verify(csvFailOverOperation, times(1)).doFailover(Mockito.<Collection<ABMFBatchOperation.BatchOperationData>> any());
            }

            @Test
            public void failoverReport() throws Exception {
                List<NonMonetoryBalance> balances = createBalances();
                abmfBatchOperation.reportBalance(SUBSCRIBER_ID, balances, transactionFactory);
                verify(csvFailOverOperation, times(1)).doFailover(Mockito.<Collection<ABMFBatchOperation.BatchOperationData>> any());
            }

            @Test
            public void failoverOnReserveAndReport() throws Exception {
                List<NonMonetoryBalance> balances = createBalances();
                abmfBatchOperation.reportAndReserveBalance(SUBSCRIBER_ID, balances, transactionFactory);
                verify(csvFailOverOperation, times(1)).doFailover(Mockito.<Collection<ABMFBatchOperation.BatchOperationData>> any());
            }

            @Test
            public void failoverOnDirectDebit() throws Exception {
                List<NonMonetoryBalance> balances = createBalances();
                abmfBatchOperation.directDebitBalance(SUBSCRIBER_ID, balances, transactionFactory);
                verify(csvFailOverOperation, times(1)).doFailover(Mockito.<Collection<ABMFBatchOperation.BatchOperationData>> any());
            }

            @Test
            public void failoverOnReset() throws Exception {
                List<NonMonetoryBalance> balances = createBalances();
                abmfBatchOperation.resetBalance(SUBSCRIBER_ID, balances, transactionFactory);
                verify(csvFailOverOperation, times(1)).doFailover(Mockito.<Collection<ABMFBatchOperation.BatchOperationData>> any());
            }
        }

        private List<NonMonetoryBalance> createBalances() {
            List<NonMonetoryBalance> balances = new ArrayList<NonMonetoryBalance>();
            balances.add(new NonMonetoryBalance.NonMonetaryBalanceBuilder(SUBSCRIBER_ID, RandomUtils.nextInt(0, Integer.MAX_VALUE), "", 1001L, "", "", 1,"", ResetBalanceStatus.NOT_RESET, null, null).build());
            balances.add(new NonMonetoryBalance.NonMonetaryBalanceBuilder(SUBSCRIBER_ID, RandomUtils.nextInt(0, Integer.MAX_VALUE), "", 1001L, "", "", 1,"", ResetBalanceStatus.NOT_RESET, null, null).build());
            balances.add(new NonMonetoryBalance.NonMonetaryBalanceBuilder(SUBSCRIBER_ID, RandomUtils.nextInt(0, Integer.MAX_VALUE), "", 1001L, "", "", 1,"", ResetBalanceStatus.NOT_RESET, null, null).build());
            balances.add(new NonMonetoryBalance.NonMonetaryBalanceBuilder(SUBSCRIBER_ID, RandomUtils.nextInt(0, Integer.MAX_VALUE), "", 1001L, "", "", 1, "", ResetBalanceStatus.NOT_RESET, null, null).build());
            balances.add(new NonMonetoryBalance.NonMonetaryBalanceBuilder(SUBSCRIBER_ID, RandomUtils.nextInt(0, Integer.MAX_VALUE), "", 1001L, "", "", 1, "", ResetBalanceStatus.NOT_RESET, null, null).build());
            return balances;
        }

    }


    public class OnSQLException {

        private static final String DB_DOWN = "DB_DOWN";
        private static final String GENERAL_SQL_EXCEPTION = "GENERAL";

        @Test
        public void failOver() throws InterruptedException, TransactionException, SQLException {

            setUpSQLException(GENERAL_SQL_EXCEPTION);

            runBatch();
            verify(failOverOperation, times(1)).doFailover(Mockito.<Collection>any());
        }

        @Test
        public void csvFailOverWhileDBDownException() throws InterruptedException, TransactionException, SQLException {

            setUpSQLException(DB_DOWN);

            runBatch();
            verify(csvFailOverOperation, times(1)).doFailover(Mockito.<Collection>any());
        }

        private void setUpSQLException(String on) throws TransactionException, SQLException {

            Transaction transaction = spy(new DummyTransaction(mock(Connection.class), null));
            doReturn(transaction).when(transactionFactory).createTransaction();
            doReturn(true).when(transactionFactory).isAlive();
            when(transactionFactory.createTransaction()).thenReturn(transaction);

            PreparedStatement preparedStatement = mock(PreparedStatement.class);
            doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

            if (on.equals(DB_DOWN)) {
                doReturn(true).when(transaction).isDBDownSQLException(Mockito.any());
            }

            doThrow(new SQLException("Test Exception")).when(preparedStatement).setQueryTimeout(Mockito.anyInt());
        }

    }

    public class OnRunTimeException {

        @Test
        public void failOver() throws InterruptedException, TransactionException, SQLException {

            setUpRunTimeException();

            runBatch();
            verify(failOverOperation, times(1)).doFailover(Mockito.<Collection>any());
        }

        private void setUpRunTimeException() throws TransactionException, SQLException {

            Transaction transaction = spy(new DummyTransaction(mock(Connection.class), null));
            doReturn(transaction).when(transactionFactory).createTransaction();
            doReturn(true).when(transactionFactory).isAlive();
            when(transactionFactory.createTransaction()).thenReturn(transaction);

            doThrow(new NumberFormatException("Test Exception")).when(transaction).begin();
        }

    }

    private void runBatch() {

        ArgumentCaptor<Runnable> batchExecutorTask = ArgumentCaptor.forClass(Runnable.class);
        verify(batchExecutor).scheduleWithFixedDelay(batchExecutorTask.capture(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyObject());
        batchExecutorTask.getValue().run();
    }

    @Test
    public void test_reserve_balance_batch_operation() throws OperationFailedException {
        NonMonetoryBalance nonMonetoryBalance = createDummyBalance();
        Random random = new Random();
        TblmDataBalanceEntity dataBalanceEntity = new TblmDataBalanceEntity();
        dataBalanceEntity.setReservationVolume((long)random.nextInt());
        dataBalanceEntity.setReservationTime((long)random.nextInt());
        dataBalanceEntity.setId(nonMonetoryBalance.getId());
        hibernateSessionFactory.save(dataBalanceEntity);

        abmfOperation.reserveBalance(nonMonetoryBalance.getSubscriberIdentity(), Arrays.asList(nonMonetoryBalance), transactionFactory);

        runBatch();

        List<TblmDataBalanceEntity>  actualEntities = hibernateSessionFactory.get(TblmDataBalanceEntity.class);

        TblmDataBalanceEntity expectedDataBalanceEntity = new TblmDataBalanceEntity();
        expectedDataBalanceEntity.setId(nonMonetoryBalance.getId());
        expectedDataBalanceEntity.setReservationVolume(dataBalanceEntity.getReservationVolume() + nonMonetoryBalance.getReservationVolume());
        expectedDataBalanceEntity.setReservationTime(dataBalanceEntity.getReservationTime() + nonMonetoryBalance.getReservationTime());
        ReflectionAssert.assertLenientEquals(Arrays.asList(expectedDataBalanceEntity), actualEntities);
    }

    @Test
    public void test_report_balance_batch_operation() throws OperationFailedException {
        NonMonetoryBalance nonMonetoryBalance = createDummyBalance();
        Random random = new Random();
        TblmDataBalanceEntity dataBalanceEntity = new TblmDataBalanceEntity();
        dataBalanceEntity.setDailyVolume((long)random.nextInt());
        dataBalanceEntity.setDailyTime((long)random.nextInt());
        dataBalanceEntity.setWeeklyVolume((long)random.nextInt());
        dataBalanceEntity.setWeeklyTime((long)random.nextInt());
        dataBalanceEntity.setBillingCycleTotalVolume((long)random.nextInt());
        dataBalanceEntity.setBillingCycleTime((long)random.nextInt());
        dataBalanceEntity.setBillingCycleAvailableVolume((long)random.nextInt());
        dataBalanceEntity.setBillingCycleAvailableTime((long)random.nextInt());
        dataBalanceEntity.setReservationVolume((long)random.nextInt());
        dataBalanceEntity.setReservationTime((long)random.nextInt());
        dataBalanceEntity.setId(nonMonetoryBalance.getId());

        hibernateSessionFactory.save(dataBalanceEntity);
        abmfOperation.reportBalance(nonMonetoryBalance.getSubscriberIdentity(), Arrays.asList(nonMonetoryBalance), transactionFactory);
        runBatch();
        List<TblmDataBalanceEntity>  actualEntities = hibernateSessionFactory.get(TblmDataBalanceEntity.class);
        TblmDataBalanceEntity expectedDataBalanceEntity = new TblmDataBalanceEntity();
        expectedDataBalanceEntity.setId(nonMonetoryBalance.getId());
        expectedDataBalanceEntity.setDailyVolume(dataBalanceEntity.getDailyVolume() + nonMonetoryBalance.getDailyVolume());
        expectedDataBalanceEntity.setDailyTime(dataBalanceEntity.getDailyTime() + nonMonetoryBalance.getDailyTime());
        expectedDataBalanceEntity.setWeeklyVolume(dataBalanceEntity.getWeeklyVolume() + nonMonetoryBalance.getWeeklyVolume());
        expectedDataBalanceEntity.setWeeklyTime(dataBalanceEntity.getWeeklyTime() + nonMonetoryBalance.getWeeklyTime());
        expectedDataBalanceEntity.setBillingCycleTotalVolume(dataBalanceEntity.getBillingCycleTotalVolume());
        expectedDataBalanceEntity.setBillingCycleTime(dataBalanceEntity.getBillingCycleTime());
        expectedDataBalanceEntity.setBillingCycleAvailableVolume(dataBalanceEntity.getBillingCycleAvailableVolume() - nonMonetoryBalance.getBillingCycleAvailableVolume());
        expectedDataBalanceEntity.setBillingCycleAvailableTime(dataBalanceEntity.getBillingCycleAvailableTime() - nonMonetoryBalance.getBillingCycleAvailableTime());
        expectedDataBalanceEntity.setReservationVolume(dataBalanceEntity.getReservationVolume() - nonMonetoryBalance.getReservationVolume());
        expectedDataBalanceEntity.setReservationTime(dataBalanceEntity.getReservationTime() - nonMonetoryBalance.getReservationTime());
        ReflectionAssert.assertLenientEquals(Arrays.asList(expectedDataBalanceEntity), actualEntities);
    }

    @Test
    public void test_report_and_Reserve_balance_batch_operation() throws OperationFailedException {
        NonMonetoryBalance nonMonetoryBalance = createDummyBalance();
        Random random = new Random();
        TblmDataBalanceEntity dataBalanceEntity = new TblmDataBalanceEntity();
        dataBalanceEntity.setId(nonMonetoryBalance.getId());
        dataBalanceEntity.setDailyVolume((long)random.nextInt());
        dataBalanceEntity.setDailyTime((long)random.nextInt());
        dataBalanceEntity.setWeeklyVolume((long)random.nextInt());
        dataBalanceEntity.setWeeklyTime((long)random.nextInt());
        dataBalanceEntity.setBillingCycleTotalVolume((long)random.nextInt());
        dataBalanceEntity.setBillingCycleTime((long)random.nextInt());
        dataBalanceEntity.setBillingCycleAvailableVolume((long)random.nextInt());
        dataBalanceEntity.setBillingCycleAvailableTime((long)random.nextInt());
        dataBalanceEntity.setReservationVolume((long)random.nextInt());
        dataBalanceEntity.setReservationTime((long)random.nextInt());
        hibernateSessionFactory.save(dataBalanceEntity);

        abmfOperation.reportAndReserveBalance(nonMonetoryBalance.getSubscriberIdentity(), Arrays.asList(nonMonetoryBalance), transactionFactory);
        runBatch();
        List<TblmDataBalanceEntity>  actualEntities = hibernateSessionFactory.get(TblmDataBalanceEntity.class);

        TblmDataBalanceEntity expectedDataBalanceEntity = new TblmDataBalanceEntity();
        expectedDataBalanceEntity.setId(nonMonetoryBalance.getId());
        expectedDataBalanceEntity.setDailyVolume(dataBalanceEntity.getDailyVolume() + nonMonetoryBalance.getDailyVolume());
        expectedDataBalanceEntity.setDailyTime(dataBalanceEntity.getDailyTime() + nonMonetoryBalance.getDailyTime());
        expectedDataBalanceEntity.setWeeklyVolume(dataBalanceEntity.getWeeklyVolume() + nonMonetoryBalance.getWeeklyVolume());
        expectedDataBalanceEntity.setWeeklyTime(dataBalanceEntity.getWeeklyTime() + nonMonetoryBalance.getWeeklyTime());
        expectedDataBalanceEntity.setBillingCycleTotalVolume(dataBalanceEntity.getBillingCycleTotalVolume());
        expectedDataBalanceEntity.setBillingCycleTime(dataBalanceEntity.getBillingCycleTime());
        expectedDataBalanceEntity.setBillingCycleAvailableVolume(dataBalanceEntity.getBillingCycleAvailableVolume() - nonMonetoryBalance.getBillingCycleAvailableVolume());
        expectedDataBalanceEntity.setBillingCycleAvailableTime(dataBalanceEntity.getBillingCycleAvailableTime() - nonMonetoryBalance.getBillingCycleAvailableTime());
        expectedDataBalanceEntity.setReservationVolume(dataBalanceEntity.getReservationVolume() + nonMonetoryBalance.getReservationVolume());
        expectedDataBalanceEntity.setReservationTime(dataBalanceEntity.getReservationTime() + nonMonetoryBalance.getReservationTime());
        ReflectionAssert.assertLenientEquals(Arrays.asList(expectedDataBalanceEntity), actualEntities);
    }

    @Test
    public void test_reset_balance_batch_operation() throws OperationFailedException {
        NonMonetoryBalance nonMonetoryBalance = createDummyBalance();
        Random random = new Random();
        TblmDataBalanceEntity dataBalanceEntity = new TblmDataBalanceEntity();
        Timestamp dailyResetTime = Timestamp.valueOf("2017-09-10 10:10:10.0");
        Timestamp weeklyResetTime = Timestamp.valueOf("2017-09-10 10:11:10.0");
        Timestamp billingCycleResetTime = Timestamp.valueOf("2017-09-10 10:12:10.0");

        dataBalanceEntity.setId(nonMonetoryBalance.getId());
        dataBalanceEntity.setDailyVolume((long)random.nextInt());
        dataBalanceEntity.setDailyTime((long)random.nextInt());
        dataBalanceEntity.setWeeklyVolume((long)random.nextInt());
        dataBalanceEntity.setWeeklyTime((long)random.nextInt());
        dataBalanceEntity.setBillingCycleAvailableVolume((long)random.nextInt());
        dataBalanceEntity.setBillingCycleAvailableTime((long)random.nextInt());
        dataBalanceEntity.setDailyResetTime(dailyResetTime);
        dataBalanceEntity.setWeeklyResetTime(weeklyResetTime);
        dataBalanceEntity.setQuotaExpiryTime(billingCycleResetTime);

        hibernateSessionFactory.save(dataBalanceEntity);
        abmfOperation.resetBalance(nonMonetoryBalance.getSubscriberIdentity(), Arrays.asList(nonMonetoryBalance), transactionFactory);
        runBatch();
        List<TblmDataBalanceEntity>  actualEntities = hibernateSessionFactory.get(TblmDataBalanceEntity.class);
        TblmDataBalanceEntity expectedDataBalanceEntity = new TblmDataBalanceEntity();
        expectedDataBalanceEntity.setId(nonMonetoryBalance.getId());
        expectedDataBalanceEntity.setDailyVolume(nonMonetoryBalance.getDailyVolume());
        expectedDataBalanceEntity.setDailyTime(nonMonetoryBalance.getDailyTime());
        expectedDataBalanceEntity.setWeeklyVolume(nonMonetoryBalance.getWeeklyVolume());
        expectedDataBalanceEntity.setWeeklyTime(nonMonetoryBalance.getWeeklyTime());
        expectedDataBalanceEntity.setBillingCycleAvailableVolume(nonMonetoryBalance.getBillingCycleAvailableVolume());
        expectedDataBalanceEntity.setBillingCycleAvailableTime(nonMonetoryBalance.getBillingCycleAvailableTime());
        expectedDataBalanceEntity.setDailyResetTime(new Timestamp(nonMonetoryBalance.getDailyResetTime()));
        expectedDataBalanceEntity.setWeeklyResetTime(new Timestamp(nonMonetoryBalance.getWeeklyResetTime()));
        expectedDataBalanceEntity.setQuotaExpiryTime(new Timestamp(nonMonetoryBalance.getBillingCycleResetTime()));
        ReflectionAssert.assertLenientEquals(Arrays.asList(expectedDataBalanceEntity), actualEntities);
    }

    @Test
    public void test_direct_debit() throws OperationFailedException {
        NonMonetoryBalance nonMonetoryBalance = createDummyBalance();
        Random random = new Random();
        TblmDataBalanceEntity dataBalanceEntity = new TblmDataBalanceEntity();
        dataBalanceEntity.setId(nonMonetoryBalance.getId());
        dataBalanceEntity.setDailyVolume((long) random.nextInt());
        dataBalanceEntity.setDailyTime((long) random.nextInt());
        dataBalanceEntity.setWeeklyVolume((long) random.nextInt());
        dataBalanceEntity.setWeeklyTime((long) random.nextInt());
        dataBalanceEntity.setBillingCycleTotalVolume((long) random.nextInt());
        dataBalanceEntity.setBillingCycleTime((long) random.nextInt());
        dataBalanceEntity.setBillingCycleAvailableVolume((long) random.nextInt());
        dataBalanceEntity.setBillingCycleAvailableTime((long) random.nextInt());
        hibernateSessionFactory.save(dataBalanceEntity);
        abmfOperation.directDebitBalance(nonMonetoryBalance.getSubscriberIdentity(), Arrays.asList(nonMonetoryBalance), transactionFactory);
        runBatch();
        List<TblmDataBalanceEntity>  actualEntities = hibernateSessionFactory.get(TblmDataBalanceEntity.class);

        TblmDataBalanceEntity expectedDataBalanceEntity = new TblmDataBalanceEntity();

        expectedDataBalanceEntity.setId(nonMonetoryBalance.getId());
        expectedDataBalanceEntity.setDailyVolume(dataBalanceEntity.getDailyVolume() + nonMonetoryBalance.getDailyVolume());
        expectedDataBalanceEntity.setDailyTime(dataBalanceEntity.getDailyTime() + nonMonetoryBalance.getDailyTime());
        expectedDataBalanceEntity.setWeeklyVolume(dataBalanceEntity.getWeeklyVolume() + nonMonetoryBalance.getWeeklyVolume());
        expectedDataBalanceEntity.setWeeklyTime(dataBalanceEntity.getWeeklyTime() + nonMonetoryBalance.getWeeklyTime());
        expectedDataBalanceEntity.setBillingCycleTotalVolume(dataBalanceEntity.getBillingCycleTotalVolume());
        expectedDataBalanceEntity.setBillingCycleTime(dataBalanceEntity.getBillingCycleTime());
        expectedDataBalanceEntity.setBillingCycleAvailableVolume(dataBalanceEntity.getBillingCycleAvailableVolume() - nonMonetoryBalance.getBillingCycleAvailableVolume());
        expectedDataBalanceEntity.setBillingCycleAvailableTime(dataBalanceEntity.getBillingCycleAvailableTime() - nonMonetoryBalance.getBillingCycleAvailableTime());
        ReflectionAssert.assertLenientEquals(Arrays.asList(expectedDataBalanceEntity), actualEntities);

    }



    private NonMonetoryBalance createDummyBalance() {
        return createServiceRGNonMonitoryBalances();
    }

    protected NonMonetoryBalance createServiceRGNonMonitoryBalances() {
        return createServiceRGNonMonitoryBalances(UUID.randomUUID().toString());
    }

    protected NonMonetoryBalance createServiceRGNonMonitoryBalances(String id) {
        Random random = new Random();

        NonMonetoryBalance serviceRgNonMonitoryBalance = new NonMonetoryBalance.NonMonetaryBalanceBuilder(id,
                RandomUtils.nextInt(0, Integer.MAX_VALUE),
                UUID.randomUUID().toString(),
                1l,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                random.nextInt(2),
                UUID.randomUUID().toString(), ResetBalanceStatus.NOT_RESET, null, null)
                .withBillingCycleVolumeBalance(random.nextInt(),random.nextInt()).withBillingCycleResetTime(random.nextInt())
                .withBillingCycleTimeBalance(random.nextInt(),random.nextInt())
                .withDailyUsage(random.nextInt(),random.nextInt()).withDailyResetTime(random.nextInt())
                .withWeeklyUsage(random.nextInt(),random.nextInt()).withWeeklyResetTime(random.nextInt())
                .withReservation(random.nextInt(),random.nextInt())
                .build();
        return serviceRgNonMonitoryBalance;
    }

}
