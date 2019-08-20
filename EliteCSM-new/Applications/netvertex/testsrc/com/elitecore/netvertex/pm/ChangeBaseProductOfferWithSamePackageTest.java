package com.elitecore.netvertex.pm;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.constants.MonetaryBalanceType;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.pm.DummyPolicyRepository;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.QosProfileFactory;
import com.elitecore.corenetvertex.pm.factory.BasePackageFactory;
import com.elitecore.corenetvertex.pm.factory.QuotaProfileFactory;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.spr.*;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.DerbyUtil;
import com.elitecore.netvertex.pm.util.MockRnCPackage;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import java.sql.SQLException;
import java.util.*;

import static org.mockito.Mockito.*;

@Ignore
public class ChangeBaseProductOfferWithSamePackageTest {

    private SubscriberRepositoryImpl subscriberRepository;
    private SubscriberRepositoryTestSuite.SPROperationTestHelper helper;
    private DummyPolicyRepository policyRepository;
    private SPRInfoImpl sprInfo;
    private DummyTransactionFactoryBuilder transactionFactoryBuilder;
    private DummyTransactionFactory transactionFactory;
    private Transaction transaction;
    private Map<String, String> productOfferToBasePackageName;
    private Map<String, String> productOfferToBasePackageId;
    private ProductOffer productOffer;
    private ProductOfferStore productOfferStore;
    private MockRnCPackage rnCPackage;
    private DummyUMOperation umOperation;
    private RnCABMFOperation rnCABMFOperation;
    private ABMFOperation abmfOperation;
    private ABMFconfiguration abmFconfiguration;
    private MonetaryABMFOperationImpl monetaryABMFOperation;
    private EDRListener balanceEdrListener;
    private AlertListener alertListener;
    private DummyDBDataSource dbDataSource;

    private long currentTime = System.currentTimeMillis();
    private final String subscriberId = "SubscriberId1";
    private static final String RNC_PACKAGE_NAME = "rncPackage";
    private static final String RNC_PACKAGE_ID = "rncPackageId";
    private static final String DATA_PACKAGE_NAME = "dataPackage";
    private static final String DATA_PACKAGE_ID = "dataPackageId";
    private static final String DS_NAME = "test-DB";
    private static final String QUOTA_PROFILE_ID = "quotaProfileId";
    private static final String PRODUCT_OFFER_NAME = "ProductOfferName";
    private static final String PRODUCT_OFFER_ID = "ProductOfferId";

    @Mock
    SubscriberMonetaryBalance subscriberMonetaryBalance;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setUpClass() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        setUpDataBaseConfig();

        policyRepository = new DummyPolicyRepository();
        umOperation = spy(new ChangeBaseProductOfferWithSamePackageTest.DummyUMOperation(alertListener, policyRepository));
        abmFconfiguration = new ABMFconfigurationImpl(1,100,10);
        balanceEdrListener = mock(EDRListener.class);
        alertListener = mock(AlertListener.class);

        subscriberRepository = spy(new SubscriberRepositoryImpl("id","name", transactionFactory, alertListener,
                policyRepository, umOperation, abmFconfiguration,null, Collectionz.<String>newArrayList(),
                null,null,null, null,
                balanceEdrListener, null, null, "INR"));

        this.abmfOperation = spy(subscriberRepository.getAbmfOperation());
        doReturn(abmfOperation).when(subscriberRepository).getAbmfOperation();
        this.rnCABMFOperation = spy(subscriberRepository.getRnCAbmfOperation());
        doReturn(rnCABMFOperation).when(subscriberRepository).getRnCAbmfOperation();
        this.monetaryABMFOperation = spy(new MonetaryABMFOperationImpl(alertListener, 5000, new RecordProcessor.EmptyRecordProcessor<>(), null));
        doReturn(monetaryABMFOperation).when(subscriberRepository).getMonetaryABMFOperation();

