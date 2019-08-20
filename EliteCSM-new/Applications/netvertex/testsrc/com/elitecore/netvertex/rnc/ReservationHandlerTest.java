package com.elitecore.netvertex.rnc;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.spr.BalanceProvider;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.QoSProfile;
import com.elitecore.netvertex.pm.QuotaProfile;
import com.elitecore.netvertex.pm.RnCQuotaProfileFactory;
import com.elitecore.netvertex.pm.quota.GyPolicyContextImpl;
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.service.pcrf.DummyPCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.RatingGroupSelectionState;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(HierarchicalContextRunner.class)
public class ReservationHandlerTest {

    public static final String INR = "INR";
    private ReservationHandler reservationHandler;
    private DummyPCRFServiceContext dummyPCRFServiceContext;
    private PCRFRequest request;
    private PCRFResponse response;
    private ExecutionContext executionContext;
    private MockBasePackage basePackage;
    private NonMonetoryBalance nonMonetoryBalance;
    private SubscriberNonMonitoryBalance subscriberNonMonitoryBalance;
    private QuotaProfile quotaProfile;
    private QoSProfile qoSProfile;
    private Subscription subscription;

    @Before
    public void setUp(){

        dummyPCRFServiceContext = DummyPCRFServiceContext.spy();
        reservationHandler = new ReservationHandler(dummyPCRFServiceContext);

        request = new PCRFRequestImpl();
        response = new PCRFResponseImpl();

        basePackage = MockBasePackage.create(UUID.randomUUID().toString(), UUID.randomUUID().toString()+"name");

        SPRInfoImpl sprInfo = (SPRInfoImpl) new SPRInfoImpl.SPRInfoBuilder()
                .withSubscriberIdentity("test")
                .withProductOffer(basePackage.getName())
                .build();

        sprInfo.setBalanceProvider(mock(BalanceProvider.class));
        request.setSPRInfo(sprInfo);
        executionContext = new ExecutionContext(request, response, mock(CacheAwareDDFTable.class), INR);

        RnCQuotaProfileDetail rnCQuotaProfileDetail = new RnCQuotaProfileFactory(UUID.randomUUID().toString(),
                UUID.randomUUID().toString()).randomBalanceWithRate().create();
        Map<String, QuotaProfileDetail> hashMap = new HashMap<>();
        hashMap.put("test", rnCQuotaProfileDetail);

        quotaProfile = new QuotaProfile("test", basePackage.getName(), "test", BalanceLevel.HSQ,2, RenewalIntervalUnit.MONTH, QuotaProfileType.RnC_BASED, Arrays.asList(hashMap), CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue());
        qoSProfile = new QoSProfile("test","test", basePackage.getName(), basePackage.getId(), quotaProfile, null,null,
                0, null, null, null, null, null);

        basePackage.mockQuotaProfie(quotaProfile);

        RatingGroupSelectionState ratingGroupSelectionState = new RatingGroupSelectionState();
        ratingGroupSelectionState.add("test", basePackage, qoSProfile, 0, 0, 0);
        request.setPCCProfileSelectionState(ratingGroupSelectionState);

        nonMonetoryBalance = createNonMonetaryBalance(request.getSPRInfo(), rnCQuotaProfileDetail);
        subscriberNonMonitoryBalance = new SubscriberNonMonitoryBalance(Arrays.asList(nonMonetoryBalance));
        response.setCurrentNonMonetoryBalance(subscriberNonMonitoryBalance);

        SubscriberMonetaryBalance subscriberMonetaryBalance = new SubscriberMonetaryBalance(new FixedTimeSource(System.currentTimeMillis()));
        subscriberMonetaryBalance.addMonitoryBalances(createMonetaryBalance("test"));
        response.setCurrentMonetaryBalance(subscriberMonetaryBalance);

        subscription = new Subscription("test", "test", basePackage.getId(),"productOfferId", null, null, null, CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY, SubscriptionType.ADDON, null, null);
        LinkedHashMap<String, Subscription> subscriptionMap = new LinkedHashMap<>();
        subscriptionMap.put(basePackage.getId(), subscription);
        response.setSubscriptions(subscriptionMap);
    }

    @Test
    public void test_handle_for_gx_gy_flow_withoutMSCC() throws OperationFailedException {
        GyPolicyContextImpl gyPolicyContextImpl = mock(GyPolicyContextImpl.class);
        reservationHandler.handle(request, executionContext, gyPolicyContextImpl);
    }

    @Test
    public void test_handle_for_gx_gy_flow_withMSCC() throws OperationFailedException {

        MSCC mscc = new MSCC();
        mscc.setRatingGroup(0);
        request.setReportedMSCCs(Arrays.asList(mscc));

        GyPolicyContextImpl gyPolicyContextImpl = mock(GyPolicyContextImpl.class);

        reservationHandler.handle(request, executionContext, gyPolicyContextImpl);
    }

