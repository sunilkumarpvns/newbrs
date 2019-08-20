package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.factory.ProductOfferDataFactory;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.BillingCycleAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.DailyAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.WeeklyAllowedUsage;
import com.elitecore.corenetvertex.pm.util.MockBasePackage;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.spr.util.ResetTimeUtility;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.unitils.reflectionassert.ReflectionAssert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ABMFOperationAddBalanceTest {

    private static final int HIGH_RESPONSE_TIME_LIMIT_IN_MS = 100;
    private static final String DS_NAME = "test-DB";
    private static final String TRANSACTION = "transaction";
    private static final String PREPARED_STATEMENT = "preparedStatement";
    private static final String BEGIN = "begin";
    private static final String EXECUTE = "execute";


    @Mock
    private AlertListener alertListener;
    @Mock private PolicyRepository policyRepository;
    private DummyTransactionFactory transactionFactory;
    private ABMFOperation abmfOperation;
    private ABMFOperation fixedTimeSourceABMFOperation;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private HibernateSessionFactory hibernateSessionFactory;
    private String pccProfileName = "pccProfileName";


    @Before
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        String folderName = UUID.randomUUID().toString();
        //jdbc:h2:mem:create-drop
        String sid = UUID.randomUUID().toString();
        String connectionURL = "jdbc:h2:mem:" + sid;
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, connectionURL, "", "", 1, 5000, 3000);
        transactionFactory = new DummyTransactionFactory(dbDataSource);
        transactionFactory.createTransaction();
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.connection.url", connectionURL);
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        hibernateSessionFactory = HibernateSessionFactory.create("hibernate/test-hibernate.cfg.xml", hibernateProperties);
        abmfOperation = new ABMFOperation(alertListener, policyRepository,10, 2);
        DataBalanceResetDBOperation dataBalanceResetDbOperation = new DataBalanceResetDBOperation(10, alertListener);
        BalanceResetOperation balanceResetOperation = new BalanceResetOperation(policyRepository, TimeSource.systemTimeSource());
        fixedTimeSourceABMFOperation = new ABMFOperation(alertListener,policyRepository, 10, 2,getFixedTimeSource(),new ABMFResetOperations(balanceResetOperation, null, null,getFixedTimeSource(), new DataBalanceOperationFactory()));
    }

    private TimeSource getFixedTimeSource(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 5);
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.YEAR, 2018);
        return new FixedTimeSource(calendar.getTimeInMillis());
    }

    @After
    public void tearDownConnection() throws Exception {
        Connection connection = transactionFactory.getConnection();
        if(Objects.nonNull(connection)) {
            DBUtility.closeQuietly(connection);
        }
        try {
            hibernateSessionFactory.shutdown();

        } catch (Exception ex) {
            LogManager.getLogger().trace(ex);
        }

    }


    protected NonMonetoryBalance createServiceRGNonMonitoryBalances(String id) {
        Random random = new Random();

        NonMonetoryBalance serviceRgNonMonitoryBalance = new NonMonetoryBalance.NonMonetaryBalanceBuilder(id,
                org.apache.commons.lang3.RandomUtils.nextInt(0, Integer.MAX_VALUE),
                UUID.randomUUID().toString(),
                1l,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                random.nextInt(2),
                UUID.randomUUID().toString(), ResetBalanceStatus.NOT_RESET, null, null)
                .withBillingCycleVolumeBalance(random.nextInt(),random.nextInt()).withBillingCycleResetTime(random.nextInt())
                .withBillingCycleTimeBalance(random.nextInt(),random.nextInt())
                .withDailyUsage(random.nextInt(),random.nextInt()).withDailyResetTime(random.nextInt())
                .withWeeklyUsage(random.nextInt(),random.nextInt()).withWeeklyResetTime(random.nextInt())
                .withReservation(random.nextInt(),random.nextInt())
                .build();
        return serviceRgNonMonitoryBalance;
    }


    @Test
    public void addBalanceForEachServiceDefinedFupLeveldsForTotalVolumeUnitType() throws OperationFailedException,TransactionException {

        SPRInfo sprInfo = createSPRInfo();

        Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage = new HashMap<>();

        aggregationKeyToAllowedUsage.put(AggregationKey.BILLING_CYCLE, new BillingCycleAllowedUsage(100,200,300,400, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.DAILY, new DailyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.WEEKLY, new WeeklyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));

        DataServiceType dataServiceType = new DataServiceType("test", "test", 1, Collections.emptyList(), Collections.emptyList());

        RatingGroup ratingGroup = new RatingGroup(UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),1);
        RncProfileDetail rncProfileDetail = new RncProfileDetail("test", dataServiceType, 0, ratingGroup,
				aggregationKeyToAllowedUsage, 0, 0, 0, 0, DataUnit.BYTE.name(),
				TimeUnit.SECOND.name(), 0.0, null, null, VolumeUnitType.TOTAL, "test", true, "pccProfileName", 0, 0, "test");

        Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails = new HashMap<>();
        fupLevelServiceWiseQuotaProfileDetails.put("test",rncProfileDetail);

        MockBasePackage base = MockBasePackage.create("1", "Base").quotaProfileTypeIsRnC().mockQuotaProfie();
        ProductOffer productOffer = getProductOffer(base);

        QuotaProfile quotaProfile = base.getQuotaProfiles().get(0);
        when(quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()).thenReturn(Arrays.asList(fupLevelServiceWiseQuotaProfileDetails));
        when(quotaProfile.getRenewalInterval()).thenReturn(30);
        when(quotaProfile.getRenewalIntervalUnit()).thenReturn(RenewalIntervalUnit.DAY);
        when(productOffer.getDataServicePkgData()).thenReturn(base);

        abmfOperation.addBalance(sprInfo.getSubscriberIdentity(), null, productOffer, getTransaction(transactionFactory), 1);

        List<TblmDataBalanceEntity> actualEntities = hibernateSessionFactory.get(TblmDataBalanceEntity.class);

        TblmDataBalanceEntity expectedDataBalanceEntity = getExpectedDataBalanceEntity(sprInfo, rncProfileDetail, base, actualEntities, VolumeUnitType.TOTAL);

        ReflectionAssert.assertLenientEquals(Arrays.asList(expectedDataBalanceEntity), actualEntities);
    }

    private ProductOffer getProductOffer(MockBasePackage base) {
        ProductOfferData productOfferData =  new ProductOfferDataFactory()
                .withId("1").withName("name").withStatus("Random")
                .withMode("LIVE").withType("BASE")
                .withDataServicePkgId(base.getId()).build();
        return new ProductOffer(
                productOfferData.getId(), productOfferData.getName(), productOfferData.getDescription(),
                PkgType.BASE, PkgMode.LIVE, productOfferData.getValidityPeriod(),
                null, productOfferData.getSubscriptionPrice() != null ? productOfferData.getSubscriptionPrice() : 0d, productOfferData.getCreditBalance() != null ? productOfferData.getCreditBalance() : 0d,
                null, null, null,
                productOfferData.getDataServicePkgId(), productOfferData.getGroupList(), productOfferData.getAvailabilityStartDate(),
                productOfferData.getAvailabilityEndDate(), PolicyStatus.SUCCESS,  null, null, (Objects.nonNull(productOfferData.isFnFOffer()) && productOfferData.isFnFOffer().booleanValue()), productOfferData.getParam1(), productOfferData.getParam2(), policyRepository,null,null,new HashMap<>(),productOfferData.getCurrency()
        );
    }

    @Test
    public void addBalanceForEachServiceDefinedFupLeveldsForUploadVolumeUnitType() throws OperationFailedException,TransactionException {

        SPRInfo sprInfo = createSPRInfo();

        Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage = new HashMap<>();

        aggregationKeyToAllowedUsage.put(AggregationKey.BILLING_CYCLE, new BillingCycleAllowedUsage(100,200,300,400, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.DAILY, new DailyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.WEEKLY, new WeeklyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));

        DataServiceType dataServiceType = new DataServiceType("test", "test", 1, Collections.emptyList(), Collections.emptyList());

        RatingGroup ratingGroup = new RatingGroup(UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),1);
        RncProfileDetail rncProfileDetail = new RncProfileDetail("test", dataServiceType, 0, ratingGroup,
				aggregationKeyToAllowedUsage, 0, 0, 0, 0, DataUnit.BYTE.name(),
				TimeUnit.SECOND.name(), 0.0, null, null, VolumeUnitType.UPLOAD, "test", true, pccProfileName, 0, 0, "test");
        Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails = new HashMap<>();
        fupLevelServiceWiseQuotaProfileDetails.put("test",rncProfileDetail);

        MockBasePackage base = MockBasePackage.create("1", "Base").quotaProfileTypeIsRnC().mockQuotaProfie();
        QuotaProfile quotaProfile = base.getQuotaProfiles().get(0);
        when(quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()).thenReturn(Arrays.asList(fupLevelServiceWiseQuotaProfileDetails));
        ProductOffer productOffer = getProductOffer(base);
        when(productOffer.getDataServicePkgData()).thenReturn(base);
        abmfOperation.addBalance(sprInfo.getSubscriberIdentity(), null,productOffer , getTransaction(transactionFactory), null);

        List<TblmDataBalanceEntity> actualEntities = hibernateSessionFactory.get(TblmDataBalanceEntity.class);

        TblmDataBalanceEntity expectedDataBalanceEntity = getExpectedDataBalanceEntity(sprInfo, rncProfileDetail, base, actualEntities, VolumeUnitType.UPLOAD);

        ReflectionAssert.assertLenientEquals(Arrays.asList(expectedDataBalanceEntity), actualEntities);
    }

    @Test
    public void addBalanceForEachServiceDefinedFupLeveldsForUploadDownloadUnitType() throws OperationFailedException,TransactionException {

        SPRInfo sprInfo = createSPRInfo();

        Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage = new HashMap<>();

        aggregationKeyToAllowedUsage.put(AggregationKey.BILLING_CYCLE, new BillingCycleAllowedUsage(100,200,300,400, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.DAILY, new DailyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.WEEKLY, new WeeklyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));

        DataServiceType dataServiceType = new DataServiceType("test", "test", 1, Collections.emptyList(), Collections.emptyList());

        RatingGroup ratingGroup = new RatingGroup(UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),1);
        RncProfileDetail rncProfileDetail = new RncProfileDetail("test", dataServiceType, 0, ratingGroup, aggregationKeyToAllowedUsage, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name() , 0.0, null, null, VolumeUnitType.DOWNLOAD, "test", true, pccProfileName, 0, 0, "test");

        Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails = new HashMap<>();
        fupLevelServiceWiseQuotaProfileDetails.put("test",rncProfileDetail);

        MockBasePackage base = MockBasePackage.create("1", "Base").quotaProfileTypeIsRnC().mockQuotaProfie();
        QuotaProfile quotaProfile = base.getQuotaProfiles().get(0);
        when(quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()).thenReturn(Arrays.asList(fupLevelServiceWiseQuotaProfileDetails));
        ProductOffer productOffer = getProductOffer(base);
        when(productOffer.getDataServicePkgData()).thenReturn(base);
        abmfOperation.addBalance(sprInfo.getSubscriberIdentity(), null, productOffer, getTransaction(transactionFactory), null);

        List<TblmDataBalanceEntity> actualEntities = hibernateSessionFactory.get(TblmDataBalanceEntity.class);

        TblmDataBalanceEntity expectedDataBalanceEntity = getExpectedDataBalanceEntity(sprInfo, rncProfileDetail, base, actualEntities, VolumeUnitType.DOWNLOAD);

        ReflectionAssert.assertLenientEquals(Arrays.asList(expectedDataBalanceEntity), actualEntities);
    }

    @Test
    public void addBalanceGrantsProratedQuotaForTillBillDate() throws OperationFailedException,TransactionException {

        SPRInfo sprInfo = createSPRInfo();

        Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage = new HashMap<>();

        aggregationKeyToAllowedUsage.put(AggregationKey.BILLING_CYCLE, new BillingCycleAllowedUsage(100,200,300,400, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.DAILY, new DailyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.WEEKLY, new WeeklyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));

        DataServiceType dataServiceType = new DataServiceType("test", "test", 1, Collections.emptyList(), Collections.emptyList());

        RatingGroup ratingGroup = new RatingGroup(UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),1);
        RncProfileDetail rncProfileDetail = new RncProfileDetail("test", dataServiceType, 0, ratingGroup, aggregationKeyToAllowedUsage, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name() , 0.0, null, null, VolumeUnitType.DOWNLOAD, "test", true, pccProfileName, 0,0, "test");

        Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails = new HashMap<>();
        fupLevelServiceWiseQuotaProfileDetails.put("test",rncProfileDetail);

        MockBasePackage base = MockBasePackage.create("1", "Base").quotaProfileTypeIsRnC().mockQuotaProfie();
        QuotaProfile quotaProfile = base.getQuotaProfiles().get(0);
        when(quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()).thenReturn(Arrays.asList(fupLevelServiceWiseQuotaProfileDetails));
        when(quotaProfile.getProration()).thenReturn(true);
        when(quotaProfile.getRenewalIntervalUnit()).thenReturn(RenewalIntervalUnit.TILL_BILL_DATE);
        when(quotaProfile.getRenewalInterval()).thenReturn(1);
        ProductOffer productOffer = getProductOffer(base);
        when(productOffer.getDataServicePkgData()).thenReturn(base);
        fixedTimeSourceABMFOperation.addBalance(sprInfo.getSubscriberIdentity(), null, productOffer, getTransaction(transactionFactory), sprInfo.getBillingDate());

        List<TblmDataBalanceEntity> actualEntities = hibernateSessionFactory.get(TblmDataBalanceEntity.class);
        TblmDataBalanceEntity savedBalance = actualEntities.get(0);
        Assert.assertTrue(savedBalance.getBillingCycleAvailableVolume()==150l);
        Assert.assertTrue(savedBalance.getBillingCycleTime()==300l);


    }

    private TblmDataBalanceEntity getExpectedDataBalanceEntity(SPRInfo sprInfo, RncProfileDetail rncProfileDetail, MockBasePackage base, List<TblmDataBalanceEntity> actualEntities, VolumeUnitType volumeUnitType) {
        TblmDataBalanceEntity expectedDataBalanceEntity = new TblmDataBalanceEntity();
        expectedDataBalanceEntity.setId(actualEntities.get(0).getId());
        expectedDataBalanceEntity.setSubscriberId(sprInfo.getSubscriberIdentity());
        expectedDataBalanceEntity.setPackageId(base.getId());
        expectedDataBalanceEntity.setProductOfferId("1");
        expectedDataBalanceEntity.setSubscriptionId(null);
        expectedDataBalanceEntity.setQuotaProfileId(base.getQuotaProfiles().get(0).getId());
        expectedDataBalanceEntity.setServiceId(rncProfileDetail.getDataServiceType().getServiceIdentifier());
        expectedDataBalanceEntity.setRatingGroup(rncProfileDetail.getRatingGroup().getIdentifier());
        expectedDataBalanceEntity.setBillingCycleTotalVolume(volumeUnitType.getVolumeInBytes(rncProfileDetail.getBillingCycleAllowedUsage()));
        expectedDataBalanceEntity.setBillingCycleTime(rncProfileDetail.getBillingCycleAllowedUsage().getTimeInSeconds());
        expectedDataBalanceEntity.setDailyVolume(rncProfileDetail.getDailyAllowedUsage().getTotalInBytes());
        expectedDataBalanceEntity.setDailyTime(rncProfileDetail.getDailyAllowedUsage().getTimeInSeconds());
        expectedDataBalanceEntity.setWeeklyVolume(rncProfileDetail.getWeeklyAllowedUsage().getTotalInBytes());
        expectedDataBalanceEntity.setWeeklyTime(rncProfileDetail.getWeeklyAllowedUsage().getTimeInSeconds());
        expectedDataBalanceEntity.setDailyResetTime(new Timestamp(ResetTimeUtility.calculateDailyResetTime()));
        expectedDataBalanceEntity.setWeeklyResetTime(new Timestamp(ResetTimeUtility.calculateWeeklyResetTime()));
        expectedDataBalanceEntity.setQuotaExpiryTime(actualEntities.get(0).getQuotaExpiryTime());
        expectedDataBalanceEntity.setReservationVolume(0l);
        expectedDataBalanceEntity.setReservationTime(0l);
        expectedDataBalanceEntity.setLastUpdateTime(actualEntities.get(0).getLastUpdateTime());
        expectedDataBalanceEntity.setStartTime(actualEntities.get(0).getStartTime());
        expectedDataBalanceEntity.setStatus(actualEntities.get(0).getStatus());
        expectedDataBalanceEntity.setRenewalInterval(actualEntities.get(0).getRenewalInterval());
        return expectedDataBalanceEntity;
    }

    @Test
    public void test_add_balance_should_generate_DB_HIGH_QUERY_RESPONSE_TIME_alert_when_execution_time_is_high() throws Exception{
        TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlertForDirectDebitBalance();

        SPRInfo sprInfo = createSPRInfo();

        Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage = new HashMap<>();

        aggregationKeyToAllowedUsage.put(AggregationKey.BILLING_CYCLE, new BillingCycleAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.DAILY, new DailyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.WEEKLY, new WeeklyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));

        DataServiceType dataServiceType = new DataServiceType("test", "test", 1, Collections.emptyList(), Collections.emptyList());

        RatingGroup ratingGroup = new RatingGroup(UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),1);
        RncProfileDetail rncProfileDetail = new RncProfileDetail("test", dataServiceType, 0, ratingGroup, aggregationKeyToAllowedUsage, 0, 0, 0, 0,DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null, null, VolumeUnitType.TOTAL, "test", true, pccProfileName, 0, 0, "test");

        Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails = new HashMap<>();
        fupLevelServiceWiseQuotaProfileDetails.put("test",rncProfileDetail);

        MockBasePackage base = MockBasePackage.create("1", "Base").quotaProfileTypeIsRnC().mockQuotaProfie();
        QuotaProfile quotaProfile = base.getQuotaProfiles().get(0);
        when(policyRepository.getBasePackageDataByName(sprInfo.getProductOffer())).thenReturn(base);
        ProductOffer productOffer = getProductOffer(base);
        when(productOffer.getDataServicePkgData()).thenReturn(base);
        when(quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()).thenReturn(Arrays.asList(fupLevelServiceWiseQuotaProfileDetails));
        abmfOperation.addBalance(sprInfo.getSubscriberIdentity(), null,productOffer , getTransaction(transactionFactory), null);

        verify(alertListener, only())
                .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void test_add_balance_should_generate_QUERY_TIMEOUT_alert_when_query_timeout_time_is_high() throws Exception {
        TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTAlertForDirectDebitBalance();

        try {
            SPRInfo sprInfo = createSPRInfo();

            Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage = new HashMap<>();

            aggregationKeyToAllowedUsage.put(AggregationKey.BILLING_CYCLE, new BillingCycleAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
            aggregationKeyToAllowedUsage.put(AggregationKey.DAILY, new DailyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
            aggregationKeyToAllowedUsage.put(AggregationKey.WEEKLY, new WeeklyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));

            DataServiceType dataServiceType = new DataServiceType("test", "test", 1, Collections.emptyList(), Collections.emptyList());
            RatingGroup ratingGroup = new RatingGroup(UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),1);
            RncProfileDetail rncProfileDetail = new RncProfileDetail("test", dataServiceType, 0, ratingGroup, aggregationKeyToAllowedUsage, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null, null, VolumeUnitType.TOTAL, "test", true, pccProfileName, 0,0, "test");

            Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails = new HashMap<>();
            fupLevelServiceWiseQuotaProfileDetails.put("test",rncProfileDetail);

            MockBasePackage base = MockBasePackage.create("1", "Base").quotaProfileTypeIsRnC().mockQuotaProfie();
            QuotaProfile quotaProfile = base.getQuotaProfiles().get(0);
            when(quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()).thenReturn(Arrays.asList(fupLevelServiceWiseQuotaProfileDetails));
            abmfOperation.addBalance(sprInfo.getSubscriberIdentity(), null, getProductOffer(base), getTransaction(transactionFactory), null);
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito
                            .anyString());
        }
    }

    @Test
    public void test_add_balance_mark_Dead_when_sql_exception_is_not_thrown_due_to_QUERY_TIME_OUT_ERROR() throws Exception {
        setupMockToGenerateExceptionOnUpdateTransaction(EXECUTE, SQLException.class);
        Transaction transaction = transactionFactory.createTransaction();
        SPRInfo sprInfo = createSPRInfo();

        Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage = new HashMap<>();

        aggregationKeyToAllowedUsage.put(AggregationKey.BILLING_CYCLE, new BillingCycleAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.DAILY, new DailyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.WEEKLY, new WeeklyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        DataServiceType dataServiceType = new DataServiceType("test", "test", 1, Collections.emptyList(), Collections.emptyList());
        RatingGroup ratingGroup = new RatingGroup(UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),1);
        RncProfileDetail rncProfileDetail = new RncProfileDetail("test", dataServiceType, 0, ratingGroup, aggregationKeyToAllowedUsage, 0,0,0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null, null, VolumeUnitType.TOTAL, "test", true, pccProfileName, 0,0, "test");

        Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails = new HashMap<>();
        fupLevelServiceWiseQuotaProfileDetails.put("test",rncProfileDetail);

        MockBasePackage base = MockBasePackage.create("1", "Base").quotaProfileTypeIsRnC().mockQuotaProfie();
        QuotaProfile quotaProfile = base.getQuotaProfiles().get(0);
        when(policyRepository.getBasePackageDataByName(sprInfo.getProductOffer())).thenReturn(base);
        when(quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()).thenReturn(Arrays.asList(fupLevelServiceWiseQuotaProfileDetails));
        when(transaction.isDBDownSQLException(any(SQLException.class))).thenReturn(true);
        try {
             ProductOffer productOffer = getProductOffer(base);
            when(productOffer.getDataServicePkgData()).thenReturn(base);
            abmfOperation.addBalance(createSPRInfo().getSubscriberIdentity(), null, productOffer, transaction, null);
        } catch (OperationFailedException e) {

        }
        verify(transaction).markDead();
    }

    @Test
    public void test_add_balance_alert_is_generated_when_sql_exception_is_thrown_due_to_QUERY_TIME_OUT_ERROR() throws Exception {
        SQLException sqlException = new SQLException("query timeout", "timeout", 1013);
        setupMockToGenerateExceptionOnUpdateTransaction(EXECUTE, sqlException);
        Transaction transaction = transactionFactory.createTransaction();

        try {
            SPRInfo sprInfo = createSPRInfo();

            Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage = new HashMap<>();

            aggregationKeyToAllowedUsage.put(AggregationKey.BILLING_CYCLE, new BillingCycleAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
            aggregationKeyToAllowedUsage.put(AggregationKey.DAILY, new DailyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
            aggregationKeyToAllowedUsage.put(AggregationKey.WEEKLY, new WeeklyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));

            DataServiceType dataServiceType = new DataServiceType("test", "test",0l, Collections.<String>emptyList(), Collections.emptyList());

            RatingGroup ratingGroup = new RatingGroup(UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),1);
            RncProfileDetail rncProfileDetail = new RncProfileDetail("test", dataServiceType, 0, ratingGroup, aggregationKeyToAllowedUsage, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null, null, VolumeUnitType.TOTAL, "test", true, pccProfileName, 0,0, "test");

            Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails = new HashMap<>();
            fupLevelServiceWiseQuotaProfileDetails.put("test",rncProfileDetail);

            MockBasePackage base = MockBasePackage.create("1", "Base").quotaProfileTypeIsRnC().mockQuotaProfie();
            QuotaProfile quotaProfile = base.getQuotaProfiles().get(0);
            when(policyRepository.getBasePackageDataByName(sprInfo.getProductOffer())).thenReturn(base);
            when(quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()).thenReturn(Arrays.asList(fupLevelServiceWiseQuotaProfileDetails));
            abmfOperation.addBalance(sprInfo.getSubscriberIdentity(), null, getProductOffer(base),transaction, null);
        } catch (OperationFailedException e) {

            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString());
        }
    }

    @Test
    public void throwsOperationFailedExceptionWhenNullPackageIsPassed() throws Exception {
        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Error while subscriber ABMF Operation, Reason. Package is null");
        Transaction transaction = transactionFactory.createTransaction();
        abmfOperation.addBalance(createSPRInfo().getSubscriberIdentity(), null,(ProductOffer)null,transaction, null);
    }

    private SPRInfo createSPRInfo(){
        SPRInfoImpl sprInfoImpl = new SPRInfoImpl();
        sprInfoImpl.setBillingDate(26);
        sprInfoImpl.setSubscriberIdentity("test");
        return sprInfoImpl;
    }

    private Transaction getTransaction(TransactionFactory transactionFactory) throws  OperationFailedException, TransactionException{
        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to perform add balance operation for subscriber ID. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to perform add balance operation for subscriber ID. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        transaction.begin();

        return transaction;
    }

    private void setupMockToGenerateExceptionOnUpdateTransaction(String whenToThrow, Class<? extends Throwable> exceptionToBeThrown) throws Exception{

        Throwable throwable = exceptionToBeThrown.getConstructor(String.class).newInstance("from test");
        setupMockToGenerateExceptionOnUpdateTransaction(whenToThrow, throwable);
    }

    private void setupMockToGenerateExceptionOnUpdateTransaction(String whenToThrow,  Throwable throwable) throws Exception{
        Transaction transaction = mock(Transaction.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        transactionFactory = spy(transactionFactory);
        doReturn(transaction).when(transactionFactory).createTransaction();
        doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());
        doReturn(1).when(preparedStatement).executeUpdate();

        if (TRANSACTION.equals(whenToThrow)) {
            doReturn(null).when(transactionFactory).createTransaction();
        } else if (PREPARED_STATEMENT.equals(whenToThrow)) {
            doThrow(throwable).when(transaction).prepareStatement(Mockito.anyString());
        } else if (BEGIN.equals(whenToThrow)) {
            doThrow(throwable).when(transaction).begin();
        } else if (EXECUTE.equals(whenToThrow)) {
            doThrow(throwable).when(preparedStatement).executeUpdate();
        }
    }

    private TransactionFactory setUpMockToGenerateQUERYTIMEOUTAlertForDirectDebitBalance() throws Exception{
        TransactionFactory factory = spy(transactionFactory);
        doReturn(true).when(factory).isAlive();
        Transaction transaction = mock(Transaction.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        doReturn(transaction).when(factory).createTransaction();
        doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("query timeout", "timeout", 1013));

        return factory;

    }

    private TransactionFactory setUpMockToGenerateHIGHRESPONSEAlertForDirectDebitBalance() throws Exception{
        TransactionFactory factory = spy(transactionFactory);
        Transaction transaction = mock(Transaction.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        doReturn(transaction).when(factory).createTransaction();
        doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

        when(preparedStatement.executeUpdate()).thenAnswer(new Answer<Integer>() {

            public Integer answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {
                long startTime = System.currentTimeMillis();
                long elapsedTime = 0;
                while (elapsedTime < HIGH_RESPONSE_TIME_LIMIT_IN_MS + 10) {
                    elapsedTime = System.currentTimeMillis() - startTime;
                }

                return 1;
            }

        });

        return factory;
    }

}
