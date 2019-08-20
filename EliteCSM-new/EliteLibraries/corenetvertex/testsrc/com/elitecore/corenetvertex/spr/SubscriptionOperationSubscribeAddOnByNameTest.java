package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.DummyPolicyRepository;
import com.elitecore.corenetvertex.pm.factory.AddOnPackageFactory;
import com.elitecore.corenetvertex.pm.factory.ProductOfferDataFactory;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.spr.SubscriptionOperationTestSuite.SubscriptionDBHelper;
import com.elitecore.corenetvertex.spr.SubscriptionOperationTestSuite.SubscriptionOperationExt;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionData;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionParameter;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@RunWith(JUnitParamsRunner.class)
public class SubscriptionOperationSubscribeAddOnByNameTest {


	private DummyPolicyRepository policyRepository;
	private DummyTransactionFactory transactionFactory;
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private SubscriptionDBHelper helper;
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

		helper = new SubscriptionDBHelper(transactionFactory);
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
	public void test_should_throw_OperationFailedException_when_DB_is_down() throws Exception {
		String subscriberId = "101";
		String parentId = "102";

		SubscriptionOperationImpl operation = new SubscriptionOperationImpl(mock(AlertListener.class), policyRepository, null,null, null, null, null);
		setUpMockTransactionFactoryDead();

		expectedException.expect(OperationFailedException.class);
		expectedException.expectMessage("Datasource not available"); // part of message
		ProductOffer productOffer = createProductOffer();

		try {
			operation .subscribeProductOfferByName(new SubscriptionParameter(createSprInfo(subscriberId), parentId, null,SubscriptionState.STARTED.state, productOffer.getName(),  null, null, null, null, null, "param1", "param2", null,null), transactionFactory);
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
	public void test_should_throw_OperationFailedException_when_starttime_is_more_or_equal_endtime(
			Long startTime, Long endTime) throws Exception {
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();

		expectedException.expect(OperationFailedException.class);
		expectedException.expectMessage("Start time(" + new Timestamp(startTime).toString() + ") is more or equal to end time("
					+ new Timestamp(endTime).toString() + ")");
		
		final Integer state = null;
		ProductOffer productOffer = createProductOffer();

		try {
			operation.subscribeProductOfferByName(new SubscriptionParameter(createSprInfo("101"), "102", null,state, productOffer.getName(),  null, null, startTime, endTime, null, "param1", "param2", null,null), transactionFactory);
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
	 * Other than these state for subscribeAddOnName() will throw OperationFailedException
	 */

	@Test
	@Parameters(value = {"1", "3", "4", "5", "6", "7", "8", "9"})
	public void test_should_throw_OperationFailedException_when_invalid_subscriptionState_provided(
			Integer subscriptionStatusValue) throws Exception {
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();
		final Long startTime = null;
		final Long endTime = null;
		ProductOffer productOffer = createProductOffer();

		expectedException.expect(OperationFailedException.class);
		if (subscriptionStatusValue == 9) {
			expectedException.expectMessage("Invalid subscription status value: " + subscriptionStatusValue + " received");
		} else {
			expectedException.expectMessage("Invalid subscription status: " + SubscriptionState.fromValue(subscriptionStatusValue).name + " received");
		}
		try {
			operation.subscribeProductOfferByName(new SubscriptionParameter(createSprInfo("101"), "102", null,subscriptionStatusValue, productOffer.getName(),  null, null, startTime, endTime, null, "param1", "param2", null,null), transactionFactory);
		} catch (OperationFailedException e) {
			assertSame(ResultCode.INVALID_INPUT_PARAMETER, e.getErrorCode());
			throw e;
		}

		fail("should throw OperationFailedException");
	}
	
	@Test
	public void test_should_status_STARTED_when_subscription_state_not_provided() throws Exception {
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();

		final Long startTime = null;
		final Long endTime = null;
		ProductOffer productOffer = createProductOffer();

		String subscriberIdentity = "501";
		Integer STATUS_NOT_PROVIDED = null;
		
		operation.subscribeProductOfferByName(new SubscriptionParameter(createSprInfo(subscriberIdentity), "102", null,STATUS_NOT_PROVIDED, productOffer.getName(),  null, null, startTime, endTime, null, "param1", "param2", null,null), transactionFactory);
		
		Map<String, Subscription> addonSubscriptions = operation.getSubscriptions(subscriberIdentity, transactionFactory);
		
		if (Maps.isNullOrEmpty(addonSubscriptions)) {
			fail("subscription should found from DB");
		}

		Subscription actualSubscription = addonSubscriptions.values().iterator().next();
		assertSame(SubscriptionState.STARTED, actualSubscription.getStatus());
	}
	
	@Test
	public void test_should_throw_OperationFailedException_when_product_offer_not_found_for_provided_productOfferaddonName() throws Exception {
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();
		final String addonName = "addonName";
		final Long startTime = null;
		final Long endTime = null;
		final Integer state = null;
		
		expectedException.expectMessage("Product Offer not found for Name: " + addonName);
		expectedException.expect(OperationFailedException.class);
		
		try {
			operation.subscribeProductOfferByName(new SubscriptionParameter(createSprInfo("101"), "102", null,state, addonName,  null, null, startTime, endTime, null, "param1", "param2", null,null), transactionFactory);
		} catch (OperationFailedException e) {
			assertSame(ResultCode.NOT_FOUND, e.getErrorCode());
			throw e;
		}

		fail("should throw OperationFailedException");
	}
	
	@Test
	public void test_should_throw_OperationFailedException_when_endtime_is_past_time() throws Exception {
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();
		final Long startTime = null;
		final Long endTime = System.currentTimeMillis() - 10;
		final Integer state = null;
		
		expectedException.expect(OperationFailedException.class);
		
		ProductOffer productOffer = createProductOffer();
		when(productOffer.getValidityPeriodUnit()).thenReturn(null);
		
		try {
			operation.subscribeProductOfferByName(new SubscriptionParameter(createSprInfo("101"), "102", null,state, productOffer.getName(),  null, null, startTime, endTime, null, "param1", "param2", null,null), transactionFactory);
		} catch (OperationFailedException e) {
			assertSame(ResultCode.INVALID_INPUT_PARAMETER, e.getErrorCode());
			getLogger().debug("TEST", e.getMessage());
			throw e;
		}

		fail("should throw OperationFailedException");
	}
	
	@Test
	public void test_should_add_subscription_provided() throws Exception {
		SubscriptionOperationExt operation = getNewSubscriptionOperation();
		String subscriberId = "501";
		long currentTime = System.currentTimeMillis();
		final long startTime = currentTime;
		final long endTime = currentTime + TimeUnit.DAYS.toMillis(1);
		final int status = 2;
		ProductOffer productOffer = createProductOffer();
		operation.subscribeProductOfferByName(
                new SubscriptionParameter(createSprInfo(subscriberId), subscriberId, null,status, productOffer.getName(),  null, null, startTime, endTime, null, null, null, null,null), transactionFactory);
		
		Map<String, Subscription> addonSubscriptions = operation.getSubscriptions(subscriberId, transactionFactory);

		if (Maps.isNullOrEmpty(addonSubscriptions)) {
			fail("subscription should found from DB");
		}
		
		if (addonSubscriptions.size() > 1) {
			fail("only one subscription should exist");
		}
		
		Subscription expectedAddonSubscription = new Subscription(operation.getLastSubscriptionID(), subscriberId, productOffer.getDataServicePkgData().getId(),productOffer.getId(),
				new Timestamp(startTime), new Timestamp(endTime), SubscriptionState.fromValue(status), CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON,  null, null);

		Subscription actualSubscription = addonSubscriptions.values().iterator().next();
		
		assertReflectionEquals(expectedAddonSubscription, actualSubscription, ReflectionComparatorMode.LENIENT_ORDER);
	}

	/*@Test
	public void test_should_throw_exception_when_multiple_subscription_is_not_allowed_and_existing_subscription_found() throws Exception {
		String subscriberId = "501";
		String STARTED_STATUS = "2";
		AddOn addOn = createProductOffer();
		doReturn(false).when(addOn).isMultipleSubscription();
		
		SubscriptionData data = new SubscriptionData(subscriberId, addOn.getId(), null, null, STARTED_STATUS, null, "1", null);

		SubscriptionOperationImpl operation = getNewSubscriptionOperation();
		
				
		expectedException.expect(OperationFailedException.class);
		expectedException.expectMessage("already exist for subscriber ID(" + subscriberId 
						+ "), package(" + addOn.getName() + ")");
		
		operation.subscribeProductOfferByName(data.getSubscriberId(), data.getSubscriberId(), addOn.getName(),
				Integer.valueOf(data.getStatus()), null, null,
				data.getParam1(), data.getParam2(), transactionFactory, null);
		
		operation.subscribeProductOfferByName(data.getSubscriberId(), data.getSubscriberId(), addOn.getName(),
				Integer.valueOf(data.getStatus()), null, null,
				data.getParam1(), data.getParam2(), transactionFactory, null);
		
	}*/
	
	public Object[][] dataProviderFor_test_should_subscribe_when_multiple_subscription_is_not_allowed_and_existing_subscription_found_with_REJECTED_and_UNSUBSCRIBED() {
		return new Object[][] {
				{
					SubscriptionState.REJECTED.state
				},
				{
					SubscriptionState.UNSUBSCRIBED.state
				}
		};
	}
	
	
	/*@Test
	@Parameters(method="dataProviderFor_test_should_subscribe_when_multiple_subscription_is_not_allowed_and_existing_subscription_found_with_REJECTED_and_UNSUBSCRIBED")
	public void test_should_subscribe_when_multiple_subscription_is_not_allowed_and_existing_subscription_found_with_REJECTED_and_UNSUBSCRIBED(
			int status) throws Exception {
		final String subscriberId = "501";

		AddOn addOn = createProductOffer();
		doReturn(false).when(addOn).isMultipleSubscription();
		
		SubscriptionData oldSubscription = new SubscriptionData(subscriberId, addOn.getId(), null, null, String.valueOf(status), "1", "1", null);
		oldSubscription.setAddOn(addOn);
		helper.insertAddonRecord(oldSubscription);
		
		SubscriptionData newAddonSubscription = new SubscriptionData(subscriberId, addOn.getId(), null, null, "2",null, "1", null);
		newAddonSubscription.setAddOn(addOn);
		
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();
		
				
				
		operation.subscribeProductOfferByName(newAddonSubscription.getSubscriberId(), newAddonSubscription.getSubscriberId(), addOn.getName(),
				Integer.valueOf(newAddonSubscription.getStatus()), null, null,
				newAddonSubscription.getParam1(), newAddonSubscription.getParam2(), transactionFactory, null);
		
		Map<String, Subscription> addonSubscriptions = operation.getSubscriptions(subscriberId, transactionFactory);
		
		if (Maps.isNullOrEmpty(addonSubscriptions)) {
			fail("subscription should found from DB");
		}

		Subscription actualSubscription = addonSubscriptions.values().iterator().next();
		assertEquals(newAddonSubscription.getAddonId(), actualSubscription.getPackageId());
		assertEquals(newAddonSubscription.getSubscriberId(), actualSubscription.getSubscriberIdentity());
	}*/
	
	/*@Test
	public void test_should_subscribe_when_multiple_subscription_is_not_allowed_and_existing_subscription_expired_subscription_found() throws Exception {
		final String subscriberId = "501";

		AddOn addOn = createProductOffer();
		doReturn(false).when(addOn).isMultipleSubscription();

		final Timestamp endDate = new Timestamp(System.currentTimeMillis()-TimeUnit.DAYS.toMillis(1));
		
		SubscriptionData oldSubscription = new SubscriptionData(subscriberId, addOn.getId(), null, endDate.toString(), String.valueOf(SubscriptionState.STARTED.state), "1", "1", null);
		oldSubscription.setAddOn(addOn);
		helper.insertAddonRecord(oldSubscription);
		
		SubscriptionData newAddonSubscription = new SubscriptionData(subscriberId, addOn.getId(), null, null, "2",null, "1", null);
		newAddonSubscription.setAddOn(addOn);
		
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();
		
				
				
		operation.subscribeProductOfferByName(newAddonSubscription.getSubscriberId(), newAddonSubscription.getSubscriberId(), addOn.getName(),
				Integer.valueOf(newAddonSubscription.getStatus()), null, null,
				newAddonSubscription.getParam1(), newAddonSubscription.getParam2(), transactionFactory, null);
		
		Map<String, Subscription> addonSubscriptions = operation.getSubscriptions(subscriberId, transactionFactory);
		
		if (Maps.isNullOrEmpty(addonSubscriptions)) {
			fail("subscription should found from DB");
		}

		Subscription actualSubscription = addonSubscriptions.values().iterator().next();
		assertEquals(newAddonSubscription.getAddonId(), actualSubscription.getPackageId());
		assertEquals(newAddonSubscription.getSubscriberId(), actualSubscription.getSubscriberIdentity());
	}*/

	
	/*@Test
	public void test_should_subscribe_when_multiple_subscription_is_not_allowed_and_existing_subscription_found_not_found() throws Exception {
		final String subscriberId = "501";
		AddOn addOn = createProductOffer();
		doReturn(false).when(addOn).isMultipleSubscription();
		
		SubscriptionData newAddonSubscription = new SubscriptionData(subscriberId, addOn.getId(), null, null, "2",null, "1", null);
		
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();
		
				

		operation.subscribeProductOfferByName(newAddonSubscription.getSubscriberId(), newAddonSubscription.getSubscriberId(), addOn.getName(),
				Integer.valueOf(newAddonSubscription.getStatus()), null, null,
				newAddonSubscription.getParam1(), newAddonSubscription.getParam2(), transactionFactory, null);
		
		Map<String, Subscription> addonSubscriptions = operation.getSubscriptions(subscriberId, transactionFactory);
		
		if (Maps.isNullOrEmpty(addonSubscriptions)) {
			fail("subscription should found from DB");
		}

		Subscription actualSubscription = addonSubscriptions.values().iterator().next();
		assertEquals(newAddonSubscription.getAddonId(), actualSubscription.getPackageId());
		assertEquals(newAddonSubscription.getSubscriberId(), actualSubscription.getSubscriberIdentity());
	}*/
	
	/*@Test
	public void test_should_generate_DB_NO_CONNECTION_alert_when_connection_not_found() throws Exception {
		AlertListener alertListener = mock(AlertListener.class);
		TransactionFactory transactionFactory = setUpMockTogenerateDB_NO_CONNECTION_alertForSubscribeByAddOnId();
		String subscriberId = "501";
		AddOn addOn = createProductOffer();
		doReturn(true).when(addOn).isMultipleSubscription();
		
		SubscriptionData data = new SubscriptionData(subscriberId, addOn.getId(), null, null, "2",null, "1", null);
		
		SubscriptionOperationImpl operation = new SubscriptionOperationImpl(alertListener, policyRepository, null,null);
		
				
				
		expectedException.expect(OperationFailedException.class);
		
		try {
			operation.subscribeProductOfferByName(data.getSubscriberId(), data.getSubscriberId(), addOn.getName(),
					Integer.valueOf(data.getStatus()), null, null,
					data.getParam1(), data.getParam2(), transactionFactory, null);
			
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), Mockito.anyString(), Mockito.anyString());
			throw e;
		}

		fail("getSubscriptions should throw Exception");
	}*/
	
	private TransactionFactory setUpMockTogenerateDB_NO_CONNECTION_alertForSubscribeByAddOnId() throws TransactionException {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doThrow(new TransactionException("Connection not found", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).begin();
		return transactionFactory;
	}
	
	@Test
	public void test_should_generate_DB_HIGH_QUERY_RESPONSE_TIME_alert_when_execution_time_is_high() throws Exception {
		TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlertForSubscriberByAddOnName();
		AlertListener alertListener = mock(AlertListener.class);
		SubscriptionOperationImpl operation = new SubscriptionOperationExt(alertListener, policyRepository);

		String subscriberId = "501";
		ProductOffer productOffer = createProductOffer();
		SubscriptionData data = new SubscriptionData(subscriberId, productOffer.getDataServicePkgData().getId(), null, null, "2",null, "1", null,productOffer.getId());
		


		operation.subscribeProductOfferByName(
                new SubscriptionParameter(createSprInfo(data.getSubscriberId()), data.getSubscriberId(), null,Integer.valueOf(data.getStatus()), productOffer.getName(),  null, null, null, null, null, data.getParam1(), data.getParam2(), null,null), transactionFactory);
		
		verify(alertListener, only())
				.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
	}

	private SPRInfo createSprInfo(String subscriberIdentity){
		SPRInfoImpl sprInfo = new SPRInfoImpl();
		sprInfo.setSubscriberIdentity(subscriberIdentity);
		return sprInfo;
	}
	
	private TransactionFactory setUpMockToGenerateHIGHRESPONSEAlertForSubscriberByAddOnName() throws Exception {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

		when(preparedStatement.executeUpdate()).thenAnswer(new Answer<Integer>() {

			/// paused call to generate high response time alert
			public Integer answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {
				long startTime = System.currentTimeMillis();
				long elapsedTime = 0;
				while (elapsedTime < AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10) {
					elapsedTime = System.currentTimeMillis() - startTime;
				}
				return 1;
			};

		});
		return transactionFactory;
	}
	
	private TransactionFactory setUpMockToGenerateHIGHRESPONSEAlertForExistingSubscriptionCheck() throws Exception {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);
		final ResultSet resultSet = mock(ResultSet.class);
		
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());
		doReturn(false).when(resultSet).next();
		
		when(preparedStatement.executeQuery()).thenAnswer(new Answer<ResultSet>() {

			/// paused call to generate high response time alert
			public ResultSet answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {
				long startTime = System.currentTimeMillis();
				long elapsedTime = 0;
				while (elapsedTime < AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10) {
					elapsedTime = System.currentTimeMillis() - startTime;
				}
				return resultSet;
			};

		});
		
		
		return transactionFactory;
	}
	
	@Test
	public void test_should_generate_QUERYTIMEOUT_alert() throws Exception {
		AlertListener alertListener = mock(AlertListener.class);
		SubscriptionOperationImpl operation = new SubscriptionOperationExt(alertListener, policyRepository);

		expectedException.expect(OperationFailedException.class);
		
		TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTalertForSubscribeAddon();
		String subscriberId = "501";
		ProductOffer productOffer = createProductOffer();
		SubscriptionData data = new SubscriptionData(subscriberId, productOffer.getDataServicePkgData().getId(), null, null, "2",null, "1", null,productOffer.getId());
		
		
		try {
			operation.subscribeProductOfferByName(
                    new SubscriptionParameter(createSprInfo(data.getSubscriberId()), data.getSubscriberId(), null,Integer.valueOf(data.getStatus()), productOffer.getName(),  null, null, null, null, null, data.getParam1(), data.getParam2(), null,null), transactionFactory);
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString());
			throw e;
		}
		
		fail("should throw OperationFailedException");
	}
	
	private TransactionFactory setUpMockToGenerateQUERYTIMEOUTalertForSubscribeAddon() throws Exception {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

		when(preparedStatement.executeUpdate()).thenThrow(new SQLException("query timeout", "timeout", CommonConstants.QUERY_TIMEOUT_ERRORCODE));
		return transactionFactory;
	}
	
	/*@Test
	public void test_should_generate_DB_HIGH_QUERY_RESPONSE_TIME_alert_when_execution_time_is_high_for_checking_existing_subscription() throws Exception {
		TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlertForExistingSubscriptionCheck();
		AlertListener alertListener = mock(AlertListener.class);
		SubscriptionOperationImpl operation = new SubscriptionOperationExt(alertListener, policyRepository);

		String subscriberId = "501";
		AddOn addOn = createProductOffer();
		
		doReturn(false).when(addOn).isMultipleSubscription();
		
		SubscriptionData data = new SubscriptionData(subscriberId, addOn.getId(), null, null, "2",null, "1", null);
		

		
		operation.subscribeProductOfferByName(data.getSubscriberId(), data.getSubscriberId(), addOn.getName(),
				Integer.valueOf(data.getStatus()), null, null,
				data.getParam1(), data.getParam2(), transactionFactory, null);
		
		verify(alertListener, only())
				.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
	}*/
	
	 /*
	 * After all operation, transaction should end properly
	 * 
	 * Actually its closing connection, so verified connection.close() call
	 */

	@Test
	public void test_should_always_end_transaction() throws Exception {

		SubscriptionOperationImpl operation = getNewSubscriptionOperation();

		String subscriberId = "501";
		
		
		ProductOffer productOffer = createProductOffer();

		operation.subscribeProductOfferByName(
                new SubscriptionParameter(createSprInfo(subscriberId), "102", null,null, productOffer.getName(),  null, null, null, null, null, null, null, null,null), transactionFactory);

		Connection connection = getConnection();

		assertTrue(connection.isClosed());
	}
	

	private Connection getConnection() {
		Connection connection = ((DummyTransactionFactory) transactionFactory).getConnection();
		return connection;
	}

	private ProductOffer createProductOffer() {

		AddOn addOn = spy(AddOnPackageFactory.create(UUID.randomUUID().toString(), "name-" + UUID.randomUUID().toString()));
		policyRepository.addAddOn(addOn);
		ProductOffer productOffer = getProductOffer(addOn);
		policyRepository.addProductOffer(productOffer);
		return productOffer;
	}

	private ProductOffer getProductOffer(AddOn addOn) {
		ProductOfferData productOfferData =  new ProductOfferDataFactory()
				.withId("1").withName("name").withStatus("Random")
				.withMode("LIVE").withType("ADDON")
				.withDataServicePkgId(addOn.getId()).build();
		return new ProductOffer(
				productOfferData.getId(), productOfferData.getName(), productOfferData.getDescription(),
				PkgType.ADDON, PkgMode.LIVE, 30, ValidityPeriodUnit.DAY, productOfferData.getSubscriptionPrice() != null ? productOfferData.getSubscriptionPrice() : 0d,
                productOfferData.getCreditBalance() != null ? productOfferData.getCreditBalance() : 0d,
				PkgStatus.ACTIVE, null, null,
				productOfferData.getDataServicePkgId(), productOfferData.getGroupList(), productOfferData.getAvailabilityStartDate(),
				productOfferData.getAvailabilityEndDate(), PolicyStatus.SUCCESS,  null, null,
				(Objects.nonNull(productOfferData.isFnFOffer()) && productOfferData.isFnFOffer().booleanValue()),
				productOfferData.getParam1(), productOfferData.getParam2(), policyRepository,
				null,null,new HashMap<>(),productOfferData.getCurrency()
		);
	}
}
