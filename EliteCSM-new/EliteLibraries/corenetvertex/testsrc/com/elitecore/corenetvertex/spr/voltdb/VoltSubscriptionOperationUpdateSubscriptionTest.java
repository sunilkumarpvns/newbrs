package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.pd.bod.BoDData;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.DummyPolicyRepository;
import com.elitecore.corenetvertex.pm.bod.BoDFactory;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.bod.BoDPackageFactory;
import com.elitecore.corenetvertex.pm.bod.BodDataDummyBuilder;
import com.elitecore.corenetvertex.pm.factory.AddOnPackageFactory;
import com.elitecore.corenetvertex.pm.factory.ProductOfferDataFactory;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.voltdb.InProcessVoltDBServer;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

@Ignore
@RunWith(JUnitParamsRunner.class)
public class VoltSubscriptionOperationUpdateSubscriptionTest {
    private static InProcessVoltDBServer voltServer;
    private String subscriberIdentity = "101";
    @Mock
    private AlertListener alertListener;
    private VoltSubscriptionOperationUpdateSubscriptionTest.VoltSubscriptionDBHelper helper;
    private DummyPolicyRepository policyRepository;

    private DummyVoltDBClient dummyVoltDBClient;

    private VoltUMOperationTest.DummySPRInfo sprInfo;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
        dummyVoltDBClient = new DummyVoltDBClient(voltServer.getClient());

        sprInfo = new VoltUMOperationTest.DummySPRInfo();

        sprInfo.setSubscriberIdentity(subscriberIdentity);
        helper = new VoltSubscriptionDBHelper();

