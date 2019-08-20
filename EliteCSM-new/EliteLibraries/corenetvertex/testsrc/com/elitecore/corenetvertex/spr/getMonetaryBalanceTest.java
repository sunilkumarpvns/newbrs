package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.constants.MonetaryBalanceType;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class getMonetaryBalanceTest {
    private static final int HIGH_RESPONSE_TIME_LIMIT_IN_MS = 100;
    private static final String DS_NAME = "test-DB";
    private static final Object TRANSACTION = "transaction";
    private static final String PREPARED_STATEMENT = "preparedStatement";
    private static final String BEGIN = "begin";
    private static final String EXECUTE = "execute";
    private long currentTime = System.currentTimeMillis();
    private long oneHourBefore = currentTime - TimeUnit.HOURS.toMillis(1l);
    private long oneHourLater = currentTime + TimeUnit.HOURS.toMillis(1l);
    private long twoHourLater = currentTime + TimeUnit.HOURS.toMillis(2l);
    private Random random = new Random();
    private static final String SUBSCRIBERID = "test";

    @Mock
    private AlertListener alertListener;
    DummyTransactionFactory transactionFactory;
    List<MonetaryBalance> lstmonetaryBalance;
    private MonetaryABMFOperationImpl monetaryABMFOperation;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private HibernateSessionFactory hibernateSessionFactory;

    private Predicate<MonetaryBalance> predicate;
    @Before
    public void setUp() throws Exception{

        MockitoAnnotations.initMocks(this);

        String ssid = UUID.randomUUID().toString();
        String url = "jdbc:h2:mem:" + ssid;
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, url, "", "", 1, 5000, 3000);
        transactionFactory = new DummyTransactionFactory(dbDataSource);
        transactionFactory.createTransaction();
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.connection.url", url);
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        hibernateSessionFactory = HibernateSessionFactory.create("hibernate/test-hibernate.cfg.xml", hibernateProperties);
        lstmonetaryBalance = new ArrayList<>();
        monetaryABMFOperation = new MonetaryABMFOperationImpl(alertListener, 10, new RecordProcessor.EmptyRecordProcessor<>(), null);
        this.predicate = (MonetaryBalance monetaryBalance) -> {
            if (monetaryBalance.getValidFromDate()>System.currentTimeMillis()) {
                return false;
            } else {
                return true;
            }
        };
    }

    @After
    public void tearDownConnection() throws Exception {
        Connection connection = transactionFactory.getConnection();
        if(Objects.nonNull(connection)) {
            DBUtility.closeQuietly(connection);
        }
        hibernateSessionFactory.shutdown();;

    }

    private TblmMonetaryBalanceEntity setDataInMonetaryBalanceTable() {
        TblmMonetaryBalanceEntity monetaryBalanceEntity = new TblmMonetaryBalanceEntity();
        monetaryBalanceEntity.setId("1");
        monetaryBalanceEntity.setSubscriberId("test");
        monetaryBalanceEntity.setServiceId("test");
        monetaryBalanceEntity.setAvailBalance(random.nextDouble());
        monetaryBalanceEntity.setInitialBalance(random.nextDouble());
        monetaryBalanceEntity.setTotalReservation(random.nextDouble());
        monetaryBalanceEntity.setValidFromDate(new Timestamp(oneHourBefore));
        monetaryBalanceEntity.setValidToDate(new Timestamp(oneHourLater));
        monetaryBalanceEntity.setLastUpdateTime(new Timestamp(currentTime));
        monetaryBalanceEntity.setCurrency("INR");
        return monetaryBalanceEntity;
    }


    private TblmMonetaryBalanceEntity createFutureBalance() {
        TblmMonetaryBalanceEntity monetaryBalanceEntity = new TblmMonetaryBalanceEntity();
        monetaryBalanceEntity.setId("1");
        monetaryBalanceEntity.setSubscriberId("test");
        monetaryBalanceEntity.setServiceId("test");
        monetaryBalanceEntity.setAvailBalance(random.nextDouble());
        monetaryBalanceEntity.setInitialBalance(random.nextDouble());
        monetaryBalanceEntity.setTotalReservation(random.nextDouble());
        monetaryBalanceEntity.setValidFromDate(new Timestamp(oneHourLater));
        monetaryBalanceEntity.setValidToDate(new Timestamp(twoHourLater));
        monetaryBalanceEntity.setLastUpdateTime(new Timestamp(currentTime));
        monetaryBalanceEntity.setCurrency("USD");
        return monetaryBalanceEntity;
    }

    @Test
    public void test_get_monetary_balance_should_always_end_transaction() throws Exception {
        monetaryABMFOperation.getMonetaryBalance(SUBSCRIBERID, predicate, transactionFactory);
        Connection connection = getConnection();
        assertTrue(connection.isClosed());
    }

    @Test
    public void test_get_monetarybalance_should_generate_DB_HIGH_QUERY_RESPONSE_TIME_alert_when_execution_time_is_high() throws Exception{
        TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlert();
        monetaryABMFOperation.getMonetaryBalance(SUBSCRIBERID, predicate, transactionFactory);
        verify(alertListener, only())
                .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
    }

    private TransactionFactory setUpMockToGenerateHIGHRESPONSEAlert() throws Exception{
        TransactionFactory factory = spy(transactionFactory);
        Transaction transaction = mock(Transaction.class);
        ResultSet resultSet = mock(ResultSet.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        doReturn(transaction).when(factory).createTransaction();
        doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

        when(preparedStatement.executeQuery()).thenAnswer(new Answer() {

            public ResultSet answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {
                long startTime = System.currentTimeMillis();
                long elapsedTime = 0;
                while (elapsedTime < HIGH_RESPONSE_TIME_LIMIT_IN_MS + 10) {
                    elapsedTime = System.currentTimeMillis() - startTime;
                }
                return resultSet;
            }
        });
        return factory;
    }

    @Test
    public void test_get_monetary_balance_should_generate_QUERY_TIMEOUT_alert_when_query_timeout_time_is_high() throws Exception {
        TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTAlert();

        try {
            monetaryABMFOperation.getMonetaryBalance(SUBSCRIBERID, predicate, transactionFactory);
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(),Mockito.anyString());
        }
    }

    private TransactionFactory setUpMockToGenerateQUERYTIMEOUTAlert() throws Exception{
        TransactionFactory factory = spy(transactionFactory);
        doReturn(true).when(factory).isAlive();
        Transaction transaction = mock(Transaction.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        doReturn(transaction).when(factory).createTransaction();
        doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

        when(preparedStatement.executeQuery()).thenThrow(new SQLException("query timeout", "timeout", 1013));
        return factory;

    }

    @Test
    public void test_get_monetary_balance_should_generate_DB_NO_CONNECTION_alert_when_connection_not_found() throws Exception {
        TransactionFactory transactionFactory = setUpMockToGenerateDBNOCONNECTIONAlert();
        try {
            monetaryABMFOperation.getMonetaryBalance(SUBSCRIBERID, predicate, transactionFactory);
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), Mockito.anyString(),Mockito.anyString());
        }
    }

    private TransactionFactory setUpMockToGenerateDBNOCONNECTIONAlert() throws Exception{
        TransactionFactory factory = spy(transactionFactory);
        Transaction transaction = mock(Transaction.class);
        doReturn(true).when(factory).isAlive();
        doReturn(transaction).when(factory).createTransaction();
        doThrow(new TransactionException("Connection not found", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).begin();
        return factory;
    }

    @SuppressWarnings("unused")
    private Object[][] dataProviderFor_get_monetarybalance_should_throw_OperationFailedException_when_any_exception_thrown() {
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
    @Parameters(method = "dataProviderFor_get_monetarybalance_should_throw_OperationFailedException_when_any_exception_thrown")
    public void test_get_monetary_balance_should_throw_operation_failed_exception_when_any_exception_is_thrown(String whenToThrow,
                                                                                                         Class<? extends Throwable> exceptionToBeThrown) throws Exception {
        setupMockToGenerateExceptionTest(whenToThrow, exceptionToBeThrown);
        expectedException.expect(OperationFailedException.class);
        monetaryABMFOperation.getMonetaryBalance(SUBSCRIBERID, predicate,transactionFactory);
    }

    private void setupMockToGenerateExceptionTest(String whenToThrow, Class<? extends Throwable> exceptionToBeThrown) throws Exception{
        Transaction transaction = mock(Transaction.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        transactionFactory = spy(transactionFactory);
        doReturn(transaction).when(transactionFactory).createTransaction();
        doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());
        doReturn(resultSet).when(preparedStatement).executeQuery();

        if (TRANSACTION.equals(whenToThrow)) {
            doReturn(null).when(transactionFactory).createTransaction();
        } else if (PREPARED_STATEMENT.equals(whenToThrow)) {
            doThrow(exceptionToBeThrown).when(transaction).prepareStatement(Mockito.anyString());
        } else if (BEGIN.equals(whenToThrow)) {
            doThrow(exceptionToBeThrown).when(transaction).begin();
        } else if (EXECUTE.equals(whenToThrow)) {
            doThrow(exceptionToBeThrown).when(preparedStatement).executeUpdate();
            doThrow(exceptionToBeThrown).when(preparedStatement).executeQuery();
        }
    }

    @Test
    public void test_get_monetary_balance_should_throw_OperationFailedException_when_DB_is_down() throws Exception {
        setUpMockTransactionFactoryDead();
        expectedException.expect(OperationFailedException.class);
        monetaryABMFOperation.getMonetaryBalance(SUBSCRIBERID, predicate, transactionFactory);
    }

    @Test
    public void test_get_monetary_balance() throws OperationFailedException {

        TblmMonetaryBalanceEntity dataBalanceEntity = setDataInMonetaryBalanceTable();
        hibernateSessionFactory.save(dataBalanceEntity);
        MonetaryBalance monetaryBalance = createmonetaryBalance();
        lstmonetaryBalance.add(monetaryBalance);
        SubscriberMonetaryBalance subscriberMonetaryBalance =monetaryABMFOperation.getMonetaryBalance(SUBSCRIBERID, predicate, transactionFactory);

        List<TblmMonetaryBalanceEntity>  actualEntities = hibernateSessionFactory.get(TblmMonetaryBalanceEntity.class);

        MonetaryBalance expectedDataBalanceEntity = new MonetaryBalance(dataBalanceEntity.getId(),
                dataBalanceEntity.getSubscriberId(),
                dataBalanceEntity.getServiceId(),
                //0,
                dataBalanceEntity.getAvailBalance(),
                dataBalanceEntity.getInitialBalance(),
                dataBalanceEntity.getTotalReservation(),
                0,
                0,
                dataBalanceEntity.getValidFromDate().getTime(),
                dataBalanceEntity.getValidToDate().getTime(),
                dataBalanceEntity.getCurrency(),
                null,
                actualEntities.get(0).getLastUpdateTime().getTime(),
                0,
                dataBalanceEntity.getParameter1(),
                dataBalanceEntity.getParameter2()
        );
        expectedDataBalanceEntity.setCurrency(dataBalanceEntity.getCurrency());
        ReflectionAssert.assertLenientEquals(Arrays.asList(expectedDataBalanceEntity), subscriberMonetaryBalance.getAllBalance());
    }

    @Test
    public void test_get_monetary_balance_sets_next_bill_cycle_credit_limit_if_update_time_passed() throws OperationFailedException {

        TblmMonetaryBalanceEntity dataBalanceEntity = setDataInMonetaryBalanceTable();
        dataBalanceEntity.setType(MonetaryBalanceType.DEFAULT.name());
        dataBalanceEntity.setCreditLimit(1000l);
        dataBalanceEntity.setNextBillingCycleCreditLimit(2000l);
        dataBalanceEntity.setCreditLimitUpdateTime(new Timestamp(System.currentTimeMillis()-1000));
        hibernateSessionFactory.save(dataBalanceEntity);
        SubscriberMonetaryBalance subscriberMonetaryBalance =monetaryABMFOperation.getMonetaryBalance(SUBSCRIBERID, predicate, transactionFactory);

        Collection<MonetaryBalance> actual =  subscriberMonetaryBalance.getAllBalance();

        actual.forEach(monetaryBalance -> {
            Assert.assertEquals(0, monetaryBalance.getCreditLimitUpdateTime());
            Assert.assertEquals(0, monetaryBalance.getNextBillingCycleCreditLimit());
            Assert.assertEquals(dataBalanceEntity.getNextBillingCycleCreditLimit().longValue(), monetaryBalance.getCreditLimit());
        });
    }

    @Test
    public void test_get_monetary_balance_and_see_predicate_ignores_future_balance() throws OperationFailedException {

        TblmMonetaryBalanceEntity dataBalanceEntity = createFutureBalance();
        hibernateSessionFactory.save(dataBalanceEntity);
        MonetaryBalance monetaryBalance = createmonetaryBalance();
        lstmonetaryBalance.add(monetaryBalance);
        SubscriberMonetaryBalance subscriberMonetaryBalance =monetaryABMFOperation.getMonetaryBalance(SUBSCRIBERID, predicate, transactionFactory);

        Assert.assertEquals(0, subscriberMonetaryBalance.getAllBalance().size());
    }

    @Test
    public void test_get_monetary_balance_mark_Dead_when_sql_exception_is_not_thrown_due_to_QUERY_TIME_OUT_ERROR() throws Exception {
        setupMockToGenerateExceptionOnUpdateTransaction(EXECUTE, SQLException.class);
        Transaction transaction = transactionFactory.createTransaction();
        when(transaction.isDBDownSQLException(any(SQLException.class))).thenReturn(true);
        try {
            monetaryABMFOperation.getMonetaryBalance(SUBSCRIBERID, predicate,transactionFactory);

        } catch (OperationFailedException e) {

        }
        verify(transaction).markDead();
    }

    @Test
    public void test_reserveBalance_should_generate_DB_NO_CONNECTION_alert_when_connection_not_found() throws Exception {
        setUpMockToGenerateDBNOCONNECTIONAlertForGetMonetaryBalance();
        try {
            monetaryABMFOperation.getMonetaryBalance(SUBSCRIBERID, predicate, transactionFactory);
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), Mockito.anyString(),Mockito.anyString());
        }
    }

    private TransactionFactory setUpMockToGenerateDBNOCONNECTIONAlertForGetMonetaryBalance() throws TransactionException{
        TransactionFactory factory = spy(transactionFactory);
        Transaction transaction = mock(Transaction.class);
        doReturn(true).when(factory).isAlive();
        doReturn(transaction).when(factory).createTransaction();
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
        doReturn(resultSet).when(preparedStatement).executeQuery();

        if (TRANSACTION.equals(whenToThrow)) {
            doReturn(null).when(transactionFactory).createTransaction();
        } else if (PREPARED_STATEMENT.equals(whenToThrow)) {
            doThrow(throwable).when(transaction).prepareStatement(Mockito.anyString());
        } else if (BEGIN.equals(whenToThrow)) {
            doThrow(throwable).when(transaction).begin();
        } else if (EXECUTE.equals(whenToThrow)) {
            doThrow(throwable).when(preparedStatement).executeUpdate();
            doThrow(throwable).when(preparedStatement).executeQuery();
        }
    }

    private MonetaryBalance createmonetaryBalance() {
        MonetaryBalance monetaryBalance = new MonetaryBalance(UUID.randomUUID().toString(),"1",
                "1",1,1,1,0,0, currentTime, currentTime,
                "INR",
                MonetaryBalanceType.DEFAULT.name(), currentTime, 0,"", "");
        return monetaryBalance;
    }

    private void setUpMockTransactionFactoryDead() {
        transactionFactory = spy(transactionFactory);
        when(transactionFactory.isAlive()).thenReturn(false);
    }

    private Connection getConnection() {
        return ((DummyTransactionFactory) transactionFactory).getConnection();
    }
}
