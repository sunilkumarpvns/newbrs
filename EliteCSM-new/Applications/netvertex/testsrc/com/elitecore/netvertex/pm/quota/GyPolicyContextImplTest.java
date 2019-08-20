package com.elitecore.netvertex.pm.quota;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.MonetaryBalanceType;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.data.ResultCode;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.spr.BalanceProvider;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.SubscriptionNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.ddf.DDFTable;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.gateway.diameter.gy.ReportedMsccBuilder;
import com.elitecore.netvertex.pm.DummyPolicyRepository;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.pm.QoSProfile;
import com.elitecore.netvertex.pm.QuotaProfile;
import com.elitecore.netvertex.pm.RnCQuotaProfileFactory;
import com.elitecore.netvertex.pm.qos.rnc.ratecard.DataRateCard;
import com.elitecore.netvertex.pm.qos.rnc.ratecard.DataRateCardVersion;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class GyPolicyContextImplTest {
    public static final String INR = "INR";
    private PCRFRequest request;
    private PCRFResponse response;
    private ExecutionContext executionContext;
    private MockBasePackage basePackage;
    private GyPolicyContextImpl gyPolicyContextImpl;
    private RnCQuotaProfileDetail rnCQuotaProfileDetail;
    private QoSProfile qoSProfile;
    private int revalidationTime = 100;
    private PolicyRepository policyRepository;
    //private ProductOfferData productOffer;

    @Before
    public void setUp() throws OperationFailedException {
        request = new PCRFRequestImpl();
        response = new PCRFResponseImpl();
        response.setQuotaReservation(new QuotaReservation());

        basePackage = MockBasePackage.create(UUID.randomUUID().toString(), UUID.randomUUID().toString()+"name");
        //productOffer = ProductOfferBuilder.newBaseOfferWithDataPackage(basePackage.getId());
        //when(policyRepository.getProductOffer().byName(productOffer.getName())).thenReturn(productOffer);
        SPRInfoImpl sprInfo = (SPRInfoImpl) new SPRInfoImpl.SPRInfoBuilder()
                .withSubscriberIdentity("test")
                .withProductOffer(basePackage.getName())
                .build();

        sprInfo.setBalanceProvider(mock(BalanceProvider.class));
        request.setSPRInfo(sprInfo);
        executionContext = spy(new ExecutionContext(request, response, mock(CacheAwareDDFTable.class), INR));

        String quotaProfileId = UUID.randomUUID().toString();

        rnCQuotaProfileDetail = spy(new RnCQuotaProfileFactory(quotaProfileId, UUID.randomUUID().toString()).randomBalanceWithRate().create());
        Map<String, QuotaProfileDetail> hashMap = new HashMap<>();
        hashMap.put("test", rnCQuotaProfileDetail);

        QuotaProfile quotaProfile = spy(new QuotaProfile("test", basePackage.getName(), quotaProfileId, BalanceLevel.HSQ,2, RenewalIntervalUnit.MONTH, QuotaProfileType.RnC_BASED, Arrays.asList(hashMap), CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue()));

        qoSProfile = new QoSProfile("test","test", basePackage.getName(), basePackage.getId(), quotaProfile, null,null,
                0, null, null, null, null, null);
        basePackage.mockQuotaProfie(quotaProfile);

        NonMonetoryBalance nonMonetoryBalance = createNonMonetaryBalance(request.getSPRInfo(), rnCQuotaProfileDetail);
        SubscriberNonMonitoryBalance subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(Arrays.asList(nonMonetoryBalance));
        response.setCurrentNonMonetoryBalance(subscriberNonMonitoryBalance);

        SubscriberMonetaryBalance subscriberMonetaryBalance = new SubscriberMonetaryBalance(new FixedTimeSource(System.currentTimeMillis()));
        subscriberMonetaryBalance.addMonitoryBalances(createMonetaryBalance("test"));
        response.setCurrentMonetaryBalance(subscriberMonetaryBalance);
        this.policyRepository = new DummyPolicyRepository();
        gyPolicyContextImpl = new GyPolicyContextImpl(request, response, basePackage, executionContext, policyRepository, revalidationTime, null);

    }

    @Test
    public void test_process_without_mscc() {

        Subscription subscription = new Subscription(basePackage.getId(), "test", basePackage.getId(),"productOfferId", null,  null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);
        Assert.assertTrue(gyPolicyContextImpl.process(qoSProfile, basePackage, subscription));
    }

    @Test
    public void test_process_with_mscc(){

        Subscription subscription = new Subscription(basePackage.getId(), "test", basePackage.getId(),"productOfferId", null,  null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);

        MSCC mscc = new MSCC();
        mscc.setRatingGroup(rnCQuotaProfileDetail.getRatingGroup().getIdentifier());
        request.setReportedMSCCs(Arrays.asList(mscc));

        Assert.assertTrue(gyPolicyContextImpl.process(qoSProfile, basePackage, subscription));
    }

    @Test
    public void test_process_distributedValidityTime_revalidationTimeIsProvided(){

        int validityTimeInSeconds = 10;

        gyPolicyContextImpl.setGrantedMSCCs();

        MSCC mscc = new MSCC();
        mscc.setRatingGroup(rnCQuotaProfileDetail.getRatingGroup().getIdentifier());
        mscc.setValidityTime(validityTimeInSeconds);
        response.getQuotaReservation().put(mscc);

        gyPolicyContextImpl.setGrantedMSCCs();

        long validityTime = response.getQuotaReservation().get(mscc.getRatingGroup()).getValidityTime();

        Assert.assertTrue(validityTime >= validityTimeInSeconds);
        Assert.assertTrue(validityTime < validityTimeInSeconds + revalidationTime);
    }

    @Test
    public void test_process_distributedValidityTime_revalidationTimeIsZero(){

        int validityTimeInSeconds = 10;

        GyPolicyContextImpl gyPolicyContextImpl = new GyPolicyContextImpl(request, response, basePackage, executionContext, null, 0, null);
        gyPolicyContextImpl.setGrantedMSCCs();

        MSCC mscc = new MSCC();
        mscc.setRatingGroup(rnCQuotaProfileDetail.getRatingGroup().getIdentifier());
        mscc.setValidityTime(validityTimeInSeconds);
        response.getQuotaReservation().put(mscc);

        long validityTime = response.getQuotaReservation().get(mscc.getRatingGroup()).getValidityTime();

        Assert.assertTrue(validityTime == validityTimeInSeconds);
    }

    private NonMonetoryBalance createNonMonetaryBalance(SPRInfo sprInfo, RnCQuotaProfileDetail rnCQuotaProfileDetail) {

        long billingCycleTotalVolume = nextLong(3, 1000);
        long billingCycleTotalTime = nextLong(3, 1000);
        return new NonMonetoryBalance.NonMonetaryBalanceBuilder(
                UUID.randomUUID().toString(), rnCQuotaProfileDetail.getDataServiceType().getServiceIdentifier(),
                basePackage.getId(),
                rnCQuotaProfileDetail.getRatingGroup().getIdentifier(),
                sprInfo.getSubscriberIdentity(),
                null,
                0,
                basePackage.getQuotaProfiles().get(0).getId(), ResetBalanceStatus.NOT_RESET, null, null).
                withBillingCycleVolumeBalance(billingCycleTotalVolume, nextLong(2, billingCycleTotalVolume)).
                withBillingCycleTimeBalance(billingCycleTotalTime, nextLong(2, billingCycleTotalTime))
                .withDailyUsage(nextLong(2, billingCycleTotalVolume), nextLong(2, billingCycleTotalTime))
                .withWeeklyUsage(nextLong(2, billingCycleTotalVolume), nextLong(2, billingCycleTotalTime))
                .withDailyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .withWeeklyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(6))
                .withBillingCycleResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .build();
    }

    private MonetaryBalance createMonetaryBalance(String subscriberId) {
        int totalBalance = nextInt(3, 1000);
        int availableBalance = nextInt(2, totalBalance);
        int reservation = nextInt(1, availableBalance);
        return new MonetaryBalance(UUID.randomUUID().toString(),
                subscriberId,
                null,
                availableBalance,
                totalBalance,
                reservation,
                0, 0,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                UUID.randomUUID().toString(), MonetaryBalanceType.DEFAULT.name(),
                System.currentTimeMillis(),0,
                "","");
    }

    @Test
    public void testSetGrantedMsccsNotSetGrantedMsccForRgWithReportingReasonFinalInQuotaReservationWhenReportingReasonIsFinal() {

        gyPolicyContextImpl.getPCRFResponse().setQuotaReservation(null);
        MSCC mscc = new ReportedMsccBuilder().reportingReasonFina().build();
        gyPolicyContextImpl.getPCRFRequest().setReportedMSCCs(Arrays.asList(mscc));
        gyPolicyContextImpl.setGrantedMSCCs();

        Assert.assertNull(gyPolicyContextImpl.getPCRFResponse().getQuotaReservation());

    }

    @Test
    public void testSetGrantedMsccsSetGrantedMsccForRgWithReportingReasonFinalInResponseWhenReportingReasonIsFinal() {

        gyPolicyContextImpl.getPCRFResponse().setQuotaReservation(null);
        MSCC mscc = new ReportedMsccBuilder().reportingReasonFina().build();
        gyPolicyContextImpl.getPCRFRequest().setReportedMSCCs(Arrays.asList(mscc));
        gyPolicyContextImpl.setGrantedMSCCs();

        List<MSCC> grantedMSCCs = gyPolicyContextImpl.getPCRFResponse().getGrantedMSCCs();
        assertThat(grantedMSCCs.size(), is(1));
        MSCC grantedMscc = grantedMSCCs.get(0);
        assertThat(grantedMscc.getResultCode(), is(ResultCode.SUCCESS));

    }

    @Test
    public void testProcessAllowPackageForUnknownSubscriberByCallingAddDataRncBalanceMethod() throws OperationFailedException {
        SPRInfo sprInfo = request.getSPRInfo();
        sprInfo.setUnknownUser(true);
        when(executionContext.getCurrentNonMonetoryBalance()).thenReturn(new SubscriberNonMonitoryBalance(null));
        DDFTable ddfTable = executionContext.getDDFTable();
        doReturn(new SubscriptionNonMonitoryBalance(basePackage.getId())).when(ddfTable).addDataRnCBalance(anyString(),any(Subscription.class),any(ProductOffer.class));
        doReturn(true).when(rnCQuotaProfileDetail).apply(any(PolicyContext.class),anyString(),any(Subscription.class), any(QuotaReservation.class));
        gyPolicyContextImpl.process(qoSProfile, basePackage, null);
        verify(ddfTable,times(1)).addDataRnCBalance(sprInfo.getSubscriberIdentity(),null,policyRepository.getProductOffer().byName(sprInfo.getProductOffer()));
    }

    @Test
    public void testProcessCallsApplyMethodOfQuotaProfileDetailForUnknownUserProcessing() throws OperationFailedException {
        SPRInfo sprInfo = request.getSPRInfo();
        sprInfo.setUnknownUser(true);
        when(executionContext.getCurrentNonMonetoryBalance()).thenReturn(new SubscriberNonMonitoryBalance(null));
        DDFTable ddfTable = executionContext.getDDFTable();
        doReturn(new SubscriptionNonMonitoryBalance(basePackage.getId())).when(ddfTable).addDataRnCBalance(anyString(),any(Subscription.class),any(ProductOffer.class));
        doReturn(true).when(rnCQuotaProfileDetail).apply(any(PolicyContext.class),anyString(),any(Subscription.class), any(QuotaReservation.class));
        gyPolicyContextImpl.process(qoSProfile, basePackage, null);
        verify((com.elitecore.netvertex.pm.QuotaProfile)qoSProfile.getQuotaProfile(),times(1)).apply(any(PolicyContext.class),any(UserPackage.class),any(Subscription.class), any(QuotaReservation.class));

    }
    @Test
    public void testProcessReturnsFalseWhenKnownSubscriberDoesNotHaveAnyBalance() throws OperationFailedException {
        when(executionContext.getCurrentNonMonetoryBalance()).thenReturn(new SubscriberNonMonitoryBalance(null));
        DDFTable ddfTable = executionContext.getDDFTable();
        doReturn(new SubscriptionNonMonitoryBalance(basePackage.getId())).when(ddfTable).addDataRnCBalance(anyString(),any(Subscription.class),any(ProductOffer.class));
        doReturn(true).when(rnCQuotaProfileDetail).apply(any(PolicyContext.class),anyString(),any(Subscription.class), any(QuotaReservation.class));
        assertFalse(gyPolicyContextImpl.process(qoSProfile, basePackage, null));
    }

    public class withDataRateCard {
        @Mock  private DataRateCard dataRateCard;
        @Mock DataRateCardVersion dataRateCardVersion;
        @Mock PolicyContext policyContext;
        @Mock QuotaReservation quotaReservation;
        @Mock Subscription subscription;

        @Before
        public void setUp() {
            MockitoAnnotations.initMocks(this);
        }

        /*@Test
        public void testProcessMethodReturnsTrueWhenIsApplicableReturnsTrue() {

            when(dataRateCard.getRateCardVersions()).thenReturn(Arrays.asList(dataRateCardVersion));

            doReturn(true).when(dataRateCardVersion).applyReservation(policyContext, "key1", "key2",
                    "packageId", subscription, quotaReservation);

            qoSProfile = new QoSProfile("test","test", basePackage.getName(), basePackage.getId(), null, dataRateCard,null,
                    0, null, null, null, null, null);

            Assert.assertTrue(gyPolicyContextImpl.process(qoSProfile, basePackage, subscription));
        }

        @Test
        public void testProcessMethodreturnsFalseWhenApplyReservationReturnsFalse() {
            doReturn(false).when(dataRateCard).applyReservation(policyContext,
                    "packageId", subscription, quotaReservation);

            *//*dataRateCard = new DataRateCard("id", "name", "key1", "key2",
                    Arrays.asList(dataRateCardVersion), Uom.BYTE, Uom.BYTE);*//*

            qoSProfile = new QoSProfile("test","test", basePackage.getName(), basePackage.getId(), null, dataRateCard,null,
                    0, null, null, null, null, null);

            Assert.assertFalse(gyPolicyContextImpl.process(qoSProfile, basePackage, subscription));
        }*/


        @Test
        public void testProcessMethodReturnsTrueWhenIsApplicableReturnsTrue() {

            when(dataRateCard.applyReservation(any(), anyString(), any(), any())).thenReturn(true);

            qoSProfile = new QoSProfile("test","test", basePackage.getName(), basePackage.getId(), null, dataRateCard,null,
                    0, null, null, null, null, null);

            Assert.assertTrue(gyPolicyContextImpl.process(qoSProfile, basePackage, subscription));
        }

        @Test
        public void testProcessMethodreturnsFalseWhenApplyReservationReturnsFalse() {
            when(dataRateCard.applyReservation(any(), anyString(), any(), any())).thenReturn(false);

            qoSProfile = new QoSProfile("test","test", basePackage.getName(), basePackage.getId(), null, dataRateCard,null,
                    0, null, null, null, null, null);

            Assert.assertFalse(gyPolicyContextImpl.process(qoSProfile, basePackage, subscription));
        }

        @Test
        public void testProcessMethodreturnsFalseWhenQuotaProfileTypeIsNotRnCbased() {

            when(basePackage.getQuotaProfileType()).thenReturn(QuotaProfileType.USAGE_METERING_BASED);

            when(dataRateCard.applyReservation(any(), anyString(), any(), any())).thenReturn(false);

            qoSProfile = new QoSProfile("test","test", basePackage.getName(), basePackage.getId(), null, dataRateCard,null,
                    0, null, null, null, null, null);

            Assert.assertFalse(gyPolicyContextImpl.process(qoSProfile, basePackage, subscription));
        }
    }
}