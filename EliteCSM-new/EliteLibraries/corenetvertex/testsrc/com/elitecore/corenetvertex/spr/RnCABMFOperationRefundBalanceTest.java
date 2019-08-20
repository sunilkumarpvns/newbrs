package com.elitecore.corenetvertex.spr;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import com.elitecore.corenetvertex.pkg.ChargingType;
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
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class RnCABMFOperationRefundBalanceTest {

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
    private RnCABMFOperation rncAbmfOperation;
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
        rncAbmfOperation = new RnCABMFOperation(alertListener,  policyRepository,10, 2);
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
    public void test_refund_balance() throws OperationFailedException {
        
    	RnCNonMonetaryBalance rncNonMonetaryBalance = createRnCNonMonetaryBalances();
        
    	TblmRnCBalanceEntity tblmRnCBalanceEntity = new TblmRnCBalanceEntity();
        Random random = new Random();
        tblmRnCBalanceEntity.setDailyLimit((long) random.nextInt());
        tblmRnCBalanceEntity.setWeeklyLimit((long) random.nextInt());
        tblmRnCBalanceEntity.setBillingCycleAvailable((long) random.nextInt());
        tblmRnCBalanceEntity.setBillingCycleTotal((long) random.nextInt());
        tblmRnCBalanceEntity.setId(rncNonMonetaryBalance.getId());

        hibernateSessionFactory.save(tblmRnCBalanceEntity);

        rncAbmfOperation.refundBalance(rncNonMonetaryBalance.getSubscriberIdentity(), Arrays.asList(rncNonMonetaryBalance), transactionFactory);

        List<TblmRnCBalanceEntity>  actualEntities = hibernateSessionFactory.get(TblmRnCBalanceEntity.class);
        TblmRnCBalanceEntity expectedRnCBalanceEntity = new TblmRnCBalanceEntity();
        expectedRnCBalanceEntity.setId(rncNonMonetaryBalance.getId());
        expectedRnCBalanceEntity.setDailyLimit(tblmRnCBalanceEntity.getDailyLimit() - rncNonMonetaryBalance.getDailyLimit());
        expectedRnCBalanceEntity.setWeeklyLimit(tblmRnCBalanceEntity.getWeeklyLimit() - rncNonMonetaryBalance.getWeeklyLimit());
        expectedRnCBalanceEntity.setBillingCycleTotal(tblmRnCBalanceEntity.getBillingCycleTotal());
        expectedRnCBalanceEntity.setBillingCycleAvailable(tblmRnCBalanceEntity.getBillingCycleAvailable() + rncNonMonetaryBalance.getBillingCycleAvailable());
        expectedRnCBalanceEntity.setDailyResetTime(actualEntities.get(0).getDailyResetTime());
        expectedRnCBalanceEntity.setWeeklyResetTime(actualEntities.get(0).getWeeklyResetTime());
        expectedRnCBalanceEntity.setLastUpdateTime(actualEntities.get(0).getLastUpdateTime());

        ReflectionAssert.assertReflectionEquals(Arrays.asList(expectedRnCBalanceEntity), actualEntities);
    }

    @Test
    public void test_clear_parameters_works_for_report_balance() throws OperationFailedException {
    	
        TblmRnCBalanceEntity rncBalanceEntity = new TblmRnCBalanceEntity();
        String id = "1";
        RnCNonMonetaryBalance rncNonMonetaryBalance = createRnCNonMonetaryBalances(id);
        RnCNonMonetaryBalance anotherRnCNonMonetaryBalance = createRnCNonMonetaryBalances(id);
        Random random = new Random();
        rncBalanceEntity.setId(id);
        rncBalanceEntity.setDailyLimit((long) random.nextInt());
        rncBalanceEntity.setWeeklyLimit((long) random.nextInt());
        rncBalanceEntity.setBillingCycleTotal((long) random.nextInt());
        rncBalanceEntity.setBillingCycleAvailable((long) random.nextInt());
        rncBalanceEntity.setId(rncNonMonetaryBalance.getId());
        hibernateSessionFactory.save(rncBalanceEntity);


        rncAbmfOperation.refundBalance(rncNonMonetaryBalance.getSubscriberIdentity(),Arrays.asList(rncNonMonetaryBalance, anotherRnCNonMonetaryBalance), transactionFactory);
        List<TblmRnCBalanceEntity>  actualEntities = hibernateSessionFactory.get(TblmRnCBalanceEntity.class);

        TblmRnCBalanceEntity expectedRnCBalanceEntity = new TblmRnCBalanceEntity();
        expectedRnCBalanceEntity.setId(rncNonMonetaryBalance.getId());
        expectedRnCBalanceEntity.setDailyLimit(rncBalanceEntity.getDailyLimit() - rncNonMonetaryBalance.getDailyLimit() - anotherRnCNonMonetaryBalance.getDailyLimit());
        expectedRnCBalanceEntity.setWeeklyLimit(rncBalanceEntity.getWeeklyLimit() - rncNonMonetaryBalance.getWeeklyLimit() - anotherRnCNonMonetaryBalance.getWeeklyLimit());
        expectedRnCBalanceEntity.setBillingCycleAvailable(rncBalanceEntity.getBillingCycleAvailable() + rncNonMonetaryBalance.getBillingCycleAvailable() + anotherRnCNonMonetaryBalance.getBillingCycleAvailable());
        expectedRnCBalanceEntity.setBillingCycleTotal(rncBalanceEntity.getBillingCycleTotal());
        expectedRnCBalanceEntity.setDailyResetTime(actualEntities.get(0).getDailyResetTime());
        expectedRnCBalanceEntity.setWeeklyResetTime(actualEntities.get(0).getWeeklyResetTime());
        expectedRnCBalanceEntity.setLastUpdateTime(actualEntities.get(0).getLastUpdateTime());

        ReflectionAssert.assertReflectionEquals(Arrays.asList(expectedRnCBalanceEntity), actualEntities);
    }


    @Test
    public void test_report_balance_should_always_end_transaction() throws Exception {
        
    	RnCNonMonetaryBalance rncNonMonetaryBalance = createRnCNonMonetaryBalances();
        TblmRnCBalanceEntity rncBalanceEntity = new TblmRnCBalanceEntity();
        rncBalanceEntity.setId("1");
        
        hibernateSessionFactory.save(rncBalanceEntity);
        rncAbmfOperation.refundBalance(rncNonMonetaryBalance.getSubscriberIdentity(), Arrays.asList(rncNonMonetaryBalance), transactionFactory);
        Connection connection = getConnection();
        assertTrue(connection.isClosed());
    }

    @Test
    public void test_refundBalance_should_generate_DB_HIGH_QUERY_RESPONSE_TIME_alert_when_execution_time_is_high() throws Exception{
        
    	TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlertForrefundBalance();

        RnCNonMonetaryBalance rncNonMonetaryBalance = createRnCNonMonetaryBalances();
        rncAbmfOperation.refundBalance(rncNonMonetaryBalance.getSubscriberIdentity(), Arrays.asList(rncNonMonetaryBalance), transactionFactory);

        verify(alertListener, only())
                .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
    }

    private TransactionFactory setUpMockToGenerateHIGHRESPONSEAlertForrefundBalance() throws Exception{
    	
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
    public void test_refundBalance_should_generate_QUERY_TIMEOUT_alert_when_query_timeout_time_is_high() throws Exception {
    	
        TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTAlertForrefundBalance();
        RnCNonMonetaryBalance rncNonMonetaryBalance = createRnCNonMonetaryBalances();

        try {
            rncAbmfOperation.refundBalance(rncNonMonetaryBalance.getSubscriberIdentity(), Arrays.asList(rncNonMonetaryBalance), transactionFactory);
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito
                            .anyString());
        }
    }

    private TransactionFactory setUpMockToGenerateQUERYTIMEOUTAlertForrefundBalance() throws Exception{
    	
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
    public void test_refundBalance_should_generate_DB_NO_CONNECTION_alert_when_connection_not_found() throws Exception {
    	
        TransactionFactory transactionFactory = setUpMockToGenerateDBNOCONNECTIONAlert();
        RnCNonMonetaryBalance rncNonMonetaryBalance = createRnCNonMonetaryBalances();
        
        try {
            rncAbmfOperation.refundBalance(rncNonMonetaryBalance.getSubscriberIdentity(), Arrays.asList(rncNonMonetaryBalance),transactionFactory);
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
    public void test_report_balance_should_throw_operation_failed_exception_when_any_exception_is_thrown(String whenToThrow,
                                                                                                         Class<? extends Throwable> exceptionToBeThrown) throws Exception {
        setupMockToGenerateExceptionOnUpdateTransaction(whenToThrow, exceptionToBeThrown);
        expectedException.expect(OperationFailedException.class);
        RnCNonMonetaryBalance rncNonMonetaryBalance = createRnCNonMonetaryBalances();
        rncAbmfOperation.refundBalance(rncNonMonetaryBalance.getSubscriberIdentity(), Arrays.asList(rncNonMonetaryBalance),transactionFactory);
    }


    @Test
    public void test_refundBalance_should_throw_OperationFailedException_when_DB_is_down() throws Exception {

        setUpMockTransactionFactoryDead();

        expectedException.expect(OperationFailedException.class);
        RnCNonMonetaryBalance rncNonMonetaryBalance = createRnCNonMonetaryBalances();
        rncAbmfOperation.refundBalance(rncNonMonetaryBalance.getSubscriberIdentity(), Arrays.asList(rncNonMonetaryBalance),transactionFactory);

    }

    private void setUpMockTransactionFactoryDead() {
        transactionFactory = spy(transactionFactory);
        when(transactionFactory.isAlive()).thenReturn(false);
    }

    @Test
    public void test_refundBalance_should_throw_OpeartionFailedException_when_transaction_factory_is_not_alive() throws OperationFailedException {
    	
        RnCNonMonetaryBalance rncNonMonetaryBalance = createRnCNonMonetaryBalances();
        String subscriberId = rncNonMonetaryBalance.getSubscriberIdentity();
        transactionFactory = spy(transactionFactory);
        doReturn(false).when(transactionFactory).isAlive();
        
        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Unable to perform refund rnc balance operation for subscriber ID: " + subscriberId + ". Reason: Datasource not available");
        
        try {
            rncAbmfOperation.refundBalance(subscriberId, Arrays.asList(rncNonMonetaryBalance), transactionFactory);
        } catch (OperationFailedException e) {
            Assert.assertEquals(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
            throw e;
        }
    }

    @Test
    public void test_refundBalance_should_throw_OpeartionFailedException_when_transaction_is_null() throws OperationFailedException {
    	
        RnCNonMonetaryBalance rncNonMonetaryBalance = createRnCNonMonetaryBalances();
        String subscriberId = rncNonMonetaryBalance.getSubscriberIdentity();
        transactionFactory = spy(transactionFactory);
        doReturn(null).when(transactionFactory).createTransaction();
        
        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Unable to perform refund rnc balance operation for subscriber ID: " + subscriberId + ". Reason: Datasource not available");
        
        try {
            rncAbmfOperation.refundBalance(subscriberId, Arrays.asList(rncNonMonetaryBalance), transactionFactory);
        } catch (OperationFailedException e) {
            Assert.assertEquals(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
            throw e;
        }
    }

    @Test
    public void test_report_balance_mark_Dead_when_sql_exception_is_not_thrown_due_to_QUERY_TIME_OUT_ERROR() throws Exception {
    	
        setupMockToGenerateExceptionOnUpdateTransaction(EXECUTE, SQLException.class);
        Transaction transaction = transactionFactory.createTransaction();
        when(transaction.isDBDownSQLException(any(SQLException.class))).thenReturn(true);
        
        RnCNonMonetaryBalance rncNonMonetaryBalance = createRnCNonMonetaryBalances();
        
        try {
            rncAbmfOperation.refundBalance(rncNonMonetaryBalance.getSubscriberIdentity(), Arrays.asList(rncNonMonetaryBalance), transactionFactory);
        } catch (OperationFailedException e) {

        }
        verify(transaction).markDead();
    }


    @Test
    public void test_report_balance_markDead_is_called_when_sql_exception_is_thrown_due_to_QUERY_TIME_OUT_ERROR() throws Exception {
        
    	SQLException sqlException = new SQLException("query timeout", "timeout", 1013);
        setupMockToGenerateExceptionOnUpdateTransaction(EXECUTE, sqlException);

        RnCNonMonetaryBalance rncNonMonetaryBalance = createRnCNonMonetaryBalances();
        Transaction transaction = transactionFactory.createTransaction();

        try {
            rncAbmfOperation.refundBalance(rncNonMonetaryBalance.getSubscriberIdentity(), Arrays.asList(rncNonMonetaryBalance), transactionFactory);
        } catch (OperationFailedException e) {

        }
        try {
            rncAbmfOperation.refundBalance(rncNonMonetaryBalance.getSubscriberIdentity(), Arrays.asList(rncNonMonetaryBalance), transactionFactory);
        } catch (OperationFailedException e) {

        }

        try {
            rncAbmfOperation.refundBalance(rncNonMonetaryBalance.getSubscriberIdentity(), Arrays.asList(rncNonMonetaryBalance), transactionFactory);
        } catch (OperationFailedException e) {

        }

        verify(transaction).markDead();
    }

    @Test
    public void test_report_balance_incrementAndGet_is_set_to_zero() throws Exception {
    	
        SQLException sqlException = new SQLException("query timeout", "timeout", 1013);
        setupMockToGenerateExceptionOnUpdateTransaction(EXECUTE, sqlException);
        Transaction transaction = transactionFactory.createTransaction();
        RnCNonMonetaryBalance rncNonMonetaryBalance = createRnCNonMonetaryBalances();

        try {
            rncAbmfOperation.refundBalance(rncNonMonetaryBalance.getSubscriberIdentity(), Arrays.asList(rncNonMonetaryBalance), transactionFactory);
        } catch (OperationFailedException e) {

        }
        try {
            rncAbmfOperation.refundBalance(rncNonMonetaryBalance.getSubscriberIdentity(), Arrays.asList(rncNonMonetaryBalance), transactionFactory);
        } catch (OperationFailedException e) {

        }

        try {
            rncAbmfOperation.refundBalance(rncNonMonetaryBalance.getSubscriberIdentity(), Arrays.asList(rncNonMonetaryBalance), transactionFactory);
        } catch (OperationFailedException e) {

        }

        verify(transaction).markDead();
        try{
            rncAbmfOperation.refundBalance(rncNonMonetaryBalance.getSubscriberIdentity(), Arrays.asList(rncNonMonetaryBalance), transactionFactory);
        } catch (OperationFailedException e) {

        }
        Mockito.reset(transaction);
        verify(transaction, times(0)).markDead();

    }

    @Test
    public void test_report_balance_alert_is_generated_when_sql_exception_is_thrown_due_to_QUERY_TIME_OUT_ERROR() throws Exception {
    	
        SQLException sqlException = new SQLException("query timeout", "timeout", 1013);
        setupMockToGenerateExceptionOnUpdateTransaction(EXECUTE, sqlException);

        RnCNonMonetaryBalance rncNonMonetaryBalance = createRnCNonMonetaryBalances();

        try {
            rncAbmfOperation.refundBalance(rncNonMonetaryBalance.getSubscriberIdentity(), Arrays.asList(rncNonMonetaryBalance), transactionFactory);
        } catch (OperationFailedException e) {

            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString());
        }
    }

    @Test
    public void test_report_balance_alert_is_generated_when_transaction_exception_is_generated() throws Exception {
    	
        TransactionException transactionException = new TransactionException("transaction", TransactionErrorCode.CONNECTION_NOT_FOUND);
        setupMockToGenerateExceptionOnUpdateTransaction(BEGIN, transactionException);

        RnCNonMonetaryBalance rncNonMonetaryBalance = createRnCNonMonetaryBalances();
        try {
            rncAbmfOperation.refundBalance(rncNonMonetaryBalance.getSubscriberIdentity(), Arrays.asList(rncNonMonetaryBalance), transactionFactory);
        } catch (OperationFailedException e) {

            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), Mockito.anyString(), Mockito.anyString());
        }
    }

    @Test
    public void test_report_balance_OperationFailedException_is_thrown_when_any_exception_is_generated() throws Exception {
    	
        expectedException.expect(OperationFailedException.class);
        setupMockToGenerateExceptionOnUpdateTransaction(BEGIN, RuntimeException.class);
        RnCNonMonetaryBalance rncNonMonetaryBalance = createRnCNonMonetaryBalances();
        rncAbmfOperation.refundBalance(rncNonMonetaryBalance.getSubscriberIdentity(), Arrays.asList(rncNonMonetaryBalance), transactionFactory);
    }

    private void setupMockToGenerateExceptionOnUpdateTransaction(String whenToThrow, Class<? extends Throwable> exceptionToBeThrown) throws Exception{

        Throwable throwable = exceptionToBeThrown.getConstructor(String.class).newInstance("from test");
        setupMockToGenerateExceptionOnUpdateTransaction(whenToThrow, throwable);
    }

    private void setupMockToGenerateExceptionOnUpdateTransaction(String whenToThrow,  Throwable throwable) throws Exception{
    	
        Transaction transaction = mock(Transaction.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

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


    protected RnCNonMonetaryBalance createRnCNonMonetaryBalances() {
        return createRnCNonMonetaryBalances(UUID.randomUUID().toString());
    }

    protected RnCNonMonetaryBalance createRnCNonMonetaryBalances(String id) {
    	
        Random random = new Random();

        RnCNonMonetaryBalance rncNonMonetaryBalance = new RnCNonMonetaryBalance.RnCNonMonetaryBalanceBuilder(id,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), ResetBalanceStatus.NOT_RESET, null, ChargingType.EVENT)
                .withBillingCycleTimeBalance(random.nextInt(),random.nextInt())
                .withDailyUsage(random.nextInt()).withDailyResetTime(random.nextInt())
                .withWeeklyUsage(random.nextInt()).withWeeklyResetTime(random.nextInt())
                .withReservation(random.nextInt())
                .build();
        
        return rncNonMonetaryBalance;
    }


    private Connection getConnection() {
        return ((DummyTransactionFactory) transactionFactory).getConnection();
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

}
