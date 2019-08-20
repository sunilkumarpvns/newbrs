package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.data.CarryForwardStatus;
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
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.pm.util.MockAddOnPackage;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.SubscriptionData;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionParameter;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionResult;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.util.ResetTimeUtility;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class SubscriptionOperationSubscribeAddOnTest {
    @Mock
    private AlertListener alertListener;
    @Mock
    private PolicyRepository policyRepository;
    @Mock
    ProductOfferStore productOfferStore;
    private DummyTransactionFactory transactionFactory;
    private ABMFOperation abmfOperation;
    private long timeForNextDay = System.currentTimeMillis()+(1000*3600*24);
    private long startTime = System.currentTimeMillis()+(1000*3600*12);
    private HibernateSessionFactory hibernateSessionFactory;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        String ssid = UUID.randomUUID().toString();
        String url = "jdbc:h2:mem:" + ssid;
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", SubscriptionOperationTestSuite.DS_NAME, url + ";create=true", "", "", 1, 5000, 3000);
        transactionFactory = new DummyTransactionFactory(dbDataSource);
        transactionFactory.createTransaction();
        abmfOperation = new ABMFOperation(alertListener, policyRepository,2000,200);

        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.connection.url", url);
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        hibernateSessionFactory = HibernateSessionFactory.create("hibernate/test-hibernate.cfg.xml", hibernateProperties);
        when(policyRepository.getProductOffer()).thenReturn(productOfferStore);

    }

    @After
    public void tearDownConnection() throws Exception {
        Connection connection = transactionFactory.getConnection();
        if(Objects.nonNull(connection)) {
            DBUtility.closeQuietly(connection);
        }
        hibernateSessionFactory.shutdown();
    }

    private ProductOffer createProductOffer(){
        MockAddOnPackage mockAddOnPackage = MockAddOnPackage.create("1", "Addon").quotaProfileTypeIsRnC().mockQuotaProfie();
        when(policyRepository.getActiveAddOnById("1")).thenReturn(mockAddOnPackage);
        when(policyRepository.getActiveAddOnByName("Addon")).thenReturn(mockAddOnPackage);
        when(policyRepository.getProductOffer()).thenReturn(productOfferStore);
        ProductOffer productOffer = getProductOffer(mockAddOnPackage);
        when(policyRepository.getPkgDataById("1")).thenReturn(mockAddOnPackage);
        when(productOfferStore.byId(productOffer.getId())).thenReturn(productOffer);
        when(productOfferStore.byName(productOffer.getName())).thenReturn(productOffer);
        return productOffer;
    }


    private Map<String, QuotaProfileDetail> createQuotaProfile(){
        Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage = new HashMap<>();

        aggregationKeyToAllowedUsage.put(AggregationKey.BILLING_CYCLE, new BillingCycleAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.DAILY, new DailyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.WEEKLY, new WeeklyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));

        DataServiceType dataServiceType = new DataServiceType("test", "test", 1, Collections.emptyList(), Collections.emptyList());
        RatingGroup ratingGroup = new RatingGroup(UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),1);
        RncProfileDetail rncProfileDetail = new RncProfileDetail("test", dataServiceType, 0, ratingGroup, aggregationKeyToAllowedUsage, 0,
				0,0,0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null, null,
				VolumeUnitType.TOTAL, "test", true, "pccProfileName", 0,0, "test");

        Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails = new HashMap<>();
        fupLevelServiceWiseQuotaProfileDetails.put("test",rncProfileDetail);

        return fupLevelServiceWiseQuotaProfileDetails;
    }

    private void verifyAddedBalance(ProductOffer productOffer, String subscriberID, RncProfileDetail rncProfileDetail,
                                    String subscriptionId, Long endTime, ResetBalanceStatus resetBalanceStatus ){
        List<TblmDataBalanceEntity> actualEntities = hibernateSessionFactory.get(TblmDataBalanceEntity.class);

        TblmDataBalanceEntity expectedDataBalanceEntity = new TblmDataBalanceEntity();
        expectedDataBalanceEntity.setId(actualEntities.get(0).getId());
        expectedDataBalanceEntity.setSubscriberId(subscriberID);
        expectedDataBalanceEntity.setPackageId(productOffer.getDataServicePkgData().getId());
        expectedDataBalanceEntity.setSubscriptionId(subscriptionId);
        expectedDataBalanceEntity.setQuotaProfileId(productOffer.getDataServicePkgData().getQuotaProfiles().get(0).getId());
        expectedDataBalanceEntity.setServiceId(rncProfileDetail.getDataServiceType().getServiceIdentifier());
        expectedDataBalanceEntity.setRatingGroup(rncProfileDetail.getRatingGroup().getIdentifier());
        expectedDataBalanceEntity.setBillingCycleTotalVolume(rncProfileDetail.getBillingCycleAllowedUsage().getTotalInBytes());
        expectedDataBalanceEntity.setBillingCycleAvailableVolume(0l);
        expectedDataBalanceEntity.setBillingCycleAvailableTime(0l);
        expectedDataBalanceEntity.setBillingCycleTime(rncProfileDetail.getBillingCycleAllowedUsage().getTimeInSeconds());
        expectedDataBalanceEntity.setDailyVolume(rncProfileDetail.getDailyAllowedUsage().getTotalInBytes());
        expectedDataBalanceEntity.setDailyTime(rncProfileDetail.getDailyAllowedUsage().getTimeInSeconds());
        expectedDataBalanceEntity.setWeeklyVolume(rncProfileDetail.getWeeklyAllowedUsage().getTotalInBytes());
        expectedDataBalanceEntity.setWeeklyTime(rncProfileDetail.getWeeklyAllowedUsage().getTimeInSeconds());
        expectedDataBalanceEntity.setDailyResetTime(new Timestamp(ResetTimeUtility.calculateDailyResetTime(startTime)));
        expectedDataBalanceEntity.setWeeklyResetTime(new Timestamp(ResetTimeUtility.calculateWeeklyResetTime(startTime)));
        expectedDataBalanceEntity.setStartTime(new Timestamp(startTime));
        expectedDataBalanceEntity.setQuotaExpiryTime(new Timestamp(endTime));
        expectedDataBalanceEntity.setReservationVolume(0l);
        expectedDataBalanceEntity.setReservationTime(0l);
        expectedDataBalanceEntity.setLastUpdateTime(actualEntities.get(0).getLastUpdateTime());
        expectedDataBalanceEntity.setRenewalInterval(actualEntities.get(0).getRenewalInterval());
        expectedDataBalanceEntity.setStatus(resetBalanceStatus.name());
        expectedDataBalanceEntity.setProductOfferId(productOffer.getId());
        expectedDataBalanceEntity.setCarryForwardStatus(CarryForwardStatus.NOT_CARRY_FORWARD.name());
        ReflectionAssert.assertReflectionEquals(Arrays.asList(expectedDataBalanceEntity), actualEntities);
    }

    private void createAndSaveSubscription(String subscriberId, String packageId, Timestamp startTime, Timestamp endTime, String status, String priority,String productOfferId){
        SubscriptionData addOnSubscriptionData = new SubscriptionData(
                subscriberId,
                packageId,
                startTime.toString(),
                endTime.toString(),
                status,
                priority,
                null,
                null,productOfferId

        );
        addOnSubscriptionData.setSubscriptionId("xyz");
        hibernateSessionFactory.save(addOnSubscriptionData);
    }

    public class SubscribeByID{
        public class ValidationFailsWhen{
            @Test
            public void invalidPackageName() throws OperationFailedException{
                expectedException.expect(OperationFailedException.class);
                expectedException.expectMessage("Unable to subscribe addOnProductOffer(Addon) for subscriber ID: abc. Reason: Package not found for ID: Addon");
                SubscriptionOperationImpl subscriptionOperation = new SubscriptionOperationImpl(alertListener,policyRepository,
                        null, abmfOperation, null, null, null);
                when(policyRepository.getPkgDataById("Addon")).thenReturn(null);
                subscriptionOperation.subscribeAddOnProductOfferById(
                        new SubscriptionParameter(createSprInfo("abc"), "xyz", "Addon", 2, null, null, null, System.currentTimeMillis(), timeForNextDay, null, null, null, null,null), transactionFactory, null);
            }

            @Test
            public void packageIsValidButIsFailure() throws OperationFailedException{
                expectedException.expect(OperationFailedException.class);
                expectedException.expectMessage("Unable to subscribe addOnProductOffer(1) for subscriber ID: abc." +
                        " Reason: AddOn(Addon) is failed addOn");
                SubscriptionOperationImpl subscriptionOperation = new SubscriptionOperationImpl(alertListener,policyRepository,
                        null, abmfOperation, null, null, null);
                ProductOffer productOffer = createProductOffer();
                productOffer = spy(productOffer);
                doReturn(PolicyStatus.FAILURE).when(productOffer).getPolicyStatus();
                when(productOfferStore.byId(productOffer.getId())).thenReturn(productOffer);
                when(productOfferStore.byName(productOffer.getName())).thenReturn(productOffer);

                subscriptionOperation.subscribeAddOnProductOfferById(
                        new SubscriptionParameter(createSprInfo("abc"), "xyz", "1", 2, null, null, null, System.currentTimeMillis(), timeForNextDay, null, null, null, null,null), transactionFactory, null);
            }

            @Test
            public void endTimeIsLessThanCurrentTime() throws OperationFailedException{
                Long endTime = 500L;
                expectedException.expect(OperationFailedException.class);
                expectedException.expectMessage("End time(" + new Timestamp(endTime).toString() + ") is less or equal to current time");
                SubscriptionOperationImpl subscriptionOperation = new SubscriptionOperationImpl(alertListener,policyRepository,
                        null, abmfOperation, null, null, null);
                ProductOffer productOffer = createProductOffer();
                subscriptionOperation.subscribeAddOnProductOfferById(
                        new SubscriptionParameter(createSprInfo("abc"), "xyz", "1", 2, null, null, null, 100L, endTime, null, null, null, null,null), transactionFactory, null);
            }
            @Test
            public void startDateIsSmallerThanToDate() throws OperationFailedException{
                Long endTime = System.currentTimeMillis()+(1000*60);
                expectedException.expect(OperationFailedException.class);
                expectedException.expectMessage("Start time(" + new Timestamp(timeForNextDay).toString() + ") is more or equal to end time("
                        + new Timestamp(endTime).toString() + ")");
                SubscriptionOperationImpl subscriptionOperation = new SubscriptionOperationImpl(alertListener,policyRepository,
                        null, abmfOperation, null, null, null);
                ProductOffer productOffer = createProductOffer();
                subscriptionOperation.subscribeAddOnProductOfferById(
                        new SubscriptionParameter(createSprInfo("abc"), "xyz", "1", 2, null, null, null, timeForNextDay, endTime, null, null, null, null,null), transactionFactory, null);
            }

        }

        @Test
        public void subscribeSuccessfully() throws OperationFailedException{
            SubscriptionOperationImpl subscriptionOperation = new SubscriptionOperationImpl(alertListener, policyRepository,
                    null, abmfOperation, null, null, null);
            ProductOffer productOffer = createProductOffer();

            Map<String, QuotaProfileDetail> quotaProfileDetailMap = createQuotaProfile();

            QuotaProfile quotaProfile = productOffer.getDataServicePkgData().getQuotaProfiles().get(0);
            when(quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()).thenReturn(Arrays.asList(quotaProfileDetailMap));

            SubscriptionResult subscription = subscriptionOperation.subscribeAddOnProductOfferById(
                    new SubscriptionParameter(createSprInfo("abc"), "xyz", "1", 2, null, null, null, startTime, timeForNextDay, null, null, null, null,null), transactionFactory, null);

            verifyAddedBalance(productOffer,"abc",(RncProfileDetail)quotaProfileDetailMap.get("test"), subscription.getSubscriptions().get(0).getId(),timeForNextDay,ResetBalanceStatus.NOT_RESET);
        }

        /*@Test
        public void failsWhenMultipleSubscriptionsAreNotAllowed() throws OperationFailedException{
            expectedException.expect(OperationFailedException.class);
            expectedException.expectMessage("Unable to subscribe package(Addon) for subscriber ID: abc. Reason: Subscription(xyz) already exist for subscriber ID(abc), package(Addon)");
            SubscriptionOperationImpl subscriptionOperation = new SubscriptionOperationImpl(alertListener, policyRepository,
                    null, abmfOperation , null );
            ProductOffer productOffer = createProductOffer();
            //when(productOffer.isMultipleSubscription()).thenReturn(false);

            Map<String, QuotaProfileDetail> quotaProfileDetailMap = createQuotaProfile();

            QuotaProfile quotaProfile = productOffer.getDataServicePkgData().getQuotaProfiles().get(0);
            when(quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()).thenReturn(Arrays.asList(quotaProfileDetailMap));

            createAndSaveSubscription("abc","1", new Timestamp(System.currentTimeMillis()), new Timestamp(timeForNextDay),
                    "0",null,productOffer.getId());

            subscriptionOperation.subscribeAddOnProductOfferById("abc", "xyz", "1", 2,
                    System.currentTimeMillis(), timeForNextDay, null, null,
                    transactionFactory, null);

        }*/

        @Test
        public void failsWhenTransactionFactoryIsDead() throws OperationFailedException{
                expectedException.expect(OperationFailedException.class);
                expectedException.expectMessage("Unable to subscribe addOn(ID:1, Name: Addon) for subscriber ID: abc Reason: Datasource not available");
                SubscriptionOperationImpl subscriptionOperation = new SubscriptionOperationImpl(alertListener, policyRepository,
                        null, abmfOperation, null, null, null);
                ProductOffer productOffer = createProductOffer();
                //when(productOffer.isMultipleSubscription()).thenReturn(false);

                Map<String, QuotaProfileDetail> quotaProfileDetailMap = createQuotaProfile();

                QuotaProfile quotaProfile = productOffer.getDataServicePkgData().getQuotaProfiles().get(0);
                when(quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()).thenReturn(Arrays.asList(quotaProfileDetailMap));

                transactionFactory = spy(transactionFactory);
                doReturn(false).when(transactionFactory).isAlive();

                subscriptionOperation.subscribeAddOnProductOfferById(
                        new SubscriptionParameter(createSprInfo("abc"), "xyz", "1", 2, null, null, null, System.currentTimeMillis(), timeForNextDay, null, null, null, null,null), transactionFactory, null);
        }

        @Test
        public void failsWhenTransactionFactoryGivesNullTransaction() throws OperationFailedException{
            expectedException.expect(OperationFailedException.class);
            expectedException.expectMessage("Unable to subscribe addOn(ID:1, Name: Addon) for subscriber ID: abc Reason: Datasource not available");
            SubscriptionOperationImpl subscriptionOperation = new SubscriptionOperationImpl(alertListener, policyRepository,
                    null, abmfOperation, null, null, null);
            ProductOffer productOffer = createProductOffer();
           // when(productOffer.isMultipleSubscription()).thenReturn(false);

            Map<String, QuotaProfileDetail> quotaProfileDetailMap = createQuotaProfile();

            QuotaProfile quotaProfile = productOffer.getDataServicePkgData().getQuotaProfiles().get(0);
            when(quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()).thenReturn(Arrays.asList(quotaProfileDetailMap));

            transactionFactory = spy(transactionFactory);
            doReturn(null).when(transactionFactory).createTransaction();

            subscriptionOperation.subscribeAddOnProductOfferById(
                    new SubscriptionParameter(createSprInfo("abc"), "xyz", "1", 2, null, null, null, System.currentTimeMillis(), timeForNextDay, null, null, null, null,null), transactionFactory, null);
        }

        @Test
        public void subscribeSuccessWhenMultipleSubscriptionsAreNotAllowedAndThereIsInactiveSubscription() throws OperationFailedException{
            SubscriptionOperationImpl subscriptionOperation = new SubscriptionOperationImpl(alertListener, policyRepository,
                    null, abmfOperation, null, null, null);
            ProductOffer productOffer = createProductOffer();

            Map<String, QuotaProfileDetail> quotaProfileDetailMap = createQuotaProfile();

            QuotaProfile quotaProfile = productOffer.getDataServicePkgData().getQuotaProfiles().get(0);
            when(quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()).thenReturn(Arrays.asList(quotaProfileDetailMap));

            createAndSaveSubscription("abc","1", new Timestamp(System.currentTimeMillis()), new Timestamp(timeForNextDay),
                    "5",null,productOffer.getId());

            SubscriptionResult subscriptionResult = subscriptionOperation.subscribeAddOnProductOfferById(
                    new SubscriptionParameter(createSprInfo("abc"), "xyz", "1", 2, null, null, null, startTime, timeForNextDay, null, null, null, null,null), transactionFactory, null);

            verifyAddedBalance(productOffer,"abc",(RncProfileDetail)quotaProfileDetailMap.get("test"), subscriptionResult.getSubscriptions().get(0).getId(), timeForNextDay, ResetBalanceStatus.NOT_RESET);
        }

        @Test
        public void subscribeSuccessWhenMultipleSubscriptionsAreNotAllowedAndThereIsSubscriptionWithPastToDate() throws OperationFailedException{
            SubscriptionOperationImpl subscriptionOperation = new SubscriptionOperationImpl(alertListener, policyRepository,
                    null, abmfOperation, null, null, null);
            ProductOffer productOffer = createProductOffer();
            //when(productOffer.isMultipleSubscription()).thenReturn(false);

            Map<String, QuotaProfileDetail> quotaProfileDetailMap = createQuotaProfile();

            QuotaProfile quotaProfile = productOffer.getDataServicePkgData().getQuotaProfiles().get(0);
            when(quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()).thenReturn(Arrays.asList(quotaProfileDetailMap));

            createAndSaveSubscription("abc","1", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),
                    "5",null,productOffer.getId());

            SubscriptionResult subscriptionResult = subscriptionOperation.subscribeAddOnProductOfferById(
                    new SubscriptionParameter(createSprInfo("abc"), "xyz", "1", 2, null, null, null, startTime, timeForNextDay, null, null, null, null,null), transactionFactory, null);

            verifyAddedBalance(productOffer,"abc",(RncProfileDetail)quotaProfileDetailMap.get("test"), subscriptionResult.getSubscriptions().get(0).getId(),timeForNextDay,ResetBalanceStatus.NOT_RESET);
        }
    }


    public class SubscribeByName{
        public class ValidationFailsWhen{
            @Test
            public void invalidPackageName() throws OperationFailedException{
                    expectedException.expect(OperationFailedException.class);
                    expectedException.expectMessage("Unable to subscribe productOffer(Addon) for subscriber ID: abc." +
                            " Reason: Product Offer not found for Name: Addon");
                    SubscriptionOperationImpl subscriptionOperation = new SubscriptionOperationImpl(alertListener,policyRepository,
                            null, abmfOperation, null, null, null);
                    when(productOfferStore.byName("Addon")).thenReturn(null);
                    subscriptionOperation.subscribeProductOfferByName(
                            new SubscriptionParameter(createSprInfo("abc"), "xyz",null, 2, "Addon", null, null,  System.currentTimeMillis(), timeForNextDay, null, null, null, null,null), transactionFactory);

            }

            @Test
            public void packageIsValidButIsFailure() throws OperationFailedException{
                expectedException.expect(OperationFailedException.class);
                expectedException.expectMessage("Unable to subscribe productOffer(Addon) for subscriber ID: abc." +
                        " Reason: AddOn(Addon) is failed productOffer");
                SubscriptionOperationImpl subscriptionOperation = new SubscriptionOperationImpl(alertListener,policyRepository,
                        null, abmfOperation, null, null, null);
                ProductOffer productOffer = createProductOffer();
                productOffer = spy(productOffer);
                doReturn(PolicyStatus.FAILURE).when(productOffer).getPolicyStatus();
                when(productOfferStore.byId(productOffer.getId())).thenReturn(productOffer);
                when(productOfferStore.byName(productOffer.getName())).thenReturn(productOffer);


                subscriptionOperation.subscribeProductOfferByName(
                        new SubscriptionParameter(createSprInfo("abc"), "xyz",null, 2, "Addon",  null, null, System.currentTimeMillis(), timeForNextDay, null, null, null, null,null), transactionFactory);
            }

            @Test
            public void endTimeIsLessThanCurrentTime() throws OperationFailedException{
                Long endTime = 500L;
                expectedException.expect(OperationFailedException.class);
                expectedException.expectMessage("End time(" + new Timestamp(endTime).toString() + ") is less or equal to current time");
                SubscriptionOperationImpl subscriptionOperation = new SubscriptionOperationImpl(alertListener,policyRepository,
                        null, abmfOperation, null, null, null);
                ProductOffer productOffer = createProductOffer();
                subscriptionOperation.subscribeProductOfferByName(
                        new SubscriptionParameter(createSprInfo("abc"), "xyz", null,2, "Addon",  null, null, 100L, endTime, null, null, null, null,null), transactionFactory);
            }
            @Test
            public void startDateIsSmallerThanToDate() throws OperationFailedException{
                Long endTime = System.currentTimeMillis()+(1000*60);
                expectedException.expect(OperationFailedException.class);
                expectedException.expectMessage("Start time(" + new Timestamp(timeForNextDay).toString() + ") is more or equal to end time("
                        + new Timestamp(endTime).toString() + ")");
                SubscriptionOperationImpl subscriptionOperation = new SubscriptionOperationImpl(alertListener,policyRepository,
                        null, abmfOperation, null, null, null);
                ProductOffer productOffer = createProductOffer();
                subscriptionOperation.subscribeProductOfferByName(
                        new SubscriptionParameter(createSprInfo("abc"), "xyz", null,2, "Addon",  null, null, timeForNextDay, endTime, null, null, null, null,null), transactionFactory);
            }
        }

        @Test
        public void subscribeSuccessfully() throws OperationFailedException{
            SubscriptionOperationImpl subscriptionOperation = new SubscriptionOperationImpl(alertListener, policyRepository,
                    null, abmfOperation, null, null, null);
            ProductOffer productOffer = createProductOffer();

            Map<String, QuotaProfileDetail> quotaProfileDetailMap = createQuotaProfile();

            QuotaProfile quotaProfile = productOffer.getDataServicePkgData().getQuotaProfiles().get(0);
            when(quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()).thenReturn(Arrays.asList(quotaProfileDetailMap));

            SubscriptionResult subscriptionResult = subscriptionOperation.subscribeProductOfferByName(
                    new SubscriptionParameter(createSprInfo("abc"), "xyz", null,2, "Addon",  null, null, startTime, timeForNextDay, null, null, null, null,null), transactionFactory);

            verifyAddedBalance(productOffer,"abc",(RncProfileDetail)quotaProfileDetailMap.get("test"), subscriptionResult.getSubscriptions().get(0).getId(),timeForNextDay,ResetBalanceStatus.NOT_RESET);
        }

        @Test
        public void subscribeSuccessfullyWithValidityPeriodWhenRenewalIntervalIsNotSetInQuotaProfile() throws OperationFailedException{
            SubscriptionOperationImpl subscriptionOperation = new SubscriptionOperationImpl(alertListener, policyRepository,
                    null, abmfOperation, null, null, null);
            ProductOffer productOffer = createProductOffer();

            Map<String, QuotaProfileDetail> quotaProfileDetailMap = createQuotaProfile();

            QuotaProfile quotaProfile = productOffer.getDataServicePkgData().getQuotaProfiles().get(0);
            when(quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()).thenReturn(Arrays.asList(quotaProfileDetailMap));

            SubscriptionResult subscriptionResult = subscriptionOperation.subscribeProductOfferByName(
                    new SubscriptionParameter(createSprInfo("abc"), "xyz", null,2, "Addon",  null, null, startTime, null, null, null, null, null,null), transactionFactory);

            verifyAddedBalance(productOffer,"abc",(RncProfileDetail)quotaProfileDetailMap.get("test"), subscriptionResult.getSubscriptions().get(0).getId(),ValidityPeriodUnit.MID_NIGHT.addTime(startTime,1) , ResetBalanceStatus.NOT_RESET);
        }

        @Test
        public void subscribeSuccessfully_AddsBalanceWithQuotaSpecificResetIntervalWhenNotNull() throws OperationFailedException{
            SubscriptionOperationImpl subscriptionOperation = new SubscriptionOperationImpl(alertListener, policyRepository,
                    null, abmfOperation, null, null, null);
            ProductOffer productOffer = createProductOffer();

            Map<String, QuotaProfileDetail> quotaProfileDetailMap = createQuotaProfile();

            QuotaProfile quotaProfile = productOffer.getDataServicePkgData().getQuotaProfiles().get(0);
            when(quotaProfile.getAllLevelServiceWiseQuotaProfileDetails()).thenReturn(Arrays.asList(quotaProfileDetailMap));
            when(quotaProfile.getRenewalInterval()).thenReturn(28);
            when(quotaProfile.getRenewalIntervalUnit()).thenReturn(RenewalIntervalUnit.MID_NIGHT);

            SubscriptionResult subscriptionResult = subscriptionOperation.subscribeProductOfferByName(
                    new SubscriptionParameter(createSprInfo("abc"), "xyz",null, 2, "Addon",  null, null, startTime, timeForNextDay + 90 * 24 * 60 * 60 * 1000L, null, null, null, null,null), transactionFactory);

            verifyAddedBalance(productOffer,"abc",(RncProfileDetail)quotaProfileDetailMap.get("test"), subscriptionResult.getSubscriptions().get(0).getId(),RenewalIntervalUnit.MID_NIGHT.addTime(startTime,28),ResetBalanceStatus.RESET);
        }


    }

    private ProductOffer getProductOffer(MockAddOnPackage addOn) {
        ProductOfferData productOfferData =  new ProductOfferDataFactory()
                .withId("1").withName("Addon").withStatus(PkgStatus.ACTIVE.name())
                .withMode("LIVE").withType("ADDON")
                .withDataServicePkgId(addOn.getId()).build();
        return new ProductOffer(
                productOfferData.getId(), productOfferData.getName(), productOfferData.getDescription(),
                PkgType.ADDON, PkgMode.LIVE, 1,
                ValidityPeriodUnit.MID_NIGHT, productOfferData.getSubscriptionPrice() != null ? productOfferData.getSubscriptionPrice() : 0d,
                productOfferData.getCreditBalance() != null ? productOfferData.getCreditBalance() : 0d,
                PkgStatus.ACTIVE, null, null,
                productOfferData.getDataServicePkgId(), productOfferData.getGroupList(), productOfferData.getAvailabilityStartDate(),
                productOfferData.getAvailabilityEndDate(), PolicyStatus.SUCCESS,  null, null,
                (Objects.nonNull(productOfferData.isFnFOffer()) && productOfferData.isFnFOffer().booleanValue()),
                productOfferData.getParam1(), productOfferData.getParam2(), policyRepository,
                null,null,new HashMap<>(),productOfferData.getCurrency()
        );
    }

    private SPRInfo createSprInfo(String subscriberIdentity){
        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(subscriberIdentity);
        return sprInfo;
    }
}
