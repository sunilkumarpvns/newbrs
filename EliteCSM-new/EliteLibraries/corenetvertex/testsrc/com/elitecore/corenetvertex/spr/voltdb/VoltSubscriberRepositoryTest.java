package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.MonetaryBalanceType;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.core.db.VoltDBClient;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.spr.DummyDBDataSource;
import com.elitecore.corenetvertex.spr.DummySubscriptionProvider;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriptionOperation;
import com.elitecore.corenetvertex.spr.TestSubscriberAwareSPInterface;
import com.elitecore.corenetvertex.spr.TestSubscriberCache;
import com.elitecore.corenetvertex.spr.TestSubscriberEnabledSPInterface;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.SubscriberInfo;
import com.elitecore.corenetvertex.spr.data.SubscriberProfileData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.voltdb.InProcessVoltDBServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Ignore
@RunWith(HierarchicalContextRunner.class)
public class VoltSubscriberRepositoryTest {

    private static final String DS_NAME = "test-DB";
    private static InProcessVoltDBServer voltServer;
    private VoltDBSPInterface voltDBSPInterface;
    private SubscriberProfileData profileData;
    private VoltSubscriberRepositoryImpl voltSubscriberRepository;
    @Mock PolicyRepository policyRepository = mock(PolicyRepository.class);
    @Mock private TestSubscriberEnabledSPInterface testSubscriberEnabledSPInterface;
    private DummyTransactionFactory transactionFactory;
    private HibernateSessionFactory hibernateSessionFactory;
    @Mock SubscriptionOperation subscriptionOperation;
    @Mock TestSubscriberCache testSubscriberCache;
    private DummySubscriptionProvider dummySubscriptioProvider;
    private long currentTime;
    @Mock
    private StaffData staffData;
    @Mock
    ProductOfferStore productOfferStore;
    private String subscriberIdentityVal = UUID.randomUUID().toString();
    @Mock
    TestSubscriberAwareSPInterface spInterface;
    @Mock
    VoltMonetaryABMFOperation monetaryABMFOperation;

    @BeforeClass
    public static void beforeClass() throws ClassNotFoundException {
        voltServer = new InProcessVoltDBServer();
        voltServer.start();
        // Add SQL File containing Create Table and Create Procedure query
        voltServer.runDDLFromPath("testsrc/resources/voltdb-test-ddl.sql");

        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    }

    @Before
    public void setUp() throws Exception {
        String sid = UUID.randomUUID().toString();
        MockitoAnnotations.initMocks(this);
        this.dummySubscriptioProvider = new DummySubscriptionProvider();

        String connectionURL = "jdbc:h2:mem:" + sid;
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, connectionURL, "", "", 1,
                5000, 3000);
        transactionFactory = new DummyTransactionFactory(dbDataSource);

        transactionFactory.createTransaction();

        profileData = createProfile();

        voltServer.runDDLFromString(profileData.insertQuery());

        voltDBSPInterface = new VoltDBSPInterface(null, new DummyVoltDBClient(voltServer.getClient()), new FixedTimeSource());