        setUpSubscriberAndSubscriptionConfig();
    }

    @After
    public void afterDropTables() throws Exception {
        helper.dropProfileTables();
        helper.dropUsageTable();

        DBUtility.closeQuietly(transactionFactory.getConnection());
        DerbyUtil.closeDerby("TestingDB");
    }

    @Test
    public void transactionFactoryIsNotAliveThenThrowException() throws OperationFailedException {
        doReturn(false).when(transactionFactory).isAlive();

        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Unable to process reset balance for Subscriber ID: " + subscriberId + " . Reason: Datasource not available");
        subscriberRepository.processReset(sprInfo,null);
    }


    @Test
    public void transactionIsNullThrowException() throws Exception {
        treatConfig(productOffer);
        doReturn(null).when(transactionFactory).createTransaction();

        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Unable to process reset balance for Subscriber ID: " + subscriberId + " . Reason: Datasource not available");
        subscriberRepository.processReset(sprInfo,null);
        transactionFactory = spy ((DummyTransactionFactory) transactionFactoryBuilder.build());
    }

    @Test
    public void transactionIsFailedToStartThrowException() throws Exception {
        treatConfig(productOffer);

        doThrow(new TransactionException("Connection not found", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).begin();
        try {
            subscriberRepository.processReset(sprInfo,null);
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), Mockito.anyString(), Mockito.anyString());
        }
    }

   @Test
    public void dataPackageNotNullThenResetDataPackageRNCBased() throws Exception {

        ProductOffer productOfferNew = createProductOfferWith1QuotaProfile1ServiceRNCBased(PRODUCT_OFFER_ID+"New", PRODUCT_OFFER_NAME+"New", QuotaProfileType.RnC_BASED);
        sprInfo = createSubscriberProfile(PRODUCT_OFFER_NAME+"New", subscriberId);

        treatConfig(productOfferNew);

        doNothing().when(abmfOperation).resetQuota(anyString(),anyString(),anyInt(),any(Transaction.class));
        doNothing().when(rnCABMFOperation).resetNonMonetaryBalance(any(SPRInfo.class),anyString(),any(RnCPackage.class),any(Transaction.class));

        subscriberRepository.processReset(sprInfo,null);

        verify(abmfOperation, times(1)).resetQuota(anyString(),anyString(),anyInt(),any(Transaction.class));
    }

    @Test
    public void dataPackageNotNullThenResetDataPackageUMBased() throws Exception {

        treatConfig(productOffer);

        doNothing().when(umOperation).resetUsage(anyString(),anyString(),any(Transaction.class));
        doNothing().when(rnCABMFOperation).resetNonMonetaryBalance(any(SPRInfo.class),anyString(),any(RnCPackage.class),any(Transaction.class));

        subscriberRepository.processReset(sprInfo,null);

        verify(umOperation, times(1)).resetUsage(anyString(),anyString(),any(Transaction.class));
    }

    @Test
    public void dataPackageNullThenDoNothing() throws Exception {
        treatConfig(productOffer);

        doNothing().when(rnCABMFOperation).resetNonMonetaryBalance(any(SPRInfo.class),anyString(),any(RnCPackage.class),any(Transaction.class));
        doReturn(null).when(productOffer).getDataServicePkgData();

        subscriberRepository.processReset(sprInfo,null);

        verify(umOperation, times(0)).resetUsage(anyString(),anyString(),any(Transaction.class));
        verify(abmfOperation, times(0)).resetQuota(anyString(),anyString(),anyInt(),any(Transaction.class));
    }

    @Test
    public void rncPackageIsNotNullThenResetNonMonetaryBalance() throws Exception {
        treatConfig(productOffer);

        doNothing().when(rnCABMFOperation).resetNonMonetaryBalance(any(SPRInfo.class),anyString(),any(RnCPackage.class),any(Transaction.class));
        doReturn(null).when(productOffer).getDataServicePkgData();

        subscriberRepository.processReset(sprInfo,null);

        verify(rnCABMFOperation, times(1)).resetNonMonetaryBalance(any(SPRInfo.class),anyString(),any(RnCPackage.class),any(Transaction.class));
    }

    @Test
    public void rncPackageIsNotNullAndtryToResetNonMonetaryBalanceThrowsOperationException() throws Exception {
        treatConfig(productOffer);

        doThrow(OperationFailedException.class).when(rnCABMFOperation).resetNonMonetaryBalance(any(SPRInfo.class),anyString(),any(RnCPackage.class),any(Transaction.class));
        doReturn(null).when(productOffer).getDataServicePkgData();

        expectedException.expect(OperationFailedException.class);
        subscriberRepository.processReset(sprInfo,null);
    }

    @Test
    public void rncPackageIsNotNullAndtryToResetNonMonetaryBalanceThrowsSQLException() throws Exception {
        treatConfig(productOffer);

        doThrow(SQLException.class).when(rnCABMFOperation).resetNonMonetaryBalance(any(SPRInfo.class),anyString(),any(RnCPackage.class),any(Transaction.class));
        doReturn(null).when(productOffer).getDataServicePkgData();

        expectedException.expect(OperationFailedException.class);
        subscriberRepository.processReset(sprInfo,null);
    }

    @Test
    public void verifyAddMonetaryBalance() throws Exception {
        treatConfig(productOffer);

        doReturn(null).when(productOffer).getDataServicePkgData();
        doNothing().when(rnCABMFOperation).resetNonMonetaryBalance(any(SPRInfo.class),anyString(),any(RnCPackage.class),any(Transaction.class));

        doReturn(Double.valueOf("100")).when(productOffer).getCreditBalance();
        doReturn(subscriberMonetaryBalance).when(subscriberRepository).getMonetaryBalance(anyString(),any(java.util.function.Predicate.class));
        doReturn(null).when(subscriberMonetaryBalance).getMainBalance();
        doNothing().when(monetaryABMFOperation).addBalance(any(MonetaryBalance.class), any(Transaction.class));

        subscriberRepository.processReset(sprInfo,null);
        verify(monetaryABMFOperation, times(1)).addBalance(any(MonetaryBalance.class), any(Transaction.class));
        verify(balanceEdrListener, times(1)).addMonetaryEDR(any(SubscriberMonetaryBalanceWrapper.class), anyString(), anyString());
    }

    @Test
    public void verifyUpdateMonetaryBalance() throws Exception {
        treatConfig(productOffer);

        doReturn(null).when(productOffer).getDataServicePkgData();
        doNothing().when(rnCABMFOperation).resetNonMonetaryBalance(any(SPRInfo.class),anyString(),any(RnCPackage.class),any(Transaction.class));

        doReturn(Double.valueOf("100")).when(productOffer).getCreditBalance();
        doReturn(subscriberMonetaryBalance).when(subscriberRepository).getMonetaryBalance(anyString(),any(java.util.function.Predicate.class));
        doReturn(createmonetaryBalance()).when(subscriberMonetaryBalance).getMainBalance();
        doNothing().when(monetaryABMFOperation).updateBalance(any(MonetaryBalance.class), any(Transaction.class));

        subscriberRepository.processReset(sprInfo,null);
        verify(monetaryABMFOperation, times(1)).updateBalance(any(MonetaryBalance.class), any(Transaction.class));
        verify(balanceEdrListener, times(1)).updateMonetaryEDR(any(SubscriberMonetaryBalanceWrapper.class), anyString(), anyString());
    }

    private void setUpSubscriberAndSubscriptionConfig() {
        this.productOfferToBasePackageName = new HashMap<>();
        this.productOfferToBasePackageId = new HashMap<>();
        this.productOfferToBasePackageName.put(PRODUCT_OFFER_NAME, DATA_PACKAGE_NAME);
        this.productOfferToBasePackageId.put(PRODUCT_OFFER_NAME, DATA_PACKAGE_ID);
        rnCPackage = MockRnCPackage.createBase(RNC_PACKAGE_ID, RNC_PACKAGE_NAME);
        productOffer = createProductOfferWith1QuotaProfile1ServiceRNCBased(PRODUCT_OFFER_ID, PRODUCT_OFFER_NAME, QuotaProfileType.USAGE_METERING_BASED);
        productOfferStore = spy(new ProductOfferStore());
        sprInfo = createSubscriberProfile(PRODUCT_OFFER_NAME, subscriberId);
    }

    private void setUpDataBaseConfig() throws Exception {
        dbDataSource = new DummyDBDataSource("1", DS_NAME,
                "jdbc:derby:memory:TestingDB;create=true", "", "", 1,
                5000, 3000);
        transactionFactoryBuilder = spy(new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1));
        transactionFactory = (DummyTransactionFactory) transactionFactoryBuilder.build();
        helper = new SubscriberRepositoryTestSuite.SPROperationTestHelper(transactionFactory);
        transactionFactory = spy(transactionFactory);
        transaction = mock(Transaction.class);
        helper.createProfileTable();
        helper.createUsageTable();
    }

    private MonetaryBalance createmonetaryBalance() {
        MonetaryBalance monetaryBalance = new MonetaryBalance(UUID.randomUUID().toString(),"1",
                "1",1,1,1,0,0, currentTime, currentTime,
                "INR",
                MonetaryBalanceType.DEFAULT.name(), currentTime, 0,"", "");
        return monetaryBalance;
    }

    private void treatConfig(ProductOffer productOffer) throws OperationFailedException, TransactionException, SQLException {
        doReturn(true).when(transactionFactory).isAlive();
        doReturn(productOffer).when(productOfferStore).byName(any());
        doReturn(transaction).when(transactionFactory).createTransaction();
    }

    private ProductOffer createProductOfferWith1QuotaProfile1ServiceRNCBased(String productOfferId, String productOfferName, QuotaProfileType quotaProfileType) {
        QuotaProfile quotaProfile = QuotaProfileFactory.createQuotaProfileWithRandomUsage(QUOTA_PROFILE_ID);
        QoSProfile qoSProfile = QosProfileFactory.createQosProfile().withQuotaProfile(quotaProfile).build();
        return createProductOfferWithDataAndRnCPackage(productOfferId, productOfferName, Arrays.asList(qoSProfile), quotaProfileType);
    }

    private SPRInfoImpl createSubscriberProfile(String productOfferName, String subscriberId) {
        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(subscriberId);
        sprInfo.setProductOffer(productOfferName);
        return sprInfo;
    }

    private ProductOffer createProductOfferWithDataAndRnCPackage(String productOfferId, String productOfferName, List<QoSProfile> qoSProfiles, QuotaProfileType quotaProfileType) {
        BasePackage basePackage = new BasePackageFactory.BasePackageBuilder(null, productOfferToBasePackageId.get(productOfferName),
                productOfferToBasePackageName.get(productOfferName))
                .withQoSProfiles(qoSProfiles)
                .withAvailabilityStatus(PkgStatus.ACTIVE)
                .withQuotaProfileType(quotaProfileType)
                .build();

        com.elitecore.netvertex.pm.util.MockProductOffer productOffer = com.elitecore.netvertex.pm.util.MockProductOffer.create(policyRepository, productOfferId, productOfferName);
        productOffer.addRnCPackage("SMS","SMSId",rnCPackage);
        productOffer.addDataPackage(basePackage);
        policyRepository.addProductOffer(productOffer);
        return productOffer;
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

    }
}
