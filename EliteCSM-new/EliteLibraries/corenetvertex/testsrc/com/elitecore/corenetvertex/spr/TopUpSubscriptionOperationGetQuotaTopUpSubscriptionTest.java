package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
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
import com.elitecore.corenetvertex.pm.factory.QuotaTopUpPackageFactory;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.BillingCycleAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.DailyAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.WeeklyAllowedUsage;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.DerbyUtil;
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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
public class TopUpSubscriptionOperationGetQuotaTopUpSubscriptionTest {

    private static final String INVALID_STATUS_VALUE = "9";
    private static final String EXPIRED_STATUS = "5";

    private static final String PREPARESTATEMENT = "preparedStatement";
    private static final String BEGIN = "begin";
    private static final String EXECUTE = "execute";
    private static final String GET = "get";
    private String subscriberIdentity = "101";
    private DummyPolicyRepository policyRepository;

    private QuotaTopUp quotaTopUp;
    private String testingDB = UUID.randomUUID().toString();

    private DummyTransactionFactory transactionFactory;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private TopUpSubscriptionOperationTestSuite.TopUpSubscriptionDBHelper helper;
    private UMOperationTest.DummySPRInfo sprInfo;


    @BeforeClass
    public static void setUpClass() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    }

    @Before
    public void setUp() throws Exception {


        MockitoAnnotations.initMocks(this);
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", SubscriptionOperationTestSuite.DS_NAME, "jdbc:derby:memory:"
                + testingDB + ";create=true", "", "", 1, 1, 3000);
        transactionFactory = (DummyTransactionFactory)new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1).build();
        transactionFactory.createTransaction();
        policyRepository = new DummyPolicyRepository();

        quotaTopUp = QuotaTopUpPackageFactory.create(UUID.randomUUID().toString(), "name" + UUID.randomUUID().toString(),
                new QuotaProfile(
                        "QuotaProfileName", "PkgName", "QuotaProfileId", BalanceLevel.HSQ,2, RenewalIntervalUnit.MONTH, QuotaProfileType.RnC_BASED, Arrays.asList(createQuotaProfile()), CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue()));

        policyRepository.addQuotaTopUp(quotaTopUp);

        helper = new TopUpSubscriptionOperationTestSuite.TopUpSubscriptionDBHelper(transactionFactory);
        sprInfo = new UMOperationTest.DummySPRInfo();
        sprInfo.setSubscriberIdentity(subscriberIdentity);
        createTablesAndInsertSubscriptionRecords();

    }

    @After
    public void afterDropTables() throws Exception {
        helper.dropTables();
        DBUtility.closeQuietly(transactionFactory.getConnection());
        DerbyUtil.closeDerby(testingDB);
    }

    /**
     * <PRE>
     * 3 subscription created
     * SubscriberIdentity > Addonsubscription
     * 				 101  ---- > 1,3
     * 	 			 102  ---- > 2
     * 	 			 103  ---- > no subscription
     * </PRE>
     */
    private void createTablesAndInsertSubscriptionRecords() throws Exception {
        getLogger().debug(this.getClass().getSimpleName(), "creating DB");
        helper.createTables();
        helper.insertAddonRecords(getAddonSubscritionData());

        getLogger().debug(this.getClass().getSimpleName(), "DB created");
    }

    /*
     * Add Adddon record here.
     *
     * Used for inserting table entry and generating expected entity
     */
    private List<SubscriptionData> getAddonSubscritionData() {

        SubscriptionData record1 = new SubscriptionData("101", quotaTopUp.getId(), "2014-10-29 09:26:50.12", "2020-12-31 09:26:50.12",
                "2", "0", "1", "2020-12-31 09:26:50.12",null);

        SubscriptionData record2 = new SubscriptionData("102", quotaTopUp.getId(), "2014-10-29 09:26:50.12", "2020-12-31 09:26:50.12",
                "2", "0", "2", "2020-12-31 09:26:50.12",null);

        SubscriptionData record3 = new SubscriptionData("101", quotaTopUp.getId(), "2014-10-29 09:26:50.12", "2020-12-31 09:26:50.12",
                "2", "0", "3", "2020-12-31 09:26:50.12",null);

        record1.setQuotaTopUp(quotaTopUp);
        record2.setQuotaTopUp(quotaTopUp);
        record3.setQuotaTopUp(quotaTopUp);

        return Arrays.asList(record1, record2, record3);
    }

    @Test
    @Parameters(value = { "101", "102", "103" })
    public void test_getQuotaTopUpsForSubscriber_should_give_list_of_subscription(String subscriberIdentity) throws Exception {
        TopUpSubscriptionOperation subscriptionOperation = getNewSubscriptionOperation();
        Map<String, Subscription> actualSubscriptions = subscriptionOperation.getQuotaTopUpSubscriptions(subscriberIdentity, transactionFactory);
        Map<String, Subscription> expectedQuotaTopUpSubscription = helper.getQuotaTopUpsForSubscriber(subscriberIdentity);

        assertReflectionEquals(expectedQuotaTopUpSubscription, actualSubscriptions, ReflectionComparatorMode.LENIENT_ORDER);
    }

    private TopUpSubscriptionOperationTestSuite.TopUpSubscriptionOperationExt getNewSubscriptionOperation() {
        return new TopUpSubscriptionOperationTestSuite.TopUpSubscriptionOperationExt(mock(AlertListener.class), policyRepository);
    }

    @Test
    @Parameters(value = { "501" })
    public void test_getQuotaTopUpSubscription_should_give_empty_list_when_no_subscription_found(String subscriberIdentity) throws Exception {
        TopUpSubscriptionOperation subscriptionOperation = getNewSubscriptionOperation();
        Map<String, Subscription> actualSubscriptions = subscriptionOperation.getQuotaTopUpSubscriptions(subscriberIdentity, transactionFactory);

        assertTrue(actualSubscriptions.isEmpty());
    }

    @Test
    @Parameters(value = { "501"})
    public void test_getAddonSubscriptionBySPRInfo_should_give_empty_list_when_no_subscription_found(String subscriberIdentity) throws Exception {
        UMOperationTest.DummySPRInfo sprInfo = new UMOperationTest.DummySPRInfo();
        sprInfo.setSubscriberIdentity(subscriberIdentity);
        TopUpSubscriptionOperation subscriptionOperation = getNewSubscriptionOperation();
        Map<String, Subscription> actualSubscriptions = subscriptionOperation.getQuotaTopUpSubscriptions(sprInfo, transactionFactory);
        assertTrue(actualSubscriptions.isEmpty());
    }

    @SuppressWarnings("unused")
    private Object[][] dataProviderFor_test_getQuotaTopUpSubscriptions_should_throw_OperationFailedException_when_any_exception_thrown() {
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
    @Parameters(method = "dataProviderFor_test_getQuotaTopUpSubscriptions_should_throw_OperationFailedException_when_any_exception_thrown")
    public void test_getQuotaTopUpSubscriptions_should_throw_OperationFailedException_when_any_exception_thrown(
            String whenToThrow,
            Class<? extends Throwable> exceptionToBeThrown) throws Exception {

        setupMockToGenerateException(whenToThrow, exceptionToBeThrown);

        expectedException.expect(OperationFailedException.class);

        TopUpSubscriptionOperation operation = getNewSubscriptionOperation();

        operation.getQuotaTopUpSubscriptions(getFirstQuotaTopUpSubscriberId(), transactionFactory);
    }

    @Test
    @Parameters(method = "dataProviderFor_test_getQuotaTopUpSubscriptions_should_throw_OperationFailedException_when_any_exception_thrown")
    public void test_getQuotaTopUpSubscriptionsBySPRInfo_should_throw_OperationFailedException_when_any_exception_thrown(
            String whenToThrow,
            Class<? extends Throwable> exceptionToBeThrown) throws Exception {
        String subscriberIdentity = "101";
        sprInfo.setSubscriberIdentity(subscriberIdentity);
        setupMockToGenerateException(whenToThrow, exceptionToBeThrown);

        expectedException.expect(OperationFailedException.class);

        TopUpSubscriptionOperation operation = getNewSubscriptionOperation();

        operation.getQuotaTopUpSubscriptions(sprInfo, transactionFactory);
    }

    @Test
    public void test_ggetQuotaTopUpSubscriptions_should_throw_OperationFailedException_when_DB_is_down() throws Exception {
        TopUpSubscriptionOperation operation = getNewSubscriptionOperation();

        setUpMockTransactionFactoryDead();

        expectedException.expect(OperationFailedException.class);

        operation.getQuotaTopUpSubscriptions(getFirstQuotaTopUpSubscriberId(), transactionFactory);

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

    private String getFirstQuotaTopUpSubscriberId() {
        return getAddonSubscritionData().get(0).getSubscriberId();
    }

    /**
     * After all operation, transaction should end properly
     *
     * Actually its closing connection, so verified connection.close() call
     *
     */
    @Test
    public void test_getQuotaTopUpSubscriptions_should_always_end_transaction() throws Exception {

        TopUpSubscriptionOperation operation = getNewSubscriptionOperation();

        operation.getQuotaTopUpSubscriptions(getFirstQuotaTopUpSubscriberId(), transactionFactory);

        Connection connection = getConnection();

        assertTrue(connection.isClosed());
    }

    @Test
    public void test_getQuotaTopUpSubscriptionsBySPRInfo_should_always_end_transaction() throws Exception {

        TopUpSubscriptionOperation operation = getNewSubscriptionOperation();

        operation.getQuotaTopUpSubscriptions(sprInfo, transactionFactory);

        Connection connection = getConnection();

        assertTrue(connection.isClosed());
    }


    private Connection getConnection() {
        Connection connection = ((DummyTransactionFactory) transactionFactory).getConnection();
        return connection;
    }

    @Test
    public void test_getQuotaTopUpSubscriptions_should_generate_DB_NO_CONNECTION_alert_when_connection_not_found() throws Exception {
        AlertListener alertListener = mock(AlertListener.class);
        TransactionFactory transactionFactory = setUpMockTogenerateDB_NO_CONNECTION_alert();
        TopUpSubscriptionOperation operation = new TopUpSubscriptionOperationTestSuite.TopUpSubscriptionOperationExt(alertListener, policyRepository);

        expectedException.expect(OperationFailedException.class);
        try {
            operation.getQuotaTopUpSubscriptions(getFirstQuotaTopUpSubscriberId(), transactionFactory);
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), Mockito.anyString(), Mockito.anyString());
            throw e;
        }

        fail("getSubscriptions should throw Exception");
    }

    @Test
    public void test_getQuotaTopUpSubscriptionsBySPRInfo_should_generate_DB_NO_CONNECTION_alert_when_connection_not_found() throws Exception {
        AlertListener alertListener = mock(AlertListener.class);
        TransactionFactory transactionFactory = setUpMockTogenerateDB_NO_CONNECTION_alert();
        TopUpSubscriptionOperation operation = new TopUpSubscriptionOperationTestSuite.TopUpSubscriptionOperationExt(alertListener, policyRepository);

        expectedException.expect(OperationFailedException.class);
        try {
            operation.getQuotaTopUpSubscriptions(sprInfo, transactionFactory);
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), Mockito.anyString(), Mockito.anyString());
            throw e;
        }

        fail("getSubscriptions should throw Exception");
    }

    @Test
    public void test_getQuotaTopUpSubscriptions_should_generate_DB_HIGH_QUERY_RESPONSE_TIME_alert_when_execution_time_is_high() throws Exception {
        TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlert();
        AlertListener alertListener = mock(AlertListener.class);
        TopUpSubscriptionOperation operation = new TopUpSubscriptionOperationTestSuite.TopUpSubscriptionOperationExt(alertListener, policyRepository);

        operation.getQuotaTopUpSubscriptions(getFirstQuotaTopUpSubscriberId(), transactionFactory);

        verify(alertListener, only())
                .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void test_getQuotaTopUpSubscriptionsBySPRInfo_should_generate_DB_HIGH_QUERY_RESPONSE_TIME_alert_when_execution_time_is_high() throws Exception {
        TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlert();
        AlertListener alertListener = mock(AlertListener.class);
        TopUpSubscriptionOperation operation = new TopUpSubscriptionOperationTestSuite.TopUpSubscriptionOperationExt(alertListener, policyRepository);

        operation.getQuotaTopUpSubscriptions(sprInfo, transactionFactory);

        verify(alertListener, only())
                .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void test_getQuotaTopUpSubscriptions_should_generate_QUERYTIMEOUT_alert_when() throws Exception {
        AlertListener alertListener = mock(AlertListener.class);
        TopUpSubscriptionOperation operation = new TopUpSubscriptionOperationTestSuite.TopUpSubscriptionOperationExt(alertListener, policyRepository);

        expectedException.expect(OperationFailedException.class);

        TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTalert();

        try {
            operation.getQuotaTopUpSubscriptions(getFirstQuotaTopUpSubscriberId(), transactionFactory);
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito
                            .anyString());
            throw e;
        }

        fail("should throw OperationFailedException");
    }

    @Test
    public void test_getQuotaTopUpSubscriptionsBySPRInfo_should_generate_QUERYTIMEOUT_alert_when() throws Exception {
        AlertListener alertListener = mock(AlertListener.class);
        TopUpSubscriptionOperation operation = new TopUpSubscriptionOperationTestSuite.TopUpSubscriptionOperationExt(alertListener, policyRepository);

        expectedException.expect(OperationFailedException.class);

        TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTalert();

        try {
            operation.getQuotaTopUpSubscriptions(sprInfo, transactionFactory);
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
    public void test_getQuotaTopUpSubscriptions_should_not_give_addons_which_has_status_unsubscribed_or_invalid(String subscriptionState) throws Exception {
        String subscriberIdentity = "110";
        setUpFor_QuotaTopUp_with_invalid_status(subscriptionState, subscriberIdentity);

        TopUpSubscriptionOperation operation = getNewSubscriptionOperation();

        assertTrue(operation.getQuotaTopUpSubscriptions(subscriberIdentity, transactionFactory).isEmpty());
    }

    @Test
    @Parameters(value = { "2014-12-31 09:26:50.12", "2014-10-29 09:26:50.12", "2015-01-01 00:00:00.00", "2015-01-01 01:01:00.00", "" })
    public void test_getQuotaTopUpSubscriptions_should_not_give_expired_addons(String expiryDate) throws Exception {
        if (expiryDate.isEmpty()) {
            expiryDate = null;
        }

        String subscriberIdentity = "110";

        setUpFor_quotaTopUp_with_expired_time(expiryDate, subscriberIdentity);

        TopUpSubscriptionOperation operation = getNewSubscriptionOperation();

        assertTrue(operation.getQuotaTopUpSubscriptions(subscriberIdentity, transactionFactory).isEmpty());
    }

    @Test
    public void test_getAddonSubscription_should_give_subscription_with_resetdate_same_as_expirydate_if_resetdate_not_configured() throws Exception {
        String subscriberIdentity = "110";

        SubscriptionData data1 = new SubscriptionData(subscriberIdentity, quotaTopUp.getId(), "2014-10-29 09:26:50.12", "2020-10-29 09:26:50.12",
                "2", "1", "10", null,null);

        data1.setQuotaTopUp(quotaTopUp);

        helper.insertAddonRecord(data1);

        TopUpSubscriptionOperation operation = getNewSubscriptionOperation();

        Map<String, Subscription> actualSubscriptions = operation.getQuotaTopUpSubscriptions(subscriberIdentity, transactionFactory);
        Map<String, Subscription> expectedQuotaTopUpSubscription = helper.getQuotaTopUpsForSubscriber(subscriberIdentity);

        getLogger().debug("TEST", "expected: " + expectedQuotaTopUpSubscription.get(0));

        assertLenientEquals(expectedQuotaTopUpSubscription, actualSubscriptions);
    }

    private void setUpFor_quotaTopUp_with_expired_time(String expiryDate, String subscriberIdentity) throws Exception {
        SubscriptionData data1 = new SubscriptionData(subscriberIdentity, quotaTopUp.getId(), "2014-10-29 09:26:50.12", expiryDate,
                "2", "1", "10", "2015-12-31 09:26:50.12",null);

        data1.setQuotaTopUp(quotaTopUp);

        helper.insertAddonRecord(data1);

    }

    private void setUpFor_QuotaTopUp_with_invalid_status(String subscriptionState, String subscriberIdentity) throws Exception {
        SubscriptionData data1 = new SubscriptionData(subscriberIdentity, quotaTopUp.getId(), "2014-10-29 09:26:50.12", "2020-11-29 09:26:50.12",
                subscriptionState, "1", "10", "2015-12-31 09:26:50.12",null);

        data1.setQuotaTopUp(quotaTopUp);

        helper.insertAddonRecord(data1);
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

    @Test
    public void testGetQuotaTopUpSubscriptionsBySPRInfoReturnsSubscriptions() throws OperationFailedException {
        sprInfo.setActiveSubscriptions(helper.getQuotaTopUpsForSubscriber(subscriberIdentity));
        LinkedHashMap<String, Subscription> actualSubscriptions= getNewSubscriptionOperation().getQuotaTopUpSubscriptions(sprInfo, transactionFactory);
        LinkedHashMap<String, Subscription> expectedSubscriptions = helper.getQuotaTopUpsForSubscriber(subscriberIdentity);
        assertReflectionEquals(expectedSubscriptions, actualSubscriptions, ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void testGetSubscriptionsBySPRInfoSetsSubscriptionsLoadTime() throws OperationFailedException {
        Assume.assumeTrue(sprInfo.getSubscriptionsLoadTime() == -1);
        getNewSubscriptionOperation().getQuotaTopUpSubscriptions(sprInfo, transactionFactory);
        Assert.assertTrue(sprInfo.getSubscriptionsLoadTime() >= 0);
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

        RncProfileDetail rncProfileDetail = new RncProfileDetail("test", dataServiceType, 0, ratingGroup, aggregationKeyToAllowedUsage,
				0, 0,0,0, DataUnit.BYTE.name(), com.elitecore.corenetvertex.constants.TimeUnit.SECOND.name(),
				0.0, null, null, VolumeUnitType.TOTAL, "test", true, "pccProfileName",0,0, "test");

        Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails = new HashMap<>();

        fupLevelServiceWiseQuotaProfileDetails.put("test",rncProfileDetail);

        return fupLevelServiceWiseQuotaProfileDetails;
    }

}