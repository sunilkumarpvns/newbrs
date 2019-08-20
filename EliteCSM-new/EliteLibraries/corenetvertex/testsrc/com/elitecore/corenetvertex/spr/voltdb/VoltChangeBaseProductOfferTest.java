package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.MonetaryBalanceType;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.db.VoltDBClient;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.QosProfileFactory;
import com.elitecore.corenetvertex.pm.factory.BasePackageFactory;
import com.elitecore.corenetvertex.pm.factory.QuotaProfileFactory;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.store.BaseProductOfferStore;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.spr.*;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SubscriberProfileData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.params.ChangeBaseProductOfferParams;
import com.elitecore.corenetvertex.util.TimeSourceChain;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.voltdb.InProcessVoltDBServer;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

@Ignore
public class VoltChangeBaseProductOfferTest {

    private static InProcessVoltDBServer voltServer;
    private SubscriberProfileData profileData;
    private VoltSubscriberRepositoryImpl voltSubscriberRepository;
    private FixedTimeSource timeSource= new FixedTimeSource();
    private Map<String, String> productOfferToBasePackageName;
    private Map<String, String> productOfferToBasePackageId;
    private ProductOfferStore productOfferStore;
    private BaseProductOfferStore baseProductOfferStore = mock(BaseProductOfferStore.class);
    private DummyVoltDBClient dummyVoltDBClient;
    private BasePackage userPackage;
    private SubscriberMonetaryBalance subscriberMonetaryBalance;
    private ChangeBaseProductOfferParams requestParam;

    private long currentTime = System.currentTimeMillis();
    private final String subscriberId = "SubscriberId1";
    private static final String DATA_PACKAGE_NAME = "dataPackage";
    private static final String DATA_PACKAGE_ID = "dataPackageId";
    private static final String QUOTA_PROFILE_ID = "quotaProfileId";
    private static final String PRODUCT_OFFER_NAME = "ProductOfferName";
    private static final String OLD_PRODUCT_OFFER_NAME = "OldProductOfferName";
    private static final String OLD_PRODUCT_OFFER_ID = "OldProductOfferId";
    private final String testParam = "test";

    @Mock TestSubscriberEnabledSPInterface testSubscriberEnabledSPInterface;
    @Mock SubscriptionOperation subscriptionOperation;
    @Mock ProductOffer productOffer = mock(ProductOffer.class);
    @Mock ProductOffer oldProductOffer = mock(ProductOffer.class);
    @Mock PolicyRepository policyRepository = mock(PolicyRepository.class);
    @Mock TransactionFactory taTransactionFactory;
    @Mock BasePackage basePackageWithoutQOS;
    @Mock AlertListener alertListener;
    @Mock QuotaProfile quotaProfile;
    @Mock VoltMonetaryABMFOperation monetaryABMFOperation;
    @Mock TestSubscriberCache testSubscriberCache;

    @Rule public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void beforeClass() throws ClassNotFoundException {
        voltServer = new InProcessVoltDBServer();
        voltServer.start();
        voltServer.runDDLFromPath("testsrc/resources/voltdb-test-ddl.sql");
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        MockitoAnnotations.initMocks(this);
        dummyVoltDBClient = spy(new DummyVoltDBClient(voltServer.getClient()));

        testSubscriberEnabledSPInterface = new TestSubscriberEnabledSPInterface(new VoltDBSPInterface(alertListener, dummyVoltDBClient, timeSource),
                policyRepository, testSubscriberCache);
        when(taTransactionFactory.isAlive()).thenReturn(true);

        profileData = createProfile();
        voltServer.runDDLFromString(profileData.insertQuery());
        productOfferStore = mock(ProductOfferStore.class);

        subscriberMonetaryBalance = spy(new SubscriberMonetaryBalance(new FixedTimeSource()));
        voltSubscriberRepository = spy(new VoltSubscriberRepositoryImpl(testSubscriberEnabledSPInterface,
                "id", "name", dummyVoltDBClient, null,
                policyRepository, new ArrayList<>(), SPRFields.SUBSCRIBER_IDENTITY, null,
                new VoltUMOperation(null, policyRepository,
                        timeSource, null, null), timeSource, "INR", null, null, null));

        QuotaProfile quotaProfile = QuotaProfileFactory.createQuotaProfileWithRandomUsage(QUOTA_PROFILE_ID);
        QoSProfile qoSProfile = QosProfileFactory.createQosProfile().withQuotaProfile(quotaProfile).build();
        userPackage = getBasePackage(qoSProfile);
        treatConfig();
    }

