package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.factory.AddOnPackageFactory;
import com.elitecore.corenetvertex.pm.factory.ProductOfferDataFactory;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionData;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionComparatorMode;
import org.voltdb.InProcessVoltDBServer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@Ignore
@RunWith(JUnitParamsRunner.class)
public class VoltSubscriptionOperationGetAddOnSubscriptionTest {
    private static InProcessVoltDBServer voltServer;
    private static final String INVALID_STATUS_VALUE = "9";
    private static final String EXPIRED_STATUS = "5";

    private String subscriberIdentity = "101";
    @Mock private AlertListener alertListener;
    private VoltSubscriptionDBHelper helper;

    @Mock
    private PolicyRepository policyRepository;
    @Mock
    ProductOfferStore productOfferStore;

    private AddOn addOn;
    private ProductOffer productOffer;

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

        addOn = AddOnPackageFactory.create(UUID.randomUUID().toString(), "name" + UUID.randomUUID().toString());
        productOffer = getProductOffer(addOn);
        when(policyRepository.getProductOffer()).thenReturn(productOfferStore);

        when(policyRepository.getAddOnById(addOn.getId())).thenReturn(this.addOn);
        when(productOfferStore.byId(productOffer.getId())).thenReturn(this.productOffer);
        sprInfo = new VoltUMOperationTest.DummySPRInfo();

