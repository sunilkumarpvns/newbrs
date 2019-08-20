package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.pd.bod.BoDData;
import com.elitecore.corenetvertex.pm.DummyPolicyRepository;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.factory.BoDDataFactory;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionData;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.DerbyUtil;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@RunWith(JUnitParamsRunner.class)
public class SubscriptionOperationGetBoDSubscriptionTest {

	private static final String INVALID_STATUS_VALUE = "9";
	private static final String EXPIRED_STATUS = "5";
	
	private static final String PREPARESTATEMENT = "preparedStatement";
	private static final String BEGIN = "begin";
	private static final String EXECUTE = "execute";
	private static final String GET = "get";
	private String subscriberIdentity = "101";
	private String testDB = UUID.randomUUID().toString();

	private DummyPolicyRepository policyRepository;

	/*@Mock
	ProductOfferStore productOfferStore;
	@Mock
	BoDPackageStore bodPackageStore;*/

	private BoDPackage bodPackage;
	private DummyTransactionFactory transactionFactory;
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private BoDSubscriptionOperationTestSuite.BoDSubscriptionDBHelper helper;
	private UMOperationTest.DummySPRInfo sprInfo;

	
	@BeforeClass
	public static void setUpClass() throws Exception {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	}

	@Before
	public void setUp() throws Exception {


		MockitoAnnotations.initMocks(this);
        String url = "jdbc:derby:memory:"+ testDB +";create=true";
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", SubscriptionOperationTestSuite.DS_NAME, url, "", "", 1, 5000, 3000);
		transactionFactory = (DummyTransactionFactory) new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1).build();
		policyRepository = new DummyPolicyRepository();
		bodPackage = getBoDPkg();
		helper = new BoDSubscriptionOperationTestSuite.BoDSubscriptionDBHelper(transactionFactory);
		sprInfo = new UMOperationTest.DummySPRInfo();
		sprInfo.setSubscriberIdentity(subscriberIdentity);
		createTablesAndInsertSubscriptionRecords();

	}
	
	@After
	public void afterDropTables() throws Exception {
		helper.dropTables();
		DBUtility.closeQuietly(transactionFactory.getConnection());
		DerbyUtil.closeDerby(testDB);
	}
	
	/**
	 * <PRE>
	 * 3 subscription created
	 * SubscriberIdentity > BoDSubscriptionData
	 * 				 101  ---- > 1,3 
	 * 	 			 102  ---- > 2
	 * 	 			 103  ---- > no subscription
	 * </PRE>
	 */
	private void createTablesAndInsertSubscriptionRecords() throws Exception {
		getLogger().debug(this.getClass().getSimpleName(), "creating DB");

		helper.createTables();

		helper.insertBoDRecords(getBoDSubscritionData());

		getLogger().debug(this.getClass().getSimpleName(), "DB created");
	}

	/*
	 * Add Adddon record here.
	 * 
	 * Used for inserting table entry and generating expected entity
	 */
	private List<SubscriptionData> getBoDSubscritionData() {

		SubscriptionData record1 = new SubscriptionData("101", bodPackage.getId(), "2014-10-29 09:26:50.12", "2020-12-31 09:26:50.12",
				"2", "0", "1", "2020-12-31 09:26:50.12",null);

		SubscriptionData record2 = new SubscriptionData("102", bodPackage.getId(), "2014-10-29 09:26:50.12", "2020-12-31 09:26:50.12",
				"2", "0", "2", "2020-12-31 09:26:50.12",null);

		SubscriptionData record3 = new SubscriptionData("101", bodPackage.getId(), "2014-10-29 09:26:50.12", "2020-12-31 09:26:50.12",
				"2", "0", "3", "2020-12-31 09:26:50.12",null);
		
		record1.setBod(bodPackage);
		record2.setBod(bodPackage);
		record3.setBod(bodPackage);

		return Arrays.asList(record1, record2, record3);
	}

	@Test
	@Parameters(value = { "101", "102", "103" })
	public void giveListOfBoDSubscriptionsForValidSubscriberId(String subscriberIdentity) throws Exception {
		SubscriptionOperationImpl subscriptionOperation = getNewSubscriptionOperation();
		Map<String, Subscription> actualSubscriptions = subscriptionOperation.getBodSubscriptions(subscriberIdentity, transactionFactory);
		Map<String, Subscription> expectedBoDSubscription = helper.getBoDForSubscriber(subscriberIdentity);
		assertReflectionEquals(expectedBoDSubscription, actualSubscriptions, ReflectionComparatorMode.LENIENT_ORDER);
	}

	private BoDSubscriptionOperationTestSuite.SubscriptionOperationExt getNewSubscriptionOperation() {
		return new BoDSubscriptionOperationTestSuite.SubscriptionOperationExt(mock(AlertListener.class), policyRepository);
	}

	@Test
	@Parameters(value = { "501" })
	public void returnsEmptyListWhenNoSubscriptionFound(String subscriberIdentity) throws Exception {
		SubscriptionOperationImpl subscriptionOperation = getNewSubscriptionOperation();
		Map<String, Subscription> actualSubscriptions = subscriptionOperation.getBodSubscriptions(subscriberIdentity, transactionFactory);

		assertTrue(actualSubscriptions.isEmpty());
	}


	@SuppressWarnings("unused")
	private Object[][] dataProviderForOperationFailedExceptionWhenAnyExceptionIsThrown() {
		return new Object[][] {
				{
						PREPARESTATEMENT, TransactionException.class
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

	/**
	 * when SQLException is thrown, it will be handled in method.
	 * 
	 * Empty field map will be returned. See console for Trace log
	 */
	@Test
	@Parameters(method = "dataProviderForOperationFailedExceptionWhenAnyExceptionIsThrown")
	public void throwOperationFailedExceptionWhenAnyExceptionIsThrown(
			String whenToThrow,
			Class<? extends Throwable> exceptionToBeThrown) throws Exception {

		setupMockToGenerateException(whenToThrow, exceptionToBeThrown);

		expectedException.expect(OperationFailedException.class);

		SubscriptionOperationImpl operation = getNewSubscriptionOperation();

		operation.getBodSubscriptions(getFirstBoDSubscriberId(), transactionFactory);
	}

	@Test
	public void throwOperationFailedExceptionWhenDBIsDown() throws Exception {
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();

		setUpMockTransactionFactoryDead();

		expectedException.expect(OperationFailedException.class);

		operation.getBodSubscriptions(getFirstBoDSubscriberId(), transactionFactory);

	}
	
	public void setupMockToGenerateException(String whenToThrow, Class<? extends Throwable> exceptionToBeThrown) throws Exception {
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);
		ResultSet resultSet = mock(ResultSet.class);

		transactionFactory = spy(transactionFactory);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());
		doReturn(resultSet).when(preparedStatement).executeQuery();

		if (PREPARESTATEMENT.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(transaction).prepareStatement(Mockito.anyString());
		} else if (BEGIN.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(transaction).begin();
		} else if (EXECUTE.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(preparedStatement).executeQuery();
		} else if (GET.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(resultSet).next();
		}

	}
	
	private void setUpMockTransactionFactoryDead() {
		transactionFactory = spy(transactionFactory);
		when(transactionFactory.isAlive()).thenReturn(false);
	}
	
	private String getFirstBoDSubscriberId() {
		return getBoDSubscritionData().get(0).getSubscriberId();
	}

	/**
	 * After all operation, transaction should end properly
	 * 
	 * Actually its closing connection, so verified connection.close() call
	 * 
	 */
	@Test
	public void alwaysEndsTransactionAfterGetBodSusbcriptionIsCalled() throws Exception {

		SubscriptionOperationImpl operation = getNewSubscriptionOperation();

		operation.getBodSubscriptions(getFirstBoDSubscriberId(), transactionFactory);

		Connection connection = getConnection();

		assertTrue(connection.isClosed());
	}
	
	private Connection getConnection() {
		Connection connection = transactionFactory.getConnection();
		return connection;
	}

	@Test
	public void generate_DB_NO_CONNECTION_AlertWhenConnectionNotFound() throws Exception {
		AlertListener alertListener = mock(AlertListener.class);
		TransactionFactory transactionFactory = setUpMockTogenerateDB_NO_CONNECTION_alert();
		SubscriptionOperationImpl operation = new BoDSubscriptionOperationTestSuite.SubscriptionOperationExt(alertListener, policyRepository);

		expectedException.expect(OperationFailedException.class);
		try {
			operation.getBodSubscriptions(getFirstBoDSubscriberId(), transactionFactory);
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), Mockito.anyString(), Mockito.anyString());
			throw e;
		}

		fail("getSubscriptions should throw Exception");
	}

	@Test
	public void generate_DB_HIGH_QUERY_RESPONSE_TIME_AlertWhenExecutionTimeIsHigh() throws Exception {
		TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlert();
		AlertListener alertListener = mock(AlertListener.class);
		SubscriptionOperationImpl operation = new BoDSubscriptionOperationTestSuite.SubscriptionOperationExt(alertListener, policyRepository);

		operation.getBodSubscriptions(getFirstBoDSubscriberId(), transactionFactory);

		verify(alertListener, only())
				.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
	}


	@Test
	public void generate_QUERYTIMEOUT_AlertWhenTimeoutOccurs() throws Exception {
		AlertListener alertListener = mock(AlertListener.class);
		SubscriptionOperationImpl operation = new BoDSubscriptionOperationTestSuite.SubscriptionOperationExt(alertListener, policyRepository);

		expectedException.expect(OperationFailedException.class);
		
		TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTalert();

		try {
			operation.getSubscriptions(getFirstBoDSubscriberId(), transactionFactory);
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito
							.anyString());
			throw e;
		}
		
		fail("should throw OperationFailedException");
	}


	@Test
	@Parameters(value = { EXPIRED_STATUS, INVALID_STATUS_VALUE })
	public void listBoDWhichHasStatusUnsubscribedOrInvalid(String subscriptionState) throws Exception {
		String subscriberIdentity = "110";
		setUpForBoDWithInvalidStatus(subscriptionState, subscriberIdentity);

		SubscriptionOperationImpl operation = getNewSubscriptionOperation();

		assertTrue(operation.getBodSubscriptions(subscriberIdentity, transactionFactory).isEmpty());
	}

	@Test
	@Parameters(value = { "2014-12-31 09:26:50.12", "2014-10-29 09:26:50.12", "2015-01-01 00:00:00.00", "2015-01-01 01:01:00.00", "" })
	public void shouldNotReturnExpiredBoDPackages(String expiryDate) throws Exception {
		if (expiryDate.isEmpty()) {
			expiryDate = null;
		}

		String subscriberIdentity = "110";

		setUpForBoDWithExpiredTime(expiryDate, subscriberIdentity);

		SubscriptionOperationImpl operation = getNewSubscriptionOperation();

		assertTrue(operation.getBodSubscriptions(subscriberIdentity, transactionFactory).isEmpty());
	}

	@Test
	public void listBoDSubscriptionWithResetDateSameAsExpirydateIfResetDateNotConfigured() throws Exception {
		String subscriberIdentity = "110";

		SubscriptionData data1 = new SubscriptionData(subscriberIdentity, bodPackage.getId(), "2014-10-29 09:26:50.12", "2020-10-29 09:26:50.12",
				"2", "1", "10", null,null);

		data1.setType(SubscriptionType.BOD.name());

		data1.setBod(bodPackage);

		helper.insertBoDSubscription(data1);

		SubscriptionOperationImpl operation = getNewSubscriptionOperation();

		Map<String, Subscription> actualSubscriptions = operation.getBodSubscriptions(subscriberIdentity, transactionFactory);
		Map<String, Subscription> expectedBoDSubscription = helper.getBoDForSubscriber(subscriberIdentity);

		getLogger().debug("TEST", "expected: " + expectedBoDSubscription.get(0));

		assertLenientEquals(expectedBoDSubscription, actualSubscriptions);
	}

	private void setUpForBoDWithExpiredTime(String expiryDate, String subscriberIdentity) throws Exception {
		SubscriptionData data1 = new SubscriptionData(subscriberIdentity, bodPackage.getId(), "2014-10-29 09:26:50.12", expiryDate,
				"2", "1", "10", "2015-12-31 09:26:50.12",null);

		data1.setBod(bodPackage);

		helper.insertBoDSubscription(data1);

	}

	private void setUpForBoDWithInvalidStatus(String subscriptionState, String subscriberIdentity) throws Exception {
		SubscriptionData data1 = new SubscriptionData(subscriberIdentity, bodPackage.getId(), "2014-10-29 09:26:50.12", "2020-11-29 09:26:50.12",
				subscriptionState, "1", "10", "2015-12-31 09:26:50.12",null);

		data1.setBod(bodPackage);

		helper.insertBoDSubscription(data1);
	}
	
	private TransactionFactory setUpMockToGenerateHIGHRESPONSEAlert() throws Exception {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

		when(preparedStatement.executeQuery()).thenAnswer(new Answer<ResultSet>() {

			// / paused call to generate high response time
			public ResultSet answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {
				long startTime = System.currentTimeMillis();
				long elapsedTime = 0;
				while (elapsedTime < AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10) {
					elapsedTime = System.currentTimeMillis() - startTime;
				}
				return mock(ResultSet.class);
			};

		});
		return transactionFactory;
	}

	private TransactionFactory setUpMockToGenerateQUERYTIMEOUTalert() throws Exception {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

		when(preparedStatement.executeQuery()).thenThrow(new SQLException("query timeout", "timeout", CommonConstants.QUERY_TIMEOUT_ERRORCODE));
		return transactionFactory;
	}

	private TransactionFactory setUpMockTogenerateDB_NO_CONNECTION_alert() throws TransactionException {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doThrow(new TransactionException("Connection not found", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).begin();
		return transactionFactory;
	}


	private BoDPackage getBoDPkg() {
		BoDData bodData = new BoDDataFactory()
				.withId("100").withName("BoD").withStatus(PkgStatus.ACTIVE.name())
				.withMode("LIVE").withValidityPeriod("30").withValidityPeriodUnit("DAY").build();

		BoDPackage bodPackage = BoDDataFactory.createBoDPkg(bodData, PolicyStatus.SUCCESS);
		policyRepository.addBoDPkg(bodPackage);
		return bodPackage;
	}

}