    @Test
    public void updateDataForScheduleDeleteUsage() throws OperationFailedException, IOException, ProcCallException {
        ArrayList<QuotaProfile> quotaProfiles = new ArrayList<>();
        quotaProfiles.add(quotaProfile);

        when(productOffer.getDataServicePkgData()).thenReturn(basePackageWithoutQOS);
        when(basePackageWithoutQOS.getAvailabilityStatus()).thenReturn(PkgStatus.ACTIVE);
        when(basePackageWithoutQOS.getQuotaProfiles()).thenReturn(quotaProfiles);

        int result = voltSubscriberRepository.changeBaseProductOffer(requestParam, null);
        assertEquals(1, result);

        verify(dummyVoltDBClient).callProcedure(Mockito.eq(VoltDBSPInterface.CHANGE_BASE_PRODUCT_OFFER_STORED_PROCEDURE), eq(subscriberId), eq(requestParam.getNewProductOfferName())
                , isNull(Timestamp.class), any(String[].class), isNull(), isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull()
        );
    }

    @Test
    public void updateDataForUsage() throws OperationFailedException, IOException, ProcCallException {
        when(policyRepository.getProductOffer()).thenReturn(productOfferStore);
        when(productOfferStore.base()).thenReturn(baseProductOfferStore);
        when(baseProductOfferStore.byName(requestParam.getNewProductOfferName())).thenReturn(productOffer);
        when(baseProductOfferStore.byId(requestParam.getNewProductOfferName())).thenReturn(productOffer);
        when(productOffer.getStatus()).thenReturn(PkgStatus.ACTIVE);
        when(productOffer.getDataServicePkgData()).thenReturn(userPackage);

        configOldProductOffer();

        int result = voltSubscriberRepository.changeBaseProductOffer(requestParam, null);
        assertEquals(1, result);

        verify(dummyVoltDBClient).callProcedure(Mockito.eq(VoltDBSPInterface.CHANGE_BASE_PRODUCT_OFFER_STORED_PROCEDURE), eq(subscriberId), eq(requestParam.getNewProductOfferName())
                , isNull(Timestamp.class), isNull(), isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(),
                any(String[].class), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull()
        );
    }

    @Test
    public void addMOnetaryBalanceWhenProductOfferHasCreditBalance() throws OperationFailedException, IOException, ProcCallException {
        when(productOffer.getCreditBalance()).thenReturn(Double.valueOf("1111"));
        doReturn(null).when(subscriberMonetaryBalance).getMainBalance();
        doReturn(subscriberMonetaryBalance).when(voltSubscriberRepository).getMonetaryBalance(anyString(), any(java.util.function.Predicate.class));

        int result = voltSubscriberRepository.changeBaseProductOffer(requestParam, null);
        assertEquals(1, result);

        verify(dummyVoltDBClient).callProcedure(Mockito.eq(VoltDBSPInterface.CHANGE_BASE_PRODUCT_OFFER_STORED_PROCEDURE), eq(subscriberId), eq(requestParam.getNewProductOfferName())
                , isNull(Timestamp.class), isNull(), isNull(), isNull(), isNull(), isNull(),
                isNull(), any(String[].class), isNull(),
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull()
        );
    }

    @Test
    public void updateMOnetaryBalanceWhenProductOfferHasCreditBalance() throws OperationFailedException, IOException, ProcCallException {
        when(productOffer.getCreditBalance()).thenReturn(Double.valueOf("1111"));
        doReturn(subscriberMonetaryBalance).when(monetaryABMFOperation).getMonetaryBalance(anyString(), any(java.util.function.Predicate.class), any(VoltDBClient.class));

        int result = voltSubscriberRepository.changeBaseProductOffer(requestParam, null);
        assertEquals(1, result);

        verify(dummyVoltDBClient).callProcedure(Mockito.eq(VoltDBSPInterface.CHANGE_BASE_PRODUCT_OFFER_STORED_PROCEDURE), eq(subscriberId), eq(requestParam.getNewProductOfferName())
                , isNull(Timestamp.class), isNull(), isNull(), isNull(), isNull(), isNull(),
                isNull(), any(String[].class), isNull(),
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull()
        );
    }