        voltSubscriberRepository = new VoltSubscriberRepositoryImpl(testSubscriberEnabledSPInterface,
                "id", "name", new DummyVoltDBClient(voltServer.getClient()), null,
                policyRepository, new ArrayList<>(), SPRFields.SUBSCRIBER_IDENTITY, null,
                new VoltUMOperation(null, policyRepository,
                new FixedTimeSource(), null, null), new FixedTimeSource(),"INR",null, null, null);

    }

    private SubscriberProfileData createProfile() {
        return new SubscriberProfileData.SubscriberProfileDataBuilder()
                .withSubscriberIdentity("kirti")
                .withImsi("1234")
                .withMsisdn("9797979797")
                .withUserName("user1")
                .withPassword("user1")
                .withPhone("123456")
                .build();
    }

    @Test
    public void testAddSubscriber() throws OperationFailedException {

        voltSubscriberRepository.addTestSubscriber(profileData.getSubscriberIdentity());
        verify(testSubscriberEnabledSPInterface, times(1)).addTestSubscriber(profileData.getSubscriberIdentity());
    }

    @Test
    public void testRemoveSubscriber() throws OperationFailedException {
        voltSubscriberRepository.removeTestSubscriber("kirti");
        verify(testSubscriberEnabledSPInterface, times(1)).removeTestSubscriber(profileData.getSubscriberIdentity(), dummySubscriptioProvider);
    }

    @Test
    public void testRemoveSubscribers() throws OperationFailedException {
        voltSubscriberRepository.removeTestSubscriber(Arrays.asList("Id1", "Id2"));
        verify(testSubscriberEnabledSPInterface, times(1)).removeTestSubscriber(Arrays.asList("Id1", "Id2"), dummySubscriptioProvider);
    }

    @Test
    public void testIsTestSubscriber() throws OperationFailedException {
        voltSubscriberRepository.isTestSubscriber(profileData.getSubscriberIdentity());
        verify(testSubscriberEnabledSPInterface, times(1)).isTestSubscriber(profileData.getSubscriberIdentity());
    }

    @Test
    public void testRefreshTestSubscriberCache() throws OperationFailedException {
        voltSubscriberRepository.refreshTestSubscriberCache();
        verify(testSubscriberEnabledSPInterface, times(1)).refreshTestSubscriberCache();

    }

    @Test
    public void testMarkForDeleteProfile() throws OperationFailedException {
        voltSubscriberRepository.markForDeleteProfile(profileData.getSubscriberIdentity(), null);
        verify(testSubscriberEnabledSPInterface, times(1)).markForDeleteProfile(profileData.getSubscriberIdentity());
    }

    @Test
    public void testrestoreProfile() throws OperationFailedException {
        voltSubscriberRepository.restoreProfile("Id3", null);
        voltSubscriberRepository.removeTestSubscriber("Id3");
        verify(testSubscriberEnabledSPInterface, times(1)).restoreProfile("Id3");
    }

    @Test
    public void testrestoreProfiles() throws OperationFailedException {
        voltSubscriberRepository.restoreProfile(Arrays.asList("Id1", "Id2"));
        verify(testSubscriberEnabledSPInterface, times(1)).restoreProfile(Arrays.asList("Id1", "Id2"));
    }

    private SubscriberInfo createSubscriberInfo() {
        SubscriberInfo subscriberInfo = new SubscriberInfo();
        return subscriberInfo;
    }

    private SPRInfoImpl createSPRInfo() {
        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(subscriberIdentityVal);
        sprInfo.setUserName("newUsername");
        return sprInfo;
    }

    private ProductOffer createProductOffer() {
        return new ProductOffer("base_offer", "base_offer", null, PkgType.BASE, PkgMode.TEST,
                30, ValidityPeriodUnit.DAY, 0.0, 0.0,
                PkgStatus.ACTIVE, new ArrayList<>(), new ArrayList<>(), null, staffData.getGroupList(),
                null, null, PolicyStatus.SUCCESS, null, null,
                false, null, null, policyRepository, null,
                null, null, "INR");
    }

    private List<MonetaryBalance> createMonetaryBalanceList() {
        List<MonetaryBalance> monetaryBalanceslist = new ArrayList<MonetaryBalance>();
        MonetaryBalance monetaryBalance = new MonetaryBalance(UUID.randomUUID().toString(), "1",
                "1", 1, 1, 1, 0, 0, currentTime, currentTime,
                "INR",
                MonetaryBalanceType.DEFAULT.name(), currentTime, 0, "", "");
        monetaryBalance.setInitialBalance(130);
        monetaryBalanceslist.add(monetaryBalance);
        return monetaryBalanceslist;
    }

    @After
    public void tearDown() {
        voltServer.runDDLFromPath("testsrc/resources/voltdb-cleanup-ddl.sql");
    }

    public class MigrateMonetaryBalance {

        SubscriberInfo subscriberInfo;
        SPRInfo sprInfo;
        MonetaryBalance monetaryBalance;
        List<MonetaryBalance> monetaryBalanceslist;

        @Before
        public void setUp() {
            sprInfo = createSPRInfo();
            sprInfo.setProductOffer("base_offer");
            monetaryBalanceslist= createMonetaryBalanceList();
            subscriberInfo = createSubscriberInfo();
            subscriberInfo.setMonetaryBalances(monetaryBalanceslist);
            subscriberInfo.setSprInfo(sprInfo);
        }

        @Test
        public void migrateMonetaryBalanceWhenMigrateSubscriber() throws OperationFailedException {
            ProductOffer po = createProductOffer();
            ProductOfferStore productOfferStore = mock(ProductOfferStore.class);
            when(policyRepository.getProductOffer()).thenReturn(productOfferStore);
            when(productOfferStore.byName("base_offer")).thenReturn(po);
            doNothing().when(spInterface).validate(sprInfo);
            voltSubscriberRepository = spy(voltSubscriberRepository);
            doReturn(monetaryABMFOperation).when(voltSubscriberRepository).getMonetaryABMFOperation();
            doNothing().when(monetaryABMFOperation).addBalance(Mockito.anyString(), Mockito.any(MonetaryBalance.class),
                    Mockito.any(VoltDBClient.class));
            voltSubscriberRepository.importSubscriber(subscriberInfo);
            verify(monetaryABMFOperation, times(1)).addBalance(Mockito.anyString(),
                    Mockito.any(MonetaryBalance.class), Mockito.any());
        }
    }

}
