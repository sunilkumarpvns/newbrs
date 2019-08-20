package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.QosProfileFactory;
import com.elitecore.corenetvertex.pm.factory.BasePackageFactory;
import com.elitecore.corenetvertex.pm.factory.QuotaProfileFactory;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.spr.SubscriberRepositoryTestSuite.SPROperationTestHelper;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.SubscriberDetails;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.DerbyUtil;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyCollectionOf;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@RunWith(HierarchicalContextRunner.class)
public class SubscriberRepositoryBasePackageUsageProvisionWhileAddProfileTest {
    public static final String QUOTA_PROFILE_ID_1 = "quotaProfileId1";
    private SPROperationTestHelper helper;
    private static final String DS_NAME = "test-DB";
    private AlertListener alertListener;
    private PolicyRepository policyRepository;
    private DummyUMOperation umOperation;
    private final String dataPackageName = "dataPackage1";
    private final String subscriberId = "SubscriberId1";
    private SubscriberRepository subscriberRepository;
    private SPRInfoImpl sprInfo;
    private DummyTransactionFactory transactionFactory;
    private ABMFconfiguration abmFconfiguration;
    private EDRListener balanceEDRListener;
    private static final String currency = "INR";

    @BeforeClass
    public static void setUpClass() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, "jdbc:derby:memory:TestingDB;create=true", "", "", 1, 5000, 3000);
        policyRepository = mock(PolicyRepository.class);
        alertListener = mock(AlertListener.class);
        umOperation = spy(new DummyUMOperation(alertListener, policyRepository));
        transactionFactory = spy((DummyTransactionFactory) new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1).build());
        abmFconfiguration = new ABMFconfigurationImpl(1, 100, 10);
        balanceEDRListener = mock(EDRListener.class);
        helper = new SPROperationTestHelper(transactionFactory);
        helper.createProfileTable();
        helper.createUsageTable();

        subscriberRepository = new SubscriberRepositoryImpl("id",
                "name",
                transactionFactory,
                alertListener,
                policyRepository,
                umOperation,
                abmFconfiguration,
                null,
                Collectionz.<String>newArrayList(),
                null,
                null,
                null,null, balanceEDRListener, null, null, "INR");

        sprInfo = createSubscriberProfile(dataPackageName, subscriberId);
    }

    public class ProvisionBasePackageUsage {

        public static final String SERVICE_ID_1 = "SERVICE_ID_1";
        public static final String SERVICE_ID_2 = "SERVICE_ID_2";
        public static final String QUOTA_PROFILE_ID_2 = "QUOTA_PROFILE_ID_2";

        @Test
        public void oneUsageRowShouldBeProvisionedWhenBasePackageHas1Service1QuotaProfile() throws Exception {
            BasePackage basePackage = createBasePackageWith1QuotaProfile1Service(dataPackageName);

            subscriberRepository.addProfile(new SubscriberDetails(sprInfo,null), null);

            verify(umOperation).insert(eq(subscriberId), anyCollectionOf(SubscriberUsage.class), any(Transaction.class));
            assertEquals(1, umOperation.getActualUsages().size());
            SubscriberUsage actualSubscriberUsage = umOperation.getActualUsages().iterator().next();
            SubscriberUsage expectedSubscriberUsage = createExpectedSubscriberUsage(actualSubscriberUsage.getId(), basePackage.getId(), CommonConstants.ALL_SERVICE_ID, QUOTA_PROFILE_ID_1, RenewalIntervalUnit.MONTH_END, 2);

            assertReflectionEquals(expectedSubscriberUsage, actualSubscriberUsage);
            verify(balanceEDRListener, times(1)).addMonetaryEDR(Mockito.any(), Mockito.anyString(), Mockito.anyString());
        }

        @Test
        public void twoUsageRowShouldBeProvisionedWhenBasePackageHas2Service1QuotaProfile() throws Exception {
            BasePackage basePackage = createBasePackageWith1QuotaProfile2Service(dataPackageName);

            subscriberRepository.addProfile(new SubscriberDetails(sprInfo,null), null);

            verify(umOperation).insert(eq(subscriberId), anyCollectionOf(SubscriberUsage.class), any(Transaction.class));
            assertEquals(2, umOperation.getActualUsages().size());
            assertSubscriberUsages(umOperation.getActualUsages(), basePackage);
        }

        @Test
        public void twoUsageRowShouldBeProvisionedWhenBasePackageHas1Service2QuotaProfile() throws Exception {
            BasePackage basePackage = createBasePackageWith1Service2QuotaProfile(dataPackageName);

            subscriberRepository.addProfile(new SubscriberDetails(sprInfo,null), null);

            verify(umOperation).insert(eq(subscriberId), anyCollectionOf(SubscriberUsage.class), any(Transaction.class));
            assertEquals(2, umOperation.getActualUsages().size());
            assertSubscriberUsages(umOperation.getActualUsages(), basePackage);
        }

        private BasePackage createBasePackageWith1Service2QuotaProfile(String dataPackageName) {
            QuotaProfile quotaProfile1 = new QuotaProfileFactory.Builder(QUOTA_PROFILE_ID_1)
                    .withHSQLevelRandomQuotaFor(SERVICE_ID_1)
                    .build();
            QuotaProfile quotaProfile2 = new QuotaProfileFactory.Builder(QUOTA_PROFILE_ID_2)
                    .withHSQLevelRandomQuotaFor(SERVICE_ID_1)
                    .build();

            QoSProfile qoSProfile1 = QosProfileFactory.createQosProfile().withQuotaProfile(quotaProfile1).build();
            QoSProfile qoSProfile2 = QosProfileFactory.createQosProfile().withQuotaProfile(quotaProfile2).build();

            BasePackage basePackage = createBasePackage(dataPackageName, Arrays.asList(qoSProfile1, qoSProfile2));
            return basePackage;
        }

        @Test
        public void singleUsageRowForOneServiceEvenIfSameServiceQuotaFoundInFupLevel() throws Exception {
            BasePackage basePackage = createBasePackageWithSameServiceInDifferentLevel(dataPackageName);

            subscriberRepository.addProfile(new SubscriberDetails(sprInfo,null), null);

            verify(umOperation).insert(eq(subscriberId), anyCollectionOf(SubscriberUsage.class), any(Transaction.class));
            assertEquals(1, umOperation.getActualUsages().size());
            assertSubscriberUsages(umOperation.getActualUsages(), basePackage);
        }

        @Test
        public void twoUsageRowShouldProvisionedWhenDifferentServiceInQuotaProfileFupLevel() throws Exception {
            BasePackage basePackage = createBasePackageWithDifferentServiceInDifferentLevel(dataPackageName);

            subscriberRepository.addProfile(new SubscriberDetails(sprInfo,null), null);

            verify(umOperation).insert(eq(subscriberId), anyCollectionOf(SubscriberUsage.class), any(Transaction.class));

            assertSubscriberUsages(umOperation.getActualUsages(), basePackage);

            doCallRealMethod().when(transactionFactory).createTransaction();
        }

        @Test
        public void usageRowProvisionWhenNoRenewalIntervalIsSetInQuotaProfileForBasePackageItSetsZeroInActualUsage() throws Exception {
            BasePackage basePackage = createBasePackageWithoutRenewalIntervalInQuotaProfile(dataPackageName);
            subscriberRepository.addProfile(new SubscriberDetails(sprInfo,null), null);

            verify(umOperation).insert(eq(subscriberId), anyCollectionOf(SubscriberUsage.class), any(Transaction.class));

            assertSubscriberUsagesForNoRenewalInterval(umOperation.getActualUsages(), basePackage);

            doCallRealMethod().when(transactionFactory).createTransaction();
        }

        private BasePackage createBasePackageWithDifferentServiceInDifferentLevel(String dataPackageName) {
            QuotaProfile quotaProfile1 = new QuotaProfileFactory.Builder(QUOTA_PROFILE_ID_1)
                    .withHSQLevelRandomQuotaFor(SERVICE_ID_1)
                    .withFUPRandomQuotaFor(SERVICE_ID_2, 1)
                    .build();

            QoSProfile qoSProfile = QosProfileFactory.createQosProfile().withQuotaProfile(quotaProfile1).build();
            BasePackage basePackage = createBasePackage(dataPackageName, Arrays.asList(qoSProfile));

            return basePackage;
        }

        private BasePackage createBasePackageWithSameServiceInDifferentLevel(String dataPackageName) {
            QuotaProfile quotaProfile1 = new QuotaProfileFactory.Builder(QUOTA_PROFILE_ID_1)
                    .withHSQLevelRandomQuotaFor(SERVICE_ID_1)
                    .withFUPRandomQuotaFor(SERVICE_ID_1, 1)
                    .build();

            QoSProfile qoSProfile = QosProfileFactory.createQosProfile().withQuotaProfile(quotaProfile1).build();
            BasePackage basePackage = createBasePackage(dataPackageName, Arrays.asList(qoSProfile));

            return basePackage;
        }

        private BasePackage createBasePackageWithoutRenewalIntervalInQuotaProfile(String dataPackageName) {
            QuotaProfile quotaProfile1 = new QuotaProfileFactory.Builder(QUOTA_PROFILE_ID_1)
                    .withHSQLevelRandomQuotaFor(SERVICE_ID_1)
                    .withFUPRandomQuotaFor(SERVICE_ID_1, 1)
                    .withRenewalInterval(0)
                    .withRenewalIntervalUnit(RenewalIntervalUnit.DAY)
                    .build();

            QoSProfile qoSProfile = QosProfileFactory.createQosProfile().withQuotaProfile(quotaProfile1).build();
            BasePackage basePackage = createBasePackage(dataPackageName, Arrays.asList(qoSProfile));

            return basePackage;
        }

        private void assertSubscriberUsagesForNoRenewalInterval(Collection<SubscriberUsage> actualUsages, BasePackage basePackage) {
            for (SubscriberUsage actualUsage : actualUsages) {
                if (actualUsage.getServiceId().equals(SERVICE_ID_1)) {
                    SubscriberUsage expectedSubscriberUsage = createExpectedSubscriberUsage(actualUsage.getId(), basePackage.getId(), SERVICE_ID_1, actualUsage.getQuotaProfileId(),RenewalIntervalUnit.DAY,0);
                    assertReflectionEquals(expectedSubscriberUsage, actualUsage);
                } else if (actualUsage.getServiceId().equals(SERVICE_ID_2)) {
                    SubscriberUsage expectedSubscriberUsage = createExpectedSubscriberUsage(actualUsage.getId(), basePackage.getId(), SERVICE_ID_2, actualUsage.getQuotaProfileId(),RenewalIntervalUnit.DAY,0);
                    assertReflectionEquals(expectedSubscriberUsage, actualUsage);
                } else {
                    fail("Service Id does not matched: " + actualUsage.getServiceId());
                }
            }
        }

        private void assertSubscriberUsages(Collection<SubscriberUsage> actualUsages, BasePackage basePackage) {
            for (SubscriberUsage actualUsage : actualUsages) {
                if (actualUsage.getServiceId().equals(SERVICE_ID_1)) {
                    SubscriberUsage expectedSubscriberUsage = createExpectedSubscriberUsage(actualUsage.getId(), basePackage.getId(), SERVICE_ID_1, actualUsage.getQuotaProfileId(),RenewalIntervalUnit.MONTH_END,2);
                    assertReflectionEquals(expectedSubscriberUsage, actualUsage);
                } else if (actualUsage.getServiceId().equals(SERVICE_ID_2)) {
                    SubscriberUsage expectedSubscriberUsage = createExpectedSubscriberUsage(actualUsage.getId(), basePackage.getId(), SERVICE_ID_2, actualUsage.getQuotaProfileId(),RenewalIntervalUnit.MONTH_END,2);
                    assertReflectionEquals(expectedSubscriberUsage, actualUsage);
                } else {
                    fail("Service Id does not matched: " + actualUsage.getServiceId());
                }
            }
        }

        private SubscriberUsage createExpectedSubscriberUsage(String id, String packageId, String serviceId, String quotaProfileId, RenewalIntervalUnit renewalIntervalUnit, int renewalInterval) {
            SubscriberUsage subscriberUsage = new SubscriberUsage(id, quotaProfileId, subscriberId, serviceId, null, packageId,"test");
            if(renewalInterval != 0){
                subscriberUsage.setBillingCycleResetTime(renewalIntervalUnit.addTime(System.currentTimeMillis(),renewalInterval));
                subscriberUsage.setCustomResetTime(renewalIntervalUnit.addTime(System.currentTimeMillis(),renewalInterval));
            }
            return subscriberUsage;
        }

        private BasePackage createBasePackageWith1QuotaProfile2Service(String dataPackageName) {
            QuotaProfile quotaProfile1 = new QuotaProfileFactory.Builder(QUOTA_PROFILE_ID_1)
                    .withHSQLevelRandomQuotaFor(SERVICE_ID_1)
                    .withHSQLevelRandomQuotaFor(SERVICE_ID_2)
                    .build();

            QoSProfile qoSProfile = QosProfileFactory.createQosProfile().withQuotaProfile(quotaProfile1).build();
            BasePackage basePackage = createBasePackage(dataPackageName, Arrays.asList(qoSProfile));

            return basePackage;
        }

        private BasePackage createBasePackageWith1QuotaProfile1Service(String dataPackageName) {
            QuotaProfile quotaProfile = QuotaProfileFactory.createQuotaProfileWithRandomUsage(QUOTA_PROFILE_ID_1);
            QoSProfile qoSProfile = QosProfileFactory.createQosProfile().withQuotaProfile(quotaProfile).build();
            BasePackage basePackage = createBasePackage(dataPackageName, Arrays.asList(qoSProfile));
            return basePackage;
        }
    }

    public class NoProvisionBasePackageUsage {

        @Test
        public void whenBasePackageHasNoQuotaProfile() throws Exception {
            BasePackage basePackage = createBasePackageWihoutQuotaProfile(dataPackageName);

            subscriberRepository.addProfile(new SubscriberDetails(sprInfo,null), null);

            verify(umOperation, times(0)).insert(eq(subscriberId), anyCollectionOf(SubscriberUsage.class), any(Transaction.class));
        }
        @Test
        public void whenBasePackageIsNotUsageMeteringType() throws Exception {
            createBasePackageWithSyType();

            subscriberRepository.addProfile(new SubscriberDetails(sprInfo,null), null);

            verify(umOperation, times(0)).insert(eq(subscriberId), anyCollectionOf(SubscriberUsage.class), any(Transaction.class));
        }

        @Test
        public void whenAddProfileOperationFailed() throws Exception {
            setUpAddSubscriberThrowsQueryTimeOut();
            BasePackage basePackage = createBasePackageWihoutQuotaProfile(dataPackageName);
            doReturn(basePackage).when(policyRepository).getBasePackageDataByName(basePackage.getName());

            subscriberRepository = new SubscriberRepositoryImpl("id",
                    "name",
                    transactionFactory,
                    alertListener,
                    policyRepository,
                    umOperation,
                    abmFconfiguration,
                    null,
                    Collectionz.<String>newArrayList(),
                    null,
                    null,
                    null, null, null, null, null, "INR");

            try {
                subscriberRepository.addProfile(new SubscriberDetails(sprInfo,null), null);
            } catch (OperationFailedException e) {
                verify(umOperation, times(0)).insert(eq(subscriberId), anyCollectionOf(SubscriberUsage.class), any(Transaction.class));
                doCallRealMethod().when(transactionFactory).createTransaction();
            }

        }

        private void setUpAddSubscriberThrowsQueryTimeOut() throws Exception {
            Transaction transaction = mock(Transaction.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);
            doReturn(transaction).when(transactionFactory).createTransaction();
            doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
            doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());
            when(preparedStatement.execute()).thenThrow(new SQLException("query timeout", "timeout", 1013));
        }

        private void createBasePackageWithSyType() {
            BasePackage basePackage = new BasePackageFactory.BasePackageBuilder(null, "packageId1", dataPackageName)
                    .withQoSProfiles(Arrays.asList(QosProfileFactory.createQosProfile().build()))
                    .withAvailabilityStatus(PkgStatus.ACTIVE)
                    .withQuotaProfileType(QuotaProfileType.SY_COUNTER_BASED)
                    .build();
            ProductOfferStore productOfferStore = spy(ProductOfferStore.class);
            doReturn(productOfferStore).when(policyRepository).getProductOffer();

            ProductOffer productOffer = new ProductOffer("test", "test", null, PkgType.BASE, PkgMode.LIVE, 0, ValidityPeriodUnit.DAY,
                    0.0,0.0, PkgStatus.ACTIVE, null, null, basePackage.getId(), null, null,
                    null, PolicyStatus.SUCCESS, null, null, false, null, null, policyRepository,null,null,new HashMap<>(),currency);

            doReturn(productOffer).when(productOfferStore).byName(dataPackageName);

            doReturn(basePackage).when(policyRepository).getBasePackageDataByName(basePackage.getName());
        }

        private BasePackage createBasePackageWihoutQuotaProfile(String dataPackageName) {
            QoSProfile qoSProfile = QosProfileFactory.createQosProfile().build();
            BasePackage basePackage = createBasePackage(dataPackageName, Arrays.asList(qoSProfile));
            return basePackage;
        }
    }

    private BasePackage createBasePackage(String dataPackageName, List<QoSProfile> qoSProfiles) {
        BasePackage basePackage = new BasePackageFactory.BasePackageBuilder(null, "packageId1", dataPackageName)
                .withQoSProfiles(qoSProfiles)
                .withAvailabilityStatus(PkgStatus.ACTIVE)
                .withQuotaProfileType(QuotaProfileType.USAGE_METERING_BASED)
                .build();
        ProductOfferStore productOfferStore = spy(ProductOfferStore.class);
        doReturn(productOfferStore).when(policyRepository).getProductOffer();

        ProductOffer productOffer = new ProductOffer("test", "test", null, PkgType.BASE, PkgMode.LIVE, 0, ValidityPeriodUnit.DAY,
                0.0,100.0,PkgStatus.ACTIVE, null, null, basePackage.getId(), null, null,
                null, PolicyStatus.SUCCESS, null, null, false, null, null, policyRepository,null,null,new HashMap<>(),currency);

        doReturn(productOffer).when(productOfferStore).byName(dataPackageName);
        doReturn(basePackage).when(policyRepository).getBasePackageDataByName(basePackage.getName());
        doReturn(basePackage).when(policyRepository).getPkgDataById(basePackage.getId());
        return basePackage;
    }

    @After
    public void afterDropTables() throws Exception {
        helper.dropProfileTables();
        helper.dropUsageTable();
        DBUtility.closeQuietly(transactionFactory.getConnection());
        DerbyUtil.closeDerby("TestingDB");
    }

    private SPRInfoImpl createSubscriberProfile(String dataPackageName, String subscriberId) {
        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(subscriberId);
        sprInfo.setProductOffer(dataPackageName);
        return sprInfo;
    }


    private static class DummyUMOperation extends UMOperation {

        private Collection<SubscriberUsage> usages;

        public DummyUMOperation(AlertListener alertListener, PolicyRepository policyRepository) {
            super(alertListener, policyRepository);
        }

        @Override
        public void insert(String SubscriberId, Collection<SubscriberUsage> usages, Transaction transaction) throws TransactionException, OperationFailedException {
            this.usages = usages;
            super.insert(SubscriberId, usages, transaction);
        }

        public Collection<SubscriberUsage> getActualUsages() {
            return usages;
        }
    }

}