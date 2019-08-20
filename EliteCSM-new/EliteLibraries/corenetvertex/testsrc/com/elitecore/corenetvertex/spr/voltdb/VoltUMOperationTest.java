package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.QosProfileFactory;
import com.elitecore.corenetvertex.pm.factory.BasePackageFactory;
import com.elitecore.corenetvertex.pm.factory.QuotaProfileFactory;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.spr.ResetUsageRequiredData;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.SubscriberUsageData;
import com.elitecore.corenetvertex.spr.balance.SubscriptionInformation;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.TimeSourceChain;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;
import org.voltdb.ClientResponseImpl;
import org.voltdb.InProcessVoltDBServer;
import org.voltdb.VoltTable;
import org.voltdb.client.ClientResponse;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@Ignore
@RunWith(JUnitParamsRunner.class)
public class VoltUMOperationTest {

    private static Timestamp FUTURE_DATE;
    private static InProcessVoltDBServer voltServer;
    private static final long CURRENT_TIME = System.currentTimeMillis();
    private static final String DEFAULT_SUBSCRIBER_ID = "007";

    private VoltUMOperation umOperation;
    private VoltUMOperationTestHelper  helper;

    @Mock private ProductOfferStore productOfferStore;
    @Mock private ProductOffer productOffer;
    @Mock private PolicyRepository policyRepository;
    @Mock private AlertListener alertListener;
    @Rule public ExpectedException expectedException = ExpectedException.none();