        policyRepository = new DummyPolicyRepository();
    }

    @Test
    public void test_should_throw_OperationFailedException_when_subscriptionState_not_provided() throws Exception {
        VoltSubscriptionOperation subscriptionOperation = new VoltSubscriptionOperation(alertListener, policyRepository, new FixedTimeSource(), null, null);

        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Subscription status value not found");
        try {
            String subscriptionId = "101";
            Integer subscriptionStatusValue = null;
            subscriptionOperation.updateSubscription(createSprInfo("Subscriber"), subscriptionId , subscriptionStatusValue , null,
                    null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null, dummyVoltDBClient);
        } catch (OperationFailedException e) {
            assertSame(ResultCode.INVALID_INPUT_PARAMETER, e.getErrorCode());
            throw e;
        }

        fail("should throw OperationFailedException");
    }

    @Test
    @Parameters(value = {"-1", "9"})
    public void test_should_throw_OperationFailedException_when_invalid_subscriptionState_provided(
            Integer subscriptionStatusValue) throws Exception {
        VoltSubscriptionOperation subscriptionOperation = new VoltSubscriptionOperation(alertListener, policyRepository, new FixedTimeSource(), null, null);

        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Invalid subscription status value: " + subscriptionStatusValue + " received");
        try {
            String subscriptionId = "101";
            subscriptionOperation.updateSubscription(createSprInfo("Subscriber"), subscriptionId , subscriptionStatusValue , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY,
                    null, dummyVoltDBClient);
        } catch (OperationFailedException e) {
            assertSame(ResultCode.INVALID_INPUT_PARAMETER, e.getErrorCode());
            throw e;
        }

        fail("should throw OperationFailedException");
    }

    @Test
    public void test_when_subscription_not_found_with_provided_subscriptionId_then_it_should_throw_OperationFailedException() throws Exception {
        VoltSubscriptionOperation subscriptionOperation = new VoltSubscriptionOperation(alertListener, policyRepository, new FixedTimeSource(), null, null);
        String subscriptionId = "101";

        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Active subscription not found with ID: " + subscriptionId);

        try {
            Integer subscriptionStatusValue = SubscriptionState.UNSUBSCRIBED.state;
            subscriptionOperation.updateSubscription(createSprInfo("Subscriber"), subscriptionId , subscriptionStatusValue , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY,
                     null, dummyVoltDBClient);
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
        VoltSubscriptionOperation subscriptionOperation = new VoltSubscriptionOperation(alertListener, policyRepository, TimeSource.systemTimeSource(), null, null);
        String subscriptionId = "101";
        String subscriber = "Subscriber";
        Timestamp endTime = new Timestamp(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
        Integer newState = SubscriptionState.UNSUBSCRIBED.state;

        ProductOffer productOffer = createProductOffer(UUID.randomUUID().toString());

        SubscriptionData data1 = new SubscriptionData(subscriber, productOffer.getDataServicePkgData().getId(), null, endTime.toString(), String.valueOf(state), null, subscriptionId,
                null,productOffer.getId());
        data1.setAddOn(productOffer.getDataServicePkgData());

        helper.insertAddonRecord(data1);



        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Unable to update subscription for subscription ID: 101. Reason: Invalid old subscription status("
                + SubscriptionState.fromValue(state) + ")");

        try {
            subscriptionOperation.updateSubscription(createSprInfo(subscriber), subscriptionId , newState , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null, dummyVoltDBClient);
        } catch (OperationFailedException e) {
            assertSame(ResultCode.INVALID_INPUT_PARAMETER, e.getErrorCode());
            throw e;
        }

        fail("should throw OperationFailedException");
    }

    private SPRInfo createSprInfo(String subscriberIdentity){
        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(subscriberIdentity);
        return sprInfo;
    }

    private ProductOffer createProductOffer(String addOnId) {

        AddOn addOn = Mockito.spy(AddOnPackageFactory.create(addOnId, "name-" + addOnId));
        policyRepository.addAddOn(addOn);
        ProductOffer productOffer = getProductOffer(addOn);
        policyRepository.addProductOffer(productOffer);
        return productOffer;
    }

    private ProductOffer getProductOffer(AddOn addOn) {
        ProductOfferData productOfferData = new ProductOfferDataFactory()
                .withId("1").withName("name").withStatus("Random")
                .withMode("LIVE").withType("ADDON")
                .withDataServicePkgId(addOn.getId()).build();
        return new ProductOffer(
                productOfferData.getId(), productOfferData.getName(), productOfferData.getDescription(),
                PkgType.ADDON, PkgMode.LIVE, 30, ValidityPeriodUnit.DAY, productOfferData.getSubscriptionPrice() != null ? productOfferData.getSubscriptionPrice() : 0d,
                0d,
                PkgStatus.ACTIVE, null,null,
                productOfferData.getDataServicePkgId(), productOfferData.getGroupList(), productOfferData.getAvailabilityStartDate(),
                productOfferData.getAvailabilityEndDate(), PolicyStatus.SUCCESS, null, null,
                (Objects.nonNull(productOfferData.isFnFOffer()) && productOfferData.isFnFOffer().booleanValue()),
                productOfferData.getParam1(), productOfferData.getParam2(), policyRepository,
                null,null,new HashMap<>(),productOfferData.getCurrency()
        );
    }


    @Test
    @Parameters(value={"0","1","3","4","6","7","8"})
    public void test_when_old_state_is_STARTED_but_new_state_has_not_UNSUBSCRIBED_OR_STARTED_then_it_should_throw_OperationFailedException(
            Integer newState
    ) throws Exception {
        VoltSubscriptionOperation subscriptionOperation = new VoltSubscriptionOperation(alertListener, policyRepository, new FixedTimeSource(), null, null);
        String subscriptionId = "101";
        String subscriber = "Subscriber";
        Timestamp startTime = new Timestamp(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1));
        Timestamp endTime = new Timestamp(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
        Integer oldState = SubscriptionState.STARTED.state;

        ProductOffer productOffer = createProductOffer(UUID.randomUUID().toString());

        SubscriptionData data1 = new SubscriptionData(subscriber, productOffer.getDataServicePkgData().getId(), startTime.toString(), endTime.toString(), String.valueOf(oldState), null, subscriptionId, null,productOffer.getId());
        data1.setAddOn(productOffer.getDataServicePkgData());
        helper.insertAddonRecord(data1);


        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Invalid subscription status(" + SubscriptionState.fromValue(newState) + ") received. Old Status: "
                + SubscriptionState.fromValue(oldState));

        try {
            subscriptionOperation.updateSubscription(createSprInfo(subscriber), subscriptionId , newState , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null, dummyVoltDBClient);
        } catch (OperationFailedException e) {
            assertSame(ResultCode.INVALID_INPUT_PARAMETER, e.getErrorCode());
            throw e;
        }

        fail("should throw OperationFailedException");
    }

    @Test
    public void test_when_existing_subscription_is_expired_then_it_should_throw_OperationFailedException() throws Exception {
        VoltSubscriptionOperation subscriptionOperation = new VoltSubscriptionOperation(alertListener, policyRepository, new FixedTimeSource(), null, null);
        String subscriptionId = "101";
        Timestamp endTime = new Timestamp(System.currentTimeMillis());
        Integer oldState = SubscriptionState.STARTED.state;
        Integer newState = SubscriptionState.UNSUBSCRIBED.state;

        ProductOffer productOffer = createProductOffer(UUID.randomUUID().toString());

        SubscriptionData data1 = new SubscriptionData(subscriptionId, productOffer.getDataServicePkgData().getId(), null, endTime.toString(),
                String.valueOf(oldState), null, subscriptionId, null,productOffer.getId());
        data1.setAddOn(productOffer.getDataServicePkgData());
        helper.insertAddonRecord(data1);

        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Active subscription not found with ID: " + subscriptionId);

        try {
            subscriptionOperation.updateSubscription(createSprInfo("Subscriber"), subscriptionId , newState , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null, dummyVoltDBClient);
        } catch (OperationFailedException e) {
            assertSame(ResultCode.NOT_FOUND, e.getErrorCode());
            throw e;
        }

        fail("should throw OperationFailedException");
    }

    @Test
    public void test_when_addon_not_found_for_existing_subscription_then_it_should_throw_OperationFailedException() throws Exception {
        VoltSubscriptionOperation subscriptionOperation = new VoltSubscriptionOperation(alertListener, policyRepository, new FixedTimeSource(), null, null);
        String subscriptionId = "101";
        Timestamp endTime = new Timestamp(System.currentTimeMillis());
        Integer oldState = SubscriptionState.STARTED.state;
        Integer newState = SubscriptionState.UNSUBSCRIBED.state;

        AddOn addOn = AddOnPackageFactory.create(UUID.randomUUID().toString(), "name-"+ UUID.randomUUID().toString());

        policyRepository.addAddOn(addOn);

        SubscriptionData data1 = new SubscriptionData(subscriptionId, addOn.getId(), null, endTime.toString(),
                String.valueOf(oldState), null, subscriptionId, null,null);
        data1.setAddOn(addOn);

        helper.insertAddonRecord(data1);

        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Active subscription not found with ID: " + subscriptionId);

        try {
            subscriptionOperation.updateSubscription(createSprInfo("Subscriber"), subscriptionId , newState , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null, dummyVoltDBClient);
        } catch (OperationFailedException e) {
            assertSame(ResultCode.NOT_FOUND, e.getErrorCode());
            throw e;
        }

        fail("should throw OperationFailedException");
    }

    @Test
    public void updateSubscriptionThrowsOperationFailedExceptionWhenBoDPackageDoesNotExistForSubscription() throws Exception {
        VoltSubscriptionOperation subscriptionOperation = new VoltSubscriptionOperation(alertListener, policyRepository, new FixedTimeSource(), null, null);
        String subscriptionId = "8965";
        Timestamp endTime = new Timestamp(System.currentTimeMillis());
        Integer oldState = SubscriptionState.STARTED.state;
        Integer newState = SubscriptionState.UNSUBSCRIBED.state;

        BoDPackage boDPackage = createBoDPackage();

        SPRInfo sprInfo = createSprInfo("4569752");
        SubscriptionData bodSubscriptionData = new SubscriptionData(sprInfo.getSubscriberIdentity(), boDPackage.getId(), null, endTime.toString(),
                String.valueOf(oldState), null, subscriptionId, null,null);
        bodSubscriptionData.setBod(boDPackage);

        helper.insertBoDRecord(bodSubscriptionData);

        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Active subscription not found with ID: " + subscriptionId);

        subscriptionOperation.updateSubscription(sprInfo, subscriptionId , newState , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null, dummyVoltDBClient);
    }

    private BoDPackage createBoDPackage() {
        BoDFactory boDFactory = new BoDFactory();
        BoDPackageFactory boDPackageFactory = new BoDPackageFactory(boDFactory, DeploymentMode.OCS);
        BoDData boDData = BodDataDummyBuilder.createBoDDataWithAllValues("Test");
        return boDPackageFactory.create(boDData);
    }

    @Test
    public void updateBoDSubscriptionWhenBoDPackageExistsForSubscription() throws Exception {
        Long currentTime = Calendar.getInstance().getTimeInMillis();
        VoltSubscriptionOperation subscriptionOperation = new VoltSubscriptionOperation(alertListener, policyRepository, new FixedTimeSource(currentTime), null, null);
        String subscriptionId = "852369741";
        Timestamp endTime = new Timestamp(currentTime+50000);
        Integer state = SubscriptionState.STARTED.state;

        BoDPackage boDPackage = createBoDPackage();

        policyRepository.addBoDPkg(boDPackage);

        SPRInfo sprInfo = createSprInfo("4848");
        SubscriptionData bodSubscriptionData = new SubscriptionData(sprInfo.getSubscriberIdentity(), boDPackage.getId(), new Timestamp(0).toString(), endTime.toString(),
                String.valueOf(state), null, subscriptionId, null,null);
        bodSubscriptionData.setBod(boDPackage);

        helper.insertBoDRecord(bodSubscriptionData);

        Long subscriptionStarttime = currentTime - 72000000000l;
        Long subscriptionEndtime = currentTime + 7200000;

        subscriptionOperation.updateSubscription(sprInfo, subscriptionId , state , subscriptionStarttime, subscriptionEndtime, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null, dummyVoltDBClient);

        Subscription subscription = subscriptionOperation.getActiveSubscriptionBySubscriptionId(sprInfo.getSubscriberIdentity(),bodSubscriptionData.getSubscriptionId(), dummyVoltDBClient);
        Assert.assertEquals(new Timestamp(subscriptionStarttime),subscription.getStartTime());
        Assert.assertEquals(new Timestamp(subscriptionEndtime),subscription.getEndTime());
    }

    @Test
    public void test_when_existing_subscription_has_UNSUBSCRIBED_state_then_it_should_throw_OperationFailedException() throws Exception {
        VoltSubscriptionOperation subscriptionOperation = new VoltSubscriptionOperation(alertListener, policyRepository, new FixedTimeSource(), null, null);
        String subscriptionId = "101";
        Integer oldState = SubscriptionState.UNSUBSCRIBED.state;
        Integer newState = SubscriptionState.UNSUBSCRIBED.state;

        ProductOffer productOffer = createProductOffer(UUID.randomUUID().toString());

        SubscriptionData data1 = new SubscriptionData(subscriptionId, productOffer.getDataServicePkgData().getId(), null, null,
                String.valueOf(oldState), null, subscriptionId, null,productOffer.getId());
        data1.setAddOn(productOffer.getDataServicePkgData());

        helper.insertAddonRecord(data1);

        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Active subscription not found with ID: " + subscriptionId);

        try {
            subscriptionOperation.updateSubscription(createSprInfo("Subscriber"), subscriptionId , newState , null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, null, dummyVoltDBClient);
        } catch (OperationFailedException e) {
            assertSame(ResultCode.NOT_FOUND, e.getErrorCode());
            throw e;
        }

        fail("should throw OperationFailedException");
    }

    @After
    public void tearDown() {
        voltServer.runDDLFromPath("testsrc/resources/voltdb-cleanup-ddl.sql");
    }

    public static class VoltSubscriptionDBHelper {

        private Map<String, LinkedHashMap<String, Subscription>> addonSubscriptions;

        public VoltSubscriptionDBHelper() {
            this.addonSubscriptions = new HashMap<>();
        }

        protected void insertAddonRecord(SubscriptionData AddOnSubscriptionData) throws Exception {

            voltServer.runDDLFromString(AddOnSubscriptionData.insertQuery());

            LinkedHashMap<String, Subscription> subscribedList = addonSubscriptions.get(AddOnSubscriptionData.getSubscriberId());

            if (subscribedList == null) {
                subscribedList = new LinkedHashMap<>();
                addonSubscriptions.put(AddOnSubscriptionData.getSubscriberId(), subscribedList);
            }

            subscribedList.put(AddOnSubscriptionData.getSubscriptionId(), AddOnSubscriptionData.getAddonSubscription());
        }

        protected void insertBoDRecord(SubscriptionData bodSubscriptionData) throws Exception {

            voltServer.runDDLFromString(bodSubscriptionData.insertQuery());

            LinkedHashMap<String, Subscription> subscribedList = addonSubscriptions.get(bodSubscriptionData.getSubscriberId());

            if (subscribedList == null) {
                subscribedList = new LinkedHashMap<>();
                addonSubscriptions.put(bodSubscriptionData.getSubscriberId(), subscribedList);
            }

            subscribedList.put(bodSubscriptionData.getSubscriptionId(), bodSubscriptionData.getBoDSubscription());
        }
    }
}
