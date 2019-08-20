package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.DummyTransaction;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.ConcurrentLinkedQueue;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.unitils.reflectionassert.ReflectionAssert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(HierarchicalContextRunner.class)
public class RnCABMFBatchOperationTest {
    private static final String DS_NAME = "test-DB";
    private RnCABMFBatchOperation abmfBatchOperation;
    private PolicyRepository policyRepository;
    private DummyTransactionFactory transactionFactory;
    private DBSingleRecordFailoverOperation<RnCABMFBatchOperation.BatchOperationData> failOverOperation;
    private ScheduledExecutorService batchExecutor;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private CSVFailOverOperation<RnCABMFBatchOperation.BatchOperationData> csvFailOverOperation;
    private HibernateSessionFactory hibernateSessionFactory;
    ConcurrentLinkedQueue<RnCABMFBatchOperation.BatchOperationData> batchOperationQueue;


    @Before
    public void init() throws Exception {
        policyRepository = mock(PolicyRepository.class);
        batchExecutor = mock(ScheduledExecutorService.class);
        scheduledThreadPoolExecutor = mock(ScheduledThreadPoolExecutor.class);
        Executor executor = mock(Executor.class);
        csvFailOverOperation = spy(new CSVFailOverOperation<RnCABMFBatchOperation.BatchOperationData>(null, executor));
        batchOperationQueue = spy(new ConcurrentLinkedQueue<RnCABMFBatchOperation.BatchOperationData>(1004));
        failOverOperation = spy(new DBSingleRecordFailoverOperation<RnCABMFBatchOperation.BatchOperationData>(new SingleRecordOperation<RnCABMFBatchOperation.BatchOperationData>() {

            @Override
            public void process(RnCABMFBatchOperation.BatchOperationData dataToProcess, TransactionFactory transactionFactory) throws OperationFailedException {

            }
        }, null, transactionFactory, executor));

        doReturn(true).when(batchExecutor).awaitTermination(5, TimeUnit.SECONDS);
        doReturn(true).when(scheduledThreadPoolExecutor).awaitTermination(5, TimeUnit.SECONDS);
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
        failOverOperation = spy(new DBSingleRecordFailoverOperation<>((SingleRecordOperation<RnCABMFBatchOperation.BatchOperationData>) (dataToProcess, transactionFactory) -> {

        }, null,transactionFactory, executor));

        abmfBatchOperation = new RnCABMFBatchOperation(transactionFactory, mock(AlertListener.class),policyRepository,  failOverOperation, csvFailOverOperation,
                scheduledThreadPoolExecutor,
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
            verify(csvFailOverOperation, times(1)).doFailover(Mockito.<Collection<RnCABMFBatchOperation.BatchOperationData>> any());
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
                List<RnCNonMonetaryBalance> balances = createBalances();

                abmfBatchOperation.reserveBalance(SUBSCRIBER_ID, balances, transactionFactory);
                verify(failOverOperation, times(1)).doFailover(Mockito.<Collection<RnCABMFBatchOperation.BatchOperationData>> any());
            }

            @Test
            public void failoverOnReport() throws Exception {
                List<RnCNonMonetaryBalance> balances = createBalances();
                abmfBatchOperation.reportBalance(SUBSCRIBER_ID, balances, transactionFactory);
                verify(failOverOperation, times(1)).doFailover(Mockito.<Collection<RnCABMFBatchOperation.BatchOperationData>> any());
            }

            @Test
            public void failoverOnRefund() throws Exception {
                List<RnCNonMonetaryBalance> balances = createBalances();
                abmfBatchOperation.refundBalance(SUBSCRIBER_ID, balances, transactionFactory);
                verify(failOverOperation, times(1)).doFailover(Mockito.<Collection<RnCABMFBatchOperation.BatchOperationData>> any());
            }

            @Test
            public void failoverOnReserveAndReport() throws Exception {
                List<RnCNonMonetaryBalance> balances = createBalances();
                abmfBatchOperation.reportAndReserveBalance(SUBSCRIBER_ID, balances, transactionFactory);
                verify(failOverOperation, times(1)).doFailover(Mockito.<Collection<RnCABMFBatchOperation.BatchOperationData>> any());
            }

        }