    static {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3650));
        calendar.set(Calendar.MILLISECOND, 0);

        FUTURE_DATE = new Timestamp(calendar.getTimeInMillis());
    }

    @BeforeClass
    public static void beforeClass() {
        voltServer = new InProcessVoltDBServer();
        voltServer.start();
        // Add SQL File containing Create Table and Create Procedure query
        voltServer.runDDLFromPath("testsrc/resources/voltdb-test-ddl.sql");
    }

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        helper = new VoltUMOperationTestHelper(new DummyVoltDBClient(voltServer.getClient()));
        when(policyRepository.getProductOffer()).thenReturn(productOfferStore);
        umOperation = new VoltUMOperation(alertListener, policyRepository, new FixedTimeSource(), null, null);
        cerateUsage();
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
    public void getUsageBySPRInfoGivesUsageInformation() throws Exception {
        DummySPRInfo sprInfo = new DummySPRInfo();
        sprInfo.setSubscriberIdentity(DEFAULT_SUBSCRIBER_ID);
        sprInfo.setActiveSubscriptions(helper.addOnSubscriptions.get(DEFAULT_SUBSCRIBER_ID));

        Map<String, Map<String, SubscriberUsage>> actualUsage = umOperation.getUsage(sprInfo, new DummyVoltDBClient(voltServer.getClient()));

        Map<String, Map<String, SubscriberUsage>> expectedUsage = helper.getUsageForSubscriber(DEFAULT_SUBSCRIBER_ID);
        assertReflectionEquals(expectedUsage, actualUsage, ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void getUsageBySubscriberIdGivesUsageInformation() throws Exception {

        Map<String, Map<String, SubscriberUsage>> actualUsage = umOperation.getUsage(DEFAULT_SUBSCRIBER_ID, helper.addOnSubscriptions.get(DEFAULT_SUBSCRIBER_ID), new DummyVoltDBClient(voltServer.getClient()));
        Map<String, Map<String, SubscriberUsage>> expectedUsage = helper.getUsageForSubscriber(DEFAULT_SUBSCRIBER_ID);

        assertReflectionEquals(expectedUsage, actualUsage, ReflectionComparatorMode.LENIENT_ORDER);

    }

    @Test
    public void getUsageBySPRInfoSetsUsageLoadTime() throws Exception {
        VoltUMOperation umOperation = new VoltUMOperation(null, null, new FixedTimeSource(), null, null);
        DummySPRInfo sprInfo = new DummySPRInfo();
        sprInfo.setSubscriberIdentity(DEFAULT_SUBSCRIBER_ID);
        Assume.assumeTrue(sprInfo.getUsageLoadTime() == -1);

        umOperation.getUsage(sprInfo, new DummyVoltDBClient(voltServer.getClient()));

        assertTrue(sprInfo.getUsageLoadTime() >= 0);
    }

    @Test
    public void getUsageGivesEmptyMapWhenNoUsageFound() throws Exception {
        Map<String, Map<String, SubscriberUsage>> result = umOperation.getUsage("UNKNOWN_SUBSCRIBER", null, new DummyVoltDBClient(voltServer.getClient()));

        Assert.assertTrue(result.isEmpty());
    }

    @SuppressWarnings("unused")
    private Object[][] dataProviderFor_getUsage_should_throw_OperationFailedException_On_Failure() {
        return new Object[][] {
            {
                    ClientResponse.CONNECTION_LOST, "Connection is lost"
            },
            {
                    ClientResponse.SERVER_UNAVAILABLE, "Service is not available"
            },
            {
                    ClientResponse.CONNECTION_TIMEOUT, "Connection timeout"
            },
            {
                    ClientResponse.OPERATIONAL_FAILURE, "INTERNAL ERROR, Error Code: -9"
            }
        };
    }
    @Test
    @Parameters(method = "dataProviderFor_getUsage_should_throw_OperationFailedException_On_Failure")
    public void getUsageThrowsOperationFailedExceptionOnFailure(byte statusToThrow, String expectedMessage) throws Exception {
        ClientResponse clientResponse = new ClientResponseImpl(statusToThrow, new VoltTable[0], expectedMessage);
        DummyVoltDBClient spiedVoltDBClient = spy(new DummyVoltDBClient(voltServer.getClient()));
        doReturn(clientResponse).when(spiedVoltDBClient).callProcedure(anyString(), anyString());

        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage(expectedMessage);

        DummySPRInfo sprInfo = new DummySPRInfo();
        sprInfo.setSubscriberIdentity(DEFAULT_SUBSCRIBER_ID);
        umOperation.getUsage(sprInfo,spiedVoltDBClient);
    }

    @Test
    public void getUsageThrowsOperationFailedExceptionOnIOErrorsOfProcedureCall() throws Exception {
        DummyVoltDBClient spiedVoltDBClient = spy(new DummyVoltDBClient(voltServer.getClient()));
        doThrow(IOException.class).when(spiedVoltDBClient).callProcedure(anyString(), anyString());

        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Error while fetching usage for Subscriber ID: "+
                        DEFAULT_SUBSCRIBER_ID+  ". Reason: ");

        DummySPRInfo sprInfo = new DummySPRInfo();
        sprInfo.setSubscriberIdentity(DEFAULT_SUBSCRIBER_ID);
        umOperation.getUsage(sprInfo,spiedVoltDBClient);
    }

    @Test
    public void getUsageGeneratesQueryTimeOutAlert() throws Exception {
        TimeSourceChain timeSource = new TimeSourceChain(3, System.currentTimeMillis(), AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10);
        VoltUMOperation umOperation = new VoltUMOperation(alertListener, policyRepository, timeSource, null, null);

        try {
            umOperation.getUsage(DEFAULT_SUBSCRIBER_ID, null, new DummyVoltDBClient(voltServer.getClient()));
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(anyString(), Matchers.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), anyString(), anyString());
        }
    }

    @Test
    public void getUsageBySPRInfoGeneratesQueryTimeOutAlert() throws Exception {
        TimeSourceChain timeSource = new TimeSourceChain(3, System.currentTimeMillis(), AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10);
        VoltUMOperation umOperation = new VoltUMOperation(alertListener, policyRepository, timeSource, null, null);

        try {
            DummySPRInfo sprInfo = new DummySPRInfo();
            sprInfo.setSubscriberIdentity(DEFAULT_SUBSCRIBER_ID);
            umOperation.getUsage(sprInfo, new DummyVoltDBClient(voltServer.getClient()));
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(anyString(), Matchers.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), anyString(), anyString());
        }
    }

    @Test
    public void deleteBasePackagUsageShouldDeleteUsageOfSubscriberBasePackage() throws Exception {
        String packageId = "default_id";
        helper.createBasePackageAndAddUsage(DEFAULT_SUBSCRIBER_ID, packageId, "default_po_id" );

        int deletedRows = umOperation.deleteBasePackageUsage(DEFAULT_SUBSCRIBER_ID, packageId, new DummyVoltDBClient(voltServer.getClient()));
        helper.removeUsage(DEFAULT_SUBSCRIBER_ID, packageId );

        assertTrue(deletedRows > 0);
    }

    @Test
    public void deleteBasePackageUsageShouldNotDeleteAnyUsageWhenUsageWithPackageDoesNotExist() throws Exception {

        int deletedRows = umOperation.deleteBasePackageUsage(DEFAULT_SUBSCRIBER_ID, "default_id" + 2, new DummyVoltDBClient(voltServer.getClient()));

        assertTrue(deletedRows == 0);
    }

    @Test
    public void deleteBasePackageUsageShouldReturnCounterOfDeltedRowsAsExpected() throws Exception {
        String packageId = "default_id";
        helper.createBasePackageAndAddUsage(DEFAULT_SUBSCRIBER_ID, packageId, "default_po_id" );

        int deletedRows = umOperation.deleteBasePackageUsage(DEFAULT_SUBSCRIBER_ID, packageId, new DummyVoltDBClient(voltServer.getClient()));

        Assert.assertSame(1, deletedRows);
    }

    @Test
    public void deleteBasePackageUsageGeneratesQueryTimeOutAlert() throws Exception {
        TimeSourceChain timeSource = new TimeSourceChain(3, System.currentTimeMillis(), AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10);
        VoltUMOperation umOperation = new VoltUMOperation(alertListener, policyRepository, timeSource, null, null);

        try {
            String packageId = "default_id";
            helper.createBasePackageAndAddUsage(DEFAULT_SUBSCRIBER_ID, packageId, "default_po_id" );
            umOperation.deleteBasePackageUsage(DEFAULT_SUBSCRIBER_ID, packageId, new DummyVoltDBClient(voltServer.getClient()));
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(anyString(), Matchers.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), anyString(), anyString());
        }
    }

    @Test
    public void deleteBaseAndPromotionalPackageUsageShouldReturnCounterOfDeltedRowsAsExpected() throws Exception {
        String packageId = "default_id";
        helper.createBasePackageAndAddUsage(DEFAULT_SUBSCRIBER_ID, packageId, "default_po_id" );

        int deletedRows = umOperation.deleteBaseAndPromotionalPackageUsage(DEFAULT_SUBSCRIBER_ID, new DummyVoltDBClient(voltServer.getClient()));

        Assert.assertSame(3, deletedRows);
    }

    @Test
    public void deleteBaseAndPromotionalPackageUsageGeneratesQueryTimeOutAlert() throws Exception {
        TimeSourceChain timeSource = new TimeSourceChain(3, System.currentTimeMillis(), AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10);
        VoltUMOperation umOperation = new VoltUMOperation(alertListener, policyRepository, timeSource, null, null);

        try {
            umOperation.deleteBaseAndPromotionalPackageUsage(DEFAULT_SUBSCRIBER_ID, new DummyVoltDBClient(voltServer.getClient()));
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(anyString(), Matchers.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), anyString(), anyString());
        }
    }

    @Test
    public void insertUsageForBasePackage() throws Exception {

        String subscriberId = UUID.randomUUID().toString();
        String packageId = UUID.randomUUID().toString();
        String productOfferId = UUID.randomUUID().toString();
        List<SubscriberUsage> subscriberUsage = helper.createSubscriberUsage(helper.createSubscriberUsageData(subscriberId, packageId, productOfferId));

        umOperation.insert(subscriberId, subscriberUsage, new DummyVoltDBClient(voltServer.getClient()));

        assertTrue(umOperation.getUsage(subscriberId, null, new DummyVoltDBClient(voltServer.getClient())) != null);
    }

    @Test
    public void insertUsageGeneratesQueryTimeOutAlert() throws Exception {
        TimeSourceChain timeSource = new TimeSourceChain(3, System.currentTimeMillis(), AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10);
        VoltUMOperation umOperation = new VoltUMOperation(alertListener, policyRepository, timeSource, null, null);

        try {
            String subscriberId = UUID.randomUUID().toString();
            String packageId = UUID.randomUUID().toString();
            String productOfferId = UUID.randomUUID().toString();
            List<SubscriberUsage> subscriberUsage = helper.createSubscriberUsage(helper.createSubscriberUsageData(subscriberId, packageId, productOfferId));
            umOperation.insert(subscriberId, subscriberUsage, new DummyVoltDBClient(voltServer.getClient()));
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(anyString(), Matchers.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), anyString(), anyString());
        }
    }

    @Test
    public void replaceUsageForBasePackage() throws Exception {

        SubscriberUsage subscriberUsage = helper.getUsageForSubscriber(DEFAULT_SUBSCRIBER_ID).get("package1").get("100:101");
        subscriberUsage = helper.replaceUsage(subscriberUsage);

        umOperation.replace(DEFAULT_SUBSCRIBER_ID, Arrays.asList(subscriberUsage), new DummyVoltDBClient(voltServer.getClient()));

        ReflectionAssert.assertReflectionEquals(subscriberUsage, umOperation.getUsage(DEFAULT_SUBSCRIBER_ID, null, new DummyVoltDBClient(voltServer.getClient())).get("package1").get("100:101"));
    }

    @Test
    public void replaceUsageGeneratesQueryTimeOutAlert() throws Exception {
        TimeSourceChain timeSource = new TimeSourceChain(3, System.currentTimeMillis(), AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10);
        VoltUMOperation umOperation = new VoltUMOperation(alertListener, policyRepository, timeSource, null, null);

        try {
            String subscriberId = UUID.randomUUID().toString();
            String packageId = UUID.randomUUID().toString();
            String productOfferId = UUID.randomUUID().toString();
            List<SubscriberUsage> subscriberUsage = helper.createSubscriberUsage(helper.createSubscriberUsageData(subscriberId, packageId, productOfferId));
            umOperation.replace(DEFAULT_SUBSCRIBER_ID, subscriberUsage, new DummyVoltDBClient(voltServer.getClient()));
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(anyString(), Matchers.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), anyString(), anyString());
        }
    }

    @Test
    public void addToExistingUsageForBasePackage() throws Exception {
        SubscriberUsage subscriberUsage = helper.getUsageForSubscriber(DEFAULT_SUBSCRIBER_ID).get("package1").get("100:101");
        subscriberUsage.setDailyTotal(10);
        subscriberUsage.setWeeklyTotal(10);
        subscriberUsage.setBillingCycleTotal(10);
        subscriberUsage.setCustomTotal(10);

        umOperation.addToExisting(DEFAULT_SUBSCRIBER_ID, Arrays.asList(subscriberUsage), new DummyVoltDBClient(voltServer.getClient()));

        SubscriberUsage expectedSubscriberUsage = new SubscriberUsage.SubscriberUsageBuilder(subscriberUsage.getId(), subscriberUsage.getSubscriberIdentity(), subscriberUsage.getServiceId(), subscriberUsage.getQuotaProfileId(), subscriberUsage.getPackageId(),subscriberUsage.getProductOfferId())
                .withSubscriptionId(null)
                .withAllTypeUsage(1010, 2000 , 2000 , 2000)
                .withDailyResetTime(subscriberUsage.getDailyResetTime())
                .withWeeklyResetTime(subscriberUsage.getWeeklyResetTime())
                .withBillingCycleResetTime(subscriberUsage.getBillingCycleResetTime())
                .withCustomResetTime(subscriberUsage.getCustomResetTime())
                .build();
        ReflectionAssert.assertReflectionEquals(expectedSubscriberUsage, umOperation.getUsage(DEFAULT_SUBSCRIBER_ID, null, new DummyVoltDBClient(voltServer.getClient())).get("package1").get("100:101"));
    }

    @Test
    public void addToExistingUsageGeneratesQueryTimeOutAlert() throws Exception {
        TimeSourceChain timeSource = new TimeSourceChain(3, System.currentTimeMillis(), AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10);
        VoltUMOperation umOperation = new VoltUMOperation(alertListener, policyRepository, timeSource, null, null);

        try {
            String subscriberId = UUID.randomUUID().toString();
            String packageId = UUID.randomUUID().toString();
            String productOfferId = UUID.randomUUID().toString();
            List<SubscriberUsage> subscriberUsage = helper.createSubscriberUsage(helper.createSubscriberUsageData(subscriberId, packageId, productOfferId));
            umOperation.addToExisting(DEFAULT_SUBSCRIBER_ID, subscriberUsage, new DummyVoltDBClient(voltServer.getClient()));
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(anyString(), Matchers.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), anyString(), anyString());
        }
    }

    @Test
    public void resetUsageForBasePackage() throws Exception {
        String packageId = "package1";
        when(policyRepository.getPkgDataById(packageId)).thenReturn(helper.getBasePackage(packageId));

        umOperation.resetUsage(DEFAULT_SUBSCRIBER_ID, packageId, new DummyVoltDBClient(voltServer.getClient()));

        assertTrue(umOperation.getUsage(DEFAULT_SUBSCRIBER_ID, null, new DummyVoltDBClient(voltServer.getClient())).get(packageId).get("100:101").getDailyTotal() == 0);
    }

    @Test
    public void resetUsageGeneratesQueryTimeOutAlert() throws Exception {
        TimeSourceChain timeSource = new TimeSourceChain(3, System.currentTimeMillis(), AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10);
        VoltUMOperation umOperation = new VoltUMOperation(alertListener, policyRepository, timeSource, null, null);

        try {
            String packageId = "package1";
            when(policyRepository.getPkgDataById(packageId)).thenReturn(helper.getBasePackage(packageId));
            umOperation.resetUsage(DEFAULT_SUBSCRIBER_ID, packageId, new DummyVoltDBClient(voltServer.getClient()));
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(anyString(), Matchers.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), anyString(), anyString());
        }
    }

    @Test
    public void resetBillingCycleUsageForBasePackage() throws Exception {
        DummyVoltDBClient spiedVoltDBClient = spy(new DummyVoltDBClient(voltServer.getClient()));
        SubscriberUsage subscriberUsage = helper.getUsageForSubscriber(DEFAULT_SUBSCRIBER_ID).get("package1").get("100:101");
        when(productOfferStore.byId(subscriberUsage.getProductOfferId())).thenReturn(productOffer);
        VoltUMOperation umOperation = new VoltUMOperation(alertListener, policyRepository, new FixedTimeSource(), null, null);

        umOperation.resetBillingCycle(subscriberUsage.getSubscriberIdentity(), null, subscriberUsage.getProductOfferId(), subscriberUsage.getBillingCycleResetTime(), null, null, null, null, spiedVoltDBClient);

        verify(spiedVoltDBClient, atLeastOnce()).callProcedure(anyString(), any(Timestamp.class), any(String[].class));
    }

    @Test
    public void resetBillingCycleUsageGeneratesQueryTimeOutAlert() throws Exception {
        TimeSourceChain timeSource = new TimeSourceChain(3, System.currentTimeMillis(), AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10);
        SubscriberUsage subscriberUsage = helper.getUsageForSubscriber(DEFAULT_SUBSCRIBER_ID).get("package1").get("100:101");
        when(productOfferStore.byId(subscriberUsage.getProductOfferId())).thenReturn(productOffer);
        VoltUMOperation umOperation = new VoltUMOperation(alertListener, policyRepository, timeSource, null, null);

        try {
            umOperation.resetBillingCycle(subscriberUsage.getSubscriberIdentity(), null, subscriberUsage.getProductOfferId(), subscriberUsage.getBillingCycleResetTime(), null, null, null, null, new DummyVoltDBClient(voltServer.getClient()));
        } catch (OperationFailedException e) {
            verify(alertListener, atLeastOnce())
                    .generateSystemAlert(anyString(), Matchers.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), anyString(), anyString());
        }
    }

    @Test
    public void scheduleForUsageDeleteForBasePackageCallsStoredProcedure() throws Exception {
        DummyVoltDBClient spiedVoltDBClient = spy(new DummyVoltDBClient(voltServer.getClient()));
        SubscriberUsage subscriberUsage = helper.getUsageForSubscriber(DEFAULT_SUBSCRIBER_ID).get("package1").get("100:101");
        when(productOfferStore.byId(subscriberUsage.getProductOfferId())).thenReturn(productOffer);
        VoltUMOperation umOperation = new VoltUMOperation(alertListener, policyRepository, new FixedTimeSource(), null, null);

        umOperation.scheduleForUsageDelete(subscriberUsage.getSubscriberIdentity(), null, subscriberUsage.getProductOfferId(), null, null, null, null, spiedVoltDBClient);

        verify(spiedVoltDBClient, atLeastOnce()).callProcedure(anyString(), any(Timestamp.class), any(String[].class));
    }

    @Test
    public void getBalanceForBasePackageThrowsOperationFailedExeptionIfPackageNotFound() throws Exception {
        DummySPRInfo sprInfo = new DummySPRInfo();
        sprInfo.setSubscriberIdentity(DEFAULT_SUBSCRIBER_ID);

        expectedException.expect(OperationFailedException.class);

        umOperation.getBalance(sprInfo, "package5", null, null, new DummyVoltDBClient(voltServer.getClient()));
    }

    @Test
    public void getBalanceForBasePackageGivesSubscriptionInformation() throws Exception {
        DummySPRInfo sprInfo = new DummySPRInfo();
        sprInfo.setSubscriberIdentity(DEFAULT_SUBSCRIBER_ID);
        String packageId = "package1";
        sprInfo.setProductOffer("100");
        when(policyRepository.getProductOffer().byName(sprInfo.getProductOffer())).thenReturn(productOffer);
        when(productOffer.getDataServicePkgData()).thenReturn(helper.getBasePackage(packageId));
        when(policyRepository.getPkgDataById(packageId)).thenReturn(helper.getBasePackage(packageId));

        List<SubscriptionInformation> subscriptionInformations = umOperation.getBalance(sprInfo, packageId, null, null, new DummyVoltDBClient(voltServer.getClient()));

        assertTrue(Collectionz.isNullOrEmpty(subscriptionInformations) == false);
    }

    @After
    public void tearDown() {
        voltServer.runDDLFromString("TRUNCATE TABLE TBLT_USAGE");
    }

    @AfterClass
    public static void afterClass() {
        voltServer.shutdown();
    }

    private class VoltUMOperationTestHelper {

        private DummyVoltDBClient VoltDBClient;
        private Map<String, Map<String, Map<String, SubscriberUsage>>> usageInfoByUserId;
        private Map<String, LinkedHashMap<String,Subscription>> addOnSubscriptions;
        private Map<String, BasePackage> idToBasePackage;
        private Calendar weeklyResetTime;
        private Calendar dailyResetTime;

        public VoltUMOperationTestHelper(DummyVoltDBClient voltDBClient) {
            this.VoltDBClient = voltDBClient;
            this.usageInfoByUserId = new HashMap<>(10);
            this.addOnSubscriptions = new HashMap<>();
            this.idToBasePackage = new HashMap<>();
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

        public SubscriberUsage replaceUsage(SubscriberUsage subscriberUsage){
            Random random = new Random();
            subscriberUsage.setDailyTotal(random.nextLong());
            subscriberUsage.setWeeklyTotal(random.nextLong());

            return subscriberUsage;
        }

        public void removeUsage(String defaultSubscriberId, String packageId) {
            usageInfoByUserId.get(defaultSubscriberId).remove(packageId);
        }

        public void insertUsageRecord(SubscriberUsageData record, Map<String, Subscription> addOnSubscriptions) throws Exception {

            record.setDailyResetTimestamp(dailyResetTime.getTimeInMillis());
            record.setWeeklyResetTimestamp(weeklyResetTime.getTimeInMillis());

            Map<String, Map<String, SubscriberUsage>> subscriptionOrPackageWiseUsage = usageInfoByUserId.get(record.getSubscriberIdentity());
            if (subscriptionOrPackageWiseUsage == null) {
                subscriptionOrPackageWiseUsage = new HashMap<>(10);
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
                serviceWiseUsage = new HashMap<>();
                subscriptionOrPackageWiseUsage.put(key, serviceWiseUsage);
            }


            if(addOnSubscriptions != null && addOnSubscriptions.isEmpty() == false) {
                LinkedHashMap<String,Subscription> addOnSubscriptions2 = this.addOnSubscriptions.get(record.getSubscriberIdentity());
                if(addOnSubscriptions2 == null){
                    addOnSubscriptions2 = new LinkedHashMap<>();
                    this.addOnSubscriptions.put(record.getSubscriberIdentity(), addOnSubscriptions2);
                }


                addOnSubscriptions2.putAll(addOnSubscriptions);
            }


            serviceWiseUsage.put(subscriberUsage.getQuotaProfileId() + CommonConstants.USAGE_KEY_SEPARATOR + subscriberUsage.getServiceId(), subscriberUsage);
        }

        public void insertUsageRecord(List<SubscriberUsageData> subscriberUsageInsertList, Map<String, Subscription> addOnSubscriptions) throws Exception {

            for (SubscriberUsageData data : subscriberUsageInsertList) {
                insertUsageRecord(data, addOnSubscriptions);
            }
        }


        public Map<String, Map<String, SubscriberUsage>> getUsageForSubscriber(String subscriberIdentity) {
            if (usageInfoByUserId.containsKey(subscriberIdentity)) {
                return usageInfoByUserId.get(subscriberIdentity);
            } else {
                return Collections.emptyMap();
            }
        }

        private List<SubscriberUsage> createSubscriberUsage(List<SubscriberUsageData> subsriberUsageDatas) {

            List<SubscriberUsage> subscriberUsageList = new ArrayList<SubscriberUsage>();

            if (subsriberUsageDatas != null) {
                for (SubscriberUsageData subscriberUsageData : subsriberUsageDatas) {
                    subscriberUsageList.add(subscriberUsageData.newSubscriberUsage());
                }
            }
            return subscriberUsageList;
        }

        public void dropTables() throws Exception {
            voltServer.runDDLFromString(SubscriberUsageData.dropTableQuery());
            voltServer.runDDLFromString(ResetUsageRequiredData.dropTableQuery());
            usageInfoByUserId.clear();

            getLogger().debug(this.getClass().getSimpleName(), "Tables Dropped");
        }

        public void createBasePackage(String id) {

            QuotaProfile quotaProfile = new QuotaProfileFactory.Builder("100")
                    .withHSQLevelRandomQuotaFor("1")
                    .build();

            QoSProfile qoSProfile = QosProfileFactory.createQosProfile().withQuotaProfile(quotaProfile).build();

            BasePackage basePackage = new BasePackageFactory.BasePackageBuilder(null, id, id)
                    .withQoSProfiles(Arrays.asList(qoSProfile))
                    .withAvailabilityStatus(PkgStatus.ACTIVE)
                    .withQuotaProfileType(QuotaProfileType.USAGE_METERING_BASED)
                    .build();

            idToBasePackage.put(id, basePackage);
        }

        private BasePackage getBasePackage(String id){
            return idToBasePackage.get(id);
        }

        public void createBasePackageAndAddUsage(String subscriberId, String packageId, String productOfferId) throws Exception {
            createBasePackage(packageId);
            List<SubscriberUsageData> subscriberUsageInsertList = createSubscriberUsageData(subscriberId, packageId, productOfferId);
            insertUsageRecord(subscriberUsageInsertList, null);
            for (SubscriberUsageData subscriberUsageData : subscriberUsageInsertList) {
                voltServer.runDDLFromString(subscriberUsageData.insertQuery());
            }
        }

        private List<SubscriberUsageData> createSubscriberUsageData(String subscriberId, String packageId, String productOfferId) {
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

            return Arrays.asList(record4);
        }
    }

    private void cerateUsage() throws Exception {
        LogManager.getLogger().debug("test", "creating Usage");

        helper.createBasePackageAndAddUsage(DEFAULT_SUBSCRIBER_ID, "package1", "100");
        helper.createBasePackageAndAddUsage(DEFAULT_SUBSCRIBER_ID, "package2", "102");

        LogManager.getLogger().debug("test", "Usage created");
    }
}
