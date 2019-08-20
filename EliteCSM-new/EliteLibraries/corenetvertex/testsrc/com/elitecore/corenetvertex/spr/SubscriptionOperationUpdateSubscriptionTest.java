package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.pm.DummyPolicyRepository;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.factory.AddOnPackageFactory;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.pm.store.AddOnProductOfferStore;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionData;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.DerbyUtil;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class SubscriptionOperationUpdateSubscriptionTest {


	private DummyPolicyRepository policyRepository;
	private DummyTransactionFactory transactionFactory;
	@Mock ProductOfferStore productOfferStore;
	@Mock AddOnProductOfferStore addOnProductOfferStore;
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private SubscriptionOperationTestSuite.SubscriptionDBHelper helper;

	private String dataSourceId = UUID.randomUUID().toString();

	@BeforeClass
	public static void setUpClass() throws Exception {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	}

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);
		policyRepository = spy(new DummyPolicyRepository());
        String url = "jdbc:derby:memory:"+ dataSourceId +";create=true";
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", SubscriptionOperationTestSuite.DS_NAME, url, "", "", 1, 5000, 3000);
		transactionFactory = (DummyTransactionFactory) new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1).build();

		helper = new SubscriptionOperationTestSuite.SubscriptionDBHelper(transactionFactory);
		helper.createTables();

		ProductOffer productOffer = helper.getProductOffer();
		when(policyRepository.getProductOffer()).thenReturn(productOfferStore);
		when(productOfferStore.addOn()).thenReturn(addOnProductOfferStore);
		when(addOnProductOfferStore.byId(productOffer.getId())).thenReturn(productOffer);
	}
	
	@org.junit.After
	public void afterDropTables() throws Exception {
		helper.dropTables();
		transactionFactory.getConnection().close();
		DerbyUtil.closeDerby(dataSourceId);
	}

	private void setUpMockTransactionFactoryDead() {
		transactionFactory = spy(transactionFactory);
		when(transactionFactory.isAlive()).thenReturn(false);
	}
	
	@Test
	public void test_should_throw_OperationFailedException_when_DB_is_down() throws Exception {
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();

		setUpMockTransactionFactoryDead();

		expectedException.expect(OperationFailedException.class);
		expectedException.expectMessage("Datasource not available"); // part of message
	
		try {
			String subscriptionId = "101";
			Integer subscriptionStatusValue = SubscriptionState.UNSUBSCRIBED.state;
			operation.updateSubscription("Subscriber", subscriptionId , subscriptionStatusValue , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null, transactionFactory);
		} catch (OperationFailedException e) {
			assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
			throw e;
		}

		fail("should throw OperationFailedException");
	}
	
	private SubscriptionOperationTestSuite.SubscriptionOperationExt getNewSubscriptionOperation() {
		return new SubscriptionOperationTestSuite.SubscriptionOperationExt(mock(AlertListener.class), policyRepository);
	}
	
	/*
	 * Subscription state not provided in update, it will throw OperationFailedException
	 */
	@Test
	public void test_should_throw_OperationFailedException_when_subscriptionState_not_provided() throws Exception {
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();

		expectedException.expect(OperationFailedException.class);
		expectedException.expectMessage("Subscription status value not found");
		try {
			String subscriptionId = "101";
			Integer subscriptionStatusValue = null;
			operation.updateSubscription("Subscriber", subscriptionId , subscriptionStatusValue , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null,  transactionFactory);
		} catch (OperationFailedException e) {
			assertSame(ResultCode.INVALID_INPUT_PARAMETER, e.getErrorCode());
			throw e;
		}

		fail("should throw OperationFailedException");
	}
	
	/*
	 * Valid Subscription states are : 
	 * 	0, 1, 2, 3, 4, 5, 6, 7, 8
	 * 
	 * @see SubscriptionState
*/
	@Test
	@Parameters(value = {"-1", "9"})
	public void test_should_throw_OperationFailedException_when_invalid_subscriptionState_provided(
			Integer subscriptionStatusValue) throws Exception {
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();

		expectedException.expect(OperationFailedException.class);
		expectedException.expectMessage("Invalid subscription status value: " + subscriptionStatusValue + " received");
		try {
			String subscriptionId = "101";
			operation.updateSubscription("Subscriber", subscriptionId , subscriptionStatusValue , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null, transactionFactory);
		} catch (OperationFailedException e) {
			assertSame(ResultCode.INVALID_INPUT_PARAMETER, e.getErrorCode());
			throw e;
		}

		fail("should throw OperationFailedException");
	}
	
	@Test
	public void test_when_subscription_not_found_with_provided_subscriptionId_then_it_should_throw_OperationFailedException() throws Exception {
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();
		String subscriptionId = "101";
		
		expectedException.expect(OperationFailedException.class);
		expectedException.expectMessage("Active subscription not found with ID: " + subscriptionId);
		
		try {
			Integer subscriptionStatusValue = SubscriptionState.UNSUBSCRIBED.state;
			operation.updateSubscription("Subscriber", subscriptionId , subscriptionStatusValue , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null, transactionFactory);
		} catch (OperationFailedException e) {
			assertSame(ResultCode.NOT_FOUND, e.getErrorCode());
			throw e;
		}
		
		fail("should throw OperationFailedException");
	}
	
	@Test
	@Parameters(value={"0","1","3","4","6","7","8"})
	public void test_when_existing_subscription_have_not_STARTED_status_then_it_should_throw_OperationFailedException(
			Integer state
			) throws Exception {
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();
		String subscriptionId = "101";
		String subscriber = "Subscriber";
		Timestamp endTime = new Timestamp(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
		Integer newState = SubscriptionState.UNSUBSCRIBED.state;
		
		AddOn addOn = createAndGetAddonWithID(UUID.randomUUID().toString());

		SubscriptionData data1 = new SubscriptionData(subscriber, addOn.getId(), null, endTime.toString(), String.valueOf(state), null, subscriptionId, null,"100");
		//fixme product offer id

		data1.setAddOn(addOn);

		helper.insertAddonRecord(data1);

		expectedException.expect(OperationFailedException.class);
		expectedException.expectMessage("Invalid subscription status(" + SubscriptionState.fromValue(newState) + ") received. Old Status: "
						+ SubscriptionState.fromValue(state));
		
		try {
			operation.updateSubscription(subscriber, subscriptionId , newState , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null,transactionFactory);
		} catch (OperationFailedException e) {
			assertSame(ResultCode.INVALID_INPUT_PARAMETER, e.getErrorCode());
			throw e;
		}
		
		fail("should throw OperationFailedException");
	}
	
	private AddOn createAndGetAddonWithID(String addonId) {
		AddOn addOn = AddOnPackageFactory.create(addonId, "name-"+ addonId);

		policyRepository.addAddOn(addOn);
		return addOn;
	}

	@Test
	@Parameters(value={"0","1","3","4","6","7","8"})
	public void test_when_old_state_is_STARTED_but_new_state_has_not_UNSUBSCRIBED_OR_STARTED_then_it_should_throw_OperationFailedException(
			Integer newState
			) throws Exception {
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();
		String subscriptionId = "101";
		String subscriber = "Subscriber";
		Timestamp startTime = new Timestamp(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1));
		Timestamp endTime = new Timestamp(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
		Integer oldState = SubscriptionState.STARTED.state;
		
		AddOn addOn = createAndGetAddonWithID(UUID.randomUUID().toString());

		SubscriptionData data1 = new SubscriptionData(subscriber, addOn.getId(), startTime.toString(), endTime.toString(), String.valueOf(oldState), null, subscriptionId, null,"100");
		//fixme product offer id
		data1.setAddOn(addOn);
		helper.insertAddonRecord(data1);
		
		
		expectedException.expect(OperationFailedException.class);
		expectedException.expectMessage("Invalid subscription status(" + SubscriptionState.fromValue(newState) + ") received. Old Status: "
						+ SubscriptionState.fromValue(oldState));
		
		try {
			operation.updateSubscription(subscriber, subscriptionId , newState , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null,transactionFactory);
		} catch (OperationFailedException e) {
			assertSame(ResultCode.INVALID_INPUT_PARAMETER, e.getErrorCode());
			throw e;
		}
		
		fail("should throw OperationFailedException");
	}
	
	
	/*
	 *This test case is correct..
	 * issue: getAddonSubscription filters UNSUBSCRIBED subscription
	 *
	 *TODO change verification process of updated subscription 
*/
	@Test
	public void test_when_old_state_is_STARTED_and_new_state_UNSUBSCRIBED_then_it_should_update_successfully() throws Exception {
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();
		String subscriberId = "101";
		String subscriptionId = "201";
		Timestamp endTime = new Timestamp(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
		Integer oldState = SubscriptionState.STARTED.state;
		Integer newState = SubscriptionState.UNSUBSCRIBED.state;
		
		AddOn addOn = createAndGetAddonWithID(UUID.randomUUID().toString());

		SubscriptionData data1 = new SubscriptionData(subscriberId, addOn.getId(), null, endTime.toString(), String.valueOf(oldState), null, subscriptionId, null,"100");
		//fixme product offer id
		data1.setAddOn(addOn);
		helper.insertAddonRecord(data1);
		
		operation.updateSubscription(subscriberId, subscriptionId, newState , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null,  transactionFactory);
		
		SubscriptionState actualState = helper.getCurrentSubscriptionStateForSubscriptionId(subscriptionId);
		
		assertSame(SubscriptionState.UNSUBSCRIBED, actualState);
	}
	
	@Test
	public void test_when_existing_subscription_is_expired_then_it_should_throw_OperationFailedException() throws Exception {
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();
		String subscriptionId = "101";
		Timestamp endTime = new Timestamp(System.currentTimeMillis());
		Integer oldState = SubscriptionState.STARTED.state;
		Integer newState = SubscriptionState.UNSUBSCRIBED.state;
		
		AddOn addOn = createAndGetAddonWithID(UUID.randomUUID().toString());

		SubscriptionData data1 = new SubscriptionData(subscriptionId, addOn.getId(), null, endTime.toString(),
				String.valueOf(oldState), null, subscriptionId, null,null);
		//fixme product offer id
		data1.setAddOn(addOn);
		helper.insertAddonRecord(data1);
		
		expectedException.expect(OperationFailedException.class);
		expectedException.expectMessage("Active subscription not found with ID: " + subscriptionId);
		
		try {
			operation.updateSubscription("Subscriber", subscriptionId , newState , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null, transactionFactory);
		} catch (OperationFailedException e) {
			assertSame(ResultCode.NOT_FOUND, e.getErrorCode());
			throw e;
		}
		
		fail("should throw OperationFailedException");
	}
	
	@Test
	public void test_when_addon_not_found_for_existing_subscription_then_it_should_throw_OperationFailedException() throws Exception {
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();
		String subscriptionId = "101";
		Timestamp endTime = new Timestamp(System.currentTimeMillis());
		Integer oldState = SubscriptionState.STARTED.state;
		Integer newState = SubscriptionState.UNSUBSCRIBED.state;
		


		AddOn addOn = AddOnPackageFactory.create(UUID.randomUUID().toString(), "name-"+ UUID.randomUUID().toString());

		policyRepository.addAddOn(addOn);

		SubscriptionData data1 = new SubscriptionData(subscriptionId, addOn.getId(), null, endTime.toString(),
				String.valueOf(oldState), null, subscriptionId, null,null);
		//fixme product offer id
		data1.setAddOn(addOn);

		helper.insertAddonRecord(data1);
		
		expectedException.expect(OperationFailedException.class);
		expectedException.expectMessage("Active subscription not found with ID: " + subscriptionId);
		
		try {
			operation.updateSubscription("Subscriber", subscriptionId , newState , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null,  transactionFactory);
		} catch (OperationFailedException e) {
			assertSame(ResultCode.NOT_FOUND, e.getErrorCode());
			throw e;
		}
		
		fail("should throw OperationFailedException");
	}
	
	@Test
	public void test_when_existing_subscription_has_UNSUBSCRIBED_state_then_it_should_throw_OperationFailedException() throws Exception {
		SubscriptionOperationImpl operation = getNewSubscriptionOperation();
		String subscriptionId = "101";
		Integer oldState = SubscriptionState.UNSUBSCRIBED.state;
		Integer newState = SubscriptionState.UNSUBSCRIBED.state;
		
		AddOn addOn = createAndGetAddonWithID(UUID.randomUUID().toString());

		SubscriptionData data1 = new SubscriptionData(subscriptionId, addOn.getId(), null, null,
				String.valueOf(oldState), null, subscriptionId, null,null);
		//fixme product offer id
		data1.setAddOn(addOn);

		helper.insertAddonRecord(data1);
		
		expectedException.expect(OperationFailedException.class);
		expectedException.expectMessage("Active subscription not found with ID: " + subscriptionId);
		
		try {
			operation.updateSubscription("Subscriber", subscriptionId , newState , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null, transactionFactory);
		} catch (OperationFailedException e) {
			assertSame(ResultCode.NOT_FOUND, e.getErrorCode());
			throw e;
		}
		
		fail("should throw OperationFailedException");
	}
	
	@Test
	public void test_should_generate_DB_NO_CONNECTION_alert_when_connection_not_found() throws Exception {
		AlertListener alertListener = mock(AlertListener.class);
		TransactionFactory transactionFactory = setUpMockTogenerateDB_NO_CONNECTION_alert();
		String subscriptionId = "101";
		SubscriptionOperationExt1 operation = new SubscriptionOperationExt1(alertListener, policyRepository);
		
		String subscriber = "Subscriber";
		operation = mockSubscriptionOperationToSkipgetSubscriotionCall(subscriptionId, operation, subscriber);
		
		expectedException.expect(OperationFailedException.class);
		
		try {
			Integer subscriptionStatusValue = SubscriptionState.UNSUBSCRIBED.state;
			operation.updateSubscription(subscriber, subscriptionId , subscriptionStatusValue , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null, transactionFactory);
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), Mockito.anyString(), Mockito.anyString());
			throw e;
		}

		fail("getSubscriptions should throw Exception");
	}
	
	@Test
	public void test_should_generate_DB_NO_CONNECTION_alert_when_connection_not_found_while_fetching_subscription() throws Exception {
		AlertListener alertListener = mock(AlertListener.class);
		TransactionFactory transactionFactory = setUpMockTogenerateDB_NO_CONNECTION_alertWhile_fetching_subscription();
		String subscriptionId = "101";
		SubscriptionOperationImpl operation = new SubscriptionOperationTestSuite.SubscriptionOperationExt(alertListener, policyRepository);
		
		expectedException.expect(OperationFailedException.class);
		
		try {
			Integer subscriptionStatusValue = SubscriptionState.UNSUBSCRIBED.state;
			operation.updateSubscription("Subscriber", subscriptionId , subscriptionStatusValue , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null, transactionFactory);
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), Mockito.anyString(), Mockito.anyString());
			throw e;
		}

		fail("getSubscriptions should throw Exception");
	}

	private SubscriptionOperationExt1 mockSubscriptionOperationToSkipgetSubscriotionCall(String subscriptionId, SubscriptionOperationExt1 operation, String subscriberId)
			throws OperationFailedException {
		operation = spy(operation);
		
		Subscription existingSubscription = getDummySubscription(subscriptionId, subscriberId);
	
		when(operation.getActiveSubscriptionBySubscriptionId(Mockito.anyString(), Mockito.anyLong(), 
				Mockito.any(TransactionFactory.class))).thenReturn(existingSubscription);
		return operation;
	}
	
	private Subscription getDummySubscription(String subscriptionId, String subscriberIdentity) {
		return new Subscription(subscriptionId, subscriberIdentity, null,null, null, null, SubscriptionState.STARTED, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);
	}

	private TransactionFactory setUpMockTogenerateDB_NO_CONNECTION_alert() throws TransactionException {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doThrow(new TransactionException("Connection not found", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).begin();
		return transactionFactory;
	}
	
	private TransactionFactory setUpMockTogenerateDB_NO_CONNECTION_alertWhile_fetching_subscription() throws TransactionException {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doThrow(new TransactionException("Connection not found", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).begin();
		return transactionFactory;
	}
	
	@Test
	public void test_should_generate_DB_HIGH_QUERY_RESPONSE_TIME_alert_when_execution_time_is_high() throws Exception {
		TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlert();
		AlertListener alertListener = mock(AlertListener.class);
		String subscriptionId = "101";
		SubscriptionOperationExt1 operation = new SubscriptionOperationExt1(alertListener, policyRepository);
		String subscriber = "Subscriber";
		operation = mockSubscriptionOperationToSkipgetSubscriotionCall(subscriptionId, operation, subscriber);
		
		Integer subscriptionStatusValue = SubscriptionState.UNSUBSCRIBED.state;
		operation.updateSubscription("Subscriber", subscriptionId , subscriptionStatusValue , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null, transactionFactory);
		
		verify(alertListener, only())
				.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
	}
	
	private TransactionFactory setUpMockToGenerateHIGHRESPONSEAlert() throws Exception {
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
				while (elapsedTime < AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 100) {
					elapsedTime = System.currentTimeMillis() - startTime;
				}
				return 0;
			};

		});
		return transactionFactory;
	}
	
	
	 /*
	 * TODO After fetching existing subscription
	 * 
	 * @throws Exception
	*/
	@Test
	public void test_should_generate_DB_HIGH_QUERY_RESPONSE_TIME_alert_when_execution_time_is_high_while_fetching_subscription() throws Exception {
		TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlertWhile_fetching_subscription();
		AlertListener alertListener = mock(AlertListener.class);
		SubscriptionOperationImpl operation = new SubscriptionOperationTestSuite.SubscriptionOperationExt(alertListener, policyRepository);
		String subscriberId = "101";
		String subscriptionId = "201";
		Timestamp endTime = new Timestamp(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
		Integer oldState = SubscriptionState.STARTED.state;
		Integer newState = SubscriptionState.UNSUBSCRIBED.state;
		
		AddOn addOn = createAndGetAddonWithID(UUID.randomUUID().toString());

		SubscriptionData data1 = new SubscriptionData(subscriberId, addOn.getId(), null, endTime.toString(), String.valueOf(oldState), null, subscriptionId, null,null);
		//fixme product offer id
		data1.setAddOn(addOn);
		helper.insertAddonRecord(data1);
		
		
		try {
			operation.updateSubscription("Subscriber", subscriptionId , newState , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null,  transactionFactory);
		} catch (OperationFailedException exception) {		
			// do nothing
		}
		
		verify(alertListener, only())
		.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
		
	}
	
	private TransactionFactory setUpMockToGenerateHIGHRESPONSEAlertWhile_fetching_subscription() throws Exception {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

		when(preparedStatement.executeQuery()).thenAnswer(new Answer<ResultSet>() {

			/// paused call to generate high response time alert
			public ResultSet answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {
				long startTime = System.currentTimeMillis();
				long elapsedTime = 0;
				while (elapsedTime < AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 100) {
					elapsedTime = System.currentTimeMillis() - startTime;
				}
				return mock(ResultSet.class);
			};

		});
		return transactionFactory;
	}
	
	@Test
	public void test_should_generate_QUERYTIMEOUT_alert() throws Exception {
		AlertListener alertListener = mock(AlertListener.class);
		String subscriptionId = "101";
		SubscriptionOperationExt1 operation = new SubscriptionOperationExt1(alertListener, policyRepository);
		String subscriber = "Subscriber";
		operation = mockSubscriptionOperationToSkipgetSubscriotionCall(subscriptionId, operation, subscriber);
	
		expectedException.expect(OperationFailedException.class);
		
		TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTalert();
		
		try {
			Integer subscriptionStatusValue = SubscriptionState.UNSUBSCRIBED.state;
			operation.updateSubscription(subscriber, subscriptionId , subscriptionStatusValue , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null, transactionFactory);
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString());
			throw e;
		}
		
		fail("should throw OperationFailedException");
	}
	
	private TransactionFactory setUpMockToGenerateQUERYTIMEOUTalert() throws Exception {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

		when(preparedStatement.executeUpdate()).thenThrow(new SQLException("query timeout", "timeout", CommonConstants.QUERY_TIMEOUT_ERRORCODE));
		return transactionFactory;
	}
	
	@Test
	public void test_should_generate_QUERYTIMEOUT_alert_while_fetchig_subscription() throws Exception {
		AlertListener alertListener = mock(AlertListener.class);
		String subscriptionId = "101";
		SubscriptionOperationImpl operation = new SubscriptionOperationTestSuite.SubscriptionOperationExt(alertListener, policyRepository);
		
		expectedException.expect(OperationFailedException.class);
		
		TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTalertWhileFetchingSubscription();
		
		try {
			Integer subscriptionStatusValue = SubscriptionState.UNSUBSCRIBED.state;
			operation.updateSubscription("Subscriber", subscriptionId , subscriptionStatusValue , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null,  transactionFactory);
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString());
			throw e;
		}
		
		fail("should throw OperationFailedException");
	}
	
	private TransactionFactory setUpMockToGenerateQUERYTIMEOUTalertWhileFetchingSubscription() throws Exception {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

		when(preparedStatement.executeQuery()).thenThrow(new SQLException("query timeout", "timeout", CommonConstants.QUERY_TIMEOUT_ERRORCODE));
		return transactionFactory;
	}
	
	/*
	 * This version is created to test expiry scenario that is based on current
	 * time
	 */

	public static class SubscriptionOperationExt1 extends SubscriptionOperationImpl {
		
		private long count = 0;
		
		public SubscriptionOperationExt1(AlertListener alertListener, PolicyRepository policyRepository) {
			super(alertListener, policyRepository, null, null,null, null, null);
		}

		@Override
		protected long getCurrentTime() {

			Calendar customCalender = Calendar.getInstance();
			customCalender.set(2015, 00, 01, 01, 01, 01);
			return customCalender.getTimeInMillis();
		}
		
		@Override
		String getNextSubscriptionId() {
			return String.valueOf(++count);
		}
		
		public String getLastSubscriptionID() {
			return String.valueOf(count);
		}
		
		@Override
		Subscription getActiveSubscriptionBySubscriptionId(String subscriptionId, long currentTimeMS, TransactionFactory transactionFactory) throws OperationFailedException {
			return null;
		}
	}
}