        public class WhenTransactionFactoryNotAlive {

            @Before
            public void setUp() {
                when(transactionFactory.isAlive()).thenReturn(false);
            }

            @Test
            public void failoverReserve() throws Exception {

                List<RnCNonMonetaryBalance> balances = createBalances();
                abmfBatchOperation.reserveBalance(SUBSCRIBER_ID, balances, transactionFactory);
                verify(csvFailOverOperation, times(1)).doFailover(Mockito.<Collection<RnCABMFBatchOperation.BatchOperationData>> any());
            }

            @Test
            public void failoverReport() throws Exception {
                List<RnCNonMonetaryBalance> balances = createBalances();
                abmfBatchOperation.reportBalance(SUBSCRIBER_ID, balances, transactionFactory);
                verify(csvFailOverOperation, times(1)).doFailover(Mockito.<Collection<RnCABMFBatchOperation.BatchOperationData>> any());
            }

            @Test
            public void failoverRefund() throws Exception {
                List<RnCNonMonetaryBalance> balances = createBalances();
                abmfBatchOperation.refundBalance(SUBSCRIBER_ID, balances, transactionFactory);
                verify(csvFailOverOperation, times(1)).doFailover(Mockito.<Collection<RnCABMFBatchOperation.BatchOperationData>> any());
            }

            @Test
            public void failoverOnReserveAndReport() throws Exception {
                List<RnCNonMonetaryBalance> balances = createBalances();
                abmfBatchOperation.reportAndReserveBalance(SUBSCRIBER_ID, balances, transactionFactory);
                verify(csvFailOverOperation, times(1)).doFailover(Mockito.<Collection<RnCABMFBatchOperation.BatchOperationData>> any());
            }
        }

        private List<RnCNonMonetaryBalance> createBalances() {
            List<RnCNonMonetaryBalance> balances = new ArrayList<RnCNonMonetaryBalance>();
            balances.add(new RnCNonMonetaryBalance.RnCNonMonetaryBalanceBuilder(SUBSCRIBER_ID, "", "", "", "", "", ResetBalanceStatus.NOT_RESET, null, ChargingType.SESSION).build());
            balances.add(new RnCNonMonetaryBalance.RnCNonMonetaryBalanceBuilder(SUBSCRIBER_ID, "", "", "", "", "", ResetBalanceStatus.NOT_RESET, null, ChargingType.SESSION).build());
            balances.add(new RnCNonMonetaryBalance.RnCNonMonetaryBalanceBuilder(SUBSCRIBER_ID, "", "", "", "", "", ResetBalanceStatus.NOT_RESET, null, ChargingType.SESSION).build());
            balances.add(new RnCNonMonetaryBalance.RnCNonMonetaryBalanceBuilder(SUBSCRIBER_ID, "", "", "", "", "", ResetBalanceStatus.NOT_RESET, null, ChargingType.SESSION).build());
            balances.add(new RnCNonMonetaryBalance.RnCNonMonetaryBalanceBuilder(SUBSCRIBER_ID, "", "", "", "", "", ResetBalanceStatus.NOT_RESET, null, ChargingType.SESSION).build());
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
        RnCNonMonetaryBalance nonMonetoryBalance = createDummyBalance();
        Random random = new Random();
        TblmRnCBalanceEntity dataBalanceEntity = new TblmRnCBalanceEntity();
        dataBalanceEntity.setReservationTime((long)random.nextInt());
        dataBalanceEntity.setId(nonMonetoryBalance.getId());
        hibernateSessionFactory.save(dataBalanceEntity);

        abmfBatchOperation.reserveBalance(nonMonetoryBalance.getSubscriberIdentity(), Arrays.asList(nonMonetoryBalance), transactionFactory);

        runBatch();

        List<TblmRnCBalanceEntity>  actualEntities = hibernateSessionFactory.get(TblmRnCBalanceEntity.class);

        TblmRnCBalanceEntity expectedDataBalanceEntity = new TblmRnCBalanceEntity();
        expectedDataBalanceEntity.setId(nonMonetoryBalance.getId());
        expectedDataBalanceEntity.setReservationTime(dataBalanceEntity.getReservationTime() + nonMonetoryBalance.getReservationTime());
        ReflectionAssert.assertLenientEquals(Arrays.asList(expectedDataBalanceEntity), actualEntities);
    }

