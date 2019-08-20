package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.DummyPolicyRepository;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.QosProfileFactory;
import com.elitecore.corenetvertex.pm.factory.BasePackageFactory;
import com.elitecore.corenetvertex.pm.factory.QuotaProfileFactory;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.util.MockProductOffer;
import com.elitecore.corenetvertex.spr.SubscriberRepositoryTestSuite.SPROperationTestHelper;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.SubscriberDetails;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.params.ChangeBaseProductOfferParams;
import com.elitecore.corenetvertex.spr.params.ChangeBaseProductOfferParams.Builder;
import com.elitecore.corenetvertex.util.DerbyUtil;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyCollectionOf;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@RunWith(HierarchicalContextRunner.class)
public class SubscriberRepositoryBasePackageUsageProvisionWhileChangeDataPackageTest {
    public static final String QUOTA_PROFILE_ID_1 = "quotaProfileId1";
	public static final String PRODUCT_OFFER_NAME1 = "ProductOfferName1";
	public static final String PRODUCT_OFFER_ID1 = "ProductOfferId1";
	public static final String PRODUCT_OFFER_NAME2 = "ProductOfferName2";
	public static final String PRODUCT_OFFER_ID2 = "ProductOfferId2";
	private SPROperationTestHelper helper;
    private static final String DS_NAME = "test-DB";
    private AlertListener alertListener;
    private DummyPolicyRepository policyRepository;
    private DummyUMOperation umOperation;
    private final String dataPackageName1 = "dataPackage1";
    private final String dataPackageName2 = "dataPackage2";
    private final String dataPackageId1 = "dataPackageId1";
    private final String dataPackageId2 = "dataPackageId2";
    private final String subscriberId = "SubscriberId1";
    private SubscriberRepository subscriberRepository;
    private SPRInfoImpl sprInfo;
    private DummyTransactionFactoryBuilder transactionFactoryBuilder;
    private DummyTransactionFactory transactionFactory;
    private ABMFconfiguration abmFconfiguration;
    private Map<String, String> productOfferToBasePackageName;
	private Map<String, String> productOfferToBasePackageId;

