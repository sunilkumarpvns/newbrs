package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.factory.BasePackageFactory;
import com.elitecore.corenetvertex.pm.factory.ProductOfferDataFactory;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.BillingCycleAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.DailyAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.WeeklyAllowedUsage;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import junitparams.JUnitParamsRunner;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class ABMFOperationResetQuotaTest {

    private static final String DS_NAME = "test-DB";
    private static final String PRODUCT_OFFER_ID = "1";
    private static final String NON_MONETARY_BAL_ID = UUID.randomUUID().toString();
    private static final String TRANSACTION = "transaction";
    private static final String PREPARED_STATEMENT = "preparedStatement";
    private static final String BEGIN = "begin";
    private static final String EXECUTE = "execute";
    private BasePackage basepackage;

    @Rule public ExpectedException expectedException = ExpectedException.none();

    @Mock private AlertListener alertListener;
    @Mock private PolicyRepository policyRepository;private DummyTransactionFactory transactionFactory;
    @Mock private ProductOfferStore productOfferStore;
    @Mock private BalanceResetOperation balanceResetOperation;

    private ABMFOperation abmfOperation;
    private ABMFOperation fixedTimeSourceABMFOperation;
    private HibernateSessionFactory hibernateSessionFactory;
    private QuotaProfile quotaProfile;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        String sid = UUID.randomUUID().toString();
        String connectionURL = "jdbc:h2:mem:"+ sid;
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, connectionURL, "", "", 1, 5000, 3000);
        transactionFactory = new DummyTransactionFactory(dbDataSource);
        transactionFactory.createTransaction();
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.connection.url", connectionURL);
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        hibernateSessionFactory = HibernateSessionFactory.create("hibernate/test-hibernate.cfg.xml", hibernateProperties);

        setupPackage();
        abmfOperation = new ABMFOperation(alertListener, policyRepository,200,300);
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

    public void setupPackage(){

        BasePackageFactory.BasePackageBuilder basePackageBuilder = new BasePackageFactory.BasePackageBuilder(null, "base_id", "base_package");
        basePackageBuilder.withQuotaProfileType(QuotaProfileType.USAGE_METERING_BASED).withQoSProfiles(new ArrayList<>());
        basepackage = basePackageBuilder.build();

        basepackage = Mockito.spy(basepackage);
        ProductOffer productOffer = getProductOffer(basepackage);

        spyQuotaProfileForPackage();
        when(policyRepository.getProductOffer()).thenReturn(productOfferStore);
        when(policyRepository.getPkgDataById("base_id")).thenReturn(basepackage);
        when(policyRepository.getProductOffer().byId(productOffer.getId())).thenReturn(productOffer);
    }

    public void spyQuotaProfileForPackage(){
        List<QuotaProfile> quotaProfileList =  new ArrayList<>();

        List<Map<String, QuotaProfileDetail>> fupLevelserviceWiseQuotaProfileDetais = new ArrayList<>();


        Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage = new HashMap<>();

        aggregationKeyToAllowedUsage.put(AggregationKey.BILLING_CYCLE, new BillingCycleAllowedUsage(100,200,300,400, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.DAILY, new DailyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.WEEKLY, new WeeklyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));

        DataServiceType dataServiceType = new DataServiceType("test", "test",0l, Collections.<String>emptyList(), Collections.emptyList());

        RatingGroup ratingGroup = new RatingGroup(UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),1);
        RncProfileDetail rncProfileDetail = new RncProfileDetail("test", dataServiceType, 0, ratingGroup, aggregationKeyToAllowedUsage, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(),
				0.0, null, null, VolumeUnitType.TOTAL, "test", true, "pccProfileName", 0, 0, "test");

        Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails = new HashMap<>();
        fupLevelServiceWiseQuotaProfileDetails.put("test",rncProfileDetail);

        fupLevelserviceWiseQuotaProfileDetais.add(fupLevelServiceWiseQuotaProfileDetails);
        fupLevelserviceWiseQuotaProfileDetais.add(new HashMap<>());
        fupLevelserviceWiseQuotaProfileDetais.add(new HashMap<>());

        quotaProfile = spy(new QuotaProfile("profile1",
                "pkg1",
                "id1",
                BalanceLevel.HSQ,
                0,
                RenewalIntervalUnit.MONTH_END,
                QuotaProfileType.RnC_BASED,
                fupLevelserviceWiseQuotaProfileDetais, CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue()));

        quotaProfileList.add(quotaProfile);
        Mockito.doReturn(quotaProfileList).when(basepackage).getQuotaProfiles();
    }

    public void createQuotaAndSaveInDatabase(List<QuotaProfile> quotaProfileList){

        Random random = new Random();
        NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances(NON_MONETARY_BAL_ID);

        TblmDataBalanceEntity dataBalanceEntity = new TblmDataBalanceEntity();
        Long currentTime = System.currentTimeMillis();

        long monthInterval=30*24*3600*1000L;
        long weekInterval=7*24*3600*1000L;
        long dayInterval=24*3600*1000L;

        dataBalanceEntity.setId(serviceRgNonMonitoryBalance.getId());
        dataBalanceEntity.setSubscriberId("sub1");
        dataBalanceEntity.setProductOfferId(PRODUCT_OFFER_ID);
        dataBalanceEntity.setDailyVolume((long) random.nextInt());
        dataBalanceEntity.setDailyTime((long) random.nextInt());
        dataBalanceEntity.setWeeklyVolume((long) random.nextInt());
        dataBalanceEntity.setWeeklyTime((long) random.nextInt());
        dataBalanceEntity.setBillingCycleTotalVolume((long) random.nextInt());
        dataBalanceEntity.setBillingCycleAvailableVolume((long) random.nextInt());
        dataBalanceEntity.setBillingCycleAvailableTime((long) random.nextInt());
        dataBalanceEntity.setBillingCycleTime((long) random.nextInt());
        dataBalanceEntity.setDailyResetTime(new Timestamp(currentTime+dayInterval));
        dataBalanceEntity.setWeeklyResetTime(new Timestamp(currentTime+weekInterval));
        dataBalanceEntity.setQuotaExpiryTime(new Timestamp(currentTime+monthInterval));
        dataBalanceEntity.setQuotaProfileId("id1");
        dataBalanceEntity.setStartTime(new Timestamp(System.currentTimeMillis()));

        hibernateSessionFactory.save(dataBalanceEntity);
    }

    protected NonMonetoryBalance createServiceRGNonMonitoryBalances(String id) {
        Random random = new Random();

        NonMonetoryBalance serviceRgNonMonitoryBalance = new NonMonetoryBalance.NonMonetaryBalanceBuilder(id,
                RandomUtils.nextInt(0, Integer.MAX_VALUE),
                UUID.randomUUID().toString(),
                1l,
                "sub1",
                UUID.randomUUID().toString(),
                random.nextInt(2),
                UUID.randomUUID().toString(), ResetBalanceStatus.NOT_RESET, null, PRODUCT_OFFER_ID)
                .withBillingCycleVolumeBalance(random.nextInt(),random.nextInt()).withBillingCycleResetTime(random.nextInt())
                .withBillingCycleTimeBalance(random.nextInt(),random.nextInt())
                .withDailyUsage(random.nextInt(),random.nextInt()).withDailyResetTime(random.nextInt())
                .withWeeklyUsage(random.nextInt(),random.nextInt()).withWeeklyResetTime(random.nextInt())
                .withReservation(random.nextInt(),random.nextInt())
                .build();
        return serviceRgNonMonitoryBalance;
    }

    @Test
    public void resetQuotaForBaseProductOffer() throws OperationFailedException {
        createQuotaAndSaveInDatabase(basepackage.getQuotaProfiles());

        abmfOperation.resetQuota("sub1", PRODUCT_OFFER_ID, 0, transactionFactory);

        SubscriberNonMonitoryBalance nonMonitoryBalance = abmfOperation.getNonMonitoryBalance(createSprInfo("sub1"), transactionFactory);
        Assert.assertTrue(nonMonitoryBalance.getBalanceById(NON_MONETARY_BAL_ID) != null);
    }

    @Test
    public void checksIfBalanceResetIsRequiredAndResetsBalanceIfApplicable() throws OperationFailedException, SQLException, TransactionException {
        createQuotaAndSaveInDatabase(basepackage.getQuotaProfiles());

        fixedTimeSourceABMFOperation.resetQuota("sub1", PRODUCT_OFFER_ID, 0, transactionFactory);

        SubscriberNonMonitoryBalance nonMonitoryBalance = fixedTimeSourceABMFOperation.getNonMonitoryBalance(createSprInfo("sub1"), transactionFactory);
        Assert.assertTrue(nonMonitoryBalance.getBalanceById(NON_MONETARY_BAL_ID) != null);
        verify(balanceResetOperation, times(1)).performDataBalanceOperations(Mockito.any(), Mockito.any());
    }

    @Test
    public void resetQuotaForProratedBaseProductOfferWithTillBillDate() throws OperationFailedException {
        when(basepackage.getQuotaProfileType()).thenReturn(QuotaProfileType.RnC_BASED);
        createQuotaAndSaveInDatabase(basepackage.getQuotaProfiles());

        QuotaProfile quotaProfile = basepackage.getQuotaProfiles().get(0);

        doReturn(1).when(quotaProfile).getRenewalInterval();
        doReturn(RenewalIntervalUnit.TILL_BILL_DATE).when(quotaProfile).getRenewalIntervalUnit();
        doReturn(true).when(quotaProfile).getProration();

        fixedTimeSourceABMFOperation.resetQuota("sub1", PRODUCT_OFFER_ID, 26, transactionFactory);

        SubscriberNonMonitoryBalance nonMonitoryBalance = abmfOperation.getNonMonitoryBalance(createSprInfo("sub1"), transactionFactory);
        NonMonetoryBalance rnCNonMonetaryBalance = nonMonitoryBalance.getBalanceById(NON_MONETARY_BAL_ID);
        Assert.assertTrue(rnCNonMonetaryBalance != null);

        Assert.assertTrue(rnCNonMonetaryBalance.getBillingCycleAvailableVolume()==75);
        Assert.assertTrue(rnCNonMonetaryBalance.getBillingCycleAvailableTime()==300);
    }

    @Test
    public void skipResetQuotaIfPackageIsNotRnCBased() throws OperationFailedException {
        when(basepackage.getQuotaProfileType()).thenReturn(QuotaProfileType.USAGE_METERING_BASED);

        abmfOperation.resetQuota("sub2", PRODUCT_OFFER_ID, 0, transactionFactory);

        Assert.assertTrue(abmfOperation.getNonMonitoryBalance(createSprInfo("sub1"), transactionFactory).getBalances().isEmpty());
    }

    @Test
    public void resetQuotaGeneratesAlertOnQueryTimeOut() throws Exception {
        SQLException sqlException = new SQLException("query timeout", "timeout", 1013);
        setupMockToGenerateExceptionOnUpdateTransaction(EXECUTE, sqlException);

        try {
            abmfOperation.resetQuota("sub1", PRODUCT_OFFER_ID, 0, transactionFactory);
        } catch (OperationFailedException e) {

            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString());
        }
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

    private ProductOffer getProductOffer(BasePackage base) {
        ProductOfferData productOfferData =  new ProductOfferDataFactory()
                .withId(PRODUCT_OFFER_ID).withName("name").withStatus("Random")
                .withMode("LIVE").withType("BASE")
                .withDataServicePkgId(base.getId()).build();
        return new ProductOffer(
                productOfferData.getId(), productOfferData.getName(), productOfferData.getDescription(),
                PkgType.BASE, PkgMode.LIVE, productOfferData.getValidityPeriod(),
                null, productOfferData.getSubscriptionPrice() != null ? productOfferData.getSubscriptionPrice() : 0d,
                productOfferData.getCreditBalance() != null ? productOfferData.getCreditBalance() : 0d,
                null, null,null,
                productOfferData.getDataServicePkgId(), productOfferData.getGroupList(), productOfferData.getAvailabilityStartDate(),
                productOfferData.getAvailabilityEndDate(), PolicyStatus.SUCCESS,  null, null,
                (Objects.nonNull(productOfferData.isFnFOffer()) && productOfferData.isFnFOffer().booleanValue()),productOfferData.getParam1(), productOfferData.getParam2(), policyRepository,null,null, Collections.emptyMap(),productOfferData.getCurrency()
        );
    }

    private SPRInfo createSprInfo(String subscriberIdentity) {
        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(subscriberIdentity);
        return sprInfo;
    }

}