    @Test
    public void test_report_balance_batch_operation() throws OperationFailedException {
        RnCNonMonetaryBalance nonMonetoryBalance = createDummyBalance();
        Random random = new Random();
        TblmRnCBalanceEntity dataBalanceEntity = new TblmRnCBalanceEntity();
        dataBalanceEntity.setDailyLimit((long)random.nextInt());
        dataBalanceEntity.setWeeklyLimit((long)random.nextInt());
        dataBalanceEntity.setBillingCycleTotal((long)random.nextInt());
        dataBalanceEntity.setBillingCycleAvailable((long)random.nextInt());
        dataBalanceEntity.setReservationTime((long)random.nextInt());
        dataBalanceEntity.setId(nonMonetoryBalance.getId());

        hibernateSessionFactory.save(dataBalanceEntity);
        abmfBatchOperation.reportBalance(nonMonetoryBalance.getSubscriberIdentity(), Arrays.asList(nonMonetoryBalance), transactionFactory);
        runBatch();
        List<TblmRnCBalanceEntity>  actualEntities = hibernateSessionFactory.get(TblmRnCBalanceEntity.class);
        TblmRnCBalanceEntity expectedDataBalanceEntity = new TblmRnCBalanceEntity();
        expectedDataBalanceEntity.setId(nonMonetoryBalance.getId());
        expectedDataBalanceEntity.setDailyLimit(dataBalanceEntity.getDailyLimit() + nonMonetoryBalance.getDailyLimit());
        expectedDataBalanceEntity.setWeeklyLimit(dataBalanceEntity.getWeeklyLimit() + nonMonetoryBalance.getWeeklyLimit());
        expectedDataBalanceEntity.setBillingCycleTotal(dataBalanceEntity.getBillingCycleTotal());
        expectedDataBalanceEntity.setBillingCycleAvailable(dataBalanceEntity.getBillingCycleAvailable() - nonMonetoryBalance.getBillingCycleAvailable());
        expectedDataBalanceEntity.setReservationTime(dataBalanceEntity.getReservationTime());
        ReflectionAssert.assertLenientEquals(Arrays.asList(expectedDataBalanceEntity), actualEntities);
    }

    @Test
    public void test_refund_balance_batch_operation() throws OperationFailedException {
        RnCNonMonetaryBalance nonMonetoryBalance = createDummyBalance();
        Random random = new Random();
        TblmRnCBalanceEntity dataBalanceEntity = new TblmRnCBalanceEntity();
        dataBalanceEntity.setDailyLimit((long)random.nextInt());
        dataBalanceEntity.setWeeklyLimit((long)random.nextInt());
        dataBalanceEntity.setBillingCycleTotal((long)random.nextInt());
        dataBalanceEntity.setBillingCycleAvailable((long)random.nextInt());
        dataBalanceEntity.setReservationTime((long)random.nextInt());
        dataBalanceEntity.setId(nonMonetoryBalance.getId());

        hibernateSessionFactory.save(dataBalanceEntity);
        abmfBatchOperation.refundBalance(nonMonetoryBalance.getSubscriberIdentity(), Arrays.asList(nonMonetoryBalance), transactionFactory);
        runBatch();
        List<TblmRnCBalanceEntity>  actualEntities = hibernateSessionFactory.get(TblmRnCBalanceEntity.class);
        TblmRnCBalanceEntity expectedDataBalanceEntity = new TblmRnCBalanceEntity();
        expectedDataBalanceEntity.setId(nonMonetoryBalance.getId());
        expectedDataBalanceEntity.setDailyLimit(dataBalanceEntity.getDailyLimit() - nonMonetoryBalance.getDailyLimit());
        expectedDataBalanceEntity.setWeeklyLimit(dataBalanceEntity.getWeeklyLimit() - nonMonetoryBalance.getWeeklyLimit());
        expectedDataBalanceEntity.setBillingCycleTotal(dataBalanceEntity.getBillingCycleTotal());
        expectedDataBalanceEntity.setBillingCycleAvailable(dataBalanceEntity.getBillingCycleAvailable() + nonMonetoryBalance.getBillingCycleAvailable());
        expectedDataBalanceEntity.setReservationTime(dataBalanceEntity.getReservationTime());
        ReflectionAssert.assertLenientEquals(Arrays.asList(expectedDataBalanceEntity), actualEntities);
    }