	@Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setUpClass() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, "jdbc:derby:memory:TestingDB;create=true", "", "", 1, 5000, 3000);
        policyRepository = new DummyPolicyRepository();
        alertListener = mock(AlertListener.class);
        umOperation = spy(new DummyUMOperation(alertListener, policyRepository));
        transactionFactoryBuilder = spy(new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1));
        abmFconfiguration = new ABMFconfigurationImpl(1,100,10);
        transactionFactory = (DummyTransactionFactory) transactionFactoryBuilder.build();
        helper = new SPROperationTestHelper(transactionFactory);
        helper.createProfileTable();
        helper.createUsageTable();
        this.productOfferToBasePackageName = new HashMap<>();
		this.productOfferToBasePackageId = new HashMap<>();
        this.productOfferToBasePackageName.put(PRODUCT_OFFER_NAME1, dataPackageName1);
		this.productOfferToBasePackageName.put(PRODUCT_OFFER_NAME2, dataPackageName2);
		this.productOfferToBasePackageId.put(PRODUCT_OFFER_NAME1, dataPackageId1);
		this.productOfferToBasePackageId.put(PRODUCT_OFFER_NAME2, dataPackageId2);

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

        sprInfo = createSubscriberProfile(PRODUCT_OFFER_NAME2, subscriberId);
		createProductOfferWith1QuotaProfile1Service(PRODUCT_OFFER_ID2, PRODUCT_OFFER_NAME2);
        subscriberRepository.addProfile(new SubscriberDetails(sprInfo,null), null);
        reset(umOperation);
    }


    @Test
    public void failOperationwhenCurrentPackageIdNotFound() throws Exception {
        createProductOfferWihoutQuotaProfile(PRODUCT_OFFER_ID1, PRODUCT_OFFER_NAME1);
        ChangeBaseProductOfferParams params = new Builder()
                .withSubscriberId(subscriberId)
                .withNewProductOfferName(PRODUCT_OFFER_NAME1)
                .build();

        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage(containsString("subscribed current base product offer not found"));
        subscriberRepository.changeBaseProductOffer(params, null);

    }

    public class ProvisionBasePackageUsageForNewPackageAndScheduleDeleteForOldPackage {

        public static final String SERVICE_ID_1 = "SERVICE_ID_1";
        public static final String SERVICE_ID_2 = "SERVICE_ID_2";
        public static final String QUOTA_PROFILE_ID_2 = "QUOTA_PROFILE_ID_2";

        @Test
        public void oneUsageRowShouldBeProvisionedWhenBasePackageHas1Service1QuotaProfile() throws Exception {
            ProductOffer productOffer = createProductOfferWith1QuotaProfile1Service(PRODUCT_OFFER_ID1, PRODUCT_OFFER_NAME1);
            ChangeBaseProductOfferParams params = new ChangeBaseProductOfferParams.Builder()
                    .withSubscriberId(subscriberId)
                    .withCurrentProductOfferId(PRODUCT_OFFER_ID2)
                    .withNewProductOfferName(PRODUCT_OFFER_NAME1)
                    .build();

            subscriberRepository.changeBaseProductOffer(params, null);
            verify(umOperation).insert(eq(subscriberId), anyCollectionOf(SubscriberUsage.class), any(Transaction.class));
            verify(umOperation).scheduleForUsageDelete(eq(subscriberId), anyString(), eq(PRODUCT_OFFER_ID2), eq("TO DELETE USAGE"),
                    anyString(), anyString(), anyString(), any(Transaction.class));

            assertEquals(1, umOperation.getActualUsages().size());
            SubscriberUsage actualSubscriberUsage = umOperation.getActualUsages().iterator().next();
            SubscriberUsage expectedSubscriberUsage = createExpectedSubscriberUsage(actualSubscriberUsage.getId(), productOffer.getDataServicePkgData().getId(),
					productOffer.getId(), CommonConstants.ALL_SERVICE_ID, QUOTA_PROFILE_ID_1);

            assertReflectionEquals(expectedSubscriberUsage, actualSubscriberUsage);
        }

        @Test
        public void twoUsageRowShouldBeProvisionedWhenBasePackageHas2Service1QuotaProfile() throws Exception {
            ProductOffer productOffer = createProductOfferWith1QuotaProfile2Service(PRODUCT_OFFER_ID1, PRODUCT_OFFER_NAME1);

            subscriberRepository.changeBaseProductOffer(createParams(), null);

            verify(umOperation).insert(eq(subscriberId), anyCollectionOf(SubscriberUsage.class), any(Transaction.class));
            verify(umOperation).scheduleForUsageDelete(eq(subscriberId), anyString(), eq(PRODUCT_OFFER_ID2), eq("TO DELETE USAGE"),
                    anyString(), anyString(), anyString(), any(Transaction.class));

            assertSubscriberUsages(umOperation.getActualUsages(), productOffer);
        }

        @Test
        public void twoUsageRowShouldBeProvisionedWhenBasePackageHas1Service2QuotaProfile() throws Exception {
            ProductOffer productOffer = createBasePackageWith1Service2QuotaProfile(PRODUCT_OFFER_ID1, PRODUCT_OFFER_NAME1);

            subscriberRepository.changeBaseProductOffer(createParams(), null);

            verify(umOperation, times(1)).insert(eq(subscriberId), anyCollectionOf(SubscriberUsage.class), any(Transaction.class));
            verify(umOperation).scheduleForUsageDelete(eq(subscriberId), anyString(), eq(PRODUCT_OFFER_ID2), eq("TO DELETE USAGE"),
                    anyString(), anyString(), anyString(), any(Transaction.class));

            assertSubscriberUsages(umOperation.getActualUsages(), productOffer);
        }

        private ProductOffer createBasePackageWith1Service2QuotaProfile(String id, String dataPackageName) {
            QuotaProfile quotaProfile1 = new QuotaProfileFactory.Builder(QUOTA_PROFILE_ID_1)
                    .withHSQLevelRandomQuotaFor(SERVICE_ID_1)
                    .build();
            QuotaProfile quotaProfile2 = new QuotaProfileFactory.Builder(QUOTA_PROFILE_ID_2)
                    .withHSQLevelRandomQuotaFor(SERVICE_ID_1)
                    .build();

            QoSProfile qoSProfile1 = QosProfileFactory.createQosProfile().withQuotaProfile(quotaProfile1).build();
            QoSProfile qoSProfile2 = QosProfileFactory.createQosProfile().withQuotaProfile(quotaProfile2).build();

            return createProductOffer(id, dataPackageName, Arrays.asList(qoSProfile1, qoSProfile2));
        }

        @Test
        public void SingalUsageRowForOneServiceEvenIfSameServiceQuotaFoundInFupLevel() throws Exception {
            ProductOffer productOffer = createProductOfferWithSameServiceInDifferentLevel(PRODUCT_OFFER_ID1, PRODUCT_OFFER_NAME1);

            subscriberRepository.changeBaseProductOffer(createParams(), null);

            verify(umOperation).insert(eq(subscriberId), anyCollectionOf(SubscriberUsage.class), any(Transaction.class));
            verify(umOperation).scheduleForUsageDelete(eq(subscriberId), anyString(), eq(PRODUCT_OFFER_ID2), eq("TO DELETE USAGE"),
                    anyString(), anyString(), anyString(), any(Transaction.class));

            assertSubscriberUsages(umOperation.getActualUsages(), productOffer);
        }

        @Test
        public void twoUsageRowShouldProvisionedWhenDifferentServiceInQuotaProfileFupLevel() throws Exception {
            ProductOffer productOffer = createProductOfferWithDifferentServiceInDifferentLevel(PRODUCT_OFFER_ID1, PRODUCT_OFFER_NAME1);

            subscriberRepository.changeBaseProductOffer(createParams(), null);

            verify(umOperation).insert(eq(subscriberId), anyCollectionOf(SubscriberUsage.class), any(Transaction.class));
            verify(umOperation).scheduleForUsageDelete(eq(subscriberId), anyString(), eq(PRODUCT_OFFER_ID2), eq("TO DELETE USAGE"),
                    anyString(), anyString(), anyString(), any(Transaction.class));

            assertSubscriberUsages(umOperation.getActualUsages(), productOffer);
        }

        private ProductOffer createProductOfferWithDifferentServiceInDifferentLevel(String basePackageId, String dataPackageName) {
            QuotaProfile quotaProfile1 = new QuotaProfileFactory.Builder(QUOTA_PROFILE_ID_1)
                    .withHSQLevelRandomQuotaFor(SERVICE_ID_1)
                    .withFUPRandomQuotaFor(SERVICE_ID_2, 1)
                    .build();

            QoSProfile qoSProfile = QosProfileFactory.createQosProfile().withQuotaProfile(quotaProfile1).build();

            return createProductOffer(basePackageId, dataPackageName, Arrays.asList(qoSProfile));
        }

        private ProductOffer createProductOfferWithSameServiceInDifferentLevel(String basePackageId, String dataPackageName) {
            QuotaProfile quotaProfile1 = new QuotaProfileFactory.Builder(QUOTA_PROFILE_ID_1)
                    .withHSQLevelRandomQuotaFor(SERVICE_ID_1)
                    .withFUPRandomQuotaFor(SERVICE_ID_1, 1)
                    .build();

            QoSProfile qoSProfile = QosProfileFactory.createQosProfile().withQuotaProfile(quotaProfile1).build();

            return createProductOffer(basePackageId, dataPackageName, Arrays.asList(qoSProfile));
        }

        private void assertSubscriberUsages(Collection<SubscriberUsage> actualUsages, ProductOffer productOffer) {
            for (SubscriberUsage actualUsage : actualUsages) {
                if (actualUsage.getServiceId().equals(SERVICE_ID_1)) {
                    SubscriberUsage expectedSubscriberUsage = createExpectedSubscriberUsage(actualUsage.getId(), productOffer.getDataServicePkgData().getId(),
							productOffer.getId(), SERVICE_ID_1, actualUsage.getQuotaProfileId());
                    assertReflectionEquals(expectedSubscriberUsage, actualUsage);
                } else if (actualUsage.getServiceId().equals(SERVICE_ID_2)) {
                    SubscriberUsage expectedSubscriberUsage = createExpectedSubscriberUsage(actualUsage.getId(), productOffer.getDataServicePkgData().getId(),
							productOffer.getId(), SERVICE_ID_2, actualUsage.getQuotaProfileId());
                    assertReflectionEquals(expectedSubscriberUsage, actualUsage);
                } else {
                    fail("Service Id is not matched: " + actualUsage.getServiceId());
                }
            }
        }

        private ProductOffer createProductOfferWith1QuotaProfile2Service(String dataPackageId, String dataPackageName) {
            QuotaProfile quotaProfile1 = new QuotaProfileFactory.Builder(QUOTA_PROFILE_ID_1)
                    .withHSQLevelRandomQuotaFor(SERVICE_ID_1)
                    .withHSQLevelRandomQuotaFor(SERVICE_ID_2)
                    .build();

            QoSProfile qoSProfile = QosProfileFactory.createQosProfile().withQuotaProfile(quotaProfile1).build();

            return createProductOffer(dataPackageId, dataPackageName, Arrays.asList(qoSProfile));
        }
    }

    private ChangeBaseProductOfferParams  createParams() {
        return new ChangeBaseProductOfferParams.Builder()
                .withSubscriberId(subscriberId)
                .withCurrentProductOfferId(PRODUCT_OFFER_ID2)
                .withNewProductOfferName(PRODUCT_OFFER_NAME1)
                .build();
    }

    private SubscriberUsage createExpectedSubscriberUsage(String id, String packageId, String productOfferId, String serviceId, String quotaProfileId) {
        SubscriberUsage subscriberUsage = new SubscriberUsage(id, quotaProfileId, subscriberId, serviceId, null, packageId,productOfferId);
        subscriberUsage.setBillingCycleResetTime(RenewalIntervalUnit.MONTH_END.addTime(System.currentTimeMillis(),2));
        subscriberUsage.setCustomResetTime(RenewalIntervalUnit.MONTH_END.addTime(System.currentTimeMillis(),2));
        return subscriberUsage;
    }

    private ProductOffer createProductOfferWith1QuotaProfile1Service(String productOfferId, String productOfferName) {
        QuotaProfile quotaProfile = QuotaProfileFactory.createQuotaProfileWithRandomUsage(QUOTA_PROFILE_ID_1);
        QoSProfile qoSProfile = QosProfileFactory.createQosProfile().withQuotaProfile(quotaProfile).build();
        return createProductOffer(productOfferId, productOfferName, Arrays.asList(qoSProfile));
    }

    public class ProvisionBasePackageUsageForNewPackageAndDoNotScheduleDeleteForOldPackage {

    	@Before
    	public void setUp() {
    		createProductOfferWihoutQuotaProfile(PRODUCT_OFFER_ID2, PRODUCT_OFFER_NAME2);
		}
        @Test
        public void oldPackageIdNotFoundAndNewPackageWithQuotaProfile() throws Exception {
            ProductOffer productOffer = createProductOfferWith1QuotaProfile1Service(PRODUCT_OFFER_ID1, PRODUCT_OFFER_NAME1);

            subscriberRepository.changeBaseProductOffer(createParams(), null);

            verify(umOperation).insert(eq(subscriberId), anyCollectionOf(SubscriberUsage.class), any(Transaction.class));
            verify(umOperation, times(0)).scheduleForUsageDelete(eq(subscriberId), anyString(), eq(PRODUCT_OFFER_ID2), eq("TO DELETE USAGE"),
                    anyString(), anyString(), anyString(), any(Transaction.class));
            assertEquals(1, umOperation.getActualUsages().size());
            SubscriberUsage actualSubscriberUsage = umOperation.getActualUsages().iterator().next();
            SubscriberUsage expectedSubscriberUsage = createExpectedSubscriberUsage(actualSubscriberUsage.getId(), productOffer.getDataServicePkgData().getId(),
					productOffer.getId(), CommonConstants.ALL_SERVICE_ID, QUOTA_PROFILE_ID_1);

            assertReflectionEquals(expectedSubscriberUsage, actualSubscriberUsage);
        }
    }

    public class NoProvisionBasePackageUsageAndDoNotScheduleDeleteUsageForOldPackage {

        @Test
        public void whenUpdateProfileOperationFailed() throws Exception {
            TransactionFactory transactionFactory = setUpAddSubscriberThrowsQueryTimeOut();
            createProductOfferWihoutQuotaProfile(dataPackageId1, dataPackageName1);

            subscriberRepository = new SubscriberRepositoryImpl(null,
                    null,
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
                subscriberRepository.changeBaseProductOffer(createParams(), null);
            } catch (OperationFailedException e) {
                verify(umOperation, times(0)).insert(eq(subscriberId), anyCollectionOf(SubscriberUsage.class), any(Transaction.class));
                verify(umOperation, times(0)).scheduleForUsageDelete(eq(subscriberId), anyString(), eq(PRODUCT_OFFER_ID2), eq("TO DELETE USAGE"),
                        anyString(), anyString(), anyString(), any(Transaction.class));
            }

        }

        private TransactionFactory setUpAddSubscriberThrowsQueryTimeOut() throws Exception {
            TransactionFactory transactionFactory = spy(transactionFactoryBuilder.build());
            Transaction transaction = mock(Transaction.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);
            doReturn(transaction).when(transactionFactory).createTransaction();
            doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
            doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());
            when(preparedStatement.execute()).thenThrow(new SQLException("query timeout", "timeout", 1013));

            return transactionFactory;
        }
    }


    public class NoProvisionBasePackageUsageAndScheduleDeleteUsageForOldPackage {

        @Test
        public void whenNewBasePackageHasNoQuotaProfileAndOldPackageIdPassed() throws Exception {
            createProductOfferWihoutQuotaProfile(PRODUCT_OFFER_ID1, PRODUCT_OFFER_NAME1);

            subscriberRepository.changeBaseProductOffer(createParams(), null);

            verify(umOperation, times(0)).insert(eq(subscriberId), anyCollectionOf(SubscriberUsage.class), any(Transaction.class));
            verify(umOperation).scheduleForUsageDelete(eq(subscriberId), anyString(), eq(PRODUCT_OFFER_ID2), eq("TO DELETE USAGE"),
                    anyString(), anyString(), anyString(), any(Transaction.class));

        }

        @Test
        public void whenBasePackageIsNotUsageMeteringType() throws Exception {
            createProductOfferWithSyType(PRODUCT_OFFER_ID1, PRODUCT_OFFER_NAME1);

            subscriberRepository.changeBaseProductOffer(createParams(), null);

            verify(umOperation, times(0)).insert(eq(subscriberId), anyCollectionOf(SubscriberUsage.class), any(Transaction.class));
            verify(umOperation).scheduleForUsageDelete(eq(subscriberId), anyString(), eq(PRODUCT_OFFER_ID2), eq("TO DELETE USAGE"),
                    anyString(), anyString(), anyString(), any(Transaction.class));
        }

        private void createProductOfferWithSyType(String productOfferId, String productOfferName) {
            BasePackage basePackage = new BasePackageFactory.BasePackageBuilder(null, dataPackageId1, dataPackageName1)
                    .withQoSProfiles(Arrays.asList(QosProfileFactory.createQosProfile().build()))
                    .withAvailabilityStatus(PkgStatus.ACTIVE)
                    .withQuotaProfileType(QuotaProfileType.SY_COUNTER_BASED)
                    .build();
			MockProductOffer productOffer = MockProductOffer.create(policyRepository, productOfferId, productOfferName, PolicyStatus.SUCCESS, PkgStatus.ACTIVE);
			productOffer.addDataPackage(basePackage);
			productOffer.setPkgType(PkgType.BASE);
			productOffer.setPolicyStatus(PolicyStatus.SUCCESS);
			productOffer.setStatus(PkgStatus.ACTIVE);
			policyRepository.addProductOffer(productOffer);
        }
    }

    private ProductOffer createProductOffer(String productOfferId, String productOfferName, List<QoSProfile> qoSProfiles) {
        BasePackage basePackage = new BasePackageFactory.BasePackageBuilder(null, productOfferToBasePackageId.get(productOfferName),
				productOfferToBasePackageName.get(productOfferName))
                .withQoSProfiles(qoSProfiles)
                .withAvailabilityStatus(PkgStatus.ACTIVE)
                .withQuotaProfileType(QuotaProfileType.USAGE_METERING_BASED)
                .build();

		MockProductOffer productOffer = MockProductOffer.create(policyRepository, productOfferId, productOfferName,PolicyStatus.SUCCESS, PkgStatus.ACTIVE);
		productOffer.addDataPackage(basePackage);
		productOffer.setPkgType(PkgType.BASE);
		productOffer.setStatus(PkgStatus.ACTIVE);
		policyRepository.addProductOffer(productOffer);
        return productOffer;
    }

    @After
    public void afterDropTables() throws Exception {
        helper.dropProfileTables();
        helper.dropUsageTable();
        DBUtility.closeQuietly(transactionFactory.getConnection());
        DerbyUtil.closeDerby("TestingDB");
    }

    private SPRInfoImpl createSubscriberProfile(String productOfferName, String subscriberId) {
        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(subscriberId);
        sprInfo.setProductOffer(productOfferName);
        return sprInfo;
    }

    private ProductOffer createProductOfferWihoutQuotaProfile(String productOfferId, String productOfferName) {
		QoSProfile qoSProfile = QosProfileFactory.createQosProfile().build();
        return createProductOffer(productOfferId, productOfferName, Arrays.asList(qoSProfile));
    }


    private static class DummyUMOperation extends UMOperation {

        private Collection<SubscriberUsage> usages;

        public DummyUMOperation(AlertListener alertListener, PolicyRepository policyRepository) {
            super(alertListener, policyRepository);
        }

        @Override
        public void insert(String SubscriberId, Collection<SubscriberUsage> usages, Transaction transaction) throws TransactionException, OperationFailedException {
            this.usages = usages;
        }

        @Override
        public void scheduleForUsageDelete(String subscriberID, String alternateID, String productOfferId, String resetReason, String parameter1, String parameter2, String parameter3, Transaction transaction) throws OperationFailedException, TransactionException {

        }

        public Collection<SubscriberUsage> getActualUsages() {
            return usages;
        }
    }

}
