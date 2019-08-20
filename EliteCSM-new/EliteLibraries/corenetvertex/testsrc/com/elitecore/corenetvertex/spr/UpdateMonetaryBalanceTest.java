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
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
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
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@RunWith(JUnitParamsRunner.class)
public class UpdateMonetaryBalanceTest {
    private static final int HIGH_RESPONSE_TIME_LIMIT_IN_MS = 100;
    private static final String DS_NAME = "test-DB";
    private static final Object TRANSACTION = "transaction";
    private static final String PREPARED_STATEMENT = "preparedStatement";
    private static final String BEGIN = "begin";
    private static final String EXECUTE = "execute";
    private long currentTime = System.currentTimeMillis();
    private Random random = new Random();
    private static final String SUBSCRIBERID = "test";

    @Mock
    private AlertListener alertListener;
    @Mock private PolicyRepository policyRepository;
    DummyTransactionFactory transactionFactory;
    List<MonetaryBalance> lstmonetaryBalance;
    private MonetaryABMFOperationImpl monetaryABMFOperation;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private HibernateSessionFactory hibernateSessionFactory;

    @Before
    public void setUp() throws Exception{

        MockitoAnnotations.initMocks(this);
        String sid = UUID.randomUUID().toString();
        String connectionURL = "jdbc:h2:mem:" + sid;
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, connectionURL, "", "", 1, 5000, 3000);
        transactionFactory = new DummyTransactionFactory(dbDataSource);
        transactionFactory.createTransaction();
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.connection.url", connectionURL);
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        hibernateSessionFactory = HibernateSessionFactory.create("hibernate/test-hibernate.cfg.xml", hibernateProperties);
        lstmonetaryBalance = new ArrayList<>();
        monetaryABMFOperation = new MonetaryABMFOperationImpl(alertListener, 10, new RecordProcessor.EmptyRecordProcessor<>(), null);
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
    public void test_update_balance_should_always_end_transaction() throws Exception {
        MonetaryBalance monetaryBalance = createmonetaryBalance();
        TblmMonetaryBalanceEntity monetaryBalanceEntity = new TblmMonetaryBalanceEntity();
        monetaryBalanceEntity.setId("1");
        hibernateSessionFactory.save(monetaryBalanceEntity);
        monetaryABMFOperation.updateBalance(SUBSCRIBERID, monetaryBalance, transactionFactory);
        Connection connection = getConnection();
        assertTrue(connection.isClosed());
    }

    @Test
    public void test_update_Balance_should_generate_DB_HIGH_QUERY_RESPONSE_TIME_alert_when_execution_time_is_high() throws Exception{
        TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlert();
        MonetaryBalance monetaryBalance = createmonetaryBalance();
        monetaryABMFOperation.updateBalance(SUBSCRIBERID, monetaryBalance, transactionFactory);
        verify(alertListener, only())
                .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
    }

    private TransactionFactory setUpMockToGenerateHIGHRESPONSEAlert() throws Exception{
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

    @Test
    public void test_updateBalance_should_generate_QUERY_TIMEOUT_alert_when_query_timeout_time_is_high() throws Exception {
        TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTAlert();
        MonetaryBalance monetaryBalance = createmonetaryBalance();

        try {
            monetaryABMFOperation.updateBalance(SUBSCRIBERID, monetaryBalance, transactionFactory);
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito
                            .anyString());
        }
    }

    private TransactionFactory setUpMockToGenerateQUERYTIMEOUTAlert() throws Exception{
        TransactionFactory factory = spy(transactionFactory);
        doReturn(true).when(factory).isAlive();
        Transaction transaction = mock(Transaction.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        doReturn(transaction).when(factory).createTransaction();
        doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("query timeout", "timeout", 1013));
        return factory;

    }

