package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.commons.lang3.RandomUtils;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.unitils.reflectionassert.ReflectionAssert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(JUnitParamsRunner.class)
public class ABMFOperationReserveBalanceTest {

    private static final int HIGH_RESPONSE_TIME_LIMIT_IN_MS = 100;
    private static final String DS_NAME = "test-DB";
    private static final String TRANSACTION = "transaction";
    private static final String PREPARED_STATEMENT = "preparedStatement";
    private static final String BEGIN = "begin";
    private static final String EXECUTE = "execute";

    @Mock
    private AlertListener alertListener;
    @Mock private PolicyRepository policyRepository;
    private DummyTransactionFactory transactionFactory;
    private ABMFOperation abmfOperation;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private HibernateSessionFactory hibernateSessionFactory;

    @Before
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        //jdbc:h2:mem:create-drop
        String sid = UUID.randomUUID().toString();
        String connectionURL = "jdbc:h2:mem:" + sid;
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, connectionURL, "", "", 1, 5000, 3000);
        transactionFactory = new DummyTransactionFactory(dbDataSource);
        transactionFactory.createTransaction();
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.connection.url", connectionURL);
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        hibernateSessionFactory = HibernateSessionFactory.create("hibernate/test-hibernate.cfg.xml", hibernateProperties);
        abmfOperation = new ABMFOperation(alertListener, policyRepository,10, 2);
    }

    @After
    public void tearDownConnection() throws Exception {
        Connection connection = transactionFactory.getConnection();
        if(Objects.nonNull(connection)) {
            DBUtility.closeQuietly(connection);
        }
        hibernateSessionFactory.shutdown();
    }

    @Test
    public void test_reserve_balance() throws OperationFailedException {
        NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();
        Random random = new Random();
        TblmDataBalanceEntity dataBalanceEntity = new TblmDataBalanceEntity();
        dataBalanceEntity.setId(serviceRgNonMonitoryBalance.getId());
        dataBalanceEntity.setReservationVolume((long) random.nextInt());
        dataBalanceEntity.setReservationTime((long) random.nextInt());
        hibernateSessionFactory.save(dataBalanceEntity);

        abmfOperation.reserveBalance(serviceRgNonMonitoryBalance.getSubscriberIdentity(), Arrays.asList(serviceRgNonMonitoryBalance), transactionFactory);

        List<TblmDataBalanceEntity>  actualEntities = hibernateSessionFactory.get(TblmDataBalanceEntity.class);

        TblmDataBalanceEntity expectedDataBalanceEntity = new TblmDataBalanceEntity();
        expectedDataBalanceEntity.setId(serviceRgNonMonitoryBalance.getId());
        expectedDataBalanceEntity.setReservationVolume(dataBalanceEntity.getReservationVolume() + serviceRgNonMonitoryBalance.getReservationVolume());
        expectedDataBalanceEntity.setReservationTime(dataBalanceEntity.getReservationTime() + serviceRgNonMonitoryBalance.getReservationTime());
        ReflectionAssert.assertLenientEquals(Arrays.asList(expectedDataBalanceEntity), actualEntities);
    }



    @Test
    public void test_clear_parameters_works_for_reserve_balance() throws OperationFailedException {
        TblmDataBalanceEntity dataBalanceEntity = new TblmDataBalanceEntity();
        String id = "1";
        dataBalanceEntity.setId(id);
        NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances(id);
        NonMonetoryBalance anotherServiceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances(id);
        Random random = new Random();
        dataBalanceEntity.setId(id);
        dataBalanceEntity.setReservationVolume((long) random.nextInt());
        dataBalanceEntity.setReservationTime((long) random.nextInt());
        hibernateSessionFactory.save(dataBalanceEntity);
        abmfOperation.reserveBalance(serviceRgNonMonitoryBalance.getSubscriberIdentity(), Arrays.asList(serviceRgNonMonitoryBalance, anotherServiceRgNonMonitoryBalance), transactionFactory);
        List<TblmDataBalanceEntity>  actualEntities = hibernateSessionFactory.get(TblmDataBalanceEntity.class);

        TblmDataBalanceEntity expectedDataBalanceEntity = new TblmDataBalanceEntity();
        expectedDataBalanceEntity.setId(serviceRgNonMonitoryBalance.getId());
        expectedDataBalanceEntity.setReservationVolume(dataBalanceEntity.getReservationVolume() + serviceRgNonMonitoryBalance.getReservationVolume() + anotherServiceRgNonMonitoryBalance.getReservationVolume());
        expectedDataBalanceEntity.setReservationTime(dataBalanceEntity.getReservationTime() + serviceRgNonMonitoryBalance.getReservationTime() + anotherServiceRgNonMonitoryBalance.getReservationTime());
        ReflectionAssert.assertLenientEquals(Arrays.asList(expectedDataBalanceEntity), actualEntities);
    }

    @Test
    public void test_reserve_balance_throws_exception_with_service_unavailable_result_code_when_transaction_factory_is_not_alive() throws Exception{
        NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();
        String subscriberIdentity = serviceRgNonMonitoryBalance.getSubscriberIdentity();
        TransactionFactory factory = spy(transactionFactory);
        doReturn(false).when(factory).isAlive();
        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Unable to perform reserve balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available");
        try {
            abmfOperation.reserveBalance(subscriberIdentity, Arrays.asList(serviceRgNonMonitoryBalance), factory);
        } catch (OperationFailedException e) {
            Assert.assertEquals(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
            throw e;
        }
    }

    @Test
    public void tets_reserve_balance_throws_exception_when_created_transaction_is_null() throws Exception{
        NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();
        String subscriberIdentity = serviceRgNonMonitoryBalance.getSubscriberIdentity();
        TransactionFactory factory = spy(transactionFactory);
        doReturn(null).when(factory).createTransaction();
        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Unable to perform reserve balance operation for subscriber ID: " + subscriberIdentity + ". Reason: Datasource not available");
        try {
            abmfOperation.reserveBalance(subscriberIdentity, Arrays.asList(serviceRgNonMonitoryBalance), factory);
        } catch (OperationFailedException e) {
            Assert.assertEquals(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
            throw e;
        }
    }

    @Test
    public void test_reserve_balance_should_always_end_transaction() throws Exception {
        NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();
        String subscriberIdentity = serviceRgNonMonitoryBalance.getSubscriberIdentity();
        TblmDataBalanceEntity dataBalanceEntity = new TblmDataBalanceEntity();
        dataBalanceEntity.setId("1");
        hibernateSessionFactory.save(dataBalanceEntity);
        abmfOperation.reserveBalance(subscriberIdentity, Arrays.asList(serviceRgNonMonitoryBalance), transactionFactory);
        Connection connection = getConnection();
        assertTrue(connection.isClosed());
    }

    @Test
    public void test_reserveBalance_should_generate_DB_HIGH_QUERY_RESPONSE_TIME_alert_when_execution_time_is_high() throws Exception{
        TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlertForReserveUsage();
        NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();
        String subscriberIdentity = serviceRgNonMonitoryBalance.getSubscriberIdentity();
        abmfOperation.reserveBalance(subscriberIdentity, Arrays.asList(serviceRgNonMonitoryBalance), transactionFactory);

        verify(alertListener, only())
                .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void test_reserveBalance_should_generate_QUERY_TIMEOUT_alert_when_query_timeout_time_is_high() throws Exception {
        TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTAlertForReserveUsage();
        NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();
        String subscriberIdentity = serviceRgNonMonitoryBalance.getSubscriberIdentity();

        try {
            abmfOperation.reserveBalance(subscriberIdentity, Arrays.asList(serviceRgNonMonitoryBalance), transactionFactory);
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito
                            .anyString());
        }
    }

    @Test
    public void test_reserveBalance_should_generate_DB_NO_CONNECTION_alert_when_connection_not_found() throws Exception {
        TransactionFactory transactionFactory = setUpMockToGenerateDBNOCONNECTIONAlert();
        NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();
        String subscriberIdentity = serviceRgNonMonitoryBalance.getSubscriberIdentity();
        try {
            abmfOperation.reserveBalance(subscriberIdentity, Arrays.asList(serviceRgNonMonitoryBalance),transactionFactory);
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), Mockito.anyString(),Mockito.anyString());
        }
    }

    @SuppressWarnings("unused")
    private Object[][] dataProviderFor_Balance_Operation_should_throw_OperationFailedException_when_any_exception_thrown() {
        return new Object[][] {
                {
                        TRANSACTION, TransactionException.class
                },
                {
                        PREPARED_STATEMENT, TransactionException.class
                },
                {
                        BEGIN, TransactionException.class
                },
                {
                        EXECUTE, SQLException.class
                }
        };
    }

    @Test
    @Parameters(method = "dataProviderFor_Balance_Operation_should_throw_OperationFailedException_when_any_exception_thrown")
    public void test_reserve_balance_should_throw_operation_failed_exception_when_any_exception_is_thrown(String whenToThrow,
                                                                                                          Class<? extends Throwable> exceptionToBeThrown) throws Exception {
        setupMockToGenerateExceptionOnUpdateTransaction(whenToThrow, exceptionToBeThrown);
        expectedException.expect(OperationFailedException.class);
        NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();
        abmfOperation.reserveBalance(serviceRgNonMonitoryBalance.getSubscriberIdentity(), Arrays.asList(serviceRgNonMonitoryBalance),transactionFactory);
    }

    @Test
    public void test_reserveBalance_should_throw_OperationFailedException_when_DB_is_down() throws Exception {
        setUpMockTransactionFactoryDead();
        expectedException.expect(OperationFailedException.class);
        NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();
        abmfOperation.reserveBalance(serviceRgNonMonitoryBalance.getSubscriberIdentity(), Arrays.asList(serviceRgNonMonitoryBalance),transactionFactory);
    }


    @Test
    public void test_reserveBalance_should_throw_OpeartionFailedException_when_transaction_factory_is_not_alive() throws OperationFailedException {
        NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();
        String subscriberId = serviceRgNonMonitoryBalance.getSubscriberIdentity();
        transactionFactory = spy(transactionFactory);
        doReturn(false).when(transactionFactory).isAlive();
        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Unable to perform reserve balance operation for subscriber ID: " + subscriberId + ". Reason: Datasource not available");
        try {
            abmfOperation.reserveBalance(subscriberId, Arrays.asList(serviceRgNonMonitoryBalance), transactionFactory);
        } catch (OperationFailedException e) {
            Assert.assertEquals(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
            throw e;
        }
    }

    @Test
    public void test_reserveBalance_should_throw_OpeartionFailedException_when_transaction_is_null() throws OperationFailedException {
        NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();
        String subscriberId = serviceRgNonMonitoryBalance.getSubscriberIdentity();
        transactionFactory = spy(transactionFactory);
        doReturn(null).when(transactionFactory).createTransaction();
        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Unable to perform reserve balance operation for subscriber ID: " + subscriberId + ". Reason: Datasource not available");
        try {
            abmfOperation.reserveBalance(subscriberId, Arrays.asList(serviceRgNonMonitoryBalance), transactionFactory);
        } catch (OperationFailedException e) {
            Assert.assertEquals(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
            throw e;
        }
    }


    @Test
    public void test_reserve_balance_mark_Dead_when_sql_exception_is_not_thrown_due_to_QUERY_TIME_OUT_ERROR() throws Exception {
        setupMockToGenerateExceptionOnUpdateTransaction(EXECUTE, SQLException.class);
        Transaction transaction = transactionFactory.createTransaction();
        when(transaction.isDBDownSQLException(any(SQLException.class))).thenReturn(true);
        NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();
        try {
            abmfOperation.reserveBalance(serviceRgNonMonitoryBalance.getSubscriberIdentity(), Arrays.asList(serviceRgNonMonitoryBalance), transactionFactory);
        } catch (OperationFailedException e) {

        }
        verify(transaction).markDead();
    }

    @Test
    public void test_reserve_balance_markDead_is_called_when_sql_exception_is_thrown_due_to_QUERY_TIME_OUT_ERROR() throws Exception {
        SQLException sqlException = new SQLException("query timeout", "timeout", 1013);
        setupMockToGenerateExceptionOnUpdateTransaction(EXECUTE, sqlException);

        NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();
        Transaction transaction = transactionFactory.createTransaction();

        try {
            abmfOperation.reserveBalance(serviceRgNonMonitoryBalance.getSubscriberIdentity(), Arrays.asList(serviceRgNonMonitoryBalance), transactionFactory);
        } catch (OperationFailedException e) {

        }
        try {
            abmfOperation.reserveBalance(serviceRgNonMonitoryBalance.getSubscriberIdentity(), Arrays.asList(serviceRgNonMonitoryBalance), transactionFactory);
        } catch (OperationFailedException e) {

        }

        try {
            abmfOperation.reserveBalance(serviceRgNonMonitoryBalance.getSubscriberIdentity(), Arrays.asList(serviceRgNonMonitoryBalance), transactionFactory);
        } catch (OperationFailedException e) {

        }

        verify(transaction).markDead();
    }

    @Test
    public void test_reserve_balance_incrementAndGet_is_set_to_zero() throws Exception {
        SQLException sqlException = new SQLException("query timeout", "timeout", 1013);
        setupMockToGenerateExceptionOnUpdateTransaction(EXECUTE, sqlException);

        NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();
        Transaction transaction = transactionFactory.createTransaction();

        try {
            abmfOperation.reserveBalance(serviceRgNonMonitoryBalance.getSubscriberIdentity(), Arrays.asList(serviceRgNonMonitoryBalance), transactionFactory);
        } catch (OperationFailedException e) {

        }
        try {
            abmfOperation.reserveBalance(serviceRgNonMonitoryBalance.getSubscriberIdentity(), Arrays.asList(serviceRgNonMonitoryBalance), transactionFactory);
        } catch (OperationFailedException e) {

        }

        try {
            abmfOperation.reserveBalance(serviceRgNonMonitoryBalance.getSubscriberIdentity(), Arrays.asList(serviceRgNonMonitoryBalance), transactionFactory);
        } catch (OperationFailedException e) {

        }

        verify(transaction).markDead();

        try {
            abmfOperation.reserveBalance(serviceRgNonMonitoryBalance.getSubscriberIdentity(), Arrays.asList(serviceRgNonMonitoryBalance), transactionFactory);
        } catch (OperationFailedException e) {

        }

        Mockito.reset(transaction);
        verify(transaction,times(0)).markDead();
    }

    @Test
    public void test_reserve_balance_alert_is_generated_when_sql_exception_is_thrown_due_to_QUERY_TIME_OUT_ERROR() throws Exception {
        SQLException sqlException = new SQLException("query timeout", "timeout", 1013);
        setupMockToGenerateExceptionOnUpdateTransaction(EXECUTE, sqlException);

        NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();
        Transaction transaction = transactionFactory.createTransaction();

        try {
            abmfOperation.reserveBalance(serviceRgNonMonitoryBalance.getSubscriberIdentity(), Arrays.asList(serviceRgNonMonitoryBalance), transactionFactory);
        } catch (OperationFailedException e) {

            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString());
        }
    }

    @Test
    public void test_reserve_balance_alert_is_generated_when_transaction_exception_is_generated() throws Exception {
        TransactionException transactionException = new TransactionException("transaction", TransactionErrorCode.CONNECTION_NOT_FOUND);
        setupMockToGenerateExceptionOnUpdateTransaction(BEGIN, transactionException);

        NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();
        Transaction transaction = transactionFactory.createTransaction();

        try {
            abmfOperation.reserveBalance(serviceRgNonMonitoryBalance.getSubscriberIdentity(), Arrays.asList(serviceRgNonMonitoryBalance), transactionFactory);
        } catch (OperationFailedException e) {

            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), Mockito.anyString(), Mockito.anyString());
        }
    }

    @Test
    public void test_reserve_balance_OperationFailedException_is_thrown_when_any_exception_is_generated() throws Exception {
        expectedException.expect(OperationFailedException.class);
        setupMockToGenerateExceptionOnUpdateTransaction(BEGIN, RuntimeException.class);
        NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();
        abmfOperation.reserveBalance(serviceRgNonMonitoryBalance.getSubscriberIdentity(), Arrays.asList(serviceRgNonMonitoryBalance), transactionFactory);
    }

    private TransactionFactory setUpMockToGenerateQUERYTIMEOUTAlertForReserveUsage() throws Exception {
        TransactionFactory factory = spy(transactionFactory);
        doReturn(true).when(factory).isAlive();
        Transaction transaction = mock(Transaction.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(transaction).when(factory).createTransaction();
        doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("query timeout", "timeout", 1013));
        return factory;

    }


    private TransactionFactory setUpMockToGenerateHIGHRESPONSEAlertForReserveUsage() throws Exception {
        TransactionFactory factory = spy(transactionFactory);
        Transaction transaction = mock(Transaction.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        doReturn(transaction).when(factory).createTransaction();
        doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

        when(preparedStatement.executeUpdate()).thenAnswer(new Answer<Integer>() {
            public Integer answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {
                long startTime = System.currentTimeMillis();
                long elapsedTime = 0;
                while (elapsedTime < HIGH_RESPONSE_TIME_LIMIT_IN_MS + 10) {
                    elapsedTime = System.currentTimeMillis() - startTime;
                }
                return 1;
            }
        });
        return factory;
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



    private Connection getConnection() {
        return ((DummyTransactionFactory) transactionFactory).getConnection();
    }

    private SPRInfo createSPRInfo(){
        SPRInfoImpl sprInfoImpl = new SPRInfoImpl();

        sprInfoImpl.setSubscriberIdentity("test");
        return sprInfoImpl;
    }

    private Transaction getTransaction(TransactionFactory transactionFactory) throws  OperationFailedException, TransactionException{
        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to perform add balance operation for subscriber ID. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to perform add balance operation for subscriber ID. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        transaction.begin();

        return transaction;
    }

    private TransactionFactory setUpMockToGenerateDBNOCONNECTIONAlert() throws Exception{
        TransactionFactory factory = spy(transactionFactory);
        Transaction transaction = mock(Transaction.class);
        doReturn(true).when(factory).isAlive();
        doReturn(transaction).when(factory).createTransaction();
        doReturn(transaction).when(factory).createReadOnlyTransaction();
        doThrow(new TransactionException("Connection not found", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).begin();
        return factory;
    }

    private void setupMockToGenerateExceptionOnUpdateTransaction(String whenToThrow, Class<? extends Throwable> exceptionToBeThrown) throws Exception{

        Throwable throwable = exceptionToBeThrown.getConstructor(String.class).newInstance("from test");
        setupMockToGenerateExceptionOnUpdateTransaction(whenToThrow, throwable);
    }

    private void setupMockToGenerateExceptionOnUpdateTransaction(String whenToThrow,  Throwable throwable) throws Exception{
        Transaction transaction = mock(Transaction.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        transactionFactory = spy(transactionFactory);
        doReturn(transaction).when(transactionFactory).createTransaction();
        doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());
        doReturn(1).when(preparedStatement).executeUpdate();

        if (TRANSACTION.equals(whenToThrow)) {
            doReturn(null).when(transactionFactory).createTransaction();
        } else if (PREPARED_STATEMENT.equals(whenToThrow)) {
            doThrow(throwable).when(transaction).prepareStatement(Mockito.anyString());
        } else if (BEGIN.equals(whenToThrow)) {
            doThrow(throwable).when(transaction).begin();
        } else if (EXECUTE.equals(whenToThrow)) {
            doThrow(throwable).when(preparedStatement).executeUpdate();
        }
    }

    private void setUpMockTransactionFactoryDead() {
        transactionFactory = spy(transactionFactory);
        when(transactionFactory.isAlive()).thenReturn(false);
    }

}
