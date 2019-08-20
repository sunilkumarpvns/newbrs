package com.elitecore.corenetvertex.spr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nonnull;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.commons.lang3.RandomUtils;
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


import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class ABMFOperationExpireBalanceTest {

	private static final int HIGH_RESPONSE_TIME_LIMIT_IN_MS = 100;
    private static final String DS_NAME = "test-DB";
    private static final String TRANSACTION = "transaction";
    private static final String PREPARED_STATEMENT = "preparedStatement";
    private static final String BEGIN = "begin";
    private static final String EXECUTE = "execute";
	private static final String SUBSCRIBER_ID = "97979797";
	public static final String SUBSCRIBER_1 = "Subscriber1";
	public static final String SUBSCRIBER_2 = "Subscriber2";
	public static final String PO_1 = "po1";
	public static final String PO_2 = "po2";

	@Mock
    private AlertListener alertListener;
    @Mock private PolicyRepository policyRepository;
    private DummyTransactionFactory transactionFactory;
    private ABMFOperation abmfOperation;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private HibernateSessionFactory hibernateSessionFactory;
    private final Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

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
        abmfOperation = new ABMFOperation(alertListener, policyRepository,10,2);
    }

    @After
    public void tearDownConnection() throws Exception {
        Connection connection = transactionFactory.getConnection();
        if(Objects.nonNull(connection)) {
            DBUtility.closeQuietly(connection);
        }
        hibernateSessionFactory.shutdown();
    }

	/**
	 * DB have 3 rows
	 * 1) SUBSCRIBER_1, PO_1
	 * 2) SUBSCRIBER_1, PO_2
	 * 3) SUBSCRIBER_2, PO_1
	 *
	 * when expireQuota called with SUBSCRIBER_1 and PO_1, it should only expire record (1)
	 *
	 * @throws OperationFailedException
	 * @throws TransactionException
	 */
	@Test
    public void expireQuota_should_expireBalance_of_provided_productOffer_and_subscriberOnly() throws OperationFailedException, TransactionException {
		abmfOperation = new ABMFOperationExt(alertListener, policyRepository, 10);
		NonMonetoryBalance balance1 = createServiceRGNonMonitoryBalances(SUBSCRIBER_1, PO_1);
		TblmDataBalanceEntity tblmDataBalanceEntity1 = createTableEntryForBalance(balance1.getSubscriberIdentity(), balance1.getProductOfferId());
		tblmDataBalanceEntity1.setId(balance1.getId());

		NonMonetoryBalance balance2 = createServiceRGNonMonitoryBalances(SUBSCRIBER_1, PO_2);
		TblmDataBalanceEntity tblmDataBalanceEntity2 = createTableEntryForBalance(balance2.getSubscriberIdentity(), balance2.getProductOfferId());
		tblmDataBalanceEntity2.setId(balance2.getId());

		NonMonetoryBalance balance3 = createServiceRGNonMonitoryBalances(SUBSCRIBER_2, PO_1);
		TblmDataBalanceEntity tblmDataBalanceEntity3 = createTableEntryForBalance(balance3.getSubscriberIdentity(), balance3.getProductOfferId());
		tblmDataBalanceEntity3.setId(balance3.getId());

		Map<String, NonMonetoryBalance> balances = new HashMap<>();
		balances.put(balance1.getId(), balance1);
		balances.put(balance2.getId(), balance2);
		balances.put(balance3.getId(), balance3);

		hibernateSessionFactory.save(tblmDataBalanceEntity1);
		hibernateSessionFactory.save(tblmDataBalanceEntity2);
		hibernateSessionFactory.save(tblmDataBalanceEntity3);

		expireQuota(balance1);

		List<TblmDataBalanceEntity> actualEntities = hibernateSessionFactory.get(TblmDataBalanceEntity.class);

		TblmDataBalanceEntity expectedBalance1 = tblmDataBalanceEntity1.copy();
		expectedBalance1.setQuotaExpiryTime(currentTimestamp);
		TblmDataBalanceEntity expectedBalance2 = tblmDataBalanceEntity2.copy();
		TblmDataBalanceEntity expectedBalance3 = tblmDataBalanceEntity3.copy();

		ReflectionAssert.assertLenientEquals(Arrays.asList(expectedBalance1, expectedBalance2, expectedBalance3), actualEntities);
    }

	private void expireQuota(NonMonetoryBalance serviceRgNonMonitoryBalance1) throws OperationFailedException, TransactionException {
		Transaction transaction = null;
		try {
			transaction = transactionFactory.createTransaction();
			abmfOperation.expireQuota(serviceRgNonMonitoryBalance1.getSubscriberIdentity(), serviceRgNonMonitoryBalance1.getProductOfferId(), transaction);
		} finally {
			if (transaction != null) transaction.end();
		}
	}

	@Nonnull
	private TblmDataBalanceEntity createBalanceWithRandomValues() {
		return createTableEntryForBalance(UUID.randomUUID().toString(), UUID.randomUUID().toString());
	}

	@Nonnull
	private TblmDataBalanceEntity createTableEntryForBalance(String subscriberId, String productOfferId) {
		TblmDataBalanceEntity tblmDataBalanceEntity = new TblmDataBalanceEntity();
		Random random = new Random();
		tblmDataBalanceEntity.setDailyVolume((long) random.nextInt());
		tblmDataBalanceEntity.setDailyTime((long) random.nextInt());
		tblmDataBalanceEntity.setWeeklyVolume((long) random.nextInt());
		tblmDataBalanceEntity.setWeeklyTime((long) random.nextInt());
		tblmDataBalanceEntity.setBillingCycleTotalVolume((long) random.nextInt());
		tblmDataBalanceEntity.setBillingCycleAvailableVolume((long) random.nextInt());
		tblmDataBalanceEntity.setBillingCycleAvailableTime((long) random.nextInt());
		tblmDataBalanceEntity.setBillingCycleTime((long) random.nextInt());
		tblmDataBalanceEntity.setReservationVolume((long) random.nextInt());
		tblmDataBalanceEntity.setReservationTime((long) random.nextInt());
		tblmDataBalanceEntity.setSubscriberId(subscriberId);
		tblmDataBalanceEntity.setProductOfferId(productOfferId);
		return tblmDataBalanceEntity;
	}

    @Test
    public void expireQuota_should_not_end_transaction() throws Exception {
		NonMonetoryBalance balance1 = createServiceRGNonMonitoryBalances(SUBSCRIBER_1, PO_1);
		TblmDataBalanceEntity tblmDataBalanceEntity1 = createTableEntryForBalance(balance1.getSubscriberIdentity(), balance1.getProductOfferId());
		tblmDataBalanceEntity1.setId(balance1.getId());

		hibernateSessionFactory.save(tblmDataBalanceEntity1);

		Transaction transaction = null;
		try {
			transaction = transactionFactory.createTransaction();
			abmfOperation.expireQuota(balance1.getSubscriberIdentity(), balance1.getProductOfferId(), transaction);
			Connection connection = getConnection();
			assertFalse(connection.isClosed());

		} finally {
			if (transaction != null) transaction.end();
		}
    }

    @Test
    public void expireQuota_should_generate_DB_HIGH_QUERY_RESPONSE_TIME_alert_when_execution_time_is_high() throws Exception{
		TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlertForReportBalance();
		NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();

		Transaction transaction = null;
		try {
			transaction = transactionFactory.createTransaction();
			abmfOperation.expireQuota(serviceRgNonMonitoryBalance.getSubscriberIdentity(), serviceRgNonMonitoryBalance.getProductOfferId(), transaction);
		} finally {
			if (transaction != null) transaction.end();
		}

		verify(alertListener, only())
				.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
    }

    private TransactionFactory setUpMockToGenerateHIGHRESPONSEAlertForReportBalance() throws Exception{
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
    public void expireQuota_should_generate_QUERY_TIMEOUT_alert_when_query_timeout_time_is_high() throws Exception {
		TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTAlertForReportBalance();
		NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();

		Transaction transaction = null;
		try {
			transaction = transactionFactory.createTransaction();
			abmfOperation.expireQuota(serviceRgNonMonitoryBalance.getSubscriberIdentity(), serviceRgNonMonitoryBalance.getProductOfferId(), transaction);
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString());
		} finally {
			if (transaction != null) transaction.end();
		}
    }

	private TransactionFactory setUpMockToGenerateQUERYTIMEOUTAlertForReportBalance() throws Exception{
		TransactionFactory factory = spy(transactionFactory);
		doReturn(true).when(factory).isAlive();
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		doReturn(transaction).when(factory).createTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

		when(preparedStatement.executeUpdate()).thenThrow(new SQLException("query timeout", "timeout", 1013));
		return factory;

	}

    @SuppressWarnings("unused")
    private Object[][] dataProviderFor_expireQuota_balance_should_throw_respective_exception_when_any_exception_is_thrown() {
        return new Object[][] {
                {
                        PREPARED_STATEMENT, TransactionException.class, TransactionException.class
                },
                {
                        EXECUTE, SQLException.class, OperationFailedException.class
                }
        };
    }

    @Test
    @Parameters(method = "dataProviderFor_expireQuota_balance_should_throw_respective_exception_when_any_exception_is_thrown")
    public void expireQuota_balance_should_throw_respective_exception_when_any_exception_is_thrown(String whenToThrow,
																								   Class<? extends Throwable> exceptionToBeThrown,
																								   Class<? extends Throwable> expectedExeption) throws Exception {
        setupMockToGenerateExceptionOnUpdateTransaction(whenToThrow, exceptionToBeThrown);
		expectedException.expect(expectedExeption);
		NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();

		Transaction transaction = null;
		try {
			transaction = transactionFactory.createTransaction();
			abmfOperation.expireQuota(serviceRgNonMonitoryBalance.getSubscriberIdentity(), serviceRgNonMonitoryBalance.getProductOfferId(), transaction);
		} finally {
			if (transaction != null) transaction.end();
		}
    }


    @Test
    public void expireQuota_should_markDead_DB_when_SQLException_has_dbdown_resultcode() throws Exception {
		setupMockToGenerateExceptionOnUpdateTransaction(EXECUTE, SQLException.class);
		Transaction transaction = transactionFactory.createTransaction();
		when(transaction.isDBDownSQLException(any(SQLException.class))).thenReturn(true);
		NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();

		try {
			transaction = transactionFactory.createTransaction();
			abmfOperation.expireQuota(serviceRgNonMonitoryBalance.getSubscriberIdentity(), serviceRgNonMonitoryBalance.getProductOfferId(), transaction);
		} catch (OperationFailedException e) {

		} finally {
			if (transaction != null) transaction.end();
		}

		verify(transaction).markDead();
    }

	@Test(expected = OperationFailedException.class)
	public void expireQuota_should_throw_OperationFailedException_when_transaction_is_null() throws Exception {
		Transaction transaction = null;
		NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();

		try {
			abmfOperation.expireQuota(serviceRgNonMonitoryBalance.getSubscriberIdentity(), serviceRgNonMonitoryBalance.getProductOfferId(), transaction);
		} finally {
			if (transaction != null) transaction.end();
		}
	}

    @Test
    public void expireQuota_should_markDead_DB_When_maxquerytimeoutcount_reached() throws Exception {
		SQLException sqlException = new SQLException("query timeout", "timeout", 1013);
		setupMockToGenerateExceptionOnUpdateTransaction(EXECUTE, sqlException);
		Transaction transaction = transactionFactory.createTransaction();
		NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();

		expireQuotaWithHandledException(transaction, serviceRgNonMonitoryBalance);
		expireQuotaWithHandledException(transaction, serviceRgNonMonitoryBalance);
		expireQuotaWithHandledException(transaction, serviceRgNonMonitoryBalance);

		verify(transaction).markDead();
    }

	private void expireQuotaWithHandledException(Transaction transaction, NonMonetoryBalance serviceRgNonMonitoryBalance) throws TransactionException {
		try {
			abmfOperation.expireQuota(serviceRgNonMonitoryBalance.getSubscriberIdentity(), serviceRgNonMonitoryBalance.getProductOfferId(), transaction);
		} catch (OperationFailedException e) {

		}
	}

	@Test
    public void expireQuota_should_generate_alert_when_sql_exception_is_thrown_due_to_QUERY_TIME_OUT_ERROR() throws Exception {
		SQLException sqlException = new SQLException("query timeout", "timeout", 1013);
		setupMockToGenerateExceptionOnUpdateTransaction(EXECUTE, sqlException);

		NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances();
		Transaction transaction = transactionFactory.createTransaction();

		try {
			abmfOperation.expireQuota(serviceRgNonMonitoryBalance.getSubscriberIdentity(), serviceRgNonMonitoryBalance.getProductOfferId(), transaction);
		} catch (OperationFailedException e) {

			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString());
		}
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



    protected NonMonetoryBalance createServiceRGNonMonitoryBalances() {
        return createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }

    protected NonMonetoryBalance createServiceRGNonMonitoryBalances(String subscriberId, String productOfferId) {
        Random random = new Random();

        NonMonetoryBalance serviceRgNonMonitoryBalance = new NonMonetoryBalance.NonMonetaryBalanceBuilder(UUID.randomUUID().toString(),
                RandomUtils.nextInt(0, Integer.MAX_VALUE),
                UUID.randomUUID().toString(),
                1l,
                subscriberId,
                UUID.randomUUID().toString(),
                random.nextInt(2),
                UUID.randomUUID().toString(), ResetBalanceStatus.NOT_RESET, null, productOfferId)
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

    private class ABMFOperationExt extends ABMFOperation {

		public ABMFOperationExt(AlertListener alertListener, PolicyRepository policyRepository, int queryTimeout) {
			super(alertListener, policyRepository,queryTimeout);
		}

		@Override
		protected long getCurrentTime() {
			return currentTimestamp.getTime();
		}
	}

}