    @Test
    public void test_updateBalance_should_generate_DB_NO_CONNECTION_alert_when_connection_not_found() throws Exception {
        TransactionFactory transactionFactory = setUpMockToGenerateDBNOCONNECTIONAlert();
        MonetaryBalance monetaryBalance = createmonetaryBalance();

        try {
            monetaryABMFOperation.updateBalance(SUBSCRIBERID, monetaryBalance,transactionFactory);
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
    private Object[][] dataProviderFor_reportBalance_should_throw_OperationFailedException_when_any_exception_thrown() {
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
    @Parameters(method = "dataProviderFor_reportBalance_should_throw_OperationFailedException_when_any_exception_thrown")
    public void test_updatebalance_should_throw_operation_failed_exception_when_any_exception_is_thrown(String whenToThrow,
                                                                                                        Class<? extends Throwable> exceptionToBeThrown) throws Exception {
        setupMockToGenerateExceptionTest(whenToThrow, exceptionToBeThrown);
        expectedException.expect(OperationFailedException.class);
        MonetaryBalance monetaryBalance = createmonetaryBalance();
        monetaryABMFOperation.updateBalance(SUBSCRIBERID, monetaryBalance,transactionFactory);
    }

    private void setupMockToGenerateExceptionTest(String whenToThrow, Class<? extends Throwable> exceptionToBeThrown) throws Exception{
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
            doThrow(exceptionToBeThrown).when(transaction).prepareStatement(Mockito.anyString());
        } else if (BEGIN.equals(whenToThrow)) {
            doThrow(exceptionToBeThrown).when(transaction).begin();
        } else if (EXECUTE.equals(whenToThrow)) {
            doThrow(exceptionToBeThrown).when(preparedStatement).executeUpdate();
        }
    }

    @Test
    public void test_updateBalance_should_throw_OperationFailedException_when_DB_is_down() throws Exception {
        setUpMockTransactionFactoryDead();
        expectedException.expect(OperationFailedException.class);
        MonetaryBalance monetaryBalance = createmonetaryBalance();
        monetaryABMFOperation.updateBalance(SUBSCRIBERID, monetaryBalance,transactionFactory);

    }

    private MonetaryBalance createmonetaryBalance() {
        MonetaryBalance monetaryBalance = new MonetaryBalance(UUID.randomUUID().toString(),"1", "1"
                ,1,1,1,0,0, currentTime, currentTime,"INR", MonetaryBalanceType.DEFAULT.name(), currentTime, 0, "", "");
        return monetaryBalance;
    }

    private void setUpMockTransactionFactoryDead() {
        transactionFactory = spy(transactionFactory);
        when(transactionFactory.isAlive()).thenReturn(false);
    }

    private Connection getConnection() {
        return ((DummyTransactionFactory) transactionFactory).getConnection();
    }

    @Test
    public void test_update_balance() throws OperationFailedException {
        TblmMonetaryBalanceEntity dataBalanceEntity = new TblmMonetaryBalanceEntity();

        dataBalanceEntity.setId("1");
        dataBalanceEntity.setSubscriberId("test");
        dataBalanceEntity.setServiceId("test");
        dataBalanceEntity.setAvailBalance(random.nextDouble());
        dataBalanceEntity.setInitialBalance(random.nextDouble());
        dataBalanceEntity.setTotalReservation(random.nextDouble());
        dataBalanceEntity.setValidFromDate(new Timestamp(currentTime));
        dataBalanceEntity.setValidToDate(new Timestamp(currentTime));
        dataBalanceEntity.setLastUpdateTime(new Timestamp(currentTime));
        dataBalanceEntity.setCurrency("INR");

        hibernateSessionFactory.save(dataBalanceEntity);

        MonetaryBalance monetaryBalance = createmonetaryBalance();
        monetaryBalance.setAvailBalance(random.nextDouble());
        monetaryBalance.setInitialBalance(random.nextDouble());
        monetaryBalance.setValidToDate(currentTime);
        monetaryBalance.setLastUpdateTime(currentTime);
        monetaryBalance.setId(dataBalanceEntity.getId());
        monetaryBalance.setSubscriberId(dataBalanceEntity.getSubscriberId());

        monetaryABMFOperation.updateBalance(SUBSCRIBERID, monetaryBalance, transactionFactory);

        List<TblmMonetaryBalanceEntity>  actualEntities = hibernateSessionFactory.get(TblmMonetaryBalanceEntity.class);

        TblmMonetaryBalanceEntity expectedDataBalanceEntity = new TblmMonetaryBalanceEntity();
        expectedDataBalanceEntity.setId(dataBalanceEntity.getId());
        expectedDataBalanceEntity.setSubscriberId(dataBalanceEntity.getSubscriberId());
        expectedDataBalanceEntity.setServiceId(dataBalanceEntity.getServiceId());
        expectedDataBalanceEntity.setAvailBalance(monetaryBalance.getAvailBalance() + dataBalanceEntity.getAvailBalance());
        expectedDataBalanceEntity.setInitialBalance(monetaryBalance.getInitialBalance() + dataBalanceEntity.getInitialBalance());
        expectedDataBalanceEntity.setTotalReservation(dataBalanceEntity.getTotalReservation());
        expectedDataBalanceEntity.setValidFromDate(dataBalanceEntity.getValidFromDate());
        expectedDataBalanceEntity.setValidToDate(new Timestamp(monetaryBalance.getValidToDate()));
        expectedDataBalanceEntity.setLastUpdateTime(actualEntities.get(0).getLastUpdateTime());
        expectedDataBalanceEntity.setCurrency(dataBalanceEntity.getCurrency());

        ReflectionAssert.assertLenientEquals(Arrays.asList(expectedDataBalanceEntity), actualEntities);
    }

}