        sprInfo.setSubscriberIdentity(subscriberIdentity);
        helper = new VoltSubscriptionDBHelper();
        createSubscriptionRecords();
    }

    @Test
    @Parameters(value = { "101", "102", "103" })
    public void test_getAddonSubscription_should_give_list_of_AddonSubscription(String subscriberIdentity) throws Exception {
        VoltSubscriptionOperation subscriptionOperation = new VoltSubscriptionOperation(alertListener, policyRepository, new FixedTimeSource(), null, null);
        Map<String, Subscription> actualSubscriptions = subscriptionOperation.getSubscriptions(subscriberIdentity, dummyVoltDBClient);
        Map<String, Subscription> expectedAddonSubscription = helper.getAddonsForSubscriber(subscriberIdentity);

        assertReflectionEquals(expectedAddonSubscription, actualSubscriptions, ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    @Parameters(value = { "501" })
    public void test_getAddonSubscription_should_give_empty_list_when_no_subscription_found(String subscriberIdentity) throws Exception {
        VoltSubscriptionOperation subscriptionOperation = new VoltSubscriptionOperation(alertListener, policyRepository, new FixedTimeSource(), null, null);
        Map<String, Subscription> actualSubscriptions = subscriptionOperation.getSubscriptions(subscriberIdentity, dummyVoltDBClient);

        assertTrue(actualSubscriptions.isEmpty());
    }

    @Test
    @Parameters(value = { "501"})
    public void test_getAddonSubscriptionBySPRInfo_should_give_empty_list_when_no_subscription_found(String subscriberIdentity) throws Exception {
        VoltUMOperationTest.DummySPRInfo sprInfo = new VoltUMOperationTest.DummySPRInfo();
        sprInfo.setSubscriberIdentity(subscriberIdentity);
        VoltSubscriptionOperation subscriptionOperation = new VoltSubscriptionOperation(alertListener, policyRepository, new FixedTimeSource(), null, null);
        Map<String, Subscription> actualSubscriptions = subscriptionOperation.getSubscriptions(sprInfo, dummyVoltDBClient);
        assertTrue(actualSubscriptions.isEmpty());
    }

    @Test
    @Parameters(value = { EXPIRED_STATUS, INVALID_STATUS_VALUE })
    public void test_getAddonSubscriptions_should_not_give_addons_which_has_status_unsubscribed_or_invalid(String subscriptionState) throws Exception {
        String subscriberIdentity = "110";
        setUpFor_addon_with_invalid_status(subscriptionState, subscriberIdentity);

        VoltSubscriptionOperation subscriptionOperation = new VoltSubscriptionOperation(alertListener, policyRepository, new FixedTimeSource(), null, null);

        assertTrue(subscriptionOperation.getSubscriptions(subscriberIdentity, dummyVoltDBClient).isEmpty());
    }

    @Test
    @Parameters(value = { "2014-12-31 09:26:50.12", "2014-10-29 09:26:50.12", "2015-01-01 00:00:00.00", "2015-01-01 01:01:00.00", "" })
    public void test_getAddonSubscriptions_should_not_give_expired_addons(String expiryDate) throws Exception {
        if (expiryDate.isEmpty()) {
            expiryDate = null;
        }

        String subscriberIdentity = "110";

        setUpFor_addon_with_expired_time(expiryDate, subscriberIdentity);

        VoltSubscriptionOperation subscriptionOperation = new VoltSubscriptionOperation(alertListener, policyRepository, TimeSource.systemTimeSource() , null, null);

        assertTrue(subscriptionOperation.getSubscriptions(subscriberIdentity, dummyVoltDBClient).isEmpty());
    }

    @Test
    public void test_getAddonSubscription_should_give_subscription_with_resetdate_same_as_expirydate_if_resetdate_not_configured() throws Exception {
        String subscriberIdentity = "110";

        SubscriptionData data1 = new SubscriptionData(subscriberIdentity, addOn.getId(), "2014-10-29 09:26:50.12", "2020-10-29 09:26:50.12",
                "2", "1", "10", null,productOffer.getId());

        data1.setAddOn(addOn);

        helper.insertAddonRecord(data1);

        VoltSubscriptionOperation subscriptionOperation = new VoltSubscriptionOperation(alertListener, policyRepository, new FixedTimeSource(), null, null);

        Map<String, Subscription> actualSubscriptions = subscriptionOperation.getSubscriptions(subscriberIdentity, dummyVoltDBClient);
        Map<String, Subscription> expectedAddonSubscription = helper.getAddonsForSubscriber(subscriberIdentity);

        getLogger().debug("TEST", "expected: " + expectedAddonSubscription.get(0));

        assertLenientEquals(expectedAddonSubscription, actualSubscriptions);
    }

    private void setUpFor_addon_with_expired_time(String expiryDate, String subscriberIdentity) throws Exception {
        SubscriptionData data1 = new SubscriptionData(subscriberIdentity, addOn.getId(), "2014-10-29 09:26:50.12", expiryDate,
                "2", "1", "10", "2015-12-31 09:26:50.12",productOffer.getId());

        data1.setAddOn(addOn);

        helper.insertAddonRecord(data1);

    }

    private List<SubscriptionData> getAddonSubscritionData() {

        SubscriptionData record1 = new SubscriptionData("101", addOn.getId(), "2014-10-29 09:26:50.12", "2020-12-31 09:26:50.12",
                "2", "0", "1", "2020-12-31 09:26:50.12",productOffer.getId());

        SubscriptionData record2 = new SubscriptionData("102", addOn.getId(), "2014-10-29 09:26:50.12", "2020-12-31 09:26:50.12",
                "2", "0", "2", "2020-12-31 09:26:50.12",productOffer.getId());

        SubscriptionData record3 = new SubscriptionData("101", addOn.getId(), "2014-10-29 09:26:50.12", "2020-12-31 09:26:50.12",
                "2", "0", "3", "2020-12-31 09:26:50.12",productOffer.getId());

        record1.setAddOn(addOn);
        record2.setAddOn(addOn);
        record3.setAddOn(addOn);

        return Arrays.asList(record1, record2, record3);
    }

    private void createSubscriptionRecords() throws Exception {
        getLogger().debug(this.getClass().getSimpleName(), "creating DB");

        helper.insertAddonRecords(getAddonSubscritionData());

        getLogger().debug(this.getClass().getSimpleName(), "DB created");
    }

    private void setUpFor_addon_with_invalid_status(String subscriptionState, String subscriberIdentity) throws Exception {
        SubscriptionData data1 = new SubscriptionData(subscriberIdentity, addOn.getId(), "2014-10-29 09:26:50.12", "2020-11-29 09:26:50.12",
                subscriptionState, "1", "10", "2015-12-31 09:26:50.12",productOffer.getId());

        data1.setAddOn(addOn);

        helper.insertAddonRecord(data1);
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

        public void insertAddonRecord(SubscriptionData AddOnSubscriptionData) throws Exception {

            voltServer.runDDLFromString(AddOnSubscriptionData.insertQuery());

            LinkedHashMap<String, Subscription> subscribedList = addonSubscriptions.get(AddOnSubscriptionData.getSubscriberId());

            if (subscribedList == null) {
                subscribedList = new LinkedHashMap<>();
                addonSubscriptions.put(AddOnSubscriptionData.getSubscriberId(), subscribedList);
            }

            subscribedList.put(AddOnSubscriptionData.getSubscriptionId(), AddOnSubscriptionData.getAddonSubscription());
        }

        public void insertAddonRecords(List<SubscriptionData> AddOnSubscriptionDatas) throws Exception {

            for (SubscriptionData AddOnSubscriptionData : AddOnSubscriptionDatas) {
                insertAddonRecord(AddOnSubscriptionData);
            }
        }

        public LinkedHashMap<String, Subscription> getAddonsForSubscriber(String subscriberIdentity) {
            return addonSubscriptions.containsKey(subscriberIdentity) ? addonSubscriptions.get(subscriberIdentity) : new LinkedHashMap<>();
        }

    }
    private ProductOffer getProductOffer(AddOn addOn) {
        ProductOfferData productOfferData =  new ProductOfferDataFactory()
                .withId("1").withName("Test").withStatus(PkgStatus.ACTIVE.name())
                .withMode("LIVE").withType("ADDON")
                .withDataServicePkgId(addOn.getId()).build();
        return new ProductOffer(
                productOfferData.getId(), productOfferData.getName(), productOfferData.getDescription(),
                PkgType.ADDON, PkgMode.LIVE, 30,
                ValidityPeriodUnit.DAY, productOfferData.getSubscriptionPrice() != null ? productOfferData.getSubscriptionPrice() : 0d,
                0d,
                PkgStatus.ACTIVE, null,null,
                productOfferData.getDataServicePkgId(), productOfferData.getGroupList(), productOfferData.getAvailabilityStartDate(),
                productOfferData.getAvailabilityEndDate(), PolicyStatus.SUCCESS,  null, null,
                (Objects.nonNull(productOfferData.isFnFOffer()) && productOfferData.isFnFOffer().booleanValue()),
                productOfferData.getParam1(), productOfferData.getParam2(), policyRepository,
                null,null,new HashMap<>(),productOfferData.getCurrency()
        );
    }
}
