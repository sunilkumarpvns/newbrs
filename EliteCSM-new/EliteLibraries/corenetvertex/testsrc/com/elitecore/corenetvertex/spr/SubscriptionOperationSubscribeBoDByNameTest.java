package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.pd.bod.BoDData;
import com.elitecore.corenetvertex.pm.DummyPolicyRepository;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.factory.BoDDataFactory;
import com.elitecore.corenetvertex.spr.SubscriptionOperationTestSuite.SubscriptionOperationExt;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionData;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@RunWith(JUnitParamsRunner.class)
public class SubscriptionOperationSubscribeBoDByNameTest {


	private DummyPolicyRepository policyRepository;
	private DummyTransactionFactory transactionFactory;
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private BoDSubscriptionOperationTestSuite.BoDSubscriptionDBHelper helper;
	private String testDB = UUID.randomUUID().toString();

	@BeforeClass
	public static void setUpClass() throws Exception {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	}

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);
		policyRepository = new DummyPolicyRepository();
        String url = "jdbc:derby:memory:"+ testDB +";create=true";
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", SubscriptionOperationTestSuite.DS_NAME, url, "", "", 1, 5000, 3000);
		transactionFactory = (DummyTransactionFactory) new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1).build();

		helper = new BoDSubscriptionOperationTestSuite.BoDSubscriptionDBHelper(transactionFactory);
		helper.createTables();
	}

	@After
	public void afterDropTables() throws Exception {
		helper.dropTables();
		DBUtility.closeQuietly(transactionFactory.getConnection());
		DerbyUtil.closeDerby(testDB);
	}

	private void setUpMockTransactionFactoryDead() {
		transactionFactory = spy(transactionFactory);
		when(transactionFactory.isAlive()).thenReturn(false);
	}


	@Test
	public void throwsOperationFailedExecptionWhenDBisDown() throws Exception {
		String subscriberId = "101";

		SubscriptionOperationImpl operation = new SubscriptionOperationImpl(mock(AlertListener.class), policyRepository, null,null, null, null, null);
		setUpMockTransactionFactoryDead();

		expectedException.expect(OperationFailedException.class);
		expectedException.expectMessage("Datasource not available"); // part of message
		BoDPackage bodPackage = createBoDPackage();

		try {
			operation.subscribeBod(bodPackage, createSprInfo(subscriberId), null, 0, null, null, transactionFactory, 100, "param1", "param2", null, 0d, null, null);
		} catch (OperationFailedException e) {
			assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
			throw e;
		}

		fail("should throw OperationFailedException");
	}

	@SuppressWarnings("unused")
	private Object dataProviderFor_startTime_endTime() {

		Long time = System.currentTimeMillis() +  TimeUnit.MINUTES.toMillis(10); /// future time

		return new Object[][] {
				{
						time, time
				},
				{
						time + TimeUnit.MINUTES.toMillis(1), time
				}
		};
	}

	@Test
	@Parameters(method = "dataProviderFor_startTime_endTime")
	public void throwsOperationFailedExceptionWhenStartTimeIsMoreOrEqualToEndtime(
			Long startTime, Long endTime) throws Exception {
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();

		expectedException.expect(OperationFailedException.class);
		expectedException.expectMessage("Start time(" + new Timestamp(startTime).toString() + ") is more or equal to end time("
					+ new Timestamp(endTime).toString() + ")");

		final Integer state = null;
		BoDPackage bodPackage = createBoDPackage();

		try {
			operation.subscribeBod(bodPackage, createSprInfo("101"), null, state, startTime, endTime, transactionFactory, 100, "param1", "param2", null, 0d, null, null);
		} catch (OperationFailedException e) {
			assertSame(ResultCode.INVALID_INPUT_PARAMETER, e.getErrorCode());
			getLogger().debug("TEST", e.getMessage());
			throw e;
		}

		fail("should throw OperationFailedException");
	}

	private SubscriptionOperationExt getNewSubscriptionOperation() {
		return new SubscriptionOperationExt(mock(AlertListener.class), policyRepository);
	}

	/*
	 * Valid Subscription states are :
	 * 	STARTED(2)
	 *
	 * Other than these state for subscribeBodByName() will throw OperationFailedException
	 */

	@Test
	@Parameters(value = {"1", "3", "4", "5", "6", "7", "8", "9"})
	public void throwsOperationFailedExceptionWhenInvalidSubscriptionStateProvided(
			Integer subscriptionStatusValue) throws Exception {
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();
		final Long startTime = null;
		final Long endTime = null;
		BoDPackage boDPackage = createBoDPackage();

		expectedException.expect(OperationFailedException.class);
		if (subscriptionStatusValue == 9) {
			expectedException.expectMessage("Invalid subscription status value: " + subscriptionStatusValue + " received");
		} else {
			expectedException.expectMessage("Invalid subscription status: " + SubscriptionState.fromValue(subscriptionStatusValue).name + " received");
		}
		try {
			operation.subscribeBod(boDPackage, createSprInfo("101"), null, subscriptionStatusValue, startTime, endTime, transactionFactory, 100, "param1", "param2", null, 0d, null, null);
		} catch (OperationFailedException e) {
			assertSame(ResultCode.INVALID_INPUT_PARAMETER, e.getErrorCode());
			throw e;
		}

		fail("should throw OperationFailedException");
	}

	@Test
	public void statusIsSTARTEDWhenSubscriptionStateIsNotProvided() throws Exception {
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();

		final Long startTime = null;
		final Long endTime = null;
		BoDPackage boDPackage = createBoDPackage();

		String subscriberIdentity = "501";
		Integer STATUS_NOT_PROVIDED = null;

		operation.subscribeBod(boDPackage, createSprInfo(subscriberIdentity), null, STATUS_NOT_PROVIDED, startTime, endTime, transactionFactory, 100, "param1", "param2", null, 0d, null, null);

		Map<String, Subscription> bodSubscriptions = operation.getBodSubscriptions(subscriberIdentity, transactionFactory);

		if (Maps.isNullOrEmpty(bodSubscriptions)) {
			fail("subscription should found from DB");
		}

		Subscription actualSubscription = bodSubscriptions.values().iterator().next();
		assertSame(SubscriptionState.STARTED, actualSubscription.getStatus());
	}

	@Test
	public void throwsOperationFailedExceptionWhenEndtimeIsPastTime() throws Exception {
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();
		final Long startTime = null;
		final Long endTime = System.currentTimeMillis() - 10;
		final Integer state = null;

		expectedException.expect(OperationFailedException.class);

		BoDPackage bodPackage = createBoDPackage();
		when(bodPackage.getValidityPeriodUnit()).thenReturn(null);

		try {
			operation.subscribeBod(bodPackage, createSprInfo("101"), null, state, startTime, endTime, transactionFactory, 10, "param1", "param2", null, 0d, null, null);
		} catch (OperationFailedException e) {
			assertSame(ResultCode.INVALID_INPUT_PARAMETER, e.getErrorCode());
			getLogger().debug("TEST", e.getMessage());
			throw e;
		}

		fail("should throw OperationFailedException");
	}

	@Test
	public void addSubscriptionWhenValidBoDPackageIsConfigured() throws Exception {
		SubscriptionOperationExt operation = getNewSubscriptionOperation();
		String subscriberId = "501";
		long currentTime = System.currentTimeMillis();
		final long startTime = currentTime;
		final long endTime = currentTime + TimeUnit.DAYS.toMillis(1);
		final int status = 2;
		BoDPackage bodPackage = createBoDPackage();
		operation.subscribeBod(bodPackage, createSprInfo(subscriberId), null, status, startTime, endTime, transactionFactory, 100, "param1", "param2", null, 0d, null, null);
		Map<String, Subscription> bodSubscriptions = operation.getBodSubscriptions(subscriberId, transactionFactory);

		if (Maps.isNullOrEmpty(bodSubscriptions)) {
			fail("subscription should found from DB");
		}

		if (bodSubscriptions.size() > 1) {
			fail("only one subscription should exist");
		}

		Subscription expectedBODSubscription = new Subscription(operation.getLastSubscriptionID(), subscriberId, bodPackage.getId(),null,
				new Timestamp(startTime), new Timestamp(endTime), SubscriptionState.fromValue(status), CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.BOD,  "param1", "param2");

		Subscription actualSubscription = bodSubscriptions.values().iterator().next();

		assertReflectionEquals(expectedBODSubscription, actualSubscription, ReflectionComparatorMode.LENIENT_ORDER);
	}


	@Test
	public void generateDB_HIGH_QUERY_RESPONSE_TIME_AlertWhenExecutionTimeIsHigh() throws Exception {
		TransactionFactory transactionFactory = setUpMockToGenerate_HIGHRESPONSE_AlertForSubscriberByBoDName();
		AlertListener alertListener = mock(AlertListener.class);
		SubscriptionOperationImpl operation = new SubscriptionOperationExt(alertListener, policyRepository);

		String subscriberId = "501";
		BoDPackage bodPackage = createBoDPackage();
		SubscriptionData data = new SubscriptionData(subscriberId, null, null, null, "2",null, "1", null,bodPackage.getId());

		operation.subscribeBod(bodPackage, createSprInfo(subscriberId), null, Integer.valueOf(data.getStatus()), null, null, transactionFactory, 10, "param1", "param2", null, 0d, null, null);
		verify(alertListener, only())
				.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
	}

	private TransactionFactory setUpMockToGenerate_HIGHRESPONSE_AlertForSubscriberByBoDName() throws Exception {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

		when(preparedStatement.execute()).thenAnswer(new Answer<Boolean>() {

			/// paused call to generate high response time alert
			public Boolean answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {
				long startTime = System.currentTimeMillis();
				long elapsedTime = 0;
				while (elapsedTime < AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10) {
					elapsedTime = System.currentTimeMillis() - startTime;
				}
				return true;
			};

		});
		return transactionFactory;
	}

	@Test
	public void generateQUERYTIMEOUTAlertWhenTimeOutOccurs() throws Exception {
		AlertListener alertListener = mock(AlertListener.class);
		SubscriptionOperationImpl operation = new SubscriptionOperationExt(alertListener, policyRepository);

		expectedException.expect(OperationFailedException.class);

		TransactionFactory transactionFactory = setUpMockToGenerate_QUERYTIMEOUT_AlertForSubscribeBoD();
		String subscriberId = "501";
		BoDPackage bodPackage = createBoDPackage();
		SubscriptionData data = new SubscriptionData(subscriberId, bodPackage.getId(), null, null, "2",null, "1", null,null);


		try {
			operation.subscribeBod(bodPackage, createSprInfo(subscriberId), null, Integer.valueOf(data.getStatus()), null, null, transactionFactory, 10, "param1", "param2", null, 0d, null, null);
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString());
			throw e;
		}

		fail("should throw OperationFailedException");
	}

	private TransactionFactory setUpMockToGenerate_QUERYTIMEOUT_AlertForSubscribeBoD() throws Exception {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

		when(preparedStatement.execute()).thenThrow(new SQLException("query timeout", "timeout", CommonConstants.QUERY_TIMEOUT_ERRORCODE));
		return transactionFactory;
	}


	 /*
	 * After all operation, transaction should end properly
	 *
	 * Actually its closing connection, so verified connection.close() call
	 */

	@Test
	public void alwaysEndTransactionAfterSubscribeBoD() throws Exception {

		SubscriptionOperationImpl operation = getNewSubscriptionOperation();

		String subscriberId = "501";


		BoDPackage boDPackage = createBoDPackage();

		operation.subscribeBod(boDPackage, createSprInfo(subscriberId), null, null, null, null, transactionFactory, 10, "param1", "param2", null, 0d, null, null);
		Connection connection = getConnection();

		assertTrue(connection.isClosed());
	}


	private Connection getConnection() {
		Connection connection = transactionFactory.getConnection();
		return connection;
	}

	private BoDPackage createBoDPackage() {
		BoDData bodData = new BoDDataFactory()
				.withId("100").withName("BoD").withStatus(PkgStatus.ACTIVE.name())
				.withMode("LIVE").withValidityPeriod("30").withValidityPeriodUnit("DAY").build();

		BoDPackage bodPackage = spy(BoDDataFactory.createBoDPkg(bodData, PolicyStatus.SUCCESS));
		policyRepository.addBoDPkg(bodPackage);
		return bodPackage;
	}

	private SPRInfo createSprInfo(String subscriberIdentity) {
		SPRInfoImpl sprInfo = new SPRInfoImpl();
		sprInfo.setSubscriberIdentity(subscriberIdentity);
		return sprInfo;
	}
}