    private NonMonetoryBalance createNonMonetaryBalance(SPRInfo sprInfo, RnCQuotaProfileDetail rnCQuotaProfileDetail) {

        long billingCycleTotalVolume = nextLong(2, 1000);
        long billingCycleTotalTime = nextLong(2, 1000);
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
                .build();
    }

    private MonetaryBalance createMonetaryBalance(String subscriberId) {
        int totalBalance = nextInt(2, 1000);
        int availableBalance = nextInt(1, totalBalance);
        int reservation = nextInt(1, availableBalance);
        return new MonetaryBalance(UUID.randomUUID().toString(),
                subscriberId,
                CommonConstants.MONEY_DATA_SERVICE,
                availableBalance,
                totalBalance,
                reservation,
                0,
                0,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                UUID.randomUUID().toString(),null,
                System.currentTimeMillis(),0,"","");
    }

    public class WhenRateCardConfiguredAndPccProfileSelectionStateFoundAnd {

        private ReservationHandler  reservationHandler;
        private GyPolicyContextImpl gyPolicyContext;
        private PolicyRepository policyRepository;
        private com.elitecore.netvertex.pm.qos.rnc.ratecard.DataRateCard dataRateCard1;
        private com.elitecore.netvertex.pm.qos.rnc.ratecard.DataRateCard dataRateCard2;
        private QoSProfile qoSProfile1;
        private QoSProfile qoSProfile2;

        @Before
        public void setUp() {

            request = new PCRFRequestImpl();
            response = new PCRFResponseImpl();
            dataRateCard1 = mock(com.elitecore.netvertex.pm.qos.rnc.ratecard.DataRateCard.class);
            dataRateCard2 = mock(com.elitecore.netvertex.pm.qos.rnc.ratecard.DataRateCard.class);

            basePackage = MockBasePackage.create(UUID.randomUUID().toString(), UUID.randomUUID().toString()+"name");
            basePackage.quotaProfileTypeIsRnC();
            qoSProfile1 = new QoSProfile(UUID.randomUUID().toString(),"qosName1", basePackage.getName(), basePackage.getId(), quotaProfile, dataRateCard1,null,
                    0, null, null, null, null, null);

            qoSProfile2 = new QoSProfile(UUID.randomUUID().toString(),"qosName2", basePackage.getName(), basePackage.getId(), quotaProfile, dataRateCard2,null,
                    0, null, null, null, null, null);

            RatingGroupSelectionState ratingGroupSelectionState = new RatingGroupSelectionState();
            ratingGroupSelectionState.add(UUID.randomUUID().toString(), basePackage, qoSProfile1, 0,
                    CommonConstants.DEFAULT_RATING_GROUP_IDENTIFIER, CommonConstants.ALL_SERVICE_IDENTIFIER);
            ratingGroupSelectionState.add(UUID.randomUUID().toString(), basePackage, qoSProfile2, 0,
                    11, 12);
            request.setPCCProfileSelectionState(ratingGroupSelectionState);

            gyPolicyContext = new GyPolicyContextImpl(request,
                    response,
                    basePackage,
                    executionContext,
                    policyRepository, 0, null);

            reservationHandler = new ReservationHandler(dummyPCRFServiceContext);
        }

        public class ReportedMSCCsFoundThen {

            @Before
            public void setUp() {
                MSCC mscc = new MSCC();
                request.setReportedMSCCs(Arrays.asList(mscc));
            }

            @Test
            public void onlyReportedMSCCSpecificRateCardApplyReservationShouldBeCalled() throws Exception {
                reservationHandler.handle(request, executionContext, gyPolicyContext);
                verify(dataRateCard1, times(1)).applyReservation(gyPolicyContext, basePackage.getId(), null, gyPolicyContext.getReservations());
                verify(dataRateCard2, times(0)).applyReservation(gyPolicyContext, basePackage.getId(), null, gyPolicyContext.getReservations());
            }
        }

        public class MSCCsNotFoundThen {

            @Before
            public void setUp() {
                request.setReportedMSCCs(null);
            }

            @Test
            public void allRateCardApplyReservationShouldBeCalled() throws Exception {
                reservationHandler.handle(request, executionContext, gyPolicyContext);
                verify(dataRateCard1, times(1)).applyReservation(gyPolicyContext, basePackage.getId(), null, gyPolicyContext.getReservations());
                verify(dataRateCard2, times(1)).applyReservation(gyPolicyContext, basePackage.getId(), null, gyPolicyContext.getReservations());
            }
        }

    }
}