    @Test
    public void whenQueryExecutionTimeIsHighThenThrowException() throws OperationFailedException {
        doReturn(null).when(subscriberMonetaryBalance).getMainBalance();
        TimeSourceChain timeSource = new TimeSourceChain(3, System.currentTimeMillis(), AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10);

        voltSubscriberRepository = spy(new VoltSubscriberRepositoryImpl(testSubscriberEnabledSPInterface,
                "id", "name", dummyVoltDBClient, alertListener,
                policyRepository, new ArrayList<>(), SPRFields.SUBSCRIBER_IDENTITY, null,
                new VoltUMOperation(alertListener, policyRepository,
                        timeSource, null, null), timeSource, "INR", null, null, null));

        try {
            voltSubscriberRepository.changeBaseProductOffer(requestParam, null);
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(anyString(), Matchers.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), anyString(), anyString());
        }
    }

    @Test
    public void whenCallingProcedureThrowsProcCallException() throws OperationFailedException, IOException, ProcCallException {
        when(productOffer.getCreditBalance()).thenReturn(Double.valueOf("1111"));
        doReturn(null).when(subscriberMonetaryBalance).getMainBalance();
        doReturn(subscriberMonetaryBalance).when(monetaryABMFOperation).getMonetaryBalance(anyString(), any(java.util.function.Predicate.class), any(VoltDBClient.class));
        doThrow(ProcCallException.class).when(dummyVoltDBClient).callProcedure(Mockito.eq(VoltDBSPInterface.CHANGE_BASE_PRODUCT_OFFER_STORED_PROCEDURE), eq(subscriberId), eq(requestParam.getNewProductOfferName())
                , any(Timestamp.class), any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String.class),
                any(String.class), any(String[].class), any(String[].class),
                any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String[].class),
                any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String[].class)
        );

        expectedException.expect(OperationFailedException.class);

        voltSubscriberRepository.changeBaseProductOffer(requestParam, null);
    }

    @Test
    public void whenCallingProcedureThrowsIOException() throws OperationFailedException, IOException, ProcCallException {
        when(productOffer.getCreditBalance()).thenReturn(Double.valueOf("1111"));
        doReturn(null).when(subscriberMonetaryBalance).getMainBalance();
        doThrow(IOException.class).when(dummyVoltDBClient).callProcedure(Mockito.eq(VoltDBSPInterface.CHANGE_BASE_PRODUCT_OFFER_STORED_PROCEDURE), eq(subscriberId), eq(requestParam.getNewProductOfferName())
                , any(Timestamp.class), any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String.class),
                any(String.class), any(String[].class), any(String[].class),
                any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String[].class),
                any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String[].class)
        );

        expectedException.expect(OperationFailedException.class);

        int result = voltSubscriberRepository.changeBaseProductOffer(requestParam, null);
    }

    @Test
    public void when_calling_procedure_throws_null_response() throws OperationFailedException, IOException, ProcCallException {
        when(productOffer.getCreditBalance()).thenReturn(Double.valueOf("1111"));
        doReturn(null).when(subscriberMonetaryBalance).getMainBalance();
        doReturn(null).when(dummyVoltDBClient).callProcedure(Mockito.eq(VoltDBSPInterface.CHANGE_BASE_PRODUCT_OFFER_STORED_PROCEDURE), eq(subscriberId), eq(requestParam.getNewProductOfferName())
                , any(Timestamp.class), any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String.class),
                any(String.class), any(String[].class), any(String[].class),
                any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String[].class),
                any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String[].class), any(String[].class)
        );

        expectedException.expect(OperationFailedException.class);

        voltSubscriberRepository.changeBaseProductOffer(requestParam, null);
    }

    @Test
    public void when_calling_procedure_throws_OperationFailed_Exception() throws OperationFailedException, IOException, ProcCallException {
        when(productOffer.getCreditBalance()).thenReturn(Double.valueOf("1111"));
        doReturn(null).when(subscriberMonetaryBalance).getMainBalance();
        doThrow(ProcCallException.class).when(dummyVoltDBClient).callProcedure("MonetaryBalanceSelectStoredProcedure", subscriberId);

        expectedException.expect(OperationFailedException.class);

        voltSubscriberRepository.changeBaseProductOffer(requestParam, null);
    }

    @After
    public void tearDown() {
        voltServer.runDDLFromPath("testsrc/resources/voltdb-cleanup-ddl.sql");
    }

    private void treatConfig() {
        when(policyRepository.getProductOffer()).thenReturn(productOfferStore);
        when(productOfferStore.base()).thenReturn(baseProductOfferStore);
        when(baseProductOfferStore.byName(anyString())).thenReturn(productOffer);
        when(baseProductOfferStore.byId(anyString())).thenReturn(productOffer);
        when(productOffer.getStatus()).thenReturn(PkgStatus.ACTIVE);
        when(productOffer.getDataServicePkgData()).thenReturn(userPackage);
        subscriberMonetaryBalance.addMonitoryBalances(createmonetaryBalance());

        requestParam = getChangeBaseRequestParam();
    }

    private void configOldProductOffer() {
        ArrayList<QuotaProfile> quotaProfiles = new ArrayList<>();
        when(policyRepository.getProductOffer()).thenReturn(productOfferStore);
        when(productOfferStore.base()).thenReturn(baseProductOfferStore);
        when(baseProductOfferStore.byId(requestParam.getCurrentProductOfferId())).thenReturn(oldProductOffer);
        when(oldProductOffer.getStatus()).thenReturn(PkgStatus.ACTIVE);
        when(oldProductOffer.getDataServicePkgData()).thenReturn(basePackageWithoutQOS);
        when(basePackageWithoutQOS.getAvailabilityStatus()).thenReturn(PkgStatus.ACTIVE);
        when(basePackageWithoutQOS.getQuotaProfileType()).thenReturn(QuotaProfileType.USAGE_METERING_BASED);
        when(basePackageWithoutQOS.getQuotaProfiles()).thenReturn(quotaProfiles);
    }

    private BasePackage getBasePackage(QoSProfile qoSProfile) {
        this.productOfferToBasePackageName = new HashMap<>();
        this.productOfferToBasePackageId = new HashMap<>();
        this.productOfferToBasePackageName.put(PRODUCT_OFFER_NAME, DATA_PACKAGE_NAME);
        this.productOfferToBasePackageId.put(PRODUCT_OFFER_NAME, DATA_PACKAGE_ID);

        return new BasePackageFactory.BasePackageBuilder(null, productOfferToBasePackageId.get(PRODUCT_OFFER_NAME),
                productOfferToBasePackageName.get(PRODUCT_OFFER_NAME))
                .withQoSProfiles(Arrays.asList(qoSProfile))
                .withAvailabilityStatus(PkgStatus.ACTIVE)
                .withPolicyStatus(PolicyStatus.SUCCESS)
                .withQuotaProfileType(QuotaProfileType.USAGE_METERING_BASED)
                .build();
    }

    private MonetaryBalance createmonetaryBalance() {
        MonetaryBalance monetaryBalance = new MonetaryBalance(UUID.randomUUID().toString(), "1",
                "1", 1, 1, 1, 0, 0, currentTime, currentTime,
                "INR",
                MonetaryBalanceType.DEFAULT.name(), currentTime, 0, "", "");
        return monetaryBalance;
    }

    private SubscriberProfileData createProfile() {
        return new SubscriberProfileData.SubscriberProfileDataBuilder()
                .withSubscriberIdentity(subscriberId)
                .withImsi(subscriberId)
                .withMsisdn(subscriberId)
                .withUserName("user1")
                .withPassword("user1")
                .withPhone(subscriberId)
                .build();
    }

    private ChangeBaseProductOfferParams getChangeBaseRequestParam() {
        requestParam = new ChangeBaseProductOfferParams();
        requestParam.setSubscriberId(subscriberId);
        requestParam.setCurrentBasePackageId(OLD_PRODUCT_OFFER_ID);
        requestParam.setCurrentProductOfferId(OLD_PRODUCT_OFFER_NAME);
        requestParam.setNewProductOfferName(PRODUCT_OFFER_NAME);
        requestParam.setAlternateId(subscriberId);
        requestParam.setParam1(testParam);
        requestParam.setParam2(testParam);
        requestParam.setParam3(testParam);
        return requestParam;
    }
}