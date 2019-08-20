package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.DummyPolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.BillingCycleAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.DailyAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.WeeklyAllowedUsage;
import com.elitecore.corenetvertex.pm.util.MockQuotaTopUp;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionData;
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

import static com.elitecore.corenetvertex.data.CarryForwardStatus.NOT_CARRY_FORWARD;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class TopUpSubscriptionOperationSubscribeQuotaTopUpTest {

    @Mock
    private AlertListener alertListener;
    private DummyPolicyRepository policyRepository;
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
        String testingDB = UUID.randomUUID().toString();
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", SubscriptionOperationTestSuite.DS_NAME, "jdbc:h2:mem:"
                + testingDB + ";create=true", "", "", 1, 5, 3000);
        transactionFactory = new DummyTransactionFactory(dbDataSource);
        transactionFactory.createTransaction();

        policyRepository = new DummyPolicyRepository();
        abmfOperation = new ABMFOperation(alertListener, policyRepository,2000,200);

        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.connection.url", "jdbc:h2:mem:" + testingDB);
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        hibernateSessionFactory = HibernateSessionFactory.create("hibernate/test-hibernate.cfg.xml", hibernateProperties);
    }

    @After
    public void tearDownConnection() throws Exception {
        Connection connection = transactionFactory.getConnection();
        if(Objects.nonNull(connection)) {
            DBUtility.closeQuietly(connection);
        }
        hibernateSessionFactory.shutdown();
    }

    private MockQuotaTopUp createMockQuotaTopUp(){
        MockQuotaTopUp mockQuotaTopUp = policyRepository.mockQuotaTopUp();
        mockQuotaTopUp.quotaProfileTypeIsRnC();
        QuotaProfile quotaProfile = new QuotaProfile("QuotaProfile",
                "PkgName",
                "QuotaProfileID",
                BalanceLevel.HSQ,2, RenewalIntervalUnit.MONTH,
                QuotaProfileType.RnC_BASED, Arrays.asList(createQuotaProfile()), CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue());
        mockQuotaTopUp.setQuotaProfiles(quotaProfile);
        when(mockQuotaTopUp.getValidityPeriodUnit()).thenReturn(ValidityPeriodUnit.MID_NIGHT);
        when(mockQuotaTopUp.getValidity()).thenReturn(28);

        return mockQuotaTopUp;
    }
    
    private Map<String, QuotaProfileDetail> createQuotaProfile(){
        Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage = new HashMap<>();

        aggregationKeyToAllowedUsage.put(AggregationKey.BILLING_CYCLE,
                new BillingCycleAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.DAILY,
                new DailyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));
        aggregationKeyToAllowedUsage.put(AggregationKey.WEEKLY,
                new WeeklyAllowedUsage(0,0,0,0, DataUnit.BYTE,DataUnit.BYTE,DataUnit.BYTE, TimeUnit.SECOND));

        DataServiceType dataServiceType = new DataServiceType("test", "test", 1, Collections.emptyList(), Collections.emptyList());

        RatingGroup ratingGroup = new RatingGroup(UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString(),1);

        RncProfileDetail rncProfileDetail = new RncProfileDetail("test", dataServiceType, 0, ratingGroup, aggregationKeyToAllowedUsage,
				0, 0,0,0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null,
				null, VolumeUnitType.TOTAL, "test", true, "pccProfileName", 0,0, "test");

        Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails = new HashMap<>();

        fupLevelServiceWiseQuotaProfileDetails.put("test",rncProfileDetail);

        return fupLevelServiceWiseQuotaProfileDetails;
    }

    private void verifyAddedBalance(QuotaTopUp quotaTopUp, String subscriberID, RncProfileDetail rncProfileDetail, String subscriptionId, long endTime){
        List<TblmDataBalanceEntity> actualEntities = hibernateSessionFactory.get(TblmDataBalanceEntity.class);

        TblmDataBalanceEntity expectedDataBalanceEntity = new TblmDataBalanceEntity();
        expectedDataBalanceEntity.setId(actualEntities.get(0).getId());
        expectedDataBalanceEntity.setSubscriberId(subscriberID);
        expectedDataBalanceEntity.setPackageId(quotaTopUp.getId());
        expectedDataBalanceEntity.setSubscriptionId(subscriptionId);
        expectedDataBalanceEntity.setQuotaProfileId(quotaTopUp.getQuotaProfile().getId());
        expectedDataBalanceEntity.setServiceId(rncProfileDetail.getDataServiceType().getServiceIdentifier());
        expectedDataBalanceEntity.setRatingGroup(rncProfileDetail.getRatingGroup().getIdentifier());
        expectedDataBalanceEntity.setBillingCycleTotalVolume(rncProfileDetail.getBillingCycleAllowedUsage().getTotalInBytes());
        expectedDataBalanceEntity.setBillingCycleAvailableVolume(0l);
        expectedDataBalanceEntity.setBillingCycleTime(rncProfileDetail.getBillingCycleAllowedUsage().getTimeInSeconds());
        expectedDataBalanceEntity.setBillingCycleAvailableTime(0l);
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
        expectedDataBalanceEntity.setStatus(actualEntities.get(0).getStatus());
        expectedDataBalanceEntity.setRenewalInterval(actualEntities.get(0).getRenewalInterval());
        expectedDataBalanceEntity.setCarryForwardStatus(NOT_CARRY_FORWARD.name());

        ReflectionAssert.assertReflectionEquals(Arrays.asList(expectedDataBalanceEntity), actualEntities);
    }

    private void createAndSaveSubscription(String subscriberId, String packageId, Timestamp startTime, Timestamp endTime, String status, String priority){
        SubscriptionData addOnSubscriptionData = new SubscriptionData(
                subscriberId,
                packageId,
                startTime.toString(),
                endTime.toString(),
                status,
                priority,
                null,
                null,null

        );
        addOnSubscriptionData.setSubscriptionId("xyz");
        hibernateSessionFactory.save(addOnSubscriptionData);
    }

    public class SubscribeByID{
        public class ValidationFailsWhen{
            @Test
            public void invalidPackageName() throws OperationFailedException {
                expectedException.expect(OperationFailedException.class);
                expectedException.expectMessage("Unable to subscribe package(QuotaTopUp) for subscriber ID: abc. Reason: Package not found for ID: QuotaTopUp");
                TopUpSubscriptionOperation topUpSubscriptionOperation = new TopUpSubscriptionOperation(alertListener,policyRepository,
                        abmfOperation,null, null);
                when(policyRepository.getPkgDataById("QuotaTopUp")).thenReturn(null);
                topUpSubscriptionOperation.subscribeQuotaTopUpById(createSprInfo("abc"),"xyz","QuotaTopUp",2,
                        System.currentTimeMillis(), timeForNextDay, null, 0, null,null ,null,
                        transactionFactory);
            }

            @Test
            public void packageIsValidButIsFailure() throws OperationFailedException{
                MockQuotaTopUp quotaTopUp= createMockQuotaTopUp();
                quotaTopUp.policyStatusFailure();

                TopUpSubscriptionOperation topUpSubscriptionOperation = new TopUpSubscriptionOperation(alertListener,policyRepository,
                        abmfOperation,null, null);

                expectedException.expect(OperationFailedException.class);
                expectedException.expectMessage("Unable to subscribe Quota TopUp("+ quotaTopUp.getId() +") for subscriber ID: abc. Reason: Quota TopUp("+ quotaTopUp.getName() +") is failed addOn");
                topUpSubscriptionOperation.subscribeQuotaTopUpById(createSprInfo("abc"),"xyz",quotaTopUp.getId(),2,
                        System.currentTimeMillis(), timeForNextDay, null, 0, null,null,null,
                        transactionFactory);
            }

            @Test
            public void endTimeIsLessThanCurrentTime() throws OperationFailedException{
                Long endTime = 500L;
                expectedException.expect(OperationFailedException.class);
                expectedException.expectMessage("End time(" + new Timestamp(endTime).toString() + ") is less or equal to current time");
                TopUpSubscriptionOperation topUpSubscriptionOperation = new TopUpSubscriptionOperation(alertListener,policyRepository,
                        abmfOperation ,null, null);
                MockQuotaTopUp quotaTopUp= createMockQuotaTopUp();
                when(quotaTopUp.getStatus()).thenReturn(PolicyStatus.SUCCESS);
                topUpSubscriptionOperation.subscribeQuotaTopUpById(createSprInfo("abc"),"xyz",quotaTopUp.getId(),2,
                        100L,endTime , null, 0, null,null ,null,
                        transactionFactory);
            }
            @Test
            public void startDateIsSmallerThanToDate() throws OperationFailedException{
                Long endTime = System.currentTimeMillis()+(1000*60);
                expectedException.expect(OperationFailedException.class);
                expectedException.expectMessage("Start time(" + new Timestamp(timeForNextDay).toString() + ") is more or equal to end time("
                        + new Timestamp(endTime).toString() + ")");
                TopUpSubscriptionOperation topUpSubscriptionOperation = new TopUpSubscriptionOperation(alertListener,policyRepository,
                        abmfOperation ,null, null);
                QuotaTopUp quotaTopUp = createMockQuotaTopUp();
                when(quotaTopUp.getStatus()).thenReturn(PolicyStatus.SUCCESS);
                topUpSubscriptionOperation.subscribeQuotaTopUpById(createSprInfo("abc"),"xyz",quotaTopUp.getId(),2,
                        timeForNextDay,endTime , null, 0, null,null ,null ,
                        transactionFactory);
            }

        }

        @Test
        public void subscribeSuccessfully() throws OperationFailedException{
            TopUpSubscriptionOperation topUpSubscriptionOperation = new TopUpSubscriptionOperation(alertListener,policyRepository,
                    abmfOperation , null, null);

            QuotaTopUp quotaTopUp = createMockQuotaTopUp();

            when(quotaTopUp.getStatus()).thenReturn(PolicyStatus.SUCCESS);

            Map<String, QuotaProfileDetail> quotaProfileDetailMap = createQuotaProfile();

            Subscription subscription = topUpSubscriptionOperation.subscribeQuotaTopUpById(createSprInfo("abc"), "xyz", quotaTopUp.getId(), 2,
                    startTime, timeForNextDay, null, 0, null,null,null,
                    transactionFactory);

            verifyAddedBalance(quotaTopUp,"abc",(RncProfileDetail)quotaProfileDetailMap.get("test"), subscription.getId(),timeForNextDay);
        }

        @Test
        public void failsWhenMultipleSubscriptionsAreNotAllowed() throws OperationFailedException{
            MockQuotaTopUp quotaTopUp = createMockQuotaTopUp();
            expectedException.expect(OperationFailedException.class);
            quotaTopUp.multipleSubscriptionNotAllowed();

            expectedException.expectMessage("Unable to subscribe package("+ quotaTopUp.getName() +") for subscriber ID: abc. Reason: Subscription(xyz) already exist for subscriber ID(abc), package("+ quotaTopUp.getName() +")");
            TopUpSubscriptionOperation topUpSubscriptionOperation = new TopUpSubscriptionOperation(alertListener,policyRepository,
                    abmfOperation,null, null);


            QuotaProfile quotaProfile = quotaTopUp.getQuotaProfile();

            createAndSaveSubscription("abc",quotaTopUp.getId(), new Timestamp(System.currentTimeMillis()), new Timestamp(timeForNextDay),
                    "0",null);

            topUpSubscriptionOperation.subscribeQuotaTopUpById(createSprInfo("abc"), "xyz", quotaTopUp.getId(), 2,
                    System.currentTimeMillis(), timeForNextDay, null,  0, null,null,null,
                    transactionFactory);

        }

        @Test
        public void failsWhenTransactionFactoryIsDead() throws OperationFailedException{
            MockQuotaTopUp quotaTopUp = createMockQuotaTopUp();
            quotaTopUp.multipleSubscriptionNotAllowed();

            expectedException.expect(OperationFailedException.class);
            expectedException.expectMessage("Unable to subscribe quota topup(ID: "+ quotaTopUp.getId() +", Name: " + quotaTopUp.getName() +") for subscriber ID: abc Reason: Datasource not available");
            TopUpSubscriptionOperation topUpSubscriptionOperation = new TopUpSubscriptionOperation(alertListener,policyRepository,
                    abmfOperation ,null, null);

            transactionFactory = spy(transactionFactory);
            doReturn(false).when(transactionFactory).isAlive();

            topUpSubscriptionOperation.subscribeQuotaTopUpById(createSprInfo("abc"), "xyz", quotaTopUp.getId(), 2,
                    System.currentTimeMillis(), timeForNextDay, null,  0, null,null,null,
                    transactionFactory);
        }

        @Test
        public void failsWhenTransactionFactoryGivesNullTransaction() throws OperationFailedException{
            MockQuotaTopUp quotaTopUp = createMockQuotaTopUp();
            quotaTopUp.multipleSubscriptionNotAllowed();

            expectedException.expect(OperationFailedException.class);
            expectedException.expectMessage("Unable to subscribe Quota TopUp(ID: "+ quotaTopUp.getId() +", Name: " + quotaTopUp.getName() +") for subscriber ID: abc Reason: Datasource not available");
            TopUpSubscriptionOperation topUpSubscriptionOperation = new TopUpSubscriptionOperation(alertListener,policyRepository,
                    abmfOperation ,null, null);

            transactionFactory = spy(transactionFactory);
            doReturn(null).when(transactionFactory).createTransaction();

            topUpSubscriptionOperation.subscribeQuotaTopUpById(createSprInfo("abc"), "xyz", quotaTopUp.getId(), 2,
                    System.currentTimeMillis(), timeForNextDay, null,  0, null,null,null,
                    transactionFactory);
        }

        @Test
        public void subscribeSuccessWhenMultipleSubscriptionsAreNotAllowedAndThereIsInactiveSubscription() throws OperationFailedException{
            TopUpSubscriptionOperation topUpSubscriptionOperation = new TopUpSubscriptionOperation(alertListener,policyRepository,
                    abmfOperation ,null, null);
            QuotaTopUp quotaTopUp = createMockQuotaTopUp();
            when(quotaTopUp.getStatus()).thenReturn(PolicyStatus.SUCCESS);
            when(quotaTopUp.isMultipleSubscription()).thenReturn(false);

            QuotaProfile quotaProfile = quotaTopUp.getQuotaProfile();

            Map<String, QuotaProfileDetail> quotaProfileDetailMap = quotaProfile.getHsqLevelServiceWiseQuotaProfileDetails();

            createAndSaveSubscription("abc","1", new Timestamp(System.currentTimeMillis()), new Timestamp(timeForNextDay),
                    "5",null);

            Subscription subscription = topUpSubscriptionOperation.subscribeQuotaTopUpById(createSprInfo("abc"), "xyz", quotaTopUp.getId(), 2,
                    startTime, timeForNextDay, null, 0, null, null,null,
                    transactionFactory);

            verifyAddedBalance(quotaTopUp,"abc",(RncProfileDetail)quotaProfileDetailMap.get("test"), subscription.getId(),timeForNextDay);
        }

        @Test
        public void subscribeSuccessWhenMultipleSubscriptionsAreNotAllowedAndThereIsSubscriptionWithPastToDate() throws OperationFailedException{
            TopUpSubscriptionOperation topUpSubscriptionOperation = new TopUpSubscriptionOperation(alertListener,policyRepository,
                    abmfOperation ,null, null);
            MockQuotaTopUp quotaTopUp = createMockQuotaTopUp();
            quotaTopUp.multipleSubscriptionNotAllowed();


            QuotaProfile quotaProfile = quotaTopUp.getQuotaProfile();

            Map<String, QuotaProfileDetail> quotaProfileDetailMap = quotaProfile.getHsqLevelServiceWiseQuotaProfileDetails();

            createAndSaveSubscription("abc","1", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),
                    "5",null);

            Subscription subscription = topUpSubscriptionOperation.subscribeQuotaTopUpById(createSprInfo("abc"), "xyz", quotaTopUp.getId(), 2,
                    startTime, timeForNextDay, null,  0, null,null,null,
                    transactionFactory);

            verifyAddedBalance(quotaTopUp,"abc",(RncProfileDetail)quotaProfileDetailMap.get("test"), subscription.getId(),timeForNextDay);
        }

        @Test
        public void subscribeSuccessfullyWithValidityPeriodWhenRenewalIntervalIsNotSetInQuotaProfile() throws OperationFailedException{
            TopUpSubscriptionOperation topUpSubscriptionOperation = new TopUpSubscriptionOperation(alertListener,policyRepository,
                    abmfOperation ,null, null);
            MockQuotaTopUp quotaTopUp = createMockQuotaTopUp();
            when(quotaTopUp.getStatus()).thenReturn(PolicyStatus.SUCCESS);
            when(quotaTopUp.getValidity()).thenReturn(10);
            when(quotaTopUp.getValidityPeriodUnit()).thenReturn(ValidityPeriodUnit.MID_NIGHT);

            QuotaProfile quotaProfile = quotaTopUp.getQuotaProfile();
            Map<String, QuotaProfileDetail> quotaProfileDetailMap = quotaProfile.getHsqLevelServiceWiseQuotaProfileDetails();

            Subscription subscription = topUpSubscriptionOperation.subscribeQuotaTopUpById(createSprInfo("abc"), "xyz", quotaTopUp.getId(), 2,
                    startTime, null, null,  0, null,null,null,
                    transactionFactory);

            verifyAddedBalance(quotaTopUp,"abc",(RncProfileDetail)quotaProfileDetailMap.get("test"), subscription.getId(),ValidityPeriodUnit.MID_NIGHT.addTime(startTime, 10));
        }

        @Test
        public void subscribeSuccessfully_AddsBalanceWithQuotaSpecificResetIntervalWhenNotNull() throws OperationFailedException{
            TopUpSubscriptionOperation topUpSubscriptionOperation = new TopUpSubscriptionOperation(alertListener,policyRepository,
                    abmfOperation ,null, null);
            MockQuotaTopUp quotaTopUp = createMockQuotaTopUp();
            when(quotaTopUp.getStatus()).thenReturn(PolicyStatus.SUCCESS);


            QuotaProfile quotaProfile = quotaTopUp.getQuotaProfile();
            Map<String, QuotaProfileDetail> quotaProfileDetailMap = quotaProfile.getHsqLevelServiceWiseQuotaProfileDetails();

            Subscription subscription = topUpSubscriptionOperation.subscribeQuotaTopUpById(createSprInfo("abc"), "xyz", quotaTopUp.getId(), 2,
                    startTime, timeForNextDay+90*24*60*60*1000L, null, 0, null,null ,null,
                    transactionFactory);

            verifyAddedBalance(quotaTopUp,"abc",(RncProfileDetail)quotaProfileDetailMap.get("test"), subscription.getId(),RenewalIntervalUnit.MONTH.addTime(startTime,2));
        }
    }

    private SPRInfo createSprInfo(String subscriberIdentity) {
        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(subscriberIdentity);
        return sprInfo;
    }
}
