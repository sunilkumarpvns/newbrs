package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.spr.UMBatchOperation.BatchOperationData;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.DBHelper;
import com.elitecore.corenetvertex.util.DerbyUtil;
import com.elitecore.corenetvertex.util.Maps;
import com.elitecore.corenetvertex.util.Maps.Entry;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@RunWith(JUnitParamsRunner.class)
public class UMOperationTest {


	private static final String DEFAULT_SUBSCRIBER_ID = "007";
	private static Timestamp FUTURE_DATE;
	private static final int HIGH_RESPONSE_TIME_LIMIT_IN_MS = 100;
	private static final String DS_NAME = "test-DB";
	private static final String PREPARED_STATEMENT = "preparedStatement";
	private static final String BEGIN = "begin";
	private static final String EXECUTE = "execute";
	private static final String GET = "get";
	private static final Object TRANSACTION = "transaction";
	private static final long CURRENT_TIME = System.currentTimeMillis();
	

	private DummyTransactionFactory transactionFactory;
	private UMOperationTestHelper  helper;
	@Mock private ProductOfferStore productOfferStore;
	@Mock private ProductOffer productOffer;
	@Mock private PolicyRepository policyRepository;
	@Mock private AlertListener alertListener;


	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	private UMOperation umOperation;

	static {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3650));
		calendar.set(Calendar.MILLISECOND, 0);

		FUTURE_DATE = new Timestamp(calendar.getTimeInMillis());
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	}
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, "jdbc:derby:memory:TestingDB;create=true", "", "", 1, 5000, 3000);
		transactionFactory = (DummyTransactionFactory) new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1).build();
		helper = new UMOperationTestHelper(transactionFactory);
		createTablesAndInsertUsageRecords();
		umOperation = newUMOperation();
		
	}

	public static class DummySPRInfo extends SPRInfoImpl {
		private LinkedHashMap<String,Subscription> subscriptions;

		@Override
		public String getSubscriberIdentity() {
			return subscriberIdentity;
		}

		@Override
		public void setSubscriberIdentity(String subscriberIdentity) {
			this.subscriberIdentity = subscriberIdentity;
		}

		private String subscriberIdentity;

		public void setActiveSubscriptions(LinkedHashMap<String,Subscription> subscriptions) {
			this.subscriptions = subscriptions;
		}

		@Override
		public LinkedHashMap<String, Subscription> getActiveSubscriptions(long currentTimeInMillies) throws OperationFailedException {
			return subscriptions;
		}
	}

	@Test
	public void test_getUsageBySPRInfo_give_usage_information() throws Exception {
		DummySPRInfo sprInfo = new DummySPRInfo();
		sprInfo.setSubscriberIdentity(DEFAULT_SUBSCRIBER_ID);
		sprInfo.setActiveSubscriptions(helper.addOnSubscriptions.get(DEFAULT_SUBSCRIBER_ID));
		Map<String, Map<String, SubscriberUsage>> actualUsage = umOperation.getUsage(sprInfo, transactionFactory);
		Map<String, Map<String, SubscriberUsage>> expectedUsage = helper.getUsageForSubscriber(DEFAULT_SUBSCRIBER_ID);
		assertReflectionEquals(expectedUsage, actualUsage, ReflectionComparatorMode.LENIENT_ORDER);
	}

	@Test
	public void test_getUsage_should_give_usage_information() throws Exception {

		Map<String, Map<String, SubscriberUsage>> actualUsage = umOperation.getUsage(DEFAULT_SUBSCRIBER_ID, helper.addOnSubscriptions.get(DEFAULT_SUBSCRIBER_ID), transactionFactory);
		Map<String, Map<String, SubscriberUsage>> expectedUsage = helper.getUsageForSubscriber(DEFAULT_SUBSCRIBER_ID);
		
		assertReflectionEquals(expectedUsage, actualUsage, ReflectionComparatorMode.LENIENT_ORDER);

	}
	
	@Test
	public void testGetUsageBySPRInfoSetsUsageLoadTime() throws Exception {
		UMOperation umOperation = new UMOperation(null, null);
		DummySPRInfo sprInfo = new DummySPRInfo();
		sprInfo.setSubscriberIdentity(DEFAULT_SUBSCRIBER_ID);
		Assume.assumeTrue(sprInfo.getUsageLoadTime() == -1);
		umOperation.getUsage(sprInfo, transactionFactory);
		assertTrue(sprInfo.getUsageLoadTime() >= 0);
	}
	
	private UMOperation newUMOperation() {
		when(policyRepository.getProductOffer()).thenReturn(productOfferStore);
		when(productOfferStore.byId("100")).thenReturn(productOffer);
		when(productOffer.getDataServicePkgId()).thenReturn("101");
		return new UMOperation(alertListener, policyRepository);
	}

	@Test
	public void test_getUsage_should_give_empty_map_when_no_usage_found() throws Exception {
		UMOperation umOperation = newUMOperation();

		Map<String, Map<String, SubscriberUsage>> result = umOperation.getUsage("UNKNOWN_SUBSCRIBER", null, transactionFactory);

		Assert.assertTrue(result.isEmpty());
	}
	
	@SuppressWarnings("unused")
	private Object[][] dataProviderFor_getUsage_should_throw_OperationFailedException_when_any_exception_thrown() {
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
		},
				{
						GET, SQLException.class
		}
		};
	}

	/*
	 * when SQLException is thrown, it will be handled in method.
	 * 
	 * Empty field map will be returned. See console for Trace log
	 */
	@Test
	@Parameters(method = "dataProviderFor_getUsage_should_throw_OperationFailedException_when_any_exception_thrown")
	public void test_getUsage_should_throw_OperationFailedException_when_any_exception_thrown(
			String whenToThrow,
			Class<? extends Throwable> exceptionToBeThrown) throws Exception {

		setupMockToGenerateExceptionTestGetUsage(whenToThrow, exceptionToBeThrown);

		expectedException.expect(OperationFailedException.class);

		umOperation.getUsage("any", Collections.<String,Subscription>emptyMap(), transactionFactory);
	}
	
	public void setupMockToGenerateExceptionTestResetBillingCycle(String whenToThrow, Class<? extends Throwable> exceptionToBeThrown) throws Exception {
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);
		ResultSet resultSet = mock(ResultSet.class);

		transactionFactory = spy(transactionFactory);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());
		doReturn(resultSet).when(preparedStatement).executeQuery();

		if (TRANSACTION.equals(whenToThrow)) {
			doReturn(null).when(transactionFactory).createTransaction();
			doReturn(null).when(transactionFactory).createReadOnlyTransaction();
		} else if (PREPARED_STATEMENT.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(transaction).prepareStatement(Mockito.anyString());
		} else if (BEGIN.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(transaction).begin();
		} else if (EXECUTE.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(preparedStatement).executeQuery();
			doThrow(exceptionToBeThrown).when(preparedStatement).executeUpdate();
		} else if (GET.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(resultSet).next();
		}
	}

	
	public void setupMockToGenerateExceptionTestGetUsage(String whenToThrow, Class<? extends Throwable> exceptionToBeThrown) throws Exception {
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);
		ResultSet resultSet = mock(ResultSet.class);
	
		transactionFactory = spy(transactionFactory);
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());
		doReturn(resultSet).when(preparedStatement).executeQuery();

		if (TRANSACTION.equals(whenToThrow)) {
			doReturn(null).when(transactionFactory).createReadOnlyTransaction();
		} else if (PREPARED_STATEMENT.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(transaction).prepareStatement(Mockito.anyString());
		} else if (BEGIN.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(transaction).begin();
		} else if (EXECUTE.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(preparedStatement).executeQuery();
			doThrow(exceptionToBeThrown).when(preparedStatement).executeUpdate();
		} else if (GET.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(resultSet).next();
		}
	}

	
	
	@SuppressWarnings("unused")
	private Object[][] dataProviderFor_insert_should_throw_OperationFailedException_when_any_exception_thrown() {
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
		},
				
		};
	}

	@Test
	@Parameters(method = "dataProviderFor_insert_should_throw_OperationFailedException_when_any_exception_thrown")
	public void test_insert_should_throw_OperationFailedException_when_any_exception_thrown(
			String whenToThrow,
			Class<? extends Throwable> exceptionToBeThrown) throws Exception {

		setupMockToGenerateExceptionTestResetBillingCycle(whenToThrow, exceptionToBeThrown);

		expectedException.expect(OperationFailedException.class);

		SubscriberUsageData record1 = new SubscriberUsageData.SubscriberUsageDataBuilder()
		.withSubscriberIdentity("101")
		.withId("1")
		.withQuotaProfileId("100")
		.withServiceId("103")
		.withSubscriptionId("1000")
		.withDefaultUsage(100)
		.withDefaultReSetDate(FUTURE_DATE.getTime())
		.build();

		umOperation.insert("any", Arrays.asList(record1.newSubscriberUsage()), transactionFactory);
	}
	
	
	@SuppressWarnings("unused")
	private Object[][] dataProviderFor_replace_should_throw_OperationFailedException_when_any_exception_thrown() {
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
		},
		};
	}
	
	@Test
	@Parameters(method = "dataProviderFor_replace_should_throw_OperationFailedException_when_any_exception_thrown")
	public void test_replace_should_throw_OperationFailedException_when_any_exception_thrown(
			String whenToThrow,
			Class<? extends Throwable> exceptionToBeThrown) throws Exception {

		setupMockToGenerateExceptionTestGetUsage(whenToThrow, exceptionToBeThrown);

		expectedException.expect(OperationFailedException.class);

		SubscriberUsageData record1 = new SubscriberUsageData.SubscriberUsageDataBuilder()
		.withSubscriberIdentity("101")
		.withId("1")
		.withQuotaProfileId("100")
		.withServiceId("103")
		.withSubscriptionId("1000")
		.withDefaultUsage(100)
		.withDefaultReSetDate(FUTURE_DATE.getTime())
		.build();

		umOperation.replace("any", Arrays.asList(record1.newSubscriberUsage()), transactionFactory);
	}
	
	
	@SuppressWarnings("unused")
	private Object[][] dataProviderFor_addToExisting_should_throw_OperationFailedException_when_any_exception_thrown() {
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
		},
				
		};
	}
	
	@Test
	@Parameters(method = "dataProviderFor_addToExisting_should_throw_OperationFailedException_when_any_exception_thrown")
	public void test_addToExisting_should_throw_OperationFailedException_when_any_exception_thrown(
			String whenToThrow,
			Class<? extends Throwable> exceptionToBeThrown) throws Exception {

		setupMockToGenerateExceptionTestResetBillingCycle(whenToThrow, exceptionToBeThrown);

		expectedException.expect(OperationFailedException.class);

		SubscriberUsageData record1 = new SubscriberUsageData.SubscriberUsageDataBuilder()
		.withSubscriberIdentity("101")
		.withId("1")
		.withQuotaProfileId("100")
		.withServiceId("103")
		.withSubscriptionId("1000")
		.withDefaultUsage(100)
		.withDefaultReSetDate(FUTURE_DATE.getTime())
		.build();

		umOperation.addToExisting("any", Arrays.asList(record1.newSubscriberUsage()), transactionFactory);
	}

	@Test
	public void test_getUsage_should_throw_OperationFailedException_when_DB_is_down() throws Exception {
		

		setUpMockTransactionFactoryDead();

		expectedException.expect(OperationFailedException.class);

		umOperation.getUsage(getFirstSubscriberId(), null, transactionFactory);

	}
	
	@Test
	public void test_insert_should_throw_OperationFailedException_when_DB_is_down() throws Exception {
		
		setUpMockTransactionFactoryDead();

		expectedException.expect(OperationFailedException.class);

		umOperation.insert(getFirstSubscriberId(), Collections.<SubscriberUsage>emptyList(), transactionFactory);

	}
	
	@Test
	public void test_replace_should_throw_OperationFailedException_when_DB_is_down() throws Exception {
		
		setUpMockTransactionFactoryDead();

		expectedException.expect(OperationFailedException.class);

		umOperation.replace(getFirstSubscriberId(), Collections.<SubscriberUsage>emptyList(), transactionFactory);

	}
	
	@Test
	public void test_addToExisting_should_throw_OperationFailedException_when_DB_is_down() throws Exception {
		
		setUpMockTransactionFactoryDead();

		expectedException.expect(OperationFailedException.class);

		umOperation.addToExisting(getFirstSubscriberId(), Collections.<SubscriberUsage>emptyList(), transactionFactory);

	}

	/**
	 * After all operation, transaction should end properly
	 * 
	 * Actually its closing connection, so verified connection.close() call
	 * 
	 */
	@Test
	public void test_getUsage_should_always_end_transaction() throws Exception {

		umOperation.getUsage(getFirstSubscriberId(), Collections.<String,Subscription>emptyMap(), transactionFactory);

		Connection connection = getConnection();

		assertTrue(connection.isClosed());
	}
	
	@Test
	public void test_insert_should_always_end_transaction() throws Exception {

		umOperation.insert(getFirstSubscriberId(), Collections.<SubscriberUsage>emptyList(), transactionFactory);

		Connection connection = getConnection();

		assertTrue(connection.isClosed());
	}
	
	@Test
	public void test_replace_should_always_end_transaction() throws Exception {

		umOperation.replace(getFirstSubscriberId(), Collections.<SubscriberUsage>emptyList(), transactionFactory);

		Connection connection = getConnection();

		assertTrue(connection.isClosed());
	}
	
	@Test
	public void test_addToExisting_should_always_end_transaction() throws Exception {

		umOperation.addToExisting(getFirstSubscriberId(), Collections.<SubscriberUsage>emptyList(), transactionFactory);

		Connection connection = getConnection();

		assertTrue(connection.isClosed());
	}

	@Test
	public void test_getUsage_should_generate_DB_NO_CONNECTION_alert_when_connection_not_found() throws Exception {
		
		TransactionFactory transactionFactory = setUpMockTogenerateDB_NO_CONNECTION_alert();

		try {
			umOperation.getUsage(getFirstSubscriberId(), null, transactionFactory);
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), Mockito.anyString(), Mockito.anyString());
		}
	}

	@Test
	public void test_getUsage_should_generate_DB_HIGH_QUERY_RESPONSE_TIME_alert_when_execution_time_is_high() throws Exception {
		TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlertForGetUsage();
		umOperation.getUsage(getFirstSubscriberId(), Collections.<String,Subscription>emptyMap(), transactionFactory);

		verify(alertListener, only())
				.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void test_getUsage_should_generate_QUERYTIMEOUT_alert_when() throws Exception {
		UMOperation operation = new UMOperation(alertListener, policyRepository);

		TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTalertForGetUsage();

		try {
			operation.getUsage(getFirstSubscriberId(), null, transactionFactory);
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito
							.anyString());
		}
	}

	private TransactionFactory setUpMockToGenerateQUERYTIMEOUTalertForGetUsage() throws Exception {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

		when(preparedStatement.executeQuery()).thenThrow(new SQLException("query timeout", "timeout", 1013));
		return transactionFactory;
	}

	private TransactionFactory setUpMockToGenerateHIGHRESPONSEAlertForGetUsage() throws Exception {
		TransactionFactory factory = spy(transactionFactory);
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		doReturn(transaction).when(factory).createReadOnlyTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

		when(preparedStatement.executeQuery()).thenAnswer(new Answer<ResultSet>() {

			// / paused call to generate high response time
			public ResultSet answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {
				long startTime = System.currentTimeMillis();
				long elapsedTime = 0;
				while (elapsedTime < HIGH_RESPONSE_TIME_LIMIT_IN_MS + 10) {
					elapsedTime = System.currentTimeMillis() - startTime;
				}
				
				return mock(ResultSet.class);
			}

		});
		
		when(preparedStatement.executeUpdate()).thenAnswer(new Answer<Integer>() {

			// / paused call to generate high response time
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

	private TransactionFactory setUpMockToGenerateHIGHRESPONSEAlertForResetBillingCycle() throws Exception {
		TransactionFactory factory = spy(transactionFactory);
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		doReturn(transaction).when(factory).createTransaction();
		doReturn(transaction).when(factory).createReadOnlyTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

		when(preparedStatement.executeQuery()).thenAnswer(new Answer<ResultSet>() {

			// / paused call to generate high response time
			public ResultSet answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {
				long startTime = System.currentTimeMillis();
				long elapsedTime = 0;
				while (elapsedTime < HIGH_RESPONSE_TIME_LIMIT_IN_MS + 10) {
					elapsedTime = System.currentTimeMillis() - startTime;
				}
				
				return mock(ResultSet.class);
			}

		});
		
		when(preparedStatement.executeUpdate()).thenAnswer(new Answer<Integer>() {

			// / paused call to generate high response time
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

	private TransactionFactory setUpMockTogenerateDB_NO_CONNECTION_alert() throws TransactionException {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doThrow(new TransactionException("Connection not found", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).begin();
		return transactionFactory;
	}

	private Connection getConnection() {
		return ((DummyTransactionFactory) transactionFactory).getConnection();
	}

	private void setUpMockTransactionFactoryDead() {
		transactionFactory = spy(transactionFactory);
		when(transactionFactory.isAlive()).thenReturn(false);
	}

	

	private String getFirstSubscriberId() {
		return "101";
		
	}

	
	@After
	public void dropTables() throws Exception {
		helper.dropTables();
		transactionFactory.getConnection().close();
		DerbyUtil.closeDerby("TestingDB");
	}
	
	@Test
	public void deleteBasePackagUsageShouldThrowOperationFailedExceptionForAnyException() throws Exception {
		
		Transaction transaction = mock(Transaction.class);
		setupMockToGenerateException_test_deleteBasePackage(EXECUTE, SQLException.class, transaction);

		expectedException.expect(OperationFailedException.class);

		umOperation.deleteBasePackageUsage("any", "any", transaction);
	}
	
	private void setupMockToGenerateException_test_deleteBasePackage(
			String whenToThrow, Class<? extends Throwable> exceptionToBeThrown,
			Transaction transaction) throws Exception {

		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());
		
		doThrow(exceptionToBeThrown).when(preparedStatement).executeUpdate();

	}

	@Test
	public void deleteBasePackagUsageShouldDeletesUsageOfSubscriberBasePackage() throws Exception {
		  String packageId = "default_id";
		  helper.createBasePackageAndAddUsage(DEFAULT_SUBSCRIBER_ID, packageId, "100" );
		  Transaction transaction = transactionFactory.createTransaction();
		  umOperation.deleteBasePackageUsage(DEFAULT_SUBSCRIBER_ID, packageId, transaction);
		  transaction.end();
		  helper.removeUsage(DEFAULT_SUBSCRIBER_ID, packageId );
		  helper.checkSubscriberUsage(DEFAULT_SUBSCRIBER_ID);
	}
	
	@Test
	public void deleteBasePackageUsageShouldNotDeleteAnyUsageWhenUsageWithPackageDoesNotExist() throws Exception {
		
		List<SubscriberUsage> beforeSubscriberUsageFromDB = helper.getSubscriberUsageFromDB(DEFAULT_SUBSCRIBER_ID);
			Transaction transaction = transactionFactory.createTransaction();
		  umOperation.deleteBasePackageUsage(DEFAULT_SUBSCRIBER_ID, "default_id" + 2, transaction);
		  transaction.end();
		  List<SubscriberUsage> afterSubscriberUsageFromDB = helper.getSubscriberUsageFromDB(DEFAULT_SUBSCRIBER_ID);
		  Assert.assertSame(beforeSubscriberUsageFromDB.size(), afterSubscriberUsageFromDB.size());
	}
	
	@Test
	public void deleteBasePackageUsageShouldReturnCounterOfDeltedRowsAsExpected()
			throws Exception {

		String packageId = "default_id";
		  helper.createBasePackageAndAddUsage(DEFAULT_SUBSCRIBER_ID, packageId, "100" );
		Transaction transaction = transactionFactory.createTransaction();
		int deletedRows = umOperation.deleteBasePackageUsage(DEFAULT_SUBSCRIBER_ID, packageId, transaction);
		transaction.end();
		
		Assert.assertSame(1, deletedRows);
	}
	
	@Test
	public void testResetBillingCycleUsageShouldInsertResetUsageRequiredRowsInTable() throws Exception {

		helper.createBasePackageAndAddUsage(DEFAULT_SUBSCRIBER_ID, "101", "100");
		
		umOperation.resetBillingCycle(DEFAULT_SUBSCRIBER_ID, "1", "100", FUTURE_DATE.getTime(), "TO DELETE", "param1", "param2", "param3", transactionFactory);
		
		List<ResetUsageRequiredData> actualResetUsageRequiredData = helper.getResetUsageRequiredRows(DEFAULT_SUBSCRIBER_ID, "101");
		
		List<ResetUsageRequiredData> expectedResetUsageRequiredData = new ArrayList<ResetUsageRequiredData>();
		expectedResetUsageRequiredData.add(getExpectedResetUsageRequiredData());
		
		ReflectionAssert.assertLenientEquals(expectedResetUsageRequiredData, actualResetUsageRequiredData);
	}
	
	@SuppressWarnings("unused")
	private Object[][] dataProviderForResetBillingCycleShouldThrowOperationFailedExceptionWhenAnyExceptionIsThrown() {
		
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
				},
		};
	}
	
	@Test
	@Parameters(method = "dataProviderForResetBillingCycleShouldThrowOperationFailedExceptionWhenAnyExceptionIsThrown")
	public void testResetBillingCycleShouldThrowOperationFailedExceptionWhenAnyExceptionIsThrown(String whenToThrow, Class<? extends Throwable> exceptionToBeThrown) throws Exception {

		setupMockToGenerateExceptionTestResetBillingCycle(whenToThrow, exceptionToBeThrown);

		expectedException.expect(OperationFailedException.class);

		umOperation.resetBillingCycle(DEFAULT_SUBSCRIBER_ID, "1", "100", 0l, "TO DELETE", "param1", "param2", "param3", transactionFactory);
	}
	
	@Test
	public void testResetBillingCycleShouldThrowOperationFailedExceptionWhenDBIsDown() throws Exception {
		
		setUpMockTransactionFactoryDead();

		expectedException.expect(OperationFailedException.class);

		umOperation.resetBillingCycle(DEFAULT_SUBSCRIBER_ID, "1", "101", 0l, "TO DELETE", "param1", "param2", "param3", transactionFactory);
	}
	
	@Test
	public void testResetBillingCycleShouldAlwaysEndransaction() throws Exception {

		umOperation.resetBillingCycle(DEFAULT_SUBSCRIBER_ID, "1", "100", 0l, "TO DELETE", "param1", "param2", "param3", transactionFactory);

		Connection connection = getConnection();

		assertTrue(connection.isClosed());
	}
	
	@Test
	public void testResetBillingCycleShouldGenerate_DB_NO_CONNECTION_AlertWhenConnectionNotFound() throws Exception {
		
		TransactionFactory transactionFactory = setUpMockTogenerateDB_NO_CONNECTION_alert();

		try {
			umOperation.resetBillingCycle(DEFAULT_SUBSCRIBER_ID, "1", "101", 0l, "TO DELETE", "param1", "param2", "param3", transactionFactory);
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), Mockito.anyString(), Mockito.anyString());
		}
	}

	@Test
	public void testResetBillingCycleShouldGenerate_DB_HIGH_QUERY_RESPONSE_TIME_AlertWhenExecutionTimeIsHigh() throws Exception {
		
		TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlertForResetBillingCycle();
		umOperation.resetBillingCycle(DEFAULT_SUBSCRIBER_ID, "1", "100", 0l, "TO DELETE", "param1", "param2", "param3", transactionFactory);

		verify(alertListener, only())
				.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void testResetBillingCycleShouldGenerate_QUERYTIMEOUTAlertWhenQueryTimesOut() throws Exception {

		TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTalertForGetUsage();
		try {
			umOperation.resetBillingCycle(DEFAULT_SUBSCRIBER_ID, "1", "100", 0l, "TO DELETE", "param1", "param2", "param3", transactionFactory);
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito
							.anyString());
		}
	}
	
	private ResetUsageRequiredData getExpectedResetUsageRequiredData() {
		
		ResetUsageRequiredData resetUsageRequiredData = new ResetUsageRequiredData();
		resetUsageRequiredData.setSubscriberIdentity(DEFAULT_SUBSCRIBER_ID);
		resetUsageRequiredData.setPackageId("101");
		return resetUsageRequiredData;
	}
	
	private class UMOperationTestHelper {

		private TransactionFactory transactionFactory;
		// /SubscriberIdentity -- > PackageName/SubscriptionId -- >
		// QuotaProfile+serviceId
		private Map<String, Map<String, Map<String, SubscriberUsage>>> usageInfoByUserId;
		private Map<String, LinkedHashMap<String,Subscription>> addOnSubscriptions;
		private Map<String, BasePackage> idToBasePackage;
		private Calendar weeklyResetTime;
		private Calendar dailyResetTime;
		private final String TABLE_TBLT_USAGE = "TBLT_USAGE";
		private final String TABLE_TBLT_USAGE_HISTORY = "TBLT_USAGE_HISTORY";
		private final String TABLE_TBLM_RESET_USAGE_REQ = "TBLM_RESET_USAGE_REQ";

		public UMOperationTestHelper(TransactionFactory transactionfactory) {
			this.transactionFactory = transactionfactory;
			this.usageInfoByUserId = new HashMap<String, Map<String, Map<String, SubscriberUsage>>>(10);
			this.addOnSubscriptions = new HashMap<String, LinkedHashMap<String,Subscription>>();
			this.idToBasePackage = new HashMap<String, BasePackage>();
			dailyResetTime = Calendar.getInstance();
			dailyResetTime.setTimeInMillis(CURRENT_TIME);
			dailyResetTime.set(Calendar.HOUR_OF_DAY, 23);
			dailyResetTime.set(Calendar.MINUTE, 59);
			dailyResetTime.set(Calendar.SECOND, 59);
			dailyResetTime.set(Calendar.MILLISECOND, 0);

			weeklyResetTime = Calendar.getInstance();
			weeklyResetTime.setTimeInMillis(CURRENT_TIME);
			weeklyResetTime.set(Calendar.HOUR_OF_DAY, 23);
			weeklyResetTime.set(Calendar.MINUTE, 59);
			weeklyResetTime.set(Calendar.SECOND, 59);
			weeklyResetTime.set(Calendar.MILLISECOND, 0);
			weeklyResetTime.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		}

		public void checkSubscriberUsage(String defaultSubscriberId) throws Exception {
			
			List<SubscriberUsage> subscriberUsageFromDB = helper.getSubscriberUsageFromDB(defaultSubscriberId);
			Map<String, Map<String, SubscriberUsage>> packageIdToServiceIdUsage = usageInfoByUserId.get(defaultSubscriberId);
			if (Collectionz.isNullOrEmpty(subscriberUsageFromDB) && com.elitecore.commons.base.Maps.isNullOrEmpty(packageIdToServiceIdUsage)) {
				return;
			}
			
			List<SubscriberUsage> subscriberUsagesToCompare = new ArrayList<SubscriberUsage>();
			for ( Map<String, SubscriberUsage> entry : packageIdToServiceIdUsage.values()) {
				
				for (SubscriberUsage susbcriberUsage : entry.values()) {
					subscriberUsagesToCompare.add(susbcriberUsage);
				}
			}
			
			ReflectionAssert.assertLenientEquals(subscriberUsagesToCompare, subscriberUsageFromDB);
		}

		public void removeUsage(String defaultSubscriberId, String packageId) {
			usageInfoByUserId.get(defaultSubscriberId).remove(packageId);			
		}

		public void insertUsageRecord(SubscriberUsageData record,Map<String, Subscription> addOnSubscriptions) throws Exception {

			record.setDailyResetTimestamp(dailyResetTime.getTimeInMillis());
			record.setWeeklyResetTimestamp(weeklyResetTime.getTimeInMillis());

			Map<String, Map<String, SubscriberUsage>> subscriptionOrPackageWiseUsage = usageInfoByUserId.get(record.getSubscriberIdentity());
			if (subscriptionOrPackageWiseUsage == null) {
				subscriptionOrPackageWiseUsage = new HashMap<String, Map<String, SubscriberUsage>>(10);
				usageInfoByUserId.put(record.getSubscriberIdentity(), subscriptionOrPackageWiseUsage);
			}

			SubscriberUsage subscriberUsage = record.newSubscriberUsage();

			String key;
			if(subscriberUsage.getSubscriptionId() != null) {
				key = subscriberUsage.getSubscriptionId();
			} else {
				key = subscriberUsage.getPackageId();
			}

			Map<String, SubscriberUsage> serviceWiseUsage = subscriptionOrPackageWiseUsage.get(key);
			if (serviceWiseUsage == null) {
				serviceWiseUsage = new HashMap<String, SubscriberUsage>();
				subscriptionOrPackageWiseUsage.put(key, serviceWiseUsage);
			}
			
			
			if(addOnSubscriptions != null && addOnSubscriptions.isEmpty() == false) {
				LinkedHashMap<String,Subscription> addOnSubscriptions2 = this.addOnSubscriptions.get(record.getSubscriberIdentity());
				if(addOnSubscriptions2 == null){
					addOnSubscriptions2 = new LinkedHashMap<String, Subscription>();
					this.addOnSubscriptions.put(record.getSubscriberIdentity(), addOnSubscriptions2);
				}
				
				
				addOnSubscriptions2.putAll(addOnSubscriptions);	
			}
			

			serviceWiseUsage.put(subscriberUsage.getQuotaProfileId() + CommonConstants.USAGE_KEY_SEPARATOR + subscriberUsage.getServiceId(), subscriberUsage);
		}

		public void insertUsageRecord(List<SubscriberUsageData> subscriberUsageInsertList, Map<String, Subscription> addOnSubscriptions) throws Exception {

			for (SubscriberUsageData data : subscriberUsageInsertList) {
				insertUsageRecord(data,addOnSubscriptions);
			}
		}

		public void createTables() throws Exception {
			executeQuery(SubscriberUsageData.createTableQuery());
			executeQuery(ResetUsageRequiredData.createTableQuery());
		}

		private void executeQuery(String query) throws Exception {
			Transaction transaction = transactionFactory.createTransaction();
			try {

				transaction.begin();
				transaction.prepareStatement(query).execute();

			} finally {
				//transaction.end();
			}
		}

		private ResultSet executeSelect(String query) throws Exception {
			
			Transaction transaction = transactionFactory.createTransaction();
			try {

				transaction.begin();
				return transaction.prepareStatement(query).executeQuery();

			} finally {
				//transaction.end();
			}
		}

		
		public Map<String, Map<String, SubscriberUsage>> getUsageForSubscriber(String subscriberIdentity) {
			if (usageInfoByUserId.containsKey(subscriberIdentity)) {
				return usageInfoByUserId.get(subscriberIdentity);
			} else {
				return Collections.emptyMap();
			}
		}
		
		
		
		public List<SubscriberUsage> getSubscriberUsageFromDB(String subscriberId) throws Exception {
			
			ResultSet resultSet = executeSelect("SELECT * FROM " + TABLE_TBLT_USAGE + " WHERE SUBSCRIBER_ID = '" + subscriberId + "'");
			List<SubscriberUsageData> subsriberUsageDatas = DBHelper.create(resultSet, SubscriberUsageData.class);
			return createSubscriberUsageFromDatas(subsriberUsageDatas);
		}
		
		public List<ResetUsageRequiredData> getResetUsageRequiredRows(String subscriberId, String packageId) throws Exception {
			
			ResultSet resultSet = executeSelect("SELECT * FROM " + TABLE_TBLM_RESET_USAGE_REQ + " WHERE SUBSCRIBER_IDENTITY = '" + subscriberId + "' AND PACKAGE_ID = '" + packageId + "'" );
			return DBHelper.create(resultSet, ResetUsageRequiredData.class);
		}

		private List<SubscriberUsage> createSubscriberUsageFromDatas(List<SubscriberUsageData> subsriberUsageDatas) {
			
			List<SubscriberUsage> subscriberUsageList = new ArrayList<SubscriberUsage>();
			
			if (subsriberUsageDatas != null) {
				for (SubscriberUsageData subscriberUsageData : subsriberUsageDatas) {
					subscriberUsageList.add(subscriberUsageData.newSubscriberUsage());
				}
			}
			return subscriberUsageList;
		}

		public void dropTables() throws Exception {
			executeQuery(SubscriberUsageData.dropTableQuery());
			executeQuery(ResetUsageRequiredData.dropTableQuery());
			usageInfoByUserId.clear();

			getLogger().debug(this.getClass().getSimpleName(), "Tables Dropped");
		}
		
		public void createBasePackage(String id) {

            BasePackage basePackage = new BasePackage(id, null, null, null, Collections.emptyList(), null, null, null, null, null,
					null, null, null, null, null, null, null, null,null);
			idToBasePackage.put(id, basePackage);
		}
		
		public void createBasePackageAndAddUsage(String subscriberId, String packageId, String productOfferId) throws Exception {
			createBasePackage(packageId);
			SubscriberUsageData record4 = new SubscriberUsageData.SubscriberUsageDataBuilder()
			.withSubscriberIdentity(subscriberId)
			.withId("4")
			.withQuotaProfileId("100")
			.withServiceId("101")
			.withSubscriptionId(null)
			.withDefaultUsage(1000)
			.withDefaultReSetDate(FUTURE_DATE.getTime())
			.withPackageId(packageId)
			.withProductOfferId(productOfferId)
			.build();
			
			List<SubscriberUsageData> subscriberUsageInsertList = Arrays.asList(record4);
			insertUsageRecord(subscriberUsageInsertList, null);
			for (SubscriberUsageData subscriberUsageData : subscriberUsageInsertList) {
				executeQuery(subscriberUsageData.insertQuery());
			}
		}
	}
	
	private void createTablesAndInsertUsageRecords() throws Exception {
		LogManager.getLogger().debug("test", "creating DB");

		helper.createTables();
		
		helper.createBasePackageAndAddUsage(DEFAULT_SUBSCRIBER_ID, "package1", "productoffer1");
		helper.createBasePackageAndAddUsage(DEFAULT_SUBSCRIBER_ID, "package2", "productoffer2");
		insertSubscriptionUsageInformationDatas(helper);
		
	
		for(String subscriberIdentity : helper.usageInfoByUserId.keySet()) {
			helper.checkSubscriberUsage(subscriberIdentity);
		}

		LogManager.getLogger().debug("test", "DB created");
	}

	private void insertSubscriptionUsageInformationDatas(UMOperationTestHelper helper) throws Exception {

		SubscriptionData subscriptionData1 = new SubscriptionData("101", "1", null, null, "0", "1", "1000", null,null);
		SubscriptionData subscriptionData2 = new SubscriptionData("102", "1", null, null, "0", "1", "1000", null,null);
		SubscriptionData subscriptionData3 = new SubscriptionData("103", "1", null, null, "0", "1", "1000", null,null);
		SubscriptionData subscriptionData4 = new SubscriptionData("007", "1", null, null, "0", "1", null, null,null);

        AddOn addOn = new AddOn("1", "addOn1", null, null, Collections.emptyList(), true, true,  1, null, null, null, null, null, null, null, null, null, null, null,null, null, null,"INR");
		subscriptionData1.setAddOn(addOn);
		subscriptionData2.setAddOn(addOn);
		subscriptionData3.setAddOn(addOn);
		subscriptionData4.setAddOn(addOn);
		
		SubscriberUsageData record1 = new SubscriberUsageData.SubscriberUsageDataBuilder()
				.withSubscriberIdentity("101")
				.withId("1")
				.withQuotaProfileId("100")
				.withSubscriptionId("1000")
				.withDefaultUsage(100)
				.withDefaultReSetDate(FUTURE_DATE.getTime())
				.build();

		SubscriberUsageData record2 = new SubscriberUsageData.SubscriberUsageDataBuilder()
				.withSubscriberIdentity("102")
				.withId("2")
				.withQuotaProfileId("100")
				.withServiceId("102")
				.withSubscriptionId("1000")
				.withDefaultUsage(100)
				.withDefaultReSetDate(FUTURE_DATE.getTime())
				.build();

		SubscriberUsageData record3 = new SubscriberUsageData.SubscriberUsageDataBuilder()
				.withSubscriberIdentity("103")
				.withId("3")
				.withQuotaProfileId("100")
				.withServiceId("101")
				.withSubscriptionId("1000")
				.withDefaultUsage(100)
				.withDefaultReSetDate(FUTURE_DATE.getTime())
				.withPackageId("default_id")
				.build();
		
		List<SubscriberUsageData> subscriberUsageInsertList = Arrays.asList(record1, record2, record3);
		LinkedHashMap<String, Subscription> addOnSubscriptions = Maps.newLinkedHashMap(Arrays.asList(
				Entry.newEntry(subscriptionData1.getSubscriptionId(), subscriptionData1.getAddonSubscription()),
				Entry.newEntry(subscriptionData2.getSubscriptionId(), subscriptionData2.getAddonSubscription()),
				Entry.newEntry(subscriptionData3.getSubscriptionId(), subscriptionData3.getAddonSubscription()),
				Entry.newEntry(subscriptionData4.getSubscriptionId(), subscriptionData4.getAddonSubscription())));

		helper.insertUsageRecord(subscriberUsageInsertList, addOnSubscriptions);

		for (SubscriberUsageData subscriberUsageData : subscriberUsageInsertList) {
			helper.executeQuery(subscriberUsageData.insertQuery());
		}
	}
	
	@Test
	public void test_process_callsReplace_whenBatchOperationIsReplace() throws OperationFailedException {
		
		umOperation = spy(new SingleRecordProcessorUMOperation(null, null));
		
		BatchOperationData batchOperationData = new UMBatchOperation(transactionFactory, null, null, null, 1, 1, null)
		.new BatchOperationData(mock(SubscriberUsage.class), DEFAULT_SUBSCRIBER_ID, BatchOperationData.REPLACE);
		
		umOperation.process(batchOperationData, transactionFactory);
		
		verify(umOperation, times(1)).replace(Matchers.eq(DEFAULT_SUBSCRIBER_ID), Mockito.anyObject(), Matchers.eq(transactionFactory));
	}
	
	@Test
	public void test_process_callsInsert_whenBatchOperationIsInsert() throws OperationFailedException {
		
		umOperation = spy(new SingleRecordProcessorUMOperation(null, null));
		
		BatchOperationData batchOperationData = new UMBatchOperation(transactionFactory, null, null, null, 1, 1, null)
		.new BatchOperationData(mock(SubscriberUsage.class), DEFAULT_SUBSCRIBER_ID, BatchOperationData.INSERT);
		
		umOperation.process(batchOperationData, transactionFactory);
		
		verify(umOperation, times(1)).insert(Matchers.eq(DEFAULT_SUBSCRIBER_ID), Mockito.anyObject(), Matchers.eq(transactionFactory));
	}
	
	@Test
	public void test_process_callsAddToExisting_whenBatchOperationIsAddToExisting() throws OperationFailedException {
		
		umOperation = spy(new SingleRecordProcessorUMOperation(null, null));
		
		BatchOperationData batchOperationData = new UMBatchOperation(transactionFactory, null, null, null, 1, 1, null)
		.new BatchOperationData(mock(SubscriberUsage.class), DEFAULT_SUBSCRIBER_ID, BatchOperationData.ADD_TO_EXISTING);
		
		umOperation.process(batchOperationData, transactionFactory);
		
		verify(umOperation, times(1)).addToExisting(Matchers.eq(DEFAULT_SUBSCRIBER_ID), Mockito.anyObject(), Matchers.eq(transactionFactory));
	}
	
	private class SingleRecordProcessorUMOperation extends UMOperation{
		
		public SingleRecordProcessorUMOperation(AlertListener alertListener, PolicyRepository policyRepository) {
			super(alertListener, policyRepository);
		}
	
		@Override
		public void replace(String subscriberIdentity, Collection<SubscriberUsage> usages, TransactionFactory transactionFactory)
				throws OperationFailedException {
			// Deliberately left blank
		}
		
		@Override
		public void insert(String subscriberIdentity, Collection<SubscriberUsage> usages, TransactionFactory transactionFactory)
				throws OperationFailedException {
			// Deliberately left blank
		}
		
		@Override
		public void addToExisting(String subscriberIdentity, Collection<SubscriberUsage> usages, TransactionFactory transactionFactory)
				throws OperationFailedException {
			// Deliberately left blank
		}
	}
}
