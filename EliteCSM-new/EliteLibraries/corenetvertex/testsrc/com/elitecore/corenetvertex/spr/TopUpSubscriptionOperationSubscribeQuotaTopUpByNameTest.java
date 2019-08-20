package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
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
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.DummyPolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.BillingCycleAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.DailyAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.WeeklyAllowedUsage;
import com.elitecore.corenetvertex.pm.util.MockQuotaTopUp;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionData;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.DerbyUtil;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
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
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static org.junit.Assert.assertEquals;
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
public class TopUpSubscriptionOperationSubscribeQuotaTopUpByNameTest {


    private DummyPolicyRepository policyRepository;
    private DummyTransactionFactory transactionFactory;
    private String testingDB = UUID.randomUUID().toString();
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private TopUpSubscriptionOperationTestSuite.TopUpSubscriptionDBHelper helper;
    private HibernateSessionFactory hibernateSessionFactory;
    @Mock
    private AlertListener alertListener;

    @BeforeClass
    public static void setUpClass() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    }

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        policyRepository = new DummyPolicyRepository();
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", SubscriptionOperationTestSuite.DS_NAME, "jdbc:derby:memory:"
                + testingDB + ";create=true", "", "", 1, 5, 3000);
        transactionFactory = (DummyTransactionFactory) new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1).build();
        transactionFactory.createTransaction();
        helper = new TopUpSubscriptionOperationTestSuite.TopUpSubscriptionDBHelper(transactionFactory);
        helper.createTables();
    }

    @After
    public void afterDropTables() throws Exception {
        helper.dropTables();
        DBUtility.closeQuietly(transactionFactory.getConnection());
        DerbyUtil.closeDerby(testingDB);
    }


    private void setUpMockTransactionFactoryDead() {
        transactionFactory = spy(transactionFactory);
        when(transactionFactory.isAlive()).thenReturn(false);
    }

    private MockQuotaTopUp createMockQuotaTopUp(){
        MockQuotaTopUp mockQuotaTopUp = policyRepository.mockQuotaTopUp();
        mockQuotaTopUp.quotaProfileTypeIsRnC();
        QuotaProfile quotaProfile = new QuotaProfile("QuotaProfile",
                "PkgName",
                "QuotaProfileID",
                BalanceLevel.HSQ, 2, RenewalIntervalUnit.MONTH,
                QuotaProfileType.RnC_BASED, Arrays.asList(createQuotaProfile()), CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue());
        mockQuotaTopUp.setQuotaProfiles(quotaProfile);
        when(mockQuotaTopUp.getValidityPeriodUnit()).thenReturn(ValidityPeriodUnit.MID_NIGHT);
        when(mockQuotaTopUp.getValidity()).thenReturn(28);

        return mockQuotaTopUp;
    }

    @Test
    public void test_should_throw_OperationFailedException_when_DB_is_down() throws Exception {
        String subscriberId = "101";
        String parentId = "102";

        TopUpSubscriptionOperation operation = getNewSubscriptionOperation();
        setUpMockTransactionFactoryDead();

        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Datasource not available"); // part of message
        MockQuotaTopUp quotaTopUp = createMockQuotaTopUp();

        try {
            operation.subscribeQuotaTopUpByName(createSprInfo(subscriberId), parentId, quotaTopUp.getName(), SubscriptionState.STARTED.state, null, null, null,0,null, "param1", "param2", transactionFactory);
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
        TopUpSubscriptionOperation operation = getNewSubscriptionOperation();

        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Start time(" + new Timestamp(startTime).toString() + ") is more or equal to end time("
                + new Timestamp(endTime).toString() + ")");

        final Integer state = null;
        MockQuotaTopUp quotaTopUp = createMockQuotaTopUp();

        try {
            operation.subscribeQuotaTopUpByName(createSprInfo("101"), "102", quotaTopUp.getName(), state,
                    startTime, endTime, null,0,null, "param1", "param2", transactionFactory);
        } catch (OperationFailedException e) {
            assertSame(ResultCode.INVALID_INPUT_PARAMETER, e.getErrorCode());
            getLogger().debug("TEST", e.getMessage());
            throw e;
        }

        fail("should throw OperationFailedException");
    }

    private TopUpSubscriptionOperationTestSuite.TopUpSubscriptionOperationExt getNewSubscriptionOperation() {
        return new TopUpSubscriptionOperationTestSuite.TopUpSubscriptionOperationExt(alertListener, policyRepository);
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
        TopUpSubscriptionOperation operation = getNewSubscriptionOperation();
        final Long startTime = null;
        final Long endTime = null;
        MockQuotaTopUp quotaTopUp = createMockQuotaTopUp();

        expectedException.expect(OperationFailedException.class);
        if (subscriptionStatusValue == 9) {
            expectedException.expectMessage("Invalid subscription status value: " + subscriptionStatusValue + " received");
        } else {
            expectedException.expectMessage("Invalid subscription status: " + SubscriptionState.fromValue(subscriptionStatusValue).name + " received");
        }
        try {
            operation.subscribeQuotaTopUpByName(createSprInfo("101"), "102", quotaTopUp.getName(), subscriptionStatusValue, startTime, endTime,
                    null, 0,null,"param1", "param2", transactionFactory);
        } catch (OperationFailedException e) {
            assertSame(ResultCode.INVALID_INPUT_PARAMETER, e.getErrorCode());
            throw e;
        }

        fail("should throw OperationFailedException");
    }

    @Test
    public void test_should_status_STARTED_when_subscription_state_not_provided() throws Exception {
        TopUpSubscriptionOperation operation = getNewSubscriptionOperation();

        final Long startTime = null;
        final Long endTime = null;
        MockQuotaTopUp quotaTopUp = createMockQuotaTopUp();

        String subscriberIdentity = "501";
        Integer STATUS_NOT_PROVIDED = null;

        operation.subscribeQuotaTopUpByName(createSprInfo(subscriberIdentity), "102", quotaTopUp.getName(), STATUS_NOT_PROVIDED, startTime, endTime,
                null, 0,null,"param1", "param2", transactionFactory);

        Map<String, Subscription> quotaTopUpSubscriptions = operation.getQuotaTopUpSubscriptions(subscriberIdentity, transactionFactory);

        if (Maps.isNullOrEmpty(quotaTopUpSubscriptions)) {
            fail("subscription should found from DB");
        }

        Subscription actualSubscription = quotaTopUpSubscriptions.values().iterator().next();
        assertSame(SubscriptionState.STARTED, actualSubscription.getStatus());
    }

    @Test
    public void test_should_throw_OperationFailedException_when_quotaTopUp_not_found_for_provided_quotaTopUpName() throws Exception {
        TopUpSubscriptionOperation operation = getNewSubscriptionOperation();
        final String addonName = "quotaTopUpName";
        final Long startTime = null;
        final Long endTime = null;
        final Integer state = null;

        expectedException.expectMessage("quota topUp not found for Name: " + addonName);
        expectedException.expect(OperationFailedException.class);
        when(policyRepository.getAddOnByName(addonName)).thenReturn(null);

        try {
            operation.subscribeQuotaTopUpByName(createSprInfo("101"), "102", addonName, state, startTime, endTime, null,  0,null,"param1", "param2", transactionFactory);
        } catch (OperationFailedException e) {
            assertSame(ResultCode.NOT_FOUND, e.getErrorCode());
            throw e;
        }

        fail("should throw OperationFailedException");
    }

    @Test
    public void test_should_throw_OperationFailedException_when_endtime_is_past_time() throws Exception {
        TopUpSubscriptionOperation operation = getNewSubscriptionOperation();
        final Long startTime = null;
        final Long endTime = System.currentTimeMillis() - 10;
        final Integer state = null;

        expectedException.expect(OperationFailedException.class);

        MockQuotaTopUp quotaTopUp = createMockQuotaTopUp();
        when(quotaTopUp.getValidityPeriodUnit()).thenReturn(null);

        try {
            operation.subscribeQuotaTopUpByName(createSprInfo("101"), "102", quotaTopUp.getName(), state, startTime, endTime, null,  0,null,"param1", "param2", transactionFactory);
        } catch (OperationFailedException e) {
            assertSame(ResultCode.INVALID_INPUT_PARAMETER, e.getErrorCode());
            getLogger().debug("TEST", e.getMessage());
            throw e;
        }

        fail("should throw OperationFailedException");
    }

    @Test
    public void test_should_add_subscription_provided() throws Exception {
        TopUpSubscriptionOperationTestSuite.TopUpSubscriptionOperationExt operation = getNewSubscriptionOperation();
        String subscriberId = "501";
        long currentTime = System.currentTimeMillis();
        final long startTime = currentTime;
        final long endTime = currentTime + TimeUnit.DAYS.toMillis(1);
        final int status = 2;
        MockQuotaTopUp quotaTopUp = createMockQuotaTopUp();
        when(quotaTopUp.getValidityPeriodUnit()).thenReturn(ValidityPeriodUnit.DAY);
        when(quotaTopUp.getValidity()).thenReturn(1);

        operation.subscribeQuotaTopUpByName(createSprInfo(subscriberId), subscriberId, quotaTopUp.getName(),
                status, startTime, endTime,
                null,  0,null,null, null, transactionFactory);

        Map<String, Subscription> quotaTopUpSubscriptions = operation.getQuotaTopUpSubscriptions(subscriberId, transactionFactory);

        if (Maps.isNullOrEmpty(quotaTopUpSubscriptions)) {
            fail("subscription should found from DB");
        }

        if (quotaTopUpSubscriptions.size() > 1) {
            fail("only one subscription should exist");
        }

        Subscription expectedQuotaTopUpSubscription = new Subscription(operation.getLastSubscriptionID(), subscriberId, quotaTopUp.getId(),null,
                new Timestamp(startTime), new Timestamp(endTime), SubscriptionState.fromValue(status), CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.TOP_UP, null, null);

        Subscription actualSubscription = quotaTopUpSubscriptions.values().iterator().next();

        assertReflectionEquals(expectedQuotaTopUpSubscription, actualSubscription, ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void test_should_throw_exception_when_multiple_subscription_is_not_allowed_and_existing_subscription_found() throws Exception {
        String subscriberId = "501";
        String STARTED_STATUS = "2";
        MockQuotaTopUp quotaTopUp = createMockQuotaTopUp();
        doReturn(false).when(quotaTopUp).isMultipleSubscription();

        SubscriptionData data = new SubscriptionData(subscriberId, quotaTopUp.getId(), null, null, STARTED_STATUS, null, "1", null,null);

        TopUpSubscriptionOperation operation = getNewSubscriptionOperation();


        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("already exist for subscriber ID(" + subscriberId
                + "), package(" + quotaTopUp.getName() + ")");

        operation.subscribeQuotaTopUpByName(createSprInfo(data.getSubscriberId()), data.getSubscriberId(), quotaTopUp.getName(),
                Integer.valueOf(data.getStatus()), null, null,
                null,0,null, data.getParam1(), data.getParam2(), transactionFactory);

        operation.subscribeQuotaTopUpByName(createSprInfo(data.getSubscriberId()), data.getSubscriberId(), quotaTopUp.getName(),
                Integer.valueOf(data.getStatus()), null, null,
                null,0,null, data.getParam1(), data.getParam2(), transactionFactory);

    }

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


    @Test
    @Parameters(method="dataProviderFor_test_should_subscribe_when_multiple_subscription_is_not_allowed_and_existing_subscription_found_with_REJECTED_and_UNSUBSCRIBED")
    public void test_should_subscribe_when_multiple_subscription_is_not_allowed_and_existing_subscription_found_with_REJECTED_and_UNSUBSCRIBED(
            int status) throws Exception {
        final String subscriberId = "501";

        MockQuotaTopUp quotaTopUp = createMockQuotaTopUp();
        doReturn(false).when(quotaTopUp).isMultipleSubscription();

        SubscriptionData oldSubscription = new SubscriptionData(subscriberId, quotaTopUp.getId(), null, null, String.valueOf(status), "1", UUID.randomUUID().toString(), null,null);
        oldSubscription.setQuotaTopUp(quotaTopUp);
        helper.insertAddonRecord(oldSubscription);

        SubscriptionData newAddonSubscription = new SubscriptionData(subscriberId, quotaTopUp.getId(), null, null, "2",null, null, null,null);
        newAddonSubscription.setQuotaTopUp(quotaTopUp);

        TopUpSubscriptionOperation operation = getNewSubscriptionOperation();



        operation.subscribeQuotaTopUpByName(createSprInfo(newAddonSubscription.getSubscriberId()), newAddonSubscription.getSubscriberId(), quotaTopUp.getName(),
                Integer.valueOf(newAddonSubscription.getStatus()), null, null,
                null, 0,null,newAddonSubscription.getParam1(), newAddonSubscription.getParam2(), transactionFactory);

        Map<String, Subscription> quotaTopUpSubscriptions = operation.getQuotaTopUpSubscriptions(subscriberId, transactionFactory);

        if (Maps.isNullOrEmpty(quotaTopUpSubscriptions)) {
            fail("subscription should found from DB");
        }

        Subscription actualSubscription = quotaTopUpSubscriptions.values().iterator().next();
        assertEquals(newAddonSubscription.getAddonId(), actualSubscription.getPackageId());
        assertEquals(newAddonSubscription.getSubscriberId(), actualSubscription.getSubscriberIdentity());
    }

    @Test
    public void test_should_subscribe_when_multiple_subscription_is_not_allowed_and_existing_subscription_expired_subscription_found() throws Exception {
        final String subscriberId = "501";

        MockQuotaTopUp quotaTopUp = createMockQuotaTopUp();
        doReturn(false).when(quotaTopUp).isMultipleSubscription();

        final Timestamp endDate = new Timestamp(System.currentTimeMillis()-TimeUnit.DAYS.toMillis(1));

        SubscriptionData oldSubscription = new SubscriptionData(subscriberId, quotaTopUp.getId(), null, endDate.toString(), String.valueOf(SubscriptionState.STARTED.state), "1", UUID.randomUUID().toString(), null,null);
        oldSubscription.setQuotaTopUp(quotaTopUp);
        helper.insertAddonRecord(oldSubscription);

        SubscriptionData newAddonSubscription = new SubscriptionData(subscriberId, quotaTopUp.getId(), null, null, "2",null, null, null,null);
        newAddonSubscription.setQuotaTopUp(quotaTopUp);

        TopUpSubscriptionOperation operation = getNewSubscriptionOperation();



        operation.subscribeQuotaTopUpByName(createSprInfo(newAddonSubscription.getSubscriberId()), newAddonSubscription.getSubscriberId(), quotaTopUp.getName(),
                Integer.valueOf(newAddonSubscription.getStatus()), null, null,
                null,0,null, newAddonSubscription.getParam1(), newAddonSubscription.getParam2(), transactionFactory);

        Map<String, Subscription> quotaTopUpSubscriptions = operation.getQuotaTopUpSubscriptions(subscriberId, transactionFactory);

        if (Maps.isNullOrEmpty(quotaTopUpSubscriptions)) {
            fail("subscription should found from DB");
        }

        Subscription actualSubscription = quotaTopUpSubscriptions.values().iterator().next();
        assertEquals(newAddonSubscription.getAddonId(), actualSubscription.getPackageId());
        assertEquals(newAddonSubscription.getSubscriberId(), actualSubscription.getSubscriberIdentity());
    }


    @Test
    public void test_should_subscribe_when_multiple_subscription_is_not_allowed_and_existing_subscription_found_not_found() throws Exception {
        final String subscriberId = "501";
        MockQuotaTopUp quotaTopUp = createMockQuotaTopUp();
        doReturn(false).when(quotaTopUp).isMultipleSubscription();

        SubscriptionData newAddonSubscription = new SubscriptionData(subscriberId, quotaTopUp.getId(), null, null, "2",null, "1", null,null);

        TopUpSubscriptionOperation operation = getNewSubscriptionOperation();



        operation.subscribeQuotaTopUpByName(createSprInfo(newAddonSubscription.getSubscriberId()), newAddonSubscription.getSubscriberId(), quotaTopUp.getName(),
                Integer.valueOf(newAddonSubscription.getStatus()), null, null,
                null, 0,null,newAddonSubscription.getParam1(), newAddonSubscription.getParam2(), transactionFactory);

        Map<String, Subscription> quotaTopUpSubscriptions = operation.getQuotaTopUpSubscriptions(subscriberId, transactionFactory);

        if (Maps.isNullOrEmpty(quotaTopUpSubscriptions)) {
            fail("subscription should found from DB");
        }

        Subscription actualSubscription = quotaTopUpSubscriptions.values().iterator().next();
        assertEquals(newAddonSubscription.getAddonId(), actualSubscription.getPackageId());
        assertEquals(newAddonSubscription.getSubscriberId(), actualSubscription.getSubscriberIdentity());
    }

    @Test
    public void test_should_generate_DB_NO_CONNECTION_alert_when_connection_not_found() throws Exception {
        AlertListener alertListener = mock(AlertListener.class);
        TransactionFactory transactionFactory = setUpMockTogenerateDB_NO_CONNECTION_alertForSubscribeByAddOnId();
        String subscriberId = "501";
        MockQuotaTopUp quotaTopUp = createMockQuotaTopUp();
        doReturn(true).when(quotaTopUp).isMultipleSubscription();

        SubscriptionData data = new SubscriptionData(subscriberId, quotaTopUp.getId(), null, null, "2",null, "1", null,null);

        TopUpSubscriptionOperation operation = new TopUpSubscriptionOperationTestSuite.TopUpSubscriptionOperationExt(alertListener, policyRepository);



        expectedException.expect(OperationFailedException.class);

        try {
            operation.subscribeQuotaTopUpByName(createSprInfo(data.getSubscriberId()), data.getSubscriberId(), quotaTopUp.getName(),
                    Integer.valueOf(data.getStatus()), null, null,
                    null,0,null, data.getParam1(), data.getParam2(), transactionFactory);

        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), Mockito.anyString(), Mockito.anyString());
            throw e;
        }

        fail("getSubscriptions should throw Exception");
    }

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
        TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlertForSubscriberByQuotaTopUpName();
        TopUpSubscriptionOperation operation = getNewSubscriptionOperation();

        String subscriberId = "501";
        MockQuotaTopUp quotaTopUp = createMockQuotaTopUp();

        SubscriptionData data = new SubscriptionData(subscriberId, quotaTopUp.getId(), null, null, "2",null, "1", null,null);



        operation.subscribeQuotaTopUpByName(createSprInfo(data.getSubscriberId()), data.getSubscriberId(), quotaTopUp.getName(),
                Integer.valueOf(data.getStatus()), null, null,
                null,0,null, data.getParam1(), data.getParam2(), transactionFactory);

        verify(alertListener, only())
                .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
    }

    private TransactionFactory setUpMockToGenerateHIGHRESPONSEAlertForSubscriberByQuotaTopUpName() throws Exception {
        TransactionFactory transactionFactory = spy(this.transactionFactory);
        Transaction transaction = mock(Transaction.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        doReturn(transaction).when(transactionFactory).createTransaction();
        doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

        ResultSet resultSet = mock(ResultSet.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(preparedStatement.execute()).thenAnswer(new Answer<Boolean>() {

            /// paused call to generate high response time alert
            public Boolean answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {
                long startTime = System.currentTimeMillis();
                long elapsedTime = 0;
                while (elapsedTime < AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10) {
                    elapsedTime = System.currentTimeMillis() - startTime;
                }
                return false;
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
        TopUpSubscriptionOperation operation =getNewSubscriptionOperation();

        expectedException.expect(OperationFailedException.class);

        TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTalertForSubscribedQuotaTopUp();
        String subscriberId = "501";
        MockQuotaTopUp quotaTopUp = createMockQuotaTopUp();
        SubscriptionData data = new SubscriptionData(subscriberId, quotaTopUp.getId(), null, null, "2",null, "1", null,null);


        try {
            operation.subscribeQuotaTopUpByName(createSprInfo(data.getSubscriberId()), data.getSubscriberId(), quotaTopUp.getName(),
                    Integer.valueOf(data.getStatus()), null, null,
                    null,0,null, data.getParam1(), data.getParam2(), transactionFactory);
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString());
            throw e;
        }

        fail("should throw OperationFailedException");
    }

    private TransactionFactory setUpMockToGenerateQUERYTIMEOUTalertForSubscribedQuotaTopUp() throws Exception {
        TransactionFactory transactionFactory = spy(this.transactionFactory);
        Transaction transaction = mock(Transaction.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        doReturn(transaction).when(transactionFactory).createTransaction();
        doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
        doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

        when(preparedStatement.executeQuery()).thenThrow(new SQLException("query timeout", "timeout", CommonConstants.QUERY_TIMEOUT_ERRORCODE));
        return transactionFactory;
    }

    @Test
    public void test_should_generate_DB_HIGH_QUERY_RESPONSE_TIME_alert_when_execution_time_is_high_for_checking_existing_subscription() throws Exception {
        TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlertForExistingSubscriptionCheck();
        AlertListener alertListener = mock(AlertListener.class);
        TopUpSubscriptionOperation operation = new TopUpSubscriptionOperationTestSuite.TopUpSubscriptionOperationExt(alertListener, policyRepository);

        String subscriberId = "501";
        MockQuotaTopUp quotaTopUp = createMockQuotaTopUp();

        doReturn(false).when(quotaTopUp).isMultipleSubscription();

        SubscriptionData data = new SubscriptionData(subscriberId, quotaTopUp.getId(), null, null, "2",null, "1", null,null);



        operation.subscribeQuotaTopUpByName(createSprInfo(data.getSubscriberId()), data.getSubscriberId(), quotaTopUp.getName(),
                Integer.valueOf(data.getStatus()), null, null,
                null,0,null, data.getParam1(), data.getParam2(), transactionFactory);

        verify(alertListener, only())
                .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
    }

	 /*
	 * After all operation, transaction should end properly
	 *
	 * Actually its closing connection, so verified connection.close() call
	 */

    @Test
    public void test_should_always_end_transaction() throws Exception {

        TopUpSubscriptionOperation operation = getNewSubscriptionOperation();

        String subscriberId = "501";


        MockQuotaTopUp quotaTopUp = createMockQuotaTopUp();

        operation.subscribeQuotaTopUpByName(createSprInfo(subscriberId), "102", quotaTopUp.getName(),
                null, null, null,
                null,0,null, null, null, transactionFactory);

        Connection connection = getConnection();

        assertTrue(connection.isClosed());
    }


    private Connection getConnection() {
        Connection connection = ((DummyTransactionFactory) transactionFactory).getConnection();
        return connection;
    }

    private Map<String, QuotaProfileDetail> createQuotaProfile(){
        Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage = new HashMap<>();

        aggregationKeyToAllowedUsage.put(AggregationKey.BILLING_CYCLE,
                new BillingCycleAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, com.elitecore.corenetvertex.constants.TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.DAILY,
                new DailyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, com.elitecore.corenetvertex.constants.TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.WEEKLY,
                new WeeklyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, com.elitecore.corenetvertex.constants.TimeUnit.SECOND));

        DataServiceType dataServiceType = new DataServiceType("test", "test", 1, Collections.emptyList(), Collections.emptyList());

        RatingGroup ratingGroup = new RatingGroup(UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),1);

        RncProfileDetail rncProfileDetail = new RncProfileDetail("test", dataServiceType, 0, ratingGroup,
				aggregationKeyToAllowedUsage, 0, 0,0,0, DataUnit.BYTE.name(),
				com.elitecore.corenetvertex.constants.TimeUnit.SECOND.name(), 0.0, null, null,
				VolumeUnitType.TOTAL, "test", true, "pccProfileName",0,0, "test");

        Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails = new HashMap<>();

        fupLevelServiceWiseQuotaProfileDetails.put("test",rncProfileDetail);

        return fupLevelServiceWiseQuotaProfileDetails;
    }

    private SPRInfo createSprInfo(String subscriberIdentity) {
        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(subscriberIdentity);
        return sprInfo;
    }
}