    @Test
    public void test_report_and_Reserve_balance_batch_operation() throws OperationFailedException {
        RnCNonMonetaryBalance nonMonetoryBalance = createDummyBalance();
        Random random = new Random();
        TblmRnCBalanceEntity dataBalanceEntity = new TblmRnCBalanceEntity();
        dataBalanceEntity.setId(nonMonetoryBalance.getId());
        dataBalanceEntity.setDailyLimit((long)random.nextInt());
        dataBalanceEntity.setWeeklyLimit((long)random.nextInt());
        dataBalanceEntity.setBillingCycleTotal((long)random.nextInt());
        dataBalanceEntity.setBillingCycleAvailable((long)random.nextInt());
        dataBalanceEntity.setReservationTime((long)random.nextInt());
        hibernateSessionFactory.save(dataBalanceEntity);

        abmfBatchOperation.reportAndReserveBalance(nonMonetoryBalance.getSubscriberIdentity(), Arrays.asList(nonMonetoryBalance), transactionFactory);
        runBatch();
        List<TblmRnCBalanceEntity>  actualEntities = hibernateSessionFactory.get(TblmRnCBalanceEntity.class);

        TblmRnCBalanceEntity expectedDataBalanceEntity = new TblmRnCBalanceEntity();
        expectedDataBalanceEntity.setId(nonMonetoryBalance.getId());
        expectedDataBalanceEntity.setDailyLimit(dataBalanceEntity.getDailyLimit() + nonMonetoryBalance.getDailyLimit());
        expectedDataBalanceEntity.setWeeklyLimit(dataBalanceEntity.getWeeklyLimit() + nonMonetoryBalance.getWeeklyLimit());
        expectedDataBalanceEntity.setBillingCycleTotal(dataBalanceEntity.getBillingCycleTotal());
        expectedDataBalanceEntity.setBillingCycleAvailable(dataBalanceEntity.getBillingCycleAvailable() - nonMonetoryBalance.getBillingCycleAvailable());
        expectedDataBalanceEntity.setReservationTime(dataBalanceEntity.getReservationTime() + nonMonetoryBalance.getReservationTime());
        ReflectionAssert.assertLenientEquals(Arrays.asList(expectedDataBalanceEntity), actualEntities);
    }

    private RnCNonMonetaryBalance createDummyBalance() {
        return createServiceRGNonMonitoryBalances();
    }

    protected RnCNonMonetaryBalance createServiceRGNonMonitoryBalances() {
        return createServiceRGNonMonitoryBalances(UUID.randomUUID().toString());
    }

    protected RnCNonMonetaryBalance createServiceRGNonMonitoryBalances(String id) {
        Random random = new Random();

        RnCNonMonetaryBalance serviceRgNonMonitoryBalance = new RnCNonMonetaryBalance.RnCNonMonetaryBalanceBuilder(id,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), ResetBalanceStatus.NOT_RESET, null, ChargingType.SESSION)
                .withBillingCycleTimeBalance(random.nextInt(),random.nextInt())
                .withDailyUsage(random.nextInt()).withDailyResetTime(random.nextInt())
                .withWeeklyUsage(random.nextInt()).withWeeklyResetTime(random.nextInt())
                .withReservation(random.nextInt())
                .build();
        return serviceRgNonMonitoryBalance;
    }

}
